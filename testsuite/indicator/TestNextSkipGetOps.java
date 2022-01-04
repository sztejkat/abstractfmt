package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.testsuite.*;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
import java.io.IOException;
import java.util.Random;
/**
	A test which check if skip, next, read and getIndicator
	are stable operations.
*/
public class TestNextSkipGetOps extends AIndicatorTest
{
	@Test public void testGetIndicatorStability()throws IOException
	{
		/* 
			We check if getIndicator returns stable results regardless
			of how many times it is called and if next moves to
			subsequent indicator.	
			
			We do it without any data elements.
		*/
		enter();
		Pair p = create();
		
		p.write.open();			
		p.write.writeBeginDirect("A");
		p.write.writeEnd();		
		p.write.writeBeginDirect("B");
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("B".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
	
	@Test public void testSkip()throws IOException
	{
		/* 
			We check if getIndicator returns stable results regardless
			of how many times it is called and if next moves to
			subsequent indicator, but skip() does not move off the
			indicator.	
			
			We do it without any data elements.
		*/
		enter();
		Pair p = create();
		
		p.write.open();			
		p.write.writeBeginDirect("A");
		p.write.writeEnd();		
		p.write.writeBeginDirect("B");
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		p.read.skip();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		p.read.skip();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.END);
		p.read.skip();
		assertGetIndicator(p.read,TIndicator.END);
		p.read.skip();
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		p.read.skip();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		p.read.skip();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("B".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.END);
		p.read.skip();
		assertGetIndicator(p.read,TIndicator.END);
		p.read.skip();
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
};