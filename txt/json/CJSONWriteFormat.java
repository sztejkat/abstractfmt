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
				/** If true the a first primitive is being collected
				into {@link #is_first_element} to decide 
				if array mode is necessary. If false array mode is
				on.*/
				private boolean is_first_element;
				/** If true the first element is a buffered char element */
				private boolean first_element_is_char;
				/** Used to collect first single primitive element in 
				structure, in raw form */
				private StringBuilder first_element;
				/** Indicates that {@link #outTokenToSignalSeparator}
				was called before writing signal */
				private boolean token_to_signal_separator_pending;
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates
	@param out writer where to write, non null, opened. Must accept all
		possible <code>char</code> values.
	*/
	public CJSONWriteFormat(Writer out)
	{
		super(0);	//We do not support registered names.
					//Due to JAVA lacking virtual multiple inheritance
					//it was for me easier to inherite the ARegisteringStructWriteFormat
					//in generic text support and then disable it here rather
					//than playing with class composition.
		assert(out!=null);
		this.out = out;
	};		
	/* *****************************************************************
	
				ATxtWriteFormat1/ATxtWriteFormat0
	
	******************************************************************/
	/* -----------------------------------------------------------
			Related to single element optimization
	-----------------------------------------------------------*/
	/** Invoked at each place which may write second element
	in structure body or an element which should switch structur
	body to array mode */
	private void flushPendingFirstElement()throws IOException
	{
		if (is_first_element)
		{
			out.write('[');
			writePendingFirstElement();
		};
	};
	/** Invoked in place where there is a possiblity that pending
	first element do construct the single element structure. 
	@return true if element was pending
	*/
	private boolean confirmPendingFirstElement()throws IOException
	{
		if (is_first_element)
		{
			writePendingFirstElement();
			return true;
		}else
			return false;
	};
	/** Invoked in place where first element must be just written.
	Writes it and drops any information about pending element.
	@throws AssertionError if there is no pending first element
	*/
	private void writePendingFirstElement()throws IOException
	{
		assert(is_first_element);
		if (first_element_is_char)
		{
				assert(first_element.length()==1);
				//push it through escaper as a JSON string
				out.append('\"');
				escaper.reset(); //we need to run complete escaper sequence.
				escaper.append(first_element.charAt(0));
				escaper.flush();
				out.append('\"');
		}
		else
		{
				out.append(first_element);
		}
		//reset them.
		first_element.setLength(0);
		is_first_element = false;
		first_element_is_char = false;
	};
	/** Invoked after begin signal to indicate that first element
	in struct may appear */
	private void startFirstElement()
	{
		is_first_element = true;
		assert(first_element_is_char == false);
		assert(first_element.length()==0);
	};
	
	
	/* ----------------------------------------------------------
			Related to "," appearing before sub structure
	----------------------------------------------------------*/	
	@Override protected void outSignalSeparator()throws IOException
	{
		//No need to write anything.
	};
	@Override protected void outTokenToSignalSeparator()throws IOException
	{
		//In JSON begin signal requires , but end signal does not
		//require anything. We will just store this information.
		assert(!token_to_signal_separator_pending);
		token_to_signal_separator_pending = true;
	};
	
	/* ----------------------------------------------------------
			Again, related to handling first element optimization
			and just doing the output
	----------------------------------------------------------*/
	@Override protected void outTokenSeparator()throws IOException
	{
		//Separator indicates that at least one element was written
		//so for sure we won't be single element.
		flushPendingFirstElement();
		//and add separator as requested.
		out.write(',');
	};
	
	
	@Override protected void openPlainTokenImpl()throws IOException{};
	@Override protected void outPlainToken(char c)throws IOException
	{
		if (is_first_element)			
			first_element.append(c);//Collect it in buffer for first element
		else
			out.write(c);//write for others
	};	
	@Override protected void closePlainTokenImpl()throws IOException{};
	
	
	
	@Override protected void openSingleCharToken()throws IOException
	{
		//Separate handling from string mode. We need to detect single
		//primitive character as a "single element structure".
		defaultOpenSingleCharToken();
	};
	@Override protected void openSingleCharTokenImpl()throws IOException
	{
		if (is_first_element)
				first_element_is_char = true; //remember it is a char element
		else
		{
			escaper.reset();
			out.write('\"');//or pass directly
		};
	}
	protected void outSingleCharToken(char c)throws IOException
	{
		if (is_first_element)
		{
			//buffer it as raw
			assert(first_element_is_char);
			assert(first_element.length()==0);
			first_element.append(c);
		}else
			escaper.write(c);	//or pass through escaped.
	};
	
	
	@Override protected void closeSingleCharToken()throws IOException
	{
		defaultCloseSingleCharToken();
	};
	@Override protected void closeSingleCharTokenImpl()throws IOException
	{
		if (!is_first_element)
		{
			escaper.flush();
			out.write('\"');//close it directly
		};
	}
 	
	
	
		
	@Override protected void openStringTokenImpl()throws IOException
	{
		//Note: String tokens do handle string sequences and char [] blocks
		//		in our implementation. Both are variable and potentially
		//		infinite in size so they can't be a part of "single element structure".
		
		is_first_element = false;//can drop it unconditionally.
		escaper.reset();//reset escaping engine.
		out.write('\"');//open the JSON string
	};	
	@Override protected void outStringToken(char c)throws IOException
	{
		escaper.write(c);
	};
	@Override protected void closeStringTokenImpl()throws IOException
	{		
		escaper.flush();
		out.write('\"');//close the JSON string
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
	/** Redirects to state engine since additional actions may be necessary */
	@Override protected void beginDirectImpl(String name)throws IOException
	{
		flushPendingFirstElement();
		//handle pending token to signal separator.
		if (token_to_signal_separator_pending)
		{
			out.write(',');
			token_to_signal_separator_pending = false;
		}
		out.write("{\"");
				escaper.reset();
				escaper.append(name);
				escaper.flush();
		out.write("\":");
		startFirstElement();
	};
	
	/* *****************************************************************
	
			AStructWriteFormatBase0
	
	******************************************************************/
	@Override protected void flushImpl()throws IOException
	{
		//Now flush may be invoked when we written first structure
		//element but could not decide yet. We need to deliver value
		//to destination tough.
		flushPendingFirstElement();
		super.flushImpl();
		out.flush();	//and low level.
	};
	@Override protected void endImpl()throws IOException
	{
		if (!confirmPendingFirstElement())
		{
			//We were in array mode, so:
			out.write(']');
		};
		//consume eventual pendig separator.
		token_to_signal_separator_pending = false;
		out.append('}');
	};
	/** Opens JSON array */
	@Override protected void openImpl()throws IOException
	{
		out.write('[');
	};
	/** Closes output writer.
	  If recursion depth is zero 
	  will also close JSON array.
	  If resursion depth is not zero it is assumed to
	  be an "abrupt close" and no closure is written.
	*/
	@Override protected void closeImpl()throws IOException
	{
		if (getCurrentStructRecursionDepth()==0)
		{
			out.write(']');
			out.flush();
		};
		out.close();
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