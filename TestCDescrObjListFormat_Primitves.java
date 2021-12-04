package sztejkat.abstractfmt;

/**
	An interoperational tests
	for {@link ASignalReadFormat}/{@link ASignalWriteFormat}
	through {@link CFullyDescrObjListWriteFormat}/{@link CDescrObjListReadFormat}
*/
public class TestCDescrObjListFormat_Primitves extends ATestISignalFormat_Primitives
{
		@Override protected Pair create()
		{
			CObjListFormat media = new CObjListFormat();
			
			return new Pair(
						new CFullyDescrObjListWriteFormat(
									 8,//int names_registry_size,
									 8,//int max_name_length,
									 8,//int max_events_recursion_depth,
									 media //CObjListFormat media
									 ),
						new CDescrObjListReadFormat(
									 8,//int names_registry_size,
									 8,//int max_name_length,
									 8,//int max_events_recursion_depth
									 media //CObjListFormat media
									 ));
						
		};
};
