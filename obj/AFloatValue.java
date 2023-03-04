package sztejkat.abstractfmt.obj;
import java.io.IOException;

abstract class AFloatValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue()throws IOException{ return floatValue()!=0; };
	@Override public final byte byteValue()throws IOException{ return (byte)floatValue(); };
	@Override public final short shortValue()throws IOException{ return (short)floatValue(); };
	@Override public final char charValue()throws IOException{ return (char)floatValue(); };
	@Override public final int intValue()throws IOException{ return (int)floatValue(); };
	@Override public final long longValue()throws IOException{ return (long)floatValue(); };
	@Override public final double doubleValue()throws IOException{ return (double)floatValue(); };
	@Override public final char stringValue()throws IOException{ return charValue(); };
};