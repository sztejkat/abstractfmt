package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.ATest;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
import java.util.NoSuchElementException;
import java.io.*;
import org.junit.Test;
import org.junit.Assert;
/**
	Test for {@link ATxtReadFormatSupport}
*/
public class Test_ATxtReadFormatSupport extends ATest
{
		private static final class DUT extends ATxtReadFormatSupport<String,String>
		{
				public DUT(Reader in){ super(in); };
				public CAdaptivePushBackReader i(){ return in; };
				@Override protected void toNextChar()throws IOException{ throw new AbstractMethodError();};
				@Override protected String getNextSyntaxElement(){ throw new AbstractMethodError();};
				@Override protected int getNextChar(){throw new AbstractMethodError();};
		};
		
	@Test public void checkIfReaderIsAttached()throws IOException
	{
		enter();
		StringReader r = new StringReader("ally");
		DUT d = new DUT(r);
		Assert.assertTrue(d.i().read()=='a');
		leave();
	};
	@Test public void checkIfReaderIsClose()throws IOException
	{
		enter();
		StringReader r = new StringReader("ally");
		DUT d = new DUT(r);
		d.close();
		try{	
				d.i().read();
				Assert.fail();
		}catch(IOException ex){ System.out.println(ex); }
		leave();
	};
	@Test public void checkIfSyntaxIsPushedAndPopped()throws IOException
	{
		enter();
		StringReader r = new StringReader("ally");
		DUT d = new DUT(r);
		d.pushSyntax("marcie");
		d.pushSyntax("darcie");
		Assert.assertTrue("darcie".equals(d.popSyntax()));
		Assert.assertTrue("marcie".equals(d.peekSyntax()));
		Assert.assertTrue("marcie".equals(d.popSyntax()));
		Assert.assertTrue(null==d.peekSyntax());
		try{
			d.popSyntax();
			Assert.fail();
		}catch(NoSuchElementException ex){};
		leave();
	};
	@Test public void checkIfSyntaxStackLimitWorks()throws IOException
	{
		enter();
		StringReader r = new StringReader("ally");
		DUT d = new DUT(r);
		d.setSyntaxStackLimit(2);
		d.pushSyntax("marcie");
		d.pushSyntax("darcie");
		d.popSyntax();
		d.pushSyntax("darcie");
		try{
			d.pushSyntax("darcie");
			Assert.fail();
		}catch(EFormatBoundaryExceeded ex){};
		leave();
	};
	@Test public void checkIfSyntaxStackLimitWorks_during_set()throws IOException
	{
		enter();
		StringReader r = new StringReader("ally");
		DUT d = new DUT(r);		
		d.pushSyntax("marcie");
		d.pushSyntax("darcie");
		d.pushSyntax("darcie");
		try{
			d.setSyntaxStackLimit(2);
			Assert.fail();
		}catch(IllegalStateException ex){};
		leave();
	};
}; 