package sztejkat.abstractfmt.testsuite;
import java.io.IOException;
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
		
			
	/** Uses {@link #FACTORY} to produce device under test
	@return new device under test 
	@throws IOException if factory have thrown.
	*/
	@SuppressWarnings({"unchecked"})
	protected TDUT create()throws IOException
	{
		assert(FACTORY!=null):"Static factory is not initialized";
		return ((IDeviceUnderTestFactory<TDUT>)(FACTORY)).create(); 
	};
};