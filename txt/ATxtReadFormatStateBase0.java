package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.CBoundStack;
import java.util.NoSuchElementException;
import java.io.IOException;

/**
	A state based, handler organized text parsing.
	
	<h1>State handler</h1>
	This class assumes, that the {@link #toNextChar}
	is implemented by an instance {@link ATxtReadFormatStateBase0.AStateHandler} class
	which do call {@link #setNextChar} to apropriate syntax
	and changes state by calling {@link #pushStateHandler},{@link #popStateHandler} or {@link #setStateHandler}
	
*/
public abstract class ATxtReadFormatStateBase0<TSyntax extends ATxtReadFormat1.ISyntax> 
						extends ATxtReadFormat1<TSyntax>
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ATxtReadFormatStateBase0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ATxtReadFormatStateBase0.",ATxtReadFormatStateBase0.class) : null;
 
			/** A state handler class used to implement {@link ATxtReadFormat1#toNextChar}
			Invoked by {@link ATxtReadFormatStateBase0#toNextChar} */
			protected abstract class AStateHandler
			{
				/** Invoked by {@link ATxtReadFormatStateBase0#toNextChar}.
				Should do everything what {@link ATxtReadFormat1#toNextChar} does.
				@throws IOException if failed.
				@see ATxtReadFormatStateBase0#setNextChar
				@see ATxtReadFormatStateBase0#pushStateHandler
				@see ATxtReadFormatStateBase0#popStateHandler
				@see ATxtReadFormatStateBase0#setStateHandler
				*/
				protected abstract void toNextChar()throws IOException;
			};
				/** Lazy initialized handler stack */
				private CBoundStack<AStateHandler> states;
				/** A current handler */
				private AStateHandler current;
				/** What to return from {@link #getNextSyntaxElement} */
				private TSyntax next_syntax_element;
				/** What to return from {@link #getNextChar} */
				private int next_char;
				
	/** Creates. Subclass must initialize state handler by calling
	{@link #setStateHandler} and adjust stack limit with {@link #setHandlerStackLimit}.
	
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
	@param token_size_limit non-zero positive, a maximum number of characters which constitutes 
			a primitive element token, excluding string tokens. Basically a maximum
			number of characters which do constitute a primitive numeric value.
	@throws AssertionError if token_size_limit is less than 16+3 which is a minimum
			number to hold hex encoded long value.
	*/			
	protected ATxtReadFormatStateBase0(int name_registry_capacity,int token_size_limit)
	{
		super( name_registry_capacity,token_size_limit);
		if (TRACE) TOUT.println("new ATxtReadFormatStateBase0()");
	};
	/* *****************************************************************
	
			Services for subclasses
			
	*******************************************************************/
	/* -----------------------------------------------------------------
				Handler related
	-----------------------------------------------------------------*/
	/** Returns current state handler
	@return can be null */
	protected final AStateHandler getStateHandler()
	{
		return current;
	};
	/** Sets state handler
	@param h can be null only when closing.
	*/
	protected void setStateHandler(AStateHandler h)
	{
		if (TRACE) TOUT.println("setStateHandler("+h+")");
		this.current = h;
	};
	/** Pushes current state handler (if not null) on stack and
	makes h current
	@param h non null.
	@throws EFormatBoundaryExceeded if exceeded {@link #setHandlerStackLimit}
	*/
	protected void pushStateHandler(AStateHandler h)throws EFormatBoundaryExceeded
	{
		if (TRACE) TOUT.println("pushStateHandler("+h+")");
		assert(h!=null);
		if (current!=null)
		{
			if (states==null) states = new CBoundStack<AStateHandler>();
			states.push(current);
		};
		current = h;
	};
	/** Pops state handler from a stack and makes it current 
	@throws NoSuchElementException if stack is empty */
	protected void popStateHandler()
	{
		if (states==null) throw new NoSuchElementException();
		current = states.pop();
		if (TRACE) TOUT.println("popStateHandler()->"+current);
	};
	/** Changes limit of  stack size used by {@link #pushStackHandler}.
	<p>
	Default limit is -1, unlimited.
	@param limit new limit, maximum size of stack before failure or -1 if not limit it 
	@throws IllegalStateException if current stack depth is larger than the limit 
	*/
	protected void setHandlerStackLimit(int limit)
	{
		if (TRACE) TOUT.println("setHandlerStackLimit("+limit+")");
		if (states==null) states = new CBoundStack<AStateHandler>();
		states.setStackLimit(limit);
	};
	/** Returns current limit of  stack
	@return -1 if disabled */
	protected final int getHandlerStackLimit()
	{
		if (states==null) return -1;
		return states.getStackLimit();
	};
	/* -----------------------------------------------------------------
				toNextCharRelated
	-----------------------------------------------------------------*/
	/** Sets values to be reported:
	@param character from {@link #getNextChar}
	@param syntax from {@link #getNextSyntaxElement}
	@throws AssertionError if syntax is null and character is not -1 or
			if syntax is not null and character is -1
	*/
	protected void setNextChar(int character, TSyntax syntax)
	{
		assert((character>=-1)&&(character<=0xFFFF));
		assert( ((syntax==null)&&(character==-1))
					||
				((syntax!=null)&&(character>=0))
				):"inconsistent syntax ="+syntax+" with character=0x"+Integer.toHexString(character);
				
		this.next_syntax_element = syntax;
		this.next_char=character;
	};
	/* *****************************************************************
	
			ATxtReadFormat1
			
	*******************************************************************/
	@Override protected final void toNextChar()throws IOException
	{
		assert(current!=null):"state handler not initialized. Call setStateHandler()";
		if (DUMP) TOUT.println("toNextChar()->"+current);
		current.toNextChar();
	};
	@Override protected final TSyntax getNextSyntaxElement(){ return next_syntax_element; };
	@Override protected final  int getNextChar(){ return next_char; };
};