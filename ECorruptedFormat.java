package sztejkat.abstractfmt;
/**
	A generic exception indicating that there is something 
	wrong with a stream at format level.
	<p>
	If this exception stream may recover by re-trying operations
	or skipping event.
*/
public class ECorruptedFormat extends java.io.IOException
{
		private static final long serialVersionUID=1L;
	public ECorruptedFormat(){};
	public ECorruptedFormat(String msg, Throwable cause){ super(msg,cause); };
	public ECorruptedFormat(Throwable cause){ super(cause); };
	public ECorruptedFormat(String msg){ super(msg); };
};