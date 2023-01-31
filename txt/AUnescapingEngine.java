package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	A reverse engine for {@link AEscapingEngine}.
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
						ESCAPE_BODY_VOID(&amp)
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
				/** Set if escape was terminated by REGULAR_CHAR and we
				need to double-buffer a char in {@link #char_pending}*/
				private boolean is_char_pending;
				/** See {@link #is_char_pending} */
				private char char_pending;
				
	public AUnescapingEngine()
	{
		collection_buffer = new StringBuilder();
	};
	
	/* ********************************************
	
			Services required form subclasses.
	
	**********************************************/
	/** Like {@link java.io.Reader#read}, reads single <code>char</code>
	from downstream.
	@return -1 if end-of-file, 0...0xFFFF represeting single UTF-16 of Java
		<code>char</code> otherwise
	@throws IOException if failed.
	*/
	protected abstract int readImpl()throws IOException;
	
	/** Tests if specified character is a part of an escape sequence.
	@param c char to check 
	@param escape_sequence_length a length of already collected escape sequence. 
			-1 if no escape collection is in progress.
	@return meaning of character
	@throws IOException if found a bad escape. Recommended to use {@link EBrokenFormat}
	*/
	protected abstract TEscapeCharType isEscape(char c, int escape_sequence_length)throws IOException;
	
	/** Called once {@link #isEscape} is used to detect
	the end of collected escape sequence.
	@param collection_buffer buffer with collected escape.
	@return unescaped character
	@throws IOException if this is a bad escape.
	*/
	protected abstract char unescape(StringBuilder collection_buffer)throws IOException;
	
	/* ********************************************
	
			public API
	
	**********************************************/
	/** Like {@link java.io.Reader#read}, reads <code>char</code>
	from downstream, unescapes it if necessary
	@return -1 if end-of-file or 0...0xFFFF represeting single UTF-16 of Java
			<code>char</code>. If an escape was found it is un-escaped.
	@throws IOException if failed.
	@throws EEof if {@link #readImpl} reported an -1 inside an escape sequence.
	*/
	public int read()throws IOException
	{
		//handle pending?
		if (is_char_pending)
		{
			is_char_pending = false;
			return char_pending;
		};
		//ask downstream.
		int ci = readImpl();
		assert((ci>=-1)&&(ci<=0xFFFF));
		if (ci==-1) return -1;
		//qualify it
		char c = (char) ci;
		TEscapeCharType t = isEscaspe(c, -1 )
		if (t==TEscapeCharType.REGULAR_CHAR) return ci; //not initiate anything.
		try{
				//now loop collecting
				for(;;)
				{
					switch(t)
					{
						case REGULAR_CHAR: 
								//not add, terminate, keep for later return 
								char_pending = unescape(collection_buffer);
								is_char_pending = true;
								return c;
						case ESCAPE_BODY:
								//collect, continue
								collection_buffer.append(c);
								break;
						case ESCAPE_LAST_BODY:
								//collect, terminate
								collection_buffer.append(c);
								return unescape(collection_buffer);
						case ESCAPE_LAST_BODY_VOID:
								//not collect, process
								return unescape(collection_buffer);
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
					t = isEscaspe(c, collection_buffer.length() )
				};
		}finally
		{
			//purge collection buffer.
			collection_buffer.setLength(0); 
		};
	};
};