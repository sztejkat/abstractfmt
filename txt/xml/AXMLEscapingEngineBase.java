package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;
import java.io.IOException;
/**
	An escaping engine base which providing
	a base to escape character using both XML entity
	and our own mode.
*/
abstract class AXMLEscapingEngineBase extends AEscapingEngine
{
				/** Hex conversion table for escaping */
				private static final char [] HEX = new char[]
										{
											'0','1','2','3','4','5','6','7','8','9',
											'A','B','C','D','E','F'
										};
										
	/* *************************************************************************
	
	
				XML entity &#x... escapes
	
	
	**************************************************************************/
	/**
		Escapes a Unicode code-point using <code>&amp;#xHHHHH;</code>
		syntax.
		<p>
		This style of escaping can be used only inside the XML element body
		and can be used to carry on only allowed code-points.
		
		@param c codepoint to escape
		@throws IOException if failed.
		@throws AssertionError if not a codepoint.
		
		@see SXMLChar_classifier#isXMLChar
	*/
	protected void escapeCodePointAsHexEntity(int c)throws IOException
	{
		//We do encode it as &#xHHHHH...;
		assert((c>=0)&&(c<=0x10FFFF)):"not a code point 0x"+Integer.toHexString(c);
		assert(SXMLChar_classifier.isXMLChar(c));
		out('&');out('#');out('x');
		boolean was_emited = false;
		//Up to 6 digits so we dig out just a part of it
		for(int i=6;--i>=0;)
		{
			int nibble = (c & 0xF0_0000)>>>(4*5);
			if (!was_emited)
			{
				if (nibble==0) continue;
			};
			was_emited = true;
			out(HEX[nibble]);
			c<<=4;
		};
		assert(c==0);
		out(';');
	};
	/**
		Escapes a Unicode code-point using <code>&amp;#xHHHHH;</code>
		syntax or dedicated obligatory set of escapes
		as "4.6 Predefined Entities" of XML specification do require.
		<p>
		This style of escaping can be used only inside the XML element body
		and can be used to carry on only allowed code-points.
		
		@param c codepoint to escape
		@throws IOException if failed.
		@throws AssertionError if not a codepoint.
		
		@see SXMLChar_classifier#isXMLChar
	*/
	protected void escapeCodePointAsEntity(int c)throws IOException
	{
		switch(c)
		{
			case '<':	out('&');out('l');out('t');out(';'); break;
			case '>':	out('&');out('g');out('t');out(';'); break;
			case '&':   out('&');out('a');out('m');out('p');out(';'); break;
			case '\'':  out('&');out('a');out('p');out('o');out('s');out(';'); break;
			case '\"':  out('&');out('q');out('u');out('o');out('t');out(';'); break;
			default: escapeCodePointAsHexEntity(c);
		}
	};
	
	/* *************************************************************************
	
	
				Custom _HHHH escapes
	
	
	**************************************************************************/
	/**
		Escapes a Java <code>char</code> using <code>_HHHHH</code>
		syntax.
		<p>
		This style of escaping can appear everywere, including inside
		elements names.
		
		@param c Java character to escape.
		@throws IOException if failed.
		@throws AssertionError if not a codepoint.
	*/
	protected void escapeAsCustomHexEscape(char c)throws IOException
	{
		out('_');
		//In this mode we do ALWAYS emit 4 digits. This is due to
		//the fact, that ; can't appear inside an XML name.
		for(int i=4;--i>=0;)
		{
			int nibble = (c & 0xF000)>>>(4*3);
			out(HEX[nibble]);
			c<<=4;
		};
		assert(c==0);
	};
	/**
		Escapes a Java <code>char</code> using <code>_HHHHH</code>
		or <code>__</code>	syntax.
		<p>
		This style of escaping can appear everywere, including inside
		elements names.
		
		@param c Java character to escape.
		@throws IOException if failed.
		@throws AssertionError if not a codepoint.
		
		@see #escapeAsCustomHexEscape
	*/
	protected void escapeAsCustomEscape(char c)throws IOException
	{
		switch(c)
		{
			case '_': out('_');	out('_'); break;
			default: escapeAsCustomHexEscape(c);
		};
	};
	
};