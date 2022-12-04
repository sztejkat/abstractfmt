package sztejkat.abstractfmt.obj;


abstract class ADoubleValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue(){ return doubleValue()!=0; };
	@Override public final byte byteValue(){ return (byte)doubleValue(); };
	@Override public final short shortValue(){ return (short)doubleValue(); };
	@Override public final char charValue(){ return (char)doubleValue(); };
	@Override public final int intValue(){ return (int)doubleValue(); };
	@Override public final long longValue(){ return (long)doubleValue(); };
	@Override public final float floatValue(){ return (float)doubleValue(); };
	@Override public final char stringValue(){ return charValue(); };
};