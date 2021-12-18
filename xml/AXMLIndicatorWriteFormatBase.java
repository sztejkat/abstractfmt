package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EClosed;
import java.io.*;
import java.util.ArrayList;
/**
	An indicator writer using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>
*/
public abstract class AXMLIndicatorWriteFormatBase implements IIndicatorWriteFormat
{
				/** XML settings */
				protected final CXMLSettings settings;
				/** Stack used to track opened event elements
				   so that can be correctly closed */
				private final ArrayList<String> events_stack;
				/** Used to optimize writing primitives
				separator. Each primitive write must 
				check if this is pending and if it is
				write primitive separator. At the end it
				should re-set it to pending. Each XML
				element write should clear it without flushing. */ 
				private boolean pending_primitive_separator;				
				/** Set to true once closed */
				private boolean is_closed;
	/* ****************************************************
	
			Creation
	
	
	*****************************************************/
	/** Creates
	@param settings xml settings, non null
	*/
	protected AXMLIndicatorWriteFormatBase(CXMLSettings settings)
	{
		assert(settings!=null);
		this.settings=settings;
		this.events_stack = new ArrayList<String>(32);
	};
	/* ****************************************************
		Services required from subclasses.
	
				Low level I/O
	
	*****************************************************/
	/** Like {@link java.io.Writer#write} or {@link Appendable#append}
	Will be used to write XML content to stream.
	@param c a part of XML content.
	*/
	protected abstract void write(char c)throws IOException;
	/** Like {@link java.io.Writer#write} or {@link Appendable#append}
	Will be used to write XML content to stream.
	@param csq a part of XML content.
	*/
	protected abstract void write(CharSequence csq)throws IOException;
	/** Tests if specified character can be written to output
	without escaping. Depends on charset use in output stream.
	<p>
	Charset must be able to write all characters included in {@link #setup}.
	
	@return true if can be properly encoded by charset, false if must
	be escaped. */
	protected abstract boolean canWrite(char c);
	/* ****************************************************
		
			State
			
	*****************************************************/
	/** Tests if format is usable
	@throws EClosed if closed
	@see #is_closed
	@see #close 
	*/
	protected void validateNotClosed()throws EClosed
	{
		if (is_closed) throw new EClosed();
	};
	
	/* ****************************************************
	
			Characters and elements escaping	
	
	*****************************************************/
	/* ------------------------------------------------------------------
				Escaping
	--------------------------------------------------------------------*/
				/** Binary nibble to hex conversion table */
				private static final char [] HEX= new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
			
	/** Unconditionally escapes <code>character</code>
	as described in  <A href="doc-files/xml-syntax.html">syntax definition</a>
	by writing {@link CXMLSettings#ESCAPE_CHARACTER}, up to four upper case hext digits
	and {@link CXMLSettings#ESCAPE_END_CHARACTER}, or, if <code>character</code>
	is escape char by <code>escape,escape,end_escape;</code>
	@param character what to escape
	@throws IOException if Appendable failed.
	*/
	protected void writeEscapedChar(char character)throws IOException
	{
		final char esc = settings.ESCAPE_CHARACTER;
		write(esc);
		if (character==esc)
		{
			write(esc);
		}else
		{
			char d0 = HEX[character & 0x0F]; character>>>=4;
			char d1 = HEX[character & 0x0F]; character>>>=4;
			char d2 = HEX[character & 0x0F]; character>>>=4;
			char d3 = HEX[character]; 
			if (d3!='0'){ write(d3); write(d2); write(d1); }
			else
			if (d2!='0'){ write(d2); write(d1); }
			else
			if (d1!='0'){ write(d1); }
			write(d0);
		};
		write(settings.ESCAPE_END_CHARACTER);
	};
	/** Tests if specified character can be put into
	the XML stream inside a text representing <code>char[]</code> block
	without escaping.
	<p>
	Default implementation tests againts rules 
	specified in <a href="doc-files/xml-syntax.html#ESCAPED_CHAR_ARRAY">xml syntax definition</a>
	and {@link #canWrite}
	<p>
	Subclasses should also return false if character cannot be encoded with a low
	level stream char-set (ie, ISO-xxx 8 bit code page).
	@param c character
	@return true if character does not need encoding,
			false if it needs to be passed through {@link #writeEscapedChar}
	*/
	protected boolean isValidTextChar(char c)
	{
		//first check if it is an escape char
		if (c==settings.ESCAPE_END_CHARACTER) return false;		
		if (
				((c>=0x20)&&(c<=0xD7FF))
					||
				((c>=0xE000)&&(c<=0xFFFD))
			)
		{
				//this is allowed XML set, but now we need to remove non-recommended.
				if (
						((c>=0x7F)&&(c<=0x84))
							||
						((c>=0x86)&&(c<=0x9F))
							||
						((c>=0xFDD0)&&(c<=0xFDDF))
							||
						((c=='>')||(c=='<')||(c=='&')) //xml elements and entities starts
							||
						(Character.isWhitespace(c))
					)	return false;
				return canWrite(c); //last call because it can be expensive.
		}else
			return false;
	};
	/** Tests if specified character is a valid character which can be put into
	the XML stream inside an attribute value
	<p>
	Default implementation returns true if {@link #isValidTextChar} and not "'
	@param c character to validate
	@return  true if character does not need encoding,
			false if it needs to be passed through {@link #writeEscapedChar}
	*/
	protected boolean isValidAttributeChar(char c)
	{
		if (!isValidTextChar(c)) return false;
		return !((c=='\'')||(c=='\"'));
	};
	/** Tests if specified signal name (as passed to {@link #begin} )
	can be used <u>directly</u> as XML element name
	<a href="doc-files/xml-syntax.html#short_signal_form">in a short form</a>
	or if it must be encoded using 
	<a href="doc-files/xml-syntax.html#long_signal_form">in a long form</a>.
	<p>
	Default implementation tests if XML rules are met and asks {@link CXMLSettings#isDefinedElement}
	if there is no name clash.
	@param signal_name name to check, non null.
	@return true if can use short form
	@see #isReservedElement
	*/
	protected boolean isPossibleXMLElement(String signal_name)
	{
		assert(signal_name!=null);
		final int L = signal_name.length();
		if (L==0) return false; //empty element name is not allowed
		//first char must be a letter.
		char c = signal_name.charAt(0);
		if (!Character.isLetter(c)) return false;
		//all characters must be letters or digits.
		for(int i = L; --i>=1; )
		{
			c = signal_name.charAt(i);
			if (!(Character.isLetter(c) || Character.isDigit(c) || (c=='_') || (c=='.'))) return false;
		};
		//Now protect default xml
		if (L>=3)
		{
			if ( 
				(Character.toUpperCase(signal_name.charAt(0))=='X')
				 &&
				(Character.toUpperCase(signal_name.charAt(1))=='M')
				&&
				(Character.toUpperCase(signal_name.charAt(2))=='L')
				) return false;
		};
		return !settings.isDefinedElement(signal_name);
	};
	
	
	
	/* ****************************************************
	
			IIndicatorWriteFormat	
	
	*****************************************************/
	/* --------------------------------------------------
			Information and escaping
	--------------------------------------------------*/
	@Override public final int getMaxRegistrations(){ return 0; };
	/** Returns {@link #isDescribed} since XML describing element
	must surround primitive from both ends */ 
	@Override public final boolean isFlushing(){ return isDescribed(); };
	/* --------------------------------------------------
			Signals related indicators
	--------------------------------------------------*/
	/** Writes signal name into name attribute value
	@param name name to write, escaping
	@throws IOException if failed 
	*/
	protected void writeNameAttribute(String name)throws IOException
	{
		for(int i=0,n=name.length();i<n;i++)
		{
			char c= name.charAt(i);
			if (isValidAttributeChar(c))
				write(c);
			else
				writeEscapedChar(c);
		};
	};
	/** Invoked when XML element which is handling begin signal
	is written. 
	@param xml_element XML element
	@see #popEvent
	*/
	protected void pushEvent(String xml_element)
	{
		events_stack.add(xml_element);
	};
	/** Invoked when XML element which is handling end signal
	is to be written and needs to know what XML element to close.
	@return XML element to close
	@see #pushEvent
	@throws IllegalStateException if there is no element.
	*/
	protected String popEvent()throws IllegalStateException
	{
		if (events_stack.isEmpty()) throw new IllegalStateException("No active event, nothing to end.");
		return events_stack.remove(events_stack.size()-1);
	};
	@Override public void writeBeginDirect(String signal_name)throws IOException
	{
		assert(signal_name!=null);
		validateNotClosed();
		clearPendingPrimitiveSeparator();
		write('<');
		//check if can be XML signal directly?
		if (isPossibleXMLElement(signal_name))
		{
			write(signal_name);
			pushEvent(signal_name);
			write('>');
		}else
		{
			write(settings.EVENT);
			pushEvent(settings.EVENT);
			write(' ');
			write(settings.SIGNAL_NAME_ATTR);
			write("=\"");
			writeNameAttribute(signal_name);
			write("\">");
		};
	};
	/** Calls {@link writeEnd} followed by {@link #writeBeginDirect} */
	@Override public final void writeEndBeginDirect(String signal_name)throws IOException		
	{
		writeEnd();
		writeBeginDirect(signal_name);
	};
	/** Throws {@link UnsupportedOperationException} */
	@Override public final void writeBeginRegister(String signal_name, int number)throws IOException
	{
		throw new UnsupportedOperationException("XML does not do registration");
	};
	/** Calls {@link writeEnd} followed by {@link #writeBeginRegister} 
	thous, efficiently, throws */
	@Override public final void writeEndBeginRegister(String signal_name, int number)throws IOException		
	{
		writeEnd();
		throw new UnsupportedOperationException("XML does not do registration");
	};
	/** Throws {@link UnsupportedOperationException} */
	@Override public final void writeBeginUse(int number)throws IOException
	{
		throw new UnsupportedOperationException("XML does not do registration");
	};
	/** Calls {@link writeEnd} followed by {@link #writeEndBeginUse} 
	thous, efficiently, throws */
	@Override public final void writeEndBeginUse(int number)throws IOException	
	{
		writeEnd();
		throw new UnsupportedOperationException("XML does not do registration");
	};
	@Override public void writeEnd()throws IOException
	{
		validateNotClosed();
		clearPendingPrimitiveSeparator();
		write("</");
		write(popEvent());	//<-- may throw if no events on stack.
		write('>');
	};
	/* --------------------------------------------------
			Type information
			
		Notes: Since we like to optimize out the primitive
		separator in described streams we can't leave
		writing it to subclass.			
	--------------------------------------------------*/
	@Override public void writeType(TIndicator type)throws IOException
	{
		validateNotClosed();
		if (isDescribed())
		{
			clearPendingPrimitiveSeparator();
			write('<');
			write(settings.elementByIndicator(type));	
			write('>');
		};
	};
	@Override public void writeFlush(TIndicator flush)throws IOException
	{
		validateNotClosed();
		if (isDescribed())
		{
			clearPendingPrimitiveSeparator();
			write("</");
			write(settings.elementByIndicator(flush));	
			write('>');
		}
	};
	
	/* --------------------------------------------------
			IPrimitiveWriteFormat
	--------------------------------------------------*/	
	/*.................................................
			Elementary
	.................................................*/
	/** Called by each XML element write to indicate, that if any primitive
	separator was pending it is no longer needed
	@throws IOException if failed */	
	private void clearPendingPrimitiveSeparator()throws IOException{ pending_primitive_separator=false; };
	/** Called by each primitive write to write pending separator if any
	@throws IOException if failed */
	private void flushPendingPrimitiveSeparator()throws IOException
	{
		if (pending_primitive_separator)
		{
			write(settings.PRIMITIVE_SEPARATOR);
			pending_primitive_separator = false;
		};
	};
	/** Called by each primitive write to indicate that there is a pending separator
	@throws IOException if failed */
	private void setPendingPrimitiveSeparator()throws IOException{ pending_primitive_separator=true; }
	
	private void startElementaryPrimitive()throws IOException
	{
		validateNotClosed();
		flushPendingPrimitiveSeparator();
	};
	private void endElementaryPrimitive()throws IOException
	{
		setPendingPrimitiveSeparator();
	};
	
	@Override public void writeBoolean(boolean v)throws IOException
	{
		startElementaryPrimitive();
		write(v ? 't' : 'f');
		endElementaryPrimitive();
	};
	@Override public void writeByte(byte v)throws IOException
	{
		startElementaryPrimitive();
		write(Byte.toString(v));
		endElementaryPrimitive();
	};
	@Override public void writeChar(char v)throws IOException
	{
		startElementaryPrimitive();
		if ((v==settings.PRIMITIVE_SEPARATOR)||(!isValidTextChar(v)))
		{
			writeEscapedChar(v);
			//now detect if we can optimize out pending sequence terminator.
			if (settings.ESCAPE_END_CHARACTER!=settings.PRIMITIVE_SEPARATOR)
													endElementaryPrimitive();
		}
		else
		{
			write(v);
			endElementaryPrimitive();
		};
		
	};
	@Override public void writeShort(short v)throws IOException
	{
		startElementaryPrimitive();
		write(Short.toString(v));
		endElementaryPrimitive();
	};
	@Override public void writeInt(int v)throws IOException
	{
		startElementaryPrimitive();
		write(Integer.toString(v));
		endElementaryPrimitive();
	};
	@Override public void writeLong(long v)throws IOException
	{
		startElementaryPrimitive();
		write(Long.toString(v));
		endElementaryPrimitive();
	};
	@Override public void writeFloat(float v)throws IOException
	{
		startElementaryPrimitive();
		write(Float.toString(v));
		endElementaryPrimitive();
	};
	@Override public void writeDouble(double v)throws IOException
	{
		startElementaryPrimitive();
		write(Double.toString(v));
		endElementaryPrimitive();
	};
	/*.................................................
			Blocks.
			
		Note: boolean, byte and char blocks do not
		use primitive separator between elements.
		Other blocks just delegate to elementary primitives.
	.................................................*/
	/** Called at start of each primitive block.
	Basically flushes pending primitives separator.
	Notice, there is end of primitive block because it
	is not using any separtor
	@throws IOException if failed */
	private void startBlockPrimitive()throws IOException
	{
		validateNotClosed();
		flushPendingPrimitiveSeparator();
	};
	@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		while(length-->0)
		{
			write(buffer[offset++] ? 't' : 'f');
		};
	};
	@Override public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		while(length-->0)
		{
			//Note: Theoretically >>> is an unsigned shift.
			//		So byte 0xFE >>> 4 should result in 0x0E.
			//		this however does NOT take place
			//		because in equation
			//			v>>>=4 we actually do have an instrict promition to ints:
			//			v = (byte)(((int)v)>>>4)
			//		what makes:
			//				0xFE  -> 0xFFFF_FFFE
			//				>>>4  -> 0x0FFF_FFFF
			//				(byte)-> 0xFF
			//		The automatic java promotion made here is tricky
			//		so it is better to cast it to int and mask int ot be 0x0000_00FE.
			int v = buffer[offset++] & 0xFF;
			char d0 = HEX[v & 0x0F] ;v>>>=4;	
			char d1 = HEX[v];
			write(d1);write(d0);
		};
	};
	@Override public void writeByteBlock(byte v)throws IOException
	{
		startBlockPrimitive();
		int vv = v & 0xFF;
		char d0 = HEX[vv & 0x0F]; vv>>>=4;
		char d1 = HEX[vv];
		write(d1);write(d0);
	};
	@Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		while(length-->0)
		{
			char c = buffer[offset++];
			if (isValidTextChar(c))
				write(c);
			else
				writeEscapedChar(c);
		};
	};
	@Override public void writeCharBlock(CharSequence characters, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		while(length-->0)
		{
			char c = characters.charAt(offset++);
			if (isValidTextChar(c))
				write(c);
			else
				writeEscapedChar(c);
		};
	};
	@Override public void writeShortBlock(short [] buffer,int offset, int length)throws IOException
	{		
		while(length-->0)
		{
			short v = buffer[offset++];
			writeShort(v);
		};
	};
	@Override public void writeIntBlock(int [] buffer,int offset, int length)throws IOException
	{		
		while(length-->0)
		{
			int v = buffer[offset++];
			writeInt(v);
		};
	};
	@Override public void writeLongBlock(long [] buffer,int offset, int length)throws IOException
	{		
		while(length-->0)
		{
			long v = buffer[offset++];
			writeLong(v);
		};
	};
	@Override public void writeFloatBlock(float [] buffer,int offset, int length)throws IOException
	{		
		while(length-->0)
		{
			float v = buffer[offset++];
			writeFloat(v);
		};
	};
	@Override public void writeDoubleBlock(double [] buffer,int offset, int length)throws IOException
	{		
		while(length-->0)
		{
			double v = buffer[offset++];
			writeDouble(v);
		};
	};
	
	/* ****************************************************
	
			Closable & Flushable
	
	*****************************************************/
	/** Flushes pending separator if any */
	public void flush()throws IOException
	{
		validateNotClosed();
		flushPendingPrimitiveSeparator();
	};
	/** Sets closed status to true.
	@see #validateNotClosed
	*/
	public void close()throws IOException
	{
		closed = true;
	};
};