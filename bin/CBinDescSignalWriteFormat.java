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
		@param output see {@link ABinIndicatorWriteFormat1#ABinIndicatorWriteFormat1}	
	*/
	protected CBinDescSignalWriteFormat(
							OutputStream output
							)
	{
		super(new CBinDescIndicatorWriteFormat(output));
	};

	/** Creates write format , using indicator format protector for defend against
		API abuse by {@link CSignalWriteFormat}.
		@param output see {@link ABinIndicatorWriteFormat1#ABinIndicatorWriteFormat1}	
		@param test_mode ignored
	*/
	CBinDescSignalWriteFormat(
							OutputStream output,
							boolean test_mode
							)
	{
		super(new CIndicatorWriteFormatProtector( new CBinDescIndicatorWriteFormat(output),true));
	};	
};