package sztejkat.abstractfmt;
import java.io.IOException;

/**
		An implementation of {@link ISignalReadFormat} over the
		{@link #IIndicatorReadFormat}
*/
public class CSignalReadFormat extends ASignalReadFormat
{
					private final boolean is_described;
	/* *************************************************
	
			Creation
			
	
	* *************************************************/
	/** Creates write format
		@param names_registry_size see {@link ASignalReadFormat#ASignalReadFormat}
		@param max_events_recursion_depth --//--
		@param output --//--
		@param is_described value to return from {@link #isDescribed}.
				If this value is true stream is described and 
				the control of details about if write type-flush
				indicators is under a control of <code>output</code>
				{@link IIndicatorReadFormat#requiresFlushes}.
				<p>
				Output must be compatible with this setting.
	*/
	protected CSignalReadFormat(
								 int names_registry_size,
								 int max_events_recursion_depth,
								 IIndicatorReadFormat input,
								 boolean is_described
								 )
	 {
	 	super( names_registry_size,
			  max_events_recursion_depth,								 
			  input);
		this.is_described=is_described;
	 };
	 
	 @Override public boolean isDescribed(){ return is_described; };
	
};