package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.ATxtWriteFormat1;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Writer;

/**
	A reference plain text format implementation, writing side.
*/
public class CPlainTxtWriteFormat extends ATxtWriteFormat1
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
				
				/** Set to true in string token	if upper surogate was detected 
				thous we may have a proper surogate sequence, possibly.
				The link {@link #upper_surogate_pending}
				do carry the surogate in question*/
				private boolean is_upper_surogate_pending;
				private char upper_surogate_pending;
				/** Used to track if inject signal separator or not.
				In generic end signals do not need signal separators. */
				private boolean last_signal_was_begin;
				
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates
	@param out writer where to write, non null, opened. Must accept all
		possible <code>char</code> values.
	*/
	public CPlainTxtWriteFormat(Writer out)
	{
		super(0);	//We do not support registered names.
					//Due to JAVA lacking virtual multiple inheritance
					//it was for me easier to inherite the ARegisteringStructWriteFormat
					//in generic text support and then disable it here rather
					//than playing with class composition.
		assert(out!=null);
		this.out = out;
	};
	
	/* *****************************************************************
		
			Extension, comment writing
	
	******************************************************************/
	/** Writes a single or multi-line comment.
	@param s comment to write. This comment <u>must not</u> contain
			invalid surogate characters, but it is not verified.
	@throws IOException if failed.
	*/
	public void writeComment(String s)throws IOException
	{
		openOffBandData();
		out.write(COMMENT_CHAR);
		//and now write body, detecting eols.
		//We need to prevent system from seeing any character AFTER
		//the EOL. We do not need to change all eols into comments
		//tough, because they are ignorable. It would be nice however
		//if we could do it. We have to however add eol after a comment
		//if it is not there.
		//So what patterns do we have?
		//			a\nb	->a\n#b
		//			a\n\nb	->a\n#\n#b
		//			a\n\rb	->a\n\r#b
		//			a\rb	->a\r#b
		//			a\r\rb	->a\r#\r#b
		//			a\r\nb	->a\r\n#b
		char c = 0;
		char found_eol = '\n';
		for(int i = 0, lim = s.length(); i<lim; i++)
		{
			char n= s.charAt(i);
			//First pattern
			if (
					(i!=lim-1)
					&&
					(
					((c=='\n')&&(n!='\r'))
						||
					((c=='\r')&&(n!='\n'))
					)
				)
				{
						//System.out.print("#<"+n+"("+Integer.toHexString(n)+")");
						out.write(COMMENT_CHAR);
						out.write(n);
						found_eol=c;
						c = n;
				}else
			if (	(i!=lim-1)
					&&
					(
						((c=='\n')&&(n=='\r'))
							||
						((c=='\r')&&(n=='\n')))
					)
				{
					found_eol=n;
					out.write(n);						
					out.write(COMMENT_CHAR);
					c = 0;
					//System.out.print(n+"("+Integer.toHexString(n)+")->#");
				}else
				{
					//System.out.print(n+"("+Integer.toHexString(n)+")");
					out.write(n);
					c = n;
				};
			
		};
		if ((c!='\n')&&(c!='\r')) 
		{
			out.write(found_eol);
		}
		closeOffBandData();
	};
	/* *****************************************************************
	
			ATxtWriteFormat1 / ATxtWriteFormat0
	
	******************************************************************/
	/* --------------------------------------------------------------
					common tokens
	----------------------------------------------------------------*/
	@Override protected void outSignalSeparator()throws IOException
	{
		if (last_signal_was_begin)
				out.write(DEFAULT_EMPTY_CHAR);
	};
	@Override protected void outTokenSeparator()throws IOException
	{
		out.write(TOKEN_SEPARATOR_CHAR);
	};
	/* --------------------------------------------------------------
					plain tokens
	----------------------------------------------------------------*/	
	@Override protected void openPlainTokenImpl()throws IOException{};
	@Override protected void closePlainTokenImpl()throws IOException{};
	@Override protected void outPlainToken(char c)throws IOException
	{
		assert(isPlainTokenChar(c));
		if (DUMP) TOUT.println("outPlainToken(0x"+Integer.toHexString(c)+")");
		out.write(c);
	};
	/* --------------------------------------------------------------
					string tokens
					
			Redirects to EscapedStringToken engine
	----------------------------------------------------------------*/
	/**
	@see #openEscapedStringToken */
	@Override protected void openStringTokenImpl()throws IOException
	{
		out.write(STRING_TOKEN_SEPARATOR_CHAR);
		openEscapedStringToken();
	};
	/**
	@see #closeEscapedStringToken */
	@Override protected void closeStringTokenImpl()throws IOException
	{
		closeEscapedStringToken();
		out.write(STRING_TOKEN_SEPARATOR_CHAR);
	};
	/**
	@see #outEscapedStringToken(char) */
	@Override protected void outStringToken(char c)throws IOException
	{
		outEscapedStringToken(c);
	};
	/* ***************************************************************
	
				state-less support for escaped string tokens
				
	****************************************************************/
	/** Used by {@link #openStringTokenImpl} to initiate escaping mechanism.
		Opposite to string tokens the 
		{@link #openEscapedStringToken},
		{@link #outEscapedStringToken(char)},{@link #outEscapedStringToken(String)},
		{@link #closeEscapedStringToken}
		can be used regardless of token state. For an example to write names
		of signals.
		<p>
		Does not write opening and closig ""
		@throws IOException if failed.
	*/
	protected void openEscapedStringToken()throws IOException
	{
		assert(!is_upper_surogate_pending);
	};
	/** Implements all escaping necessary for handling {@link #outStringToken(char)}.
	Sequence of calls to this method must be terminated by calling 
	{@link #closeEscapedStringToken} to purge dangling upper surogate.
	@param c char to write 
	@see #outEscapedStringToken(String)
	@throws IOException if failed 
	@see #is_upper_surogate_pending
	@see #openEscapedStringToken
	*/
	protected void outEscapedStringToken(char c)throws IOException
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
	/** Invokes {@link #outEscapedStringToken} for every charcter
	@param token non-null, but can be empty.
	@throws IOException if failed.
	@see #openEscapedStringToken
	*/
	protected void outEscapedStringToken(String token)throws IOException
	{
		if (TRACE) TOUT.println("outEscapedStringToken(\""+token+"\") ENTER");
		for(int i=0,n=token.length();i<n;i++)
		{
			outEscapedStringToken(token.charAt(i));
		};
		if (TRACE) TOUT.println("outEscapedStringToken() LEAVE");
	};
	/** Terminates sequence of {@link #outEscapedStringToken(char)}
	by purging pending upper surogates.
	@throws IOException if failed.
	@see #openEscapedStringToken
	@see #flushPendingSurogates
	*/
	protected void closeEscapedStringToken()throws IOException
	{
		flushPendingSurogates();
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
	/** Called by {@link #closeStringTokenImpl()} and {@link #closeEscapedStringToken} to flush pending
	dangling upper surogate if any.
	@throws IOException .
	*/
	private void flushPendingSurogates()throws IOException
	{
		//Now we have two tasks: first handle eventual
		//pending upper surogate
		if (is_upper_surogate_pending)
		{
				escape(upper_surogate_pending);
				is_upper_surogate_pending = false;
				upper_surogate_pending=0;
		};
	};
	
	
	/* **********************************************************
	
			ARegisteringStructWriteFormat
	
	***********************************************************/
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
	protected boolean isPlainName(String name)
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
		//Tokens are managed by superclass so we just need to decide on how to
		//write the body.		
		out.write(BEGIN_SIGNAL_CHAR);
		if (isPlainName(name))
		{
			out.write(name);
		}else
		{
			out.write(STRING_TOKEN_SEPARATOR_CHAR);
				openEscapedStringToken();
				outEscapedStringToken(name);			
				closeEscapedStringToken();
			out.write(STRING_TOKEN_SEPARATOR_CHAR);
		}
		last_signal_was_begin = true;
		if (DUMP) TOUT.println("beginDirectImpl() LEAVE");
	};
	/* *****************************************************************
	
			AStructWriteFormatBase0
	
	******************************************************************/
	@Override protected void endImpl()throws IOException
	{
		if (DUMP) TOUT.println("endImpl() ENTER");
		out.write(END_SIGNAL_CHAR);
		last_signal_was_begin = false;
		if (DUMP) TOUT.println("endImpl() LEAVE");
	};
	/** Empty */
	@Override protected void openImpl()throws IOException{};
	/** Closes output writer */
	@Override protected void closeImpl()throws IOException
	{
		out.close();
	};
	/** Flushes output writer, terminates pending token */
	@Override protected void flushImpl()throws IOException
	{
		super.flushImpl();	
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