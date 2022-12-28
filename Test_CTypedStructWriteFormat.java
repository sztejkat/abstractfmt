package sztejkat.abstractfmt;
import sztejkat.abstractfmt.obj.*;
import sztejkat.abstractfmt.utils.CAddablePollableArrayList;
import sztejkat.abstractfmt.test.*;
import java.util.Iterator;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
import static sztejkat.abstractfmt.Test_AStructWriteFormatBase0.printStream;
/**
	A bunch of tests designed to validate 
	{@link CTypedStructWriteFormat} when implemented over
	the {@link CObjStructWriteFormat0}.
	<p>
	This test do check if a proper data are produced.
*/
public class Test_CTypedStructWriteFormat extends ATest
{
	/** Just make sure default type info is consistently defined */
	@Test public void ensureDefaultNameIndexingIsCorrect()
	{
		enter();
		Assert.assertTrue("bool".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.BOOLEAN_idx]));
		Assert.assertTrue("short".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.SHORT_idx]));
		Assert.assertTrue("char".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.CHAR_idx]));
		Assert.assertTrue("int".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.INT_idx]));
		Assert.assertTrue("long".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.LONG_idx]));
		Assert.assertTrue("float".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.FLOAT_idx]));
		Assert.assertTrue("double".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.DOUBLE_idx]));
		
		Assert.assertTrue("str".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.STRING_blk_idx]));
		Assert.assertTrue("bool[]".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.BOOLEAN_blk_idx]));
		Assert.assertTrue("short[]".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.SHORT_blk_idx]));
		Assert.assertTrue("char[]".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.CHAR_blk_idx]));
		Assert.assertTrue("int[]".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.INT_blk_idx]));
		Assert.assertTrue("long[]".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.LONG_blk_idx]));
		Assert.assertTrue("float[]".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.FLOAT_blk_idx]));
		Assert.assertTrue("double[]".equals(CTypedStructWriteFormat.DEFAULT_TYPE_NAMES[CTypedStructWriteFormat.DOUBLE_blk_idx]));
		
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx>CTypedStructWriteFormat.BOOLEAN_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx>CTypedStructWriteFormat.BYTE_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx>CTypedStructWriteFormat.CHAR_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx>CTypedStructWriteFormat.SHORT_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx>CTypedStructWriteFormat.INT_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx>CTypedStructWriteFormat.LONG_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx>CTypedStructWriteFormat.FLOAT_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx>CTypedStructWriteFormat.DOUBLE_idx);
		
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx<CTypedStructWriteFormat.BOOLEAN_blk_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx<CTypedStructWriteFormat.BYTE_blk_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx<CTypedStructWriteFormat.CHAR_blk_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx<CTypedStructWriteFormat.SHORT_blk_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx<CTypedStructWriteFormat.INT_blk_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx<CTypedStructWriteFormat.LONG_blk_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx<CTypedStructWriteFormat.FLOAT_blk_idx);
		Assert.assertTrue(CTypedStructWriteFormat.STRING_blk_idx<CTypedStructWriteFormat.DOUBLE_blk_idx);
		leave();
	};
	
	/** Check if elementary type info is stored correctly 
	@throws IOException .*/
	@Test public void testElementary_boolean_flat()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructWriteFormat0 backend =
				new CObjStructWriteFormat0(
								  false,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  stream //IAddable<IObjStructFormat0> stream
								  ); 
			CTypedStructWriteFormat w = new CTypedStructWriteFormat(
											backend,//IStructWriteFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			//produce elementary operation
			w.open();
			w.writeBoolean(false);
			w.close();	//is it flushed after single op?
			
			printStream(stream);
			Iterator<IObjStructFormat0> i = stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("bool").equalsTo(i.next()));
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(false).equalsTo(i.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(i.next()));
			Assert.assertTrue(!i.hasNext());
		leave();
	};
	
	/** Check if elementary type info is stored correctly 
	@throws IOException .*/
	@Test public void testElementary_boolean_flat_seq()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructWriteFormat0 backend =
				new CObjStructWriteFormat0(
								  false,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  stream //IAddable<IObjStructFormat0> stream
								  ); 
			CTypedStructWriteFormat w = new CTypedStructWriteFormat(
											backend,//IStructWriteFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			//produce elementary operation
			w.open();
			w.writeBoolean(false);
			w.writeBoolean(true); //is it flushed after dual op? Are dual ops stitched?
			w.close();
			
			printStream(stream);
			Iterator<IObjStructFormat0> i = stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("bool").equalsTo(i.next()));
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(false).equalsTo(i.next()));
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(true).equalsTo(i.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(i.next()));
			Assert.assertTrue(!i.hasNext());
		leave();
	};
	
	/** Check if elementary type info is stored correctly 
	@throws IOException .*/
	@Test public void testElementary_boolean_end_terminates()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructWriteFormat0 backend =
				new CObjStructWriteFormat0(
								  false,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  stream //IAddable<IObjStructFormat0> stream
								  ); 
			CTypedStructWriteFormat w = new CTypedStructWriteFormat(
											backend,//IStructWriteFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			//produce elementary operation
			w.open();
			w.begin("bool[]"); //intentional conflict
			w.writeBoolean(false);
			w.writeBoolean(true); //is it flushed after dual op? Are dual ops stitched?
			w.end();
			w.close();
			
			printStream(stream);
			Iterator<IObjStructFormat0> i = stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("/bool[]").equalsTo(i.next()));//escaped
			Assert.assertTrue(new SIG_BEGIN("bool").equalsTo(i.next()));
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(false).equalsTo(i.next()));
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(true).equalsTo(i.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(i.next()));  //type end
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(i.next())); //user end
			Assert.assertTrue(!i.hasNext());
		leave();
	};
	
	/** Check if elementary type info is stored correctly 
	@throws IOException .*/
	@Test public void testElementary_boolean_begin_terminates()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructWriteFormat0 backend =
				new CObjStructWriteFormat0(
								  false,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  stream //IAddable<IObjStructFormat0> stream
								  ); 
			CTypedStructWriteFormat w = new CTypedStructWriteFormat(
											backend,//IStructWriteFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			//produce elementary operation
			w.open();
			w.begin("bool[]"); //intentional conflict
			w.writeBoolean(false);
			w.writeBoolean(true); //is it flushed after dual op? Are dual ops stitched?
			w.begin("marcie");
			w.close();
			
			printStream(stream);
			Iterator<IObjStructFormat0> i = stream.iterator();
			Assert.assertTrue(new SIG_BEGIN("/bool[]").equalsTo(i.next()));//escaped
			Assert.assertTrue(new SIG_BEGIN("bool").equalsTo(i.next()));
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(false).equalsTo(i.next()));
			Assert.assertTrue(ELMT_BOOLEAN.valueOf(true).equalsTo(i.next()));
			Assert.assertTrue(SIG_END.INSTANCE.equalsTo(i.next()));  //type end
			Assert.assertTrue(new SIG_BEGIN("marcie").equalsTo(i.next()));
			Assert.assertTrue(!i.hasNext());
		leave();
	};
};
