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
	as expected in more complex cases.
*/
public class TestNestedSignalOperations extends AIndicatorTest
{
	private static String mkName( int i, int Lmax)
	{
		int L = Math.min(i,Lmax);
		StringBuilder sb = new StringBuilder();
		for(int j=0;j<L; j++) sb.append((char)j);
		return sb.toString();
	};
	@Test public void testNestedOperations()throws IOException
	{
		/* 
			Test if we can perform nested operations.
			
			In this we basically nest large number of signals
			without any data inside.
		*/
		enter();
		Pair p = create();
		
		int Lmax = Math.min(2048, p.write.getMaxSupportedSignalNameLength());		
		Assume.assumeTrue(Lmax>0);
		
		p.write.open();			
		for(int i =0;i<8192; i++)
		{
			p.write.writeBeginDirect(mkName(i,Lmax));		
		};
		for(int i =0;i<8192; i++)
		{
			p.write.writeEnd();
		};		
		p.write.flush();
		p.write.close();
		
		//need to set up limit which may be above default 1024 size.
		p.read.setMaxSignalNameLength(Lmax);
		p.read.open();
		
		for(int i =0;i<8192; i++)
		{
			assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
			Assert.assertTrue(mkName(i,Lmax).equals(p.read.getSignalName()));
		};
		for(int i =0;i<8192; i++)
		{
			assertReadIndicator(p.read,TIndicator.END);
		};
		assertReadIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
};