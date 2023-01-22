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
							"*\"maccaronii \"\"prompte\"\"\";"));
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
	
	@Test public void testCharStitching()throws IOException
	{
		enter();
		CPlainTxtReadFormat d=
				new CPlainTxtReadFormat(
						new StringReader(
							"14,\"\",m,,ar"));
		d.open();
		Assert.assertTrue(d.readChar()=='1');
		Assert.assertTrue(d.readChar()=='4');
		Assert.assertTrue(d.readChar()=='m');
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
};