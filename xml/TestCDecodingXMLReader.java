package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EBrokenFormat;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	Test for {@link CDecodingXMLReader} 
*/
public class TestCDecodingXMLReader extends sztejkat.utils.test.ATest
{
	@Test public void testDecoding()throws IOException
	{
		enter();
		/* Check un-escape reading */
		CDecodingXMLReader r = new CDecodingXMLReader(
					new StringReader("abc"),10,100, SXMLSettings.LONG_BARE
					);
		Assert.assertTrue('a'==r.readBodyChar());
		Assert.assertTrue('b'==r.readBodyChar());
		Assert.assertTrue('c'==r.readBodyChar());
		try{
			r.readBodyChar(); Assert.fail();
			}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testDecodingEscaped()throws IOException
	{
		enter();
		/* Check escaped reading */
		CDecodingXMLReader r = new CDecodingXMLReader(
					new StringReader("%20;%30;"),10,100, SXMLSettings.LONG_BARE
					);
		Assert.assertTrue(-0x20-1==r.readBodyChar());
		Assert.assertTrue(-0x30-1==r.readBodyChar());
		try{
			r.readBodyChar(); Assert.fail();
			}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testDecodingEscapedXMLterminator()throws IOException
	{
		enter();
		/* Check escaped reading when terminator is optimized 
		and XML tag is found */
		CDecodingXMLReader r = new CDecodingXMLReader(
					new StringReader("%2A;%3c<"),10,100, SXMLSettings.LONG_BARE
					);
		Assert.assertTrue(-0x2A-1==r.readBodyChar());
		Assert.assertTrue(-0x3C-1==r.readBodyChar());
		Assert.assertTrue('<'==r.readBodyChar());	//non-consumable terminator.
		try{
			r.readBodyChar(); Assert.fail();
			}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testDecodingEscapedBroken()throws IOException
	{
		enter();
		/* Check escaped reading when terminator is optimized 
		and XML tag is found */
		CDecodingXMLReader r = new CDecodingXMLReader(
					new StringReader("%20;%30G<"),10,100, SXMLSettings.LONG_BARE
					);
		Assert.assertTrue(-0x20-1==r.readBodyChar());
		try{
			r.readBodyChar(); Assert.fail();
			}catch(EBrokenFormat ex){};
		
		leave();
	};
	
	
	@Test public void testDecodingStandardEscapes()throws IOException
	{
		enter();
		/* Check escaped reading */
		CDecodingXMLReader r = new CDecodingXMLReader(
					new StringReader("&gt;&lt;&amp;"),10,100, SXMLSettings.LONG_BARE
					);
		Assert.assertTrue(-'>'-1==r.readBodyChar());
		Assert.assertTrue(-'<'-1==r.readBodyChar());
		Assert.assertTrue(-'&'-1==r.readBodyChar());
		try{
			r.readBodyChar(); Assert.fail();
			}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testDecodingStandardEscapesUnknown()throws IOException
	{
		enter();
		/* Check escaped reading */
		CDecodingXMLReader r = new CDecodingXMLReader(
					new StringReader("&gt;&lt;&ampere;"),10,100, SXMLSettings.LONG_BARE
					);
		Assert.assertTrue(-'>'-1==r.readBodyChar());
		Assert.assertTrue(-'<'-1==r.readBodyChar());
		try{
			r.readBodyChar(); Assert.fail();
			}catch(EBrokenFormat ex){};
		
		leave();
	};
};