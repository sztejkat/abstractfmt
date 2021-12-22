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
};