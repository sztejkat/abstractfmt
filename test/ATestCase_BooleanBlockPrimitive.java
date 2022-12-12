package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EEof;
import sztejkat.abstractfmt.ENoMoreData;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running tests for boolean block primitives.
	<p>
	<i>Note: This is a pattern for all other primitives. Shame java has no
	preprocessor.</i>
*/
public class ATestCase_BooleanBlockPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
	/** Computes block filled with predictable, fixed pattern
	@param length length of block
	@return new block with data */
	private boolean [] newBlock(int length)
	{
			boolean [] src = new boolean[length];
			int x = 0;
			for(int i=src.length;--i>=0;)
			{
				x = x*37+i;
				src[i] = ((x & 0x01)!=0);
			};
			return src;
	};
	private void assertArraysEqual(
							boolean [] A,int offA, int length,
							boolean [] B,int offB
							)
	{
		for(int i =0;i<length; i++)
		{
			if (A[offA]!=B[offB])
			{
				Assert.fail("Arrays do differ at index "+i+" A["+offA+"]="+A[offA]+"!=B["+offB+"]="+B[offB]);
			};
			offA++;
			offB++;
		};
	};
							
	/**
		Test if boolean block can be written and read without 
		an enclosing structure.
	@throws IOException .
	*/
	@Test public void testWriteFlat()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			boolean [] written = newBlock(1024);
			w.writeBooleanBlock(written,7,900);
			w.close();
			
			
			r.open();
			boolean [] readen = new boolean[1001];
			int x = r.readBooleanBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			Assert.assertTrue(x==900);
			try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };			
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	
	
	/**
		Test if boolean block can be written and read with
		an enclosing structure.
	@throws IOException .
	*/
	@Test public void testWriteEnclosed()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.begin("aka");
			boolean [] written = newBlock(1024);
			w.writeBooleanBlock(written,7,900);
			w.end();
			w.writeBoolean(false);
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			boolean [] readen = new boolean[1001];
			{
				int x = r.readBooleanBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			}
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readBooleanBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue(null==r.next());//consume end signal
			Assert.assertTrue(r.readBoolean()==false);
			r.close();			
			
			leave();
	};   
	
	
	/**
		Test if boolean block can be written and read with
		an an inner struct terminating block
	@throws IOException .
	*/
	@Test public void testWriteInner()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.begin("aka");
			boolean [] written = newBlock(1024);
			w.writeBooleanBlock(written,7,900);
			w.begin("ally");
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			boolean [] readen = new boolean[1001];
			{
				int x = r.readBooleanBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			};
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readBooleanBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue("ally".equals(r.next()));//consume begin signal
			r.close();			
			
			leave();
	};
}