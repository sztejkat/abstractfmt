package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.AStructReadFormatBase0;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.ENoMoreData;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import sztejkat.abstractfmt.IFormatLimits;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.IRollbackPollable;
import sztejkat.abstractfmt.utils.IPollable;
import sztejkat.abstractfmt.utils.CRollbackPollable;
import java.util.Iterator;
import java.io.IOException;
/**
	Format reading {@link IObjStructFormat0} stream.
	<p>
	This stream is <u>intentionally</u> implemented as non-type checking
	even tough type information <u>is</u> stored in stream. As an effect
	this stream allows abusive reads of miss-matched types and blocks.
	This strategy better reflects how streams will be used in a real life
	and allows better testing of defensive functionality.
	<p>
	This stream has no support for temporary lack of data. 
	
	
	@see CObjStructWriteFormat0
*/
public class CObjStructReadFormat0 extends AStructReadFormatBase0 
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
				/** Used by {@link #pickLastSignalName} */
				private String last_signal_name;
	/** Creates
	@param stream stream to read content from, non null. 
	@param max_supported_recursion_depth see {@link IFormatLimits#getMaxSupportedStructRecursionDepth}
	@param max_supported_name_length see {@link IFormatLimits#getMaxSupportedSignalNameLength}
    */
	public CObjStructReadFormat0(IRollbackPollable<IObjStructFormat0> stream,
								  int max_supported_recursion_depth,
								  int max_supported_name_length
								  )
	{
		assert(max_supported_name_length>0);
		assert(max_supported_recursion_depth>=-1);
		assert(stream!=null);
		
		this.stream = stream;
		this.max_supported_recursion_depth=max_supported_recursion_depth;
		this.max_supported_name_length=max_supported_name_length;
		
		trimLimitsToSupportedLimits();
	};
	/** Creates
	@param stream stream to read content from, non null. 
	@param max_supported_recursion_depth see {@link IFormatLimits#getMaxSupportedStructRecursionDepth}
	@param max_supported_name_length see {@link IFormatLimits#getMaxSupportedSignalNameLength}
    */
	public CObjStructReadFormat0(IPollable<IObjStructFormat0> stream,
								  int max_supported_recursion_depth,
								  int max_supported_name_length
								  )
	{
		this( new CRollbackPollable<IObjStructFormat0>(stream),
				max_supported_recursion_depth,
				max_supported_name_length
				);
	};
	/* ***********************************************************************
		
				AStructReadFormatBase0
				
		
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
	@Override protected TSignal readSignal()throws IOException
	{
		if (TRACE) TOUT.println("readSignal() ENTER");
		IObjStructFormat0 item;
		while((item=stream.poll())!=null)
		{
			if (TRACE) TOUT.println("readSignal() item="+item);
			if (item instanceof SIG_BEGIN)
			{
				 String n = ((SIG_BEGIN)item).name;
				 validateName(n);
			     last_signal_name = n; 
				 return TSignal.SIG_BEGIN;
			};
			if (item instanceof SIG_END_BEGIN)
			{
			     String n = ((SIG_END_BEGIN)item).name;
				 validateName(n);
			     last_signal_name = n;
				 return TSignal.SIG_END_BEGIN;
			};
			if (item instanceof SIG_END)
			{
				 last_signal_name = null;
				 return TSignal.SIG_END;
			};
		};
		throw new EUnexpectedEof();
	};
	@Override protected String pickLastSignalName()
	{
		final String n = last_signal_name;
		last_signal_name = null;
		return n;
	};
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
				buffer[offset++]= i.blockBooleanValue();
				r++;
			};
		};
		return r;
	};
	@Override protected boolean readBooleanBlockImpl()throws IOException
	{
		return nextValue().blockBooleanValue();
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
				buffer[offset++]= i.blockByteValue();
				r++;
			};
		};
		return r;
	};
	@Override protected byte readByteBlockImpl()throws IOException
	{
		return nextValue().blockByteValue();
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
				buffer[offset++]= i.blockCharValue();
				r++;
			};
		};
		return r;
	};
	@Override protected char readCharBlockImpl()throws IOException
	{
		return nextValue().blockCharValue();
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
				buffer[offset++]= i.blockShortValue();
				r++;
			};
		};
		return r;
	};
	@Override protected short readShortBlockImpl()throws IOException
	{
		return nextValue().blockShortValue();
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
				buffer[offset++]= i.blockIntValue();
				r++;
			};
		};
		return r;
	};
	@Override protected int readIntBlockImpl()throws IOException
	{
		return nextValue().blockIntValue();
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
				buffer[offset++]= i.blockLongValue();
				r++;
			};
		};
		return r;
	};
	@Override protected long readLongBlockImpl()throws IOException
	{
		return nextValue().blockLongValue();
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
				buffer[offset++]= i.blockFloatValue();
				r++;
			};
		};
		return r;
	};
	@Override protected float readFloatBlockImpl()throws IOException
	{
		return nextValue().blockFloatValue();
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
				buffer[offset++]= i.blockDoubleValue();
				r++;
			};
		};
		return r;
	};
	@Override protected double readDoubleBlockImpl()throws IOException
	{
		return nextValue().blockDoubleValue();
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
				characters.append(i.blockStringValue());
				r++;
			};
		};
		return r;
	};
	@Override protected char readStringImpl()throws IOException
	{
		return nextValue().blockStringValue();
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
