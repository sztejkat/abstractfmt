package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.test.ATest;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.*;
import sztejkat.abstractfmt.obj.*;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;


/**
	An elementary test for {@link CStringStructWriter} 
	during which we write data using {@link CStringStructWriter}
	and read using {@link IStructReadFormat}.
*/
public class Test_CStringStructWriter extends ATest
{
	@Test public void testWritingMixed()throws IOException
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
				CStringStructWriter o = new CStringStructWriter(fo);
				o.write("Albi");
				o.write('c');
				o.close();
			};
			fo.close();
			
			fi.open();
			Assert.assertTrue("Albic".equals(fi.readString(1000)));
			try{
				fi.readString();
				Assert.fail();
			}catch(EUnexpectedEof ex){};
		leave();
	};
	
};