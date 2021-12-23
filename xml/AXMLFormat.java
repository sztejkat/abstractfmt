package sztejkat.abstractfmt.xml;

/**
		Set of common utility methods for reading
		and writing XML formats
*/
abstract class AXMLFormat
{
				/** XML settings */
				protected final CXMLSettings settings;
				
				/** Binary nibble to hex conversion table */
				private static final char [] _D2HEX= new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
			
				
	/* ****************************************************************
	
			Construction
	
	
	*****************************************************************/				
	protected AXMLFormat(CXMLSettings settings)
	{
		assert(settings!=null);
		this.settings = settings;
	};
	/* ****************************************************************
	
			HEX/DEC conversions.
	
	
	*****************************************************************/	
	/** Hex to nibble conversion
	@param digit 0...9,a...f,A...F
	@return value 0x0...0x0F or -1 if not hex digit.
	*/
	static int HEX2D(char digit)
	{
		if ((digit>='0')&&(digit<='9')) return digit-'0';
		if ((digit>='a')&&(digit<='f')) return digit-'a'+10;
		if ((digit>='A')&&(digit<='F')) return digit-'A'+10;
		return -1;
	};
		
	/** Nibble to hex conversion
	@param nibble 0...15
	@return '0'...'F' character.
	*/
	static char D2HEX(int nibble)
	{
		assert((nibble & 0x0F)==nibble);
		return _D2HEX[nibble];
	}	
	/** Tests if character is a subject of known &amp;xx; XML 
	escapes
	@param c char to test
	@return true if it is
	@see #settings
	@see CXMLSettings#AMP_XML_ESCAPED_CHAR	
	*/
	protected final boolean isStandardAmpEscapeChar(char c)
	{
		char [] escaped = settings.AMP_XML_ESCAPED_CHAR;
		for(int i = escaped.length; --i>=0;)
		{
			if (c==escaped[i]) return true;
		};
		return false;
	};
	/** Tests if character is a subject of known &amp;xx; XML 
	escapes and returns that escape
	@param c char to test
	@return null if it is not.
	@see #settings
	@see CXMLSettings#AMP_XML_ESCAPED_CHAR
	@see CXMLSettings#AMP_XML_ESCAPES
	*/
	protected final String getStandardAmpEscape(char c)
	{
		char [] escaped = settings.AMP_XML_ESCAPED_CHAR;		
		for(int i = escaped.length; --i>=0;)
		{
			if (c==escaped[i]) return settings.AMP_XML_ESCAPES[i];
		};
		return null;
	};
	
	/* ****************************************************************
	
			Characters classification for escapes.	
	
	*****************************************************************/
	/** Checks is character is in XML allowed space
	@param c character
	@return true if it is
	*/
	protected static boolean isValidXMLCharacter(char c)
	{
	if (
				((c>=0x20)&&(c<=0xD7FF))
					||
				((c>=0xE000)&&(c<=0xFFFD))
			)
			{
				if (
						((c>=0x7F)&&(c<=0x84))
							||
						((c>=0x86)&&(c<=0x9F))
							||
						((c>=0xFDD0)&&(c<=0xFDDF))
						) return false;
				return true;
			};
		return false;
	};
	/** Tests if it is an allowed XML character in 
	 portions of XML which cannot carry XML escapes
	 like tags, attribute names and etc.
	 @param c character to check
	 @return true if allowed.
	*/
	protected boolean isValidXMLTokenChar(char c)
	{
		if (isValidXMLCharacter(c))
		{
			if (Character.isWhitespace(c)) return false;
			switch(c)
			{
				case '<':
				case '>':
				case '\'':
				case '\"':
				case '&':
						return false;
				default: return true;
			}
		}else
			return false;
	};
	/** Tests if specified character can be put into
	the XML stream inside a text representing <code>char[]</code> block
	without escaping.
	<p>
	Default implementation tests againts rules 
	specified in <a href="doc-files/xml-syntax.html#ESCAPED_CHAR_ARRAY">xml syntax definition</a>
	<p>
	Subclasses should also return false if character cannot be encoded with a low
	level stream char-set (ie, ISO-xxx 8 bit code page).
	@param c character
	@return true if character does not need escaping
	*/
	protected boolean isValidUnescapedTextChar(char c)
	{
		//it must be in XML charset.
		if (!isValidXMLCharacter(c)) return false;
		//first check if it is an escape char
		if (c==settings.ESCAPE_CHARACTER) return false;
		//standard XML elements which needs to be escaped.
		//This must include < >	
		if (isStandardAmpEscapeChar(c)) return false;
		//And we escape all white spaces since they must be correctly preserved
		if (Character.isWhitespace(c)) return false;
		return true;
	};
	/** Tests if specified character is a valid character which can be put into
	the XML stream inside an attribute value without escaping.	
	Calls {@link #isValidUnescapedTextChar} 
	@param c character to validate
	@return  true if character does not need escaping
	*/
	protected boolean isValidUnescapedAttributeValueChar(char c)
	{
		//Note: We escape all white spaces since they must be correctly preserved.
		if (!isValidUnescapedTextChar(c)) return false;
		return !((c=='\'')||(c=='\"'));
	};
	/** Tests if specified character can start a tag 
	@param c character to check
	@return true if it can start tag name
	*/
	protected boolean isValidStartingTagChar(char c)
	{
		return Character.isLetter(c);
	};
	/** Tests if specified character can be inside tag name 
	@param c character to check
	@return true if it can be inside tag name
	*/
	protected boolean isValidTagChar(char c)
	{
		return (Character.isLetterOrDigit(c) || (c=='_') || (c=='.'));
	};
	
};
