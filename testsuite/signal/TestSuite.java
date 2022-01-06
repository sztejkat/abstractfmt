package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.testsuite.ATestCase;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.AfterClass;
import org.junit.runners.Suite.SuiteClasses;
/**
	A test suite running all tests.
*/
@RunWith(Suite.class)	//Tell that it is a composite test
@SuiteClasses({
			TestOpenClose.class,
			TestFailsBeforeOpen.class,
			TestFailsAfterClose.class,
			
			TestBooleanPrimitiveOps.class,
			TestBytePrimitiveOps.class,
			TestCharPrimitiveOps.class,
			TestShortPrimitiveOps.class,
			TestIntPrimitiveOps.class,
			TestLongPrimitiveOps.class,
			TestFloatPrimitiveOps.class,
			TestDoublePrimitiveOps.class,
			
			TestBooleanBlockOps.class
				})	//list, using {x.class,y.class,....} test components.
public class TestSuite extends ATestCase<Pair>
{	
	//With this method we do release any factory assigned to
	//a field, so if something conflicts set-up of next test
	//suite implementation may detect it.
	@AfterClass public static void releaseTest()
	{
		System.out.println("Releasing test factory");
		TestSuite.FACTORY = null;
	};
};