package sztejkat.abstractfmt;
import java.io.IOException;

/**
	A test for {@link ASignalReadFormat} made around {@link CObjListReadFormat}
	<p>
	Those test are run using hand-crafted stream data (not inter-operational write-read tests).
*/
public class TestASignalReadFormat extends sztejkat.utils.test.ATest
{
	/* ---------------------------------------------------------------------------
	
			Untyped elementary primitive reads.
	
	---------------------------------------------------------------------------*/
	@org.junit.Test public void testUntypedPrimitiveReads_1()throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data can be safely
				read from un-type stream format.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(new Boolean(false));
			m.add(new Boolean(true));
			m.add(new Byte((byte)-3));
			m.add(new Character('z'));
			m.add(new Short((short)3490));
			m.add(new Integer(999999));
			m.add(new Long(-349L));
			m.add(new Float(0.0049f));
			m.add(new Double(0.0149));
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
			Start-typed elementary primitive reads.
	
	---------------------------------------------------------------------------*/
	private void _testTypedPrimitiveReads_1(boolean strict)throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data can be safely
				read from start-typed stream format.
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 strict,//boolean strict_described_types
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
	
	@org.junit.Test public void testTypedPrimitiveReads_1()throws IOException
	{
		enter();
			_testTypedPrimitiveReads_1(false);
		leave();
	};
	@org.junit.Test public void testTypedPrimitiveReads_1a()throws IOException
	{
		enter();
			_testTypedPrimitiveReads_1(true);
		leave();
	};
	
	
	/* ---------------------------------------------------------------------------
	
			Start-end-typed elementary primitive reads.
	
	---------------------------------------------------------------------------*/
	private void _testFullyTypedPrimitiveReads_1(boolean strict)throws IOException
	{
		enter();
			/*
				In this test we test, if primitive data can be safely
				read from start-end-typed stream format.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(false));
			m.add(CObjListFormat.TYPE_BOOLEAN_END);
			m.add(CObjListFormat.TYPE_BOOLEAN);
			m.add(new Boolean(true));
			m.add(CObjListFormat.TYPE_BOOLEAN_END);
			m.add(CObjListFormat.TYPE_BYTE);
			m.add(new Byte((byte)-3));
			m.add(CObjListFormat.TYPE_BYTE_END);
			m.add(CObjListFormat.TYPE_CHAR);
			m.add(new Character('z'));
			m.add(CObjListFormat.TYPE_CHAR_END);
			m.add(CObjListFormat.TYPE_SHORT);
			m.add(new Short((short)3490));
			m.add(CObjListFormat.TYPE_SHORT_END);
			m.add(CObjListFormat.TYPE_INT);
			m.add(new Integer(999999));
			m.add(CObjListFormat.TYPE_INT_END);
			m.add(CObjListFormat.TYPE_LONG);			
			m.add(new Long(-349L));
			m.add(CObjListFormat.TYPE_LONG_END);
			m.add(CObjListFormat.TYPE_FLOAT);
			m.add(new Float(0.0049f));
			m.add(CObjListFormat.TYPE_FLOAT_END);
			m.add(CObjListFormat.TYPE_DOUBLE);
			m.add(new Double(0.0149));
			m.add(CObjListFormat.TYPE_DOUBLE_END);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 strict,//boolean strict_described_types
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
	@org.junit.Test public void testFullyTypedPrimitiveReads_1()throws IOException
	{
		enter();
			_testFullyTypedPrimitiveReads_1(false);
		leave();
	};
	@org.junit.Test public void testFullyTypedPrimitiveReads_1a()throws IOException
	{
		enter();
			_testFullyTypedPrimitiveReads_1(true);
		leave();
	};
	
	
	
	
	
	/* ---------------------------------------------------------------------------
	
			Signals
	
	---------------------------------------------------------------------------*/
	@org.junit.Test public void readBeginSignalDirect_1()throws IOException
	{
		enter();
			/*	
				In this test we check if begin-end signals, in various
				sequences can be correctly read when supplied directly.
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERSH");
			m.add(CObjListFormat.END_INDICATOR);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HARSH");
			m.add(CObjListFormat.END_INDICATOR);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("CLONK");
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("DONK");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
		
			org.junit.Assert.assertTrue("PERSH".equals(f.next()));
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HARSH".equals(f.next()));
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("CLONK".equals(f.next()));
			org.junit.Assert.assertTrue("DONK".equals(f.next()));
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			
		leave();
	};
	
	@org.junit.Test public void readBeginSignalRegistered_1()throws IOException
	{
		enter();
			/*	
				In this test we check if begin-end signals, in various
				sequences can be correctly read when via registration
				and re-use.
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_INDICATOR(0));
			m.add("PERSH");
			m.add(CObjListFormat.END_INDICATOR);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_INDICATOR(1));
			m.add("HARSH");
			m.add(CObjListFormat.END_INDICATOR);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_USE_INDICATOR(0));
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_USE_INDICATOR(1));
			m.add(CObjListFormat.END_INDICATOR);
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 2,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
		
			org.junit.Assert.assertTrue("PERSH".equals(f.next()));
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HARSH".equals(f.next()));
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("PERSH".equals(f.next()));
			org.junit.Assert.assertTrue("HARSH".equals(f.next()));
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue(f.next()==null);
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			
		leave();
	};
	
	
	@org.junit.Test public void readBeginSignalEndBegin()throws IOException
	{
		enter();
			/*	
				In this test we check if begin-end signals can be used
				with end-begin optimization
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_INDICATOR(0));
			m.add("PERSH");
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_INDICATOR(1));
			m.add("HARSH");
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_USE_INDICATOR(0));
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_USE_INDICATOR(1));
			m.add(CObjListFormat.END_INDICATOR);
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 2,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
		
			org.junit.Assert.assertTrue("PERSH".equals(f.next()));
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HARSH".equals(f.next()));
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("PERSH".equals(f.next()));
			org.junit.Assert.assertTrue("HARSH".equals(f.next()));
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue(f.next()==null);
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			
		leave();
	};
	
	
	@org.junit.Test public void readBeginSignalTooLong_1()throws IOException
	{
		enter();
			/*	
				In this test we check if begin signal correctly detects too
				long signal name in direct scenario
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERSHINGDOCTOR");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 2,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
		
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(EFormatBoundaryExceeded ex){};
			
		leave();
	};
	
	@org.junit.Test public void readBeginSignalTooLong_2()throws IOException
	{
		enter();
			/*	
				In this test we check if begin signal correctly detects too
				long signal name in registration scenario
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_INDICATOR(0));
			m.add("PERSHINGDOCTOR");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 2,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
		
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(EFormatBoundaryExceeded ex){};
			
		leave();
	};
	
	@org.junit.Test public void readBeginSignalUseBeforeRegister()throws IOException
	{
		enter();
			/*	
				In this test we check if begin signal correctly detects
				use of registered name before it was registered
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_USE_INDICATOR(0));
			
			ISignalReadFormat f = new CObjListReadFormat(
										 2,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
		
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){};
			
		leave();
	};
	
	@org.junit.Test public void readBeginSignalRegisterError_1()throws IOException
	{
		enter();
			/*	
				In this test we check if begin signal correctly detects
				registration with a wrong index.
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_USE_INDICATOR(394));
			
			ISignalReadFormat f = new CObjListReadFormat(
										 2,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
		
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){};
			
		leave();
	};
	@org.junit.Test public void readBeginSignalRegisterError_2()throws IOException
	{
		enter();
			/*	
				In this test we check if begin signal correctly detects
				registration with a wrong index.
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_USE_INDICATOR(-409));
			
			ISignalReadFormat f = new CObjListReadFormat(
										 2,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
		
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){};
			
		leave();
	};
	@org.junit.Test public void readBeginSignalRegisterError_3()throws IOException
	{
		enter();
			/*	
				In this test we check if begin signal correctly detects
				registration override
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_INDICATOR(0));
			m.add("PERISH");
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_INDICATOR(0));
			m.add("ALISH");
			ISignalReadFormat f = new CObjListReadFormat(
										 2,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){};
			
		leave();
	};
	
	@org.junit.Test public void readBeginSignalRegisterError_4()throws IOException
	{
		enter();
			/*	
				In this test we check if begin signal correctly detects
				registration when no registry is enabled.
			*/
			final CObjListFormat m = new CObjListFormat();
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(new CObjListFormat.REGISTER_INDICATOR(0));
			m.add("PERISH");
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){};
			
		leave();
	};
	
	
	
	
	/*-------------------------------------------------------------------------
	
				Primitive boundary tests.
	
	---------------------------------------------------------------------------*/
	@org.junit.Test public void testUntypedPrimitiveBoundaryRead_1()throws IOException
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
			m.add(new Boolean(false));
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	@org.junit.Test public void testUntypedPrimitiveBoundaryRead_2()throws IOException
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
			m.add(new Boolean(false));
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	@org.junit.Test public void testUntypedPrimitiveBoundaryRead_3()throws IOException
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
			m.add(new Boolean(false));
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_BOOLEAN_END);
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_BOOLEAN_END);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_BOOLEAN_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	@org.junit.Test public void testUntypedPrimitiveMissmatch_1()throws IOException
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			try{
				f.readDouble();
				org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
		leave();
	};
	
	@org.junit.Test public void testUntypedPrimitiveMissmatch_2()throws IOException
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			try{
				f.readLong();
				org.junit.Assert.fail();
			}catch(EDataMissmatch ex){};
		leave();
	};
	
	@org.junit.Test public void testUntypedPrimitiveTypeMissed()throws IOException
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
	
				Skipping tests.
	
	---------------------------------------------------------------------------*/
	@org.junit.Test public void testSkipping_1()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly skip 
				content of an event till next signal.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new Byte((byte)33));
			m.add(new Byte((byte)33));
			m.add(new Byte((byte)33));
			m.add(new Byte((byte)33));
			m.add(CObjListFormat.END_INDICATOR);
			m.add(new Integer(44));
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			org.junit.Assert.assertTrue(null==f.next());
			org.junit.Assert.assertTrue(f.readInt()==44);
		leave();
	};
	@org.junit.Test public void testSkipping_2()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly skip 
				content of an event till next signal.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new Byte((byte)34));
			m.add(new Byte((byte)33));
			m.add(new Byte((byte)33));
			m.add(new Byte((byte)33));
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SMASH");
			m.add(new Integer(44));
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			org.junit.Assert.assertTrue(f.readByte()==(byte)34);
			org.junit.Assert.assertTrue("SMASH".equals(f.next()));
			org.junit.Assert.assertTrue(f.readInt()==44);
		leave();
	};
	
	
	
	
	
	/*-------------------------------------------------------------------------
	
				Block operations
	
			Note: We test most operations on byte block
			because we assume that the rest is Copy & paste of the same code
			and elementary tests will be sufficient.
			
	---------------------------------------------------------------------------*/
	@org.junit.Test public void testBlockRead_1()throws IOException
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
			m.add(new byte[]{(byte)3,(byte)5,(byte)7,(byte)9});
			m.add(new byte[]{(byte)34,(byte)54});	
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==1);
				org.junit.Assert.assertTrue(buff[1]==(byte)3);
			};
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==1);
				org.junit.Assert.assertTrue(buff[1]==(byte)5);
			};
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==1);
				org.junit.Assert.assertTrue(buff[1]==(byte)7);
			};
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==1);
				org.junit.Assert.assertTrue(buff[1]==(byte)9);
			};
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==1);
				org.junit.Assert.assertTrue(buff[1]==(byte)34);
			};
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==1);
				org.junit.Assert.assertTrue(buff[1]==(byte)54);
			};
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==0);
				//end check "stuck on eof"
				r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==0);
				r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
		leave();
	};
	
	@org.junit.Test public void testBlockRead_2()throws IOException
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
			m.add(new byte[]{(byte)3,(byte)5,(byte)7,(byte)9});
			m.add(new byte[]{(byte)34,(byte)54});	
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==(byte)3);
				org.junit.Assert.assertTrue(buff[2]==(byte)5);
				org.junit.Assert.assertTrue(buff[3]==(byte)7);
				org.junit.Assert.assertTrue(buff[4]==(byte)9);
				org.junit.Assert.assertTrue(buff[5]==(byte)34);
				org.junit.Assert.assertTrue(buff[6]==(byte)54);
			};
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,99);
				org.junit.Assert.assertTrue(r==0);
				//end check "stuck on eof"
				r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==0);
				r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
		leave();
	};
	
	@org.junit.Test public void testBlockRead_3()throws IOException
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
			m.add(new byte[0]);		//Empty blocks injecttion all around.
			m.add(new byte[]{(byte)3,(byte)5,(byte)7,(byte)9});
			m.add(new byte[0]);
			m.add(new byte[0]);
			m.add(new byte[]{(byte)34,(byte)54});
			m.add(new byte[0]);
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,99);
				System.out.println(java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==6);
				org.junit.Assert.assertTrue(buff[1]==(byte)3);
				org.junit.Assert.assertTrue(buff[2]==(byte)5);
				org.junit.Assert.assertTrue(buff[3]==(byte)7);
				org.junit.Assert.assertTrue(buff[4]==(byte)9);
				org.junit.Assert.assertTrue(buff[5]==(byte)34);
				org.junit.Assert.assertTrue(buff[6]==(byte)54);
			};
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,99);
				org.junit.Assert.assertTrue(r==0);
				//end check "stuck on eof"
				r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==0);
				r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==0);
			};
			org.junit.Assert.assertTrue(f.next()==null);
		leave();
	};
	
	
	@org.junit.Test public void testBlockRead_4()throws IOException
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
			m.add(new byte[]{(byte)3,(byte)5,(byte)7,(byte)9});
			m.add(new byte[]{(byte)34,(byte)54});	
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("MARSH");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				byte [] buff = new byte[10];
				int r= f.readByteBlock(buff,1,0);
				org.junit.Assert.assertTrue(r==0);
				
				r= f.readByteBlock(buff,1,5);	
				System.out.println("r="+r+" buff="+java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==5);
				org.junit.Assert.assertTrue(buff[1]==(byte)3);
				org.junit.Assert.assertTrue(buff[2]==(byte)5);
				org.junit.Assert.assertTrue(buff[3]==(byte)7);
				org.junit.Assert.assertTrue(buff[4]==(byte)9);
				org.junit.Assert.assertTrue(buff[5]==(byte)34);				
			};
			{
				byte [] buff = new byte[100];
				int r= f.readByteBlock(buff,1,99);
				org.junit.Assert.assertTrue(r==1);
				org.junit.Assert.assertTrue(buff[1]==(byte)54);
				//end check "stuck on eof"
				r= f.readByteBlock(buff,1,0);
				org.junit.Assert.assertTrue(r==0);
				r= f.readByteBlock(buff,1,1);
				org.junit.Assert.assertTrue(r==0);
			};
			org.junit.Assert.assertTrue("MARSH".equals(f.next()));
		leave();
	};
	
	
	@org.junit.Test public void testBlockMixedRead_1()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests using mixture
				of block and single element reads.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new byte[]{(byte)3,(byte)5,(byte)7,(byte)9});
			m.add(new byte[]{(byte)34,(byte)-100});	
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				org.junit.Assert.assertTrue(f.readByteBlock()==3);
				org.junit.Assert.assertTrue(f.readByteBlock()==5);
				org.junit.Assert.assertTrue(f.readByteBlock()==7);
				org.junit.Assert.assertTrue(f.readByteBlock()==9);
				org.junit.Assert.assertTrue(f.readByteBlock()==34);
				org.junit.Assert.assertTrue(f.readByteBlock()==((-100)&0xFF));
				org.junit.Assert.assertTrue(f.readByteBlock()==-1);
				org.junit.Assert.assertTrue(f.readByteBlock()==-1);
			};
			org.junit.Assert.assertTrue(f.next()==null);
		leave();
	};
	
	@org.junit.Test public void testBlockMixedRead_2()throws IOException
	{
		enter();
			/*
				In this test we test, if we can correctly read block
				using various partial read requests using mixture
				of block and single element reads.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new byte[0]);
			m.add(new byte[]{(byte)3,(byte)5,(byte)7,(byte)9});
			m.add(new byte[]{(byte)34,(byte)-100});	
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				org.junit.Assert.assertTrue(f.readByteBlock()==3);
				org.junit.Assert.assertTrue(f.readByteBlock()==5);
				{
					byte [] buff=new byte[100];
					int r= f.readByteBlock(buff,1,5);	
					System.out.println("r="+r+" buff="+java.util.Arrays.toString(buff));
					org.junit.Assert.assertTrue(r==4);
					org.junit.Assert.assertTrue(buff[1]==(byte)7);
					org.junit.Assert.assertTrue(buff[2]==(byte)9);
					org.junit.Assert.assertTrue(buff[3]==(byte)34);
					org.junit.Assert.assertTrue(buff[4]==(byte)-100);
				}
				org.junit.Assert.assertTrue(f.readByteBlock()==-1);
				org.junit.Assert.assertTrue(f.readByteBlock()==-1);
			};
			org.junit.Assert.assertTrue(f.next()==null);
		leave();
	};
	
	
	
	@org.junit.Test public void testBlockReadEof_1()throws IOException
	{
		enter();
			/*
				In this test we test, block read correctly 
				reacts to different EOF conditions.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				byte [] buff = new byte[10];
				try{
					int r= f.readByteBlock(buff,1,0);//Zero read also should NOT initialie
					org.junit.Assert.fail();
				}catch(ENoMoreData ex){};
			};
			{
				byte [] buff = new byte[10];
				try{
					int r= f.readByteBlock(buff,1,10);
					org.junit.Assert.fail();
				}catch(ENoMoreData ex){};
			};
		leave();
	};
	
	@org.junit.Test public void testBlockReadEof_2()throws IOException
	{
		enter();
			/*
				In this test we test, block read correctly 
				reacts to different EOF conditions.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new byte[]{(byte)3,(byte)5,(byte)7,(byte)9});
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				byte [] buff = new byte[10];
				try{
					int r= f.readByteBlock(buff,0,10);
					org.junit.Assert.fail();
				}catch(EUnexpectedEof ex){};
			};
		leave();
	};
	
	@org.junit.Test public void testBlockReadEof_3()throws IOException
	{
		enter();
			/*
				In this test we test, block read correctly 
				reacts to different EOF conditions.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new byte[0]);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				byte [] buff = new byte[10];
				try{
					int r= f.readByteBlock(buff,0,10);
					org.junit.Assert.fail();
				}catch(EUnexpectedEof ex){};
			};
		leave();
	};
	
	@org.junit.Test public void testBlockReadEof_4()throws IOException
	{
		enter();
			/*
				In this test we test, block read correctly 
				reacts to different EOF conditions.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new byte[0]);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			{
				try{
					int r= f.readByteBlock();
					org.junit.Assert.fail();
				}catch(EUnexpectedEof ex){};
			};
		leave();
	};
	
	
	
	@org.junit.Test public void testBlockSkip()throws IOException
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
			m.add(new byte[]{(byte)3,(byte)5,(byte)7,(byte)9});
			m.add(new byte[]{(byte)34,(byte)54});	
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("MARSH");
			m.add(new byte[]{(byte)13,(byte)15,(byte)17,(byte)19});
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
			{
				byte [] buff = new byte[10];
				int r= f.readByteBlock(buff,1,3);
				System.out.println("r="+r+" buff="+java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==3);
				org.junit.Assert.assertTrue(buff[1]==(byte)13);
				org.junit.Assert.assertTrue(buff[2]==(byte)15);
				org.junit.Assert.assertTrue(buff[3]==(byte)17);;				
			};
		leave();
	};
	
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
			m.add(CObjListFormat.TYPE_BYTE_BLOCK_END);
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("MARSH");
			m.add(new byte[]{(byte)13,(byte)15,(byte)17,(byte)19});
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
			{
				byte [] buff = new byte[10];
				int r= f.readByteBlock(buff,1,3);
				System.out.println("r="+r+" buff="+java.util.Arrays.toString(buff));
				org.junit.Assert.assertTrue(r==3);
				org.junit.Assert.assertTrue(buff[1]==(byte)13);
				org.junit.Assert.assertTrue(buff[2]==(byte)15);
				org.junit.Assert.assertTrue(buff[3]==(byte)17);;				
			};
		leave();
	};
	
	
	
	
	
	
	/*-------------------------------------------------------------------------
	
				Block operations
				
				Rough tests of other types.
				
	--------------------------------------------------------------------------*/
	@org.junit.Test public void testBooleanBlockRead()throws IOException
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
			m.add(new boolean[]{false,true,false,false});
			m.add(new boolean[]{true,true});	
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	@org.junit.Test public void testShortBlockRead()throws IOException
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
			m.add(new short[]{(short)100,(short)55,(short)-44,(short)99});
			m.add(new short[]{(short)1001,(short)551});	
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	
	@org.junit.Test public void testCharBlockRead()throws IOException
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
			m.add(new char[]{(char)100,(char)55,(char)-44,(char)99});
			m.add(new char[]{(char)1001,(char)551});	
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	@org.junit.Test public void testIntBlockRead()throws IOException
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
			m.add(new int[]{100,55,-44,99});
			m.add(new int[]{1001,551});	
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	
	@org.junit.Test public void testLongBlockRead()throws IOException
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
			m.add(new long[]{(long)100,(long)55,(long)-44,(long)99});
			m.add(new long[]{(long)1001,(long)551});	
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	@org.junit.Test public void testFloatBlockRead()throws IOException
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
			m.add(new float[]{(float)100,(float)55,(float)-44,(float)99});
			m.add(new float[]{(float)1001,(float)551});	
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	@org.junit.Test public void testDoubleBlockRead()throws IOException
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
			m.add(new double[]{(double)100,(double)55,(double)-44,(double)99});
			m.add(new double[]{(double)1001,(double)551});	
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
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
	
	
	
	
	
	/*-------------------------------------------------------------------------
	
				Block operations
				
				Rough tests of typed reads
				
	--------------------------------------------------------------------------*/
	@org.junit.Test public void testBooleanBlockReadTyped()throws IOException
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
			m.add(CObjListFormat.TYPE_BOOLEAN_BLOCK_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_BYTE_BLOCK_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_SHORT_BLOCK_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_CHAR_BLOCK_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_INT_BLOCK_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_LONG_BLOCK_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_FLOAT_BLOCK_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_DOUBLE_BLOCK_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
			m.add(CObjListFormat.TYPE_FLOAT_BLOCK_END);
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 true,//boolean strict_described_types
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
	
			Test peeking with "hasData"
	
	------------------------------------------------------------------------*/
	@org.junit.Test public void testHasData_undescribed_1()throws IOException
	{
		enter();
			/*
				In this test we test if has data can read info about
				primititves and signals and does not corrupt a flow.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(new Integer(33));
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new float[]{(float)100,(float)55,(float)-44,(float)99,(float)5});
			m.add(CObjListFormat.END_BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("HORHE");
			m.add(new Integer(34));
			m.add(new Integer(45));
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			org.junit.Assert.assertTrue(f.hasData()>0);
			org.junit.Assert.assertTrue(f.readInt()==33);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("PERISH".equals(f.next()));
			org.junit.Assert.assertTrue(f.hasData()>0);
			org.junit.Assert.assertTrue(f.hasData()>0);	//multiple peeks, intentionally
			org.junit.Assert.assertTrue(f.hasData()>0);
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,4)==4);
			org.junit.Assert.assertTrue(f.hasData()>0);//partially read block
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,5)==1);			
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
			org.junit.Assert.assertTrue(f.hasData()>0);
			org.junit.Assert.assertTrue(f.readInt()==34);
			org.junit.Assert.assertTrue(f.hasData()>0);
			org.junit.Assert.assertTrue(f.readInt()==45);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.EOF);
	};
	
	@org.junit.Test public void testHasData_undescribed_2()throws IOException
	{
		enter();
			/*
				In this test we test if has data can read info about
				primititves and signals and does not corrupt a flow
				but focusing on block processing.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new float[]{(float)100,(float)55,(float)-44,(float)99,(float)5});
			m.add(new float[]{(float)100,(float)55,(float)-44,(float)99,(float)5});
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("PERISH".equals(f.next()));
			org.junit.Assert.assertTrue(f.hasData()>0);
			org.junit.Assert.assertTrue(f.hasData()>0);	//multiple peeks, intentionally
			org.junit.Assert.assertTrue(f.hasData()>0);
			//Do a read which will return partial read.
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,100)==10);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);			
			org.junit.Assert.assertTrue(f.next()==null);
	};
	@org.junit.Test public void testHasData_undescribed_3()throws IOException
	{
		enter();
			/*
				In this test we test if has data can read info about
				primititves and signals and does not corrupt a flow
				but focusing on block processing.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("PERISH");
			m.add(new float[]{(float)100,(float)55,(float)-44,(float)99,(float)5});
			m.add(new float[]{(float)100,(float)55,(float)-44,(float)99,(float)5});
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("PERISH".equals(f.next()));
			org.junit.Assert.assertTrue(f.hasData()>0);
			org.junit.Assert.assertTrue(f.hasData()>0);	//multiple peeks, intentionally
			org.junit.Assert.assertTrue(f.hasData()>0);
			//Do a read which will return full read, but read everything
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,10)==10);
			org.junit.Assert.assertTrue(f.hasData()>0);	
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,8)==0);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);			
			org.junit.Assert.assertTrue(f.next()==null);
	};
	
	
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_INT);
			org.junit.Assert.assertTrue(f.readInt()==33);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("PERISH".equals(f.next()));
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,4)==4);
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,5)==1);			
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(f.next()==null);
			org.junit.Assert.assertTrue("HORHE".equals(f.next()));
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_BOOLEAN);
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_BYTE);
			org.junit.Assert.assertTrue(f.readByte()==(byte)5);
			org.junit.Assert.assertTrue(f.hasData()==ISignalReadFormat.EOF);
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_BOOLEAN);
			org.junit.Assert.assertTrue(f.readBoolean()==false);
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_BYTE);
			org.junit.Assert.assertTrue(f.readByte()==(byte)-3);
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_CHAR);
			org.junit.Assert.assertTrue(f.readChar()=='z');
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_SHORT);
			org.junit.Assert.assertTrue(f.readShort()==(short)3490);
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_INT);
			org.junit.Assert.assertTrue(f.readInt()==999999);
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_LONG);
			org.junit.Assert.assertTrue(f.readLong()==-349L);
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_FLOAT);
			org.junit.Assert.assertTrue(f.readFloat()==0.0049f);
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_DOUBLE);
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
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_BOOLEAN_BLOCK);
			org.junit.Assert.assertTrue(f.readBooleanBlock(new boolean[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_BYTE_BLOCK);
			org.junit.Assert.assertTrue(f.readByteBlock(new byte[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_CHAR_BLOCK);
			org.junit.Assert.assertTrue(f.readCharBlock(new char[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_SHORT_BLOCK);
			org.junit.Assert.assertTrue(f.readShortBlock(new short[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_INT_BLOCK);
			org.junit.Assert.assertTrue(f.readIntBlock(new int[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_LONG_BLOCK);
			org.junit.Assert.assertTrue(f.readLongBlock(new long[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(f.readFloatBlock(new float[100],0,100)==0);
			
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.SIGNAL);	f.next();
			org.junit.Assert.assertTrue(f.hasData()==IDescribedSignalReadFormat.PRMTV_DOUBLE_BLOCK);
			org.junit.Assert.assertTrue(f.readDoubleBlock(new double[100],0,100)==0);
			
	};
	
	/* ------------------------------------------------------------------------------------
	
			Recursion depth attack protection
	
	-------------------------------------------------------------------------------------*/
	
	@org.junit.Test public void testRecursionDepthAttack()throws IOException
	{
		enter();
			/*
				In this test we test if stream defends agains event
				recursion depth attacks.
			*/
			final CObjListFormat m = new CObjListFormat();
			
			//Note: block must have begin signal.
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.BEGIN_INDICATOR);
			m.add(CObjListFormat.DIRECT_INDICATOR);
			m.add("SANA");
			m.add(CObjListFormat.END_INDICATOR);
			
			ISignalReadFormat f = new CObjListReadFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 3,//int max_events_recursion_depth,
										 false,//boolean strict_described_types
										 m //CObjListFormat media
										 );
			f.next();
			f.next();
			f.next();
			try{
				f.next();
				org.junit.Assert.fail();
			}catch(EFormatBoundaryExceeded ex){};
			
	};
	
};

	