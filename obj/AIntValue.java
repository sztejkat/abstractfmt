package sztejkat.abstractfmt.obj;


abstract class AIntValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue(){ return intValue()!=0; };
	@Override public final byte byteValue(){ return (byte)intValue(); };
	@Override public final short shortValue(){ return (short)intValue(); };
	@Override public final char charValue(){ return (char)intValue(); };	
	@Override public final long longValue(){ return (long)intValue(); };
	@Override public final float floatValue(){ return (float)intValue(); };
	@Override public final double doubleValue(){ return (double)intValue(); };
	@Override public final char stringValue(){ return charValue(); };
};