package sztejkat.abstractfmt.txt.json;
import sztejkat.abstractfmt.test.ATest;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import org.junit.Test;
import org.junit.Assert;

/**
	A manual test for {@link CHFJSONWriteFormat} validating
	produced content against well known and crafted code.
	Validates what is not tested in {@link CJSONWriteFormat}
	
*/
public class Test_CHFJSONWriteFormat extends ATest
{
	@Test public void testIndentationFlat()throws IOException
	{
			enter();
			StringWriter ow = new StringWriter();
			CHFJSONWriteFormat w = new CHFJSONWriteFormat(ow,4);
		
			
			w.open();
			w.begin("A");
			w.writeInt(777);
			w.end();
			w.begin("B");
			w.end();
			w.begin("C");
			w.end();
			w.close();
		
			String o = ow.toString();
			System.out.println("----------------");
			System.out.println(o);
			System.out.println("----------------");
			
				
			final String expected=
"[\n"+
"{\"A\":777\n"+
"},\n"+
"{\"B\":[]\n"+
"},\n"+
"{\"C\":[]\n"+
"}]";
			System.out.println("----------------");
			System.out.println(expected);
			System.out.println("----------------");
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
	@Test public void testIndentationNested()throws IOException
	{
			enter();
			StringWriter ow = new StringWriter();
			CHFJSONWriteFormat w = new CHFJSONWriteFormat(ow,4);
		
			
			w.open();
			w.begin("A");
			w.writeChar('c');
			w.begin("B");
			w.begin("C");
			w.writeChar('c');
			w.writeChar('c');
			w.writeChar('c');
			w.begin("D");
			w.end();
			w.end();
			w.end();
			w.begin("Z");
			w.end();
			w.end();
			w.close();
		
			String o = ow.toString();
			System.out.println("----------------");
			System.out.println(o);
			System.out.println("----------------");
			
				
			final String expected=
"[\n"+			
"{\"A\":[\"c\",\n"+
" {\"B\":[\n"+
"  {\"C\":[\"c\",\"c\",\"c\",\n"+
"   {\"D\":[]\n"+
"   }]\n"+
"  }]\n"+
" },\n"+
" {\"Z\":[]\n"+
" }]\n"+
"}]";		
			System.out.println("----------------");
			System.out.println(expected);
			System.out.println("----------------");
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
	
	@Test public void testIndentationWithElements()throws IOException
	{
			enter();
			StringWriter ow = new StringWriter();
			CHFJSONWriteFormat w = new CHFJSONWriteFormat(ow,4);
		
			
			w.open();
			w.begin("A");			
			w.writeInt(3);w.writeInt(2);w.writeInt(1);
			w.end();
			w.begin("A");			
			w.writeInt(3);w.writeInt(2);w.writeInt(1);
			w.end();
			w.close();
		
			String o = ow.toString();
			System.out.println("----------------");
			System.out.println(o);
			System.out.println("----------------");
			
				
			final String expected=
"[\n"+
"{\"A\":[3,2,1]\n"+
"},\n"+
"{\"A\":[3,2,1]\n"+
"}]";		
			System.out.println("----------------");
			System.out.println(expected);
			System.out.println("----------------");
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
	
	@Test public void testIndentationWithNestedElements()throws IOException
	{
			enter();
			StringWriter ow = new StringWriter();
			CHFJSONWriteFormat w = new CHFJSONWriteFormat(ow,4);
		
			
			w.open();
			w.begin("Z");			
			w.begin("A");			
			w.writeInt(3);w.writeInt(2);w.writeInt(1);
				w.begin("B");
				w.writeInt(3);w.writeInt(2);w.writeInt(1);
				w.end();
				w.writeInt(3);w.writeInt(2);w.writeInt(1);
			w.end();
			w.end();
			w.close();
		
			String o = ow.toString();
			System.out.println("----------------");
			System.out.println(o);
			System.out.println("----------------");
			
				
			final String expected=
"[\n"+
"{\"Z\":[\n"+
" {\"A\":[3,2,1,\n"+
"  {\"B\":[3,2,1]\n"+
"  },3,2,1]\n"+
" }]\n"+
"}]";				
			System.out.println("----------------");
			System.out.println(expected);
			System.out.println("----------------");
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
};