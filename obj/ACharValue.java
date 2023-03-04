package sztejkat.abstractfmt.obj;
import java.io.IOException;

abstract class ACharValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue()throws IOException{ return charValue()!=0; };
	@Override public final byte byteValue()throws IOException{ return (byte)charValue(); };
	@Override public final short shortValue()throws IOException{ return (short)charValue(); };
	@Override public final int intValue()throws IOException{ return (int)charValue(); };
	@Override public final long longValue()throws IOException{ return (long)charValue(); };
	@Override public final float floatValue()throws IOException{ return (float)charValue(); };
	@Override public final double doubleValue()throws IOException{ return (double)charValue(); };
	@Override public final char stringValue()throws IOException{ return charValue(); };
};