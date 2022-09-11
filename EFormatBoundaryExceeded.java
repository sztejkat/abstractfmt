package sztejkat.abstractfmt;
/**
	A generic exception indicating that certain format 
	imposed limits are exceeded, for an example a name of a signal is too long or recursion
	is too deep and etc. 	
*/
public class EFormatBoundaryExceeded extends EBrokenFormat
{
		private static final long serialVersionUID=1L;
	public EFormatBoundaryExceeded(){};
	public EFormatBoundaryExceeded(String msg, Throwable cause){ super(msg,cause); };
	public EFormatBoundaryExceeded(Throwable cause){ super(cause); };
	public EFormatBoundaryExceeded(String msg){ super(msg); };
};