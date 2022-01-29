package sztejkat.abstractfmt.json;
import sztejkat.abstractfmt.TIndicator;
/**
	Represents a set of JSON settings,
	as described in <a href="doc-files/json-syntax.html">syntax</a>
	description.
*/
public class CJSONSettings
{
		/** A text representing a "begin" name
		used in objects to represent a begin signal */
		public final String BEGIN;
		/** A text representing a "content" name
		used in objects to represent a begin signal */			
		public final String CONTENT;
		
		/** Type representing  boolean elementary primitive. */
		public final String BOOLEAN_ELEMENT;
		/** Type representing  byte elementary primitive. */
		public final String BYTE_ELEMENT;
		/** Type representing  char elementary primitive. */
		public final String CHAR_ELEMENT;
		/** Type representing  short elementary primitive. */
		public final String SHORT_ELEMENT;
		/** Type representing  int elementary primitive. */
		public final String INT_ELEMENT;
		/** Type representing  long elementary primitive. */
		public final String LONG_ELEMENT;
		/** Type representing  float elementary primitive. */
		public final String FLOAT_ELEMENT;
		/** Type representing  double elementary primitive. */
		public final String DOUBLE_ELEMENT;
		
		
		/** Type representing  boolean block primitive. */
		public final String BOOLEAN_BLOCK_ELEMENT;
		/** Type representing  byte block primitive. */
		public final String BYTE_BLOCK_ELEMENT;
		/** Type representing  char block primitive. */
		public final String CHAR_BLOCK_ELEMENT;
		/** Type representing  short block primitive. */
		public final String SHORT_BLOCK_ELEMENT;
		/** Type representing  int block primitive. */
		public final String INT_BLOCK_ELEMENT;
		/** Type representing  long block primitive. */
		public final String LONG_BLOCK_ELEMENT;
		/** Type representing  float block primitive. */
		public final String FLOAT_BLOCK_ELEMENT;
		/** Type representing  double block primitive. */
		public final String DOUBLE_BLOCK_ELEMENT;
		
		/** A set if java-script reserved words */
		public final String [] RESERVED_WORDS; 
		
	/** Creates
	 @param BEGIN non null
		 @param CONTENT non null
		 @param BOOLEAN_ELEMENT non null
		 @param BYTE_ELEMENT non null
		 @param CHAR_ELEMENT non null
		 @param SHORT_ELEMENT non null
		 @param INT_ELEMENT non null
		 @param LONG_ELEMENT non null
		 @param FLOAT_ELEMENT non null
		 @param DOUBLE_ELEMENT non null
		 @param BOOLEAN_BLOCK_ELEMENT non null
		 @param BYTE_BLOCK_ELEMENT non null
		 @param CHAR_BLOCK_ELEMENT non null
		 @param SHORT_BLOCK_ELEMENT non null
		 @param INT_BLOCK_ELEMENT non null
		 @param LONG_BLOCK_ELEMENT non null
		 @param FLOAT_BLOCK_ELEMENT non null
		 @param DOUBLE_BLOCK_ELEMENT non null
		 @param RESERVED_WORDS non null, can't carry null. This
		 		array is taken not copied.
	*/
	public CJSONSettings(
		 final String BEGIN,
		 final String CONTENT,
		 final String BOOLEAN_ELEMENT,
		 final String BYTE_ELEMENT,
		 final String CHAR_ELEMENT,
		 final String SHORT_ELEMENT,
		 final String INT_ELEMENT,
		 final String LONG_ELEMENT,
		 final String FLOAT_ELEMENT,
		 final String DOUBLE_ELEMENT,
		 final String BOOLEAN_BLOCK_ELEMENT,
		 final String BYTE_BLOCK_ELEMENT,
		 final String CHAR_BLOCK_ELEMENT,
		 final String SHORT_BLOCK_ELEMENT,
		 final String INT_BLOCK_ELEMENT,
		 final String LONG_BLOCK_ELEMENT,
		 final String FLOAT_BLOCK_ELEMENT,
		 final String DOUBLE_BLOCK_ELEMENT,
		 final String [] RESERVED_WORDS
		 )
	 {
	 	 assert( BEGIN!=null);
		 assert( CONTENT!=null);
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
		 assert(RESERVED_WORDS!=null);
		 
		 this.BEGIN=BEGIN;
		 this.CONTENT=CONTENT;
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
		 this.RESERVED_WORDS=RESERVED_WORDS;
	 };
	 
	 /** Tests if <code>s</code> is in {@link #RESERVED_WORDS}
	 list. The comparison is case sensitive.
	 @param s string to check.
	 @return true if on reserved words list.
	 */ 
	 public boolean isReservedWord(String s)
	 {
	 	for(String r: RESERVED_WORDS)
	 	{
	 		if (r.equals(s)) return true; 
	 	};
	 	return false;
	 };
	 /** Tests if <code>s</code> allowed signal name
	 to be directly encoded in JSON.
	 @param s string to check.
	 @param is_described true if stream is described. Described streams do
	 	exclude <code>xxx_ELEMENT</code> from the allowed list while
	 	un-described streams do allow them.
	 @return true if on reserved words list, is begin, content, or for
	 	described, a type element.
	 */ 
	 public boolean isAllowedSignalName(String s, boolean is_described)
	 {
	 	if (isReservedWord(s) 
	 		||
	 		BEGIN.equals(s)
	 		||
	 		CONTENT.equals(s)
	 		) return false;
	 	if (!is_described) return true;
	 	if (
	 		BOOLEAN_ELEMENT.equals(s) ||
	 		BYTE_ELEMENT.equals(s) ||
	 		CHAR_ELEMENT.equals(s) ||
	 		SHORT_ELEMENT.equals(s) ||
	 		INT_ELEMENT.equals(s) ||
	 		LONG_ELEMENT.equals(s) ||
	 		FLOAT_ELEMENT.equals(s) ||
	 		DOUBLE_ELEMENT.equals(s) ||
	 		
	 		BOOLEAN_BLOCK_ELEMENT.equals(s) ||
	 		BYTE_BLOCK_ELEMENT.equals(s) ||
	 		CHAR_BLOCK_ELEMENT.equals(s) ||
	 		SHORT_BLOCK_ELEMENT.equals(s) ||
	 		INT_BLOCK_ELEMENT.equals(s) ||
	 		LONG_BLOCK_ELEMENT.equals(s) ||
	 		FLOAT_BLOCK_ELEMENT.equals(s) ||
	 		DOUBLE_BLOCK_ELEMENT.equals(s)
	 		) return false;
	 	return true;
	 };
	 
	 /** Returns type describing string <code>xxx_ELEMENT</code>
	 depending on indicator.
	 @param type_indicator type indicator with {@link TIndicator#TYPE}
	 flag set.
	 */
	 public String getTypeString(TIndicator type_indicator)
	 {
	 	switch(type_indicator)
	 	{
	 		case TYPE_BOOLEAN: return BOOLEAN_ELEMENT;
	 		case TYPE_BYTE: return BYTE_ELEMENT;
	 		case TYPE_CHAR: return CHAR_ELEMENT;
	 		case TYPE_SHORT: return SHORT_ELEMENT;
	 		case TYPE_INT: return INT_ELEMENT;
	 		case TYPE_LONG: return LONG_ELEMENT;
	 		case TYPE_FLOAT: return FLOAT_ELEMENT;
	 		case TYPE_DOUBLE: return DOUBLE_ELEMENT;
	 		
	 		case TYPE_BOOLEAN_BLOCK: return BOOLEAN_BLOCK_ELEMENT;
	 		case TYPE_BYTE_BLOCK: return BYTE_BLOCK_ELEMENT;
	 		case TYPE_CHAR_BLOCK: return CHAR_BLOCK_ELEMENT;
	 		case TYPE_SHORT_BLOCK: return SHORT_BLOCK_ELEMENT;
	 		case TYPE_INT_BLOCK: return INT_BLOCK_ELEMENT;
	 		case TYPE_LONG_BLOCK: return LONG_BLOCK_ELEMENT;
	 		case TYPE_FLOAT_BLOCK: return FLOAT_BLOCK_ELEMENT;
	 		case TYPE_DOUBLE_BLOCK: return DOUBLE_BLOCK_ELEMENT;
	 		
	 		default: throw new AssertionError();
	 	}
	 }
};