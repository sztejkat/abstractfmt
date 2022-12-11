package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.ESignalCrossed;
import sztejkat.abstractfmt.ENoMoreData;
import sztejkat.abstractfmt.ETypeMissmatch;
import sztejkat.abstractfmt.EBrokenFormat;
import java.io.File;
import java.io.IOException;
/**
	A contract for a test case factory which is responsible for 
	producing inter-operational tests for reader-writer pairs.
	<p>
	This factory will be passed to all test-suite based inter-op tests.
*/
public interface IInteropTestDeviceFactory
{
		/**
			Method responsible for creating a test pair.
			@param temp_folder a folder in which temporary test files, if any,
					should be created by returned writer. Non null, must exist.
			@return test pair such as:
				<ul>
					<li>writer and reader are ready to use but not open;</li>
					<li>reader is configured in such a way, that it will be
					ready to read data produced by writer once writer <code>flush()</code>
					method is invoked;</li>
					<li>any temporary data created by writer are allowed to be deleted at 
					<code>reader.close()</code>. They must <u>not</u> be deleted
					without <code>reader.close()</code> since if test-case fails 
					they should be available for an inspection;</li>
				</ul>
			@param <R>  contract. This factory may be used for 
					some specific contract extensions too and this typed
					call will save on some manual checks.
			@param <W>  contract
			@throws IOException if failed.
		*/
		public <R extends IStructReadFormat,
			    W extends IStructWriteFormat>
			    CPair<R,W> createTestDevice(File temp_folder)throws IOException;
		  
};