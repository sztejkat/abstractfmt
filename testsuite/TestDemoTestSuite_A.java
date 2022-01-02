package sztejkat.abstractfmt.testsuite;
import java.io.IOException;
import org.junit.BeforeClass;
/**
	A demo test-suite showing how to use
	a test suite {@link DemoTestSuite}
	with a specific implementation of 
	"device under test".
*/
public class TestDemoTestSuite_A extends DemoTestSuite<StringBuilder>
{
	//In theory the static class initializer:
	// static{ ...}
	// is run when any class data or code is used
	//for a first time. The tricky part is, that JUnit default runner
	//which is encountering this class sees @RunWith(Suite) and does
	//NOT in fact initialize this class unless there is some code
	//which it is bound to run. Thous instead of static initializer
	//we are bound to use @org.junit.BeforeClass annotated method. 
		
	@BeforeClass public static void setupTestSuiteFactory()
	{
		System.out.println("TestDemoTestSuite_A.setupTestSuiteFactory()");
		//Validate if some composite test already set it up?
		assert(DemoTestSuite.FACTORY==null):"Conflicting tests initialization";
		DemoTestSuite.FACTORY = new IDeviceUnderTestFactory<StringBuilder>()
		{
			public StringBuilder create()throws IOException	
			{
				System.out.println("TestDemoTestSuite_A factory creates DUT");
				return new StringBuilder();
			};
		};
	};
};