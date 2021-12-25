package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;
import java.io.IOException;
/**
	An adapter which changes all flushes to {@link TIndicator#FLUSH_ANY}
*/
public class CFlushAnonymizingIndicatorReadFormat extends CIndicatorReadFormatAdapter
{
	public CFlushAnonymizingIndicatorReadFormat(IIndicatorReadFormat in)
	{
		super(in);
	};
	public TIndicator getIndicator()throws IOException
	{ 
		TIndicator i = in.getIndicator();
		if ((i.FLAGS & TIndicator.FLUSH)!=0) i =  TIndicator.FLUSH_ANY;
		return i; 
	};
	public TIndicator readIndicator()throws IOException
	{ 
		TIndicator i = in.readIndicator();
		if ((i.FLAGS & TIndicator.FLUSH)!=0) i =  TIndicator.FLUSH_ANY;
		return i; 
	};
};