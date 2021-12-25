package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;

public class TestCObjFormat_Blocks_Dfa extends ATestIIndicatorFormat_Blocks
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
			new CFlushAnonymizingIndicatorReadFormat(
					new CObjIndicatorReadFormat(
										media,//CObjListFormat media, 
										4,//final int max_registrations,
										1024,  //final int max_supported_signal_name_length
										true,//final boolean is_described,
										true //final boolean is_flushing
										)))
					);
	};
};