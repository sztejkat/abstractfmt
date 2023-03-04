package sztejkat.abstractfmt.obj;
import java.io.IOException;

abstract class AByteValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue()throws IOException{ return byteValue()!=0; };	
	@Override public final char charValue()throws IOException{ return (char)byteValue(); };
	@Override public final short shortValue()throws IOException{ return (short)byteValue(); };
	@Override public final int intValue()throws IOException{ return (int)byteValue(); };
	@Override public final long longValue()throws IOException{ return (long)byteValue(); };
	@Override public final float floatValue()throws IOException{ return (float)byteValue(); };
	@Override public final double doubleValue()throws IOException{ return (double)byteValue(); };
	@Override public final char stringValue()throws IOException{ return charValue(); };
};