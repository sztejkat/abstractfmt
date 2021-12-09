package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;
/**
	An interoperational tests
	for {@link ASignalReadFormat}/{@link ASignalWriteFormat}
	through {@link COptObjListWriteFormat}/{@link CObjListReadFormat}
*/
public class TestCObjListFormat_Primitves extends ATestISignalFormat_Primitives
{
		@Override protected Pair create(int max_name_length, int max_recursion_depth)
		{
			CObjListFormat media = new CObjListFormat();
			
			return new Pair(
						new CObjListWriteFormat(
									 8,//int names_registry_size,
									 max_name_length,//int max_name_length,
									 max_recursion_depth,//int max_events_recursion_depth,
									 media //CObjListFormat media
									 ),
						new CObjListReadFormat(
									 8,//int names_registry_size,
									 max_name_length,//int max_name_length,
									 max_recursion_depth,//int max_events_recursion_depth
									 media //CObjListFormat media
									 ));
						
		};
};
