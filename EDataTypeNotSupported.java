package sztejkat.abstractfmt;
/**
	An exception thrown when stream which is not described encounters
	type information.
*/
public class EDataTypeNotSupported extends ECorruptedFormat
{
		private static final long serialVersionUID=1L;
	public EDataTypeNotSupported(){};
	public EDataTypeNotSupported(String msg, Throwable cause){ super(msg,cause); };
	public EDataTypeNotSupported(Throwable cause){ super(cause); };
	public EDataTypeNotSupported(String msg){ super(msg); };
};