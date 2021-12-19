package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.util.CEofInjectingSequenceReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;
import org.junit.Assert;

public class TestCXMLWhitespaceNormalizingFilter extends sztejkat.utils.test.ATest
{
	private void testWithEof(String [] texts, String expected)throws IOException
	{
		enter();
		Reader [] readers = new Reader[texts.length];
		for(int i=0;i<texts.length;i++)
		{
			readers[i]=texts[i]!=null ? new StringReader(texts[i]) : null;
		};
		CXMLWhitespaceNormalizingFilter f = new CXMLWhitespaceNormalizingFilter(
									new CEofInjectingSequenceReader(
									//Note: very sub-optimial, but acceptable in tests.
											java.util.Arrays.asList(readers).iterator()
											)
									);
		/* Note:
			The nature of AAdaptiveFilterReader is such, that after encountering
			first partial read it will re-try and will report partial read itself
			only if AAdaptiveFilterReader.filter() did not fill buffer with any data
			at all.
			
			Thous if there is a single eof it is getting stitched.
			So we try up to 5 stitches before returning.
		*/ 
		StringBuilder sb = new StringBuilder();
		int cont_eofs =0;
		for(;;)
		{ 
			char [] c= new char[expected.length()*2];
			int r = f.read(c,0,c.length);
			if (r>0)
			{
				cont_eofs =0;		
				sb.append(c, 0, r);
			}else
			{
				if (cont_eofs==5) break;
				cont_eofs++;
				System.out.println("stitching...");
			};
		};		
		String s = sb.toString();
		System.out.println(s);		
		Assert.assertTrue(expected.equals(s));		
		leave();
	};
	
	@Test public void testNoNormalization()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "ruskie<voter>maklemburg"},"ruskie<voter>maklemburg");
		leave();
	};
	@Test public void testAfterTagNormalization_1()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "ruskie<voter> maklemburg"},"ruskie<voter>maklemburg");
		leave();
	};
	@Test public void testAfterTagNormalization_2()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "ruskie<voter> \t\nmaklemburg"},"ruskie<voter>maklemburg");
		leave();
	};
	@Test public void testBeforTagNormalization_1()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "ruskie<voter>maklemburg <marcie>"},"ruskie<voter>maklemburg<marcie>");
		leave();
	};
	@Test public void testBeforTagNormalization_2()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "ruskie<voter>maklemburg \n<marcie>"},"ruskie<voter>maklemburg<marcie>");
		leave();
	};
	@Test public void testBeforInsideBodyNormalization_1()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "ruskie<voter>maklemburg stinking<marcie>"},"ruskie<voter>maklemburg stinking<marcie>");
		leave();
	};
	@Test public void testBeforInsideBodyNormalization_2()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "ruskie<voter>maklemburg\t   \tstinking<marcie>"},"ruskie<voter>maklemburg stinking<marcie>");
		leave();
	};
	
	@Test public void testTrailingTagNormalization_2()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "ruskie<voter  >stinking"},"ruskie<voter>stinking");
		leave();
	};
	
	@Test public void testLeadingTagNormalization_1()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "  <voter  >stinking"},"<voter>stinking");
		leave();
	};
	
	@Test public void testLeadingNormalization_1()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "\truskie<voter  >stinking"}," ruskie<voter>stinking");
		leave();
	};
	@Test public void testLeadingNormalization_2()throws IOException
	{
		enter();
		testWithEof( new String[]
						{ "\t\truskie<voter  >stinking"}," ruskie<voter>stinking");
		leave();
	};
};