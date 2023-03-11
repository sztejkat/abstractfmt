package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;


/**
	Just a wrapper which passes calls to wrapped {@link IStructReadFormat}.
	<p>
	Good base for filters.
*/
public class AStructReadFormatAdapter extends AStructFormatAdapterBase<IStructReadFormat> 
										 implements IStructReadFormat
{
	public AStructReadFormatAdapter(IStructReadFormat engine)
	{
		super(engine);
	};
	/* ***************************************************************
	
			IStructWriteFormat
	
	
	****************************************************************/
	@Override public String next()throws IOException{ return engine.next(); };
	@Override public boolean hasElementaryData()throws IOException{ return engine.hasElementaryData(); }
	@Override public void skip(int levels)throws IOException{ engine.skip(levels); };
	@Override public void skip()throws IOException{ engine.skip(); };
	@Override public int depth()throws IOException{ return engine.depth(); };
	@Override public boolean readBoolean()throws IOException{ return engine.readBoolean(); };
	@Override public byte readByte()throws IOException{ return engine.readByte(); };
	@Override public char readChar()throws IOException{ return engine.readChar(); };
	@Override public short readShort()throws IOException{ return engine.readShort(); };
	@Override public int readInt()throws IOException{ return engine.readInt(); };
	@Override public long readLong()throws IOException{ return engine.readLong(); };
	@Override public float readFloat()throws IOException{ return engine.readFloat(); };
	@Override public double readDouble()throws IOException{ return engine.readDouble(); };
	
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		return engine.readBooleanBlock(buffer, offset, length);
	};
	@Override public int readBooleanBlock(boolean [] buffer)throws IOException
	{
		return engine.readBooleanBlock(buffer);
	};
	@Override public boolean readBooleanBlock()throws IOException,ENoMoreData
	{
		return engine.readBooleanBlock();
	};
	
	@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		return engine.readByteBlock(buffer, offset, length);
	};
	@Override public int readByteBlock(byte [] buffer)throws IOException
	{
		return engine.readByteBlock(buffer);
	};
	@Override public byte readByteBlock()throws IOException,ENoMoreData
	{
		return engine.readByteBlock();
	};
	
	@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		return engine.readCharBlock(buffer, offset, length);
	};
	@Override public int readCharBlock(char [] buffer)throws IOException
	{
		return engine.readCharBlock(buffer);
	};
	@Override public char readCharBlock()throws IOException,ENoMoreData
	{
		return engine.readCharBlock();
	};
	
	@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		return engine.readShortBlock(buffer, offset, length);
	};
	@Override public int readShortBlock(short [] buffer)throws IOException
	{
		return engine.readShortBlock(buffer);
	};
	@Override public short readShortBlock()throws IOException,ENoMoreData
	{
		return engine.readShortBlock();
	};
	
	@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		return engine.readIntBlock(buffer, offset, length);
	};
	@Override public int readIntBlock(int [] buffer)throws IOException
	{
		return engine.readIntBlock(buffer);
	};
	@Override public int readIntBlock()throws IOException,ENoMoreData
	{
		return engine.readIntBlock();
	};
	
	@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		return engine.readLongBlock(buffer, offset, length);
	};
	@Override public int readLongBlock(long [] buffer)throws IOException
	{
		return engine.readLongBlock(buffer);
	};
	@Override public long readLongBlock()throws IOException,ENoMoreData
	{
		return engine.readLongBlock();
	};
	
	@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		return engine.readFloatBlock(buffer, offset, length);
	};
	@Override public int readFloatBlock(float [] buffer)throws IOException
	{
		return engine.readFloatBlock(buffer);
	};
	@Override public float readFloatBlock()throws IOException,ENoMoreData
	{
		return engine.readFloatBlock();
	};
	
	@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		return engine.readDoubleBlock(buffer, offset, length);
	};
	@Override public int readDoubleBlock(double [] buffer)throws IOException
	{
		return engine.readDoubleBlock(buffer);
	};
	@Override public double readDoubleBlock()throws IOException,ENoMoreData
	{
		return engine.readDoubleBlock();
	};
	
	@Override public int readString(Appendable characters,  int length)throws IOException
	{
		return engine.readString(characters,length);
	};
	@Override public char readString()throws IOException,ENoMoreData
	{
		return engine.readString();
	};
	
	
	@Override public void open()throws IOException{ engine.open(); };
	@Override public void close()throws IOException{ engine.close(); };
		
};