package sztejkat.abstractfmt.obj;

/**
	Common data types for {@link CObjStructWriteFormat0}/
	{@link CObjStructReadFormat0}.
	
	<h1>Format</h1>
	An object stream is a collection of instances
	of {@link IObjStructFormat0} objects.
	<p>
	The {@link SIG_BEGIN},{@link SIG_END} and {@link SIG_END_BEGIN}
	are used to implement signals.
	<p>
	Both elementary values and elements of sequences are
	of {@link IObjStructFormat0Value} type.
	<p>
	The <code>ELMT_xxx</code> elements are used to implement elementary
	values, while <code>BLK_xxx</code> elements are used to implement
	block elements.
	<p>
	Blocks are always written using single element per object, regardless
	if stored by array operations or single item operation.
	
	
*/
public interface IObjStructFormat0
{
	/** A human friendly representation of data */
	@Override public String toString();
	/** True for signal elements (of any kind. Signal just puts an unpassable boundary in
	elementary data), false for data elements.
	@return true if element represent signal
	flase if primitive data. False means it is instance of
	{@link IObjStructFormat0Value} */
	public default boolean isSignal(){ return this instanceof IObjStructFormat0Value; };
	/** Tests if exactly same type and value
	@param x what to compare with. 
	@return true if same type and value
	*/
	public boolean equalsTo(IObjStructFormat0 x);
};