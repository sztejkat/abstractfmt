package sztejkat.abstractfmt.testsuite;
import java.io.IOException;
import org.junit.rules.TestWatcher;
import org.junit.Rule;
import org.junit.runner.Description;
/**
	A base of test cases.
	<p>
	Each test case is supposed to run some sub-set of
	tests on <code>TDUT</code> contract using
	instances created with {@link #create} methods.
	<p>
	For how to implement inspect source of 
	{@link DemoTestCase}.
	<p>
	For how to collate many test cases in 
	test suite inspect source code of {@link DemoTestSuite}.
	<p>
	For how to actually run test suite on some implementation
	of <code>TDUT</code> inspect source code of {@link TestDemoTestSuite_A}. 
*/
public abstract class ATestCase<TDUT extends Object>
				//Note: remove this test dependency later.
				extends sztejkat.utils.test.ATest
{
			/** A field which must be initialized with a 
			specific test factory */
			public static IDeviceUnderTestFactory<?> FACTORY;
			/** Dynamic test name holder */
			public static final class TestCapture extends TestWatcher
			{
					String class_name;
					String method_name;
				protected void starting(Description d)
				{
					class_name = d.getClassName();
					method_name = d.getMethodName();
				};
			};   
	  		@Rule public TestCapture name= new TestCapture();
			
	/** Uses {@link #FACTORY} to produce device under test
	@return new device under test 
	@throws IOException if factory have thrown.
	*/
	@SuppressWarnings({"unchecked"})
	protected TDUT create()throws IOException
	{
		assert(FACTORY!=null):"Static factory is not initialized";
		return ((IDeviceUnderTestFactory<TDUT>)(FACTORY)).create(name.class_name, name.method_name); 
	};
};