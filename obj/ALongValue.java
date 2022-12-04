package sztejkat.abstractfmt.obj;


abstract class ALongValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue(){ return longValue()!=0; };
	@Override public final byte byteValue(){ return (byte)longValue(); };
	@Override public final short shortValue(){ return (short)longValue(); };
	@Override public final char charValue(){ return (char)longValue(); };	
	@Override public final int intValue(){ return (int)longValue(); };
	@Override public final float floatValue(){ return (float)longValue(); };
	@Override public final double doubleValue(){ return (double)longValue(); };
	@Override public final char stringValue(){ return charValue(); };
};