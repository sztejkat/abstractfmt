package sztejkat.abstractfmt.obj;
import java.io.IOException;
/**
	Prevents all possible conversion.
*/
public abstract class AStrictValue implements IObjStructFormat0Value
{
	
	@Override public final boolean isSignal(){ return false; };
	
	@Override public boolean booleanValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public byte byteValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public char charValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public short shortValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public int intValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public long longValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public float floatValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public double doubleValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public char stringValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	
	@Override public boolean blockBooleanValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public byte blockByteValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public char blockCharValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public short blockShortValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public int blockIntValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public long blockLongValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public float blockFloatValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public double blockDoubleValue()throws IOException{ throw new EAbusedFormat(this.toString());};
	@Override public char blockStringValue()throws IOException{ throw new EAbusedFormat(this.toString());};
};