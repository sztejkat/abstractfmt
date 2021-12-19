package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.util.AAdaptiveFilterReader;
import java.io.IOException;
import java.io.Reader;

/**
	A filter which performs XML white-space normalization
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
			Whitespace normalization rules are:
			1.Each whitespace is replaced with ' ' (d32)
			2.If there is > right before the whitespace
			  the whitespace is removed.
			3.If there is < or > right after the whitespace,
			  the whitespace is removed
			4.If there is a whitespace after right after
			 the whitespace, the whitespace is removed
		*/
		int ci = in.read();
		if (ci==-1) return;
		char c = (char)ci;
		switch(state)
		{
			case STATE_CHAR:
					if (c=='>')
					{
						state = STATE_TAG;
						write(c);
						return;
					};
					if (Character.isWhitespace(c))
					{
						state = STATE_WHITESPACE;
						return;
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
					if (Character.isWhitespace(c)) return;
					//write pending white space
					write(' ');
					write(c);
					state = STATE_CHAR;
					break;
			case STATE_TAG:
					if (Character.isWhitespace(c))
					{
						 return;
					};
					state = STATE_CHAR;
					write(c);
					break;
		}
	};
	@Override public void close()throws IOException
	{
		super.close();
		in.close();
	};
};