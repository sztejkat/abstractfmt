package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EClosed;
import java.io.*;
import java.util.ArrayList;
/**
	An indicator writer using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>
*/
public abstract class AXMLIndicatorWriteFormat extends AXMLIndicatorWriteFormatBase
{
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
	protected AXMLIndicatorWriteFormat(CXMLSettings settings)
	{
		super(settings);
		this.events_stack = new ArrayList<String>(32);
	};
	
	/* ****************************************************
		
			Services required from subclasses
			
	*****************************************************/
	/** Called in {@link #close} when closed for a first 
	time.
	@throws IOException if failed.
	*/
	protected abstract void closeOnce()throws IOException;
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
			if (isValidUnescapedAttributeValueChar(c))
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
		if ((v==settings.PRIMITIVE_SEPARATOR)||(!isValidUnescapedTextChar(v)))
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
			char d0 = D2HEX(v & 0x0F) ;v>>>=4;	
			char d1 = D2HEX(v);
			write(d1);write(d0);
		};
	};
	@Override public void writeByteBlock(byte v)throws IOException
	{
		startBlockPrimitive();
		int vv = v & 0xFF;
		char d0 = D2HEX(vv & 0x0F); vv>>>=4;
		char d1 = D2HEX(vv);
		write(d1);write(d0);
	};
	@Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		startBlockPrimitive();
		while(length-->0)
		{
			char c = buffer[offset++];
			if (isValidUnescapedTextChar(c))
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
			if (isValidUnescapedTextChar(c))
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
	@Override public void flush()throws IOException
	{
		validateNotClosed();
		flushPendingPrimitiveSeparator();
	};
	
	/** Sets closed status to true.
	If it was false calls {@link #closeOnce}
	@see #validateNotClosed
	*/
	@Override public void close()throws IOException
	{
		if (!is_closed)
		{
			try{
				flush();
			}finally{
					try{
					closeOnce();
				}finally{is_closed = true; };
			}
		};
	};
};