package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.TIndicator;
import java.io.*;

/**
	Settings of XML, names of elements, escapes and etc.
*/
public class CXMLSettings
{
		/** Character initializing escape sequence.
		<p>
		See <A href="doc-files/xml-syntax.html#ESCAPING">syntax definition</a>.
		*/
		public final char ESCAPE_CHARACTER;
		/** Character terminating escape sequence.
		<p>
		See <A href="doc-files/xml-syntax.html#ESCAPING">syntax definition</a>.
		*/
		public final char ESCAPE_END_CHARACTER;
		/** String representing XML element used to
		store 
		<A href="doc-files/xml-syntax.html#long_signal_form">long signal form</a>.
		*/
		public final String EVENT;
		/** String representing a name of an XML attribute 
		of {@link EVENT} element used to carry
		<A href="doc-files/xml-syntax.html#ESCAPED_ATTR">escaped signal name</a>.
		*/
		public final String SIGNAL_NAME_ATTR;
		/** Root XML element. May be null if no root element is used
		(bare XML format) */
		public final String ROOT_ELEMENT;
		/** Character used to separate primitives */
		public final char PRIMITIVE_SEPARATOR;		
		/** Element to enclose boolean elementary primitive. */
		public final String BOOLEAN_ELEMENT;
		/** Element to enclose byte elementary primitive. */
		public final String BYTE_ELEMENT;
		/** Element to enclose char elementary primitive. */
		public final String CHAR_ELEMENT;
		/** Element to enclose short elementary primitive. */
		public final String SHORT_ELEMENT;
		/** Element to enclose int elementary primitive. */
		public final String INT_ELEMENT;
		/** Element to enclose long elementary primitive. */
		public final String LONG_ELEMENT;
		/** Element to enclose float elementary primitive. */
		public final String FLOAT_ELEMENT;
		/** Element to enclose double elementary primitive. */
		public final String DOUBLE_ELEMENT;
		
		
		/** Element to enclose boolean block primitive. */
		public final String BOOLEAN_BLOCK_ELEMENT;
		/** Element to enclose byte block primitive. */
		public final String BYTE_BLOCK_ELEMENT;
		/** Element to enclose char block primitive. */
		public final String CHAR_BLOCK_ELEMENT;
		/** Element to enclose short block primitive. */
		public final String SHORT_BLOCK_ELEMENT;
		/** Element to enclose int block primitive. */
		public final String INT_BLOCK_ELEMENT;
		/** Element to enclose long block primitive. */
		public final String LONG_BLOCK_ELEMENT;
		/** Element to enclose float block primitive. */
		public final String FLOAT_BLOCK_ELEMENT;
		/** Element to enclose double block primitive. */
		public final String DOUBLE_BLOCK_ELEMENT;
		
		
		CXMLSettings(
				 char ESCAPE_CHARACTER,
			 	 char ESCAPE_END_CHARACTER,
				 String EVENT,
				 String SIGNAL_NAME_ATTR,
				 char PRIMITIVE_SEPARATOR,
				 String ROOT_ELEMENT,
				 String BOOLEAN_ELEMENT,
				 String BYTE_ELEMENT,
				 String CHAR_ELEMENT,
				 String SHORT_ELEMENT,
				 String INT_ELEMENT,
				 String LONG_ELEMENT,
				 String FLOAT_ELEMENT,
				 String DOUBLE_ELEMENT,
				 String BOOLEAN_BLOCK_ELEMENT,		
				 String BYTE_BLOCK_ELEMENT,		
				 String CHAR_BLOCK_ELEMENT,		
				 String SHORT_BLOCK_ELEMENT,		
				 String INT_BLOCK_ELEMENT,		
				 String LONG_BLOCK_ELEMENT,		
				 String FLOAT_BLOCK_ELEMENT,		
				 String DOUBLE_BLOCK_ELEMENT
				 )
	{
		assert(ESCAPE_CHARACTER!=ESCAPE_END_CHARACTER):"escape start and end have to differ";
		assert(PRIMITIVE_SEPARATOR!=ESCAPE_CHARACTER):"primitive separator can't be escape.";
		assert(".-e0123456789ABCDEF".indexOf(PRIMITIVE_SEPARATOR)==-1):"PRIMITIVE_SEPARATOR can't be hex digit, decimal dot, exponent char or minus";
		assert( EVENT!=null);
		 assert( SIGNAL_NAME_ATTR!=null);
		 //assert( ROOT_ELEMENT!=null); <-- allowed.
		 assert( BOOLEAN_ELEMENT!=null);
		 assert( BYTE_ELEMENT!=null);
		 assert( CHAR_ELEMENT!=null);
		 assert( SHORT_ELEMENT!=null);
		 assert( INT_ELEMENT!=null);
		 assert( LONG_ELEMENT!=null);
		 assert( FLOAT_ELEMENT!=null);
		 assert( DOUBLE_ELEMENT!=null);
		 assert( BOOLEAN_BLOCK_ELEMENT!=null);		
		 assert( BYTE_BLOCK_ELEMENT!=null);		
		 assert( CHAR_BLOCK_ELEMENT!=null);		
		 assert( SHORT_BLOCK_ELEMENT!=null);		
		 assert( INT_BLOCK_ELEMENT!=null);		
		 assert( LONG_BLOCK_ELEMENT!=null);		
		 assert( FLOAT_BLOCK_ELEMENT!=null);		
		 assert( DOUBLE_BLOCK_ELEMENT!=null);
		 
		 
		 this.ESCAPE_CHARACTER=ESCAPE_CHARACTER;
		 this.ESCAPE_END_CHARACTER=ESCAPE_END_CHARACTER;
		 this.EVENT=EVENT;
		 this.SIGNAL_NAME_ATTR=SIGNAL_NAME_ATTR;
		 this.PRIMITIVE_SEPARATOR=PRIMITIVE_SEPARATOR;
		 this.ROOT_ELEMENT=ROOT_ELEMENT;
		 this.BOOLEAN_ELEMENT=BOOLEAN_ELEMENT;
		 this.BYTE_ELEMENT=BYTE_ELEMENT;
		 this.CHAR_ELEMENT=CHAR_ELEMENT;
		 this.SHORT_ELEMENT=SHORT_ELEMENT;
		 this.INT_ELEMENT=INT_ELEMENT;
		 this.LONG_ELEMENT=LONG_ELEMENT;
		 this.FLOAT_ELEMENT=FLOAT_ELEMENT;
		 this.DOUBLE_ELEMENT=DOUBLE_ELEMENT;
		 this.BOOLEAN_BLOCK_ELEMENT=BOOLEAN_BLOCK_ELEMENT;		
		 this.BYTE_BLOCK_ELEMENT=BYTE_BLOCK_ELEMENT;		
		 this.CHAR_BLOCK_ELEMENT=CHAR_BLOCK_ELEMENT;		
		 this.SHORT_BLOCK_ELEMENT=SHORT_BLOCK_ELEMENT;		
		 this.INT_BLOCK_ELEMENT=INT_BLOCK_ELEMENT;		
		 this.LONG_BLOCK_ELEMENT=LONG_BLOCK_ELEMENT;		
		 this.FLOAT_BLOCK_ELEMENT=FLOAT_BLOCK_ELEMENT;		
		 this.DOUBLE_BLOCK_ELEMENT=DOUBLE_BLOCK_ELEMENT;
	};
	/** Checks if specified text equals to any of 
	defined elements
	@param text what to check, non null
	@return true if any element matches
	*/
	public boolean isDefinedElement(String text)
	{		
		//Now since text representing names are usually short
		//and we will compare with many elements using hashCode
		//as fast-reject is worth paying.		
		
		if (equals(EVENT,text)) return true;
		if ((ROOT_ELEMENT!=null)&&(equals(ROOT_ELEMENT,text))) return true;
		if (equals(BOOLEAN_ELEMENT,text)) return true;
		if (equals(BYTE_ELEMENT,text)) return true;
		if (equals(CHAR_ELEMENT,text)) return true;
		if (equals(SHORT_ELEMENT,text)) return true;
		if (equals(INT_ELEMENT,text)) return true;
		if (equals(LONG_ELEMENT,text)) return true;
		if (equals(FLOAT_ELEMENT,text)) return true;
		if (equals(DOUBLE_ELEMENT,text)) return true;
		
		if (equals(BOOLEAN_BLOCK_ELEMENT,text)) return true;
		if (equals(BYTE_BLOCK_ELEMENT,text)) return true;
		if (equals(CHAR_BLOCK_ELEMENT,text)) return true;
		if (equals(SHORT_BLOCK_ELEMENT,text)) return true;
		if (equals(INT_BLOCK_ELEMENT,text)) return true;
		if (equals(LONG_BLOCK_ELEMENT,text)) return true;
		if (equals(FLOAT_BLOCK_ELEMENT,text)) return true;
		if (equals(DOUBLE_BLOCK_ELEMENT,text)) return true;
		
		return false;
	};
	/** Retrives element selected by indicator
	@param indicator with either {@link TIndicator#TYPE}
		or {@link TIndicator#FLUSH} set.
	@return XML element matching the indicator
	@throws AssertionError if indicator has no specified flags set
	*/
	public String elementByIndicator(TIndicator indicator)
	{
		assert(indicator!=null);
		assert((indicator.FLAGS & ( TIndicator.TYPE + TIndicator.FLUSH) )!=0);
		switch(indicator)
		{
		case TYPE_BOOLEAN: return BOOLEAN_ELEMENT;
		case TYPE_BYTE: return BYTE_ELEMENT;
		case TYPE_CHAR: return CHAR_ELEMENT;
		case TYPE_SHORT: return SHORT_ELEMENT;
		case TYPE_INT: return INT_ELEMENT;
		case TYPE_LONG: return LONG_ELEMENT;
		case TYPE_FLOAT: return FLOAT_ELEMENT;
		case TYPE_DOUBLE: return DOUBLE_ELEMENT;
		case FLUSH_BOOLEAN: return BOOLEAN_ELEMENT;
		case FLUSH_BYTE: return BYTE_ELEMENT;
		case FLUSH_CHAR: return CHAR_ELEMENT;
		case FLUSH_SHORT: return SHORT_ELEMENT;
		case FLUSH_INT: return INT_ELEMENT;
		case FLUSH_LONG: return LONG_ELEMENT;
		case FLUSH_FLOAT: return FLOAT_ELEMENT;
		case FLUSH_DOUBLE: return DOUBLE_ELEMENT;
		
		case TYPE_BOOLEAN_BLOCK: return BOOLEAN_BLOCK_ELEMENT;
		case TYPE_BYTE_BLOCK: return BYTE_BLOCK_ELEMENT;
		case TYPE_CHAR_BLOCK: return CHAR_BLOCK_ELEMENT;
		case TYPE_SHORT_BLOCK: return SHORT_BLOCK_ELEMENT;
		case TYPE_INT_BLOCK: return INT_BLOCK_ELEMENT;
		case TYPE_LONG_BLOCK: return LONG_BLOCK_ELEMENT;
		case TYPE_FLOAT_BLOCK: return FLOAT_BLOCK_ELEMENT;
		case TYPE_DOUBLE_BLOCK: return DOUBLE_BLOCK_ELEMENT;
		case FLUSH_BOOLEAN_BLOCK: return BOOLEAN_BLOCK_ELEMENT;
		case FLUSH_BYTE_BLOCK: return BYTE_BLOCK_ELEMENT;
		case FLUSH_CHAR_BLOCK: return CHAR_BLOCK_ELEMENT;
		case FLUSH_SHORT_BLOCK: return SHORT_BLOCK_ELEMENT;
		case FLUSH_INT_BLOCK: return INT_BLOCK_ELEMENT;
		case FLUSH_LONG_BLOCK: return LONG_BLOCK_ELEMENT;
		case FLUSH_FLOAT_BLOCK: return FLOAT_BLOCK_ELEMENT;
		case FLUSH_DOUBLE_BLOCK: return DOUBLE_BLOCK_ELEMENT;
		default: throw new AssertionError(indicator);
		}
	};
	private static boolean equals(String A, String B)
	{
		if (A.hashCode()!=B.hashCode()) return false;
		return A.equals(B);
	};
	public String explain()
	{
		return "CXMLSettings:\n"+
			"     escape character \'"+ESCAPE_CHARACTER+"\'\n"+
			"           escape end \'"+ESCAPE_END_CHARACTER+"\'\n"+
			"   long event element <"+EVENT+">\n"+
		 	"signal name attribute \'"+SIGNAL_NAME_ATTR+"\'\n"+
		    " primitives separator \'"+PRIMITIVE_SEPARATOR+"\'\n"+
		    "   long event element <"+EVENT+">\n"+
		    (ROOT_ELEMENT==null ? "  root element is undefined\n"
		    			: 
		    "         root element <"+ROOT_ELEMENT+">\n")+
		    "              boolean <"+BOOLEAN_ELEMENT+">\n"+
		    "                 byte <"+BYTE_ELEMENT+">\n"+
		    "                 char <"+CHAR_ELEMENT+">\n"+
		    "                short <"+SHORT_ELEMENT+">\n"+
		    "                  int <"+INT_ELEMENT+">\n"+
		    "                 long <"+LONG_ELEMENT+">\n"+
		    "                float <"+FLOAT_ELEMENT+">\n"+
		    "               double <"+DOUBLE_ELEMENT+">\n"+
			"             boolean[]<"+BOOLEAN_BLOCK_ELEMENT+">\n"+
		    "                byte[]<"+BYTE_BLOCK_ELEMENT+">\n"+
		    "                char[]<"+CHAR_BLOCK_ELEMENT+">\n"+
		    "               short[]<"+SHORT_BLOCK_ELEMENT+">\n"+
		    "                 int[]<"+INT_BLOCK_ELEMENT+">\n"+
		    "                long[]<"+LONG_BLOCK_ELEMENT+">\n"+
		    "               float[]<"+FLOAT_BLOCK_ELEMENT+">\n"+
		    "              double[]<"+DOUBLE_BLOCK_ELEMENT+">\n";
	};
};