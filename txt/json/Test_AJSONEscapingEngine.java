package sztejkat.abstractfmt.txt.json;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;
import org.junit.Assert;
/**
	Test for {@link AJSONEscapingEngine}.
*/
public class Test_AJSONEscapingEngine extends sztejkat.abstractfmt.test.ATest
{
			static class DUT extends AJSONEscapingEngine
			{
					
					final StringWriter o = new StringWriter();
					
				@Override protected void out(char c)throws IOException{ o.write(c); };
					
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
			
			Assert.assertTrue("\\uD801".equals(y));
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
			
			Assert.assertTrue("a\\uD801".equals(y));
		leave();
	};
	
	@Test public void testEscapesQuotation()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append("\"zoo");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("\\\"zoo".equals(y));
		leave();
	};
	
	
	@Test public void testEscapesSlash()throws IOException
	{
		enter();
			DUT d = new DUT();
			
			d.append("a\\moon");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("a\\\\moon".equals(y));
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
			
			Assert.assertTrue("\\uDCFE\\uDCFF".equals(y));
		leave();
	};
	
	@Test public void test_controls_escaped_1()throws IOException
	{
		enter();
		
			DUT d = new DUT();
			d.append("\u0000\u0001\u0002\u0003");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("\\u0000\\u0001\\u0002\\u0003".equals(y));
		leave();
	};
	
	@Test public void test_controls_escaped_2()throws IOException
	{
		enter();
		
			DUT d = new DUT();
			d.append("\u0004\u0005\u0006\u0007");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			Assert.assertTrue("\\u0004\\u0005\\u0006\\u0007".equals(y));
		leave();
	};
	
	@Test public void test_controls_escaped_3()throws IOException
	{
		enter();
		
			DUT d = new DUT();
			d.append((char)0x08);
			d.append((char)0x09);
			d.append((char)0x0A);
			d.append((char)0x0B);
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("\\b\\t\n\\u000B".equals(y));
		leave();
	};
	@Test public void test_controls_escaped_4()throws IOException
	{
		enter();
		
			DUT d = new DUT();
			d.append((char)0x0C);
			d.append((char)0x0D);
			d.append((char)0x0E);
			d.append((char)0x0F);
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("\\f\\r\\u000E\\u000F".equals(y));
		leave();
	};
	@Test public void test_controls_escaped_5()throws IOException
	{
		enter();
		
			DUT d = new DUT();
			d.append("\u0010\u0011\u0012\u0013");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("\\u0010\\u0011\\u0012\\u0013".equals(y));
		leave();
	};
	@Test public void test_controls_escaped_6()throws IOException
	{
		enter();
		
			DUT d = new DUT();
			d.append("\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("\\u0014\\u0015\\u0016\\u0017\\u0018\\u0019\\u001A\\u001B\\u001C\\u001D\\u001E\\u001F".equals(y));
		leave();
	};
	@Test public void test_controls_escaped_7()throws IOException
	{
		enter();
		
			DUT d = new DUT();
			d.append("\u007F");
			d.flush();
			
			final String y = d.o.toString();
			System.out.println("\""+y+"\"");
			
			Assert.assertTrue("\\u007F".equals(y));
		leave();
	};
};