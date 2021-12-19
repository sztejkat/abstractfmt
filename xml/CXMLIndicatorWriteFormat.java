package sztejkat.abstractfmt.xml;
import java.io.Writer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
/**
		Implementation which is using {@link java.io.Writer}
		as a back-end. 
*/
public class CXMLIndicatorWriteFormat extends AXMLIndicatorWriteFormat
{
				/** Low level output */
				private final Writer out;
				/** Used to implement {@link #canWrite}. Can be null. */
				private final CharsetEncoder charset_enc;
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
		super(settings);
		assert(out!=null);
		this.out = out;
		this.charset_enc  = charset==null ? null : charset.newEncoder();
		this.is_described = is_described;
	};
	/** If root element is defined opens it. 
	@throws IOException if failed.
	*/
	public void open()throws IOException
	{
		if (settings.PROLOG!=null)
		{
			write(settings.PROLOG);
		};
		if (settings.ROOT_ELEMENT!=null)
		{
				write('<');
				write(settings.ROOT_ELEMENT);
				write('>');
				is_root_open = true;
		};
	};
	/* ******************************************************
	
			Services required by AXMLIndicatorWriteFormat
	
	* *****************************************************/
	@Override protected void closeOnce()throws IOException
	{
		if (is_root_open)
		{
			write("</");
			write(settings.ROOT_ELEMENT);
			write('>');
		};
		out.close();
	};
	/* ******************************************************
	
			Services tuned in AXMLIndicatorWriteFormat
	
	* *****************************************************/
	@Override public void flush()throws IOException
	{
		super.flush();
		out.flush();
	};
	/* ******************************************************
	
			Services required by AXMLIndicatorWriteFormatBase
	
	* *****************************************************/
	@Override protected void write(char c)throws IOException
	{
		out.write(c);
	};
	@Override protected void write(CharSequence csq)throws IOException
	{
		out.append(csq);
	};
	/** Uses charset specified in constructor */
	@Override protected boolean canWrite(char c)
	{
		if (charset_enc==null) return true;
		return charset_enc.canEncode(c);
	};
	/* ******************************************************
	
			IIndicatorWriteFormat
	
	* *****************************************************/
	/** Returns what is specified in constructor */
	@Override public boolean isDescribed(){ return is_described; };
};