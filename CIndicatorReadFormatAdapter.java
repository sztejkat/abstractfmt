package sztejkat.abstractfmt;
import java.io.IOException;

/**
	Adapter which just passes all calls to underling indicator
*/
public class CIndicatorReadFormatAdapter implements IIndicatorReadFormat
{
				protected final IIndicatorReadFormat in;
				
	public CIndicatorReadFormatAdapter(IIndicatorReadFormat in)
	{
		assert(in!=null);
		this.in = in;
	};
	public int getMaxRegistrations(){ return in.getMaxRegistrations(); };
	public boolean isDescribed(){ return in.isDescribed(); };
	public boolean isFlushing(){ return in.isFlushing(); };
	public int getMaxSupportedSignalNameLength(){ return in.getMaxSupportedSignalNameLength(); };
	public void setMaxSignalNameLength(int characters){ in.setMaxSignalNameLength(characters); };
	public int getMaxSignalNameLength(){ return in.getMaxSignalNameLength(); };
	public TIndicator getIndicator()throws IOException{ return in.getIndicator(); };
	public TIndicator readIndicator()throws IOException{ return in.readIndicator(); };
	public void skip()throws IOException{ in.skip(); };
	public void next()throws IOException{ in.next(); };
	public String getSignalName(){ return in.getSignalName(); };
	public int getSignalNumber(){ return in.getSignalNumber(); };
	public boolean readBoolean()throws IOException{ return in.readBoolean(); };
	public byte readByte()throws IOException{ return in.readByte(); };
	public char readChar()throws IOException{ return in.readChar(); };
	public short readShort()throws IOException{ return in.readShort(); };
	public int readInt()throws IOException{ return in.readInt(); };
	public long readLong()throws IOException{ return in.readLong(); };
	public float readFloat()throws IOException{ return in.readFloat(); };
	public double readDouble()throws IOException{ return in.readDouble(); };
	
	public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		return in.readBooleanBlock(buffer,offset,length);
	};		
	public int readBooleanBlock(boolean [] buffer)throws IOException
	{
		return in.readBooleanBlock(buffer);
	};	
	public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		return in.readByteBlock(buffer,offset,length);
	};		
	public int readByteBlock(byte [] buffer)throws IOException
	{
		return in.readByteBlock(buffer);
	};
	public int readByteBlock()throws IOException
	{
		return in.readByteBlock();
	};
	public int readCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		return in.readCharBlock(buffer,offset,length);
	};		
	public int readCharBlock(Appendable characters,  int length)throws IOException		
	{
		return in.readCharBlock(characters,length);
	};
	public int readCharBlock(char [] buffer)throws IOException
	{
		return in.readCharBlock(buffer);
	};
	public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		return in.readShortBlock(buffer,offset,length);
	};		
	public int readShortBlock(short [] buffer)throws IOException
	{
		return in.readShortBlock(buffer);
	};
	public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		return in.readIntBlock(buffer,offset,length);
	};		
	public int readIntBlock(int [] buffer)throws IOException
	{
		return in.readIntBlock(buffer);
	};
	public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		return in.readLongBlock(buffer,offset,length);
	};		
	public int readLongBlock(long [] buffer)throws IOException
	{
		return in.readLongBlock(buffer);
	};
	public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		return in.readFloatBlock(buffer,offset,length);
	};		
	public int readFloatBlock(float [] buffer)throws IOException
	{
		return in.readFloatBlock(buffer);
	};
	public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		return in.readDoubleBlock(buffer,offset,length);
	};		
	public int readDoubleBlock(double [] buffer)throws IOException
	{
		return in.readDoubleBlock(buffer);
	};
	
	public void open()throws IOException{in.open();};
	public void close()throws IOException{in.close();};
	
};