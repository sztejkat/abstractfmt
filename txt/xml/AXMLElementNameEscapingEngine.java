package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;
import java.io.IOException;
/**
	An escaping engine specialized in XML elements names
	using _XXXX syntax.
	<p>
	This escaping engine <u>must be</u> {@link #reset}
	with each new name to escape because characters set for
	first name char is far more restrictive than for remainder
	of the name.
*/
abstract class AXMLElementNameEscapingEngine extends AXMLEscapingEngineBase
{
				private boolean is_first_char;
	/* *************************************************************************
	
				AEscapingEngine
	
	* *************************************************************************/
	@Override protected boolean mustEscapeCodepoint(int code_point)
	{
		return !(is_first_char  ?
				SXMLChar_classifier.isNameStartChar(code_point)
				:
				SXMLChar_classifier.isNameChar(code_point));
	};
	@Override protected void escape(char c)throws IOException
	{
		escapeAsCustomEscape(c);
	};
	/** Overriden to handle {@link #is_first_char} */
	@Override protected void appendImpl(char c)throws IOException
	{
		super.appendImpl(c);
		//Now clear that decission only if we processed at least one char. 
		if (!isCodepointPending())
				is_first_char = false;
			
	};
	/** Overriden to handle {@link #is_first_char} */
	@Override public void reset()
	{
		super.reset();
		is_first_char = false;
	};
};