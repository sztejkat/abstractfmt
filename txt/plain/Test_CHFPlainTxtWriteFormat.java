package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.ATxtWriteFormat0;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.ATest;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import org.junit.Test;
import org.junit.Assert;

/**
	A manual test for {@link CHFPlainTxtWriteFormat} validating
	produced content against well known and crafted code.
	Validates what is not tested in {@link CPlainTxtWriteFormat}
	
*/
public class Test_CHFPlainTxtWriteFormat extends ATest
{
	@Test public void testIndentationFlat()throws IOException
	{
			enter();
			StringWriter ow = new StringWriter();
			CHFPlainTxtWriteFormat w = new CHFPlainTxtWriteFormat(ow,4);
		
			
			w.open();
			w.begin("A");
			w.end();
			w.begin("B");
			w.end();
			w.begin("C");
			w.end();
			w.close();
		
			String o = ow.toString();
			System.out.println(o);
			
				
			final String expected="\n*A;\n*B;\n*C;";
			System.out.println(expected);
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
	@Test public void testIndentationNested()throws IOException
	{
			enter();
			StringWriter ow = new StringWriter();
			CHFPlainTxtWriteFormat w = new CHFPlainTxtWriteFormat(ow,4);
		
			
			w.open();
			w.begin("A");			
			w.begin("B");
			w.begin("C");
			w.begin("D");
			w.end();
			w.end();
			w.end();
			w.begin("Z");
			w.end();
			w.end();
			w.close();
		
			String o = ow.toString();
			System.out.println(o);
			
				
			final String expected="\n*A"+
								  "\n *B"+
								  "\n  *C"+
								  "\n   *D;;;"+
								  "\n *Z;;";
			System.out.println(expected);
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
	
	@Test public void testIndentationWithElements()throws IOException
	{
			enter();
			StringWriter ow = new StringWriter();
			CHFPlainTxtWriteFormat w = new CHFPlainTxtWriteFormat(ow,4);
		
			
			w.open();
			w.begin("A");			
			w.writeInt(3);w.writeInt(2);w.writeInt(1);
			w.end();
			w.begin("A");			
			w.writeInt(3);w.writeInt(2);w.writeInt(1);
			w.end();
			w.close();
		
			String o = ow.toString();
			System.out.println(o);
			
				
			final String expected="\n*A 3,2,1;"+
			                      "\n*A 3,2,1;";
			System.out.println(expected);
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
	
	@Test public void testIndentationWithNestedElements()throws IOException
	{
			enter();
			StringWriter ow = new StringWriter();
			CHFPlainTxtWriteFormat w = new CHFPlainTxtWriteFormat(ow,4);
		
			
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
			System.out.println(o);
			
				
			final String expected="\n*Z"+
								  "\n *A 3,2,1"+
								  "\n  *B 3,2,1;"+
								  "\n  3,2,1;;";
			System.out.println(expected);
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
	
	@Test public void testIndentationNestedSaturated()throws IOException
	{
			enter();
			StringWriter ow = new StringWriter();
			CHFPlainTxtWriteFormat w = new CHFPlainTxtWriteFormat(ow,2);
		
			
			w.open();
			w.begin("A");			
			w.begin("B");
			w.begin("C");
			w.begin("D");
			w.end();
			w.end();
			w.end();
			w.begin("Z");
			w.end();
			w.end();
			w.close();
		
			String o = ow.toString();
			System.out.println(o);
			
				
			final String expected="\n*A"+
								  "\n *B"+
								  "\n  *C"+
								  "\n  *D;;;"+ //D is not indented any more.
								  "\n *Z;;";
			System.out.println(expected);
			Assert.assertTrue(expected.equals(o));
		
			leave();
	};
};