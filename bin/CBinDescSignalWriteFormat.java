package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.ISignalWriteFormat;
import sztejkat.abstractfmt.CSignalWriteFormat;
import sztejkat.abstractfmt.ASignalWriteFormat;
import sztejkat.abstractfmt.CIndicatorWriteFormatProtector;
import java.io.*;
import java.io.OutputStream;

/**
	A chunk-based {@link ISignalWriteFormat}, described.
*/
public class CBinDescSignalWriteFormat extends CSignalWriteFormat
{			
	/** Creates write format
		@param max_events_recursion_depth see {@link ASignalWriteFormat#ASignalWriteFormat}
		@param output see {@link ABinIndicatorWriteFormat1#ABinIndicatorWriteFormat1}	
	*/
	protected CBinDescSignalWriteFormat(
							int max_events_recursion_depth,
							OutputStream output
							)
	{
		super(max_events_recursion_depth, 
				new CBinDescIndicatorWriteFormat(output)
				);
	};

	/** Creates write format , using indicator format protector for defend against
		API abuse by {@link CSignalWriteFormat}.
		@param max_events_recursion_depth see {@link ASignalWriteFormat#ASignalWriteFormat}
		@param output see {@link ABinIndicatorWriteFormat1#ABinIndicatorWriteFormat1}	
		@param test_mode ignored
	*/
	CBinDescSignalWriteFormat(
							int max_events_recursion_depth,
							OutputStream output,
							boolean test_mode
							)
	{
		super(max_events_recursion_depth, 
				new CIndicatorWriteFormatProtector( new CBinIndicatorWriteFormat(output))
				);
	};	
};