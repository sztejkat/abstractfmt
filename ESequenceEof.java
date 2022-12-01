package sztejkat.abstractfmt;
import java.io.IOException;
/**
	An exception thrown when stream sequence element cannot
	be fully read because the signal appears in an incorrect
	place, but this error condition does not break the stream.
	<p>
	This is assumed that when this exception is thrown all
	data in sequence operation in question are discarded 
	and cursor is left at the signal. 
*/
public class ESequenceEof extends ESignalCrossed
{
		private static final long serialVersionUID=1L;
	public ESequenceEof(){};
	public ESequenceEof(String msg, Throwable cause){ super(msg,cause); };
	public ESequenceEof(Throwable cause){ super(cause); };
	public ESequenceEof(String msg){ super(msg); };
};