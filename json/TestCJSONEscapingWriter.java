package sztejkat.abstractfmt.json;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import org.junit.Test;
import org.junit.Assert;

/**
	A test bed for {@link CJSONEscapingWriter}
*/
public class TestCJSONEscapingWriter extends sztejkat.utils.test.ATest
{
	@Test public void testNotEscaped()throws IOException
	{
		enter();
		
			StringWriter s = new StringWriter();
			CJSONEscapingWriter w = new CJSONEscapingWriter(s, null);
			
			w.write("123456789");
			w.close();
			
			Assert.assertTrue("123456789".equals(s.toString()));
			
		
		leave();
	};
	
	@Test public void testNotEscapedStringMode()throws IOException
	{
		enter();
		
			StringWriter s = new StringWriter();
			CJSONEscapingWriter w = new CJSONEscapingWriter(s, null);
			
			w.writeString("123456789");
			w.close();
			
			Assert.assertTrue("123456789".equals(s.toString()));
			
		
		leave();
	};
	
	@Test public void testCharsetEscaped()throws IOException
	{
		enter();
		
			StringWriter s = new StringWriter();
			CJSONEscapingWriter w = new CJSONEscapingWriter(s, Charset.forName("ASCII"));
			
			w.writeString("p");
			w.writeString((char)0xABCD);
			w.writeString((char)0xABCE);
			w.close();
			
			String y = s.toString();
			System.out.println(y);
			Assert.assertTrue("p\\uABCD\\uABCE".equals(y));
			
		
		leave();
	};
	
	@Test public void testCharsetEscaped_2()throws IOException
	{
		enter();
		
			StringWriter s = new StringWriter();
			CJSONEscapingWriter w = new CJSONEscapingWriter(s, Charset.forName("ASCII"));
			
			w.writeString("p\"\n\r\t");;
			w.close();
			
			String y = s.toString();
			System.out.println(y);
			Assert.assertTrue("p\\\"\\n\\r\\t".equals(y));
			
		
		leave();
	};
	
	@Test public void testZoneEscaped_1()throws IOException
	{
		enter();
		
			StringWriter s = new StringWriter();
			CJSONEscapingWriter w = new CJSONEscapingWriter(s, Charset.forName("UTF-8"));
			
			w.writeString("p\r\n\t");
			w.write("\n\t");
			w.close();
			
			String y = s.toString();
			System.out.println(y);
			Assert.assertTrue("p\\r\\n\\t\n\t".equals(y));
			
		
		leave();
	};
	
	@Test public void testZoneEscaped_2()throws IOException
	{
		enter();
		
			StringWriter s = new StringWriter();
			CJSONEscapingWriter w = new CJSONEscapingWriter(s, Charset.forName("UTF-8"));
			
			for(int i = 0;i<=32;i++)
			{
				w.writeString((char)i);
			};
			w.close();
			
			String y = s.toString();
			System.out.println(y);
			String expected = "\\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\\b"+
						      "\\t\\n\\u000B\\f\\r\\u000E\\u000F"+
						      "\\u0010\\u0011\\u0012\\u0013\\u0014\\u0015\\u0016"+
						      "\\u0017\\u0018\\u0019\\u001A\\u001B\\u001C\\u001D\\u001E\\u001F ";
			Assert.assertTrue(expected.equals(y));
			
		
		leave();
	};
	
	@Test public void testZoneEscaped_3()throws IOException
	{
		enter();
		
			StringWriter s = new StringWriter();
			CJSONEscapingWriter w = new CJSONEscapingWriter(s, Charset.forName("UTF-8"));
			
			w.write('\"');
			w.writeString("Malinkaja \"dievoczka\" \\ tuze malczik");
			w.write("\"");
			w.close();
			
			String y = s.toString();
			System.out.println(y);
			String expected = "\"Malinkaja \\\"dievoczka\\\" \\\\ tuze malczik\"";
			Assert.assertTrue(expected.equals(y));
			
		
		leave();
	};
	
	
	@Test public void testIndexed()throws IOException
	{
		enter();
		
			StringWriter s = new StringWriter();
			CJSONEscapingWriter w = new CJSONEscapingWriter(s, Charset.forName("UTF-8"));
			
			w.writeString("AR\"KA",0,5);
			w.close();
			
			String y = s.toString();
			System.out.println(y);
			String expected = "AR\\\"KA";
			Assert.assertTrue(expected.equals(y));
			
		
		leave();
	};
};