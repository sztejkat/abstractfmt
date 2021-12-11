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
public abstract class ADescXMLSignalWriteFormat extends AXMLSignalWriteFormat
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
		protected ADescXMLSignalWriteFormat(
									final int max_name_length,
									final int max_events_recursion_depth,
									final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
									final char PRIMITIVES_SEPARATOR_CHAR,
									final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR,
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
				 	LONG_SIGNAL_ELEMENT, LONG_SIGNAL_ELEMENT_ATTR);
				 	
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
		
				Services which needs to be tuned in AXMLSignalWriteFormat
		
		*********************************************************************/		
		@Override protected boolean isReservedElement(String signal_name)
		{
			return 	(TYPE_BOOLEAN_TAG.equals(signal_name))||
					(TYPE_BYTE_TAG.equals(signal_name))||
					(TYPE_CHAR_TAG.equals(signal_name))||
					(TYPE_SHORT_TAG.equals(signal_name))||
					(TYPE_INT_TAG.equals(signal_name))||
					(TYPE_LONG_TAG.equals(signal_name))||
					(TYPE_FLOAT_TAG.equals(signal_name))||
					(TYPE_DOUBLE_TAG.equals(signal_name))||
					
					(TYPE_BOOLEAN_BLOCK_TAG.equals(signal_name))||
					(TYPE_BYTE_BLOCK_TAG.equals(signal_name))||
					(TYPE_CHAR_BLOCK_TAG.equals(signal_name))||
					(TYPE_SHORT_BLOCK_TAG.equals(signal_name))||
					(TYPE_INT_BLOCK_TAG.equals(signal_name))||
					(TYPE_LONG_BLOCK_TAG.equals(signal_name))||
					(TYPE_FLOAT_BLOCK_TAG.equals(signal_name))||
					(TYPE_DOUBLE_BLOCK_TAG.equals(signal_name));
		};
		/* ********************************************************************
		
				Services which needs to be tuned in ASignalWriteFormat
				
				Note: This class is intentionally skipping writing 
				PRIMITIVES_SEPARATOR_CHAR during flush.
		
		*********************************************************************/	
		@Override public final boolean isDescribed(){ return true; };
		@Override protected void writeBooleanType()throws IOException{ write('<');write(TYPE_BOOLEAN_TAG);write('>');};
		@Override protected void flushBoolean()throws IOException{ write("</");write(TYPE_BOOLEAN_TAG);write('>');};
		
		@Override protected void writeByteType()throws IOException{ write('<');write(TYPE_BYTE_TAG);write('>');};
		@Override protected void flushByte()throws IOException{ write("</");write(TYPE_BYTE_TAG);write('>');};
	
		@Override protected void writeCharType()throws IOException{ write('<');write(TYPE_CHAR_TAG);write('>');};
		@Override protected void flushChar()throws IOException{ write("</");write(TYPE_CHAR_TAG);write('>');};
			
		@Override protected void writeShortType()throws IOException{ write('<');write(TYPE_SHORT_TAG);write('>');};
		@Override protected void flushShort()throws IOException{ write("</");write(TYPE_SHORT_TAG);write('>');};
			
		@Override protected void writeIntType()throws IOException{ write('<');write(TYPE_INT_TAG);write('>');};
		@Override protected void flushInt()throws IOException{ write("</");write(TYPE_INT_TAG);write('>');};
	
		@Override protected void writeLongType()throws IOException{ write('<');write(TYPE_LONG_TAG);write('>');};
		@Override protected void flushLong()throws IOException{ write("</");write(TYPE_LONG_TAG);write('>');};
			
		@Override protected void writeFloatType()throws IOException{ write('<');write(TYPE_FLOAT_TAG);write('>');};
		@Override protected void flushFloat()throws IOException{ write("</");write(TYPE_FLOAT_TAG);write('>');};
				 
		@Override protected void writeDoubleType()throws IOException{ write('<');write(TYPE_DOUBLE_TAG);write('>');};
		@Override protected void flushDouble()throws IOException{ write("</");write(TYPE_DOUBLE_TAG);write('>');};
		
		
		@Override protected void writeBooleanBlockType()throws IOException{ write('<');write(TYPE_BOOLEAN_BLOCK_TAG);write('>');};
		@Override protected void flushBooleanBlock()throws IOException{ write("</");write(TYPE_BOOLEAN_BLOCK_TAG);write('>');};
		
		@Override protected void writeByteBlockType()throws IOException{ write('<');write(TYPE_BYTE_BLOCK_TAG);write('>');};
		@Override protected void flushByteBlock()throws IOException{ write("</");write(TYPE_BYTE_BLOCK_TAG);write('>');};
	
		@Override protected void writeCharBlockType()throws IOException{ write('<');write(TYPE_CHAR_BLOCK_TAG);write('>');};
		@Override protected void flushCharBlock()throws IOException{ write("</");write(TYPE_CHAR_BLOCK_TAG);write('>');};
			
		@Override protected void writeShortBlockType()throws IOException{ write('<');write(TYPE_SHORT_BLOCK_TAG);write('>');};
		@Override protected void flushShortBlock()throws IOException{ write("</");write(TYPE_SHORT_BLOCK_TAG);write('>');};
			
		@Override protected void writeIntBlockType()throws IOException{ write('<');write(TYPE_INT_BLOCK_TAG);write('>');};
		@Override protected void flushIntBlock()throws IOException{ write("</");write(TYPE_INT_BLOCK_TAG);write('>');};
	
		@Override protected void writeLongBlockType()throws IOException{ write('<');write(TYPE_LONG_BLOCK_TAG);write('>');};
		@Override protected void flushLongBlock()throws IOException{ write("</");write(TYPE_LONG_BLOCK_TAG);write('>');};
			
		@Override protected void writeFloatBlockType()throws IOException{ write('<');write(TYPE_FLOAT_BLOCK_TAG);write('>');};
		@Override protected void flushFloatBlock()throws IOException{ write("</");write(TYPE_FLOAT_BLOCK_TAG);write('>');};
				 
		@Override protected void writeDoubleBlockType()throws IOException{ write('<');write(TYPE_DOUBLE_BLOCK_TAG);write('>');};
		@Override protected void flushDoubleBlock()throws IOException{ write("</");write(TYPE_DOUBLE_BLOCK_TAG);write('>');};
		
};