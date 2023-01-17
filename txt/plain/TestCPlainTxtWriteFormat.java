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
public class TestCPlainTxtWriteFormat extends ATest
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
	
	@Test public void testSpecialCharToken()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			CPlainTxtWriteFormat w = new CPlainTxtWriteFormat(ow);
		
			w.open();
			w.writeChar('\"');
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("\"\"\"\"".equals(o));
			
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
			
			Assert.assertTrue("*\"name=\"\"Anna\"\"\" \"a\";".equals(o));
			
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
			
			Assert.assertTrue("*girl *age 33; *3sizes 90,40,90;;".equals(o));
			
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
			
			Assert.assertTrue("*[] true,false,false;".equals(o));
			
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
			
			Assert.assertTrue("*[] \"ab\"\"q\";".equals(o));
			
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
};