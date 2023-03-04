package sztejkat.abstractfmt.obj;
import java.io.IOException;
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
	/* ----------------------------------------------------------
	
			Conversions to single elementary primitives
	
	----------------------------------------------------------*/
	public boolean booleanValue()throws IOException;
	public byte byteValue()throws IOException;
	public char charValue()throws IOException;
	public short shortValue()throws IOException;
	public int intValue()throws IOException;
	public long longValue()throws IOException;
	public float floatValue()throws IOException;
	public double doubleValue()throws IOException;
	public char stringValue()throws IOException;
	/* ----------------------------------------------------------
	
			Conversions to sequence elementary primitives
			
			Note: The conversions are separated so that
			"strict" versions of values can be used to
			boldly refuse incorrect conversions when testing
			some contracts.
	
	----------------------------------------------------------*/
	public boolean blockBooleanValue()throws IOException;
	public byte blockByteValue()throws IOException;
	public char blockCharValue()throws IOException;
	public short blockShortValue()throws IOException;
	public int blockIntValue()throws IOException;
	public long blockLongValue()throws IOException;
	public float blockFloatValue()throws IOException;
	public double blockDoubleValue()throws IOException;
	public char blockStringValue()throws IOException;
	
	/* ----------------------------------------------------------
	
			IObjStructFormat0
	
	----------------------------------------------------------*/
	/** Always false */
	@Override public default boolean isSignal(){ return false; };
};