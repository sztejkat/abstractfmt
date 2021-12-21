package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EClosed;
import java.io.Writer;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
/**
	An intermediate level indicator writer using XML as specified in
	<A href="doc-files/xml-syntax.html">syntax definition</a>
	<p>
	Basically intermediate level I/O definitions, including escaping
	support.
*/
abstract class AXMLIndicatorWriteFormatBase extends AXMLFormat 
											implements IIndicatorWriteFormat
{
				/** Output to which data are written */
				protected final Writer output;
				/** Used to implement {@link #canWrite}. Can be null. */
				private final CharsetEncoder charset_enc;
				/** Set to true once closed */
				private boolean is_closed;
	/* ****************************************************
	
			Creation
	
	
	*****************************************************/
	/** Creates
	@param out output to which write.
	@param charset optional charset which will be used to detect
		characters which can't be correctly encoded by output.
		If this value is null this format assumes that <code>out</code>
		can encode a full <code>char</code> space.
	@param settings XML settings, non null. 
	*/
	protected AXMLIndicatorWriteFormatBase( 
										  Writer output,
										  Charset charset,
										  CXMLSettings settings)
	{
		super(settings);
		assert(output!=null);
		this.output = output;
		this.charset_enc  = charset==null ? null : charset.newEncoder();
	};
	/* ****************************************************
	
				Low level I/O
	
	*****************************************************/
	/** Tests if specified character can be written to output
	without escaping. Depends on charset use in output stream.
	<p>
	Charset must be able to write all characters included in {@link #settings}.
	@param c character to check.	
	@return true if can be properly encoded by charset, false if must
	be escaped. 
	*/
	protected boolean canWrite(char c)
	{
		if (charset_enc==null) return true;
		return charset_enc.canEncode(c);
	};
	/* ****************************************************
		
			Services tunable from subclasses
			
	*****************************************************/
	/** Called in {@link #close} when closed for a first 
	time. Default implementation closes output.
	@throws IOException if failed.
	*/
	protected void closeOnce()throws IOException
	{
			output.close();
	};
	/* ****************************************************
		
			State
			
	*****************************************************/
	/** Tests if format is usable
	@throws EClosed if closed
	@see #is_closed
	@see #close 
	*/
	protected void validateNotClosed()throws EClosed
	{
		if (is_closed) throw new EClosed();
	};
	
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
		writeEscapedChar(character, false);
	};
	/** Unconditionally escapes <code>character</code>
	as described in  <A href="doc-files/xml-syntax.html">syntax definition</a>
	by writing {@link CXMLSettings#ESCAPE_CHARACTER}, up to four upper case hext digits
	and {@link CXMLSettings#ESCAPE_END_CHARACTER}, or, if <code>character</code>
	is escape char by <code>escape,escape,end_escape;</code>. Alternatively
	if character can be escape with standard &amp;xxx; escape it is preferred.
	@param ommit_escape_end_character if true the traling escape character
		is ommited in hex escapes (but remains in standard AMP escapes to 
		be fully XML compatible). Set this to true if writing a single, elementary
		character primitive in described stream. 
	@param character what to escape
	@throws IOException if Appendable failed.
	*/
	protected void writeEscapedChar(char character, boolean ommit_escape_end_character)throws IOException
	{
		//Check if standard or custom escape?
		String amp_escape = getStandardAmpEscape(character);
		if (amp_escape!=null)
		{
			output.write(amp_escape);
		}else
		{
			final char esc = settings.ESCAPE_CHARACTER;
			final Writer o = output;
			o.write(esc);
			if (character==esc)
			{
				o.write(esc);
			}else		
			{
				char d0 = D2HEX(character & 0x0F); character>>>=4;
				char d1 = D2HEX(character & 0x0F); character>>>=4;
				char d2 = D2HEX(character & 0x0F); character>>>=4;
				char d3 = D2HEX(character); 
				if (d3!='0'){ o.write(d3); o.write(d2); o.write(d1); }
				else
				if (d2!='0'){ o.write(d2); o.write(d1); }
				else
				if (d1!='0'){ o.write(d1); }
				o.write(d0);
			};
			if (!ommit_escape_end_character) o.write(settings.ESCAPE_END_CHARACTER);
		};
	};
	
	
	/** Tests if specified signal name (as passed to {@link #writeBeginDirect} and others )
	can be used <u>directly</u> as XML element name
	<a href="doc-files/xml-syntax.html#short_signal_form">in a short form</a>
	or if it must be encoded using 
	<a href="doc-files/xml-syntax.html#long_signal_form">in a long form</a>.
	<p>
	Default implementation tests if XML rules are met and asks {@link CXMLSettings#isDefinedElement}
	if there is no name clash.
	@param signal_name name to check, non null.
	@return true if can use short form
	@see CXMLSettings#isDefinedElement
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
	
	
	/* ****************************************************
	
			Closable & Flushable
	
	*****************************************************/
	/** Flushes pending separator if any */
	@Override public void flush()throws IOException
	{
		validateNotClosed();
		output.flush();
	};
	
	/** Flushes, Sets closed status to true.
	If it was false calls {@link #closeOnce}
	@see #validateNotClosed
	*/
	@Override public void close()throws IOException
	{
		if (!is_closed)
		{
			try{
				flush();
			}finally{
					try{
					closeOnce();
				}finally{is_closed = true; };
			}
		};
	};
	
};