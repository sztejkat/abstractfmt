package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.CIndicatorWriteFormatProtector;
import sztejkat.abstractfmt.CIndicatorReadFormatProtector;
import sztejkat.abstractfmt.testsuite.IDeviceUnderTestFactory;
import sztejkat.abstractfmt.testsuite.indicator.TestSuite;
import sztejkat.abstractfmt.testsuite.indicator.Pair;
import sztejkat.abstractfmt.util.CFileExchangeBuffer;
import java.io.IOException;
import java.io.File;
import org.junit.BeforeClass;
/**
	Appplies indicator formats test suite.
	See source code for exactly tested setup.	
*/
public class TestCBinDescIndicatorFormat extends TestSuite
{
	@BeforeClass public static void setupTestSuiteFactory()
	{
		assert(TestSuite.FACTORY==null):"Conflicting tests initialization";
		TestSuite.FACTORY = new IDeviceUnderTestFactory<Pair>()
		{
			public Pair create(String test_class_name, String test_name)throws IOException	
			{
				final CFileExchangeBuffer media = new CFileExchangeBuffer(
								new File("test-data/id"),//File folder,
								test_class_name+"."+test_name+"_#.bin" //String file_name_pattern
									);
				return new Pair(
					new CIndicatorWriteFormatProtector(
					new CBinDescIndicatorWriteFormat(
										media.getOutput()//OutputStream output
										),true),
					new CIndicatorReadFormatProtector(			
						new CBinDescIndicatorReadFormat(
										media.getInput()//InputStream input
										))
						);
			}
		};
	};
};