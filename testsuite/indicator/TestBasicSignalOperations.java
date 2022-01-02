package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.testsuite.*;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
import java.io.IOException;
/**
	A test which check if signal related operations do work
	as expected in basic cases.
*/
public class TestBasicSignalOperations extends AIndicatorTest
{
	private void testSingleDirectBegin(String name)throws IOException
	{
		/* Test if direct begin works */
		enter("testSingleDirectBegin(name="+name+")");
		Pair p = create();
		
		Assume.assumeTrue(p.write.getMaxSupportedSignalNameLength()>=name.length());
		
		p.write.open();
			
			p.write.writeBeginDirect(name);
			p.write.writeEnd();
		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue(name.equals(p.read.getSignalName()));
		
		assertReadIndicator(p.read,TIndicator.END);
		assertReadIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	@Test public void testSingleDirectBeginEmpty()throws IOException
	{
		enter();
		testSingleDirectBegin("");
		leave();
	};
	@Test public void testSingleDirectBeginPlain()throws IOException
	{
		enter();
		testSingleDirectBegin("anice");
		leave();
	};
	@Test public void testSingleDirectBeginStrange1()throws IOException
	{
		enter();
		testSingleDirectBegin(new String(new char[]{'_',(char)0,(char)0x3490,(char)0xffff,(char)0x001,(char)0x011,(char)0x111}));
		leave();
	};
	@Test public void testSingleDirectBeginStrange2()throws IOException
	{
		enter();
		testSingleDirectBegin(new String("&^%!@#$<>"));
		leave();
	};
};