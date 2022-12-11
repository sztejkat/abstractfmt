package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running signal operations.
*/
public class ATestCase_BasicSignalOperations extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{

	/**
		A test checking if stream refuses to read next signal 
		if not opened.
	@throws IOException .
	*/
	@Test public void testUnopendedFailsNext()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			p.writer.open();
			p.writer.close();
			try{
					p.reader.next();
			}catch(ENotOpen ex){System.out.println(ex); };
	};
	/**
		Test flat sequence of structures
	@throws IOException .
	*/
	@Test public void testFlatNext()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
			w.end();
			w.begin("gimikis");
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(null==r.next());
			Assert.assertTrue("gimikis".equals(r.next()));
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	/**
		Test some nested structure.
	@throws IOException .
	*/
	@Test public void testNestedStructure()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			
			for(int i=0;i<32;i++)
			{
				w.begin("struct_"+i);
			};
			for(int i=0;i<32;i++)
			{
				w.end();
			};
			w.close();
			
			r.open();
			for(int i=0;i<32;i++)
				Assert.assertTrue(("struct_"+i).equals(r.next()));
			for(int i=0;i<32;i++)
				Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
};
