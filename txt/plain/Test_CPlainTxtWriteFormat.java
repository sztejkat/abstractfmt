package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.ATxtWriteFormat0;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.ATest;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import org.junit.Test;
import org.junit.Assert;

/**
	A manual test for {@link CPlainTxtWriteFormat} validating
	produced content against well known and crafted code.
	<p>
	The in-depth tests are left for interop-test cases. 
*/
public class Test_CPlainTxtWriteFormat extends ATest
{
	
	@Test public void testSingleBoolean()throws IOException
	{
		/*
			Check if token separator is correctly managed
			without enclosing signal
		*/
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeBoolean(true);
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("true".equals(o));
			
		leave();
	};
	@Test public void testDualTokens()throws IOException
	{
		/*
			Check if token separator is correctly managed
			without enclosing signal
		*/
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeBoolean(true);
			w.writeBoolean(false);
			w.writeInt(3456);
			w.writeFloat(1.04E-3f);
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("true,false,3456,0.00104".equals(o));
			
		leave();
	};
	@Test public void testCharTokens()throws IOException
	{
		/*
			Check if char token is corectly enclosed
			when file terminates.
		*/
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeChar('a');
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("\"a\"".equals(o));
			
		leave();
	};
	
	@Test public void testSpecialCharToken_quote()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeChar('\"');
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("\"\\\"\"".equals(o));
			
		leave();
	};
	@Test public void testSpecialCharToken_single_uppersurogate()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeChar('\uD831');
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("\"\\D831;\"".equals(o));
			
		leave();
	};
	@Test public void testSpecialCharToken_dualstitched_surogates()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeChar('\uD831');
			w.writeChar('\uDC31');
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("\"\uD831\uDC31\"".equals(o));
			
		leave();
	};
	@Test public void testSpecialCharToken_slash()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeChar('\\');
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("\"\\\\\"".equals(o));
			
		leave();
	};
	
	@Test public void testCharTokensStitching()throws IOException
	{
		/*
			Check if char token is corectly stitched
		*/
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeChar('a');
			w.writeChar('b');
			w.writeChar('c');
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("\"abc\"".equals(o));
			
		leave();
	};
	
	@Test public void testCharTokensInterrupted()throws IOException
	{
		/*
			Check if char token is corectly stitched, interrupted and stitched.
		*/
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeChar('a');
			w.writeChar('b');
			w.writeChar('c');
			w.writeLong(0);
			w.writeChar('e');
			w.writeChar('f');
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("\"abc\",0,\"ef\"".equals(o));
			
		leave();
	};
	
	
	
	@Test public void testPlainBeginEndSignal()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("mordimer"); //this does NOT require any escaping
			w.writeChar('a');
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*mordimer \"a\";".equals(o));
			
		leave();
	};
	
	@Test public void testPlainBeginEndSignal_empty()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("mordimer"); //this does NOT require any escaping
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*mordimer;".equals(o));
			
		leave();
	};
	
	@Test public void testPlainBeginEndSignal_empty_twice()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("mordimer"); //this does NOT require any escaping
			w.end();
			w.begin("mordimer"); //this does NOT require any escaping
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*mordimer;*mordimer;".equals(o));
			
		leave();
	};
	
	@Test public void testComplexBeginEndSignal_1()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("mordimer martini"); //this DOES require enclosing
			w.writeChar('a');
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"mordimer martini\" \"a\";".equals(o));
			
		leave();
	};
	@Test public void testComplexBeginEndSignal_2()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("name=\"Anna\""); //this DOES require escaping
			w.writeChar('a');
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"name=\\\"Anna\\\"\" \"a\";".equals(o));
			
		leave();
	};
	@Test public void testComplexBeginEndSignal_3()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("flying*ar"); //this DOES require enclosing
			w.writeChar('a');
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"flying*ar\" \"a\";".equals(o));
			
		leave();
	};
	@Test public void testComplexBeginEndSignal_4()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("#Duuup"); //this DOES require enclosing
			w.writeChar('a');
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"#Duuup\" \"a\";".equals(o));
			
		leave();
	};
	
	@Test public void testComplexBeginEndSignal_Empty()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("flying*ar"); //this DOES require enclosing
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"flying*ar\";".equals(o));
			
		leave();
	};
	
	
	@Test public void testNameWithValidSurogate()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("flying\uD800\uDC00moth"); //this DOES require enclosing, but won't be escaped
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"flying\uD800\uDC00moth\";".equals(o));
			
		leave();
	};
	
	@Test public void testNameWithValidSurogate_at_eof()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("flying\uD899\uDC99"); //this DOES require enclosing, but won't be escaped
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"flying\uD899\uDC99\"".equals(o));
			
		leave();
	};
	@Test public void testNameWithValidSurogate_at_end()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("flying\uD899\uDC99"); //this DOES require enclosing, but won't be escaped
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"flying\uD899\uDC99\";".equals(o));
			
		leave();
	};
	
	@Test public void testNameWithInvalidUpperSurogate()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("flying\uD800moth"); //this DOES require enclosing, and will be escaped
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"flying\\D800;moth\";".equals(o));
			
		leave();
	};
	
	@Test public void testNameWithInvalidUpperSurogate_at_end()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("flying\uD8EF"); //this DOES require enclosing, and will be escaped
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"flying\\D8EF;\";".equals(o));
			
		leave();
	};
	
	@Test public void testNameWithInvalidUpperSurogate_at_eof()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("flying\uD8EF"); //this DOES require enclosing, and will be escaped
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"flying\\D8EF;\"".equals(o));
			
		leave();
	};
	@Test public void testNameWithInvalidLowerSurogate()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("flying\uDCE0moth"); //this DOES require enclosing, and will be escaped
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*\"flying\\DCE0;moth\";".equals(o));
			
		leave();
	};
	
	
	@Test public void testSignalInSignal()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("girl"); //this DOES require enclosing
				w.begin("age");
					w.writeInt(33);
				w.end();
				w.begin("3sizes");
					w.writeInt(90);
					w.writeInt(40);
					w.writeInt(90);
				w.end();
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*girl *age 33;*3sizes 90,40,90;;".equals(o));
			
		leave();
	};
	
	
	@Test public void testBooleanBlock()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("[]");
				w.writeBooleanBlock(new boolean[]{ true, false });
				w.writeBooleanBlock(false);
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*[] t,f,f;".equals(o));
			
		leave();
	};
	
	@Test public void testByteBlock()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("[]");
				w.writeByteBlock(new byte[]{ (byte)0,(byte)3},1,1);
				w.writeByteBlock((byte)-3);
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*[] 3,-3;".equals(o));
			
		leave();
	};
	
	@Test public void testCharBlock()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("[]");
				w.writeCharBlock(new char[]{'a','b','\"'});
				w.writeCharBlock('q');
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*[] \"ab\\\"q\";".equals(o));
			
		leave();
	};
	
	@Test public void testStringBlock()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.begin("[]");
				w.writeString("skopolamina");
				w.writeString('-');
				w.writeString("brain");
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*[] \"skopolamina-brain\";".equals(o));
			
		leave();
	};
	
	
	
	private static void printlnDetailed(String s)
	{
		for(int i=0;i<s.length();i++)
		{
			char c= s.charAt(i);
			System.out.print(c+"("+Integer.toHexString(c)+")");
		};
		System.out.println();
	};
	private void testComment_StandAlone(String cmt, String expected)throws IOException
	{
		enter("\""+cmt+"\"");
		StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeComment(cmt);
			w.close();
			
			String o = ow.toString();
			
			System.out.println("\""+o+"\"");
			printlnDetailed(o);
			printlnDetailed(expected);
			Assert.assertTrue(expected.equals(o));
		leave();
	};
	@Test public void testSingleLineComment_StandAlone()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is my comment",
				"#This is my comment\n"
				);
		leave();
	};
	@Test public void testSingleLineComment_StandAlone_withEol()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is my comment\n",
				"#This is my comment\n"
				);
		leave();
	};
	@Test public void testSingleLineComment_StandAlone_withEol_r()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is my comment\r",
				"#This is my comment\r"
				);
		leave();
	};
	@Test public void testSingleLineComment_StandAlone_withEol_rn()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is my comment\r\n",
				"#This is my comment\r\n"
				);
		leave();
	};
	
	@Test public void testTwoLineComment_StandAlone()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is\nSecond line",
				"#This is\n#Second line\n"
				);
		leave();
	};
	
	@Test public void testTwoLineComment_StandAlone_withEol()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is\nSecond line\n",
				"#This is\n#Second line\n"
				);
		leave();
	};
	
	@Test public void testTwoLineComment_StandAlone_rn()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is\r\nSecond line",
				"#This is\r\n#Second line\n"
				);
		leave();
	};
	@Test public void testTwoLineComment_StandAlone_nr()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is\n\rSecond line",
				"#This is\n\r#Second line\r"
				);
		leave();
	};
	@Test public void testBlockComment_StandAlone_n()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is\n\n\nSecond line",
				"#This is\n#\n#\n#Second line\n"
				);
		leave();
	};
	@Test public void testBlockComment_StandAlone_rn()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is\r\n\r\n\r\nSecond line",
				"#This is\r\n#\r\n#\r\n#Second line\n"
				);
		leave();
	};
	@Test public void testBlockComment_StandAlone_nr()throws IOException
	{
		enter();
			testComment_StandAlone(
				"This is\n\r\n\r\n\rSecond line",
				"#This is\n\r#\n\r#\n\r#Second line\r"
				);
		leave();
	};
	
	@Test public void testCommentAndTokens()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeInt(10);
			w.writeComment("X");
			w.writeInt(10);
			w.close();
			
			String o = ow.toString();
			
			System.out.println("\""+o+"\"");
			final String expected="10#X\n,10";
			Assert.assertTrue(expected.equals(o));
		leave();
		leave();
	};
	
	
	@Test public void testSurogate_followedby_tokens()throws IOException
	{
		/*
			A test case verifying if surogate manipulation is correctly terminated by 
			plain tokens.
		*/
			enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			
			w.open();
			w.writeChar((char)0xD800);
			w.writeChar((char)0xDC01);	//good surogate pair
			w.writeInt(-234);
			w.writeChar((char)0xDC00);
			w.writeChar((char)0xD801);	//bad surogate pair
			w.writeInt(-235);
			w.writeChar((char)0xD800);	//dangling upper surogate
			w.writeInt(-236);
			w.close();
		
			String o = ow.toString();
			System.out.println(o);
			
				
			final String expected="\"\uD800\uDC01\",-234,\"\\DC00;\\D801;\",-235,\"\\D800;\",-236";
									// good surogate pair is not escaped
															//bad is escaped and dangling too.
			System.out.println(expected);
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
	
	@Test public void testSurogate_followedby_begin()throws IOException
	{
		/*
			A test case verifying if surogate manipulation is correctly terminated by 
			signals
		*/
			enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			
			w.open();
			w.writeChar((char)0xD800);
			w.writeChar((char)0xDC01);	//good surogate pair
			w.begin("");
			w.writeChar((char)0xDC00);
			w.writeChar((char)0xD801);	//bad surogate pair
			w.begin("");
			w.writeChar((char)0xD800);	//dangling upper surogate
			w.begin("");
			w.close();
		
			String o = ow.toString();
			System.out.println("-"+o+"-");
			
				
			final String expected="\"\uD800\uDC01\"* \"\\DC00;\\D801;\"* \"\\D800;\"*";
									// good surogate pair is not escaped
															//bad is escaped and dangling too.
			System.out.println(expected);
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
	
	@Test public void testSurogate_followedby_end()throws IOException
	{
		/*
			A test case verifying if surogate manipulation is correctly terminated by 
			signals
		*/
			enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			
			w.open();
			w.begin("");
			w.begin("");
			w.begin("");
			w.writeChar((char)0xD800);
			w.writeChar((char)0xDC01);	//good surogate pair
			w.end();
			w.writeChar((char)0xDC00);
			w.writeChar((char)0xD801);	//bad surogate pair
			w.end();
			w.writeChar((char)0xD800);	//dangling upper surogate
			w.end();
			w.close();
		
			String o = ow.toString();
			System.out.println("-"+o+"-");
			
				
			final String expected="* * * \"\uD800\uDC01\";\"\\DC00;\\D801;\";\"\\D800;\";";
									// good surogate pair is not escaped
															//bad is escaped and dangling too.
			System.out.println(expected);
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
};