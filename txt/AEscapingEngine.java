package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Flushable;
/**
	An escaping engine which can be used to process characters
	before passing them down the stream and "escape" characters
	which should NOT be there.
	<p>
	This class is ALWAYS and AUTOMATICALLY escaping bad "surogate"
	sequences.
	<p>
	This class assumes, that escapes are used to map the sequence of  
	UTF-16 Java <code>char</code> into an "escaped" sequences
	into UTF-16 which are warrent to not:
	<ul>
		<li>contain any of JAVA char which is not allowed, as per {@link #mustEscape};</li>
		<li>contain any of Unicode upper code point which is not allowed, as per {@link #mustEscapeCodepoint};</li>
		<li>not contain invalid "surogate" sequences (missing lower surogate, lower surogate without
		upper surogate and etc.) not forming valid UTF-16 text;</li>
	</ul>
*/
public abstract class AEscapingEngine implements Appendable,Flushable
{
	/*
		Design note:
			I decided to NOT extend java.io.Writer since
			it sucks ass due to:
				1) Stupid assumption that block write is used
					to IMPLEMENT character write what required
					to internally allocate an array just for that
					case.
				2) Even dumber assumption that it must be thread
					safe even tough per-call thread safety for
					I/O is pointless.
	*/
	
				/** Set to true if upper surogate was detected 
				thous we may have a proper surogate sequence, possibly.
				The {@link #upper_surogate_pending}
				do carry the surogate in question*/
				private boolean is_upper_surogate_pending;
				private char upper_surogate_pending;
	/* ***************************************************************************
	
			Construction
	
	
	*****************************************************************************/
	protected AEscapingEngine()
	{
	};
	
	
	/* ******************************************
	
			Services required from subclasses		
	
	********************************************/
	/*--------------------------------------------------------------------
				downstream
	--------------------------------------------------------------------*/
	/** Passes UTF-16 character down the stream.
	@param c what to pass down
	@throws IOException if failed */
	protected abstract void out(char c)throws IOException;
	/*--------------------------------------------------------------------
				escaping
	--------------------------------------------------------------------*/	
	/** Tests if specified Java character must be escaped.
	This test happens prior to solving UTF-16 encoding on every incomming
	character.
	<p>
	If the UTF-16 encoding is fine for You and You don't need to 
	handle differently Java character and Unicode code-point
	the default implementation may be left as it is since
	it calls {@link #mustEscapeCodepoint}.
	<p>
	This method is used <u>prior</u> to {@link #mustEscapeCodepoint}
	on any incomming character.
	
	@param c char to test. The surogates range 0xD800...0xDFFF is 
			excluded from here, decoded and passed to {@link #mustEscapeCodepoint}.
			The invalid surogates are uncodintionally escaped.
	@return true if it must be escaped.
	*/
	protected boolean mustEscape(char c){ return mustEscapeCodepoint(c); };
	
	/** Tests if specified Unicode code-point must be escaped.
	<p>
	This method is used <u>after</u> {@link #mustEscape} and 
	for both chars and unicode code-points produced by "upper surogate"+"lower surogate" pair.
	<p>
	This method will be invoked for each and every code point, in order of appearance
	and once and only once for each code point.
	
	@param code_point code-point to test, full range.
	@return true if it must be escaped.
	*/
	protected abstract boolean mustEscapeCodepoint(int code_point);
	
	/** Should, possibly using a sequence of calls to {@link #out},
	write the UTF-16 character into a stream using an escape sequence.
	@param c a char to escape, may be a surogate.
	@throws IOException if failed */
	protected abstract void escape(char c)throws IOException;
	/** Escapes code-point 
	By default splits it into upper/lower surogate and
	passes down to {@link #escape} 
	@param c a code-point, above 0xFFFF range.
	@param upper_surogate upper surogate of <code>c</code>, as it was stored in UTF-16 stream
	@param lower_surogate lowe surogate of <code>c</code>
	@throws IOException if failed.
	*/
	protected void escapeCodepoint(int c, char upper_surogate, char lower_surogate)throws IOException
	{
		assert(c>0xFFFF);
		escape(upper_surogate);
		escape(lower_surogate);
	};
	/* ******************************************
	
			Public API
	
	********************************************/
	/** Makes the engine to forget everything.
	Usefull for state based escapers using for an example
	different set of escapes for first character  */
	public void reset()
	{
		this.is_upper_surogate_pending = false;
		this.upper_surogate_pending = 0;
	};
	/** An array variant missing in original Appendable
	@param c what to write, non-null.
	@param off where to start
	@param length how many chars to process
	@return this
	@throws IOException if failed.
	@see #flush
	*/	
	public final Appendable append(char c[], int off, int length)throws IOException
	{
		assert(c!=null);
		assert(off>=0);
		assert(off<c.length);
		assert(length>=0);
		assert(off+length<=c.length);
		while(--length>=0){ append(c[off++]); }
		return this;
	};
	/** An array variant missing in original Appendable
	@param c what to write, non-null.
	@return this
	@throws IOException if failed.
	@see #flush
	*/
	public final Appendable append(char c[])throws IOException
	{
		assert(c!=null);
		append(c,0,c.length);
		return this;
	};
	/** As {@link #append(char)}
	@param c --
	@throws IOException if failed
	*/
	public final void write(char c)throws IOException
	{
		appendImpl(c);
	};
	/* ******************************************
	
			Appendable
	
	********************************************/
	/** Delegates to {@link #appendImpl}.
	<p>
	Writes a single character, escaping it if necessary.
	If character represents an "upper surogate" the operation
	is delayed till next call or {@link #flush}.
	
	@param c char to write
	@throws IOException if failed.
	@see #flush
	*/
	public final Appendable append(char c)throws IOException
	{
		appendImpl(c);
		return this;
	};
	/* ----------------------------------------------
	
			Core service for Appendable
	
	---------------------------------------------- */
	/** Returns true if most recent call to {@link #appendImpl}
	resulted in character NOT being fully processeed because
	it was a start of UTF-16 surogate pair and next character
	is required to make a decission
	@return false if nothing is pending.
	*/
	protected final boolean isCodepointPending(){ return is_upper_surogate_pending; };
	/** Writes a single character, escaping it if necessary.
	If character represents an "upper surogate" the operation
	is delayed till next call or {@link #flush}.
	@param c char to write
	@throws IOException if failed.
	@see #flush
	@see #isCodepointPending
	*/
	protected void appendImpl(char c)throws IOException
	{
		//Check if have pending surogate?
		if (is_upper_surogate_pending)
		{
			//a proper stream allows only lower surogate
			//if character is a lower surogate?
			if ((c>=0xDC00)&&(c<=0xDFFF))
			{
				//yes, this is a proper surogate.
				//Now we need to make a code-point of it.
				int code_point = ((((int)upper_surogate_pending-0xD800)<<10)
								 |
								 ((int)c-0xDC00))+0x1_0000;
				if(mustEscapeCodepoint(code_point))
				{
					//This is a code point which may not be passed in direct,
					//un-escaped form. We do escape both surogates.
					escapeCodepoint(code_point,
									upper_surogate_pending,
									c);
				}else
				{
					//we can pass it as it is.
					out(upper_surogate_pending);
					out(c);
				};
				upper_surogate_pending = 0;
				is_upper_surogate_pending = false;
				return;
			}
			//it is not a propert surogate
			escape(upper_surogate_pending);				
			upper_surogate_pending = 0;
			is_upper_surogate_pending = false;
		};
			//Check if we do init a pending upper surogate?
		if ((c>=0xD800)&&(c<=0xDBFF))
		{
			//yes, make it pending, delay it for later.
			upper_surogate_pending = c;
			is_upper_surogate_pending = true;
			return;
		};
		//now check if character needs escaping?
		if (
				((c>=0xDC00)&&(c<=0xDFFF))	//cause stand-alone lower surogate 
				|| 
				mustEscape(c)				//cause by char
				|| 
				mustEscapeCodepoint(c)	//cause by code point which is char in this case.
				)
			escape(c);
		else
			out(c);
		return;
	};
	/** 
	@param csq can't be null. Yes, it breaks the contract, but for 
			this specific case it is important.
	@see #flush
	*/
	public final Appendable append(CharSequence csq)throws IOException
	{
		assert(csq!=null);
		return append(csq,0,csq.length());
	};
	/** 
	@param csq can't be null. Yes, it breaks the contract, but for 
			this specific case it is important.
	@see #flush
	*/
	public final Appendable append(CharSequence csq,int start,int end)throws IOException
	{
		assert(csq!=null);
		for(int i=start; i<end; i++)
		{
			appendImpl(csq.charAt(i));
		};
		return this;
	};
	
	/* ******************************************
	
			Flushable
	
	********************************************/
	/** Tests if there is an unprocessed "upper surogate" pending and if it
	is passes it to {@link #escape}. 
	<p>
	Notice that if there are surogates the effect of:
	<pre>
		append("some text with surogates"); flush();
	</pre>
	will be <u>different</u> from the effect of:
	<pre>
	for(char C: "some text with surogates")
	{
		append(C); flush();
	}
	</pre>
	as all surogates will be treated as "dangling" and require escaping.
	<p>
	This is the price we also pay with <code>java.io.Writer</code>, altough in
	the last case in a different way: the dangling surogate is not flushed down
	the stream.
	*/
	public void flush()throws IOException
	{
		//Handle eventual pending upper surogate
		if (is_upper_surogate_pending)
		{
				escape(upper_surogate_pending);
				is_upper_surogate_pending = false;
				upper_surogate_pending=0;
		};
	};
};