package sztejkat.abstractfmt.testsuite;
import java.io.IOException;
import org.junit.Test;
/**
	A demo test-case showing how to construct test
	cases.
*/
public class DemoTestCase extends ATestCase<StringBuilder>
{
	@Test public void testMethod_1()throws IOException
	{
		/*
				Human readable test description must 
				be there. What do we test? What do we
				expect?
		*/
		enter();	//show log message of test
		
		//Create "device under test" to run tests on.
		//Use method instead of FACTORY field directly,
		//because it allows it to be overriden and 
		//provide wrapping or whatever and tune 
		//this specific test case.
		StringBuilder DUT = create();
		
		leave();	//show log message of test
	};
};