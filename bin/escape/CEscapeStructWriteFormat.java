package sztejkat.abstractfmt.bin.escape;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.OutputStream;

/**
	An escape based format, as described in <a href="package-summary.html">package description</a>
	<p>
	This class completes the implementation by providing some necessary methods.
*/
public class CEscapeStructWriteFormat extends AEscapeStructWriteFormat0
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CEscapeStructWriteFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CEscapeStructWriteFormat.",CEscapeStructWriteFormat.class) : null;

         
      			
	/* *************************************************************
	
			Construction
			
	
	***************************************************************/
	/** Creates
		@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
			This value cannot be larger than 256. Recommended value is 256, minimum resonable is 8.
		@param raw raw binary stream to write to. Will be closed on {@link #close}.	
		@param indexed_registration if true names are registered directly, by index.
			If false names are registered indirectly, by order of appearance.
			For typed streams it is recommended to use "by index" registration.
	*/
	CEscapeStructWriteFormat(int name_registry_capacity, 
							  OutputStream raw,
							  boolean indexed_registration
							  )
	{
		super(name_registry_capacity,raw, indexed_registration);
	}; 
	
	/* *****************************************************************************
	
			Services required by ARegisteringStructWriteFormat
	
	******************************************************************************/
	/* ------------------------------------------------------------------
				State related.
	---------------------------------------------------------------------*/
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