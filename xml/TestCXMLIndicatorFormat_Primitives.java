package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CCharExchangeBuffer;
import java.nio.charset.Charset;

public class TestCXMLIndicatorFormat_Primitives extends ATestIIndicatorFormat_Primitives
{
	@Override protected Pair create()
	{
		CCharExchangeBuffer media = new CCharExchangeBuffer();
		return new Pair(
				new CIndicatorWriteFormatProtector(
					new CXMLIndicatorWriteFormat(
										 media.getWriter(),//Writer out,
								   		 Charset.forName("UTF-8"),// charset,
								    	 SXMLSettings.LONG_FULL_UTF8,//CXMLSettings settings,
								    	 false //boolean is_described
										)),
				new CIndicatorReadFormatProtector(			
					new CXMLIndicatorReadFormat(
									media.getReader(),//final Reader input,
								   	SXMLSettings.LONG_FULL_UTF8,//final CXMLSettings settings,
								   	false //boolean is_described
										))
					);
	};
};