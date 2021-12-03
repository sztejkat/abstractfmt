package sztejkat.abstractfmt;
import java.io.IOException;
/**
	A test for {@link ASignalWriteFormat} made around {@link CObjListWriteFormat}	
*/
public class TestASignalWriteFormat extends sztejkat.utils.test.ATest
{
	@org.junit.Test public void testCapsReporting()throws IOException
	{
		enter();
			/*
				In this test we check if capabilities are correctly reported.
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			
			org.junit.Assert.assertTrue(f.getMaxSignalNameLength()==8);
		leave();
	};
	
	@org.junit.Test public void testOffEventPrimitives()throws IOException
	{
		enter();
			/*
				In this test we check if we can write primitives
				without an enclosing event.
				
				We test un-described implementation, so no type
				indicators are expected.
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
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
			
			//Now poll media. Since this is object stream it is enough to
			//validate types to ensure we did it correctly.
			org.junit.Assert.assertTrue(m.getFirst() instanceof Boolean);
			org.junit.Assert.assertTrue(((Boolean)m.pollFirst()).booleanValue()==false);
			
			org.junit.Assert.assertTrue(m.getFirst() instanceof Byte);
			org.junit.Assert.assertTrue(((Byte)m.pollFirst()).byteValue()==(byte)-7);
			
			org.junit.Assert.assertTrue(m.getFirst() instanceof Character);
			org.junit.Assert.assertTrue(((Character)m.pollFirst()).charValue()=='c');
			
			org.junit.Assert.assertTrue(m.getFirst() instanceof Short);
			org.junit.Assert.assertTrue(((Short)m.pollFirst()).shortValue()==(short)44);
			
			org.junit.Assert.assertTrue(m.getFirst() instanceof Integer);
			org.junit.Assert.assertTrue(((Integer)m.pollFirst()).intValue()==20394);
			
			org.junit.Assert.assertTrue(m.getFirst() instanceof Long);
			org.junit.Assert.assertTrue(((Long)m.pollFirst()).longValue()==3344909);
			
			org.junit.Assert.assertTrue(m.getFirst() instanceof Float);
			org.junit.Assert.assertTrue(((Float)m.pollFirst()).floatValue()==3.555f);
			
			org.junit.Assert.assertTrue(m.getFirst() instanceof Double);
			org.junit.Assert.assertTrue(((Double)m.pollFirst()).floatValue()==49.5);
			
			org.junit.Assert.assertTrue(m.isEmpty());
		leave();
	};
	
	
	@org.junit.Test public void testBeginEnd()throws IOException
	{
		enter();
			/*
				In this test we check if we can have begin and end
				signal without name optimization. 
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("SPAMERKA");
			f.end();
			f.close();
			
			System.out.println(m);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("SPAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		leave();
	};
	
	@org.junit.Test public void testBeginEndNameRegistration()throws IOException
	{
		enter();
			/*
				In this test we check if we can have begin and end
				signal with name optimization, just if there is registration.
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 2,//int names_registry_size,
										 16,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("SPAMERKA");
			f.end();
			f.close();
			
			System.out.println(m);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.getFirst() instanceof CObjListFormat.REGISTER_INDICATOR);
			org.junit.Assert.assertTrue(((CObjListFormat.REGISTER_INDICATOR)(m.pollFirst())).name_index==0);
			org.junit.Assert.assertTrue("SPAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		leave();
	};
	
	@org.junit.Test public void testBeginEndNameRegistration2()throws IOException
	{
		enter();
			/*
				In this test we check if we can have begin and end
				signal with name optimization, registration and later use
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 2,//int names_registry_size,
										 16,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("SPAMERKA");
			f.end();
			f.begin("SPAMERKA");
			f.end();
			f.close();
			
			System.out.println(m);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.getFirst() instanceof CObjListFormat.REGISTER_INDICATOR);
			org.junit.Assert.assertTrue(((CObjListFormat.REGISTER_INDICATOR)(m.pollFirst())).name_index==0);
			org.junit.Assert.assertTrue("SPAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.getFirst() instanceof CObjListFormat.REGISTER_USE_INDICATOR);
			org.junit.Assert.assertTrue(((CObjListFormat.REGISTER_USE_INDICATOR)(m.pollFirst())).name_index==0);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		leave();
	};
	
	@org.junit.Test public void testBeginEndNameRegistration3()throws IOException
	{
		enter();
			/*
				In this test we check if we can have begin and end
				signal with name optimization, registration and later use,
				with more names than registry size.
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 2,//int names_registry_size,
										 16,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("SPAMERKA");
			f.end();
			f.begin("LAMERKA");
			f.end();
			f.begin("KLAMERKA");
			f.end();
			f.begin("LAMERKA");
			f.end();
			f.begin("KLAMERKA");
			f.end();			
			f.close();
			
			System.out.println(m);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.getFirst() instanceof CObjListFormat.REGISTER_INDICATOR);
			org.junit.Assert.assertTrue(((CObjListFormat.REGISTER_INDICATOR)(m.pollFirst())).name_index==0);
			org.junit.Assert.assertTrue("SPAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.getFirst() instanceof CObjListFormat.REGISTER_INDICATOR);
			org.junit.Assert.assertTrue(((CObjListFormat.REGISTER_INDICATOR)(m.pollFirst())).name_index==1);
			org.junit.Assert.assertTrue("LAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("KLAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.getFirst() instanceof CObjListFormat.REGISTER_USE_INDICATOR);
			org.junit.Assert.assertTrue(((CObjListFormat.REGISTER_USE_INDICATOR)(m.pollFirst())).name_index==1);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("KLAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	
	
	@org.junit.Test public void testBeginEndNameRegistration4()throws IOException
	{
		enter();
			/*
				In this test we check if we can have begin and end
				signal with name optimization, registration and later use,
				in case if first use was optimized but second was requested
				to be not optimized.
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 2,//int names_registry_size,
										 16,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("SPAMERKA");
			f.end();			
			f.begin("SPAMERKA",true);
			f.end();			
			f.close();
			
			System.out.println(m);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.getFirst() instanceof CObjListFormat.REGISTER_INDICATOR);
			org.junit.Assert.assertTrue(((CObjListFormat.REGISTER_INDICATOR)(m.pollFirst())).name_index==0);
			org.junit.Assert.assertTrue("SPAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);			
			
			//Note: Even tough second use was required to be not optimized we should observe optimized
			//use here because it is inside registry anyway so it would be pointless to not optimize it.
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.getFirst() instanceof CObjListFormat.REGISTER_USE_INDICATOR);
			org.junit.Assert.assertTrue(((CObjListFormat.REGISTER_USE_INDICATOR)(m.pollFirst())).name_index==0);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
	
	
	
	@org.junit.Test public void testBeginEndNameRegistrationDisabled()throws IOException
	{
		enter();
			/*
				In this test we check if we can have begin and end
				signal with name optimization, but when directly prohibitted.
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 2,//int names_registry_size,
										 16,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("SPAMERKA",true);
			f.end();
			f.close();
			
			System.out.println(m);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("SPAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		leave();
	};
	
	
	@org.junit.Test public void testBeginNameLengthTolong()throws IOException
	{
		enter();
			/*
				In this test we check if stream defends against too long
				name and that it does not get to name registry.
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 2,//int names_registry_size,
										 9,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			try{
				f.begin("123456789A");
				org.junit.Assert.fail("Should have thrown");
			}catch(IllegalArgumentException ex)
			{
				System.out.println(ex);
			};
				f.begin("123456789");
				f.close();
			System.out.println(m);
			
			//Note: If bad name would reach registry we would get incorrect index here.
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.getFirst() instanceof CObjListFormat.REGISTER_INDICATOR);
			org.junit.Assert.assertTrue(((CObjListFormat.REGISTER_INDICATOR)(m.pollFirst())).name_index==0);
			org.junit.Assert.assertTrue("123456789".equals(m.pollFirst()));
			
			
		leave();
	};
	
	
	
	@org.junit.Test public void testRecursionDepth()throws IOException
	{
		enter();
			/*
				In this test we check if we allow recursion and we are protected 
				against depth oveflow. 
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("123456789");
			f.begin("123456789");
			f.end();
			f.begin("123456789");
			f.end();
			f.end();
			f.begin("123456789");
			f.begin("123456789");
			
			try{
				f.begin("123456789");
				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
				
			System.out.println(m);
			
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("123456789".equals(m.pollFirst()));
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("123456789".equals(m.pollFirst()));
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("123456789".equals(m.pollFirst()));
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("123456789".equals(m.pollFirst()));
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("123456789".equals(m.pollFirst()));
			
			org.junit.Assert.assertTrue(m.isEmpty());
		leave();
	};
	
	@org.junit.Test public void testRecursionUnderrun()throws IOException
	{
		enter();
			/*
				In this test we check if we detected too many end()
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("123456789");
			f.begin("123456789");
			f.end();
			f.end();
			
			try{
				f.end();
				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
				
			System.out.println(m);
			
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("123456789".equals(m.pollFirst()));
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("123456789".equals(m.pollFirst()));
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
			org.junit.Assert.assertTrue(m.isEmpty());
		leave();
	};
	
	
	@org.junit.Test public void testBlocksWihtoutBegin()throws IOException
	{
		enter();
		/*
			In this test we check if stream rejects writing block 
			if there is no event open.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		try{
				f.writeBooleanBlock(new boolean[7]);				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};	
			
		try{
				f.writeByteBlock(new byte[7]);				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};	
		try{
				f.writeByteBlock((byte)44);				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
		try{
				f.writeCharBlock("Sppp");				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
		try{
				f.writeCharBlock(new char[3]);				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
		try{
				f.writeShortBlock(new short[3]);				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
		try{
				f.writeIntBlock(new int[3]);				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
		try{
				f.writeLongBlock(new long[3]);				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
		try{
				f.writeFloatBlock(new float[3]);				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
		try{
				f.writeDoubleBlock(new double[3]);				
				System.out.println(m);
				org.junit.Assert.fail("Should have failed");
			}catch(IllegalStateException ex)
			{
				System.out.println(ex);
			};
		leave();
	};
	
	
	@org.junit.Test public void testBlocks_1()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end terminates them correctly.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeBooleanBlock(new boolean[7]);				
		f.writeBooleanBlock(new boolean[3]);
		f.end();
		f.close();
		
		System.out.println(m);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
		org.junit.Assert.assertTrue("sss".equals(m.pollFirst()));
		
		org.junit.Assert.assertTrue(m.pollFirst() instanceof boolean[]);
		org.junit.Assert.assertTrue(m.pollFirst() instanceof boolean[]);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		
		org.junit.Assert.assertTrue(m.isEmpty());
			
		leave();
	};
	
	@org.junit.Test public void testBlocks_2()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end terminates them correctly.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeByteBlock(new byte[7]);				
		f.writeByteBlock(new byte[3]);
		f.end();
		f.close();
		
		System.out.println(m);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
		org.junit.Assert.assertTrue("sss".equals(m.pollFirst()));
		
		org.junit.Assert.assertTrue(m.pollFirst() instanceof byte[]);
		org.junit.Assert.assertTrue(m.pollFirst() instanceof byte[]);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		
		org.junit.Assert.assertTrue(m.isEmpty());
			
		leave();
	};
	
	@org.junit.Test public void testBlocks_3()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end terminates them correctly.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeCharBlock(new char[7]);				
		f.writeCharBlock("888");	//Intentionally, char type block also.
		f.end();
		f.close();
		
		System.out.println(m);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
		org.junit.Assert.assertTrue("sss".equals(m.pollFirst()));
		
		org.junit.Assert.assertTrue(m.pollFirst() instanceof char[]);
		org.junit.Assert.assertTrue(m.pollFirst() instanceof char[]);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		
		org.junit.Assert.assertTrue(m.isEmpty());
			
		leave();
	};
	
	
	@org.junit.Test public void testBlocks_4()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end terminates them correctly.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeShortBlock(new short[7]);				
		f.writeShortBlock(new short[3]);
		f.end();
		f.close();
		
		System.out.println(m);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
		org.junit.Assert.assertTrue("sss".equals(m.pollFirst()));
		
		org.junit.Assert.assertTrue(m.pollFirst() instanceof short[]);
		org.junit.Assert.assertTrue(m.pollFirst() instanceof short[]);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		
		org.junit.Assert.assertTrue(m.isEmpty());
			
		leave();
	};
	
	
	@org.junit.Test public void testBlocks_5()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end terminates them correctly.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeIntBlock(new int[7]);				
		f.writeIntBlock(new int[3]);
		f.end();
		f.close();
		
		System.out.println(m);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
		org.junit.Assert.assertTrue("sss".equals(m.pollFirst()));
		
		org.junit.Assert.assertTrue(m.pollFirst() instanceof int[]);
		org.junit.Assert.assertTrue(m.pollFirst() instanceof int[]);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		
		org.junit.Assert.assertTrue(m.isEmpty());
			
		leave();
	};
	
	@org.junit.Test public void testBlocks_6()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end terminates them correctly.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeLongBlock(new long[7]);				
		f.writeLongBlock(new long[3]);
		f.end();
		f.close();
		
		System.out.println(m);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
		org.junit.Assert.assertTrue("sss".equals(m.pollFirst()));
		
		org.junit.Assert.assertTrue(m.pollFirst() instanceof long[]);
		org.junit.Assert.assertTrue(m.pollFirst() instanceof long[]);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		
		org.junit.Assert.assertTrue(m.isEmpty());
			
		leave();
	};
	
	
	@org.junit.Test public void testBlocks_7()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end terminates them correctly.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeFloatBlock(new float[7]);				
		f.writeFloatBlock(new float[3]);
		f.end();
		f.close();
		
		System.out.println(m);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
		org.junit.Assert.assertTrue("sss".equals(m.pollFirst()));
		
		org.junit.Assert.assertTrue(m.pollFirst() instanceof float[]);
		org.junit.Assert.assertTrue(m.pollFirst() instanceof float[]);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		
		org.junit.Assert.assertTrue(m.isEmpty());
			
		leave();
	};
	
	
	@org.junit.Test public void testBlocks_8()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end terminates them correctly.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeDoubleBlock(new double[7]);				
		f.writeDoubleBlock(new double[3]);
		f.end();
		f.close();
		
		System.out.println(m);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
		org.junit.Assert.assertTrue("sss".equals(m.pollFirst()));
		
		org.junit.Assert.assertTrue(m.pollFirst() instanceof double[]);
		org.junit.Assert.assertTrue(m.pollFirst() instanceof double[]);
		
		org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		
		org.junit.Assert.assertTrue(m.isEmpty());
			
		leave();
	};
	
	
	
	@org.junit.Test public void testBlocksRejectsPrimitives_1()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end then rejects primitive write,
			even if same elementary type.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeBooleanBlock(new boolean[7]);				
		f.writeBooleanBlock(new boolean[3]);
		
		try{
			f.writeBoolean(true);
			org.junit.Assert.fail("should have thrown");
		}catch(IllegalStateException ex)
		{
			System.out.println(ex);
		};
		leave();
	};
	
	@org.junit.Test public void testBlocksRejectsPrimitives_2()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end then rejects primitive write,
			even if same elementary type.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeByteBlock(new byte[7]);				
		f.writeByteBlock(new byte[3]);
		
		try{
			f.writeByte((byte)7);
			org.junit.Assert.fail("should have thrown");
		}catch(IllegalStateException ex)
		{
			System.out.println(ex);
		};
		leave();
	};       
	
	
	@org.junit.Test public void testBlocksRejectsPrimitives_3()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end then rejects primitive write,
			even if same elementary type.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeCharBlock(new char[7]);				
		f.writeCharBlock(new char[3]);
		
		try{
			f.writeChar((char)7);
			org.junit.Assert.fail("should have thrown");
		}catch(IllegalStateException ex)
		{
			System.out.println(ex);
		};
		leave();
	};
	
	@org.junit.Test public void testBlocksRejectsPrimitives_4()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end then rejects primitive write,
			even if same elementary type.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeShortBlock(new short[7]);				
		f.writeShortBlock(new short[3]);
		
		try{
			f.writeShort((short)7);
			org.junit.Assert.fail("should have thrown");
		}catch(IllegalStateException ex)
		{
			System.out.println(ex);
		};
		leave();
	};
	
	@org.junit.Test public void testBlocksRejectsPrimitives_5()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end then rejects primitive write,
			even if same elementary type.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeIntBlock(new int[7]);				
		f.writeIntBlock(new int[3]);
		
		try{
			f.writeInt(7);
			org.junit.Assert.fail("should have thrown");
		}catch(IllegalStateException ex)
		{
			System.out.println(ex);
		};
		leave();
	};
	
	@org.junit.Test public void testBlocksRejectsPrimitives_6()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end then rejects primitive write,
			even if same elementary type.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeLongBlock(new long[7]);				
		f.writeLongBlock(new long[3]);
		
		try{
			f.writeLong((long)7);
			org.junit.Assert.fail("should have thrown");
		}catch(IllegalStateException ex)
		{
			System.out.println(ex);
		};
		leave();
	};
	
	@org.junit.Test public void testBlocksRejectsPrimitives_7()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end then rejects primitive write,
			even if same elementary type.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeFloatBlock(new float[7]);				
		f.writeFloatBlock(new float[3]);
		
		try{
			f.writeFloat((float)7);
			org.junit.Assert.fail("should have thrown");
		}catch(IllegalStateException ex)
		{
			System.out.println(ex);
		};
		leave();
	};
	
	@org.junit.Test public void testBlocksRejectsPrimitives_8()throws IOException
	{
		enter();
		/*
			In this test we check if stream allows writing 
			two blocks of same type end then rejects primitive write,
			even if same elementary type.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.begin("sss");
		f.writeDoubleBlock(new double[7]);				
		f.writeDoubleBlock(new double[3]);
		
		try{
			f.writeDouble((double)7);
			org.junit.Assert.fail("should have thrown");
		}catch(IllegalStateException ex)
		{
			System.out.println(ex);
		};
		leave();
	};
	
	
	
					int close_cnt;
	@org.junit.Test public void testCloseJustOnce()throws IOException
	{
		enter();
		/*
			In this test we check if stream calls closeImpl just once.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 )
				{
					@Override protected void closeImpl()throws IOException
					{
						close_cnt++;
						super.closeImpl();
					};
				};
				close_cnt =0;
		f.close();
		f.close();
		f.close();
		
		org.junit.Assert.assertTrue(close_cnt==1);
		
		leave();
	};
	
	
					
	@org.junit.Test public void testCloseMakesUnusable()throws IOException
	{
		enter();
		/*
			In this test we check if closed stream barfs with an
			apropriate exception (EClosed) at any operation.
		*/
		final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new CObjListWriteFormat(
										 0,//int names_registry_size,
										 9,//int max_name_length,
										 2,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
		f.close();
		
		try{ f.begin("dd"); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.end(); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeBoolean(false); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeByte((byte)0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeChar(' '); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeShort((short)0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeInt(0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeLong(0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeFloat(0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeDouble(0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeBooleanBlock(new boolean[0],0,0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeByteBlock(new byte[0],0,0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeCharBlock(new char[0],0,0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeCharBlock("ss"); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeShortBlock(new short[0],0,0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeIntBlock(new int[0],0,0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeLongBlock(new long[0],0,0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeFloatBlock(new float[0],0,0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.writeDoubleBlock(new double[0],0,0); org.junit.Assert.fail(); }catch(EClosed ex){};
		try{ f.flush(); org.junit.Assert.fail(); }catch(EClosed ex){};
		leave();
	};
	
};