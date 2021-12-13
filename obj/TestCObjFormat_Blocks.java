package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;

public class TestCObjFormat_Blocks extends ATestIIndicatorFormat_Blocks
{
	@Override protected Pair create()
	{
		CObjListFormat media = new CObjListFormat();
		return new Pair(
					new CObjIndicatorWriteFormat(
										media,
										4,//final int max_registrations,
										false,//final boolean is_described,
										false //final boolean is_flushing
										),
					new CObjIndicatorReadFormat(
										media,//CObjListFormat media, 
										4,//final int max_registrations,
										false,//final boolean is_described,
										false //final boolean is_flushing
										)
					);
	};
};