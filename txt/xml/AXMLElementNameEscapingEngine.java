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
				private boolean is_first_char = true;
	
	/* *************************************************************************
	
				Services required from subclasses.
	
	* *************************************************************************/
	/** Returns XML classifier 
	@return a classifier to use for XML chars recognition */
	protected abstract IXMLCharClassifier getClassifier();
	/* *************************************************************************
	
				AEscapingEngine
	
	* *************************************************************************/
	/** Overriden to correctly detect that first char must be treated differently
	in all possible scenarios. */
	@Override protected void appendImpl(char c)throws IOException
	{
		super.appendImpl(c);
		is_first_char = false;		
	};
	@Override protected boolean mustEscapeCodepoint(int code_point)
	{
		boolean f = is_first_char;
		return  (code_point=='_') || 
				(!(f  ?
					getClassifier().isNameStartChar(code_point)
					:
					getClassifier().isNameChar(code_point)));
	};
	@Override protected void escape(char c)throws IOException
	{
		escapeAsCustomEscape(c);
	};
	
	/** Overriden to handle {@link #is_first_char} */
	@Override public void reset()
	{
		super.reset();
		is_first_char = true;
	};
	@Override public void flush()throws IOException
	{
		//Now handle the special case: if nothing is written write _
		boolean f = is_first_char && !isCodepointPending();
		super.flush();
		if (f) out('_');
	};
};