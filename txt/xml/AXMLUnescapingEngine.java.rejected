package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.utils.SStringUtils;
import sztejkat.abstractfmt.txt.AUnescapingEngine;
import sztejkat.abstractfmt.EBrokenFormat;
import java.io.IOException;

/**
	A common rountines for un-escaping engines for XML processing
*/
abstract class AXMLUnescapingEngine extends AUnescapingEngine
{
				/** Maximum length of entity escape sequence */
				private static final int MAX_ESCAPE_SEQUENCE_LENGTH = 8;
				
				/** Character processing mode, {@link AXMLUnescapingEngine#isEscape} handler. */
				private abstract class AIsEscapeMode
				{
					protected abstract TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException;
				};
				/** Character processing mode, {@link AXMLUnescapingEngine#unescape} handler. */
				private abstract class AUnescapeMode
				{
					protected abstract int unescape(StringBuilder collection_buffer)throws IOException;
				};
				
				
				
				
				
				
				
				
				private final AUnescapeMode UNESCAPE_REJECT = new AUnescapeMode()
				{
					@Override protected int unescape(StringBuilder collection_buffer)throws IOException
					{
						throw new IllegalStateException("UNESCAPE_REJECT");
					}
				};
				
				
				
				
				
				
				/** Collecting regular character, on the lookup for an escape */
				private final AIsEscapeMode IS_ESCAPE_REGULAR = new AIsEscapeMode()
				{
					@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
					{
						assert(sequence_index==0);
						switch(c)
						{
							case '&':
										current_isEscape= IS_ESCAPE_ENTITY;
										return TEscapeCharType.ESCAPE_BODY;
							case '_':
										current_isEscape= IS_ESCAPE_CUSTOM;
										return TEscapeCharType.ESCAPE_BODY;
							default:
										return TEscapeCharType.REGULAR_CHAR;
						}
					};
				};
				
				
				
				/** Collecting XML entity mode escape, lookup for numeric, void or symbolic*/
				private final AIsEscapeMode IS_ESCAPE_ENTITY = new AIsEscapeMode()
				{
					@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
					{
						assert(sequence_index==1);
						switch(c)
						{
							case '#':
										current_isEscape= IS_ESCAPE_NUMERIC_ENTITY;
										return TEscapeCharType.ESCAPE_BODY;
							case ';':
										current_isEscape= IS_ESCAPE_REGULAR;
										current_unescape= UNESCAPE_VOID;
										return TEscapeCharType.ESCAPE_LAST_BODY;
							default:
										current_isEscape= IS_ESCAPE_SYMBOLIC_ENTITY;
										return TEscapeCharType.ESCAPE_BODY;
						}
					};
				};
				/** Represents escape producing void. Zero length &; and _ followed by space */
				private final AUnescapeMode UNESCAPE_VOID = new AUnescapeMode()
				{
					@Override protected int unescape(StringBuilder collection_buffer)throws IOException
					{
						return -1;
					}
				};
				
				
				
				
				/** Collecting XML symbolic entity, size limited. */
				private final AIsEscapeMode IS_ESCAPE_SYMBOLIC_ENTITY = new AIsEscapeMode()
				{
					@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
					{
						assert(sequence_index>=2);
						switch(c)
						{
							case ';':
										current_isEscape= IS_ESCAPE_REGULAR;
										current_unescape= UNESCAPE_SYMBOLIC_ENTITY;
										return TEscapeCharType.ESCAPE_LAST_BODY;
							default:
										if (escape_sequence_length>=MAX_ESCAPE_SEQUENCE_LENGTH)
											throw new EBrokenFormat("Too long symbolic & entity.");
										return TEscapeCharType.ESCAPE_BODY;
										
						}
					};
				};
				/** Represents &amp; symbolic escape. */
				private final AUnescapeMode UNESCAPE_SYMBOLIC_ENTITY = new AUnescapeMode()
				{
					@Override protected int unescape(StringBuilder collection_buffer)throws IOException
					{
						return unescapeSymbolicEntity(collection_buffer);
					}
				};
				
				/** Collecting XML entity mode escape, size limited. */
				private final AIsEscapeMode IS_ESCAPE_NUMERIC_ENTITY = new AIsEscapeMode()
				{
					@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
					{
						assert(sequence_index==2);
						switch(c)
						{
							case 'x':
										current_isEscape= IS_ESCAPE_HEX_NUMERIC_ENTITY;
										return TEscapeCharType.ESCAPE_BODY;
							case ';':
										current_isEscape= IS_ESCAPE_REGULAR;
										current_unescape= UNESCAPE_DECIMAL_ENTITY;
										return TEscapeCharType.ESCAPE_LAST_BODY;
							default:
										if (
											((c>='0')&&(c<='9'))
										)
										{
											current_isEscape= IS_ESCAPE_DECIMAL_NUMERIC_ENTITY;
											return TEscapeCharType.ESCAPE_BODY;
										}else
											throw new EBrokenFormat("Unexpected \'"+c+"\' in & entity");
										
						}
					};
				};
				
				/** Collecting XML decimal entity */
				private final AIsEscapeMode IS_ESCAPE_DECIMAL_NUMERIC_ENTITY = new AIsEscapeMode()
				{
					@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
					{
						assert(sequence_index>=3);
						switch(c)
						{
							case ';':
										current_isEscape= IS_ESCAPE_REGULAR;
										current_unescape= UNESCAPE_DECIMAL_ENTITY;
										return TEscapeCharType.ESCAPE_LAST_BODY;
							default:
										if (
											((c>='0')&&(c<='9'))
										)	
										{
											// &#x10FFFF --> &#1114111;
											if (escape_sequence_length>=9)
													throw new EBrokenFormat("Too long decimal & entity");
											return TEscapeCharType.ESCAPE_BODY;
										}else
											throw new EBrokenFormat("Unexpected \'"+c+"\' in & decimal entity");
										
						}
					};
				};
				/** For un-escaping &amp; decimal entity */
				private final AUnescapeMode UNESCAPE_DECIMAL_ENTITY = new AUnescapeMode()
				{
					@Override protected int unescape(StringBuilder collection_buffer)throws IOException
					{
						return unescapeDecimalEntity(collection_buffer);
					}
				};
				/** Collecting XML hex entity */
				private final AIsEscapeMode IS_ESCAPE_HEX_NUMERIC_ENTITY = new AIsEscapeMode()
				{
					@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
					{
						assert(sequence_index>=3);
						switch(c)
						{
							case ';':
										current_isEscape= IS_ESCAPE_REGULAR;
										current_unescape= UNESCAPE_HEX_ENTITY;
										return TEscapeCharType.ESCAPE_LAST_BODY;
							default:
										if(
											((c>='0')&&(c<='9'))
											||
											((c>='a')&&(c<='f'))
											||
											((c>='A')&&(c<='F'))											
										)	
										{
											// &#x10FFFF --> &#1114111;
											if (escape_sequence_length>=9)
													throw new EBrokenFormat("Too long hex & entity");
											return TEscapeCharType.ESCAPE_BODY;
										}else
											throw new EBrokenFormat("Unexpected \'"+c+"\' in & hex entity");
										
						}
					};
				};
				/** For un-escaping &amp; hex entity */
				private final AUnescapeMode UNESCAPE_HEX_ENTITY = new AUnescapeMode()
				{
					@Override protected int unescape(StringBuilder collection_buffer)throws IOException
					{
						return unescapeHexEntity(collection_buffer);
					}
				};
				
				
				
				/** Collecting custom underscore escape, lookup if void, __ or hex. */
				private final AIsEscapeMode IS_ESCAPE_CUSTOM = new AIsEscapeMode()
				{
					@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
					{
						assert(sequence_index==1);
						switch(c)
						{
							case '_':   //A __ escape
										current_isEscape= IS_ESCAPE_REGULAR;
										current_unescape= UNESCAPE_CUSTOM_UNDERSCORE;
										return TEscapeCharType.ESCAPE_LAST_BODY;
							default:
									if (
											((c>='0')&&(c<='9'))
											||
											((c>='a')&&(c<='f'))
											||
											((c>='A')&&(c<='F'))											
										)
										{
												//hex mode escape 
												current_isEscape= IS_ESCAPE_CUSTOM_HEX;
												return TEscapeCharType.ESCAPE_BODY;											
										}else
										if (getClassifier().isXMLSpace(c))
										{
												//The stand-alone _ is void 
												current_isEscape= IS_ESCAPE_REGULAR;
												current_unescape= UNESCAPE_VOID;
												return TEscapeCharType.REGULAR_CHAR;
										}else
											throw new EBrokenFormat("Unexpected \'"+c+"\' in underscore escape");
						}
					};
				};
				/** Unescape custom __ hex escape */
				private final AUnescapeMode UNESCAPE_CUSTOM_UNDERSCORE = new AUnescapeMode()
				{
					@Override protected int unescape(StringBuilder collection_buffer)throws IOException
					{
						assert(collection_buffer.length()==2);
						return '_';
					}
				};
				
				
				/** Collecting custom underscore hex escape. */
				private final AIsEscapeMode IS_ESCAPE_CUSTOM_HEX = new AIsEscapeMode()
				{
					@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
					{
						assert(sequence_index>=2);
						
						if (
							((c>='0')&&(c<='9'))
							||
							((c>='a')&&(c<='f'))
							||
							((c>='A')&&(c<='F'))											
						)
						{
							if (sequence_index==4)
							{
								current_isEscape= IS_ESCAPE_REGULAR;
								current_unescape= UNESCAPE_CUSTOM_HEX;
								return TEscapeCharType.ESCAPE_LAST_BODY;
							}
							else
							{
								return TEscapeCharType.ESCAPE_BODY;
							}
						}
						else
							throw new EBrokenFormat("Unexpected \'"+c+"\' in underscore escape");
					};
				};			
				/** Unescape custom _XXXX hex escape */
				private final AUnescapeMode UNESCAPE_CUSTOM_HEX = new AUnescapeMode()
				{
					@Override protected int unescape(StringBuilder collection_buffer)throws IOException
					{
						return unescapeCustomHex(collection_buffer);
					}
				};
				
				
							/** Current handler for {@link #isEscape} */
							private AIsEscapeMode current_isEscape = IS_ESCAPE_REGULAR;
							/** Current handler for {@link #unescape} */
							private AUnescapeMode current_unescape = UNESCAPE_REJECT;
							
	/* *************************************************************************
	
				Services required from subclasses.
	
	* *************************************************************************/
	/** Returns XML classifier 
	@return a classifier to use for XML chars recognition */
	protected abstract IXMLCharClassifier getClassifier();
	/* *********************************************************************************
	
		AUnescapingEngine
	
	**********************************************************************************/
	@Override public void reset()
	{
		super.reset();
		//reset handlers.
		current_isEscape = IS_ESCAPE_REGULAR;
		current_unescape = UNESCAPE_REJECT;
	}
	@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
	{
		//use handler
		return current_isEscape.isEscape(c,escape_sequence_length,sequence_index);
	};
	@Override protected int unescape(StringBuilder collection_buffer)throws IOException
	{
		//use and reset handler since it is a one-shot function.
		final AUnescapeMode h = current_unescape; 
			 current_unescape = UNESCAPE_REJECT;
		return h.unescape(collection_buffer);
	}
	
	/* *********************************************************************************
		
		Support routines
		
	**********************************************************************************/
	
	/** Decodes specified section if collection buffer as hex number
	@param collection_buffer a buffer, non null
	@param from first character to decode
	@param tolast_minus_this last character to decode, 0 last in string, 1 one before last and so on.
	@return decoded hex code point
	@throws IOException if failed.
	*/
	private int unescapeHexPortion(StringBuilder collection_buffer, int from, int tolast_minus_this)throws IOException
	{
		//hex
		int v = 0;
		for(int i=from,n=collection_buffer.length()-tolast_minus_this;i<n;i++)
		{
			final char digit = collection_buffer.charAt(i);
			final int nibble;
			if ((digit>='0')&&(digit<='9'))
			{
				nibble = digit - '0';
			}else
			if ((digit>='A')&&(digit<='F'))
			{
				nibble = digit - 'A'+10;
			}else
			if ((digit>='a')&&(digit<='f'))
			{
				nibble = digit - 'a'+10;
			}else
				throw new EBrokenFormat("\""+digit+"\" is not hex digit 0...9A...Fa...f");
			v<<=4;
			v|=nibble;
		};
		if ((v<0)||(v>0x10FFFF))throw new EBrokenFormat(collection_buffer+" represents 0x"+Integer.toHexString(v)+" which is not a valid unicode code-point");
		return v;
	};
	/** Decodes specified section if collection buffer as decimal number
	@param collection_buffer a buffer, non null
	@param from first character to decode
	@param tolast_minus_this last character to decode, 0 last in string, 1 one before last and so on.
	@return decoded hex code point
	@throws IOException if failed.
	*/
	private int unescapeDecimalPortion(StringBuilder collection_buffer, int from, int tolast_minus_this)throws IOException
	{
		//hex
		int v = 0;
		for(int i=from,n=collection_buffer.length()-tolast_minus_this;i<n;i++)
		{
			final char digit = collection_buffer.charAt(i);
			final int digit_value;
			if ((digit>='0')&&(digit<='9'))
			{
				digit_value = digit - '0';
			}else
				throw new EBrokenFormat("\""+digit+"\" is not decimal digit 0...9A...Fa...f");
			v*=10;
			v+=digit_value;
		};
		if ((v<0)||(v>0x10FFFF))throw new EBrokenFormat(collection_buffer+" represents 0x"+Integer.toHexString(v)+" which is not a valid unicode code-point");
		return v;
	};
	/** Unescapes _XXXX hex escape 
	@param collection_buffer carries _XXXX
	@return XXXX
	@throws IOException if failed.
	*/
	private int unescapeCustomHex(StringBuilder collection_buffer)throws IOException
	{
		assert(collection_buffer.length()==5);
		return unescapeHexPortion(collection_buffer,1,0);
	}
	/** Unescapes &amp;#xXXXX; hex escape 
	@param collection_buffer carries &amp;#xXXXX;
	@return XXXX
	@throws IOException if failed.
	*/
	private int unescapeHexEntity(StringBuilder collection_buffer)throws IOException
	{
		assert(collection_buffer.length()>=4);
		return unescapeHexPortion(collection_buffer,3,1);
	}
	/** Unescapes &amp;#XXXX; decimal escape 
	@param collection_buffer carries &amp;#XXXX;
	@return XXXX
	@throws IOException if failed.
	*/
	private int unescapeDecimalEntity(StringBuilder collection_buffer)throws IOException
	{
		assert(collection_buffer.length()>=3);
		return unescapeDecimalPortion(collection_buffer,2,1);
	}
	/** Unescapes &amp;???; symbolic escape 
	@param collection_buffer carries &amp;????;
	@return symbol value.
	@throws IOException if failed.
	*/
	private int unescapeSymbolicEntity(StringBuilder collection_buffer)throws IOException
	{
		assert(collection_buffer.length()>=2);
		//A direct set
		//Now a tough choice - use switch-case and allocate String or
		//use direct equals?
		//Note: We might use here a TreeMap and support defining of entities, but our format
		//		is specified to NOT allow entity definition. 
		if (SStringUtils.equalsCaseSensitive(collection_buffer,"&lt;")) return '<';
		if (SStringUtils.equalsCaseSensitive(collection_buffer,"&gt;")) return '>';
		if (SStringUtils.equalsCaseSensitive(collection_buffer,"&amp;")) return '&';
		if (SStringUtils.equalsCaseSensitive(collection_buffer,"&apos;")) return '\'';
		if (SStringUtils.equalsCaseSensitive(collection_buffer,"&quot;")) return '\"';
		throw new EBrokenFormat("Unrecognized XML entity: "+collection_buffer);
	}
};