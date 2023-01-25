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
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CPlainTxtWriteFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CPlainTxtWriteFormat.",CPlainTxtWriteFormat.class) : null;
 
			/** A token separator character */
			static final char TOKEN_SEPARATOR_CHAR = ',';
			/** A default empty character to write*/
			static final char DEFAULT_EMPTY_CHAR = ' ';
			/** A string token separator character */
			static final char STRING_TOKEN_SEPARATOR_CHAR = '\"';
			/** An escape character inside a string character */
			static final char ESCAPE_CHAR = '\\';
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
					/** Nothing was written to a stream yet*/
					NOTHING,
					/** A begin indicator was written, name follows in form of a token */
					BEGIN_INDICATOR,
					/** A begin name was written */
					BEGIN_WRITTEN,
					/** An end indicator was written */
					END_WRITTEN,
					/** A token is terminated */
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
				/** Set to true in {@link TTokenState#STRING_TOKEN_OPENED}
				if upper surogate was detected thous we may have 
				a proper surogate sequence, possibly.
				The link {@link #upper_surogate_pending}
				do carry the surogate in question*/
				private boolean is_upper_surogate_pending;
				private char upper_surogate_pending;
				
				
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
	/* --------------------------------------------------------------
					Common part for plain and string tokens.
	----------------------------------------------------------------*/
	/** Finishes pending closeToken action if any.
	@throws IOException if failed
	@see #finishPendingCloseStringToken
	@see #finishPendingClosePlainToken
	*/
	private void finishPendingCloseToken()throws IOException
	{
		if (TRACE) TOUT.println("finishPendingCloseToken()");
		switch(token_state)
		{
			//Note: Intentionally no toggling if other state 
			//is in progress. We just terminate what is pending.
			case STRING_TOKEN_CLOSING:
					finishPendingCloseStringToken();
					token_state= TTokenState.NO_TOKEN;
					break;
			case PLAIN_TOKEN_CLOSING:
					finishPendingClosePlainToken();
					token_state= TTokenState.NO_TOKEN;
					break;
		};
	};
	/* --------------------------------------------------------------
					plain tokens
	----------------------------------------------------------------*/
	@Override protected void openPlainToken()throws IOException
	{	
		if (TRACE) TOUT.println("openPlainToken() ENTER");
		//handle pending operation
		switch(token_state)
		{
			case NOTHING:
			case END_WRITTEN:
			case BEGIN_INDICATOR: 
								break;
			case BEGIN_WRITTEN:
			case NO_TOKEN:
								out.write(DEFAULT_EMPTY_CHAR);
								break;
			case PLAIN_TOKEN_OPENED:
								//not re-opening.
								break;
			case PLAIN_TOKEN_CLOSING: 
								if (TRACE) TOUT.println("openPlainToken(), closing pending plain token");
								out.write(TOKEN_SEPARATOR_CHAR); 
								break;
			case STRING_TOKEN_OPENED: throw new AssertionError();
			case STRING_TOKEN_CLOSING:
							    if (TRACE) TOUT.println("openPlainToken(), closing pending string token");
								out.write(STRING_TOKEN_SEPARATOR_CHAR);
								out.write(TOKEN_SEPARATOR_CHAR); 
								break;
		};
		token_state = TTokenState.PLAIN_TOKEN_OPENED;
		if (TRACE) TOUT.println("openPlainToken() LEAVE");
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
		//We do NOT allow surogates in plain token because our tokenization
		//procedure has no way to escape eventuall incorrect surogates
		if ((c>=0xD800)&&(c<=0xDFFF)) return false;
		return true;
	};
	/** Validates state and calls {@link #outPlainTokenImpl} */
	@Override protected final void outPlainToken(char c)throws IOException
	{
		assert(token_state == TTokenState.PLAIN_TOKEN_OPENED);
		outPlainTokenImpl(c);
	};
	
	/** Called by {@link #outPlainToken}. This method intentionally does not validate 
	state, as it will be also called for begin signal handling.
	@param c must be {@link #isPlainTokenChar}
	@throws IOException if failed;
	*/
	protected void outPlainTokenImpl(char c)throws IOException
	{
		assert(isPlainTokenChar(c));
		if (DUMP) TOUT.println("outPlainToken(0x"+Integer.toHexString(c)+")");
		out.write(c);
	};
	
	
	@Override protected void closePlainToken()throws IOException
	{
		if (TRACE) TOUT.println("closePlainToken()");
		assert(token_state==TTokenState.PLAIN_TOKEN_OPENED);
		//rememeber for the future.
		token_state = TTokenState.PLAIN_TOKEN_CLOSING;
	};
	/** Called by {@link #finishPendingCloseToken()} when detected that 
	plain token closure is pending.
	<p>
	Empty.
	@throws IOException .
	*/
	protected void finishPendingClosePlainToken()throws IOException{	};
	
	
	/* -----------------------------------------------------------------
			String tokens	
	-----------------------------------------------------------------*/
	@Override protected void openStringToken()throws IOException
	{
		if (TRACE) TOUT.println("openStringToken() ENTER");
		//handle pending operation depending on state.
		switch(token_state)
		{
			case NOTHING:
		    case BEGIN_INDICATOR:
		    case END_WRITTEN:
		    					out.write(STRING_TOKEN_SEPARATOR_CHAR);
								break;
			case NO_TOKEN:
			case BEGIN_WRITTEN:
								out.write(DEFAULT_EMPTY_CHAR);
								out.write(STRING_TOKEN_SEPARATOR_CHAR);
								break;
			case PLAIN_TOKEN_OPENED: throw new AssertionError();
			case PLAIN_TOKEN_CLOSING: 
								if (TRACE) TOUT.println("openStringToken(), closing pending plain token");
								out.write(TOKEN_SEPARATOR_CHAR);
								out.write(STRING_TOKEN_SEPARATOR_CHAR);
								break;
			case STRING_TOKEN_OPENED: break; //no re-opening.
			case STRING_TOKEN_CLOSING:
								if (TRACE) TOUT.println("openStringToken() continuing previous string token");
								//now optimize it, no separators. 
								break;
		};
		token_state = TTokenState.STRING_TOKEN_OPENED;
		if (TRACE) TOUT.println("openStringToken() LEAVE");
		
	}
	
	/**
		Validates state and calls {@link #outStringTokenImpl} 
	*/
	@Override protected final void outStringToken(char c)throws IOException
	{
		assert(token_state==TTokenState.STRING_TOKEN_OPENED);
		outStringTokenImpl(c);
	};
	
	/** Called by {@link #outStringToken}. This method intentionally does not validate 
	state, as it will be also called for begin signal handling.
	@param c must be {@link #isPlainTokenChar}
	@throws IOException if failed;
	*/
	protected void outStringTokenImpl(char c)throws IOException
	{
		if (DUMP) TOUT.println("outStringTokenImpl(0x"+Integer.toHexString(c)+") ENTER");
		//Now inside a string we do allow __correct__ surogates.
		//and let them to be handled by down-stream
		
		if (is_upper_surogate_pending)
		{
			//a proper stream allows only lower surogate
			//if character is a lower surogate?
			if ((c>=0xDC00)&&(c<=0xDFFF))
			{
				//yes, we can pass both to encoder and be sure it will
				//encode it correctly
				if (DUMP) TOUT.println("outStringTokenImpl() surogate pair "+Integer.toHexString(upper_surogate_pending)+
										" "+Integer.toHexString(c)+" LEAVE");
					
				out.write(upper_surogate_pending);
				out.write(c);
				upper_surogate_pending = 0;
				is_upper_surogate_pending = false;
				
				return;
			}
			//it is not a propert surogate
			if (DUMP) TOUT.println("outStringTokenImpl(), improper surogate pair");
			escape(upper_surogate_pending);				
			upper_surogate_pending = 0;
			is_upper_surogate_pending = false;
		};
		//Check if we do init a pending upper surogate?
		if ((c>=0xD800)&&(c<=0xDBFF))
		{
			if (DUMP) TOUT.println("outStringTokenImpl(), detected upper surogate LEAVE");
			//yes, make it pending
			upper_surogate_pending = c;
			is_upper_surogate_pending = true;
			return;
		};
		//and escape it if necessary.
		switch(c)
		{
			case STRING_TOKEN_SEPARATOR_CHAR:
					//escape it
					if (DUMP) TOUT.println("outStringTokenImpl() escaping "+STRING_TOKEN_SEPARATOR_CHAR);
					out.write(ESCAPE_CHAR);
					out.write(STRING_TOKEN_SEPARATOR_CHAR);
					break;
			case ESCAPE_CHAR:
					if (DUMP) TOUT.println("outStringTokenImpl() escaping "+ESCAPE_CHAR);
					out.write(ESCAPE_CHAR);
					out.write(ESCAPE_CHAR);
					break;
			default:
					//now the only possible problem is a lower surogate
					if ((c>=0xDC00)&&(c<=0xDFFF))
					{
						if (DUMP) TOUT.println("outStringTokenImpl() escaping lower surogate");
						escape(c);
					}else
					{
						if (DUMP) TOUT.println("outStringTokenImpl() writing directly");
						out.write(c);
					};
		}
		if (DUMP) TOUT.println("outStringTokenImpl() LEAVE");
	};
	
	
	@Override protected void closeStringToken()throws IOException
	{
		if (TRACE) TOUT.println("closeStringToken()"); 
		assert(token_state==TTokenState.STRING_TOKEN_OPENED);
		//rememeber for the future.
		token_state = TTokenState.STRING_TOKEN_CLOSING;
	};
				/** Hex conversion table for escaping */
				private static final char [] HEX = new char[]
										{
											'0','1','2','3','4','5','6','7','8','9',
											'A','B','C','D','E','F'
										};
	/** Writes character hex escape 
	@param c what to escape
	@throws IOException if failed.
	*/
	protected void escape(char c)throws IOException
	{
		if (DUMP) TOUT.println("escape(0x"+Integer.toHexString(c)+")");
		out.write(ESCAPE_CHAR);
		boolean was_emited = false;
		for(int i=4;--i>=0;)
		{
			int nibble = (c & 0xF000)>>>(4+4+4);
			if (!was_emited)
			{
				if (nibble==0) continue;
			};
			was_emited = true;
			out.write(HEX[nibble]);
			c<<=4;
		};
		out.write(';');
	};
	/** Called by {@link #finishPendingCloseToken()} when detected that 
	string token closure is pending.
	@throws IOException .
	*/
	protected void finishPendingCloseStringToken()throws IOException
	{
			//Now we have two tasks: first handle eventual
			//pending upper surogate
			if (is_upper_surogate_pending)
			{
					escape(upper_surogate_pending);
					is_upper_surogate_pending = false;
					upper_surogate_pending=0;
			};
			//and then write closing ".
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
			//Note: We do exclude surogates from plain names.
			if (!isPlainTokenChar(name.charAt(i))) return false;
		};
		return true;
	};
	@Override protected void beginDirectImpl(String name)throws IOException
	{
		if (DUMP) TOUT.println("beginDirectImpl(name=\""+name+"\") ENTER");
		//terminate pending token before writing a signal.
		finishPendingCloseToken();
		//The special case is a begin-begin which must produce
		//a separator.
		if (token_state==TTokenState.BEGIN_WRITTEN) out.write(DEFAULT_EMPTY_CHAR);
		out.write(BEGIN_SIGNAL_CHAR);
		token_state = TTokenState.BEGIN_INDICATOR; //force no space before token
		if (isPlainName(name))
		{
			openPlainToken();
			outPlainToken(name);
			closePlainToken();
			finishPendingCloseToken();
		}else
		{
			openStringToken();
			outStringToken(name);
			closeStringToken();
			finishPendingCloseToken();
		}
		token_state = TTokenState.BEGIN_WRITTEN;
		if (DUMP) TOUT.println("beginDirectImpl() LEAVE");
	};
	/* *****************************************************************
	
			AStructWriteFormatBase0
	
	******************************************************************/
	@Override protected void endImpl()throws IOException
	{
		if (DUMP) TOUT.println("endImpl() ENTER");
		//terminate pending token before writing a signal.
		finishPendingCloseToken();	
		out.write(END_SIGNAL_CHAR);
		token_state = TTokenState.END_WRITTEN;
		if (DUMP) TOUT.println("endImpl() LEAVE");
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
		finishPendingCloseToken();	
		out.flush();
	};
	
	/* ----------------------------------------------------------------
				tuning
	 ----------------------------------------------------------------*/
	/** Formats to "t" and "f" to get denser boolean blocks*/
	@Override protected String formatBooleanBlock(boolean v)
	{
		return v ? "t" : "f";
	};
	/* *****************************************************************
	
			IFormatLimits
	
	******************************************************************/
	/** No limit */
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	/** No limit */
	@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
};