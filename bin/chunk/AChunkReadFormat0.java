package sztejkat.abstractfmt.bin.chunk;
import  sztejkat.abstractfmt.*;
import  sztejkat.abstractfmt.bin.ABinReadFormat;
import  sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.InputStream;
/**
	A base chunk implementation.
	<p>
	This implementation adds intermediate layer responsible for
	raw byte access on chunk processing, some elementary state
	management and signal processing what includes names encoding.
	
*/
abstract class AChunkReadFormat0 extends ABinReadFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(AChunkReadFormat0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AChunkReadFormat0.",AChunkReadFormat0.class) : null;

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
			This value cannot be larger than 128. Recommended value is 128, minimum resonable is 8.
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
		if (TRACE) TOUT.println("new AChunkReadFormat0(name_registry_capacity="+name_registry_capacity+")");
		assert(name_registry_capacity<=128):"name registry too large";
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
		@param throw_on_eof if true will throw on eof
				when attempts to pick next header. If false will return false.
		@return false if it is not.
		@see #pending_header
		@throws IOException if raw stream failed 
		@throws EUnexpectedEof if raw stream failed to provide a header.
	*/
	private boolean tryContinueChunk(boolean throw_on_eof)throws IOException
	{
		if (TRACE) TOUT.println("tryContinueChunk ENTER");
		if (pending_header!=-1)
		{
			if (TRACE) TOUT.println("tryContinueChunk=false, has pending chunk header "+Integer.toHexString(pending_header)+", LEAVE ");
			return false; //we already tested it and should be stuck at it.
		};
		int r = raw.read();
		assert(r>=-1);
		assert(r<=0xFF);
		if (r==-1)
		{
			if (throw_on_eof)
				throw new EUnexpectedEof();
			else
				return false;
		};
		if (((byte)(r & 0b111))==AChunkWriteFormat0.HEADER_CONTINUE)
		{
			if (TRACE) TOUT.println("tryContinueChunk, found CONTINUE");
			assert(pending_header == -1); 
			handle_HEADER_CONTINUE(r);
			//No need to pre-load it.
			if (TRACE) TOUT.println("tryContinueChunk=true, LEAVE");
			return true;
		}else
		{
			//save it for later.
			if (TRACE) TOUT.println("tryContinueChunk, found header "+Integer.toHexString(r));
			pending_header = r;
			if (TRACE) TOUT.println("tryContinueChunk=false, LEAVE");
			return false;
		}
	};
	/* ***************************************************************************
	
			Servicec required by ABinReadFormat
	
	
	*****************************************************************************/
	
	/** Reads data from chunk payload buffer. Will fill-in a buffer or move
	to "continue" chunk if necessary
	@return -1 if reached chunk indicating a header other than "continue" .
		Otherwise 0...0xff representing a byte from chunk payload.
		
	@throws IOException if low level failed
	@throws EUnexpectedEof if encountered end of file. 
	*/
	@Override final protected int in()throws IOException
	{
		if (DUMP) TOUT.println("in() buffer_at="+buffer_at+" buffer_size="+buffer_size+" ENTER");
		//Try from current buffer. Notice chunks may have zero
		//size, so we need to loop.
		while ((declared_chunk_size<=0)||(buffer_at==declared_chunk_size))
		{
			if (TRACE) TOUT.println("in(), moving to next chunk");
			if (!tryContinueChunk(true))
			{
				if (TRACE) TOUT.println("in()=-1, no next chunk, LEAVE");
				return -1;
			};
			assert(declared_chunk_size!=-1);
		};
		//now check if there is a byte?
		if (buffer_at==buffer_size)
		{
			if (TRACE) TOUT.println("in() loading "+(declared_chunk_size - buffer_size)+" data to buffer");
			int r = raw.read(buffer,buffer_size, declared_chunk_size - buffer_size);
			if (r<1) throw new EUnexpectedEof("Failed to read at least one byte of chunk payload");
			buffer_size+=r;
			if (TRACE) TOUT.println("in() new buffer size = "+buffer_size);
		};
		final int v = buffer[buffer_at++] & 0xFF;
		if (DUMP) TOUT.println("in()=0x"+Integer.toHexString(v)+" LEAVE");
		return v;
	};
	/** A separated portion of {@link #hasElementaryDataImpl} which does 
	not call <code>super.hasElementaryDataImpl</code> and just checks
	if there is something in chunk payload (including transparent handling of "continue")
	@return true if there are some data, false if not
	@throws IOException if failed
	*/
	@Override protected boolean hasUnreadPayload()throws IOException
	{
		if (TRACE) TOUT.println("hasUnreadPayload() ENTER");
		//Now this is very alike in() but we have to react diffently on EOFs
		//Try from current buffer. Notice chunks may have zero
		//size, so we need to loop.
		while ((declared_chunk_size<=0)||(buffer_at==declared_chunk_size))
		{
			if (TRACE) TOUT.println("hasUnreadPayload(), moving to next chunk");
			//We can't throw on EOF
			if (!tryContinueChunk(false))
			{
				if (TRACE) TOUT.println("hasUnreadPayload()=false, no more chunks, LEAVE");
				return false;
			};
			assert(declared_chunk_size!=-1);
		};
		//and no need to preload anything.
		final boolean v= buffer_at<declared_chunk_size;
		if (TRACE) TOUT.println("hasUnreadPayload()="+v+", LEAVE");
		return v;
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
		if (TRACE) TOUT.println("handle_HEADER_CONTINUE() ENTER");
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
		if (TRACE) TOUT.println("handle_HEADER_CONTINUE() declared_chunk_size="+declared_chunk_size+" LEAVE");
	};
	/** Invoked when needs to process "register" header
	@param h HEADER_REGISTER
	@return what to return from readSignalReg
	@throws IOException if down-stream failed
	@throws EUnexpectedEof if detected end-of-file inside a header
	@throws EBrokenFormat if detected unallowed combination of data.
	*/
	private TSignalReg handle_HEADER_REGISTER(int h)throws IOException
	{
		if (TRACE) TOUT.println("handle_HEADER_REGISTER() ENTER");
		//indexed or ordered?
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_REGISTER);
		int pending_index; //preserve index in here since we will call some handler which may override it.
		if ((h & 0b1000)!=0)
		{
			if (TRACE) TOUT.println("handle_HEADER_REGISTER(), indexed mode");
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
			if (TRACE) TOUT.println("handle_HEADER_REGISTER(), ordered mode");
			//ordered mode.
			//We skip mess-up detection here?
			if (detected_indexed_mode) throw new EBrokenFormat("Mixed ordered and indexed registration");
			pending_index = ++this.last_ordered_signal;
			//Below condition won't ever trigger due to registrty limits.
			//so assertion is enough.
			assert(this.last_ordered_signal>=0);
		};
		if (TRACE) TOUT.println("handle_HEADER_REGISTER(), pending_index="+pending_index);
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
					if (TRACE) TOUT.println("handle_HEADER_REGISTER()=SIG_BEGIN_AND_REGISTER LEAVE");
					return TSignalReg.SIG_BEGIN_AND_REGISTER;
					
			case AChunkWriteFormat0.HEADER_END_BEGIN_DIRECT:
					handle_HEADER_END_BEGIN_DIRECT(h);
					//above arms pickLastSignalRegName
					this.pending_last_signal_index  = pending_index; //re-arm index.
					if (TRACE) TOUT.println("handle_HEADER_REGISTER()=SIG_END_BEGIN_AND_REGISTER LEAVE");
					return TSignalReg.SIG_END_BEGIN_AND_REGISTER;
			default:
					throw new EBrokenFormat("unexpected header "+Integer.toHexString(h & 0b111));
		}
	};
	/** Invoked when needs to process "begin-direct" header,
	either as a stand alone header or a part of registration
	@param h  HEADER_BEGIN_DIRECT
	@throws IOException if down-stream failed
	@throws EUnexpectedEof if detected end-of-file inside a header
	@throws EBrokenFormat if detected unallowed combination of data.
	*/
	private void handle_HEADER_BEGIN_DIRECT(int h)throws IOException
	{
		if (TRACE) TOUT.println("handle_HEADER_BEGIN_DIRECT()");
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_BEGIN_DIRECT);
		handle_HEADER_xx_BEGIN_DIRECT(h);
	};
	/** Invoked when needs to process "end-begin-direct" header,
	either as a stand alone header or a part of registration
	@param h  HEADER_END_BEGIN_DIRECT
	@throws IOException if down-stream failed
	@throws EUnexpectedEof if detected end-of-file inside a header
	@throws EBrokenFormat if detected unallowed combination of data.
	*/
	private void handle_HEADER_END_BEGIN_DIRECT(int h)throws IOException
	{
		if (TRACE) TOUT.println("handle_HEADER_END_BEGIN_DIRECT()");
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_END_BEGIN_DIRECT);
		handle_HEADER_xx_BEGIN_DIRECT(h);
	};
	/** Common for both {@link #handle_HEADER_BEGIN_DIRECT}
	and {@link #handle_HEADER_END_BEGIN_DIRECT} 
	@param h header
	@throws IOException if down-stream failed
	@throws EUnexpectedEof if detected end-of-file inside a header
	@throws EBrokenFormat if detected unallowed combination of data.
	*/
	private void handle_HEADER_xx_BEGIN_DIRECT(int h)throws IOException
	{
		if (TRACE) TOUT.println("handle_HEADER_xx_BEGIN_DIRECT() ENTER");
		//arm payload size.
		int s = h >>>3;
		//arm chunk buffer.
		this.declared_chunk_size = s;
		this.buffer_size =0;
		this.buffer_at=0;
		if (TRACE) TOUT.println("handle_HEADER_xx_BEGIN_DIRECT() declared_chunk_size="+declared_chunk_size);
		//reset name buffer and load name		
		loadSignalNameTo(name_building_buffer);
		//pending header MUST be set and MUST be HEADER_END
		assert(pending_header!=-1);//this is state machine warranty
		h = this.pending_header;
		this.pending_header = -1;
		//validate format 
		if ( ((byte)(h & 0b111))!=AChunkWriteFormat0.HEADER_END)
				throw new EBrokenFormat("END chunk is required after signal name, found "+Integer.toHexString(h & 0b111));	
		handle_HEADER_END(h);
		//override what handle_HEADER_END did.
		pending_name = name_building_buffer.toString();
		//drop buffer.
		name_building_buffer.setLength(0);
		if (TRACE) TOUT.println("handle_HEADER_xx_BEGIN_DIRECT() pending_name=\""+pending_name+"\" LEAVE");
	};
	/** Invoked when needs to process "begin-registered" header
	@param h  HEADER_END_BEGIN_DIRECT
	*/
	private void handle_HEADER_BEGIN_REGISTERED(int h)
	{
		if (TRACE) TOUT.println("handle_HEADER_BEGIN_REGISTERED");
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_BEGIN_REGISTERED);
		handle_HEADER_xx_BEGIN_REGISTERED(h);
	};
	/** Invoked when needs to process "end-begin-registered" header
	@param h  HEADER_END_BEGIN_DIRECT
	*/
	private void handle_HEADER_END_BEGIN_REGISTERED(int h)
	{
		if (TRACE) TOUT.println("handle_HEADER_END_BEGIN_REGISTERED");
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_END_BEGIN_REGISTERED);
		handle_HEADER_xx_BEGIN_REGISTERED(h);
	};		
	/** Common for both {@link #handle_HEADER_BEGIN_REGISTERED}
	and {@link #handle_HEADER_END_BEGIN_REGISTERED} 
	@param h header
	*/
	private void handle_HEADER_xx_BEGIN_REGISTERED(int h)
	{
		if (TRACE) TOUT.println("handle_HEADER_xx_BEGIN_REGISTERED() ENTER");
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
		if (TRACE) TOUT.println("handle_HEADER_xx_BEGIN_REGISTERED() pending_last_signal_index="+pending_last_signal_index+" declared_chunk_size="+declared_chunk_size+",  LEAVE");
	};
	/** Invoked when needs to process "end-begin-registered" header,
	either during name processing or during stand alone processing.
	@param h  HEADER_END
	*/
	private void handle_HEADER_END(int h)
	{
		if (TRACE) TOUT.println("handle_HEADER_END() ENTER");
		assert(((byte)(h & 0b111))==AChunkWriteFormat0.HEADER_END);
		int s = h >>>3; 
		//arm chunk buffer.
		this.declared_chunk_size = s;
		this.buffer_size =0;
		this.buffer_at=0;
		//arm info
		this.pending_name = null;
		if (TRACE) TOUT.println("handle_HEADER_END() declared_chunk_size="+declared_chunk_size+" LEAVE");
	};
	
	/** Invoked when needs to process "extended registered" header,
	either during name processing or during stand alone processing.
	@param h  HEADER_END
	@return either SIG_BEGIN_REGISTERED or SIG_END_BEGIN_REGISTERED
	@throws IOException if down-stream failed
	@throws EUnexpectedEof if encountered end of file in header
	*/
	private TSignalReg handle_HEADER_EXTENDED_REGISTERED(int h)throws IOException
	{
		if (TRACE) TOUT.println("handle_HEADER_EXTENDED_REGISTERED()");
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
		if ((r & 0x80)!=0)
		{
				if (TRACE) TOUT.println("handle_HEADER_EXTENDED_REGISTERED()=SIG_END_BEGIN_REGISTERED declared_chunk_size="+declared_chunk_size+" LEAVE");
				return TSignalReg.SIG_END_BEGIN_REGISTERED;
		}else
		{		
				if (TRACE) TOUT.println("handle_HEADER_EXTENDED_REGISTERED()=SIG_BEGIN_REGISTERED declared_chunk_size="+declared_chunk_size+" LEAVE");
				return TSignalReg.SIG_BEGIN_REGISTERED;
		}
	};
	
	/** Loads signal name, avoiding limitless loading 
	@param b where to load, will be override existing content 
			and clear it prior to loading anything. 
	@throws IOException if failed
	@throws EFormatBoundaryExceeded if name is too long.
	*/
	private void loadSignalNameTo(StringBuilder b)throws IOException
	{
		if (TRACE) TOUT.println("loadSignalNameTo ENTER");
		b.setLength(0);
		final int max = getMaxSignalNameLength();
		int s = decodeString(b,max);
		//Now we need to check if some data are un-read.
		//Notice calling hasElementaryData() may create a tricky situation
		//due to opened way for overriding it and some logic cycle possible
		//so we call directly chunk payload content test ignoring boolean
		//content pending.
		if ((s==max)&&(hasUnreadPayload())) throw new EFormatBoundaryExceeded("Signal name too long");
		if (TRACE) TOUT.println("loadSignalNameTo=\""+b+"\", LEAVE");
	};
	
	
	@Override protected TSignalReg readSignalReg()throws IOException
	{
		if (TRACE) TOUT.println("readSignalReg() ENTER");
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
					if (TRACE) TOUT.println("readSignalReg() skipping content");
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
				if (TRACE) TOUT.println("readSignalReg() pre-fetching next header");
				//pre-fetch header.
				int r = raw.read();
				assert(r>=-1);
				assert(r<=0xFF);
				if (r==-1) throw new EUnexpectedEof();
				h = r;	
			};
			//Now we are sure we have a header. What is it?
			if (TRACE) TOUT.println("readSignalReg() switching to header->");
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
						if (TRACE) TOUT.println("readSignalReg(), looping on CONTINUE");
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
