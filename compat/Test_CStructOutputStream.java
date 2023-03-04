package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.test.ATest;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.*;
import sztejkat.abstractfmt.obj.*;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;


/**
	An elementary test for {@link CStructOutputStream} 
	during which we write data using {@link CStructOutputStream}
	and read using {@link IStructReadFormat}.
*/
public class Test_CStructOutputStream extends ATest
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
			{
				CStructOutputStream o = new CStructOutputStream(fo);
				o.write(33);
				o.write(44);
				o.write(45);
				o.close();
			};
			fo.close();
			
			fi.open();
			Assert.assertTrue(fi.readByteBlock()==(byte)33);
			Assert.assertTrue(fi.readByteBlock()==(byte)44);
			Assert.assertTrue(fi.readByteBlock()==(byte)45);
			try{
				fi.readByteBlock();
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			
		leave();
	};
	
	
	
	@Test public void testBlockByteOps()throws IOException
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
			{
				CStructOutputStream o = new CStructOutputStream(fo);
				o.write(new byte[]{(byte)33,(byte)44,(byte)45});
				o.write(new byte[]{(byte)33,(byte)44,(byte)45});
				o.close();
			};
			fo.close();
			
			fi.open();
			Assert.assertTrue(fi.readByteBlock()==(byte)33);
			Assert.assertTrue(fi.readByteBlock()==(byte)44);
			Assert.assertTrue(fi.readByteBlock()==(byte)45);
			
			Assert.assertTrue(fi.readByteBlock()==(byte)33);
			Assert.assertTrue(fi.readByteBlock()==(byte)44);
			Assert.assertTrue(fi.readByteBlock()==(byte)45);
			try{
				fi.readByteBlock();
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			
		leave();
	};
	
	@Test public void testBlockByteOpsGeneratesCloseImpl()throws IOException
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
			fo.begin("aaa");
			{
				CStructOutputStream o = new CStructOutputStream(fo)
				{
					protected void closeImpl()throws IOException{ out.end(); };
				};
				o.write(new byte[]{(byte)33,(byte)44,(byte)45});
				o.write(new byte[]{(byte)33,(byte)44,(byte)45});
				o.close();
			};
			fo.close();
			
			fi.open();
			fi.next();
			Assert.assertTrue(fi.readByteBlock()==(byte)33);
			Assert.assertTrue(fi.readByteBlock()==(byte)44);
			Assert.assertTrue(fi.readByteBlock()==(byte)45);
			
			Assert.assertTrue(fi.readByteBlock()==(byte)33);
			Assert.assertTrue(fi.readByteBlock()==(byte)44);
			Assert.assertTrue(fi.readByteBlock()==(byte)45);
			try{
				fi.readByteBlock();
				Assert.fail();
			}catch(ENoMoreData ex){};
			
		leave();
	};
};