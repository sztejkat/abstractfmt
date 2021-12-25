package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CCharExchangeBuffer;
import java.nio.charset.Charset;

public class TestCXMLSignalFormat_bD_Events extends ATestISignalFormat_Events
{
	@Override protected Pair create()
	{
		CCharExchangeBuffer media = new CCharExchangeBuffer();
		return new Pair(
				new CXMLSignalWriteFormat(
										 0,//int max_events_recursion_depth,
										 media.getWriter(),//Writer out,
								   		 Charset.forName("UTF-8"),// charset,
								    	 SXMLSettings.LONG_BARE,//CXMLSettings settings,
								    	 true, //boolean is_described
								    	 true //test_mode
										),
				new CXMLSignalReadFormat(	
									0,//int max_events_recursion_depth,
								    media.getReader(),//final Reader input,
								    SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								   	true,//boolean is_described
								   	true //boolean test_mode
										)
					);
	};
};