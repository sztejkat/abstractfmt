package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EEof;
import sztejkat.abstractfmt.ENoMoreData;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running tests for double elementary primitives.	
*/
public class ATestCase_DoubleElementaryPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
			
	
	/**
		Test if double can be written and read without 
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
			w.writeDouble((double)0);
			w.writeDouble((double)1);
			w.writeDouble((double)-1);
			w.writeDouble(Double.longBitsToDouble(0x8F44_4900_3334L));
			w.writeDouble(Double.longBitsToDouble(0x7F44_4900_3334L));
			w.close();
			
			
			r.open();
			Assert.assertTrue(r.readDouble()==(double)0);
			Assert.assertTrue(r.readDouble()==(double)1);
			Assert.assertTrue(r.readDouble()==(double)-1);
			Assert.assertTrue(r.readDouble()==Double.longBitsToDouble(0x8F44_4900_3334L));
			Assert.assertTrue(r.readDouble()==Double.longBitsToDouble(0x7F44_4900_3334L));
			r.close();
	};
	
	/**
		Test how double read reacts on end-of-file
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
				r.readDouble();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();
	};
	
	
	/**
		Test how writing double enclosed in signals
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
				w.writeDouble((double)(-32761));
				w.writeDouble((double)(-161));
			w.end();
			w.writeDouble((double)(12761));
			w.writeDouble((double)(761));
			w.close();
			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
				Assert.assertTrue(r.readDouble()==(double)(-32761));
				Assert.assertTrue(r.readDouble()==(double)(-161));
				for(int i=0;i<10;i++) //check if this is a persisiting condition.
				{
					try{
						r.readDouble();
						Assert.fail();
					}catch(ENoMoreData ex){System.out.println("caught expected "+ex); };
				};
			Assert.assertTrue(null==r.next());
			//if condition was reset by signal.
			Assert.assertTrue(r.readDouble()==(double)(12761));
			Assert.assertTrue(r.readDouble()==(double)(761));			
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
				w.writeDouble((double)(761));
				w.writeDouble((double)(761));
				w.writeDoubleBlock(new double[]{(double)(731),(double)(711)});
				try{
					w.writeDouble((double)(7621));
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
				w.writeDoubleBlock(new double[]{(double)(761),(double)(-131)});				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readDoubleBlock(new double[100])==2);
			//Now we try reading double
			try{
				r.readDouble();
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
				w.writeDoubleBlock(new double[2]);				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readDoubleBlock(new double[100],0,1)==1);
			//Now we try reading double inside a block still
			try{
				r.readDouble();
				Assert.fail();
				//Note: contract allows only one kind of exception.
			}catch(IllegalStateException ex){ System.out.println(ex); }
			r.close();
	};
};
