package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;

/**
	A toolbox class which is responsible for 
	classifying Unicode code-points if they can
	perform certain XML functions or not, defined
	in context of <u>generating</u> the XML document.
	<p>
	The classification is taken directly from 
	XML specs.
	<p>
	Note: Even tough I do apply it for "codepoints"
	You can safely apply it for base-plane chars
	represented by <code>char</code>.
	<p>
	<i>Note: Whenever I say XML I do mean:
	<a href="doc-files/XML_specification.htm">
	Extensible Markup Language (XML) 1.0 (Fifth Edition) W3C Recommendation 26 November 2008</a>
*/
static class SXMLChar_classifier
{
	/** Tests against 2.2 of XML specs
	@param c unicode code-point.
	@return true if c represents allowed XML character.
	*/
	static boolean isXMLChar(int c)
	{
		return (c==0x9)||(c==0xA)||(c==0xD)||
			   ((c>=0x20)&&(c<=0xD7FF))||
			   ((c>=0xE000)&&(c<=0xFFFD))||
			   ((c>=0x1_0000)&&(c<=0x10_FFFF));
	};
	/** Tests agains "unrecomended" section of 2.2
	@param c unicode code-point.
	@return true if c represents allowed XML character
			and is not "discouraged" character. 
	*/
	static boolean isXMLRecommendedChar(int c)
	{
		return isXML(c) && (!
				(
					((c>=0x7F)&&(c<=0x84))|| ((c>=0x86)&&(c<=0x9F))|| ((c>=0xFDD0)&&(c<=0xFDEF))||
					((c>=0x1FFFE)&&(c<=0x1FFFF))|| ((c>=0x2FFFE)&&(c<=0x2FFFF))|| ((c>=0x3FFFE)&&(c<=0x3FFFF))||
					((c>=0x4FFFE)&&(c<=0x4FFFF))|| ((c>=0x5FFFE)&&(c<=0x5FFFF))|| ((c>=0x6FFFE)&&(c<=0x6FFFF))||
					((c>=0x7FFFE)&&(c<=0x7FFFF))|| ((c>=0x8FFFE)&&(c<=0x8FFFF))|| ((c>=0x9FFFE)&&(c<=0x9FFFF))||
					((c>=0xAFFFE)&&(c<=0xAFFFF))|| ((c>=0xBFFFE)&&(c<=0xBFFFF))|| ((c>=0xCFFFE)&&(c<=0xCFFFF))||
					((c>=0xDFFFE)&&(c<=0xDFFFF))|| ((c>=0xEFFFE)&&(c<=0xEFFFF))|| ((c>=0xFFFFE)&&(c<=0xFFFFF))||
					((c>=0x10FFFE)&&(c<=0x10FFFF))
				);
	};
	/** Tests against 2.3 of XML specs
	@param c unicode code-point.
	@return true if c represents XML "space"
	*/
	static boolean isXMLSpace(int c)
	{
		return (c==0x20)||(c==0x9)||(c==0xD)||(c==0xA);
	};
	/** Tests against 2.3 of XML specs
	@param c unicode code-point.
	@return true if c represents a character which may start XML element
			name, except that we intentionally removed ':' which is 
			reserved in that chapter for namespace.
	*/
	static boolean isNameStartChar(int c)
	{
			return /*(c==':') || */
					(c=='_') ||
					((c>='A')&&(c<='Z')) || ((c>='a')&&(c<='z')) || ((c>=0xC0&&(c<=0xD6)) || ((c>=0xD8&&(c<=0xF6)) ||
					((c>=0xF8)&&(c<=0x2FF)) || ((c>=0x370)&&(c<=0x37D)) || ((c>=0x37F)&&(c<=0x1FFF)) ||
					((c>=0x200C)&&(c<=0x200D)) || ((c>=0x2070)&&(c<=0x218F)) || ((c>=0x2C00)&&(c<=0x2FEF)) ||
					((c>=0x3001)&&(c<=0xD7FF)) || ((c>=0xF900)&&(c<=0xFDCF)) || ((c>=0xFDF0)&&(c<=0xFFFD)) ||
					((c>=0x10000)&&(c<=0xEFFFF));
	};
	/** Tests against 2.3 of XML specs
	@param c unicode code-point.
	@return true if c represents a character which may be second
			and later character in xml element name.
	*/
	static boolean isNameChar(int c)
	{
		return isNameStartChar(c)
				||
				(c=='-')|| (c=='.') ||
				((c>='0')&&(c<='9')) || 
				(c==0xB7)||
				((c>=0x0300)&&(c<=036F)) || ((c>=0x203F)&&(c<=2040));
	};
		
			/* Note: I decided to use int instead of enum because
			ints are significantly faster and clear enough in this
			situation since we won't be using any possibly conflicting
			sets in here.
			*/
			static final int XML_DATA_CHAR = 0;
			static final int XML_ENTITY = 1;
			static final int NON_XML_COMPATIBLE = 2;
			
	/** Tests against 2.4 of XML specs.
	@param c unicode code-point.
	@return
			<ul>
				<li>0 ({@link #XML_DATA_CHAR})if c represents a character which may contained
				in XML element body WITHOUT the need of escaping
				either with XML entity or our custom escaping.
				<p>
				Since the specs is slightly flexible int that point here we harden
				it to:
				<ul>
					<li>the c must be {@link #isXMLRecommendedChar};</li>
					<li>it must not contain &lt; &gt; nor &amp;</li>
				</ul>
				</li>
				
				<li>if this method returns <code>1</code> ({@link #XML_ENTITY}) the 
				<code>c</code> must be escaped using schema pointed
				in 4.1/4.6 of XML specs;</li>
				
				<li>if this method retrns <code>2</code> ({@link #NON_XML_COMPATIBLE})
				the <code>c</code> must be escaped using custom schema which 
				allows for absolutely any character.
				</li>
				
			</ul>
	*/
	static int isCharData(int c)
	{
		if ((c=='<')||(c=='>')||(c=='&')) return XML_ENTITY;
		if (!isXMLRecommendedChar(c)) return  NON_XML_COMPATIBLE;
		return XML_DATA_CHAR;
	};
	
};


