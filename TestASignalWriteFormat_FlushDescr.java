package sztejkat.abstractfmt;
import sztejkat.abstractfmt.obj.CObjListFormat;
import sztejkat.abstractfmt.obj.CDescrFlushObjListWriteFormat;
import java.io.IOException;
/**
	A test for {@link ASignalWriteFormat} type information
	made around {@link CDescrFlushObjListWriteFormat}	
*/
public class TestASignalWriteFormat_FlushDescr extends sztejkat.utils.test.ATest
{
	@org.junit.Test public void testPrimitiveTypePresence()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly.
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.writeBoolean(false);
			f.writeByte((byte)-7);
			f.writeChar('c');
			f.writeShort((short)44);
			f.writeInt(20394);
			f.writeLong(3344909);
			f.writeFloat(3.555f);
			f.writeDouble(49.5);
			f.close();
			
			System.out.println(m);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_BOOLEAN);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof Boolean);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_BOOLEAN);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_BYTE);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof Byte);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_BYTE);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_CHAR);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof Character);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_CHAR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_SHORT);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof Short);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_SHORT);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_INT);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof Integer);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_INT);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_LONG);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof Long);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_LONG);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_FLOAT);			
			org.junit.Assert.assertTrue(m.pollFirst() instanceof Float);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_FLOAT);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_DOUBLE);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof Double);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_DOUBLE);
		leave();
	};
	@org.junit.Test public void testBlockTypePresence1()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly when doing block ops
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("x");
			f.writeBooleanBlock(new boolean[3]);
			f.writeBooleanBlock(new boolean[3]);
			f.end();
			f.close();
			
			System.out.println(m);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("x".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_BOOLEAN_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof boolean[]);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof boolean[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_BOOLEAN_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	
	@org.junit.Test public void testBlockTypePresence2()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly when doing block ops
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("x");
			f.writeByteBlock(new byte[3]);
			f.writeByteBlock(new byte[3]);
			f.end();
			f.close();
			
			System.out.println(m);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("x".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_BYTE_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof byte[]);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof byte[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_BYTE_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	
	@org.junit.Test public void testBlockTypePresence2a()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly when doing block ops
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("x");
			f.writeByteBlock((byte)5);
			f.writeByteBlock(new byte[3]);
			f.end();
			f.close();
			
			System.out.println(m);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("x".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_BYTE_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof byte[]);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof byte[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_BYTE_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	
	
	@org.junit.Test public void testBlockTypePresence3()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly when doing block ops
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("x");
			f.writeCharBlock(new char[3]);
			f.writeCharBlock(new char[3]);
			f.end();
			f.close();
			
			System.out.println(m);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("x".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_CHAR_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof char[]);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof char[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_CHAR_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	
	@org.junit.Test public void testBlockTypePresence4()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly when doing block ops
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("x");
			f.writeShortBlock(new short[3]);
			f.writeShortBlock(new short[3]);
			f.end();
			f.close();
			
			System.out.println(m);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("x".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_SHORT_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof short[]);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof short[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_SHORT_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	@org.junit.Test public void testBlockTypePresence5()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly when doing block ops
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("x");
			f.writeIntBlock(new int[3]);
			f.writeIntBlock(new int[3]);
			f.end();
			f.close();
			
			System.out.println(m);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("x".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_INT_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof int[]);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof int[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_INT_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	@org.junit.Test public void testBlockTypePresence6()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly when doing block ops
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("x");
			f.writeLongBlock(new long[3]);
			f.writeLongBlock(new long[3]);
			f.end();
			f.close();
			
			System.out.println(m);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("x".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_LONG_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof long[]);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof long[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_LONG_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	@org.junit.Test public void testBlockTypePresence7()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly when doing block ops
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("x");
			f.writeFloatBlock(new float[3]);
			f.writeFloatBlock(new float[3]);
			f.end();
			f.close();
			
			System.out.println(m);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("x".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof float[]);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof float[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	@org.junit.Test public void testBlockTypePresence8()throws IOException
	{
		enter();
			/*
				In this test we check if we type information (begin only)
				is put in stream correctly when doing block ops
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CDescrFlushObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("x");
			f.writeDoubleBlock(new double[3]);
			f.writeDoubleBlock(new double[3]);
			f.end();
			f.close();
			
			System.out.println(m);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("x".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.TYPE_DOUBLE_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof double[]);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof double[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.FLUSH_DOUBLE_BLOCK);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
};
