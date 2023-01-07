package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EEof;
import sztejkat.abstractfmt.ENoMoreData;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running tests for float elementary primitives.	
*/
public class ATestCase_FloatElementaryPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
			
	
	/**
		Test if float can be written and read without 
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
			w.writeFloat((float)0);
			w.writeFloat((float)1);
			w.writeFloat((float)-1);
			w.writeFloat(Float.intBitsToFloat(0x8F44_4900));
			w.writeFloat(Float.intBitsToFloat(0x7F44_4900));
			w.close();
			
			
			r.open();
			Assert.assertTrue(r.readFloat()==(float)0);
			Assert.assertTrue(r.readFloat()==(float)1);
			Assert.assertTrue(r.readFloat()==(float)-1);
			Assert.assertTrue(r.readFloat()==Float.intBitsToFloat(0x8F44_4900));
			Assert.assertTrue(r.readFloat()==Float.intBitsToFloat(0x7F44_4900));
			r.close();
	};
	
	/**
		Test how float read reacts on end-of-file
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
				r.readFloat();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();
	};
	
	
	/**
		Test how writing float enclosed in signals
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
				w.writeFloat((float)(-32761));
				w.writeFloat((float)(-161));
			w.end();
			w.writeFloat((float)(12761));
			w.writeFloat((float)(761));
			w.close();
			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
				Assert.assertTrue(r.readFloat()==(float)(-32761));
				Assert.assertTrue(r.readFloat()==(float)(-161));
				for(int i=0;i<10;i++) //check if this is a persisiting condition.
				{
					try{
						r.readFloat();
						Assert.fail();
					}catch(ENoMoreData ex){System.out.println("caught expected "+ex); };
				};
			Assert.assertTrue(null==r.next());
			//if condition was reset by signal.
			Assert.assertTrue(r.readFloat()==(float)(12761));
			Assert.assertTrue(r.readFloat()==(float)(761));			
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
				w.writeFloat((float)(761));
				w.writeFloat((float)(761));
				w.writeFloatBlock(new float[]{(float)(731),(float)(711)});
				try{
					w.writeFloat((float)(7621));
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
				w.writeFloatBlock(new float[]{(float)(761),(float)(-131)});				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readFloatBlock(new float[100])==2);
			//Now we try reading float
			try{
				r.readFloat();
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
				w.writeFloatBlock(new float[2]);				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readFloatBlock(new float[100],0,1)==1);
			//Now we try reading float inside a block still
			try{
				r.readFloat();
				Assert.fail();
				//Note: contract allows only one kind of exception.
			}catch(IllegalStateException ex){ System.out.println(ex); }
			r.close();
	};
};
