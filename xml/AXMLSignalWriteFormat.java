package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import java.io.IOException;
/**
	A writer which is writing data to a stream using XML
	like format, base abstract class.
	<p>
	For used XML format definition see <a href="doc-files/xml-syntax.html">there</a>.
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
				
				/** Bin nibble 2 hex conversion table */
				private static final char [] HEX= new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		/* ********************************************************************
		
					Construction
		
		********************************************************************/
		/** Creates write format
		
		@param max_name_length see {@link ASignalWriteFormat#ASignalWriteFormat(int,int,int)}
		@param max_events_recursion_depth --//--
		@param ESCAPE_CHAR see {@link #ESCAPE_CHAR}
		@param ESCAPE_CHAR_END see {@link #ESCAPE_CHAR_END}
		*/
		protected AXMLSignalWriteFormat(
									 int max_name_length,
									 int max_events_recursion_depth,
									 char ESCAPE_CHAR, char ESCAPE_CHAR_END
									 )
		{
			super(0, max_name_length, max_events_recursion_depth);//no names registry!
			this.ESCAPE_CHAR=ESCAPE_CHAR;
			this.ESCAPE_CHAR_END=ESCAPE_CHAR_END;
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
							(Character.isWhiteSpace(c))
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
			if (isValidTextChar(c)) return true;
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
			for(int i = L; --i>=1; 0)
			{
				char c = signal_name.charAt(i);
				if (!(Character.isLetter(c) || Character.isDigit(c) || (c=='_') || (c=='.'))) return false;
			};
			return !isReservedElement(signal_name);
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
							  '%',';'//char ESCAPE_CHAR, char ESCAPE_CHAR_END
							);
					};
					protected void closeImpl()throws IOException{ throw new AbstractMethodError(); }
					protected void writeBeginSignalIndicator()throws IOException{ throw new AbstractMethodError(); }
					protected void writeEndSignalIndicator()throws IOException{ throw new AbstractMethodError(); }
					protected void writeDirectName()throws IOException{ throw new AbstractMethodError(); }
					protected void writeSignalNameData(String name)throws IOException{ throw new AbstractMethodError(); }
					protected void writeRegisterName(int name_index)throws IOException{ throw new AbstractMethodError(); }
					protected void writeRegisterUse(int name_index)throws IOException{ throw new AbstractMethodError(); }
					protected  void writeBooleanImpl(boolean v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeByteImpl(byte v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeCharImpl(char v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeShortImpl(short v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeIntImpl(int v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeLongImpl(long v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeFloatImpl(float v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeDoubleImpl(double v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeByteBlockImpl(byte [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeByteBlockImpl(byte data)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeCharBlockImpl(char [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeCharBlockImpl(CharSequence characters, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeShortBlockImpl(short [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeIntBlockImpl(int [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeLongBlockImpl(long [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeFloatBlockImpl(float [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
				
		
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
				
		};
};