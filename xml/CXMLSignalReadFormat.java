package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.CSignalReadFormat;
import sztejkat.abstractfmt.CIndicatorReadFormatProtector;
import java.io.Reader;
/**	
	A signal format using XML.
*/
public class CXMLSignalReadFormat extends CSignalReadFormat
{
	/** Creates read format
		@param input  see {@link CXMLIndicatorReadFormat#CXMLIndicatorReadFormat}
		@param settings --//--	
		@param is_described --//--
	*/
	public CXMLSignalReadFormat(
								   final Reader input,
								   final CXMLSettings settings,
								   boolean is_described
								   )
	{
		super(
				new CXMLIndicatorReadFormat( input, settings, is_described )
				);
	};
	/** Creates write format, using indicator format protector for defend against
		API abuse by {@link CSignalReadFormat}.
		@param input  see {@link CXMLIndicatorReadFormat#CXMLIndicatorReadFormat}
		@param settings --//--	
		@param is_described --//--
		@param test_mode ignored parameter.
	*/
	CXMLSignalReadFormat(
								   final Reader input,
								   final CXMLSettings settings,
								   boolean is_described,
								   boolean test_mode
								   )
	{
		super(
			new CIndicatorReadFormatProtector(
							new CXMLIndicatorReadFormat( input, settings, is_described )
							)
				);
	};
};