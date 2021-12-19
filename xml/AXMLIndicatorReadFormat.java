package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.util.CBoundAppendable;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EClosed;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import sztejkat.abstractfmt.ECorruptedFormat;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EDataMissmatch;
import sztejkat.abstractfmt.ENoMoreData;
import java.io.*;
import java.util.ArrayList;

/**
	A reading counterpart for {@link AXMLIndicatorWriteFormatBase}
	using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>.
	<p>
	This format is NOT strictly validating XML data and does:
	<ul>
		<li>do allow bare format, without root element;</li>
		<li>do allow "anonymous end tags" <code>&lt;/&gt;</code>;</li>
		<li>the &amp; escapes are only partially recognized and if &amp; is encountered
		anywhere an exception is thrown to indicate that XML file may be XML compilant
		but we are not fully XML compilant.
		<p>
		This class recognizes: <code>&amp;gt; &amp;amp; &amp;lt;</code>  ;</li>
	</ul>
	This class is using a stack to track XML elements, so the longer containg 
	XML element chain is, the more memory it consumes. This means that the user
	of this class should take care about recursion limit.
	<p>
	If this class encounters root element then the opening root tag is
	silently skipped while closing root tag toggles stream to 
	permanently return end-of-file on every operation except {@link #close}.
*/
public abstract class AXMLIndicatorReadFormat extends AXMLIndicatorReadFormatBase 
{
				/* ***********************************************
				
						XML processing
				
				************************************************/
				/** A bound, limitied buffer for colleting all
				xml elements, attributes, tokens and etc. 
				Non final because can be re-initialized when
				name limit is set since it must also accomodate
				short-encoded begin signal names.
				*/
				private CBoundAppendable token_buffer;
				/** Used to validate name lengths */
				private int max_signal_name_length;
				/* ***********************************************
				
						Low level I/O
				
				************************************************/
				/** A maximum length of any skippable block, like
				comments and etc. Used to avoid DoS attacks
				by providing XML with infinite comment, processing
				or skippable characters.*/
				private final int maximum_idle_characters;
				/** Value incremented by any skipping routine
				and clared if data are picked */
				private int skipped_idle_character;
				/* ***********************************************
				
						State
				
				************************************************/
				/** Used to track open/close events for XML elements */
				private final ArrayList<String> xml_elements_stack;				
				/** Indicator cache.
				Set to null to say that we don't know what is under
				a cursor. Cleared by any cursor movement.
				 */
				private TIndicator indicator_cache;
				/** Cache for {@link #getSignalName} */
				private String signal_name_cache;
				
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
				/** Stream definition has root element, but root element
				was not read */
				private static final byte STATE_ROOT_PENDING = (byte)3;
				
	/** Creates 
	@param settings XML settings to use
	@param maximum_idle_characters safety limit, setting upper boundary
		 for comment, processing commands and other skipable characters.
		 <p>
		 Non zero, positive
	*/
	protected AXMLIndicatorReadFormat(
					final CXMLSettings settings,
					final int maximum_idle_characters
					)
	{
		super(settings,
				//un-read buffer depth is practically speaking related only to
				//slight read-ahead when reading and processing tokens so we can
				//just set it to some reasonable value
				8
			  );
		assert(maximum_idle_characters>0);
		this.maximum_idle_characters=maximum_idle_characters;
		this.max_signal_name_length=1024; //contract default size.
		//now compute maximum necessary for token buffer.
		//This is the signal name length, 37 characters for floating points
		//or max element name length
		this.token_buffer = new CBoundAppendable(Math.max(1024,settings.getMaximumTokenLength()));
		//pre-allocate some stack
		this.xml_elements_stack = new ArrayList<String>(32);//same as write format, just for symetry.
		this.state = settings.ROOT_ELEMENT==null ? STATE_RDY : STATE_ROOT_PENDING;
	};
	/* ************************************************************
	
			Tuned superclass services
	
	* ************************************************************/
	/** Overriden to clear indicators cache. */
	protected int tryRead()throws EUnexpectedEof, IOException
	{
		indicator_cache = null;
		return super.tryRead();
	};	
	/** Overriden to clear indicators cache. */
	protected void unread(char c)throws AssertionError
	{
		indicator_cache = null;
		super.unread(c);
	};	
	/** Overriden to clear indicators cache. */
	protected void unread(CharSequence c)throws AssertionError
	{
		indicator_cache = null;
		super.unread(c);
	};	
	
	/* ****************************************************
		
			Services required from subclasses
			
	*****************************************************/
	/** Called in {@link #close} when closed for a first 
	time.
	@throws IOException if failed.
	*/
	protected abstract void closeOnce()throws IOException;
	
	
	
	/* *************************************************************************
			State validation	
	* *************************************************************************/
	/** Validates if stream is readable.
	Stream is not readbale if closed or if root element is closed.
	This method also validates if root element was read and attempts
	to silently fetch it.
	@throws EClosed if closed
	@throws EUnexpectedEof if root element is closed 
	*/
	protected void validateReadable()throws IOException
	{
	 System.out.println("validateReadable state="+state);
		switch(state)
		{
			case STATE_RDY: break;
			case STATE_CLOSED: throw new EClosed();
			case STATE_ROOT_CLOSED: throw new EUnexpectedEof("root <"+settings.ROOT_ELEMENT+"> XML element is closed, can't read anymore.");
			case STATE_ROOT_PENDING:
			  System.out.println("validateReadable --> pending");
					//This is a state in which root element was not read, but 
					//an operation was requested.
					//Usually root is silently picked up by getIndicator, both closing and opening.
					assert(settings.ROOT_ELEMENT!=null);
					getIndicator();
					if (!xml_elements_stack.contains(settings.ROOT_ELEMENT))
						throw new EBrokenFormat("root element <"+settings.ROOT_ELEMENT+"> required, but not found");
					state =  STATE_RDY;
					break;
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
		assert(characters>0):"characters="+characters;
		//Note: Contract allows unpredictable results when
		//setting it during work, so we don't have to preserve
		//previously buffered data.
		assert(token_buffer.isEmpty()):"Can't set name length when processing is in progress.";
		//no re-allocate other buffers if necessary
		if (characters>token_buffer.capacity())
		{
			token_buffer = new CBoundAppendable(characters);
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
		  may be called inside a "skippable white-space"
		  and that we must move physical cursor to read
		  indicator 
		*/		
		if (indicator_cache!=null) return indicator_cache;		
		loop:
		for(;;)
		{
			//Now we need to do a real stream processing		
			//Handle closed and root terminated states.
			switch(state)
			{
				case STATE_RDY: break;
				case STATE_CLOSED: throw new EClosed();
				case STATE_ROOT_CLOSED: return TIndicator.EOF;
			};	
			int ci = tryRead();
			if (ci==-1)return TIndicator.EOF;
			char c = (char)ci;
			switch(c)
			{
				case '<': 
						{
							TIndicator i = processTag();
							if (i!=null)
							{ 
								//cache value for later use.
								indicator_cache = i;
								skipped_idle_character=0;
								return i;
							}else							
								break; 
						}
				case '>': 
						unread(c);
						throw new EBrokenFormat("Unexpected >");
				default : 						
						if (Character.isWhitespace(c))
						{
							if (skipped_idle_character==maximum_idle_characters) 
								throw new EFormatBoundaryExceeded("Too many skippable white spaces, possible Denial Of Service attack?");
							//When cursor is at white space, this white-space
							//is skippable or is a part of data block. We may
							//safely assume, that it is a part of data block
							//if next character after it is NOT <, that is it
							//is not before XML tag.
							//A data white space must be always replaced with ' '							
			
							ci = tryRead();
							if (ci==-1)
							{
								unread(c);
								return TIndicator.EOF;
							}else
							{
								c =(char)ci;
								if (c=='<')
								{
									//skipped space, retry processing
									unread(c);
									continue loop;
								}else
								if (Character.isWhitespace(c))
								{
									//first was surely a skippable white space,
									//but current might be not, because it may be a part of character 
									//block followed by letter.
									unread(c);
									skipped_idle_character++;	//to capture too many skips.
									continue loop;
								}else
								{
									//Now we are hitting data. The white space
									//and data character must be present									
									unread(c);
									unread(' ');	//<-- fixed white-space conversion according to XML specs.
									indicator_cache=TIndicator.DATA; 
									skipped_idle_character=0;
									return TIndicator.DATA; 
								}
							}
						}else
						{
							//this is a simple condition
							unread(c);
							indicator_cache=TIndicator.DATA;
							skipped_idle_character=0;
							return TIndicator.DATA;
						}
			}
		}
	};
	
	@Override public void next()throws IOException
	{
		validateReadable();
		//Now in our processing style if a logic cursor is
		//at non-data indicator, then physical cursor is
		//at first character after it. So this is enough to 
		//clear cache.
		if (getIndicator()!=TIndicator.DATA)
		{
			indicator_cache = null;
		}else
		{
			//Now if we are inside data we should be able scan to next
			// < regardles of what is there.
			for(;;)
			{
				int c = read();
				if (c==-1) throw new EUnexpectedEof();
				if (c=='<')
				{ 
					unread((char)c);
					return;
				};
				//Now since we may need to skip large blocks of data 
				//we do reset skip counter at each non-white-space.
				if (!Character.isWhitespace(c))
				{
					 skipped_idle_character=0;
				}else
				{
					if (skipped_idle_character ==maximum_idle_characters)
						throw new EFormatBoundaryExceeded("Too many idle characters to skip. Possible DoS attack?");
					skipped_idle_character++;
				}
			}
		}
	};
	@Override public String getSignalName()
	{
		//Theoretically we should check if we are at valid indicator, but
		//we do NOT have to be defensive.
		return signal_name_cache;
	};
	/** Always throws, as this stream does not support it. */
	@Override public final int getSignalNumber()
	{
		throw new IllegalStateException("This stream does not support it at all");
	};
	/* ..............................................
				Tags	
	..............................................*/	
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
	/** Helper for {@link #processOpeningTag}/{@link #processClosingTag}
	checking if there are no attributes
	@param element used to format error message in thrown exception
	@throws EBrokenFormat if there are attributes */
	private void validateNoAttributes(String element)throws IOException
	{
		if (read()!='>')
			throw new EBrokenFormat("<"+element+"> element does not take attributes"); 
	};
	/** Invoked after <code>&gt;</code> was read from stream. Is expected
	to read and process start tag. This method will be called only 
	after comment or processing tags are checked.
	@return indicator corresponding to processed tag
		or null if tag is processed and should be ignored
	@throws IOException if failed to recognize tag.
	*/
	private TIndicator processOpeningTag()throws IOException
	{
		//Collect tag.
		CBoundAppendable b = readTag();	//filled the token_buffer
		//The opening tag may be one of indicator tags, long encoded name, root element
		//or short encoded name.		
		//Just compare them using quick compare routines.
		if ((settings.ROOT_ELEMENT!=null)&&(b.equalsString(settings.ROOT_ELEMENT)))
		{
			//Check if root is not already on stack? If it would be, it would
			//be first element.
			if (!xml_elements_stack.isEmpty())
			{
				if (settings.ROOT_ELEMENT.equals(xml_elements_stack.get(0)))
						throw new EBrokenFormat("Nested root <"+settings.ROOT_ELEMENT+"> element");
			};
			//Root takes no attributes.
			validateNoAttributes(settings.ROOT_ELEMENT);
			xml_elements_stack.add(settings.ROOT_ELEMENT);//adding constant instead of b.toString() avoids string allocation
			//root is ignored, look-up for next tag is necessary. Since our test for attributes already skipped > we
			//can safely proceed.
			return null; 
		}else
		if (b.equalsString(settings.EVENT))
		{
			//long for do require attribute carying singnal name
			if (peek()=='>') throw new EBrokenFormat("<"+settings.EVENT+"> requires "+settings.SIGNAL_NAME_ATTR+" attribute");
			b = readAttributeName();
			if (!b.equalsString(settings.SIGNAL_NAME_ATTR))
				throw new EBrokenFormat("<"+settings.EVENT+"> requires "+settings.SIGNAL_NAME_ATTR+" attribute while \""+b+"\" was found");
			b = readAttributeValue();
			//prepare it.
			String n  = b.toString();
			if (n.length()>max_signal_name_length)
				throw new EFormatBoundaryExceeded("signal name \""+n+"\" too long");
			//check length. Notice, tag buffer might be longer, so we need to test it.		
			this.signal_name_cache = n;
			//put on stack
			xml_elements_stack.add(settings.EVENT);	
			return TIndicator.BEGIN_DIRECT;			
		}else
		if (b.equalsString(settings.BOOLEAN_ELEMENT))
		{
			validateNoAttributes(settings.BOOLEAN_ELEMENT);
			xml_elements_stack.add(settings.BOOLEAN_ELEMENT);
			return TIndicator.TYPE_BOOLEAN;
		}else
		if (b.equalsString(settings.BYTE_ELEMENT))
		{
			validateNoAttributes(settings.BYTE_ELEMENT);
			xml_elements_stack.add(settings.BYTE_ELEMENT);
			return TIndicator.TYPE_BYTE;
		}else
		if (b.equalsString(settings.CHAR_ELEMENT))
		{
			validateNoAttributes(settings.CHAR_ELEMENT);
			xml_elements_stack.add(settings.CHAR_ELEMENT);
			return TIndicator.TYPE_CHAR;
		}else
		if (b.equalsString(settings.SHORT_ELEMENT))
		{
			validateNoAttributes(settings.SHORT_ELEMENT);
			xml_elements_stack.add(settings.SHORT_ELEMENT);
			return TIndicator.TYPE_SHORT;
		}else
		if (b.equalsString(settings.INT_ELEMENT))
		{
			validateNoAttributes(settings.INT_ELEMENT);
			xml_elements_stack.add(settings.INT_ELEMENT);
			return TIndicator.TYPE_INT;
		}else
		if (b.equalsString(settings.LONG_ELEMENT))
		{
			validateNoAttributes(settings.LONG_ELEMENT);
			xml_elements_stack.add(settings.LONG_ELEMENT);
			return TIndicator.TYPE_LONG;
		}else
		if (b.equalsString(settings.FLOAT_ELEMENT))
		{
			validateNoAttributes(settings.FLOAT_ELEMENT);
			xml_elements_stack.add(settings.FLOAT_ELEMENT);
			return TIndicator.TYPE_FLOAT;
		}else
		if (b.equalsString(settings.DOUBLE_ELEMENT))
		{
			validateNoAttributes(settings.DOUBLE_ELEMENT);
			xml_elements_stack.add(settings.DOUBLE_ELEMENT);
			return TIndicator.TYPE_DOUBLE;
		}else
		if (b.equalsString(settings.BOOLEAN_BLOCK_ELEMENT))
		{
			validateNoAttributes(settings.BOOLEAN_BLOCK_ELEMENT);
			xml_elements_stack.add(settings.BOOLEAN_BLOCK_ELEMENT);
			return TIndicator.TYPE_BOOLEAN_BLOCK;
		}else
		if (b.equalsString(settings.BYTE_BLOCK_ELEMENT))
		{
			validateNoAttributes(settings.BYTE_BLOCK_ELEMENT);
			xml_elements_stack.add(settings.BYTE_BLOCK_ELEMENT);
			return TIndicator.TYPE_BYTE_BLOCK;
		}else
		if (b.equalsString(settings.CHAR_BLOCK_ELEMENT))
		{
			validateNoAttributes(settings.CHAR_BLOCK_ELEMENT);
			xml_elements_stack.add(settings.CHAR_BLOCK_ELEMENT);
			return TIndicator.TYPE_CHAR_BLOCK;
		}else
		if (b.equalsString(settings.SHORT_BLOCK_ELEMENT))
		{
			validateNoAttributes(settings.SHORT_BLOCK_ELEMENT);
			xml_elements_stack.add(settings.SHORT_BLOCK_ELEMENT);
			return TIndicator.TYPE_SHORT_BLOCK;
		}else
		if (b.equalsString(settings.INT_BLOCK_ELEMENT))
		{
			validateNoAttributes(settings.INT_BLOCK_ELEMENT);
			xml_elements_stack.add(settings.INT_BLOCK_ELEMENT);
			return TIndicator.TYPE_INT_BLOCK;
		}else
		if (b.equalsString(settings.LONG_BLOCK_ELEMENT))
		{
			validateNoAttributes(settings.LONG_BLOCK_ELEMENT);
			xml_elements_stack.add(settings.LONG_BLOCK_ELEMENT);
			return TIndicator.TYPE_LONG_BLOCK;
		}else
		if (b.equalsString(settings.FLOAT_BLOCK_ELEMENT))
		{
			validateNoAttributes(settings.FLOAT_BLOCK_ELEMENT);
			xml_elements_stack.add(settings.FLOAT_BLOCK_ELEMENT);
			return TIndicator.TYPE_FLOAT_BLOCK;
		}else
		if (b.equalsString(settings.DOUBLE_BLOCK_ELEMENT))
		{
			validateNoAttributes(settings.DOUBLE_BLOCK_ELEMENT);
			xml_elements_stack.add(settings.DOUBLE_BLOCK_ELEMENT);
			return TIndicator.TYPE_DOUBLE_BLOCK;
		}else
		{
			//short encoded signal name.
			String n  = b.toString();
			if (n.length()>max_signal_name_length)
				throw new EFormatBoundaryExceeded("signal name \""+n+"\" too long");
			//check length. Notice, tag buffer might be longer, so we need to test it.		
			this.signal_name_cache = n;
			//put on stack
			xml_elements_stack.add(n);	
			return TIndicator.BEGIN_DIRECT;		
		}
	};
	/** Buffers tag in {@link #token_buffer}. Once this
	method return the stream cursor is at the first non-whitespace
	character after the tag which may be &gt; or a first character
	of attribute name.
	@return {@link #token_buffer} filled with tag
	@throws IOException if failed
	*/ 
	private CBoundAppendable readTag()throws IOException
	{
		token_buffer.reset();
		char c= read();
		if (!isValidStartingTagChar(c)) throw new EBrokenFormat("\""+c+"\" is not a valid first character in XML tag");
		token_buffer.append(c);
		for(;;)
		{
			c = read();
			if (isValidTagChar(c))
				token_buffer.append(c);	//this will be somewhat limited against DoS attack.
			else
			{
				//Check if we stopped collecting because we get end of token or just non-name char?
				if (!((Character.isWhitespace(c)||(c=='>'))))
					throw new EBrokenFormat("\""+c+"\" is not valid tag terminator");
				//Now tag terminator is >, whitespace. We need to skip white spaces to get to > or attribute name.
				while(Character.isWhitespace(c))
				{
					c = read();
				};
				//now un-read it.
				unread(c);	//to let later test for presence of an attribute.
				return token_buffer;
			} 
		}
	};
	/** Buffers attribute name in {@link #token_buffer}. Once this
	method return the stream cursor is at the first first character
	of attribute value which is "
	@return {@link #token_buffer} filled with attribute name
	@throws IOException if failed
	*/ 
	private CBoundAppendable readAttributeName()throws IOException
	{
		//We assume that attribute has the same charset as tag.
		token_buffer.reset();
		char c= read();
		if (!isValidStartingTagChar(c)) throw new EBrokenFormat("\""+c+"\" is not a valid first character in XML tag");
		token_buffer.append(c);
		for(;;)
		{
			c = read();
			if (isValidTagChar(c))
				token_buffer.append(c);	//this will be somewhat limited against DoS attack.
			else
			{
				//Attribute may be followed only by white space or =
				//Check if we stopped collecting because we get end of token or just non-name char?
				if (!((Character.isWhitespace(c)||(c=='='))))
					throw new EBrokenFormat("\""+c+"\" is not valid attribute terminator");
				//Now tag terminator is >, whitespace. We need to skip white spaces to get to > or attribute name.
				while(Character.isWhitespace(c))
				{
					c = read();
				};
				if (c!='=') throw new EBrokenFormat("\""+c+"\" found but = is expected after an attribute name");
				//now un-read it.
				//Now skip remaning white spaces.
				while(Character.isWhitespace(c))
				{
					c = read();
				};
				if (c!='\"') throw new EBrokenFormat("\""+c+"\" found but \" is expected after = in attribute");
				unread(c);
				return token_buffer;
			} 
		}
	};
	
	
	/** Buffers attribute value in {@link #token_buffer}, assuming cursor
	is at opening ". Once this
	method return the stream cursor is at the first first non-white space
	character after value which should be either &gt; or next attribute name.
	@return {@link #token_buffer} filled with attribute value stripped of "
	@throws IOException if failed
	*/ 
	private CBoundAppendable readAttributeValue()throws IOException
	{
		//We assume that attribute has the same charset as tag.
		token_buffer.reset();
		char c= read();
		assert(c=='\"');		
		for(;;)
		{
			int ci = read();
			if (ci>=0)
			{
				//unescaped should be subject of detection of \"
				if (ci=='\"')
				{
					//end of attribute
					//skip till non-white space.
					do
					{
						c = read();
					}while(Character.isWhitespace(c));
					unread(c);
					return token_buffer;
				}
				//Now a bit of non XML space treatment, we just allow
				//all characters in here.
			};
			//accepted as to be appended are just appended.
			token_buffer.append((char)(-ci -1));
		}
	};
	
	
	
	/** Invoked after <code>&gt;/</code> was read from stream. Is expected
	to read and process start tag. 
	@return indicator corresponding to processed tag
		or null if tag is processed and should be ignored
	@throws IOException if failed to recognize tag.
	*/
	private TIndicator processClosingTag()throws IOException
	{
		//basically we need to buffer tag and check if we have match on stack
		//Notice, processing root tag should also be done here and should
		//toggle stream to be unusable.	
		CBoundAppendable b = readTag();	//filled the token_buffer
		if (xml_elements_stack.isEmpty()) throw new EBrokenFormat("too many closing tags, \""+b+"\"");		
		//validate, allowing anonymous closing tag
		final String closed =  xml_elements_stack.remove(xml_elements_stack.size()-1);		
		if (!b.isEmpty())
		{
			if (!b.equalsString(closed)) throw new EBrokenFormat("Closing tag does not match, expected \""+closed+"\" found \""+b+"\"");	
		};
		validateNoAttributes(closed);
		//act accordingly.
		if ((settings.ROOT_ELEMENT!=null)&&(b.equalsString(settings.ROOT_ELEMENT)))
		{
			//toggle to unusable.
			state = STATE_ROOT_CLOSED;
			return null;	
		}else
		if (closed.equals(settings.EVENT))
		{
			return TIndicator.END;			
		}else
		if (closed.equals(settings.BOOLEAN_ELEMENT))
		{
			return TIndicator.FLUSH_BOOLEAN;
		}else
		if (closed.equals(settings.BYTE_ELEMENT))
		{
			return TIndicator.FLUSH_BYTE;
		}else
		if (closed.equals(settings.CHAR_ELEMENT))
		{
			return TIndicator.FLUSH_CHAR;
		}else
		if (closed.equals(settings.SHORT_ELEMENT))
		{
			return TIndicator.FLUSH_SHORT;
		}else
		if (closed.equals(settings.INT_ELEMENT))
		{
			return TIndicator.FLUSH_INT;
		}else
		if (closed.equals(settings.LONG_ELEMENT))
		{
			return TIndicator.FLUSH_LONG;
		}else
		if (closed.equals(settings.FLOAT_ELEMENT))
		{
			return TIndicator.FLUSH_FLOAT;
		}else
		if (closed.equals(settings.DOUBLE_ELEMENT))
		{
			return TIndicator.FLUSH_DOUBLE;
		}else
		if (closed.equals(settings.BOOLEAN_BLOCK_ELEMENT))
		{
			return TIndicator.FLUSH_BOOLEAN_BLOCK;
		}else
		if (closed.equals(settings.BYTE_BLOCK_ELEMENT))
		{
			return TIndicator.FLUSH_BYTE_BLOCK;
		}else
		if (closed.equals(settings.CHAR_BLOCK_ELEMENT))
		{
			return TIndicator.FLUSH_CHAR_BLOCK;
		}else
		if (closed.equals(settings.SHORT_BLOCK_ELEMENT))
		{
			return TIndicator.FLUSH_SHORT_BLOCK;
		}else
		if (closed.equals(settings.INT_BLOCK_ELEMENT))
		{
			return TIndicator.FLUSH_INT_BLOCK;
		}else
		if (closed.equals(settings.LONG_BLOCK_ELEMENT))
		{
			return TIndicator.FLUSH_LONG_BLOCK;
		}else
		if (closed.equals(settings.FLOAT_BLOCK_ELEMENT))
		{
			return TIndicator.FLUSH_FLOAT_BLOCK;
		}else
		if (closed.equals(settings.DOUBLE_BLOCK_ELEMENT))
		{
			return TIndicator.FLUSH_DOUBLE_BLOCK;
		}else
		{
			return TIndicator.END;		
		}
	};
	
	
	/** Invoked after &gt; was read from stream to check,
	if it is a comment tag and eventually skip it.
	@return true if processed it, false if it was not a comment.
			If false stream cursor is not moved.
	@throws IOException if failed
	*/
	private boolean tryCommentTag()throws IOException
	{
		//recognize comment.
		char c=read();
		if (c!='!') { unread(c); return false; };
		c = read();
		if (c!='-')  { unread(c); unread('!');return false; };
		c = read();
		if (c!='-')  { unread(c);unread('-');unread('!');  return false; };
		//Now skip comment, testing maximum_idle_characters limit.
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
			if (skipped_idle_character==maximum_idle_characters)
					throw new EFormatBoundaryExceeded("XML comment too long, possible Denial Of Service attack?");			
			skipped_idle_character++;
		}
	};
	/** Invoked after &gt; was read from stream to check,
	if it is a processing instruction  tag and eventually skip it.
	@return true if processed it, false if it was not a comment.
			If false stream cursor is not moved.
	@throws IOException if failed
	*/
	private boolean tryProcessingTag()throws IOException
	{
		//recognize processing command.
		char c=read();
		if (c!='?') { unread(c); return false; };
		//Now skip processing command, testing maximum_idle_characters limit.
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
			if (skipped_idle_character==maximum_idle_characters)
					throw new EFormatBoundaryExceeded("XML processig block too long, possible Denial Of Service attack?");			
			skipped_idle_character++;
		}
	};
	
	/** This method is running a closing "root" element handling as
	a counterpart for {@link #validateReadable}. It should be called
	in every place where primitive element reached &lt;
	<p>
	Validates if it is not closing root tag.
	@throws ENoMoreData if it was non-root tag or
	@throws EUnexpectedEof it is was a closing root tag.
	*/
	private void checkRootOnNoMoreData()throws IOException
	{
		if (settings.ROOT_ELEMENT==null) throw new ENoMoreData();
		
		//Root defined, must be checked.
		char c = read();
		assert(c=='<');	//this is pre-condition
		char c = read();
		if (c!='/')
		{
			//not closing so not a root.
			unread(c); unread('<'); throw new ENoMoreData();
		};
		String root = settings.ROOT_ELEMENT;
		//now it may be a root tag?
		for(int i=0, n = root.length(); i<n; i++)
		{
			char c = read();
			if (c!=root.charAt(i))
			{
				//not a root.
				unread(c);
				while(i>0)
				{
					unread(root.charAt(i--));
				};
				unread('/');
				unread('<');
				throw new ENoMoreData();
			};
		};
		c = read();
		if ((c=='>')||(Character.isWhitespace(c)))
		{
			//we have root tag.
			state = STATE_ROOT_CLOSED;
			throw new EUnexpectedEof("</"+root+"> reached"); 
		}else
		{
			//have to un-read.
			unread(c);
			unread(root);
			unread('/');
			unread('<');
			throw new ENoMoreData();
		}
	};
	
	/** Skips skippable white-spaces which may be present in front of
	of primitive elements or between them and eventuall comments.
	@throws IOException if failed at low level
	@throws EFormatBoundaryExceeded if there were too many spaces.
	*/
	private void skipWhitespacesAndComments()throws IOException,EFormatBoundaryExceeded
	{
		System.out.println("skipWhitespacesAndComments ENTER");
		int ci;
		char c;
		for(;;)
		{
			do{
				//remember about limiting.
				if (skipped_idle_character==maximum_idle_characters)
					throw new EFormatBoundaryExceeded("Too many whitespaces to skip possible Denial Of Service attack?");
				ci = tryRead();
				if (ci==-1) return;
				c = (char)ci;
				skipped_idle_character++;									
			 }while(Character.isWhitespace(c));
			 if (c=='<')
			 {
			 	//need to skip comments or processing tags, accumulating
			 	//count of skipped elements to avoid DoS attack by 
			 	//interlaved comments.
			 	if (tryCommentTag()) { System.out.println("skipWhitespacesAndComments had comment");continue;}
			 	if (tryProcessingTag()) {System.out.println("skipWhitespacesAndComments had processing");continue;};
			 }
			 unread(c);
			 System.out.println("skipWhitespacesAndComments LEAVE, due to "+c);
			 return;
		 }
	};
	
	/* -------------------------------------------------		
				Elementary primitives
	-------------------------------------------------*/
	/** Fetches numeric primitive to {@link #token_buffer}. After return from this method
	cursor is at first character after a numeric value which is either whitespace or &gt;.
	@param allowedCharacters list of allowed characters. White spaces are NOT allowed in this set.
	@param max_length maximum length of primitive token in characters. If there is 
		more characters from <code>allowedCharacters</code> it throws. 
	@return {@link #token_buffer}. This buffer has zero size if there was no non-white space characters
		after initial white-spaces (skipped) and &gt; was reached.
	@throws IOException if failed
	*/
	protected CBoundAppendable fetchNumericPrimitive(String allowedCharacters, int max_length)throws IOException
	{
		assert(token_buffer.capacity()>=max_length);
		System.out.println("fetchNumericPrimitive ENTER");
		skipWhitespacesAndComments();	//skip leading white-spaces
		token_buffer.reset();
		final char separator = settings.PRIMITIVE_SEPARATOR;
		loop:
		for(;;)
		{
			char c = read();			
			if (c==separator) break loop;	//this is consumable.
			if ((c=='<') || Character.isWhitespace(c))
			{	//this is not consumable terminator.
				unread(c);
				break loop;
			};
			//check if in allowed set?
			if (allowedCharacters.indexOf(c)==-1)
				throw new EBrokenFormat("Unallowed \""+c+"\" in primitive, allowed set is "+allowedCharacters);
			//check if allowed length?
			if (token_buffer.length()>=max_length)
					throw new EBrokenFormat("Too long primitive \""+token_buffer+"\"");
			//ok, accumulate.
			token_buffer.append(c);
		};
		skipped_idle_character =0;	//because we break empty sequence.
		System.out.println("fetchNumericPrimitive =\""+token_buffer+"\" LEAVE");
		return token_buffer;
	};
	@Override public boolean readBoolean()throws IOException
	{
		validateReadable();
		CBoundAppendable b = fetchNumericPrimitive("tTfF01",1);
		if (b.length()==0) throw new ENoMoreData();
		switch(b.charAt(0))
		{
			case 't':
			case 'T':			
			case '1':	return true;
			
			case 'f':
			case 'F':			
			case '0':	return false;
			default: throw new AssertionError();
		}
	}; 
	@Override public byte readByte()throws IOException
	{
		validateReadable();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789",1);
		if (b.length()==0) throw new ENoMoreData();
		String s = b.toString();
		try{
			return Byte.parseByte(s);
		}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as byte"); }
	};
	@Override public char readChar()throws IOException
	{
		validateReadable();
		if (peek()=='<') throw new ENoMoreData();
		int ci = readEscaped();
		char c =(char)( (ci<0) ? (-ci-1) : ci );
		//now poll for next character		
		char next = read();
		if (next==settings.PRIMITIVE_SEPARATOR) return c;
		if ((next=='<')||Character.isWhitespace(next)){ unread(next); return c; }
		throw new EBrokenFormat("Unexpected \""+next+"\" after character primitive");
	};
	@Override public short readShort()throws IOException
	{
		validateReadable();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789",6);
		if (b.length()==0) throw new ENoMoreData();
		String s = b.toString();
		try{
			return Short.parseShort(s);
		}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as short"); }
	};
	@Override public int readInt()throws IOException
	{
		validateReadable();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789",12);
		if (b.length()==0) throw new ENoMoreData();
		String s = b.toString();
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as int"); }
	};
	@Override public long readLong()throws IOException
	{
		validateReadable();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789",21);
		if (b.length()==0) throw new ENoMoreData();
		String s = b.toString();
		try{
			return Long.parseLong(s);
		}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as long"); }
	};
	@Override public float readFloat()throws IOException
	{
		validateReadable();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789.eE",40);
		if (b.length()==0) throw new ENoMoreData();
		String s = b.toString();
		try{
			return Float.parseFloat(s);
		}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as float"); }
	};
	@Override public double readDouble()throws IOException
	{
		validateReadable();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789.eE",40);
		if (b.length()==0) throw new ENoMoreData();
		String s = b.toString();
		try{
			return Double.parseDouble(s);
		}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as double"); }
	};
	/* -------------------------------------------------		
				block primitives
	-------------------------------------------------*/
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		validateReadable();
		//Blocks do allow skipping white-spaces in a body
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			char c = read();
			if (Character.isWhitespace(c))
			{
				 skipped_idle_character++;
				 if (skipped_idle_character==maximum_idle_characters)
				 	throw new EFormatBoundaryExceeded("Too many skippable white spaces, possible Denial Of Service attack?"); 
				 continue;
			}else
			{
				skipped_idle_character=0;
			}
			boolean v;
			switch(c)
			{
				case 't':
				case 'T':			
				case '1': v=true; break;
				
				case 'f':
				case 'F':			
				case '0': v=false; break;
				case '<': 
						if (tryCommentTag()) continue loop;
			 			if (tryProcessingTag()) continue loop; 
						unread(c); break loop;
				default: throw new ECorruptedFormat("Invalid character \""+c+"\" in boolean block");
			}
			buffer[offset++]=v;
			length--;
			read++;
		}
		return read;
	};
	
	@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		validateReadable();
		//Blocks do allow skipping white-spaces in a body, but only at values boundary.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			char c = read();
			if (Character.isWhitespace(c))
			{
				 skipped_idle_character++;
				 if (skipped_idle_character==maximum_idle_characters)
				 	throw new EFormatBoundaryExceeded("Too many skippable white spaces, possible Denial Of Service attack?"); 
				 continue loop;
			}else
			{
				skipped_idle_character=0;
			}
			if (c=='<')
			{ 
				if (tryCommentTag()) continue loop;
			 	if (tryProcessingTag()) continue loop;
				unread(c); 
				break loop; 
			};
			int digit_1 = HEX2D(c);
			if (digit_1==-1) throw new ECorruptedFormat("Invalid character \""+c+"\" in byte block");
			c = read();	//note: no inside byte termination allowed.
			int digit_0 = HEX2D(c);
			if (digit_0==-1) throw new ECorruptedFormat("Invalid character \""+c+"\" in byte block");
			
			buffer[offset++]= (byte)((digit_1<<4) | (digit_0));
			length--;
			read++;
		}
		return read;
	};
	
	@Override public int readByteBlock()throws IOException
	{
		validateReadable();
		//Blocks do allow skipping white-spaces in a body, but only at values boundary.
		int read = 0;		//how many data were read.
		loop:
		for(;;)
		{
			char c = read();
			if (Character.isWhitespace(c))
			{
				 skipped_idle_character++;
				 if (skipped_idle_character==maximum_idle_characters)
				 	throw new EFormatBoundaryExceeded("Too many skippable white spaces, possible Denial Of Service attack?"); 
				 continue loop;
			}else
			{
				skipped_idle_character=0;
			}
			if (c=='<')
			{ 
				if (tryCommentTag()) continue loop;
			 	if (tryProcessingTag()) continue loop;
				unread(c); 
				return -1; 
			};
			int digit_1 = HEX2D(c);
			if (digit_1==-1) throw new ECorruptedFormat("Invalid character \""+c+"\" in byte block");
			c = read();	//note: no inside byte termination allowed.
			int digit_0 = HEX2D(c);
			if (digit_0==-1) throw new ECorruptedFormat("Invalid character \""+c+"\" in byte block");			
			return ((digit_1<<4) | (digit_0));
		}
	};
	
	@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		validateReadable();
		//Character block has a bit special white-space treatment.
		//that is they do replace any sequence of white spaces with single
		//d32 space.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			int ci = readEscaped();
			char c;
			//now process un-escaped data
			if (ci>=0)
			{
				c =(char)ci;
				if (Character.isWhitespace(c))
				{
					 if (skipped_idle_character++==1)
					 {
						//This is a first whie space. We process it
						//normally
						c = ' ';
					 }else
					 {
					 if (skipped_idle_character==maximum_idle_characters)
						throw new EFormatBoundaryExceeded("Too many skippable white spaces, possible Denial Of Service attack?"); 
					 continue loop;
					 };
				}else
				{
					skipped_idle_character = 0;
				};
				if (c=='<')
				{ 
					if (tryCommentTag()) continue loop;
					if (tryProcessingTag()) continue loop;
					unread(c); 
					break loop; 
				};
			}else
			{
				//escaped
				c = (char)(-ci -1);
				skipped_idle_character=0;
			};
			buffer[offset++]= c;
			length--;
			read++;
		}
		return read;
	};
	
	@Override public int readCharBlock(Appendable buffer, int length)throws IOException
	{
		validateReadable();
		//Character block has a bit special white-space treatment.
		//that is they do replace any sequence of white spaces with single
		//d32 space.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			int ci = readEscaped();
			char c;
			//now process un-escaped data
			if (ci>=0)
			{
				c =(char)ci;
				if (Character.isWhitespace(c))
				{
					 if (skipped_idle_character++==1)
					 {
						//This is a first whie space. We process it
						//normally
						c = ' ';
					 }else
					 {
					 if (skipped_idle_character==maximum_idle_characters)
						throw new EFormatBoundaryExceeded("Too many skippable white spaces, possible Denial Of Service attack?"); 
					 continue loop;
					 };
				}else
				{
					skipped_idle_character = 0;
				};
				if (c=='<')
				{ 
					if (tryCommentTag()) continue loop;
					if (tryProcessingTag()) continue loop;
					unread(c); 
					break loop; 
				};
			}else
			{
				//escaped
				c = (char)(-ci -1);
				skipped_idle_character=0;
			};
			buffer.append(c);
			length--;
			read++;
		}
		return read;
	};
	
	
	@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		validateReadable();
		//Blocks do allow skipping white-spaces in a body, but only at values boundary.
		//The short block is a numeric block, which is just a sequence of numeric
		//primitives. However we can't use plain numeric primitive fetches
		//because they fail if there is no data at all.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789",6);
			if (b.length()==0) break loop;	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Short.parseShort(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as short"); }
		}
		return read;
	};
	
	@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		validateReadable();
		//Blocks do allow skipping white-spaces in a body, but only at values boundary.
		//The int block is a numeric block, which is just a sequence of numeric
		//primitives. However we can't use plain numeric primitive fetches
		//because they fail if there is no data at all.
		int skipped = 0;	//counting skipped white spaces.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789",12);
			if (b.length()==0) break loop;	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Integer.parseInt(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as int"); }
		}
		return read;
	};
	
	@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		validateReadable();
		//Blocks do allow skipping white-spaces in a body, but only at values boundary.
		//The long block is a numeric block, which is just a sequence of numeric
		//primitives. However we can't use plain numeric primitive fetches
		//because they fail if there is no data at all.
		int skipped = 0;	//counting skipped white spaces.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789",21);
			if (b.length()==0) break loop;	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Long.parseLong(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as long"); }
		}
		return read;
	};
	
	@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		validateReadable();
		//Blocks do allow skipping white-spaces in a body, but only at values boundary.
		//The float block is a numeric block, which is just a sequence of numeric
		//primitives. However we can't use plain numeric primitive fetches
		//because they fail if there is no data at all.
		int skipped = 0;	//counting skipped white spaces.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789.eE",40);
			if (b.length()==0) break loop;	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Float.parseFloat(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as float"); }
		}
		return read;
	};
	
	@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		validateReadable();
		//Blocks do allow skipping white-spaces in a body, but only at values boundary.
		//The double block is a numeric block, which is just a sequence of numeric
		//primitives. However we can't use plain numeric primitive fetches
		//because they fail if there is no data at all.
		int skipped = 0;	//counting skipped white spaces.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789.eE",40);
			if (b.length()==0) break loop;	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Double.parseDouble(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EDataMissmatch("Could not decode \""+s+"\" as double"); }
		}
		return read;
	};
	
	
	/* ***********************************************************************
	
	
			Closeable
		
	
	************************************************************************/
	/** Sets closed status to true.
	If it was false calls {@link #closeOnce}
	@see #validateNotClosed
	*/
	@Override public void close()throws IOException
	{
		try{
			if (state!=STATE_CLOSED)
								closeOnce();
		}finally { state = STATE_CLOSED;}
	};
	
};