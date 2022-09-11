package sztejkat.abstractfmt;

/**
	Throw when encountered end of file in a place from which
	it can recover by re-trying the operation.
*/
public class ETemporaryEndOfFile extends EEof
{
		private static final long serialVersionUID=1L;
	public ETemporaryEndOfFile(){};
	public ETemporaryEndOfFile(String msg, Throwable cause){ super(msg,cause); };
	public ETemporaryEndOfFile(Throwable cause){ super(cause); };
	public ETemporaryEndOfFile(String msg){ super(msg); };
};