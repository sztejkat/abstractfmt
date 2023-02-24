package sztejkat.abstractfmt.txt.xml;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;
import org.junit.Assert;
/**
	Test for {@link AXMLElementNameEscapingEngine}.
*/
public class Test_AXMLElementNameEscapingEngine extends sztejkat.abstractfmt.test.ATest
{
			static class DUT extends AXMLElementNameEscapingEngine
			{
					
					final StringWriter o = new StringWriter();
					
				@Override protected void out(char c)throws IOException{ o.write(c); };
					private static final IXMLCharClassifier c = new CXMLChar_classifier_1_0_E4();
				@Override protected IXMLCharClassifier getClassifier(){ return c; };
			};
		
	@Test public void escapeEmpty()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append("");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("_".equals(y));
		leave();
	};
	@Test public void escapeEmptyNoWrite()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("_".equals(y));
		leave();
	};
	
	@Test public void noEmptyAtBadSurogate()throws IOException
	{
		enter();
			DUT d = new DUT();
			//This is a boundary condition, when we have 
			//a first character NOT written at flush,
			//but a pending surogate instead.
			d.append("\uD801");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("_D801".equals(y));
		leave();
	};
	
	@Test public void noEmptyAtDanglingBadSurogate()throws IOException
	{
		enter();
			DUT d = new DUT();
			//This is a boundary condition, when we have 
			//a first character written at flush,
			//and a pending surogate instead.
			d.append("a\uD801");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("a_D801".equals(y));
		leave();
	};
	
	@Test public void escapeEmptyIsReset()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append("");
			d.flush();
			d.reset();
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("__".equals(y));
		leave();
	};
	
	@Test public void test_if_escapes_differently_first_char()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append(".ma.2");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("_002Ema.2".equals(y));
		leave();
	};
	
	@Test public void test_if_escapes_differently_first_char_after_reset()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append(".ma.2");
			d.flush();
			d.reset();
			d.append("-ma-2");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("_002Ema.2_002Dma-2".equals(y));
		leave();
	};
	
	
	@Test public void test_surogateAtStart()throws IOException
	{
		enter();
			//Note: This surogate must represent a valid name char,
			//      so we need to force it to use XML 1.1 / 1.0E5
			//		since 1.0 E4 does not allow upper page chars in names.
			DUT d = new DUT()
			{
					private final IXMLCharClassifier c = new CXMLChar_classifier_1_1_E2();
				@Override protected IXMLCharClassifier getClassifier(){ return c; };
			};
			
			d.append("\uD830\uDC30<>");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("\uD830\uDC30_003C_003E".equals(y));
		leave();
	};
	
	@Test public void test_name_with_underscores()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append("_int_32");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("__int__32".equals(y));
		leave();
	};
	
	@Test public void test_bad_surogates()throws IOException
	{
		enter();
		
			DUT d = new DUT();
			
			d.append("\uDCFE\uDCFF");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("_DCFE_DCFF".equals(y));
		leave();
	};
};
	