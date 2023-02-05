package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;
import java.io.IOException;
/**
	An escaping engine specialized in XML body,
	regardless if it is a "token" or not.
	<p>
	Allows only "body" characters and escapes them first using
	XML entity and only if can't do it, using our custom system.
*/
abstract class AXMLBodyEscapingEngine extends AXMLEscapingEngineBase
{
				
	/* *************************************************************************
	
				AEscapingEngine
	
	* *************************************************************************/
	@Override protected boolean mustEscapeCodepoint(int code_point)
	{
		//Only characters which are 
		return SXMLChar_classifier.isCharData(code_point)!=SXMLChar_classifier.XML_DATA_CHAR;
	};
	@Override protected void escape(char c)throws IOException
	{
		switch(SXMLChar_classifier.isCharData(c))
		{
			case SXMLChar_classifier.XML_DATA_CHAR:
						//This should NOT be escape, but hell why not?
						escapeCodePointAsEntity(c);
						break;
			case SXMLChar_classifier.XML_ENTITY:
						//This can be expressed by XML entity, so let us do it.
						escapeCodePointAsEntity(c);
						break;
			case SXMLChar_classifier.NON_XML_COMPATIBLE:
						//This must be escaped using our custom escape system
						//Single char version
						escapeAsCustomHexEscape(c);
						break;
			default: throw new AssertionError();
		}
	};
	@Override protected void escapeCodepoint(int c, char upper_surogate, char lower_surogate)throws IOException
	{
		assert(c>0xFFFF);
		switch(SXMLChar_classifier.isCharData(c))
		{
			case SXMLChar_classifier.XML_DATA_CHAR:
						//This should NOT be escape, but hell why not?
						escapeCodePointAsEntity(c);
						break;
			case SXMLChar_classifier.XML_ENTITY:
						//This can be expressed by XML entity, so let us do it.
						escapeCodePointAsEntity(c);
						break;
			case SXMLChar_classifier.NON_XML_COMPATIBLE:
						//This must be escaped using our custom escape system,
						//surogate by surogate.
						escapeAsCustomHexEscape(upper_surogate);
						escapeAsCustomHexEscape(lower_surogate);
						break;
			default: throw new AssertionError();
		}
	};
	
};
