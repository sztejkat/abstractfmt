package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.util.CAdaptivePushBackReader;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EUnexpectedEof;
import java.io.IOException;
import java.io.Reader;

/**
	XML reader with characters deconding capaibilities.
*/
class CDecodingXMLReader extends CAdaptivePushBackReader
{	
				private final CXMLSettings settings;
	/** 
	Creates	
	@param in source to read from
	@param initial_size intial buffer capacity
	@param size_increment buffer growth increment.
	@param CXMLSettings settings
	*/
	public CDecodingXMLReader(Reader in, int initial_size,int size_increment, CXMLSettings settings)
	{
		super(in, initial_size, size_increment);
		assert(settings!=null);
		this.settings=settings;
	};
	/* -------------------------------------------------------------------
	
		Additional API related to plain reading.
	
	-------------------------------------------------------------------*/
	/** Returns character which would be read next, without reading it.
	@return -1 if eof, otherwise 0....0xffff
	*/
	public final int peek()throws IOException
	{
		int c = read();
		if (c==-1) return -1;
		unread((char)c);
		return c;
	};
	/** Checks if end-of-file is reached 
	@return true if it is
	*/
	public final boolean isEof()throws IOException
	{
		return peek()==-1;
	};
	/** Reads, throws if end-of-file is reached
	@return character read
	@throws EUnexpectedEof if reached end of file.
	*/
	public char readChar()throws IOException,EUnexpectedEof
	{
		int c = read();
		if (c==-1) throw new EUnexpectedEof();
		return (char)c;
	};
	/** Reads XML body character, un-escaping it if necessary.
	This method understands both standard XML escapes declared
	in settings and our custom XML escape.	
	@return if non-negative, this is a directly read character.
				If negative this is an un-escaped character
				computed as:
				<pre>
					return_value = -character -1
					character = -return_value -1
				</pre>
	@throws EUnexpectedEof if reached end of file
	@throws EBrokenFormat if could not decode the sequence.
	*/	
	public int readBodyChar()throws IOException,EUnexpectedEof,EBrokenFormat
	{
		char c = readChar();		
		final char esc =settings.ESCAPE_CHARACTER; 
		final char end_esc = settings.ESCAPE_END_CHARACTER;
		if (c==esc)
		{
			/*	Our own escape.
			
				We are leninent and recognize as an end-of escape:
				- settings.ESCAPE_END_CHARACTER - as aconsumable end consumable
				- any unexpected character as a non-consumable.	
			*/
				char digit = readChar();
				//detect self-escape
				if (digit==esc)
				{
					c= readChar();
					if (c!=end_esc)
					{			
						unread(c);	//put allowed non-escape ends back for processing
					};
					return -esc-1;
				}else
				{
					//variable length hex escape.
					int unescaped = HEX2D(digit);
					if (unescaped==-1) throw new EBrokenFormat("Found "+esc+" escape start but "+digit+" follows instead of 0...F");
					int i=4;
					for(;;)
					{
						digit = readChar();
						int nibble = HEX2D(digit);
						if (nibble==-1)
						{
							if (digit!=end_esc) unread(digit);	//non-consumable end.
							return -unescaped-1;
						}
						{	//and it should be a digit.
							if ((--i)==0) throw new EBrokenFormat("Escape sequence too long");
							unescaped <<= 4;
							unescaped += nibble;
						}
					}
				}
		}else
		if (c=='&')	//standard XML escape.
		{
				String [] escapes  = settings.AMP_XML_ESCAPES;
				int at = 1;
				characters_fetching:
				for(;;)
				{
					c = readChar();
					for(int i=escapes.length;--i>=0;)
					{
						String s = escapes[i];
						if (s.length()<=at) continue;	//too short, can't match.
						if (s.charAt(at)==c)
						{
							//Surely starts with, so either equal or begins with.
							at++;	// next comparison must be with next character.
							if (s.length()==at+1)
							{	
								//found match.
								return -settings.AMP_XML_ESCAPED_CHAR[i]-1;
							}else
								continue characters_fetching;
						}
					}	
					throw new EBrokenFormat("Uknown &xx; escape");
				}
		}else
			return c;
	};
	
	/** Hex to nibble conversion
	@param digit 0...9,a...f,A...F
	@return value 0x0...0x0F or -1 if not hex digit.
	*/
	static int HEX2D(char digit)
	{
		if ((digit>='0')&&(digit<='9')) return digit-'0';
		if ((digit>='a')&&(digit<='f')) return digit-'a'+10;
		if ((digit>='A')&&(digit<='F')) return digit-'A'+10;
		return -1;
	};
	
};