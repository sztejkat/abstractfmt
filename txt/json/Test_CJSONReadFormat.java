package sztejkat.abstractfmt.txt.json;
import sztejkat.abstractfmt.txt.*;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.ATest;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;
import org.junit.Assert;

/**
	A test bed for {@link CJSONReadFormat} against known good and known bad test cases.
*/
public class Test_CJSONReadFormat extends ATest
{
	/* ****************************************************************************
	
	
	
				Known good cases
	
	
	
	*****************************************************************************/
	/* ------------------------------------------------------------------------
	
				Opening and closing
	
	------------------------------------------------------------------------*/
	@Test public void testOpen()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[ 3 ]")
							);
			
			d.open();
			d.close();
		leave();
	};
	@Test public void testOpenWithWhitespaces()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("\n\t   [ 3 ]")
							);
			
			d.open();
			d.close();
		leave();
	};
	@Test public void testOpenWithBOM()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("\uFEFF\n\t   []")
							);
			
			d.open();
			d.close();
		leave();
	};
	/* ------------------------------------------------------------------------
	
				Processing stand-alone values.
	
	------------------------------------------------------------------------*/
	@Test public void standAlonePlain()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[3]")
							);
			
			d.open();
			Assert.assertTrue(d.readInt()==3);
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void standAloneString()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[\"tomcat\"]")
							);
			
			d.open();
			Assert.assertTrue("tomcat".equals(d.readString(100)));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void standAloneMorePlainElements()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[3,4,true]")
							);
			
			d.open();
			Assert.assertTrue(d.readInt()==3);
			Assert.assertTrue(d.readInt()==4);
			Assert.assertTrue(d.readBoolean());
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	
	@Test public void standAloneMorePlainElementsWithWhitespaces()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[   3,  4  ,true   ]")
							);
			
			d.open();
			Assert.assertTrue(d.readInt()==3);
			Assert.assertTrue(d.readInt()==4);
			Assert.assertTrue(d.readBoolean());
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	
	@Test public void standAloneMoreStrings()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[\"tomcat\",  \" is \\\"bronco\\\"\"   ,\" zaurus\"]")
							);
			
			d.open();
			Assert.assertTrue("tomcat is \"bronco\" zaurus".equals(d.readString(100)));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ------------------------------------------------------------------------
	
				Processing structures
	
	------------------------------------------------------------------------*/
	@Test public void testSingleEmptyNamedStruct()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":[]}]")
							);
			
			d.open();
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void testSingleEmptyNonameStructWithSpaces()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[  {  \"\"  : [   ] }   ]")
							);
			
			d.open();
			Assert.assertTrue("".equals(d.next()));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	
	@Test public void testTwoStructs()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":[]},{\"paolo\":[]}]")
							);
			
			d.open();
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue("paolo".equals(d.next()));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void testStructAfterPlain()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[3,{\"struct\":[]}]")
							);
			
			d.open();
			Assert.assertTrue(3==d.readInt());
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void testPlainAfterStruct()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":[]},4]")
							);
			
			d.open();			
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(d.hasElementaryData());
			Assert.assertTrue(4==d.readInt());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void testStructAfterString()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[\"?\",{\"struct\":[]}]")
							);
			
			d.open();
			Assert.assertTrue("?".equals(d.readString(100)));
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void testStructInterleavedWithString()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":[]},\"?\",{\"urk\":[]}]")
							);
			
			d.open();
			
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(d.hasElementaryData());
			Assert.assertTrue("?".equals(d.readString(100)));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue("urk".equals(d.next()));
			Assert.assertTrue(null==d.next());
			d.close();
		leave();
	};       
	
	
	@Test public void structWithData()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":[1,2,  4, 5]}]")
							);
			
			d.open();
			
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(d.hasElementaryData());
			Assert.assertTrue(d.readInt()==1);
			Assert.assertTrue(d.readInt()==2);
			Assert.assertTrue(d.readInt()==4);
			Assert.assertTrue(d.readInt()==5);
			Assert.assertTrue(null==d.next());
			
			
			d.close();
		leave();
	};
	
	@Test public void structWithStruct()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":[ {\"ally\":[3]}]}]")
							);
			
			d.open();
			
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue("ally".equals(d.next()));
			Assert.assertTrue(d.readInt()==3);
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(null==d.next());
			
			
			d.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	/* *******************************************************************
	
	
				Special constructs
	
	
	
	********************************************************************/
	
	@Test public void plainSingleElementStruct()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":44},45]")
							);
			
			d.open();
			
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(d.hasElementaryData());
			Assert.assertTrue(d.readInt()==44);
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(d.readInt()==45);
			
			d.close();
		leave();
	};
	
	@Test public void charSingleElementStruct()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":\"c\"},45]")
							);
			
			d.open();
			
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(d.hasElementaryData());
			Assert.assertTrue(d.readChar()=='c');
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(d.readInt()==45);
			
			d.close();
		leave();
	};
	
	@Test public void stringSingleElementStruct()throws IOException
	{
		//Note: this struct won't ever be generated by a writer.
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":\"car\"},45]")
							);
			
			d.open();
			
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(d.hasElementaryData());
			Assert.assertTrue(d.readChar()=='c');
			Assert.assertTrue(d.readChar()=='a');
			Assert.assertTrue(d.readChar()=='r');
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(d.readInt()==45);
			
			d.close();
		leave();
	};
	
	@Test public void stringSingleElementStruct2()throws IOException
	{
		//Note: this struct won't ever be generated by a writer.
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":\"car\"},45]")
							);
			
			d.open();
			
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(d.hasElementaryData());
			Assert.assertTrue("car".equals(d.readString(1000)));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(d.readInt()==45);
			
			d.close();
		leave();
	};
	
	
	
	
	
	
	
	
	@Test public void whitespaceLimit_noLimit()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[                         {\"struct\":[]}]")
							);
			
			d.open();
			
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(null==d.next());
			
			d.close();
		leave();
	};
	
	@Test public void whitespaceLimit_limitApplied()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[                         {\"struct\":[]}]")
							);
			d.setContinousWhitespaceLimit(10);
			d.open();
			try{
				Assert.assertTrue("struct".equals(d.next()));
				Assert.fail();
				Assert.assertTrue(null==d.next());
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	@Test public void whitespaceLimit_limitApplied2()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{                         \"struct\":[]}]")
							);
			d.setContinousWhitespaceLimit(10);
			d.open();
			try{
				Assert.assertTrue("struct".equals(d.next()));
				Assert.fail();
				Assert.assertTrue(null==d.next());
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	@Test public void whitespaceLimit_limitApplied3()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\"                         :[]}]")
							);
			d.setContinousWhitespaceLimit(10);
			d.open();
			try{
				Assert.assertTrue("struct".equals(d.next()));
				Assert.assertTrue(null==d.next());
				Assert.fail();
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	@Test public void whitespaceLimit_limitApplied4()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("[{\"struct\":                         []}]")
							);
			d.setContinousWhitespaceLimit(10);
			d.open();
			try{
				Assert.assertTrue("struct".equals(d.next()));
				Assert.assertTrue(null==d.next());
				Assert.fail();
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	@Test public void whitespaceLimit_limitAppliedNonAccumulating()throws IOException
	{
		enter();
			CJSONReadFormat d = new CJSONReadFormat(
							new StringReader("       [       {      \"struct\"        :        [        ]     }     ]")
							);
			d.setContinousWhitespaceLimit(10);
			d.open();
			Assert.assertTrue("struct".equals(d.next()));
			Assert.assertTrue(null==d.next());
			d.close();
		leave();
	};
};