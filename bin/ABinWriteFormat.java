package sztejkat.abstractfmt.bin;
import  sztejkat.abstractfmt.logging.SLogging;
import  sztejkat.abstractfmt.*;
import java.io.IOException;
/**

	A common base for binary formats, especially chunk and escape.
	<p>
	This class provides content encoding around a dummy
	abstract {@link #out} method which is expected to write a byte
	to binary "payload" of a signal.
	<p>
	The encoding method is described in <code>sztejkat.abstractfmt.bin.chunk</code>
	format specification and is common for most of provided binary implementations.
*/
public abstract class ABinWriteFormat extends ARegisteringStructWriteFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ABinWriteFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ABinWriteFormat.",ABinWriteFormat.class) : null;
	
	/* *****************************************************************************
	
	
			Construction
			
	
	
	* *****************************************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
			This value cannot be larger than 128. Recommended value is 128, minimum resonable is 8.
	*/
	public ABinWriteFormat(int name_registry_capacity)
	{
		super(name_registry_capacity);
	};
	/* *****************************************************************************
	
	
			Services absolutely required from subclasses.
			
	
	
	* *****************************************************************************/
	/** Writes a payload byte to signal payload executing all necessary
	payload management (ie. chunk continuation or escaping) transparently
	@param b what to write
	@throws IOException if failed */
	protected abstract void out(byte b)throws IOException;
	
	/**
		Encodes string, as specs are saying.
		Uses {@link #encodeStringCharacter}
		@param s text to encode
		@param at from where
		@param length how many
		@throws IOException if failed.
	*/
	/* *****************************************************************************
	
			String encoding			
	
	* *****************************************************************************/
	/** Encodes string using {@link #encodeStringCharacter}
	@param s non null text to encode
	@param at where to start taking chars from s
	@param length how many chars to encode
	@throws IOException .
	*/
	protected void encodeString(CharSequence s, int at, int length)throws IOException
	{
		assert(s!=null);
		assert(at>=0);
		assert(length>=0);
		assert(at+length<=s.length());
		if (TRACE) TOUT.println("encodeString("+s+",at="+at+",length="+length+") ENTER");
		for(int i=at;i<length;i++)
		{
			encodeStringCharacter(s.charAt(i));
		};
		if (TRACE) TOUT.println("encodeString() LEAVE");
	};
	/** Encodes string using {@link #encodeString(CharSequence,int,int)}
	@param s non null text to encode
	@throws IOException .
	*/
	protected final void encodeString(CharSequence s)throws IOException
	{
		assert(s!=null);
		encodeString(s,0,s.length());
	};
	/**
		Encodes string character, as chunk format are saying.
		 
		@param c text to encode
		@throws IOException if failed.
		@see #out
	*/
	protected void encodeStringCharacter(char c)throws IOException
	{		
		if (DUMP) TOUT.println("encodeStringCharacter("+Integer.toHexString(c)+") ENTER");
		char z = (char)(c>>>7);
		if (z==0)
		{
			out((byte)c);
		}else
		{ 
			out((byte)((c & 0x7F) | 0x80));
			c = (char)(c>>>7);
			z = (char)(z>>>7);
			if (z==0)
			{
				out((byte)c);
			}else
			{
				out((byte)((c & 0x7F) | 0x80));
				c = (char)(c>>>7);
				out((byte)c);
			};
		};
		if (DUMP) TOUT.println("encodeStringCharacter("+Integer.toHexString(c)+") LEAVE");
	}
	/* *****************************************************************************
	
			Services required by ARegisteringStructWriteFormat
	
	******************************************************************************/
	/* ------------------------------------------------------------------
				State related.
	---------------------------------------------------------------------*/
	/** Overriden to eventually flush boolean block in progress */
	@Override protected void flushImpl()throws IOException
	{
		if (TRACE) TOUT.println("flushImpl ENTER");
		if (bitpos_in_boolean_block!=0)
		{
			flushBooleanBlockBuffer();
		};
		if (TRACE) TOUT.println("flushImpl LEAVE");
	};
	/* ------------------------------------------------------------------
				Primitive related, elementary
	------------------------------------------------------------------*/
	@Override protected void writeBooleanImpl(boolean v)throws IOException
	{
		out( v ? (byte)1 : (byte)0);
	};
	@Override protected void writeByteImpl(byte v)throws IOException
	{
		out( v );
	};
	@Override protected void writeCharImpl(char v)throws IOException
	{
		out( (byte)v);
		out( (byte)(v>>>8));
	};
	@Override protected void writeShortImpl(short v)throws IOException
	{
		out( (byte)v);
		out( (byte)(v>>>8));
	};
	@Override protected void writeIntImpl(int v)throws IOException
	{
		out( (byte)v); v>>>=8;
		out( (byte)v); v>>>=8;
		out( (byte)v); v>>>=8;
		out( (byte)v);
	};
	@Override protected void writeLongImpl(long v)throws IOException
	{
		out( (byte)v); v>>>=8;
		out( (byte)v); v>>>=8;
		out( (byte)v); v>>>=8;
		out( (byte)v); v>>>=8;
		out( (byte)v); v>>>=8;
		out( (byte)v); v>>>=8;
		out( (byte)v); v>>>=8;
		out( (byte)v);
	};
	@Override protected void writeFloatImpl(float v)throws IOException
	{
		writeIntImpl(Float.floatToRawIntBits(v));
	};
	@Override protected void writeDoubleImpl(double v)throws IOException
	{
		writeLongImpl(Double.doubleToRawLongBits(v));
	};
	/* ------------------------------------------------------------------
			Datablock related.
	------------------------------------------------------------------*/
	/* .............................................................
				boolean block needs a bit of tricks.
	...............................................................*/
			static final int BIT_BLOCK_SIZE_BITS = 256; 
			/** Bit-buffer for boolean block, lazy initialized*/
			private byte [] boolean_block_buffer;
			/** Cursor in bit buffer, in bits */
			private int     bitpos_in_boolean_block;
	/** Overriden to initiate boolean block buffer */
	@Override protected void startBooleanBlock()throws IOException
	{
		if (TRACE) TOUT.println("startBooleanBlock ENTER");
		super.startBooleanBlock();
		if (boolean_block_buffer==null)
				 boolean_block_buffer = new byte[BIT_BLOCK_SIZE_BITS/8];
		bitpos_in_boolean_block = 0;
		if (TRACE) TOUT.println("startBooleanBlock LEAVE");
	};
	@Override protected void writeBooleanBlockImpl(boolean v)throws IOException
	{		
		if (TRACE) TOUT.println("writeBooleanBlockImpl("+v+") ENTER");
		if (bitpos_in_boolean_block==BIT_BLOCK_SIZE_BITS)
		{
			flushBooleanBlockBuffer();
			assert(bitpos_in_boolean_block==0);
		};
		//Wipe byte, if just start writing to it. This will ensure zero bits
		//on un-used space at lowest possible expense.
		int bi = bitpos_in_boolean_block % 8;
		int ba = bitpos_in_boolean_block/8;
		if (bi==0) boolean_block_buffer[ba]=(byte)0;
		if (v)
		{
			boolean_block_buffer[ba] |= (1<<(bi));
		}else
		{
			boolean_block_buffer[ba] &= ~(1<<(bi));
		}
		bitpos_in_boolean_block++;
		if (TRACE) TOUT.println("writeBooleanBlockImpl("+v+") LEAVE");
	};
	private void flushBooleanBlockBuffer()throws IOException
	{
		if (TRACE) TOUT.println("flushBooleanBlockBuffer ENTER");
		if (bitpos_in_boolean_block!=0)
		{
			if (TRACE) TOUT.println("flushBooleanBlockBuffer, flushing bitpos_in_boolean_block="+bitpos_in_boolean_block);
			//empty blocks are NOT written.
			assert(bitpos_in_boolean_block<=256);
			out((byte)(bitpos_in_boolean_block-1));//number of bits - 1
			//now compute the number of bytes
			int b = (bitpos_in_boolean_block-1)/8+1;
			//write them to chunk payload.
			for(int i=0;i<b;i++) out(boolean_block_buffer[i]);			
			//and reset
			bitpos_in_boolean_block = 0;
		};
		if (TRACE) TOUT.println("flushBooleanBlockBuffer LEAVE");
	};
	/** Overriden to flush boolean block buffer */
	@Override protected void endBooleanBlock()throws IOException
	{
		if (TRACE) TOUT.println("endBooleanBlock() ENTER");
		flushBooleanBlockBuffer();
		super.endBooleanBlock();
		if (TRACE) TOUT.println("endBooleanBlock() LEAVE");
	};
	/* .............................................................
				String
	...............................................................*/
	@Override protected void writeStringImpl(char c)throws IOException
	{
		encodeStringCharacter(c);
	};
	/* .............................................................
				other blocks are stright forwards to elementary ops.
	...............................................................*/
	@Override protected void writeByteBlockImpl(byte v)throws IOException
	{
		writeByteImpl(v);
	};
	@Override protected void writeCharBlockImpl(char v)throws IOException
	{
		writeCharImpl(v);
	};
	@Override protected void writeShortBlockImpl(short v)throws IOException
	{
		writeShortImpl(v);
	};
	@Override protected void writeIntBlockImpl(int v)throws IOException
	{
		writeIntImpl(v);
	};
	@Override protected void writeLongBlockImpl(long v)throws IOException
	{
		writeLongImpl(v);
	};
	@Override protected void writeFloatBlockImpl(float v)throws IOException
	{
		writeFloatImpl(v);
	};
	@Override protected void writeDoubleBlockImpl(double v)throws IOException
	{
		writeDoubleImpl(v);
	};
};