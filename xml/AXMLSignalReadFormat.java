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
				/** Used to buffer data for XML elements to process.
				The upper bound of this appendable is set to 
				the max of known XML elements and max name length
				*/
				private final CBoundAppendable xml_element_buffer;
				/* ****************************************************
						State
				* ***************************************************/
				/** Indidates that we are in space when whitespaces
				and eols are void */
				private static byte STATE_NONE = 0;
				/** Indicates that we are inside a character block
				and no character is "void". */
				private static byte STATE_CHARACTER_BLOCK = (byte)1;
				private byte state;
	
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
		protected char unescape()throws IOException;
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
				char unescaped = HEX(digit);
				int i=4;
				for(;;)
				{
					digit = read();
					if (digit==ESCAPE_CHAR) return unescaped;
					else
					{
						if ((--i)==0) throw new ECorruptedFormat("Escape sequence too long");
						unescaped <<= 4;
						unescaped += HEX(digit);
					}
				}
			};
		};
		
		/* ********************************************************
		
				Core services required by superclass		
		
		**********************************************************/
		@Override protected int readIndicator()throws IOException
		{
			//Now we need to check what is under cursor.
			//If it is a tag marker we need to process it.
			//The trickier part is when it is NOT a tag.
			//We must correctly detect if we are inside a data
			//or not. In all data except of char blocks
			//whitespaces and eols are ignorable and we must chew them.
			if (isEof()) return EOF_INDICATOR;
			if (state!=STATE_CHARACTER_BLOCK)
			{
				//we are in plain data block, when we should skip
				//white spaces.
				char c = read();
				while(
						Character.isWhitespace(c)//Note: This test covers EOL and page feeds too.
					)
					{
						//skip it ang go for next.
						if (isEof()) return EOF_INDICATOR;
						c = read();
					};
				//this is a non-space, put it back
				unread(c);
			};
			//Now we either skipped spaces or should not skip them.
			//Anyway char under cursor describes next stream element.
			char c = read();
			if (c=='<') 
			{	
				//detected tag. Let us chew on it.
				//Note: The buffer will take care of testing for maximum XML element size
				//Note: During space skipping and first fetches we tested for eof.
				//	    Once however element is detected we no longer test 
				//		and throw EUnexepectedEof instread. Doing it otherwise
				//		would require un-reading the buffer, what is inconvinient
				//		and does not give significant benefits if thanks to
				//		that we will re-try reading after a some time.
				//		This is simply bad to start supplying XML content and then
				//		pause for so long that EOF is returned.
				xml_element_buffer.reset();
				for(;;)
				{
					c = read();
					if (Character.isWhitespace(c))
					{
						//element name is terminated, attribute may follow
						//or it may be just nothing.
						for(;;)
						{
						};
						
					}else
					if (c=='>')
					{
						//element name is terminated, no attribute follows.
						
					}else
					{
						//this is a part of element name.
						//We are lenient so we accept any character here
						xml_element_buffer.append(c);
					};
					........... todo............
				};
					
			}else
			{
				unread(c);	//put it back, we can't move cursor.
				return NO_INDICATOR;
			};
		};
		/* ******************************************************
		
					Stream access.				
					
				Basically a simplified push-back reader.
		
		* *******************************************************/
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
				final int r = readImpl();
				if (r==-1) return true;
				assert(r>=-1);
				assert(r<=0x0FFFF);
				unread((char)r);
				return false;
			}else
				return false;
		};
		/**
			Reads character either from push-back buffer or
			from stream.
			@return read character
			@throws EUnexpectedEof if end of file was encounterd
			@throws IOException if low level have failed.
			
			@see #readEscaped
		*/
		protected final char read()throws EUnexpectedEof, IOException
		{
			int i =unread_at;
			if (i==0)
			{
				final int r = readImpl();
				if (r==-1) throw new EUnexpectedEof();
				assert(r>=-1);
				assert(r<=0x0FFFF);
				unread (char)r;
			}else
			{
				final char c = undread[i];
				i--;
				this.unread_at=i;
				return c;
			};
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
			@throws EBrokenStream if could not un-read character 
					because there is no place in buffer. It usually means,
					that some stream structure is too large.
		*/
		protected final void unread(char c)throws EBrokenStream
		{
			int i =unread_at;
			final char [] u = unread; 
			if (i>=u.length) throw new EBrokenStream("Can't un-read so many characters");
			u[i]=c;
			i++;
			this.undread_at = i;
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
};
