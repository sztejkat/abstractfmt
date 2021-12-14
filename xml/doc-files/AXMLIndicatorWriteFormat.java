package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import java.io.*;

/**
	An indicator writer using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>
*/
public abstract class AXMLIndicatorWriteFormat implements IIndicatorWriteFormat
{
				/** XML settings */
				private final CXMLSettings settings;
	
				
	/* ****************************************************
	
			Creation
	
	
	*****************************************************/
	/** Creates
	@param settings xml settings 
	*/
	protected AXMLIndicatorWriteFormat(CXMLSettings settings)
	{
		assert(settings!=null);
		this.settings=settings;
	};
	
	
};