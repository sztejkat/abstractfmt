package sztejkat.abstractfmt;

/**
	A generic exception indicating that end of data was encountered
	at low level.
*/
public class EEof extends java.io.EOFException
{
		private static final long serialVersionUID=1L;
	public EEof(){};
	public EEof(String msg,Throwable cause)
	{ 
		super(msg);
		initCause(cause);
	};
	public EEof(Throwable cause)
	{
		this(null,cause); 
	};
	public EEof(String msg){ super(msg); };
};