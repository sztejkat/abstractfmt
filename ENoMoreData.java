package sztejkat.abstractfmt;
/**
	An exception thrown when a primitive data read 
	is attempted with cursor at signal boundary.
	<p>
	Stream can always recover from this exception by
	reading the signal.
	<p>
	This is a usual way to indicate that 
	<i>"you correctly read it and there is nothing more in structure".</i>
*/
public class ENoMoreData extends java.io.IOException
{
		private static final long serialVersionUID=1L;
	public ENoMoreData(){};
	public ENoMoreData(String msg, Throwable cause){ super(msg,cause); };
	public ENoMoreData(Throwable cause){ super(cause); };
	public ENoMoreData(String msg){ super(msg); };
};