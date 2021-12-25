package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.util.CBlockFilter;
import java.io.IOException;
import java.io.Reader;

/**
	A filter removing XML processing commands.
	This filter may be disabled on demand
	with {@link #setBypassEnabled}
	<p>
	Not thread safe.
*/
class CXMLProcessingCommandFilter extends CBlockFilter
{				
			private boolean enable_bypass;
	CXMLProcessingCommandFilter(Reader in)
	{
		super(in, "<?","?>");
	};
	/** Allows to disable filtering 
	@param enable_bypass true to disable filtering.
	Default: enabled.
	*/
	void setBypassEnabled(boolean enable_bypass)
	{
		this.enable_bypass = enable_bypass;
	};
	@Override protected void filter()throws IOException
	{
		if (enable_bypass)
		{
			int r = in.read();
			if (r!=-1) write((char)r);
		}else
		{
			super.filter();
		};
	};
};