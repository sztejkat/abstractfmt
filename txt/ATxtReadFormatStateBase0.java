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
	which do call {@link #queueNextChar} to apropriate syntax
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
				/** Invoked by {@link ATxtReadFormatStateBase0#toNextChar}
				when syntax queue is empty.
				<p>
				Should do everything what {@link ATxtReadFormat1#toNextChar} does
				using {@link #queueNextChar} or {@link #setNextChar}
				or state management.
				<p>
				If after return from this method syntax queue is empty
				the {@link ATxtReadFormatStateBase0#toNextChar}
				will invoke again this method of currently active handler.
				
				@throws IOException if failed.
				@see ATxtReadFormatStateBase0#queueNextChar
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
				
				private static final int SYNTAX_QUEUE_INIT_SIZE = 8;
				private static final int SYNTAX_QUEUE_SIZE_INCREMENT = 32;
				/**
				Pre-allocated FIFO.
				<p>
				We need a relatively fast, but shallow FIFO of
				pairs {@link #TSyntax}-<code>int</code> to allow
				more than one syntax element to be produced by a
				single call to {@link AStateHandler#toNextChar}
				since sometimes a single characte would have to
				trigger faked multi-char syntax results. 
				<p>
				This is a first array of queue ring.
				<p>
				The value at {@link #next_queue_rptr} points
				to what is to be returned from {@link #getNextSyntaxElement}
				*/
				private ATxtReadFormat1.ISyntax [] next_syntax_element = new ATxtReadFormat1.ISyntax[SYNTAX_QUEUE_INIT_SIZE];
				/**
				The value at {@link #next_queue_rptr} points
				to what is to be returned from {@link #getNextChar}
				@see #next_syntax_element
				*/
				private int [] next_char = new int [SYNTAX_QUEUE_INIT_SIZE];
				/** Read pointer in syntax queue.  */
				private int next_queue_rptr;
				/** Write pointer in syntax queue. 
				If both read and write pointers are equal queue is empty.
				With each write pointer moves towards zero.*/
				private int next_queue_wptr;

				
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
	/** Tests if syntax queue is empty 
	@return true if empty*/
	private boolean syntaxQueueEmpty()
	{
		return next_queue_rptr==next_queue_wptr;
	};
	/** Pushes onto syntax queue, ensuring necessary space
	@param character what 
	@param syntaxt what
	*/
	private void queueSyntax( int character, TSyntax syntax )
	{
		int wptr = this.next_queue_wptr;
		//We always make sure there is at least one empty spacer so
		this.next_syntax_element[wptr]=syntax;
		this.next_char[wptr]=character;
		//move cursor towards zero with rollover
		final int L = this.next_syntax_element.length;
		wptr = (wptr==0) ? L-1: wptr-1;
		if (wptr == this.next_queue_rptr)
		{
			//this is queue overflow, we need to boost it up.
			final int NL = L + SYNTAX_QUEUE_SIZE_INCREMENT;
			final ATxtReadFormat1.ISyntax [] new_next_syntax_element = new ATxtReadFormat1.ISyntax[ NL];
			final int [] new_next_char = new int[NL];
			final ATxtReadFormat1.ISyntax [] old_next_syntax_element = this.next_syntax_element;
			final int [] old_next_char = this.next_char;
			//now just read the WHOLE queue. It is now full absolutely
			//so we can read old into a tail of the new.
			for(int i = 0, r = this.next_queue_rptr ; i<L; i++)
			{
				int at = NL-1-i;
				new_next_syntax_element[at]=old_next_syntax_element[r];
				new_next_char[at]=old_next_char[r];
				r = (r==0) ? L-1: r-1;
			};
			this.next_syntax_element=new_next_syntax_element;
			this.next_char=new_next_char;
			this.next_queue_rptr = NL-1;
			this.next_queue_wptr = NL-1-L;
		}else
		{
			//we fit 
			this.next_queue_wptr = wptr;
		};
	};
	/** Removes top of queue
	@throws AssertionError if empty*/
	private void dropQueueSyntax()
	{
		assert(!syntaxQueueEmpty());
		int r= this.next_queue_rptr;
		if (r==0)
		{
			r = this.next_syntax_element.length;
		};
		r--;
		this.next_queue_rptr=r;
	};
	/** Sets value at the read pointer of a syntax queue overriding previously stored value. 
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
		//needs to queue it if empty or override if not.
		if (syntaxQueueEmpty())
		{
			queueSyntax(character, syntax);
		}
		else
		{
			next_char[next_queue_wptr] = character;
			next_syntax_element[next_queue_wptr] = syntax;
		};
	};
	/** Puts next value, to be read after current, from  syntax queue 
	@param character from {@link #getNextChar}
	@param syntax from {@link #getNextSyntaxElement}
	@throws AssertionError if syntax is null and character is not -1 or
			if syntax is not null and character is -1
	*/
	protected void queueNextChar(int character, TSyntax syntax)
	{
		assert((character>=-1)&&(character<=0xFFFF));
		assert( ((syntax==null)&&(character==-1))
					||
				((syntax!=null)&&(character>=0))
				):"inconsistent syntax ="+syntax+" with character=0x"+Integer.toHexString(character);
		queueSyntax(character, syntax);
	};
	/* *****************************************************************
	
			ATxtReadFormat1
			
	*******************************************************************/
	@Override protected final void toNextChar()throws IOException
	{
		assert(current!=null):"state handler not initialized. Call setStateHandler()";
		//Now first call may be with an empty, but subsequent needs to 
		//drop what is in queue and eventually update.
		if (!syntaxQueueEmpty())
					dropQueueSyntax();
		//Now test again and produce eventually new data.
		while(syntaxQueueEmpty())
		{
			if (DUMP) TOUT.println("toNextChar()->"+current);
			current.toNextChar();
		}
	};
	@SuppressWarnings("unchecked")
	@Override protected final TSyntax getNextSyntaxElement()
	{
		assert(!syntaxQueueEmpty());
		return (TSyntax)next_syntax_element[next_queue_rptr]; 
	};
	@Override protected final  int getNextChar()
	{
		assert(!syntaxQueueEmpty());
		return next_char[next_queue_rptr]; 
	};
};