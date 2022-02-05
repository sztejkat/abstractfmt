package sztejkat.abstractfmt.json;
import sztejkat.abstractfmt.CAdaptivePushBackReader;
import static sztejkat.abstractfmt.util.SHex.HEX2D;
import java.io.IOException;
import java.io.Reader;


/**
	A base low-level operations.
	<p>
	This reader accepts some broken JSON formats. Especially it accepts formats which 
	missed or duplicates , separator in lists or arrays.
*/
abstract class AJSONIndicatorReadFormat extends AJSONFormat implements IIndicatorReadFormat
{
				/** Low level input */
				private final CAdaptivePushBackReader input;
				/** A count for maximum consequent insignificant
				white spaces before reporting an error */
				private final int max_consequence_whitespaces;
				/** A bound, limitied buffer for colleting all
				elements, tokens and etc. Non final because can be re-initialized when
				name limit is set since it must also accomodate
				short-encoded begin signal names.
				*/
				private CBoundAppendable token_buffer;
				/** Used to validate name lengths */
				private int max_signal_name_length;		
				
				/* ***********************************************
				
						State
				
				************************************************/	
				/** Cache for {@link #getSignalName} */
				private String signal_name_cache;
				/** True if expect , before next token.
				This is set to false at type and begin indicators
				and to true when any value is fetched.*/
				private boolean token_list_separator_expected;
				/** Set to true if currently processing 
				described array and did to fetch its end yet.
				Used to tell apart ]} trailing this array from
				]} end indicator.
				*/
				private boolean is_described_array;
				/** Cache for {@link #getIndicator} */
				private TIndicator indicator_cache;				
	/**
		Creates
		@param input direct input, non null.
		@param settings settings, non null
		@param max_consequence_whitespaces c count for maximum consequent insignificant
				white spaces before reporting an error. This is a protection against
				denial of service attack when source is producing an infinite sequence
				of insignificat space characters between JSON tokens.
	*/
	AJSONIndicatorReadFormat(Reader input,
								CJSONSettings settings,
								int max_consequence_whitespaces
								 )
	{
		super(settings);
		assert(input!=null);
		assert(max_consequence_whitespaces>=1);
		this.input = new CAdaptivePushBackReader(input,1,16);
		this.max_consequence_whitespaces=max_consequence_whitespaces;
		//Note: 37 is max float length, but default name length is 1024
		this.max_signal_name_length = 1024;
		this.token_buffer = new CBoundAppendable(
					Math.max(settings.getMaximumReserverWordLength(),
								1027));
	};
	
	/* *************************************************************************
	
	
				Low level routines
	
	
	* *************************************************************************/
	/**
		Skips all available continous white spaces.
		At the return from this method stream is either at EOF
		or at non-white space character.
		@throws EFormatBoundaryExceeded if there was too many white-space characters.
	*/
	protected final void skipWhitespaces()throws IOException,EFormatBoundaryExceeded
	{
		int c = max_consequence_whitespaces;
		for(;;)
		{
			int c = input.read();
			if (c==-1) break;
			if (!Character.isWhitespace(c))
			{
				input.unread((char)c);
				break;
			}else
			{
				if (c==0) throw new EFormatBoundaryExceeded("Too many insignifcant white spaces, allowed up to "+max_consequence_whitespaces);
			 	c--;			 	
			};
		}
	};
	/** Reads from input and throws on eof
	@return read value
	@throws IOException if failed at low level
	@throws EUnexpectedEof if reached end of file.
	*/
	protected char readAlways()throws IOException,EUnexpectedEof
	{
		int c = input.read();
		if (c==-1) throw new EUnexpectedEof();
		return (char)c;
	};
	/**
		Reads string character, unescaping it
		@return 0...0xFFFF character, -1 for EOF, -2 for closing "
		@throws EBrokenFormat if encountered an escape it cannot understand.					
	*/
	protected int readStringChar()throws EBrokenFormat,IOException
	{
		int c = input.read();
		if (c==-1) return -1;
		if (c=='\"') return -2;
		if (c=='\\')
		{
			char d = readAlways();
			switch(d)
			{
				case '\"': 
				case '\\': 
				case '/': return d;
				case 'b': return (char)0x0008;
				case 'f': return (char)0x000C;
				case 'n': return (char)0x000A;
				case 'r': return (char)0x000D;
				case 't': return (char)0x0009;
				case 'u':
						{							
							int v = 0;
							for(int i = 0; i<4; i++)
							{
								int d0 = HEX2D (readAlways());
								if (d0==-1) throw new EBrokenFormat("Not a hex digit");
								v = v<<4;
								v = v | d0
							}
							return (char)v;
						};
				default: throw new EBrokenFormat("Unknow escape character \'"+(char)c+"\'");				
			};
		};
		return (char)c;
	};
	/** Reads "eclosed" name. Expects cursor to be at or before first character of a token, possibly
	in white spaces. 
	@return #token_buffer
	@throws EFormatBoundaryExceeded if token is too long.
	@throws EUnexpectedEof if reached end of file
	@throws EBrokenFormat if encountered " 
	*/
	protected CBoundAppendable readName()throws IOException,
												 EUnexpectedEof,
												 EBrokenFormat,
												 EFormatBoundaryExceeded
	{
		skipWhitespaces();				
		token_buffer.reset();
		char c = readAlways();
		if (c!='\"') throw new EBrokenFormat("Expected \" but \'"+c+"\' was found");
		for(;;)
		{
			int d=readStringChar();
			switch(d)
			{
				case -1: throw new EUnexpectedEof();
				case -2: break;
				default: token_buffer.append((char)d);
			}
		};
		return token_buffer;
	};
	/** Reads value token into an internal shared buffer.
	Expects cursor to be at or before first character of a token, possibly
	in white spaces. If first token character is " processed token
	using {@link #readStringChar}. Otherwise processes is using
	<code>input.read()</code>.
	<p>
	Token is finished when " is read for string tokens
	or white space, comma or ]} for normal non-string values
	@return #token_buffer
	@throws EFormatBoundaryExceeded if token is too long.
	@throws EUnexpectedEof if reached end of file
	@throws EBrokenFormat if encountered " 
	*/	
	protected CBoundAppendable readValue()throws IOException,
												 EUnexpectedEof,
												 EBrokenFormat,
												 EFormatBoundaryExceeded
	{		
		skipWhitespaces();				
		token_buffer.reset();
		//Read first character
		char c = readAlways();
		boolean string_mode = (c=='\"');
		if (string_mode)
		{
			for(;;)
			{
				int d=readStringChar();
				switch(d)
				{
					case -1: throw new EUnexpectedEof();
					case -2: break;
					default: token_buffer.append((char)d);
				}
			};
		}else
		{
			for(;;)
			{
				c = readAlways();			
				if (Character.isWhitespace(c) || (c==',') || (c=='}') || (c==']'))
				{
					 input.unread(c);
					 break;
				};
				if (c=='\"') throw new EBrokenFormat("Unexpected \"");
				token_buffer.append(c);
			};
		};
		return token_buffer;
	};
	/**	
		Reads indicator under cursor and updates caches.
		Once returned the cursor is after the indicator
		and indicator cache is set.
		
		@return either value set in cache or TIndicator.EOF. 
	*/
	private TIndicator updateIndicator()throws IOException
	{
		/*
			Now we need to fetch what is under cursor.
			The cursor should be after most recent token
			what means it can be inside a white space
			or at first character of next token.
			
			Otherwise we should not make any assumption about
			when this method is called.
			
			We do intentionally use a simplified processing 
			which do not fully process , in arrays because
			fully processing it would require deeper state
			tracking using stack.
		*/
		skipWhitespaces();
		if (token_list_separator_expected)
		{
			//Now we poll if we have list separator.
			int c = input.read();
			if (c==-1) return TIndicator.EOF;
			if (c==',')
			{
				//If , is present then set of allowed indicators is restricted
				//to begin and type and data.
				skipWhiteSpaces();
			};
		};
		int c = input.read();
		/* Now we may have begin, type, flush, end or data.
		   The only thing we may assume is that the cursor is 
		   now at the beginning of next JSON token. We may not assume
		   that we were processing previous commands using correct methods.
		   
		   The begin is:
		   	{"name":[
		   	{"begin":"name","content":[
		   The end is
		   	]}
		   The type is:
		   	{"type":
		   and flush is 
		   	}
		   The tricky part is when type denotes an array and cursor is at the
		   end of an array:
		    {"int[]":[.....]}
		   in which case flush looks exactly like end indicator.
		*/
		switch(c)
		{			
			case '{':
					//We may have begin or type.
					return processBeginOrType();
			case ']':
				 	//This character may indicate:
				 	//	- a first character of ]} pair which may be either of
				 	//			- end indicator;
				 	//			- flush of an array
				 	//	- just an end of an array
				 	//	- end of enclosed stream.
				 	return processEndOrFlush();
			case '}':
					//this is a flush indicator if we are in a described format.
					if (isDescribed())
					{
						//we have no strict flush information.
						signal_name_cache =null;
						indicator_cache=TIndicator.FLUSH_ANY;
					}else
						throw new EBrokenFormat("Unexpected \'}\'");
			default:
				// in this case we have data
				input.unread(c);
				signal_name_cache =null;
				indicator_cache=TIndicator.DATA;
				return TIndicator.DATA;
		}
	}
	/** 
		Invoked when during indicator look-up ] was read from stream.
		This method checks if ]} is found and decides if it is flush (described mode),
		end indicator or just an end of an array.
	*/
	private TIndicator processEndOrFlush()throws IOException
	{
		  skipWhitespaces();
		  c = input.read();
		  switch(c)
		  {
			case -1:
					//Now we may just be unable to read what is next, or be really at the end
					//and touched the 
					input.unread(']');
					signal_name_cache =null;
					return TIndicator.EOF;
			case '}':
					if (is_described_array)
					{
						signal_name_cache =null;
						indicator_cache = TIndicator.FLUSH_BLOCK;
						is_described_array=false;								
					}else
					{
						signal_name_cache =null;
						indicator_cache = TIndicator.END;
					}
					return indicator_cache;                                                         
			default:
					//this might have been end of an array in undescribed mode.
					if (isDescribed())
						throw new EBrokenFormat("Unexpected \'"+(char)c+"\' in end indicator.");
					else
					{
						input.unread((char)c);
						input.unread(']');
						indicator_cache=TIndicator.DATA;
					}
		  };
	};
	/** Invoked when { was read from a stream. Processes type or begin 
	indicator and returns it.
	*/
	private TIndicator processBeginOrType()throws IOException
	{
		CBoundAppendable n = readName();
		//Compare it with known elements.
		if (n.equalsString(settings.BEGIN))
		{
			//we have long form begin: {"begin":"name","content":
			skipWhitespaces();
			
			char d = readAlways();
			if (d!=':') throw new EBrokenFormat("Unexpected \'"+d+"\' while : required");
			n = readName();
			if (b.length()>max_signal_name_length)
				throw new EFormatBoundaryExceeded("Signal name too long");
				
			signal_name_cache=b.toString();
			
			skipWhitespaces();							
			char d = readAlways();
			if (d!=',') throw new EBrokenFormat("Unexpected \'"+d+"\' while , required");
			n = readName();
			if (!n.equalsString(settings.CONTENT))
				throw new EBrokenFormat("Expected \""+settings.CONTENT+"\" but \""+n+"\"" found);						
			
			is_described_array = false;
			token_list_separator_expected =false;
			indicator_cache = TIndicator.BEGIN_DIRECT;
			return TIndicator.BEGIN_DIRECT;
		}else
		{
			if (isDescribed())
			{
				//Possibly type: {"typename":
				TIndicator possible = null;
				if (n.equalsString(settings.BOOLEAN_ELEMENT))
				{
					possible=TYPE_BOOLEAN;
				}else
				if (n.equalsString(settings.BYTE_ELEMENT))
				{
					possible=TYPE_BYTE;
				}else
				if (n.equalsString(settings.CHAR_ELEMENT))
				{
					possible=TYPE_CHAR;
				}else
				if (n.equalsString(settings.SHORT_ELEMENT))
				{
					possible=TYPE_SHORT;
				}else
				if (n.equalsString(settings.INT_ELEMENT))
				{
					possible=TYPE_INT;
				}else
				if (n.equalsString(settings.LONG_ELEMENT))
				{
					possible=TYPE_LONG;
				}else
				if (n.equalsString(settings.FLOAT_ELEMENT))
				{
					possible=TYPE_FLOAT;
				}else
				if (n.equalsString(settings.DOUBLE_ELEMENT))
				{
					possible=TYPE_DOUBLE;
				}else
				if (n.equalsString(settings.BOOLEAN_BLOCK_ELEMENT))
				{
					possible=TYPE_BOOLEAN_BLOCK;
				}else
				if (n.equalsString(settings.BYTE_BLOCK_ELEMENT))
				{
					possible=TYPE_BYTE_BLOCK;
				}else
				if (n.equalsString(settings.CHAR_BLOCK_ELEMENT))
				{
					possible=TYPE_CHAR_BLOCK;
				}else
				if (n.equalsString(settings.SHORT_BLOCK_ELEMENT))
				{
					possible=TYPE_SHORT_BLOCK;
				}else
				if (n.equalsString(settings.INT_BLOCK_ELEMENT))
				{
					possible=TYPE_INT_BLOCK;
				}else
				if (n.equalsString(settings.LONG_BLOCK_ELEMENT))
				{
					possible=TYPE_LONG_BLOCK;
				}else
				if (n.equalsString(settings.FLOAT_BLOCK_ELEMENT))
				{
					possible=TYPE_FLOAT_BLOCK;
				}else
				if (n.equalsString(settings.DOUBLE_BLOCK_ELEMENT))
				{
					possible=TYPE_DOUBLE_BLOCK;
				}
				if (possible!=null)
				{
					is_described_array = (possible.FLAGS & TIndicator.BLOCK)!=0;
					skipWhitespaces();
					char d = readAlways();
					if (d!=':') throw new EBrokenFormat("Unexpected \'"+d+"\' while : required");
					signal_name_cache =null;
					indicator_cache = posible;
					token_list_separator_expected=false;
					return indicator_cache;
				};
				//fall through to short form.
			}
		//we have short form begin:   {"signalname":
		skipWhitespaces();
		char d = readAlways();
		if (d!=':') throw new EBrokenFormat("Unexpected \'"+d+"\' while : required");
		skipWhitespaces();
		//Additionally validate signal name
		if (b.length()>max_signal_name_length)
			throw new EFormatBoundaryExceeded("Signal name too long");
		signal_name_cache=b.toString();						
		is_described_array = false;
		token_list_separator_expected=false;
		indicator_cache = TIndicator.BEGIN_DIRECT;
		return TIndicator.BEGIN_DIRECT;
		}
	};
		
	};
	
	/* *************************************************************************
	
	
				IIndicatorReadFormat
	
	
	* *************************************************************************/
	/* ----------------------------------------------------------------------------
					Information and settings
	----------------------------------------------------------------------------*/
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
		token_buffer = new CBoundAppendable(Math.max(Math.max(37,characters,settings.getMaximumReserverWordLength())));
	};
	@Override public int getMaxSignalNameLength(){ return max_signal_name_length;}
	/** Set to Integer.MAX_VALUE characters */
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; }
	/* ----------------------------------------------------------------------------
					Indicators
	----------------------------------------------------------------------------*/
	public TIndicator getIndicator()throws IOException
	{
		if (indicator_cache!=null) return indicator_cache;
		return updateIndicator();
	};
	public void next()throws IOException
	{
		TIndicator current = getIndicator();
		if (current==TIndicator.DATA)
		{
			//Now we have to skip data.
			//Data do terminate at nearest flush, end, type or begin.
			//For us it should be enough to get to nearest "}" "{"  or "]}", but properly
			//detecting string values. So basically we read character and if 
			//we encounter , we test if next non-whitespace character is ".
			//if it is we skip string.
			for(;;)
			{
				char c = readAlways();
				switch(c)
				{
					case ',':
							//Value skip, possibly string value.
							skipWhitespaces();
							c = readAlways();
							if (c=='\"')
							{
								//Now skip string content.
								//Note: There is potential for denial of service attack,
								//but we can't avoid it.
								while(readAlways()!='\"');
							}else
								input.unread(c);
							break;
					case '{':
							//this is certain begin or type indicator.
							input.unread(c);
							token_list_separator_expected =false;
							is_described_array=false;
							indicator_cache=null;
							return
					case ']':
							//this may be an end of array of a piece of flush/end 
							//indicator.
							skipWhitespaces();
							c = readAlways();
							if (c=='}')
							{
								//yes, it is an indicator. Leave indicator machine
								//to process it.
								input.unread('}');
								input.unread(']');
								token_list_separator_expected =false;
								//is_described_array=false; <-- this must be left untouched.
								indicator_cache=null;
								return
							};
							break;
				};
			}
		}else
		{
			//In this case in our implementation cursor is already AFTER 
			//the indicator, so it is enough to wipe out cache. Notice,
			//content control and name cache must stay untouched.
			indicator_cache = null;
		};
	};
	
};