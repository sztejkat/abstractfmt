package sztejkat.abstractfmt.txt.xml;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;
import org.junit.Assert;
/**
	Test for {@link AStringTokenEscapingEngine}.
*/
public class Test_AStringTokenEscapingEngine extends sztejkat.abstractfmt.test.ATest
{
			static final class DUT extends AStringTokenEscapingEngine
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
			
			Assert.assertTrue("".equals(y));
		leave();
	};
	
	@Test public void escape_tags()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append("mama & papa > granpa < granma");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("mama &amp; papa &gt; granpa &lt; granma".equals(y));
		leave();
	};
	
	@Test public void escape_unrecommended()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append("\u0008sorry\u0000");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("_0008sorry_0000".equals(y));
		leave();
	};
	@Test public void escape_underscore()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append("120_304");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("120__304".equals(y));
		leave();
	};
	
	@Test public void escape_doublequote()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append("\"ammy\"");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("&quot;ammy&quot;".equals(y));
		leave();
	};
	
};
	