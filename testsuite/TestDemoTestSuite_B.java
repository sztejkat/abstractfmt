package sztejkat.abstractfmt.testsuite;
import java.io.IOException;
import org.junit.BeforeClass;
/**
	See {@link TestDemoTestSuite_A}
*/
public class TestDemoTestSuite_B extends DemoTestSuite<StringBuilder>
{
	//See notes in TestDemoTestSuite_A		
	@BeforeClass public static void setupTestSuiteFactory()
	{
		System.out.println("TestDemoTestSuite_B.setupTestSuiteFactory()");
		assert(DemoTestSuite.FACTORY==null):"Conflicting tests initialization";
		DemoTestSuite.FACTORY = new IDeviceUnderTestFactory<StringBuilder>()
		{
			public StringBuilder create()throws IOException	
			{
				System.out.println("TestDemoTestSuite_B factory creates DUT");
				return new StringBuilder();
			};
		};
	};
};