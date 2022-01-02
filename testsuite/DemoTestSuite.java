package sztejkat.abstractfmt.testsuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.AfterClass;
import org.junit.runners.Suite.SuiteClasses;
/**
	A demo test-suite showing how to construct test
	suite made of set of test-cases.
*/
@RunWith(Suite.class)	//Tell that it is a composite test
@SuiteClasses({DemoTestCase.class})	//list, using {x.class,y.class,....} test components.
public class DemoTestSuite<TDUT extends StringBuilder> extends ATestCase<TDUT>
{	
	//With this method we do release any factory assigned to
	//a field, so if something conflicts set-up of next test
	//suite implementation may detect it.
	@AfterClass public static void releaseTest()
	{
		System.out.println("Releasing test factory");
		DemoTestSuite.FACTORY = null;
	};
};