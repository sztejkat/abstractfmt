package sztejkat.abstractfmt.txt.json;
import java.io.IOException;
import java.io.Writer;

/**
	A human friendly variant of {@link CJSONWriteFormat}
	<p>
	A human firendly variant:
	<ul>
		<li>breaks line before each begin/end  and outputs up to set limit of
		white-space leading characters to reflect the current recursion depth;</li>
	<p>
*/
public class CHFJSONWriteFormat extends CJSONWriteFormat
{
				/** See constructor */
				private final int indent_limit;
				/** Used to track signal depth and to 
				ellimite influence of end-begin optimization 
				on the {@link #getCurrentStructRecursionDepth}
				during the {@link #endImpl} */
				private int signal_depth;
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates
	@param out writer where to write, non null, opened. Must accept all
		possible <code>char</code> values.
	@param indent_limit maximum number of white spaces in front of begin
		signal which are to be used to represent a current recursion depth.
	*/
	public CHFJSONWriteFormat(Writer out, int indent_limit)
	{
		super(out);
		assert(indent_limit>=0);
		this.indent_limit= indent_limit;
	};
	/* ***********************************************************************
		
				AStructFormatBase
		
		
	************************************************************************/
	
	/** Overriden to trace the {@link #signal_depth} */
	@Override protected void openImpl()throws IOException
	{
		signal_depth = 0;
		super.openImpl();
	};
	
	/* -------------------------------------------------------------
				Actual signal indentation
	-------------------------------------------------------------*/
	private void indent(int n)throws IOException
	{
		if (n>indent_limit) n = indent_limit;
		while(--n>=0)
			out.write(' ');
	};
	
	@Override protected void writeOpenJSONObject(String name)throws IOException
	{
		out.write('\n');		
		indent(signal_depth++);
		assert(signal_depth>0):"too deep recursion, integer wrap-around";
		super.writeOpenJSONObject(name);
	};
	@Override protected void writeCloseJSONObject()throws IOException
	{
		out.write('\n');
		indent(--signal_depth);
		super.writeCloseJSONObject();
	};	
}
