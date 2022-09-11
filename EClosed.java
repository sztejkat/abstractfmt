package sztejkat.abstractfmt;

/**
	Throw when operation is initiated, but stream was closed.
*/
public class EClosed extends java.io.IOException
{
		private static final long serialVersionUID=1L;
	public EClosed(){};
	public EClosed(String msg, Throwable cause){ super(msg,cause); };
	public EClosed(Throwable cause){ super(cause); };
	public EClosed(String msg){ super(msg); };
};