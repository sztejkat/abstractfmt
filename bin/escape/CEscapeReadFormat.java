package sztejkat.abstractfmt.bin.escape;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.InputStream;

/**
	An escape based format, as described in <a href="package-summary.html">package description</a>
	<p>
	This class completes the implementation by providing some necessary methods.
*/
public class CEscapeReadFormat extends AEscapeReadFormat0
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CEscapeReadFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CEscapeReadFormat.",CEscapeReadFormat.class) : null;

         
      			
	/* *************************************************************
	
			Construction
			
	
	***************************************************************/
	/** Creates
		@param name_registry_capacity {@link ARegisteringStructReadFormat#ARegisteringStructReadFormat(int)}
			This value cannot be larger than 256. Recommended value is 256, minimum resonable is 8.
		@param raw raw input stream, non null. Will be closed.
			<p>
			This stream <u>must</u> be such, that it returns
			partial read/partial skip only if there is actually no data in stream, timeout happen, file
			was fully read or connection is broken. If this stream will return partial reads
			or partial skips just "because i like it" this format will report {@link EUnexpectedEof}.
	*/
	public CEscapeReadFormat(int name_registry_capacity, 
							  InputStream raw
							  )
	{
		super(name_registry_capacity,raw);
		if (TRACE) TOUT.println("new CEscapeReadFormat()");
	}; 
	
	/* *****************************************************************************
	
			Services required by ARegisteringStructReadFormat
	
	******************************************************************************/
	/* ------------------------------------------------------------------
				State related.
	---------------------------------------------------------------------*/
	/** Empty */
	@Override protected void openImpl()throws IOException
	{
		if (TRACE) TOUT.println("openImpl()");
	};
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