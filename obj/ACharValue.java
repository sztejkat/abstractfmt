package sztejkat.abstractfmt.obj;


abstract class ACharValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue(){ return charValue()!=0; };
	@Override public final byte byteValue(){ return (byte)charValue(); };
	@Override public final short shortValue(){ return (short)charValue(); };
	@Override public final int intValue(){ return (int)charValue(); };
	@Override public final long longValue(){ return (long)charValue(); };
	@Override public final float floatValue(){ return (float)charValue(); };
	@Override public final double doubleValue(){ return (double)charValue(); };
	@Override public final char stringValue(){ return charValue(); };
};