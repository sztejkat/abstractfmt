package sztejkat.abstractfmt.obj;


abstract class AByteValue extends AValue implements IObjStructFormat0
{
	@Override public final boolean booleanValue(){ return byteValue()!=0; };	
	@Override public final char charValue(){ return (char)byteValue(); };
	@Override public final short shortValue(){ return (short)byteValue(); };
	@Override public final int intValue(){ return (int)byteValue(); };
	@Override public final long longValue(){ return (long)byteValue(); };
	@Override public final float floatValue(){ return (float)byteValue(); };
	@Override public final double doubleValue(){ return (double)byteValue(); };
	@Override public final char stringValue(){ return charValue(); };
};