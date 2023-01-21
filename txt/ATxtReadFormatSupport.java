package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.Reader;
import java.io.Closeable;

/**
	This is a code template which shows the easiest and cleaniest 
	path to parsing text files into format readers.
	<p>
	This is a support for {@link ATxtReadFormat1} abstract methods.
	
	<h1>Text parsing</h1>
	<h2>Required services</h2>
	As You probably noticed the {@link ATxtReadFormat0}
	turns around two methods:
	<ul>
		<li>{@link ATxtReadFormat0#tokenIn} and;</li>
		<li>{@link ATxtReadFormat0#hasUnreadToken};</li>
	</ul>
	Both are practically the same and could be replaced with <code>peek()/drop()</code> model.
	<p>
	Those methods do work on "per-character" basis. The "per-character"
	basis, instead of "per-word" or "per-token", is intentional since
	it allows easy processing of inifnitely long tokens. If we would
	not have an assumption about infinitely long strings we could do
	it per-token way, but later You will see it would be useless.
	<p>
	They do however a bit more than character classification. In fact they
	do skip some characters and interprete others or decode escape sequences.
	<p>
	The lower layer which is {@link ARegisteringStructReadFormat} is
	designed around a single method:	
	<ul>
		<li>{@link ARegisteringStructReadFormat#readSignalReg()};</li>
	</ul>
	This method works on slightly higher abstraction level and does
	a lot of decoding in a background.
	
	<h2>Parsing state machine</h2>
	The most clean concept which can present a text parsing to a user
	is to present him with following API:
	<pre>
		void toNextChar()
		TSyntax getNextSyntaxElement()
		int getNextChar()
	</pre>
	where:
	<ul>
		<li>the {@link #toNextChar} takes next character from a stream
		and knowing the state of a stream deduces what does it mean;</li>
		<li>the {@link #getNextSyntaxElement} returns a "syntax element" 
		of a stream which corresponds to character to which {@link #toNextChar}
		moved.
		<p>
		Note: <code>TSyntax</code> should be immutable. Best if it would be an <code>Enum</code>.</li>
		
		<li>and finally {@link #getNextChar} returns a character
		collected by {@link #toNextChar};</li>
	</ul>
	For an example the XML:
	<pre>
		&lt;name style="full" &gt;
	</pre>
	will report:
	<table border="1" >
		<caption>XML states</caption>
		<tr><td> &lt; </td><td>XML element start entry (<a href="#STAR1">*</a>)</td></tr>
		<tr><td> n </td><td>XML element name</td></tr>
		<tr><td> ... </td><td>-//-</td></tr>
		<tr><td> </td><td>XML separator</td></tr>
		<tr><td>s</td><td>XML attribute name</td></tr>
		<tr><td> ... </td><td>-//-</td></tr>
		<tr><td> = </td><td>XML attribute operator</td></tr>
		<tr><td> &#22; </td><td>XML attribute value separator</td></tr>
		<tr><td>f</td><td>XML attribute value</td></tr>
		<tr><td> ... </td><td>-//-</td></tr>
		<tr><td> &#22; </td><td>XML attribute value separator</td></tr>
		<tr><td> </td><td>XML separator</td></tr>
		<tr><td> &gt; </td><td>XML element start exit</td></tr>
	</table>
	<p id="STAR1">*)Notice that "XML element start entry" requires some  look forward to check
	if it is not: <code>&lt;/</code> nor <code>&lt;!--</code><p>
	<p>
	This is a relatively simple and well isolated syntax processing machine You may provide
	for Your format and encode in this class. I don't make any assumption if You provide it
	by subclassing or by a separate engine object.
	
	<h2>Parsing support</h2>
	The text parsing into characters and syntax elementd do requires, mostly, two kinds of services:
	<ul>
		<li>character stream related:
			<ul>
				<li>{@link CAdaptivePushBackReader} via {@link #in}
				which allows un-reading charactes if necessary;
				</li>
			</ul>			
		</li>
		<li>syntax state related:
			<ul>
				<li>{@link #pushSyntax} - which pushes syntax element on syntax stack;</li>
				<li>{@link #popSyntax} - which pops from stack.
				<p>
				Both can be used for maintaining syntaxt recognition in case of formats
				which are defined by state graphs with sub-graphs or recursive state graphs.
				Notice however, that it is best to avoid state stack if possible (for an example 
				use objects counter in JSON or nested elements counter in XML), since 
				in case of heavily recusrive data structures the stack will consume significant
				amount of memory and will be a limiting factor which may lead to <code>OutOfMemoryError</code>.
				</li>
				<li>{@link #setSyntaxStackLimit}/{@link #getSyntaxStackLimit} - which do allow to 
				set up a barrier against <code>OutOfMemoryError</code>;</li>
			</ul>
		</li>
	</ul>
	
	<h2>Parsing specials</h2>
	Now if You provide me with such a parser which will implement {@link #toNextChar}/{@link #getNextSyntaxElement}/{@link #getNextChar}
	then I can esily implement all necessary services over them.
	<p>
	I won't implement them in here directly, because the required set of known state sequences required to detect
	signals may vary from format to format and You may decide on implementing some escapes on lower or higher levels.
	<p>
	I will however provide a sketchy, reference implementation usefull for standard
	formats defined in this library in {@link ATxtReadFormat1}.
	
*/
public abstract class ATxtReadFormatSupport<TSyntax extends Object, TSyntaxState extends Object>
					  implements Closeable
{
				/** Wrapped input, provides unreading and line numbering */
				protected final CAdaptivePushBackReader in;
				/** syntax stack, lazy initialized */
				private ArrayDeque<TSyntaxState> syntax_stack;
				/** Current syntax stack limit, -1 if unlimited */
				private int syntax_stack_limit = -1;
				
	/* *********************************************************************
	
		Construction
		
	
	* *********************************************************************/
	/** Creates
	@param in non null input from which read raw text. Will be closed together with 
			format. Available throgh a push-back adapter via {@link #in}.
	*/
	protected ATxtReadFormatSupport(Reader in)
	{
		assert(in!=null);
		this.in = new CAdaptivePushBackReader(in,8,8);
	};
	/* *********************************************************************
	
		Syntax processing contract required from subclasses.
		
			See class description.
		
	
	* *********************************************************************/
	/** Reads next char from input and deuduces what does it mean.
	Should use {@link #in} and {@link #pushState} familly 
	to manipulate state of syntax processor	if necessary.
	Updates {@link #getNextSyntaxElement} and {@link #getNextChar}
	@throws IOException if failed to deduce. End-of-file is explicite excluded.
	*/
	protected abstract void toNextChar()throws IOException;
	/** Tells what the character fetched by most recent call to do mean.
	@return syntax element describing the meaning of character or null
			to indicate end-of-file condition.
	@throws AssertionError if {@link #toNextChar} was never called.
	*/
	protected abstract TSyntax getNextSyntaxElement();
	/** Returns the character fetched by most recent call to do mean.
	@return a character read 0...0xFFFF, or -1 to indicate end-of-file
			condition.
	@throws AssertionError if {@link #toNextChar} was never called.
	*/
	protected abstract int getNextChar();
	/* *********************************************************************
	
		Syntax stack
		
	
	* *********************************************************************/
	/** Pushes syntax item on syntax stack
	@param syntax non null
	@throws EFormatBoundaryExceeded if there is no more place on stack
	*/
	protected void pushSyntax(TSyntaxState syntax)throws EFormatBoundaryExceeded
	{
		assert(syntax!=null);
		if (syntax_stack==null) syntax_stack = new ArrayDeque<TSyntaxState>();
		int slimit = getSyntaxStackLimit();
		if ((slimit!=-1)&&(slimit<=syntax_stack.size()))
				throw new EFormatBoundaryExceeded("Syntax stack exceeded, up to "+slimit+" elements are allowed");
		syntax_stack.addLast(syntax);		
	};
	/** Peeks syntax element from syntax stack
	@return null if there is nothing on stack.
	*/
	protected final TSyntaxState peekSyntax()
	{
		if (syntax_stack==null) return null;
		return syntax_stack.peekLast();
	};
	/** Pops syntax element from syntax stack
	@return stack element
	@throws NoSuchElementException if stack is empty
	*/
	protected final TSyntaxState popSyntax()
	{
		if (syntax_stack==null) throw new NoSuchElementException("Syntax stack is empty");
		return syntax_stack.removeLast();
	};
	/** Changes limit of syntax stack size used by {@link #pushSyntax}
	@param limit new limit, maximum size of stack before failure or -1 if not limit it 
	@throws IllegalStateException if current stack depth is larger than the limit 
	*/
	protected void setSyntaxStackLimit(int limit)throws IllegalStateException
	{
		assert(limit>=-1);
		int current = (syntax_stack==null) ? 0 : syntax_stack.size();
		if ((limit!=-1)&&(limit<current))
			throw new IllegalStateException("New limit is smaller than current stack size="+current);
		this.syntax_stack_limit = limit;
	};
	/** Returns current limit of syntax stack
	@return -1 if disabled */
	protected final int getSyntaxStackLimit()
	{
		return syntax_stack_limit;
	};
	/* ***************************************************************
	
			Closeable	
	
	***************************************************************/
	@Override public void close()throws IOException
	{
		in.close();
	};
};


