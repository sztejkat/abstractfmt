package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.CSignalWriteFormat;
import sztejkat.abstractfmt.CIndicatorWriteFormatProtector;
import java.nio.charset.Charset;
import java.io.Writer;
/**	
	A signal format using XML.
*/
public class CXMLSignalWriteFormat extends CSignalWriteFormat
{
	/** Creates write format
		@param max_events_recursion_depth {@link CSignalWriteFormat#CSignalWriteFormat}
		@param out  see {@link CXMLIndicatorWriteFormat#CXMLIndicatorWriteFormat}
		@param charset --//--
		@param settings --//--	
		@param is_described --//--
	*/
	public CXMLSignalWriteFormat(
								   int max_events_recursion_depth,
								   final Writer out,
								   final Charset charset,
								   final CXMLSettings settings,
								   boolean is_described
								   )
	{
		super(max_events_recursion_depth,
				new CXMLIndicatorWriteFormat( out, charset, settings, is_described )
				);
	};
	/** Creates write format, using indicator format protector for defend against
		API abuse by {@link CSignalWriteFormat}.
		@param max_events_recursion_depth {@link CSignalWriteFormat#CSignalWriteFormat}
		@param out  see {@link CXMLIndicatorWriteFormat#CXMLIndicatorWriteFormat}
		@param charset --//--
		@param settings --//--	
		@param is_described --//--
		@param test_mode ignored parameter.
	*/
	CXMLSignalWriteFormat(
								   int max_events_recursion_depth,
								   final Writer out,
								   final Charset charset,
								   final CXMLSettings settings,
								   boolean is_described,
								   boolean test_mode
								   )
	{
		super(max_events_recursion_depth,
			new CIndicatorWriteFormatProtector(
							new CXMLIndicatorWriteFormat( out, charset, settings, is_described )
							)
				);
	};
};