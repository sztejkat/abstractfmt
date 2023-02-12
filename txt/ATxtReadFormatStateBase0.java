package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.CBoundStack;
import java.util.NoSuchElementException;
import java.io.IOException;

/**
	A state based, handler organized text parsing.
	
	<h1>State handler</h1>
	This class assumes that threre is a certain "state graph" represented by a graph
	of instances of {@link ATxtReadFormatStateBase0.AStateHandler} class.
	<p>
	One of such states is "current" and initialized during a class construction.
	<p>
	This class basically does:
	<ul>
		<li>it serves the syntax avaliable through {@link #getNextSyntaxElement}
		and {@link #getNextChar} from the "syntax queue";</li>
		<li>it implements {@link #toNextChar} by calling "current" state 
		in a loop as long, as "syntax queue" is empty;</li>
		<li>the state handlers are expected to process characters from low level
		stream and use:
			<ul>
				<li>{@link #queueNextChar}/{@link #setNextChar} to feed the "syntax queue";</li>
				<li>{@link #pushStateHandler},{@link #popStateHandler} or {@link #setStateHandler}
				to traverse the state graph;</li>
			</ul>
		</li>
	</ul>
	
	<h1>Syntax definition with handlers</h1>
	Except of what is specified above the syntax may be defined
	with the help of {@link COptionalHandler},
	{@link CRequiredHandler},{@link CAlterinativeHandler},
	{@link CNextHandler}, {@link CRepeatHandler}.
	<p>
	All those handlers do delegate job tu sub-handlers and use folowing convention:
	<ul>
		<li>if they delegate a job to other handler they will always
		make them current;</li>
		<li>if handler does not regonize a syntax it should
		un-read everything, NOT call {@link #queueNextChar}/{@link #setNextChar}
		and {@link #popStateHandler};
		</li>
		<li>if handler do process element it should call {@link #queueNextChar}/{@link #setNextChar}
		an stay current;</li>
		<li>if handler does completes processing of its syntax element
		it should call {@link #popStateHandler};</li>
	</ul>
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
				<p>
				This can be used to implement alternatives o optinal elements
				since this method is called only when {@link #syntaxQueueEmpty}
				gives true.
			
				@throws IOException if failed.
				@see ATxtReadFormatStateBase0#queueNextChar
				@see ATxtReadFormatStateBase0#pushStateHandler
				@see ATxtReadFormatStateBase0#popStateHandler
				@see ATxtReadFormatStateBase0#setStateHandler
				*/
				protected abstract void toNextChar()throws IOException;
				/** Invoked when state becomes current. Default is empty. */
				protected void onEnter(){};
				/** Invoked when is no longer current. Default is empty. */
				protected void onLeave(){};
			};
			
			
			/**
				A handler which always throws informing that nothing can be read
				anymore. This should be first handler pushed on syntax stack
				or You may expect {@link NoSuchElementException} to be thrown
				by various handlers.
			*/
			protected final class CCannotReadHandler extends AStateHandler
			{
							/** An error message if not found */
							private final String message;
				/** Creates.	
				@param message an error message to be thrown if element is
							not recognized.
				*/
				protected CCannotReadHandler(String message)
				{
					assert(message!=null);
					this.message = message;
				};
				protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("CCannotReadHandler.toNextChar()");
					throw new EBrokenFormat(message);
				};
			};
			
			
			/**
				A handler which do require certain syntax element to appear exactly one time.
			*/
			protected final class CRequiredHandler extends AStateHandler
			{
							/** Current handler */
							private AStateHandler required;
							/** An error message if not found */
							private final String message;
				/** Creates.					
				@param required non null, an element which must recognize
							a syntax or an exception will be thrown.
				@param message an error message to be thrown if element is
							not recognized.
				*/
				protected CRequiredHandler(AStateHandler required, String message)
				{
					assert(required!=null);
					assert(message!=null);
					this.required = required;
					this.message = message;
				};
				/** Creates, forming default message from <code>required.toString</code>				
				@param required non null, an element which must recognize
							a syntax or an exception will be thrown.
				*/
				protected CRequiredHandler(AStateHandler required)
				{
					this(required,"Required element "+required+"  not found");
				};
				protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("CRequiredHandler.toNextChar() ENTER");
					//make it current, replacing ourselves.
					setStateHandler(required);
					//call to be sure, that syntax element is recognized.
					if (TRACE) TOUT.println("CRequiredHandler.toNextChar()->"+required);
					required.toNextChar();
					if (syntaxQueueEmpty())
					{
						throw new EBrokenFormat(message);
					};
					if (TRACE) TOUT.println("CRequiredHandler.toNextChar() LEAVE");
				};
			};
			
			/**
				A handler which can be used to implement repetition
				of certain element.
			*/
			protected final class CRepeatHandler extends AStateHandler
			{
							/** Current handler */
							private final AStateHandler repeat;
				/** Creates.					
				@param repeat non null a "current handler" which recognizes  
								single occurence of syntax element.
				*/
				protected CRepeatHandler(AStateHandler repeat )
				{
					assert(repeat!=null);
					this.repeat = repeat;
				};
				protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("CRepeatHandler.next() ENTER");
					//Push it and make current
					pushStateHandler(repeat);
					//call to be sure, that syntax element is recognized.
					repeat.toNextChar();
					if (syntaxQueueEmpty())
					{
						//we failed to recognize it.
						assert(getStateHandler()==this);
						popStateHandler();
						if (TRACE) TOUT.println("CRepeatHandler.next() not repeating anymore");
					};
					if (TRACE) TOUT.println("CRepeatHandler.next() LEAVE");
				};
			};
			
			/**
				A handler which can be used to implement "optional" syntax elemement
				with a regular element handler.
			*/
			protected final class COptionalHandler extends AStateHandler
			{
							/** Should become current if {@link #optional} does not recognize a syntax */
							private final AStateHandler next;
							/** Will be used to regonize syntax */
							private final AStateHandler optional;
				/** Creates.					
				@param optional non null. If this handler does not recognize any syntax 
							({@link #syntaxQueueEmpty} gives true) the {@link #next} is set
							as current and tried. If {@link #next} also fails {@link #popStateHandler}
							is called.
				@param next non null.
				*/
				protected COptionalHandler(AStateHandler optional, AStateHandler next)
				{
					assert(optional!=null);
					assert(next!=null);
					this.next = next;
					this.optional = optional;
				};
				protected final void toNextChar()throws IOException
				{
					//replace self with next
					setStateHandler(next);
					//Make optional current
					pushStateHandler(optional);
					optional.toNextChar();
					if (syntaxQueueEmpty())
					{
						assert(getStateHandler()==next);
						//try next
						next.toNextChar();
					};
				};
			};
			
			/**
				A handler which can be used to implement "alternative" syntax elemements
				by a regular element handler.
			*/
			protected final class CAlterinativeHandler extends AStateHandler
			{
							/** Set of alternatives, tried in order of appearance*/
							private final AStateHandler  [] alternatives;
				/** Creates.					
				@param alternatives non null, cannot carry nulls.
							Handlers in this array are tried one by one,
							until a handler which recognize syntax 
							({@link #syntaxQueueEmpty} gives false) is found. This
							handler is becoming current handler.
							If none produced any syntax {@link #popStateHandler} is called.
				*/
				protected CAlterinativeHandler(AStateHandler  [] alternatives)
				{
					assert(alternatives!=null);
					this.alternatives = alternatives;
				};
				protected final void toNextChar()throws IOException
				{
					for(AStateHandler H : alternatives)
					{
						pushStateHandler(H);
						H.toNextChar();
						if (!syntaxQueueEmpty())
						{
							return;
						}else
						{
							assert(getStateHandler()==this);
						};
					}
					assert(getStateHandler()==this);
					popStateHandler();
				};
			};
			
			/**
				A handler which can be used to implement sequence of
				required elements.
			*/
			protected final class CNextHandler extends AStateHandler
			{
							/** Current handler */
							private final AStateHandler current;
							/** Next handler */
							private final AStateHandler next;
				/** Creates.					
				@param current non null a "current handler" which implements 
								first element in this chain.
								<p>
								This handler is expected to call {@link #popStateHandler}
								once it completes its job.
				@param next non null, next state handler. Will become current after
								<code>current</code> detects that there is nothing more 
								to do.
				*/
				protected CNextHandler(AStateHandler current, AStateHandler next )
				{
					assert(current!=null);
					assert(next!=null);
					this.current = current;
					this.next = next;
				};
				protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("CNextHandler.toNextChar() ENTER");
					//prepare the processing chain, replacing self
					setStateHandler(next);
					//Make current "current"
					pushStateHandler(current);
					//ask it to be run. This allows chains to be efficiently 
					//used in in optional/alternative because it will detect the syntax.
					current.toNextChar();
					if (TRACE) TOUT.println("CNextHandler.toNextChar() LEAVE");
				};
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
				pairs <code>TSyntax</code>-<code>int</code> to allow
				more than one syntax element to be produced by a
				single call to {@link AStateHandler#toNextChar}
				since sometimes a single characte would have to
				trigger faked multi-char syntax results. 
				<p>
				This is a first array of queue ring.
				<p>
				The value at {@link #next_queue_rptr} points
				to what is to be returned from {@link #getNextSyntaxElement}
				<p>
				Note: This array actually carries <code>TSyntax</code> objects. We simply can't have generic arrays.
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
	/** Sets state handler, invokes {@link AStateHandler#onLeave}/{@link AStateHandler#onEnter}
	@param h can be null only when closing.
	*/
	protected void setStateHandler(AStateHandler h)
	{
		if (TRACE) TOUT.println("setStateHandler("+h+")");
		if (this.current!=null) this.current.onLeave();
		this.current = h;
		if (this.current!=null) this.current.onEnter();
	};
	/** Pushes current state handler (if not null) on stack and
	makes h current, invokes {@link AStateHandler#onLeave}/{@link AStateHandler#onEnter}
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
			if (this.current!=null) this.current.onLeave();
		};
		current = h;
		if (this.current!=null) this.current.onEnter();
	};
	/** Pops state handler from a stack and makes it current,
	invokes {@link AStateHandler#onLeave}/{@link AStateHandler#onEnter}
	@throws NoSuchElementException if stack is empty */
	protected void popStateHandler()
	{
		if (states==null) throw new NoSuchElementException();
		if (this.current!=null) this.current.onLeave();
		current = states.pop();
		if (this.current!=null) this.current.onEnter();
		if (TRACE) TOUT.println("popStateHandler()->"+current);
	};
	/** Changes limit of  stack size used by {@link #pushStateHandler}.
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
	/** Tests if syntax queue is empty. Can
	be used to test if handler did recognize the
	syntax element or not. 
	@return true if empty*/
	protected final boolean syntaxQueueEmpty()
	{
		return next_queue_rptr==next_queue_wptr;
	};
	/** Pushes onto syntax queue, ensuring necessary space
	@param character what 
	@param syntax what
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
	/** Makes in loop attempt to ask <code>{@link #current}.toNextChar</code>
	to produce someting in syntax queue. */
	@Override protected final void toNextChar()throws IOException
	{
		assert(current!=null):"state handler not initialized. Call setStateHandler()";
		//Now first call may be with an empty, but subsequent needs to 
		//drop what is in queue and eventually update.
		if (DUMP) TOUT.println("toNextChar() ENTER");
		if (!syntaxQueueEmpty())
					dropQueueSyntax();
		//Now test again and produce eventually new data.
		while(syntaxQueueEmpty())
		{
			if (DUMP) TOUT.println("toNextChar()->"+current);
			current.toNextChar();
		}
		if (DUMP) TOUT.println("toNextChar() LEAVE");
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