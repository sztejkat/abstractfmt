package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.ATest;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;
import org.junit.Assert;

/**
	A test of {@link CPlainTxtReadFormat} against
	well known test cases.
*/
public class Test_CPlainTxtReadFormat extends ATest
{
	@Test public void testEmptyStruct()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*mamma;"));
		d.open();
		Assert.assertTrue("mamma".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	@Test public void testEmptyStructWithLeadingSeparators()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							" \t\n*mamma;"));
		d.open();
		Assert.assertTrue("mamma".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	@Test public void testNamelessBeginSequence()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"***;;;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		leave();
	};
	@Test public void testNamelessBeginSequenceWithSeparator()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* *\t*\n;\t;\r\n;\n"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		leave();
	};
	@Test public void testNamedBeginSequenceWithSeparator()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*robo\n*skunk *\"marty\" ;;;"));
		d.open();
		Assert.assertTrue("robo".equals(d.next()));
		Assert.assertTrue("skunk".equals(d.next()));
		Assert.assertTrue("marty".equals(d.next()));
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		leave();
	};
	
	@Test public void testNamedBeginSequenceWithoutSeparator()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*robo*skunk*\"marty\";;;"));
		d.open();
		Assert.assertTrue("robo".equals(d.next()));
		Assert.assertTrue("skunk".equals(d.next()));
		Assert.assertTrue("marty".equals(d.next()));
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		leave();
	};
	
	@Test public void testNamedBeginChainWithoutSeparator()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*robo;*skunk;*\"marty\";"));
		d.open();
		Assert.assertTrue("robo".equals(d.next()));
		Assert.assertTrue(null==d.next());
		Assert.assertTrue("skunk".equals(d.next()));
		Assert.assertTrue(null==d.next());
		Assert.assertTrue("marty".equals(d.next()));
		Assert.assertTrue(null==d.next());
		leave();
	};
	
	@Test public void testBeginTerminatesPlainToken()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"10*hobbo"));
		d.open();
		Assert.assertTrue(10==d.readInt());
		Assert.assertTrue("hobbo".equals(d.next()));
		
		leave();
	};  
	@Test public void testEndTerminatesPlainToken()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* 10;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(10==d.readInt());
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	@Test public void testNoSeparatorAfterQuotedBegin()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*\"hobbo\"10"));
		d.open();
		Assert.assertTrue("hobbo".equals(d.next()));
		Assert.assertTrue(10==d.readInt());
		leave();
	};  
	
	@Test public void testEmptyStructWithInnerSeparators()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*mamma   \t\n\r;"));
		d.open();
		Assert.assertTrue("mamma".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	@Test public void testEmptyStructWithTailingSeparators()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*mamma; \t\n\r"));
		d.open();
		Assert.assertTrue("mamma".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	@Test public void testEmptyNamelessStruct()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	@Test public void testEmptyStruct_Quoted()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*\"maccaronii prompte\";"));
		d.open();
		Assert.assertTrue("maccaronii prompte".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	@Test public void testEmptyStruct_Quoted_trailing_whitespaces()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*\"maccaronii prompte\"   ;"));
		d.open();
		Assert.assertTrue("maccaronii prompte".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	@Test public void testEmptyStruct_Quoted_empty_name()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*\"\"   ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	@Test public void testEmptyStruct_Quoted_escaped()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*\"maccaronii \\\"prompte\\\"\";"));
		d.open();
		//System.out.println(d.next());
		Assert.assertTrue("maccaronii \"prompte\"".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};    
	
	
	@Test public void testSequenceOfEmptyStructs()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*ma;*pa;"));
		d.open();
		//System.out.println(d.next());
		Assert.assertTrue("ma".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue("pa".equals(d.next()));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	@Test public void testNestedStructs()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*ma *pa ; ;"));
		d.open();
		//System.out.println(d.next());
		Assert.assertTrue("ma".equals(d.next()));
		Assert.assertTrue("pa".equals(d.next()));
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		leave();
	};
	
	@Test public void testNestedStructsTight()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*ma*pa;;"));
		d.open();
		//System.out.println(d.next());
		Assert.assertTrue("ma".equals(d.next()));
		Assert.assertTrue("pa".equals(d.next()));
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		leave();
	};
	@Test public void testNestedNamelessStructsTight()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"**;;"));
		d.open();
		//System.out.println(d.next());
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(null==d.next());
		leave();
	};
	
	
	@Test public void testSingleNumeric()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"123"));
		d.open();
		Assert.assertTrue(d.readInt()==123);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	@Test public void testDualNumeric()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"123,345"));
		d.open();
		Assert.assertTrue(d.readInt()==123);
		Assert.assertTrue(d.readInt()==345);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	@Test public void testMessingCharsWithNumeric()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"ab,345"));
		d.open();
		Assert.assertTrue(d.readChar()=='a');
		Assert.assertTrue(d.readChar()=='b');
		Assert.assertTrue(d.readInt()==345);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	
	@Test public void testMessingCharsWithNumeric_space_before_separator()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"ab ,345"));
		d.open();
		Assert.assertTrue(d.readChar()=='a');
		Assert.assertTrue(d.readChar()=='b');
		Assert.assertTrue(d.readInt()==345);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	@Test public void testMessingCharsWithNumeric_space_after_separator()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"ab, 345"));
		d.open();
		Assert.assertTrue(d.readChar()=='a');
		Assert.assertTrue(d.readChar()=='b');
		Assert.assertTrue(d.readInt()==345);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	
	
	@Test public void testDualNumeric_with_spaces()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"\t123 ,\n345\n\n"));
		d.open();
		Assert.assertTrue(d.readInt()==123);
		Assert.assertTrue(d.readInt()==345);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	@Test public void testSingleQuotedNumeric()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"\"0x44\""));
		d.open();
		Assert.assertTrue(d.readShort()==0x44);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	@Test public void testDualQuotedNumeric()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"\"0x44\",\"false\""));
		d.open();
		Assert.assertTrue(d.readShort()==0x44);
		Assert.assertTrue(d.readBoolean()==false);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	@Test public void testEmptyNumeric()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"1,,4"));
		d.open();
		Assert.assertTrue(d.readShort()==1);
		Assert.assertTrue(d.readShort()==0);
		Assert.assertTrue(d.readShort()==4);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	@Test public void testEmptyNumericQuoted()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"1,\"\",4"));
		d.open();
		Assert.assertTrue(d.readShort()==1);
		Assert.assertTrue(d.readShort()==0);
		Assert.assertTrue(d.readShort()==4);
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	@Test public void testCharStitchingWithEmptyToken()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"14,\"\",m,   ,ar"));
		d.open();
		Assert.assertTrue(d.readChar()=='1');
		Assert.assertTrue(d.readChar()=='4');
		Assert.assertTrue(d.readChar()==0);	//empty ""
		Assert.assertTrue(d.readChar()=='m');
		Assert.assertTrue(d.readChar()==0);
		Assert.assertTrue(d.readChar()=='a');
		Assert.assertTrue(d.readChar()=='r');
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};    
	
	@Test public void testCharStitching()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"14,\"\",m,ar,\"kar\""));
		d.open();
		Assert.assertTrue(d.readChar()=='1');
		Assert.assertTrue(d.readChar()=='4');
		Assert.assertTrue(d.readChar()==0);	//empty ""
		Assert.assertTrue(d.readChar()=='m');
		Assert.assertTrue(d.readChar()=='a');
		Assert.assertTrue(d.readChar()=='r');
		Assert.assertTrue(d.readChar()=='k');
		Assert.assertTrue(d.readChar()=='a');
		Assert.assertTrue(d.readChar()=='r');
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	
	@Test public void testValuesInStruct()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*start 1,3,  35;"));
		d.open();
		Assert.assertTrue("start".equals(d.next()));
		Assert.assertTrue(d.readChar()=='1');
		Assert.assertTrue(d.readChar()=='3');
		Assert.assertTrue(d.readInt()==35);
		
		leave();
	};
	
	@Test public void testValuesInStructNoMoreDataAndEof()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*start 1,3,  35;-77"));
		d.open();
		Assert.assertTrue("start".equals(d.next()));
		Assert.assertTrue(d.readChar()=='1');
		Assert.assertTrue(d.readChar()=='3');
		Assert.assertTrue(d.readInt()==35);
		try{
				d.readInt();
				Assert.fail();
		}catch(ENoMoreData ex){};
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(d.readInt()==-77);
		try{
				d.readInt();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	@Test public void testCommentIsIgnoredInFistBlock()throws IOException
	{		
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"#A comment\n"+
							"*ma 4,3;"));
							
		d.open();
		Assert.assertTrue("ma".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		Assert.assertTrue(d.readInt()==4);
		Assert.assertTrue(d.readInt()==3);
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	@Test public void testCommentTerminatesName()throws IOException
	{		
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*ma#comment\n4,3;"));
							
		d.open();
		Assert.assertTrue("ma".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		Assert.assertTrue(d.readInt()==4);
		Assert.assertTrue(d.readInt()==3);
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	@Test public void testCommentDoesNotPreventStchingOfStrigs()throws IOException
	{		
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*ma robo,#comment\n, \"teur\";"));
							
		d.open();
		Assert.assertTrue("ma".equals(d.next()));
		String s= d.readString(100);
		System.out.println(s);
		Assert.assertTrue("roboteur".equals(s));
		leave();
	};
	
	@Test public void testCommentTerminatesPlainToken()throws IOException
	{		
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*ma 4#comment\n4,3;"));
							
		d.open();
		Assert.assertTrue("ma".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		Assert.assertTrue(d.readInt()==4);
		//Note: If comment would not terminted, we would get 44.
		//		We can't however fetch next int, because we would get
		//		a syntax error due to lack of ,
		leave();
	};
	
	@Test public void testCommentIgnoredInQuoted()throws IOException
	{
		/*
			Check if comment does not influence quouted signal name
		*/
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*\"#mamba\" ;"));
		d.open();
		Assert.assertTrue("#mamba".equals(d.next()));
		Assert.assertTrue(!d.hasElementaryData());
		Assert.assertTrue(null==d.next());
		Assert.assertTrue(!d.hasElementaryData());
		try{
				d.next();
				Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	
	
	@Test public void testEscapedSlash()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\\\.com\" ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("\\.com".equals(v));
		Assert.assertTrue(null==d.next());		
		leave();
	};
	
	@Test public void testUnescapedUnqotedGoodSurogate()throws IOException
	{
		/*
			This is a piece of text which writer won't produce.
			Writer will always double-quote text with surogate pairs
		*/
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \uD801\uDC01 ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("\uD801\uDC01".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	@Test public void testEscapedQutedGoodSurogate()throws IOException
	{
		/*
			This is a piece of text which writer won't produce.
			Writer will always double-quote text with surogate pairs,
			but won't escape a good pair
		*/
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\D801;\\DC01;\" ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("\uD801\uDC01".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	@Test public void testEscapedLoneUpperSurogate()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\D801;\" ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("\uD801".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	@Test public void testEscaped4digitter()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\34af;\" ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("\u34AF".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	
	@Test public void testEscaped3digitter()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\34a;\" ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("\u034A".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	@Test public void testEscaped2digitter()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\4f;\" ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println("-"+v+"-");
		Assert.assertTrue("\u004F".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	@Test public void testEscaped1digitter()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\8;\" ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("\u0008".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	@Test public void testEscaped0digitter()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\;\" ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("\u0000".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	
	@Test public void testStringAfterEmptyName()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* string ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("string".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	@Test public void testStringAfterEmptyQuotedName()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*\"\" string ;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		Assert.assertTrue(d.hasElementaryData());
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("string".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	@Test public void testStringsStitchingAfterEmptyName()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* string,_marlene;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("string_marlene".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	@Test public void testStringsStitchingAfterEmptyNameWithSeparator()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* string\t,\n\t_marlene;"));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		String v = d.readString(100);
		System.out.println(v);
		Assert.assertTrue("string_marlene".equals(v));
		Assert.assertTrue(null==d.next());
		
		leave();
	};
	
	@Test public void testHandlingTooLongEscape()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"*u \"\\00000;\" , 3"));
		d.open();
		Assert.assertTrue("u".equals(d.next()));
		try{
			String v = d.readString(100);
			Assert.fail();
		}catch(EBrokenFormat ex){System.out.println(ex); };
		leave();
	};
	@Test public void testHandlingMissingEscapeSemicolon()throws IOException
	{
		enter();
		/*
			Design notes:
		*/
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\0000\" "));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		try{
			String v = d.readString(100);
			Assert.fail();
		}catch(EBrokenFormat ex){System.out.println(ex); };
		leave();
	};
	@Test public void testHandlingIvalidEscapeTerminator()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\0000!\" "));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		try{
			String v = d.readString(100);
			Assert.fail();
		}catch(EBrokenFormat ex){System.out.println(ex); };
		leave();
	};
	@Test public void testHandlingIvalidEscapeDigit()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"* \"\\000q;\" "));
		d.open();
		Assert.assertTrue("".equals(d.next()));
		try{
			String v = d.readString(100);
			Assert.fail();
		}catch(EBrokenFormat ex){System.out.println(ex); };
		leave();
	};
};