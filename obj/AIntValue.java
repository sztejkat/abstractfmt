package sztejkat.abstractfmt.obj;
import java.io.IOException;

abstract class AIntValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue()throws IOException{ return intValue()!=0; };
	@Override public final byte byteValue()throws IOException{ return (byte)intValue(); };
	@Override public final short shortValue()throws IOException{ return (short)intValue(); };
	@Override public final char charValue()throws IOException{ return (char)intValue(); };	
	@Override public final long longValue()throws IOException{ return (long)intValue(); };
	@Override public final float floatValue()throws IOException{ return (float)intValue(); };
	@Override public final double doubleValue()throws IOException{ return (double)intValue(); };
	@Override public final char stringValue()throws IOException{ return charValue(); };
};