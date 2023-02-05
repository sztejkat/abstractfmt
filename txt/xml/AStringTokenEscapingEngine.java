package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;

/**
	An escaping engine specialized in a string token body
	carried inside an XML body.
	<p>
	As superclass but does escape the "
	too.
*/
abstract class AStringTokenEscapingEngine extends AXMLBodyEscapingEngine
{
				
	/* *************************************************************************
	
				AEscapingEngine
	
	* *************************************************************************/
	@Override protected boolean mustEscapeCodepoint(int code_point)
	{
		return (code_point=='\"')||super.mustEscapeCodepoint(code_point);
	};	
};
