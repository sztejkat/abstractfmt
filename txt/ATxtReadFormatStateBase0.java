package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.CBoundStack;
import java.util.NoSuchElementException;
import java.io.IOException;

/**
	A "state graph" based parser.
	
	<h1>State graph</h1>
	This class assumes that threre is a certain "state graph" represented by a graph
	of instances of {@link ATxtReadFormatStateBase0.IStateHandler} class.
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
	
	<h2>Extended state handler</h2>
	 A basic state handler is {@link ATxtReadFormatStateBase0.IStateHandler}.
	 You can do absolutely anything with it.
	 <p>
	 You will however find it a bit tricky to express the syntax using just this class 
	 alone. You need a bit more helpfull framework.
	 <p>
	 This framework is defined for You by {@link ATxtReadFormatStateBase0.ISyntaxHandler}.
	
	
	<h1>State graph is not grammar</h1>
	There is one significant difference between "state graph" and "grammar" approach.
	<p>
	The state graph is a direct method where the parser is at certain "state" and with each
	character processed it decides wheter to stay in that state or transit to another state.
	The side effect of this decission making is production of {@link #queueNextChar}.
	<p>
	The "grammar" approach is different - the parser has been given a certain grammar,
	makes an attempt to "match" it with input and the result of that "match" triggers 
	some action.
	<p>
	The first problem with "grammar" approch is that to match a complex grammar it must consume
	a lot of input data, roll back if fails to match and try an another path. Grammar is excellent
	when performing a "callback" based processing. 
	<p>
	The second problem with "grammar" is that to match something to a definition it must consume
	it. At least in most generic approach. It will be then problematic to handle elements of
	infinite length as we require to be able to handle them.
*/
public abstract class ATxtReadFormatStateBase0<TSyntax extends ATxtReadFormat1.ISyntax> 
						extends ATxtReadFormat1<TSyntax>
{
	/*
		Notes: The sad truth is that I do seems to be too dumb to efficiently
				implement the "grammar" based approach with assumed boundary 
				conditions of minumim read-forwarad and infinitie size of some
				elements.
	*/
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ATxtReadFormatStateBase0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ATxtReadFormatStateBase0.",ATxtReadFormatStateBase0.class) : null;
 
         	/* **************************************************************************
         	
         			State handler
         	
         	
         	****************************************************************************/
         	/* ------------------------------------------------------------------------
         				Generic
         	------------------------------------------------------------------------*/
			/** A state handler class used to implement {@link ATxtReadFormat1#toNextChar}
			and is invoked by {@link ATxtReadFormatStateBase0#toNextChar}.
			<p>
			Only one state handler is active (that is <i>current</i>).
			This state handler is on the same top of state handlers stack.
			<p>
			A state handler can replace current state handler 
			or be pushed on state handlers stack over the existing current handler.
			<p>
			A state handler life is controlled by following states:
			<ul>
				<li>state handler is left - the state handler is neither current
					nor on state handlers stack;</li>
				<li>state handler is entered - the state handler is on state handlers
					stack but not necessairly it is a <i>current</i> state handler;</li>
				<li>state handler is deactivated - the state handler is on state handlers
					stack but is NOT a <i>current</i> state handler;</li>
				<li>state handler is active - it is on state handlers stack,
					on the same top of it and thous is <i>current</i>.
					Only current state handler do receive calls to 
					{@link #toNextChar};</li>
			</ul>
			*/
			public interface IStateHandler
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
				This can be used to implement alternatives or optional elements
				since this method is called only when {@link #syntaxQueueEmpty}
				gives true.
			
				@throws IOException if failed.
				@see ATxtReadFormatStateBase0#queueNextChar
				@see ATxtReadFormatStateBase0#pushStateHandler
				@see ATxtReadFormatStateBase0#popStateHandler
				@see ATxtReadFormatStateBase0#setStateHandler
				*/
				public void toNextChar()throws IOException;
				/** Used for logging 
				@return by default a class name */
				public default String getName(){ return this.getClass().getName(); };
				/** Invoked when state is "entered". Default implementation logs trace.
				<p>
				State is "entered" when it is either replacing current
				state with {@link #setStateHandler} or is pushed on top
				of current state with {@link #pushStateHandler}. */
				default public void onEnter()
				{
					if (TRACE) TOUT.println("entered "+this.getName());
				};
				/** Invoked when is "left". Default implementation logs trace.
				<p>
				State is "left" when is it replaced by an
				another state with {@link #setStateHandler} or removed
				from stack by {@link #popStateHandler}.
				*/
				default public void onLeave()
				{
					if (TRACE) TOUT.println("left "+this.getName());
				};
				/** Invoked when state is "activated" (becomes current). Default implementation logs trace.
				<p>
				State is "activated" after it is "entered". Activation happens
				if state replaces preivously active state with {@link #setStateHandler},
				is pushed on stack with {@link #pushStateHandler} <u>or</u> returns 
				to the top of stack as a side effect of {@link #popStateHandler} */
				default public void onActivated()
				{
					if (TRACE) TOUT.println("activated "+this.getName());
				};
				/** Invoked when state is "de-activated" (no longer current). Default implementation logs trace.
				<p>
				State is "de-activated" after it is "entered" and before it is "left".
				Deactivation happens if state is replaced with {@link #setStateHandler}
				or an another state is pushed over it with {@link #pushStateHandler}.
				*/
				default public void onDeactivated()
				{
					if (TRACE) TOUT.println("deactivated "+this.getName());
				};
			};
			/* ------------------------------------------------------------------------
         				Syntax
         	------------------------------------------------------------------------*/
         	/**
         		A syntax state handler.
         		<p>
         		This handler defines a basic framework focused around a concept that
         		each state must be able to perform following actions:
         		<ul>
         			<li>to become activated by transition from an another state.
         			This is handled by {@link #onEnter}+{@link #onActivated};</li>
         			<li>to become hidden by pushing an another state.
         			This is handled by {@link #onDeactivated};</li>
         			<li>to become active again by removing pushed state.
         			This is handled by {@link #onActivated};</li>
         			<li>to be left by transtion to an another state.
         			This is handled by {@link #onDeactivated}+{@link #onLeave};</li>
         		</ul>
         		When state is "active" it is receving calls to {@link #toNextChar}
         		and must decide if and in what condition to transit to another state.
         		<p>
         		We may say, that we have two kinds of states:
         		<ul>
         			<li>"consumers" which do process characters in {@link #toNextChar}
         			and once they detect that all is processed do either {@link #popStateHandler}
         			(if they do not know what state is next)
         			or {@link #setStateHandler} (if they now what state is next);</li>
         			<li>"catchers" which instead of processing characters actively look
         			for answer to the question which state should be entered next;</li>
         		</ul>
         		
         		<h2>Consumers</h2>
         		Consumers concept is fully handler by a superclass. Nothing special 
         		to add here.
         			
         		<h2>Catchers</h2>
         		Catchers will usually read some characters and match them with "catch phrases"
         		of subsequent states. For an example XML catcher may react on "&lt;" to transit
         		to "XML element" state.
         		<p>
         		The process of "catching" the phrase is trivial for single character phrases,
         		but may be significantly complex and messy for multi-character phrases and will
         		require own state graph to handle different lengths of phrases.
         		<p>
         		To make things easier we define one additional action:
         		<ul>
         			<li>{@link #tryEnter} which is responsible for detecting if the cursor
         			in stream is at the location in which the state should be entered;</li>
         		</ul>
         		With that the "catcher" can be just:
         		<pre>
         			if (!X.tryEnter()) if (!Y.tryEnter()) nothing_catched;
         		</pre>
         	*/
			public interface ISyntaxHandler extends IStateHandler
			{
				/**
					Tests if can enter a state and enters it.
					<p>
					This method basically does:
					<pre>
			...<i>read all characters necessary to say if should enter</i>
			if (should enter)
			{
				setStateHandler(this) <i>or</i> pushStateHandler(this);
				return true;
			}else
			{
				...<i>unread all characters restoring downstream
					to state from at entrance</i>
				return false;
			}
					</pre>
					@return <ul>
							<li>
								<code>true</code> if performed required state transition either by
								replacing current state handler with self or pushing
								self on the stack;
							</li>
							<li>
								<code>false</code> if figured out that this state
								does not match input, un-read all characters read and 
								left state unchanged;
							</li>
							</ul>
					@throws IOException if failed.
				*/
				public boolean tryEnter()throws IOException;
			};
			
				/** Lazy initialized handler stack */
				private CBoundStack<IStateHandler> states;
				/** A current handler */
				private IStateHandler current;
				
				private static final int SYNTAX_QUEUE_INIT_SIZE = 8;
				private static final int SYNTAX_QUEUE_SIZE_INCREMENT = 32;
				/**
				Pre-allocated FIFO.
				<p>
				We need a relatively fast, but shallow FIFO of
				pairs <code>TSyntax</code>-<code>int</code> to allow
				more than one syntax element to be produced by a
				single call to {@link IStateHandler#toNextChar}
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

	/* ***************************************************************************
	
			Construction
	
	
	*****************************************************************************/			
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
	public final IStateHandler getStateHandler()
	{
		return current;
	};
	/** Sets state handler, invokes {@link IStateHandler#onLeave}/{@link IStateHandler#onEnter}
	and {@link IStateHandler#onActivated}/{@link IStateHandler#onDeactivated}.
	@param h can be null. Reasonable only when closing stream.
	*/
	public void setStateHandler(IStateHandler h)
	{
		if (TRACE) TOUT.println("setStateHandler("+h+")");
		IStateHandler c = this.current; 
		if (c!=null)
		{
			c.onDeactivated();
			c.onLeave();
		}
			this.current = h;
		if (h!=null)
		{
			h.onEnter();
			h.onActivated();
		};
	};
	/** Pushes current state handler (if not null) on stack and
	makes h current, invokes {@link IStateHandler#onLeave}/{@link IStateHandler#onEnter}
	and {@link IStateHandler#onActivated}/{@link IStateHandler#onDeactivated}.
	@param h non null.
	@throws EFormatBoundaryExceeded if exceeded {@link #setHandlerStackLimit}
	*/
	public void pushStateHandler(IStateHandler h)throws EFormatBoundaryExceeded
	{
		if (TRACE) TOUT.println("pushStateHandler("+h+")");
		assert(h!=null);
		
		IStateHandler c = this.current; 
		if (c!=null)
		{
			if (states==null) states = new CBoundStack<IStateHandler>();
			states.push(c);
			c.onDeactivated();
		};
		current = h;
		if (h!=null)
		{
			h.onEnter();
			h.onActivated();
		};
	};
	/** Pops state handler from a stack and makes it current,
	invokes {@link IStateHandler#onLeave}/{@link IStateHandler#onEnter}
	@throws NoSuchElementException if stack is empty */
	public void popStateHandler()
	{
		if (states==null) throw new NoSuchElementException();
		IStateHandler c = this.current;
		if (c!=null)
		{
			c.onDeactivated();
			c.onLeave();
		}
		c = current = states.pop();
		if (c!=null)
		{
			c.onActivated();
		};
		if (TRACE) TOUT.println("popStateHandler()->"+current);
	};
	/** Returns state handler which would become current after {@link #popStateHandler}
	@return null if stack is empty */
	public final IStateHandler peekStateHandler()
	{
		return states.peek();
	}
	/** Changes limit of  stack size used by {@link #pushStateHandler}.
	<p>
	Default limit is -1, unlimited.
	@param limit new limit, maximum size of stack before failure or -1 if not limit it 
	@throws IllegalStateException if current stack depth is larger than the limit 
	*/
	protected void setHandlerStackLimit(int limit)
	{
		if (TRACE) TOUT.println("setHandlerStackLimit("+limit+")");
		if (states==null) states = new CBoundStack<IStateHandler>();
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
	public void setNextChar(int character, TSyntax syntax)
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
	@param character for {@link #getNextChar} -1 or 0...0xFFFF
	@param syntax for {@link #getNextSyntaxElement}
	@throws AssertionError if syntax is null and character is not -1 or
			if syntax is not null and character is -1
	*/
	public void queueNextChar(int character, TSyntax syntax)
	{
		assert((character>=-1)&&(character<=0xFFFF));
		assert( ((syntax==null)&&(character==-1))
					||
				((syntax!=null)&&(character>=0))
				):"inconsistent syntax ="+syntax+" with character=0x"+Integer.toHexString(character);
		queueSyntax(character, syntax);
	};
	/** Puts next value, to be read after current, from  syntax queue 
	@param characters list of character for {@link #getNextChar} 
	@param syntax for {@link #getNextSyntaxElement}
	@throws AssertionError if syntax is null.
	*/
	public void queueNextChars(String characters, TSyntax syntax)
	{
		assert(syntax!=null);
		for(int i=0,n=characters.length();i<n;i++)
		{
			queueSyntax(characters.charAt(i), syntax);
		};
	};
	/**
		Turns specified unicode code point into surogates pair, if necessary
		and puts them into character queue under a specified syntax.
		@param c unicode code point or single java character or -1. 
				Full set of unicode code-points	and full set of  characters is allowed here,
				including bad surogates.
		@param syntax syntax to queue, null allowed only if code_point is -1.
		@see ATxtReadFormatStateBase0#queueNextChar
	*/
	public void queueNextCodepoint(int c, TSyntax syntax)
	{
		assert((c>=-1)&&(c<=0x10FFFF)):"0x"+Integer.toHexString(c)+"("+c+") is not Unicode";
		if (c==-1)
		{
			assert(syntax==null);
			queueNextChar(-1,syntax);
		}else
		if (c>0xFFFF)
		{
				//needs to be split to surogates.
				c = c - 0x1_0000;
				char upper = (char)( (c >> 10)+0xD800);
				char lower = (char)( (c & 0x3FF)+0xDC00);
				queueNextChar(upper,syntax);
				queueNextChar(lower,syntax);
		}else
		{
			queueNextChar(c,syntax);
		}
	};
	/* *****************************************************************
	
			ATxtReadFormat1
			
	*******************************************************************/
	/** Ask <code>{@link #current}.toNextChar</code>
	to produce someting in syntax queue. This operation
	is repeated in loop until somethig is put in syntax queue.
	
	@see IStateHandler#toNextChar
	*/
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
	/* ********************************************************************
	
		
			AStructReadFormatBase0
	
	
	*********************************************************************/
	/** Overriden to brutally drop all the state handlers and queue 
	to make garbage collection easier.*/ 
	@Override protected void closeImpl()throws IOException
	{
		states = null;
		next_char = null;
		current = null;
		next_queue_rptr=0;
		next_queue_wptr=0;
		next_syntax_element=null;
		next_char=null;
	};
};