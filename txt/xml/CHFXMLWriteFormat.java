package sztejkat.abstractfmt.txt.xml;
import java.io.IOException;
import java.io.Writer;

/**
	A human friendly variant of {@link CXMLWriteFormat}
	<p>
	A human firendly variant:
	<ul>
		<li>breaks line before each begin/end tag and outputs up to set limit of
		white-space leading characters to reflect the current recursion depth;</li>
	</ul>
*/
public class CHFXMLWriteFormat extends CXMLWriteFormat
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
	public CHFXMLWriteFormat(Writer out, int indent_limit)
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
	/* ***********************************************************************
		
				AXMLWriteFormat 0
		
		
	************************************************************************/
	/** Overriden to inject eol */
	@Override protected void writeXMLDecl()throws IOException
	{
		super.writeXMLDecl();
		outXML('\n');
	};
	/** Overriden to inject eol */
	@Override protected void writeXMLProlog()throws IOException
	{
		super.writeXMLProlog();
		outXML('\n');
	};
	/** Overriden to inject eol */
	@Override protected void writeXMLClosure()throws IOException
	{
		outXML('\n');
		super.writeXMLClosure();
	};
	/* -------------------------------------------------------------
				Actual signal indentation
	-------------------------------------------------------------*/
	private void indent(int n)throws IOException
	{
		if (n>indent_limit) n = indent_limit;
		while(--n>=0)
			outXML(' ');
	};
	@Override protected void outEndSignalSeparator()throws IOException
	{
		if (signal_depth!=0)
		{
			outXML('\n');
			indent(signal_depth);
		}
	};
	@Override protected void beginDirectImpl(String name)throws IOException
	{
		//Note: Due to the "pending end" mechanism the 
		//		actuall call to "endImpl" may happen when
		//		recursion depth
		outXML('\n');		
		indent(signal_depth++);
		assert(signal_depth>0):"too deep recursion, integer wrap-around";
		super.beginDirectImpl(name);
	};
	@Override protected void endImpl()throws IOException
	{
		outXML('\n');
		indent(--signal_depth);
		super.endImpl();
	};	
}
