package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import java.io.IOException;

/**
	A fully described XML format.
	<p>
	For used XML format definition see <a href="doc-files/xml-syntax.html">there</a>.
	<p>
	This class does NOT write XML root element.
*/
public abstract class ADescXMLSignalReadFormat extends AXMLSignalReadFormat
{
				/** A tag used to enclose elementary primitive operation of that type */
				protected final String TYPE_BOOLEAN_TAG;
				/** A tag used to enclose elementary primitive operation of that type */
				protected final String TYPE_BYTE_TAG;
				/** A tag used to enclose elementary primitive operation of that type */
				protected final String TYPE_CHAR_TAG;
				/** A tag used to enclose elementary primitive operation of that type */
				protected final String TYPE_SHORT_TAG;
				/** A tag used to enclose elementary primitive operation of that type */
				protected final String TYPE_INT_TAG;
				/** A tag used to enclose elementary primitive operation of that type */
				protected final String TYPE_LONG_TAG;
				/** A tag used to enclose elementary primitive operation of that type */
				protected final String TYPE_FLOAT_TAG;
				/** A tag used to enclose elementary primitive operation of that type */
				protected final String TYPE_DOUBLE_TAG;
				
				/** A tag used to enclose block primitive operation of that type */
				protected final String TYPE_BOOLEAN_BLOCK_TAG;
				/** A tag used to enclose block primitive operation of that type */
				protected final String TYPE_BYTE_BLOCK_TAG;
				/** A tag used to enclose block primitive operation of that type */
				protected final String TYPE_CHAR_BLOCK_TAG;
				/** A tag used to enclose block primitive operation of that type */
				protected final String TYPE_SHORT_BLOCK_TAG;
				/** A tag used to enclose block primitive operation of that type */
				protected final String TYPE_INT_BLOCK_TAG;
				/** A tag used to enclose block primitive operation of that type */
				protected final String TYPE_LONG_BLOCK_TAG;
				/** A tag used to enclose block primitive operation of that type */
				protected final String TYPE_FLOAT_BLOCK_TAG;
				/** A tag used to enclose block primitive operation of that type */
				protected final String TYPE_DOUBLE_BLOCK_TAG;
		
		/** Creates 
		@param max_name_length as in {@link AXMLSignalWriteFormat#AXMLSignalWriteFormat}
		@param max_events_recursion_depth --//--
		@param ESCAPE_CHAR --//-- 
		@param ESCAPE_CHAR_END --//-- 
		@param PRIMITIVES_SEPARATOR_CHAR --//--
		@param LONG_SIGNAL_ELEMENT --//--
		@param LONG_SIGNAL_ELEMENT_ATTR --//--
		@param max_type_tag_length --//--
		@param max_inter_signal_chars --//--
		@param TYPE_BOOLEAN_TAG a type tag used to surround elementary primitive
		@param TYPE_BYTE_TAG --//--
		@param TYPE_CHAR_TAG --//--
		@param TYPE_SHORT_TAG --//--
		@param TYPE_INT_TAG --//--
		@param TYPE_LONG_TAG --//--
		@param TYPE_FLOAT_TAG --//--
		@param TYPE_DOUBLE_TAG --//--
		@param TYPE_BOOLEAN_BLOCK_TAG  a type tag used to surround block primitive
		@param TYPE_BYTE_BLOCK_TAG --//--
		@param TYPE_CHAR_BLOCK_TAG --//--
		@param TYPE_SHORT_BLOCK_TAG --//--
		@param TYPE_INT_BLOCK_TAG --//--
		@param TYPE_LONG_BLOCK_TAG --//--
		@param TYPE_FLOAT_BLOCK_TAG --//--
		@param TYPE_DOUBLE_BLOCK_TAG  --//--
		*/
		protected ADescXMLSignalReadFormat(
									final int max_name_length,
									final int max_events_recursion_depth,
									final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
									final char PRIMITIVES_SEPARATOR_CHAR,									
									final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR,
									final int max_type_tag_length,
									final int max_inter_signal_chars,
									final String TYPE_BOOLEAN_TAG,
									final String TYPE_BYTE_TAG,
									final String TYPE_CHAR_TAG,
									final String TYPE_SHORT_TAG,
									final String TYPE_INT_TAG,
									final String TYPE_LONG_TAG,
									final String TYPE_FLOAT_TAG,
									final String TYPE_DOUBLE_TAG,
									final String TYPE_BOOLEAN_BLOCK_TAG,
									final String TYPE_BYTE_BLOCK_TAG,
									final String TYPE_CHAR_BLOCK_TAG,
									final String TYPE_SHORT_BLOCK_TAG,
									final String TYPE_INT_BLOCK_TAG,
									final String TYPE_LONG_BLOCK_TAG,
									final String TYPE_FLOAT_BLOCK_TAG,
									final String TYPE_DOUBLE_BLOCK_TAG
									)
		{
			super( 	max_name_length,
					max_events_recursion_depth,
				 	ESCAPE_CHAR,  ESCAPE_CHAR_END, 
				 	PRIMITIVES_SEPARATOR_CHAR,
				 	LONG_SIGNAL_ELEMENT, LONG_SIGNAL_ELEMENT_ATTR,max_type_tag_length,max_inter_signal_chars);
				 	
			assert(TYPE_BOOLEAN_TAG!=null);
			this.TYPE_BOOLEAN_TAG=TYPE_BOOLEAN_TAG;
			assert(TYPE_BYTE_TAG!=null);
			this.TYPE_BYTE_TAG=TYPE_BYTE_TAG;
			assert(TYPE_CHAR_TAG!=null);
			this.TYPE_CHAR_TAG=TYPE_CHAR_TAG;
			assert(TYPE_SHORT_TAG!=null);
			this.TYPE_SHORT_TAG=TYPE_SHORT_TAG;
			assert(TYPE_INT_TAG!=null);
			this.TYPE_INT_TAG=TYPE_INT_TAG;
			assert(TYPE_LONG_TAG!=null);
			this.TYPE_LONG_TAG=TYPE_LONG_TAG;
			assert(TYPE_FLOAT_TAG!=null);
			this.TYPE_FLOAT_TAG=TYPE_FLOAT_TAG;
			assert(TYPE_DOUBLE_TAG!=null);
			this.TYPE_DOUBLE_TAG=TYPE_DOUBLE_TAG;
			assert(TYPE_BOOLEAN_BLOCK_TAG!=null);
			this.TYPE_BOOLEAN_BLOCK_TAG=TYPE_BOOLEAN_BLOCK_TAG;
			assert(TYPE_BYTE_BLOCK_TAG!=null);
			this.TYPE_BYTE_BLOCK_TAG=TYPE_BYTE_BLOCK_TAG;
			assert(TYPE_CHAR_BLOCK_TAG!=null);
			this.TYPE_CHAR_BLOCK_TAG=TYPE_CHAR_BLOCK_TAG;
			assert(TYPE_SHORT_BLOCK_TAG!=null);
			this.TYPE_SHORT_BLOCK_TAG=TYPE_SHORT_BLOCK_TAG;
			assert(TYPE_INT_BLOCK_TAG!=null);
			this.TYPE_INT_BLOCK_TAG=TYPE_INT_BLOCK_TAG;
			assert(TYPE_LONG_BLOCK_TAG!=null);
			this.TYPE_LONG_BLOCK_TAG=TYPE_LONG_BLOCK_TAG;
			assert(TYPE_FLOAT_BLOCK_TAG!=null);
			this.TYPE_FLOAT_BLOCK_TAG=TYPE_FLOAT_BLOCK_TAG;
			assert(TYPE_DOUBLE_BLOCK_TAG!=null);
			this.TYPE_DOUBLE_BLOCK_TAG=TYPE_DOUBLE_BLOCK_TAG;
		};
		/* ********************************************************************
		
				Services which needs to be tuned in AXMLSignalReadFormat
		
		*********************************************************************/
		@Override protected int processOpeningTag(String tag)throws IOException
		{
			if (TYPE_BOOLEAN_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_BOOLEAN;
			if (TYPE_BYTE_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_BYTE;
			if (TYPE_CHAR_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_CHAR;
			if (TYPE_SHORT_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_SHORT;
			if (TYPE_INT_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_INT;
			if (TYPE_LONG_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_LONG;
			if (TYPE_FLOAT_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_FLOAT;
			if (TYPE_DOUBLE_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_DOUBLE;
			
			if (TYPE_BOOLEAN_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_BOOLEAN_BLOCK;
			if (TYPE_BYTE_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_BYTE_BLOCK;
			if (TYPE_CHAR_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_CHAR_BLOCK;
			if (TYPE_SHORT_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_SHORT_BLOCK;
			if (TYPE_INT_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_INT_BLOCK;
			if (TYPE_LONG_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_LONG_BLOCK;
			if (TYPE_FLOAT_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_FLOAT_BLOCK;
			if (TYPE_DOUBLE_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.TYPE_DOUBLE_BLOCK;
			
			return super.processOpeningTag(tag);
		};
		
		@Override protected int processClosingTag(String tag)throws IOException
		{
			if (TYPE_BOOLEAN_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_BOOLEAN;
			if (TYPE_BYTE_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_BYTE;
			if (TYPE_CHAR_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_CHAR;
			if (TYPE_SHORT_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_SHORT;
			if (TYPE_INT_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_INT;
			if (TYPE_LONG_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_LONG;
			if (TYPE_FLOAT_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_FLOAT;
			if (TYPE_DOUBLE_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_DOUBLE;
			
			if (TYPE_BOOLEAN_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_BOOLEAN_BLOCK;
			if (TYPE_BYTE_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_BYTE_BLOCK;
			if (TYPE_CHAR_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_CHAR_BLOCK;
			if (TYPE_SHORT_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_SHORT_BLOCK;
			if (TYPE_INT_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_INT_BLOCK;
			if (TYPE_LONG_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_LONG_BLOCK;
			if (TYPE_FLOAT_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_FLOAT_BLOCK;
			if (TYPE_DOUBLE_BLOCK_TAG.equals(tag)) return AXMLSignalReadFormat.FLUSH_DOUBLE_BLOCK;
			
			return super.processClosingTag(tag);
		};
		/* ********************************************************************
		
				Services which needs to be tuned in ASignalReadFormat
		
		*********************************************************************/
		@Override public final boolean isDescribed(){ return true; };	
};