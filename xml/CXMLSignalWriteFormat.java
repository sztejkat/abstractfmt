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
		@param out  see {@link CXMLIndicatorWriteFormat#CXMLIndicatorWriteFormat}
		@param charset --//--
		@param settings --//--	
		@param is_described --//--
	*/
	public CXMLSignalWriteFormat(
								   final Writer out,
								   final Charset charset,
								   final CXMLSettings settings,
								   boolean is_described
								   )
	{
		super(
				new CXMLIndicatorWriteFormat( out, charset, settings, is_described )
				);
	};
	/** Creates write format, using indicator format protector for defend against
		API abuse by {@link CSignalWriteFormat}.
		@param out  see {@link CXMLIndicatorWriteFormat#CXMLIndicatorWriteFormat}
		@param charset --//--
		@param settings --//--	
		@param is_described --//--
		@param test_mode ignored parameter.
	*/
	CXMLSignalWriteFormat(
								   final Writer out,
								   final Charset charset,
								   final CXMLSettings settings,
								   boolean is_described,
								   boolean test_mode
								   )
	{
		super(
			new CIndicatorWriteFormatProtector(
							new CXMLIndicatorWriteFormat( out, charset, settings, is_described )
							)
				);
	};
};