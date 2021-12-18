package sztejkat.abstractfmt;
import java.io.IOException;

/**
		An implementation of {@link ISignalReadFormat} over the
		{@link IIndicatorReadFormat}
*/
public class CSignalReadFormat extends ASignalReadFormat
{
					
	/* *************************************************
	
			Creation
			
	
	* *************************************************/
	/** Creates read format
		@param max_events_recursion_depth {@link ASignalReadFormat#ASignalReadFormat}
		@param input --//--		
	*/
	public CSignalReadFormat(
								 int max_events_recursion_depth,
								 IIndicatorReadFormat input
								 )
	 {
	 	super( 
			  max_events_recursion_depth,								 
			  input
			  );
	 };
	
};