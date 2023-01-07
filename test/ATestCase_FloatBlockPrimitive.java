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
	A test case running tests for float block primitives.
	<p>
	<i>Note: This is a pattern for all other primitives. Shame java has no
	preprocessor.</i>
*/
public class ATestCase_FloatBlockPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
	/** Computes block filled with predictable, fixed pattern
	@param length length of block
	@return new block with data */
	private float [] newBlock(int length)
	{
			float [] src = new float[length];
			int x = 0;
			for(int i=src.length;--i>=0;)
			{
				x = x*33714091+i;
				src[i] = Float.intBitsToFloat(x);
			};
			return src;
	};
	private void assertArraysEqual(
							float [] A,int offA, int length,
							float [] B,int offB
							)
	{
		for(int i =0;i<length; i++)
		{
			float a = A[offA];
			float b = B[offB];
			if (a!=b)
			{
				//Now the Java does Nan!=Nan
				if (!(Float.isNaN(a) && Float.isNaN(b)))
					Assert.fail("Arrays do differ at index "+i+" A["+offA+"]="+A[offA]+"!=B["+offB+"]="+B[offB]);
			};
			offA++;
			offB++;
		};
	};
							
	/**
		Test if float block can be written and read without 
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
			float [] written = newBlock(1024);
			w.writeFloatBlock(written,7,900);
			w.close();
			
			
			r.open();
			float [] readen = new float[1001];
			int x = r.readFloatBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			Assert.assertTrue(x==900);
			try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if float block can be written and read without 
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
			float [] written = newBlock(1024);
			w.writeFloatBlock(written,7,900);
			w.close();
			
			
			r.open();
			float [] readen = new float[1001];
			int x = r.readFloatBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			Assert.assertTrue(x==900);
			try{
				r.readFloatBlock(new float[1]);
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
			float [] written = newBlock(5);
			w.writeFloatBlock(written,0,5);
			w.close();
			
			
			r.open();
			float [] readen = new float[10];
			int x = r.readFloatBlock(readen,0,0);
			Assert.assertTrue(x==0);
			r.close();			
			
			leave();
	};
	
	/**
		Test if float block can be written and read without 
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
			float [] written = newBlock(1024);
			w.writeFloatBlock(written,7,900);
			w.close();
			
			
			r.open();
			float [] readen = new float[1001];
			int x = r.readFloatBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.
			Assert.assertTrue(x==900);
			try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	
	/**
		Test if float block can be written and read without 
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
			float [] written = newBlock(1024);
			w.writeFloatBlock(written,7,900);
			w.close();
			
			
			r.open();
			float [] readen = new float[1001];
			int x = r.readFloatBlock(readen,1,1000);
			//API says that block read will throw EOF if could not read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//(what in block read will result in -1)
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.			
			Assert.assertTrue(x==900);
			try{
				Assert.assertTrue(r.readFloatBlock(new float[3])==-1);
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if float block can be written and read with
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
			float [] written = newBlock(1024);
			w.writeFloatBlock(written,7,900);
			w.end();
			w.writeFloat((float)44);
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			float [] readen = new float[1001];
			{
				int x = r.readFloatBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			}
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readFloatBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue(null==r.next());//consume end signal
			Assert.assertTrue(r.readFloat()==(float)44);
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
			float [] written = new float[]
				{
					0,-1,
					Float.intBitsToFloat(0x00119340),
					Float.intBitsToFloat(0x00119340),
					Float.intBitsToFloat(0xFF119340),
					Float.intBitsToFloat(0x00119340)
				};
			w.writeFloatBlock(written);
			w.end();
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			float [] readen = new float[100];
			{
				int x = r.readFloatBlock(readen,1,99);
				Assert.assertTrue(x==written.length);
				assertArraysEqual(written,0,written.length,readen,1);
			}
			r.close();
			leave();
	};   
	
	
	/**
		Test if float block can be written and read with
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
			float [] written = newBlock(1024);
			w.writeFloatBlock(written,7,900);
			w.begin("ally");
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			float [] readen = new float[1001];
			{
				int x = r.readFloatBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			};
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readFloatBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue("ally".equals(r.next()));//consume begin signal
			r.close();			
			
			leave();
	};
}