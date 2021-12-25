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
		@param max_events_recursion_depth {@link CSignalReadFormat#CSignalReadFormat}
		@param input  see {@link CXMLIndicatorReadFormat#CXMLIndicatorReadFormat}
		@param settings --//--	
		@param is_described --//--
	*/
	public CXMLSignalReadFormat(
								   int max_events_recursion_depth,
								   final Reader input,
								   final CXMLSettings settings,
								   boolean is_described
								   )
	{
		super(max_events_recursion_depth,
				new CXMLIndicatorReadFormat( input, settings, is_described )
				);
	};
	/** Creates write format, using indicator format protector for defend against
		API abuse by {@link CSignalReadFormat}.
		@param max_events_recursion_depth {@link CSignalReadFormat#CSignalReadFormat}
		@param input  see {@link CXMLIndicatorReadFormat#CXMLIndicatorReadFormat}
		@param settings --//--	
		@param is_described --//--
		@param test_mode ignored parameter.
	*/
	CXMLSignalReadFormat(
								   int max_events_recursion_depth,
								   final Reader input,
								   final CXMLSettings settings,
								   boolean is_described,
								   boolean test_mode
								   )
	{
		super(max_events_recursion_depth,
			new CIndicatorReadFormatProtector(
							new CXMLIndicatorReadFormat( input, settings, is_described )
							)
				);
	};
};