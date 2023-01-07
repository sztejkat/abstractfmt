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
	A test case running tests for char block primitives.
	<p>
	<i>Note: This is a pattern for all other primitives. Shame java has no
	preprocessor.</i>
*/
public class ATestCase_CharBlockPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
	/** Computes block filled with predictable, fixed pattern
	@param length length of block
	@return new block with data */
	private char [] newBlock(int length)
	{
			char [] src = new char[length];
			int x = 0;
			for(int i=src.length;--i>=0;)
			{
				x = x*337+i;
				src[i] =(char)x;
			};
			return src;
	};
	private void assertArraysEqual(
							char [] A,int offA, int length,
							char [] B,int offB
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
		Test if char block can be written and read without 
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
			char [] written = newBlock(1024);
			w.writeCharBlock(written,7,900);
			w.close();
			
			
			r.open();
			char [] readen = new char[1001];
			int x = r.readCharBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			Assert.assertTrue(x==900);
			try{
				r.readCharBlock();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if char block can be written and read without 
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
			char [] written = newBlock(1024);
			w.writeCharBlock(written,7,900);
			w.close();
			
			
			r.open();
			char [] readen = new char[1001];
			int x = r.readCharBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			Assert.assertTrue(x==900);
			try{
				r.readCharBlock(new char[100]);
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
			char [] written = newBlock(5);
			w.writeCharBlock(written,0,5);
			w.close();
			
			
			r.open();
			char [] readen = new char[10];
			int x = r.readCharBlock(readen,0,0);
			Assert.assertTrue(x==0);
			r.close();			
			
			leave();
	};
	
	/**
		Test if char block can be written and read without 
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
			char [] written = newBlock(1024);
			w.writeCharBlock(written,7,900);
			w.close();
			
			
			r.open();
			char [] readen = new char[1001];
			int x = r.readCharBlock(readen,1,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.
			Assert.assertTrue(x==900);
			try{
				r.readCharBlock();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if char block can be written and read without 
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
			char [] written = newBlock(1024);
			w.writeCharBlock(written,7,900);
			w.close();
			
			
			r.open();
			char [] readen = new char[1001];
			int x = r.readCharBlock(readen,1,1000);
			//API says that block read will throw EOF if could not read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//(what in block read will result in -1)
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.			
			
			Assert.assertTrue(x==900);
			try{
				Assert.assertTrue(r.readCharBlock(new char[100])==-1);
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
			r.close();			
			assertArraysEqual(written,7,900,readen,1);
			
			leave();
	};
	
	/**
		Test if char block can be written and read with
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
			char [] written = newBlock(1024);
			w.writeCharBlock(written,7,900);
			w.end();
			w.writeChar((char)44);
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			char [] readen = new char[1001];
			{
				int x = r.readCharBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			}
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readCharBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue(null==r.next());//consume end signal
			Assert.assertTrue(r.readChar()==(char)44);
			r.close();			
			
			leave();
	};   
	
	
	/**
		Test if char block can be written and read with
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
			char [] written = newBlock(1024);
			w.writeCharBlock(written,7,900);
			w.begin("ally");
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			char [] readen = new char[1001];
			{
				int x = r.readCharBlock(readen,1,1000);
				Assert.assertTrue(x==900);
				assertArraysEqual(written,7,900,readen,1);
			};
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readCharBlock(readen,1,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue("ally".equals(r.next()));//consume begin signal
			r.close();			
			
			leave();
	};
}