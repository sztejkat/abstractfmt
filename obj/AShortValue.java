package sztejkat.abstractfmt.obj;


abstract class AShortValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue(){ return shortValue()!=0; };
	@Override public final byte byteValue(){ return (byte)shortValue(); };
	@Override public final int intValue(){ return (int)shortValue(); };
	@Override public final char charValue(){ return (char)shortValue(); };	
	@Override public final long longValue(){ return (long)shortValue(); };
	@Override public final float floatValue(){ return (float)shortValue(); };
	@Override public final double doubleValue(){ return (double)shortValue(); };
	@Override public final char stringValue(){ return charValue(); };
};