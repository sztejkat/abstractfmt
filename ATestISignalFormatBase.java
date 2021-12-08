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
		@param max_name_length greater or equal to 8. Maximum length of names
			to be accepted in {@link #begin(String, boolean)} and be passed to
			{@link #writeSignalNameData}
		@param max_events_recursion_depth specifies the allowed depth of elements
			nesting. Zero disables limit, 1 sets limit to: "no nested elements allowed",
			2 allows element within an element and so on. If this limit is exceed
			the {@link #begin(String,boolean)} will throw <code>IllegalStateException</code>.
		@return created, coupled pair.
		*/
		protected abstract Pair create(int max_name_length, int max_recursion_depth);
};