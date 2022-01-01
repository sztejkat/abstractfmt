package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CByteExchangeBuffer;

public class TestCBinDescIndicatorFmt_Indicators extends ATestIIndicatorFormat_Indicators
{
	@Override protected Pair create()
	{
		final CByteExchangeBuffer media = new CByteExchangeBuffer();
		return new Pair(
				new CIndicatorWriteFormatProtector(
					new CBinDescIndicatorWriteFormat(
										media.getWriter()//OutputStream output
										)
										{
											public void close()throws java.io.IOException
											{
												super.close();
												System.out.println(media.toString());
											};
										}),
				new CIndicatorReadFormatProtector(			
					new CBinDescIndicatorReadFormat(
										media.getReader()//InputStream input
										))
					);
	};
};