package sztejkat.abstractfmt.xml;

/**
	A storage for default XML settings
*/
public abstract class SXMLSettings
{
			/** Bare XML settings, without root and prolog, as described in 
			<A href="doc-files/xml-syntax.html#ESCAPING">syntax definition</a>.
			*/
			public static final CXMLSettings BARE = new CXMLSettings(   
							 null,//String PROLOG,
							 '%',//char ESCAPE_CHARACTER,
							 ';',//char ESCAPE_END_CHARACTER,
							 "e",//String EVENT,
							 "n",//String SIGNAL_NAME_ATTR,
							 ';',//char PRIMITIVE_SEPARATOR,
							 null,//String ROOT_ELEMENT,
							 "o",//String BOOLEAN_ELEMENT,
							 "b",//String BYTE_ELEMENT,
							 "c",//String CHAR_ELEMENT,
							 "s",//String SHORT_ELEMENT,
							 "i",//String INT_ELEMENT,
							 "l",//String LONG_ELEMENT,
							 "f",//String FLOAT_ELEMENT,
							 "d",//String DOUBLE_ELEMENT,
							 "oa",//String BOOLEAN_BLOCK_ELEMENT,		
							 "ba",//String BYTE_BLOCK_ELEMENT,		
							 "ca",//String CHAR_BLOCK_ELEMENT,		
							 "sa",//String SHORT_BLOCK_ELEMENT,		
							 "ia",//String INT_BLOCK_ELEMENT,		
							 "la",//String LONG_BLOCK_ELEMENT,		
							 "fa",//String FLOAT_BLOCK_ELEMENT,		
							 "da"//String DOUBLE_BLOCK_ELEMENT
							 );
			/** Full XML settings, with root and prolog for UTF-8 encoding, as described in 
			<A href="doc-files/xml-syntax.html#ESCAPING">syntax definition</a>.
			*/
			public static final CXMLSettings FULL_UTF8 = new CXMLSettings(
											BARE,//CXMLSettings copy_from,
				 							"<?xml version=\"1.0\" encoding=\"UTF-8\"?> <?sztejkat.abstractfmt.xml?>",// String PROLOG,
				 							"root" //String ROOT_ELEMENT
				 							);
			/** A long format, using same escapes as and sequene terminators
			as {@link #BARE} and:
			<ul>
				<li><code>event</code> for long event tag;</li>
				<li><code>name</code> for name attribute in it;</li>
				<li>java types <code>boolean, byte,</code> etc for primitives;</li>
				<li><code>boolean_array</code> and etc. for blocks.</li> 
			</ul>
			*/
			public static final CXMLSettings LONG_BARE = new CXMLSettings(   
							 null,//String PROLOG,
							 '%',//char ESCAPE_CHARACTER,
							 ';',//char ESCAPE_END_CHARACTER,
							 "event",//String EVENT,
							 "name",//String SIGNAL_NAME_ATTR,
							 ';',//char PRIMITIVE_SEPARATOR,
							 null,//String ROOT_ELEMENT,
							 "boolean",//String BOOLEAN_ELEMENT,
							 "byte",//String BYTE_ELEMENT,
							 "char",//String CHAR_ELEMENT,
							 "short",//String SHORT_ELEMENT,
							 "int",//String INT_ELEMENT,
							 "long",//String LONG_ELEMENT,
							 "float",//String FLOAT_ELEMENT,
							 "double",//String DOUBLE_ELEMENT,
							 "boolean_array",//String BOOLEAN_BLOCK_ELEMENT,		
							 "byte_array",//String BYTE_BLOCK_ELEMENT,		
							 "char_array",//String CHAR_BLOCK_ELEMENT,		
							 "short_array",//String SHORT_BLOCK_ELEMENT,		
							 "int_array",//String INT_BLOCK_ELEMENT,		
							 "long_array",//String LONG_BLOCK_ELEMENT,		
							 "float_array",//String FLOAT_BLOCK_ELEMENT,		
							 "double_array"//String DOUBLE_BLOCK_ELEMENT
							 );				
			/** Full XML settings for {@link #LONG_BARE} with "root" as root element
			 and prolog:
			 <pre>
			 &lt;?xml version="1.0" encoding="UTF-8"?&gt; &lt;?sztejkat.abstractfmt.xml variant="long"&gt;
			 </pre>
			*/
			public static final CXMLSettings LONG_FULL_UTF8 = new CXMLSettings(
											LONG_BARE,//CXMLSettings copy_from,
				 							"<?xml version=\"1.0\" encoding=\"UTF-8\"?> <?sztejkat.abstractfmt.xml variant=\"long\"?>",// String PROLOG,
				 							"root" //String ROOT_ELEMENT
				 							);				

		private SXMLSettings(){};
}; 