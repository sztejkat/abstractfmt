package sztejkat.abstractfmt.txt.json;
import sztejkat.abstractfmt.txt.ATxtWriteFormat1;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Writer;

/**	
	A JSON format writer.
*/
public class CJSONWriteFormat extends ATxtWriteFormat1
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CJSONWriteFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CJSONWriteFormat.",CJSONWriteFormat.class) : null;
 
         		/** Where to write. Protected to allow some data injcection in superclasses modifications */
				protected final Writer out;
				/** An escaping engine for JSON string elements which will be used
				for signal names and string tokens.*/
				private final AJSONEscapingEngine escaper = new AJSONEscapingEngine()
				{
					@Override protected void out(char c)throws IOException
					{
						CJSONWriteFormat.this.out.write(c);
					};
				};
				
				private static enum TState
				{
					/** If "begin" was written and first plain or char token
					is to be collected. If this state is active
					when "end" signal is written it means we have an empty structure.
					If it is reached when "begin" signal is written means
					that begin signal is a first element in a parent structure.
					*/
					DEDUCE_SINGLE_ELEMENT,
					/** If "begin" was written and first plain token is beeing collected.*/
					DEDUCING_PLAIN_SINGLE_ELEMENT,
					/** If "begin" was written and first char token is beeing collected.*/
					DEDUCING_CHAR_SINGLE_ELEMENT,
					/** If first token was collected or first token was a string token
					or otherwise we are already in JSON array mode used to write 
					multi-element or variable size element structure. */
					NEXT_ELEMENT,
					/** Mens that we are in array mode but no token was written yet
					and no token collection is in progress.
					Practically only after opening a file
					*/ 
					FIRST_ELEMENT
				}
				
				private TState state; 
				
				/** Used to collect first single primitive element in 
				structure, in raw form */
				private final StringBuilder first_element = new StringBuilder();
				
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates
	@param out writer where to write, non null, opened. Must accept all
		possible <code>char</code> values. If directed to true byte level
		I/O stream the stream encoding is recommended to be UTF-8 even tough JSON 
		standard does not reuqire that directly.
	*/
	public CJSONWriteFormat(Writer out)
	{
		super(0);	//We do not support registered names.
					//Due to JAVA lacking virtual multiple inheritance
					//it was for me easier to inherite the ARegisteringStructWriteFormat
					//in generic text support and then disable it here rather
					//than playing with class composition.
		if (TRACE) TOUT.println("new CJSONWriteFormat()");
		assert(out!=null);
		this.out = out;
	};		
	
	/* *****************************************************************
	
				ATxtWriteFormat1/ATxtWriteFormat0
	
	******************************************************************/
	/* -----------------------------------------------------------
			Related to single element optimization
	-----------------------------------------------------------*/
	
	/** Invoked in place where first element must be written 
	unconditionally.
	<p>
	Writes it and drops any information about pending element.
	@throws AssertionError if state is incorrect.
	@throws IOException if failed.
	*/
	private void flushFirstElement()throws IOException
	{
		if (TRACE) TOUT.println("writePendingFirstElement() state="+state+" ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
						throw new AssertionError("state="+state);
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
						if (TRACE) TOUT.println("writePendingFirstElement() plain element");
						out.append(first_element);
						first_element.setLength(0);
						break;
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						if (TRACE) TOUT.println("writePendingFirstElement() char element");
						//push it through escaper as a JSON string
						out.append('\"');
						escaper.reset(); //we need to run complete escaper sequence.
						escaper.append(first_element); // in theory it can be multi-char.
						escaper.flush();
						out.append('\"');
						first_element.setLength(0);
						break;
			default:
						throw new AssertionError("state="+state);
		};
		if (TRACE) TOUT.println("writePendingFirstElement() LEAVE");
	};
	
	
	/* ----------------------------------------------------------
			Related to "," appearing before sub structure
	----------------------------------------------------------*/	
	@Override protected void outBeginSignalSeparator()throws IOException{};
	@Override protected void outEndSignalSeparator()throws IOException
	{
		if (TRACE) TOUT.println("outEndSignalSeparator() state="+state+" ENTER");
		//end signal is always followed by separator
		out.write(',');
		if (TRACE) TOUT.println("outEndSignalSeparator() LEAVE");
	};
	@Override protected void outTokenToEndSignalSeparator()throws IOException{};
	@Override protected void outTokenToBeginSignalSeparator()throws IOException
	{
		//begin signal after token is always preceeded by separator
		//however due to single element detection it has to be left to
		//beginDirectImpl.
	};
	/* ----------------------------------------------------------
			Again, related to handling first element optimization
			and just doing the output
	----------------------------------------------------------*/
	@Override protected void outTokenSeparator()throws IOException
	{
		if (TRACE) TOUT.println("outTokenSeparator() state="+state+" ENTER");
		//Separator indicates that at least one element was written
		//so for sure we won't be single element.
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
						//this would mean that no token was written. This won't happen.
						throw new AssertionError("state="+state);
			case DEDUCING_PLAIN_SINGLE_ELEMENT:				
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						out.write('[');	//array mode.
						flushFirstElement();
						state = TState.NEXT_ELEMENT;
						break;
			case NEXT_ELEMENT:
						break;
			case FIRST_ELEMENT:
						state = TState.NEXT_ELEMENT;
						break;
		};
		//and add separator as requested.
		out.write(',');
		if (TRACE) TOUT.println("outTokenSeparator() LEAVE");
	};
	
	
	@Override protected void openPlainTokenImpl()throws IOException
	{
		if (TRACE) TOUT.println("openPlainTokenImpl() state="+state+" ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
						//First character of first token
						if (TRACE) TOUT.println("openPlainTokenImpl() first element will be plain");
						state = TState.DEDUCING_PLAIN_SINGLE_ELEMENT;			
						break;
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						throw new AssertionError("state="+state);
			case NEXT_ELEMENT:
			case FIRST_ELEMENT:
						break;
		};
		if (TRACE) TOUT.println("openPlainTokenImpl() state="+state+" LEAVE");
	};
	@Override protected void outPlainToken(char c)throws IOException
	{
		if (TRACE) TOUT.println("outPlainToken() state="+state+" ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:			
						throw new AssertionError("state="+state);
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
						if (TRACE) TOUT.println("outPlainToken() collecting first element");
						first_element.append(c);//Collect it in buffer for first element			
						break;
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						throw new AssertionError("state="+state);
			case NEXT_ELEMENT:
			case FIRST_ELEMENT:
						out.write(c);//write for others			
		};
		if (TRACE) TOUT.println("outPlainToken() LEAVE");
	};	
	@Override protected void closePlainTokenImpl()throws IOException
	{
		if (TRACE) TOUT.println("closePlainTokenImpl() state="+state+" ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:			
						throw new AssertionError("state="+state);
			case DEDUCING_PLAIN_SINGLE_ELEMENT:			
						break;
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						throw new AssertionError("state="+state);
			case NEXT_ELEMENT:
						break;
			case FIRST_ELEMENT:
						state=TState.NEXT_ELEMENT;			
		};
		if (TRACE) TOUT.println("closePlainTokenImpl() LEAVE");
	};
	
	
	@Override protected void openSingleCharToken()throws IOException
	{
		//Separate handling from string mode. We need to detect single
		//primitive character as a "single element structure".
		defaultOpenSingleCharToken();
	};
	@Override protected void openSingleCharTokenImpl()throws IOException
	{
		if (TRACE) TOUT.println("openSingleCharTokenImpl()  state="+state+" ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
						//First character of first token
						if (TRACE) TOUT.println("openSingleCharTokenImpl() deducing element is char");
						state = TState.DEDUCING_CHAR_SINGLE_ELEMENT;			
						break;
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						throw new AssertionError("state="+state);
			case NEXT_ELEMENT:
			case FIRST_ELEMENT:
						escaper.reset();
						out.write('\"');//or pass directly			
		}
		if (TRACE) TOUT.println("openSingleCharTokenImpl() LEAVE");
	}
	protected void outSingleCharToken(char c)throws IOException
	{
		if (TRACE) TOUT.println("outSingleCharToken() state="+state+" ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
						throw new AssertionError("state="+state);
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						if (TRACE) TOUT.println("outSingleCharToken() collecting first token ENTER");
						first_element.append(c);
						break;
			case NEXT_ELEMENT:
			case FIRST_ELEMENT:
						escaper.write(c);			
		}
		if (TRACE) TOUT.println("outSingleCharToken() LEAVE");
	};
	
	
	@Override protected void closeSingleCharToken()throws IOException
	{
		defaultCloseSingleCharToken();
	};
	@SuppressWarnings("fallthrough")
	@Override protected void closeSingleCharTokenImpl()throws IOException
	{
		if (TRACE) TOUT.println("closeSingleCharTokenImpl() ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
						throw new AssertionError("state="+state);
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						break;
			case FIRST_ELEMENT:
						state = TState.NEXT_ELEMENT;
						//fallthrough
			case NEXT_ELEMENT:
						escaper.flush();
						out.write('\"');//close it directly			
		};
		if (TRACE) TOUT.println("closeSingleCharTokenImpl() LEAVE");
	}
 	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected void openStringTokenImpl()throws IOException
	{
		if (TRACE) TOUT.println("openStringTokenImpl() ENTER");
		//Note: String tokens do handle string sequences and char [] blocks
		//		in our implementation. Both are variable and potentially
		//		infinite in size so they can't be a part of "single element structure".		
		switch(state)
		{
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						throw new AssertionError("state="+state);
			case DEDUCE_SINGLE_ELEMENT:
						out.write('[');//to array mode.
						state = TState.NEXT_ELEMENT;
						//fallthrough
			case NEXT_ELEMENT:
			case FIRST_ELEMENT:
							escaper.reset();//reset escaping engine.
							out.write('\"');//open the JSON string			
		};

		if (TRACE) TOUT.println("openStringTokenImpl() LEAVE");
	};	
	@Override protected void outStringToken(char c)throws IOException
	{
		if (TRACE) TOUT.println("outStringToken() ENTER");
		assert((state==TState.NEXT_ELEMENT)||(state==TState.FIRST_ELEMENT)):"state="+state;
		escaper.write(c);
		if (TRACE) TOUT.println("outStringToken() LEAVE");
	};
	@SuppressWarnings("fallthrough")
	@Override protected void closeStringTokenImpl()throws IOException
	{		
		if (TRACE) TOUT.println("closeStringTokenImpl() ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						throw new AssertionError("state="+state);
			case FIRST_ELEMENT:
						state = TState.NEXT_ELEMENT;
						//fallthrough
			case NEXT_ELEMENT:
						escaper.flush();
						out.write('\"');//close JSON string			
		};
		if (TRACE) TOUT.println("closeStringTokenImpl() LEAVE");
	};
	
	/* *****************************************************************
	
				ARegisteringStructWriteFormat
	
	******************************************************************/
	/** Always throws, no names registry */
	@Override protected void beginAndRegisterImpl(String name, int index, int order)throws IOException
	{
		throw new AssertionError();
	}
	/** Always throws, no names registry */
	@Override protected void beginRegisteredImpl(int index, int order)throws IOException
	{
		throw new AssertionError();
	}
	/** Used by {@link #begiDirectImpl} to open json object of given name 
	@param name name of object to open.
	@throws IOException if failed */
	protected void writeOpenJSONObject(String name)throws IOException
	{
		out.write("{\"");
				escaper.reset();
				escaper.append(name);
				escaper.flush();
		out.write("\":");
	};
	/** Redirects to state engine since additional actions may be necessary */
	@Override protected void beginDirectImpl(String name)throws IOException
	{
		if (TRACE) TOUT.println("beginDirectImpl(\""+name+"\") state="+state+" ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
						//We are first element in struture, but we are structure,
						//so array mode is necessary
						out.write('[');
						break;
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						//some element was collected, but again we have to go to array
						//mode.
						out.write('[');
						flushFirstElement();
						out.write(',');
						break;
			case NEXT_ELEMENT:
						//We are next.
						out.write(',');
						break;
			case FIRST_ELEMENT:
						//we are first after array is opened and nothing is to be deduced.
						break;			
		};
		writeOpenJSONObject(name);
		state = TState.DEDUCE_SINGLE_ELEMENT;
		if (TRACE) TOUT.println("beginDirectImpl() LEAVE");
	};
	
	/* *****************************************************************
	
			AStructWriteFormatBase0
	
	******************************************************************/
	/** Used by {@link #endImpl} to close JSON object
	@throws IOException if failed */
	protected void writeCloseJSONObject()throws IOException
	{
		out.write("}");
	}
	@Override protected void endImpl()throws IOException
	{
		if (TRACE) TOUT.println("endImpl() state="+state+" ENTER");
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
						//Nothing written, it is an empty struct
						out.write("[]");
						break;
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						//single element structure
						flushFirstElement();
						break;
			case NEXT_ELEMENT:						
			case FIRST_ELEMENT:
						//we are first after array is opened and nothing is to be deduced
						//or next in array mode.
						out.write("]");
						break;			
		};
		writeCloseJSONObject();
		state = TState.NEXT_ELEMENT; 
		if (TRACE) TOUT.println("endImpl() LEAVE");
	};
	
	@Override protected void flushImpl()throws IOException
	{
		if (TRACE) TOUT.println("flushImpl() ENTER");
		//Now flush may be invoked when we written first structure
		//element but could not decide yet. We need to deliver value
		//to destination tough, but without any closing.
		switch(state)
		{
			case DEDUCE_SINGLE_ELEMENT:
						//Nothing written, so we are not doing anything.
						break;
			case DEDUCING_PLAIN_SINGLE_ELEMENT:
			case DEDUCING_CHAR_SINGLE_ELEMENT:
						//This must become array structur
						out.write('[');
						flushFirstElement();
						state = TState.NEXT_ELEMENT;
						break;
			case NEXT_ELEMENT:						
			case FIRST_ELEMENT:
						break;			
		};
		super.flushImpl();
		out.flush();	//and low level.
		if (TRACE) TOUT.println("flushImpl() LEAVE");
	};
	
	/** Opens JSON array */
	@Override protected void openImpl()throws IOException
	{
		if (TRACE) TOUT.println("openImpl() ENTER");
		//Sanitize possible states.
		first_element.setLength(0);
		state = TState.FIRST_ELEMENT;
		out.write('[');
		if (TRACE) TOUT.println("openImpl() LEAVE");
	};
	/** Closes output writer.
	  If recursion depth is zero 
	  will also close JSON array.
	  If resursion depth is not zero it is assumed to
	  be an "abrupt close" and no closure is written.
	*/
	@Override protected void closeImpl()throws IOException
	{
		if (TRACE) TOUT.println("closeImpl() ENTER");
		if (getCurrentStructRecursionDepth()==0)
		{
			out.write(']');
			out.flush();
		}else
		{
			if (TRACE) TOUT.println("closeImpl(), dangling signal, not closing JSON");
		}
		out.close();
		if (TRACE) TOUT.println("closeImpl() LEAVE");
	};
	
	/* ***********************************************************************
		
				IFormatLimits
		
		
	************************************************************************/
	/** 
	-1, unbound. 
	*/
	@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
	/** Integer.MAX_VALUE */
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
};