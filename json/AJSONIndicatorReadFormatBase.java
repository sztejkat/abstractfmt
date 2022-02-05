package sztejkat.abstractfmt.json;
import sztejkat.abstractfmt.CAdaptivePushBackReader;
import static sztejkat.abstractfmt.util.SHex.HEX2D;
import java.io.IOException;
import java.io.Reader;


/**
	A base low-level operations.
	
*/
abstract class AJSONIndicatorReadFormat extends AJSONFormat implements IIndicatorReadFormat
{
				/** Low level input */
				private final CAdaptivePushBackReader input;
				/** A count for maximum consequent insignificant
				white spaces before reporting an error */
				private final int max_consequence_whitespaces;
				/** A bound, limitied buffer for colleting all
				elements, tokens and etc. Non final because can be re-initialized when
				name limit is set since it must also accomodate
				short-encoded begin signal names.
				*/
				private CBoundAppendable token_buffer;
				
	/* ***************************************************************************
	
	
			Construction
	
	
	****************************************************************************/					
	/**
		Creates
		@param input direct input, non null.
		@param settings settings, non null
		@param max_consequence_whitespaces c count for maximum consequent insignificant
				white spaces before reporting an error. This is a protection against
				denial of service attack when source is producing an infinite sequence
				of insignificat space characters between JSON tokens.
	*/
	AJSONIndicatorReadFormat(Reader input,
							CJSONSettings settings,
							int max_consequence_whitespaces
							 )
	{
		super(settings);
		assert(input!=null);
		assert(max_consequence_whitespaces>=1);
		this.input = new CAdaptivePushBackReader(input,1,16);
		this.max_consequence_whitespaces=max_consequence_whitespaces;
		setTokenBufferCapacity(1024);
	};
	/* ***************************************************************************
	
	
			Settinsg
	
	
	****************************************************************************/	
	/** Sets maximum size of token buffer for {@link #readStringValue} and {@link #readValue}.
	@param cap the required size of token buffer. The actuall size of token buffer will
		be maximum of this value, maximum length of reserved words in JSON settings and
		a maximum length of double decimal number
	*/ 
	protected final void setTokenBufferCapacity(int cap)
	{
		token_buffer = new CBoundAppendable(
					Math.max(settings.getMaximumReserverWordLength(),
								Math.max(cap,37)	//37 is max float number length
								)
								);
	};
	/* *************************************************************************
	
	
				Character access
	
	
	* *************************************************************************/
	/** Reads character from input
	@return 0...0xFFFF or -1 at eof.
	*/
	protected final int read()throws IOException{ return input.read(); };
	/** Reads from input and throws on eof
	@return read value
	@throws IOException if failed at low level
	@throws EUnexpectedEof if reached end of file.
	*/
	protected char readAlways()throws IOException,EUnexpectedEof
	{
		int c = input.read();
		if (c==-1) throw new EUnexpectedEof();
		return (char)c;
	};
	/**
		Reads string character, unescaping it
		@return 0...0xFFFF character, -1 for EOF, -2 for closing "
		@throws EBrokenFormat if encountered an escape it cannot understand.					
	*/
	protected int readStringChar()throws EBrokenFormat,IOException
	{
		int c = input.read();
		if (c==-1) return -1;
		if (c=='\"') return -2;
		if (c=='\\')
		{
			char d = readAlways();
			switch(d)
			{
				case '\"': 
				case '\\': 
				case '/': return d;
				case 'b': return (char)0x0008;
				case 'f': return (char)0x000C;
				case 'n': return (char)0x000A;
				case 'r': return (char)0x000D;
				case 't': return (char)0x0009;
				case 'u':
						{							
							int v = 0;
							for(int i = 0; i<4; i++)
							{
								int d0 = HEX2D (readAlways());
								if (d0==-1) throw new EBrokenFormat("Not a hex digit");
								v = v<<4;
								v = v | d0
							}
							return (char)v;
						};
				default: throw new EBrokenFormat("Unknow escape character \'"+(char)c+"\'");				
			};
		};
		return (char)c;
	};
	/* *************************************************************************
	
	
				Token access.
	
	
	* *************************************************************************/	
	/**
		Skips all available continous white spaces.
		At the return from this method stream is either at EOF
		or at non-white space character.
		@throws EFormatBoundaryExceeded if there was too many white-space characters.
	*/
	protected final void skipWhitespaces()throws IOException,EFormatBoundaryExceeded
	{
		int c = max_consequence_whitespaces;
		for(;;)
		{
			int c = input.read();
			if (c==-1) break;
			if (!Character.isWhitespace(c))
			{
				input.unread((char)c);
				break;
			}else
			{
				if (c==0) throw new EFormatBoundaryExceeded("Too many insignifcant white spaces, allowed up to "+max_consequence_whitespaces);
			 	c--;			 	
			};
		}
	};
	/** Reads double quote enclosed string value into {@link #token_buffer}. 
	 Expects cursor to be at or before first character of a token, possibly
	 in white spaces. 
	@return {@link #token_buffer}
	@throws EFormatBoundaryExceeded if token is too long.
	@throws EUnexpectedEof if reached end of file
	@throws EBrokenFormat if encounterd unknown escape.
	@see #setTokenBufferCapacity
	*/
	private final CBoundAppendable readStringValue()throws IOException,
												 EUnexpectedEof,
												 EBrokenFormat,
												 EFormatBoundaryExceeded
	{
		skipWhitespaces();				
		token_buffer.reset();
		char c = readAlways();
		if (c!='\"') throw new EBrokenFormat("Expected \" but \'"+c+"\' was found");
		for(;;)
		{
			int d=readStringChar();
			switch(d)
			{
				case -1: throw new EUnexpectedEof();
				case -2: break;
				default: token_buffer.append((char)d);
			}
		};
		return token_buffer;
	};
	/** Reads value token into into {@link #token_buffer}. .
	Expects cursor to be at or before first character of a token, possibly
	in white spaces. If first token character is " processed token
	using {@link #readStringChar}.
	<p>
	If first character was not " 
	reads it until whitespace, comma or ],} are encountered. 
	The token terminator is put back to stream.
	@return {@link #token_buffer}
	@throws EFormatBoundaryExceeded if token is too long.
	@throws EUnexpectedEof if reached end of file
	@throws EBrokenFormat if encountered { [ or "
	*/	
	private final CBoundAppendable readValue()throws IOException,
												 EUnexpectedEof,
												 EBrokenFormat,
												 EFormatBoundaryExceeded
	{		
		skipWhitespaces();				
		token_buffer.reset();
		//Read first character
		char c = readAlways();
		if (c=='\"')
		{
			input.unread(c);
			return readStringValue();
		};
		for(;;)
		{
			c = readAlways();			
			if (Character.isWhitespace(c) || (c==',') || (c=='}') || (c==']'))
			{
				 input.unread(c);
				 break;
			};
			if ((c=='\"')||(c=='[')||(c=='{')) throw new EBrokenFormat("Unexpected \'"+c+"\'");
			token_buffer.append(c);
		};
		return token_buffer;
	};
	/* *****************************************************************************
	
	
			JSON state machine
			
			Designers note:
			
				Even tough at the first glance JSON looks simpler than
				XML this is not always the case. With XML we can just
				crawl through the text to encounter < to find an indicator
				and clearly tell apart <start> from </end>. The only problematic
				case is could be if  < would used in a name of an attribute or value of an
				attribute, but this is a malformed XML. So the scanning for
				skipping content is trival.
				
				JSON in its simplicity is not that trivial nad does not 
				conform a valid "markup" format. Especially following:
				
				{"{blank}":"{ruppert[]}"} is a valid JSON. This means that plain
				scanning through JSON stream is not good enough to skip content
				which we are not interrested in.
				
				Second problem is, that JSON is using different kinds of separators
				for different purposes. In XML the structural information is provided
				by <tags>. In JSON by { , and [ ] } " ".
				
				This means that to be able to actually skip content we do ignore
				we need to be able to parse stream into a correct sequence of JSON
				elements.
			
	
	* *****************************************************************************/
			/** Enumerates JSON elements we are going to parse*/
			protected static enum TJSONElement
			{
				/** Detected , */
				LIST_SEPARATOR,
				/** Dected  { */
				START_OBJECT,
				/** Detected :  */
				PAIR_SEPARATOR,
				/** Detected [ */
				START_ARRAY,
				/** Detected ] */
				END_ARRAY,
				/** Detected } */
				END_OBJECT,
				/** A non-string value. Our parser do ignore what it exactly is */
				NS_VALUE,
				/** A double quoted enclosed string value */
				STRING_VALUE
			};
			/** Enumerates JSON processing state 
			and provides basic information about what kind
			of state it is.
			<p>
			State is just a name for a bunch of state transitions
			which are deciding from-to traveration over processing
			states.
			@see Transition
			*/
			private static enum TJSONState
			{
					/** Start of JSON file. Nothing was read
					yet. The system expects a JSON value
					to be next.
					*/
					START(),
					/** End of JSON file.
					This state means that the processing of
					top level value was finished. If this is 
					a fully enclosed state, then this is a true
					end of file. If however file is not fully
					enclosed, this state can transit to other states
					due to {@link TJSONElement#LIST_SEPARATOR}
					*/
					END(),
					/** An object. Expectes name of name-value pair */
					PAIR_NAME(),
					/** An object. Expects name-value separator */
					PAIR_SEPARATOR(),
					/** An object, expectes value in name-value pair*/
					PAIR_VALUE(),
					/** An object, after reading the value from name-value pair,
					expects value separator and then next pair name or object end*/
					PAIR_NEXT(),
					/** Array item, either beginning of an array or after reading
					an array element. Expects value */
					ARRAY_ITEM(),
					/** Array item, expects array value separattor */
					ARRAY_NEXT()
					
					
					private TJSONState()
					{
						
					};
			};			
			/** State transition for JSON processing state machine.
			The JSON processing state machine is a stack on which
			top element can transit from one state to another due
			to finding a certain {@link TJSONElement}, state can be
			pushed or popped from stack.
			*/
			private static class Transition
			{
					/** From which state. Non null. */
					private final TJSONState from;
					/** Due to what found JSON element this transition
					should be triggered.*/
					private final TJSON due_to;
					/** State to which transit to at once
					or null if pop state form a stack */
					private final TJSONState to;
					/** State which push on stack before transition to state <code>to</code>,
					null to not push anything.
					Must be null if {@link #to} is null.
					*/
					private final TJSONState push;
					
					/** Defines transtion without stack involved. */
					Transition(TJSONState from,TJSON due_to,TJSONState next)
					{
						this.from =from;
						this.due_to = due_to;
						this.to = next;
						this.push = null;
					};
					/** Defines transtion with stack involved stack involved. */
					Transition(TJSONState from,TJSON due_to,TJSONState push, TJSONState next)
					{
						this.from =from;
						this.due_to = due_to;
						this.to = next;
						this.push = push;
					};
					/** Defines transtion with which returns to state pushed on stack. */
					Transition(TJSONState from,TJSON due_to)
					{
						this.from =from;
						this.due_to = due_to;
						this.to = null;
						this.push = null;
					};
			};
			/** State transitions for correct, fully enclosed JSON */
			private static Transition [] transitions = 
				new Transition []
				{
					//Initial state, 
					//After initial state we basically expect, object, array or single value.
					//If we find beginning of an array
					new Transition(TJSONState.START, TJSONElement.START_ARRAY, TJSONState.END, TJSONState.ARRAY),
					//if we find a beginning of an object
					new Transition(TJSONState.START, TJSONElement.START_OBJECT, TJSONState.END, TJSONState.PAIR_NAME),
					//if we find a non-string value we just have to read it.
					new Transition(TJSONState.START, TJSONElement.NS_VALUE, TJSONState.END),
					//if we find a string value we just have to read it.
					new Transition(TJSONState.START, TJSONElement.STRING_VALUE, TJSONState.END),
					
					//Termial state.
					//This may depend on if we are fully enclosed or not. This state machine just
					//allows to re-transit to start.
					new Transition(TJSONState.END,TJSONElement.LIST_SEPARATOR,TJSON.START),
					
					//Now process the object
					//Object starts with PAIR_NAME state.
					//An initial phase is just a stright-forward transition of states.
					new Transition(TJSONState.PAIR_NAME, TJSONElement.STRING_VALUE, TJSONState.PAIR_SEPARATOR),
					new Transition(TJSONState.PAIR_SEPARATOR, TJSONElement.PAIR_SEPARATOR, TJSONState.PAIR_VALUE),
					//And now we have object name-value pair, the value.
					//This may be again, the same as in START object, array or single value.
					//If we find beginning of an array
					new Transition(TJSONState.PAIR_VALUE, TJSONElement.START_ARRAY, TJSONState.PAIR_NEXT, TJSONState.ARRAY),
					//if we find a beginning of an object
					new Transition(TJSONState.PAIR_VALUE, TJSONElement.START_OBJECT, TJSONState.PAIR_NEXT, TJSONState.PAIR_NAME),
					//if we find a non-string value we just have to read it.
					new Transition(TJSONState.PAIR_VALUE, TJSONElement.NS_VALUE, TJSONState.PAIR_NEXT),
					//if we find a string value we just have to read it.
					new Transition(TJSONState.PAIR_VALUE, TJSONElement.STRING_VALUE, TJSONState.PAIR_NEXT),
					//And what happens if we expect the next pair in object? Basically either end of an object
					//or list and next name-value pair
					//End object. We just pop what is on stack.
					new Transition(TJSONState.PAIR_NEXT, TJSONElement.END_OBJECT),
					//Next pair
					new Transition(TJSONState.PAIR_NEXT,TJSONElement.LIST_SEPARATOR,TJSONState.PAIR_VALUE),
					
					
					//Now what happens inside an array.
					new Transition(TJSONState.ARRAY, TJSON.TRUE, TJSONState.ARRAY_NEXT),
					new Transition(TJSONState.ARRAY, TJSON.FALSE, TJSONState.ARRAY_NEXT),
					new Transition(TJSONState.ARRAY, TJSON.NULL, TJSONState.ARRAY_NEXT),
					new Transition(TJSONState.ARRAY, TJSON.NUMBER, TJSONState.ARRAY_NEXT),
					new Transition(TJSONState.ARRAY, TJSON.STRING_VALUE, TJSONState.ARRAY_NEXT),
					new Transition(TJSONState.ARRAY, TJSON.START_OBJECT, TJSONState.ARRAY_NEXT,TJSONState.PAIR_NAME),
					new Transition(TJSONState.ARRAY, TJSON.START_ARRAY, TJSONState.ARRAY_NEXT,TJSONState.ARRAY),					
					
					
					new Transition(TJSONState.ARRAY, TJSON.END_ARRAY),
					new Transition(TJSONState.ARRAY_NEXT, TJSON.END_ARRAY),
					new Transition(TJSONState.ARRAY_NEXT, TJSON.LIST_SEPARATOR, TJSONState.ARRAY)
				}
				
				private final ArrayList<TJSONState> state_stack;				
				private final TJSONState current;
			
	/**
		Checks what JSON element is to be read next.
		Does not read this token, but prepares getter methods
		@return element 
		@throws EBrokenFormat if found element is not allowed in current state. 
	*/
	protected final TJSONElement getJSONElement()throws EBrokenFormat
	{
		//first attempt to read what kind of element is available in stream
		
		
		//Then validate if current state allows such element. 
		
		//Store element type to enable getter methods
		
		//store state transition to let it happen once getter is called.
	};
	/** True if cursor is currently inside an array. Does not take in an account any recursive arrays. */
	protected final boolean isArray(){ return current.is_array; };
	/** True if cursor is currently inside an object. Does not take in an account any recursive arrays. */
	protected final boolean isObject(){ return current.is_object; };
	/** Depth of objects and arrays currently processed */
	protected final int getDepth(){ return state_stack.size() + (current.is_object || current.is_array ? 1 : 0); };
	/** Moves cursor after JSON token indicated by {@link #getJSONToken} */
	protected final void nextJSONToken()
	/** Reads {@link TJSON#TRUE},{@link TJSON#FALSE},{@link TJSON#NULL},{@link TJSON#NUMBER}
	to shared buffer and returns it */
	protected final CBoundAppendable readJSONValue()
	/** Reads {@link TJSON#STRING} unescaping it, to shared buffer and returns it */
	protected final CBoundAppendable readJSONString()
	/** Reads single character of {@link TJSON#STRING}, unescaping it, returns -1 of reached end of string. */ 
	protected final int readJSONStringChar()
	
};