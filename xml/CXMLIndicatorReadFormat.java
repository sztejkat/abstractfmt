package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.ENoMoreData;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.TIndicator;
import java.io.Reader;
import java.io.IOException;
/**
	A reading counterpart for {@link AXMLIndicatorWriteFormatBase}
	using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>.
	<p>
	Adds optional support for "full" format writing.
*/	
public class CXMLIndicatorReadFormat extends AXMLIndicatorReadFormat
{
				/** Described status */
				private final boolean is_described;
				/** Root element is undefined, use plain processing */
				private static final byte STATE_NO_ROOT_PROCESSING = (byte)0;
				/** Root element defined and open */
				private static final byte STATE_IN_ROOT = (byte)1;
				/** Root element defined, not open */
				private static final byte STATE_WAITING_FOR_ROOT = (byte)2;
				/** Root element defined, closed*/
				private static final byte STATE_ROOT_CLOSED = (byte)3;
				/** Root look-up status */
				private byte state;
				/** Used to track root element */
				private int depth;
	/** Creates 
	@param input input from which read data
	@param settings XML settings, non null. 
		If those settings carry non-null value in 
		{@link CXMLSettings#ROOT_ELEMENT} then
		this class will ensure to check if root element
		is opened before every operation and will
		start returning EOF/UnexpectedEof if root
		element is closed.		
	@param is_described true to require primitive type description data.
	*/
	public CXMLIndicatorReadFormat(final Reader input,
								   final CXMLSettings settings,
								   boolean is_described
								   )
   {
   		super(input, settings);
   		this.is_described=is_described;
   		this.state = settings.ROOT_ELEMENT==null ? STATE_NO_ROOT_PROCESSING : STATE_WAITING_FOR_ROOT;
   };
   
   /* ******************************************************
	
			IIndicatorReadFormat
	
	* *****************************************************/
	/** Returns what is specified in constructor */
	@Override public boolean isDescribed(){ return is_described; };
	/*
		Note: 
			All methods below are overriden only to support
			proper root element handling.
	*/
	@Override public TIndicator getIndicator()throws IOException
	{
		switch(state)
		{
			case STATE_NO_ROOT_PROCESSING: 	return super.getIndicator();
			case STATE_IN_ROOT:
						{
							TIndicator i = super.getIndicator();
							if ((i.FLAGS & TIndicator.IS_END)!=0)
							{
								if (depth>0)
								{
									depth--;
									if (depth==0)
									{
										state=STATE_ROOT_CLOSED;
										return TIndicator.EOF;
									};
								}
							}else
								if ((i.FLAGS & TIndicator.IS_BEGIN)!=0) depth++;
							return i;	
						}
			case STATE_WAITING_FOR_ROOT:
						{
							TIndicator i = super.getIndicator();
							if (
							    ((i.FLAGS & TIndicator.IS_BEGIN)!=0)
								||
								(!settings.ROOT_ELEMENT.equals(super.getSignalName()))
								)
								throw new EBrokenFormat("<"+settings.ROOT_ELEMENT+"> XML element required");
							depth++;
							state=STATE_IN_ROOT;
							super.next();
							return getIndicator();
						}
			case STATE_ROOT_CLOSED: return TIndicator.EOF;
			default: throw new AssertionError();
		}
	};
	private void ensureUsable()throws IOException,EUnexpectedEof
	{
		switch(state)
		{
			case STATE_ROOT_CLOSED: throw new EUnexpectedEof("</"+settings.ROOT_ELEMENT+"> is already closed.");
			case STATE_WAITING_FOR_ROOT: getIndicator(); 
		}
	};
	private void processENoMoreData()throws IOException,EUnexpectedEof
	{
		switch(state)
		{
			case STATE_IN_ROOT:
					//This may need to be turned into EUnexpectedEof.
					//Polling indicator will check it.
					getIndicator();
					if (state==STATE_ROOT_CLOSED) throw new EUnexpectedEof("reached </"+settings.ROOT_ELEMENT+">");
		};
	};
	@Override public void next()throws IOException
	{
		ensureUsable();
		super.next();
	};
	@Override public boolean readBoolean()throws IOException
	{
		ensureUsable();
		try{
				return super.readBoolean();
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	}
	@Override public byte readByte()throws IOException
	{
		ensureUsable();
		try{
				return super.readByte();
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	}
	@Override public char readChar()throws IOException
	{
		ensureUsable();
		try{
				return super.readChar();
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	}
	@Override public short readShort()throws IOException
	{
		ensureUsable();
		try{
				return super.readShort();
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	}
	@Override public int readInt()throws IOException
	{
		ensureUsable();
		try{
				return super.readInt();
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	}
	@Override public long readLong()throws IOException
	{
		ensureUsable();
		try{
				return super.readLong();
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	}
	@Override public float readFloat()throws IOException
	{
		ensureUsable();
		try{
				return super.readFloat();
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	}
	@Override public double readDouble()throws IOException
	{
		ensureUsable();
		try{
				return super.readDouble();
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	}
	
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readBooleanBlock(buffer,offset,length);
				if (r<length) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};		
	
	@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readByteBlock(buffer,offset,length);
				if (r<length) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};	
	
	@Override public int readByteBlock()throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readByteBlock();
				if (r==-1) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};	
	@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readCharBlock(buffer,offset,length);
				if (r<length) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};
	
	@Override public int readCharBlock(Appendable characters,  int length)throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readCharBlock(characters,length);
				if (r<length) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};
	
	@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readShortBlock(buffer,offset,length);
				if (r<length) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};	
	
	@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readIntBlock(buffer,offset,length);
				if (r<length) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};	
	
	@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readLongBlock(buffer,offset,length);
				if (r<length) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};	
		
	@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readFloatBlock(buffer,offset,length);
				if (r<length) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};
	
	@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		ensureUsable();
		try{
				final int r = super.readDoubleBlock(buffer,offset,length);
				if (r<length) processENoMoreData();
				return r;
			}catch(ENoMoreData ex)
			{
				processENoMoreData();
				throw ex;
			}
	};		
};