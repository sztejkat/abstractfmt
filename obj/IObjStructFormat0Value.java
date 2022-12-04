package sztejkat.abstractfmt.obj;

/**
	Primitive values stored in stream.
	<p>
	The object stream is by default implemented as un-typed
	(no type checking at all, including abuse of blocks)
	and this contract provides type casting which can be
	used to transparently missuse elements.
*/
public interface IObjStructFormat0Value extends IObjStructFormat0
{
	public boolean booleanValue();
	public byte byteValue();
	public char charValue();
	public short shortValue();
	public int intValue();
	public long longValue();
	public float floatValue();
	public double doubleValue();
	public char stringValue();
	/** Always false */
	@Override public default boolean isSignal(){ return false; };
};