package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.CSignalReadFormat;
import sztejkat.abstractfmt.ASignalReadFormat;
import sztejkat.abstractfmt.CIndicatorReadFormatProtector;
import java.io.*;
import java.io.InputStream;

/**
	A chunk-based {@link ISignalReadFormat}, undescribed.
*/
public class CBinSignalReadFormat extends CSignalReadFormat
{			
	/** Creates write format
		@param input see {@link ABinIndicatorReadFormat1#ABinIndicatorReadFormat1}	
	*/
	public CBinSignalReadFormat(
							InputStream input
							)
	{
		super(new CBinIndicatorReadFormat(input));
	};
	/** Creates write format , using indicator format protector for defend against
		API abuse by {@link CSignalReadFormat}.
		@param input see {@link ABinIndicatorReadFormat1#ABinIndicatorReadFormat1}	
		@param test_mode ignored parameter.
	*/
	CBinSignalReadFormat(
							InputStream input,
							boolean test_mode
							)
	{
		super(
				new CIndicatorReadFormatProtector( new CBinIndicatorReadFormat(input))
				);
	};
};