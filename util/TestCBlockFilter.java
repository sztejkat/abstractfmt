package sztejkat.abstractfmt.util;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;

public class TestCBlockFilter extends sztejkat.utils.test.ATest
{
	/* ****************************************************
		
				Tests without handling premature
				END-OF-FILE conditions.
	
	******************************************************/
	private void testNoEof(String text, String expected)throws IOException
	{
		enter();
			CBlockFilter f = new CBlockFilter(
										new StringReader(text),
										"<!--","-->"
										);
		char [] c= new char[expected.length()*2];
		int r = f.read(c,0,c.length);		
		String s = new String(c,0,r);		
		System.out.println(s);		
		Assert.assertTrue(expected.equals(s));		
		leave();
	};
	@Test public void testNoSkip()throws IOException
	{
		enter();
		/*
			Check if nothin is skipped, even tough partial
			matches are possible.
		*/
		testNoEof("Volkov <!-James","Volkov <!-James");
		leave();
	};
	
	@Test public void testSimpleSkip_1()throws IOException
	{
		enter();
		/*
			Check if data are skipped
		*/
		testNoEof("Volkov <!--James-->","Volkov ");
		
		leave();
	};
	@Test public void testAdjacentSkip_2()throws IOException
	{
		enter();
		/*
			Check if data are skipped
		*/
		testNoEof("Volkov <!--James--><!--hansen-->Markuc","Volkov Markuc");
		
		leave();
	};
	@Test public void testAdjacentSkip_3()throws IOException
	{
		enter();
		/*
			Check if data are skipped
		*/
		testNoEof("Volkov <!--James-->XANTH<!--hansen-->Markuc","Volkov XANTHMarkuc");
		
		leave();
	};
	
	@Test public void testMissingEndSkip_1()throws IOException
	{
		enter();
		/*
			Check if data are skipped
		*/
		testNoEof("Volkov <!--James-- <!--hansen--Markuc","Volkov ");
		
		leave();
	};
	
	
	/* ****************************************************
		
				Tests WITH  premature
				END-OF-FILE conditions.
	
	******************************************************/
	private void testWithEof(String [] texts, String expected)throws IOException
	{
		enter();
		Reader [] readers = new Reader[texts.length];
		for(int i=0;i<texts.length;i++)
		{
			readers[i]=texts[i]!=null ? new StringReader(texts[i]) : null;
		};
		CBlockFilter f = new CBlockFilter(
									new CEofInjectingSequenceReader(
									//Note: very sub-optimial, but acceptable in tests.
											java.util.Arrays.asList(readers).iterator()
											),
									"<!--","-->"
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
	@Test public void testNoSkip_Eof()throws IOException
	{
		enter();
		/*
			Check if nothing is skipped with eofs which
			are not getting through filter.
		*/
		testWithEof(
			new String[]{
						"Vol",
						"kov Jam",
						"es"},"Volkov James");
		leave();
	};
	
	@Test public void testNoSkip_Eof2()throws IOException
	{
		enter();
		/*
			Check if nothing is skipped with eofs which
			are getting through filter.
		*/
		testWithEof(
			new String[]{
						"Vol",null,
						"kov Jam",null,
						"es",null,null},"Volkov James");
		leave();
	};
	
	
	@Test public void testSkip_Eof()throws IOException
	{
		enter();
		/*
			Check if nothing is skipped with eofs which
			are not getting through filter.
		*/
		testWithEof(
			new String[]{
						"Vol","kov <!","-","- comment -->Marcus"
						},"Volkov Marcus");
		leave();
	};
	
	@Test public void testSkip_Eof2()throws IOException
	{
		enter();
		/*
			Check if nothing is skipped with eofs which
			are not getting through filter.
		*/
		testWithEof(
			new String[]{
						"Vol","kov <!","-","- comment -","-><!--joe-->Marcus"
						},"Volkov Marcus");
		leave();
	};
	@Test public void testSkip_Eof3()throws IOException
	{
		enter();
		/*
			Check if nothing is skipped with eofs which
			are not getting through filter.
		*/
		testWithEof(
			new String[]{
						"Vol","kov <!",null,"-",null,"- comment -",null,"-><!--joe-->Marcus"
						},"Volkov Marcus");
		leave();
	};
};