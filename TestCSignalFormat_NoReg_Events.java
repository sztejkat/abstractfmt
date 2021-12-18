package sztejkat.abstractfmt;
import sztejkat.abstractfmt.obj.*;
import java.io.IOException;

/**
	Test bed for {@link CSignalReadFormat}/{@link CSignalWriteFormat}
	running in non-described mode over {@link CObjListFormat}
	with disabled names registry.
*/
public class TestCSignalFormat_NoReg_Events extends ATestISignalFormat_Events
{
	protected Pair create()
	{
		final CObjListFormat media = new CObjListFormat(); 
		
		return new Pair(
						new CSignalWriteFormat(
								 10000, //int max_events_recursion_depth,								 
								 new CObjIndicatorWriteFormat(
										media,
										0,//final int max_registrations,
										false,//final boolean is_described,
										false //final boolean is_flushing
										)//IIndicatorWriteFormat output,
								 )
								 {
								 public void closeOnce()throws IOException
								 {
								 	super.closeOnce();
								 	System.out.println(media);
								 };
								 },//ISignalWriteFormat write,
						new CSignalReadFormat(
								 10000,//int max_events_recursion_depth,
								 new CObjIndicatorReadFormat(
										media,//CObjListFormat media, 
										0,//final int max_registrations,
										false,//final boolean is_described,
										false //final boolean is_flushing
										)//  IIndicatorReadFormat input,
								 )			//ISignalReadFormat read
						);
	};
};