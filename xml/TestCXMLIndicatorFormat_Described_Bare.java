package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.CIndicatorWriteFormatProtector;
import sztejkat.abstractfmt.CIndicatorReadFormatProtector;
import sztejkat.abstractfmt.testsuite.IDeviceUnderTestFactory;
import sztejkat.abstractfmt.testsuite.indicator.TestSuite;
import sztejkat.abstractfmt.testsuite.indicator.Pair;
import sztejkat.abstractfmt.util.CCharExchangeBuffer;
import java.nio.charset.Charset;
import java.io.IOException;
import org.junit.BeforeClass;

public class TestCXMLIndicatorFormat_Described_Bare extends TestSuite
{
	@BeforeClass public static void setupTestSuiteFactory()
	{
		assert(TestSuite.FACTORY==null):"Conflicting tests initialization";
		TestSuite.FACTORY = new IDeviceUnderTestFactory<Pair>()
		{
			public Pair create()throws IOException	
			{
				CCharExchangeBuffer media = new CCharExchangeBuffer();
				return new Pair(
					new CIndicatorWriteFormatProtector(
						new CXMLIndicatorWriteFormat(
										media.getWriter(),//Writer out,
								   		 Charset.forName("UTF-8"),// charset,
								    	 SXMLSettings.LONG_BARE,//CXMLSettings settings,
								    	 true //boolean is_described
										),true),
					new CIndicatorReadFormatProtector(
						new CXMLIndicatorReadFormat(
										media.getReader(),//final Reader input,
								   		SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								   		true //boolean is_described
										))
						);
			};
		};
	};
};