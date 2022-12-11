package sztejkat.abstractfmt.obj;
import  sztejkat.abstractfmt.IStructReadFormat;
import  sztejkat.abstractfmt.IStructWriteFormat;
import  sztejkat.abstractfmt.test.*;
import  sztejkat.abstractfmt.utils.CAddablePollableArrayList;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
/**
		A test suite checking if {@link CObjStructWriteFormat0}/{@link CObjStructWriteFormat1}
		do obey base contract.
		<p>
		This test is running the implementation with
		<pre>
		end_begin_enabled = true;
		max_supported_recursion_depth = -1;
		max_supported_name_length = 1024*1024;
		</pre>
		<p>
		This test suite dumps test files content to text files on writer close
		using struct level indentation. 
*/
@RunWith(Suite.class)
@Suite.SuiteClasses({
					ATestCase_BasicSignalOperations.class,
					ATestCase_SignalOperationsSafety.class,
					ATestCase_BooleanElementaryPrimitive.class
					})
public class TestSuite_ObjFormat0 extends ATest
{
	
	@BeforeClass public static void armImplementation()
	{
		//Here You decide what implementations will be tested
		AInterOpTestCase.factory = new IInteropTestDeviceFactory()
		{
			@SuppressWarnings("unchecked")
			public <R extends IStructReadFormat,
			   	    W extends IStructWriteFormat>
			        CPair<R,W> createTestDevice(File temp_folder)throws IOException
			{
					//We will provide content dump to file.
					final File file = new File(temp_folder+"/content.txt");
					final PrintStream ps = new PrintStream(file,"UTF-8");
					//Data storage, in memory
					CAddablePollableArrayList<IObjStructFormat0> content = new CAddablePollableArrayList<IObjStructFormat0>()
					{
								private static final long serialVersionUID=1L;
								//Indentation management
								private int level;
						//Of IAddable
						@Override public boolean add(IObjStructFormat0 e)
						{
							if (super.add(e))
							{
								//indent
								if (e instanceof SIG_BEGIN) level++;
								if (e instanceof SIG_END) level--;
								for(int i=level;--i>=0;)ps.print(' ');
								//content.
								ps.println(e);
								return true;
							}else
								return false;
						};
					};
					//Content.
					CObjStructWriteFormat0 writer = new CObjStructWriteFormat0(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024*1024, //int max_supported_name_length,
								  content // IAddable<IObjStructFormat0> stream
								  )
					{
						// Overriden to close dump.
						@Override protected void closeImpl()throws IOException
						{
							super.closeImpl();
							ps.close();
						};
					};
					CObjStructReadFormat0 reader = new CObjStructReadFormat0(
								  content,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024*1024 //int max_supported_name_length
								  );
					return new CPair<R,W>( (R)reader, (W)writer, file); 
			};
		};
	};
	@AfterClass public static void disarmImplementation()
	{
		AInterOpTestCase.factory = null;
	};

};
