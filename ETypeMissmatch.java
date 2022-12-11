package sztejkat.abstractfmt;
/**
	An exception thrown when a primitive data read 
	is using a method not matching a data type stored
	in a stream.
	<p>
	When method throws this exception the cursor should
	be left un-moved. Especially a code should be able to
	invoke all primitive reads in sequence and use first
	one which did not throw as a correct one.
	<p>
	Stream can always recover from this exception by either
	skipping structure content or trying other method.
	<p>
	This exception is specific for <i>fully described streams</i>.
*/
public class ETypeMissmatch extends java.io.IOException
{
		private static final long serialVersionUID=1L;
	public ETypeMissmatch(){};
	public ETypeMissmatch(String msg, Throwable cause){ super(msg,cause); };
	public ETypeMissmatch(Throwable cause){ super(cause); };
	public ETypeMissmatch(String msg){ super(msg); };
};