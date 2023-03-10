package sztejkat.abstractfmt;

/**
	A generic exception indicating that there is something 
	wrong with a stream because we did encounter a physical
	end-of-file when not allowed. Usually may not recover from it
	by re-trying.
*/
public class EUnexpectedEof extends EEof
{
		private static final long serialVersionUID=1L;
	public EUnexpectedEof(){};
	public EUnexpectedEof(String msg, Throwable cause){ super(msg,cause); };
	public EUnexpectedEof(Throwable cause){ super(cause); };
	public EUnexpectedEof(String msg){ super(msg); };
};