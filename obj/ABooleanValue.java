package sztejkat.abstractfmt.obj;


abstract class ABooleanValue extends AValue implements IObjStructFormat0
{
			
	@Override public final byte byteValue(){ return booleanValue() ? (byte)1 : (byte)0; };
	@Override public final char charValue(){ return booleanValue() ? 't' : 'f'; };
	@Override public final short shortValue(){ return booleanValue() ? (short)1 : (short)0; };
	@Override public final int intValue(){ return booleanValue() ? 1 : 0; };
	@Override public final long longValue(){ return booleanValue() ? 1 : 0; };
	@Override public final float floatValue(){ return booleanValue() ? 1 : 0; };
	@Override public final double doubleValue(){ return booleanValue() ? 1 : 0; };
	@Override public final char stringValue(){ return charValue(); };
};