package sztejkat.abstractfmt;
/**
	An exception thrown when a primitive data encountered in stream
	do not match requested data.
*/
public class EDataMissmatch extends ECorruptedFormat
{
		private static final long serialVersionUID=1L;
	public EDataMissmatch(){};
	public EDataMissmatch(String msg, Throwable cause){ super(msg,cause); };
	public EDataMissmatch(Throwable cause){ super(cause); };
	public EDataMissmatch(String msg){ super(msg); };
};