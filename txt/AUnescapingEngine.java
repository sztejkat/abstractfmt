package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	A reverse engine for {@link AEscapingEngine}.
	<p>
	This engine is indended to work with parsers which do NOT use
	the "state graph" approach presented in {@link ATxtReadFormatStateBase0}/{@link ATxtReadFormatStateBase1}.
	<p>
	State-graph based parsers should use own state machines to handle it
	and they don't need any dedicated support.
*/
public abstract class AUnescapingEngine 
{
				/** A pre-allocated collection buffer */
				private final StringBuilder collection_buffer;
				/** Definition of escapes 
				<p>
				The example escape sequences are:
				<ul>
					<li>for java "abc\u0000efg":
						<pre>
						REGULAR_CHAR(a)
						REGULAR_CHAR(b)
						REGULAR_CHAR(c)
						ESCAPE_BODY_VOID(\)
						ESCAPE_BODY_VOID(u)
						ESCAPE_BODY(0)
						ESCAPE_BODY(0)
						ESCAPE_BODY(0)
						ESCAPE_LAST_BODY(0)
						REGULAR_CHAR(e)
						REGULAR_CHAR(f)
						REGULAR_CHAR(g)
						</pre>
				    </li>
				    <li>for XML "abc&amp;#x33;efg":
				    	<pre>
				    	REGULAR_CHAR(a)
						REGULAR_CHAR(b)
						REGULAR_CHAR(c)
						ESCAPE_BODY_VOID(&amp;)
						ESCAPE_BODY(#)
						ESCAPE_BODY(x)
						ESCAPE_BODY(3)
						ESCAPE_BODY(3)
						ESCAPE_LAST_BODY_VOID(;)
						REGULAR_CHAR(e)
						REGULAR_CHAR(f)
						REGULAR_CHAR(g)
				    	</pre>
				    </li>
				</ul>
				*/
				public enum TEscapeCharType
				{
					/** A regular, non escape character.
					It terminates collection of any escape sequence in
					progress */
					REGULAR_CHAR,
					/** An escape body character. If no escape was 
					in progess it starts collection of escape sequence.
					This character is added to collection buffer.
					*/
					ESCAPE_BODY,
					/** A terminating escape character. If escape was NOT
					progress this a single char escape. 
					This character is added to collection buffer.
					*/
					ESCAPE_LAST_BODY,
					/** A terminating escape character. If escape was NOT
					progress this a zero length escape.
					This character is NOT added to collection buffer.
					*/
					ESCAPE_LAST_BODY_VOID,
					/** An escape body character. If no escape was 
					in progess it starts collection of escape sequence.
					This character NOT added to collection buffer.
					*/
					ESCAPE_BODY_VOID,
				};
				
				/** Used to process {@link #isEscaped} */
				private boolean is_escaped;
				/** Set to true if {@link #unescape} returned 
				value greater than 0xFFFF which needs to be 
				turned into a surogate pair. The lower part
				of it is left pending. */
				private boolean is_pending_lower_surogate;
				private char pending_lower_surogate;
	/* ***************************************************************************
	
			Construction
	
	
	*****************************************************************************/			
	public AUnescapingEngine()
	{
		collection_buffer = new StringBuilder();
	};
	
	/* ********************************************
	
			Services required form subclasses.
			
			Note:
				Those services may look a bit awkward and non-streamline
				but I liked them to be focused on just escapes and have
				nothig to do with the actual reading or writing 
				from a stream. 
	
	**********************************************/
	/*--------------------------------------------------------------------
				downstream
	--------------------------------------------------------------------*/
	/** Like {@link java.io.Reader#read}, reads single <code>char</code>
	from downstream, dumb way without any processing.
	@return -1 if end-of-file, 0...0xFFFF represeting single UTF-16 of Java
		<code>char</code> otherwise
	@throws IOException if failed.
	*/
	protected abstract int readImpl()throws IOException;
	/** Un-reads character back to {@link #readImpl}
	Will be used only in case when REGULAR_CHAR is found to be terminating
	an escape sequence.
	@param c a regular charater which is to be put back to down-stream
			and re-interpreted later.
	@throws IOException if failed
	*/
	protected abstract void unread(char c)throws IOException;
	/*--------------------------------------------------------------------
				un-escaping
	--------------------------------------------------------------------*/
	/** Tests if specified character is a part/start/end of an escape sequence.
	
	@param c char to check, got from {@link #readImpl}
	@param escape_sequence_length a length of already collected escape sequence. 
			-1 if no escape collection is in progress.
	@param sequence_index 0 in a call which is detecting the start of
			escape sequence, then incremented with each processed escape sequence
			character regardless if it was added to collection buffer or not.
			A kind of state-machine index for fixed length escapes.
	@return meaning of character
	@throws IOException if found a bad escape. Recommended to use {@link EBrokenFormat}
	*/
	protected abstract TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException;
	
	/** Called once {@link #isEscape} is used to detect
	the end of collected escape sequence. Can be called only once per each collected
	escape sequence as the engine is allowed to track some state of collected escapes.
	
	@param collection_buffer buffer with collected escape. May be altered by this
			call, so one must call this method only once per each collected sequence.
	@return unescaped unicode code point or single java character or -1. Full set of unicode code-points
			and full set of  characters is allowed here.
			<p>
			The -1 means: "This escape is an empty character".
	@throws IOException if this is a bad escape.
	*/
	protected abstract int unescape(StringBuilder collection_buffer)throws IOException;
	
	/* ********************************************
	
			public API
	
	**********************************************/
	/** Forgets everything it had in memory, including 
	pending characters and escape results.
	<p>
	Intended to be used in case when some data were read 
	from downstream behind the back of the un-escaping engine. 
	*/
	public void reset()
	{
		is_escaped = false;
		is_pending_lower_surogate = false;
		pending_lower_surogate = 0;
		collection_buffer.setLength(0); 
	};
	/** Returns if a last character returned by 
	{@link #read} was produced by un-escaping 
	@return true if escaping was used to produce last character.
	*/
	public final boolean isEscaped()
	{
		return is_escaped;
	};
	/** Calls {@link #unescape} and handles returned upper code points 
	@param collection_buffer see {@link #unescape}
	@return --//--
	@see #is_pending_lower_surogate
	@throws IOException if {@link #unescape} failed.
	*/
	private int unescapeCodePoint(StringBuilder collection_buffer)throws IOException
	{
		int c = unescape(collection_buffer);
		assert((c>=-1)&&(c<=0x10FFFF)):"0x"+Integer.toHexString(c)+"("+c+") is not Unicode";
		if (c==-1) return -1;
		if (c>0xFFFF)
		{
				//needs to be split to surogates.
				c = c - 0x1_0000;
				char upper = (char)( (c >> 10)+0xD800);
				char lower = (char)( (c & 0x3FF)+0xDC00);
				is_pending_lower_surogate = true;
				pending_lower_surogate = lower;
				return upper;
		}else
			return (char)c;
	};
	/** Like {@link java.io.Reader#read}, reads <code>char</code>
	from downstream, unescapes it if necessary
	@return -1 if end-of-file or 0...0xFFFF represeting single UTF-16 of Java
			<code>char</code>. If an escape was found it is un-escaped.
	@throws IOException if failed.
	@throws EEof if {@link #readImpl} reported an -1 inside an escape sequence.
	*/
	public int read()throws IOException
	{
		scanning:
		for(;;)
		{
			//handle pending surogate.
			if (is_pending_lower_surogate)
			{
				is_pending_lower_surogate = false;
				int c = pending_lower_surogate;
				pending_lower_surogate = 0;
				return c;
			};
			is_escaped = false;	//always, for pending, regulars and eof
			//ask downstream.
			int ci = readImpl();
			assert((ci>=-1)&&(ci<=0xFFFF));
			if (ci==-1) return -1;
			//qualify it
			char c = (char) ci;
			TEscapeCharType t = isEscape(c, -1, 0 );
			if (t==TEscapeCharType.REGULAR_CHAR) return ci; //not initiate anything.
			try{
					is_escaped = true;
					//now loop collecting
					for(int sequence_char=1;;sequence_char++)
					{
						switch(t)
						{
							case REGULAR_CHAR: 
									//not add, terminate, keep for later return
									{
										unread(c);
										int r= unescapeCodePoint(collection_buffer);
										if (r==-1) continue scanning;
										return r;
									}
							case ESCAPE_BODY:
									//collect, continue
									collection_buffer.append(c);
									break;
							case ESCAPE_LAST_BODY:
									//collect, terminate									
									collection_buffer.append(c);
									{
										int r= unescapeCodePoint(collection_buffer);
										if (r==-1) continue scanning;
										return r;
									}
							case ESCAPE_LAST_BODY_VOID:
									//not collect, process
									{
										int r= unescapeCodePoint(collection_buffer);
										if (r==-1) continue scanning;
										return r;
									}
							case ESCAPE_BODY_VOID:
									//drop it 
									break;
							default: throw new AssertionError(t);
						};				
						//fetch next.
						ci = readImpl();
						assert((ci>=-1)&&(ci<=0xFFFF));
						if (ci==-1) throw new EUnexpectedEof("Eof inside escape sequence!");
						//qualify
						c = (char) ci;
						t = isEscape(c, collection_buffer.length(),sequence_char );
					}
			}finally
			{
				//purge collection buffer.
				collection_buffer.setLength(0); 
			}
		}
	};
};