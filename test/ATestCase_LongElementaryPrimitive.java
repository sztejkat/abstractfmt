package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EEof;
import sztejkat.abstractfmt.ENoMoreData;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running tests for long elementary primitives.	
*/
public class ATestCase_LongElementaryPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
			
	
	/**
		Test if long can be written and read without 
		an enclosing structure.
	@throws IOException .
	*/
	@Test public void long_testWriteFlat()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.writeLong((long)0);
			w.writeLong((long)1);
			w.writeLong(0x01000000_1A103900L);
			w.writeLong(0x00000000_FA103945L);
			w.writeLong(0x80818284_FAEC4001L);
			w.close();
			
			
			r.open();
			
			Assert.assertTrue(r.readLong()==(long)0);
			Assert.assertTrue(r.readLong()==(long)1);
			Assert.assertTrue(r.readLong()==0x01000000_1A103900L);
			
			Assert.assertTrue(r.readLong()==0x00000000_FA103945L);
			Assert.assertTrue(r.readLong()==0x80818284_FAEC4001L);
			r.close();
	};
	
	/**
		Test how long read reacts on end-of-file
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
				r.readLong();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();
	};
	
	
	/**
		Test how writing long enclosed in signals
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
				w.writeLong((long)(-32761));
				w.writeLong((long)(-161));
			w.end();
			w.writeLong((long)(12761));
			w.writeLong((long)(761));
			w.close();
			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
				Assert.assertTrue(r.readLong()==(long)(-32761));
				Assert.assertTrue(r.readLong()==(long)(-161));
				for(int i=0;i<10;i++) //check if this is a persisiting condition.
				{
					try{
						r.readLong();
						Assert.fail();
					}catch(ENoMoreData ex){System.out.println("caught expected "+ex); };
				};
			Assert.assertTrue(null==r.next());
			//if condition was reset by signal.
			Assert.assertTrue(r.readLong()==(long)(12761));
			Assert.assertTrue(r.readLong()==(long)(761));			
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
				w.writeLong((long)(761));
				w.writeLong((long)(761));
				w.writeLongBlock(new long[]{(long)(731),(long)(711)});
				try{
					w.writeLong((long)(7621));
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
				w.writeLongBlock(new long[]{(long)(761),(long)(-131)});				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readLongBlock(new long[100])==2);
			//Now we try reading long
			try{
				r.readLong();
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
				w.writeLongBlock(new long[2]);				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readLongBlock(new long[100],0,1)==1);
			//Now we try reading long inside a block still
			try{
				r.readLong();
				Assert.fail();
				//Note: contract allows only one kind of exception.
			}catch(IllegalStateException ex){ System.out.println(ex); }
			r.close();
	};
};
