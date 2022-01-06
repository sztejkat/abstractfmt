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
		@param output --//--		
	*/
	public CSignalWriteFormat(
								 IIndicatorWriteFormat output
								 )
	 {
	 	super(output);
	 };
	 
};