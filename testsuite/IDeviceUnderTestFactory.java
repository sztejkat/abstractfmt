package sztejkat.abstractfmt.testsuite;
import java.io.IOException;

/**
	A factory which is set to test case factory
	field and is used to produce test cases.
	<p>
	Instances of this class must be state-less.
*/
public interface IDeviceUnderTestFactory<T extends Object>
{	
	/** Creates test case.
	@param test_class_name name of test case class
	@param test_name name of test for which creates test device.
			Usually ignored, can be used to create some temporary data.
	@return new independent instance 
	@throws IOException if failed.
	*/
	public T create(String test_class_name, String test_name)throws IOException;
};