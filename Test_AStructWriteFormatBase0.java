package sztejkat.abstractfmt;
import sztejkat.abstractfmt.obj.*;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
/**
	A test bed for {@link AStructWriteFormatBase0}, stand alone junit tests.
	<p>
	Note: Those tests are based on {@link CObjStructWriteFormat0} implementation
	since this is an easiest way to check how exactly the write format do behave
	in its implementation-specific aspects
*/
public class Test_AStructWriteFormatBase0 extends sztejkat.abstractfmt.test.ATest
{
					/** Just a convinience implementation, if for the future I would have to
					change it or modify */
					private static final class DUT extends CObjStructWriteFormat0
					{
						public DUT(boolean end_begin_enabled,
											  int max_supported_recursion_depth,
											  int max_supported_name_length
											  ){ super(end_begin_enabled,
														max_supported_recursion_depth,
														max_supported_name_length
														);};
								int _openImpl;	
						@Override protected void openImpl()throws IOException{_openImpl++;};
								int _closeImpl;
						@Override protected void closeImpl()throws IOException{_closeImpl++;};
								int _flushImpl;
						@Override protected void flushImpl()throws IOException
						{
							Assert.assertTrue(_closeImpl==0);
							_flushImpl++;
						};
					};
					
	private static void printStream(Collection<IObjStructFormat0> s)
	{
		System.out.println("Stream length:"+s.size());
		int i=0;
		System.out.println("start of stream");
		for(IObjStructFormat0 o: s)
		{
			System.out.println("["+(i++)+"]{"+o+"}");
		};
		System.out.println("end of stream");
	};
		
	@Test public void testIfFlushesBeforeClose_A()throws IOException
	{
		/* Check if flush() is invoked before closing() */
		enter();
			DUT d = new DUT(true,-1,2048);
			d.open();
			d.close();
			Assert.assertTrue(d._closeImpl==1);
			Assert.assertTrue(d._flushImpl==1);
		leave();
	}; 
	@Test public void testIfFlushesBeforeClose_B()throws IOException
	{
		/* Check if flush() is invoked before closing() */
		enter();
			DUT d = new DUT(false,-1,2048);
			d.open();
			d.close();
			Assert.assertTrue(d._closeImpl==1);
			Assert.assertTrue(d._flushImpl==1);
		leave();
	}; 
	
	
	@Test public void testEndBeginOptimization()throws IOException
	{
		/* Check if end-begin is optimized when requested. */
		enter();
			DUT d = new DUT(true,-1,2048);
			d.open();
				d.begin("andy");
				d.end();
				d.begin("sandy");
				d.end();
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("andy").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("sandy").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	}; 
	
	@Test public void testEndBeginOptimizationOff()throws IOException
	{
		/* Check if end-begin is not optimized when requested. */
		enter();
			DUT d = new DUT(false,-1,2048);
			d.open();
				d.begin("andy");
				d.end();
				d.begin("sandy");
				d.end();
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("andy").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("sandy").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	}; 
	
	
	@Test public void testEndBeginOptimizationInDepth()throws IOException
	{
		/* Check if end-begin is optimized when requested. */
		enter();
			DUT d = new DUT(true,-1,2048);
			d.open();
				d.begin("andy");
				d.end();
				d.begin("sandy");
				d.end();
				d.begin("sandy");
				d.end();
				d.begin("sandy");				
				d.end();
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("andy").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("sandy").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("sandy").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("sandy").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	}; 
	
	@Test public void testEndBeginOptimizationInDepthWithFlush()throws IOException
	{
		/* Check if end-begin is optimized when requested
		and if flush() flushes pending end signal */
		enter();
			DUT d = new DUT(true,-1,2048);
			d.open();
				d.begin("andy");
				d.end();
				d.begin("sandy");
				d.end();
				d.begin("sandy");
				d.end();
				d.flush(); //this should flush pending end without awaiting for begin.
				d.begin("sandy");				
				d.end();
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("andy").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("sandy").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("sandy").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("sandy").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	}; 
	
	@Test public void testEndBeginOptimizationInNesting()throws IOException
	{
		/* Check if end-begin is optimized when requested. */
		enter();
			DUT d = new DUT(true,-1,2048);
			d.open();
				d.begin("andy");
				d.end();
				d.begin("sandy");
					d.begin("jonny");
					d.end();
				d.end();
				d.begin("randy");
				d.end();
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("andy").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("sandy").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("jonny").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("randy").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
			
		leave();
	}; 
	
	@Test public void testIfDetectsAdditionalEnd()throws IOException
	{
		/* We check if it detects unnecessary end */
		enter();
			DUT d = new DUT(true,-1,2048);
			d.open();
				d.begin("andy");
				d.end();
				try{
						d.end();
						Assert.fail();
					}catch(EFormatBoundaryExceeded ex)
					{
						System.out.println(ex);
					};
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("andy").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	}; 
	
	
	@Test public void testIfDetectsTooDeepRecursion_0()throws IOException
	{
		/* We check if it detects unnecessary end */
		enter();
			DUT d = new DUT(true,0,2048);
			d.open();
				try{
						d.begin("andy");
						Assert.fail();
					}catch(EFormatBoundaryExceeded ex)
					{
						System.out.println(ex);
					};
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(!I.hasNext());
		leave();
	}; 
	@Test public void testIfDetectsTooDeepRecursion_1a()throws IOException
	{
		/* We check if it detects unnecessary end */
		enter();
			DUT d = new DUT(true,1,2048);
			d.open();
				d.begin("rolly");
				try{
						d.begin("andy");
						Assert.fail();
					}catch(EFormatBoundaryExceeded ex)
					{
						System.out.println(ex);
					};
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("rolly").equalsTo(I.next()));			
			Assert.assertTrue(!I.hasNext());
		leave();
	}; 
	@Test public void testIfDetectsTooDeepRecursion_1b()throws IOException
	{
		/* We check if it detects unnecessary end */
		enter();
			DUT d = new DUT(false,1,2048);
			d.open();
				d.begin("rorry");
				d.end();
				d.begin("morice");
				try{
						d.begin("andy");
						Assert.fail();
					}catch(EFormatBoundaryExceeded ex)
					{
						System.out.println(ex);
					};
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("rorry").equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("morice").equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	}; 
	
	
	@Test public void testIfDetectsTooLongName()throws IOException
	{
		/* We check if it detects unnecessary end */
		enter();
			DUT d = new DUT(true,-1,3);
			d.open();
				d.begin("rol");
				try{
						d.begin("andy");
						Assert.fail();
					}catch(EFormatBoundaryExceeded ex)
					{
						System.out.println(ex);
					};
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("rol").equalsTo(I.next()));			
			Assert.assertTrue(!I.hasNext());
		leave();
	}; 
	
	
	@Test public void testWritingPrimitives()throws IOException
	{
		/* We check if it allows us to write elementary primitives
		without an enclosing structure */
		enter();
			DUT d = new DUT(true,-1,100);
			d.open();
				d.writeBoolean(false);
				d.writeByte((byte)-3);
				d.writeChar('x');
				d.writeShort((short)-1345);
				d.writeInt(333488);
				d.writeLong(999843848L);
				d.writeFloat(1.44f);
				d.writeDouble(33e3);
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(false).equalsTo(I.next()));
			Assert.assertTrue(ELMT_BYTE.valueOf((byte)-3).equalsTo(I.next()));
			Assert.assertTrue(ELMT_CHAR.valueOf('x').equalsTo(I.next()));
			Assert.assertTrue(ELMT_SHORT.valueOf((short)-1345).equalsTo(I.next()));
			Assert.assertTrue(ELMT_INT.valueOf(333488).equalsTo(I.next()));
			Assert.assertTrue(ELMT_LONG.valueOf(999843848L).equalsTo(I.next()));
			Assert.assertTrue(ELMT_FLOAT.valueOf(1.44f).equalsTo(I.next()));
			Assert.assertTrue(ELMT_DOUBLE.valueOf(33e3).equalsTo(I.next()));			
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	
	
	@Test public void testWritingBooleanBlock_A()throws IOException
	{
		/* We check if it allows us to write elementary primitives
		followed by structure with a primitive and two block chunks */
		enter();
			DUT d = new DUT(true,-1,1000);
			d.open();
				d.writeBoolean(false);
				d.begin("array");
					d.writeBoolean(true);
					d.writeBooleanBlock(new boolean[]{true,false,false});
					d.writeBooleanBlock(new boolean[]{true,false,false},1,2);
				d.end();
				d.writeBoolean(true);
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(false).equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("array").equalsTo(I.next()));
				Assert.assertTrue(ELMT_BOOLEAN.valueOf(true).equalsTo(I.next()));
				Assert.assertTrue(BLK_BOOLEAN.valueOf(true).equalsTo(I.next()));
				Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
				Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
				
				Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
				Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
				
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(true).equalsTo(I.next()));			
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	
	@Test public void testWritingBooleanBlock_B()throws IOException
	{
		/* We check if it allows us to write elementary primitives
		followed by structure with a primitive and two block chunks */
		enter();
			DUT d = new DUT(true,-1,1000);
			d.open();
				d.writeBoolean(false);
				d.begin("array");
					d.writeBooleanBlock(new boolean[]{true,false,false});
					d.writeBooleanBlock(new boolean[]{true,false,false},1,2);
				d.end();
				d.writeBoolean(false);
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(false).equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("array").equalsTo(I.next()));
				Assert.assertTrue(BLK_BOOLEAN.valueOf(true).equalsTo(I.next()));
				Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
				Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
				
				Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
				Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
				
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(false).equalsTo(I.next()));			
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	
	@Test public void testWritingBlockNested()throws IOException
	{
		/* Check if we can terminate block in nested structure with
			both end and begin signals. We do interlave block
			types to ensure that we check operation is terminated. 
		*/
		enter();
			DUT d = new DUT(true,-1,1000);
			d.open();			
				d.writeBooleanBlock(new boolean[]{true,false,false});
				d.begin("struct");
					d.writeByteBlock(new byte[]{(byte)0,(byte)3});
					d.begin("array");
						d.writeCharBlock(new char[]{'a','b','c'});
					d.end();
					d.writeIntBlock(new int[]{44});
				d.end();
				d.writeDouble(44445);
			d.close();			
			printStream(d.stream);
			
			Iterator<IObjStructFormat0> I = d.stream.iterator();
			Assert.assertTrue(BLK_BOOLEAN.valueOf(true).equalsTo(I.next()));
			Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
			Assert.assertTrue(BLK_BOOLEAN.valueOf(false).equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("struct").equalsTo(I.next()));
				Assert.assertTrue(BLK_BYTE.valueOf((byte)0).equalsTo(I.next()));
				Assert.assertTrue(BLK_BYTE.valueOf((byte)3).equalsTo(I.next()));
				Assert.assertTrue(new SIG_BEGIN("array").equalsTo(I.next()));
					Assert.assertTrue(BLK_CHAR.valueOf('a').equalsTo(I.next()));
					Assert.assertTrue(BLK_CHAR.valueOf('b').equalsTo(I.next()));
					Assert.assertTrue(BLK_CHAR.valueOf('c').equalsTo(I.next()));
				Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
				Assert.assertTrue(BLK_INT.valueOf(44).equalsTo(I.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(I.next()));
			Assert.assertTrue(ELMT_DOUBLE.valueOf(44445).equalsTo(I.next()));			
			Assert.assertTrue(!I.hasNext());
		leave();
	};
}; 