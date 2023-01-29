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
	and that sequence of string tokens are to be "stitched"
	into a one string token (see {@link #closeStringToken} for
	how to disable this behavior).
	<p>
	This class achives that by intercepting {@link #openPlainToken}
	and {@link #openStringToken} and converting them to
	necessary sequence of calls to:
	{@link #outTokenSeparator},{@link #openPlainTokenImpl},
	{@link #openStringTokenImpl} and etc. according to current
	state of the token list.
	<p>
	The state of token list is tracked by interception
	of token related methods {@link #openPlainToken},{@link #openStringToken},
	{@link #closePlainToken},{@link #closeStringToken}
	and signal related method {@link #terminatePendingBlockOperation}.
	
	<h1>User API</h1>
	No change when comparet to {@link ATxtWriteFormat0}.
*/
public abstract class ATxtWriteFormat1 extends ATxtWriteFormat0
{
			private enum TTokenState
			{
					/** Once file was opened. 
					Indicates that no token seprator is necessary.
					*/
					NOTHING,
					/** Once signal was written or begun to be written.
					Indicates that no token seprator is necessary
					*/
					SIGNAL,
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
	/** Invoken when class detects that token was written and
	next token is to be opened. Once this method returns
	{@link #openPlainTokenImpl} or {@link #openStringTokenImpl}
	is invoked */
	protected abstract void outTokenSeparator()throws IOException;
	/** Invoked by {@link #openPlainToken} when it decides that
	actuall opening should take place */
	protected abstract void openPlainTokenImpl()throws IOException;
	/** Invoked by {@link #openStringToken} when it decides that
	actuall opening should take place */
	protected abstract void openStringTokenImpl()throws IOException;
	/** Invoked by {@link #closePlainToken} when it decides that
	actuall closeing should take place */
	protected abstract void closePlainTokenImpl()throws IOException;
	/** Invoked by {@link #closeStringToken} when it decides that
	actuall closeing should take place */
	protected abstract void closeStringTokenImpl()throws IOException;
	
	/* **********************************************************
	
			ATxtWriteFromat0
			
	***********************************************************/
	/** This decides if	token separator is necessary and invokes
	{@link #outTokenSeparator} and {@link #openPlainTokenImpl}.
	If string token stitching is in progress will terminate it.
	@throws AssertionError if in incorrect state
	*/
	@Override protected final void openPlainToken()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	//no separator necessary at the beginning of a file
			case SIGNAL:	//no separator necessary after signal
						openPlainTokenImpl();
						break;
			case AFTER_TOKEN:
							//separator is necessary
						outTokenSeparator();
						openPlainTokenImpl();
						break;
			case PLAIN_TOKEN:						
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
			case SIGNAL:	//no separator necessary after signal
						openStringTokenImpl();
						break;
			case AFTER_TOKEN:
							//separator is necessary
						outTokenSeparator();
						openStringTokenImpl();
						break;
			case PLAIN_TOKEN:						
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
	/** "Stitching" variant of {@link #closeStringToken} */
	protected final void closeStringToken_stitching()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	
			case SIGNAL:
			case AFTER_TOKEN:
			case PLAIN_TOKEN:
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
	/** Non-"stitching" variant of {@link #closeStringToken} */
	protected final void closeStringToken_no_stitching()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	
			case SIGNAL:
			case AFTER_TOKEN:
			case PLAIN_TOKEN:
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
			case SIGNAL:
			case AFTER_TOKEN:
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
	/* **********************************************************
	
			Support necessary for state handling
			
	***********************************************************/
	/** Checks if string token stitching operation is pending.
	If it is terminates it.
	<p>
	Should be invoked before writing any signal and before flushing
	data to down-stream.
	@throws AssertionError if some tokens were not closed through API
	*/
	protected final void flushStringTokenStitching()throws IOException
	{
		switch(token_state)
		{
			case NOTHING:	
			case SIGNAL:	
			case AFTER_TOKEN:
						break;
			case PLAIN_TOKEN:						
			case STRING_TOKEN:
						//both are assertion errors.
						throw new AssertionError("token_state="+token_state);						
			case STRING_TOKEN_STITCHING:
						//in this case we need to actually close it.
						closeStringTokenImpl();
						token_state = TTokenState.AFTER_TOKEN;
						break;
		};
	};
	/**
		Should be invoked after signal was written to a stream.
		@throws AssertionError if some tokens were not closed through API
		@throws AssertionError {@link #flushStringTokenStitching} was not called.
	*/
	protected final void setTokenStateToAfterSignal()
	{
		assert(
				(token_state!=TTokenState.PLAIN_TOKEN)
				&&
				(token_state!=TTokenState.STRING_TOKEN)
				&&
				(token_state!=TTokenState.STRING_TOKEN_STITCHING)
				):"token_state="+token_state+" did You forgot flushStringTokenStitching or closing tokens?";
		token_state = TTokenState.SIGNAL;
	};
	/* **********************************************************
	
			AStructWriteFormatBase0
			
	***********************************************************/
	@Override protected void flushImpl()throws IOException
	{
		//Note: flush is warranted to be called before close,
		//		so flushing stitched tokens here is fine.
		flushStringTokenStitching();
	};
	/* **********************************************************
		
			Note:
				We have three points at which we can intercept
				signal operations:
				
				AStructWriteFormatBase.begin
				ARegisteringStructWriteFormat.beginImpl
				and
				ARegisteringStructWriteFormat.beginAndRegisterImpl familly.
				
				Our interception do serve two purposes:
					1.To call flushStringTokenStitching which may produce
					  some output;
					2.To trace state with setTokenStateToAfterSignal
					
				The AStructWriteFormatBase.begin does:
					- tests if this is allowed call.
						* here we need to terminate pending stitching.
					- terminate pending block operation which MAY generate output
						* here we should 
					- goes to beginImpl
						ARegisteringStructWriteFormat.beginImpl does:
							- not generate anything, but dispateches to beginAndRegisterImpl
							  and so on.
				Since our flushStringTokenStitching() needs to terminate the token which may
				be a part of a sequence the correct position is to capture the begin() and
				end(). 
				
				The ideal point will be a termination point which 
				is provided to us by AStructWriteFormatBase0.flushSignalPayload();
			
	***********************************************************/
	
	/** Overriden to call link {@link #flushStringTokenStitching} 
	to terminate any pending token, call super implementation 
	and then indicate with {@link #setTokenStateToAfterSignal}
	that form point of view of tokens we are after the signal.
	
	*/
	@Override protected void flushSignalPayload()throws IOException
	{
		flushStringTokenStitching();
		super.flushSignalPayload();
		setTokenStateToAfterSignal();
	}; 
};