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
	A basic test of {@link ARegisteringStructReadFormat}
	based over the {@link CObjStructReadFormat1}
	implementation.
	<p>
	Just test if added API is used right.
	<p>
	Note: just changed API is tested. Base API is covered by 
	{@link Test_AStructReadFormatBase0}. This is a bit of
	lenincy on my side, since {@link CObjStructReadFormat1}
	does not extend {@link CObjStructReadFormat0}, but remember,
	we are not testing here the implementation but only if abstract
	class works well.
*/
public class Test_ARegisteringStructReadFormat extends ATest
{
	
	@Test public void testIfDirectNamesAreHandledRight()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    s.add(new SIG_BEGIN("honey"));
		    s.add(new SIG_END_BEGIN("johan"));
		    printStream(s);
		    
		    CObjStructReadFormat1 r = new CObjStructReadFormat1(
		    					  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0//int name_registry_capacity
								  );
			r.open();
			Assert.assertTrue("honey".equals(r.next()));
			Assert.assertTrue(null==r.next());
			Assert.assertTrue("johan".equals(r.next()));
			try{
				r.next();
				Assert.fail();
			}catch(EEof ex){};
		leave();
	};
	
	@Test public void testIfDirectNamesWithRegistryAreHandledRight()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    s.add(new SIG_BEGIN("honey"));
		    s.add(new SIG_END_BEGIN("johan"));
		    printStream(s);
		    
		    CObjStructReadFormat1 r = new CObjStructReadFormat1(
		    					  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  2//int name_registry_capacity
								  );
			r.open();
			Assert.assertTrue("honey".equals(r.next()));
			Assert.assertTrue(null==r.next());
			Assert.assertTrue("johan".equals(r.next()));
			try{
				r.next();
				Assert.fail();
			}catch(EEof ex){};
		leave();
	};
	
	
	@Test public void testRegistrationWithBegin_index()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    s.add(new SIG_BEGIN_AND_REGISTER("honey",1,0));
		    s.add(new SIG_BEGIN_AND_REGISTER("money",0,1));
		    printStream(s);
		    
		    CObjStructReadFormat1 r = new CObjStructReadFormat1(
		    					  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  true,//boolean use_index_instead_of_order,
								  2//int name_registry_capacity
								  );
			r.open();
			Assert.assertTrue("honey".equals(r.next()));
			Assert.assertTrue("money".equals(r.next()));
			try{
				r.next();
				Assert.fail();
			}catch(EEof ex){};
		leave();
	};
	
	@Test public void testRegistrationWithBegin_order()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    s.add(new SIG_BEGIN_AND_REGISTER("honey",1,0));
		    s.add(new SIG_BEGIN_AND_REGISTER("money",0,1));
		    printStream(s);
		    
		    CObjStructReadFormat1 r = new CObjStructReadFormat1(
		    					  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  2//int name_registry_capacity
								  );
			r.open();
			Assert.assertTrue("honey".equals(r.next()));
			Assert.assertTrue("money".equals(r.next()));
			try{
				r.next();
				Assert.fail();
			}catch(EEof ex){};
		leave();
	};
	
	@Test public void testRegistrationWithEndBegin_index()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    s.add(new SIG_BEGIN("zupak"));		    
		    s.add(new SIG_END_BEGIN_AND_REGISTER("honey",1,0));
		    s.add(new SIG_END_BEGIN_AND_REGISTER("money",0,1));
		    printStream(s);
		    
		    CObjStructReadFormat1 r = new CObjStructReadFormat1(
		    					  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  true,//boolean use_index_instead_of_order,
								  2//int name_registry_capacity
								  );
			r.open();
			Assert.assertTrue("zupak".equals(r.next()));
			Assert.assertTrue(null==r.next());
			Assert.assertTrue("honey".equals(r.next()));
			Assert.assertTrue(null==r.next());
			Assert.assertTrue("money".equals(r.next()));
			try{
				r.next();
				Assert.fail();
			}catch(EEof ex){};
		leave();
	};
	
	
	@Test public void testRegistered_index()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    s.add(new SIG_BEGIN_AND_REGISTER("honey",1,0));
		    s.add(new SIG_BEGIN_AND_REGISTER("money",0,1));
		    s.add(new SIG_END_BEGIN_REGISTERED(1,0));
		    s.add(new SIG_END_BEGIN_REGISTERED(0,1));
		    s.add(new SIG_BEGIN_REGISTERED(1,0));
		    printStream(s);
		    
		    CObjStructReadFormat1 r = new CObjStructReadFormat1(
		    					  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  true,//boolean use_index_instead_of_order,
								  2//int name_registry_capacity
								  );
			r.open();
			Assert.assertTrue("honey".equals(r.next()));
			Assert.assertTrue("money".equals(r.next()));
			Assert.assertTrue(null==r.next());
			Assert.assertTrue("honey".equals(r.next()));
			Assert.assertTrue(null==r.next());
			Assert.assertTrue("money".equals(r.next()));
			Assert.assertTrue("honey".equals(r.next()));
			try{
				r.next();
				Assert.fail();
			}catch(EEof ex){};
		leave();
	};
	
	
	@Test public void testRegistration_without_support()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    s.add(new SIG_BEGIN("zupak"));		    
		    s.add(new SIG_END_BEGIN_AND_REGISTER("honey",1,0));
		    s.add(new SIG_END_BEGIN_AND_REGISTER("money",0,1));
		    printStream(s);
		    
		    CObjStructReadFormat1 r = new CObjStructReadFormat1(
		    					  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  true,//boolean use_index_instead_of_order,
								  0//int name_registry_capacity
								  );
			r.open();
			Assert.assertTrue("zupak".equals(r.next()));
			try{
				r.next();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex);};
		leave();
	};
	
	@Test public void testRegistration_invalid_index()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    s.add(new SIG_BEGIN("zupak"));		    
		    s.add(new SIG_END_BEGIN_AND_REGISTER("honey",2,0));
		    s.add(new SIG_END_BEGIN_AND_REGISTER("money",0,1));
		    printStream(s);
		    
		    CObjStructReadFormat1 r = new CObjStructReadFormat1(
		    					  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  true,//boolean use_index_instead_of_order,
								  2//int name_registry_capacity
								  );
			r.open();
			Assert.assertTrue("zupak".equals(r.next()));
			try{
				r.next();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex);};
		leave();
	};
	
	@Test public void testRegistration_invalid_order()throws IOException
	{
		enter();
		    CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
		    s.add(new SIG_BEGIN("zupak"));		    
		    s.add(new SIG_END_BEGIN_AND_REGISTER("honey",1,1));
		    s.add(new SIG_END_BEGIN_AND_REGISTER("money",0,1));
		    printStream(s);
		    
		    CObjStructReadFormat1 r = new CObjStructReadFormat1(
		    					  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  2//int name_registry_capacity
								  );
			r.open();
			Assert.assertTrue("zupak".equals(r.next()));
			try{
				r.next();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex);};
		leave();
	};
};