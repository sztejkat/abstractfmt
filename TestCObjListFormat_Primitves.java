package sztejkat.abstractfmt;

/**
	An interoperational tests
	for {@link ASignalReadFormat}/{@link ASignalWriteFormat}
	through {@link COptObjListWriteFormat}/{@link CObjListReadFormat}
*/
public class TestCObjListFormat_Primitves extends ATestISignalFormat_Primitives
{
		@Override protected Pair create()
		{
			CObjListFormat media = new CObjListFormat();
			
			return new Pair(
						new COptObjListWriteFormat(
									 8,//int names_registry_size,
									 8,//int max_name_length,
									 8,//int max_events_recursion_depth,
									 media //CObjListFormat media
									 ),
						new CObjListReadFormat(
									 8,//int names_registry_size,
									 8,//int max_name_length,
									 8,//int max_events_recursion_depth,
									 false,//boolean strict_described_types,
									 media //CObjListFormat media
									 ));
						
		};
};
