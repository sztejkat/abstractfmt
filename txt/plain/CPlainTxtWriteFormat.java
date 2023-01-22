package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.ATxtWriteFormat0;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Writer;

/**
	A reference plain text format implementation, writing side.
*/
public class CPlainTxtWriteFormat extends ATxtWriteFormat0
{
			/** A token separator character */
			static final char TOKEN_SEPARATOR_CHAR = ',';
			/** A default empty character to write*/
			static final char DEFAULT_EMPTY_CHAR = ' ';
			/** A string token separator character */
			static final char STRING_TOKEN_SEPARATOR_CHAR = '\"';
			/** A end signal char */
			static final char END_SIGNAL_CHAR = ';';
			/** A begin signal char */
			static final char BEGIN_SIGNAL_CHAR = '*';
			/** A comment start character */
			static final char COMMENT_CHAR = '#';
			
				/** Where to write. Protected to allow some data injcection in superclasses modifications */
				protected final Writer out;
				/** What are we doing with a token? */
				private static enum TTokenState
				{
					/** Nothing was written to a stream yet */
					NOTHING,
					/** No token generated yet in signal payload */
					NO_TOKEN,
					/** Plain token in progress */
					PLAIN_TOKEN_OPENED,
					/** String token in progress */
					STRING_TOKEN_OPENED,
					/** Plain token closed, but writing closure data is pending */
					PLAIN_TOKEN_CLOSING,
					/** String token closed, but writing closure data is pending */
					STRING_TOKEN_CLOSING;
				};
				private TTokenState token_state = TTokenState.NOTHING;
				
				
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates
	@param out writer where to write, non null, opened. Must accept all
		possible <code>char</code> values.
	*/
	public CPlainTxtWriteFormat(Writer out)
	{
		super(0);	// no registry support.
		assert(out!=null);
		this.out = out;
	};
	
	/* *****************************************************************
	
			ATxtWriteFormat0
	
	******************************************************************/
	@Override protected void openPlainToken()throws IOException
	{	
		//handle pending operation
		switch(token_state)
		{
			case NO_TOKEN:
								out.write(DEFAULT_EMPTY_CHAR);
								break;
			case PLAIN_TOKEN_CLOSING: 
								out.write(TOKEN_SEPARATOR_CHAR); 
								break;
			case STRING_TOKEN_CLOSING:
								out.write(STRING_TOKEN_SEPARATOR_CHAR);
								out.write(TOKEN_SEPARATOR_CHAR); 
								break;
		};
		token_state = TTokenState.PLAIN_TOKEN_OPENED;
	};
	@Override protected void closePlainToken()throws IOException
	{
		assert(token_state==TTokenState.PLAIN_TOKEN_OPENED);
		//rememeber for the future.
		token_state = TTokenState.PLAIN_TOKEN_CLOSING;
	};
	@Override protected void openStringToken()throws IOException
	{
		//handle pending operation depending on state.
		switch(token_state)
		{
		    case NOTHING:       out.write(STRING_TOKEN_SEPARATOR_CHAR);
								break;
			case NO_TOKEN:    
								out.write(DEFAULT_EMPTY_CHAR);
								out.write(STRING_TOKEN_SEPARATOR_CHAR);
								break;
			case PLAIN_TOKEN_CLOSING: 
								out.write(TOKEN_SEPARATOR_CHAR);
								out.write(STRING_TOKEN_SEPARATOR_CHAR);
								break;
			case STRING_TOKEN_CLOSING:
								//now optimize it, no separators. 
								break;
		};
		token_state = TTokenState.STRING_TOKEN_OPENED;
		
	}
	@Override protected void closeStringToken()throws IOException
	{
		assert(token_state==TTokenState.STRING_TOKEN_OPENED);
		//rememeber for the future.
		token_state = TTokenState.STRING_TOKEN_CLOSING;
	};
	/** Terminate pending token which still may be opened in stream.
	Mainly string tokens which are optimized.
	@throws IOException if failed */
	private void terminateToken()throws IOException
	{
		if (token_state==TTokenState.STRING_TOKEN_CLOSING)
		{
			out.write(STRING_TOKEN_SEPARATOR_CHAR);
		};
		token_state= TTokenState.NO_TOKEN;
	};
	/** Dispatches to {@link #outPlainToken}/ {@link #outStringToken} */
	@Override protected void outToken(char c)throws IOException
	{
		switch(token_state)
		{
			case PLAIN_TOKEN_OPENED: outPlainToken(c); break;
			case STRING_TOKEN_OPENED: outStringToken(c); break;
			default: throw new AssertionError(token_state);
		}
	};
	/** Tests if specified character can be used in plain token
	@param c char to check
	@return true if is allowed, false if not */
	static boolean isPlainTokenChar(char c)
	{
		if (Character.isWhitespace(c)) return false;
		switch(c)
		{
			case TOKEN_SEPARATOR_CHAR:
			case STRING_TOKEN_SEPARATOR_CHAR:
			case END_SIGNAL_CHAR:
			case COMMENT_CHAR:  
			case BEGIN_SIGNAL_CHAR:  return false;
		};
		return true;
	};
	/** Called by {@link #outToken}. This method intentionally does not validate 
	state, as it will be also called for begin signal handling.
	@param c must be {@link #isPlainTokenChar}
	@throws IOException if failed;
	*/
	protected void outPlainToken(char c)throws IOException
	{
		assert(isPlainTokenChar(c));
		out.write(c);
	};
	/** Called by {@link #outToken}. This method intentionally does not validate 
	state, as it will be also called for begin signal handling.
	@param c must be {@link #isPlainTokenChar}
	@throws IOException if failed;
	*/
	protected void outStringToken(char c)throws IOException
	{
		out.write(c);
		//and escape it if necessary.
		if (c==STRING_TOKEN_SEPARATOR_CHAR)
			out.write(STRING_TOKEN_SEPARATOR_CHAR);
	};
	/* *****************************************************************
	
			ARegisteringStructWriteFormat
	
	******************************************************************/
	/** Always throws, should not be ever called */
	@Override protected void beginAndRegisterImpl(String name, int index, int order)throws IOException
	{
		throw new UnsupportedOperationException();
	};
	/** Always throws, should not be ever called */
	@Override protected void beginRegisteredImpl(int index, int order)throws IOException
	{
		throw new UnsupportedOperationException();
	};
	/** Tests if specified string name can be stored as a plain token
	@param name name to test
	@return true if can be stored as a plain token, false if needs to be stored
			as a string token 
	*/
	protected boolean isPlainName(String name)throws IOException
	{
		assert(name!=null);
		for(int i=0,n =name.length(); i<n; i++)
		{
			if (!isPlainTokenChar(name.charAt(i))) return false;
		};
		return true;
	};
	@Override protected void beginDirectImpl(String name)throws IOException
	{
		//check if we need to inject space before begin since there was no token?
		//Notice, we don't need it before end token.
		boolean inject_empty = token_state==TTokenState.NO_TOKEN;
		//terminate pending token before writing a signal.
		terminateToken();
		if (inject_empty) out.write( DEFAULT_EMPTY_CHAR );
		out.write(BEGIN_SIGNAL_CHAR);
		if (isPlainName(name))
		{
			for(int i=0,n =name.length(); i<n; i++)
			{
				outPlainToken(name.charAt(i));
			};
		}else
		{
			out.write(STRING_TOKEN_SEPARATOR_CHAR);
			for(int i=0,n =name.length(); i<n; i++)
			{
				outStringToken(name.charAt(i));
			};
			out.write(STRING_TOKEN_SEPARATOR_CHAR);
		}
	};
	/* *****************************************************************
	
			AStructWriteFormatBase0
	
	******************************************************************/
	@Override protected void endImpl()throws IOException
	{
		//terminate pending token before writing a signal.
		terminateToken();	
		out.write(END_SIGNAL_CHAR);
	};
	/** Empty */
	@Override protected void openImpl()throws IOException
	{
		
	};
	/** Closes output writer */
	@Override protected void closeImpl()throws IOException
	{
		out.close();
	};
	/** Flushes output writer, terminates pending token */
	@Override protected void flushImpl()throws IOException
	{
		//This is a slight overkill, but helps to keep stream consistency
		//and is absolutely necessary on close.
		terminateToken();	
		out.flush();
	};
	/* *****************************************************************
	
			IFormatLimits
	
	******************************************************************/
	/** No limit */
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	/** No limit */
	@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
};