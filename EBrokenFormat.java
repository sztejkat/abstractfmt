package sztejkat.abstractfmt;
import java.io.IOException;
/**
	An exception thrown when stream is broken, usually beyond repair.
*/
public class EBrokenFormat extends IOException
{
		private static final long serialVersionUID=1L;
	public EBrokenFormat(){};
	public EBrokenFormat(String msg, Throwable cause){ super(msg,cause); };
	public EBrokenFormat(Throwable cause){ super(cause); };
	public EBrokenFormat(String msg){ super(msg); };
};