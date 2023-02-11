package sztejkat.abstractfmt.txt.xml;

/**
	XML classifier
*/
public interface IXMLCharClassifier
{
	/** Tests if specified unicode code point is
	a character which is allowed in XML element body
	@param c unicode code-point
	@return true if is recommended as a body char
	*/
	public boolean isXMLRecommendedChar(int c);
	/** Tests if specified unicode code point is
	a character which can start a name of an XML element
	@param c unicode code-point
	@return true if can.
	*/
	public boolean isNameStartChar(int c);
	/** Tests if specified unicode code point is
	a character which can be in a name of an XML element (except
	a first char, which is tested by {@link #isNameStartChar})
	@param c unicode code-point
	@return true if can.
	*/
	public boolean isNameChar(int c);
	/** Tests if specified unicode code point is
	a character which is a space in XML
	@param c unicode code-point
	@return true if can.
	*/
	public boolean isXMLSpace(int c);
	/** Returns string representing version to be put in XML prolog 
	@return non null, life-time constant.
	*/ 
	public String getXMLVersion();
	
	
			/* Note: I decided to use int instead of enum because
			ints are significantly faster and clear enough in this
			situation since we won't be using any possibly conflicting
			sets in here.
			*/
			/** See {@link #isDataChar}. A valid body character. */
			static final int XML_DATA_CHAR = 0;
			/** See {@link #isDataChar}. Not a body character, but allowed in XML */
			static final int XML_ENTITY = 1;
			/** See {@link #isDataChar}. Not a body character, and NOT allowed in XML */
			static final int NON_XML_COMPATIBLE = 2;
			
	/** Tests against if character is a valid element body character.
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
					<li>the c must be recommended XML character;</li>
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
	public default int isCharData(int c)
	{
		if ((c=='<')||(c=='>')||(c=='&')) return XML_ENTITY;
		if (!isXMLRecommendedChar(c)) return  NON_XML_COMPATIBLE;
		return XML_DATA_CHAR;
	}
};