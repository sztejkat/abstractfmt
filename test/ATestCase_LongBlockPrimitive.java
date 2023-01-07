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
	A test case running tests for long block primitives.
	<p>
	<i>Note: This is a pattern for all other primitives. Shame java has no
	preprocessor.</i>
*/
public class ATestCase_LongBlockPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
	/** Computes block filled with predictable, fixed pattern
	@param length length of block
	@return new block with data */
	private long [] newBlock(int length)
	{
			long [] src = new long[length];
			long x = 0;
			for(int i=src.length;--i>=0;)
			{
				x = x*33714091+i;
				src[i] =x;
			};
			return src;
	};
	private void assertArraysEqual(
							long [] A,int offA, int length,
							long [] B,int offB
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
		Test if long block can be written and read without 
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
			long [] written = newBlock(1024);
			w.writeLongBlock(written,7,900);
			w.close();
			
			
			r.open();
			long [] readen = new long[1001];
			int x = r.readLongBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			Assert.assertTrue(x==900);
			try{
				r.readLongBlock();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if long block can be written and read without 
		an enclosing structure, variant for un-typed stream.
	@throws IOException .
	*/
	@Test public void testWriteFlat_untyped_blk()throws IOException
	{
			enter();
			
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			Assume.assumeFalse( r instanceof ITypedStructReadFormat);
			
			w.open();
			long [] written = newBlock(1024);
			w.writeLongBlock(written,7,900);
			w.close();
			
			
			r.open();
			long [] readen = new long[1001];
			int x = r.readLongBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			Assert.assertTrue(x==900);
			try{
				r.readLongBlock(new long[1]);
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
			long [] written = newBlock(5);
			w.writeLongBlock(written,0,5);
			w.close();
			
			
			r.open();
			long [] readen = new long[10];
			int x = r.readLongBlock(readen,0,0);
			Assert.assertTrue(x==0);
			r.close();			
			
			leave();
	};
	
	/**
		Test if long block can be written and read without 
		an enclosing structure, variant for typed stream.
	@throws IOException .
	*/
	@Test public void testWriteFlat_typed()throws IOException
	{
			enter();
			
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			Assume.assumeTrue( r instanceof ITypedStructReadFormat);
			
			w.open();
			long [] written = newBlock(1024);
			w.writeLongBlock(written,7,900);
			w.close();
			
			
			r.open();
			long [] readen = new long[1001];
			int x = r.readLongBlock(readen,1,1000);
			//API says that block read will throw EOF if could not read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//(what in block read will result in -1)
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.
			Assert.assertTrue(x==900);
			try{
				r.readLongBlock();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if long block can be written and read without 
		an enclosing structure, variant for typed stream.
	@throws IOException .
	*/
	@Test public void testWriteFlat_typed_blk()throws IOException
	{
			enter();
			
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			Assume.assumeTrue( r instanceof ITypedStructReadFormat);
			
			w.open();
			long [] written = newBlock(1024);
			w.writeLongBlock(written,7,900);
			w.close();
			
			
			r.open();
			long [] readen = new long[1001];
			int x = r.readLongBlock(readen,1,1000);
			//API says that block read will throw EOF if could not read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//(what in block read will result in -1)
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.			
			Assert.assertTrue(x==900);
			try{
				Assert.assertTrue(r.readLongBlock(new long[1])==-1);
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if long block can be written and read with
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
			long [] written = newBlock(1024);
			w.writeLongBlock(written,7,900);
			w.end();
			w.writeLong((long)44);
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			long [] readen = new long[1001];
			{
				int x = r.readLongBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			}
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readLongBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue(null==r.next());//consume end signal
			Assert.assertTrue(r.readLong()==(long)44);
			r.close();			
			
			leave();
	};   
	
	
	/**
		Test if can write values which are suspected to be problematic.
	@throws IOException .
	*/
	@Test public void testWriteSpecial()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.begin("aka");
			long [] written = new long[]
				{
					0,-1,
					0x80778899_00119340L,
					0x70778899_00119340L,
					0x00000000_FF119340L,
					0x00000000_00119340L
				};
			w.writeLongBlock(written);
			w.end();
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			long [] readen = new long[100];
			{
				int x = r.readLongBlock(readen,1,99);
				Assert.assertTrue(x==written.length);
				assertArraysEqual(written,0,written.length,readen,1);
			}
			r.close();
			leave();
	};   
	
	
	/**
		Test if long block can be written and read with
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
			long [] written = newBlock(1024);
			w.writeLongBlock(written,7,900);
			w.begin("ally");
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			long [] readen = new long[1001];
			{
				int x = r.readLongBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			};
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readLongBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue("ally".equals(r.next()));//consume begin signal
			r.close();			
			
			leave();
	};
}