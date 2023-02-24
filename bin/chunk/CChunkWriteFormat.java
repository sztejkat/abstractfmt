package sztejkat.abstractfmt.bin.chunk;
import  sztejkat.abstractfmt.*;
import  sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.OutputStream;

/**
	A chunk write format, as described in <a href="package-summary.html">package description</a>
	<p>
	This class completes the implementation by providing some necessary methods.
*/
public class CChunkWriteFormat extends AChunkWriteFormat0
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CChunkWriteFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CChunkWriteFormat.",CChunkWriteFormat.class) : null;

	/* *****************************************************************************
	
	
			Construction
			
	
	
	* *****************************************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
			This value cannot be larger than 128. Recommended value is 128, minimum resonable is 8.	
	@param raw raw binary stream to write to. Will be closed on {@link #close}.
	@param indexed_registration if true names are registered directly, by index.
			If false names are registered indirectly, by order of appearance.
			For typed streams it is recommended to use "by index" registration,
			as first 8 registered names can be encoded very efficiently for 
			typical elementary elements lengths.
	*/
	CChunkWriteFormat(
					   int name_registry_capacity,
					   OutputStream raw,
					   boolean indexed_registration
					   )
	{
		super(name_registry_capacity,raw, indexed_registration);		
		if (TRACE) TOUT.println("new CChunkWriteFormat(...)");
	};
	
	/* *****************************************************************************
	
			Services required by ARegisteringStructWriteFormat
	
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