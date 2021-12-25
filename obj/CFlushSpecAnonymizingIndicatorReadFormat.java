package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;
import java.io.IOException;
/**
	An adapter which changes all flushes to {@link TIndicator#FLUSH_BLOCK} or  {@link TIndicator#FLUSH_ELEMENTARY} 
	respectively.
*/
public class CFlushSpecAnonymizingIndicatorReadFormat extends CIndicatorReadFormatAdapter
{
	public CFlushSpecAnonymizingIndicatorReadFormat(IIndicatorReadFormat in)
	{
		super(in);
	};
	public TIndicator getIndicator()throws IOException
	{ 
		TIndicator i = in.getIndicator();
		if ((i.FLAGS & TIndicator.FLUSH)!=0) 
		{
			i =  ((i.FLAGS & TIndicator.BLOCK)!=0) ? TIndicator.FLUSH_BLOCK : TIndicator.FLUSH_ELEMENTARY;
		};
		return i; 
	};
	public TIndicator readIndicator()throws IOException
	{ 
		TIndicator i = in.readIndicator();
		if ((i.FLAGS & TIndicator.FLUSH)!=0) 
		{
			i =  ((i.FLAGS & TIndicator.BLOCK)!=0) ? TIndicator.FLUSH_BLOCK : TIndicator.FLUSH_ELEMENTARY;
		};
		return i; 
	};
};