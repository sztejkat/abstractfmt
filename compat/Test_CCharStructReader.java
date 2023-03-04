package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.test.ATest;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.*;
import sztejkat.abstractfmt.obj.*;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;


/**
	An elementary test for {@link CCharStructReader} 
	during which we write data using {@link IStructWriteFormat}
	and read using {@link CCharStructReader}.
*/
public class Test_CCharStructReader extends ATest
{
	@Test public void testSingleCharRead()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeCharBlock(new char[]{'A','l','b','i'});
			fo.close();
			
			fi.open();
			{
				CCharStructReader i = new CCharStructReader(fi);
				Assert.assertTrue(i.read()=='A');
				Assert.assertTrue(i.read()=='l');
				Assert.assertTrue(i.read()=='b');
				Assert.assertTrue(i.read()=='i');
				Assert.assertTrue(i.read()==-1);
				Assert.assertTrue(i.read()==-1);
			};
		leave();
	};
	
	
	@Test public void testSingleCharReadInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeCharBlock(new char[]{'A','l','b','i'});
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CCharStructReader i = new CCharStructReader(fi);
				Assert.assertTrue(i.read()=='A');
				Assert.assertTrue(i.read()=='l');
				Assert.assertTrue(i.read()=='b');
				Assert.assertTrue(i.read()=='i');
				Assert.assertTrue(i.read()==-1);
				Assert.assertTrue(i.read()==-1);
			};
		leave();
	};
	
	
	@Test public void testBlockCharRead()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeCharBlock(new char[]{'A','l','b','i'});
			fo.close();
			
			fi.open();
			{
				CCharStructReader i = new CCharStructReader(fi);
				char [] c = new char[100];
				Assert.assertTrue(3==i.read(c,0,3));
				Assert.assertTrue(c[0]=='A');
				Assert.assertTrue(c[1]=='l');
				Assert.assertTrue(c[2]=='b');
				Assert.assertTrue(1==i.read(c,0,3));
				Assert.assertTrue(c[0]=='i');
				Assert.assertTrue(-1==i.read(c));
			};
		leave();
	};
	@Test public void testBlockCharReadInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeCharBlock(new char[]{'A','l','b','i'});
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CCharStructReader i = new CCharStructReader(fi);
				char [] c = new char[100];
				Assert.assertTrue(3==i.read(c,0,3));
				Assert.assertTrue(c[0]=='A');
				Assert.assertTrue(c[1]=='l');
				Assert.assertTrue(c[2]=='b');
				Assert.assertTrue(1==i.read(c,0,3));
				Assert.assertTrue(c[0]=='i');
				Assert.assertTrue(-1==i.read(c));
			};
		leave();
	};
};