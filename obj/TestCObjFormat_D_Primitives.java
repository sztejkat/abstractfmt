package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;

public class TestCObjFormat_D_Primitives extends ATestIIndicatorFormat_Primitives
{
	@Override protected Pair create()
	{
		CObjListFormat media = new CObjListFormat();
		return new Pair(
					new CObjIndicatorWriteFormat(
										media,
										4,//final int max_registrations,
										true,//final boolean is_described,
										false //final boolean is_flushing
										),
					new CObjIndicatorReadFormat(
										media,//CObjListFormat media, 
										4,//final int max_registrations,
										true,//final boolean is_described,
										false //final boolean is_flushing
										)
					);
	};
};