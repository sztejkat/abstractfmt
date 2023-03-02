package sztejkat.abstractfmt.txt.json;
import  sztejkat.abstractfmt.*;
import  sztejkat.abstractfmt.test.*;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import java.io.*;
/**
		A test suite checking if {@link CHFJSONWriteFormat}/{@link CJSONReadFormat}
		do obey base contract. 
*/
public class TestSuite_CHFJSONFormat extends ADefaultUntypedTestSuite
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
					final File file = new File(temp_folder+"/content.json");
					
					//Content.
					CHFJSONWriteFormat writer = new CHFJSONWriteFormat(
									new OutputStreamWriter(
											new FileOutputStream(file),
											"UTF-8"),16
								  );
					CJSONReadFormat reader = new CJSONReadFormat(
											new InputStreamReader(
														new FileInputStream(file),
														"UTF-8")
								  );
					return new CPair<R,W>( (R)reader, (W)writer, file); 
			};
		};
	};
	@AfterClass public static void disarmImplementation()
	{
		AInterOpTestCase.factory = null;
	};
	
};
