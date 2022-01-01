package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.CSignalReadFormat;
import sztejkat.abstractfmt.ASignalReadFormat;
import sztejkat.abstractfmt.CIndicatorReadFormatProtector;
import java.io.*;
import java.io.InputStream;

/**
	A chunk-based {@link ISignalReadFormat}, described.
*/
public class CBinDescSignalReadFormat extends CSignalReadFormat
{			
	/** Creates write format
		@param max_events_recursion_depth see {@link ASignalReadFormat#ASignalReadFormat}
		@param input see {@link ABinIndicatorReadFormat1#ABinIndicatorReadFormat1}	
	*/
	public CBinDescSignalReadFormat(
							int max_events_recursion_depth,
							InputStream input
							)
	{
		super(max_events_recursion_depth, new CBinDescIndicatorReadFormat(input));
	};
	/** Creates write format , using indicator format protector for defend against
		API abuse by {@link CSignalReadFormat}.
		@param max_events_recursion_depth see {@link ASignalReadFormat#ASignalReadFormat}
		@param input see {@link ABinIndicatorReadFormat1#ABinIndicatorReadFormat1}	
		@param test_mode ignored parameter.
	*/
	CBinDescSignalReadFormat(
							int max_events_recursion_depth,
							InputStream input,
							boolean test_mode
							)
	{
		super(max_events_recursion_depth,
				new CIndicatorReadFormatProtector( new CBinDescIndicatorReadFormat(input))
				);
	};
};