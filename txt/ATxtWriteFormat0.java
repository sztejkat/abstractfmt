package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;


/**
	A bottom most layer of text based formats providing transformation
	of tokens into primitive data, writing end.
	<p>
	This class is providing token level production inside a payaload
	and is encoding primitives in a way compatible with {@link ATxtReadFormat0}
*/
public abstract class ATxtWriteFormat0 extends ARegisteringStructWriteFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ATxtWriteFormat0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ATxtWriteFormat0.",ATxtWriteFormat0.class) : null;
					 
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates
	@param name_registry_capacity as {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat}
			do specify.
	*/
	protected ATxtWriteFormat0(int name_registry_capacity)
	{
		super(name_registry_capacity);
		if (TRACE) TOUT.println("new ATxtWriteFormat0(name_registry_capacity="+name_registry_capacity+")"); 
	};
	
	/* *****************************************************************
	
			Services required from subclasses	
	
	******************************************************************/
	/* ---------------------------------------------------------------
			Token processing
	---------------------------------------------------------------*/
	/** Will be invoked before {@link #outPlainToken}'d the content of token
	representing an elementary primitivie value or an element of primitive
	sequence, for all values except <code>char</code>,<code>char[]</code>
	or <code>String</code>. In such case the {@link #openStringToken}
	is invoked 
	@throws IOException if failed.
	@see #closePlainToken
	*/
	protected abstract void openPlainToken()throws IOException;
	/** Will be invoked after a content of token opened by {@link #openPlainToken}
	is outed 
	@throws IOException if failed.
	*/
	protected abstract void closePlainToken()throws IOException;
	/** Will be invoked before {@link #outStringToken}'d the content of token
	representing an elementary primitivie value or an element of primitive
	sequence for <code>char</code>,<code>char[]</code>
	or <code>String</code>. For other values the {@link #openPlainToken}
	is invoked.
	<p>
	This is up to subclass to optimize sequence of string tokens into one
	larger token, if necessary. This class will only ensure that each
	call to block char/string write produces one token.
	@throws IOException if failed.
	@see #closeStringToken
	*/
	protected abstract void openStringToken()throws IOException;
	/** Will be invoked after a content of token opened by {@link #openStringToken}
	is outed 
	@throws IOException if failed.
	*/
	protected abstract void closeStringToken()throws IOException;
	/** Writes single character of a plain token.
	<p>
	Invoked when token is open by {@link #openPlainToken}.
	<p>
	This method indirectly assumes, that lower encoding is capable of handling any 
	combination of <code>char</code> values, including unallowed surogate pairs,
	however the standard formatting of plain tokens will not generate any surogates.
	@param c char to write.
	@throws IOException if failed
	*/
	protected abstract void outPlainToken(char c)throws IOException;
	
	/** Writes single character of a string token.
	<p>
	Invoked when token is open by {@link #openStringToken}.
	<p>
	This method assumes, that lower encoding is capable of handling any 
	combination of <code>char</code> values, including unallowed surogate pairs. 
	@param c char to write.
	@throws IOException if failed
	*/	
	protected abstract void outStringToken(char c)throws IOException;
	/* --------------------------------------------------------------------------
				Elementary primitive values.
	--------------------------------------------------------------------------*/
	/** Invokes {@link #outPlainToken} for every charcter
	@param token non-null, but can be empty.
	@throws IOException if failed.
	*/
	protected void outPlainToken(String token)throws IOException
	{
		if (TRACE) TOUT.println("outPlainToken(\""+token+"\") ENTER");
		for(int i=0,n=token.length();i<n;i++)
		{
			outPlainToken(token.charAt(i));
		};
		if (TRACE) TOUT.println("outPlainToken() LEAVE");
	};
	/** Invokes {@link #outStringToken} for every charcter
	@param token non-null, but can be empty.
	@throws IOException if failed.
	*/
	protected void outStringToken(String token)throws IOException
	{
		if (TRACE) TOUT.println("outStringToken(\""+token+"\") ENTER");
		for(int i=0,n=token.length();i<n;i++)
		{
			outStringToken(token.charAt(i));
		};
		if (TRACE) TOUT.println("outStringToken() LEAVE");
	};
	
	/** Called by {@link #writeBooleanImpl} to produce boolean token.
	<p>
	Default implementation returns "true" or "false"
	@param v value 
	@return text representation of v
	*/
	protected String formatBoolean(boolean v){ return v ? "true" : "false"; };
	@Override protected final void writeBooleanImpl(boolean v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatBoolean(v));
		closePlainToken();
	};
	
	/** Called by {@link #writeByteImpl} to produce byte token.
	<p>
	Default implementation returns {@link Byte#toString}
	@param v value 
	@return text representation of v
	*/
	protected String formatByte(byte v){ return Byte.toString(v); };
	@Override protected void writeByteImpl(byte v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatByte(v));
		closePlainToken();
	};
	
	/** Called by {@link #writeCharImpl} to produce char token.
	@param v value 
	@return text representation of v, {@link Character#toString}. 
	*/
	protected String formatChar(char v)
	{
		return Character.toString(v);
	};
	/** Uses {@link #formatChar} */
	@Override protected void writeCharImpl(char v)throws IOException
	{
		openStringToken();
		outStringToken(formatChar(v));
		closeStringToken();
	};
	
	/** Called by {@link #writeShortImpl} to produce short token.
	<p>
	Default implementation returns {@link Short#toString}
	@param v value 
	@return text representation of v
	*/
	protected String formatShort(short v){ return Short.toString(v); };
	@Override protected void writeShortImpl(short v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatShort(v));
		closePlainToken();
	};
	
	/** Called by {@link #writeIntImpl} to produce int token.
	<p>
	Default implementation returns {@link Integer#toString}
	@param v value 
	@return text representation of v
	*/
	protected String formatInt(int v){ return Integer.toString(v); };
	@Override protected void writeIntImpl(int v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatInt(v));
		closePlainToken();
	};
	
	/** Called by {@link #writeLongImpl} to produce long token.
	<p>
	Default implementation returns {@link Long#toString}
	@param v value 
	@return text representation of v
	*/
	protected String formatLong(long v){ return Long.toString(v); };
	@Override protected void writeLongImpl(long v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatLong(v));
		closePlainToken();
	};
	
	/** Called by {@link #writeFloatImpl} to produce float token.
	<p>
	Default implementation returns {@link Float#toString}
	@param v value 
	@return text representation of v
	*/
	protected String formatFloat(float v){ return Float.toString(v); };
	@Override protected void writeFloatImpl(float v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatFloat(v));
		closePlainToken();
	};
	
	/** Called by {@link #writeDoubleImpl} to produce double token.
	<p>
	Default implementation returns {@link Double#toString}
	@param v value 
	@return text representation of v
	*/
	protected String formatDouble(double v){ return Double.toString(v); };
	@Override protected void writeDoubleImpl(double v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatDouble(v));
		closePlainToken();
	};
	
	/* ------------------------------------------------------------------
				Datablock related.
	------------------------------------------------------------------*/
	/** Called by {@link #writeBooleanBlockImpl} to produce boolean token.
	<p>
	Default implementation returns {@link #formatBoolean}
	@param v value 
	@return text representation of v
	*/
	protected String formatBooleanBlock(boolean v){ return formatBoolean(v); };
	@Override protected final void writeBooleanBlockImpl(boolean v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatBooleanBlock(v));
		closePlainToken();
	};
	
	/** Called by {@link #writeByteBlockImpl} to produce byte token.
	<p>
	Default implementation returns {@link #formatByte}
	@param v value 
	@return text representation of v
	*/
	protected String formatByteBlock(byte v){ return formatByte(v); };
	@Override protected final void writeByteBlockImpl(byte v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatByteBlock(v));
		closePlainToken();
	};		
	
	/** Called by {@link #writeShortBlockImpl} to produce short token.
	<p>
	Default implementation returns {@link #formatShort}
	@param v value 
	@return text representation of v
	*/
	protected String formatShortBlock(short v){ return formatShort(v); };
	@Override protected final void writeShortBlockImpl(short v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatShortBlock(v));
		closePlainToken();
	};
	
	
	/** Called by {@link #writeCharBlockImpl} to produce char token.
	<p>
	Default implementation returns {@link #formatChar}
	@param v value 
	@return text representation of v
	*/	
	protected String formatCharBlock(char v){ return formatChar(v); };
	/** Encloses entire operation in one {@link #openStringToken}/{@link #closeStringToken}
	*/
	@Override protected void writeCharBlockImpl(char [] buffer, int offset, int length)throws IOException
	{
		openStringToken();
			while(length--!=0)
			{
				outStringToken(formatCharBlock(buffer[offset++]));
			};
		closeStringToken();
	};
	@Override protected final void writeCharBlockImpl(char v)throws IOException
	{
		openStringToken();
		outStringToken(formatCharBlock(v));
		closeStringToken();
	};
	
	
	/** Called by {@link #writeIntBlockImpl} to produce int token.
	<p>
	Default implementation returns {@link formatInt}
	@param v value 
	@return text representation of v
	*/
	protected String formatIntBlock(int v){ return formatInt(v); };
	@Override protected final void writeIntBlockImpl(int v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatIntBlock(v));
		closePlainToken();
	};
	
	
	/** Called by {@link #writeLongBlockImpl} to produce long token.
	<p>
	Default implementation returns {@link formatLong}
	@param v value 
	@return text representation of v
	*/
	protected String formatLongBlock(long v){ return formatLong(v); };
	@Override protected final void writeLongBlockImpl(long v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatLongBlock(v));
		closePlainToken();
	};
	
	
	
	/** Called by {@link #writeFloatBlockImpl} to produce float token.
	<p>
	Default implementation returns {@link formatFloat}
	@param v value 
	@return text representation of v
	*/
	protected String formatFloatBlock(float v){ return formatFloat(v); };
	@Override protected final void writeFloatBlockImpl(float v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatFloatBlock(v));
		closePlainToken();
	};
	
	/** Called by {@link #writeDoubleBlockImpl} to produce double token.
	<p>
	Default implementation returns {@link formatDouble}
	@param v value 
	@return text representation of v
	*/
	protected String formatDoubleBlock(double v){ return formatDouble(v); };
	@Override protected final void writeDoubleBlockImpl(double v)throws IOException
	{
		openPlainToken();
		outPlainToken(formatDoubleBlock(v));
		closePlainToken();
	};
	
	
	/** Called by {@link #writeStringImpl} to produce string token.
	<p>
	Default implementation returns {@link #formatCharBlock}
	@param v value 
	@return text representation of v
	*/	
	protected String formatStringBlock(char v){ return formatCharBlock(v); };
	/** Encloses entire operation in one {@link #openStringToken}/{@link #closeStringToken}
	*/
	@Override protected void writeStringImpl(CharSequence characters, int offset, int length)throws IOException
	{
		openStringToken();
			while(length--!=0)
			{
				outStringToken(formatStringBlock(characters.charAt(offset++)));
			};
		closeStringToken();
	};
	@Override protected final void writeStringImpl(char v)throws IOException
	{
		openStringToken();
		outStringToken(formatStringBlock(v));
		closeStringToken();
	};
};