package sztejkat.abstractfmt;
import sztejkat.abstractfmt.obj.CObjListFormat;
import sztejkat.abstractfmt.obj.CDescrObjListReadFormat;
import java.io.IOException;

/**
	A test for {@link ASignalReadFormat} using {@link CDescrObjListReadFormat} test vehicle.
	<p>
	Those test are run using hand-crafted stream data (not inter-operational write-read tests)
	for described streams.
	Interop tests are run inside <code>sztejkat.abstractfmt.obj</code> package using
	various test vehicles.
*/
public class TestASignalReadFormat_Descr extends sztejkat.utils.test.ATest
{
	
	
	
	/* ---------------------------------------------------------------------------
	
			Typed elementary primitive reads.
	
	---------------------------------------------------------------------------*/
	@org.junit.Test public void testTypedPrimitiveReads_1()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data can be safely
				read from typed stream format.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(true));
			m.add(CObjListFormat.TYPE_BYTE);
			m.add(new Byte((byte)-3));
			m.add(CObjListFormat.TYPE_CHAR);
			m.add(new Character('z'));
			m.add(CObjListFormat.TYPE_SHORT);
			m.add(new Short((short)3490));
			m.add(CObjListFormat.TYPE_INT);
			m.add(new Integer(999999));
			m.add(CObjListFormat.TYPE_LONG);
			m.add(new Long(-349L));
			m.add(CObjListFormat.TYPE_FLOAT);
			m.add(new Float(0.0049f));
			m.add(CObjListFormat.TYPE_DOUBLE);
			m.add(new Double(0.0149));
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			org.junit.Assert.assertTrue(f.readBoolean()==true);
			org.junit.Assert.assertTrue(f.readByte()==(byte)-3);
			org.junit.Assert.assertTrue(f.readChar()=='z');
			org.junit.Assert.assertTrue(f.readShort()==(short)3490);
			org.junit.Assert.assertTrue(f.readInt()==999999);
			org.junit.Assert.assertTrue(f.readLong()==-349L);
			org.junit.Assert.assertTrue(f.readFloat()==0.0049f);
			org.junit.Assert.assertTrue(f.readDouble()==0.0149);
			
		leave();
	};
	
	
	
	/* ---------------------------------------------------------------------------
	
			Typed and flushed elementary primitive reads.
	
	---------------------------------------------------------------------------*/
	@org.junit.Test public void testFullyTypedPrimitiveReads_1()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data can be safely
				read from typed-flushed stream format.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.FLUSH_BOOLEAN);
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(true));
			m.add(CObjListFormat.FLUSH_BOOLEAN);
			m.add(CObjListFormat.TYPE_BYTE);
			m.add(new Byte((byte)-3));
			m.add(CObjListFormat.FLUSH_BYTE);
			m.add(CObjListFormat.TYPE_CHAR);
			m.add(new Character('z'));
			m.add(CObjListFormat.FLUSH_CHAR);
			m.add(CObjListFormat.TYPE_SHORT);
			m.add(new Short((short)3490));
			m.add(CObjListFormat.FLUSH_SHORT);
			m.add(CObjListFormat.TYPE_INT);
			m.add(new Integer(999999));
			m.add(CObjListFormat.FLUSH_INT);
			m.add(CObjListFormat.TYPE_LONG);			
			m.add(new Long(-349L));
			m.add(CObjListFormat.FLUSH_LONG);
			m.add(CObjListFormat.TYPE_FLOAT);
			m.add(new Float(0.0049f));
			m.add(CObjListFormat.FLUSH_FLOAT);
			m.add(CObjListFormat.TYPE_DOUBLE);
			m.add(new Double(0.0149));
			m.add(CObjListFormat.FLUSH_DOUBLE);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			org.junit.Assert.assertTrue(f.readBoolean()==true);
			org.junit.Assert.assertTrue(f.readByte()==(byte)-3);
			org.junit.Assert.assertTrue(f.readChar()=='z');
			org.junit.Assert.assertTrue(f.readShort()==(short)3490);
			org.junit.Assert.assertTrue(f.readInt()==999999);
			org.junit.Assert.assertTrue(f.readLong()==-349L);
			org.junit.Assert.assertTrue(f.readFloat()==0.0049f);
			org.junit.Assert.assertTrue(f.readDouble()==0.0149);
			
		leave();
	};
	
	
	@org.junit.Test public void testFullyTypedPrimitiveReads_2()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data can be safely
				read from typed stream format, but with generic flush
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.FLUSH);
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(true));
			m.add(CObjListFormat.FLUSH);
			m.add(CObjListFormat.TYPE_BYTE);
			m.add(new Byte((byte)-3));
			m.add(CObjListFormat.FLUSH);
			m.add(CObjListFormat.TYPE_CHAR);
			m.add(new Character('z'));
			m.add(CObjListFormat.FLUSH);
			m.add(CObjListFormat.TYPE_SHORT);
			m.add(new Short((short)3490));
			m.add(CObjListFormat.FLUSH);
			m.add(CObjListFormat.TYPE_INT);
			m.add(new Integer(999999));
			m.add(CObjListFormat.FLUSH);
			m.add(CObjListFormat.TYPE_LONG);			
			m.add(new Long(-349L));
			m.add(CObjListFormat.FLUSH);
			m.add(CObjListFormat.TYPE_FLOAT);
			m.add(new Float(0.0049f));
			m.add(CObjListFormat.FLUSH);
			m.add(CObjListFormat.TYPE_DOUBLE);
			m.add(new Double(0.0149));
			m.add(CObjListFormat.FLUSH);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			org.junit.Assert.assertTrue(f.readBoolean()==true);
			org.junit.Assert.assertTrue(f.readByte()==(byte)-3);
			org.junit.Assert.assertTrue(f.readChar()=='z');
			org.junit.Assert.assertTrue(f.readShort()==(short)3490);
			org.junit.Assert.assertTrue(f.readInt()==999999);
			org.junit.Assert.assertTrue(f.readLong()==-349L);
			org.junit.Assert.assertTrue(f.readFloat()==0.0049f);
			org.junit.Assert.assertTrue(f.readDouble()==0.0149);
			
		leave();
	};
	
	
	@org.junit.Test public void testFullyTypedPrimitiveReads_3()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data can be safely
				read from typed stream format, but with bulk flush
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.FLUSH_ANY);
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(true));
			m.add(CObjListFormat.FLUSH_ANY);
			m.add(CObjListFormat.TYPE_BYTE);
			m.add(new Byte((byte)-3));
			m.add(CObjListFormat.FLUSH_ANY);
			m.add(CObjListFormat.TYPE_CHAR);
			m.add(new Character('z'));
			m.add(CObjListFormat.FLUSH_ANY);
			m.add(CObjListFormat.TYPE_SHORT);
			m.add(new Short((short)3490));
			m.add(CObjListFormat.FLUSH_ANY);
			m.add(CObjListFormat.TYPE_INT);
			m.add(new Integer(999999));
			m.add(CObjListFormat.FLUSH_ANY);
			m.add(CObjListFormat.TYPE_LONG);			
			m.add(new Long(-349L));
			m.add(CObjListFormat.FLUSH_ANY);
			m.add(CObjListFormat.TYPE_FLOAT);
			m.add(new Float(0.0049f));
			m.add(CObjListFormat.FLUSH_ANY);
			m.add(CObjListFormat.TYPE_DOUBLE);
			m.add(new Double(0.0149));
			m.add(CObjListFormat.FLUSH_ANY);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			org.junit.Assert.assertTrue(f.readBoolean()==true);
			org.junit.Assert.assertTrue(f.readByte()==(byte)-3);
			org.junit.Assert.assertTrue(f.readChar()=='z');
			org.junit.Assert.assertTrue(f.readShort()==(short)3490);
			org.junit.Assert.assertTrue(f.readInt()==999999);
			org.junit.Assert.assertTrue(f.readLong()==-349L);
			org.junit.Assert.assertTrue(f.readFloat()==0.0049f);
			org.junit.Assert.assertTrue(f.readDouble()==0.0149);
			
		leave();
	};
	
	
	
	/*-------------------------------------------------------------------------
	
				Primitive boundary tests.
	
	---------------------------------------------------------------------------*/
	
	
	@org.junit.Test public void testTypedPrimitiveBoundaryRead_1()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data 
				do correctly throw ENoMoreData if begin/end signal is reached.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.FLUSH_BOOLEAN);
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			try{
				f.readDouble();
				org.junit.Assert.fail();
			}catch(ENoMoreData ex){};
		leave();
	};
	
	@org.junit.Test public void testTypedPrimitiveBoundaryRead_2()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data 
				do correctly throw ENoMoreData if begin/end signal is reached.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.FLUSH_BOOLEAN);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			try{
				f.readDouble();
				org.junit.Assert.fail();
			}catch(ENoMoreData ex){};
		leave();
	};
	
	@org.junit.Test public void testTypedPrimitiveBoundaryRead_3()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data 
				do correctly throw ENoMoreData if begin/end signal is reached.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.FLUSH_BOOLEAN);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			try{
				f.readDouble();
				org.junit.Assert.fail();
			}catch(ENoMoreData ex){};
		leave();
	};
	
	
	          
	
	
	
	/*-------------------------------------------------------------------------
	
				Primitive type-check tests.
	
	---------------------------------------------------------------------------*/
	
	@org.junit.Test public void testTypedPrimitiveMissmatch_1()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data type missmatch is detected
				in typed streams.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try{
				f.readDouble();
				org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
		leave();
	};
	
	@org.junit.Test public void testTypedPrimitiveMissmatch_2()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data type missmatch is detected
				in typed streams.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BYTE);
			m.add(new Byte((byte)33));
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try{
				f.readLong();
				org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
		leave();
	};
	
	@org.junit.Test public void testTypedPrimitiveTypeMissed()throws IOException
	{
		enter();
			/*
				In this test we test, if strict mode detects missing start type 
				indicators.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new Byte((byte)33));
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try{
				f.readLong();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){};
		leave();
	};
	
	
	/*-------------------------------------------------------------------------
	
				Flush type missmatch in elementary ops.
				
				
				Note: We assume a copy & paste code in tested class, so just
				few cases are tested. Shame JAVA lacks preprocessor or true
				templates.
	
	---------------------------------------------------------------------------*/
	
	@org.junit.Test public void testTypedPrimitiveFlushMissmatch_1()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data flush missmatch is detected
				in typed streams.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.FLUSH_FLOAT);
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try{
				f.readBoolean();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
		leave();
	};
	
	
	
	/*-------------------------------------------------------------------------
	
				Skipping tests.
	
	---------------------------------------------------------------------------*/
	
	
	
	/*-------------------------------------------------------------------------
	
				Block operations
	
			Note: We test most operations on byte block
			because we assume that the rest is Copy & paste of the same code
			and elementary tests will be sufficient.
			
	---------------------------------------------------------------------------*/
	
	@org.junit.Test public void testBlockSkipTyped()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly skip
				unread content of block.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BYTE_BLOCK);
			m.add(new byte[]{(byte)3,(byte)5,(byte)7,(byte)9});
			m.add(new byte[]{(byte)34,(byte)54});	
			m.add(CObjListFormat.FLUSH_BYTE_BLOCK);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("MARSH");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				byte [] buff = new byte[10];
				int r= f.readByteBlock(buff,1,3);
				System.out.println("r="+r+" buff="+java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==3);
				org.junit.Assert.assertTrue(buff[1]==(byte)3);
				org.junit.Assert.assertTrue(buff[2]==(byte)5);
				org.junit.Assert.assertTrue(buff[3]==(byte)7);;				
			};
			org.junit.Assert.assertTrue("MARSH".equals(f.next()));
			
		leave();
	};
	
	
	
	
	
	/*-------------------------------------------------------------------------
	
				Block operations
				
				Rough tests of typed reads.
				
				Again, we test more detailed just some variants.
				
	--------------------------------------------------------------------------*/
	@org.junit.Test public void testBooleanBlockReadTyped_1()throws IOException
	{
		enter();
		testBooleanBlockReadTyped(CObjListFormat.FLUSH_BOOLEAN_BLOCK);
		leave();
	};
	@org.junit.Test public void testBooleanBlockReadTyped_2()throws IOException
	{
		enter();
		testBooleanBlockReadTyped(CObjListFormat.FLUSH_BLOCK);
		leave();
	};
	@org.junit.Test public void testBooleanBlockReadTyped_3()throws IOException
	{
		enter();
		testBooleanBlockReadTyped(CObjListFormat.FLUSH_ANY);
		leave();
	};
	private void testBooleanBlockReadTyped(CObjListFormat.INDICATOR flush_type)throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BOOLEAN_BLOCK);
			m.add(new boolean[]{false,true,false,false});
			m.add(new boolean[]{true,true});
			m.add(flush_type);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				boolean [] buff = new boolean[100];
				int r= f.readBooleanBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==false);
				org.junit.Assert.assertTrue(buff[2]==true);
				org.junit.Assert.assertTrue(buff[3]==false);
				org.junit.Assert.assertTrue(buff[4]==false);
				org.junit.Assert.assertTrue(buff[5]==true);
				org.junit.Assert.assertTrue(buff[6]==true);
				org.junit.Assert.assertTrue(f.readBooleanBlock(buff,1,99)==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	@org.junit.Test public void testByteBlockReadTyped()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BYTE_BLOCK);
			m.add(new byte[]{(byte)100,(byte)55,(byte)-44,(byte)99});
			m.add(new byte[]{(byte)11,(byte)91});
			m.add(CObjListFormat.FLUSH_BYTE_BLOCK);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==(byte)100);
				org.junit.Assert.assertTrue(buff[2]==(byte)55);
				org.junit.Assert.assertTrue(buff[3]==(byte)-44);
				org.junit.Assert.assertTrue(buff[4]==(byte)99);
				org.junit.Assert.assertTrue(buff[5]==(byte)11);
				org.junit.Assert.assertTrue(buff[6]==(byte)91);
				org.junit.Assert.assertTrue(f.readByteBlock(buff,1,99)==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	@org.junit.Test public void testShortBlockReadTyped()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_SHORT_BLOCK);
			m.add(new short[]{(short)100,(short)55,(short)-44,(short)99});
			m.add(new short[]{(short)1001,(short)551});
			m.add(CObjListFormat.FLUSH_SHORT_BLOCK);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				short [] buff = new short[100];
				int r= f.readShortBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==(short)100);
				org.junit.Assert.assertTrue(buff[2]==(short)55);
				org.junit.Assert.assertTrue(buff[3]==(short)-44);
				org.junit.Assert.assertTrue(buff[4]==(short)99);
				org.junit.Assert.assertTrue(buff[5]==(short)1001);
				org.junit.Assert.assertTrue(buff[6]==(short)551);
				org.junit.Assert.assertTrue(f.readShortBlock(buff,1,99)==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	
	@org.junit.Test public void testCharBlockReadTyped()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_CHAR_BLOCK);
			m.add(new char[]{(char)100,(char)55,(char)-44,(char)99});
			m.add(new char[]{(char)1001,(char)551});
			m.add(CObjListFormat.FLUSH_CHAR_BLOCK);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				char [] buff = new char[100];
				int r= f.readCharBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==(char)100);
				org.junit.Assert.assertTrue(buff[2]==(char)55);
				org.junit.Assert.assertTrue(buff[3]==(char)-44);
				org.junit.Assert.assertTrue(buff[4]==(char)99);
				org.junit.Assert.assertTrue(buff[5]==(char)1001);
				org.junit.Assert.assertTrue(buff[6]==(char)551);
				org.junit.Assert.assertTrue(f.readCharBlock(buff,1,99)==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	@org.junit.Test public void testIntBlockReadTyped()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_INT_BLOCK);
			m.add(new int[]{100,55,-44,99});
			m.add(new int[]{1001,551});	
			m.add(CObjListFormat.FLUSH_INT_BLOCK);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				int [] buff = new int[100];
				int r= f.readIntBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==100);
				org.junit.Assert.assertTrue(buff[2]==55);
				org.junit.Assert.assertTrue(buff[3]==-44);
				org.junit.Assert.assertTrue(buff[4]==99);
				org.junit.Assert.assertTrue(buff[5]==1001);
				org.junit.Assert.assertTrue(buff[6]==551);
				org.junit.Assert.assertTrue(f.readIntBlock(buff,1,99)==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	
	@org.junit.Test public void testLongBlockReadTyped()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_LONG_BLOCK);
			m.add(new long[]{(long)100,(long)55,(long)-44,(long)99});
			m.add(new long[]{(long)1001,(long)551});
			m.add(CObjListFormat.FLUSH_LONG_BLOCK);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				long [] buff = new long[100];
				int r= f.readLongBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==(long)100);
				org.junit.Assert.assertTrue(buff[2]==(long)55);
				org.junit.Assert.assertTrue(buff[3]==(long)-44);
				org.junit.Assert.assertTrue(buff[4]==(long)99);
				org.junit.Assert.assertTrue(buff[5]==(long)1001);
				org.junit.Assert.assertTrue(buff[6]==(long)551);
				org.junit.Assert.assertTrue(f.readLongBlock(buff,1,99)==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	@org.junit.Test public void testFloatBlockReadTyped()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_FLOAT_BLOCK);
			m.add(new float[]{(float)100,(float)55,(float)-44,(float)99});
			m.add(new float[]{(float)1001,(float)551});
			m.add(CObjListFormat.FLUSH_FLOAT_BLOCK);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				float [] buff = new float[100];
				int r= f.readFloatBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==(float)100);
				org.junit.Assert.assertTrue(buff[2]==(float)55);
				org.junit.Assert.assertTrue(buff[3]==(float)-44);
				org.junit.Assert.assertTrue(buff[4]==(float)99);
				org.junit.Assert.assertTrue(buff[5]==(float)1001);
				org.junit.Assert.assertTrue(buff[6]==(float)551);
				org.junit.Assert.assertTrue(f.readFloatBlock(buff,1,99)==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	@org.junit.Test public void testDoubleBlockReadTyped()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_DOUBLE_BLOCK);
			m.add(new double[]{(double)100,(double)55,(double)-44,(double)99});
			m.add(new double[]{(double)1001,(double)551});
			m.add(CObjListFormat.FLUSH_DOUBLE_BLOCK);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				double [] buff = new double[100];
				int r= f.readDoubleBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==(double)100);
				org.junit.Assert.assertTrue(buff[2]==(double)55);
				org.junit.Assert.assertTrue(buff[3]==(double)-44);
				org.junit.Assert.assertTrue(buff[4]==(double)99);
				org.junit.Assert.assertTrue(buff[5]==(double)1001);
				org.junit.Assert.assertTrue(buff[6]==(double)551);
				org.junit.Assert.assertTrue(f.readDoubleBlock(buff,1,99)==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	/*--------------------------------------------------------------- 
			Block, typed, type-missmatch test
	---------------------------------------------------------------*/
	@org.junit.Test public void testBlockReadTypeMissmatch_1()throws IOException
	{
		enter();
			/*
				In this test we test, if we detect block type missmatch
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BOOLEAN_BLOCK);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try
			{
				 f.readDoubleBlock(new double[100],1,9);
				 org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
	};
	@org.junit.Test public void testBlockReadTypeMissmatch_2()throws IOException
	{
		enter();
			/*
				In this test we test, if we detect block type missmatch
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_BYTE_BLOCK);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try
			{
				 f.readFloatBlock(new float[100],1,9);
				 org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
	};
	@org.junit.Test public void testBlockReadTypeMissmatch_3()throws IOException
	{
		enter();
			/*
				In this test we test, if we detect block type missmatch
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_CHAR_BLOCK);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try
			{
				 f.readLongBlock(new long[100],1,9);
				 org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
	};
	@org.junit.Test public void testBlockReadTypeMissmatch_4()throws IOException
	{
		enter();
			/*
				In this test we test, if we detect block type missmatch
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_SHORT_BLOCK);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try
			{
				 f.readIntBlock(new int[100],1,9);
				 org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
	};
	@org.junit.Test public void testBlockReadTypeMissmatch_5()throws IOException
	{
		enter();
			/*
				In this test we test, if we detect block type missmatch
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_INT_BLOCK);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try
			{
				 f.readByteBlock(new byte[100],1,9);
				 org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
	};
	@org.junit.Test public void testBlockReadTypeMissmatch_6()throws IOException
	{
		enter();
			/*
				In this test we test, if we detect block type missmatch
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_LONG_BLOCK);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try
			{
				 f.readByteBlock(new byte[100],1,9);
				 org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
	};
	@org.junit.Test public void testBlockReadTypeMissmatch_7()throws IOException
	{
		enter();
			/*
				In this test we test, if we detect block type missmatch
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_FLOAT_BLOCK);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try
			{
				 f.readBooleanBlock(new boolean[100],1,9);
				 org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
	};
	@org.junit.Test public void testBlockReadTypeMissmatch_8()throws IOException
	{
		enter();
			/*
				In this test we test, if we detect block type missmatch
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_DOUBLE_BLOCK);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			try
			{
				 f.readByteBlock();
				 org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
	};
	
	
	/* ----------------------------------------------------------------------
	
			Blocks against primitives
	
	------------------------------------------------------------------------*/
	@org.junit.Test public void testBlockAgainstPrimitive_1()throws IOException
	{
		enter();
			/*
				In this test we test, if we detect as a problem a primitive
				read during block read.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_FLOAT_BLOCK);
			m.add(new float[]{(float)100,(float)55,(float)-44,(float)99});
			m.add(new float[]{(float)1001,(float)551});
			m.add(CObjListFormat.FLUSH_FLOAT_BLOCK);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			f.next();
			{
				float [] buff = new float[100];
				int r= f.readFloatBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==(float)100);
				org.junit.Assert.assertTrue(buff[2]==(float)55);
				org.junit.Assert.assertTrue(buff[3]==(float)-44);
				org.junit.Assert.assertTrue(buff[4]==(float)99);
				org.junit.Assert.assertTrue(buff[5]==(float)1001);
				org.junit.Assert.assertTrue(buff[6]==(float)551);
				org.junit.Assert.assertTrue(f.readFloatBlock(buff,1,99)==0);
			};
			try{ f.readBoolean(); org.junit.Assert.fail();}catch(IllegalStateException ex){};
			try{ f.readByte(); org.junit.Assert.fail();}catch(IllegalStateException ex){};
			try{ f.readChar(); org.junit.Assert.fail();}catch(IllegalStateException ex){};
			try{ f.readShort(); org.junit.Assert.fail();}catch(IllegalStateException ex){};
			try{ f.readLong(); org.junit.Assert.fail();}catch(IllegalStateException ex){};
			try{ f.readInt(); org.junit.Assert.fail();}catch(IllegalStateException ex){};
			try{ f.readFloat(); org.junit.Assert.fail();}catch(IllegalStateException ex){};
			try{ f.readDouble(); org.junit.Assert.fail();}catch(IllegalStateException ex){};
			
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	
	
	
	
	
	
	/* ----------------------------------------------------------------------
	
			Test peeking with "whatNext"
	
	------------------------------------------------------------------------*/
	@org.junit.Test public void testHasData_described_1()throws IOException
	{
		enter();
			/*
				In this test we test if has data can read info about
				primititves and signals and does not corrupt a flow.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.TYPE_INT);
			m.add(new Integer(33));
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.TYPE_FLOAT_BLOCK);
			m.add(new float[]{(float)100,(float)55,(float)-44,(float)99,(float)5});
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.TYPE_BYTE);
			m.add(new Byte((byte)5));
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_INT);
			org.junit.Assert.assertTrue(f.readInt()==33);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("PERISH".equals(f.next()));
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,4)==4);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,5)==1);			
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN);
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_BYTE);
			org.junit.Assert.assertTrue(f.readByte()==(byte)5);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.EOF);
	};
	
	
	@org.junit.Test public void testHasData_described_2()throws IOException
	{
		enter();
			/*
				In this test we test if has data can read info about
				primititves and signals and does not corrupt a flow.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.TYPE_BYTE);
			m.add(new Byte((byte)-3));
			m.add(CObjListFormat.TYPE_CHAR);
			m.add(new Character('z'));
			m.add(CObjListFormat.TYPE_SHORT);
			m.add(new Short((short)3490));
			m.add(CObjListFormat.TYPE_INT);
			m.add(new Integer(999999));
			m.add(CObjListFormat.TYPE_LONG);
			m.add(new Long(-349L));
			m.add(CObjListFormat.TYPE_FLOAT);
			m.add(new Float(0.0049f));
			m.add(CObjListFormat.TYPE_DOUBLE);
			m.add(new Double(0.0149));
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN);
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_BYTE);
			org.junit.Assert.assertTrue(f.readByte()==(byte)-3);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_CHAR);
			org.junit.Assert.assertTrue(f.readChar()=='z');
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_SHORT);
			org.junit.Assert.assertTrue(f.readShort()==(short)3490);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_INT);
			org.junit.Assert.assertTrue(f.readInt()==999999);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_LONG);
			org.junit.Assert.assertTrue(f.readLong()==-349L);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_FLOAT);
			org.junit.Assert.assertTrue(f.readFloat()==0.0049f);
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_DOUBLE);
			org.junit.Assert.assertTrue(f.readDouble()==0.0149);
	};
	
	@org.junit.Test public void testHasData_described_3()throws IOException
	{
		enter();
			/*
				In this test we test if has data can read info about
				primititves and signals and does not corrupt a flow.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.TYPE_BOOLEAN_BLOCK);
			m.add(new boolean[0]);			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.TYPE_BYTE_BLOCK);
			m.add(new byte[0]);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.TYPE_CHAR_BLOCK);
			m.add(new char[0]);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.TYPE_SHORT_BLOCK);
			m.add(new short[0]);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.TYPE_INT_BLOCK);
			m.add(new int[0]);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.TYPE_LONG_BLOCK);
			m.add(new long[0]);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.TYPE_FLOAT_BLOCK);
			m.add(new float[0]);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.TYPE_DOUBLE_BLOCK);
			m.add(new double[0]);
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CDescrObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth
										 m //CObjListFormat media
										 );
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN_BLOCK);
			org.junit.Assert.assertTrue(f.readBooleanBlock(new boolean[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_BYTE_BLOCK);
			org.junit.Assert.assertTrue(f.readByteBlock(new byte[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_CHAR_BLOCK);
			org.junit.Assert.assertTrue(f.readCharBlock(new char[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_SHORT_BLOCK);
			org.junit.Assert.assertTrue(f.readShortBlock(new short[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_INT_BLOCK);
			org.junit.Assert.assertTrue(f.readIntBlock(new int[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_LONG_BLOCK);
			org.junit.Assert.assertTrue(f.readLongBlock(new long[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.whatNext()==ISignalReadFormat.PRMTV_DOUBLE_BLOCK);
			org.junit.Assert.assertTrue(f.readDoubleBlock(new double[100],0,100)==0);
			
	};
	
	
	
};

	