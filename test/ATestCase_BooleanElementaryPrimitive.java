package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EEof;
import sztejkat.abstractfmt.ENoMoreData;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running tests for boolean elementary primitives.
	<p>
	<i>Note: This is a pattern for all other primitives. Shame java has no
	preprocessor.</i>
*/
public class ATestCase_BooleanElementaryPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
			
	
	/**
		Test if boolean can be written and read without 
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
			w.writeBoolean(true);
			w.writeBoolean(false);
			w.close();
			
			
			r.open();
			Assert.assertTrue(r.readBoolean()==true);
			Assert.assertTrue(r.readBoolean()==false);
			r.close();
	};
	
	/**
		Test how boolean read reacts on end-of-file
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
				r.readBoolean();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();
	};
	
	
	/**
		Test how writing boolean enclosed in signals
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
				w.writeBoolean(true);
				w.writeBoolean(false);
			w.end();
			w.writeBoolean(true);
			w.writeBoolean(false);
			w.close();
			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
				Assert.assertTrue(r.readBoolean()==true);
				Assert.assertTrue(r.readBoolean()==false);
				for(int i=0;i<10;i++) //check if this is a persisiting condition.
				{
					try{
						r.readBoolean();
						Assert.fail();
					}catch(ENoMoreData ex){System.out.println(ex); };
				};
			Assert.assertTrue(null==r.next());
			//if condition was reset by signal.
			Assert.assertTrue(r.readBoolean()==true);
			Assert.assertTrue(r.readBoolean()==false);			
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
				w.writeBoolean(true);
				w.writeBoolean(false);
				w.writeBooleanBlock(new boolean[]{false,false});
				try{
					w.writeBoolean(false);
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
				w.writeBooleanBlock(new boolean[]{false,false});				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readBooleanBlock(new boolean[100])==2);
			//Now we try reading boolean
			try{
				r.readBoolean();
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
				w.writeBooleanBlock(new boolean[]{false,false});				
			w.close();			
			
			r.open();
			Assert.assertTrue("struct".equals(r.next()));
			Assert.assertTrue(r.readBooleanBlock(new boolean[100],0,1)==1);
			//Now we try reading boolean inside a block still
			try{
				r.readBoolean();
				Assert.fail();
				//Note: contract allows only one kind of exception.
			}catch(IllegalStateException ex){ System.out.println(ex); }
			r.close();
	};
};
