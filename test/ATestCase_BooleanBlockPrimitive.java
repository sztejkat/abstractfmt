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
		Test if boolean block can be written and read without 
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
				r.readBooleanBlock(new boolean[10]);
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
			boolean [] written = newBlock(5);
			w.writeBooleanBlock(written,0,5);
			w.close();
			
			
			r.open();
			boolean [] readen = new boolean[10];
			int x = r.readBooleanBlock(readen,0,0);
			Assert.assertTrue(x==0);
			r.close();			
			
			leave();
	};
	
	/**
		Test if boolean block can be written and read without 
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
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	
	/**
		Test if boolean block can be written and read without 
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
			boolean [] written = newBlock(1024);
			w.writeBooleanBlock(written,7,900);
			w.close();
			
			
			r.open();
			boolean [] readen = new boolean[1001];
			int x = r.readBooleanBlock(readen,1,1000);
			//API says that block read will throw EOF if could not read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//(what in block read will result in -1)
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.			
			
			Assert.assertTrue(x==900);
			try{
				Assert.assertTrue(r.readBooleanBlock(new boolean[1])==-1);
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
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
		an enclosing structure, using varying size parameters.
		<p>
		Note: Boolean blocks and Strings are expected to use
		more sophisticated encodings than other blocks, so they
		have to be more detailed tested.
		<p>
		Boolean blocks will be usually "bit-packed" so
		we need to test different pack sizes.
	@param pack_size non zero positive.
	@throws IOException .
	*/
	private void testWriteEnclosed_VariedSize(int pack_size)throws IOException
	{
			enter();
			assert(pack_size>0);
			
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.begin("aka");
			boolean [] written = newBlock(pack_size);
			w.writeBooleanBlock(written,0,pack_size);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			boolean [] readen = new boolean[pack_size];
			{
				int x = r.readBooleanBlock(readen,0,pack_size);
				System.out.println("x="+x);
				Assert.assertTrue(x==pack_size);
				assertArraysEqual(written,0,pack_size,readen,0);
			}
			//pool for eof?
			{
				int x = r.readBooleanBlock(readen,0,pack_size);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue(null==r.next());//consume end signal
			r.close();
			leave();
	};   
	@Test public void testWriteEnclosed_1()throws IOException{ enter(); testWriteEnclosed_VariedSize(1); leave();};	
	@Test public void testWriteEnclosed_5()throws IOException{ enter(); testWriteEnclosed_VariedSize(5); leave();};
	@Test public void testWriteEnclosed_256()throws IOException{ enter(); testWriteEnclosed_VariedSize(256); leave();};
	@Test public void testWriteEnclosed_301()throws IOException{ enter(); testWriteEnclosed_VariedSize(301); leave();};
	
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