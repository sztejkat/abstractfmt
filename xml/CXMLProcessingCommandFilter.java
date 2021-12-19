package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.util.CBlockFilter;
import java.io.IOException;
import java.io.Reader;

/**
	A filter removing XML processing commands.
	<p>
	Not thread safe.
*/
class CXMLProcessingCommandFilter extends CBlockFilter
{				
	CXMLProcessingCommandFilter(Reader in)
	{
		super(in, "<?","?>");
	};
};