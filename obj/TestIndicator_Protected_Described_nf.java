package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.CIndicatorWriteFormatProtector;
import sztejkat.abstractfmt.CIndicatorReadFormatProtector;
import sztejkat.abstractfmt.testsuite.IDeviceUnderTestFactory;
import sztejkat.abstractfmt.testsuite.indicator.TestSuite;
import sztejkat.abstractfmt.testsuite.indicator.Pair;
import java.io.IOException;
import org.junit.BeforeClass;
/**
	Appplies indicator streams test suite.
	See source code for exactly tested setup.	
*/
public class TestIndicator_Protected_Described_nf extends TestSuite
{
	@BeforeClass public static void setupTestSuiteFactory()
	{
		assert(TestSuite.FACTORY==null):"Conflicting tests initialization";
		TestSuite.FACTORY = new IDeviceUnderTestFactory<Pair>()
		{
			public Pair create(String test_class_name, String test_name)throws IOException	
			{
				final CObjListFormat media = new CObjListFormat();
				return new Pair(
					new CIndicatorWriteFormatProtector(
						new CObjIndicatorWriteFormat(
										media,//final CObjListFormat media,	
										2,//final int max_registrations,
										1024,//final int max_supported_signal_name_length,										
										true,//final boolean is_described,
										false//final boolean is_flushing
										),true),
					new CIndicatorReadFormatProtector(
						new CObjIndicatorReadFormat(
										media,//final CObjListFormat media, 
										2,//final int max_registrations,
										1024,//final int max_supported_signal_name_length,
										true,//final boolean is_described,
										false //final boolean is_flushing
										))
						);
			};
		};
	};
};