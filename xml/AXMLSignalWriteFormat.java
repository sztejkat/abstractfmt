package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import java.io.IOException;
import java.util.ArrayList;
/**
	A writer which is writing data to a stream using XML
	like format, base abstract class.
	<p>
	For used XML format definition see <a href="doc-files/xml-syntax.html">there</a>.
	<p>
	This base class is undescribed.
*/
public abstract class AXMLSignalWriteFormat extends ASignalWriteFormat
{
				/* ****************************************************
						XML elements					
				* ***************************************************/
				/** Character used to mark
				<a href="doc-files/xml-syntax.html#escaped">escape sequence</a>.*/				
				protected final char ESCAPE_CHAR;
				/** Character used to mark end of
				<a href="doc-files/xml-syntax.html#escaped">escape sequence</a>.*/
				protected final char ESCAPE_CHAR_END;
				/** A character used to separate primitive data in un-typed streams */
				protected final char PRIMITIVES_SEPARATOR_CHAR;
 				/** An XML element used to represent
 				a <a href="doc-files/xml-syntax.html#long_signal_form">a long form</a>
 				of a begin signal */
				protected final String LONG_SIGNAL_ELEMENT;
				/** An attribute of {@link #LONG_SIGNAL_ELEMENT} which will carry
				encoded signal name.*/
				protected final String LONG_SIGNAL_ELEMENT_ATTR;
				/* *******************************************************
						Helpers.
				* *******************************************************
				/** Bin nibble 2 hex conversion table */
				private static final char [] HEX= new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
			
				
				/* *********************************************************
						state tracking
				* *********************************************************/
			
				/** A stack which is tracking the signals written
				with {@link #begin}. Stack element is pushed
				when signal name is written and popped when
				{@link #end} signal is written.
				<p>
				If signal name is encoded in a short form 
				this stack carries the true signal name.
				If it is encoded in long form it is carrying
				the {@link #LONG_SIGNAL_ELEMENT}
				*/
				private final ArrayList<String> signal_stack;
				
		/* ********************************************************************
		
					Construction
		
		********************************************************************/
		/** Creates write format
		
		@param max_name_length see {@link ASignalWriteFormat#ASignalWriteFormat(int,int,int)}
		@param max_events_recursion_depth --//--
		@param ESCAPE_CHAR see {@link #ESCAPE_CHAR}
		@param ESCAPE_CHAR_END see {@link #ESCAPE_CHAR_END}
		@param PRIMITIVES_SEPARATOR_CHAR see {@link #PRIMITIVES_SEPARATOR_CHAR}
		@param LONG_SIGNAL_ELEMENT see {@link #LONG_SIGNAL_ELEMENT}, non null
		@param LONG_SIGNAL_ELEMENT_ATTR see {@link #LONG_SIGNAL_ELEMENT_ATTR}, non null
		*/
		protected AXMLSignalWriteFormat(
									 final int max_name_length,
									 final int max_events_recursion_depth,
									 final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
									 final char PRIMITIVES_SEPARATOR_CHAR,
									 final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR
									 )
		{
			super(0, max_name_length, max_events_recursion_depth);//no names registry!
			assert(LONG_SIGNAL_ELEMENT!=null);
			assert(LONG_SIGNAL_ELEMENT_ATTR!=null);
			this.ESCAPE_CHAR=ESCAPE_CHAR;
			this.ESCAPE_CHAR_END=ESCAPE_CHAR_END;
			this.LONG_SIGNAL_ELEMENT=LONG_SIGNAL_ELEMENT;
			this.LONG_SIGNAL_ELEMENT_ATTR=LONG_SIGNAL_ELEMENT_ATTR;
			this.PRIMITIVES_SEPARATOR_CHAR=PRIMITIVES_SEPARATOR_CHAR;
			this.signal_stack = new ArrayList<String>(max_events_recursion_depth);
		};
		/* ******************************************************************
		
				Services required from subclasses
				
		
		* ******************************************************************/
		/** Should check if specified signal_name can be used as an XML element name
		or do it clash with XML elements used by this writer.
		<p>
		Used to test if use short or long begin signal form.
		@param element a name of XML element to check, fitting XML rules, non null.
		@return true if it clashes and cannot be used in short form.
				Subclasses may enforce long form by always returning false from this 
				method. Enforcing of short form is NOT possible.
		*/
		protected abstract boolean isReservedElement(String signal_name);
		/** Should return an output to which we should write data.
		@return never null, life time constant.
		*/
		protected abstract Appendable getOutput();
		/* ******************************************************************
		
				Services tunable by subclasses
		
		* ******************************************************************/
		/* ------------------------------------------------------------------
				Escaping
		--------------------------------------------------------------------*/
		/** Unconditionally escapes <code>character</code>
		with either <code>{@link ESCAPE_CHAR} {@link ESCAPE_CHAR} {@link ESCAPE_CHAR_END}</code>
		or <code>{@link ESCAPE_CHAR} <i>1 to 4 hex upper case digits</i> {@link ESCAPE_CHAR_END}</code>
		@param character what to escape
		@param a where to put escaped data
		@throws IOException if Appendable failed.
		*/
		protected void escape(Appendable a, char character)throws IOException
		{
			a.append(ESCAPE_CHAR);
			if (character==ESCAPE_CHAR)
			{
				a.append(ESCAPE_CHAR);
			}else
			{
				char d0 = HEX[character & 0x0F]; character>>>=4;
				char d1 = HEX[character & 0x0F]; character>>>=4;
				char d2 = HEX[character & 0x0F]; character>>>=4;
				char d3 = HEX[character]; 
				if (d3!='0'){ a.append(d3); a.append(d2); a.append(d1); }
				else
				if (d2!='0'){ a.append(d2); a.append(d1); }
				else
				if (d1!='0'){ a.append(d1); }
				a.append(d0);
			};
			a.append(ESCAPE_CHAR_END);
		};
		/** Tests if specified character is a valid character which can be put into
		the XML stream inside a text representing <code>char[]</code> block.
		<p>
		Default implementation tests againts rules 
		specified in <a href="doc-files/xml-syntax.html#escape_in_char">xml syntax definition</a>.
		<p>
		Subclasses should also return false if character cannot be encoded with a low
		level stream char-set (ie, ISO-xxx 8 bit code page).
		@param c character
		@return true if character does not need encoding,
				false if it needs to be passed through {@link #escape}
		*/
		protected boolean isValidTextChar(char c)
		{
			//first check if it is an escape char
			if (c==ESCAPE_CHAR) return false;
			if (
					((c>=0x20)&&(c<=0xD7FF))
						||
					((c>=0xE000)&&(c<=0xFFFD))
				)
			{
					//this is allowed XML set, but now we need to remove non-recommended.
					if (
							((c>=0x7F)&&(c<=0x84))
								||
							((c>=0x86)&&(c<=0x9F))
								||
							((c>=0xFDD0)&&(c<=0xFDDF))
								||
							((c=='>')||(c=='<')||(c=='&')) //xml elements and entities starts
								||
							(Character.isWhitespace(c))
						)	return false;
					return true;
			}else
				return false;
		};
		/** Tests if specified character is a valid character which can be put into
		the XML stream inside an attribute value
		<p>
		Default implementation returns true if {@link #isValidTextChar} and not "'
		@param c character to validate
		@return  true if character does not need encoding,
				false if it needs to be passed through {@link #escape}
		*/
		protected boolean isValidAttributeChar(char c)
		{
			if (!isValidTextChar(c)) return false;
			return !((c=='\'')||(c=='\"'));
		};
		/** Tests if specified signal name (as passed to {@link #begin} )
		can be used directly as XML element name
		<a href="doc-files/xml-syntax.html#short_signal_form">in a short form</a>
		or if it must be encoded using 
		<a href="doc-files/xml-syntax.html#long_signal_form">in a long form</a>.
		<p>
		Default implementation tests if XML rules are met and asks {@link #isReservedXMLElement}
		if there is no name clash.
		@param signal_name name to check, non null.
		@return true if can use short form
		@see #isReservedXMLElement
		*/
		protected boolean isPossibleXMLElement(String signal_name)
		{
			assert(signal_name!=null);
			final int L = signal_name.length();
			if (L==0) return false; //empty element name is not allowed
			//first char must be a letter.
			char c = signal_name.charAt(0);
			if (!Character.isLetter(c)) return false;
			//all characters must be letters or digits.
			for(int i = L; --i>=1; )
			{
				c = signal_name.charAt(i);
				if (!(Character.isLetter(c) || Character.isDigit(c) || (c=='_') || (c=='.'))) return false;
			};
			//Now protect default xml
			if (L>=3)
			{
				if ( 
					(Character.toUpperCase(signal_name.charAt(0))=='X')
					 &&
				    (Character.toUpperCase(signal_name.charAt(1))=='M')
				    &&
				    (Character.toUpperCase(signal_name.charAt(2))=='L')
				    ) return false;
			};
			return !isReservedElement(signal_name);
		};
		
		/* ******************************************************************
		
				Services required by superclass.
				
		
		* ******************************************************************/
		/* ----------------------------------------------------------------
				signals & names in signals.
		----------------------------------------------------------------*/
		/** Writes "&lt" */
		@Override protected void writeBeginSignalIndicator()throws IOException
		{
			getOutput().append('<');
		};
		/** Writes ending tag */
		@Override protected void writeEndSignalIndicator()throws IOException
		{
			final Appendable a =getOutput(); 
			assert(!signal_stack.isEmpty()):"superclass contract broken";
			a.append("</");
			a.append(signal_stack.remove(signal_stack.size()-1));
			a.append('>');
		};
		/** Empty. Only direct names are supported, no need for indicator. */
		@Override protected void writeDirectName()throws IOException{};
		
		/** Uses {@link #isValidAttributeChar} to determine if each character
		of <code>text</code> needs to be escaped or not. Escapes it with {@link #escape}.
		@param Appendable a where to put resutlt;
		@param text what to put, not null
		@throws IOException if Appendable failed.
		*/
		private void escapeAttribute(Appendable a, String text)throws IOException
		{
			assert(text!=null);
			final int L = text.length();
			for(int i=0;i<L;i++)
			{
				char c= text.charAt(i);
				if (isValidAttributeChar(c))
					a.append(c);
				else
					escape(a,c);
			};
		};
		
		/** Decieds on signal long/short form and encodes name.
		Puts it on signal stack. */
		@Override protected void writeSignalNameData(String name)throws IOException
		{
			final Appendable a =getOutput();
			if (isPossibleXMLElement(name))
			{
					//short form
					signal_stack.add(name); //Note: overflow is controlled by superclass.
					a.append(name); 
					a.append('>');
			}else
			{
					//long form.
					signal_stack.add(LONG_SIGNAL_ELEMENT);
					a.append(LONG_SIGNAL_ELEMENT);
					a.append(' ');
					a.append(LONG_SIGNAL_ELEMENT_ATTR);
					a.append("=\"");
					escapeAttribute(a,name);
					a.append("\">");
			};
		};
		/** Always throws
		@throws UnsupportedOperationException always
		*/
		@Override protected void writeRegisterName(int name_index)throws IOException
		{
			throw new UnsupportedOperationException("XML writer does not support signal names registry.");
		};
		/** Always throws
		@throws UnsupportedOperationException always
		*/
		@Override protected void writeRegisterUse(int name_index)throws IOException
		{
			throw new UnsupportedOperationException("XML writer does not support signal names registry.");
		};
		/* -------------------------------------------------------
				Types of primitives for described streams	
				
			Note: This is undescribed stream, so no type information is
			written. The end-type writes are used to write ;
			but block ends are not written in this way.
		---------------------------------------------------------*/
		/** Called by all <code>writeXXXTypeEnd</code>
		to write {@link #PRIMITIVES_SEPARATOR_CHAR} */
		protected void writeXXXTypeEnd()throws IOException
		{
			getOutput().append(PRIMITIVES_SEPARATOR_CHAR);
		};
		/** Calls {@link #writeXXXTypeEnd} */
		@Override protected  void writeBooleanTypeEnd()throws IOException{ writeXXXTypeEnd(); };
		/** Calls {@link #writeXXXTypeEnd} */
		@Override protected  void writeByteTypeEnd()throws IOException{ writeXXXTypeEnd(); };
		/** Calls {@link #writeXXXTypeEnd} */
		@Override protected  void writeCharTypeEnd()throws IOException{ writeXXXTypeEnd(); };
		/** Calls {@link #writeXXXTypeEnd} */
		@Override protected  void writeShortTypeEnd()throws IOException{ writeXXXTypeEnd(); };
		/** Calls {@link #writeXXXTypeEnd} */
		@Override protected  void writeIntTypeEnd()throws IOException{ writeXXXTypeEnd(); };
		/** Calls {@link #writeXXXTypeEnd} */
		@Override protected  void writeLongTypeEnd()throws IOException{ writeXXXTypeEnd(); };
		/** Calls {@link #writeXXXTypeEnd} */
		@Override protected  void writeDoubleTypeEnd()throws IOException{ writeXXXTypeEnd(); };
		/** Calls {@link #writeXXXTypeEnd} */
		@Override protected  void writeFloatTypeEnd()throws IOException{ writeXXXTypeEnd(); };
		
		
		/*========================================================
		
				primitive writes				
		
		=========================================================*/
		
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_boolean">specification</a>
		*/
		@Override protected void writeBooleanImpl(boolean v)throws IOException
		{
			getOutput().append(
						v ? 't' : 'f'
								);
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_numeric">specification</a>
		*/
		@Override protected void writeByteImpl(byte v)throws IOException
		{
			getOutput().append(Byte.toString(v));
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_char">specification</a>
		*/
		@Override protected void writeCharImpl(char v)throws IOException
		{
			if ((v==';')||(!isValidTextChar(v)))
				escape(getOutput(),v);
			else
				getOutput().append(v);
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_numeric">specification</a>
		*/
		@Override protected void writeShortImpl(short v)throws IOException
		{
			getOutput().append(Short.toString(v));
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_numeric">specification</a>
		*/
		@Override protected void writeIntImpl(int v)throws IOException
		{
			getOutput().append(Integer.toString(v));
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_numeric">specification</a>
		*/
		@Override protected void writeLongImpl(long v)throws IOException
		{
			getOutput().append(Long.toString(v));
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_numeric">specification</a>
		*/
		@Override protected void writeFloatImpl(float v)throws IOException
		{
			getOutput().append(Float.toString(v));
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_numeric">specification</a>
		*/
		@Override protected void writeDoubleImpl(double v)throws IOException
		{
			getOutput().append(Double.toString(v));
		};
		/* -------------------------------------------------------
				Block primitives
		-------------------------------------------------------*/
		
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_boolean_blk">specification</a>
		*/
		@Override protected void writeBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException
		{
			Appendable a = getOutput();
			while(length-->0)
			{
				a.append(buffer[offset++] ? 't' : 'f');
			};
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_byte_blk">specification</a>
		*/
		@Override protected void writeByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
		{
			Appendable a = getOutput();
			while(length-->0)
			{
				//Note: Theoretically >>> is an unsigned shift.
				//		So byte 0xFE >>> 4 should result in 0x0E.
				//		this however does NOT take place
				//		because in equation
				//			v>>>=4 we actually do have an instrict promition to ints:
				//			v = (byte)(((int)v)>>>4)
				//		what makes:
				//				0xFE  -> 0xFFFF_FFFE
				//				>>>4  -> 0x0FFF_FFFF
				//				(byte)-> 0xFF
				//		The automatic java promotion made here is tricky
				//		so it is better to cast it to int and do 0x0000_00FE from it.
				int v = buffer[offset++] & 0xFF;
				char d0 = HEX[v & 0x0F] ;v>>>=4;	
				char d1 = HEX[v];
				a.append(d1);a.append(d0);
			};
		};
		@Override protected void writeByteBlockImpl(byte v)throws IOException
		{
			Appendable a = getOutput();
			int vv = v & 0xFF;
			char d0 = HEX[vv & 0x0F]; vv>>>=4;
			char d1 = HEX[vv];
			a.append(d1);a.append(d0);
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_char_blk">specification</a>
		*/
		@Override protected void writeCharBlockImpl(char [] buffer, int offset, int length)throws IOException
		{
			Appendable a = getOutput();
			while(length-->0)
			{
				char c = buffer[offset++];
				if (isValidTextChar(c))
					a.append(c);
				else
					escape(a,c);
			};
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#enc_char_blk">specification</a>
		*/
		@Override protected void writeCharBlockImpl(CharSequence characters, int offset, int length)throws IOException
		{
			Appendable a = getOutput();
			while(length-->0)
			{
				char c = characters.charAt(offset++);
				if (isValidTextChar(c))
					a.append(c);
				else
					escape(a,c);
			};
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#end_numeric_blk">specification</a>
		*/
		@Override protected void writeShortBlockImpl(short [] buffer,int offset, int length)throws IOException
		{
			Appendable a = getOutput();
			while(length-->0)
			{
				short v = buffer[offset++];
				a.append(Short.toString(v));
				a.append(PRIMITIVES_SEPARATOR_CHAR);
			};
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#end_numeric_blk">specification</a>
		*/
		@Override protected void writeIntBlockImpl(int [] buffer,int offset, int length)throws IOException
		{
			Appendable a = getOutput();
			while(length-->0)
			{
				int v = buffer[offset++];
				a.append(Integer.toString(v));
				a.append(PRIMITIVES_SEPARATOR_CHAR);
			};
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#end_numeric_blk">specification</a>
		*/
		@Override protected void writeLongBlockImpl(long [] buffer,int offset, int length)throws IOException
		{
			Appendable a = getOutput();
			while(length-->0)
			{
				long v = buffer[offset++];
				a.append(Long.toString(v));
				a.append(PRIMITIVES_SEPARATOR_CHAR);
			};
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#end_numeric_blk">specification</a>
		*/
		@Override protected void writeFloatBlockImpl(float [] buffer,int offset, int length)throws IOException
		{
			Appendable a = getOutput();
			while(length-->0)
			{
				float v = buffer[offset++];
				a.append(Float.toString(v));
				a.append(PRIMITIVES_SEPARATOR_CHAR);
			};
		};
		/** Encodes as described
		in <a href="doc-files/xml-syntax.html#end_numeric_blk">specification</a>
		*/
		@Override protected void writeDoubleBlockImpl(double [] buffer,int offset, int length)throws IOException
		{
			Appendable a = getOutput();
			while(length-->0)
			{
				double v = buffer[offset++];
				a.append(Double.toString(v));
				a.append(PRIMITIVES_SEPARATOR_CHAR);
			};
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* *****************************************************************************
		
		
				Junit test area (junit 4 style)
		
				
				This test is intendend to test private routines.
		
		* *****************************************************************************/	
		/** Routine for internal tests */
		public static final class Test extends sztejkat.utils.test.ATest
		{
				/** Test bed device, throws on almost all methods */
				private static final class DUT extends AXMLSignalWriteFormat
				{
					DUT()
					{
						super(16,32, // int max_name_length, int max_events_recursion_depth,
							  '%',';',//char ESCAPE_CHAR, char ESCAPE_CHAR_END,
							   ';',//final char PRIMITIVES_SEPARATOR_CHAR,
							   "e","n"//final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR
							);
					};
					protected Appendable getOutput(){ throw new AbstractMethodError(); }
					protected void closeImpl()throws IOException{ throw new AbstractMethodError(); }
					protected boolean isReservedElement(String signal_name)
					{
						return "root".equals(signal_name);
					};
		
				};
				private void testEscaping(char c, String exp)throws IOException
				{
					enter();
					/*
						We test unconditional escaping
					*/					
					StringBuilder sb = new StringBuilder();
					DUT d = new DUT();
					d.escape(sb, c);
					String r = sb.toString();
					System.out.println(Integer.toHexString(c)+"=> \""+r+"\"");
					org.junit.Assert.assertTrue(exp.equals(r));
					leave();
				};
				@org.junit.Test public void testEscaping_1()throws IOException
				{
					enter();testEscaping('%',"%%;");leave();
				};
				@org.junit.Test public void testEscaping_2()throws IOException
				{
					enter();testEscaping((char)0x3FC1,"%3FC1;");leave();
				};
				@org.junit.Test public void testEscaping_3()throws IOException
				{
					enter();testEscaping((char)0x0FC1,"%FC1;");leave();
				};
				@org.junit.Test public void testEscaping_4()throws IOException
				{
					enter();testEscaping((char)0x0A5,"%A5;");leave();
				};
				@org.junit.Test public void testEscaping_5()throws IOException
				{
					enter();testEscaping((char)0x1,"%1;");leave();
				};
				@org.junit.Test public void testEscaping_6()throws IOException
				{
					enter();testEscaping((char)0x0,"%0;");leave();
				};
				
				@org.junit.Test public void testPossibleElements()
				{	
					enter();
					/*
						Test known non-xml names.
					*/
					DUT d = new DUT();
					org.junit.Assert.assertTrue(d.isPossibleXMLElement("el234eemnt_.do"));
					org.junit.Assert.assertTrue(!d.isPossibleXMLElement("1eleemnt_.do"));
					org.junit.Assert.assertTrue(!d.isPossibleXMLElement("e%000;x"));
					org.junit.Assert.assertTrue(!d.isPossibleXMLElement("e;x"));
					org.junit.Assert.assertTrue(!d.isPossibleXMLElement("d\"ddkio"));
					org.junit.Assert.assertTrue(!d.isPossibleXMLElement("d\'ddkio"));
					org.junit.Assert.assertTrue(!d.isPossibleXMLElement("d   ddkio"));
					org.junit.Assert.assertTrue(!d.isPossibleXMLElement("dd\rdkio"));
					org.junit.Assert.assertTrue(!d.isPossibleXMLElement("dd\tdkio"));
					org.junit.Assert.assertTrue(!d.isPossibleXMLElement("dd\ndkio"));
					leave();
				};
				
				@org.junit.Test public void testValidTextChar_1()
				{	
					enter();
					/*
						Test known valid text chars.
					*/
					DUT d = new DUT();
					{
						final String x="0123456789qwertyuiopasdfghjklzxcvbnm"+
									   "QWERTYYUIOPASDFGHJKLZXCVBNM~!@#$^*()_+-=[];"+
									   "\'\""+
									   ",./?"+
									   "ąęłśćóż";
						for(int i=0;i<x.length();i++)
						{
							System.out.print(x.charAt(i));
							org.junit.Assert.assertTrue(" should be valid:"+x.charAt(i),d.isValidTextChar(x.charAt(i)));
						};
					};
					
					leave();
				};
				@org.junit.Test public void isValidAttributeChar_1()
				{	
					enter();
					/*
						Test known valid text chars.
					*/
					DUT d = new DUT();
					{
						final String x="0123456789qwertyuiopasdfghjklzxcvbnm"+
									   "QWERTYYUIOPASDFGHJKLZXCVBNM~!@#$^*()_+-=[];"+
									   ",./?"+
									   "ąęłśćóż";
						for(int i=0;i<x.length();i++)
						{
							System.out.print(x.charAt(i));
							org.junit.Assert.assertTrue(" should be valid:"+x.charAt(i),d.isValidAttributeChar(x.charAt(i)));
						};
					};
					
					leave();
				};
				
				@org.junit.Test public void testValidTextChar_2()
				{	
					enter();
					/*
						Test known invalid text chars.
					*/
					DUT d = new DUT();
					{
						final String x=" \t\n\r\b\u0000%<>&";
						for(int i=0;i<x.length();i++)
						{
							System.out.print(x.charAt(i));
							org.junit.Assert.assertTrue(" should be invalid:"+x.charAt(i),!d.isValidTextChar(x.charAt(i)));
						};
					};
					
					leave();
				}
				@org.junit.Test public void isValidAttributeChar_2()
				{	
					enter();
					/*
						Test known invalid text chars.
					*/
					DUT d = new DUT();
					{
						final String x=" \t\n\r\b\u0000%<>&\"\'";
						for(int i=0;i<x.length();i++)
						{
							System.out.print(x.charAt(i));
							org.junit.Assert.assertTrue(" should be invalid:"+x.charAt(i),!d.isValidAttributeChar(x.charAt(i)));
						};
					};
					
					leave();
				};
		};
};