package sztejkat.abstractfmt.bin.chunk;
import  sztejkat.abstractfmt.*;
import  sztejkat.abstractfmt.test.*;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import java.io.File;
import java.io.IOException;
/**
	An inter-operational test for chunk formats, using 
	indexed registration mode
*/

@RunWith(Suite.class)
@Suite.SuiteClasses({
					ATestCase_BasicSignalOperations.class,
					ATestCase_SignalOperationsSafety.class,					
					ATestCase_OptimizedSignalOperations.class,
					ATestCase_ComplexSignalName.class,
					ATestCase_BooleanElementaryPrimitive.class,
					ATestCase_BooleanBlockPrimitive.class,
					ATestCase_ByteElementaryPrimitive.class,
					ATestCase_ByteBlockPrimitive.class,
					ATestCase_ShortElementaryPrimitive.class,
					ATestCase_ShortBlockPrimitive.class,
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
public class TestSuite_ChunkFormat extends ATest
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
					final File file = new File(temp_folder+"/content.bin");
					//Now low level I/O
					FileOutputStream o = new FileOutputStream(file);
					FileInputStream i  = new FileInputStream(file);
					CChunkWriteFormat writer = new CChunkWriteFormat(
					   128,//int name_registry_capacity,
					   o,//OutputStream raw,
					   true //boolean indexed_registration
					   );
					CChunkReadFormat reader = new CChunkReadFormat(
								128,//int name_registry_capacity,
								i  //InputStream raw)
								);
					return new CPair<R,W>( 
									(R)reader, 
									(W)writer, 
									file
									); 
			};
		};
	};
	@AfterClass public static void disarmImplementation()
	{
		AInterOpTestCase.factory = null;
	};
};