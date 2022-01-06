package sztejkat.abstractfmt.util;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
/**
	A {@link CByteExchangeBuffer} which is using file
	to exchange data.
	<p>
	This is usefull for tests in environments in which
	we would like to capture data which are too large
	to be fit in Junit test memory or when You like
	to preserve data.
	<p>
	Opposite to {@link CByteExchangeBuffer} the file
	back-end preserves the full track of all operations
	so it can't be used for infinite runs.
*/
public class CFileExchangeBuffer
{
			/** A static numerator used to
			generate unique name. Incremeted after each file creation.
			 */
			private static int numerator;
			/** A file which is used */
			private final File file;
			/** Writing end */
			private final OutputStream output;
			/** Reading end */
			private final InputStream input;
			
	/** Creates exchange buffer 
	@param folder optional folder to be used where create files.
			If not null and not existing will be created.			
	@param file_name_pattern file name with # inside to be replaced 
			with {@link #numerator}.Files will be overwritten.
	@throws IOException if failed.
	*/
	public CFileExchangeBuffer(
						File folder,
						String file_name_pattern
						)throws IOException
	{
		//Note: We assume, that file system can create
		//read-write files which can be written and read at the same moment.
		
		//retrive unique numerator
		final int n;
		synchronized(CCharFileExchangeBuffer.class)
		{
			 n = numerator;
			 numerator++;
		};
		//construct file name
		String fname = file_name_pattern.replace("#",Integer.toString(n));
		if (folder!=null)
		{
			if (!folder.exists())
				folder.mkdirs();
		};
		//construct file
		File f = new File(folder,fname);
		
		this.file = f;
		this.output = new FileOutputStream(f);
		this.input = new FileInputStream(f);
	};
	
	public final File getFile(){ return file; };
	
	/** Returns reader of that buffer 
	@return life time constant.
	*/
	public final InputStream getInput()
	{
		return input;
	};
	/** Returns writer of that buffer 
	@return life time constant.
	*/
	public final OutputStream getOutput()
	{
		return output;
	};
		
		
	public final void close()throws IOException
	{
		output.close();
		input.close();
	};
};