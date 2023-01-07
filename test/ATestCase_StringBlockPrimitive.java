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
	A test case running tests for string block primitives.	
*/
public class ATestCase_StringBlockPrimitive extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
	/** Computes block filled with predictable, fixed pattern
	@param length length of block
	@return new block with data */
	private String newBlock(int length)
	{
			StringBuilder sb = new StringBuilder(); 
			int x = 0;
			for(int i=length;--i>=0;)
			{
				x = x*37+i;
				sb.append((char)x);
			};
			return sb.toString();
	};
					
	/**
		Test if string block can be read without enclosing structure.
	@throws IOException .
	*/
	@Test public void testWriteFlat()throws IOException
	{
			enter();
			
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			Assume.assumeFalse( r instanceof ITypedStructReadFormat);
			
			w.open();
			String written = newBlock(65536);
			w.writeString(written);
			w.close();
			
			
			r.open();
			String readen= r.readString(65536);
			Assert.assertTrue(readen.equals(written));
			r.close();
			leave();
	};
	
	/**
		Test if string block can be read without enclosing structure
		and how eof do behave.
	@throws IOException .
	*/
	@Test public void testWriteFlat_untyped_EOF()throws IOException
	{
		
			enter();
			
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			Assume.assumeFalse( r instanceof ITypedStructReadFormat);
			
			w.open();
			String written = newBlock(1000);
			w.writeString(written,7,900);
			w.close();
			
			
			r.open();
			String readen= r.readString(1000);
			Assert.assertTrue(readen.equals(written.substring(7,900+7)));
			//API says that block read will throw EOF if could not
			//read ANY data.
			try{
				r.readString();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();		
			
			leave();
	};
	
	
	/**
		Test if string block can be read without enclosing structure
		and how eof do behave.
	@throws IOException .
	*/
	@Test public void testWriteFlat_untyped_EOF_blk()throws IOException
	{
		
			enter();
			
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			Assume.assumeFalse( r instanceof ITypedStructReadFormat);
			
			w.open();
			String written = newBlock(1000);
			w.writeString(written,7,900);
			w.close();
			
			
			r.open();
			String readen= r.readString(1000);
			Assert.assertTrue(readen.equals(written.substring(7,900+7)));
			//API says that block read will throw EOF if could not
			//read ANY data.
			try{
				r.readString(new StringBuilder(),10);
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
			r.close();		
			
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
			String written = newBlock(5);
			w.writeString(written,0,5);
			w.close();
			
			
			r.open();
			int x = r.readString(new StringBuilder(),0);
			Assert.assertTrue(x==0);
			r.close();			
			
			leave();
	};
	
	/**
		Test if String block can be written and read without 
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
			String written = newBlock(1024);
			w.writeString(written,7,900);
			w.close();
			
			
			r.open();
			StringBuilder readen = new StringBuilder();
			int x = r.readString(readen,1000);
			//API says that block read will throw EOF if could not
			//read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.
			Assert.assertTrue(x==900);
			try{
				r.readString();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); }
			catch(ENoMoreData ex){ System.out.println(ex); };
			r.close();			
			Assert.assertTrue(readen.toString().equals(written.substring(7,900+7)));
			
			leave();
	};
	
	/**
		Test if String block can be written and read without 
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
			String written = newBlock(1024);
			w.writeString(written,7,900);
			w.close();
			
			
			r.open();
			StringBuilder readen = new StringBuilder();
			int x = r.readString(readen,1000);
			//API says that block read will throw EOF if could not read ANY data.
			//A typed stream is however allowed to throw ENoMoreData
			//(what in block read will result in -1)
			//in this condition, because we can't force missing 
			//end-of-type information. The detail will depend
			//on implementation detail, so for typed streams we do allow both.
			Assert.assertTrue(x==900);
			try{
				Assert.assertTrue(r.readString(new StringBuilder(),100)==-1);
			}catch(EEof ex){ System.out.println(ex); }
			r.close();			
			Assert.assertTrue(readen.toString().equals(written.substring(7,900+7)));
			
			leave();
	};
	
	/**
		Test if String block can be written and read with
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
			String written = newBlock(1024);
			w.writeString(written,7,900);
			w.end();
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			StringBuilder readen = new StringBuilder();
			{
				int x = r.readString(readen,1000);
				Assert.assertTrue(x==900);
				Assert.assertTrue(readen.toString().equals(written.substring(7,900+7)));
			}
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readString(readen,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue(null==r.next());//consume end signal
			r.close();			
			
			leave();
	};   
	
	
	/**
		Test if String block can be written and read with
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
			String written = newBlock(1024);
			w.writeString(written,7,900);
			w.begin("ally");
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			StringBuilder readen = new StringBuilder();
			{
				int x = r.readString(readen,1000);
				Assert.assertTrue(x==900);
				Assert.assertTrue(readen.toString().equals(written.substring(7,900+7)));
			};
			//poll for presistent no more data
			for(int i=0;i<10;i++)
			{
				int x = r.readString(readen,1000);
				Assert.assertTrue(x==-1);
			};
			Assert.assertTrue("ally".equals(r.next()));//consume begin signal
			r.close();			
			
			leave();
	};
	
	
	/**
		Test if String block can be written and read with
		some special strings encode
		@param written string do test.
	@throws IOException .
	*/
	private void testWriteSpecial(String written)throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			w.begin("aka");
			w.writeString(written);
			w.end();
			w.close();
			
			
			r.open();
			Assert.assertTrue("aka".equals(r.next()));
			Assert.assertTrue(r.readString(written.length()+100).equals(written));
			r.close();			
			
			leave();
	};
	
	@Test public void writeXML_unfiendly()throws IOException
	{
		enter();	//Note: dump files will be overriden by each call.
		testWriteSpecial("<mar>");
		testWriteSpecial("&amp;mar>");
		testWriteSpecial("<!-- -->");
		testWriteSpecial("\"ooops");
		leave();
	};
	
	@Test public void writeJSON_unfiendly()throws IOException
	{
		enter();	//Note: dump files will be overriden by each call.
		testWriteSpecial("{arc:");
		testWriteSpecial("\t");
		testWriteSpecial("\n ");
		testWriteSpecial("\r ");
		testWriteSpecial("\"ooops");
		leave();
	};
}