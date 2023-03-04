package sztejkat.abstractfmt.obj;
import java.io.IOException;

abstract class ADoubleValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue()throws IOException{ return doubleValue()!=0; };
	@Override public final byte byteValue()throws IOException{ return (byte)doubleValue(); };
	@Override public final short shortValue()throws IOException{ return (short)doubleValue(); };
	@Override public final char charValue()throws IOException{ return (char)doubleValue(); };
	@Override public final int intValue()throws IOException{ return (int)doubleValue(); };
	@Override public final long longValue()throws IOException{ return (long)doubleValue(); };
	@Override public final float floatValue()throws IOException{ return (float)doubleValue(); };
	@Override public final char stringValue()throws IOException{ return charValue(); };
};