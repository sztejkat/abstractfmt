package sztejkat.abstractfmt.txt.xml;
import  sztejkat.abstractfmt.*;
import  sztejkat.abstractfmt.test.*;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import java.io.*;
/**
		A test suite checking if {@link CXMLWriteFormat}/{@link CXMLReadFormat}
		do obey base contract.
		<p>
		This test suite dumps test files content to text files on writer close
		using struct level indentation. 
*/
public class TestSuite_CXMLFormat extends ADefaultUntypedTestSuite
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
					final File file = new File(temp_folder+"/content.txt");
					
					//Content.
					CXMLWriteFormat writer = new CXMLWriteFormat(
									new OutputStreamWriter(
											new FileOutputStream(file),
											"UTF-8")
								  );
					CXMLReadFormat reader = new CXMLReadFormat(
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
