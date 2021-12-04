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
				/** Read end */
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
				/** Controls tests run.
				@return true if test type safety. By default checks
					if read implements {@link IDescribedSignalReadFormat}.
				*/
				public boolean isDescribed()
				{
					return read instanceof IDescribedSignalReadFormat;
				};
		};
		/** Creates a pair which is to be used for testing purposes.
		This pair must have:
		<ul>
			<li>some name length limit set above 8 characters;</li>
			<li>recursion depth protection set at 8 levels;</li>
			<li>if {@link Pair#isDescribed()}==false tests agaist type safety are not run;</li>
		</ul>
		*/
		protected abstract Pair create();
};