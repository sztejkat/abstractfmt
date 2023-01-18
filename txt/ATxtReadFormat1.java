package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Reader;

/**
	This is a code template which shows the easiest and cleaniest 
	path to parsing text files into format readers.
	
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
		char getNextChar()
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
	The text parsing requires, mostly, two kinds of services:
	<ul>
		<li>character stream related:
			<ul>
				<li>{@link #readRaw} which reads raw character and returns -1 on eof, and;</li>
				<li>{@link #unreadRaw(char)}/{@link #unreadRaw(CharSequence)} which un-read character(s)
				back into a stream so that {@link #readRaw} can process it. 
				<p>
				Notice the read-unread mechanism is a more flexible variant of <code>mark/reset</code>.
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
	I will however provide a sketchy implementation in {@link ATxtReadFormat2}.
*/
public abstract class ATxtReadFormat1<TSyntax extends Object> extends ATxtReadFormat0
{
	protected ATxtReadFormat1(int name_registry_capacity,int token_size_limit)
	{
		super(name_registry_capacity,token_size_limit);
	};
};