package sztejkat.abstractfmt.bin.chunk;
import  sztejkat.abstractfmt.*;
import java.io.IOException;
import java.io.InputStream;
/**
	A base chunk implementation.
	<p>
	This implementation adds intermediate layer responsible for
	raw byte access on chunk processing, some elementary state
	management and signal processing what includes names encoding.
	
*/
abstract class AChunkReadFormat0 extends ARegisteringStructReadFormat
{
					/** A down-stream of chunk format, raw binary stream */
					private final InputStream raw;
					/** A current chunk payload buffer.
					<p>
					Due to the fact, that our writing end implementation
					do <u>always</u> flush the whole chunk (header + payload)
					we may safely reduce the amount of low level I/O and 
					read the entire chunk into a buffer at once.
					<p>
					If the low level I/O will throtlle the read by returing
					partial reads we will still be able to handle it.
					<p>
					Pre-allocated to max chunk size.
					*/
					private final byte [] buffer = new byte[4095];
					/** A declared usable size of {@link #buffer} in current
					chunk header.
					<p>
					Set to -1 if there is no chunk header processed.
					@see #buffer_size
					*/
					private int declared_chunk_size = -1;
					/** A currently available amount of data in {@link #buffer}.
					Can be smaller than {@link #declared_chunk_size} if low level
					I/O returned with partial read. In such case missing data
					will be loaded on demand.
					*/
					private int buffer_size;
					/** A cursor in {@link #buffer} from where next data are to be fetched.
					If equal to {@link #declared_chunk_size} whole chunk is consumed. */
					private int buffer_at;
					/** Either a -1 or a header which was pre-fetched from 
					raw stream by {@link #tryContinueChunk} which failed
					to detect "continue" chunk.
					See HEADER_xxx constants in {@link AChunkWriteFormat0} */
					private int pending_header = -1;
					/** Pending last signal index. 
					@see #pickLastSignalIndex
					*/
					private int pending_last_signal_index;
					/** Used to count ordered signal registration events
					and detect if mix-up is present.
					Carries last returned ordered index or -1 if no ordered
					indexes where used.*/
					private int last_ordered_signal=-1;
					/** Set if indexed registration was detected. */
					private boolean detected_indexed_mode;
					/** Buffer to build signal names in */
					private StringBuilder name_building_buffer = new StringBuilder();
					/** Made of {@link #name_building_buffer} */
					private String pending_name;
					
					
	/* ***************************************************************************
	
			Construction
	
	
	*****************************************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
			This value cannot be larger than 127. Recommended value is 127, minimum resonable is 8.
	@param raw raw input stream, non null. Will be closed.
			<p>
			This stream <u>must</u> be such, that it returns
			partial read/partial skip only if there is actually no data in stream, timeout happen, file
			was fully read or connection is broken. If this stream will return partial reads
			or partial skips just "because i like it" this format will report {@link EUnexpectedEof}
			when such condition will happen inside chunk headers or when it fails to read
			at least one byte in a chunk body.
	*/
	AChunkReadFormat0(int name_registry_capacity, InputStream raw)
	{
		super(name_registry_capacity);
		assert(name_registry_capacity<=127):"name registry too large";
		assert(raw!=null);
		this.raw = raw;
	};
	/* ****************************************************************************
	
			Intermediate level, providing transparent chunk payload 
			processing.
	
	
	*****************************************************************************/
	
	/**
		Assuming all the data in chunk are currently read will try
		to test if next chunk in stream is "continue" and arms chunk buffer
		if it is.
		@return false if it is not.
		@see #pending_header
		@throws IOException if raw stream failed 
		@throws EUnexpectedEof if raw stream failed to provide a header.
	*/
	private boolean tryContinueChunk()throws IOException
	{
		if (pending_header!=-1) return false; //we already tested it and should be stuck at it.
		int r = raw.read();
		assert(r>=-1);
		assert(r<=0xFF);
		if (r==-1) throw new EUnexpectedEof();
		if (((byte)(r & 0b111))==AChunkWriteFormat0.HEADER_CONTINUE)
		{
			assert(pending_header == -1); 
			handle_HEADER_CONTINUE(r);
			//No need to pre-load it.
			return true;
		}else
		{
			//save it for later.
			pending_header = r;
			return false;
		}
	};
	/** Assuming that not all chunk payload data are yet loaded into chunk 
	buffer attempts to read <u>at least one byte</u> and update the payload buffer.
	@throws IOException if low level failed
	@throws EUnexpectedEof if low level failed to provide at least one byte in a single
			operation.
	*/
	private void fillInChunk()throws IOException
	{
		assert(buffer_size!=declared_chunk_size); //everything is read, you must not call it.
		int r = raw.read(buffer,buffer_size, declared_chunk_size - buffer_size);
		if (r<1) throw new EUnexpectedEof("Failed to read at least one byte of chunk payload");
		buffer_size+=r;
		assert(buffer_size<=declared_chunk_size); //post condition. 
	};
	/** Reads data from chunk payload buffer. Will fill-in a buffer or move
	to "continue" chunk if necessary 
	@return -1 if reached chunk indicating a header other than "continue".
		Otherwise 0...0xff representing a byte from chunk payload.
	*/
	protected int in()throws IOException
	{
		//try from current buffer?
		if ((declared_chunk_size==-1)||(buffer_at==declared_chunk_size))
		{
			if (!tryContinueChunk()) return -1;
			assert(declared_chunk_size!=-1);
		};
		//now check if there is a byte?
		if (buffer_at==buffer_size)
		{
			assert(buffer_size!=declared_chunk_size); //this should be captured above.
			fillInChunk();
			//the above must not fail in any different way that providing at last
			//one byte.
			assert(buffer_at<buffer_size);
		};
		return buffer[buffer_at++] & 0xFF;
	};
	/* *******************************************************
			
			ARegisteringStructReadFormat		
	
		
	 ********************************************************/
	 /** Processes "continue" header, by initiating payload buffer but
	NOT pre-loading it.
	@param h HEADER_CONTINUE 
	@throws IOException if failed to load chunk header
	*/
	private void handle_HEADER_CONTINUE(int h)throws IOException
	{
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_CONTINUE);
		//now detect long versus short form                               
		int s  = h >>>4;
		if ((h & 0b1000)!=0)
		{
			//long form
			h = raw.read();
			assert(h>=-1);
			assert(h<=0xFF);
			if (h==-1) throw new EUnexpectedEof();
			s |= (h<<4);
		}
		//initialize buffer			
		declared_chunk_size = s;
		buffer_size = 0;
		buffer_at = 0;
	};
	/** Invoked when needs to process "register" header
	@param h HEADER_REGISTER
	@return what to return from readSignalReg
	*/
	private TSignalReg handle_HEADER_REGISTER(int h)throws IOException
	{
		//indexed or ordered?
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_REGISTER);
		int pending_index; //preserve index in here since we will call some handler which may override it.
		if ((h & 0b1000)!=0)
		{
			//indexed mode 
			if (last_ordered_signal!=-1) throw new EBrokenFormat("Mixed ordered and indexed registration");
			int r = raw.read();
			assert(r>=-1);
			assert(r<=0xFF);
			if (r==-1) throw new EUnexpectedEof();
			if (r>127) throw new EBrokenFormat("Index too large");
			pending_index = r;
			this.detected_indexed_mode = true;
		}else
		{
			//ordered mode.
			//We skip mess-up detection here?
			if (detected_indexed_mode) throw new EBrokenFormat("Mixed ordered and indexed registration");
			pending_index = ++this.last_ordered_signal;
		};
		//Now we need to load the header which must be HEADER_BEGIN_DIRECT
		//or HEADER_END_BEGIN_DIRECT
		h = raw.read();
		assert(h>=-1);
		assert(h<=0xFF);
		if (h==-1) throw new EUnexpectedEof();
		switch((byte)(h & 0b111))
		{
			case AChunkWriteFormat0.HEADER_BEGIN_DIRECT:
					handle_HEADER_BEGIN_DIRECT(h);
					//above arms pickLastSignalRegName
					this.pending_last_signal_index  = pending_index; //re-arm index.
					return TSignalReg.SIG_BEGIN_AND_REGISTER;
					
			case AChunkWriteFormat0.HEADER_END_BEGIN_DIRECT:
					handle_HEADER_END_BEGIN_DIRECT(h);
					//above arms pickLastSignalRegName
					this.pending_last_signal_index  = pending_index; //re-arm index.
					return TSignalReg.SIG_END_BEGIN_AND_REGISTER;
			default:
					throw new EBrokenFormat("unexpected header "+Integer.toHexString(h & 0b111));
		}
	};
	/** Invoked when needs to process "begin-direct" header,
	either as a stand alone header or a part of registration
	@param h  HEADER_BEGIN_DIRECT
	@return 
	*/
	private void handle_HEADER_BEGIN_DIRECT(int h)throws IOException
	{
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_BEGIN_DIRECT);
		handle_HEADER_xx_BEGIN_DIRECT(h);
	};
	/** Invoked when needs to process "end-begin-direct" header,
	either as a stand alone header or a part of registration
	@param h  HEADER_END_BEGIN_DIRECT
	@return 
	*/
	private void handle_HEADER_END_BEGIN_DIRECT(int h)throws IOException
	{
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_END_BEGIN_DIRECT);
		handle_HEADER_xx_BEGIN_DIRECT(h);
	};
	/** Common for both {@link #handle_HEADER_BEGIN_DIRECT}
	and {@link #handle_HEADER_END_BEGIN_DIRECT} 
	@param h header
	*/
	private void handle_HEADER_xx_BEGIN_DIRECT(int h)throws IOException
	{
		//arm payload size.
		int s = h >>>3;
		//arm chunk buffer.
		this.declared_chunk_size = s;
		this.buffer_size =0;
		this.buffer_at=0;
		//reset name buffer and load name
		name_building_buffer.setLength(0);
		loadSignalNameTo(name_building_buffer);
		//pending header MUST be set and MUST be HEADER_END
		assert(pending_header!=-1);//this is state machine warranty
		h = this.pending_header;
		this.pending_header = -1;
		//validate format 
		if ( ((byte)(h & 0b111))!=AChunkWriteFormat0.HEADER_END)
				throw new EBrokenFormat("END chunk is required after signal name");	
		handle_HEADER_END(h);
		//override what handle_HEADER_END did.
		pending_name = name_building_buffer.toString();
		//drop buffer.
		name_building_buffer.setLength(0);
	};
	/** Invoked when needs to process "begin-registered" header
	@param h  HEADER_END_BEGIN_DIRECT
	@return 
	*/
	private void handle_HEADER_BEGIN_REGISTERED(int h)throws IOException
	{
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_BEGIN_REGISTERED);
		handle_HEADER_xx_BEGIN_REGISTERED(h);
	};
	/** Invoked when needs to process "end-begin-registered" header
	@param h  HEADER_END_BEGIN_DIRECT
	@return 
	*/
	private void handle_HEADER_END_BEGIN_REGISTERED(int h)throws IOException
	{
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_END_BEGIN_REGISTERED);
		handle_HEADER_xx_BEGIN_REGISTERED(h);
	};		
	/** Common for both {@link #handle_HEADER_BEGIN_REGISTERED}
	and {@link #handle_HEADER_END_BEGIN_REGISTERED} 
	@param h header
	*/
	private void handle_HEADER_xx_BEGIN_REGISTERED(int h)throws IOException
	{
		//arm index
		int i = (h >>>3) & 0x7;
		//arm size
		int s  = 1 << ( h >>> 6);
		//arm chunk buffer.
		this.declared_chunk_size = s;
		this.buffer_size =0;
		this.buffer_at=0;
		//arm info
		this.pending_last_signal_index = i;
		this.pending_name = null;
	};
	/** Invoked when needs to process "end-begin-registered" header,
	either during name processing or during stand alone processing.
	@param h  HEADER_END
	@return 
	*/
	private void handle_HEADER_END(int h)throws IOException
	{
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_END);
		int s = h >>>3; 
		//arm chunk buffer.
		this.declared_chunk_size = s;
		this.buffer_size =0;
		this.buffer_at=0;
		//arm info
		this.pending_name = null;
	};
	
	/** Invoked when needs to process "extended registered" header,
	either during name processing or during stand alone processing.
	@param h  HEADER_END
	@return either SIG_BEGIN_REGISTERED or SIG_END_BEGIN_REGISTERED
	*/
	private TSignalReg handle_HEADER_EXTENDED_REGISTERED(int h)throws IOException
	{
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_EXTENDED_REGISTERED);
		//load extension index
		int r = raw.read();
		assert(r>=-1);
		assert(r<=0xFF);
		if (r==-1) throw new EUnexpectedEof();
		
		//arm chunk buffer.
		this.declared_chunk_size = h >>>3;
		this.buffer_size =0;
		this.buffer_at=0;
		//arm info
		this.pending_last_signal_index =  r & 0x7F;
		this.pending_name = null;
		return ((r & 0x80)!=0)
				?
				TSignalReg.SIG_END_BEGIN_REGISTERED
				:
				TSignalReg.SIG_BEGIN_REGISTERED;
	};
	
	/** Loads signal name, avoiding limitless loading 
	@param b where to load, must be cleared at input
	@throws IOException if failed
	@throws EFormatBoundaryExceeded if name is too long.
	*/
	private void loadSignalNameTo(Appendable b)throws IOException
	{
		int size = 0;
		for(;;)
		{
			if (size>getMaxSignalNameLength()) throw new EFormatBoundaryExceeded("Signal name too long");
			int c = decodeStringChar();
			if (c==-1) return;
			b.append((char)c);
			size++;
		}
	};
	/** Loads next string char from chunk, decoding it as specified in specs.
	@return -1 if reached end of payload ("continue" is handled transparently)
	*/
	private int decodeStringChar()throws IOException
	{
		int n = in();
		if (n==-1) return -1;
		char c = (char)(n & 0x7F);
		if ((n & 0x80)!=0)
		{
			 n = in();
			 if (n==-1) throw new EUnexpectedEof();
			 c |= (char)((( n & 0x7F)<<7));
			 if ((n & 0x80)!=0)
			 {
			 	 n = in();
			 	 if (n==-1) throw new EUnexpectedEof();
			 	 if ((n & 0b1111_1100)!=0) throw new EBrokenFormat("Invalid string character");
			 	 c |= (char)(( n & 0x3) << (7+7));
			 };
		};
		return c;
	};
	@Override protected TSignalReg readSignalReg()throws IOException
	{
		for(;;)//loop to skip all "continue" chunks.
		{
			//check if we already touched the chunk?		
			int h = pending_header;		//pick and forget.
			this.pending_header = -1;
			if (h == -1)
			{
				//no, we need to touch it.
				//Check if we have chunk buffer?
				if (declared_chunk_size!=-1)
				{
					//check if we have something not loaded to chunk buffer?
					if (declared_chunk_size!=buffer_size)
					{
						//we need raw to skip it.
						long to_skip = declared_chunk_size-buffer_size;
						long skipped = raw.skip(to_skip);
						//now we assume, that if not everything was skipped, we have an eof.
						if (skipped!=to_skip) throw new EUnexpectedEof();
					};
				};
				//pre-fetch header.
				int r = raw.read();
				assert(r>=-1);
				assert(r<=0xFF);
				if (r==-1) throw new EUnexpectedEof();
				h = r;	
			};
			//Now we are sure we have a header. What is it?
			switch((byte)(h & 0b111))
			{
				case AChunkWriteFormat0.HEADER_REGISTER:
						return handle_HEADER_REGISTER(h);
				case AChunkWriteFormat0.HEADER_BEGIN_DIRECT:
						handle_HEADER_BEGIN_DIRECT(h);
						return TSignalReg.SIG_BEGIN_DIRECT;
				case AChunkWriteFormat0.HEADER_END_BEGIN_DIRECT:
						handle_HEADER_END_BEGIN_DIRECT(h);
						return TSignalReg.SIG_END_BEGIN_DIRECT;
				case AChunkWriteFormat0.HEADER_BEGIN_REGISTERED:
						handle_HEADER_BEGIN_REGISTERED(h);
						return TSignalReg.SIG_BEGIN_REGISTERED;
				case AChunkWriteFormat0.HEADER_END_BEGIN_REGISTERED:
						handle_HEADER_END_BEGIN_REGISTERED(h);
						return TSignalReg.SIG_END_BEGIN_REGISTERED;
				case AChunkWriteFormat0.HEADER_END:
						handle_HEADER_END(h);
						return TSignalReg.SIG_END;
				case AChunkWriteFormat0.HEADER_CONTINUE:
						handle_HEADER_CONTINUE(h);
						break;
				case AChunkWriteFormat0.HEADER_EXTENDED_REGISTERED:
						return handle_HEADER_EXTENDED_REGISTERED(h);
			}
		}
	};
	@Override protected int pickLastSignalIndex()
	{
		int r = pending_last_signal_index;
		pending_last_signal_index = -1;
		return r;
	};
	@Override protected String pickLastSignalRegName()
	{
		String n = pending_name;
		pending_name = null;
		return n;
	};
	/* ********************************************************************
	
			AStructFormatBase
	
	*********************************************************************/
	/** Overriden to close low level I/O */
	@Override protected void closeImpl()throws IOException
	{
		raw.close();
	};
};
