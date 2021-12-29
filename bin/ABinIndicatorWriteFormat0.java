package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import java.io.*;
import java.io.OutputStream;

/**
	A base for chunk-based write formats implementing
	{@link IIndicatorWriteFormat}
	<p>
	This class basically provides intermediate level 
	chunk buffer management for payload processing.
*/
public abstract class ABinIndicatorWriteFormat0 implements IIndicatorWriteFormat
{
				/** Low level binary output */
				private final OutputStream output;
				/** A pre-allocated chunk buffer.
				The size of this array matches the
				maximum size of possible chunk payload
				and is specified in constructor.
				*/
				private final byte [] chunk;
				/** A capacity, representing how much
				of {@link #chunk} can be used to store
				data in currently chosen {@link #header_indicator}
				model */
				private int chunk_capacity;
				/** Where to put next data into {@link #chunk}.
				If this value equals to {@link #chunk_capacity}
				chunk buffer is full and needs to be send
				down-stream.*/
				private int chunk_at;				
				/** An indicator representing type of current
				header. It can be {@link TIndicator#DATA},
				all <code>TYPE_xxx</code> and <code>TYPE_xxx_BLOCK</code>
				and <code>BEGIN_xxx</code>/<code>END_xxx</code> indicators.
				<p>
				If this value is null chunk does not exist.
				*/
				private TIndicator header_indicator;
				/** A pre-allocated header buffer, of size
				equal to maximum size of header. */
				private final byte [] header;
				
				
	/** Creates
	@param output down-stream binary output 
	@param max_header_size maximum size of chunk header,  will be used to
			pre-allocate header buffer.
	@param max_chunk_size maximum size of chunk payload, will be used to
			pre-allocate chunk buffer.	
	*/
	protected ABinIndicatorWriteFormat0(
							OutputStream output,							
							int max_header_size,
							int max_chunk_size
							)
	{
		assert(output!=null);
		assert(max_header_size>0);
		assert(max_chunk_size>0);
		
		this.output =output;	
		this.chunk = new byte [max_chunk_size];
		this.header = new byte [max_header_size];	
	};		
	/* ********************************************************************
	
	
			Services required from subclasses.
	
	
	* *********************************************************************/
	/** This method is invoked when chunk is to be flushed down-stream
	and header must be updated to reflect payload size.
	<p>	
	This code should:
	<ul>
		<li>retrive size of payload with {@link #getPayloadSize};</li>
		<li>retrive capacity of current payload buffer with {@link #getPayloadCapactity};</li>
		<li>manipulate payload content, if necessary with
		{@link #getPayload(int)},{@link #setPayload(int,byte)},{@link #setPayloadSize(int)};</li>
	</ul>
	This method must not use {@link #writePayload(byte)} nor {@link #writePayload(byte[],int,int)}.
	@param header_indicator indicator which is stored or is to be updated
			into a header buffer. Will be the same which was passed to
			{@link #startChunk}
	@param header_buffer header buffer buffer in which fill header content.
			This is pre-allocated to <code>max_header_size</code> specified
			in constructor and re-used across calls. This buffer is un-touched
			since {@link #startChunk}.
			
	@return returns:
			<ul>
				<li>zero, if there is no need to write anything down-stream,
				including header and payload;</li>
				<li>positive non-zero indicates how many bytes of <code>header_buffer</code>
				write to stream as header followed by payload;
				</li>
			</ul>
	@throws IOException if failed to update.
	*/
	protected abstract int prepareChunkHeaderForFlushing(
													 TIndicator header_indicator,
													 byte [] header_buffer
													 )throws IOException;
	
													 
	/** Invoked when format must initialize DATA chunk. This method
	is expected to call {@link #startChunk} with {@link TIndicator#DATA}
	and apropriate capacity information. 
	@throws IOException if failed, even tough this method usually does not 
		need to write any data to down-stream.
	*/													 
	protected abstract void startDataChunk()throws IOException;
	
	/** Invoked in {@link #flush} and {@link #writeFlush}.
	<p>
	This method will be usually empty, except if class buffers some
	data outside a payload. If it does, it must write them inside
	this method.
	@throws IOException if failed.  
	*/
	protected abstract void flushPayload()throws IOException;
	
	/* ********************************************************************
	
	
			Intermediate level I/O
	
	
	* *********************************************************************/
	/** Prepares for next chunk.
	This method flushes current chunk, if any, and sets-up
	all buffers.
	@param header_indicator indicator which denotes this header.
		Will be passed to {@link #prepareChunkHeaderForFlushing}
	@param chunk_payload_capacity chunk payload buffer capactity.
		Must be in boundaries specified in constructor.
		
	@throws IOException if failed to flush current chunk
	
	@see #startDataChunk
	@see #writePayload(byte)
	@see #writePayload(byte[],int,int)
	@see #flushChunk	
	*/
	protected void startChunk(TIndicator header_indicator,
						      int chunk_payload_capacity
						      )throws IOException
	{		
		flushChunk();
		assert(header_indicator!=null);
		assert(chunk_payload_capacity>0);
		this.header_indicator=header_indicator;
		this.chunk_capacity=chunk_payload_capacity;
		this.chunk_at=0;
	};
	/** Updates payload size information in header and 
	writes header followed by payload to down-stream.
	Resets chunk to empty 
	@throws IOException if failed to pass chunk downstream
	*/
	protected void flushChunk()throws IOException
	{
		if (this.header_indicator==null)
		{
			assert(chunk_at==0);
			assert(chunk_capacity==0);
			return;
		};
		final int header_size = prepareChunkHeaderForFlushing( header_indicator, header);
		assert(header_size>=0):"header_size="+header_size;
		if (header_size!=0)
		{
			//Note: we need to use class fields directly here
			//because they can be modified by  
			output.write(header,0, header_size);
			output.write(chunk,0,this.chunk_at);
		};
		
		this.header_indicator=null;
		this.chunk_capacity=0;
		this.chunk_at=0;
	};
	/** Flushes if there is no free spaces and creates data chunk
	@throws IOException if failed to flush current chunk or failed
		to start new data chunk.
	@see #flushChunk
	@see #startDataChunk
	*/
	private void ensureHasPayloadSpace()throws IOException
	{
		if (chunk_at==chunk_capacity)	//this will be also true if header_indicator is null.
		{
				flushChunk();
				startDataChunk();
		};
	};
	/** Writes payload byte, flushing and creating data chunks if necessary.
	@param v byte to write 
	@throws IOException if failed to do {@link #ensureHasPayloadSpace}
	*/
	protected void writePayload(byte v)throws IOException
	{
		ensureHasPayloadSpace();
		assert(chunk_at<chunk_capacity);		
		chunk[chunk_at++]=v;
	};
	/** Writes payload data, flushing and creating data chunks if necessary.
	@param data buffer of data to write
	@param offset offset in above to start taking data from
	@param length how many bytes.
	@throws IOException if failed to do {@link #ensureHasPayloadSpace}
	*/
	protected void writePayload(byte [] data, int offset, int length)throws IOException
	{   
		assert(data!=null);
		assert(offset>=0);
		assert(length>=0);
		assert(length+offset<=data.length);
		
		while(length>0)
		{         
			ensureHasPayloadSpace();
			int free_space = chunk_capacity-chunk_at;
			assert(free_space>0);
			int transfer = Math.min(free_space,length);
			System.arraycopy(data,offset,chunk,chunk_at,transfer);
			length-=transfer;
			offset+=transfer;
			chunk_at+=transfer;
		}; 
	};
	
	/* ********************************************************************
	
	
			Payload manipulation for header processing.
	
	
	* *********************************************************************/	
	/** Returns size of payload 
	@return size of payload.*/
	protected final int getPayloadSize()
	{
		return chunk_at;		
	};
	/** Returns capactity of payload buffer. 
	@return size of payload.*/
	protected final int getPayloadCapactity()
	{
		return chunk_capacity;
	};
	/** Retrives byte of payload
	@param at 0... {@link #getPayloadSize} -1
	@return byte fetched
	@throws AssertionError if at is out of bounds.
	*/
	protected final byte getPayload(int at)
	{
		assert(at>=0);
		assert(at<chunk_at);
		return chunk[at];
	};
	/** Sets byte of payload
	@param at 0... {@link #getPayloadSize} -1
	@param v byte to set
	@throws AssertionError if at is out of bounds.
	*/
	protected final void setPayload(int at, byte v)
	{
		assert(at>=0);
		assert(at<chunk_at);
		chunk[at]=v;
	};
	/** Sets size of payload
	@param size 0... {@link #getPayloadCapactity}
	@throws AssertionError if at is out of bounds.
	*/
	protected final void setPayloadSize(int size)
	{
		assert(size>=0);
		assert(size<=chunk_capacity);
		chunk_at = size;
	};
	/* ********************************************************************
	
	
			IIndicatorWriteFormat
	
	
	* *********************************************************************/
	/** Calls {@link #flushPayload() }
	<p>
	<i>Note: Event if this class is un-described the contract requires that this 
	method <u>is called</u>. So we are using it to flush buffered content. In practice
	it will apply to buffered bit-chain.</i>
	
	 */
	@Override public void writeFlush(TIndicator flush)throws IOException{ flushPayload(); };
	/** Flushes any pending payload with {@link #flushPayload}, then flushes chunk with 
	{@link #flushChunk} and finally flushes down-stream output */
	@Override public void flush()throws IOException
	{
		flushPayload();
		flushChunk();
		output.flush();
	}; 
	@Override public void close()throws IOException
	{
		//Note: API allows to ignore flushing, so we are not flushing.
		output.close();
	};
};