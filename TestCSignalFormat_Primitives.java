package sztejkat.abstractfmt;
import sztejkat.abstractfmt.obj.*;
import java.io.IOException;

/**
	Test bed for {@link CSignalReadFormat}/{@link CSignalWriteFormat}
	running in non-described mode over {@link CObjFormat}.
*/
public class TestCSignalFormat_Primitives extends ATestISignalFormat_Primitives
{
	protected Pair create(int max_name_length, int max_events_recursion_depth)
	{
		final CObjListFormat media = new CObjListFormat(); 
		
		return new Pair(
						new CSignalWriteFormat(
								 4,//int names_registry_size,
								 max_name_length, //int max_name_length,
								 max_events_recursion_depth, //int max_events_recursion_depth,								 
								 new CObjIndicatorWriteFormat(media),//IIndicatorWriteFormat output,
								 false // boolean is_described
								 )
								 {
								 public void closeOnce()throws IOException
								 {
								 	super.closeOnce();
								 	System.out.println(media);
								 };
								 },//ISignalWriteFormat write,
						new CSignalReadFormat(
								 4,//int names_registry_size,
								 max_events_recursion_depth,//int max_events_recursion_depth,
								 new CObjIndicatorReadFormat( media,max_name_length),//  IIndicatorReadFormat input,
								 false //boolean is_described
								 )			//ISignalReadFormat read
						);
	};
};