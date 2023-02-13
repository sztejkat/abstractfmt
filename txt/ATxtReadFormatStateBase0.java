package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.CBoundStack;
import sztejkat.abstractfmt.utils.SStringUtils;
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
	with the help of "control handlers": 
	{@link CCannotReadHandler}, {@link CRequiredHandler},
	{@link CRepeatHandler},{@link COptionalHandler},
	{@link CAlterinativeHandler}, {@link CNextHandler} and 
	"catch handlers":
	
	
	<h2>Control handlers</h2>
	All those handlers do delegate job to delegate-handlers and use folowing convention:
	<ul>
		<li>delegate handlers:
			<ul>
				<li>whenever delegate {@link AStateHandler#toNextChar} is called 
				it may assuem it is the current handler;</li>
				<li>the delegate handler does not decide about state transition,
				except inside it's own sub-set of states. All the syntax state
				transition is controlled by <i>control handlers</i>;</li>
				<li>first call to delegate {@link AStateHandler#toNextChar} 
				after {@link AStateHandler#onEnter} is the "recognition"
				call. The recognition process must complete in first call even
				if it requires more than one character to be processed.
				<p>
				If delegate does recognize that what is in stream is its own syntax 
				it calls {@link #queueNextChar} to indicate that it recognized a syntax.
				<p>
				If it does not recognize own syntax element it doesn't queue anything,
				unread all read characters and calls {@link #popStateHandler};
				</li>
				<li>during subsequent calls, called "consumption" phase,
				it invokes {@link #queueNextChar} to indicate what it is processing
				and invokes {@link #popStateHandler} when it finished processing the element.
				</li>
			</ul>
		</li>
		<li>control handlers:
			<ul>
				<li>a control handler always makes delegate current and performs
					the "recognition" call inside own {@link AStateHandler#toNextChar};</li>
				<li>if delegate did not recognize syntax element control handler
					acts accordingly to own definition. The action may be:
					<ul>
						<li>throw if this is a "required" syntax element;</li>
						<li>try other alternative;</li>
						<li>skip to next element;</li>
					</ul>
				</li>
				<li>control handler which did it's job is removed from stack;</li>
			</ul>
		</li>
	</ul>
	
	<h2>Catch handlers</h2>
	Those handlers are expected to be enclosed in "control handlers" 
	and serves just one purpose: to consume up to some limit of characters
	and test if they do match the requested text.
*/
public abstract class ATxtReadFormatStateBase0<TSyntax extends ATxtReadFormat1.ISyntax> 
						extends ATxtReadFormat1<TSyntax>
{
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
			
			/* ------------------------------------------------------------------------
			
         				Control state handlers
         				
         	------------------------------------------------------------------------*/
			/**
				A control handler which always throws informing that nothing can be read
				anymore. 
				<p>
				This should be first handler pushed on syntax stack	or You may
				expect {@link NoSuchElementException} to be thrown by various handlers.
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
				/** Always throws */
				@Override protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("CCannotReadHandler.toNextChar()");
					assert( getStateHandler()==this);
					throw new EBrokenFormat(message);
				};
			};
			
			
			/**
				A control handler which do require certain syntax element to appear exactly one time.
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
				@Override protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("CRequiredHandler.toNextChar() ENTER");
					assert( getStateHandler()==this);
					//make it current, replacing ourselves.
					setStateHandler(required);
					//run the recognition phase.
					if (TRACE) TOUT.println("CRequiredHandler.toNextChar()->"+required);
					required.toNextChar();
					//Test if recognized?
					if (syntaxQueueEmpty())
					{
						throw new EBrokenFormat(message);
					};
					if (TRACE) TOUT.println("CRequiredHandler.toNextChar() LEAVE");
				};
			};
			
			/**
				A control handler which can be used to implement repetition
				of certain element, from zero to infinite number of times.
			*/
			protected final class CRepeatHandler extends AStateHandler
			{
							/** Current handler */
							private AStateHandler repeat;
				/** Creates.					
				@param repeat non null a "current handler" which recognizes  
								single occurence of syntax element.
				*/
				protected CRepeatHandler(AStateHandler repeat )
				{
					assert(repeat!=null);
					this.repeat = repeat;
				};
				/** Creates. You must call {@link #initialize} */
				protected CRepeatHandler(){};
				/** Initializes. Can be called only once  
				@param repeat see constructor
				*/
				protected void initialize(AStateHandler repeat )
				{
					assert(repeat!=null);
					this.repeat = repeat;
				};
				@Override protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("CRepeatHandler.next() ENTER");
					assert( repeat!=null ):"not initialized";
					assert( getStateHandler()==this);
					//Push delegate and make current
					pushStateHandler(repeat);
					//call to be sure, that syntax element is recognized.
					repeat.toNextChar();
					//Test if recognized?
					if (syntaxQueueEmpty())
					{
						//we failed to recognize it.
						assert(getStateHandler()==this);
						//remove self from stack.
						popStateHandler();
						if (TRACE) TOUT.println("CRepeatHandler.next() not repeating anymore");
					};
					//else
					//	- in this case repeat is current on stack, so we do not control
					//	  it anymore.
					if (TRACE) TOUT.println("CRepeatHandler.next() LEAVE");
				};
			};
			
			/**
				A control handler which can be used to implement "optional" syntax elemement,
				that is such which may appear zero or exactly one time.
			*/
			protected final class COptionalHandler extends AStateHandler
			{
							/** Will be used to regonize syntax */
							private AStateHandler optional;
				/** Creates.					
				@param optional non null. If this handler does not recognize any syntax 
							({@link #syntaxQueueEmpty} gives true) then this handler
							is popped from a stack and top of stack is tried.
				*/
				protected COptionalHandler(AStateHandler optional)
				{
					assert(optional!=null);
					this.optional = optional;
				};
				/** Creates. You must call {@link #initialize} */
				protected COptionalHandler(){};
				/** Initializes. Can be called only once  
				@param optional see constructor
				*/
				protected void initialize(AStateHandler optional )
				{
					assert(optional!=null);
					assert(this.optional==null):"already initialized";
					this.optional = optional;
				};
				@Override protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("COptionalHandler.toNextChar() ENTER");
					assert( optional!=null ):"not initialized";
					AStateHandler expected_if_not_recognized = null;
					assert ( (expected_if_not_recognized = peekStateHandler())!=null ):"empty stack, it won't work";
					assert( getStateHandler()==this);
					//replace current with optional, so that if it finishes it moves to what
					setStateHandler(optional);
					//do recognition call
					optional.toNextChar();
					if (syntaxQueueEmpty())
					{
						if (TRACE) TOUT.println("COptionalHandler.toNextChar(), optional is not recognized");
						//Not recognized. In this case optional already popped self from stack
						assert( getStateHandler()==expected_if_not_recognized);
						//Now what to do?
						//If we just return we will inform a caller that we failed recognition
						//while in fact next could be recognizing syntax.
						getStateHandler().toNextChar();
					};
					if (TRACE) TOUT.println("COptionalHandler.toNextChar() LEAVE");
				};
			};
			
			/**
				A control handler which can be used to implement "alternative" syntax elemements
				by a regular element handler.
			*/
			protected final class CAlterinativeHandler extends AStateHandler
			{
							/** Set of alternatives, tried in order of appearance*/
							private final AStateHandler  [] alternatives;
							
				/*
					Design notes:
					
						The AStateHandler is seen as a generic class, even tough it
						is not using any parameters. To void generic array creation
						use
						
						new ATxtReadFormatStateBase0.AStateHandler [] {....}
						
						and suppress warnings.
				*/
				/** Creates.					
				@param alternatives array form can't be null, cannot carry nulls.
							Handlers in this array are tried one by one, in order of appearance,
							until a handler which recognize syntax 
							({@link #syntaxQueueEmpty} gives false) is found. This
							handler is becoming current handler. If non recognized syntax
							this control handler is removed from stack.
							<p>
							This array is taken, not copied.
				*/
				@SafeVarargs 
				@SuppressWarnings("varargs")
				protected CAlterinativeHandler(AStateHandler... alternatives)
				{
					assert(alternatives!=null);
					this.alternatives = alternatives;
				};
				@Override protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("CAlterinativeHandler.toNextChar() ENTER");
					AStateHandler expected_if_not_recognized = null;
					assert ( (expected_if_not_recognized = peekStateHandler())!=null ):"empty stack, it won't work";
					assert( getStateHandler()==this);
					//remove self.
					popStateHandler();
					//try-out all alternatives
					for(AStateHandler H : alternatives)
					{
						//replace self with alternative?
						if (TRACE) TOUT.println("CAlterinativeHandler.toNextChar() trying "+H);
						pushStateHandler(H);
						H.toNextChar();
						if (!syntaxQueueEmpty())
						{
							//Alternative is recognized, let it be current and work.
							if (TRACE) TOUT.println("CAlterinativeHandler.toNextChar(), alternative is recognized LEAVE");
							return;
						}else
						{
							assert(getStateHandler()==expected_if_not_recognized);
						};
					}
					//Now we inform caller that nothing is recognized.
					assert(getStateHandler()==expected_if_not_recognized);
					if (TRACE) TOUT.println("CAlterinativeHandler.toNextChar(), none of alternatives matched LEAVE");
				};
			};
			
			/**
				A control handler which can be used to implement a chain of elements.
			*/
			protected final class CNextHandler extends AStateHandler
			{
							/** Current handler */
							private AStateHandler first;
							/** Next handler */
							private AStateHandler next;
				/** Creates.					
				@param first non null a "current handler" which implements 
								first element in this chain.
				@param next non null, next state handler. Will become current after
								<code>current</code> detects that there is nothing more 
								to do.
				*/
				protected CNextHandler(AStateHandler first, AStateHandler next )
				{
					assert(first!=null);
					assert(next!=null);
					this.first = first;
					this.next = next;
				};
				/** Creates. You must call {@link #initialize} */
				protected CNextHandler(){};
				/** Initializes. Can be called only once  
				@param first see constructor
				@param next --//--
				*/
				protected void initialize(AStateHandler first, AStateHandler next )
				{
					assert(first!=null);
					assert(next!=null);
					assert((this.first==null)&&(this.next==null)):"already initialized";
					this.first = first;
					this.next = next;
				};
				protected final void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("CNextHandler.toNextChar() ENTER");
					assert( (first!=null)&&(next!=null)):"not initialized";
					assert( getStateHandler()==this);
					//replace self with next.
					setStateHandler(next);
					//Make current "current"
					pushStateHandler(first);
					//ask it to be run. 
					first.toNextChar();
					//No we have two possibilities: This element recognized syntax
					// or it did not.
					//If it did, we can safely return, because completion will move to next. 
					//If however it did not we must consider what will the one who
					//called us see? If we leave stack untouched the caller will see next
					//and be confused.
					if (syntaxQueueEmpty())
					{
						assert(getStateHandler()==next);
						popStateHandler();
						if (TRACE) TOUT.println("CNextHandler.toNextChar(), first failed to recognize sequence");
					};
					if (TRACE) TOUT.println("CNextHandler.toNextChar() LEAVE");
				};
			};
			
			
			
			
			
			/* -------------------------------------------------------------------
			
			
						Catch handlers
			
			
			
			
			-------------------------------------------------------------------*/
			/** Base for catch handlers and handlers which do consume some text */
			protected abstract class AConsumingHandler extends AStateHandler
			{
							/** A collection buffer. Persistent instance. */
							protected final StringBuilder collected = new StringBuilder();
							
				/* ***********************************************************
				
							Services required from subclasses.
				
				************************************************************/
				/** Reads single java character from down stream.
				@return -1 if end-of-file, 0...0xFFFF if found a character.
				@throws IOException if failed.
				*/
				protected abstract int readImpl()throws IOException;
				/** Un-reads character back to down-stream
				@param c what to un-read
				@throws IOException if failed
				*/
				protected abstract void unread(char c)throws IOException;
				
				/* ***********************************************************
				
							Services for subclasses
				
				************************************************************/
				/** Un-reads specified string buffer
				@param collected what to un-read
				@throws IOException if failed
				*/
				protected void unread(StringBuilder collected)throws IOException
				{
					for(int i=collected.length(); --i>=0;)
					{
							unread(collected.charAt(i));
					};
				};
				/** Un-reads all collected characters.
				@throws IOException if failed
				*/
				protected void unreadCollected()throws IOException{ unread(collected); };
				
				/** Calls {@link #readImpl}. If finds eof queues eof syntax.
				@return -1 if end-of-file, 0...0xFFFF if found a character.
				@throws IOException if failed.
				*/
				protected final int read()throws IOException
				{
					final int r = readImpl();
					assert((r>=-1)&&(r<=0xFFFF));
					if (r==-1)
					{
						if (TRACE) TOUT.println("AConsumingHandler.read()-> queues EOF syntax");
						queueNextChar(-1,null);
					};
					return r;
				};
				/** Calls {@link #readImpl}. If finds eof throws
				@return 0...0xFFFF if found a character.
				@throws IOException if failed.
				@throws EUnexpectedEof if found end-of-file.
				*/
				protected final char readNoEof()throws IOException
				{
					final int r = readImpl();
					assert((r>=-1)&&(r<=0xFFFF));
					if (r==-1) throw new EUnexpectedEof();
					return (char)r;
				};
				
				/* ***********************************************************
				
							AStateHandler
				
				************************************************************/
				/** Wipes collection buffer */
				@Override protected void onEnter()
				{
					if (TRACE) TOUT.println("AConsumingHandler.onEnter()");
					collected.setLength(0);
				};
				/** Wipes collection buffer. Subclasses should be aware that
				if they will alter current state handler this method will
				be called and collection buffer will be wiped.*/
				@Override protected void onLeave()
				{
					if (TRACE) TOUT.println("AConsumingHandler.onLeave()");
					collected.setLength(0);
				};
			};
			
			/** A "catch phrase" state which collects some characters during "recognition phase"
			and tests them against a "catch phrase". The catch phrase is consumed
			and {@link #onCatchPhraseCompleted} is called.
			*/
			protected abstract class ACatchPhrase extends AConsumingHandler
			{		
				/* ***********************************************************
				
							Services required from subclasses.
				
				************************************************************/
				/** Should test if collected buffer represents a catch-phrase.
				@param collected the {@link #collected} containig catch phrase
				@return state of collection process:
						<ul>
							<li>-1 if this can't be a catch phrase;</li>
							<li>0 if needs more characters;</li>
							<li>1 if it is a catch phrase;</li>
						</ul>
				*/
				protected abstract int isCatchPhrase(StringBuilder collected)throws IOException;
				/** Called right after {@link #isCatchPhrase} returned 1. Should queue a proper
				syntax, at least <code>queueNextChar(0,TIntermediateSyntax.VOID)</code>
				to indicate that it recognized it and alter state according to what
				should happen next.
				<p>
				Default implementations queues 0,VOID and pop state handler
				thous moving to next element. This is a behavior for required but meaning-less
				catch-phrases.
				<p>
				After returns from this method collection buffer is wiped out.
				@param collected the buffer with collected catch-phrase
				@throws IOException if failed.
				*/
				@SuppressWarnings("unchecked")
				protected void onCatchPhraseCompleted(StringBuilder collected)throws IOException
				{
					if (TRACE) TOUT.println("ACatchPhrase.onCatchPhraseCompleted() ENTER");
					queueNextChar(0,(TSyntax)TIntermediateSyntax.VOID);
					popStateHandler();
					if (TRACE) TOUT.println("ACatchPhrase.onCatchPhraseCompleted() LEAVE");
				};
				/* ***********************************************************
				
							AStateHandler
				
				************************************************************/
				/** Processes, trying to match catch-phrase. Will throw if
				finds end-of-file during this process.
				*/
				@Override protected void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("ACatchPhrase.toNextChar() ENTER");
					//it must be a recognition phase.
					assert(collected.length()==0);
					for(;;)
					{
						collected.append(readNoEof());
						switch(isCatchPhrase(collected))
						{
							case -1:
										assert(syntaxQueueEmpty());//must indicate no recognition.
										//cannot be, un-read it in proper order.
										unreadCollected();
										popStateHandler();
										if (TRACE) TOUT.println("ACatchPhrase.toNextChar(), not a catch phrase LEAVE");
										return;
							case  0:	
										//needs to read more;
										if (TRACE) TOUT.println("ACatchPhrase.toNextChar(), needs more");
										break;
							case  1:
										//is catch-phrase
										if (TRACE) TOUT.println("ACatchPhrase.toNextChar(), found a catch phrase");
										onCatchPhraseCompleted(collected);
										assert(!syntaxQueueEmpty());//must indidicate that we recognized phrase.
										if (TRACE) TOUT.println("ACatchPhrase.toNextChar() LEAVE");
										return;
						}
					}
				};
			};
			
			/** A "catch phrase" state which recognizes a specific text.
			*/
			protected abstract class ARequiredPhrase extends ACatchPhrase
			{
								/** Text to recognized, case sensitive */
								private final String required;
					
					/** Creates
					@param required text to recognize, case sensitive, non null 
					*/
					protected ARequiredPhrase(String required)
					{
						assert(required!=null);
						this.required = required;
					};
					
					@Override protected int isCatchPhrase(StringBuilder collected)throws IOException
					{
						return SStringUtils.canStartWithCaseSensitive(required,collected);
					};
			};
			/** A "catch phrase" state which recognizes a specific text.
			*/
			protected abstract class ARequiredPhraseCaseInsensitive extends ACatchPhrase
			{
								/** Text to recognized, case insensitive */
								private final String required;
					
					/** Creates
					@param required text to recognize, case insensitive, non null 
					*/
					protected ARequiredPhraseCaseInsensitive(String required)
					{
						assert(required!=null);
						this.required = required;
					};
					
					@Override protected int isCatchPhrase(StringBuilder collected)throws IOException
					{
						return SStringUtils.canStartWithCaseInsensitive(required,collected);
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
	/** Returns state handler which would become current after {@link #popStateHandler}
	@return null if stack is empty */
	protected final AStateHandler peekStateHandler()
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