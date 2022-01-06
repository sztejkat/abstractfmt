package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.util.CAdaptivePushBackReader;
import sztejkat.abstractfmt.util.CBoundAppendable;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EDataMissmatch;
import sztejkat.abstractfmt.EUnexpectedEof;
import java.io.IOException;
import java.io.Reader;

/**
	XML reader with characters decoding capaibilities.
	<p>
	NOT thread safe.
*/
class CDecodingXMLReader extends CAdaptivePushBackReader
{	
				private final CXMLSettings settings;
				private final CBoundAppendable escape_completion_buffer;
	/** 
	Creates	
	@param in source to read from
	@param initial_size intial buffer capacity
	@param size_increment buffer growth increment.
	@param settings settings which will be used to decode special characters in {@link readBodyChar}.
	*/
	public CDecodingXMLReader(Reader in, int initial_size,int size_increment, CXMLSettings settings)
	{
		super(in, initial_size, size_increment);
		assert(settings!=null);
		this.settings=settings;
		this.escape_completion_buffer= new CBoundAppendable(settings.getMaxAmpEscapeLength());
	};
	/* -------------------------------------------------------------------
	
		Additional API related to plain reading.
	
	-------------------------------------------------------------------*/
	/** Returns character which would be read next, without reading it.
	Technically speaking either checks a push-back buffer or reads
	from stream and pushes result back.
	@return -1 if eof, otherwise 0....0xffff
	@throws IOException if failed to peek into a stream
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
	@throws IOException if failed to peek into a stream
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
	
	
	/** This method behaves exactly as {@link #readBodyChar}
	but instead of throwing {@link EDataMissmatch}
	throws {@link EBrokenFormat}.
	<p>
	This method is to be used inside attribute value reads since it throws
	a correct, un-recoverable exception on deconding problems.
	@return --//--
	@throws EUnexpectedEof if reached end of file
	@throws EBrokenFormat if could not decode the sequence.
	@throws IOException if failed at low level.
	@see #readBodyCharImpl
	*/
	public int readEncodedChar()throws IOException,EUnexpectedEof,EBrokenFormat
	{
		try{
			return readBodyCharImpl();
		}catch(ECouldNotDecodeChar ex){ throw new EBrokenFormat(ex);}
	};
	/** Reads XML body character, un-escaping it if necessary.
	This method understands both standard XML escapes declared
	in settings and our custom XML escape.	
	<p>
	This method is to be used inside primitive reads since it throws
	a correct, recoverable exception on deconding problems.
	
	@return if non-negative, this is a directly read character.
				If negative this is an un-escaped character
				computed as:
				<pre>
					return_value = -character -1
					character = -return_value -1
				</pre>
	@throws EUnexpectedEof if reached end of file
	@throws EDataMissmatch if could not decode the sequence.
	@throws IOException if failed at low level.
	@see #readBodyCharImpl
	*/	
	public int readBodyChar()throws IOException,EUnexpectedEof,EDataMissmatch
	{
		try{
			return readBodyCharImpl();
		}catch(ECouldNotDecodeChar ex){ throw new EDataMissmatch(ex);}
	};
			
	protected int readBodyCharImpl()throws IOException,EUnexpectedEof,ECouldNotDecodeChar
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
					if (unescaped==-1) throw new ECouldNotDecodeChar("Found "+esc+" escape start but "+digit+" follows instead of 0...F");
					int i=4;
					for(;;)
					{
						digit = readChar();
						int nibble = HEX2D(digit);
						if (nibble==-1)
						{
							if (digit!=end_esc)
							{
								//Non-consumable end can be only the < 
								if (digit!='<')  throw new ECouldNotDecodeChar("Found in character escape sequence "+digit+" but either "+end_esc+" or < are expected");
								unread(digit);	//non-consumable end.
							};
							return -unescaped-1;
						}
						{	//and it should be a digit.
							if ((--i)==0) throw new ECouldNotDecodeChar("Escape sequence too long");
							unescaped <<= 4;
							unescaped += nibble;
						}
					}
				}
		}else
		if (c=='&')	//standard XML escape.
		{
				String [] escapes  = settings.AMP_XML_ESCAPES;
				escape_completion_buffer.reset();
				escape_completion_buffer.append('&');
				characters_fetching:
				for(;;)
				{
					c = readChar();
					escape_completion_buffer.append(c);
					for(int i=escapes.length;--i>=0;)
					{
						String s = escapes[i];
						switch(escape_completion_buffer.isStartOf(s))
						{
							case -1:	break;//does not.
							case  0: //starts, but not equals.
							 	//Note: theoretically this is incorrect, if
							 	//we would compare generic strings. But in this
							 	//specific case we compare strings which ends with
							 	//a very specific terminator (;) so it is fine.
							 	continue characters_fetching;
							 case 1: //equals
							 	return -settings.AMP_XML_ESCAPED_CHAR[i]-1;
							 default: throw new AssertionError();
						}
					}	
					//None did capture partial match
					throw new ECouldNotDecodeChar("The "+escape_completion_buffer+" does not match any known &xx; escape");
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