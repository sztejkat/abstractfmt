package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running signal operations and checking them gainst limmits..
*/
public class ATestCase_SignalOperationsSafety extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{

	
	/**
		Test if write rejects to long name
	@throws IOException .
	*/
	@Test public void testWriteNameLimit()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			//Test notes: Typed streams may use internally longer
			//names so trimming it too much here will be a problem
			//and will cause false fails because they do register
			//them as optimized during open().
			w.setMaxSignalNameLength(16);
			
			w.open();
			w.begin("1234567890123456");
			w.end();
			try{
				w.begin("12345678901234567");
				Assert.fail("Should have thrown");
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex);};
			w.close();
			r.open();
			r.close();
	};
	/**
		Test if read rejects to long name
	@throws IOException .
	*/
	@Test public void testReadNameLimit()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			//Test notes: Typed streams may use internally longer
			//names so trimming it too much here will be a problem
			//and will cause false fails.
			r.setMaxSignalNameLength(16);
			
			w.open();
			w.begin("1234567890123456");
			w.end();
			w.begin("12345678901234567");
			w.close();
			
			r.open();
			r.next();
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex);};
			r.close();
	};	
	
	
	
	/**
		Test if write rejects write recursion limit
	@throws IOException .
	*/
	@Test public void testWriteRecursionLimit()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.setMaxStructRecursionDepth(1);
			
			w.open();
			w.begin("lark");
				try{
					w.begin("karki");
					Assert.fail("Should have thrown");
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex);};
			w.close();
			r.open();
			r.close();
	};
	
	/**
		Test if reader rejects write recursion limit
	@throws IOException .
	*/
	@Test public void testReadRecursionLimit()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			r.setMaxStructRecursionDepth(1);
			
			w.open();
			w.begin("lark");
				w.begin("karki");
				w.end();
			w.end();
			w.close();
			
			r.open();
			r.next();
				try{
					r.next();
					Assert.fail("Should have thrown");
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex);};
			r.close();
	};
};
