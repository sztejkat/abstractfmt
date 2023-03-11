package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.ITypedStructReadFormat;
import sztejkat.abstractfmt.ITypedStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
import static sztejkat.abstractfmt.ITypedStructReadFormat.TElement;

/**
	A peek() test case for 
	{@link ITypedStructReadFormat}/{@link ITypedStructWriteFormat}
	testing if depth and skip is not affected by peeking.
*/
public class ATestCase_DepthAndSkipInPeekOperations extends AInterOpTestCase<ITypedStructReadFormat,ITypedStructWriteFormat>
{

	@Test public void testPeekInfluencesDepth()throws IOException
	{
			enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			final ITypedStructWriteFormat w= p.writer;
			final ITypedStructReadFormat  r= p.reader;
			w.open();
			w.begin("struct_1");
				w.writeInt(33);
			w.end();
			w.writeInt(104);
			w.close();
			r.open();
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue(r.peek()==TElement.SIG);
				Assert.assertTrue(r.depth()==0);
				
				Assert.assertTrue("struct_1".equals(r.next()));
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(r.peek()==TElement.INT);
				Assert.assertTrue(r.depth()==1);
				
				Assert.assertTrue(r.readInt()==33);
				Assert.assertTrue(r.depth()==1);
				
				Assert.assertTrue(r.peek()==TElement.SIG);
				Assert.assertTrue(r.depth()==1);
				
				Assert.assertTrue(r.next()==null);
				Assert.assertTrue(r.depth()==0);
				
			r.close();
	};
	
	
	@Test public void testPeekInfluenceSkip()throws IOException
	{
			enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			final ITypedStructWriteFormat w= p.writer;
			final ITypedStructReadFormat  r= p.reader;
			w.open();
			w.begin("struct_1");
				w.writeInt(33);
			w.end();
			w.writeInt(104);
			w.close();
			r.open();
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue(r.peek()==TElement.SIG);
				Assert.assertTrue(r.depth()==0);
				
				Assert.assertTrue("struct_1".equals(r.next()));
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(r.peek()==TElement.INT);
				Assert.assertTrue(r.depth()==1);
				
				r.skip();
				
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue(r.readInt()==104);
				
			r.close();
	};
	
	
	@Test public void testSkipAffectsPeek()throws IOException
	{
			enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			final ITypedStructWriteFormat w= p.writer;
			final ITypedStructReadFormat  r= p.reader;
			w.open();
			w.begin("struct_1");
				w.writeInt(33);
			w.end();
			w.writeLong(104);
			w.close();
			r.open();
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue(r.peek()==TElement.SIG);
				Assert.assertTrue(r.depth()==0);
				
				Assert.assertTrue("struct_1".equals(r.next()));
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(r.peek()==TElement.INT);
				Assert.assertTrue(r.depth()==1);
				
				r.skip();
				
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue(r.peek()==TElement.LONG);
				
			r.close();
	};
	
	
	
	@Test public void testPeekInfluencesDepthInBlock()throws IOException
	{
			enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			final ITypedStructWriteFormat w= p.writer;
			final ITypedStructReadFormat  r= p.reader;
			w.open();
			w.begin("struct_1");
				w.writeIntBlock(new int[]{33,220,303});
			w.end();
			w.writeInt(104);
			w.close();
			r.open();
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue(r.peek()==TElement.SIG);
				Assert.assertTrue(r.depth()==0);
				
				Assert.assertTrue("struct_1".equals(r.next()));
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(r.peek()==TElement.INT_BLK);
				Assert.assertTrue(r.depth()==1);
				
				Assert.assertTrue(r.readIntBlock()==33);
				Assert.assertTrue(r.depth()==1);
				
				Assert.assertTrue(r.peek()==TElement.INT_BLK);
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(r.readIntBlock()==220);
				
				Assert.assertTrue(r.peek()==TElement.INT_BLK);
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(r.readIntBlock()==303);
				
				Assert.assertTrue(r.peek()==TElement.INT_BLK);
				Assert.assertTrue(r.depth()==1);
				
				Assert.assertTrue(r.readIntBlock(new int[10])==-1);
				
				Assert.assertTrue(r.peek()==TElement.INT_BLK);
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(r.readIntBlock(new int[10])==-1);
				
				r.skip();
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue(r.peek()==TElement.INT);
				Assert.assertTrue(r.depth()==0);
				
				Assert.assertTrue(r.readInt()==104);
				
			r.close();
	};
	
};