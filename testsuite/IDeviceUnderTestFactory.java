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
	@return new independent instance 
	@throws IOException if failed.
	*/
	public T create()throws IOException;
};