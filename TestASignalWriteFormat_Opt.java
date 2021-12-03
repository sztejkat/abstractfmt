package sztejkat.abstractfmt;
import java.io.IOException;
/**
	A test for {@link ASignalWriteFormat} begin-end optimization
	made around {@link COptObjListWriteFormat}	
*/
public class TestASignalWriteFormat_Opt extends sztejkat.utils.test.ATest
{
	@org.junit.Test public void testBeginEnd()throws IOException
	{
		enter();
			/*
				In this test we check if we can have begin and end
				signal without name optimization and no begin/end optimization 
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new COptObjListWriteFormat(
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
	
	@org.junit.Test public void testBeginEndOpt1()throws IOException
	{
		enter();
			/*
				In this test we check if we can have begin and end
				signal without name optimization but with begin/end optimization
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new COptObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("SPAMERKA");
			f.writeByteBlock(new byte[0]);
			f.end();
			f.begin("LYET");
			f.end();
			f.close();
			
			System.out.println(m);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("SPAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst() instanceof byte[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("LYET".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
		leave();
	};
	
	@org.junit.Test public void testBeginEndOpt2()throws IOException
	{
		enter();
			/*
				In this test we check if we can have begin and end
				signal without name optimization but with begin/end optimization
				correctly behaving if something is in between
			*/
			final CObjListFormat m = new CObjListFormat();
			ISignalWriteFormat f = new COptObjListWriteFormat(
										 0,//int names_registry_size,
										 8,//int max_name_length,
										 0,//int max_events_recursion_depth,
										 m //CObjListFormat media
										 );
			f.begin("SPAMERKA");
			f.writeByteBlock(new byte[0]);
			f.end();
			f.writeByte((byte)0);
			f.begin("LYET");
			f.end();
			f.close();
			
			System.out.println(m);
			
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("SPAMERKA".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst() instanceof byte[]);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst() instanceof Byte);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.BEGIN_INDICATOR);
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.DIRECT_INDICATOR);
			org.junit.Assert.assertTrue("LYET".equals(m.pollFirst()));
			org.junit.Assert.assertTrue(m.pollFirst()==CObjListFormat.END_INDICATOR);
			
		leave();
	};
};
