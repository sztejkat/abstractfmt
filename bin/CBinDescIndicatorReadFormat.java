package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.util.CBoundAppendable;
import sztejkat.abstractfmt.util.CAdaptivePushBackInputStream;
import java.io.*;
import java.io.InputStream;

/**
	A chunk-based {@link IIndicatorWriteFormat}, described.
*/
public class CBinDescIndicatorReadFormat extends ABinIndicatorReadFormat1
{			
					/** Used to buffer signal names */
					private CBoundAppendable signal_name_buffer;
					/** Used to count registration indicators
					to provide implied registration numbers.
					This value is incremented each time 
					register event is found. */
					private int register_count;
					/** Buffer for use-register signals */
					private int use_registered_buffer;
					/** Cache for {@link #readBoolean()}, filled in from header. 
					If this value contains -1 cache is invalid, 0 is for false,
					1 for true. The valid state is necessary for {@link #getIndicator}
					to mimmic DATA status.*/
					private byte boolean_cache;
					
	/** Creates
	@param input see {@link ABinIndicatorReadFormat1#ABinIndicatorReadFormat1}
	*/				
	protected CBinDescIndicatorReadFormat(
							InputStream input
							)
	{
		super(input);
		this.boolean_cache=(byte)-1;
		this.signal_name_buffer = new CBoundAppendable(getMaxSignalNameLength());
	};
	/* ********************************************************************
	
	
			Services required by superclass
	
	
	* *********************************************************************/
	/** Overriden to manipulate {@link #boolean_cache} */
	protected void invalidateCachedData()
	{
		super.invalidateCachedData();
		this.boolean_cache=(byte)-1;
	};
	/** Overriden to manipulate {@link #boolean_cache} */
	protected boolean hasCachedData()
	{
		return (this.boolean_cache>=0)||(super.hasCachedData());
	};
	protected int tryNextDataChunk(CAdaptivePushBackInputStream in)throws IOException
	{
		int header = in.read();
		if (header==-1) return -2;	//physical eof.
		switch(header & 0x1F)
		{
			case TBinDescribed.DATA_SHORT:
						//Size is directly encoded.
						//as size minus 1
						return ((header & 0xE0)>>5)+1;
			case TBinDescribed.DATA_MEDIUM:
						{
							//need to fetch one more byte.
							int b = in.read();
							if (b==-1) throw new EUnexpectedEof();
							return (
							        ((header & 0xE0)>>5)
									|
									(b<<3)
									)+1;
						}
			case TBinDescribed.DATA_LONG:
						{
							//header ssss must be zero.
							if ((header & 0xE0)!=0) throw new EBrokenFormat("DATA(long) header is invalid");
							//need to fetch two more bytes.
							int b0 = in.read();
							if (b0==-1) throw new EUnexpectedEof();
							int b1 = in.read();
							if (b1==-1) throw new EUnexpectedEof();							
							return ((b0)|(b1<<8))+1;
						}
			default:
				//non-data header, put it back.
				in.unread((byte)header);
				return -1;
		}
	}
	protected TIndicator tryNextIndicatorChunk(CAdaptivePushBackInputStream in)throws IOException
	{
		int header = in.read();
		if (header==-1) return null;	//physical eof.
		//now decode headers
		switch(header & 0x1F)
		{
			case TBinDescribed.BEGIN_DIRECT:
								return processBEGIN_DIRECT(header, in);
			case TBinDescribed.BEGIN_REGISTER:
								return processBEGIN_REGISTER(header, in);
			case TBinDescribed.BEGIN_USE:
								return processBEGIN_USE(header, in);
			case TBinDescribed.END:
								return processEND(header, in);
			case TBinDescribed.END_BEGIN_DIRECT:
								return processEND_BEGIN_DIRECT(header, in);
			case TBinDescribed.END_BEGIN_REGISTER:
								return processEND_BEGIN_REGISTER(header, in);
			case TBinDescribed.END_BEGIN_USE:
								return processEND_BEGIN_USE(header, in);
			
								
			case TBinDescribed.TYPE_BOOLEAN:
								return processTYPE_BOOLEAN(header, in);
			case TBinDescribed.TYPE_BYTE:
								return processTYPE_BYTE(header, in);
			case TBinDescribed.TYPE_CHAR:
								return processTYPE_CHAR(header, in);	
			case TBinDescribed.TYPE_SHORT:
								return processTYPE_SHORT(header, in);												
			case TBinDescribed.TYPE_INT:
								return processTYPE_INT(header, in);												
			case TBinDescribed.TYPE_LONG:
								return processTYPE_LONG(header, in);												
			case TBinDescribed.TYPE_FLOAT:
								return processTYPE_FLOAT(header, in);												
			case TBinDescribed.TYPE_DOUBLE:
								return processTYPE_DOUBLE(header, in);	
								
			case TBinDescribed.TYPE_BOOLEAN_BLOCK:
								return processTYPE_BOOLEAN_BLOCK(header, in);
			case TBinDescribed.TYPE_BYTE_BLOCK:
								return processTYPE_BYTE_BLOCK(header, in);
			case TBinDescribed.TYPE_CHAR_BLOCK:
								return processTYPE_CHAR_BLOCK(header, in);	
			case TBinDescribed.TYPE_SHORT_BLOCK:
								return processTYPE_SHORT_BLOCK(header, in);												
			case TBinDescribed.TYPE_INT_BLOCK:
								return processTYPE_INT_BLOCK(header, in);												
			case TBinDescribed.TYPE_LONG_BLOCK:
								return processTYPE_LONG_BLOCK(header, in);												
			case TBinDescribed.TYPE_FLOAT_BLOCK:
								return processTYPE_FLOAT_BLOCK(header, in);												
			case TBinDescribed.TYPE_DOUBLE_BLOCK:
								return processTYPE_DOUBLE_BLOCK(header, in);												
								
			case TBinDescribed.DATA_SHORT:		//fallthrough		
			case TBinDescribed.DATA_MEDIUM:		//fallthrough
			case TBinDescribed.DATA_LONG:			//fallthrough
							{
								//fall back to data header processing.
								in.unread((byte)header);
								int r = tryNextDataChunk(in);
								assert(r>=0);
								startNextIndicatorChunk(r);
								return TIndicator.DATA;
							}
			default: throw new EBrokenFormat("Header 0x"+Integer.toHexString(header)+" is not valid chunk header");
		}
	};
	private void processX_BEGIN_DIRECT(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		int s = ((header & 0xE0)>>5)+1;
		//initialize readPayload
		startNextIndicatorChunk(s);
		//load name, limited
		signal_name_buffer.reset();
		readBeginDirectPayload(signal_name_buffer,getMaxSignalNameLength());
	};
	private TIndicator processBEGIN_DIRECT(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processX_BEGIN_DIRECT(header,in);
		return TIndicator.BEGIN_DIRECT;
	};
	private TIndicator processEND_BEGIN_DIRECT(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processX_BEGIN_DIRECT(header,in);
		return TIndicator.END_BEGIN_DIRECT;
	};
	
	
	private void processX_BEGIN_REGISTER(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		int s = ((header & 0xE0)>>5)+1;
		//initialize readPayload
		startNextIndicatorChunk(s);
		//load name, limited
		signal_name_buffer.reset();
		readBeginDirectPayload(signal_name_buffer,getMaxSignalNameLength());
		//figure out implied registration number
		use_registered_buffer =register_count; 
		register_count++;
	};
	private TIndicator processBEGIN_REGISTER(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processX_BEGIN_REGISTER(header,in);
		return TIndicator.BEGIN_REGISTER;
	};
	private TIndicator processEND_BEGIN_REGISTER(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processX_BEGIN_REGISTER(header,in);
		return TIndicator.END_BEGIN_REGISTER;
	};
	
	private void processX_BEGIN_USE(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		int s = ((header & 0xE0)>>5);
		//load index directly from header.
		int i = in.read();
		if (i==-1) throw new EUnexpectedEof();
		startNextIndicatorChunk(s);		
		use_registered_buffer =i;
	};
	private TIndicator processBEGIN_USE(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processX_BEGIN_USE(header,in);
		return TIndicator.BEGIN_USE;
	};
	private TIndicator processEND_BEGIN_USE(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processX_BEGIN_USE(header,in);
		return TIndicator.END_BEGIN_USE;
	};
	
	private TIndicator processEND(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		int s = ((header & 0xE0)>>5);
		startNextIndicatorChunk(s);
		return TIndicator.END;
	};
	
	private TIndicator processTYPE_BOOLEAN(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		boolean_cache = ((header & 0x20)!=0) ? (byte)1 : (byte)0;
		startNextIndicatorChunk(0);
		return TIndicator.TYPE_BOOLEAN;
	};
	private TIndicator processTYPE_BYTE(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		startNextIndicatorChunk(1);
		return TIndicator.TYPE_BYTE;
	};
	private TIndicator processTYPE_CHAR(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		startNextIndicatorChunk(2);
		return TIndicator.TYPE_CHAR;
	};
	private TIndicator processTYPE_SHORT(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		startNextIndicatorChunk(2);
		return TIndicator.TYPE_SHORT;
	};
	private TIndicator processTYPE_INT(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		startNextIndicatorChunk(4);
		return TIndicator.TYPE_INT;
	};
	private TIndicator processTYPE_LONG(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		startNextIndicatorChunk(8);
		return TIndicator.TYPE_LONG;
	};
	private TIndicator processTYPE_FLOAT(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		startNextIndicatorChunk(4);
		return TIndicator.TYPE_FLOAT;
	};
	private TIndicator processTYPE_DOUBLE(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		//extract size
		startNextIndicatorChunk(8);
		return TIndicator.TYPE_DOUBLE;
	};
	
	private void processTYPE_x_BLOCK(int header, int size_unit)throws IOException
	{
		//extract size, multiply it by size unit to get actual size in bytes. 
		int s = (header>>>5);
		startNextIndicatorChunk(s*size_unit);
	};
	private TIndicator processTYPE_BOOLEAN_BLOCK(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processTYPE_x_BLOCK(header,1);		
		return TIndicator.TYPE_BOOLEAN_BLOCK;
	};
	private TIndicator processTYPE_BYTE_BLOCK(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processTYPE_x_BLOCK(header,1);		
		return TIndicator.TYPE_BYTE_BLOCK;
	};
	private TIndicator processTYPE_CHAR_BLOCK(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processTYPE_x_BLOCK(header,1);		
		return TIndicator.TYPE_CHAR_BLOCK;
	};
	private TIndicator processTYPE_SHORT_BLOCK(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processTYPE_x_BLOCK(header,2);		
		return TIndicator.TYPE_SHORT_BLOCK;
	};
	private TIndicator processTYPE_INT_BLOCK(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processTYPE_x_BLOCK(header,4);		
		return TIndicator.TYPE_INT_BLOCK;
	};
	private TIndicator processTYPE_LONG_BLOCK(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processTYPE_x_BLOCK(header,8);		
		return TIndicator.TYPE_LONG_BLOCK;
	};
	private TIndicator processTYPE_FLOAT_BLOCK(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processTYPE_x_BLOCK(header,4);		
		return TIndicator.TYPE_FLOAT_BLOCK;
	};
	private TIndicator processTYPE_DOUBLE_BLOCK(int header, CAdaptivePushBackInputStream in)throws IOException
	{
		processTYPE_x_BLOCK(header,8);		
		return TIndicator.TYPE_DOUBLE_BLOCK;
	};
	/* *******************************************************************
	
	
			IIndicatorReadFormat
	
	
	* *******************************************************************/
	/** Always false */
	@Override public final boolean isDescribed(){ return true; };
	/** Overrided to adjust name buffer */
	@Override public void setMaxSignalNameLength(int characters)
	{
		super.setMaxSignalNameLength(characters);
		this.signal_name_buffer = new CBoundAppendable(getMaxSignalNameLength());
	};
	@Override public String getSignalName(){ return signal_name_buffer.toString(); };
	@Override public int getSignalNumber(){ return use_registered_buffer; };
	/** Do nothing */
	@Override public void open()throws IOException{ };
	
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html">format specification</a>.
	*/	
	@Override public boolean readBoolean()throws IOException
	{
		//just pick it from cache AND invalidate cache.
		assert((boolean_cache>=0)&&(boolean_cache<=1));
		
		boolean v = boolean_cache!=0;
		boolean_cache =(byte)-1;
		return v;
	};	
	
};	