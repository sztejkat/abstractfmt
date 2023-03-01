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
			/** A end signal char */
			static final char END_SIGNAL_CHAR = ';';
			/** A begin signal char */
			static final char BEGIN_SIGNAL_CHAR = '*';
			/** A comment start character */
			static final char COMMENT_CHAR = '#';
			/** A escape character */
			static final char ESCAPE_CHAR = '\\';
			
				/** Where to write. Protected to allow some data injection in superclasses modifications */
				protected final Writer out;
				
				/** An escaping engine for names and string tokens */
				private final APlainEscapingEngine escaper = new APlainEscapingEngine()
				{
					@Override protected void out(char c)throws IOException
					{
						CPlainTxtWriteFormat.this.out.write(c);
					};
				};
				/** An escaping engine for comments*/
				private final ACommentEscapingEngine comment_escaper = new ACommentEscapingEngine()
				{
					@Override protected void out(char c)throws IOException
					{
						CPlainTxtWriteFormat.this.out.write(c);
					};
				};
				
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
			comment_escaper.reset();
			comment_escaper.append(s);
			comment_escaper.flush();
		closeOffBandData();
	};
	/* *****************************************************************
	
			ATxtWriteFormat1 / ATxtWriteFormat0
	
	******************************************************************/
	/* --------------------------------------------------------------
					common tokens
	----------------------------------------------------------------*/
	@Override protected void outEndSignalSeparator()throws IOException
	{
	};
	@Override protected void outBeginSignalSeparator()throws IOException
	{
		out.write(DEFAULT_EMPTY_CHAR);
	};
	@Override protected void outTokenSeparator()throws IOException
	{
		out.write(TOKEN_SEPARATOR_CHAR);
	};
	@Override protected void outTokenToEndSignalSeparator()throws IOException{};
	@Override protected void outTokenToBeginSignalSeparator()throws IOException{};
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
	
	/* ----------------------------------------------------------------
				tuning
	 ----------------------------------------------------------------*/
	/** Formats to "t" and "f" to get denser boolean blocks*/
	@Override protected String formatBooleanBlock(boolean v)
	{
		return v ? "t" : "f";
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
		escaper.reset();
	};
	/** Implements all escaping necessary for handling {@link #outStringToken(char)}.
	Sequence of calls to this method must be terminated by calling 
	{@link #closeEscapedStringToken} to purge dangling upper surogate.
	@param c char to write 
	@see #outEscapedStringToken(String)
	@throws IOException if failed 
	@see #openEscapedStringToken
	*/
	protected void outEscapedStringToken(char c)throws IOException
	{
		if (DUMP) TOUT.println("outStringTokenImpl(0x"+Integer.toHexString(c)+") ENTER");
		escaper.write(c);
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
	@see #escaper
	*/
	protected void closeEscapedStringToken()throws IOException
	{
		escaper.flush();
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
		if (DUMP) TOUT.println("beginDirectImpl() LEAVE");
	};
	/* *****************************************************************
	
			AStructWriteFormatBase0
	
	******************************************************************/
	@Override protected void endImpl()throws IOException
	{
		if (DUMP) TOUT.println("endImpl() ENTER");
		out.write(END_SIGNAL_CHAR);
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
	
	/* *****************************************************************
	
			IFormatLimits
	
	******************************************************************/
	/** No limit */
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	/** No limit */
	@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
};