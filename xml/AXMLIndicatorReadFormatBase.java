package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.util.CBoundAppendable;
import sztejkat.abstractfmt.util.CUnbufferingMapper;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import sztejkat.abstractfmt.ECorruptedFormat;
import java.io.*;
import java.util.ArrayList;

/**
	A reading counterpart for {@link AXMLIndicatorWriteFormatBase}
	using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>.
	<p>
	This format is NOT strictly validating XML data and does:
	<ul>
		<li>do allow bare format, without root element;</li>
		<li>do allow "anonymous end tags" <code>&lt;/;&gt</code>;</li>
		<li>the &amp; escapes are only partially recognized and if &amp; is encountered
		anywhere an exception is thrown to indicate that XML file may be XML compilant
		but we are not fully XML compilant.
		<p>
		This class recognizes: <code>&amp;gt; &amp;amp; &amp;lt;</code>  ;</li>
	</ul>
	This class is using a stack to track XML elements, so the longer containg 
	XML element chain is, the more memory it consumes. This means that the user
	of this class should take care about recursion limit.
	See {@link ASignalReadFormat0#ASignalReadFormat0}.
	<p>
	If this class encounters root element then the opening root tag is
	silently skipped while closing root tag toggles stream to 
	permanently return end-of-file on every operation except {@link #close}.
*/
public abstract class AXMLIndicatorReadFormatBase implements IIndicatorReadFormat
{
				/* ***********************************************
				
						XML processing
				
				************************************************/
				/** XML settings */
				protected final CXMLSettings settings;
				/** A bound, limited buffer for signal names
				with length fixed to match {@link #setMaxSignalNameLength}
				Non final because can be re-initialized when
				name limit is set.
				 */
				private CBoundAppendable signal_name_buffer;
				/** A bound, limitied buffer for colleting all
				xml elements, attributes, tokens and etc. 
				Non final because can be re-initialized when
				name limit is set.
				*/
				private CBoundAppendable token_buffer;
				/** Tool used to match subset of &amp;xxx; XML escapes.*/
				private final CUnbufferingMapper standard_XML_amp_escapes_matcher;
				/** Used to initialize {@link #standard_XML_amp_escapes_matcher} */
				private static final String [] AMP_XML_ESCAPES = 
					new String[]{
								"&gt;",
								"&lt;",
								"&amp;"
								};
				/** Used to initialize {@link #standard_XML_amp_escapes_matcher} */
				private static final char [] AMP_XML_ESCAPES_VALUES =
					new char[] {
								'>',
								'<',
								'&'
								};
				/* ***********************************************
				
						Low level I/O
				
				************************************************/
				/** A maximum length of any skippable block, like
				comments and etc. Used to avoid DoS attacks
				by providing XML with infinite comment, processing
				or skippable characters.*/
				private final int maximum_idle_characters;
				
				/** The un-read buffer.
				Each un-read character is put at the end
				of a buffer, and when {@link #read} 
				is invoked the last character in buffer is returned.
				<p>
				This un-read buffer is used to handle processing
				of elements, data and etc, and it is fixed in size.
				<p>
				Non final because can be re-initialized when
				name limit is set.
				@see #unread_at
				@see #unread
				*/
				private char [] unread;
				private int unread_at;
				/* ***********************************************
				
						State
				
				************************************************/
				/** Used to track open/close events for XML elements */
				private final ArrayList<String> xml_elements_stack;				
				/** Used to avoid continous re-parsing of indicator.
				Set to null to say that we don't know what is under
				a cursor. */
				private TIndicator indicator_cache;
				/** State representing stream which is ready for action
				and processing of any indicator or data */
				private static final byte STATE_RDY = (byte)0;
				/** State representing stream which was closed by {@link #close} 
				and is unusable */
				private static final byte STATE_CLOSED = (byte)1;
				/** State representing stream which was not closed by {@link #close} 
				but is unusable due to the fact, that closing root element
				was read. */
				private static final byte STATE_ROOT_CLOSED = (byte)2;
				/** State variable */
				private byte state;
				/** Indicates that we are inside a character block
				This status is cleared by XML tag. Theoretically
				it might be also cleared by read of other type,
				but such reads are prohibitted by API. */
				private boolean is_inside_character_block;
				
	/** Creates 
	@param settings XML settings to use
	@param maximum_idle_characters safety limit, setting upper boundary
		 for comment, processing commands and other skipable characters.
		 This value will also control, in some cases, the number of 
		 continous un-escaped white space characters inside a character block
		 and will, together with maximum name length and XML tokens,
		 control the size of character buffers.
		 <p>
		 Non zero, positive
	*/
	protected AXMLIndicatorReadFormatBase(
					final CXMLSettings settings,
					final int maximum_idle_characters
					)
	{
		assert(settings!=null);
		assert(maximum_idle_characters>0);
		this.settings = settings;
		this.maximum_idle_characters=maximum_idle_characters;
		this.signal_name_buffer = new CBoundAppendable(1024); //contract default size.
		//now compute maximum necessary for token buffer.
		//This is the signal name length, 37 characters for floating points
		//or max element name length
		int max_token = Math.max(37,
					    Math.max(signal_name_buffer.capacity(),
					    Math.max(maximum_idle_characters,
					    	settings.getMaximumTokenLength()));
		//we allocate token buffer and unread-buffer to those values.
		this.token_buffer = new CBoundAppendable(max_token);
		this.unread = new char[max_token];
		//pre-allocate some stack
		this.xml_elements_stack = new ArrayList<String>(32);//same as write format, just for symetry.
		this.standard_XML_amp_escapes_matcher = new CUnbufferingMapper(
														AMP_XML_ESCAPES,
														AMP_XML_ESCAPES_VALUES
														);
		this.state = STATE_RDY;
	};
				
	/* ************************************************************
	
			Low level I/O
	
	* ************************************************************/
	/** As {@link java.io.Reader#read}
	@return --//--
	@throws IOException --//--
	*/
	protected abstract int readFromInput()throws IOException;
	
	/* ************************************************************
	
			Intermediate I/O
			
			Those I/O routines are providing read and un-read
			routines and end-of-file monitoring.
			
	
	* ************************************************************/
	/**
		Reads character either from push-back buffer or
		from stream. Differs from {@link #read} in such a way that it does not throw.
		<p>
		Internally clears indicator cache.
		
		@return read character or -1 if end of stream is reached.
		@throws IOException if low level have failed.
		@throws EFormatBoundaryExceeded if {@link #max_inter_signal_chars} is exceeded.
		@see #read
		@see _tryRead
	*/
	protected int tryRead()throws EUnexpectedEof, IOException,EFormatBoundaryExceeded
	{		
		indicator_cache = null;	//any cursor motion invalidates cache.
		return _tryRead();	
	};
	/** Internal service for {@link #tryRead}, assuming that no cursor motion
	will be actually done. Used to implement {@link #peek} and {@link #isEof}.
	@return as {@link #tryRead}
	@throws IOException if low level have failed.
	*/
	private int _tryRead()throws EUnexpectedEof, IOException,EFormatBoundaryExceeded
	{
		int i =unread_at;		
		if (i==0)
		{
			final int r = readFromInput();
			assert(r>=-1);
			assert(r<=0x0FFFF);			
			if (r==-1) return r;
			return r;
		}else
		{
			final char c = unread[i-1];
			i--;
			this.unread_at=i;
			return c;
		}
	};
	/**
		Reads character either from push-back buffer or
		from stream
		@return read character
		@throws EUnexpectedEof if end of file was encounterd
		@throws IOException if low level have failed.
		@see #tryRead
		@see #readSafe
		@see #readEscaped
		@see #tryRead
	*/
	protected final char read()throws EUnexpectedEof, IOException
	{
		int r = tryRead();
		assert(r>=-1);
		assert(r<=0x0FFFF);
		if (r==-1) throw new EUnexpectedEof();						
		return (char)r;
	};
	/** Reads and checks if character is allowed XML character.
	This version basically rejects &amp; since it is an XML escape
	we can't handle. To handle it use {@link #readEscaped}
	@return read character
	@throws EUnexpectedEof if end of file was encounterd
	@throws IOException if low level have failed.
	@throws ECorruptedFormat if encountered &amp;
	@see #readUnsafe
	*/	
	protected final char readSafe()throws EUnexpectedEof, IOException
	{
		char c = read();
		switch(c)
		{
			case '&': 
				throw new ECorruptedFormat("Not well formed, unexpected &");				
			default: return c;
		}
	};
	
	/** Puts character back in stream
		@param c character to put back
		@throws AssertionError if could not un-read character 
				because there is no place in buffer. It usually means,
				that some stream structure was larger than expected
				and exceeded buffers specified in constructor.
	*/
	protected void unread(char c)throws AssertionError
	{
		indicator_cache = null;	//any cursor motion invalidates cache.		
		_unread(c);
	};
	/** Puts characters back in stream
		@param c text to put back in such a way, that reading will
				read it.
		@throws AssertionError if could not un-read character 
				because there is no place in buffer. It usually means,
				that some stream structure was larger than expected
				and exceeded buffers specified in constructor.
	*/
	protected void unread(CharSequence c)throws AssertionError
	{
		indicator_cache = null;	//any cursor motion invalidates cache.
		int put_at =this.unread_at;	
		final char [] u = this.unread;
		if (c.length() + put_at >u.length)	throw new AssertionError("Can't un-read so many characters");		
		for(int i =c.length(); --i>=0;)
		{
			u[put_at++] = c.charAt(i);
		};
		this.unread_at = put_at;
	};
	/** Internal service for {@link #unread}, assuming no cursor was moved.
		Always used in pair with {@link #_tryRead}.
		@param c as {@link #unread}
		@throws AssertionError as {@link #unread}
	*/
	private void _unread(char c)throws AssertionError
	{
		int i =this.unread_at;
		final char [] u = this.unread; 
		if (i>=u.length) throw new AssertionError("Can't un-read so many characters");		
		u[i]=c;
		i++;
		this.unread_at = i;
	};
	/** Checks what is under cursor in stream.
		@return read character
		@throws EUnexpectedEof if end of file was encounterd
		@throws IOException if low level have failed.
	*/
	protected final char peek()throws EUnexpectedEof, IOException
	{		
		int r = tryPeek();
		if (r==-1) throw new EUnexpectedEof();
		return (char)r;
	};	
	/** Checks what is under cursor in stream.
		@return -1 if end of stream or 0...0xFFFF representing character under cursor.
		@throws EUnexpectedEof if end of file was encounterd
		@throws IOException if low level have failed.
	*/
	protected final int tryPeek()throws IOException
	{
		//We need to use non cache cleaning versions, so easiest
		//is to peek directly in cache.
		int i =unread_at;		
		if (i==0)
		{
			final int r = readFromInput();
			assert(r>=-1);
			assert(r<=0x0FFFF);			
			if (r==-1) return -1;
			final char c = (char)r;
			_unread(c);	//put it back.
			return c;
		}else
		{
			return unread[i-1];
		}
	};
	/**
			Tests if next {@link #read} will throw
			{@link UnexpectedEof}. Performs read of stream
			if necessary to test the condition.
			
			@return true if at end of stream
			@throws IOException if low level have failed.
	*/
	protected final boolean isEof()throws IOException
	{
		//We return false if there is anything to
		//un-read, or read char and unread it if else.
		int i =unread_at;
		if (i==0)
		{
			//This operation must not move logic cursor so
			final int r = readFromInput(); 	
			assert(r>=-1);
			assert(r<=0x0FFFF);				
			if (r==-1) return true;
			_unread((char)r);	//and un-read it.
			return false;
		}else
			return false;
	};
	/* *************************************************************************
	
	
				Decoding
	
	
	* *************************************************************************/
	/** Invoked when {@link #readEscaped} encountered &amp; Should process escape or throw
	@return processed char.
	@throws EUnexpectedEof if end of file was encounterd
	@throws IOException if low level have failed.
	@see #readUnsafe
	*/
	protected char readAmpXMLEscapes()throws EUnexpectedEof, IOException
	{
		//We do support a limited sub-set.
		standard_XML_amp_escapes_matcher.reset();
		standard_XML_amp_escapes_matcher.match('&');
		for(;;)
		{
			char c = read();
			int r = standard_XML_amp_escapes_matcher.match(c);
			if (r==-2)
				throw new ECorruptedFormat("This program does understand only &amp;&gt;&lt; XML escapes. "+
										   " Please use "+settings.ESCAPE_CHARACTER+"0000"+settings.ESCAPE_END_CHARACTER+" hex escape instead.");
			if (r>=0) return (char)r;	
		}
	};
	/** Reads character, detects
		if it is an escape sequence and 
		un-escapes it. This is lenient method
		assuming that end-of-escape character is optional.
		If it is there, it is consumend. If it is not, what
		could be processed is processed and what could not
		is put back to stream.
		
		@return un-escaped or directly read character.
		@throws IOException if failed at low level or reached end of stream 
				during processing.
	*/
	protected char readEscaped()throws IOException
	{
		char c = read();
		/*	
			We are leninent and recognize as an end-of escape:
			- settings.ESCAPE_END_CHARACTER - as aconsumable end consumable
			- any unexpected character as a non-consumable.	
		*/
		final char esc =settings.ESCAPE_CHARACTER; 
		final char end_esc = settings.ESCAPE_END_CHARACTER;
		if (c==esc)
		{
				char digit = read();
				//detect self-escape
				if (digit==esc)
				{
					c= read();
					if (c!=end_esc)
					{			
						unread(c);	//put allowed non-escape ends back for processing
					};
					return esc;
				}else
				{
					//variable length hex escape.
					int unescaped = HEX(digit);
					if (unescaped==-1) throw new ECorruptedFormat("Found "+esc+" escape start but "+digit+" follows instead of 0...F");
					int i=4;
					for(;;)
					{
						digit = read();
						int nibble = HEX(digit);
						if (nibble==-1)
						{
							if (digit!=end_esc) unread(digit);	//non-consumable end.
							return (char)unescaped;
						}
						{	//and it should be a digit.
							if ((--i)==0) throw new ECorruptedFormat("Escape sequence too long");
							unescaped <<= 4;
							unescaped += nibble;
						}
					}
				}
		}else
		if (c=='&')
			readAmpXMLEscapes();
		else
			return c;
	};
	/** Hex to nibble conversion
	@param digit 0...9,a...f,A...F
	@return value 0x0...0x0F or -1 if not hex digit.
	*/
	private static int HEX(char digit)
	{
		if ((digit>='0')&&(digit<='9')) return digit-'0';
		if ((digit>='a')&&(digit<='f')) return digit-'a'+10;
		if ((digit>='A')&&(digit<='F')) return digit-'A'+10;
		return -1;
	};
	/* *************************************************************************
			State validation	
	* *************************************************************************/
	/** Validates if stream is readable.
	Stream is not readbale if closed or if root element is closed.
	@throws EClosed if closed
	@throws EUnexpectedEof if root element is closed 
	*/
	protected void validateReadable()throws IOException
	{
		switch(state)
		{
			case STATE_IDLE: break;
			case STATE_CLOSED: throw new EClosed();
			case STATE_ROOT_CLOSED: throw new EUnexpectedEof("root "+settings.ROOT_ELEMENT+" XML element is closed, can't read anymore.");
		};
	};
	
	/* *************************************************************************
	
	
				IIndicatorReadFormat
	
	
	* *************************************************************************/
	/* -------------------------------------------------		
				Information and settings.		
	-------------------------------------------------*/
	/** Always zero, this format does not support registrations */
	public final int getMaxRegistrations(){ return 0; };
	/** Returns {@link #isDescribed} because it is always flushing */
	public final boolean isFlushing(){ return isDescribed(); };
	
	public void setMaxSignalNameLength(int characters)
	{
		assert(characters>=0):"characters="+characters;
		//Note: Contract allows unpredictable results when
		//setting it during work, so we don't have to preserve
		//previously buffered data.
		assert((unread_at==0)
				&&
				(signal_name_buffer.isEmpty())
				&&
				(token_buffer.isEmpty())):"Can't set name length when processing is in progress.";
		//we do implement it by re-allocating bound buffer.
		signal_name_buffer = new CBoundAppendable(characters);
		//no re-allocate other buffers if necessary
		if (characters>token_buffer.capacity())
		{
			token_buffer = new CBoundAppendable(characters);
		};
		if (characters>unread.length)
		{
			unread = new char[characters];
		};
	};
	/* -------------------------------------------------		
				Indicators	
	-------------------------------------------------*/
	public TIndicator getIndicator()throws IOException
	{				
		/* Notes:
		
		  This method is, by definition, said to NOT
		  move the cursor inside a stream. We should
		  understand it as a "logic cursor" not a physical
		  one. This is due to the fact, that this method
		  may be called inside a "skippable white-space".
		  
		  Notice, the status of what white-spaces are 
		  skippable depends on if we read character 
		  block or not.				   	
		*/
		
		if (indicator_cache!=null) return indicator_cache;
		loop:
		for(;;)
		{
			//Now we need to do a real stream processing		
			//Handle closed and root terminated states.
			switch(state)
			{
				case STATE_IDLE: break;
				case STATE_CLOSED: throw new EClosed();
				case STATE_ROOT_CLOSED: return TIndicator.EOF;
			};	
			int ci = tryRead();
			if (ci==-1)return TIndicator.EOF;
			char c = (char)ci;
			switch(c)
			{
				case -1: 
				case '<': 
						{
							TIndicator i = processTag();
							if (i!=null)
							{ 
								is_inside_character_block = false;	//comments do not clear this status.
								indicator_cache = i;
								return i;
							}else							
								break; 
						}
				case '>': 
						unread(c);
						throw new ECorruptedFormat("Unexpected >");
				default : 
						if (Character.isWhitespace(c)
						{
							//but now we should decide how do we
							//skip it?
							//Basically if we are NOT inside a character
							//block we can just skip them.
							//If we ARE inside character block we have
							//to skip them, but if we hit non-XML-tag character
							//we MUST unread them because they are part of
							//text.
							if (is_inside_character_block)
							{
								if (trySkipCharBlockWhiteSpaces()) continue loop;
								return TIndicator.DATA;
							}else
							{
								 skipWhiteSpaces();
								 continue loop;
							};
						}else
						{
							//this is a simple condition
							unread(c);
							return TIndicator.DATA;
						};
			};
		};
	};
	/** Tries to skippable white-spaces if within character block.
	After return from this method stream may be either at
	&gt; character or have cursor not moved.
	Can't be called if outside character block.
	@return true if characters were skipped and cursor is at &gt;
			false if detected EOF or other non-whitespace character	
	@throws IOException if failed at low level
	@throws EFormatBoundaryExceeded if there were too many spaces.
	*/
	private boolean trySkipCharBlockWhiteSpaces()throws IOException,EFormatBoundaryExceeded
	{
		assert(is_inside_character_block);
		int skipped = 0;
		token_buffer.reset();
		do{
			//remember about limiting.
			if (skipped==maximum_idle_characters)
				throw new EFormatBoundaryExceeded("XML comment too long, possible Denial Of Service attack?");			
			
			ci = tryRead();
			if (ci==-1)
			{
				uread(token_buffer);
		 		token_buffer.reset();
		 		return false;
			};
			c = (char)ci;
			token_buffer.append(c);									
			skipped++;									
		 }while(Character.isWhitespace(c))
		 //Now we reached the end of white space sequence inside a character block.
		 //If this is an XML token, we had trailing whitespaces. If not in-body
		 //whitespaces which has to be un-read.
		 if (c=='<')
		 {
			 token_buffer.reset();
			 return true;
		 }
		 uread(token_buffer);
		 token_buffer.reset();
		 return false;
	};
	/** Skips skippable white-spaces.
	After return from this method stream may be either at
	non-whitespace character or end-of-stream.
	Can't be called if within character block.
	@throws IOException if failed at low level
	@throws EFormatBoundaryExceeded if there were too many spaces.
	*/
	private void skipWhiteSpaces()throws IOException,EFormatBoundaryExceeded
	{
		assert(!is_inside_character_block);
		int skipped = 0;
		do{
			//remember about limiting.
			if (skipped==maximum_idle_characters)
				throw new EFormatBoundaryExceeded("XML comment too long, possible Denial Of Service attack?");
			ci = tryRead();
			if (ci==-1) return;
			c = (char)ci;
			skipped++;									
		 }while(Character.isWhitespace(c))
		 unread(c);
	};
	/** Invoked after &gt; was read from stream. Is expected
	to read and process start tag. 
	@return indicator corresponding to processed tag
		or null if tag is processed and should be ignored
		because it is a comment tag, processing or root.
		Caller must search for next tag.
	@throws IOException if failed to recognize tag.
	*/
	private TIndicator processTag()throws IOException
	{
		//Split to begin and end tags.
		char c = read();
		if (c=='/')
			return processClosingTag();
		else
		{
			unread(c);
			if (tryCommentTag()) return null;
			if (tryProcessingTag()) return null;						
			return processOpeningTag();
		}
	};
	/** Invoked after <code>&gt;</code> was read from stream. Is expected
	to read and process start tag. This method will be called only 
	after comment or processing tags are checked.
	@return indicator corresponding to processed tag
		or null if tag is processed and should be ignored
	@throws IOException if failed to recognize tag.
	*/
	private TIndicator processOpeningTag()
	{
		//Note: The UnbufferingMatcher is not a best idea to 
		//compare with strings without a clear end-indicator
		//because it is "greedy" and will cause early trigger
		//on shortest matching string. XML tags may have infinite
		//number of white-spaces after them or > to indicate ending
		//element. We also need to treat unrecognized tags
		//as short event begin, so it is better to just buffer it. 
		... todo ...
	};
	/** Buffers tag in {@link #token_buffer}. Once this
	method return the stream cursor is at the first non-whitespace
	character after the tag which may be > or a first character
	of attribute name.
	@return {@link #token_buffer} filled with tag
	*/ 
	private CBoundAppendable readTag()
	{
		token_buffer.reset();
		... todo...
	};
	/** Buffers attribute value in {@link #token_buffer}. Once this
	method return the stream cursor is at the first non-white space
	character after	attribute value, which should be > 
	@param required_attribute name of required attribute
	@return {@link #token_buffer} filled with value of an attribute
	@throws ECorruptedFormat if there is no attribute, or attribute name
		does not match.
	*/ 
	private CBoundAppendable readAttribute(String required_attribute)
	{
		token_buffer.reset();
		... todo...
	};
	/** Invoked after <code>&gt;/</code> was read from stream. Is expected
	to read and process start tag. 
	@return indicator corresponding to processed tag
		or null if tag is processed and should be ignored
	@throws IOException if failed to recognize tag.
	*/
	private TIndicator processClosingTag()
	{
		//basically we need to buffer tag and check if we have match on stack
		//Notice, processing root tag should also be done here and should
		//toggle stream to be unusable.	
		... todo ...
	};
	
	
	/** Invoked after &gt; was read from stream to check,
	if it is a comment tag and eventually skip it.
	@return true if processed it, false if it was not a comment.
			If false stream cursor is not moved.
	*/
	private boolean tryCommentTag()throws IOException
	{
		//recognize comment.
		char c=readSafe();
		if (c!='!') { unread(c); return false; };
		c = readSafe();
		if (c!='-')  { unread(c); unread('!');return false; };
		c = readSafe();
		if (c!='-')  { unread(c);unread('-');unread('!');  return false; };
		//Now skip comment, testing maximum_idle_characters limit.
		int skipped = 0;
		for(;;)
		{
			c = read();
			//detect trailing condition.
			if (c=='-')
			{
				c=read();
				if (c=='-')
				{
					c=read();
					if (c=='>') return true;
				}
			};
			//just skip it.
			if (skipped==maximum_idle_characters)
					throw new EFormatBoundaryExceeded("XML comment too long, possible Denial Of Service attack?");			
			skipped++;
		}
	};
	/** Invoked after &gt; was read from stream to check,
	if it is a processing instruction  tag and eventually skip it.
	@return true if processed it, false if it was not a processing tag.
			If false stream cursor is not moved.
	*/
	private boolean tryProcessingTag()throws IOException
	{
		//recognize processing command.
		char c=readSafe();
		if (c!='?') { unread(c); return false; };
		//Now skip processing command, testing maximum_idle_characters limit.
		int skipped = 0;
		for(;;)
		{
			c = read();
			//detect trailing condition.
			if (c=='?')
			{
				c=read();
				if (c=='>') return true;
			};
			//just skip it.
			if ((maximum_idle_characters>=0)&&(skipped==maximum_idle_characters))
					throw new EFormatBoundaryExceeded("XML processig block too long, possible Denial Of Service attack?");			
			skipped++;
		}
	};
};