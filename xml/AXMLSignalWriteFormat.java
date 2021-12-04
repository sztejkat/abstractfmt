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