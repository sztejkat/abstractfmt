package sztejkat.abstractfmt;
import java.io.IOException;
/**
	A test bed for paired {@link ISignalReadFormat}/{@link ISignalWriteFormat}
	using only a contract to read data through reading end writtten with write end.
*/
public abstract class ATestISignalFormatBase extends sztejkat.utils.test.ATest
{
		/** A class representing a coupled pair of format 
		streams. Whatever is written with {@link #write}
		must be available to {@link #read} no
		later than after {@link ISignalWriteFormat#flush}.
		*/
		public static final class Pair
		{
				/** Write end */
				public final ISignalWriteFormat write;
				/** 
				Read end. The <code>read.isDescribed</code>
				will be used in <code>junit.org.Assume</code>
				to test if test should be run if it is specifically
				testing functionality of described or un-described 
				formats.
				*/
				public final ISignalReadFormat read;
				
				public Pair(
						ISignalWriteFormat write,
						ISignalReadFormat read
						)
				{
					assert(write!=null);
					assert(read!=null);
						this.write = write;
						this.read = read;
				};
				
				public void close()throws IOException
				{
					write.close();	//ignoring the fact, that write may throw.
					read.close();
				};				
		};
		/** Creates a pair which is to be used for testing purposes.	
		@param max_name_length as declared in {@link ASignalWriteFormat#ASignalWriteFormat(int,int,int)}
		@param max_events_recursion_depth --//--
		@return created, coupled pair.
		*/
		protected abstract Pair create(int max_name_length, int max_events_recursion_depth);
};