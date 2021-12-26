package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.ENoMoreData;
import sztejkat.abstractfmt.util.CAdaptivePushBackInputStream;
import java.io.*;
import java.io.InputStream;

/**
	A base for chunk-based write formats implementing
	{@link IIndicatorWriteFormat}
	<p>
	This class basically provides intermediate level 
	chunk management for payload processing.
*/
public abstract class ABinIndicatorReadFormat0 implements IIndicatorReadFormat
{
					/** Low level binary input */
					private final CAdaptivePushBackInputStream input;
					/** Number of bytes left in current
					chunk payload to process */
					private int chunk_payload_size;	
					
	protected ABinIndicatorReadFormat0(InputStream in)
	{
		assert(in!=null);
		this.input = new CAdaptivePushBackInputStream(in,4,8);
	};
				
	/* **********************************************************************
	
			Services required from subclasses	
	
	***********************************************************************/
	/** Invoked whenever {@link #readPayload} encountered end-of-chunk.
	<p>
	This method is expected to read header from <code>in</code>, check
	if it is a DATA chunk, and if it is decode it and return the size of 
	said chunk payload. Cursor in <code>in</code> should be at first byte
	of payload.
	<p>
	If it is <u>not</u> a DATA chunk, it should use un-read functionality
	of <code>in</code> to leave cursor where it was at the call.
	<p>
	If this method encounters end-of-file at first byte of chunk header
	it should not do anything and return -2. If it encounters it at following bytes
	it should throw {@link EUnexpectedEof}
	<p>
	This method MAY NOT call {@link #readPayload}. This method does NOT have to
	call {@link #startNextIndicatorChunk} 
	
	@param in input, wrapped in un-read buffer, with cursor at the first byte
		of next chunk header.
	@return size of decoded DATA chunk (zero is allowed), or -1 if it is not
		a data chunk or -2 if at physical end of file was reached at the begining of the header.
	*/
	protected abstract int tryNextDataChunk(CAdaptivePushBackInputStream in)throws IOException;
	
	/** Invoked whenever {@link #getIndicator} finds, that it does not know
	where it is and needs read indicator.
	<p>
	This method is expected to read header from <code>in</code>,
	decode it and call {@link #startNextIndicatorChunk}. It may then
	read necessary data from payload with {@link #readPayload}.
	Finally it returns indicator stored in header.
	<p>
	<i>Note:The found indicator may be DATA.</i>
	<p>
	After a return cursor in <code>in</code> should be at first byte of payload
	(if nothing was read from payload), or may be deep in payload or even in
	next DATA chunk.
	<p>
	If this method encounters end-of-file at first byte of chunk header
	it should return null. If it happens during decoding it should throw {@link EUnexpectedEof}.
	
	@param in input, wrapped in un-read buffer, with cursor at the first byte
		of next chunk header.
	@return indicator or null if at eof.
	*/
	protected abstract TIndicator tryNextIndicatorChunk(CAdaptivePushBackInputStream in)throws IOException;
								
	/* **********************************************************************
	
			Intermediate I/O services.	
	
	***********************************************************************/
	/** Invokes {@link #tryNextDataChunk(CAdaptivePushBackInputStream)} 
	@return --//--
	@throws IOException --//--
	*/
	protected final TIndicator tryNextIndicatorChunk()throws IOException
	{
		assert(this.chunk_payload_size==0);
		return tryNextIndicatorChunk(input);
	};
	/** Should be invoked when non-DATA chunk header was decoded and cursor moved 
	at the begining of payload. Arms {@link #readPayload} to be ready for processing
	@param chunk_payload_size size of chunk payload, may be zero.
	*/
	protected final void startNextIndicatorChunk(int chunk_payload_size)throws IOException
	{
		assert(chunk_payload_size>=0);
		assert(this.chunk_payload_size==0);	//<-- this is a pre-condition for data continuity.
	};
	/** Checks if there is no data left in current chunk, and if it is not,
	attempts to use {@link #tryNextDataChunk(CAdaptivePushBackInputStream)}
	to make it available.
	@return non-zero remaning size of data,
			 or -1 if reached any non-data chunk header, or -2 if reached physical 
			 end of file at the begining of the header.
	@throws IOException if failed at low level
	@throws EUnexpectedEof if failed due to eof in DATA header.
	*/
	private int tryNextDataChunk()throws IOException
	{
		while(chunk_payload_size==0)	//we do use "while" because zero sized chunks may happen.
										//well... they should not, but it is better to loop, than
										//be sorry.
		{
			//No, we need to process header.
			//This will update both chunk payload information
			//and indicator cache.
			int r = tryNextDataChunk(input);
			if (r<0) return r;
			this.chunk_payload_size=r;
		};
		assert(this.chunk_payload_size!=0);
		return this.chunk_payload_size;
	};
	/** Makes an attempt to read next byte from payload.
	Transparently handles DATA chunks.
	<p>
	If this method reaches chunk header decodes it. If this header 
	is something else than DATA header, updates indicator cache.
	
	@return byte 0...0xff or -1 if cursor is at the chunk header which
			is not a DATA chunk or -2 if physical end of stream was reached.
	*/
	protected final int readPayload()throws IOException
	{		
		//Check if has anything to read?
		int r = tryNextDataChunk();
		assert(r>=-2);
		if (r<0) return r;
		//we have data
		int v = input.read();
		assert((r>=-1)&&(r<=255));
		if (v==-1) return -2;	//physical eof. 
		//data are fetched, we need to do accounting
		chunk_payload_size--;
		return v;
	};
	/** Call {@link #readPayload} to fetch next byte from a payload.
	@return 0...0xFF
	@throws IOException if failed at low level
	@throws ENoMoreData if reached end of payload
	@throws EUnexpectedEof if reached physical end of file
	*/
	protected final int readPayloadByte()throws IOException,ENoMoreData,EUnexpectedEof
	{
		int r= readPayload();
		assert((r>=-2)&&(r<=255));
		switch(r)
		{	
			case -1: throw new ENoMoreData();
			case -2: throw new EUnexpectedEof();
			default: return r;
		}
	};
	/** Puts back payload byte to chunk buffer.
	This method works correctly even across DATA chunks boundaries. 
	@param b byte to put back 
	*/
	protected final void unreadPayloadByte(byte b)throws IOException
	{
		input.unread(b);
		chunk_payload_size++;
	};
	/** Checks if there is some payload to read.
	<p>
	If this method returns true cursor is at the header of
	non-DATA indicator.
	
	@return true if after checking subsequent header, if needed,
		there is no more payload in chunk.
		
	@throws EUnexpectedEof if failed to verify if there is a payload.
	 */
	protected final boolean isPayloadEof()throws IOException,EUnexpectedEof
	{
		int r = tryNextDataChunk();
		if (r==-2) throw new EUnexpectedEof();
		return r==-1;
	};
	/** Skips remaning data and moves cursor to the header of next non-DATA chunk	 
	@throws EUnexpectedEof if encountered physical end-of-file during this process.
	*/
	protected final void skipRemaningData()throws IOException, EUnexpectedEof
	{
		for(;;)
		{
			int r = tryNextDataChunk();
			if (r==-1) return;
			if (r==-2) throw new EUnexpectedEof();
			
			assert(this.chunk_payload_size!=0);
			//we have data, ask input to skip them, because it is much faster than reading.
			while(chunk_payload_size>0)
			{
				long skipped = input.skip(chunk_payload_size);
				if (skipped<=0) throw new EUnexpectedEof(); //<-- this is what happens when input can't skip.
				chunk_payload_size-=skipped;
			}
		}
	};
	
	/* *********************************************************
			
				IIndicatorReadFormat
	
	
	********************************************************* */
	@Override public void close()throws IOException
	{
		input.close();
	};
};