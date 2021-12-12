package sztejkat.abstractfmt;
import java.io.IOException;

/**
		An implementation of {@link ISignalWriteFormat} over the
		{@link #IIndicatorWriteFormat}
*/
public class CSignalWriteFormat extends ASignalWriteFormat
{
					private final boolean is_described;
	/* *************************************************
	
			Creation
			
	
	* *************************************************/
	/** Creates write format
		@param names_registry_size see {@link ASignalWriteFormat#ASignalWriteFormat}
		@param max_name_length --//--
		@param max_events_recursion_depth --//--
		@param output --//--
		@param is_described value to return from {@link #isDescribed}.
				If this value is true stream is described and 
				the control of details about if write type-flush
				indicators is under a control of <code>output</code>
				{@link IIndicatorWriteFormat#requiresFlushes}.
				<p>
				Output must be compatible with this setting.
	*/
	protected CSignalWriteFormat(
								 int names_registry_size,
								 int max_name_length,
								 int max_events_recursion_depth,								 
								 IIndicatorWriteFormat output,
								 boolean is_described
								 )
	 {
	 	super( names_registry_size,
			  max_name_length,
			  max_events_recursion_depth,								 
			  output);
		this.is_described=is_described;
	 };
	 
	 @Override public boolean isDescribed(){ return is_described; };
	
};