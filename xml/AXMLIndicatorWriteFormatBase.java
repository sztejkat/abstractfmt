package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EClosed;
import java.io.*;
import java.util.ArrayList;
/**
	An intermediate level
	indicator writer using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>
*/
abstract class AXMLIndicatorWriteFormatBase extends AXMLFormat 
											implements IIndicatorWriteFormat
{
	/* ****************************************************
	
			Creation
	
	
	*****************************************************/
	/** Creates
	@param settings xml settings, non null
	*/
	protected AXMLIndicatorWriteFormatBase(CXMLSettings settings)
	{
		super(settings);
	};
	/* ****************************************************
		Services required from subclasses.
	
				Low level I/O
	
	*****************************************************/
	/** Like {@link java.io.Writer#write} or {@link Appendable#append}
	Will be used to write XML content to stream.
	@param c a part of XML content.
	*/
	protected abstract void write(char c)throws IOException;
	/** Like {@link java.io.Writer#write} or {@link Appendable#append}
	Will be used to write XML content to stream.
	@param csq a part of XML content.
	*/
	protected abstract void write(CharSequence csq)throws IOException;
	/** Tests if specified character can be written to output
	without escaping. Depends on charset use in output stream.
	<p>
	Charset must be able to write all characters included in {@link #setup}.
	
	@return true if can be properly encoded by charset, false if must
	be escaped. */
	protected abstract boolean canWrite(char c);
	
	
	/* ****************************************************
	
			Characters and elements escaping	
	
	*****************************************************/
	/* ------------------------------------------------------------------
				superclass tuning.
	--------------------------------------------------------------------*/
	/** Adds call to {@link #canWrite}
	*/
	@Override protected boolean isValidUnescapedTextChar(char c)
	{
		return super.isValidUnescapedTextChar(c) && canWrite(c); 		
	};
	/** Adds call to {@link #canWrite}
	*/
	@Override protected boolean isValidStartingTagChar(char c)
	{
		return super.isValidStartingTagChar(c) && canWrite(c); 		
	};
	/** Adds call to {@link #canWrite}
	*/
	@Override protected boolean isValidTagChar(char c)
	{
		return super.isValidTagChar(c) && canWrite(c); 		
	};
	/* ------------------------------------------------------------------
				Escaping
	--------------------------------------------------------------------*/			
	/** Unconditionally escapes <code>character</code>
	as described in  <A href="doc-files/xml-syntax.html">syntax definition</a>
	by writing {@link CXMLSettings#ESCAPE_CHARACTER}, up to four upper case hext digits
	and {@link CXMLSettings#ESCAPE_END_CHARACTER}, or, if <code>character</code>
	is escape char by <code>escape,escape,end_escape;</code>. Alternatively
	if character can be escape with standard &amp;xxx; escape it is preferred.
	@param character what to escape
	@throws IOException if Appendable failed.
	*/
	protected void writeEscapedChar(char character)throws IOException
	{
		//Check if standard or custom escape?
		String amp_escape = getStandardAmpEscape(character);
		if (amp_escape!=null)
		{
			write(amp_escape);
		}else
		{
			final char esc = settings.ESCAPE_CHARACTER;
			write(esc);
			if (character==esc)
			{
				write(esc);
			}else		
			{
				char d0 = D2HEX(character & 0x0F); character>>>=4;
				char d1 = D2HEX(character & 0x0F); character>>>=4;
				char d2 = D2HEX(character & 0x0F); character>>>=4;
				char d3 = D2HEX(character); 
				if (d3!='0'){ write(d3); write(d2); write(d1); }
				else
				if (d2!='0'){ write(d2); write(d1); }
				else
				if (d1!='0'){ write(d1); }
				write(d0);
			};
			write(settings.ESCAPE_END_CHARACTER);
		};
	};
	
	
	/** Tests if specified signal name (as passed to {@link #begin} )
	can be used <u>directly</u> as XML element name
	<a href="doc-files/xml-syntax.html#short_signal_form">in a short form</a>
	or if it must be encoded using 
	<a href="doc-files/xml-syntax.html#long_signal_form">in a long form</a>.
	<p>
	Default implementation tests if XML rules are met and asks {@link CXMLSettings#isDefinedElement}
	if there is no name clash.
	@param signal_name name to check, non null.
	@return true if can use short form
	@see #isReservedElement
	*/
	protected boolean isPossibleXMLElement(String signal_name)
	{
		assert(signal_name!=null);
		final int L = signal_name.length();
		if (L==0) return false; //empty element name is not allowed
		//first char must be a letter.
		char c = signal_name.charAt(0);
		if (!isValidStartingTagChar(c)) return false;
		//all characters must be letters or digits.
		for(int i = L; --i>=1; )
		{
			c = signal_name.charAt(i);
			if (!isValidTagChar(c)) return false;
		};
		//Now protect default xml
		if (L>=3)
		{
			if ( 
				(Character.toUpperCase(signal_name.charAt(0))=='X')
				 &&
				(Character.toUpperCase(signal_name.charAt(1))=='M')
				&&
				(Character.toUpperCase(signal_name.charAt(2))=='L')
				) return false;
		};
		return !settings.isDefinedElement(signal_name);
	};
	
	
	
	
};