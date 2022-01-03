package sztejkat.abstractfmt.util;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.Reader;
/**
	A {@link CCharExchangeBuffer} which is using file
	to exchange data.
	<p>
	This is usefull for tests in environments in which
	we would like to capture data which are too large
	to be fit in Junit test memory or when You like
	to preserve data.
	<p>
	Opposite to {@link CCharExchangeBuffer} the file
	back-end preserves the full track of all operations
	so it can't be used for infinite runs.
*/
public class CCharFileExchangeBuffer
{
			/** A static numerator used to
			generate unique name. Incremeted after each file creation.
			 */
			private static int numerator;
			/** A file which is used */
			private final File file;
			/** Writing end */
			private final Writer writer;
			/** Reading end */
			private final Reader reader;
			
	/** Creates exchange buffer 
	@param folder optional folder to be used where create files.
			If not null and not existing will be created.
			
	@param file_name_pattern file name with # inside to be replaced 
			with {@link #numerator}. File will be created, written
			and read with "UTF-8" encoding.
			Files will be overwritten.
	@throws IOException if failed.
	*/
	public CCharFileExchangeBuffer(
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
		this.writer = new OutputStreamWriter(new FileOutputStream(f),"UTF-8");
		this.reader = new InputStreamReader(new FileInputStream(f),"UTF-8");
	};
	
	public final File getFile(){ return file; };
	
	/** Returns reader of that buffer 
	@return life time constant.
	*/
	public final Reader getReader()
	{
		return reader;
	};
	/** Returns writer of that buffer 
	@return life time constant.
	*/
	public final Writer getWriter()
	{
		return writer;
	};
		
		
	public final void close()throws IOException
	{
		writer.close();
		reader.close();
	};
	
	
		
	/* ********************************************************************************
	
	
	
				Junit org test arena
	
	
		Note: tests might need update if GROWTH_INCREMENT/MOVE_TRIGGER/SHRINK_TRIGGER
		are changed.
	
	* *********************************************************************************/
	public static final class Test extends sztejkat.utils.test.ATest
	{
		private static char of(int i){ return (char)((i*31) +i ); }
		private void testReadAndWrite(final int chunk_size, CCharFileExchangeBuffer b)throws IOException
		{
			for(int i=0;i<chunk_size; i++)
			{
				b.getWriter().write(of(i));
			};
			b.getWriter().flush();
			for(int i=0;i<chunk_size; i++)
			{
				org.junit.Assert.assertTrue(b.getReader().ready());
				org.junit.Assert.assertTrue(b.getReader().read()==of(i));
			};
		};
		@org.junit.Test public void testReadAndWrite1()throws IOException
		{	
			enter();
			/*
					We do small, sub growth/move/shring read-write test.
			*/
			CCharFileExchangeBuffer b = new CCharFileExchangeBuffer(new File("test-data"),"test#.txt");
			for(int i=0;i<100; i++)
			{
				testReadAndWrite(16,b);
			};
			leave();
		};
		
		@org.junit.Test public void testReadAndWrite2()throws IOException
		{	
			enter();
			/*
					We do small, sub growth/move/shring read-write test.
			*/
			CCharFileExchangeBuffer b = new CCharFileExchangeBuffer(new File("test-data"),"test#.txt");
			for(int i=0;i<100; i++)
			{
				testReadAndWrite(1024,b);
			};
			leave();
		};
	};
};