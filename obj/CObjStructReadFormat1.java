package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.AStructReadFormatBase0;
import sztejkat.abstractfmt.ARegisteringStructReadFormat;
import sztejkat.abstractfmt.ARegisteringStructWriteFormat;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.ENoMoreData;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import sztejkat.abstractfmt.IFormatLimits;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.IRollbackPollable;
import sztejkat.abstractfmt.utils.IPollable;
import sztejkat.abstractfmt.utils.CRollbackPollable;
import java.util.Iterator;
import java.io.IOException;
/**
	Format reading {@link IObjStructFormat1} stream.
	<p>
	This stream is <u>intentionally</u> implemented as non-type checking
	even tough type information <u>is</u> stored in stream. As an effect
	this stream allows abusive reads of miss-matched types and blocks.
	This strategy better reflects how streams will be used in a real life
	and allows better testing of defensive functionality.
	<p>
	This stream has no support for temporary lack of data 
	(<a href="../IStructReadFormat.html#TEMPEOF">"None"</a>). 
	
	
	@see CObjStructWriteFormat1
*/
public class CObjStructReadFormat1 extends ARegisteringStructReadFormat 
{			
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CObjStructReadFormat0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CObjStructReadFormat0.",AStructReadFormatBase0.class) : null;
	
				/** A collection to which objects representing
				stream operations are added. */
				public final IRollbackPollable<IObjStructFormat0> stream;
				/** A bounadry of format */
				private final int max_supported_recursion_depth;
				/** A bounadry of format */
				private final int max_supported_name_length;
				/** Controls if name registry is build using index
				or order.
				<p>
				Note: Even tough in this type of stream both are present
				we need to be able to select just one of them. */
				private final boolean use_index_instead_of_order;
				/** Used by {@link #pickLastSignalRegName} */
				private String last_signal_reg_name;
				/** Used by {@link #pickLastSignalIndex} */
				private int last_signal_index;
				/** Used only to validate if writing and wrote order correctly */
				private int order_tracking;
	/** Creates
	@param stream stream to read content from, non null. 
	@param max_supported_recursion_depth see {@link IFormatLimits#getMaxSupportedStructRecursionDepth}
	@param max_supported_name_length see {@link IFormatLimits#getMaxSupportedSignalNameLength}
	@param use_index_instead_of_order if true index attached to registered name will be used,
			if false order. Regular streams will have this option on writing side, we use
			it on reading side to test implementations more thoughly.
	@param name_registry_capacity capactity of name registry used
			to support {@link ARegisteringStructWriteFormat#optimizeBeginName}.
			Zero to disable optimization. This value should be at least equal
			to value used at writing side or an error will occur. 
    */
	public CObjStructReadFormat1(IRollbackPollable<IObjStructFormat0> stream,
								  int max_supported_recursion_depth,
								  int max_supported_name_length,
								  boolean use_index_instead_of_order,
								  int name_registry_capacity
								  )
	{
		super(name_registry_capacity);
		assert(max_supported_name_length>0);
		assert(max_supported_recursion_depth>=-1);
		assert(stream!=null);
		
		this.stream = stream;
		this.max_supported_recursion_depth=max_supported_recursion_depth;
		this.max_supported_name_length=max_supported_name_length;
		this.use_index_instead_of_order = use_index_instead_of_order;
		
		trimLimitsToSupportedLimits();
	};
	/** Creates
	@param stream stream to read content from, non null. 
	@param max_supported_recursion_depth see {@link IFormatLimits#getMaxSupportedStructRecursionDepth}
	@param max_supported_name_length see {@link IFormatLimits#getMaxSupportedSignalNameLength}
	@param use_index_instead_of_order if true index attached to registered name will be used,
			if false order. Regular streams will have this option on writing side, we use
			it on reading side to test implementations more thoughly.
	@param name_registry_capacity capactity of name registry used
			to support {@link ARegisteringStructWriteFormat#optimizeBeginName}.
			Zero to disable optimization. This value should be at least equal
			to value used at writing side or an error will occur. 
    */
	public CObjStructReadFormat1(IPollable<IObjStructFormat0> stream,
								  int max_supported_recursion_depth,
								  int max_supported_name_length,
								  boolean use_index_instead_of_order,
								  int name_registry_capacity
								  )
	{
		this( new CRollbackPollable<IObjStructFormat0>(stream),
				max_supported_recursion_depth,
				max_supported_name_length,
				use_index_instead_of_order,
				name_registry_capacity
				);
	};
	/* ***********************************************************************
		
				AStructReadFormatBase1
				
		
	************************************************************************/
	private void validateName(String n)throws EFormatBoundaryExceeded
	{
		//Note: Real implementation shoudl do it before fetching name from a stream
		//		or during fetching. We can do it afterwards.
		if (n.length()>getMaxSignalNameLength()) throw new EFormatBoundaryExceeded("name too long");
	};
	@Override protected boolean hasElementaryDataImpl()throws IOException
	{
		if (TRACE) TOUT.println("hasElementaryDataImpl() ENTER");
		IObjStructFormat0 item = stream.peek();
		if (item==null)
		{
			//if there is nothing we do not have anything to skip.
			if (TRACE) TOUT.println("hasElementaryDataImpl()=false, eof LEAVE");
			return false;
		};
		if (item.isSignal())
		{
			if (TRACE) TOUT.println("hasElementaryDataImpl()=false, LEAVE");
			return false;
		}else
		{
			if (TRACE) TOUT.println("hasElementaryDataImpl()=true, LEAVE");
			return true;
		}
	};
	@Override protected TSignalReg readSignalReg()throws IOException
	{
		if (TRACE) TOUT.println("readSignalReg() ENTER");
		IObjStructFormat0 item;
		while((item=stream.poll())!=null)
		{
			if (TRACE) TOUT.println("readSignalReg() item="+item);
			if (item instanceof SIG_BEGIN)
			{
				 String n = ((SIG_BEGIN)item).name;
				 validateName(n);
			     this.last_signal_reg_name = n; 
			     this.last_signal_index = -1; //to make sure picking will fail
				 return TSignalReg.SIG_BEGIN_DIRECT;
			};
			if (item instanceof SIG_BEGIN_AND_REGISTER)
			{
				 SIG_BEGIN_AND_REGISTER sig = (SIG_BEGIN_AND_REGISTER)item; 
				 String n = sig.name;
				 validateName(n);
			     this.last_signal_reg_name = n; 
			     this.last_signal_index = use_index_instead_of_order ? sig.index : sig.order;
			     
			     assert(sig.order == order_tracking);
			     order_tracking++;
			     
				 return TSignalReg.SIG_BEGIN_AND_REGISTER;
			};
			if (item instanceof SIG_BEGIN_REGISTERED)
			{
				 SIG_BEGIN_REGISTERED sig = (SIG_BEGIN_REGISTERED)item; 
				 this.last_signal_reg_name = null; 
			     this.last_signal_index = use_index_instead_of_order ? sig.index : sig.order;
				 return TSignalReg.SIG_BEGIN_REGISTERED;
			};
			if (item instanceof SIG_END)
			{
				 this.last_signal_reg_name = null;
				 this.last_signal_index = -1; //to make sure picking will fail
				 return TSignalReg.SIG_END;
			};
			if (item instanceof SIG_END_BEGIN)
			{
			     String n = ((SIG_END_BEGIN)item).name;
				 validateName(n);
			     this.last_signal_reg_name = n; 
			     this.last_signal_index = -1; //to make sure picking will fail
				 return TSignalReg.SIG_END_BEGIN_DIRECT;
			};
			if (item instanceof SIG_END_BEGIN_AND_REGISTER)
			{
				 SIG_END_BEGIN_AND_REGISTER sig = (SIG_END_BEGIN_AND_REGISTER)item; 
				 String n = sig.name;
				 validateName(n);
			     this.last_signal_reg_name = n; 
			     this.last_signal_index = use_index_instead_of_order ? sig.index : sig.order;
			     
			     if(sig.order != order_tracking) throw new EBrokenFormat();
			     order_tracking++;
			     
				 return TSignalReg.SIG_END_BEGIN_AND_REGISTER;
			};
			if (item instanceof SIG_END_BEGIN_REGISTERED)
			{
				 SIG_END_BEGIN_REGISTERED sig = (SIG_END_BEGIN_REGISTERED)item; 
				 this.last_signal_reg_name = null; 
			     this.last_signal_index = use_index_instead_of_order ? sig.index : sig.order;
				 return TSignalReg.SIG_END_BEGIN_REGISTERED;
			};
		};
		throw new EUnexpectedEof();
	};
	@Override protected int pickLastSignalIndex()
	{
		return last_signal_index;
	};
	@Override protected String pickLastSignalRegName()
	{
		final String n = last_signal_reg_name;
		last_signal_reg_name = null;
		return n;
	};
	/* ***********************************************************************
		
				AStructReadFormatBase0
				
		
	************************************************************************/
	
	/** Picks next {@link IObjStructFormat0Value} and returns it
	or throws an apropriate exception. This is for elementary operations.
	@return never null. 
	@throws EUnexpectedEof on no more data in iterator
	@throws ENoMoreData on signal (signal is rolled back)
	*/
	private IObjStructFormat0Value nextValue()throws IOException
	{
		IObjStructFormat0 item = stream.poll();
		if (item==null) throw new EUnexpectedEof();
		if (item.isSignal())
		{
				stream.rollback();
				throw new ENoMoreData(); //due to our natrue there is no ESignalCrossed possible.
		};
		assert(item instanceof IObjStructFormat0Value);
		return (IObjStructFormat0Value)item;
	};
	@Override protected boolean readBooleanImpl()throws IOException
	{
		return nextValue().booleanValue();
	};
	@Override protected byte readByteImpl()throws IOException
	{
		return nextValue().byteValue();
	};
	@Override protected char readCharImpl()throws IOException
	{
		return nextValue().charValue();
	};
	@Override protected short readShortImpl()throws IOException
	{
		return nextValue().shortValue();
	};
	@Override protected int readIntImpl()throws IOException
	{
		return nextValue().intValue();
	};
	@Override protected long readLongImpl()throws IOException
	{
		return nextValue().longValue();
	};
	@Override protected float readFloatImpl()throws IOException
	{
		return nextValue().floatValue();
	};
	@Override protected double readDoubleImpl()throws IOException
	{
		return nextValue().doubleValue();
	};
	//Note: Blocks are implemented exactly the same way, without type checking what is
	//intentional.
	/** Picks next {@link IObjStructFormat0Value} and returns it
	or throws an apropriate exception. This is for block operations.
	@param r number of items read up to now.
	@return null at signal or no more data if r!=0
	@throws EUnexpectedEof on no more data in iterator, but only if r==0 
	@throws ENoMoreData on signal (signal is rolled back)
	*/
	private IObjStructFormat0Value nextBlockValue(int r)throws IOException
	{
		//See block API for details.
		IObjStructFormat0 item =stream.poll();
		if (item==null)
		{
			if (r==0) throw new EUnexpectedEof();
		 	else return null;
		};
		assert(item!=null);
		if (item.isSignal())
		{
				stream.rollback();
				return null;
		};
		assert(item instanceof IObjStructFormat0Value);
		return (IObjStructFormat0Value)item;
	};
	@Override protected int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException
	{
		int r = 0;
		while(length-->0)
		{
			IObjStructFormat0Value i = nextBlockValue(r);
			if (i==null)
			{
				 return r==0 ? -1: r;
			}else
			{
				buffer[offset++]= i.booleanValue();
				r++;
			};
		};
		return r;
	};
	@Override protected boolean readBooleanBlockImpl()throws IOException
	{
		return nextValue().booleanValue();
	};
	@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
	{
		int r = 0;
		while(length-->0)
		{
			IObjStructFormat0Value i = nextBlockValue(r);
			if (i==null)
			{
				 return r==0 ? -1: r;
			}else
			{
				buffer[offset++]= i.byteValue();
				r++;
			};
		};
		return r;
	};
	@Override protected byte readByteBlockImpl()throws IOException
	{
		return nextValue().byteValue();
	};
	@Override protected int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException
	{
		int r = 0;
		while(length-->0)
		{
			IObjStructFormat0Value i = nextBlockValue(r);
			if (i==null)
			{
				 return r==0 ? -1: r;
			}else
			{
				buffer[offset++]= i.charValue();
				r++;
			};
		};
		return r;
	};
	@Override protected char readCharBlockImpl()throws IOException
	{
		return nextValue().charValue();
	};
	@Override protected int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException
	{
		int r = 0;
		while(length-->0)
		{
			IObjStructFormat0Value i = nextBlockValue(r);
			if (i==null)
			{
				 return r==0 ? -1: r;
			}else
			{
				buffer[offset++]= i.shortValue();
				r++;
			};
		};
		return r;
	};
	@Override protected short readShortBlockImpl()throws IOException
	{
		return nextValue().shortValue();
	};
	@Override protected int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException
	{
		int r = 0;
		while(length-->0)
		{
			IObjStructFormat0Value i = nextBlockValue(r);
			if (i==null)
			{
				 return r==0 ? -1: r;
			}else
			{
				buffer[offset++]= i.intValue();
				r++;
			};
		};
		return r;
	};
	@Override protected int readIntBlockImpl()throws IOException
	{
		return nextValue().intValue();
	};
	@Override protected int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException
	{
		int r = 0;
		while(length-->0)
		{
			IObjStructFormat0Value i = nextBlockValue(r);
			if (i==null)
			{
				 return r==0 ? -1: r;
			}else
			{
				buffer[offset++]= i.longValue();
				r++;
			};
		};
		return r;
	};
	@Override protected long readLongBlockImpl()throws IOException
	{
		return nextValue().longValue();
	};
	@Override protected int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException
	{
		int r = 0;
		while(length-->0)
		{
			IObjStructFormat0Value i = nextBlockValue(r);
			if (i==null)
			{
				 return r==0 ? -1: r;
			}else
			{
				buffer[offset++]= i.floatValue();
				r++;
			};
		};
		return r;
	};
	@Override protected float readFloatBlockImpl()throws IOException
	{
		return nextValue().floatValue();
	};
	@Override protected int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException
	{
		int r = 0;
		while(length-->0)
		{
			IObjStructFormat0Value i = nextBlockValue(r);
			if (i==null)
			{
				 return r==0 ? -1: r;
			}else
			{
				buffer[offset++]= i.doubleValue();
				r++;
			};
		};
		return r;
	};
	@Override protected double readDoubleBlockImpl()throws IOException
	{
		return nextValue().doubleValue();
	};
	@Override protected int readStringImpl(Appendable characters, int length)throws IOException
	{
		int r = 0;
		while(length-->0)
		{
			IObjStructFormat0Value i = nextBlockValue(r);
			if (i==null)
			{
				 return r==0 ? -1: r;
			}else
			{
				characters.append(i.stringValue());
				r++;
			};
		};
		return r;
	};
	@Override protected char readStringImpl()throws IOException
	{
		return nextValue().stringValue();
	};
	/** Doesn't do anything */
	@Override protected void openImpl()throws IOException{};
	/** Doesn't do anything */	
	@Override protected void closeImpl()throws IOException{};
	/* ***********************************************************************
		
				IFormatLimits
				
		
	************************************************************************/
	@Override public final int getMaxSupportedSignalNameLength(){ return max_supported_name_length; };
	@Override public final int getMaxSupportedStructRecursionDepth(){ return max_supported_recursion_depth; };
};
