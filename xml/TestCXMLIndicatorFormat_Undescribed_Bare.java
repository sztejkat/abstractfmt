package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.CIndicatorWriteFormatProtector;
import sztejkat.abstractfmt.CIndicatorReadFormatProtector;
import sztejkat.abstractfmt.testsuite.IDeviceUnderTestFactory;
import sztejkat.abstractfmt.testsuite.indicator.TestSuite;
import sztejkat.abstractfmt.testsuite.indicator.Pair;
import sztejkat.abstractfmt.util.CCharFileExchangeBuffer;
import java.nio.charset.Charset;
import java.io.IOException;
import org.junit.BeforeClass;

public class TestCXMLIndicatorFormat_Undescribed_Bare extends TestSuite
{
	@BeforeClass public static void setupTestSuiteFactory()
	{
		assert(TestSuite.FACTORY==null):"Conflicting tests initialization";
		TestSuite.FACTORY = new IDeviceUnderTestFactory<Pair>()
		{
			public Pair create(String test_class_name, String test_name)throws IOException	
			{
				CCharFileExchangeBuffer media = new CCharFileExchangeBuffer(
										new java.io.File("test-data/ub"),// folder,
										test_class_name+"."+test_name+".#.xml" //String file_name_pattern
														);
				System.out.println("will use file \""+media.getFile()+"\"");
				return new Pair(
					new CIndicatorWriteFormatProtector(
						new CXMLIndicatorWriteFormat(
										media.getWriter(),//Writer out,
								   		 Charset.forName("UTF-8"),// charset,
								    	 SXMLSettings.BARE,//CXMLSettings settings,
								    	 false //boolean is_described
										),true),
					new CIndicatorReadFormatProtector(
						new CXMLIndicatorReadFormat(
										media.getReader(),//final Reader input,
								   		SXMLSettings.BARE,//final CXMLSettings settings,
								   		false //boolean is_described
										))
						);
			};
		};
	};
};