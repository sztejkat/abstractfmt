package sztejkat.abstractfmt;
import java.io.IOException;
/**
	A base class for all exceptions which are indicating
	faults from which stream <u>can</u> recover.
*/
public abstract class ERecoverableIOException extends IOException
{
		private static final long serialVersionUID=1L;
	public ERecoverableIOException(){};
	public ERecoverableIOException(String msg, Throwable cause){ super(msg,cause); };
	public ERecoverableIOException(Throwable cause){ super(cause); };
	public ERecoverableIOException(String msg){ super(msg); };
};