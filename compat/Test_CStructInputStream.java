package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.test.ATest;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.*;
import sztejkat.abstractfmt.obj.*;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;


/**
	An elementary test for {@link CStructInputStream} 
	during which we write data using {@link IStructWriteFormat}
	and read using {@link CStructInputStream}.
*/
public class Test_CStructInputStream extends ATest
{
	@Test public void testSingleByteOps()throws IOException
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
			fo.writeByteBlock(new byte[]{(byte)33,(byte)44,(byte)45});
			fo.close();
			fi.open();
			{
				CStructInputStream i = new CStructInputStream(fi);
				Assert.assertTrue(i.read()==(33 & 0xFF));
				Assert.assertTrue(i.read()==(44 & 0xFF));
				Assert.assertTrue(i.read()==(45 & 0xFF));
				Assert.assertTrue(i.read()==-1);
				Assert.assertTrue(i.read()==-1);
				i.close();
			};
			
		leave();
	};
	
	@Test public void testSingleByteOpsInSignal()throws IOException
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
			fo.writeByteBlock(new byte[]{(byte)33,(byte)44,(byte)45});
			fo.end();
			fo.close();
			fi.open();
			fi.next();
			{
				CStructInputStream i = new CStructInputStream(fi);
				Assert.assertTrue(i.read()==(33 & 0xFF));
				Assert.assertTrue(i.read()==(44 & 0xFF));
				Assert.assertTrue(i.read()==(45 & 0xFF));
				Assert.assertTrue(i.read()==-1);
				Assert.assertTrue(i.read()==-1);
				i.close();
			};
			
		leave();
	};
	
	@Test public void testBlockOps()throws IOException
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
			fo.writeByteBlock(new byte[]{(byte)33,(byte)44,(byte)45});
			fo.close();
			
			fi.open();
			{
				CStructInputStream i = new CStructInputStream(fi);
				byte [] b = new byte[32];
				Assert.assertTrue(3==i.read(b));
				Assert.assertTrue(b[0]==(byte)33);
				Assert.assertTrue(b[1]==(byte)44);
				Assert.assertTrue(b[2]==(byte)45);
				Assert.assertTrue(-1==i.read(b));
				i.close();
			};
			
		leave();
	};
	
	@Test public void testBlockOpsInSignal()throws IOException
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
			fo.writeByteBlock(new byte[]{(byte)33,(byte)44,(byte)45});
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructInputStream i = new CStructInputStream(fi);
				byte [] b = new byte[32];
				Assert.assertTrue(3==i.read(b));
				Assert.assertTrue(b[0]==(byte)33);
				Assert.assertTrue(b[1]==(byte)44);
				Assert.assertTrue(b[2]==(byte)45);
				Assert.assertTrue(-1==i.read(b));
				Assert.assertTrue(-1==i.read(b));
				i.close();
			};
			
		leave();
	};
};