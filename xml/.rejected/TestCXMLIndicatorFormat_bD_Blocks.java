package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CCharExchangeBuffer;
import java.nio.charset.Charset;
import java.io.IOException;

public class TestCXMLIndicatorFormat_bD_Blocks extends ATestIIndicatorFormat_Blocks
{
	@Override protected Pair create()
	{
		CCharExchangeBuffer media = new CCharExchangeBuffer();
		return new Pair(
				new CIndicatorWriteFormatProtector(
					new CXMLIndicatorWriteFormat(
										 media.getWriter(),//Writer out,
								   		 Charset.forName("UTF-8"),// charset,
								    	 SXMLSettings.LONG_BARE,//CXMLSettings settings,
								    	 true //boolean is_described
										){
										public void close()throws IOException
										{
											System.out.println(media.toString());
											super.close();
										}}),
				new CIndicatorReadFormatProtector(			
					new CXMLIndicatorReadFormat(
									media.getReader(),//final Reader input,
								   	SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								   	true //boolean is_described
										))
					);
	};
};