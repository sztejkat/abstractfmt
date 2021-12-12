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
				/** Read end. */
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
		@param max_name_length a maximum name of signal length above which reading and 
				writing ends should complain.
		@param max_events_recursion_depth a maximum name depth of events opened at time
				 length above which reading and	writing ends should complain or -1 to
				 disable control.
		@return created, coupled pair.
		*/
		protected abstract Pair create(int max_name_length, int max_events_recursion_depth);
};