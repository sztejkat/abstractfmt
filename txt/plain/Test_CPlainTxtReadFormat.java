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
	};
	@Test public void testEmptyStructWithLeadingSeparators()throws IOException
	{
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
	};
	@Test public void testEmptyStructWithInnerSeparators()throws IOException
	{
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
	};
};