package sztejkat.abstractfmt.xml;
import java.io.Writer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
/**
	An indicator writer using XML as specified in 
	<A href="doc-files/xml-syntax.html">syntax definition</a>.
	<p>
	Adds optional support for "full" format writing.
*/
public class CXMLIndicatorWriteFormat extends AXMLIndicatorWriteFormat
{
				/** True if {@link #open} was called */
				private boolean is_root_open;
				/** Described status */
				private final boolean is_described;
	/** Creates new write format.
	@param out output to which write.
	@param charset optional charset which will be used to detect
		characters which can't be correctly encoded by output.
		If this value is null this format assumes that <code>out</code>
		can encode a full <code>char</code> space.
	@param settings XML settings, non null. 
		If those settings carry non-null value in 
		{@link CXMLSettings#ROOT_ELEMENT} then 
		{@link #open} will open this element it and {@link #close}
		will close it (if opened).
		<br>
		If those settings carry prolog it also will be written in open.
	@param is_described true to write primitive type description data.
	*/
	public CXMLIndicatorWriteFormat(Writer out,
								    Charset charset,
								    CXMLSettings settings,
								    boolean is_described
								    )
	{
		super(out,charset,settings);
		this.is_described = is_described;
	};
	/** If root element is defined opens it. 
	@throws IOException if failed.
	*/
	public void open()throws IOException
	{
		if (settings.PROLOG!=null)
		{
			output.write(settings.PROLOG);
		};
		if (settings.ROOT_ELEMENT!=null)
		{
			final Writer o = output;
			o.write('<');
			o.write(settings.ROOT_ELEMENT);
			o.write('>');
			is_root_open = true;
		};
	};
	/* ******************************************************
	
			Services tuned in AXMLIndicatorWriteFormat
	
	* *****************************************************/
	@Override protected void closeOnce()throws IOException
	{
		if (is_root_open)
		{
			final Writer o = output;
			o.write("</");
			o.write(settings.ROOT_ELEMENT);
			o.write('>');
		};
		super.closeOnce();
	};
	
	/* ******************************************************
	
			IIndicatorWriteFormat
	
	* *****************************************************/
	/** Returns what is specified in constructor */
	@Override public boolean isDescribed(){ return is_described; };
};