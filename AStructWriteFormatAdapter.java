package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	Just a wrapper which passes calls to wrapped {@link IStructWriteFormat}.
	<p>
	Good base for filters.
*/
public class AStructWriteFormatAdapter extends AStructFormatAdapterBase<IStructWriteFormat> 
										 implements IStructWriteFormat
{
					
	public AStructWriteFormatAdapter(IStructWriteFormat engine)
	{
		super(engine);
	};
	/* ***************************************************************
	
			IStructWriteFormat
	
	
	****************************************************************/
	@Override public void begin(String name)throws IOException{ engine.begin(name); }; 
	@Override public void end()throws IOException{ engine.end(); };
	@Override public boolean optimizeBeginName(String name){ return engine.optimizeBeginName(name); };
	@Override public void writeBoolean(boolean v)throws IOException{ engine.writeBoolean(v); };
	@Override public void writeByte(byte v)throws IOException{ engine.writeByte(v); };
	@Override public void writeChar(char v)throws IOException{ engine.writeChar(v); };
	@Override public void writeShort(short v)throws IOException{ engine.writeShort(v); };
	@Override public void writeInt(int v)throws IOException{ engine.writeInt(v); };
	@Override public void writeLong(long v)throws IOException{ engine.writeLong(v); };
	@Override public void writeFloat(float v)throws IOException{ engine.writeFloat(v); };
	@Override public void writeDouble(double v)throws IOException{ engine.writeDouble(v); };
	@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		engine.writeBooleanBlock(buffer,offset,length);
	};
	@Override public void writeBooleanBlock(boolean [] buffer)throws IOException
	{
		engine.writeBooleanBlock(buffer);
	};
	@Override public void writeBooleanBlock(boolean v)throws IOException
	{
		engine.writeBooleanBlock(v);
	};
	@Override public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		engine.writeByteBlock(buffer,offset,length);
	};
	@Override public void writeByteBlock(byte [] buffer)throws IOException
	{
		engine.writeByteBlock(buffer);
	};
	@Override public void writeByteBlock(byte v)throws IOException
	{
		engine.writeByteBlock(v);
	};
	@Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		engine.writeCharBlock(buffer,offset,length);
	};
	@Override public void writeCharBlock(char [] buffer)throws IOException
	{
		engine.writeCharBlock(buffer);
	};
	@Override public void writeCharBlock(char v)throws IOException
	{
		engine.writeCharBlock(v);
	};		
	@Override public void writeShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		engine.writeShortBlock(buffer,offset,length);
	};
	@Override public void writeShortBlock(short [] buffer)throws IOException
	{
		engine.writeShortBlock(buffer);
	};
	@Override public void writeShortBlock(short v)throws IOException
	{
		engine.writeShortBlock(v);
	};
	@Override public void writeIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		engine.writeIntBlock(buffer,offset,length);
	};
	@Override public void writeIntBlock(int [] buffer)throws IOException
	{
		engine.writeIntBlock(buffer);
	};
	@Override public void writeIntBlock(int v)throws IOException
	{
		engine.writeIntBlock(v);
	};
	@Override public void writeLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		engine.writeLongBlock(buffer,offset,length);
	};
	@Override public void writeLongBlock(long [] buffer)throws IOException
	{
		engine.writeLongBlock(buffer);
	};
	@Override public void writeLongBlock(long v)throws IOException
	{
		engine.writeLongBlock(v);
	};
	@Override public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		engine.writeFloatBlock(buffer,offset,length);
	};
	@Override public void writeFloatBlock(float [] buffer)throws IOException
	{
		engine.writeFloatBlock(buffer);
	};
	@Override public void writeFloatBlock(float v)throws IOException
	{
		engine.writeFloatBlock(v);
	};
	@Override public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		engine.writeDoubleBlock(buffer,offset,length);
	};
	@Override public void writeDoubleBlock(double [] buffer)throws IOException
	{
		engine.writeDoubleBlock(buffer);
	};
	@Override public void writeDoubleBlock(double v)throws IOException
	{
		engine.writeDoubleBlock(v);
	};
	@Override public void writeString(CharSequence characters, int offset, int length)throws IOException
	{
		engine.writeString(characters,offset,length);
	};	
	@Override public void writeString(CharSequence characters)throws IOException
	{
		engine.writeString(characters);	
	};
	@Override public void writeString(char c)throws IOException
	{
		engine.writeString(c);
	};
	@Override public void open()throws IOException{	engine.open();};
	
	/* ***************************************************************
	
			IFlushable
	
	
	****************************************************************/
	@Override public void flush()throws IOException{ engine.flush(); };
		 
};