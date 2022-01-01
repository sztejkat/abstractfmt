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
	A chunk-based {@link IIndicatorWriteFormat}, undescribed.
*/
public class CBinIndicatorReadFormat extends ABinIndicatorReadFormat1
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
					
	/** Creates
	@param input see {@link ABinIndicatorReadFormat1#ABinIndicatorReadFormat1}
	*/				
	protected CBinIndicatorReadFormat(
							InputStream input
							)
	{
		super(input);
		this.signal_name_buffer = new CBoundAppendable(getMaxSignalNameLength());
	};
	/* ********************************************************************
	
	
			Services required by superclass
	
	
	* *********************************************************************/
	protected int tryNextDataChunk(CAdaptivePushBackInputStream in)throws IOException
	{
		int header = in.read();
		if (header==-1) return -2;	//physical eof.
		switch(header & 0x0F)
		{
			case TBinUndescribed.DATA_SHORT:
						//Size is directly encoded.
						//as size minus 1
						return ((header & 0xF0)>>4)+1;
			case TBinUndescribed.DATA_MEDIUM:
						{
							//need to fetch one more byte.
							int b = in.read();
							if (b==-1) throw new EUnexpectedEof();
							return (
							        ((header & 0xF0)>>4)
									|
									(b<<4)
									)+1;
						}
			case TBinUndescribed.DATA_LONG:
						{
							//header ssss must be zero.
							if ((header & 0xF0)!=0) throw new EBrokenFormat("DATA(long) header is invalid");
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
		switch(header & 0x0F)
		{
			case TBinUndescribed.BEGIN_DIRECT:
								return processBEGIN_DIRECT(header, in);
			case TBinUndescribed.BEGIN_REGISTER:
								return processBEGIN_REGISTER(header, in);
			case TBinUndescribed.BEGIN_USE:
								return processBEGIN_USE(header, in);
			case TBinUndescribed.END:
								return processEND(header, in);
			case TBinUndescribed.END_BEGIN_DIRECT:
								return processEND_BEGIN_DIRECT(header, in);
			case TBinUndescribed.END_BEGIN_REGISTER:
								return processEND_BEGIN_REGISTER(header, in);
			case TBinUndescribed.END_BEGIN_USE:
								return processEND_BEGIN_USE(header, in);
			case TBinUndescribed.DATA_SHORT:		//fallthrough		
			case TBinUndescribed.DATA_MEDIUM:		//fallthrough
			case TBinUndescribed.DATA_LONG:			//fallthrough
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
		int s = ((header & 0xF0)>>4)+1;
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
		int s = ((header & 0xF0)>>4)+1;
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
		int s = ((header & 0xF0)>>4);
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
		int s = ((header & 0xF0)>>4);
		startNextIndicatorChunk(s);
		return TIndicator.END;
	};
	
	/* *******************************************************************
	
	
			IIndicatorReadFormat
	
	
	* *******************************************************************/
	/** Always false */
	@Override public final boolean isDescribed(){ return false; };
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
	<a href="doc-files/chunk-syntax-undescribed.html">format specification</a>.
	*/	
	@Override public boolean readBoolean()throws IOException
	{
		int r = readPayloadByte();
		switch(r)
		{
			case 0x00: return false;
			case 0x01: return true;
			default: throw new EBrokenFormat(r+" is not 0 or 1 and can't be boolean");
		}
	};	
};	