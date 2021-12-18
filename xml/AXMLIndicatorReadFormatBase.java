package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.util.CBoundAppendable;
import sztejkat.abstractfmt.util.CUnbufferingMapper;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import sztejkat.abstractfmt.ECorruptedFormat;
import sztejkat.abstractfmt.EBrokenFormat;
import java.io.*;
import java.util.ArrayList;

/**
	A base reading counterpart for {@link AXMLIndicatorWriteFormatBase}
	using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>.
	<p>
	Basically set of intermediate I/O.
*/
public abstract class AXMLIndicatorReadFormatBase extends AXMLFormat 
												  implements IIndicatorReadFormat
{				
				/*	Designer note:
						Instead of un-reading we could use a buffered
						reader mark-reset capabilities. The standard
						mark-reset has however some significant flaws:
							1.You can't have more than one mark active.
							2.Stream does not know when to stop buffering,
							  since it has problems with guessing when
							  mark is no longer resetable (only due to 
							  read-ahead limit).
							3.There is no peek capabiltity.
						We could craft a multi-mark stream, but unreading
						seemed to be easier.
				*/
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
				
	/** Creates 
	@param settings XML settings to use
	@param unread_buffer_size size of un-read buffer. Must carry longest
			allowed sequence of white-spaces inside a character block,
			longest size of 
	*/
	protected AXMLIndicatorReadFormatBase(
					final CXMLSettings settings,
					final int unread_buffer_size
					)
	{
		super(settings);
		assert(unread_buffer_size>0);
		this.unread = new char[unread_buffer_size];		
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
	/** Allows to override un-read buffer depth. Cannot be 
	invoked when anythig is unread.
	@param characters size of buffer non-zero, positive
	*/
	protected final void setUnreadBufferDepth(int characters)
	{
		assert(characters>0):"characters="+characters;
		assert(unread_at==0):"Can't set name length when processing is in progress.";
		unread = new char[characters];
	};
	/* ------------------------------------------------------------
	
			Direct reading
	
	------------------------------------------------------------*/
	/**
		Reads character either from push-back buffer or
		from stream. Differs from {@link #read} in such a way that it does not throw.
		<p>
		Internally clears indicator cache.
		
		@return read character or -1 if end of stream is reached.
		@throws IOException if low level have failed.
		@see #read
		@see _tryRead
	*/
	protected int tryRead()throws EUnexpectedEof, IOException
	{		
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
	protected char read()throws EUnexpectedEof, IOException
	{
		int r = tryRead();
		assert(r>=-1);
		assert(r<=0x0FFFF);
		if (r==-1) throw new EUnexpectedEof();						
		return (char)r;
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
	/* ------------------------------------------------------------------------
	
				Processing read.
	
	------------------------------------------------------------------------*/
	/** Reads and checks if character is allowed XML token character.	
	@return read character
	@throws EUnexpectedEof if end of file was encounterd
	@throws IOException if low level have failed.
	@throws ECorruptedFormat if encountered non-token character.
	@see #read
	@see #isValidXMLTokenChar
	*/	
	protected char readXMLTokenCharacter()throws EUnexpectedEof, IOException
	{
		final char c = read();
		if (!isValidXMLTokenChar(c)) throw new EBrokenFormat("Invalid XML token character: \""+c+"\"");
		return c;
	};
	
	
	
	/* *************************************************************************
	
	
				Decoding
	
	
	* *************************************************************************/	
	/** Invoked when {@link #readEscaped} encountered &amp; Should process escape or throw
	@return processed char.
	@throws EUnexpectedEof if end of file was encounterd
	@throws IOException if low level have failed.
	@throws EBrokenFormat if could not recognize escape.
	@see #read
	*/
	private char readAmpXMLEscapes()throws EUnexpectedEof, IOException
	{
		String [] escapes = getAMP_XML_ESCAPES();
		int at = 1;
		characters_fetching:
		for(;;)
		{
			char c = read();
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
						return getAMP_XML_ESCAPED_CHAR()[i];
					}else
						continue characters_fetching;
				}
			}	
			throw new EBrokenFormat("Uknown &xx; escape");
		}
	};
	/** Reads character, detects if it is an escape sequence and 
		un-escapes it. This is lenient method
		assuming that end-of-escape character is optional.
		If it is there, it is consumend. If it is not, what
		could be processed is processed and what could not
		is put back to stream.
		
		@return if non-negative, this is a directly read character.
				If negative this is an un-escaped character
				computed as:
				<pre>
					return_value = -character -1
					character = -return_value -1
				</pre>
		@throws IOException if failed at low level or reached end of stream 
				during processing.
		@throws EBrokenFormat if could not process escape.
	*/
	protected int readEscaped()throws IOException
	{
		char c = read();
		
		final char esc =settings.ESCAPE_CHARACTER; 
		final char end_esc = settings.ESCAPE_END_CHARACTER;
		if (c==esc)
		{
			/*	Our own escape.
			
				We are leninent and recognize as an end-of escape:
				- settings.ESCAPE_END_CHARACTER - as aconsumable end consumable
				- any unexpected character as a non-consumable.	
			*/
				char digit = read();
				//detect self-escape
				if (digit==esc)
				{
					c= read();
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
						digit = read();
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
			return -readAmpXMLEscapes()-1;
		else
			return c;
	};
	
	
	
	
};