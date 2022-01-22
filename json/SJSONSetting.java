package sztejkat.abstractfmt.json;

/**
	Standard selection of JSON settings
*/
public class SJSONSetting 
{
	/** A standard JSON set which removes all AJAX conflicting
	names and uses non-conflicting type information.
	This setting is described in a
	<a href="doc-files/json-syntax.html">syntax</a>.
	*/
	public static final CJSONSettings AJAX = new
		CJSONSettings(
			"begin",// String BEGIN,
		 	"content",// String CONTENT,
		 	"_bool",// String BOOLEAN_ELEMENT,
		 	"_byte",// String BYTE_ELEMENT,
		 	"_char",// String CHAR_ELEMENT,
		 	"_short",// String SHORT_ELEMENT,
		 	"_int",// String INT_ELEMENT,
		 	"_long",// String LONG_ELEMENT,
		 	"_float",// String FLOAT_ELEMENT,
		 	"_double",// String DOUBLE_ELEMENT,
		 	"_bools",// String BOOLEAN_BLOCK_ELEMENT,
		 	"_bytes",// String BYTE_BLOCK_ELEMENT,
		 	"_text",// String CHAR_BLOCK_ELEMENT,
		 	"_shorts",// String SHORT_BLOCK_ELEMENT,
		 	"_ints",// String INT_BLOCK_ELEMENT,
		 	"_longs",// String LONG_BLOCK_ELEMENT,
		 	"_floats",// String FLOAT_BLOCK_ELEMENT,
		 	"_doubles",// String DOUBLE_BLOCK_ELEMENT,
		 	new String[]
		 	{
		 	//After https://www.w3schools.com/js/js_reserved.asp
		 	"abstract","arguments","await",
		 	"boolean","break","byte",
			"case","catch","char","class","const","continue",
			 "debugger","default","delete","do","double",
			 "else","enum","eval","export","extends",
			 "false","final","finally","float","for","function",
			 "goto",
			  "if","implements","import","in","instanceof","int","interface",
			  "let","long",
			   "native","new","null",
			   "package","private","protected","public",
				"return",
				"short","static","super","switch","synchronized",
				"this","throw","throws","transient","true","try","typeof",
				"var","void","volatile",
				"while","with",
				"yield"
		 	}// String [] RESERVED_WORD
		 	); 
	/** A standard JSON set which does not remove any reserved words.
	Streams produced with this setting won't be AJAX compatible, but can
	be used to produce more compact files if not to be used within AJAX content.*/
	public static final CJSONSettings PLAIN = new
		CJSONSettings(
			"begin",// String BEGIN,
		 	"content",// String CONTENT,
		 	"bool",// String BOOLEAN_ELEMENT,
		 	"byte",// String BYTE_ELEMENT,
		 	"char",// String CHAR_ELEMENT,
		 	"short",// String SHORT_ELEMENT,
		 	"int",// String INT_ELEMENT,
		 	"long",// String LONG_ELEMENT,
		 	"float",// String FLOAT_ELEMENT,
		 	"double",// String DOUBLE_ELEMENT,
		 	"bool[]",// String BOOLEAN_BLOCK_ELEMENT,
		 	"byte[]",// String BYTE_BLOCK_ELEMENT,
		 	"text",// String CHAR_BLOCK_ELEMENT,
		 	"short[]",// String SHORT_BLOCK_ELEMENT,
		 	"int[]",// String INT_BLOCK_ELEMENT,
		 	"long[]",// String LONG_BLOCK_ELEMENT,
		 	"float[]",// String FLOAT_BLOCK_ELEMENT,
		 	"double[]",// String DOUBLE_BLOCK_ELEMENT,
		 	new String[0]// String [] RESERVED_WORD
		 	); 
};