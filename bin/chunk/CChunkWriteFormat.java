package sztejkat.abstractfmt.bin.chunk;
import  sztejkat.abstractfmt.*;
import java.io.IOException;
import java.io.OutputStream;

/**
	A chunk write format, as described in <a href="package-summary.html">package description</a>
	<p>
	This class adds actuall encoding of elementary primitives and blocks,
	except strings which are defined in subclass.
*/
public class CChunkWriteFormat extends AChunkWriteFormat0
{
	/* *****************************************************************************
	
	
			Construction
			
	
	
	* *****************************************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
			This value cannot be larger than 127. Recommended value is 127, minimum resonable is 8.	
	@param raw raw binary stream to write to. Will be closed on {@link #close}.
	@param indexed_registration if true names are registered directly, by index.
			If false names are registered indirectly, by order of appearance.
			For typed streams it is recommended to use "by index" registration,
			as first 8 registered names can be encoded very efficiently for 
			typical elementary elements lengths.
	*/
	CChunkWriteFormat(
					   int name_registry_capacity,
					   OutputStream raw,
					   boolean indexed_registration
					   )
	{
		super(name_registry_capacity,raw, indexed_registration);
	};
	
	/* *****************************************************************************
	
			Services required by ARegisteringStructWriteFormat
	
	******************************************************************************/
	/* ------------------------------------------------------------------
				State related.
	---------------------------------------------------------------------*/
	/** Empty */
	@Override protected void openImpl()throws IOException{};
	/** Overriden to eventually flush boolean block in progress */
	@Override protected void flushImpl()throws IOException
	{
		if (bitpos_in_boolean_block!=0)
		{
			flushBooleanBlockBuffer();
		};
		super.flushImpl();
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
		super.startBooleanBlock();
		if (boolean_block_buffer==null)
				 boolean_block_buffer = new byte[BIT_BLOCK_SIZE_BITS/8];
		bitpos_in_boolean_block = 0;
	};
	@Override protected void writeBooleanBlockImpl(boolean v)throws IOException
	{		
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
	};
	private void flushBooleanBlockBuffer()throws IOException
	{
		if (bitpos_in_boolean_block!=0)
		{
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
	};
	/** Overriden to flush boolean block buffer */
	@Override protected void endBooleanBlock()throws IOException
	{
		flushBooleanBlockBuffer();
		super.endBooleanBlock();
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
	/* *****************************************************************************
	
	
			IFormatLimits
			
	
	
	* *****************************************************************************/
	/** Unbound, Integer.MAX_VALUE */
	@Override public int getMaxSupportedSignalNameLength()
	{
		return Integer.MAX_VALUE;
	};
	/** Unbound, -1 */
	@Override public int getMaxSupportedStructRecursionDepth()
	{
		return -1;
	};
};