package sztejkat.abstractfmt;
/**
	An exception thrown when stream is broken beyond repair
	and any subsequent attempt to use it <i>should</i> throw.
*/
public class EBrokenFormat extends ECorruptedFormat
{
		private static final long serialVersionUID=1L;
	public EBrokenFormat(){};
	public EBrokenFormat(String msg, Throwable cause){ super(msg,cause); };
	public EBrokenFormat(Throwable cause){ super(cause); };
	public EBrokenFormat(String msg){ super(msg); };
};