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
		<li>do allow bare format, without root element. This class
		treats it as a regular event;</li>
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
				
	/** Creates 
	@param input reader from which to read content.
	@param settings XML settings to use.
		If those settings carry non-null value in 
		{@link CXMLSettings#ROOT_ELEMENT} then
		this class will ensure to check if root element
		is opened before every operation and will
		start returning EOF/UnexpectedEof if root
		element is closed.	
	*/
	protected AXMLIndicatorReadFormat(
					final Reader input,
					final CXMLSettings settings
					)
	{
		super(input,settings);
		this.max_signal_name_length=1024; //contract default size.
		//now compute maximum necessary for token buffer.
		//This is the signal name length, 37 characters for floating points
		//or max element name length
		this.token_buffer = new CBoundAppendable(Math.max(1024,settings.getMaximumTokenLength()));
		//pre-allocate some stack
		this.xml_elements_stack = new ArrayList<String>(32);//same as write format, just for symetry.		
	};
	
	/* *************************************************************************
	
	
				IIndicatorReadFormat
	
	
	* *************************************************************************/
	/* -------------------------------------------------		
				Information and settings.		
	-------------------------------------------------*/
	/** Always zero, this format does not support registrations */
	@Override public final int getMaxRegistrations(){ return 0; };
	/** Returns {@link #isDescribed} because it is always flushing */
	@Override public final boolean isFlushing(){ return isDescribed(); };
	
	@Override public void setMaxSignalNameLength(int characters)
	{
		assert(characters>0):"characters="+characters;
		//Note: Contract allows unpredictable results when
		//setting it during work, so we don't have to preserve
		//previously buffered data.	
		assert(token_buffer.isEmpty()):"Can't set name length when processing is in progress.";
		//Limit must be set two fold: by adjusting capactity of name buffer (upwards)
		//and by setting hard limit for testing.
		this.max_signal_name_length = characters;
		//no re-allocate other buffers if necessary
		token_buffer = new CBoundAppendable(Math.max(characters,settings.getMaximumTokenLength()));
	};
	@Override public int getMaxSignalNameLength(){ return max_signal_name_length;}
	/** Set to 1024*1024 characters */
	@Override public int getMaxSupportedSignalNameLength(){ return 1024*1024; }
	/* -------------------------------------------------		
				Indicators	
	-------------------------------------------------*/
	private void invalidateGetIndicator(){ indicator_cache=null; };
	
	public TIndicator getIndicator()throws IOException
	{				
				
		if (indicator_cache!=null) return indicator_cache;		
		int ci = input.read();
		if (ci==-1)return TIndicator.EOF;
		char c = (char)ci;
		switch(c)
		{
			case '<': 
					indicator_cache= processTag();
					return indicator_cache;
			case '>': 
					input.unread(c);
					throw new EBrokenFormat("Unexpected >");
			default : 						
					//Since all skippable whitespaces
					//are get rid off by "input" this is for sure data.
					input.unread(c);
					indicator_cache=TIndicator.DATA;
					return TIndicator.DATA;
		}
	};
	
	@Override public void next()throws IOException
	{		
		//Now in our processing style if a logic cursor is
		//at non-data indicator, then physical cursor is
		//at first character after it. So this is enough to 
		//clear cache.
		if (getIndicator()!=TIndicator.DATA)
		{
			indicator_cache = null;
		}else
		{
			indicator_cache = null;
			//Now if we are inside data we should be able scan to next
			// < regardles of what is there.
			for(;;)
			{
				int c = input.read();
				if (c==-1) throw new EUnexpectedEof();
				if (c=='<')
				{ 
					input.unread((char)c);
					return;
				};
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
	@return indicator corresponding to processed tag, never null.
	@throws IOException if failed to recognize tag.
	*/
	private TIndicator processTag()throws IOException
	{
		//Split to begin and end tags.		
		char c = input.readChar();
		if (c=='/')
			return processClosingTag();
		else
		{
			input.unread(c);						
			return processOpeningTag();
		}
	};
	/** Helper for {@link #processOpeningTag}/{@link #processClosingTag}
	checking if there are no (more) attributes
	@throws EBrokenFormat if there are attributes */
	private void validateNoMoreAttributes()throws IOException
	{
		if (input.read()!='>')
			throw new EBrokenFormat("element does not take attributes"); 
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
		//The opening tag may be one of indicator tags, long encoded name,
		//or short encoded name (Note: root element, if happens will fall into this category)
		//Just compare them using quick compare routines.		
		if (b.equalsString(settings.EVENT))
		{
			//long form do require attribute carying singnal name
			if (input.peek()=='>') throw new EBrokenFormat("<"+settings.EVENT+"> requires "+settings.SIGNAL_NAME_ATTR+" attribute");
			b = readAttributeName();
			if (!b.equalsString(settings.SIGNAL_NAME_ATTR))
				throw new EBrokenFormat("<"+settings.EVENT+"> requires "+settings.SIGNAL_NAME_ATTR+" attribute while \""+b+"\" was found");
			b = readAttributeValue();
			validateNoMoreAttributes();
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
		{
			validateNoMoreAttributes();			
			if (b.equalsString(settings.BOOLEAN_ELEMENT))
			{
				xml_elements_stack.add(settings.BOOLEAN_ELEMENT);
				return TIndicator.TYPE_BOOLEAN;
			}else
			if (b.equalsString(settings.BYTE_ELEMENT))
			{
				xml_elements_stack.add(settings.BYTE_ELEMENT);
				return TIndicator.TYPE_BYTE;
			}else
			if (b.equalsString(settings.CHAR_ELEMENT))
			{
				xml_elements_stack.add(settings.CHAR_ELEMENT);
				return TIndicator.TYPE_CHAR;
			}else
			if (b.equalsString(settings.SHORT_ELEMENT))
			{
				xml_elements_stack.add(settings.SHORT_ELEMENT);
				return TIndicator.TYPE_SHORT;
			}else
			if (b.equalsString(settings.INT_ELEMENT))
			{
				xml_elements_stack.add(settings.INT_ELEMENT);
				return TIndicator.TYPE_INT;
			}else
			if (b.equalsString(settings.LONG_ELEMENT))
			{
				xml_elements_stack.add(settings.LONG_ELEMENT);
				return TIndicator.TYPE_LONG;
			}else
			if (b.equalsString(settings.FLOAT_ELEMENT))
			{
				xml_elements_stack.add(settings.FLOAT_ELEMENT);
				return TIndicator.TYPE_FLOAT;
			}else
			if (b.equalsString(settings.DOUBLE_ELEMENT))
			{
				xml_elements_stack.add(settings.DOUBLE_ELEMENT);
				return TIndicator.TYPE_DOUBLE;
			}else
			if (b.equalsString(settings.BOOLEAN_BLOCK_ELEMENT))
			{
				xml_elements_stack.add(settings.BOOLEAN_BLOCK_ELEMENT);
				return TIndicator.TYPE_BOOLEAN_BLOCK;
			}else
			if (b.equalsString(settings.BYTE_BLOCK_ELEMENT))
			{
				xml_elements_stack.add(settings.BYTE_BLOCK_ELEMENT);
				return TIndicator.TYPE_BYTE_BLOCK;
			}else
			if (b.equalsString(settings.CHAR_BLOCK_ELEMENT))
			{
				xml_elements_stack.add(settings.CHAR_BLOCK_ELEMENT);
				return TIndicator.TYPE_CHAR_BLOCK;
			}else
			if (b.equalsString(settings.SHORT_BLOCK_ELEMENT))
			{
				xml_elements_stack.add(settings.SHORT_BLOCK_ELEMENT);
				return TIndicator.TYPE_SHORT_BLOCK;
			}else
			if (b.equalsString(settings.INT_BLOCK_ELEMENT))
			{
				xml_elements_stack.add(settings.INT_BLOCK_ELEMENT);
				return TIndicator.TYPE_INT_BLOCK;
			}else
			if (b.equalsString(settings.LONG_BLOCK_ELEMENT))
			{
				xml_elements_stack.add(settings.LONG_BLOCK_ELEMENT);
				return TIndicator.TYPE_LONG_BLOCK;
			}else
			if (b.equalsString(settings.FLOAT_BLOCK_ELEMENT))
			{
				xml_elements_stack.add(settings.FLOAT_BLOCK_ELEMENT);
				return TIndicator.TYPE_FLOAT_BLOCK;
			}else
			if (b.equalsString(settings.DOUBLE_BLOCK_ELEMENT))
			{
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
		}
	};
	/** Buffers remaning content of a tag in {@link #token_buffer}.
	This method is called when cursor is at first character after <code>&lt;</code>
	or <code>&lt;/</code>.
	 <p>
	Once this method return the stream cursor is at the first non-whitespace
	character after the tag which may be &gt; or a first character
	of attribute name.
	@return {@link #token_buffer} filled with tag
	@throws IOException if failed
	*/ 
	private CBoundAppendable readTag()throws IOException
	{
		//Note: input normalizes spaces to single ' '
		token_buffer.reset();
		char c= input.readChar();
		if (c=='>') 
		{	
			//this is anonymous tag. We allow it.
			input.unread(c);
			return token_buffer;
		} 
		if (!isValidStartingTagChar(c)) throw new EBrokenFormat("\""+c+"\" is not a valid first character in XML tag");
		token_buffer.append(c);
		for(;;)
		{			
			c = input.readChar();
			if (isValidTagChar(c))
				token_buffer.append(c);	//this will be somewhat limited against DoS attack.
			else
			{	
				//Valid elements are ' ' before attribute name or '>'
				if ((c!=' ')&&(c!='>')) throw new EBrokenFormat("\""+c+"\" is not valid tag terminator");
				if (c!=' ') input.unread(c);	//we need to leave > unprocessed.	
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
		//Note: input normalizes spaces to single ' '
		//We assume that attribute has the same charset as tag.
		token_buffer.reset();
		char c= input.readChar();
		if (!isValidStartingTagChar(c)) throw new EBrokenFormat("\""+c+"\" is not a valid first character in attribute name");
		token_buffer.append(c);
		for(;;)
		{
			c = input.readChar();
			if (isValidTagChar(c))
				token_buffer.append(c);	//this will be somewhat limited against DoS attack.
			else
			{
				//Valid elements are ' ' before = or '='
				if (c==' '){ c = input.readChar(); };				
				if (c!='=') throw new EBrokenFormat("Expected = but found \""+c+"\" ");
				//Now after = we expecte either ' ' or "
				c = input.readChar();
				if (c==' '){ c= input.readChar(); };
				if (c!='\"') throw new EBrokenFormat("Expected \" but found \""+c+"\" ");
				input.unread(c);	//this must be not consumed.
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
		//Note: input normalizes spaces to single ' '
		token_buffer.reset();
		char c= input.readChar();
		assert(c=='\"');		
		for(;;)
		{
			int ci = input.readBodyChar();			
			if (ci>=0)
			{
				c = (char)ci;
				//unescaped should be subject of detection of \"
				if (c=='\"')
				{
					//end of attribute. Input layer does normalize spaces
					//so it is > or, eventually, there can be a space if 
					//second attribute would follow.
					c = input.readChar();
					if ((c!=' ')&&(c!='>')) throw new EBrokenFormat("\""+c+"\" is not valid character after attribute value");				
					if (c!=' ')input.unread(c);	//leave > unconsumed.	
					return token_buffer;
				}
			}else
			{
				c = (char)(-ci -1);
			};
			token_buffer.append(c);
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
		if (xml_elements_stack.isEmpty())
		{
			//In this place </root> element is allowed.
			if (settings.ROOT_ELEMENT==null)
			{
				throw new EBrokenFormat("too many closing tags, \""+b+"\"");
			}
			else
			{
				if (b.equalsString(	settings.ROOT_ELEMENT ))
				{
					//Unread it completely, so that we will be stuck at it.
					//and will be unable to move forwards.
					input.unread(b);
					input.unread("</");
					return TIndicator.EOF;
				}else 
				throw new EBrokenFormat("too many closing tags, \""+b+"\"");
			}
		};		
		//validate, allowing anonymous closing tag
		final String closed =  xml_elements_stack.remove(xml_elements_stack.size()-1);		
		if (!b.isEmpty())
		{
			if (!b.equalsString(closed)) throw new EBrokenFormat("Closing tag does not match, expected \""+closed+"\" found \""+b+"\"");	
		};
		validateNoMoreAttributes();
		//act accordingly.
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
	
	
	/* -------------------------------------------------		
				Elementary primitives
	-------------------------------------------------*/
	private void skipWhitespaces()throws IOException
	{
		///Note: space normalization is handled by input, but
		//in un-described streams inner spaces may still be present
		//in a form of a single ' ', so we have to skip them.
		//We loop, just in case, even tough a single ' ' should be
		//just enough to skip. 
		//Notice, skipping may reach eof, and must be eof proof
		//because primitive may end the file and we skip spaces
		//after primitives to make sure getIndicator can peek
		//the < character.
		for(;;)
		{
			 int r= input.read();
			 if (r==-1) break;
			 char c = (char)r;
			 if (c!=' ')
			 {
			 	input.unread(c);
			 	break;
			 };	 
		}
	};
	/** Is invoked at the begininng of each elementary fetch.
	Validates if operation is allowed and clears indicators cache.
	@throws IOException if failed */
	private void startElementaryPrimitive()throws IOException
	{
		invalidateGetIndicator();				
	};
	/** Method should be invoked if cursor is at &lt; which
	caused either {@link ENoMoreData} or partial read.
	The found tag may indicate either signal or root closing tag.
	If it is not a root closing tag, then the partial read or 
	throwing {@link ENoMoreData} are correct.
	<p>
	If it is a root closing tag then throwing {@link EUnexpectedEof}
	is a correct behavior.
	<p>
	This method reads tag and tests if it is root.
	The read tag is always, regardless of result, unread back to
	input and cursor is again at &lt; 
	
	@return true if root element closing tag is found.
	@throws IOException if failed to check it.
	*/
	private boolean isClosingRootElement()throws IOException
	{
		if (settings.ROOT_ELEMENT==null)  return false;
		//Not we have to test for root element
		char c = input.readChar();
		if (c!='<'){ input.unread(c); return false; }
		c = input.readChar();
		if (c!='/'){ input.unread(c); input.unread('<'); return false; };
		//now it can be root tag.
		CBoundAppendable b = readTag();
		//always un-read it.
		input.unread(b);
		input.unread("</");
		if (b.equalsString(settings.ROOT_ELEMENT))
		{
			return true;
		}else
			return false;
	};
	/** This method should be invoked in any place in which {@link ENoMoreData}
	would be normally thrown. The condition for {@link ENoMoreData} is when
	XML tag is reached during data processing. This tag may indicate either
	signal or root closing tag. If it is not a root element closing that,
	then {@link ENoMoreData} is a valid exception. If it is a root closing
	element then the {@link EUnexpectedEof} is a valid condition to throw. 
	@return what to throw.
	@throws IOException if failed to validate it.
	*/
	private IOException throwENoMoreData()throws IOException
	{		
		return isClosingRootElement() ? new EUnexpectedEof() : new ENoMoreData();
	};
	/** Fetches numeric primitive to {@link #token_buffer}. After return from this method
	cursor is at next non-white-space character after fetched numeric value.
	@param allowedCharacters list of allowed characters. White spaces are NOT allowed in this set.
	@param max_length maximum length of primitive token in characters. If there is 
		more characters from <code>allowedCharacters</code> it throws. 
	@return {@link #token_buffer}. This buffer has zero size if there was no characters
		which should not be skipped before a separator or XML tag is reached.
	@throws IOException if failed
	*/
	private CBoundAppendable fetchNumericPrimitive(String allowedCharacters, int max_length)throws IOException
	{
		//Note: space normalization is handled by input, but
		//in un-described streams inner spaces may still be present
		//in a form of a single ' '.
		assert(token_buffer.capacity()>=max_length);
		skipWhitespaces();		
		token_buffer.reset();
		final char separator = settings.PRIMITIVE_SEPARATOR;
		loop:
		for(;;)
		{
			char c = input.readChar();			
			if ((c==separator)||(c==' ')) break loop;	//this is consumable terminator
			if (c=='<')
			{	//this is non consumable terminator.
				input.unread(c);
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
		skipWhitespaces();
		return token_buffer;
	};
	@Override public boolean readBoolean()throws IOException
	{
		startElementaryPrimitive();
		CBoundAppendable b = fetchNumericPrimitive("tTfF01",1);
		if (b.length()==0) throw throwENoMoreData();
		
		switch(b.charAt(0))
		{
			case 't':
			case 'T':			
			case '1':	return true;
			
			case 'f':
			case 'F':			
			case '0':	return false;
			default: throw new AssertionError();	//fetchNumeric should defend against it.
		}
	}; 
	@Override public byte readByte()throws IOException
	{
		startElementaryPrimitive();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789",4);
		if (b.length()==0) throw throwENoMoreData();
		String s = b.toString();
		try{
			return Byte.parseByte(s);
		}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as byte"); }
	};
	@Override public char readChar()throws IOException
	{
		startElementaryPrimitive();
		int ci = input.readBodyChar();
		char c;
		//now process un-escaped data
		if (ci>=0)
		{
			c =(char)ci;
			if (c=='<')	 
			{ 
				//Considering this method as a stand-alone this _may_ happen
				//if user supplied stream like <c> </c> and the whitespace was optimized
				//to nothing.
				//If however API is used correctly then the getIndicator() must be called
				//prior to this read and it must return DATA. In case of optimized out
				//single space it will return some other indicator.
				//
				//Also if user supplied <e>abcfer  </e> it will be read as <e>abcfer</e>
				input.unread(c); 
				throw new AssertionError("This method reached XML tag. Did You forgot to poll the getIndicator()?");
			};
		}else
		{
			//escaped
			c = (char)(-ci -1);
		};
		//Next character may be primitive separator but does not have to be.		
		char next = input.readChar();
		if (next!=settings.PRIMITIVE_SEPARATOR)
			input.unread(next);
		return c;
	};
	@Override public short readShort()throws IOException
	{
		startElementaryPrimitive();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789",6);
		if (b.length()==0) throw throwENoMoreData();
		String s = b.toString();
		try{
			return Short.parseShort(s);
		}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as short"); }
	};
	@Override public int readInt()throws IOException
	{
		startElementaryPrimitive();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789",13);
		if (b.length()==0) throw throwENoMoreData();
		String s = b.toString();
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as int"); }
	};
	@Override public long readLong()throws IOException
	{
		startElementaryPrimitive();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789",22);
		if (b.length()==0) throw throwENoMoreData();
		String s = b.toString();
		try{
			return Long.parseLong(s);
		}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as long"); }
	};
	@Override public float readFloat()throws IOException
	{
		startElementaryPrimitive();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789.eE",41);
		if (b.length()==0) throw throwENoMoreData();
		String s = b.toString();
		try{
			return Float.parseFloat(s);
		}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as float"); }
	};
	@Override public double readDouble()throws IOException
	{
		startElementaryPrimitive();
		CBoundAppendable b = fetchNumericPrimitive("-0123456789.eE",41);
		if (b.length()==0) throw throwENoMoreData();
		String s = b.toString();
		try{
			return Double.parseDouble(s);
		}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as double"); }
	};
	/* -------------------------------------------------		
				block primitives
	-------------------------------------------------*/
	/** Is invoked at the begininng of each block fetch.
	Validates if operation is allowed and clears indicators cache.
	@throws IOException if failed */
	private void startBlockPrimitive()throws IOException
	{
		invalidateGetIndicator();				
	};
	/** Must be invoked on partial read.
	Will test if root closing element caused it and if it was,
	will throw.	
	@throws EUnexpectedEof if root closing caused eof.
	@throws IOException if failed during check.
	*/
	private void tryThrowOnPartialRead()throws EUnexpectedEof,IOException
	{
		if (isClosingRootElement()) throw new EUnexpectedEof();
	};
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		//Note: Even tough we have input to normalize spaces, single space may still appear.
		//	 	inside a block.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			char c = input.readChar();			
			boolean v;
			switch(c)
			{
				case 't':
				case 'T':			
				case '1': v=true; break;
				
				case 'f':
				case 'F':			
				case '0': v=false; break;
				case '<': input.unread(c);
					 	  tryThrowOnPartialRead();
						  break loop;
				case ' ': continue loop;
				default: throw new EBrokenFormat("Invalid character \""+c+"\" in boolean block");
			}
			buffer[offset++]=v;
			length--;
			read++;
		}
		return read;
	};
	
	@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		//Note: Even tough we have input to normalize spaces, single space may still appear.
		//	 	inside a block.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			char c = input.readChar();
			switch(c)
			{
				case '<': input.unread(c); tryThrowOnPartialRead(); break loop;
				case ' ': continue loop;
			};
			int digit_1 = HEX2D(c);
			if (digit_1==-1) throw new EBrokenFormat("Invalid character \""+c+"\" in byte block");
			c = input.readChar();	//note: no inside byte termination allowed.
			int digit_0 = HEX2D(c);
			if (digit_0==-1) throw new EBrokenFormat("Invalid character \""+c+"\" in byte block");
			
			buffer[offset++]= (byte)((digit_1<<4) | (digit_0));
			length--;
			read++;
		}
		return read;
	};
	
	@Override public int readByteBlock()throws IOException
	{
		startBlockPrimitive();
		//Note: Even tough we have input to normalize spaces, single space may still appear.
		//	 	inside a block.
		loop:
		for(;;)
		{
			char c = input.readChar();
			switch(c)
			{
				case '<': input.unread(c);//see notes in getChar.
						 throw new AssertionError("This method must be called when cursor is at DATA. Did You forget to validate it with getIndicator()?");
				case ' ': continue loop;
			};
			int digit_1 = HEX2D(c);
			if (digit_1==-1) throw new EBrokenFormat("Invalid character \""+c+"\" in byte inside a byte block");
			c = input.readChar();	//note: no inside byte termination allowed.
			int digit_0 = HEX2D(c);
			if (digit_0==-1) throw new EBrokenFormat("Invalid character \""+c+"\" in byte inside a byte block");		
			return ((digit_1<<4) | (digit_0));
		}
	};
	
	@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		//Note: Even tough we have input to normalize spaces, single space may still appear.
		//	 	inside a block, but in character block it is just a character.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			int ci = input.readBodyChar();
			char c;
			//now process un-escaped data
			if (ci>=0)
			{
				c =(char)ci;
				if (c=='<')
				{ 
					input.unread(c); 
					tryThrowOnPartialRead();
					break loop; 
				};
			}else
			{
				//escaped
				c = (char)(-ci -1);
			};
			buffer[offset++]= c;
			length--;
			read++;
		}
		return read;
	};
	
	@Override public int readCharBlock(Appendable buffer, int length)throws IOException
	{
		startBlockPrimitive();
		//Note: Even tough we have input to normalize spaces, single space may still appear.
		//	 	inside a block, but in character block it is just a character.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			int ci = input.readBodyChar();
			char c;
			//now process un-escaped data
			if (ci>=0)
			{
				c =(char)ci;
				if (c=='<')
				{ 
					input.unread(c); 
					tryThrowOnPartialRead();
					break loop; 
				};
			}else
			{
				//escaped
				c = (char)(-ci -1);
			};
			buffer.append(c);
			length--;
			read++;
		}
		return read;
	};
	
	
	@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		//Note: Even tough we have input to normalize spaces, single space may still appear.
		//	 	inside a block.
		//The short block is a numeric block, which is just a sequence of numeric
		//primitives. However we can't use plain numeric primitive fetches
		//because they fail if there is no data at all.
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789",6);
			if (b.length()==0){ tryThrowOnPartialRead(); break loop;}	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Short.parseShort(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as short"); }
		}
		return read;
	};
	
	@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789",12);
			if (b.length()==0){ tryThrowOnPartialRead(); break loop;}	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Integer.parseInt(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as int"); }
		}
		return read;
	};
	
	@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789",21);
			if (b.length()==0){ tryThrowOnPartialRead(); break loop;}	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Long.parseLong(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as long"); }
		}
		return read;
	};
	
	@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789.eE",40);
			if (b.length()==0){ tryThrowOnPartialRead(); break loop;}	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Float.parseFloat(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as float"); }
		}
		return read;
	};
	
	@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		int read = 0;		//how many data were read.
		loop:
		while(length>0)
		{
			CBoundAppendable b = fetchNumericPrimitive("-0123456789.eE",40);
			if (b.length()==0){ tryThrowOnPartialRead(); break loop;}	//<-- this is empty when < was reached.
			final String s = b.toString();
			try{
				buffer[offset++]= Double.parseDouble(s);
				length--;
				read++;
			}catch(NumberFormatException ex){ throw new EBrokenFormat("Could not decode \""+s+"\" as double"); }
		}
		return read;
	};
	
	
	
	
};