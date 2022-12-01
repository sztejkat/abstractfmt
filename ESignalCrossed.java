package sztejkat.abstractfmt;
import java.io.IOException;
/**
	An exception thrown when stream element cannot
	be fully read because the signal appears in an incorrect
	place, but this error condition does not break the stream.
	<p>
	This is assumed that when this exception is thrown all
	data read up to the momentare discarded and cursor is left 
	at the signal. 
*/
public class ESignalCrossed extends EBrokenFormat
{
		private static final long serialVersionUID=1L;
	public ESignalCrossed(){};
	public ESignalCrossed(String msg, Throwable cause){ super(msg,cause); };
	public ESignalCrossed(Throwable cause){ super(cause); };
	public ESignalCrossed(String msg){ super(msg); };
};