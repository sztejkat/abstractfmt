package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;

public class TestCObjFormat_Indicators extends ATestIIndicatorFormat_Indicators
{
	@Override protected Pair create(int max_name_length, int max_registrations)
	{
		CObjListFormat media = new CObjListFormat();
		return new Pair(
					new CObjIndicatorWriteFormat(media),
					new CObjIndicatorReadFormat(media, max_name_length)
					);
	};
};