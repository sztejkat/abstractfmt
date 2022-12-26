package sztejkat.abstractfmt;
import sztejkat.abstractfmt.test.ATest;
import sztejkat.abstractfmt.obj.*;
import sztejkat.abstractfmt.utils.CAddablePollableArrayList;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.util.Iterator;
import static sztejkat.abstractfmt.Test_AStructWriteFormatBase0.printStream;
/**
	A test of {@link AReservedNameReadFormat}
	using {@link CObjStructReadFormat1} 
*/
public class Test_AReservedNameReadFormat extends ATest
{
			public final class DUT extends AReservedNameReadFormat
			{
				DUT(IStructReadFormat engine, char escape)
				{
					super(engine,escape);
				};
				@Override public void reserveName(String name)
				{
					super.reserveName(name);
				};
				public boolean _isReservedName(String name){ return super.isReservedName(name); };
				public String _unescape(String name)throws EBrokenFormat
				{
					return super.unescape(name);
				}
			};
			
	@Test public void testReservationWithoutRegistry()throws IOException
	{
		//Note: There is no need to test it against names registry, because
		//names registry is transparently handled by lower level. It might
		//be testable on writing side, but there is no need to test it on
		//reading side.
		enter();
			CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructReadFormat1 back_end = new CObjStructReadFormat1( 
								  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  true,//boolean use_index_instead_of_order,
								  2 //int name_registry_capacity
								  );
			DUT r = new DUT(
							back_end,//IStructWriteFormat engine, 
							'/' //char escape)
							);
			r.open();
			//Make sure some names are reserved
			r.reserveName("int");
			r.reserveName("char");
			
			//Now fake a stream which do carry them as reserved names.
			s.add(new SIG_BEGIN("int"));
			s.add(new SIG_BEGIN("char"));
			//and in their escaped form
			s.add(new SIG_BEGIN("/int"));
			s.add(new SIG_BEGIN("/char"));
			//followed by a regular name which starts from an escape
			s.add(new SIG_BEGIN("//name"));
			
			printStream(s);
			
			//Now fetch them
			{
				String n = r.next();
				Assert.assertTrue("int".equals(n));
				Assert.assertTrue(r._isReservedName(n));
			};
			{
				String n = r.next();
				Assert.assertTrue("char".equals(n));
				Assert.assertTrue(r._isReservedName(n));
			};
			{
				String n = r.next();
				Assert.assertTrue("/int".equals(n));
				Assert.assertTrue(!r._isReservedName(n));
				n = r._unescape(n);
				Assert.assertTrue("int".equals(n));
			};
			{
				String n = r.next();
				Assert.assertTrue("/char".equals(n));
				Assert.assertTrue(!r._isReservedName(n));
				n = r._unescape(n);
				Assert.assertTrue("char".equals(n));
			};
			{
				String n = r.next();
				Assert.assertTrue("//name".equals(n));
				Assert.assertTrue(!r._isReservedName(n));
				n = r._unescape(n);
				Assert.assertTrue("/name".equals(n));
			};
			
		leave();
	};
	
	
	@Test public void testRobustness_escaped_non_reserved()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructReadFormat1 back_end = new CObjStructReadFormat1( 
								  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  true,//boolean use_index_instead_of_order,
								  2 //int name_registry_capacity
								  );
			DUT r = new DUT(
							back_end,//IStructWriteFormat engine, 
							'/' //char escape)
							);
			r.open();
			//Make sure some names are reserved
			r.reserveName("int");
			r.reserveName("char");
			
			//Now fake a stream which do carry escaped IMPROPER reserved name
			s.add(new SIG_BEGIN("/long"));
			
			printStream(s);
			
			//Now fetch them, failing.
			{
				String n = r.next();
				Assert.assertTrue("/long".equals(n));
				Assert.assertTrue(!r._isReservedName(n));
				try{
					n = r._unescape(n);
					Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex); };
			};
			
		leave();
	};
	
	
	@Test public void testRobustness_empty()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructReadFormat1 back_end = new CObjStructReadFormat1( 
								  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  true,//boolean use_index_instead_of_order,
								  2 //int name_registry_capacity
								  );
			DUT r = new DUT(
							back_end,//IStructWriteFormat engine, 
							'/' //char escape)
							);
			r.open();
			//Make sure some names are reserved
			r.reserveName(""); //empty will be reserved.
			
			//Fake it to carry a reserved empty.
			s.add(new SIG_BEGIN(""));
			//followed by escaped empty
			s.add(new SIG_BEGIN("/"));
			
			printStream(s);
			
			{
				String n = r.next();
				Assert.assertTrue("".equals(n));
				Assert.assertTrue(r._isReservedName(n));
			};
			{
				String n = r.next();
				Assert.assertTrue("/".equals(n));
				Assert.assertTrue(!r._isReservedName(n));
				n = r._unescape(n);
				Assert.assertTrue("".equals(n));
			};
			
		leave();
	};
	
	@Test public void testRobustness_empty_2()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructReadFormat1 back_end = new CObjStructReadFormat1( 
								  s,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  true,//boolean use_index_instead_of_order,
								  2 //int name_registry_capacity
								  );
			DUT r = new DUT(
							back_end,//IStructWriteFormat engine, 
							'/' //char escape)
							);
			r.open();
			
			//Pass empty non-reserved, non escaped.
			s.add(new SIG_BEGIN(""));
			
			printStream(s);
			
			{
				String n = r.next();
				Assert.assertTrue("".equals(n));
				Assert.assertTrue(!r._isReservedName(n));
				n = r._unescape(n);
				Assert.assertTrue("".equals(n));
			};
			
		leave();
	};
	
};