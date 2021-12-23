package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.util.AAdaptiveFilterReader;
import java.io.IOException;
import java.io.Reader;

/**
	A filter which performs XML white-space normalization
	<p>
	Whitespace normalization rules are:
	<ul>
		<li>Each whitespace is replaced with ' ' (d32), where
		whitespace is any Java whitespace what includes tabs,
		eol, page-feed and etc;</li>
		<li>If there is &gt; or &lt; right before the whitespace
		the whitespace is removed;</li>
		<li>If there is &lt; or &gt; right after the whitespace,
		the whitespace is removed;</li>
		<li>If there is a whitespace after right after
		 the whitespace, the whitespace is removed;</li>
	</ul>
	Examples:
	<ol>
		<li><code>&lt;  x  &gt; &rarr; &lt;x&gt;</code></li>
		<li><code>&lt;x&gt;     &lt;x&gt; &rarr; &lt;x&gt;&lt;x&gt;</code></li>
		<li><code>&lt;x  name   =   x   &gt; &rarr; &lt;x name = x&gt;</code></li>
		<li><code>&lt;x&gt; Mary   had a   pony.  &lt;/x&gt;  &rarr; &lt;x&gt;Mary had a pony.&lt;/x&gt; </code></li>
	</ol>
*/
class CXMLWhitespaceNormalizingFilter extends AAdaptiveFilterReader
{
				/** Input  */
				private final Reader in;
				/** If recently returned regular char */
				private static final byte STATE_CHAR = (byte)0;
				/** If recently processed skipped whitespace */
				private static final byte STATE_WHITESPACE = (byte)1;
				/** If recently returned &gt; */
				private static final byte STATE_TAG = (byte)2;
				/** State variable */
				private byte state;	
				
	CXMLWhitespaceNormalizingFilter(Reader in)
	{
		super(2,2);
		assert(in!=null);
		this.in=in;
	};			
	@Override protected void filter()throws IOException
	{
		/*
			Note:
			
			We should not return with an empty
			fill buffer, because it will create numerous fragmented
			reads by generating numerous fake EOF in upper level
			class which returns eof if fill() returns empty.
			
			On the other hand we can't fetch ahead too much, so
			we spin until we read at least one character or input
			returns eof.
		*/
		for(;;)
		{
			int ci = in.read();
			if (ci==-1) return;
			char c = (char)ci;
			switch(state)
			{
				case STATE_CHAR:
						if ((c=='<')||(c=='>'))
						{
							state = STATE_TAG;
							write(c);
							return;
						};
						if (Character.isWhitespace(c))
						{
							state = STATE_WHITESPACE;
							continue;
						}
						write(c);
						break;
				case STATE_WHITESPACE:
						if ((c=='<')||(c=='>'))
						{
							state = STATE_CHAR;
							write(c);
							return;
						};
						if (Character.isWhitespace(c)) continue;
						//write pending white space
						write(' ');
						write(c);
						state = STATE_CHAR;
						break;
				case STATE_TAG:
						if (Character.isWhitespace(c))
						{
							 continue;
						};
						state = STATE_CHAR;
						write(c);
						break;
			}
		}
	};
	@Override public void close()throws IOException
	{
		super.close();
		in.close();
	};
};