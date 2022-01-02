package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import java.io.IOException;
/**
	A "device under test" representation.
	<p>
	A class representing a coupled pair of formats 
	Whatever is written with {@link #write}	must be available to
	{@link #read} no later than after 
	{@link IIndicatorWriteFormat#flush}.
*/
public final class Pair
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
		