package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CByteExchangeBuffer;

public class TestCBinSignalFormat_Primitives extends ATestISignalFormat_Primitives
{
	@Override protected Pair create()
	{
		final CByteExchangeBuffer media = new CByteExchangeBuffer();
		return new Pair(
					new CBinSignalWriteFormat(
								0,//int max_events_recursion_depth,
								media.getWriter(),//OutputStream output,
								true //boolean test_mode
								),
					new CBinSignalReadFormat(
								0,//int max_events_recursion_depth,
								media.getReader(),//InputStream output,
								true //boolean test_mode
								)
					)
					{
						public void dump()
						{
							System.out.println(media.toString());
						};
					};
	};
};