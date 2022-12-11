package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import java.io.File;
import java.io.IOException;

/** A coupled pair of interfaces implementations to be tested.
<p>
This class is declared with generics to make it easier to indicate
that a specific test case do require a specific kind of contract
extension. 
@see IInteropTestDeviceFactory
*/
public class CPair<R extends IStructReadFormat,
						W extends IStructWriteFormat>
{
		/** A reader. It must be configured in such a way
		that when <code>reader.open()</code> it will attach
		itself to data written by <code>writer</code>.
		It should also delete all data written by <code>writer</code>
		when <code>reader.close()</code> is called */
		public final R reader;
		/** A writer. It must be configured in such a way,
		that any data written to it must be available to
		reader when <code>writer.flush()</code> is invoked. */
		public final W writer;
		/** Optional file where writer stored data. This may
		be null, may be a folder or may be a file. All depends
		on what kind of writer do we test. */
		public final File file;
		
		/** Creates
		@param reader non null reader, see {@link #reader}
		@param writer non null writer, see {@link #writer}
		@param file optional test case file, see {@link #file}
		*/
		public CPair(R reader, W writer, File file)
		{
			assert(reader!=null);
			assert(writer!=null);
			this.reader = reader;
			this.writer = writer;
			this.file = file;
		};
		
		public String toString()
		{
			return "CPair{\n\tR = "+reader+",\n\tW="+writer+"\n\tfile="+(file==null ? "" : ("\""+file+"\""))+"}";
		};
};