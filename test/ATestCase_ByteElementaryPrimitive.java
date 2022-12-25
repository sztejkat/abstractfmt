package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EEof;
import sztejkat.abstractfmt.ENoMoreData;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running tests for byte elementary primitives.
	<p>
	<i>Note: This is a pattern for all other primitives. Shame java has no
	preprocessor.</i>
*/
public class ATestCase_ByteElementaryPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
			
	
	/**
		Test if byte can be written and read without 
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
			for(int i=0;i<256;i++)
			{
				w.writeByte((byte)i);
			}
			w.close();
			
			
			r.open();
			for(int i=0;i<256;i++)
			{
				Assert.assertTrue(r.readByte()==(byte)i);
			}
			r.close();
	};
	
	/**
		Test how byte read reacts on end-of-file
		without an enclosing structure.
		@throws IOException .
	*/
	@Test public void testReadFlatEof()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			//Nothing
			w.close();
			
			
			r.open();
			try{
				r.readByte();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();
	};
	
	
	/**
		Test how writing byte enclosed in signals
		do work and how end-of-struct is handled.
	@throws IOException .
	*/
	@Test public void testWriteEnclosed()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.begin("struct");
				w.writeByte((byte)33);
				w.writeByte((byte)-33);
			w.end();
			w.writeByte((byte)44);
			w.writeByte((byte)45);
			w.close();
			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
				Assert.assertTrue(r.readByte()==(byte)33);
				Assert.assertTrue(r.readByte()==(byte)-33);
				for(int i=0;i<10;i++) //check if this is a persisiting condition.
				{
					try{
						r.readByte();
						Assert.fail();
					}catch(ENoMoreData ex){System.out.println(ex); };
				};
			Assert.assertTrue(null==r.next());
			//if condition was reset by signal.
			Assert.assertTrue(r.readByte()==(byte)44);
			Assert.assertTrue(r.readByte()==(byte)45);			
			r.close();
	};
	
	
	/**
		Test if writing a block prevents elementary write.
	@throws IOException .
	*/
	@Test public void testWriteBlockPreventsElementary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.begin("struct");
				w.writeByte((byte)0);
				w.writeByte((byte)-1);
				w.writeByteBlock(new byte[]{(byte)3,(byte)7});
				try{
					w.writeByte((byte)44);
				}catch(IllegalStateException ex){ System.out.println(ex); }
			w.close();			
			
			r.open();
			r.close();
	};
	
	
	/**
		Test if read a block prevents elementary read.
	@throws IOException .
	*/
	@Test public void testReadWholeBlockPreventsElementary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.begin("struct");
				w.writeByteBlock(new byte[2]);				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readByteBlock(new byte[100])==2);
			//Now we try reading 
			try{
				r.readByte();
				Assert.fail();
				//Note: contract allows both exceptions.
			}catch(ENoMoreData ex){ System.out.println(ex); }
	         catch(IllegalStateException ex){ System.out.println(ex); }
			r.close();
	};
	/**
		Test if read a block prevents elementary read.
	@throws IOException .
	*/
	@Test public void testReadPartBlockPreventsElementary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.begin("struct");
				w.writeByteBlock(new byte[2]);				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readByteBlock(new byte[100],0,1)==1);
			//Now we try reading boolean inside a block still
			try{
				r.readByte();
				Assert.fail();
				//Note: contract allows only one kind of exception.
			}catch(IllegalStateException ex){ System.out.println(ex); }
			r.close();
	};
};
