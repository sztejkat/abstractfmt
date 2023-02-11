package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;
import java.io.IOException;
/**
	An escaping engine specialized in XML body,
	regardless if it is a "token" or not.
	<p>
	Allows only "body" characters and escapes them first using
	XML entity and only if can't do it, using our custom system.
	<p>
	This class escapes all un-recommended characters.
*/
abstract class AXMLBodyEscapingEngine extends AXMLEscapingEngineBase
{
				
	/* *************************************************************************
	
				Services required from subclasses.
	
	* *************************************************************************/
	/** Returns XML classifier 
	@return a classifier to use for XML chars recognition */
	protected abstract IXMLCharClassifier getClassifier();
	/* *************************************************************************
	
				AEscapingEngine
	
	* *************************************************************************/
	@Override protected boolean mustEscapeCodepoint(int code_point)
	{
		//We need to escape _ to make sure it is an unique escape marker.
		return (code_point=='_') || 
			   (getClassifier().isCharData(code_point)!=IXMLCharClassifier.XML_DATA_CHAR);
	};
	@Override protected void escape(char c)throws IOException
	{
		if (c=='_')
		{
			escapeAsCustomEscape(c);
		}else
		{
			switch(getClassifier().isCharData(c))
			{
				case IXMLCharClassifier.XML_DATA_CHAR:
							//This should NOT be escape, but hell why not?
							escapeCodePointAsEntity(c);
							break;
				case IXMLCharClassifier.XML_ENTITY:
							//This can be expressed by XML entity, so let us do it.
							escapeCodePointAsEntity(c);
							break;
				case IXMLCharClassifier.NON_XML_COMPATIBLE:
							//This must be escaped using our custom escape system
							//Single char version
							escapeAsCustomHexEscape(c);
							break;
				default: throw new AssertionError();
			}
		};
	};
	@Override protected void escapeCodepoint(int c, char upper_surogate, char lower_surogate)throws IOException
	{
		assert(c>0xFFFF);
		switch(getClassifier().isCharData(c))
		{
			case IXMLCharClassifier.XML_DATA_CHAR:
						//This should NOT be escape, but hell why not?
						escapeCodePointAsEntity(c);
						break;
			case IXMLCharClassifier.XML_ENTITY:
						//This can be expressed by XML entity, so let us do it.
						escapeCodePointAsEntity(c);
						break;
			case IXMLCharClassifier.NON_XML_COMPATIBLE:
						//This must be escaped using our custom escape system,
						//surogate by surogate.
						escapeAsCustomHexEscape(upper_surogate);
						escapeAsCustomHexEscape(lower_surogate);
						break;
			default: throw new AssertionError();
		}
	};
	
};
