package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EEof;
import sztejkat.abstractfmt.ENoMoreData;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running tests for int elementary primitives.	
*/
public class ATestCase_IntElementaryPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
			
	
	/**
		Test if int can be written and read without 
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
			w.writeInt(0);
			w.writeInt(1);
			w.writeInt(255);
			w.writeInt(32761);
			w.writeInt(0xABCD_F080);
			w.close();
			
			
			r.open();
			Assert.assertTrue(r.readInt()==0);
			Assert.assertTrue(r.readInt()==1);
			Assert.assertTrue(r.readInt()==255);
			Assert.assertTrue(r.readInt()==32761);
			Assert.assertTrue(r.readInt()==0xABCD_F080);
			r.close();
	};
	
	/**
		Test how int read reacts on end-of-file
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
				r.readInt();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();
	};
	
	
	/**
		Test how writing int enclosed in signals
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
				w.writeInt((-32761));
				w.writeInt((-161));
			w.end();
			w.writeInt((12761));
			w.writeInt((761));
			w.close();
			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
				Assert.assertTrue(r.readInt()==(-32761));
				Assert.assertTrue(r.readInt()==(-161));
				for(int i=0;i<10;i++) //check if this is a persisiting condition.
				{
					try{
						r.readInt();
						Assert.fail();
					}catch(ENoMoreData ex){System.out.println("caught expected "+ex); };
				};
			Assert.assertTrue(null==r.next());
			//if condition was reset by signal.
			Assert.assertTrue(r.readInt()==(12761));
			Assert.assertTrue(r.readInt()==(761));			
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
				w.writeInt((761));
				w.writeInt((761));
				w.writeIntBlock(new int[]{(731),(711)});
				try{
					w.writeInt((7621));
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
				w.writeIntBlock(new int[]{(761),(-131)});				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readIntBlock(new int[100])==2);
			//Now we try reading int
			try{
				r.readInt();
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
				w.writeIntBlock(new int[2]);				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readIntBlock(new int[100],0,1)==1);
			//Now we try reading int inside a block still
			try{
				r.readInt();
				Assert.fail();
				//Note: contract allows only one kind of exception.
			}catch(IllegalStateException ex){ System.out.println(ex); }
			r.close();
	};
};
