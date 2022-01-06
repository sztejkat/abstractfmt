package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.testsuite.IDeviceUnderTestFactory;
import sztejkat.abstractfmt.testsuite.signal.TestSuite;
import sztejkat.abstractfmt.testsuite.signal.Pair;
import sztejkat.abstractfmt.util.CFileExchangeBuffer;
import java.io.IOException;
import java.io.File;
import org.junit.BeforeClass;
/**
	Appplies signal formats test suite.
	See source code for exactly tested setup.	
*/
public class TestCBinSignalFormat extends TestSuite
{
	@BeforeClass public static void setupTestSuiteFactory()
	{
		assert(TestSuite.FACTORY==null):"Conflicting tests initialization";
		TestSuite.FACTORY = new IDeviceUnderTestFactory<Pair>()
		{
			public Pair create(String test_class_name, String test_name)throws IOException	
			{
				final CFileExchangeBuffer media = new CFileExchangeBuffer(
								new File("test-data/s"),//File folder,
								test_class_name+"."+test_name+"_#.bin" //String file_name_pattern
									);
				return new Pair(
					new CBinSignalWriteFormat(
										media.getOutput(),//OutputStream output
										true	//test_mode
										),
					new CBinSignalReadFormat(
										media.getInput(),//InputStream input
										true //test mode
										)
						);
			}
		};
	};
};