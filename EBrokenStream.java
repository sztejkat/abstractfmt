package sztejkat.abstractfmt;
/**
	An exception thrown when stream is broken beyond repair
	and any subsequent attempt to read it should throw.
*/
public class EBrokenStream extends ECorruptedFormat
{
		private static final long serialVersionUID=1L;
	public EBrokenStream(){};
	public EBrokenStream(String msg, Throwable cause){ super(msg,cause); };
	public EBrokenStream(Throwable cause){ super(cause); };
	public EBrokenStream(String msg){ super(msg); };
};