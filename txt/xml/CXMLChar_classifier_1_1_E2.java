package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;

/**
	A toolbox class which is responsible for classifying Unicode code-points if they can
	perform certain XML functions or not, defined in context of <u>generating</u> the XML document.
	<p>
	The classification is taken directly from XML specs.
	<p>
	Note: Even tough I do apply it for "codepoints"	You can safely apply it for base-plane chars
	represented by <code>char</code>.
	<p>
	<i>Note: Whenever I say XML I do mean:
	<a href="doc-files/xml1.1/XML_specification.htm">
	Extensible Markup Language (XML) 1.1 (Second Edition)
	W3C Recommendation 16 August 2006, edited in place 29 September 2006</a>
	<p>
*/
public class CXMLChar_classifier_1_1_E2 implements IXMLCharClassifier 
{
	/* *****************************************************************
	
					IXMLCharClassifier
					
	 ******************************************************************/
	@Override public boolean isXMLRecommendedChar(int c)
	{
		return _isXMLRecommendedChar(c);
	};
	@Override public boolean isNameStartChar(int c)
	{
		return _isNameStartChar(c);
	}
	@Override public boolean isNameChar(int c)
	{
		return _isNameChar(c);
	}
	@Override public boolean isXMLSpace(int c)
	{
		return _isXMLSpace(c);
	}
	@Override public String getXMLVersion(){ return "1.1"; };
	 /* *****************************************************************
	
					Support
					
	 ******************************************************************/
	/** Tests against 2.2 of XML specs
	@param c unicode code-point.
	@return true if c represents allowed XML character.
	*/
	private static boolean isXMLChar(int c)
	{
		return ((c>=0x1)&&(c<=0xD7FF)) || ((c>=0xE000)&&(c<=0xFFFD)) ||
			   ((c>=0x10000)&&(c<=0x10FFFF));
	};
	/** Tests against 2.2 of XML specs
	@param c unicode code-point.
	@return true if c represents a restricted character as XML calls it. 
	*/
	private static boolean isRestricedChar(int c)
	{
		return  ((c>=0x1)&(c<=0x8)) || ((c>=0xB)&(c<=0xC)) || ((c>=0xE)&(c<=0x1F)) || ((c>=0x7F)&(c<=0x84)) || ((c>=0x86)&(c<=0x9F));
	};
	/** Tests agains "unrecomended" section of 2.2
	@param c unicode code-point.
	@return true if c represents character which is unrecommended. 
	*/
	private static boolean isUnrecommendedChar(int c)
	{
		return 
		((c>=0x1)&&(c<=0x8))|| ((c>=0xB)&&(c<=0xC))|| ((c>=0xE)&&(c<=0x1F))|| ((c>=0x7F)&&(c<=0x84))|| ((c>=0x86)&&(c<=0x9F))|| ((c>=0xFDD0)&&(c<=0xFDDF))||
		((c>=0x1FFFE)&&(c<=0x1FFFF))|| ((c>=0x2FFFE)&&(c<=0x2FFFF))|| ((c>=0x3FFFE)&&(c<=0x3FFFF))||
		((c>=0x4FFFE)&&(c<=0x4FFFF))|| ((c>=0x5FFFE)&&(c<=0x5FFFF))|| ((c>=0x6FFFE)&&(c<=0x6FFFF))||
		((c>=0x7FFFE)&&(c<=0x7FFFF))|| ((c>=0x8FFFE)&&(c<=0x8FFFF))|| ((c>=0x9FFFE)&&(c<=0x9FFFF))||
		((c>=0xAFFFE)&&(c<=0xAFFFF))|| ((c>=0xBFFFE)&&(c<=0xBFFFF))|| ((c>=0xCFFFE)&&(c<=0xCFFFF))||
		((c>=0xDFFFE)&&(c<=0xDFFFF))|| ((c>=0xEFFFE)&&(c<=0xEFFFF))|| ((c>=0xFFFFE)&&(c<=0xFFFFF))||
		((c>=0x10FFFE)&&(c<=0x10FFFF));
	};
	/** Tests agains "unrecomended" section of 2.2
	@param c unicode code-point.
	@return true if c represents character which allowed xml char and is not unrecommended 
	*/
	private static boolean _isXMLRecommendedChar(int c)
	{
		return isXMLChar(c) && !isUnrecommendedChar(c);
	};
	/** Tests against 2.3 of XML specs
	@param c unicode code-point.
	@return true if c represents XML "space"
	*/
	private static boolean _isXMLSpace(int c)
	{
		return (c==0x20)||(c==0x9)||(c==0xD)||(c==0xA);
	};
	/** Tests against 2.3 of XML  specs
	@param c unicode code-point.
	@return true if c represents a character which may start XML element
			name, except that we intentionally removed ':' which is 
			reserved in that chapter for namespace.
	*/
	private static boolean _isNameStartChar(int c)
	{
		return /*(c==':') || */
					(c=='_') ||
					((c>='A')&&(c<='Z')) || ((c>='a')&&(c<='z')) || ((c>=0xC0)&&(c<=0xD6)) || ((c>=0xD8)&&(c<=0xF6)) ||
					((c>=0xF8)&&(c<=0x2FF)) || ((c>=0x370)&&(c<=0x37D)) || ((c>=0x37F)&&(c<=0x1FFF)) ||
					((c>=0x200C)&&(c<=0x200D)) || ((c>=0x2070)&&(c<=0x218F)) || ((c>=0x2C00)&&(c<=0x2FEF)) ||
					((c>=0x3001)&&(c<=0xD7FF)) || ((c>=0xF900)&&(c<=0xFDCF)) || ((c>=0xFDF0)&&(c<=0xFFFD)) ||
					((c>=0x10000)&&(c<=0xEFFFF));
		 
	};
	/** Tests against 2.3 of XML ed 5. specs
	@param c unicode code-point.
	@return true if c represents a character which may be second
			and later character in xml element name.
	*/
	private static boolean _isNameChar(int c)
	{
		return _isNameStartChar(c)
				||
				(c=='-')|| (c=='.') ||
				((c>='0')&&(c<='9')) || 
				(c==0xB7)||
				((c>=0x0300)&&(c<=036F)) || ((c>=0x203F)&&(c<=2040));
	};
		
		
	
};


