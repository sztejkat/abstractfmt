package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Reader;

/**
	Uses low level text parsing over a known set of syntax
	elements to process all necessary tokens.
	
	<h1>Syntax definition</h1>
	The syntax is defined by {@link ATxtReadFormat1.ISyntax} 
	which transforms Your specific syntax to syntax known
	by this class which is {@link ATxtReadFormat1.TIntermediateSyntax}
	
*/	
public abstract class ATxtReadFormat1<TSyntax extends ATxtReadFormat1.ISyntax,
									  TSyntaxState extends Object> 
									  extends ATxtReadFormat0
{
			/** A contract which allows to provide a kind of "enum"
			extension. */
			public interface ISyntax
			{
				/**	Returns {@link ATxtReadFormat1} syntax element 
				which is represented by this syntax element.
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
				</ul>
				The collection stops on any other non {@link #VOID} character.
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
				and text will be parsed by <code>Integer.decode</code>. The index cannot be negative. */
				SIG_INDEX,
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
	SIG_TOKEN(a),SIG_TOKEN(b),SIG_SEPARATOR( ),SIG_SEPARATOR( ),SIG_TOKEN(d), SIG_TOKEN(e)
				</pre>
				or:
				<pre>
	SIG_TOKEN(a),SIG_TOKEN(b),SIG_SEPARATOR( ),SIG_VOID,SIG_TOKEN(d), SIG_TOKEN(e)
				</pre>
				<p>
				If we define that white-spaces are not parts of tokens and , is a delimiter
				and ,, defines an empty token we do:
				<pre>
				ab , de
				</pre>
				emits:
				<pre>
	SIG_TOKEN(a),SIG_TOKEN(b),SIG_VOID,SIG_NEXT_TOKEN,SIG_VOID,SIG_TOKEN(d),SIG_TOKEN(e)
				</pre>			
				*/
				SIG_SEPARATOR,
				/** A token or signal terminator. This character do not belong to any
				syntax element and do terminate a previous syntax element 
				and starts new token. 
				*/ 				
				SIG_NEXT_TOKEN,
				/** A token character. This character do belong to a token, and if
				token was not started, starts it and becomes a part of it. */
				SIG_TOKEN;
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
			
	/* *********************************************************************
	
		Construction
		
	
	* *********************************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
	@param token_size_limit non-zero positive, a maximum number of characters which constitutes 
			a primitive element token, excluding string tokens. Basically a maximum
			number of characters which do constitute a primitive numeric value.
	*/
	protected ATxtReadFormat1(int name_registry_capacity,int token_size_limit)
	{
		super(name_registry_capacity,token_size_limit);
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
	@return of {@link #getNextSyntaxElement} */
	private ISyntax peekSyntax()throws IOException
	{
		validatePeek();
		return getNextSyntaxElement();
	};
	/** Consumes peeked character,
	makes sure that nearest {@link #validatePeek} will call {@link #nextChar} */
	private void consume()
	{
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
		assert(token_state==TTokenState.TOKEN_LOOKUP);
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax().syntax();
			if (stx==null) return false;
			switch(stx)
			{
				case VOID: consume(); break;	//consume and move forwards
				case SIG_BEGIN:
				case SIG_END_BEGIN:
				case SIG_END:
						//place tokenizer at rest, do not consume the character
						token_state = TTokenState.TOKEN_AT_SIGNAL;
						return true;									
				case SIG_SEPARATOR:	consume(); break; //as void				
				case SIG_NEXT_TOKEN:
						//we have found a token, but char does not belong to
						//it. Consume it and move to token collection.
						consume();
						token_state = TTokenState.TOKEN_BODY;
						return true;
				case SIG_TOKEN: 
						//we have found a token, do NOT consume it, move to token collection
						token_state = TTokenState.TOKEN_BODY;
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
		assert(token_state==TTokenState.TOKEN_BODY);
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax().syntax();
			if (stx==null) return TOKEN_EOF;
			switch(stx)
			{
				case VOID: consume(); break;	//consume and move forwards
				case SIG_BEGIN:
				case SIG_END_BEGIN:
				case SIG_END:
						//place tokenizer at rest, do not consume the character
						token_state = TTokenState.TOKEN_AT_SIGNAL;						
						return TOKEN_SIGNAL;									
				case SIG_SEPARATOR:
						//terminated a token. Do consume it and move at lookup. 
						consume();
						token_state = TTokenState.TOKEN_LOOKUP;
						return TOKEN_BOUNDARY;
				case SIG_NEXT_TOKEN:
						//terminated token, but also beginning of next token.
						consume(); //must be consumed, so that we start serving next token.	
						//Do NOT change token lookup.
						return TOKEN_BOUNDARY;
				case SIG_TOKEN: 
						//we have found a token char, consume it and return.
						{
							int c = peekChar();
							assert((c>=0)&&(c<=0xFFFF));
							consume();
							return c;
						}
				default:
					// SIG_NAME_VOID, SIG_NAME, SIG_INDEX
					throw new EBrokenFormat(stx+" found while expecting token");
			}
		}
	};
	@Override protected int tokenIn()throws IOException
	{
		loop:
		for(;;) //because token lookup may toggle states to signal or body.
		{
			switch(token_state)
			{
				case TOKEN_AT_SIGNAL: return TOKEN_SIGNAL;
				case TOKEN_LOOKUP:
							if (!findNextToken()) return TOKEN_EOF;
							//Note: TOKEN_LOOKUP may be only due to eof, so we should not get in here.
							assert(token_state!=TTokenState.TOKEN_LOOKUP);
							continue loop;
				case TOKEN_BODY:
							return nextTokenBody();
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
		assert(token_state==TTokenState.TOKEN_BODY);
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax().syntax();
			if (stx==null) return TOKEN_EOF;
			switch(stx)
			{
				case VOID: consume(); break;	//consume and move forwards
				case SIG_BEGIN:
				case SIG_END_BEGIN:
				case SIG_END:
						//place tokenizer at rest, do not consume the character
						token_state = TTokenState.TOKEN_AT_SIGNAL;						
						return TOKEN_SIGNAL;									
				case SIG_SEPARATOR:
						//terminated a token. Do not consume it
						return TOKEN_BOUNDARY;
				case SIG_NEXT_TOKEN:
						//terminated token, but also beginning of next token.
						//Oppositie to whend processing token, we do not consume it
						return TOKEN_BOUNDARY;
				case SIG_TOKEN: 
						//we have found a token char, NOT consume it and return.
						return 0;
				default:
					// SIG_NAME_VOID, SIG_NAME, SIG_INDEX
					throw new EBrokenFormat(stx+" found while expecting token");
			}
		}
	};
	@Override protected int hasUnreadToken()throws IOException
	{
		loop:
		for(;;) //because token lookup may toggle states to signal or body.
		{
			switch(token_state)
			{
				case TOKEN_AT_SIGNAL: return TOKEN_SIGNAL;
				case TOKEN_LOOKUP:
							//We moved to token boundary already. We need to check what would be next.
							if (!findNextToken()) return TOKEN_EOF;
							//Note: TOKEN_LOOKUP may be only due to eof, so we should not get in here.
							assert(token_state!=TTokenState.TOKEN_LOOKUP);
							continue loop;
				case TOKEN_BODY:
							//Now we are in a body. We need to look forward but not consume
							return hasNextTokenBody();
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
		assert(token_state == TTokenState.TOKEN_AT_SIGNAL);
		
		//determine what kind of signal?
		final TIntermediateSyntax stx= peekSyntax().syntax();
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
		assert(token_state == TTokenState.TOKEN_AT_SIGNAL);
		assert(peekSyntax().syntax() == TIntermediateSyntax.SIG_END);
		consume();
		token_state = TTokenState.TOKEN_LOOKUP;
		return TSignalReg.SIG_END;
	};
	/** Invoked by {@link #processSignal} when cursor is at {@link TIntermediateSyntax.SIG_BEGIN} character.
	@return what return from caller
	@throws IOException if failed */
	private TSignalReg processBeginSignal()throws IOException
	{
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
		//wipe buffers
		this.pick_last_signal_index=-1;
		this.pick_last_signal_reg_name = null;
		this.name_and_index_buffer.setLength(0);
		//prepare variables
		String collected_name = null;	//we will collect name here.
		int collected_index = -1;		//we collect index here, non negative
		//now process
		consume();//consume leading character
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax().syntax();
			if (stx==null) throw new EUnexpectedEof();
			switch(stx)
			{
				case VOID: 	consume(); break;
				case SIG_NAME_VOID:
				case SIG_NAME:
						if (collected_name!=null) throw new EBrokenFormat("Signal name already collected");
						collectSignalName(name_and_index_buffer);
						collected_name = name_and_index_buffer.toString();
						name_and_index_buffer.setLength(0);	//wipe.
						break;
				case SIG_INDEX:
						if (collected_index!=-1) throw new EBrokenFormat("Signal index already collected");
						collected_index = collectSignalIndex(name_and_index_buffer);
						name_and_index_buffer.setLength(0);	//wipe.
						break;
				default:
						//we are in token reset tokenizer (but not peeker!)
						token_state = TTokenState.TOKEN_LOOKUP;
						//now decide on what to do?
						if (collected_index==-1)
						{
							this.pick_last_signal_index=-1;
							this.pick_last_signal_reg_name= (collected_name==null) ? "" : collected_name;
							return direct;
						}else
						if (collected_name==null)
						{
							this.pick_last_signal_index=collected_index;
							this.pick_last_signal_reg_name=null;
							return registered;
						}else
						{
							this.pick_last_signal_index=collected_index;
							this.pick_last_signal_reg_name=collected_name;
							return register;
						}
			}
		}
	};
	/** Collects signal name to specified buffer
	@param where_to a buffer, will be wiped
	@throws IOException if failed
	@throws EFormatBoundaryExceeded if name is too long
	*/
	private void collectSignalName(StringBuilder where_to)throws IOException
	{
		where_to.setLength(0);	//wipe for sure.
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax().syntax();
			if (stx==null) throw new EUnexpectedEof();
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
						return;
			}
		}
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
		where_to.setLength(0);	//wipe for sure.
		for(;;)
		{
			final TIntermediateSyntax stx= peekSyntax().syntax();
			if (stx==null) throw new EUnexpectedEof();
			switch(stx)
			{
				case VOID: 	
				case SIG_NAME_VOID: consume(); break;
				case SIG_NAME:
						if (where_to.length()>=16) throw new EFormatBoundaryExceeded("Signal index \""+where_to.toString()+"\" too long");
						where_to.append((char)peekChar());
						consume();
						break;
				default:
						//not consume, terminate collection, parse
						{
							String s= where_to.toString();
							try{
								int v = Integer.decode(s);
								if (v<0) throw new EBrokenFormat("The signal index "+v+" is negative");
								return v;
							}catch(NumberFormatException ex)
							{
								throw new EBrokenFormat("Could not parse \""+s+"\" to number", ex); 
							}
						}
			}
		}
	};
	
	@Override protected TSignalReg readSignalReg()throws IOException
	{
		//Theoretically we just could do a dumb skip to SIG_BEGIN/SIG_END_BEGIN/SIG_END
		//And we can do it safely, because the subclass syntax processor must validate
		//if it's own syntax is correct.
		//If we however skip the token processor the we may have problems with recovering
		//from eof since token machine will be for sure broken.
		for(;;)
		{
			switch(tokenIn())
			{
				case TOKEN_SIGNAL:
						return processSignal();
				case TOKEN_EOF: throw new EUnexpectedEof();
				default:
						//just continue looping.
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
