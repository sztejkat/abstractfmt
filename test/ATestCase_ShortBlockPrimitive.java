package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.ITypedStructReadFormat;
import sztejkat.abstractfmt.ITypedStructWriteFormat;
import sztejkat.abstractfmt.EEof;
import sztejkat.abstractfmt.ENoMoreData;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
/**
	A test case running tests for short block primitives.
	<p>
	<i>Note: This is a pattern for all other primitives. Shame java has no
	preprocessor.</i>
*/
public class ATestCase_ShortBlockPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
	/** Computes block filled with predictable, fixed pattern
	@param length length of block
	@return new block with data */
	private short [] newBlock(int length)
	{
			short [] src = new short[length];
			int x = 0;
			for(int i=src.length;--i>=0;)
			{
				x = x*317+i;
				src[i] =(short)x;
			};
			return src;
	};
	private void assertArraysEqual(
							short [] A,int offA, int length,
							short [] B,int offB
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
		Test if short block can be written and read without 
		an enclosing structure, variant for un-typed stream.
	@throws IOException .
	*/
	@Test public void testWriteFlat_untyped()throws IOException
	{
			enter();
			
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			Assume.assumeFalse( r instanceof ITypedStructReadFormat);
			
			w.open();
			short [] written = newBlock(1024);
			w.writeShortBlock(written,7,900);
			w.close();
			
			
			r.open();
			short [] readen = new short[1001];
			int x = r.readShortBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			Assert.assertTrue(x==900);
			try{
				r.readShortBlock();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if zero size read returns 0 instead of -1.
	@throws IOException .
	*/
	@Test public void testZeroSizeReadGivesZero()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			
			w.open();
			short [] written = newBlock(5);
			w.writeShortBlock(written,0,5);
			w.close();
			
			
			r.open();
			short [] readen = new short[10];
			int x = r.readShortBlock(readen,0,0);
			Assert.assertTrue(x==0);
			r.close();			
			
			leave();
	};
	
	/**
		Test if short block can be written and read without 
		an enclosing structure, variant for typed stream.
	@throws IOException .
	*/
	@Test public void testWriteFlattyped()throws IOException
	{
			enter();
			
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			Assume.assumeTrue( r instanceof ITypedStructReadFormat);
			
			w.open();
			short [] written = newBlock(1024);
			w.writeShortBlock(written,7,900);
			w.close();
			
			
			r.open();
			short [] readen = new short[1001];
			int x = r.readShortBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.
			Assert.assertTrue(x==900);
			try{
				r.readShortBlock();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if short block can be written and read with
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
			short [] written = newBlock(1024);
			w.writeShortBlock(written,7,900);
			w.end();
			w.writeShort((short)44);
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			short [] readen = new short[1001];
			{
				int x = r.readShortBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			}
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readShortBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue(null==r.next());//consume end signal
			Assert.assertTrue(r.readShort()==(short)44);
			r.close();			
			
			leave();
	};   
	
	
	/**
		Test if short block can be written and read with
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
			short [] written = newBlock(1024);
			w.writeShortBlock(written,7,900);
			w.begin("ally");
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			short [] readen = new short[1001];
			{
				int x = r.readShortBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			};
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readShortBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue("ally".equals(r.next()));//consume begin signal
			r.close();			
			
			leave();
	};
}