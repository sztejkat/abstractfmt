package sztejkat.abstractfmt;
import  sztejkat.abstractfmt.obj.*;
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
		A test suite checking if 
		{@link CTypedStructReadFormat}/
		{@link CTypedStructWriteFormat}.
		do not corrupt the base contract of a back-end engine.
		<p>
		This test is run using a {@link CObjStructWriteFormat1} back-end
		with name indexing enabled and basically runs all test cases
		for un-typed streams. 
		<p>
		There is a separate test suite for typed tests.
*/
@RunWith(Suite.class)
@Suite.SuiteClasses({
					ATestCase_BasicSignalOperations.class,
					ATestCase_SignalOperationsSafety.class,
					ATestCase_BooleanElementaryPrimitive.class,
					ATestCase_BooleanBlockPrimitive.class,
					ATestCase_ByteBlockPrimitive.class,
					ATestCase_ByteElementaryPrimitive.class,
					ATestCase_OptimizedSignalOperations.class
					})
public class TestSuite_CTypedFormat_overObjFormat1 extends ATest
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
					CObjStructWriteFormat1 back_end_writer = new CObjStructWriteFormat1(
								  true,//boolean end_begin_enabled,
								  -1,//int max_supported_recursion_depth,
								  1024*1024, //int max_supported_name_length,
								  8,// int name_registry_capacity,
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
					CObjStructReadFormat1 back_end_reader = new CObjStructReadFormat1(
								  content,//IPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024*1024, //int max_supported_name_length
								  false,//use_index_instead_of_order
								  8   //name_registry_capacity
								  );
					
					CTypedStructReadFormat typed_reader = new CTypedStructReadFormat(
									back_end_reader,//IStructReadFormat engine, 
									'/',//char escape,
									CTypedStructWriteFormat.DEFAULT_TYPE_NAMES // String [] type_names
									);
					CTypedStructWriteFormat typed_writer=new CTypedStructWriteFormat(
							back_end_writer,//IStructWriteFormat engine, 
							'/',//char escape,
							CTypedStructWriteFormat.DEFAULT_TYPE_NAMES  //String [] type_names
							);
					return new CPair<R,W>( (R)typed_reader, (W)typed_writer, file); 
			};
		};
	};
	@AfterClass public static void disarmImplementation()
	{
		AInterOpTestCase.factory = null;
	};

};
