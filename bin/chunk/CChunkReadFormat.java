package sztejkat.abstractfmt.bin.chunk;
import  sztejkat.abstractfmt.logging.SLogging;
import  sztejkat.abstractfmt.*;
import java.io.IOException;
import java.io.InputStream;


/**
	A chunk read format, as described in <a href="package-summary.html">package description</a>
	<p>
	This class adds actuall encoding of elementary primitives and blocks,
	except strings which are defined in superclass.
*/
public class CChunkReadFormat extends AChunkReadFormat0
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CChunkReadFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CChunkReadFormat.",CChunkReadFormat.class) : null;

	/* *******************************************************
		
			Construction
		
	* ******************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
			This value cannot be larger than 128. Recommended value is 128, minimum resonable is 8.
	@param raw raw input stream, non null. Will be closed.
			<p>
			This stream <u>must</u> be such, that it returns
			partial read/partial skip only if there is actually no data in stream, timeout happen, file
			was fully read or connection is broken. If this stream will return partial reads
			or partial skips just "because i like it" this format will report {@link EUnexpectedEof}
			when such condition will happen inside chunk headers or when it fails to read
			at least one byte in a chunk body.
	*/
	public CChunkReadFormat(int name_registry_capacity, InputStream raw)
	{
		super(name_registry_capacity, raw);
		if (TRACE) TOUT.println("new CChunkReadFormat()");
	};
	/* *******************************************************
		
			AStructFormatBase
		
	* ******************************************************/
	/** Empty */
	@Override protected void openImpl()throws IOException{};
	
	/* *****************************************************************************
	
	
			IFormatLimits
			
	
	
	* *****************************************************************************/
	/** Unbound, Integer.MAX_VALUE */
	@Override public int getMaxSupportedSignalNameLength()
	{
		return Integer.MAX_VALUE;
	};
	/** Unbound, -1 */
	@Override public int getMaxSupportedStructRecursionDepth()
	{
		return -1;
	};
};