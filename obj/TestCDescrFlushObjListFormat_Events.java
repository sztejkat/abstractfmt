package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;
/**
	An interoperational tests
	for {@link ASignalReadFormat}/{@link ASignalWriteFormat}
	through {@link CDescrFlushObjListWriteFormat}/{@link CDescrObjListReadFormat}
*/
public class TestCDescrFlushObjListFormat_Events extends ATestISignalFormat_Events
{
		@Override protected Pair create(int max_name_length, int max_recursion_depth)
		{
			CObjListFormat media = new CObjListFormat();
			
			return new Pair(
						new CDescrFlushObjListWriteFormat(
									 8,//int names_registry_size,
									 max_name_length,//int max_name_length,
									 max_recursion_depth,//int max_events_recursion_depth,
									 media //CObjListFormat media
									 ),
						new CDescrObjListReadFormat(
									 8,//int names_registry_size,
									 max_name_length,//int max_name_length,
									 max_recursion_depth,//int max_events_recursion_depth
									 media //CObjListFormat media
									 ));
		};
};
