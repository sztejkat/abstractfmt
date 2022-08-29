package sztejkat.abstractfmt;
import java.io.IOException;
/**
	An exception thrown when stream is broken beyond repair
	and any subsequent attempt to use it <i>should</i> throw.
	Will be also throw by any method once this exception is thrown
	for a first time.
*/
public class EBrokenFormat extends IOException
{
		private static final long serialVersionUID=1L;
	public EBrokenFormat(){};
	public EBrokenFormat(String msg, Throwable cause){ super(msg,cause); };
	public EBrokenFormat(Throwable cause){ super(cause); };
	public EBrokenFormat(String msg){ super(msg); };
};