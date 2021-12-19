package sztejkat.abstractfmt.util.xml;
import sztejkat.abstractfmt.util.AAdaptiveFilterReader;
import sztejkat.abstractfmt.util.CAdaptivePushBackReader;
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