package sztejkat.abstractfmt;

/**
	Throw when operation is initiated , but stream was not open yet.
*/
public class ENotOpen extends java.io.IOException
{
		private static final long serialVersionUID=1L;
	public ENotOpen(){};
	public ENotOpen(String msg, Throwable cause){ super(msg,cause); };
	public ENotOpen(Throwable cause){ super(cause); };
	public ENotOpen(String msg){ super(msg); };
};