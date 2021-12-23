package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import java.io.StringReader;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;

/**
	A test of {@link CXMLIndicatorReadFormat} over known good and 
	known bad hand crafted text files.
*/
public class TestCXMLIndicatorReadFormat extends sztejkat.utils.test.ATest
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
	@Test public void testBaseEventShort()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<marcie></marcie>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("marcie".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBaseEventShortWithSpaces()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test when additional spaces
			are injected.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<marcie  >    </marcie  >"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("marcie".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	@Test public void testBaseEventLong()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test, but this time we do use
			an event name which is not encoded directly
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<event name=\"Monet\"></event>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("Monet".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	@Test public void testBaseEventLongWithSpaces()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test, but this time we do use
			an event name which is not encoded directly and inject 
			multiple spaces 
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<event    name =   \"Monet\"   >   </event   >"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("Monet".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBaseEventLongEscaped()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test, but this time we do use
			an event name which is not encoded directly and carries encoded
			characters 
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<event name=\"%20;&gt;\"></event>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			System.out.println("f.getSignalName()=\""+f.getSignalName()+"\"");
			Assert.assertTrue(" >".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	
	
	@Test public void testPrimitiveBooleanDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			with description indicators.
			
			Note:
				When testing elementary primitives boolean is 
				a good example because it is using the same core
				engines as all fetches EXCEPT getChar() which
				is different. Thous detailed testing of getBoolean
				will test most of engine and remaning test may be
				more rough.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<boolean>t</boolean>\n<boolean>t</boolean>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	@Test public void testPrimitiveBooleanDescribedWithInterspaces()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			with description indicators.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<boolean  >   t   </boolean  > \n<boolean>  t  </boolean>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	
	@Test public void testPrimitiveBooleanUnDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			without type indicators.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"t;t;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	@Test public void testPrimitiveBooleanUnDescribedWithInterSpaces()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			without type indicators.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"   t;  t; "
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	
	@Test public void testPrimitiveBooleanUnDescribedInEvent()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			without type indicators, but enclosed in event.
		*/
		CXMLIndicatorReadFormat f = new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>t;t;</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								);
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			Assert.assertTrue("x".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
};