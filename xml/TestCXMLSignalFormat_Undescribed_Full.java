package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.testsuite.IDeviceUnderTestFactory;
import sztejkat.abstractfmt.testsuite.signal.TestSuite;
import sztejkat.abstractfmt.testsuite.signal.Pair;
import sztejkat.abstractfmt.util.CCharFileExchangeBuffer;
import java.nio.charset.Charset;
import java.io.IOException;
import org.junit.BeforeClass;

public class TestCXMLSignalFormat_Undescribed_Full extends TestSuite
{
	@BeforeClass public static void setupTestSuiteFactory()
	{
		assert(TestSuite.FACTORY==null):"Conflicting tests initialization";
		TestSuite.FACTORY = new IDeviceUnderTestFactory<Pair>()
		{
			public Pair create(String test_class_name, String test_name)throws IOException	
			{
				CCharFileExchangeBuffer media = new CCharFileExchangeBuffer(
										new java.io.File("test-data/uf"),// folder,
										test_class_name+"."+test_name+".#.xml" //String file_name_pattern
														);
				System.out.println("will use file \""+media.getFile()+"\"");
				return new Pair(
						new CXMLSignalWriteFormat(
								   media.getWriter(),// final Writer out,
								   Charset.forName("UTF-8"),//final Charset charset,
								   SXMLSettings.LONG_FULL_UTF8,//final CXMLSettings settings,
								   false,//boolean is_described,
								   true //boolean test_mode
								   ),
						new CXMLSignalReadFormat(
										media.getReader(),//final Reader input,
								   		SXMLSettings.LONG_FULL_UTF8,//final CXMLSettings settings,
								   		false, //boolean is_described
								   		true //boolean test_mode
										)
						);
			};
		};
	};
};