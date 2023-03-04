package sztejkat.abstractfmt.obj;
import java.io.IOException;

abstract class AShortValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue()throws IOException{ return shortValue()!=0; };
	@Override public final byte byteValue()throws IOException{ return (byte)shortValue(); };
	@Override public final int intValue()throws IOException{ return (int)shortValue(); };
	@Override public final char charValue()throws IOException{ return (char)shortValue(); };	
	@Override public final long longValue()throws IOException{ return (long)shortValue(); };
	@Override public final float floatValue()throws IOException{ return (float)shortValue(); };
	@Override public final double doubleValue()throws IOException{ return (double)shortValue(); };
	@Override public final char stringValue()throws IOException{ return charValue(); };
};