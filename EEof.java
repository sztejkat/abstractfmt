package sztejkat.abstractfmt;

/**
	A generic exception indicating that end of data was encountered
	at low level.
*/
public class EEof extends java.io.IOException
{
		private static final long serialVersionUID=1L;
	public EEof(){};
	public EEof(String msg, Throwable cause){ super(msg,cause); };
	public EEof(Throwable cause){ super(cause); };
	public EEof(String msg){ super(msg); };
};