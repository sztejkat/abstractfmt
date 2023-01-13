package sztejkat.abstractfmt.bin.escape;
import  sztejkat.abstractfmt.bin.ABinWriteFormat;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.OutputStream;

/**
	A low level, signal and escaping for escape based write format.
	<p>
	This class provides signal encoding and payload escaping.
*/
abstract class AEscapeStructWriteFormat0 extends ABinWriteFormat
{
	     private static final long TLEVEL = SLogging.getDebugLevelForClass(AEscapeStructWriteFormat0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AEscapeStructWriteFormat0.",AEscapeStructWriteFormat0.class) : null;

			/** An escape byte ecoded as <code>int</code> for convinience */
			static final int ESCAPE = 0x80;
			/** An escape byte ecoded as <code>byte</code> for convinience */
			static final byte bESCAPE = (byte)ESCAPE;
			/** Signal code, see package description */
			static final int HEADER_BEGIN_DIRECT          = 0;
			static final int HEADER_END_BEGIN_DIRECT      = 0b0000_0001;
			static final int HEADER_BEGIN_REGISTER_idx    = 0b0000_0010;
			static final int HEADER_BEGIN_REGISTER_ord    = 0b0000_0011;
			static final int HEADER_END_BEGIN_REGISTER_idx= 0b0000_0100;
			static final int HEADER_END_BEGIN_REGISTER_ord= 0b0000_0101;
			static final int HEADER_BEGIN_REGISTERED_short     = 0b0000_0110;
			static final int HEADER_BEGIN_REGISTERED_long      = 0b0000_0111;
			static final int HEADER_END_BEGIN_REGISTERED_short = 0b0000_1000;
			static final int HEADER_END_BEGIN_REGISTERED_long  = 0b0000_1001;
			static final int HEADER_END                   = 0b0000_1010;
			
			/** True if use index based registration, false if order based */
			private final boolean indexed_registration;
			/** Raw binary input stream */
			private final OutputStream raw;
				
	/* *************************************************************
	
			Construction
			
	
	***************************************************************/
	/** Creates
		@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
			This value cannot be larger than 256. Recommended value is 256, minimum resonable is 8.
		@param raw raw binary stream to write to. Will be closed on {@link #close}.	
		@param indexed_registration if true names are registered directly, by index.
			If false names are registered indirectly, by order of appearance.
			For typed streams it is recommended to use "by index" registration.
	*/
	AEscapeStructWriteFormat0(int name_registry_capacity, 
							  OutputStream raw,
							  boolean indexed_registration
							  )
	{
		super(name_registry_capacity);
		if (TRACE) TOUT.println("new AEscapeStructWriteFormat0(name_registry_capacity="+name_registry_capacity+",indexed_registration="+indexed_registration+")");
		assert(name_registry_capacity<=256);
		this.raw = raw;
		this.indexed_registration=indexed_registration;
	};
	/* *****************************************************************************
	
			ABinWriteFormat
	
    *******************************************************************************/
    /** Writes payload byte, using escaping 
    @param v what to write 
    @throws IOException if failed.
    */
	@Override protected final void out(byte v)throws IOException
	{
		if (DUMP) TOUT.println("out(0x"+Integer.toHexString(v & 0xFF)+")");
		raw.write(v);
		if (v==bESCAPE)
		{
			if (DUMP) TOUT.println("out -> escaping");
			raw.write(bESCAPE);
		};
	}; 
	
	/* ***********************************************************************
		
				ARegisteringStructWriteFormat
		
	************************************************************************/
	/* ---------------------------------------------------------------------
	
				Signals
	
	---------------------------------------------------------------------*/
	private void  beginAndRegisterImpl_idx(String name, int index)throws IOException
	{
		if (TRACE) TOUT.println("beginAndRegisterImpl_idx(index="+index+")->");
		assert(index<=255);
		raw.write(bESCAPE);
		raw.write(HEADER_BEGIN_REGISTER_idx);
		raw.write(index);
		beginAndRegisterImpl_common(name);
	};	
	private void  beginAndRegisterImpl_order(String name)throws IOException
	{
		if (TRACE) TOUT.println("beginAndRegisterImpl_order->");
		raw.write(bESCAPE);
		raw.write(HEADER_BEGIN_REGISTER_ord);
		beginAndRegisterImpl_common(name);
	};
	private void  beginAndRegisterImpl_common(String name)throws IOException
	{
		if (TRACE) TOUT.println("beginAndRegisterImpl_common("+name+") ENTER");
		encodeString(name,0,name.length());
		raw.write(bESCAPE);
		raw.write(HEADER_END);
		if (TRACE) TOUT.println("beginAndRegisterImpl_common() LEAVE");
	};
	@Override protected void beginAndRegisterImpl(String name, int index, int order)throws IOException
	{
		if (TRACE) TOUT.println("beginAndRegisterImpl("+name+")->");
		if (indexed_registration)	
			beginAndRegisterImpl_idx(name,index);
		else
			beginAndRegisterImpl_order(name);
	};
	private void  endBeginAndRegisterImpl_idx(String name, int index)throws IOException
	{
		if (TRACE) TOUT.println("endBeginAndRegisterImpl_idx("+index+") ENTER");
		assert(index<=255);
		raw.write(bESCAPE);
		raw.write(HEADER_END_BEGIN_REGISTER_idx);
		raw.write(index);
		beginAndRegisterImpl_common(name);
		if (TRACE) TOUT.println("endBeginAndRegisterImpl_idx() LEAVE");
	};	
	private void  endBeginAndRegisterImpl_order(String name)throws IOException
	{
		if (TRACE) TOUT.println("endBeginAndRegisterImpl_order() ENTER");
		raw.write(bESCAPE);
		raw.write(HEADER_END_BEGIN_REGISTER_ord);
		beginAndRegisterImpl_common(name);
		if (TRACE) TOUT.println("endBeginAndRegisterImpl_order() LEAVE");
	};
	@Override protected void endBeginAndRegisterImpl(String name, int index, int order)throws IOException
	{
		if (TRACE) TOUT.println("endBeginAndRegisterImpl("+name+")->");
		if (indexed_registration)	
			endBeginAndRegisterImpl_idx(name,index);
		else
			endBeginAndRegisterImpl_order(name);
	};
	/** Common for both forms of beginRegistered / endBeginRegistered
	@param short_header short form header
	@param long_header long form header
	@param registered_index chosen index to write
	@throws IOException if failed.
	*/
	private void beginRegisteredCommon(							
							byte short_header,
							byte long_header,
							int registered_index
							)throws IOException
	{
		if (TRACE) TOUT.println("beginRegisteredCommon(registered_index="+registered_index+") ENTER");
		raw.write(bESCAPE);
		assert(registered_index<=255);
		if (( registered_index & ~0b0000_0111)==0)
		{
			//short form
			if (TRACE) TOUT.println("beginRegisteredCommon() short form");
			raw.write( short_header & registered_index<<4);
		}else
		{
			//long form
			if (TRACE) TOUT.println("beginRegisteredCommon() long form");
			raw.write( long_header );
			raw.write( registered_index );
		};
		if (TRACE) TOUT.println("beginRegisteredCommon() LEAVE");
	};
	@Override protected void beginRegisteredImpl(int index, int order)throws IOException
	{
		if (TRACE) TOUT.println("beginRegisteredImpl->");
		beginRegisteredCommon(
							(byte)HEADER_BEGIN_REGISTERED_short, //byte short_header,
							(byte)HEADER_BEGIN_REGISTERED_long, //byte long_header,
							indexed_registration ? index : order //int registered_index
							);
	};
	@Override protected void endBeginRegisteredImpl(int index, int order)throws IOException
	{
		if (TRACE) TOUT.println("endBeginRegisteredImpl->");
		beginRegisteredCommon(
							(byte)HEADER_END_BEGIN_REGISTERED_short, //byte short_header,
							(byte)HEADER_END_BEGIN_REGISTERED_long, //byte long_header,
							indexed_registration ? index : order //int registered_index
							);
	};
	@Override protected void beginDirectImpl(String name)throws IOException
	{
		if (TRACE) TOUT.println("beginDirectImpl("+name+") ENTER");
		raw.write(bESCAPE);
		raw.write(HEADER_BEGIN_DIRECT);
		encodeString(name,0,name.length());
		raw.write(bESCAPE);
		raw.write(HEADER_END);
		if (TRACE) TOUT.println("beginDirectImpl() LEAVE");
	};
	@Override protected void endBeginDirectImpl(String name)throws IOException
	{
		if (TRACE) TOUT.println("endBeginDirectImpl("+name+") ENTER");
		raw.write(bESCAPE);
		raw.write(HEADER_END_BEGIN_DIRECT);
		encodeString(name,0,name.length());
		raw.write(bESCAPE);
		raw.write(HEADER_END);
		if (TRACE) TOUT.println("endBeginDirectImpl() LEAVE");
	};
	@Override protected void endImpl()throws IOException
	{
		if (TRACE) TOUT.println("endImpl()");
		raw.write(bESCAPE);
		raw.write(HEADER_END);
	};
	/* ***********************************************************************
		
				AStructWriteFormat0
		
	************************************************************************/
	/** Overriden to close raw output */
	@Override protected void closeImpl()throws IOException
	{
		if (TRACE) TOUT.println("closeImpl()");
		raw.close();
	};
	/** Overriden to flush raw output */
	@Override protected void flushImpl()throws IOException
	{
		if (TRACE) TOUT.println("flushImpl()");
		//make sure payload data are flushed
		super.flushImpl();
		//now
		raw.flush();
	};
};