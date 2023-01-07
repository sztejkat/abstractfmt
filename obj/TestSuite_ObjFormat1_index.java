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
		A test suite checking if {@link CObjStructWriteFormat1}/{@link CObjStructReadFormat1}
		do obey base contract.
		<p>
		This test is running the implementation with
		<pre>
		end_begin_enabled = true;
		max_supported_recursion_depth = -1;
		max_supported_name_length = 1024*1024;
		name_registry_capacity = 32;
		use_index_instead_of_order = true;
		</pre>
		<p>
		This test suite dumps test files content to text files on writer close
		using struct level indentation. 
*/
@RunWith(Suite.class)
@Suite.SuiteClasses({
					ATestCase_BasicSignalOperations.class,
					ATestCase_SignalOperationsSafety.class,
					ATestCase_OptimizedSignalOperations.class,
					ATestCase_ComplexSignalName.class,
					ATestCase_BooleanElementaryPrimitive.class,
					ATestCase_BooleanBlockPrimitive.class,
					ATestCase_ByteBlockPrimitive.class,
					ATestCase_ByteElementaryPrimitive.class,
					ATestCase_ShortBlockPrimitive.class,
					ATestCase_ShortElementaryPrimitive.class,					
					ATestCase_CharElementaryPrimitive.class,
					ATestCase_CharBlockPrimitive.class,
					ATestCase_IntElementaryPrimitive.class,
					ATestCase_IntBlockPrimitive.class,
					ATestCase_LongElementaryPrimitive.class,
					ATestCase_LongBlockPrimitive.class,
					ATestCase_FloatElementaryPrimitive.class,
					ATestCase_FloatBlockPrimitive.class,
					ATestCase_DoubleElementaryPrimitive.class,
					ATestCase_DoubleBlockPrimitive.class,
					ATestCase_StringBlockPrimitive.class
					})
public class TestSuite_ObjFormat1_index extends ATest
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
								int l = level;
								if (l>10) { ps.print("..."); l=10; };
								for(int i=l;--i>=0;)ps.print(' ');
								//content.
								ps.println(e);
								return true;
							}else
								return false;
						};
					};
					//Content.
					CObjStructWriteFormat1 writer = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024*1024, //int max_supported_name_length,
								  32,// int name_registry_capacity,
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
					CObjStructReadFormat1 reader = new CObjStructReadFormat1(
								  content,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024*1024, //int max_supported_name_length
								  true,//use_index_instead_of_order
								  32   //name_registry_capacity
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
