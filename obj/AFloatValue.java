package sztejkat.abstractfmt.obj;


abstract class AFloatValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue(){ return floatValue()!=0; };
	@Override public final byte byteValue(){ return (byte)floatValue(); };
	@Override public final short shortValue(){ return (short)floatValue(); };
	@Override public final char charValue(){ return (char)floatValue(); };
	@Override public final int intValue(){ return (int)floatValue(); };
	@Override public final long longValue(){ return (long)floatValue(); };
	@Override public final double doubleValue(){ return (double)floatValue(); };
	@Override public final char stringValue(){ return charValue(); };
};