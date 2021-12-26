package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import sztejkat.abstractfmt.util.CAdaptivePushBackInputStream;
import java.io.*;
import java.io.InputStream;

/**
	A base for chunk-based write formats implementing
	{@link IIndicatorWriteFormat}
	<p>
	Adds functionality necessary for both described and un-described formats.
*/
public abstract class ABinIndicatorReadFormat1 extends ABinIndicatorReadFormat0
{
				/** Cache for {@link #getIndicator} */
				private TIndicator indicator_cache;
				
				/** A cache for {@link #readBooleanBlock}.
				Keeps number of bits which are available
				in currently processed packed bit chain. */
				private int bits_in_bit_chain;
				/** Position of bit in byte in bit chain */
				private int bit_in_byte;
				/** Bit stream content cache */
				private byte byte_cache;
				/** Set maximum name length */
				private int max_name_length;
				
	protected ABinIndicatorReadFormat1(InputStream in)
	{
		super(in);
		this.max_name_length = 1024;
	};
	/** Invoked on {@link #next} to invalidate all cached data.
	This class uses it to manage boolean block reads  */
	protected void invalidateCachedDataState()
	{
		bits_in_bit_chain =0;
		bit_in_byte=0;
		byte_cache=0;
	};
	/* **********************************************************************
	
			Decoding support
	
	***********************************************************************/
	/** Decodes character as described in 
	<a href="doc-files/chunk-syntax-described.html#CHAR_BLOCK_ENCODING">format
	specification</a>
	@return 0...0xFFFF if read character is not an end-of-text marker.
			-1 if it is end-of-text marker not carrying any character,
			-c-2 if it is an end-of-text marker with character
	*/
	protected int readEncodedCharacter()throws IOException
	{
		char c = 0;
		int b = readPayloadByte();
		c = (char)(b & 0x7F);
		if ((b & 0x80)!=0)
		{
			b = readPayloadByte();
			c |= ((b & 0x7F)<<7);
			if ((b & 0x80)!=0)
			{
				b = readPayloadByte();
				c |= ((b & 0x3)<<(7+7));
				
				switch( b & 0b1111_1100)
				{
					case 0:	//plain character
							return c;
					case 0b0111_1100:
							//end character
							return -((int)c) -2 ;
					default:
						if ((b==0b0111_1000)&&(c==0)) return -1;
						throw new EBrokenFormat("Could not decode character, last byte is "+Integer.toHexString(b)); 
				}
			}
		}
		return c;
	};
	/** Decodes character as described in 
	<a href="doc-files/chunk-syntax-described.html#CHAR_BLOCK_ENCODING">format
	specification</a>, throwing at end of text marks
	@return 0...0xFFFF 
	*/
	protected char readEncodedBlockCharacter()throws IOException
	{
		int c =readEncodedCharacter();
		if (c<0) throw new EBrokenFormat("end-of-text mark in char[] block");
		return (char)c;
	};
	
	/** Decodes content of payload which appears after 
	<a href="doc-files/chunk-syntax-described.html#BEGIN_DIRECT">BEGIN_DIRECT</a> header.
	@param buffer where to put decoded name
	@param max_characters how long name is allowed. If name is longer will throw 
			{@link EFormatBoundaryExceeded}
	@throws IOException if failed.
	@throws EFormatBoundaryExceeded if name is longer than <code>max_characters</code>.
	*/
	protected void readBeginDirectPayload(Appendable buffer, int max_characters)throws IOException
	{
		for(;;)
		{
			int c = readEncodedCharacter();
			if (c<0)
			{
				if (c==-1) return;
				//x = -c-2
				//-x-2 = c
				if (max_characters==0) throw new EFormatBoundaryExceeded("Signal name too long");
				buffer.append((char)(-c-2));
				return;
			}; 
			if (max_characters--==0) throw new EFormatBoundaryExceeded("Signal name too long");
			buffer.append((char)c);
		}
	};
	/* *********************************************************
			
				IIndicatorReadFormat	
	
	********************************************************* */
	/* ---------------------------------------------------------------
	
			Information and settings
	
	---------------------------------------------------------------*/
	/** Returns 256 */
	@Override public final int getMaxRegistrations(){ return 256; };
	/** Returns Integer.MAX_VALUE */
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	/** Always false */
	@Override public final boolean isFlushing(){ return false; };
	@Override public void setMaxSignalNameLength(int characters)
	{ 
		assert(characters>0);
		this.max_name_length = characters; 
	};
	@Override public final int getMaxSignalNameLength(){ return max_name_length; };
	/* ---------------------------------------------------------------
	
			Indicators
	
	---------------------------------------------------------------*/
	/** Calls {@link #tryNextIndicatorChunk} or returns cached value */
	@Override public TIndicator getIndicator()throws IOException
	{
		for(;;)
		{
			//Take indicator from cache, if has one
			if (indicator_cache==null)
			{
				//no, try to pick up one
				indicator_cache = tryNextIndicatorChunk();
				assert(indicator_cache!=TIndicator.EOF);
				if (indicator_cache==null) return TIndicator.EOF;
			};
			//Now consider, that data might reached end-of-data
			//due to natural reads (without call to next) so
			//poll this condition.
			if (indicator_cache==TIndicator.DATA)
			{
				if (isPayloadEof())
				{
					//force re-freshing it
					 indicator_cache = null;
					 continue;
				}
			};
			//this may be safely returned.
			return indicator_cache;
		}
	};
	@Override public void next()throws IOException
	{
		//In our system cursor is always after the header
		//of an indicator, so our behaviour depends of
		//what we return with getIndicator
		invalidateCachedDataState();
		switch(getIndicator())
		{
			case DATA:
					skipRemaningData();
					indicator_cache=null;
					break;
			case EOF: throw new EUnexpectedEof();
			default:
					//and in this case we should toggle it
					//to DATA if there are some data.
					if (!isPayloadEof())	
					{
						indicator_cache=TIndicator.DATA;
					}else
					{
						//isPayloadEof() returning true means, that it reached nonDATA indicator. 
						indicator_cache=null;
					}
		} 
	};
	
	/* ---------------------------------------------------------------
	
			Primitives
	
	---------------------------------------------------------------*/
	/** Reads  payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_BYTE">TYPE_BYTE</a> header.
	*/
	@Override public byte readByte()throws IOException
	{
		return (byte)readPayloadByte();
	};
	/** Reads  payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_CHAR">TYPE_CHAR</a> header.
	*/
	@Override public char readChar()throws IOException
	{
		char c = (char)readPayloadByte();
		c |= readPayloadByte()<<8;
		return c;
	};
	private short readShortImpl()throws IOException
	{
		short c = (short)readPayloadByte();
		c |= readPayloadByte()<<8;
		return c;
	};
	/** Reads payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_SHORT">TYPE_SHORT</a> header.
	*/
	@Override public short readShort()throws IOException{ return readShortImpl(); };
	
	private int readIntImpl()throws IOException
	{
		int v = readPayloadByte();
		v |= readPayloadByte()<<8;
		v |= readPayloadByte()<<(8+8);
		v |= readPayloadByte()<<(8+8+8);
		return v;
	};
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_INT">TYPE_INT</a> header.
	*/
	@Override public int readInt()throws IOException{ return readIntImpl(); };
	
	private long readLongImpl()throws IOException
	{
		long v = readPayloadByte();
		v |= readPayloadByte()<<8;
		v |= readPayloadByte()<<(8+8);
		v |= ((long)readPayloadByte())<<(8+8+8); //need to convert to long earlier, because int may become negative.
		v |= ((long)readPayloadByte())<<(8+8+8+8);
		v |= ((long)readPayloadByte())<<(8+8+8+8+8);
		v |= ((long)readPayloadByte())<<(8+8+8+8+8+8);
		v |= ((long)readPayloadByte())<<(8+8+8+8+8+8+8);
		return v;
	};
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_LONG">TYPE_LONG</a> header.
	*/
	@Override public long readLong()throws IOException{ return readLongImpl(); };
	
	
	private float readFloatImpl()throws IOException
	{
		return Float.intBitsToFloat(readIntImpl());
	};
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_FLOAT">TYPE_FLOAT</a> header.
	*/
	@Override public float readFloat()throws IOException{ return readFloatImpl(); };
	
	
	private double readDoubleImpl()throws IOException
	{
		return Double.longBitsToDouble(readLongImpl());
	};
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_DOUBLE">TYPE_DOUBLE</a> header.
	*/
	@Override public double readDouble()throws IOException{ return readDoubleImpl(); };
	
	/* ---------------------------------------------------------------
	
			Blocks
	
	---------------------------------------------------------------*/
					
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_BOOLEAN_BLOCK">TYPE_BOOLEAN_BLOCK</a> header.
	*/
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		/*	Note: Reading bit-blocks which are stored in chain requires additional, external chain
				  support and state tracking.
		*/
		int bits_in_bit_chain = this.bits_in_bit_chain;
		int bit_in_byte = this.bit_in_byte;
		byte byte_cache =  this.byte_cache;
		int readen  =0;
		try{
			while(length>0)
			{
				if (isPayloadEof()) break;	//Note: A proper partial read happens
											//only at chain end. If we hit end-of-data-chunks
											//at any other place stream is badly broken.	
				if (bits_in_bit_chain==0)
				{
					//we need to check next packed bit chain
					bits_in_bit_chain = readPayloadByte();
					if (bits_in_bit_chain==0) continue;		//yes, this is allowed.
					bit_in_byte = 0;
					byte_cache = (byte)readPayloadByte();	
				};
				//retrive bit from byte cached
				boolean v = ((1<<bit_in_byte) & byte_cache )!=0;
				buffer[offset++]=v;
				length--;
				readen++;
				//now manage bit indexing.
				bits_in_bit_chain--;
				if (bits_in_bit_chain==0)
				{
						//this chain is finished.
						bit_in_byte=0;
						byte_cache =0;
				}else
				{
					bit_in_byte++;
					if (bit_in_byte==8)
					{
						//byte cache is fully used up.
						bit_in_byte = 0;
						byte_cache = (byte)readPayloadByte();	
					}
				}
			}
		}finally{
			//put results in class cache. We use finally to get some robustness against
			//exceptions thrown.
			this.bits_in_bit_chain=bits_in_bit_chain;
			this.bit_in_byte=bit_in_byte;
			this.byte_cache=byte_cache;
		};
		return readen;
	};	
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_BYTE_BLOCK">TYPE_BYTE_BLOCK</a> header.
	*/	
	@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		int readen = 0;
		while(length>0)
		{
			if (isPayloadEof()) break;
			buffer[offset++] =  (byte)readPayloadByte();
			length--;
			readen++;
		};
		return readen;
	};	
	
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_BYTE_BLOCK">TYPE_BYTE_BLOCK</a> header.
	*/	
	@Override public int readByteBlock()throws IOException
	{
		int r = readPayload();
		if (r==-2) throw new EUnexpectedEof();
		return r;
	};		
		
		
		
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_CHAR_BLOCK">TYPE_CHAR_BLOCK</a> header.
	*/	
	@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException		
	{
		int readen = 0;
		while(length>0)
		{
			if (isPayloadEof()) break;
			buffer[offset++] =  readEncodedBlockCharacter();
			length--;
			readen++;
		};
		return readen;
	};	
	/** Reads byte payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_CHAR_BLOCK">TYPE_CHAR_BLOCK</a> header.
	*/
	@Override public int readCharBlock(Appendable characters,  int length)throws IOException
	{
		int readen = 0;
		while(length>0)
		{
			if (isPayloadEof()) break;
			characters.append(readEncodedBlockCharacter());
			length--;
			readen++;
		};
		return readen;
	};		
	
	
	/** Reads short payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_SHORT_BLOCK">TYPE_SHORT_BLOCK</a> header.
	*/	
	@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		int readen = 0;
		while(length>0)
		{
			if (isPayloadEof()) break;
			buffer[offset++] = readShortImpl();
			length--;
			readen++;
		};
		return readen;
	};		
		
	/** Reads int payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_INT_BLOCK">TYPE_INT_BLOCK</a> header.
	*/	
	@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		int readen = 0;
		while(length>0)
		{
			if (isPayloadEof()) break;
			buffer[offset++] = readIntImpl();
			length--;
			readen++;
		};
		return readen;
	};
	
	/** Reads long payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_LONG_BLOCK">TYPE_LONG_BLOCK</a> header.
	*/	
	@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		int readen = 0;
		while(length>0)
		{
			if (isPayloadEof()) break;
			buffer[offset++] = readLongImpl();
			length--;
			readen++;
		};
		return readen;
	};
	
	
	/** Reads float payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_FLOAT_BLOCK">TYPE_FLOAT_BLOCK</a> header.
	*/	
	@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		int readen = 0;
		while(length>0)
		{
			if (isPayloadEof()) break;
			buffer[offset++] = readFloatImpl();
			length--;
			readen++;
		};
		return readen;
	};
	
	
	/** Reads double payload as described in 
	<a href="doc-files/chunk-syntax-described.html#TYPE_DOUBLE_BLOCK">TYPE_DOUBLE_BLOCK</a> header.
	*/	
	@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		int readen = 0;
		while(length>0)
		{
			if (isPayloadEof()) break;
			buffer[offset++] = readDoubleImpl();
			length--;
			readen++;
		};
		return readen;
	};
}