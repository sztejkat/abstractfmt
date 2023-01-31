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
		<li>contain any of Unicode upper code point which is not allowed, as per {@link #mustEscapeCodePoint};</li>
		<li>not contain invalid "surogate" sequences;</li>
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
	
	protected AEscapingEngine()
	{
	};
	
	
	/* ******************************************
	
			Services required from subclasses		
	
	********************************************/
	/** Tests if specified JAVA character must be escaped.
	<p>
	This method is used <u>prior</u> to {@link #mustEscapeCodepoint}
	on any incomming character.
	@param c char to test, except surogates which are tested separately.
	@return true if it must be escaped.
	*/
	protected abstract boolean mustEscape(char c);
	
	/** Tests if specified Unicode code-point must be escaped.
	<p>
	This method is used <u>after</u> {@link #mustEscape} and 
	only on unicode code-points produced by "upper surogate"+"lower surogate" pair.
	@param c char to test
	@return true if it must be escaped.
	*/
	protected abstract boolean mustEscapeCodepoint(int code_point);
	
	/** Should, using a sequence of calls to {@link #out},
	write the UTF-16 character into a stream using an escape sequence.
	@param c a char to escape, may be a surogate.
	@throws IOException if failed */
	protected abstract void escape(char c)throws IOException;
	
	/** Passes UTF-16 character down the stream.
	@param c what to pass down
	@throws IOException if failed */
	protected abstract void out(char c)throws IOException;
	
	/* ******************************************
	
			Public API
	
	********************************************/
	/** An array variant missing in original Appendable
	@param c what to write, non-null.
	@param off where to start
	@param length how many chars to process
	@return this
	@throws IOException if failed.
	@see #flush
	*/	
	public Appendable append(char c[], int off, int length)throws IOException
	{
		assert(c!=null);
		assert(off>=0);
		assert(off<c.length);
		assert(length>=0);
		assert(off+length<=c.length);
		while(--length>=0){ append(c[off++]); }
		return this;
	};
	/* ******************************************
	
			Appendable
	
	********************************************/
	/** Writes a single character, escaping it if necessary.
	If character represents an "upper surogate" the operation
	is delayed till next call or {@link #flush}.
	@param c char to write
	@throws IOException if failed.
	@see #flush
	*/
	public Appendable append(char c)throws IOException
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
				int code_point = (((int)upper_surogate_pending-0xD800)<<10)
								 |
								 (((int)c-0xDC00);
				if(mustEscapeCodepoint(code_point))
				{
					//This is a code point which may not be passed in direct,
					//un-escaped form. We do escape both surogates.
					escape(mustEscapeCodepoint);
					escape(c);
				}else
				{
					//we can pass it as it is.
					out(mustEscapeCodepoint);
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
		if (mustEscape(c))
			escape(c);
		else
			out(c);
		return this;
	};
	/** 
	@param csq can't be null. Yes, it breaks the contract, but for 
			this specific case it is important.
	@see #flush
	*/
	public Appendable append(CharSequence csq)throws IOException
	{
		assert(csq!=null);
		append(csq,0,csq.length());
	};
	/** 
	@param csq can't be null. Yes, it breaks the contract, but for 
			this specific case it is important.
	@see #flush
	*/
	public Appendable append(CharSequence csq,int start,int end)throws IOException
	{
		assert(csq!=null);
		for(int i=start; i<end; i++)
		{
			append(csq.charAt());
		};
	};
	
	/* ******************************************
	
			Flushable
	
	********************************************/
	/** Tests if there is an unprocessed "upper surogate" pending and if it
	is passes it to {@link #escape}. */
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