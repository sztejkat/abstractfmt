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
	A test of {@link AReservedNameWriteFormat}
	using {@link CObjStructWriteFormat1} 
*/
public class Test_AReservedNameWriteFormat extends ATest
{
			public final class DUT extends AReservedNameWriteFormat
			{
				DUT(IStructWriteFormat engine, char escape)
				{
					super(engine,escape);
				};
				@Override public void reserveName(String name)
				{
					super.reserveName(name);
				};
				public boolean _isReservedName(String name){ return super.isReservedName(name); };
				@Override public void beginReserved(String name)throws IOException
				{
					super.beginReserved(name);
				}
			};
	@Test public void testReservationWithoutRegistry()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructWriteFormat1 back_end = new CObjStructWriteFormat1(  
								  false,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  10,//int name_registry_capacity,
								  s //IAddable<IObjStructFormat0> stream
								  );
			DUT w = new DUT(
							back_end,//IStructWriteFormat engine, 
							'/' //char escape)
							);
			
			w.open();
			//reserve some names
				w.reserveName("int");
				w.reserveName("char");
				
				Assert.assertTrue(w._isReservedName("int"));
				Assert.assertTrue(w._isReservedName("char"));
				Assert.assertTrue(!w._isReservedName("long"));
				//Now write them using reserved mode:
				w.beginReserved("int");
				w.beginReserved("char");
				//and using non reserved mode.
				w.begin("int");
				w.begin("char");
				w.begin("long");
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			//in reserved mode they are un-escaped.
			Assert.assertTrue(new SIG_BEGIN("int").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("char").equalsTo(I.next()));
			//in non-reserved they are escaped.
			Assert.assertTrue(new SIG_BEGIN("/int").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("/char").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("long").equalsTo(I.next()));;
			
		leave();
	};
	
	
	@Test public void testReservationWithoutRegistry_leadingEscape()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructWriteFormat1 back_end = new CObjStructWriteFormat1(  
								  false,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  10,//int name_registry_capacity,
								  s //IAddable<IObjStructFormat0> stream
								  );
			DUT w = new DUT(
							back_end,//IStructWriteFormat engine, 
							'/' //char escape)
							);
			
			w.open();
			//reserve some names
				w.reserveName("int");
				w.reserveName("char");
				
				Assert.assertTrue(w._isReservedName("int"));
				Assert.assertTrue(w._isReservedName("char"));
				Assert.assertTrue(!w._isReservedName("/long"));
				//Now write them using reserved mode:
				w.beginReserved("int");
				w.beginReserved("char");
				//and using non reserved mode.
				w.begin("int");
				w.begin("char");
				w.begin("/long"); //<-- here we use an escape which needs to be escaped.
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			//in reserved mode they are un-escaped.
			Assert.assertTrue(new SIG_BEGIN("int").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("char").equalsTo(I.next()));
			//in non-reserved they are escaped.
			Assert.assertTrue(new SIG_BEGIN("/int").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("/char").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("//long").equalsTo(I.next()));;
			
		leave();
	};
	
	
	@Test public void testReservationWithRegistry()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> s = new CAddablePollableArrayList<IObjStructFormat0>();
			CObjStructWriteFormat1 back_end = new CObjStructWriteFormat1(  
								  false,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  10,//int name_registry_capacity,
								  s //IAddable<IObjStructFormat0> stream
								  );
			DUT w = new DUT(
							back_end,//IStructWriteFormat engine, 
							'/' //char escape)
							);
			
			w.open();
				//reserve some names
				w.reserveName("int");
				w.reserveName("char");
				//indicate they are to be optimized with the registry
				w.optimizeBeginName("char");
				w.optimizeBeginName("int");
				
				Assert.assertTrue(w._isReservedName("int"));
				Assert.assertTrue(w._isReservedName("char"));
				Assert.assertTrue(!w._isReservedName("long"));
				//Now write them using reserved mode:
				w.beginReserved("int");
				w.beginReserved("char");
				//and using non reserved mode.
				w.begin("int");
				w.begin("char");
				w.begin("long");
			w.close();
			
			printStream(s);
			Iterator<IObjStructFormat0> I = s.iterator();
			//in reserved mode they are un-escaped.
			Assert.assertTrue(new SIG_BEGIN_AND_REGISTER("int",1,0).equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN_AND_REGISTER("char",0,1).equalsTo(I.next()));
			//in non-reserved they are escaped.
			Assert.assertTrue(new SIG_BEGIN("/int").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("/char").equalsTo(I.next()));
			Assert.assertTrue(new SIG_BEGIN("long").equalsTo(I.next()));;
			
		leave();
	};
	
};