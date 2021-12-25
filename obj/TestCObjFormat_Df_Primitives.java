package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;

public class TestCObjFormat_Df_Primitives extends ATestIIndicatorFormat_Primitives
{
	@Override protected Pair create()
	{
		CObjListFormat media = new CObjListFormat();
		return new Pair(
					new CIndicatorWriteFormatProtector(
						new CObjIndicatorWriteFormat(
										media,
										4,//final int max_registrations,
										1024,  //final int max_supported_signal_name_length
										true,//final boolean is_described,
										true //final boolean is_flushing
										)),
					new CIndicatorReadFormatProtector(
					new CObjIndicatorReadFormat(
										media,//CObjListFormat media, 
										4,//final int max_registrations,
										1024,  //final int max_supported_signal_name_length
										true,//final boolean is_described,
										true //final boolean is_flushing
										))
					);
	};
};