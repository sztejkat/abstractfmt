package sztejkat.abstractfmt;
import java.io.IOException;
/**
	A test bed for paired {@link IIndicatorReadFormat}/{@link IIndicatorWriteFormat}
	using only a contract to read data through reading end writtten with write end.
*/
public abstract class ATestIIndicatorFormatBase extends sztejkat.utils.test.ATest
{
		/** A class representing a coupled pair of format 
		streams. Whatever is written with {@link #write}
		must be available to {@link #read} no
		later than after {@link IIndicatorWriteFormat#flush}.
		*/
		public static final class Pair
		{
				/** Write end */
				public final IIndicatorWriteFormat write;
				/** Read end.	*/
				public final IIndicatorReadFormat read;
				
				public Pair(
						IIndicatorWriteFormat write,
						IIndicatorReadFormat read
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
		@param max_name_length a maximum boundary of signal names which can be read from
				name related indicators.
		@param max_registrations maximum number of registration calls.
				If the writing end does not support specified number of registartions
				it should use <code>org.junit.Assume.assumeTrue</code> to disable tests
				related to it.
		@return created, coupled pair. This pair must NOT fail if executed with names
				or registrations in above bounds, but is allowed to not fail if executed
				with data exceeding it.
		*/
		protected abstract Pair create(int max_name_length, int max_registrations);
};