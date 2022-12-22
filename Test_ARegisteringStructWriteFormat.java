package sztejkat.abstractfmt;
import sztejkat.abstractfmt.test.*;
import sztejkat.abstractfmt.obj.*;
import sztejkat.abstractfmt.utils.CAddablePollableArrayList;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.util.Iterator;
import static sztejkat.abstractfmt.Test_AStructWriteFormatBase0.printStream;
/**
	A basic test of {@link ARegisteringStructWriteFormat}
	based over the {@link CObjStructWriteFormat1}
	implementation.
	<p>
	Just test if added API is used right.
	<p>
	Note: just changed API is tested. Base API is covered by 
	{@link Test_AStructWriteFormatBase0}. This is a bit of
	lenincy on my side, since {@link CObjStructWriteFormat1}
	does not extend {@link CObjStructWriteFormat0}, but remember,
	we are not testing here the implementation but only if abstract
	class works well.
*/
public class Test_ARegisteringStructWriteFormat extends ATest
{
	@Test public void testIfDirectNamesWorksWithoutRegistry()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    CObjStructWriteFormat1 w = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  0, //int name_registry_capacity
								  s
								  );
			w.open();
			w.begin("honney");
			w.begin("money");
			w.end();
			w.begin("zortax");
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			Assert.assertTrue(new SIG_BEGIN("honney").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("money").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("zortax").equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	
	
	@Test public void testIfDirectNamesWorksIsUsedWithoutARegistry()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    CObjStructWriteFormat1 w = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  0, //int name_registry_capacity
								  s
								  );
			w.open();
			Assert.assertTrue(w.optimizeBeginName("money")); //stream does NOT support optimization
			w.begin("honney");
			w.begin("money");
			w.end();
			w.begin("zortax");
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			Assert.assertTrue(new SIG_BEGIN("honney").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("money").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("zortax").equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	
	@Test public void testIfRegistyIsUsed_1()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    CObjStructWriteFormat1 w = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  1, //int name_registry_capacity
								  s
								  );
			w.open();
			Assert.assertTrue(w.optimizeBeginName("money"));
			w.begin("honney");
			w.begin("money");
			w.end();
			w.begin("zortax");
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			Assert.assertTrue(new SIG_BEGIN("honney").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN_AND_REGISTER("money",0,0).equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("zortax").equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	@Test public void testIfRegistyIsUsed_2()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    CObjStructWriteFormat1 w = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  1, //int name_registry_capacity
								  s
								  );
			w.open();
			Assert.assertTrue(w.optimizeBeginName("money"));
			w.begin("honney");
			w.begin("loony");
			w.end();
			w.begin("money");
			w.end();
			w.begin("zortax");
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			Assert.assertTrue(new SIG_BEGIN("honney").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("loony").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN_AND_REGISTER("money",0,0).equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN("zortax").equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	
	@Test public void testIfRegistyIsReUsed()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    CObjStructWriteFormat1 w = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  1, //int name_registry_capacity
								  s
								  );
			w.open();
			Assert.assertTrue(w.optimizeBeginName("money"));
			w.begin("honney");
			w.begin("money");
			w.end();
			w.begin("money");
			w.begin("money");
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			Assert.assertTrue(new SIG_BEGIN("honney").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN_AND_REGISTER("money",0,0).equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN_REGISTERED(0,0).equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN_REGISTERED(0,0).equalsTo(I.next()));
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	
	@Test public void testIfRegistyIsCorrectlyFindingOrder_1()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    CObjStructWriteFormat1 w = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  2, //int name_registry_capacity
								  s
								  );
			w.open();
			Assert.assertTrue(w.optimizeBeginName("money")); //at index 0
			Assert.assertTrue(w.optimizeBeginName("joe"));   //at index 1
			
			//but use in other order.
			w.begin("joe");
			w.begin("money");
			
			w.begin("joe");
			w.begin("money");
			
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			Assert.assertTrue(new SIG_BEGIN_AND_REGISTER("joe",1,0).equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN_AND_REGISTER("money",0,1).equalsTo(I.next()));
			
			Assert.assertTrue(new SIG_BEGIN_REGISTERED(1,0).equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN_REGISTERED(0,1).equalsTo(I.next()));
			
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	
	@Test public void testIfRegistyIsCorrectlyFindingOrder_2()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    CObjStructWriteFormat1 w = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  2, //int name_registry_capacity
								  s
								  );
			w.open();
			Assert.assertTrue(w.optimizeBeginName("money")); //at index 0
			Assert.assertTrue(w.optimizeBeginName("joe"));   //at index 1
			
			//but use in other order.
			w.begin("aniki");
			w.end();
			w.begin("joe");
			w.end();
			w.begin("money");
			
			w.end();
			w.begin("joe");
			w.end();
			w.begin("money");
			
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			Assert.assertTrue(new SIG_BEGIN("aniki").equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN_AND_REGISTER("joe",1,0).equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN_AND_REGISTER("money",0,1).equalsTo(I.next()));
			
			Assert.assertTrue(new SIG_END_BEGIN_REGISTERED(1,0).equalsTo(I.next()));
			Assert.assertTrue(new SIG_END_BEGIN_REGISTERED(0,1).equalsTo(I.next()));
			
			Assert.assertTrue(!I.hasNext());
		leave();
	};
	
	
	@Test public void testIfRegistyOverflows()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    CObjStructWriteFormat1 w = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  1, //int name_registry_capacity
								  s
								  );
			w.open();
			Assert.assertTrue(w.optimizeBeginName("money")); //at index 0
			Assert.assertTrue(!w.optimizeBeginName("joe"));   //rejected
			
			//but use in other order.
			w.begin("joe");
			w.begin("money");
			
			w.begin("joe");
			w.begin("money");
			
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			Assert.assertTrue(new SIG_BEGIN("joe").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN_AND_REGISTER("money",0,0).equalsTo(I.next()));
			
			Assert.assertTrue(new SIG_BEGIN("joe").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN_REGISTERED(0,0).equalsTo(I.next()));
			
			Assert.assertTrue(!I.hasNext());
		leave();
	};
};