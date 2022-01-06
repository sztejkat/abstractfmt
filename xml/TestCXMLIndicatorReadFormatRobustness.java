package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EDataMissmatch;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import java.io.StringReader;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;

/**
	A test of {@link CXMLIndicatorReadFormat} over known bad hand crafted text files.
*/
public class TestCXMLIndicatorReadFormatRobustness extends sztejkat.utils.test.ATest
{
	/** Validates if <code>is</code> is same as <code>expected</code>
		and prints state information.
		@param is what is read from format
		@param expected what is expected from format.
		@throws AssertionError if not the same.
	*/
	private static void expect(TIndicator is, TIndicator expected)
	{
		System.out.println("Expected indicator:"+expected+" found "+is);
		Assert.assertTrue(is==expected);
	};
	@Test public void testEventNameMissmatch()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test, where end does not match begin.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<marcie></johan>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			Assert.assertTrue("marcie".equals(f.getSignalName()));
			f.next();
			try{
				expect(f.getIndicator(), TIndicator.END);
				Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex); };
		leave();
	};
	@Test public void testInvalidTagName()throws IOException
	{
		enter();
		/*
			This is a test when XML tag contains invalid characters. 
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<ma::rcie></johan>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);			
			try{
				f.getIndicator();
				Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex); };
		leave();
	};
	
	@Test public void testMissingAttribute()throws IOException
	{
		enter();
		/*
			This is a test when long event tag is missing a name attribute
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<event></>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);			
			try{
				f.getIndicator();
				Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex); };
		leave();
	};
	
	@Test public void testUnexpectedAttribute()throws IOException
	{
		enter();
		/*
			This is a test short event tag is having attributes
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<x name=\"tag\"></>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);			
			try{
				f.getIndicator();
				Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex);};
		leave();
	};
	
	@Test public void testUnexpectedAttribute2()throws IOException
	{
		enter();
		/*
			This is a test closing tag is having attributes
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<x></x name=\"tag\">"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.getIndicator();
			f.next();			
			try{
				f.getIndicator();
				Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testWrongAttributeName()throws IOException
	{
		enter();
		/*
			This is a test when long event tag is missing a name attribute
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<event zorka=\"spaaa\"></>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);			
			try{
				f.getIndicator();
				Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex); };
		leave();
	};
	
	@Test public void testWrongEscapeInAttribiteValue()throws IOException
	{
		enter();
		/*
			This is a test when long event tag is missing a name attribute
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<event name=\"s&&&;paaa\"></>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);			
			try{
				f.getIndicator();
				Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex); };
		leave();
	};
	
	@Test public void testTooLongShortEventName()throws IOException
	{
		enter();
		/*
			Check if length defense works
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<a12345678></a12345678>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.setMaxSignalNameLength(8);
			try{
				f.getIndicator();
				Assert.fail();
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
		leave();
	};
	@Test public void testTooLongShortEventName2()throws IOException
	{
		enter();
		/*
			Check if length defense works, bordeline case.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<a1234567></a1234567>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.setMaxSignalNameLength(8);
			expect(f.getIndicator(),TIndicator.BEGIN_DIRECT);	//<--this time it must not fail. 
			
		leave();
	};
	
	
	@Test public void testInvalidBoolean()throws IOException
	{
		enter();
		/*
			Check if detects invalid boolean
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"7;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readBoolean();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testLongBoolean()throws IOException
	{
		enter();
		/*
			Check if detects boolean of too many valid characters
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"tttttt;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readBoolean();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testTooShortEscape()throws IOException
	{
		enter();
		/*
			Check if detects char 
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"%;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readChar();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	@Test public void testTooLongEscape()throws IOException
	{
		enter();
		/*
			Check if detects char 
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"%00253532;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readChar();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testUnknownAmpEscape()throws IOException
	{
		enter();
		/*
			Check if detects char 
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"&tr;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readChar();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testVeryLongAmpEscape()throws IOException
	{
		enter();
		/*
			Check if detects char 
			
			Note: This must throw EBrokenFormat not EDataMissmatch, because indicators
			are not type checking and this is unrecoverable in XML.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"&amperioeiopqwopeoppdqopdeopjqopwejdjqweojdiopjqwjdjqwedopqejwopdjqweopjdopeqwjopdjeqwpjdpqowr;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readChar();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	@Test public void testTooShortAmpEscape()throws IOException
	{
		enter();
		/*
			Check if detects char 
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"&;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readChar();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	
	
	@Test public void testInvalidByte()throws IOException
	{
		enter();
		/*
			Check if detects invalid byte
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"1.4e-3;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readByte();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	@Test public void testInvalidByte2()throws IOException
	{
		enter();
		/*
			Check if detects invalid byte
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"258;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readByte();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	@Test public void testPrimitiveLengthAttack()throws IOException
	{
		enter();
		/*
			Check if detects too long numeric primitive
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"2580000000000000000000000;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readByte();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	@Test public void testPrimitiveLengthAttack2()throws IOException
	{
		enter();
		/*
			Check if detects too long numeric primitive
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"2580000000000000000000000;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readShort();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	@Test public void testPrimitiveLengthAttack3()throws IOException
	{
		enter();
		/*
			Check if detects too long numeric primitive
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"2580000000000000000000000;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readInt();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	@Test public void testPrimitiveLengthAttack4()throws IOException
	{
		enter();
		/*
			Check if detects too long numeric primitive
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"2580000000000000000000000;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			try{
				f.readLong();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testInvalidByteBlock()throws IOException
	{
		enter();
		/*
			Check if detects invalid byte block, specifically how does it
			react on whitespaces.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"ABC D"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			f.readByteBlock();
			try{
				f.readByteBlock();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
	
	@Test public void testInvalidByteBlock2()throws IOException
	{
		enter();
		/*
			Check if detects invalid byte block, specifically how does it
			react on whitespaces.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"ABC;D"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			f.readByteBlock();
			try{
				f.readByteBlock();
				Assert.fail();
				}catch(EDataMissmatch ex){ System.out.println(ex); };
		leave();
	};
};