package sztejkat.abstractfmt.bin.escape;
import  sztejkat.abstractfmt.*;
import  sztejkat.abstractfmt.test.*;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import java.io.File;
import java.io.IOException;
/**
	An inter-operational test for escape formats, using 
	indexed registration mode
*/
public class TestSuite_EscapeFormat extends ADefaultUntypedTestSuite
{
	@BeforeClass public static void armImplementation()
	{
		//Here You decide what implementations will be tested
		AInterOpTestCase.factory = new IInteropTestDeviceFactory()
		{
			@SuppressWarnings("unchecked")
			public <R extends IStructReadFormat,
			   	    W extends IStructWriteFormat>
			        CPair<R,W> createTestDevice(File temp_folder)throws IOException
			{
					//We will provide content dump to file.
					final File file = new File(temp_folder+"/content.bin");
					//Now low level I/O
					FileOutputStream o = new FileOutputStream(file);
					FileInputStream i  = new FileInputStream(file);
					CEscapeWriteFormat writer = new CEscapeWriteFormat(
					   128,//int name_registry_capacity,
					   o,//OutputStream raw,
					   true //boolean indexed_registration
					   );
					CEscapeReadFormat reader = new CEscapeReadFormat(
								128,//int name_registry_capacity,
								i  //InputStream raw)
								);
					return new CPair<R,W>( 
									(R)reader, 
									(W)writer, 
									file
									); 
			};
		};
	};
	@AfterClass public static void disarmImplementation()
	{
		AInterOpTestCase.factory = null;
	};
};