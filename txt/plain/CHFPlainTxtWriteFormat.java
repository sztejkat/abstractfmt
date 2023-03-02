package sztejkat.abstractfmt.txt.plain;
import java.io.IOException;
import java.io.Writer;

/**
	A human friendly variant of {@link CPlainTxtWriteFormat}
	<p>
	A human firendly variant:
	<ul>
		<li>breaks line before each begin signal and outputs up to set limit of
		white-space leading characters to reflect the current recursion depth;</li>
		<li>alike for separator between end signal and token;</li>
	</ul>
*/
public class CHFPlainTxtWriteFormat extends CPlainTxtWriteFormat
{
				/** See constructor */
				private final int indent_limit;
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates
	@param out writer where to write, non null, opened. Must accept all
		possible <code>char</code> values.
	@param indent_limit maximum number of white spaces in front of begin
		signal which are to be used to represent a current recursion depth.
	*/
	public CHFPlainTxtWriteFormat(Writer out, int indent_limit)
	{
		super(out);
		assert(indent_limit>=0);
		this.indent_limit= indent_limit;
	};
	@Override protected void beginDirectImpl(String name)throws IOException
	{
		out.write('\n');		
		int n = getCurrentStructRecursionDepth();
		n=n-1;
		if (n>indent_limit) n = indent_limit;
		while(--n>=0)
			out.write(' ');
		super.beginDirectImpl(name);
	};
	@Override protected void outEndSignalSeparator()throws IOException
	{
		super.outEndSignalSeparator();
		int n = getCurrentStructRecursionDepth();
		if (n!=0)
		{
			out.write('\n');		
			if (n>indent_limit) n = indent_limit;
			//We use > because the entrance to struct is made before this call.
			//so n is recursion limit +1
			while(--n>=0)
				out.write(' ');
		};
	};
}
