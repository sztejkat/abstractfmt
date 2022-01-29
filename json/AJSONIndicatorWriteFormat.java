package sztejkat.abstractfmt.json;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import static sztejkat.abstractfmt.util.SHex.D2HEX;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.Writer;

/**
	Common denominator for JSON write formats. Technically speaking it
	is a "bare" format.
	<p>
	See <a href="doc-files/json-syntax.html">syntax description</a>.
	<p>
	This class is writing a compact, non-human friendly version of JSON.
*/
abstract class AJSONIndicatorWriteFormat extends AJSONFormat implements IIndicatorWriteFormat
{				
				/** An output, which is escaping. */
				protected final CJSONEscapingWriter output;
				/** True if , needs to be written in front of next
				JSON value.
				<p>
				This value is re-set to false after each begin
				signal is written (that is once <code>"content":[</code>
				of JSON is started) and set to true after each primitive element
				or end signal. 
				@see #writeSeparator				
				*/
				private boolean needs_value_separator;
	/** Creates
	@param output output to which write.
	@param charset optional charset which will be used to detect
		characters which can't be correctly encoded by output.
		If this value is null this format assumes that <code>out</code>
		can encode a full <code>char</code> space.
	@param settings JSON settings, non null. 
	*/			
	 protected AJSONIndicatorWriteFormat(
								 		  Writer output,
										  Charset charset,
										  CJSONSettings settings
										  )
	 {
	    super(settings);
	  	assert(output!=null);
		this.output = new CJSONEscapingWriter(output,charset);
	 };
	 /* *******************************************************************
	 
	 			Intermediate JSON support.
	 
	 
	 ********************************************************************/
	 /** Writes , if needed between values and sets {@link #needs_value_separator}
	 to true. Will be called in each place where possibly a value separator
	 may happen.
	 <p>
	 A subclass running in "human friendly" mode may use it to inject
	 some separators.
	 */
	 protected void writeSeparator()throws IOException
	 {
	 	if (needs_value_separator) output.write(',');
	 	needs_value_separator =true;
	 };
	 /** Writes <a href="doc-files/json-syntax.html#LONG_BEGIN">long form</a>
	 of begin signal
	 @param signal_name see {@link #writeBeginDirect}
	 */
	 protected void writeLongFormBeginDirect(String signal_name)throws IOException
	 {
	 	output.write("{\"");					//opening sequence
	 	output.writeString(settings.BEGIN);		//escaped begin.
	 	output.write("\":\"");
	 	output.writeString(signal_name);		//escaped name
	 	output.write("\",\"");
	 	output.writeString(settings.CONTENT);	//escaped content block start.
	 	output.write("\":[");					//begin of content array
	 }
	 /** Writes <a href="doc-files/json-syntax.html#SHORT_BEGIN">short form</a>
	 of begin signal
	 @param signal_name see {@link #writeBeginDirect}
	 */
	 protected void writeShortFormBeginDirect(String signal_name)throws IOException
	 {
	 	output.write("{\"");				//opening sequence
	 	output.writeString(signal_name);	//escaped name.
	 	output.write("\":[");				//begin of content array
	 }
	 /* ******************************************************************
	 
	 			IIndicatorWriteFormat
	 
	 
	 ********************************************************************/
	 /* =================================================================
	 			Information and settings.
	 ===================================================================*/
	 /** Always zero, this format does not support registration */
	 @Override final public int getMaxRegistrations(){ return 0; };
	 /** True if described, otherwise false */
	 @Override final public boolean isFlushing(){ return isDescribed(); };
	 /** Integer.MAX_VALUE */
	 @Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	 /* =================================================================
	 			Signals related indicators.
	 ===================================================================*/		 
	 /** Selects either long or short form
	 @see #writeLongFormBeginDirect
	 @see #writeShortFormBeginDirect
	 */
	 @Override public void writeBeginDirect(String signal_name)throws IOException
	 {
	 	//We need to decide if we can use short or long form.
		writeSeparator(); 	
		needs_value_separator = false; //so that first element does not write separator.
	 	if (settings.isAllowedSignalName(signal_name,isDescribed()))
	 	{
	 		writeShortFormBeginDirect(signal_name);
	 	}else
	 	{
	 		writeLongFormBeginDirect(signal_name);
	 	};
	 };
	 /** Always as end followed by begin. No end-begin optimization. */
	 @Override final public void writeEndBeginDirect(String signal_name)throws IOException
	 {
	 	writeEnd();
	 	writeBeginDirect(signal_name);
	 };
	 /** Throws {@link UnsupportedOperationException} */
	 @Override public final void writeBeginRegister(String signal_name, int number)throws IOException
	 {
	 	throw new UnsupportedOperationException("JSON does not do registration");
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
	 }
	 
	 /** Writes as <a href="doc-files/json-syntax.html#END">end</a> specifies
	 and toggles {@link #needs_value_separator} to true. */
	 @Override public void writeEnd()throws IOException
	 {
	 	needs_value_separator = true;
	 	output.write("]}");	 	
	 };
	 
	 
	 /* =================================================================
	 			Type related indicators.
	 ===================================================================*/
	 /** 
	 As a house-keeping calls {@link #writeSeparator}
	 and opens block.
	 <p>
	 Then acts depending on {@link #isDescribed}
	 writing as <a href="doc-files/json-syntax.html#TYPE">end</a> specifies
	 and clearing {@link #needs_value_separator}
	 */
	 @Override public void writeType(TIndicator type)throws IOException
	 {
	 	//always inject separator if necessary.
	    writeSeparator();
	    //Act on described mode.
	 	if (isDescribed())
	 	{
	 		output.write("{\"");
	 		output.writeString(settings.getTypeString(type));
	 		output.write("\":");
	 		needs_value_separator = false;
	 	};
	 	//Opening arrays can be handled here because this method
	 	//is always called.
	 	if ((type.FLAGS & TIndicator.BLOCK)!=0)
	 	{
	 		//now we have a choice between [ and "
	 		if ((type==TIndicator.TYPE_BYTE_BLOCK)||(type==TIndicator.TYPE_CHAR_BLOCK))
	 			output.write('\"');
	 		else
	 			output.write('[');
	 	};
	 };
	
	 /**
	 As house-keeping  closes block and then acts depending on {@link #isFlushing}
	 writing as <a href="doc-files/json-syntax.html#FLUSH">end</a> specifies
	 and setting {@link #needs_value_separator} to true.
	  */
	 @Override public void writeFlush(TIndicator flush)throws IOException
	 {
	 	//Housekeeping first.	 	
	 	if ((flush.FLAGS & TIndicator.BLOCK)!=0)
	 	{
	 		//now we have a choice between [ and "
	 		if ((flush==TIndicator.FLUSH_BYTE_BLOCK)||(flush==TIndicator.FLUSH_CHAR_BLOCK))
	 			output.write('\"');
	 		else
	 			output.write(']');
	 	};
	 	//Act on described mode.
	 	if (isFlushing())
	 	{
	 		needs_value_separator = true;
	 		output.write('}');
	 	};
	 };
	 /* =================================================================
	 			IPrimitiveWriteFormat
	 ===================================================================*/
	 /* ----------------------------------------------------------------
	 			Elementary
	 ----------------------------------------------------------------*/
	 @Override public void writeBoolean(boolean v)throws IOException
	 {
	 	 output.write(v ? "true" : "false" );
	 };
	 @Override public void writeByte(byte v)throws IOException
	 {
	 	 output.write(Byte.toString(v));
	 };
	 @Override public void writeChar(char v)throws IOException
	 {
	 	output.write('\"');
	 	output.writeString(v);
	 	output.write('\"');
	 };
	 @Override public void writeShort(short v)throws IOException
	 {
	 	output.write(Short.toString(v));
	 }; 
	 @Override public void writeInt(int v)throws IOException
	 {
	 	output.write(Integer.toString(v));
	 };
	 @Override public void writeLong(long v)throws IOException
	 {
	 	output.write(Long.toString(v));
	 };
	 @Override public void writeFloat(float v)throws IOException
	 {
	 	output.write(Float.isFinite(v) ? Float.toString(v) : "\""+Float.toString(v)+"\"");
	 };
	 @Override public void writeDouble(double v)throws IOException
	 {
	 	output.write(Double.isFinite(v) ? Double.toString(v) : "\""+Double.toString(v)+"\"");
	 };
	 /* ----------------------------------------------------------------
	 			Blocks.
	 			
	 	Note: In JSON we handle blocks as sequences of regular writes
	 	except of char[] and byte[]. The opening and closing [ ] or ""
	 	is handled by writeType and writeFlush so we just need to
	 	use writeSeparator() between elementary items.
	 ----------------------------------------------------------------*/
	 @Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	 {
	 	while(length-->0)
	 	{
	 		writeSeparator();
	 		writeBoolean(buffer[offset++]);
	 	}	
	 };	
	 /** Writes in a string form as <a href="doc-files/json-syntax.html#BYTEBLOCK">do specify</a> */
	 @Override public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
	 {
	 	while(length-->0)
	 	{	 		
	 		writeByteBlock(buffer[offset++]);
	 	}	
	 };		
	 @Override public void writeByteBlock(byte data)throws IOException
	 {
	 		output.write(D2HEX( (data >>4 ) & 0xF));
	 		output.write(D2HEX( data & 0xF));
	 };
	 
	 @Override public void writeCharBlock(CharSequence characters, int offset, int length)throws IOException
	 {
	 	 	output.writeString(characters,offset,length);
	 };		
		
	 @Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
	 {
	 		output.writeString(buffer,offset,length);
	 };
	 
	 @Override public void writeShortBlock(short [] buffer, int offset, int length)throws IOException
	 {
	 	while(length-->0)
	 	{
	 		writeSeparator();
	 		writeShort(buffer[offset++]);
	 	}	
	 };	
	 
	 @Override public void writeIntBlock(int [] buffer, int offset, int length)throws IOException
	 {
	 	while(length-->0)
	 	{
	 		writeSeparator();
	 		writeInt(buffer[offset++]);
	 	}	
	 };
	 @Override public void writeLongBlock(long [] buffer, int offset, int length)throws IOException
	 {
	 	while(length-->0)
	 	{
	 		writeSeparator();
	 		writeLong(buffer[offset++]);
	 	}	
	 };
	 @Override public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException
	 {
	 	while(length-->0)
	 	{
	 		writeSeparator();
	 		writeFloat(buffer[offset++]);
	 	}	
	 };
	 @Override public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException
	 {
	 	while(length-->0)
	 	{
	 		writeSeparator();
	 		writeDouble(buffer[offset++]);
	 	}	
	 };
	 /* ----------------------------------------------------------------
	 			Flushable
	 ----------------------------------------------------------------*/
	 @Override public void flush()throws IOException
	 {
	 	output.flush();
	 };
	 /* ----------------------------------------------------------------
	 			Closable
	 ----------------------------------------------------------------*/
	 @Override public void close()throws IOException
	 {
	 	output.close();
	 };
};