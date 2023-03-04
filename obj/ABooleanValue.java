package sztejkat.abstractfmt.obj;
import java.io.IOException;

abstract class ABooleanValue extends AValue implements IObjStructFormat0
{
			
	@Override public final byte byteValue()throws IOException{ return booleanValue() ? (byte)1 : (byte)0; };
	@Override public final char charValue()throws IOException{ return booleanValue() ? 't' : 'f'; };
	@Override public final short shortValue()throws IOException{ return booleanValue() ? (short)1 : (short)0; };
	@Override public final int intValue()throws IOException{ return booleanValue() ? 1 : 0; };
	@Override public final long longValue()throws IOException{ return booleanValue() ? 1 : 0; };
	@Override public final float floatValue()throws IOException{ return booleanValue() ? 1 : 0; };
	@Override public final double doubleValue()throws IOException{ return booleanValue() ? 1 : 0; };
	@Override public final char stringValue()throws IOException{ return charValue(); };
	
	
};