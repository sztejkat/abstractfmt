package sztejkat.abstractfmt.txt.plain;
import  sztejkat.abstractfmt.*;
import  sztejkat.abstractfmt.test.*;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import java.io.*;
/**
		A test suite checking if {@link CPlainTxtWriteFormat}/{@link CPlainTxtReadFormat}
		do obey base contract.
		<p>
		This test suite dumps test files content to text files on writer close
		using struct level indentation. 
*/
@RunWith(Suite.class)
@Suite.SuiteClasses({
					//ATestCase_BasicSignalOperations.class,
					//ATestCase_OptimizedSignalOperations.class,
					//ATestCase_SignalOperationsSafety.class,
					ATestCase_ComplexSignalName.class/*,
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
					ATestCase_StringBlockPrimitive.class*/
					})
public class TestSuite_CPlainTxtFormat extends ATest
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
					
					//Content.
					CPlainTxtWriteFormat writer = new CPlainTxtWriteFormat(
									new OutputStreamWriter(
											new FileOutputStream(file),
											"UTF-8")
								  );
					CPlainTxtReadFormat reader = new CPlainTxtReadFormat(
											new InputStreamReader(
														new FileInputStream(file),
														"UTF-8")
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
