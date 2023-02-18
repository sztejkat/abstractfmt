package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.EUnexpectedEof;
import java.io.IOException;
import sztejkat.abstractfmt.utils.SStringUtils;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;

/** Adds functions focused on consuming and matching
	characters read from {@link ATxtReadFormatStateBase1#in} by providing
	various methods which can be used to collect some text from the down-stream
	and compare it with expected pattern(s).
	<p>
	Since the {@link ASyntaxHandler} is focused on "consumer"
	part of syntax processing described in {@link ATxtReadFormatStateBase0.ISyntaxHandler}
	this class focuses on supporting detection of "catch phrases".
	<p>
	If the "catch phrase" for the syntax handler is known and fixed the
	{@link #tryEnter} may look like:
	<pre>
protected boolean tryEnter()throws IOException
{
if (!looksAt("phrase")) //<i>or other variants of looksAt</i>
{
	unread();
	return false;
}else
{
	setStateHandler(this); //<i>or pushStateHandler(this)</i>
	return true;
}
}
	</pre>
	We intentionally do NOT provide this as default implementation because
	we would have to provice eight variants of them what would be rather
	confusing to a person subclassing it, because there may be more than one
	"catch-phrase" or because a totally different way of finding a match
	will have to be used.
	*/
public abstract class ASyntaxHandler<TSyntax extends ATxtReadFormat1.ISyntax>
				   extends AStateHandler<TSyntax>
				   implements ATxtReadFormatStateBase0.ISyntaxHandler
{
				/** A "collected token" buffer. 
					It is wiped out on {@link #onLeave}, so be sure to 
					pick up all data before making a state transition which 
					will trigger {@link #onLeave}.
				*/
				protected final StringBuilder collected = new StringBuilder(); 
				
	/* ************************************************************************
	
				Construction
	
	
	  *************************************************************************/
	/** Creates, servicing specified parser 
		@param parser non-null parser which states and characters queue 
				will be modified by this handler.
	*/
	protected ASyntaxHandler(ATxtReadFormatStateBase1<TSyntax> parser)
	{
		super(parser);
	};
	
	/* ************************************************************************
	
	
				Recognition phrase collection API
				
				
	************************************************************************ */
	/** Performs character collection into a {@link #collected} buffer.
	@return <ul>
				<li>if {@link #collected} is empty:
					<ul>
						<li>-1(eof). <u>Nothing</u> is done;</li>
						<li>0...0xFFFF - a collected character. This character
							is automatically appended to {@link #collected} buffer;
						</li>
					</ul>
				</li>
				<li>if {@link #collected} is NOT empty:
					<ul>
						<li>if encounters an end-of-file throws {@link EUnexpectedEof};</li>
						<li>0...0xFFFF - a collected character. This character
							is automatically appended to {@link #collected} buffer;
						</li>
					</ul>
				</li>
			</ul>
		@throws EUnexpectedEof if could not collect subsequent characters, except first
				for which returns -1.
		@throws IOException if failed.
	*/
	protected int collect()throws EUnexpectedEof,IOException
	{
		int r = tryRead();
		assert((r>=-1)&&(r<=0xFFFF));
		if (r==-1)
		{
			if (collected.length()!=0) throw new EUnexpectedEof();
			return -1;
		};
		collected.append((char)r);
		return r;
	};
	/** Performs character collection into a {@link #collected} buffer.
	@return <ul>
				<li>-1(eof). <u>Nothing</u> is done;</li>
				<li>0...0xFFFF - a collected character. This character
					is automatically appended to {@link #collected} buffer;
				</li>
			</ul>
		@throws IOException if failed.
	*/
	protected int tryCollect()throws IOException
	{
		int r = tryRead();
		assert((r>=-1)&&(r<=0xFFFF));
		if (r==-1)
		{
			return -1;
		};
		collected.append((char)r);
		return r;
	};
	/** Performs character collection into a {@link #collected} buffer.
	@return collected character
	@throws IOException if failed.
	@throws EUnexpectedEof if could not collect
	*/
	protected char collectAlways()throws IOException,EUnexpectedEof
	{
		char r = readAlways();
		collected.append(r);
		return r;
	};
	
	/** Un-reads entire collected buffer to the down-stream and 
	clears the collected buffer.
	@throws IOException if failed.
	@see #collected 
	*/
	protected final void unread()throws IOException
	{
		in().unread(collected);
		collected.setLength(0);
	};
	/** Tests if {@link #collected} can represent <code>text</code>.
	This is a case-sensitive comparison.
	<p>
	Avoids creating temporary String objects.
	@param text text which should be compared with buffer
	@return <ul>
				<li>-1 if {@link #collected} cannot represent text;</li>
				<li>0 if {@link #collected} do represent a starting portion of the text, but not full text;</li>
				<li>1 if {@link #collected} do represent text;</li>
		</ul>
	@see SStringUtils
	*/
	protected final int canStartWithCollected(String text)
	{
		return SStringUtils.canStartWithCaseSensitive(collected,text);
	};
	/** Tests if {@link #collected} can represent <code>text</code>.
	This is a case-insensitive comparison.
	Avoids creating temporary String objects.
	@param text text which should be compared with buffer
	@return <ul>
				<li>-1 if {@link #collected} cannot represent text;</li>
				<li>0 if {@link #collected} do represent a starting portion of the text, but not full text;</li>
				<li>1 if {@link #collected} do represent text;</li>
		</ul>
	@see SStringUtils
	*/
	protected final int canStartWithCollectedCaseInsensitive(String text)
	{
		return SStringUtils.canStartWithCaseInsensitive(collected,text);
	};
	/** Clears collection buffer and collects up to <code>text.length()</code> characters and passes the collected
	data through {@link #canStartWithCollected} with each collected character.
	<p>
	Collection stops if {@link #canStartWithCollected} returns 1 or -1
	or end-of-file is reached.
	<p>
	Nothing is reported through {@link #queueNextChar}.
	<p>
	After return the {@link #collected} do carry what was collected
	during the process, what includes first not-matching character.
	
	@param text to compare with what.
	@return true if {@link #canStartWithCollected} returned 1,
			false if returned -1 or could not read even one character.
	@throws IOException if failed at low level
	@throws EUnexpectedEof if end-of-file is reached at any character except the
			first one.
	*/
	protected final boolean looksAt(String text)throws IOException, EUnexpectedEof
	{
		collected.setLength(0);
		for(int at=0;;at++)
		{
			if (collect()==-1) return false;
			//For efficiency use counted method
			//Comparison will automatically give -1 if we try to collect too much characters.
			switch(SStringUtils.canStartWithCaseSensitive(collected,text,at))
			{
				case -1: return false;
				case  0: break;
				case  1: return true;
			};
		}
	};
	/** A case-insentive variant of {@link #looksAt}
	@param text text to compare with
	@return see {@link #looksAt}
	@throws IOException --//--
	@throws EUnexpectedEof --//--
	*/
	protected final boolean looksAtCaseInsensitive(String text)throws IOException, EUnexpectedEof
	{		
		//See comments in looksAt(...)
		collected.setLength(0);
		for(int at=0;;at++)
		{
			if (collect()==-1) return false;
			switch(SStringUtils.canStartWithCaseInsensitive(collected,text,at)) 
			{
				case -1: return false;
				case  0: break;
				case  1: return true;
			};
		}
	};
	/** A variant of {@link #looksAt} which never throws {@link EUnexpectedEof}
	returning false instead.
	@param text text to compare with
	@return see {@link #looksAt}
	@throws IOException --//--
	*/
	protected final boolean tryLooksAt(String text)throws IOException
	{
		//See comments in looksAt(...)
		collected.setLength(0);
		for(int at=0;;at++)
		{
			if (tryCollect()==-1) return false;
			switch(SStringUtils.canStartWithCaseSensitive(collected,text,at)) 
			{
				case -1: return false;
				case  0: break;
				case  1: return true;
			};
		}
	};
	/** A variant of {@link #looksAtCaseInsensitive} which never throws {@link EUnexpectedEof}
	returning false instead.
	@param text text to compare with
	@return see {@link #looksAt}
	@throws IOException --//--
	*/
	protected final boolean tryLooksAtCaseInsensitive(String text)throws IOException
	{
		//See comments in looksAt(...)
		collected.setLength(0);
		for(int at=0;;at++)
		{
			if (tryCollect()==-1) return false;
			switch(SStringUtils.canStartWithCaseInsensitive(collected,text,at))  
			{
				case -1: return false;
				case  0: break;
				case  1: return true;
			};
		}
	};
	/* ************************************************************************
				AStateHandler
	************************************************************************ */
	/** Overriden to wipe out {@link #collected} */
	@Override public void onLeave()
	{
		super.onLeave();
		collected.setLength(0);
	};
};