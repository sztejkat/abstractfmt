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
		@param input --//--		
	*/
	public CSignalReadFormat(
								 IIndicatorReadFormat input
								 )
	 {
	 	super( 
			 input
			  );
	 };
	
};