package sztejkat.abstractfmt;
import java.io.IOException;

/**
	Adapter which just passes all calls to underling indicator
*/
public class CIndicatorWriteFormatAdapter implements IIndicatorWriteFormat
{
				protected final IIndicatorWriteFormat out;
				
	public CIndicatorWriteFormatAdapter(IIndicatorWriteFormat out)
	{
		assert(out!=null);
		this.out = out;
	};
	public int getMaxRegistrations(){ return out.getMaxRegistrations(); };
	public boolean isDescribed(){ return out.isDescribed(); };
	public boolean isFlushing(){ return out.isFlushing(); };
	public int getMaxSupportedSignalNameLength(){ return out.getMaxSupportedSignalNameLength(); };
	
	public void writeBeginDirect(String signal_name)throws IOException{ out.writeBeginDirect(signal_name);};
	public void writeEndBeginDirect(String signal_name)throws IOException{ out.writeEndBeginDirect(signal_name);};
	public void writeBeginRegister(String signal_name, int number)throws IOException
	{ 
		out.writeBeginRegister(signal_name,number);
	};
	public void writeEndBeginRegister(String signal_name, int number)throws IOException		
	{ 
		out.writeEndBeginRegister(signal_name,number);
	};	
	public void writeBeginUse(int number)throws IOException{out.writeBeginUse(number); };
	public void writeEndBeginUse(int number)throws IOException{out.writeEndBeginUse(number); };
	public void writeEnd()throws IOException{out.writeEnd(); };
	
	public void writeType(TIndicator type)throws IOException{ out.writeType(type); };
	public void writeFlush(TIndicator flush)throws IOException{ out.writeFlush(flush); };
	
	
	
	public void writeBoolean(boolean v)throws IOException{ out.writeBoolean(v); };
	public void writeByte(byte v)throws IOException{ out.writeByte(v); };
	public void writeChar(char v)throws IOException{ out.writeChar(v); };
	public void writeShort(short v)throws IOException{ out.writeShort(v); };
	public void writeInt(int v)throws IOException{ out.writeInt(v); };
	public void writeLong(long v)throws IOException{ out.writeLong(v); };
	public void writeFloat(float v)throws IOException{ out.writeFloat(v); };
	public void writeDouble(double v)throws IOException{ out.writeDouble(v); };
	
	public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		 out.writeBooleanBlock(buffer,offset,length);
	};		
	public void writeBooleanBlock(boolean [] buffer)throws IOException
	{
		 out.writeBooleanBlock(buffer);
	};	
	public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		 out.writeByteBlock(buffer,offset,length);
	};		
	public void writeByteBlock(byte [] buffer)throws IOException
	{
		 out.writeByteBlock(buffer);
	};
	public void writeByteBlock(byte data)throws IOException{ out.writeByteBlock(data); };
	
	public void writeCharBlock(CharSequence characters, int offset, int length)throws IOException	
	{
		out.writeCharBlock(characters,offset,length);
	};
	public void writeCharBlock(CharSequence characters)throws IOException		
	{
		out.writeCharBlock(characters);
	};
	public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		 out.writeCharBlock(buffer,offset,length);
	};		
	public void writeCharBlock(char [] buffer)throws IOException
	{
		 out.writeCharBlock(buffer);
	};
	public void writeShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		 out.writeShortBlock(buffer,offset,length);
	};		
	public void writeShortBlock(short [] buffer)throws IOException
	{
		 out.writeShortBlock(buffer);
	};
	public void writeIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		 out.writeIntBlock(buffer,offset,length);
	};		
	public void writeIntBlock(int [] buffer)throws IOException
	{
		 out.writeIntBlock(buffer);
	};
	public void writeLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		 out.writeLongBlock(buffer,offset,length);
	};		
	public void writeLongBlock(long [] buffer)throws IOException
	{
		 out.writeLongBlock(buffer);
	};
	public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		 out.writeFloatBlock(buffer,offset,length);
	};		
	public void writeFloatBlock(float [] buffer)throws IOException
	{
		 out.writeFloatBlock(buffer);
	};
	public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		 out.writeDoubleBlock(buffer,offset,length);
	};		
	public void writeDoubleBlock(double [] buffer)throws IOException
	{
		 out.writeDoubleBlock(buffer);
	};
	
	
	
	public void open()throws IOException{out.open();};
	public void close()throws IOException{out.close();};
	public void flush()throws IOException{out.flush();};
	
};