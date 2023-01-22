package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Reader;

/**
	Uses low level text parsing over a known set of syntax
	elements to process all necessary tokens.
	
	<h1>Text parsing</h1>
	<h2>Required services</h2>
	As You probably noticed the {@link ATxtReadFormat0}
	turns around two methods:
	<ul>
		<li>{@link ATxtReadFormat0#tokenIn} and;</li>
		<li>{@link ATxtReadFormat0#hasUnreadToken};</li>
	</ul>
	Both are practically the same and could be replaced with <code>peek()/drop()</code> model.
	<p>
	Those methods do work on "per-character" basis. The "per-character"
	basis, instead of "per-word" or "per-token", is intentional since
	it allows easy processing of inifnitely long tokens. If we would
	not have an assumption about infinitely long strings we could do
	it per-token way, but later You will see it would be useless.
	<p>
	They do however a bit more than character classification. In fact they
	do skip some characters and interprete others or decode escape sequences.
	<p>
	The lower layer which is {@link ARegisteringStructReadFormat} is
	designed around a single method:	
	<ul>
		<li>{@link ARegisteringStructReadFormat#readSignalReg()};</li>
	</ul>
	This method works on slightly higher abstraction level and does
	a lot of decoding in a background.
	
	<h2>Parsing state machine</h2>
	The most clean concept which can present a text parsing to a user
	is to present him with following API (see {@link ATxtReadFormat1}:
	<pre>
		void toNextChar()
		TSyntax getNextSyntaxElement()
		int getNextChar()
	</pre>
	where:
	<ul>
		<li>the {@link ATxtReadFormat1#toNextChar} takes next character from a stream
		and knowing the state of a stream deduces what does it mean;</li>
		<li>the {@link ATxtReadFormat1#getNextSyntaxElement} returns a "syntax element" 
		of a stream which corresponds to character to which {@link ATxtReadFormat1#toNextChar}
		moved.
		<p>
		Note: <code>TSyntax</code> should be immutable. Best if it would be an <code>Enum</code>.</li>
		
		<li>and finally {@link ATxtReadFormat1#getNextChar} returns a character
		collected by {@link ATxtReadFormat1#toNextChar};</li>
	</ul>
	For an example the XML:
	<pre>
		&lt;name style="full" &gt;
	</pre>
	will report:
	<table border="1" >
		<caption>XML states</caption>
		<tr><td> &lt; </td><td>XML element start entry (<a href="#STAR1">*</a>)</td></tr>
		<tr><td> n </td><td>XML element name</td></tr>
		<tr><td> ... </td><td>-//-</td></tr>
		<tr><td> </td><td>XML separator</td></tr>
		<tr><td>s</td><td>XML attribute name</td></tr>
		<tr><td> ... </td><td>-//-</td></tr>
		<tr><td> = </td><td>XML attribute operator</td></tr>
		<tr><td> &#22; </td><td>XML attribute value separator</td></tr>
		<tr><td>f</td><td>XML attribute value</td></tr>
		<tr><td> ... </td><td>-//-</td></tr>
		<tr><td> &#22; </td><td>XML attribute value separator</td></tr>
		<tr><td> </td><td>XML separator</td></tr>
		<tr><td> &gt; </td><td>XML element start exit</td></tr>
	</table>
	<p id="STAR1">*)Notice that "XML element start entry" requires some  look forward to check
	if it is not: <code>&lt;/</code> nor <code>&lt;!--</code><p>
	<p>
	This is a relatively simple and well isolated syntax processing machine You may provide
	for Your format and encode in this class. I don't make any assumption if You provide it
	by subclassing or by a separate engine object.
	
	<h2>Parsing support</h2>
	The text parsing into characters and syntax elementd do requires, mostly, two kinds of services:
	<ul>
		<li>character stream related:
			<ul>
				<li>{@link CAdaptivePushBackReader}	which allows un-reading charactes if necessary;
				</li>
			</ul>			
		</li>
		<li>syntax state related:
			<ul>
				<li>{@link CBoundStack#push} - which pushes syntax element on syntax stack;</li>
				<li>{@link CBoundStack#pop} - which pops from stack.
				<p>
				Both can be used for maintaining syntaxt recognition in case of formats
				which are defined by state graphs with sub-graphs or recursive state graphs.
				Notice however, that it is best to avoid state stack if possible (for an example 
				use objects counter in JSON or nested elements counter in XML), since 
				in case of heavily recusrive data structures the stack will consume significant
				amount of memory and will be a limiting factor which may lead to <code>OutOfMemoryError</code>.
				</li>
				<li>{@link CBoundStack#setStackLimit}/{@link CBoundStack#getStackLimit} - which do allow to 
				set up a barrier against <code>OutOfMemoryError</code>;</li>
			</ul>
		</li>
	</ul>
	
	<h1>Syntax definition</h1>
	The syntax is defined by {@link ATxtReadFormat1.ISyntax} 
	which transforms Your specific syntax to syntax known
	by this class which is {@link ATxtReadFormat1.TIntermediateSyntax}
	
*/	
public abstract class ATxtReadFormat1<TSyntax extends ATxtReadFormat1.ISyntax>
									  extends ATxtReadFormat0
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ATxtReadFormat1.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ATxtReadFormat1.",ATxtReadFormat1.class) : null;
  
			/** A contract which allows to provide a kind of "enum"
			extension.
			<p>
			If You don't need more syntax elements than 
			{@link TIntermediateSyntax} You may just use that
			enum since it implements {@link ISyntax}
			*/
			public interface ISyntax
			{
				/**	Returns {@link ATxtReadFormat1} syntax element 
				which is represented by this syntax element.
				Basically converts from Your enum type to enum
				understood by this class.
				@return never null, life-time constant.
				*/
				public ATxtReadFormat1.TIntermediateSyntax syntax();
			};
			/**
				A syntax definition for {@link #getNextSyntaxElement}
				used to perform all remaning decoding.
			*/
			public static enum TIntermediateSyntax implements ISyntax
			{
				/** An ignorable character which can appear anywhere.
				Does not cause any action except beeing ignored and skipped.*/
				VOID,
				/** A character which is a start of a "begin" signal syntax.
				<p>
				In reaction to this character the processing of current signal content 
				is stopped and {@link ARegisteringStructReadFormat#readSignalReg}
				will be used to process the begin signal by collecting, in any order:
				<ul>
					<li>the name using a sequence {@link #SIG_NAME},{@link #SIG_NAME_VOID} characters;</li>
					<li>the index using {@link #SIG_INDEX};</li>
					<li>the index using {@link #SIG_ORDER};</li>
				</ul>
				The collection stops on any other non {@link #VOID} character or end-of-file which 
				terminates signal definition.
				<p>
				This is an error if any of name or index was collected twice.
				<p>
				The signal is then interpreted as:
				<ul>
					<li>if neither name or index was collected, as {@link ARegisteringStructReadFormat.TSignalReg#SIG_BEGIN_DIRECT}
						with an empty name;</li>
					<li>if both name or index was collected, as {@link ARegisteringStructReadFormat.TSignalReg#SIG_BEGIN_AND_REGISTER};</li>
					<li>if only index was collected as {@link ARegisteringStructReadFormat.TSignalReg#SIG_BEGIN_REGISTERED};</li>
				</ul>
				*/
				SIG_BEGIN,
				/** A character appearing after {@link #SIG_BEGIN} or {@link #SIG_END_BEGIN}
				indicating that collection of name should take place, but that character is not a part 
				of a name. Allows to decode empty signal names.
				<p>
				For an example the:
				<pre>
				name
				</pre>
				will be reported by SIG_NAME(n),SIG_NAME(a),SIG_NAME(m),SIG_NAME(e) while
				<pre>
				"name"
				</pre>
				will be reported by SIG_NAME_VOID("),SIG_NAME(n),SIG_NAME(a),SIG_NAME(m),SIG_NAME(e),SIG_NAME_VOID("),
				*/
				SIG_NAME_VOID,
				/** Alike {@link SIG_NAME_VOID}, but is a part of signal name and should be collected. */ 
				SIG_NAME,
				/** A character making a part of decimal signal index. Up to 16 digits are allowed
				and text will be parsed by <code>Integer.decode</code>. The index cannot be negative.				
				*/
				SIG_INDEX,
				/** A character indicating that begin or end-begin signal is a signal registration
				and that index of registration is order based */
				SIG_ORDER,
				/** Alike {@link #SIG_BEGIN} but causes {@link ARegisteringStructReadFormat#readSignalReg}
				to return {@link ARegisteringStructReadFormat.TSignalReg#SIG_END_BEGIN_DIRECT},
				{@link ARegisteringStructReadFormat.TSignalReg#SIG_END_BEGIN_DIRECT},
				{@link ARegisteringStructReadFormat.TSignalReg#SIG_END_BEGIN_AND_REGISTER},
				{@link ARegisteringStructReadFormat.TSignalReg#SIG_END_BEGIN_REGISTERED}
				accordingly. */
				SIG_END_BEGIN,
				/**A character which is a start of a "end" signal syntax.
				<p>
				In reaction to this character the processing of current signal content 
				is stopped and {@link ARegisteringStructReadFormat#readSignalReg}
				will be used to process the end signal by collecting
				this one {@link #SIG_END}.*/
				SIG_END,
				/** A token or signal terminator. This character do not belong to any
				syntax element and do terminate a previous syntax element.
				<p>
				For an example, if our syntax defines that we can't have empty
				tokens and whitespace is token separator the:
				<pre>
				ab  de
				</pre>
				emits: 
				<pre>
	TOKEN(a),TOKEN(b),SEPARATOR( ),SEPARATOR( ),TOKEN(d), TOKEN(e)
				</pre>
				or:
				<pre>
	TOKEN(a),TOKEN(b),SEPARATOR( ),VOID,TOKEN(d), TOKEN(e)
				</pre>
				<p>
				If we define that white-spaces are not parts of tokens and , is a delimiter
				and ,, defines an empty token we do:
				<pre>
				ab , de
				</pre>
				emits:
				<pre>
	TOKEN(a),TOKEN(b),VOID,NEXT_TOKEN,VOID,TOKEN(d),TOKEN(e)
				</pre>			
				*/
				SEPARATOR,
				/** A token or signal terminator. This character do not belong to any
				syntax element and do terminate a previous syntax element 
				and starts new token. 
				*/ 				
				NEXT_TOKEN,
				/** A token character. This character do belong to a token, and if
				token was not started, starts it and becomes a part of it. */
				TOKEN;
				@Override public ATxtReadFormat1.TIntermediateSyntax syntax(){ return this; };
			};
			/** Token parsing state machine */
			private static enum TTokenState
			{
				/** Token machine is at signal, don't do more tokenization */
				TOKEN_AT_SIGNAL,
				/** Token machine is inside a token, collect it till end */
				TOKEN_BODY,
				/** Token machine is on the lookup for tokens, skipp all intermediate chars */
				TOKEN_LOOKUP;
			}
			/** Parser state */
			private TTokenState token_state;
			/** Used to implement peek/drop look-ahead parsing model 
			True if {@link #peek_next_char} and {@link #peek_syntax}
			are up to date and not consumed
			@see #validatePeek
			*/ 
			private boolean peek_valid;
			/** Buffer for collecting name and index */
			private final StringBuilder name_and_index_buffer;
			/** Support for {@link #pickLastSignalRegName} */
			private String pick_last_signal_reg_name;
			/** Support for {@link #pickLastSignalIndex} */
			private int pick_last_signal_index=-1;
			/** An ordered registration counter */
			private int ordered_registration_counter=-1;
			/** A conflict preveter for registration modes */
			private boolean was_index_based_registration;
	/* *********************************************************************
	
		Construction
		
	
	* *********************************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
	@param token_size_limit non-zero positive, a maximum number of characters which constitutes 
			a primitive element token, excluding string tokens. Basically a maximum
			number of characters which do constitute a primitive numeric value.
	@throws AssertionError if token_size_limit is less than 16+3 which is a minimum
			number to hold hex encoded long value.
	*/
	protected ATxtReadFormat1(int name_registry_capacity,int token_size_limit)
	{
		super(name_registry_capacity,token_size_limit);
		if (TRACE) TOUT.println("new ATxtReadFormat1()");
		token_state = TTokenState.TOKEN_LOOKUP;
		name_and_index_buffer = new StringBuilder();
	};
	/* *********************************************************************
	
		Syntax processing contract required from subclasses.
		
			See class description.
		
	
	* *********************************************************************/
	/** Reads next char from input and deuduces what does it mean.
	Updates {@link #getNextSyntaxElement} and {@link #getNextChar}
	@throws IOException if failed to deduce. End-of-file is explicite excluded.
	@see ATxtReadFormatSupport#toNextChar
	*/
	protected abstract void toNextChar()throws IOException;
	/** Tells what the character fetched by most recent call to do mean.
	@return syntax element describing the meaning of character or null
			to indicate end-of-file condition.
	@throws AssertionError if {@link #toNextChar} was never called.
	@see ATxtReadFormatSupport#getNextSyntaxElement
	*/
	protected abstract TSyntax getNextSyntaxElement();
	/** Returns the character fetched by most recent call to do mean.
	@return a character read 0...0xFFFF, or -1 to indicate end-of-file
			condition.
	@throws AssertionError if {@link #toNextChar} was never called.
	@see ATxtReadFormatSupport#getNextChar
	*/
	protected abstract int getNextChar();
	/* ********************************************************************
	
			Syntax processing.
	
	* *********************************************************************/
	/** Makes sure that {@link #peek_valid} is up to date.
	If nothings is peeed will call {@link #nextChar}.*/
	private void validatePeek()throws IOException
	{
		if (!this.peek_valid)
		{
			if (TRACE) TOUT.println("validatePeek->calling toNextChar");
			toNextChar();
			this.peek_valid=true;
		};
	};
	/** Calls {@link #validatePeek} and returns character at cursor
	@return of {@link #getNextChar} */
	private int peekChar()throws IOException
	{
		validatePeek();
		return getNextChar();
	};
	/** Calls {@link #validatePeek} and returns syntax at cursor
	@return take from {@link #getNextSyntaxElement}, can be null at eof
	*/
	private TIntermediateSyntax peekSyntax()throws IOException
	{
		validatePeek();
		final ISyntax stx = getNextSyntaxElement();
		if (stx==null) 
		{
			if (TRACE) TOUT.println("peekSyntax()=null");
			return null;
		}else
		{
			assert(stx.syntax()!=null):stx+" breaks contract";
			if (TRACE) TOUT.println("peekSyntax()="+stx.syntax());
			return stx.syntax();
		}
	};
	/** Consumes peeked character, if any.
	Makes sure that nearest {@link #validatePeek} will call {@link #nextChar} */
	private void consume()
	{
		if (TRACE) TOUT.println("consume() "+(this.peek_valid  ? "consumed":"nothing to consume"));
		this.peek_valid = false;
	};
	/* *********************************************************************
	
		ATxtReadFormat0
		
	
	* *********************************************************************/
	
	/** Moves cursor so that it points to next token
	or signal. Updates state accordingly.
	@return false if on eof, true if updated state.
	@throws IOException if failed
	*/
	private boolean findNextToken()throws IOException
	{
		if (TRACE) TOUT.println("findNextToken() ENTER");
		assert(token_state==TTokenState.TOKEN_LOOKUP);
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax();
			if (stx==null)
			{
				if (TRACE) TOUT.println("findNextToken()=false, eof LEAVE");
				return false;
			};
			switch(stx)
			{
				case VOID: if (TRACE) TOUT.println("findNextToken() consuming void"); 
						   consume(); break;	//consume and move forwards
				case SIG_BEGIN:
				case SIG_END_BEGIN:
				case SIG_END:
						//place tokenizer at rest, do not consume the character
						token_state = TTokenState.TOKEN_AT_SIGNAL;
						if (TRACE) TOUT.println("findNextToken()=true, stx="+stx+" token_state="+token_state+" LEAVE");
						return true;									
				case SEPARATOR:
						if (TRACE) TOUT.println("findNextToken() consuming separator");
						consume(); break; //as void				
				case NEXT_TOKEN:
						//we have found a token, but char does not belong to
						//it. Consume it and move to token collection.
						consume();
						token_state = TTokenState.TOKEN_BODY;
						if (TRACE) TOUT.println("findNextToken()=true, stx="+stx+" token_state="+token_state+" LEAVE");
						return true;
				case TOKEN: 
						//we have found a token, do NOT consume it, move to token collection
						token_state = TTokenState.TOKEN_BODY;
						if (TRACE) TOUT.println("findNextToken()=true, stx="+stx+" token_state="+token_state+" LEAVE");
						return true;
				default:
					// SIG_NAME_VOID, SIG_NAME, SIG_INDEX
					throw new EBrokenFormat(stx+" found while expecting token");
			}
		}
	};
	/** Fetches next token character, updates state accordingly.
	@return what to return from {@link #tokenIn} 
	@throws IOException if failed
	*/
	private int nextTokenBody()throws IOException
	{
		if (TRACE) TOUT.println("nextTokenBody() ENTER");
		assert(token_state==TTokenState.TOKEN_BODY);
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax();
			if (stx==null)
			{
				if (TRACE) TOUT.println("nextTokenBody()=TOKEN_EOF, stx="+stx+" token_state="+token_state+" LEAVE");
				return TOKEN_EOF;
			};
			switch(stx)
			{
				case VOID: consume(); break;	//consume and move forwards
				case SIG_BEGIN:
				case SIG_END_BEGIN:
				case SIG_END:
						//place tokenizer at rest, do not consume the character
						token_state = TTokenState.TOKEN_AT_SIGNAL;
						if (TRACE) TOUT.println("nextTokenBody()=TOKEN_SIGNAL, stx="+stx+" token_state="+token_state+" LEAVE");
						return TOKEN_SIGNAL;									
				case SEPARATOR:
						//terminated a token. Do consume it and move at lookup. 
						consume();
						token_state = TTokenState.TOKEN_LOOKUP;
						if (TRACE) TOUT.println("nextTokenBody()=TOKEN_BOUNDARY, stx="+stx+" token_state="+token_state+" LEAVE");
						return TOKEN_BOUNDARY;
				case NEXT_TOKEN:
						//terminated token, but also beginning of next token.
						consume(); //must be consumed, so that we start serving next token.	
						//Do NOT change token lookup.
						if (TRACE) TOUT.println("nextTokenBody()=TOKEN_BOUNDARY, stx="+stx+" token_state="+token_state+" LEAVE");
						return TOKEN_BOUNDARY;
				case TOKEN: 
						//we have found a token char, consume it and return.
						{
							int c = peekChar();
							assert((c>=0)&&(c<=0xFFFF));
							consume();
							if (DUMP) TOUT.println("nextTokenBody+=\'"+c+"'(0x"+Integer.toHexString(c)+") stx="+stx+" token_state="+token_state+" LEAVE");
							return c;
						}
				default:
					// SIG_NAME_VOID, SIG_NAME, SIG_INDEX
					throw new EBrokenFormat(stx+" found while expecting token");
			}
		}
	};
	@Override protected final int tokenIn()throws IOException
	{
		if (TRACE) TOUT.println("tokenIn() ENTER");
		loop:
		for(;;) //because token lookup may toggle states to signal or body.
		{
			switch(token_state)
			{
				case TOKEN_AT_SIGNAL:
							if (TRACE) TOUT.println("tokenIn()=TOKEN_SIGNAL, stuck at signal, LEAVE");
							return TOKEN_SIGNAL;
				case TOKEN_LOOKUP:
							if (!findNextToken())
							{
								if (TRACE) TOUT.println("tokenIn()=TOKEN_EOF, LEAVE");
								return TOKEN_EOF;
							};
							//Note: TOKEN_LOOKUP may be only due to eof, so we should not get in here.
							assert(token_state!=TTokenState.TOKEN_LOOKUP);
							if (TRACE) TOUT.println("tokenIn(), looping after lookup");
							continue loop;
				case TOKEN_BODY:
							{
								if (TRACE) TOUT.println("tokenIn(), processing body");
								final int r = nextTokenBody();
								if (TRACE) TOUT.println("tokenIn()="+((r>=0)? ("\'"+(char)r+"'(0x"+Integer.toHexString(r)+")") : r)+" LEAVE");
								return r;
							}
				default: throw new AssertionError();
			}
		}
	};
	
	
	
	
	/** Finds next token character, updates state accordingly.
	@return what to return from {@link #tokenIn} 
	@throws IOException if failed
	*/
	private int hasNextTokenBody()throws IOException
	{
		if (TRACE) TOUT.println("hasNextTokenBody() ENTER");
		assert(token_state==TTokenState.TOKEN_BODY);
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax();
			if (stx==null)
			{
				if (TRACE) TOUT.println("nextTokenBody()=TOKEN_EOF, stx="+stx+" token_state="+token_state+" LEAVE");
				return TOKEN_EOF;
			};
			switch(stx)
			{
				case VOID: consume(); break;	//consume and move forwards
				case SIG_BEGIN:
				case SIG_END_BEGIN:
				case SIG_END:
						//place tokenizer at rest, do not consume the character
						token_state = TTokenState.TOKEN_AT_SIGNAL;		
						if (TRACE) TOUT.println("nextTokenBody()=TOKEN_SIGNAL, stx="+stx+" token_state="+token_state+" LEAVE");
						return TOKEN_SIGNAL;									
				case SEPARATOR:
						//terminated a token. Do not consume it
						if (TRACE) TOUT.println("nextTokenBody()=TOKEN_BOUNDARY, separator, stx="+stx+" token_state="+token_state+" LEAVE");
						return TOKEN_BOUNDARY;
				case NEXT_TOKEN:
						//terminated token, but also beginning of next token.
						//Oppositie to whend processing token, we do not consume it
						if (TRACE) TOUT.println("nextTokenBody()=TOKEN_BOUNDARY, next token, stx="+stx+" token_state="+token_state+" LEAVE");
						return TOKEN_BOUNDARY;
				case TOKEN: 
						//we have found a token char, NOT consume it and return.
						if (TRACE) TOUT.println("nextTokenBody()=0, token, stx="+stx+" token_state="+token_state+" LEAVE");
						return 0;
				default:
					// SIG_NAME_VOID, SIG_NAME, SIG_INDEX
					throw new EBrokenFormat(stx+" found while expecting token");
			}
		}
	};
	@Override protected final int hasUnreadToken()throws IOException
	{
		if (TRACE) TOUT.println("hasUnreadToken() ENTER");
		loop:
		for(;;) //because token lookup may toggle states to signal or body.
		{
			switch(token_state)
			{
				case TOKEN_AT_SIGNAL:
							if (TRACE) TOUT.println("hasUnreadToken()=TOKEN_SIGNAL, stuck at signal, LEAVE");
							return TOKEN_SIGNAL;
				case TOKEN_LOOKUP:
							//We moved to token boundary already. We need to check what would be next.
							if (!findNextToken())
							{
								if (TRACE) TOUT.println("hasUnreadToken()=TOKEN_EOF LEAVE");
								return TOKEN_EOF;
							};
							//Note: TOKEN_LOOKUP may be only due to eof, so we should not get in here.
							assert(token_state!=TTokenState.TOKEN_LOOKUP);
							if (TRACE) TOUT.println("hasUnreadToken(), looping");
							continue loop;
				case TOKEN_BODY:
							//Now we are in a body. We need to look forward but not consume
							{
							final int r = hasNextTokenBody();
							if (TRACE) TOUT.println("hasUnreadToken()="+r+" LEAVE");
							return r;
							}
				default: throw new AssertionError();
			}
		}
	};
	
	
	
	/* *********************************************************************
	
		ARegisteringStructReadFormat
		
	
	* *********************************************************************/
	/** Invoked by {@link #readSignalReg} when cursor is at signal character
	@return what return from caller
	@throws IOException if failed */
	private TSignalReg processSignal()throws IOException
	{
		if (TRACE) TOUT.println("processSignal->");
		assert(token_state == TTokenState.TOKEN_AT_SIGNAL);
		
		//determine what kind of signal?
		final TIntermediateSyntax stx= peekSyntax();
		assert(stx!=null);
		switch(stx)
		{
			case SIG_BEGIN: return processBeginSignal();
			case SIG_END_BEGIN: return processEndBeginSignal();
			case SIG_END: return processEndSignal();
			default: throw new AssertionError();
		}
	};
	/** Invoked by {@link #processSignal} when cursor is at {@link TIntermediateSyntax.SIG_END} character.
	@return what return from caller
	@throws IOException if failed */
	private TSignalReg processEndSignal()throws IOException
	{
		if (TRACE) TOUT.println("processEndSignal ENTER");
		assert(token_state == TTokenState.TOKEN_AT_SIGNAL);
		assert(peekSyntax().syntax() == TIntermediateSyntax.SIG_END);
		consume();
		token_state = TTokenState.TOKEN_LOOKUP;
		if (TRACE) TOUT.println("processEndSignal=TSignalReg.SIG_END, LEAVE");
		return TSignalReg.SIG_END;
	};
	/** Invoked by {@link #processSignal} when cursor is at {@link TIntermediateSyntax.SIG_BEGIN} character.
	@return what return from caller
	@throws IOException if failed */
	private TSignalReg processBeginSignal()throws IOException
	{
		if (TRACE) TOUT.println("processBeginSignal->");
		assert(token_state == TTokenState.TOKEN_AT_SIGNAL);
		assert(peekSyntax().syntax() == TIntermediateSyntax.SIG_BEGIN);
		return processXXXBeginSignal(
						TSignalReg.SIG_BEGIN_DIRECT,
						TSignalReg.SIG_BEGIN_AND_REGISTER,
						TSignalReg.SIG_BEGIN_REGISTERED
						);
	};
	/** Invoked by {@link #processSignal} when cursor is at {@link TIntermediateSyntax.SIG_END_BEGIN} character.
	@return what return from caller
	@throws IOException if failed */
	private TSignalReg processEndBeginSignal()throws IOException
	{
		if (TRACE) TOUT.println("processEndBeginSignal->");
		assert(token_state == TTokenState.TOKEN_AT_SIGNAL);
		assert(peekSyntax().syntax() == TIntermediateSyntax.SIG_END_BEGIN);
		return processXXXBeginSignal(
						TSignalReg.SIG_END_BEGIN_DIRECT,
						TSignalReg.SIG_END_BEGIN_AND_REGISTER,
						TSignalReg.SIG_END_BEGIN_REGISTERED
						);
	};
	
						
	/** Parameterized processing of begin/end-begin signal
	@param direct if detected direct operation
	@param register if detected register operation
	@param registered if detected registered operation
	@return what to return from caller
	@throws IOException if failed 
	*/
	private TSignalReg processXXXBeginSignal(TSignalReg direct, TSignalReg register, TSignalReg registered)throws IOException
	{
		if (TRACE) TOUT.println("processXXXBeginSignal() ENTER");
		//wipe buffers
		this.pick_last_signal_index=-1;
		this.pick_last_signal_reg_name = null;
		this.name_and_index_buffer.setLength(0);
		//prepare variables
		String collected_name = null;	//we will collect name here.
		int collected_index = -1;		//we collect index here, non negative
		boolean collected_index_directly=false;
		//now process
		consume();//consume leading character
		loop:
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax();
			if (TRACE) TOUT.println("processXXXBeginSignal() stx="+stx);
			if (stx==null)
			{
				break loop;
			};
			switch(stx)
			{
				case VOID:
						if (TRACE) TOUT.println("processXXXBeginSignal(), dropping void");
						consume(); break;
				case SIG_NAME_VOID:
				case SIG_NAME:
						if (TRACE) TOUT.println("processXXXBeginSignal(), collecting name");
						if (collected_name!=null) throw new EBrokenFormat("Signal name already collected");
						collectSignalName(name_and_index_buffer);
						collected_name = name_and_index_buffer.toString();
						name_and_index_buffer.setLength(0);	//wipe.
						break;
				case SIG_INDEX:
						if (TRACE) TOUT.println("processXXXBeginSignal(), collecting index");
						if (collected_index!=-1) throw new EBrokenFormat("Signal index already collected");
						collected_index_directly = true;
						collected_index = collectSignalIndex(name_and_index_buffer);
						name_and_index_buffer.setLength(0);	//wipe.
						break;
				case SIG_ORDER:
						if (TRACE) TOUT.println("processXXXBeginSignal(), collecting ordered index mode");						
						if (collected_index!=-1) throw new EBrokenFormat("Signal index already collected");
						consume(); //consume that character.
						collected_index = this.ordered_registration_counter+1;
						collected_index_directly = false;
						name_and_index_buffer.setLength(0);	//wipe.
						break;
				default:
						if (TRACE) TOUT.println("processXXXBeginSignal(), got "+stx);
						break loop;
			}
		}
		if (TRACE) TOUT.println("processXXXBeginSignal(), processing signal definition");
		//we are in token reset tokenizer (but not peeker!)
		token_state = TTokenState.TOKEN_LOOKUP;
		//now decide on what to do?
		if (collected_index==-1)
		{
			this.pick_last_signal_index=-1;
			this.pick_last_signal_reg_name= (collected_name==null) ? "" : collected_name;
			if (TRACE) TOUT.println("processXXXBeginSignal()="+direct+", LEAVE");
			return direct;
		}else
		if (collected_name==null)
		{
			if (!collected_index_directly) throw new EBrokenFormat("Can't use SIG_ORDER for re-use of registered name"); 
			this.pick_last_signal_index=collected_index;
			this.pick_last_signal_reg_name=null;
			if (TRACE) TOUT.println("processXXXBeginSignal()="+registered+", LEAVE");
			return registered;
		}else
		{
			if (!collected_index_directly)
			{
				//by order registration
				if (was_index_based_registration)
					throw new EBrokenFormat("order based registration after index based");
				this.ordered_registration_counter++;
				assert(this.ordered_registration_counter == collected_index);
			}else
			{
				//by index.
				if (this.ordered_registration_counter!=-1)
					throw new EBrokenFormat("index based registration after order based");
				this.was_index_based_registration=true;
			};
			this.pick_last_signal_index=collected_index;
			this.pick_last_signal_reg_name=collected_name;
			if (TRACE) TOUT.println("processXXXBeginSignal()="+register+", LEAVE");
			return register;
		}
	};
	/** Collects signal name to specified buffer
	@param where_to a buffer, will be wiped
	@throws IOException if failed
	@throws EFormatBoundaryExceeded if name is too long
	*/
	private void collectSignalName(StringBuilder where_to)throws IOException
	{
		if (TRACE) TOUT.println("collectSignalName() ENTER");
		where_to.setLength(0);	//wipe for sure.
		loop:
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax();
			if (stx==null)
			{
				if (where_to.length()==0)
						throw new EUnexpectedEof();
				if (TRACE) TOUT.println("collectSignalName(), eof, but got data");
				break loop;
			};
			switch(stx)
			{
				case VOID: 	
				case SIG_NAME_VOID: consume(); break;
				case SIG_NAME:
						if (where_to.length()>=getMaxSignalNameLength()) throw new EFormatBoundaryExceeded("Signal name \""+where_to.toString()+"\" too long");
						where_to.append((char)peekChar());						
						consume();
						break;
				default:
						//not consume, terminate collection.
						break loop;						
			}
		}
		if (TRACE) TOUT.println("collectSignalName()=\""+where_to.toString()+"\" LEAVE");
	};
	/** Collects signal index to specified buffer, parses it to int
	@param where_to a buffer, will be wiped
	@return parsed value
	@throws IOException if failed
	@throws EBrokenFormat if index is non-parsable or negative
	@throws EFormatBoundaryExceeded if index is too long
	*/
	private int collectSignalIndex(StringBuilder where_to)throws IOException
	{
		if (TRACE) TOUT.println("collectSignalIndex() ENTER");
		where_to.setLength(0);	//wipe for sure.
		loop:
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax();
			if (stx==null)
			{
				if (where_to.length()==0)
						throw new EUnexpectedEof();
				if (TRACE) TOUT.println("collectSignalIndex(), eof, but got data");
				break loop;
			};
			switch(stx)
			{
				case VOID: 	
				case SIG_INDEX:
						if (where_to.length()>=16) throw new EFormatBoundaryExceeded("Signal index \""+where_to.toString()+"\" too long");
						where_to.append((char)peekChar());
						consume();
						break;
				default:
						//not consume, terminate collection, parse
						break loop;						
			}
		}
		//compute effect.
		{
			String s= where_to.toString();
			if (TRACE) TOUT.println("collectSignalIndex(), decoding \""+s+"\"");
			try{
				int v = Integer.decode(s);
				if (v<0) throw new EBrokenFormat("The signal index "+v+" is negative");
				if (TRACE) TOUT.println("collectSignalIndex()="+v+" LEAVE");
				return v;
			}catch(NumberFormatException ex)
			{
				throw new EBrokenFormat("Could not parse \""+s+"\" to number", ex); 
			}
		}
	};
	
	@Override protected TSignalReg readSignalReg()throws IOException
	{
		//Theoretically we just could do a dumb skip to SIG_BEGIN/SIG_END_BEGIN/SIG_END
		//And we can do it safely, because the subclass syntax processor must validate
		//if it's own syntax is correct. Best is however to pass it through token processor
		//what will keep it's state consistent.
		if (TRACE) TOUT.println("readSignalReg() ENTER");
		for(;;)
		{
			switch(tokenIn())
			{
				case TOKEN_SIGNAL:
						{
							final TSignalReg r = processSignal();
							if (TRACE) TOUT.println("readSignalReg()="+r+" LEAVE");
							return r;
						}
				case TOKEN_EOF: throw new EUnexpectedEof();
				default:
						//just continue looping.
						if (TRACE) TOUT.println("readSignalReg(), skipping");
						;
			}
		}
	};
	@Override protected int pickLastSignalIndex()
	{
		final int r = pick_last_signal_index;
		pick_last_signal_index = -1;
		return r;
	};
	@Override protected String pickLastSignalRegName()
	{
		final String s = pick_last_signal_reg_name;
		pick_last_signal_reg_name = null;
		return s;
	};
};
