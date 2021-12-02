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
		enter();
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
		enter();
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
		enter();
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
			
		enter();
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
			
		enter();
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
		enter();
	};
	
	
	@org.junit.Test public void testBeginNameLengthToolong()throws IOException
	{
		enter();
			/*
				In this test we check if we stream defends against too long
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
			
			
		enter();
	};
};