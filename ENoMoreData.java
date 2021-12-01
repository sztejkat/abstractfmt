package sztejkat.abstractfmt;
/**
	An exception thrown when a primitive data read attempts to
	cross event boundaries.
	<p>
	Stream can recover from this exception by skipping the event
	content.
*/
public class ENoMoreData extends ECorruptedFormat
{
		private static final long serialVersionUID=1L;
	public ENoMoreData(){};
	public ENoMoreData(String msg, Throwable cause){ super(msg,cause); };
	public ENoMoreData(Throwable cause){ super(cause); };
	public ENoMoreData(String msg){ super(msg); };
};