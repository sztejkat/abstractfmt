package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.util.CBlockFilter;
import java.io.IOException;
import java.io.Reader;

/**
	A filter removing XML comments.
	<p>
	Not thread safe.
*/
class CXMLCommentFilter extends CBlockFilter
{				
	CXMLCommentFilter(Reader in)
	{
		super(in, "<!--","-->");
	};
};