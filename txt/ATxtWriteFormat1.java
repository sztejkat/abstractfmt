package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	Adds additional level of support for sequencing tokens
	used to represent privimitve elements.
	
	<h1>Tokens sequencing</h1>
	This class provides support for "list" token format,
	where a structure of signal can be described as
	a simplified regular expression:
	<pre>
	  signal token (token_separator token)* signal
	</pre>
	This class uses following methods for following goals:
	<ul>
		<li>{@link #outBeginSignalSeparator}/{@link #outEndSignalSeparator}
		to allow injection of a separator between signal and token, if token is present;</li>
		<li>{@link #outTokenSeparator} to allow injection of separators between tokens;</li>
		<li>{@link #outTokenToBeginSignalSeparator},{@link #outTokenToEndSignalSeparator}
		to allow injection of separators between tokens and signals;</li>
		<li>{@link #openPlainTokenImpl},{@link #openStringTokenImpl},{@link #openSingleCharTokenImpl},
		{@link #openBlockCharTokenImpl} to maintain token state;</li> 
		<li>{@link #outPlainToken},{@link #outStringToken},{@link #outSingleCharToken},
		{@link #outBlockCharToken} to allow separate production of four kinds of tokens;</li>
		<li>{@link #closePlainTokenImpl},{@link #closeStringTokenImpl},{@link #closeSingleCharTokenImpl},
		{@link #closeBlockCharTokenImpl} to maintain token state;</li>
	</ul>
	
	<h2>String tokens</h2>
	This class provide a mechanism which is "stitching" 
	multiple ajacent string tokens into one string token.
	<p>
	If this is not desired see {@link #closeStringToken} how to disable it.
	
	<h2>Single char and block char tokens</h2>
	This class by default redirects all methods related to those
	tokens to apropriate methods for string token, thous
	the {@link #openBlockCharTokenImpl}/{@link #closeBlockCharTokenImpl}
	and {@link #openSingleCharTokenImpl}/{@link #closeSingleCharTokenImpl}
	are never used.
	<p>
	This means that by default there is no difference in behavior
	between elementary {@link #writeChar} and block operation {@link #writeCharBlock},
	{@link #writeString}. 
	<p>
	If this is not desired see {@link #openBlockCharToken}
	and {@link #openSingleCharToken} how to enable separate non-stitching
	behavior for any of them.
*/
public abstract class ATxtWriteFormat1 extends ATxtWriteFormat0
{
			private enum TTokenState
			{
					/** Once file was opened. 
					Indicates that no token seprator is necessary.
					*/
					NOTHING,
					/** Once "begin" signal was written or begun to be written.
					Indicates that no token seprator is necessary,
					however a signal separator is necessary.
					*/
					BEGIN_SIGNAL,
					/** Once "end" signal was written or begun to be written.
					Indicates that no token seprator is necessary,
					however a signal separator is necessary.
					*/
					END_SIGNAL,
					/** Once token of any type was written
					and is closed. Indicates the fact that
					token sperator should be written before
					next token can be opened */
					AFTER_TOKEN,
					/** A plain token is open and content may
					be written */
					PLAIN_TOKEN,
					/** A string token is open and content may be
					written */
					STRING_TOKEN,
					/** A single char token is open and content may be
					written */
					SINGLE_CHAR_TOKEN,
					/** A block char token is open and content may be
					written */
					BLOCK_CHAR_TOKEN,
					/** A string token was closed by user API, 
					but due to possible stitching this operation
					is post-poned and not passed to subclass 
					API yet. */
					STRING_TOKEN_STITCHING
			}
				private TTokenState token_state = TTokenState.NOTHING;
				
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates
	@param name_registry_capacity as {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat}
			do specify.
	*/
	protected ATxtWriteFormat1(int name_registry_capacity)
	{
		super(name_registry_capacity); 
	};
	/* **********************************************************
	
			Services required from subclasses
	
	***********************************************************/
	/* ---------------------------------------------------------
				Token related
	---------------------------------------------------------*/
	/** Invoken when class detects that "begin" signal was written and
	token is to be opened. Once this method returns
	the {@link #openPlainTokenImpl},{@link #openStringTokenImpl},
	{@link #openSingleCharTokenImpl} or {@link #openBlockCharTokenImpl}
	is invoked. 
	@throws IOException if failed.
	*/
	protected abstract void outBeginSignalSeparator()throws IOException;
	/** Invoken when class detects that "end" signal was written and
	token is to be opened. Once this method returns
	the {@link #openPlainTokenImpl},{@link #openStringTokenImpl},
	{@link #openSingleCharTokenImpl} or {@link #openBlockCharTokenImpl}
	is invoked. 
	@throws IOException if failed.
	*/
	protected abstract void outEndSignalSeparator()throws IOException;
	/** Invoked when class detects that "begin" signal is to be written
	after some tokens were written. 
	@throws IOException if failed */
	protected abstract void outTokenToBeginSignalSeparator()throws IOException;
	/** Invoked when class detects that "end" signal is to be written
	after some tokens were written. 
	@throws IOException if failed */
	protected abstract void outTokenToEndSignalSeparator()throws IOException;
	/** Invoken when class detects that token was written and
	next token is to be opened. Once this method returns
	the {@link #openPlainTokenImpl} or {@link #openStringTokenImpl}
	is invoked
	@throws IOException if failed.
	*/
	protected abstract void outTokenSeparator()throws IOException;
	/** Invoked by {@link #openPlainToken} when it decides that
	actuall opening should take place 
	@throws IOException if failed.
	*/
	protected abstract void openPlainTokenImpl()throws IOException;
	/** Invoked by {@link #openStringToken} when it decides that
	actuall opening should take place 
	@throws IOException if failed.
	*/
	protected abstract void openStringTokenImpl()throws IOException;
	/** Invoked by {@link #closePlainToken} when it decides that
	actuall closing should take place 
	@throws IOException if failed.
	*/
	protected abstract void closePlainTokenImpl()throws IOException;
	/** Invoked by {@link #closeStringToken} when it decides that
	actuall closing should take place 
	@throws IOException if failed.
	*/
	protected abstract void closeStringTokenImpl()throws IOException;
	
	/* ---------------------------------------------------------
			Tunable services.
	------------------------------------------------------------*/
	/** Invoked by {@link #defaultOpenBlockCharToken},
	defaults to {@link #openStringTokenImpl}. 
	@throws IOException if failed. */
	protected void openBlockCharTokenImpl()throws IOException{ openStringTokenImpl(); };
	/** Invoked by {@link #defaultCloseBlockCharToken},
	defaults to {@link #closeStringTokenImpl}.
	@throws IOException if failed. */
	protected void closeBlockCharTokenImpl()throws IOException{ closeStringTokenImpl(); };
	
	/** Invoked by {@link #defaultOpenSingleCharToken},
	defaults to {@link #openStringTokenImpl}.
	@throws IOException if failed. */
	protected void openSingleCharTokenImpl()throws IOException{ openStringTokenImpl(); };
	/** Invoked by {@link #defaultCloseSingleCharToken},
	defaults to {@link #closeStringTokenImpl}.
	@throws IOException if failed.*/
	protected void closeSingleCharTokenImpl()throws IOException{ closeStringTokenImpl(); };
	/* **********************************************************
	
			ATxtWriteFromat0
			
	***********************************************************/
	/** {@inheritDoc}
	<p>
	Subclasses which wishes to treat block character tokens differently should
	override this and other methods with:
	<pre>
	&#64;Override protected void openBlockCharToken()throws IOException
	{
		{@link #defaultOpenBlockCharToken()};
	}
	&#64;Override protected void closeBlockCharToken()throws IOException
	{
		{@link #defaultCloseBlockCharToken()};
	}
	&#64;Override protected void {@link #openBlockCharTokenImpl()}throws IOException{...
	&#64;Override protected void {@link #closeBlockCharTokenImpl()}throws IOException{...
	&#64;Override protected void {@link #outBlockCharToken(char c)}throws IOException{...
	</pre>
	*/
	@Override protected void openBlockCharToken()throws IOException{ openStringToken(); };
	/** This decides if	token separator is necessary and invokes
	{@link #outTokenSeparator} and {@link #openBlockCharTokenImpl}.
	If string token stitching is in progress will terminate it.
	<p>
	Not used unless {@link #openBlockCharToken} is overriden.
	<p>
	@throws AssertionError if in incorrect state
	@throws IOException if failed.
	@see #openBlockCharToken
	*/
	protected final void defaultOpenBlockCharToken()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	//no separator necessary at the beginning of a file
						openBlockCharTokenImpl();
						break;
			case BEGIN_SIGNAL:	//no token separator necessary after signal, but signal is a must
						outBeginSignalSeparator();
						openBlockCharTokenImpl();
						break;
			case END_SIGNAL:	//no token separator necessary after signal, but signal is a must
						outEndSignalSeparator();
						openBlockCharTokenImpl();
						break;
			case AFTER_TOKEN:
							//separator is necessary
						outTokenSeparator();
						openBlockCharTokenImpl();
						break;
			case PLAIN_TOKEN:		
			case SINGLE_CHAR_TOKEN:
			case BLOCK_CHAR_TOKEN:
			case STRING_TOKEN:
						//both are assertion errors.
						throw new AssertionError("token_state="+token_state);
			case STRING_TOKEN_STITCHING:
						//in this case we need to actually close it.
						closeStringTokenImpl();
						outTokenSeparator();
						openBlockCharTokenImpl();
						break;
		};
		token_state = TTokenState.BLOCK_CHAR_TOKEN;
	};
	/** {@inheritDoc}
	<p>
	Subclasses which wishes to treat single character tokens differently should
	override it with:
	<pre>
	&#64;Override protected void openSingleCharToken()throws IOException
	{
		{@link #defaultOpenSingleCharToken()};
	}
	&#64;Override protected void closeSingleCharToken()throws IOException
	{
		{@link #defaultCloseSingleCharToken()};
	}
	&#64;Override protected void {@link #openSingleCharTokenImpl()}throws IOException{...
	&#64;Override protected void {@link #closeSingleCharTokenImpl()}throws IOException{...
	&#64;Override protected void {@link #outSingleCharToken(char c)}throws IOException{...
	</pre>
	*/
	@Override protected void openSingleCharToken()throws IOException{ openStringToken(); };
	/** This decides if	token separator is necessary and invokes
	{@link #outTokenSeparator} and {@link #openSingleCharTokenImpl}.
	If string token stitching is in progress will terminate it.
	<p>
	Not used unless {@link #openBlockCharToken} is overriden.
	@throws AssertionError if in incorrect state
	@throws IOException if failed.
	@see #openSingleCharToken
	*/
	protected final void defaultOpenSingleCharToken()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	//no separator necessary at the beginning of a file
						openSingleCharTokenImpl();
						break;
			case BEGIN_SIGNAL:	//no token separator necessary after signal, but signal is a must
						outBeginSignalSeparator();
						openSingleCharTokenImpl();
						break;
			case END_SIGNAL:	//no token separator necessary after signal, but signal is a must
						outEndSignalSeparator();
						openSingleCharTokenImpl();
						break;
			case AFTER_TOKEN:
							//separator is necessary
						outTokenSeparator();
						openSingleCharTokenImpl();
						break;
			case PLAIN_TOKEN:		
			case SINGLE_CHAR_TOKEN:
			case BLOCK_CHAR_TOKEN:
			case STRING_TOKEN:
						//both are assertion errors.
						throw new AssertionError("token_state="+token_state);
			case STRING_TOKEN_STITCHING:
						//in this case we need to actually close it.
						closeStringTokenImpl();
						outTokenSeparator();
						openSingleCharTokenImpl();
						break;
		};
		token_state = TTokenState.SINGLE_CHAR_TOKEN;
	};
	
	/** This decides if	token separator is necessary and invokes
	{@link #outTokenSeparator} and {@link #openPlainTokenImpl}.
	If string token stitching is in progress will terminate it.
	@throws AssertionError if in incorrect state
	@throws IOException if failed.
	*/
	@Override protected final void openPlainToken()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	//no separator necessary at the beginning of a file
						openPlainTokenImpl();
						break;
			case BEGIN_SIGNAL:	//no token separator necessary after signal, but signal is a must
						outBeginSignalSeparator();
						openPlainTokenImpl();
						break;
			case END_SIGNAL:	//no token separator necessary after signal, but signal is a must
						outEndSignalSeparator();
						openPlainTokenImpl();
						break;
			case AFTER_TOKEN:
							//separator is necessary
						outTokenSeparator();
						openPlainTokenImpl();
						break;
			case PLAIN_TOKEN:		
			case SINGLE_CHAR_TOKEN:
			case BLOCK_CHAR_TOKEN:
			case STRING_TOKEN:
						//both are assertion errors.
						throw new AssertionError("token_state="+token_state);
						
			case STRING_TOKEN_STITCHING:
						//in this case we need to actually close it.
						closeStringTokenImpl();
						outTokenSeparator();
						openPlainTokenImpl();
						break;
		};
		token_state = TTokenState.PLAIN_TOKEN;
	};
	/** This decides if	token separator is necessary and invokes
	{@link #outTokenSeparator} and {@link #openStringTokenImpl}.
	<p>
	This method do perform "stitching" of multiplie string tokens
	into one large token. If this is not desired see {@link #closeStringToken}.
	@throws AssertionError if in incorrect state
	*/
	@Override protected final void openStringToken()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	//no separator necessary at the beginning of a file
						openStringTokenImpl();
						break;
			case BEGIN_SIGNAL:	//no token separator necessary after signal, but signal is a must
						outBeginSignalSeparator();
						openStringTokenImpl();
						break;
			case END_SIGNAL:	//no token separator necessary after signal, but signal is a must
						outEndSignalSeparator();
						openStringTokenImpl();
						break;
			case AFTER_TOKEN:
							//separator is necessary
						outTokenSeparator();
						openStringTokenImpl();
						break;
			case PLAIN_TOKEN:
			case SINGLE_CHAR_TOKEN:
			case BLOCK_CHAR_TOKEN:
			case STRING_TOKEN:
						//both are assertion errors.
						throw new AssertionError("token_state="+token_state);
						
			case STRING_TOKEN_STITCHING:
						//in this case we just continue the token
						//without any calls to lower layer as they were delayed.
						break;
		};
		token_state = TTokenState.STRING_TOKEN;
	};
	/** Terminates string token in progess, by default delaying the
	actuall call to {@link #closeStringTokenImpl} till it is actually
	necessary, thous performs "stitching" string tokens.
	<p>
	If it is not necessary override this method with:
	<pre>
	protected void closeStringToken()throws IOException{ closeStringToken_no_stitching(); }; 
	</pre>
	@see #closeStringToken_stitching
	@see #closeStringToken_no_stitching
	@throws AssertionError if not inside string token
	*/
	@Override protected void closeStringToken()throws IOException
	{
		closeStringToken_stitching();
	};
	/** "Stitching" variant of {@link #closeStringToken}
	@throws IOException if failed.
	*/
	protected final void closeStringToken_stitching()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	
			case BEGIN_SIGNAL:
			case END_SIGNAL:
			case AFTER_TOKEN:
			case PLAIN_TOKEN:
			case SINGLE_CHAR_TOKEN:
			case BLOCK_CHAR_TOKEN:
						//all are assertion errors.
						throw new AssertionError("token_state="+token_state);						
			case STRING_TOKEN:
						//just indicate it needs some work.
						token_state = TTokenState.STRING_TOKEN_STITCHING;
						break;
			case STRING_TOKEN_STITCHING:
						throw new AssertionError("token_state="+token_state);
		}
	};
	/** Non-"stitching" variant of {@link #closeStringToken} 
	@throws IOException if failed.
	*/
	protected final void closeStringToken_no_stitching()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	
			case BEGIN_SIGNAL:
			case END_SIGNAL:
			case AFTER_TOKEN:
			case PLAIN_TOKEN:
			case SINGLE_CHAR_TOKEN:
			case BLOCK_CHAR_TOKEN:				
						//all are assertion errors.
						throw new AssertionError("token_state="+token_state);						
			case STRING_TOKEN:
						//do it.
						token_state = TTokenState.AFTER_TOKEN;
						closeStringTokenImpl();
						break;
			case STRING_TOKEN_STITCHING:
						throw new AssertionError("token_state="+token_state);
		}
	};
	/**
	@throws AssertionError if not during plain token
	*/
	@Override protected final void closePlainToken()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	
			case BEGIN_SIGNAL:
			case END_SIGNAL:
			case AFTER_TOKEN:
			case SINGLE_CHAR_TOKEN:
			case BLOCK_CHAR_TOKEN:	
						//all are assertion errors.
						throw new AssertionError("token_state="+token_state);
			case PLAIN_TOKEN:
						//do it. No stitching for plain tokens.
						token_state = TTokenState.AFTER_TOKEN;
						closePlainTokenImpl();
						break;						
			case STRING_TOKEN:						
			case STRING_TOKEN_STITCHING:
						throw new AssertionError("token_state="+token_state);
		}
	};
	
	/** {@inheritDoc}
	<p>
	See notes in {@link #openBlockCharToken}.
	*/
	@Override protected void closeBlockCharToken()throws IOException{ closeStringToken(); };
	/** Implements non-stitching behavior for block character tokens.
	Not used unless {@link #closeBlockCharToken} is overriden.
	@throws AssertionError if not during char token
	@throws IOException if failed. 
	*/
	protected final void defaultCloseBlockCharToken()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	
			case BEGIN_SIGNAL:
			case END_SIGNAL:
			case AFTER_TOKEN:
			case SINGLE_CHAR_TOKEN:
			case PLAIN_TOKEN:
						//all are assertion errors.
						throw new AssertionError("token_state="+token_state);
			case BLOCK_CHAR_TOKEN:
						//do it. No stitching for plain tokens.
						token_state = TTokenState.AFTER_TOKEN;
						closeBlockCharTokenImpl();
						break;						
			case STRING_TOKEN:						
			case STRING_TOKEN_STITCHING:
						throw new AssertionError("token_state="+token_state);
		}
	}
	/** {@inheritDoc}
	<p>
	See notes in {@link #openSingleCharToken}.
	*/
	@Override protected void closeSingleCharToken()throws IOException{ closeStringToken(); };
	/** Implements non-stitching behavior for single character tokens.
	Not used unless {@link #closeSingleCharToken} is overriden.
	@throws AssertionError if not during char token
	@throws IOException if failed. 
	*/
	protected final void defaultCloseSingleCharToken()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	
			case BEGIN_SIGNAL:
			case END_SIGNAL:
			case AFTER_TOKEN:
			case BLOCK_CHAR_TOKEN:
			case PLAIN_TOKEN:
						//all are assertion errors.
						throw new AssertionError("token_state="+token_state);
			case SINGLE_CHAR_TOKEN:
						//do it. No stitching for plain tokens.
						token_state = TTokenState.AFTER_TOKEN;
						closeSingleCharTokenImpl();
						break;						
			case STRING_TOKEN:						
			case STRING_TOKEN_STITCHING:
						throw new AssertionError("token_state="+token_state);
		}
	};
	/* **********************************************************
	
			Support necessary for injecting off-band data
			which are not a part of token-signal chain
			
	***********************************************************/
	/** Prepares state machine to inject off band data.
		Off band data can be incjected only between tokens.
		and always do terminate current token (if any)
	@throws IOException if failed.
	*/
	protected void openOffBandData()throws IOException
	{
		flushStringTokenStitching();
	};
	/** Prepares state machine to return to token processing
	@throws IOException if failed.
	*/
	protected void closeOffBandData()throws IOException
	{
	};
	
	/* **********************************************************
	
			Support necessary for state handling
			
	***********************************************************/
	/** Checks if string token stitching operation is pending.
	If it is terminates it.
	<p>
	Should be invoked before writing any signal and before flushing
	data to down-stream.
	@throws AssertionError if some tokens were not closed through API
	@throws IOException if failed.
	*/
	private void flushStringTokenStitching()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:		//no problem
			case BEGIN_SIGNAL:	//no problem
			case END_SIGNAL:	//no problem
			case AFTER_TOKEN:	//no problem
			case PLAIN_TOKEN:	//potential problem, but may happen in un-terminated blocks
			case SINGLE_CHAR_TOKEN: //--//--
			case BLOCK_CHAR_TOKEN:	//--//--
						break;
			case STRING_TOKEN:
						//this is for sure a problem
						throw new AssertionError("token_state="+token_state);						
			case STRING_TOKEN_STITCHING:
						//in this case we need to actually close it.
						closeStringTokenImpl();
						token_state = TTokenState.AFTER_TOKEN;
						break;
		};
	};
	
	/* **********************************************************
	
			AStructWriteFormatBase0
			
	***********************************************************/
	@Override protected void flushImpl()throws IOException
	{
		//Note: flush is warranted to be called before close,
		//		so flushing stitched tokens here is fine.
		//flush tokens
		flushStringTokenStitching();
	};
	/* **********************************************************
		
		AStructWriteFormatBase0
			
	***********************************************************/
	
	/** Overriden to call link {@link #flushStringTokenStitching} 
	to terminate any pending token, call super implementation 
	and then change the state to {@link TTokenState#BEGIN_SIGNAL}
	*/
	@Override protected void flushSignalPayloadBeginNext()throws IOException
	{
		//Now there is a bit of conflict here:
		//	the super.flushSignalPayloadBeginNext()
		//	calls terminatePendingBlockOperation()
		//	which may be responsible for terminating block operation
		//	which may include some block closing code generation.
		//	
		//	If however block contains string tokens, then those tokens 
		//	must be closed first.
		//
		//	If block however keeps un-opened plain token there is 
		//	
		flushStringTokenStitching();
		//Now terminate block
		super.flushSignalPayloadBeginNext();
		//Inject eventual token 		
		if(token_state==TTokenState.AFTER_TOKEN)
							outTokenToBeginSignalSeparator();
		token_state = TTokenState.BEGIN_SIGNAL;
	}; 
	/** Overriden to call link {@link #flushStringTokenStitching} 
	to terminate any pending token, call super implementation 
	and then change the state to {@link TTokenState#BEGIN_SIGNAL}
	*/
	@Override protected void flushSignalPayloadEndNext()throws IOException
	{
		//See notes in flushSignalPayloadBeginNext
		flushStringTokenStitching();
		super.flushSignalPayloadEndNext();
		//Inject eventual token 		
		if(token_state==TTokenState.AFTER_TOKEN)
							outTokenToEndSignalSeparator();		
		token_state = TTokenState.END_SIGNAL;
	}; 
};