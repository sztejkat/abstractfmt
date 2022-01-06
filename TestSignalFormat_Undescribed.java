package sztejkat.abstractfmt;
import sztejkat.abstractfmt.obj.*;
import sztejkat.abstractfmt.testsuite.IDeviceUnderTestFactory;
import sztejkat.abstractfmt.testsuite.signal.TestSuite;
import sztejkat.abstractfmt.testsuite.signal.Pair;
import java.io.IOException;
import java.io.File;
import org.junit.BeforeClass;
/**
	Applies signal format test suite to generic implementations
	defined in this package with {@link CObjListFormat}
	as a back-end.
*/
public class TestSignalFormat_Undescribed extends TestSuite
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
					new CSignalWriteFormat(
						new CIndicatorWriteFormatProtector(
							new CDumpingObjIndicatorWriteFormat(
											media,//final CObjListFormat media,	
											2,//final int max_registrations,
											1024,//final int max_supported_signal_name_length,										
											false,//final boolean is_described,
											false,//final boolean is_flushing
											false, //final boolean disable_end_begin_opt
											new File("test-data/u"), //final File folder,
											test_class_name+"."+test_name+"_#.dump" //final String file_name_pattern
											),true) //IIndicatorWriteFormat output
									),
					new CSignalReadFormat(
							new CIndicatorReadFormatProtector(
								new CObjIndicatorReadFormat(
												media,//final CObjListFormat media, 
												2,//final int max_registrations,
												1024,//final int max_supported_signal_name_length,
												false,//final boolean is_described,
												false //final boolean is_flushing
												))// IIndicatorReadFormat input
								)
						);
			};
		};
	};
};