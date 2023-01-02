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
	{@link CTypedStructReadFormat} when implemented over
	the {@link CObjStructReadFormat0}.
	<p>
	This test do check if how it reacts on manually crafted data.
*/
public class Test_CTypedStructReadFormat extends ATest
{
	
	/** How does it react when there is not type info? 
	@throws IOException .*/
	@Test public void test_how_reacts_on_missing_type_info()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(ELMT_BOOLEAN.valueOf(true));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			try{
				r.readBoolean();
				Assert.fail("should have thrown");
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			
			
		leave();
	};
	
	
	/** How does it react when there is are type info? 
	@throws IOException .*/
	@Test public void testBoolean()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(new SIG_BEGIN("bool"));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(ELMT_BOOLEAN.valueOf(true));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(SIG_END.INSTANCE);
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			Assert.assertTrue(r.readBoolean()==false);
			Assert.assertTrue(r.readBoolean()==true);
			Assert.assertTrue(r.readBoolean()==false);
			Assert.assertTrue(!r.hasElementaryData());
			try{
				r.readBoolean();
				Assert.fail();
			}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	/**  
	@throws IOException .*/
	@Test public void testCanPeekType_boolean()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(new SIG_BEGIN("bool"));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(ELMT_BOOLEAN.valueOf(true));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(SIG_END.INSTANCE);
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			Assert.assertTrue(r.readBoolean()==false);
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			Assert.assertTrue(r.readBoolean()==true);
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			Assert.assertTrue(r.readBoolean()==false);
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.EOF);
			
		leave();
	};
	
	/**  
	@throws IOException .*/
	@Test public void testCanPeekType_boolean_loop()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(new SIG_BEGIN("bool"));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(ELMT_BOOLEAN.valueOf(true));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(SIG_END.INSTANCE);
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			for(int i=0;i<10;i++)
			{
				Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			};
			Assert.assertTrue(r.readBoolean()==false);
			for(int i=0;i<10;i++)
			{
				Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			};
			Assert.assertTrue(r.readBoolean()==true);
			for(int i=0;i<10;i++)
			{
				Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			};
			Assert.assertTrue(r.readBoolean()==false);
			for(int i=0;i<10;i++)
			{
				Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.EOF);
			};
			
		leave();
	};
	
	/**  
	@throws IOException .*/
	@Test public void testDetectsInvalidRead_boolean()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(new SIG_BEGIN("bool"));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(ELMT_BOOLEAN.valueOf(true));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(SIG_END.INSTANCE);
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			Assert.assertTrue(r.readBoolean()==false);
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			try{
				r.readByte();
				Assert.fail();
			}catch(ETypeMissmatch ex){System.out.println(ex); };
			
		leave();
	};
	
	/**  
	@throws IOException .*/
	@Test public void testDetectsInvalidBlockRead_boolean()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(new SIG_BEGIN("bool"));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(ELMT_BOOLEAN.valueOf(true));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(SIG_END.INSTANCE);
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			Assert.assertTrue(r.readBoolean()==false);
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN);
			try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(ETypeMissmatch ex){System.out.println(ex); };
			
		leave();
	};
	
	
	/**  
	@throws IOException .*/
	@Test public void testDetectsTypeChage_boolean_to_byte()throws IOException
	{
		//Note: Type change detections do heavily peek so there is no need
		//	    to test peek() separately.
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(new SIG_BEGIN("bool"));
			stream.add(ELMT_BOOLEAN.valueOf(false));
			stream.add(ELMT_BOOLEAN.valueOf(true));
			stream.add(SIG_END.INSTANCE);
			stream.add(new SIG_BEGIN("byte"));
			stream.add(ELMT_BYTE.valueOf((byte)3));
			stream.add(ELMT_BYTE.valueOf((byte)7));
			stream.add(SIG_END.INSTANCE);
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			Assert.assertTrue(r.readBoolean()==false);
			Assert.assertTrue(r.readBoolean()==true);
			Assert.assertTrue(r.readByte()==(byte)3);
			Assert.assertTrue(r.readByte()==(byte)7);
			try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EEof ex){System.out.println(ex); };
			
		leave();
	};
	
	/**  
	@throws IOException .*/
	@Test public void testDetectsTypeChage_short_to_int_to_long()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(new SIG_BEGIN("short"));
			stream.add(ELMT_SHORT.valueOf((short)1));
			stream.add(ELMT_SHORT.valueOf((short)2));
			stream.add(SIG_END.INSTANCE);
			stream.add(new SIG_BEGIN("int"));
			stream.add(ELMT_INT.valueOf(3));
			stream.add(ELMT_INT.valueOf(7));
			stream.add(SIG_END.INSTANCE);
			stream.add(new SIG_BEGIN("long"));
			stream.add(ELMT_LONG.valueOf(3));
			stream.add(ELMT_LONG.valueOf(7));
			stream.add(SIG_END.INSTANCE);
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			Assert.assertTrue(r.readShort()==1);
			Assert.assertTrue(r.readShort()==2);
			Assert.assertTrue(r.readInt()==3);
			Assert.assertTrue(r.readInt()==7);
			Assert.assertTrue(r.readLong()==3);
			Assert.assertTrue(r.readLong()==7);
		leave();
	};
	
	
	/**  
	@throws IOException .*/
	@Test public void testDetectsTypeChage_float_to_double()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(new SIG_BEGIN("float"));
			stream.add(ELMT_FLOAT.valueOf(3));
			stream.add(ELMT_FLOAT.valueOf(7));
			stream.add(SIG_END.INSTANCE);
			stream.add(new SIG_BEGIN("double"));
			stream.add(ELMT_DOUBLE.valueOf(3));
			stream.add(ELMT_DOUBLE.valueOf(7));
			stream.add(SIG_END.INSTANCE);
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			Assert.assertTrue(r.readFloat()==3);
			Assert.assertTrue(r.readFloat()==7);
			Assert.assertTrue(r.readDouble()==3);
			Assert.assertTrue(r.readDouble()==7);
		leave();
	};
	
	
	
	/**  
	@throws IOException .*/
	@Test public void testBlockOpEnd_boolean()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream = 
					new CAddablePollableArrayList<IObjStructFormat0>();
					
			stream.add(new SIG_BEGIN("/bool[]"));//user signal!
			stream.add(new SIG_BEGIN("bool[]"));
			stream.add(BLK_BYTE.valueOf((byte)3));
			stream.add(BLK_BYTE.valueOf((byte)3));
			stream.add(BLK_BYTE.valueOf((byte)3));
			stream.add(BLK_BYTE.valueOf((byte)3));
			stream.add(SIG_END.INSTANCE);
			stream.add(SIG_END.INSTANCE); //user end
			//second array
			stream.add(new SIG_BEGIN("2"));//user signal!
			stream.add(new SIG_BEGIN("bool[]"));
			stream.add(BLK_BYTE.valueOf((byte)3));
			stream.add(BLK_BYTE.valueOf((byte)3));
			stream.add(BLK_BYTE.valueOf((byte)3));
			stream.add(BLK_BYTE.valueOf((byte)3));
			stream.add(SIG_END.INSTANCE);
			stream.add(SIG_END.INSTANCE); //user end
			
			
			CObjStructReadFormat0 backend =
				new CObjStructReadFormat0(
								  stream,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024 //int max_supported_name_length
								  ); 
			CTypedStructReadFormat r = new CTypedStructReadFormat(
											backend,//IStructReadFormat engine, 
											'/',//char escape,
											CTypedStructWriteFormat.DEFAULT_TYPE_NAMES //String [] type_names
											);
			r.open();
			Assert.assertTrue("bool[]".equals(r.next()));
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			r.readBooleanBlock();
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			r.readBooleanBlock();
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			r.readBooleanBlock();
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			r.readBooleanBlock();
			//still should serve the block.
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			try{
					r.readBooleanBlock();
					Assert.fail();
			}catch(ENoMoreData ex){ System.out.println(ex); };
			Assert.assertTrue(r.readBooleanBlock(new boolean[1])==-1);
			
			//And next block.
			Assert.assertTrue(r.next()==null);
			Assert.assertTrue("2".equals(r.next()));
				Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			r.readBooleanBlock();
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			r.readBooleanBlock();
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			r.readBooleanBlock();
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			r.readBooleanBlock();
			//still should serve the block.
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			Assert.assertTrue(r.peek()==ITypedStructReadFormat.TElement.BOOLEAN_BLK);
			try{
					r.readBooleanBlock();
					Assert.fail();
			}catch(ENoMoreData ex){ System.out.println(ex); };
			Assert.assertTrue(r.readBooleanBlock(new boolean[1])==-1);
		leave();
	};
};