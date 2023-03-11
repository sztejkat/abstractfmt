package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
import static sztejkat.abstractfmt.ITypedStructReadFormat.TElement;

/**
	A test case running skip() and depth() operation 
	in various scenarios. Especially indented to expose
	transparency of typed streams.
*/
public class ATestCase_SkipAndDepth extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{
	@Test public void testSkippingStructureZero()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("struct_1");
				w.writeInt(33);
			w.end();
			w.writeInt(104);
			w.close();
			r.open();
				Assert.assertTrue("struct_1".equals(r.next()));
				r.skip();
				Assert.assertTrue(104==r.readInt());
			r.close();
	};
	
	@Test public void testDepthInformationStructureZero()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("struct_1");
				w.writeInt(33);
			w.end();
			w.writeInt(104);
			w.close();
			r.open();
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue("struct_1".equals(r.next()));
				Assert.assertTrue(r.depth()==1);
				r.skip();
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue(104==r.readInt());
			r.close();
	};
	
	
	@Test public void testSkippingStructureOne()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("struct_1");
				w.writeInt(33);
				w.begin("struct_2");
					w.writeInt(44);
					w.writeInt(39);
				w.end();
			w.end();
			w.writeInt(104);
			w.close();
			r.open();
				Assert.assertTrue("struct_1".equals(r.next()));
				Assert.assertTrue("struct_2".equals(r.next()));
				Assert.assertTrue(44==r.readInt());
				r.skip(1);
				Assert.assertTrue(104==r.readInt());
			r.close();
	};
	
	
	@Test public void testDepthStructureOne()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("struct_1");
				w.writeInt(33);
				w.begin("struct_2");
					w.writeInt(44);
					w.writeInt(39);
				w.end();
			w.end();
			w.writeInt(104);
			w.close();
			r.open();
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue("struct_1".equals(r.next()));
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue("struct_2".equals(r.next()));
				Assert.assertTrue(r.depth()==2);
				Assert.assertTrue(44==r.readInt());
				Assert.assertTrue(r.depth()==2);
				r.skip(1);
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue(104==r.readInt());
			r.close();
	};
	
	
	
	@Test public void testDepthStructureInBlock()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("struct_1");
				w.writeIntBlock(new int[]{33,100,1000});
			w.end();
			w.writeInt(104);
			w.close();
			r.open();
				Assert.assertTrue(r.depth()==0);
				Assert.assertTrue("struct_1".equals(r.next()));
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(33==r.readIntBlock());
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(100==r.readIntBlock());
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(1000==r.readIntBlock());
				Assert.assertTrue(-1==r.readIntBlock(new int[10]));
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(-1==r.readIntBlock(new int[10]));
				Assert.assertTrue(r.depth()==1);
				Assert.assertTrue(r.next()==null);
				Assert.assertTrue(r.depth()==0);
			r.close();
	};
};