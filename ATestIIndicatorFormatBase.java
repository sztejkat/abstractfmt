package sztejkat.abstractfmt;
import java.io.IOException;
import org.junit.Assert;
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
	@return created, coupled pair. 
	*/
	protected abstract Pair create();
		
		
	/** Validates if <code>is</code> is same as <code>expected</code>
		and prints state information.
		@param is what is read from format
		@param expected what is expected from format.
		@throws AssertionError if not the same.
	*/
	protected static void expect(TIndicator is, TIndicator expected)
	{
		System.out.println("Expected indicator "+expected+" found "+is);
		Assert.assertTrue(is==expected);
	};
};