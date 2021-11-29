package sztejkat.abstractfmt;

/**
	A generic exception indicating that there is something 
	wrong with a stream because it's MIME type is incorrect.
*/
public class EInvalidMIME extends java.io.IOException
{
		private static final long serialVersionUID=1L;
	public EInvalidMIME(){};
	public EInvalidMIME(String msg, Throwable cause){ super(msg,cause); };
	public EInvalidMIME(Throwable cause){ super(cause); };
	public EInvalidMIME(String msg){ super(msg); };
};