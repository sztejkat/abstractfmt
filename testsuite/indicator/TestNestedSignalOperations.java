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
		int rmax = 2048;
		
		p.write.open();			
		for(int i =0;i<rmax; i++)
		{
			p.write.writeBeginDirect(mkName(i,Lmax));		
		};
		for(int i =0;i<rmax; i++)
		{
			p.write.writeEnd();
		};		
		p.write.flush();
		p.write.close();
		
		//need to set up limit which may be above default 1024 size.
		p.read.setMaxSignalNameLength(Lmax);
		p.read.open();
		
		for(int i =0;i<rmax; i++)
		{
			assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
			Assert.assertTrue(mkName(i,Lmax).equals(p.read.getSignalName()));
		};
		for(int i =0;i<rmax; i++)
		{
			assertReadIndicator(p.read,TIndicator.END);
		};
		assertReadIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
	
	@Test public void testNestedRegister()throws IOException
	{
		/* 
			Test if we can perform nested operations
			but using a register operations up to 
			allowed limit.			
		*/
		enter();
		Pair p = create();
		
		int Lmax = Math.min(2048, p.write.getMaxSupportedSignalNameLength());		
		Assume.assumeTrue(Lmax>0);		
		Assume.assumeTrue(p.write.getMaxRegistrations()>0);
		
		int rmax= Math.min(1024,p.write.getMaxRegistrations());
		
		p.write.open();			
		for(int i =0;i<rmax; i++)
		{
			p.write.writeBeginRegister(mkName(i,Lmax),i);		
		};
		for(int i =0;i<rmax; i++)
		{
			p.write.writeEnd();
		};
		//Now make use of registered names.
		for(int i =0;i<rmax; i++)
		{
			p.write.writeBeginUse(i);		
		};
		for(int i =0;i<rmax; i++)
		{
			p.write.writeEnd();
		};
		p.write.flush();
		p.write.close();
		
		//need to set up limit which may be above default 1024 size.
		p.read.setMaxSignalNameLength(Lmax);
		p.read.open();
		
		
		for(int i =0;i<rmax; i++)
		{
			assertReadIndicator(p.read,TIndicator.BEGIN_REGISTER);
			Assert.assertTrue(mkName(i,Lmax).equals(p.read.getSignalName()));
			Assert.assertTrue(p.read.getSignalNumber()==i);
		};
		for(int i =0;i<rmax; i++)
		{
			assertReadIndicator(p.read,TIndicator.END);
		};
		//Now make use of registered names.
		for(int i =0;i<rmax; i++)
		{
			assertReadIndicator(p.read,TIndicator.BEGIN_USE);
			Assert.assertTrue(p.read.getSignalNumber()==i);	
		};
		for(int i =0;i<rmax; i++)
		{
			assertReadIndicator(p.read,TIndicator.END);
		};
		assertReadIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
	
	
	
	@Test public void testChainRegister()throws IOException
	{
		/* 
			Test if we can perform chained operations
			but using a register operations up to 
			allowed limit.			
		*/
		enter();
		Pair p = create();
		
		int Lmax = Math.min(2048, p.write.getMaxSupportedSignalNameLength());		
		Assume.assumeTrue(Lmax>0);		
		Assume.assumeTrue(p.write.getMaxRegistrations()>0);
		
		int rmax= Math.min(1024,p.write.getMaxRegistrations());
		
		p.write.open();			
		p.write.writeBeginDirect("A");
		for(int i =0;i<rmax; i++)
		{
			p.write.writeEndBeginRegister(mkName(i,Lmax),i);		
		};
		p.write.writeEndBeginDirect("A");
		for(int i =0;i<rmax; i++)
		{
			p.write.writeEndBeginUse(i);		
		};
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		//need to set up limit which may be above default 1024 size.
		p.read.setMaxSignalNameLength(Lmax);
		p.read.open();
		
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		for(int i =0;i<rmax; i++)
		{
			assertReadOptIndicator(p.read,TIndicator.END_BEGIN_REGISTER);
			Assert.assertTrue(mkName(i,Lmax).equals(p.read.getSignalName()));
			Assert.assertTrue(p.read.getSignalNumber()==i);
		};		
		assertReadOptIndicator(p.read,TIndicator.END_BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		//Now make use of registered names.
		for(int i =0;i<rmax; i++)
		{
			assertReadOptIndicator(p.read,TIndicator.END_BEGIN_USE);
			Assert.assertTrue(p.read.getSignalNumber()==i);	
		};
		assertReadIndicator(p.read,TIndicator.END);
		assertReadIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
	
	
	
	
	@Test public void testChainDirect()throws IOException
	{
		/* 
			Test if we can perform chained operations
			but using a direct operations
		*/
		enter();
		Pair p = create();
		
		int Lmax = Math.min(1024, p.write.getMaxSupportedSignalNameLength());		
		Assume.assumeTrue(Lmax>0);	
		int rmax = Lmax;
		
		p.write.open();			
		p.write.writeBeginDirect("A");
		for(int i =0;i<rmax; i++)
		{
			p.write.writeEndBeginDirect(mkName(i,Lmax));		
		};		
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		//need to set up limit which may be above default 1024 size.
		p.read.setMaxSignalNameLength(Lmax);
		p.read.open();
		
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		for(int i =0;i<rmax; i++)
		{
			//Not every stream will chain it with optimization, so:
			assertReadOptIndicator(p.read,TIndicator.END_BEGIN_DIRECT);
			Assert.assertTrue(mkName(i,Lmax).equals(p.read.getSignalName()));
		};		
		assertReadIndicator(p.read,TIndicator.END);
		assertReadIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
};