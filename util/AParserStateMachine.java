package sztejkat.abstractfmt.util;
import java.util.Deque;
/**
	A parser state machine.
	<p>
	Managing even simplest parser can be done in an easy and consistent
	way by using a kind of state-driven parser. This state-driven parser
	decides what kind of <i>element</i> is allowed in what kind of <i>state</i>
	and depending on that element transits to other state or throws an exception
	informing that the syntax is incorrect.
	<p>
	Of course this is possible to parse any format without such a state machine,
	but using state machine allows easy validation of format syntax. This is especially
	usefull if we are using just a part of original format specification but we
	like to be robust against malformed input files. 
	<p>
	This class provides a <i>transition driven</i> state machine which do support
	recursive state transitions.
	
	<h1>Class parameters</h1>
	This class takes two parameters:<code>TState</code> and <code>TElement</code>.
	Both are expected to be <code>Enum</code> types and by default the <code>==</code> operator
	will be used for the equality tests.
	<p>
	The <code>TState</code> represents a current state of state machine
	and can be retrived from {@link #getState}.
	<p>
	The <code>TElement</code> represents a type of an element which is found in stream.
	
	<h1>Thread safety</h1>
	This machine is NOT thread safe.
*/
public abstract class AParserStateMachine<TState extends Object, TElement extends Object>
{
	/* *******************************************************************************************
	
	
				State syntax definitions
					
	
	
	********************************************************************************************/
		/** Represents a single allowed state transtion.
		<p>
		A state transition is an action of moving from one <i>state<i> to an 
		another state due to the effect of encountering an <i>element</i>
		inside a stream.
		<p>
		We have three kinds of state transition:
		<ol id="TRANSITION_KIND">
			<li>Stright, representing motion from state to state: <code>from_state &rarr; due_to_element ; &rarr; to_state</code>;</li>
			<li>Enterig, representing a beginning of a recursive state:
				<pre>
					from_state &rrar; due_to_element &rrar; to_state
						<i>push</i> next_state
				</pre>
			</li>
			<li>Returning, representing an end of entered state:
				<pre>
					from_state &rrar; due_to_element &rrar; <i>pop</i> next_state
				</pre>
			</li>
		</ol> 
		*/
		public static class Transition<TState exteds Object, TElement extends Object>
		{
						/** From which state. Non null. */
						private final TState from;
						/** Due to what found element this transition
						should be triggered. Null is a valid element.*/
						private final TElement due_to;
						/** State to which transit to at once
						or null if pop state form a stack */
						private final TState to;
						/** State which push on stack before transition to state <code>to</code>,
						null to not push anything.	Must be null if {@link #to} is null.
						*/
						private final TState push;
			/** Defines "stright" transition
			@param from a state from which transit, non null
			@param due_to an element which triggers the transition, null is a valid element.
			@param next a destination state, non null
			*/
			Transition(TState from,TElement due_to,TState next)
			{
				assert(from!=null);
				assert(next!=null);
				this.from =from;
				this.due_to = due_to;
				this.to = next;
				this.push = null;
			};
			/** Defines "entering" transition
			@param from a state from which transit, non null
			@param due_to an element which triggers the transition, null is a valid element.
			@param push a state to be pushed on stack, non null
			@param next a destination state, non null
			*/
			Transition(TState from,TElement due_to,TState push, TState next)
			{
				assert(from!=null);
				assert(push!=null);
				assert(next!=null);
				this.from =from;
				this.due_to = due_to;
				this.to = next;
				this.push = push;
			};
			/** Defines "returning" transition
			@param from a state from which transit, non null
			@param due_to an element which triggers the transition, null is a valid element.
			*/
			Transition(TState from,TElement due_to)
			{
				assert(from!=null);				
				this.from =from;
				this.due_to = due_to;
				this.to = null;
				this.push = null;
			};		
			
			/**	
				This method is called each time state machine checks, if transition
				matches the current state of machine and found element.
				<p>
				Default implementation is using <code>==</code> operator.
				
				@param state current state of state machine
				@param element currently found element
				@return true if state transition matches.
			*/
			protected boolean matches(TState state, TElement element)
			{
				return this.from==state && this.due_to==element;
			};
			/** Checks if transition is of specified <a href="#TRANSITION_KIND">kind</a>
			@return if is of that kind
			*/ 
			private final boolean isStright(){ return (push==null)&&(to!=null); };
			/** Checks if transition is of specified <a href="#TRANSITION_KIND">kind</a>
			@return if is of that kind
			*/ 
			private final boolean isEntering(){ return (push!=null)&&(to!=null); };
			/** Checks if transition is of specified <a href="#TRANSITION_KIND">kind</a>
			@return if is of that kind
			*/ 
			private final boolean isReturning(){ return (push==null)&&(to==null); };
			
			/**
				Invoked when transition did happen.
				Default: empty.
			*/
			protected void onTransitionMade(){};
			
			public String toString()
			{
				if (isStright())
				{
					return from+" -->-- "+due_to+" ---> "+to)
				}else
				if (isEntering())
				{
					return from+" -->-- "+due_to+" ---> (push:"+push+") --> "+to)
				}else
				if (isReturning())
				{
					return from+" -->-- "+due_to+" ---> (pop)")
				}else
					throw new AssertionError();
			};
		}; 
		
		
	/* *******************************************************************************************
	
	
				Reported errors
					
	
	
	********************************************************************************************/
	
	
		/** Thrown if recursion is too deep */
		public static final class EParsingStackOverflow extends EFormatBoundaryExceeded
		{
				private static final long serialVersionUID=1L;
			private EParsingStackOverflow(){};
			private EParsingStackOverflow(String msg){ super(msg); };
		};
		/** Thrown if there is no transition which matches an encountered element */
		public static final class ENotAllowedElement extends EBrokenFormat
		{
				private static final long serialVersionUID=1L;
			private ENotAllowedElement(){};
			private ENotAllowedElement(String msg){ super(msg); };
		};
	/* *******************************************************************************************
	
	
				Data
					
	
	
	********************************************************************************************/	
		
					/** A current state of state machine, non null. */
					private TState current_state;
					/** A stack, lazy built */
					private Deque<TState> stack;
					/** An optional stack limit. This value represents
					a maximum size of processing stack after which
					the {@link EParsingStackOverflow} is thrown.
					-1 disables the limit. Default is: disabled */
					private final stack_limit;
					/** A defined syntax. May be updated */
					private Transition<TState,TElement> [] syntax;
					
	/* *******************************************************************************************
	
	
				Creation & setup.
					
	
	
	********************************************************************************************/
	/**
		Creates state machine which is defined syntax.
		
		@param initial_state non-null initial state
		@param syntax non-null array of non-null elements representing state machine syntax.
				This array is taken as it is and is not copied.
				<p>
				Syntax array is searched for match linearry from beginning (syntax[0]) towards the end.
				
		@param stack_limit optional stack limit. If stack size is to exceed this value
			the {@link EParsingStackOverflow} is thrown. If -1 limiting is disabled. 
	*/
	protected AParserStateMachine(TState initial_state, Transition<TState,TElement> [] syntax, int stack_limit)
	{
		assert(initial_state!=null);
		assert(syntax!=null);
		assert(stack_limit>=-1);
		
		this.current_state = initial_state;
		this.stack_limit=stack_limit;
		this.syntax=syntax;
	}
	/** Allows to add transitions to existing set. This method can be used if portion of syntax depends
	on state machine settings.
	@param syntax syntax to append, non-null, not containing nulls. This array is copied. The process of copying
		detaches orginally attached array passed in constructor and creates a copy containing sum of both arrays.
		This array is appended at the <u>beginning</u> of current syntax array.
	*/
	protected void prependSyntax(Transition<TState,TElement> [] syntax)
	{
		assert(syntax!=null);
		if ( syntax.length==0) return;
		
		Transition<TState,TElement> [] n = (Transition<TState,TElement>) new Transition[syntax.length + this.syntax.length]
		System.arraycopy(syntax,0,n,0,syntax.length);
		System.arraycopy(this.syntax,0,n,syntax.length,this.syntax.length);		
		this.syntax = n;
	};
	/* *******************************************************************************************
	
	
				Action
					
	
	
	********************************************************************************************/
	public void TState next(TElement element)throws ENotAllowedElement,EParsingStackOverflow
	{
		//Identify the state transition
		TState c = current_state;
		for(Transition<TState,TElement> t:syntax)
		{
			if (t.matches(c,element))
			{
				if (t.isStright())
				{
					...to do
				}else
				if (t.isEntering())
				{
				}else
				{
				}
			};
		};
	};
}
