package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
	This is a reading counterpart for {@link AXMLSignalWriteFormat}
	
	<h1>XML processing notes</h1>
	Writing XML parser is not an easy thing. Writing a robust XML parser
	is even harder. Instead this class will put a pressure on two aspects:
	<ul>
		<li>be <i>lenient</i> on XML syntax;</li>
		<li>be robust on attempts to break things;</li>
	</ul>
	
	<h2>What features of XML are supported?</h2>
	Only comments, element, directly placed characters and a single XML attribute
	only when expected.
	
	<h2>What features of XML are not supported?</h2>
	XML escape coed &amp;#xxx; and dedicated ones, entities and schemas.
	And many more I am even not aware of.
	
	<h2>Spaces and EOL treatment</h2>
	All non-encoded EOL characters and spaces are ignored except when
	in a character block.
	<p>
	Inside a character block all characters are accepted.
	
	<h2>Described formats</h2>
	Described formats should:
	<ul>
		<li>override {@link #isDescribed} to return true;</li>
		<li>override {@link #processOpeningTag(String)} to recognize type 
		indicators and return <code>TYPE_xxx</code> constants;</li>
		<li>override {@link #processClosingTag(String)} to recognize closing type 
		indicators and return <code>FLUSH_xxx</code> constants;</li>
	</ul>
*/
public abstract class AXMLSignalReadFormat extends ASignalReadFormat
{
				/* ****************************************************
						XML elements					
				* ***************************************************/
				/** Character used to mark
				<a href="doc-files/xml-syntax.html#escaped">escape sequence</a>.*/				
				protected final char ESCAPE_CHAR;
				/** Character used to mark end of
				<a href="doc-files/xml-syntax.html#escaped">escape sequence</a>.*/
				protected final char ESCAPE_CHAR_END;
				/** A character used to separate primitive data in un-typed streams */
				protected final char PRIMITIVES_SEPARATOR_CHAR;
 				/** An XML element used to represent
 				a <a href="doc-files/xml-syntax.html#long_signal_form">a long form</a>
 				of a begin signal */
				protected final String LONG_SIGNAL_ELEMENT;
				/** An attribute of {@link #LONG_SIGNAL_ELEMENT} which will carry
				encoded signal name.*/
				protected final String LONG_SIGNAL_ELEMENT_ATTR;
				/* ****************************************************
						Character buffering.
				* ***************************************************/
				/** The un-read buffer.
				Each un-read character is put at the end
				of a buffer, and when {@link #read} or {@link #eol}
				is invoked the last character in buffer is returned.
				<p>
				This un-read buffer is used to handle processing
				of elements, data and etc, and it is fixed in size.
				<p>
				The size is computed in constructor from maximum
				known length of name and known XML strings.
				@see #unread_at
				*/
				private final char [] unread;
				private int unread_at;
				/** Used to buffer data for XML elements and primitives
				to process.	The upper bound of this appendable is set to 
				the max of known XML elements and max name length
				*/
				private final CBoundAppendable xml_element_buffer;
				/** As specified in constructor. */
				private final int max_name_length;
				/** A maximum size of XML element.
				If 0 there is no boundary. The positive value sets
				how many characters can be read from stream between
				calls to {@link #nextIndicator} before
				{@link EFormatBoundaryExceeded} is thrown
				@see #current_xml_element_size*/
				private final int max_inter_signal_chars;
				/* ****************************************************
						State
				* ***************************************************/
				/** Indidates that we are in space when whitespaces
				and eols are void */
				private static final byte STATE_NONE = 0;
				/** Indicates that we are inside a character block
				and no character is "void". */
				private static final byte STATE_CHARACTER_BLOCK = (byte)1;
				/** If needs to return {@link #DIRECT_INDICATOR} 
				@see #begin_name*/
				private static final byte STATE_DIRECT_INDICATOR = (byte)2;
				/** Processing state */
				private byte state;
				/** A stack of xml begin-end elements.
				Carries element actually sent to XML */
				private final ArrayList<String> signal_stack;
				/** A name of a begin signal to be returned from 
				{@link #readSignalNameData} */
				private String begin_name;
				/** Used by {@link #read}/{@link #unread} to control
				{@link #max_xml_element_size} */
				private int current_inter_signal_chars;
	
		/* ********************************************************************
		
					Construction
		
		********************************************************************/
		/** Creates write format
		
		@param max_name_length see {@link ASignalWriteFormat#ASignalWriteFormat(int,int,int)}
		@param max_events_recursion_depth --//--
		@param ESCAPE_CHAR see {@link #ESCAPE_CHAR}
		@param ESCAPE_CHAR_END see {@link #ESCAPE_CHAR_END}
		@param PRIMITIVES_SEPARATOR_CHAR see {@link #PRIMITIVES_SEPARATOR_CHAR}
		@param LONG_SIGNAL_ELEMENT see {@link #LONG_SIGNAL_ELEMENT}, non null
		@param LONG_SIGNAL_ELEMENT_ATTR see {@link #LONG_SIGNAL_ELEMENT_ATTR}, non null
		@param max_type_tag_length maximum length of other XML tags, that is type tags.
				This value will be used to pre-allocated some limit checking buffers
				(max of Double.toString(), above elements and attributes, above max name
				length and this value).
				There is a warranty that syntax elements up to this limit will not cause
				{@link EFormatBoundaryExceeded}, but there is no warranty that
				longer will throw.
		@param max_inter_signal_chars a maximum size of characters in stream 
				between calls to {@link #nextIndicator} before
				{@link EFormatBoundaryExceeded} is thrown. Zero disables this check.
				This is a defense against streams of infinite size filled with
				spaces or comments which has to be skipped. This value does not affect
				block reads.
		*/
		protected AXMLSignalReadFormat(
									 final int max_name_length,
									 final int max_events_recursion_depth,
									 final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
									 final char PRIMITIVES_SEPARATOR_CHAR,
									 final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR,
									 final int max_type_tag_length,
									 final int max_inter_signal_chars
									 )
		{
			super(0, max_name_length, max_events_recursion_depth);//no names registry!
			assert(LONG_SIGNAL_ELEMENT!=null);
			assert(LONG_SIGNAL_ELEMENT_ATTR!=null);
			assert(max_inter_signal_chars>=0);
			assert(max_type_tag_length>=0);
			this.max_name_length=max_name_length;
			this.ESCAPE_CHAR=ESCAPE_CHAR;
			this.ESCAPE_CHAR_END=ESCAPE_CHAR_END;
			this.LONG_SIGNAL_ELEMENT=LONG_SIGNAL_ELEMENT;
			this.LONG_SIGNAL_ELEMENT_ATTR=LONG_SIGNAL_ELEMENT_ATTR;
			this.PRIMITIVES_SEPARATOR_CHAR=PRIMITIVES_SEPARATOR_CHAR;
			this.signal_stack = new ArrayList<String>(max_events_recursion_depth);
			int max_length = Math.max(max_name_length,
								Math.max(LONG_SIGNAL_ELEMENT.length(),
									Math.max(
											LONG_SIGNAL_ELEMENT_ATTR.length(),
										Math.max(
												max_type_tag_length,
												32 //guess we need for Double.toString and etc.
												)
									)));
			this.max_inter_signal_chars=max_inter_signal_chars;
			this.unread = new char[max_length];
			this.xml_element_buffer = new CBoundAppendable(max_length);
		};
		
		/* ********************************************************
		
				Services required from subclasses		
		
		**********************************************************/
		/** Like {@link java.io.Reader#read}.
		<p>
		<i>Note: It is a shame there is no simple counterpart to Appendable
		which is a nice subset of java.io.Writer.
		The Readable looked like this, but the sole fact that it works only
		with CharBuffer is intimidating.</i>
		@return --//--
		@throws IOException if failed
		*/
		protected abstract int readImpl()throws IOException;
		
		/* ********************************************************
		
				Tunable services.		
		
		**********************************************************/
		/** Reverses {@link AXMLSignalWriteFormat#escape}.
		This method is called when {@link #ESCAPE_CHAR} was read
		from stream and an escape sequence is expected to follow.
		@return decoded character
		@throws IOException if failed.
		*/
		protected char unescape()throws IOException
		{
			char digit = read();
			//detect self-escape
			if (digit==ESCAPE_CHAR)
			{
				if (read()!=ESCAPE_CHAR_END)
					throw new ECorruptedFormat("Missing "+ESCAPE_CHAR_END+" in character escape sequence");
				return ESCAPE_CHAR;
			}else
			{
				//variable length hex escape.
				int unescaped = HEX(digit);
				int i=4;
				for(;;)
				{
					digit = read();
					if (digit==ESCAPE_CHAR_END) return (char)unescaped;
					else
					{
						if ((--i)==0) throw new ECorruptedFormat("Escape sequence too long");
						unescaped <<= 4;
						unescaped += HEX(digit);
					}
				}
			}
		};
		/** Invoked from {@link #next} via {@link processClosingTag()}
		once it checked, that closing tag does not match a signal tag
		or there was no signal.
		<p>
		Default implementation is for an un-described stream and throws
		{@link ECorruptedFormat}.
		@param tag tag which did not match.
		@return an indicator to return from {@link #next}
		@throws IOException if failed
		*/
		protected int processClosingTag(String tag)throws IOException
		{
			throw new ECorruptedFormat("Unexpected closing tag \""+tag+"\"");
		};
		/** Invoked from {@link #next} via {@link processOpeningTag()}
		once tag is fetched.
		<p>
		Default implementation is for un-described stream and returns {@link #BEGIN_INDICATOR}.
		@param tag tag which did not match.
		@return an indicator to return from {@link #next} or
				{@link BEGIN_INDICATOR} if it did not identify 
				a tag as a type tag.
		@throws IOException if failed
		*/
		protected int processOpeningTag(String tag)throws IOException
		{
			return BEGIN_INDICATOR;
		};
		
		
		
		
		/* ********************************************************
		
		
		
				Core services required by superclass
				
				
				
		
		**********************************************************/
		/*========================================================		
				Signals		
		=========================================================*/
		@Override protected int readIndicator()throws IOException
		{						
			resetInterSignalLimit();
			again:
			for(;;) //due to comments.
			{
				//Now we need to check what is under cursor.
				//If it is a tag marker we need to process it.
				//The trickier part is when it is NOT a tag.
				//We must correctly detect if we are inside a data
				//or not. In all data except of char blocks
				//whitespaces and eols are ignorable and we must chew them.
				//We also need to handle fake state indicating direct name return.
				switch(state)
				{
					case STATE_NONE:
						{
							if (isEof()) return EOF_INDICATOR;
							//we are in plain data block, when we should skip
							//white spaces.
							char c = read();
							while(Character.isWhitespace(c))//Note: This test covers EOL and page feeds too.
							{
								//skip it and go for next.
								if (isEof()) return EOF_INDICATOR;
								c = read();
							};
							//this is a non-space, put it back
							unread(c);
						};						
						break; 
					case STATE_CHARACTER_BLOCK:
						state = STATE_NONE;		// no more if it, we are skipping the rest of the content.
						continue again;
					case STATE_DIRECT_INDICATOR: 
						state = STATE_NONE;
						return DIRECT_INDICATOR; //a fake state, triggered by begin tag.
				};				
				
				//Now we either skipped spaces or should not skip them.
				//Anyway char under cursor describes next stream element.
				char c = read();
				if (c=='<') 
				{	
					//detected tag. Let us chew on it.
					//Now check what kind of tag it is?			
					c = read();
					switch(c)
					{
							case '/':  //end tag
									return processClosingTag();
							case '!':  //comment tag 
									skipCommentElement();
									continue again;
							case '?':	//processing tag
									skipProcessingCommand();
									continue again;
							default: //begin tag.
									unread(c); //unread first name character.
									return processOpeningTag();
					}
				}else
				{
					assert(!Character.isWhitespace(c)):"spaces should have been skipped";
					unread(c);	//put it back, we can't move cursor.
					return NO_INDICATOR;
				}
			}
		};
		@Override protected void skipData()throws IOException,EUnexpectedEof
		{
			resetInterSignalLimit();
			//A bit more complex due to faked state.
			switch(state)
			{
				case STATE_NONE:
					break; 
				case STATE_CHARACTER_BLOCK: 
					break;
				case STATE_DIRECT_INDICATOR: 
					return;			//this is a case when we need to ignore skipping because we
									//are at faked indicator.
			};				
			state = STATE_NONE;		//we skipped all
			again:
			for(;;)
			{
				char c = read();
				if (c=='<') 
				{	
					//detected tag, but it may be a comment or processing command.
					c = read();
					switch(c)
					{
							case '!':  //comment tag 
									skipCommentElement();
									continue again;
							case '?':	//processing tag
									skipProcessingCommand();
									continue again;
							default: //begin tag.
									unread(c); //unread first tag character.
									unread('<'); //unread tag, so next can detect it.
									return;
					}
				}
			}
		};
		/** Just skips comment element. 
		@throws IOException if failed. */
		private void skipCommentElement()throws IOException
		{
			//we just read till we encounter -->
			int state = 0;	// 0 - chars
							// 1 - first -
							// 2 - second -
							// 3 - found!
			for(;;)
			{
				char c = read();
				switch(state)
				{
						case 0: if (c=='-') state++; break;
						case 1: if (c=='-') state++; else state=0; break;
						case 2:
								switch(c)
								{
									case '>': return;
									case '-': break; //for --- or more.
									default:  state=0;
								};
				}
			}
		};
		/** Just skips XML processing command
		@throws IOException if failed. */
		private void skipProcessingCommand()throws IOException
		{
			//just read till we encounter ?>
			int state = 0;	// 0 - chars
							// 1 - ?
							// 2 - found!
			for(;;)
			{
				char c = read();
				switch(state)
				{
						case 0: if (c=='?') state++; break;
						case 1:
								switch(c)
								{
									case '>': return;
									case '?': break; //for ??> or more.
									default:  state=0;
								};
				};
			}
		};
		/** Machinery for fetching and processing the closing XML tag.
		@return indicator to return from the {@link #next} method.
		@throws IOException if failed */
		private int processClosingTag()throws IOException
		{
			//End tag consists just with a name. We fill up the buffer 
			//with it
			xml_element_buffer.reset();
			for(;;)
			{
				char c=read();
				if ((c=='>') || (Character.isWhitespace(c)))
				{
					unread(c);	//so we know what to purge till end of element.
					break;
				};
				xml_element_buffer.append(c);	//this internally takes control of length limit.
			};
			//Now skip till >
			for(;;)
			{
				char c=read();
				if (c=='>') break;
				if (!Character.isWhitespace(c))
					throw new ECorruptedFormat("Unexpected \'"+c+"\' in closing tag");
			};
			//The name of closing tag may be
			//	- the closing event, which is on events stack or
			//	- closing type, which is known to subclass.
			//First process event tag, possibly, since we can easily check them
			if (!signal_stack.isEmpty())
			{
				if (xml_element_buffer.equalsString(signal_stack.get(signal_stack.size()-1)))
				{
					signal_stack.remove(signal_stack.size()-1);
					return END_INDICATOR;
				};
			};
			//Now we have to drirect it to subclass specific method to detect non-event
			//tags.
			return processClosingTag(xml_element_buffer.toString());
		};
		/** Machinery for fetching and processing the opening XML tag.
		@return indicator to return from the {@link #next} method.
		@throws IOException if failed
		*/
		private int processOpeningTag()throws IOException
		{
			//We need to read till > or first white space.
			xml_element_buffer.reset();
			for(;;)
			{
				char c=read();
				if ((c=='>') || (Character.isWhitespace(c)))
				{
					unread(c);	//so we know what to purge till end of element.
					break;
				};
				xml_element_buffer.append(c);	//this internally takes control of length limit. Notice, we need to honour
												//internal tags longer than names, so no name length control here is allowed.
			};
			//Now buffer is carying a name. Let's check if it is a long or short signal name
			if (xml_element_buffer.equalsString(LONG_SIGNAL_ELEMENT))
			{
					//this is a complex element. We now expect and attribute, so scan
					//for it.
					char c;
					for(;;)
					{
						c = read();
						if (c=='>') throw new ECorruptedFormat("Expected "+LONG_SIGNAL_ELEMENT_ATTR+" attribute, found none");
						if (!Character.isWhitespace(c)) break;
					};
					//collect attribute name.
					xml_element_buffer.reset();
					xml_element_buffer.append(c);
					for(;;)
					{
						c = read();
						if (c=='>') throw new ECorruptedFormat("Expected attribute "+LONG_SIGNAL_ELEMENT_ATTR+"=\"some name\", found no value");
						if (c=='=') break;
						if (Character.isWhitespace(c)) continue;
						xml_element_buffer.append(c);
					};
					//we collected name.
					if (!xml_element_buffer.equalsString(LONG_SIGNAL_ELEMENT_ATTR))
						throw new ECorruptedFormat("Expected "+LONG_SIGNAL_ELEMENT_ATTR+" attribute but found \""+xml_element_buffer.toString()+"\"");
					//now consume till "
					for(;;)
					{
						c = read();
						if (c=='>') throw new ECorruptedFormat("Expected "+LONG_SIGNAL_ELEMENT_ATTR+" attribute value but found none");
						if (c=='"') break;
						if (!Character.isWhitespace(c)) 
							throw new ECorruptedFormat("Expected "+LONG_SIGNAL_ELEMENT_ATTR+" attribute value but found some text");
					};
					//now consume attribute value and collect it as a name.
					xml_element_buffer.reset();
					for(;;)
					{
						c = read();
						if (c=='"') break;
						//early length check.
						if (xml_element_buffer.length()>=max_name_length)
							throw new EFormatBoundaryExceeded("Signal name \""+xml_element_buffer+""+c+"\" too long");
						if (c==ESCAPE_CHAR)
						{
							c = unescape();
						};
						xml_element_buffer.append(c);
					};	
					//We just need to purge till > or complain if there
					//is something non-whitespace.
					for(;;)
					{
						c=read();
						if (c=='>') break;
						if (!Character.isWhitespace(c))
							throw new ECorruptedFormat("Unexpected character after \'"+c+"\' after \"<"+xml_element_buffer.toString()+"\"");
					};
					//and put it on elements stack. Notice, we do put a found tag, not begin name..
					signal_stack.add(LONG_SIGNAL_ELEMENT);
					//and keep as direct name to return.
					this.begin_name=xml_element_buffer.toString();
					//and make sure we will return this fake state later.
					this.state = STATE_DIRECT_INDICATOR;
					return BEGIN_INDICATOR;
			}else
			{
				//We just need to purge till > or complain if there
				//is something non-whitespace.
				for(;;)
				{
					char c=read();
					if (c=='>') break;
					if (!Character.isWhitespace(c))
						throw new ECorruptedFormat("Unexpected character after \'"+c+"\' after \"<"+xml_element_buffer.toString()+"\"");
				};
				//Now the tag is identified. We assume, that writer is expected to
				//avoid tags clash between signals or type tags, so we ask subclass
				//if this tag is type tag, and if it is not it will be a signal name.
				String tag = xml_element_buffer.toString();
				final int v = processOpeningTag(tag);
				if (v==BEGIN_INDICATOR)
				{
					//name is identified, we put it on stack
					signal_stack.add(tag);
					//and keep as direct name to return.
					this.begin_name = tag;
					//and at last we fake a state to enforce 
					this.state = STATE_DIRECT_INDICATOR;
				};
				return v;
			}
		};
		
		
		@Override protected void readSignalNameData(Appendable a, int limit)throws IOException
		{
			assert(begin_name!=null):"this should not be invoked, no name is preserved"; 
			a.append(this.begin_name);
			this.begin_name=begin_name;
		};
		@Override protected int readRegisterIndex()throws IOException
		{
			throw new UnsupportedOperationException("This format does not support names index");
		};
		@Override protected int readRegisterUse()throws IOException
		{
			throw new UnsupportedOperationException("This format does not support names index");
		};
		/*========================================================
		
				primitive reads
		
		=========================================================*/
		/* -------------------------------------------------------
				Elementary
		-------------------------------------------------------*/
		/** Reads a numeric primitive, that is a sequence down to ;
		into a {@link #xml_element_buffer} 
		@return xml_element_buffer or null if reached XML token	
				without collecting any data.
		*/
		private CharSequence tryReadNumericPrimitive()throws IOException
		{
			//skip leading white-spaces.
			//Notice, we should detected primitives separator as the end
			//and other XML as problems.
			xml_element_buffer.reset();
			char c;
			boolean tag_reached = false; 
			for(;;)
			{
				c = read();
				if (c==PRIMITIVES_SEPARATOR_CHAR) return xml_element_buffer;	//empty primitive.
				if (!Character.isWhitespace(c)) 
				{
					unread(c);
					break;
				};
			};
			//read till separator.
			for(;;)
			{
				c = read();
				if (c=='<')
				{
						unread(c);	//need to detect this element indicator.
									//because we use it in blocks too.
						tag_reached=true;
						break;
				};
				if ((c=='>')||( Character.isWhitespace(c)))
				{	
						unread(c);
						throw new ECorruptedFormat("Unexpected \'"+c+"\' inside a body a numeric primitive");
				};
				if (c==PRIMITIVES_SEPARATOR_CHAR) break;
				xml_element_buffer.append(c);
			};
			resetInterSignalLimit();	//to not limit block size, but limit iddle spaces.
			return ( tag_reached && xml_element_buffer.length()==0)
					 ? 
					  null
					   :
					  xml_element_buffer;
		};
		/** Reads a numeric primitive, that is a sequence down to ;
		into a {@link #xml_element_buffer} 
		@return xml_element_buffer
		@throws ENoMoreData if reached XML token
		*/
		private CharSequence readNumericPrimitive()throws IOException
		{
			CharSequence s = tryReadNumericPrimitive();
			if (s==null) throw new ENoMoreData();
			return s;
		};
		/** Converts boolean char representation to boolean
		@param c char, 0,1,f,F,t,T
		@return boolean value
		@throws ECorruptedFormat if not known char.
		*/
		private static boolean charToBoolean(char c)throws ECorruptedFormat
		{
			switch(c)
			{
				case 't': 
				case 'T':
				case '1': return true;
				case 'f':
				case 'F':
				case '0': return false;
				default: throw new ECorruptedFormat("Character \""+c+"\" does not represent boolean, \"f\",\"F\",\"0\",\"1\",\"t\" or \"T\" is expected.");
			}
		};
		/** This method is lenient and recognized tT1 and fF0 as true and false accordingly */
		@Override protected boolean readBooleanImpl()throws IOException
		{
			//Now we may safely assume, that if type tag was present
			//it was fetched. So cursor may be at some white-space characters
			CharSequence s = readNumericPrimitive();
			if (s.length()!=1) throw new ECorruptedFormat("Text \""+s+"\" does not represent boolean, \"f\",\"F\",\"0\",\"1\",\"t\" or \"T\" is expected.");
			return charToBoolean(s.charAt(0));
		};
		@Override protected byte readByteImpl()throws IOException
		{
			String s = readNumericPrimitive().toString();
			try{
				return Byte.parseByte(s);
			}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a byte "+ex.getMessage(),ex); }
		};
		
		
		@Override protected char readCharImpl()throws IOException
		{
			//Character needs to be polled, optionally escaped
			//and tested for trailing ;
			xml_element_buffer.reset();
			char c = read();
			if (c=='<')
			{
				unread(c);	//need to detect this element indicator.
							//Note: If we would not allow spaces we would not have to
							//do it.
				throw new ENoMoreData();
			};
			if (c=='>')
			{
				unread(c);
				throw new ECorruptedFormat("Unexpected \'"+c+"\' in a character primitive");
			};
			if (c==ESCAPE_CHAR)
			{
				c = unescape(); //unescape is consuming trailing ;
				//Now be leninet and allow missing ; which should appear after %0;
				//ie it should be %0;; in primitives.
				//Notice this leninecy will have troubles with understanding
				//	%20;;;a; as " ;a" which should be %20;;;;a;
				if (PRIMITIVES_SEPARATOR_CHAR==ESCAPE_CHAR_END)	//
				{
					 char v = read();
					 if (v!=PRIMITIVES_SEPARATOR_CHAR)
					 	unread(v);
				};
			}else
			{
				//check if there is a trailing ; which is optional however..			
				char v = read();
				if (v=='<')
				{
					unread(v);		//this is when optional ; is not found.
				}else
				if (v!=PRIMITIVES_SEPARATOR_CHAR)
				{
					unread(c);	//possible xml?
					throw new ECorruptedFormat("Expected "+PRIMITIVES_SEPARATOR_CHAR+" but found \""+v+"\"");
				}
			};
			return c;
		};
			
		@Override protected short readShortImpl()throws IOException
		{
			String s = readNumericPrimitive().toString();
			try{
				return Short.parseShort(s);
			}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a short "+ex.getMessage(),ex); }
		};
		@Override protected int readIntImpl()throws IOException
		{
			String s = readNumericPrimitive().toString();
			try{
				return Integer.parseInt(s);
			}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not an integer "+ex.getMessage(),ex); }
		};
		@Override protected long readLongImpl()throws IOException
		{
			String s = readNumericPrimitive().toString();
			try{
				return Long.parseLong(s);
			}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a long "+ex.getMessage(),ex); }
		};
		@Override protected float readFloatImpl()throws IOException
		{
			String s = readNumericPrimitive().toString();
			try{
				return Float.parseFloat(s);
			}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a float "+ex.getMessage(),ex); }
		};
		@Override protected double readDoubleImpl()throws IOException
		{
			String s = readNumericPrimitive().toString();				
			try{
				return Double.parseDouble(s);
			}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a double "+ex.getMessage(),ex); }
		};
		
		/*-------------------------------------------------------
					Blocks
		-------------------------------------------------------*/
		@Override protected int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException
		{
			//Boolean blocks are chains of 0,1,t,T,f,F followed by XML tag.
			//In leninent mode we allow white-spaces (which include EOL)
			char c;
			int readen = 0;
			while(length>0)
			{
				c = read();
				if (Character.isWhitespace(c)) continue;
				if (c=='<') { unread(c); break; }
				buffer[offset++] = charToBoolean(c);
				resetInterSignalLimit();	//to not limit block size, but limit iddle spaces.
				readen++;
				length--;
			};
			return readen;
		};
		@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
		{
			//Byte blocks are chains of XX hex pairs followed by XML tag.
			//In leninent mode we allow white-spaces (which include EOL) but only
			//between bytes.			
			int readen = 0;
			while(length>0)
			{
				char d1 = read();
				if (Character.isWhitespace(d1)) continue;
				if (d1=='<') { unread(d1); break; }
				char d0 = read();
				buffer[offset++] = (byte)((HEX(d1)<<4)+(HEX(d0)));
				resetInterSignalLimit();	//to not limit block size, but limit iddle spaces.
				readen++;
				length--;
			};
			return readen;
		};
		@Override protected int readByteBlockImpl()throws IOException
		{
			//Byte blocks are chains of XX hex pairs followed by XML tag.
			//In leninent mode we allow white-spaces (which include EOL) but only
			//between bytes.			
			for(;;)
			{
				char d1 = read();
				if (Character.isWhitespace(d1)) continue;
				if (d1=='<') { unread(d1); return -1; }
				char d0 = read();
				resetInterSignalLimit();	//to not limit block size, but limit iddle spaces.
				return ((HEX(d1)<<4)+(HEX(d0)));
			}
		};
		@Override protected int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException
		{
			//Char blocks are chains of char or escapes followed by XML tag.
			//This time we do not allow spaces.	
			state=STATE_CHARACTER_BLOCK;	//to control space skipping in nextIndicator()
			int readen = 0;
			while(length>0)
			{
				char c = read();
				if (c=='<') { unread(c); break; }
				if (c==ESCAPE_CHAR)
						c = unescape();
				buffer[offset++] = c;
				resetInterSignalLimit();	//to not limit block size, but limit iddle spaces.
				readen++;
				length--;
			};
			return readen;
		};
		@Override protected int readCharBlockImpl(Appendable a, int length)throws IOException
		{
			//Char blocks are chains of char or escapes followed by XML tag.
			//This time we do not allow spaces.		
			state=STATE_CHARACTER_BLOCK;	//to control space skipping in nextIndicator()
			int readen = 0;
			while(length>0)
			{
				char c = read();
				if (c=='<') { unread(c); break; }
				if (c==ESCAPE_CHAR)
						c = unescape();
				a.append(c);
				resetInterSignalLimit();	//to not limit block size, but limit iddle spaces.
				readen++;
				length--;
			};
			return readen;
		};
		
		@Override protected int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException
		{
			//Short blocks are chains short primitive elements.	
			//We can't however use readShortImpl because it would throw ENoMoreData.
			int readen = 0;
			while(length>0)
			{
				CharSequence s = tryReadNumericPrimitive();
				if (s==null) break;
				try{
					buffer[offset++] = Short.parseShort(s.toString());
					readen++;
					length--; 
				}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a short "+ex.getMessage(),ex); }
			};
			return readen;
		};
		@Override protected int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException
		{
			//Int blocks are chains int primitive elements.	
			//We can't however use readIntImpl because it would throw ENoMoreData.
			int readen = 0;
			while(length>0)
			{
				CharSequence s = tryReadNumericPrimitive();
				if (s==null) break;
				try{
					buffer[offset++] = Integer.parseInt(s.toString());
					readen++;
					length--; 
				}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a int "+ex.getMessage(),ex); }
			};
			return readen;
		};
		@Override protected int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException
		{
			//Long blocks are chains long primitive elements.	
			//We can't however use readLongImpl because it would throw ENoMoreData.
			int readen = 0;
			while(length>0)
			{
				CharSequence s = tryReadNumericPrimitive();
				if (s==null) break;
				try{
					buffer[offset++] = Long.parseLong(s.toString());
					readen++;
					length--; 
				}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a long "+ex.getMessage(),ex); }
			};
			return readen;
		};
		@Override protected int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException
		{
			//Float blocks are chains float primitive elements.	
			//We can't however use readFloatImpl because it would throw ENoMoreData.
			int readen = 0;
			while(length>0)
			{
				CharSequence s = tryReadNumericPrimitive();
				if (s==null) break;
				try{
					buffer[offset++] = Float.parseFloat(s.toString());
					readen++;
					length--; 
				}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a float "+ex.getMessage(),ex); }
			};
			return readen;
		};
		@Override protected int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException
		{
			//Double blocks are chains double primitive elements.	
			//We can't however use readDoubleImpl because it would throw ENoMoreData.
			int readen = 0;
			while(length>0)
			{
				CharSequence s = tryReadNumericPrimitive();
				if (s==null) break;
				try{
					buffer[offset++] = Double.parseDouble(s.toString());
					readen++;
					length--; 
				}catch(NumberFormatException ex){ throw new ECorruptedFormat("\""+s+"\" is not a double "+ex.getMessage(),ex); }
			};
			return readen;
		};
		
		
		
		
		
		
		
		
		
		/* ******************************************************
		
					Stream access.				
					
				Basically a simplified push-back reader.
		
		* *******************************************************/
		/** Handles {@link #current_inter_signal_chars} in {@link #read}
		@throws EFormatBoundaryExceeded if inter-signal space is too long.
		*/
		private void incrInterSignalLimit()throws EFormatBoundaryExceeded
		{
			if(max_inter_signal_chars!=0)
			{
				if (current_inter_signal_chars==max_inter_signal_chars)
					throw new EFormatBoundaryExceeded("Too many characters between signals. Up to "+max_inter_signal_chars+" chars is allowed");
					else current_inter_signal_chars++;
			};
		};
		/** Handles {@link #current_inter_signal_chars} in {@link #unread}*/
		private void decrInterSignalLimit()
		{
			if(max_inter_signal_chars!=0)
			{
				if (current_inter_signal_chars>0) current_inter_signal_chars--;
			};
		};
		/** Handles {@link #current_inter_signal_chars} in {@link #nextIndicator} and {@link #skip}*/
		private void resetInterSignalLimit()
		{
			current_inter_signal_chars=0;
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
			//un-read, or read char and unread it else.
			int i =unread_at;
			if (i==0)
			{
				final int r = tryRead(); //Note: we need to poll un-read buffer, but not throw.	
				assert(r>=-1);
				assert(r<=0x0FFFF);				
				if (r==-1) return true;
				unread((char)r);	//and un-read it.
				return false;
			}else
				return false;
		};
		/**
			Reads character either from push-back buffer or
			from stream. Differs from {@link #read} in such a way that it does not throw.
			@return read character or -1 if end of stream is reached.
			@throws IOException if low level have failed.
			@throws EFormatBoundaryExceeded if {@link #max_inter_signal_chars} is exceeded.
			@see #readEscaped
		*/
		protected final int tryRead()throws EUnexpectedEof, IOException,EFormatBoundaryExceeded
		{
			int i =unread_at;
			if (i==0)
			{
				final int r = readImpl();
				assert(r>=-1);
				assert(r<=0x0FFFF);
				
				if (r==-1) return r;					
				incrInterSignalLimit();
				return r;
			}else
			{
				final char c = unread[i-1];
				i--;
				this.unread_at=i;
				incrInterSignalLimit();
				return c;
			}
		};
		
		/**
			Reads character either from push-back buffer or
			from stream.
			@return read character
			@throws EUnexpectedEof if end of file was encounterd
			@throws IOException if low level have failed.
			@throws EFormatBoundaryExceeded if {@link #max_inter_signal_chars} is exceeded.
			@see #readEscaped
		*/
		protected final char read()throws EUnexpectedEof, IOException,EFormatBoundaryExceeded
		{
			int r = tryRead();
			assert(r>=-1);
			assert(r<=0x0FFFF);
			if (r==-1) throw new EUnexpectedEof();						
			return (char)r;
		};
		
		/** Calls {@link #read} and puts result back to stream.
			Subsequent calls to this method do not advance the stream
			cursor.
			@return read character
			@throws EUnexpectedEof if end of file was encounterd
			@throws IOException if low level have failed.
		*/
		protected final char peek()throws EUnexpectedEof, IOException
		{
			char c = read();
			unread(c);
			return c;
		};
		/** Puts character back in stream
			@param c character to put back
			@throws AssertionError if could not un-read character 
					because there is no place in buffer. It usually means,
					that some stream structure was larger than expected
					and exceeded buffers specified in constructor.
		*/
		private final void unreadImpl(char c)throws EBrokenStream
		{
			int i =unread_at;
			final char [] u = unread; 
			if (i>=u.length) throw new AssertionError("Can't un-read so many characters");
			u[i]=c;
			i++;
			this.unread_at = i;
		};
		/** Puts character back in stream and managed inter-signal length limit.
			@param c character to put back
			@throws AssertionError if could not un-read character 
					because there is no place in buffer. It usually means,
					that some stream structure was larger than expected
					and exceeded buffers specified in constructor.
		*/
		protected final void unread(char c)throws EBrokenStream
		{
			unreadImpl(c);
			decrInterSignalLimit();			
		};
		/** Reads character, detects
			if it is an escape sequence and 
			un-escapes it by reverses {@link AXMLSignalWriteFormat#escape}
			by calling {@link #unescape}
			@return un-escaped or directly read character.
		*/
		protected char readEscaped()throws IOException
		{
			char c = read();
			if (c==ESCAPE_CHAR)
			{
				return unescape();
			}else
				return c;
		};
		
		/* ********************************************************
		
				Tooling
		
		**********************************************************/
		private static int HEX(char digit)throws ECorruptedFormat
		{
			if ((digit>='0')&&(digit<='9')) return digit-'0';
			if ((digit>='a')&&(digit<='f')) return digit-'a'+10;
			if ((digit>='A')&&(digit<='F')) return digit-'A'+10;
			throw new ECorruptedFormat("\'"+digit+"\' is not a hex digit");
		};
		
		
		/* ********************************************************
		
				ISignalReadFormat
		
		**********************************************************/
		/** Always false, as this class defaults to non-described */
		@Override public boolean isDescribed(){ return false; };
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* ***********************************************************************
		
		
				Junit.org (4.2 style) test arena for private operations
		
		
		
		
		
		* ***********************************************************************/
		public static final class Test extends sztejkat.utils.test.ATest
		{
				private static final class DUT extends AXMLSignalReadFormat
				{
						java.io.StringReader in; 
					DUT()
					{
						super(
								8,//final int max_name_length,
								16,// final int max_events_recursion_depth,
								'%',';',//final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
								';',//final char PRIMITIVES_SEPARATOR_CHAR,
								"e","n",//final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR,
								4, //final int max_type_tag_length
								0  //max_inter_signal_chars
								); 
					};
					DUT(String s)
					{
						this();
						open(s);
					};
					@Override public boolean isDescribed(){ return false; };
					void open(String s){ in = new java.io.StringReader(s); };
					@Override protected int readImpl()throws IOException
					{
						return in.read();
					};
					@Override protected int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readByteBlockImpl()throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readCharBlockImpl(Appendable buffer, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected void closeImpl()throws IOException{ throw new AbstractMethodError(); };;
	
				};
				
			@org.junit.Test public void testHex()throws ECorruptedFormat
			{
				enter();
				/* Check elementary HEX->int conversion */
				{
					org.junit.Assert.assertTrue(HEX('0')==0);
					org.junit.Assert.assertTrue(HEX('A')==10);
					org.junit.Assert.assertTrue(HEX('a')==10);
					org.junit.Assert.assertTrue(HEX('F')==15);
					org.junit.Assert.assertTrue(HEX('f')==15);
				};
				leave();
			};
			
			@org.junit.Test public void testReadEscaped()throws IOException
			{
				enter();
				/*
						Check readEscaped(), what basically will also test
						unescape(), positive tests.
				*/
				DUT d = new DUT();
				d.open("a");
				org.junit.Assert.assertTrue(d.readEscaped()=='a');
				d.open("%%;");
				org.junit.Assert.assertTrue(d.readEscaped()=='%');
				d.open("%3;");
				org.junit.Assert.assertTrue(d.readEscaped()=='\u0003');
				d.open("%31;");
				org.junit.Assert.assertTrue(d.readEscaped()=='\u0031');
				d.open("%312;");
				org.junit.Assert.assertTrue(d.readEscaped()=='\u0312');
				d.open("%AfBc;");
				org.junit.Assert.assertTrue(d.readEscaped()=='\uafbc');
				leave();
			};
			
			@org.junit.Test public void testReadEscapedErr()throws IOException
			{
				enter();
				/*
						Check readEscaped(), what basically will also test
						unescape(), error detection tests.
				*/
				DUT d = new DUT();
				d.open("%u");
				try{
					d.readEscaped(); org.junit.Assert.fail();
				}catch(ECorruptedFormat ex){};
				
				d.open("%%%");
				try{
					d.readEscaped(); org.junit.Assert.fail();
				}catch(ECorruptedFormat ex){};
				
				d.open("%;");
				try{
					d.readEscaped(); org.junit.Assert.fail();
				}catch(ECorruptedFormat ex){};   
				
				d.open("%0");
				try{
					d.readEscaped(); org.junit.Assert.fail();
				}catch(EUnexpectedEof ex){};
				
				d.open("%0994898;");
				try{
					d.readEscaped(); org.junit.Assert.fail();
				}catch(ECorruptedFormat ex){};
				
				d.open("%09-;");
				try{
					d.readEscaped(); org.junit.Assert.fail();
				}catch(ECorruptedFormat ex){};
				
				leave();
			};
			
			
			@org.junit.Test public void testReadingAndUnReading()throws IOException
			{
				enter();
				/*
						Check if we can read, un-read 
				*/
				DUT d = new DUT();
				d.open("abcdef");
				
				org.junit.Assert.assertTrue(d.read()=='a');
				org.junit.Assert.assertTrue(d.read()=='b');
				d.unread('x');
				d.unread('y');
				org.junit.Assert.assertTrue(d.read()=='y');
				org.junit.Assert.assertTrue(d.read()=='x');
				org.junit.Assert.assertTrue(d.read()=='c');
				org.junit.Assert.assertTrue(d.read()=='d');
				
				leave();
			};
			
			@org.junit.Test public void testReadingAndUnReadingPeeking()throws IOException
			{
				enter();
				/*
						Check if we can read, un-read 
				*/
				DUT d = new DUT();
				d.open("abcdef");
				org.junit.Assert.assertTrue(d.peek()=='a');
				org.junit.Assert.assertTrue(d.read()=='a');
				org.junit.Assert.assertTrue(d.read()=='b');
				d.unread('x');
				d.unread('y');
				org.junit.Assert.assertTrue(d.peek()=='y');
				org.junit.Assert.assertTrue(d.read()=='y');
				org.junit.Assert.assertTrue(d.read()=='x');
				org.junit.Assert.assertTrue(d.peek()=='c');
				org.junit.Assert.assertTrue(d.read()=='c');
				org.junit.Assert.assertTrue(d.read()=='d');
				
				leave();
			};
			
			@org.junit.Test public void testReadingAndUnReadingEof()throws IOException
			{
				enter();
				/*
						Check if we can read, un-read 
				*/
				DUT d = new DUT();
				d.open("abcdef");
				org.junit.Assert.assertTrue(d.peek()=='a');
				org.junit.Assert.assertTrue(d.read()=='a');
				org.junit.Assert.assertTrue(d.read()=='b');
				org.junit.Assert.assertTrue(d.read()=='c');
				org.junit.Assert.assertTrue(d.read()=='d');
				org.junit.Assert.assertTrue(d.read()=='e');
				org.junit.Assert.assertTrue(!d.isEof());
				org.junit.Assert.assertTrue(d.read()=='f');
				org.junit.Assert.assertTrue(d.isEof());
				org.junit.Assert.assertTrue(d.isEof());
				org.junit.Assert.assertTrue(d.isEof());
				d.unread('x');
				org.junit.Assert.assertTrue(!d.isEof());
				d.unread('y');
				org.junit.Assert.assertTrue(!d.isEof());
				org.junit.Assert.assertTrue(d.peek()=='y');
				org.junit.Assert.assertTrue(d.read()=='y');
				org.junit.Assert.assertTrue(d.read()=='x');
				org.junit.Assert.assertTrue(d.isEof());
				
				try{
					d.peek();
					org.junit.Assert.fail();
				}catch(EUnexpectedEof x){};
				
				try{
					d.read();
					org.junit.Assert.fail();
				}catch(EUnexpectedEof x){};
				
				d.unread('z');
				org.junit.Assert.assertTrue(d.read()=='z');
				
				leave();
			};
		};
};
