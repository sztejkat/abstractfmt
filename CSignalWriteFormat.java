package sztejkat.abstractfmt;
import java.io.IOException;

/**
		An implementation of {@link ISignalWriteFormat} over the
		{@link IIndicatorWriteFormat}
*/
public class CSignalWriteFormat extends ASignalWriteFormat
{
					
	/* *************************************************
	
			Creation
			
	
	* *************************************************/
	/** Creates write format
		@param max_events_recursion_depth see {@link ASignalWriteFormat#ASignalWriteFormat}
		@param output --//--		
	*/
	public CSignalWriteFormat(
								 int max_events_recursion_depth,
								 IIndicatorWriteFormat output
								 )
	 {
	 	super(max_events_recursion_depth,								 
			  output);
	 };
	 
};