package sztejkat.abstractfmt.obj;
import java.io.IOException;
/**
	Adds value conversion to make streams un-typed.
*/
public abstract class AValue implements IObjStructFormat0Value
{
	
	@Override public final boolean isSignal(){ return false; };
	
	
	//All below fallback to single element getters 
	@Override public boolean blockBooleanValue()throws IOException{ return booleanValue(); };
	@Override public byte blockByteValue()throws IOException{ return byteValue(); };
	@Override public char blockCharValue()throws IOException{ return charValue(); };
	@Override public short blockShortValue()throws IOException{ return shortValue(); };
	@Override public int blockIntValue()throws IOException{ return intValue(); };
	@Override public long blockLongValue()throws IOException{ return longValue(); };
	@Override public float blockFloatValue()throws IOException{ return floatValue(); };
	@Override public double blockDoubleValue()throws IOException{ return doubleValue(); };
	@Override public char blockStringValue()throws IOException{ return stringValue(); };
};