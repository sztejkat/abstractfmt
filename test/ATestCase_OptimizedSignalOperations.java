package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
/**
	A test case running signal operations
	with names optimization.
*/
public class ATestCase_OptimizedSignalOperations extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
	/**
		Test flat sequence of structures, using generated
		names in a number which exceed optimization
		or about 1000, whichever is less.
	@throws IOException .
	*/
	@Test public void testFlatOptimizedNames()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();//make sure it is allowed.
			//first run registration stage
			int names = 0;
			for(;names<1000;names++)
			{
				String n = "larkis_"+names; 
				if (!w.optimizeBeginName(n)) break;
			};
			//Now write those names plus some.
			for(int i=0;i<names+10;i++)
			{
				String n = "larkis_"+i; 
				w.begin(n);
				w.end();
			};
			w.close();
			
			//and read it back
			r.open();
			for(int i=0;i<names+10;i++)
			{
				String n = "larkis_"+i; 
				Assert.assertTrue(n.equals(r.next()));
				Assert.assertTrue(null==r.next());	
			};			
			r.close();
	};
	
	
	/**
		Test nested sequence of structures, using generated
		names in a number which exceed optimization
		or about 1000, whichever is less.
	@throws IOException .
	*/
	@Test public void testNestedOptimizedNames()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();//make sure it is allowed.
			//first run registration stage
			int names = 0;
			for(;names<1000;names++)
			{
				String n = "larkis_"+names; 
				if (!w.optimizeBeginName(n)) break;
			};
			final int to_generate = names+10;
			//test if it can happen?
			Assume.assumeTrue(
						(w.getMaxSupportedStructRecursionDepth()==-1)
						||
						(w.getMaxSupportedStructRecursionDepth()>to_generate)
						);
			if ((w.getMaxStructRecursionDepth()!=-1) && (w.getMaxStructRecursionDepth()<to_generate))
					w.setMaxStructRecursionDepth(to_generate);
			if ((r.getMaxStructRecursionDepth()!=-1) && (r.getMaxStructRecursionDepth()<to_generate))
					r.setMaxStructRecursionDepth(to_generate);
			//Now write those names plus some.
			for(int i=0;i<to_generate;i++)
			{
				String n = "larkis_"+i; 
				w.begin(n);
			};
			for(int i=0;i<to_generate;i++)
			{
				w.end();
			};
			w.close();
			
			//and read it back
			r.open();
			for(int i=0;i<to_generate;i++)
			{
				String n = "larkis_"+i; 
				Assert.assertTrue(n.equals(r.next()));
			}
			for(int i=0;i<to_generate;i++)
			{
				Assert.assertTrue(null==r.next());	
			};			
			r.close();
	};
};