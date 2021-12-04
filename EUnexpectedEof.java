package sztejkat.abstractfmt;

/**
	A generic exception indicating that there is something 
	wrong with a stream because we did encounter a physical
	end-of-file when not allowed.
	<p>
	Stream may recover from this error by re-trying, however
	an operation which was broken due to this exception may
	be lost and unrecoverable.
*/
public class EUnexpectedEof extends ECorruptedFormat
{
		private static final long serialVersionUID=1L;
	public EUnexpectedEof(){};
	public EUnexpectedEof(String msg, Throwable cause){ super(msg,cause); };
	public EUnexpectedEof(Throwable cause){ super(cause); };
	public EUnexpectedEof(String msg){ super(msg); };
};