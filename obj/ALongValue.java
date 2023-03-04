package sztejkat.abstractfmt.obj;
import java.io.IOException;

abstract class ALongValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue()throws IOException{ return longValue()!=0; };
	@Override public final byte byteValue()throws IOException{ return (byte)longValue(); };
	@Override public final short shortValue()throws IOException{ return (short)longValue(); };
	@Override public final char charValue()throws IOException{ return (char)longValue(); };	
	@Override public final int intValue()throws IOException{ return (int)longValue(); };
	@Override public final float floatValue()throws IOException{ return (float)longValue(); };
	@Override public final double doubleValue()throws IOException{ return (double)longValue(); };
	@Override public final char stringValue()throws IOException{ return charValue(); };
};