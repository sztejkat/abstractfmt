package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import java.io.*;
import java.io.OutputStream;

/**
	A base for chunk-based write formats implementing
	{@link IIndicatorWriteFormat}
	<p>
	This class basically provides services common for
	both described and un-described formats.
*/
public abstract class ABinIndicatorWriteFormat1 extends ABinIndicatorWriteFormat0
{
					/** Number of bits which are pending for writing 
					in bit_chain_buffer. Zero is a valid number, -1
					means, that chain buffer is empty */
					private int bits_in_chain_buffer;
					/** Boolean block chain buffer, without initial
					header. */
					private final byte [] bit_chain_buffer;
	/** Creates
	@param output see {@link ABinIndicatorWriteFormat0#ABinIndicatorWriteFormat0}
	@param max_header_size --//--
	@param max_chunk_size --//--	
	*/
	protected ABinIndicatorWriteFormat1(
							OutputStream output,							
							int max_header_size,
							int max_chunk_size
							)
	{
		super(output, max_header_size, max_chunk_size);
		this.bit_chain_buffer=new byte[32];	//chain buffer of 255 bits capacity.
		this.bits_in_chain_buffer=-1;
	};
	/* ***********************************************************************
	
	
			Services required by superclass.
	
	
	* ***********************************************************************/
	/** Calls {@link #flushChainBuffer}*/
	@Override protected void flushPayload()throws IOException
	{
		flushChainBuffer();
	};
	/* ***********************************************************************
	
	
			Content encoding support
	
	
	* ***********************************************************************/
	/** Encodes character as described in 
	<a href="doc-files/chunk-syntax-described.html#CHAR_BLOCK_ENCODING">format
	specification</a>
	@param _c character to encode
	@throws IOException if failed to write it
	*/
	protected void writeEncodedCharacter(char _c)throws IOException
	{
		int c = _c+1;
		if ((c & 0x1FF80)==0)
		{
			//1-byte form
			writePayload((byte)c);
		}else
		if ((c & (0x4000+0x8000+0x10000))==0)
		{
			//2-byte form
			writePayload((byte)((c & 0x7F)|0x80));
			c>>=7;
			writePayload((byte)c);
		}else
		{
			//3-byte form
			writePayload((byte)((c & 0x7F)|0x80));
			c>>=7;
			writePayload((byte)((c & 0x7F)|0x80));
			c>>=7;
			writePayload((byte)c);
		}
	};
	
	/** Encodes character as described in 
	<a href="doc-files/chunk-syntax-described.html#CHAR_BLOCK_ENCODING">format
	specification</a> as an "end-of-text" mark.
	@throws IOException if failed to write it
	*/
	protected void writeEndMarkCharacter()throws IOException
	{
		//1-byte form, zero is terminator.
		writePayload((byte)0);
	};
	
	
	/* *****************************************************************************
	
				support methods for	IIndicatorWriteFormat
	
	
	* ****************************************************************************/
	/** Encodes content of payload which appears after 
	<a href="doc-files/chunk-syntax-described.html#BEGIN_DIRECT">BEGIN_DIRECT</a> header.
	@param signal_name name to encode.
	@throws IOException if failed.
	*/
	protected void writeBeginDirectPayload(String signal_name)throws IOException
	{
		final int n = signal_name.length();
		for(int i=0;i<n;i++)
		{
			writeEncodedCharacter(signal_name.charAt(i));
		};
		writeEndMarkCharacter();
	};
	/* *****************************************************************************
	
				IIndicatorWriteFormat
	
	
	* ****************************************************************************/
	/** Returns 256 */
	@Override public final int getMaxRegistrations(){ return 256; };
	/** Returns Integer.MAX_VALUE */
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	/** Always false */
	@Override public final boolean isFlushing(){ return false; };
	
	/** Writes  payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_BYTE">TYPE_BYTE</a> header.
	*/
	@Override public void writeByte(byte v)throws IOException
	{
		writePayload(v);
	}; 
	/** Writes  payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_CHAR">TYPE_CHAR</a> header.
	*/
	@Override public void writeChar(char v)throws IOException
	{
		writePayload((byte)v);
		writePayload((byte)(v>>8));
	};
	private void writeShortImpl(short v)throws IOException
	{
		writePayload((byte)v);
		writePayload((byte)(v>>8));
	};
	/** Writes  payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_SHORT">TYPE_SHORT</a> header.
	*/
	@Override public void writeShort(short v)throws IOException
	{
		writeShortImpl(v);
	};
	private void writeIntImpl(int v)throws IOException
	{
		writePayload((byte)v);
		v=v>>>8;
		writePayload((byte)v);
		v=v>>>8;
		writePayload((byte)v);
		v=v>>>8;
		writePayload((byte)v);
	};
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_INT">TYPE_INT</a> header.
	*/
	@Override public void writeInt(int v)throws IOException
	{
		writeIntImpl(v);
	};
	private void writeLongImpl(long v)throws IOException
	{
		writePayload((byte)v);
		v=v>>>8;
		writePayload((byte)v);
		v=v>>>8;
		writePayload((byte)v);
		v=v>>>8;
		writePayload((byte)v);
		
		v=v>>>8;
		writePayload((byte)v);
		v=v>>>8;
		writePayload((byte)v);
		v=v>>>8;
		writePayload((byte)v);
		v=v>>>8;
		writePayload((byte)v);		
	};
	/** Writes  payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_LONG">TYPE_LONG</a> header.
	*/
	@Override public void writeLong(long v)throws IOException
	{
		writeLongImpl(v);		
	};
	private void writeFloatImpl(float f)throws IOException
	{
		writeIntImpl( Float.floatToRawIntBits(f) );
		//Note: Intentionally not calling writeInt, so that subclass override
		//won't colide.
	};
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_FLOAT">TYPE_FLOAT</a> header.
	*/
	@Override public void writeFloat(float f)throws IOException
	{
		writeFloatImpl(f);
	};
	private void writeDoubleImpl(double d)throws IOException
	{
		writeLongImpl( Double.doubleToRawLongBits(d) );
		//Note: Intentionally not calling writeInt, so that subclass override
		//won't colide.
	};
	
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_DOUBLE">TYPE_DOUBLE</a> header.
	*/
	@Override public void writeDouble(double d)throws IOException
	{
		writeDoubleImpl(d);
	};
	/* ---------------------------------------------------------------
	
			Blocks
	
	---------------------------------------------------------------*/
	/** Flushes chain buffered by {@link #writeBooleanBlock} 
	@throws IOException if failed to write it
	@see #flushPayload
	*/
	private void flushChainBuffer()throws IOException
	{	
		if (bits_in_chain_buffer!=-1)
		{
			assert(bits_in_chain_buffer<=255);
			assert(bits_in_chain_buffer>=0);
			try{
				writePayload((byte)bits_in_chain_buffer);
				writePayload(bit_chain_buffer,0,(bits_in_chain_buffer-1)/8+1);
			}finally
			{
				bits_in_chain_buffer =-1;
			};
		};
	};
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_BOOLEAN_BLOCK">TYPE_BOOLEAN_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		/*	
			Note: This version is using chain buffer and relies on
			writeFlush() beeing called after a block or flushPayload be called
			in flush(). Thanks to that there is very little block fragmentation.
			Note: Due to that flush() will produce fragmented chain, but this is correct.
		*/
		//pick from class fields for efficiency.
		int bits_in_chain_buffer = this.bits_in_chain_buffer;
		while(length>0)
		{
			if (bits_in_chain_buffer==-1)
			{
				//handle buffer clean.
				bits_in_chain_buffer = 0;
			};
			int bptr = bits_in_chain_buffer>>>3;
			int biptr = bits_in_chain_buffer&0x7;
			if (biptr==0)
			{
				//handle byte wipe out to not emit garbage from previous buffered tasks.
				bit_chain_buffer[bptr]=0;			
			};
			//now out boolean
			if (buffer[offset++])
				bit_chain_buffer[bptr]|=(1<<biptr);
			length--;
			bits_in_chain_buffer++;
			if (bits_in_chain_buffer==255)
			{
				//put to class fields.
				this.bits_in_chain_buffer=bits_in_chain_buffer;
				flushChainBuffer();
				bits_in_chain_buffer = this.bits_in_chain_buffer;
			};
		}
		//store back in class fields.
		this.bits_in_chain_buffer=bits_in_chain_buffer;
	};		
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_BYTE_BLOCK">TYPE_BYTE_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		while(length>0)
		{
			writePayload(buffer[offset++]);
			length--;
		}
	};	
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_BYTE_BLOCK">TYPE_BYTE_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/	
	@Override public void writeByteBlock(byte data)throws IOException
	{
		writePayload(data);
	};
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_CHAR_BLOCK">TYPE_CHAR_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeCharBlock(CharSequence characters, int offset, int length)throws IOException		
	{
		while(length>0)
		{
			writeEncodedCharacter(characters.charAt(offset++));
			length--;
		}
	};
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_CHAR_BLOCK">TYPE_CHAR_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException	
	{
		while(length>0)
		{
			writeEncodedCharacter(buffer[offset++]);
			length--;
		}
	};
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_SHORT_BLOCK">TYPE_SHORT_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeShortBlock(short [] buffer, int offset, int length)throws IOException		
	{
		while(length>0)
		{
			//Note: Again, intentionally not calling public elementary write to not confuse subclass overrides.
			writeShortImpl(buffer[offset++]);
			length--;
		}
	};	
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_INT_BLOCK">TYPE_INT_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeIntBlock(int [] buffer, int offset, int length)throws IOException		
	{
		while(length>0)
		{
			//Note: Again, intentionally not calling public elementary write to not confuse subclass overrides.
			writeIntImpl(buffer[offset++]);
			length--;
		}
	};	
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_LONG_BLOCK">TYPE_LONG_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeLongBlock(long [] buffer, int offset, int length)throws IOException		
	{
		while(length>0)
		{
			//Note: Again, intentionally not calling public elementary write to not confuse subclass overrides.
			writeLongImpl(buffer[offset++]);
			length--;
		}
	};	
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_FLOAT_BLOCK">TYPE_FLOAT_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException		
	{
		while(length>0)
		{
			//Note: Again, intentionally not calling public elementary write to not confuse subclass overrides.
			writeFloatImpl(buffer[offset++]);
			length--;
		}
	};	
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_DOUBLE_BLOCK">TYPE_DOUBLE_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException		
	{
		while(length>0)
		{
			//Note: Again, intentionally not calling public elementary write to not confuse subclass overrides.
			writeDoubleImpl(buffer[offset++]);
			length--;
		}
	};	
};