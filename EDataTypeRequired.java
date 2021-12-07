package sztejkat.abstractfmt;
/**
	An exception thrown when stream which is described does not find
	required type information.
*/
public class EDataTypeRequired extends ECorruptedFormat
{
		private static final long serialVersionUID=1L;
	public EDataTypeRequired(){};
	public EDataTypeRequired(String msg, Throwable cause){ super(msg,cause); };
	public EDataTypeRequired(Throwable cause){ super(cause); };
	public EDataTypeRequired(String msg){ super(msg); };
};