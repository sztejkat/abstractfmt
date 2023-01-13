package sztejkat.abstractfmt.bin.escape;
import  sztejkat.abstractfmt.bin.ABinReadFormat;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.InputStream;


/**
	A low level, signal and escaping for escape based read format.
	<p>
	This class provides signal encoding and payload escaping.
*/
abstract class AEscapeReadFormat0 extends ABinReadFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(AEscapeReadFormat0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AEscapeReadFormat0.",AEscapeReadFormat0.class) : null;

					/** Raw input stream */
					private final InputStream raw;
					/** Nothing is in {@link #pending_byte} */
					private final int PENDING_NONE = 0;
					/** A header is in {@link #pending_byte} */
					private final int PENDING_HEADER = 1;
					/** A data byte is in {@link #pending_byte} */
					private final int PENDING_DATA = 2;
					/** One of PENDIG_xxx constants, describes what is
					in {@link #pending_byte}*/
					private int is_pending;
					/** A pending header or pending data  */
					private byte pending_byte;
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
	/* *************************************************************
	
			Construction
			
	
	***************************************************************/
	/** Creates
		@param name_registry_capacity {@link ARegisteringStructReadFormat#ARegisteringStructReadFormat(int)}
			This value cannot be larger than 256. Recommended value is 256, minimum resonable is 8.
		@param raw raw input stream, non null. Will be closed.
			<p>
			This stream <u>must</u> be such, that it returns
			partial read/partial skip only if there is actually no data in stream, timeout happen, file
			was fully read or connection is broken. If this stream will return partial reads
			or partial skips just "because i like it" this format will report {@link EUnexpectedEof}.
	*/
	AEscapeReadFormat0(int name_registry_capacity, InputStream raw)
	{
		super(name_registry_capacity);
		if (TRACE) TOUT.println("new AEscapeReadFormat0(name_registry_capacity="+name_registry_capacity+")");
		assert(name_registry_capacity<=256):"name registry too large";
		assert(raw!=null);
		this.raw = raw;
	};
	
	/* **************************************************************************
	
			ABinReadFormat
	
	
	* ***************************************************************************/
	/** Like {@link #in}, but does not probe any pending state, instead updates
	it eventually. 
	@return <ul>
				<li>-1 if reached signal updated {@link #is_pending} and {@link #pending_byte};</li>
				<li>-2 if reached end-of-file;</li>
				<li>otherwise 0...0xff representing a byte from payload;</li>
			</ul>
	@throws IOException if low level failed
	@throws EBrokenFormat if found structural problem.
	@throws EUnexpectedEof if found an escape sequence and could not fetch next byte.
	@throws AssertionError if {@link #is_pending} is not {@link #PENDING_NONE}.
	*/
	private int inImpl()throws IOException
	{		
		assert(is_pending==PENDING_NONE);
		int i = raw.read();
		if (i==-1)
		{
			if (DUMP) TOUT.println("inImpl()=-2");
			return -2; //here return gracefully.
		};
		if (i==AEscapeWriteFormat0.ESCAPE)
		{
			if (DUMP) TOUT.println("inImpl(), un-escaping");
			//can be signal or data?
			i = raw.read();
			switch(i)
			{
				case -1: throw new EUnexpectedEof();
				case AEscapeWriteFormat0.ESCAPE:
						//a data
						if (DUMP) TOUT.println("inImpl()=0x"+Integer.toHexString(i));
						return i;
				default:
					//a signal. Keep it for future processing
					this.is_pending = PENDING_HEADER;
					this.pending_byte = (byte)i;
					if (DUMP) TOUT.println("inImpl()=-1, found heder=0x"+Integer.toHexString(i));
					return -1;
			}
		}else
		{
			//non-escaped content
			if (DUMP) TOUT.println("inImpl()=0x"+Integer.toHexString(i));
			return i;
		}
	};
	@Override protected int in()throws IOException
	{
		if (DUMP) TOUT.println("in() ENTER");
		//handle pending state
		switch(is_pending)
		{
			case PENDING_NONE:
					//update eventual pending state from stream
					{
						int i = inImpl();
						assert(i>=-2);
						assert(i<=0xFF);
						if (i==-2) throw new EUnexpectedEof();
						if (DUMP) TOUT.println("in()=0x"+Integer.toHexString(i)+" LEAVE");
						return i;
					}
			case PENDING_HEADER:
					//header is already fetched but not processed yet.
					if (DUMP) TOUT.println("in()=-1, PENDING_HEADER, LEAVE");
					return -1;
			case PENDING_DATA:
					//some data were pre-fetched by hasUnreadPayload,
					//needs to return it.
					is_pending = PENDING_NONE;
					if (DUMP) TOUT.println("in()="+Integer.toHexString(pending_byte & 0xFF)+", PENDING_DATA, LEAVE");
					return pending_byte & 0xFF;
			default: throw new AssertionError();
		}
	};
	@Override protected boolean hasUnreadPayload()throws IOException
	{
		if (TRACE) TOUT.println("hasUnreadPayload() ENTER");
		//handle pending state
		switch(is_pending)
		{
			case PENDING_NONE:
					{
						//there is no other way that check what is in stream
						int i = inImpl();
						if (i<0)
						{
							if (TRACE) TOUT.println("hasUnreadPayload()=false, touched signal, LEAVE");
							return false; //either signal or eof, doesn't matter.
						};
						//content was read, need to be put into a pending state
						this.is_pending = PENDING_DATA;
						this.pending_byte = (byte)i;
						if (TRACE) TOUT.println("hasUnreadPayload()=true, set PENDING_DATA, LEAVE");
						return true;
					}
			case PENDING_HEADER:
					//header is already fetched but not processed yet.
					if (TRACE) TOUT.println("hasUnreadPayload()=false, PENDING_HEADER, LEAVE");
					return false;
			case PENDING_DATA:
					//there are some pre-fetched data, so:
					if (TRACE) TOUT.println("hasUnreadPayload()=true, PENDING_DATA, LEAVE");
					return true;
			default: throw new AssertionError();
		}
	};
	/* **************************************************************************
	
			ARegisteringStructReadFormat
	
	
	* ***************************************************************************/
	/* ..........................................................................
				Name processing
	..........................................................................*/
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
	
	
	/* ..........................................................................
				Header handlers
	..........................................................................*/
	/** Header handler, direct begin header
	@param header with four less significant bits tested
	@throws IOException if failed */
	private void processHEADER_BEGIN_DIRECT(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_BEGIN_DIRECT(0x"+Integer.toHexString(header & 0xFF)+") ENTER");
		//now simply name follows
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_BEGIN_DIRECT);
		//validate format, more significant bits. 
		if ( (header & 0xFF)!= AEscapeWriteFormat0.HEADER_BEGIN_DIRECT)
				throw new EBrokenFormat("The HEADER_BEGIN_DIRECT does not take params, header=0x"+Integer.toHexString( header & 0xFF));
		processHeader_XXX_DIRECT();
		if (TRACE) TOUT.println("processHEADER_BEGIN_DIRECT() LEAVE");
	};
	/** Header handler, direct end-begin header
	@param header with four less significant bits tested
	@throws IOException if failed */
	private void processHEADER_END_BEGIN_DIRECT(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_DIRECT(0x"+Integer.toHexString(header & 0xFF)+") ENTER");
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_END_BEGIN_DIRECT);
		//validate format, more significant bits. 
		if ( (header & 0xFF)!= AEscapeWriteFormat0.HEADER_END_BEGIN_DIRECT)
				throw new EBrokenFormat("The HEADER_END_BEGIN_DIRECT does not take params, header=0x"+Integer.toHexString( header & 0xFF));
		processHeader_XXX_DIRECT();
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_DIRECT() LEAVE");
	};
	/** Common part for begin-direct and end-begin-direct header
	@throws IOException if failed */
	private void processHeader_XXX_DIRECT()throws IOException
	{
		if (TRACE) TOUT.println("processHeader_XXX_DIRECT() ENTER");
		processDirectName();
		this.pending_last_signal_index = -1;
		if (TRACE) TOUT.println("processHeader_XXX_DIRECT() LEAVE");
	}
	/** Common part for name handling in header.
	Loads name to {@link #pending_name} after validating name length
	and correctly consuming terminating end signal.
	@throws IOException if failed */
	private void processDirectName()throws IOException
	{
		if (TRACE) TOUT.println("processDirectName() ENTER");
		//In this type of header name follows and is terminated by a signal which must be an end-signal
		//and must be fetched.
		loadSignalNameTo(name_building_buffer);	//will validate size also.
		//Check if we had caught a signal? If not loadSignalNameTo would have thrown
		assert(is_pending == PENDING_HEADER);
		if (pending_byte!=AEscapeWriteFormat0.HEADER_END) throw new EBrokenFormat("HEADER_END is required after name but 0x"+Integer.toHexString(pending_byte & 0xFF)+" is found");
		//consume that header
		is_pending = PENDING_NONE;
		//udpate for TSignalReg.SIG_BEGIN_DIRECT/SIG_END_BEGIN_DIRECT
		this.pending_name = name_building_buffer.toString();
		if (TRACE) TOUT.println("processDirectName() LEAVE");
	};
	
	
	
	
	
	
	/**	Handles begin-register header in index mode
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_BEGIN_REGISTER_idx(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_BEGIN_REGISTER_idx(0x"+Integer.toHexString(header & 0xFF)+") ENTER");		
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_BEGIN_REGISTER_idx);
		//validate format, more significant bits. 
		if ( (header & 0xFF)!= AEscapeWriteFormat0.HEADER_BEGIN_REGISTER_idx)
				throw new EBrokenFormat("The HEADER_BEGIN_REGISTER_idx does not take params, header=0x"+Integer.toHexString( header & 0xFF));
		processHEADER_xxx_REGISTER_idx();
		if (TRACE) TOUT.println("processHEADER_BEGIN_REGISTER_idx() LEAVE");
	};	
	/**	Handles begin-register header in index mode
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_END_BEGIN_REGISTER_idx(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_REGISTER_idx(0x"+Integer.toHexString(header & 0xFF)+") ENTER");
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTER_idx);
		//validate format, more significant bits. 
		if ( (header & 0xFF)!= AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTER_idx)
				throw new EBrokenFormat("The HEADER_END_BEGIN_REGISTER_idx does not take params, header=0x"+Integer.toHexString( header & 0xFF));
		processHEADER_xxx_REGISTER_idx();
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_REGISTER_idx() LEAVE");
	};
	/** Common for begin-register and end-begin-register in indexed mode
	@throws IOException if failed */
	private void processHEADER_xxx_REGISTER_idx()throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_xxx_REGISTER_idx() ENTER");
		//check if not conflicting indexing mode?
		if (last_ordered_signal>=0) throw new EBrokenFormat("indexed registration header found but order indexing was used");
		//now fetch the index.
		int idx = inImpl();
		switch(idx)
		{
			case -1: //fatal index is required
					throw new EBrokenFormat("Index expected");
			case -2:
					throw new EUnexpectedEof();
			default:;	//nothing, will preserve it later.
		};
		processDirectName();
		//prepare index context.
		this.detected_indexed_mode = true;
		this.pending_last_signal_index = idx;
		if (TRACE) TOUT.println("processHEADER_xxx_REGISTER_idx(), pending_last_signal_index="+pending_last_signal_index+" LEAVE");
	};
	
	
	
	
	
	
	/**	Handles begin-register header in ordered mode.
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_BEGIN_REGISTER_ord(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_BEGIN_REGISTER_ord(0x"+Integer.toHexString(header & 0xFF)+") ENTER");		
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_BEGIN_REGISTER_ord);
		//validate format, more significant bits. 
		if ( (header & 0xFF)!= AEscapeWriteFormat0.HEADER_BEGIN_REGISTER_ord)
				throw new EBrokenFormat("The HEADER_BEGIN_REGISTER_ord does not take params, header=0x"+Integer.toHexString( header & 0xFF));
		processHEADER_xxx_REGISTER_ord();
		if (TRACE) TOUT.println("processHEADER_BEGIN_REGISTER_ord() LEAVE");
	};
	/**	Handles begin-register header in ordered mode.
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_END_BEGIN_REGISTER_ord(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_REGISTER_ord(0x"+Integer.toHexString(header & 0xFF)+") ENTER");
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTER_ord);
		//validate format, more significant bits. 
		if ( (header & 0xFF)!= AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTER_ord)
				throw new EBrokenFormat("The HEADER_END_BEGIN_REGISTER_ord does not take params, header=0x"+Integer.toHexString( header & 0xFF));
		processHEADER_xxx_REGISTER_ord();
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_REGISTER_ord() LEAVE");
	};
	/** Common for begin-register and end-begin-register in order-mode
	@throws IOException if failed */
	private void processHEADER_xxx_REGISTER_ord()throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_xxx_REGISTER_ord() ENTER");
		//check if not conflicting indexing mode?
		if (detected_indexed_mode) throw new EBrokenFormat("order based registration header found but index mode was used");
		//now fetch the index.
		int idx = ++last_ordered_signal;
		//Below condition won't ever trigger due to registrty limits.
		//so assertion is enough.
		assert(this.last_ordered_signal>=0);
		//for TSignalReg.SIG_BEGIN_AND_REGISTER/SIG_END_BEGIN_AND_REGISTER
		processDirectName();
		//prepare index context.
		this.pending_last_signal_index = idx;
		if (TRACE) TOUT.println("processHEADER_xxx_REGISTER_ord(), pending_last_signal_index="+pending_last_signal_index+" LEAVE");
	};
	
	
	
	
	/**	Handles begin-registered in short mode
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_BEGIN_REGISTERED_short(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_BEGIN_REGISTERED_short(0x"+Integer.toHexString(header & 0xFF)+") ENTER");
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_BEGIN_REGISTERED_short);
		processHEADER_xxx_REGISTERED_short(header);
		if (TRACE) TOUT.println("processHEADER_BEGIN_REGISTERED_short() LEAVE");
	};
	/**	Handles end-begin-registered in short mode
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_END_BEGIN_REGISTERED_short(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_REGISTERED_short(0x"+Integer.toHexString(header & 0xFF)+") ENTER");		
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTERED_short);
		processHEADER_xxx_REGISTERED_short(header);
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_REGISTERED_short() LEAVE");
	};
	/** Common for begin-registered and end-begin-registered in short mode
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_xxx_REGISTERED_short(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_xxx_REGISTERED_short(0x"+Integer.toHexString(header & 0xFF)+") ENTER");
		int idx = (header & 0b0111_0000)>>4;
		//for TSignalReg.SIG_BEGIN_REGISTERED/SIG_END_BEGIN_REGISTERED
		this.pending_name = null;
		this.pending_last_signal_index = idx;
		if (TRACE) TOUT.println("processHEADER_xxx_REGISTERED_short() pending_last_signal_index="+pending_last_signal_index+" LEAVE");
	};
	
	
	
	
	
	
	/**	Handles begin-registered in long mode
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_BEGIN_REGISTERED_long(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_BEGIN_REGISTERED_long(0x"+Integer.toHexString(header & 0xFF)+") ENTER");		
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_BEGIN_REGISTERED_long);
		if ( (header & 0xFF)!= AEscapeWriteFormat0.HEADER_BEGIN_REGISTERED_long)
				throw new EBrokenFormat("The HEADER_BEGIN_REGISTERED_long does not take params, header=0x"+Integer.toHexString( header & 0xFF));		
		processHEADER_xxx_REGISTERED_long();
		if (TRACE) TOUT.println("processHEADER_BEGIN_REGISTERED_long() LEAVE");
	};
	/**	Handles end-begin-registered in long mode
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_END_BEGIN_REGISTERED_long(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_REGISTERED_long(0x"+Integer.toHexString(header & 0xFF)+") ENTER");		
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTERED_long);
		if ( (header & 0xFF)!= AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTERED_long)
				throw new EBrokenFormat("The HEADER_END_BEGIN_REGISTERED_long does not take params, header=0x"+Integer.toHexString( header & 0xFF));
		processHEADER_xxx_REGISTERED_long();
		if (TRACE) TOUT.println("processHEADER_END_BEGIN_REGISTERED_long() LEAVE");
	};
	/** Common for begin-registered and end-begin-registered in long mode
	@throws IOException if failed
	*/
	private void processHEADER_xxx_REGISTERED_long()throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_xxx_REGISTERED_long() ENTER");
		//fetch byte index.
		int idx = inImpl();
		switch(idx)
		{
			case -1: throw new EBrokenFormat("Expected index, but signal found");
			case -2: throw new EUnexpectedEof();
			default:;
		};
		//for TSignalReg.SIG_BEGIN_REGISTERED/SIG_END_BEGIN_REGISTERED
		this.pending_name = null;
		this.pending_last_signal_index = idx;
		if (TRACE) TOUT.println("processHEADER_xxx_REGISTERED_long() pending_last_signal_index="+pending_last_signal_index+" LEAVE");
	};
	
	
	
	/**	Handles end header
	@param header header, after checking 4 lsb.
	@throws IOException if failed
	*/
	private void processHEADER_END(byte header)throws IOException
	{
		if (TRACE) TOUT.println("processHEADER_END(0x"+Integer.toHexString(header & 0xFF)+") ENTER");
		assert( (header & 0x0F)==AEscapeWriteFormat0.HEADER_END);
		if ( (header & 0xFF)!= AEscapeWriteFormat0.HEADER_END)
				throw new EBrokenFormat("The HEADER_END does not take params, header=0x"+Integer.toHexString( header & 0xFF));
		//Nothing special.
		if (TRACE) TOUT.println("processHEADER_END() LEAVE");
	};
	
	
	
	
	/* ..........................................................................
				Superclass requested.
	..........................................................................*/
	@SuppressWarnings("fallthrough")
	@Override protected TSignalReg readSignalReg()throws IOException
	{
		if (TRACE) TOUT.println("readSignalReg() ENTER");
		//We can enter with any pending state
		for(;;)
		{			
			switch(is_pending)
			{
				case PENDING_NONE:
							if (TRACE) TOUT.println("readSignalReg(), PENDING_NONE, reading");
							//unknown, need to read
							int i = inImpl(); //will toggle state to PENDING_HEADER
							assert(i>=-2);
							assert(i<=0xFF);
							if (i==-2) throw new EUnexpectedEof();
							if (i!=-1)
							{ 
									if (TRACE) TOUT.println("readSignalReg(), data, looping"); 
									break;
							};
							//fall-through if -1 because it indicates
							//that pending state was updated.
				case PENDING_HEADER:
							if (TRACE) TOUT.println("readSignalReg(), PENDING_HEADER, header code=0x"+Integer.toHexString(pending_byte & 0x0F));
							//Some header is pending
							this.is_pending = PENDING_NONE;	//we consume it.
							switch(pending_byte & 0x0F)
							{
								case AEscapeWriteFormat0.HEADER_BEGIN_DIRECT:
										processHEADER_BEGIN_DIRECT(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_BEGIN_DIRECT, LEAVE");
										return TSignalReg.SIG_BEGIN_DIRECT;
								case AEscapeWriteFormat0.HEADER_END_BEGIN_DIRECT:
										processHEADER_END_BEGIN_DIRECT(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_END_BEGIN_DIRECT, LEAVE");
										return TSignalReg.SIG_END_BEGIN_DIRECT;
								case AEscapeWriteFormat0.HEADER_BEGIN_REGISTER_idx:
										processHEADER_BEGIN_REGISTER_idx(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_BEGIN_AND_REGISTER, LEAVE");
										return TSignalReg.SIG_BEGIN_AND_REGISTER;
								case AEscapeWriteFormat0.HEADER_BEGIN_REGISTER_ord:
										processHEADER_BEGIN_REGISTER_ord(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_BEGIN_AND_REGISTER, LEAVE");
										return TSignalReg.SIG_BEGIN_AND_REGISTER;
								case AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTER_idx:
										processHEADER_END_BEGIN_REGISTER_idx(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_END_BEGIN_AND_REGISTER, LEAVE");
										return TSignalReg.SIG_END_BEGIN_AND_REGISTER;
								case AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTER_ord:
										processHEADER_END_BEGIN_REGISTER_ord(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_END_BEGIN_AND_REGISTER, LEAVE");
										return TSignalReg.SIG_END_BEGIN_AND_REGISTER;
								case AEscapeWriteFormat0.HEADER_BEGIN_REGISTERED_short:
										processHEADER_BEGIN_REGISTERED_short(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_BEGIN_REGISTERED, LEAVE");
										return TSignalReg.SIG_BEGIN_REGISTERED;
								case AEscapeWriteFormat0.HEADER_BEGIN_REGISTERED_long:
										processHEADER_BEGIN_REGISTERED_long(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_BEGIN_REGISTERED, LEAVE");
										return TSignalReg.SIG_BEGIN_REGISTERED;
								case AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTERED_short:
										processHEADER_END_BEGIN_REGISTERED_short(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_END_BEGIN_REGISTERED, LEAVE");
										return TSignalReg.SIG_END_BEGIN_REGISTERED;
								case AEscapeWriteFormat0.HEADER_END_BEGIN_REGISTERED_long:
										processHEADER_END_BEGIN_REGISTERED_long(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_END_BEGIN_REGISTERED, LEAVE");
										return TSignalReg.SIG_END_BEGIN_REGISTERED;
								case AEscapeWriteFormat0.HEADER_END:
										processHEADER_END(pending_byte);
										if (TRACE) TOUT.println("readSignalReg()=SIG_END, LEAVE");
										return TSignalReg.SIG_END;
								default: throw new EBrokenFormat("Unknown header code 0x"+Integer.toHexString(pending_byte & 0xFF));
							}
				case PENDING_DATA:
							//drop them
							if (TRACE) TOUT.println("readSignalReg(), PENDING_DATA, looping");
							this.is_pending = PENDING_NONE;
							break;
				default:
						throw new AssertionError();
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
	/* ***********************************************************************
		
				AStructReadFormat0
		
	************************************************************************/
	/** Overriden to close raw output */
	@Override protected void closeImpl()throws IOException
	{
		if (TRACE) TOUT.println("closeImpl()");
		raw.close();
	};
};