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
	/** Creates
	@param output see {@link ABinIndicatorWriteFormat0#ABinIndicatorWriteFormat}
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
	};
	
	/* ***********************************************************************
	
	
			Content encoding support
	
	
	* ***********************************************************************/
	/** Encodes character as described in 
	<a href="doc-files/chunk-syntax-described.html#CHAR_BLOCK_ENCODING">format
	specification</a>
	@param c character to encode
	*/
	protected void writeEncodedCharacter(char c)throws IOException
	{
		if ((c & 0xFF80)==0)
		{
			//1-byte form
			writePayload((byte)c);
		}else
		if ((c & (0x4000+0x8000))==0)
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
	@param c character to encode
	*/
	protected void writeEndMarkCharacter(char c)throws IOException
	{
		//3-byte form
		writePayload((byte)((c & 0x7F)|0x80));
		c>>=7;
		writePayload((byte)((c & 0x7F)|0x80));
		c>>=7;
		writePayload((byte)(c | (0x70+0x04+0x08)));
	};
	
	/** Writes "no-char-carrying-end-of-text" marker as specified in
	<a href="doc-files/chunk-syntax-described.html#CHAR_BLOCK_ENCODING">format specification</a>
	*/
	protected void writeEndMarkCharacter()throws IOException
	{
		//3-byte form
		writePayload((byte)(0x80));
		writePayload((byte)(0x80));
		writePayload((byte)(0x70+0x08));
	}
	
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
		if (n==0)
		{
			writeEndMarkCharacter();
		}else
		{ 
			int i=0;
			final int m = n-1;
			for(;i<m;i++)
			{
				writeEncodedCharacter(signal_name.charAt(i));
			};
			writeEndMarkCharacter(signal_name.charAt(i));
		};
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
	/** Always do nothing */
	@Override public final void writeFlush(TIndicator flush)throws IOException{};
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
	/** Writes byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_BOOLEAN_BLOCK">TYPE_BOOLEAN_BLOCK</a> header.
	<p>
	This method generates separate bit-chain for each write operation.
	*/
	@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		/*	Note: This is a sub-optimal version which writes each boolean block write
			as a separate chain. Writing it as a single, packed chain would either
			require back-write into payload, what is impossible, or separate chain buffer.
		*/
		while(length>0)
		{
			int bits_in_chain = Math.min(255,length);
			//write number of bits in stream.
			writePayload((byte)bits_in_chain);
			//now process
			int bits_written = 0;
			while(bits_written<bits_in_chain)
			{
				int byte_buffer = 0;
				int bit_mask = 0x01;
				while((bit_mask!=0x100) && ( bits_written<bits_in_chain))
				{	
					boolean bit = buffer[offset++];
					if (bit) byte_buffer|=bit_mask;
					bit_mask<<=1;
					bits_written++;				
				};
				writePayload((byte)byte_buffer);
			};
			length-=bits_in_chain;
		}
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