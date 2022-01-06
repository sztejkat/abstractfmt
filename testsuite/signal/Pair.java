package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.ISignalWriteFormat;
import sztejkat.abstractfmt.ISignalReadFormat;
import java.io.IOException;
/**
	A "device under test" representation.
	<p>
	A class representing a coupled pair of formats 
	Whatever is written with {@link #write}	must be available to
	{@link #read} no later than after 
	{@link ISignalWriteFormat#flush}.
*/
public final class Pair
{
		/** Write end */
		public final ISignalWriteFormat write;
		/** Read end.	*/
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
		