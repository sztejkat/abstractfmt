package sztejkat.abstractfmt.bin.chunk;
import  sztejkat.abstractfmt.logging.SLogging;
import  sztejkat.abstractfmt.*;
import java.io.IOException;
import java.io.InputStream;


/**
	A chunk read format, as described in <a href="package-summary.html">package description</a>
	<p>
	This class adds actuall encoding of elementary primitives and blocks,
	except strings which are defined in superclass.
*/
public class CChunkReadFormat extends AChunkReadFormat0
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CChunkReadFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CChunkReadFormat.",CChunkReadFormat.class) : null;

	/* *******************************************************
		
			Construction
		
	* ******************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
			This value cannot be larger than 128. Recommended value is 128, minimum resonable is 8.
	@param raw raw input stream, non null. Will be closed.
			<p>
			This stream <u>must</u> be such, that it returns
			partial read/partial skip only if there is actually no data in stream, timeout happen, file
			was fully read or connection is broken. If this stream will return partial reads
			or partial skips just "because i like it" this format will report {@link EUnexpectedEof}
			when such condition will happen inside chunk headers or when it fails to read
			at least one byte in a chunk body.
	*/
	public CChunkReadFormat(int name_registry_capacity, InputStream raw)
	{
		super(name_registry_capacity, raw);
		if (TRACE) TOUT.println("new CChunkReadFormat()");
	};
	/* *******************************************************
		
			AStructFormatBase
		
	* ******************************************************/
	/** Empty */
	@Override protected void openImpl()throws IOException{};
	/* *******************************************************
		
			AStructReadFormatBase0
		
	* ******************************************************/
	/** Reads first byte of primitive element, returns -1
	if there are no data either due to signal or physical eof.
	@return 0...0xFF or -1
	@throws IOException if back-end failed.
	@see #inNext
	*/
	private int inOpt()throws IOException
	{
		if (!super.hasElementaryDataImpl()) return -1;
		return in();
	};
	/** Reads first byte of primitive element, throws {@link ENoMoreData}
	if {@link #in} returned -1.
	@return 0...0xFF
	@throws IOException if back-end failed
	@throws ENoMoreData if {@link #in} returned -1.
	@throws EEof if {@link #in} had thrown it.
	@see #inNext
	*/
	private int inFirst()throws IOException
	{
		int r = in();
		if (r==-1) throw new ENoMoreData();
		return r;
	};
	/** Reads subsequent bytes of primitive element, throws {@link ENoMoreData}
	if {@link #in} returned -1.
	@return 0...0xFF
	@throws IOException if back-end failed
	@throws ESignalCrossed if {@link #in} returned -1.
	@throws EEof if {@link #in} had thrown it.
	@see #inFirst
	*/
	private int inNext()throws IOException
	{
		int r = in();
		if (r==-1) throw new ESignalCrossed();
		return r;
	};
	/** Overriden to add boolean block operations */
	@Override protected boolean hasElementaryDataImpl()throws IOException
	{
		//The positive non zero in boolean_block_bits_remaning indicates,
		//that for sure we have some boolean bits in packed stream to get.
		//Negative means, we must delegeate to super, as no packed boolean
		//op is in progress.
		//And zero means, that packed op block is exhaused but we don't know
		//if there is next block. Since those blocks are carying at least
		//one bit, checking if there is a byte available will be sufficient,
		//so we can call super in that case too.
		return boolean_block_bits_remaning>0 ? true : super.hasElementaryDataImpl();
	};
	/* ------------------------------------------------------------------
				Primitive related, elementary
	------------------------------------------------------------------*/
	@Override protected boolean readBooleanImpl()throws IOException
	{
		int v = inFirst();
		switch(v)
		{
			case 0: return false;
			case 1: return true;
			default: throw new EBrokenFormat("Invalid boolean value");
		}
	};
	@Override protected byte readByteImpl()throws IOException
	{
		return (byte)inFirst();
	};
	@Override protected short readShortImpl()throws IOException
	{
		return (short)(inFirst() |  (inNext()<<8));
	};
	@Override protected char readCharImpl()throws IOException
	{
		return (char)(inFirst() |  (inNext()<<8));
	};
	@Override protected int readIntImpl()throws IOException
	{
		return (
				inFirst()
				|
				(inNext()<<8)
				|
				(inNext()<<(2*8))
				|
				(inNext()<<(3*8))
			);
	};
	@Override protected long readLongImpl()throws IOException
	{
		return  ((long)inFirst())
				|
				(((long)inNext())<<(8))
				|
				(((long)inNext())<<(2*8))
				|
				(((long)inNext())<<(3*8))
				|
				(((long)inNext())<<(4*8))
				|
				(((long)inNext())<<(5*8))
				|
				(((long)inNext())<<(6*8))
				|
				(((long)inNext())<<(7*8));
	};
	@Override protected float readFloatImpl()throws IOException
	{
		return Float.intBitsToFloat(readIntImpl());
	};
	@Override protected double readDoubleImpl()throws IOException
	{
		return Double.longBitsToDouble(readLongImpl());
	};
	/* ------------------------------------------------------------------
				Datablock related.				
	------------------------------------------------------------------*/
	/* .............................................................
				boolean block needs a bit of tricks.
	...............................................................*/
				/** Number of bits remaning in packed boolean block,
				-1 if no byte block operation is in progress.*/
				private int boolean_block_bits_remaning = -1;
				/** Bit number in {@link #boolean_block_buffer} to
				be picked up next */
				private int boolean_block_bit;
				/** Buffer of boolean packed block */
				private byte boolean_block_buffer;
	/** Overriden to initiate boolean block buffer */
	@Override protected void startBooleanBlock()throws IOException
	{
		if (TRACE) TOUT.println("startBooleanBlock() ENTER");
		super.startBooleanBlock();
		armNextBooleanBlock(false);
		if (TRACE) TOUT.println("startBooleanBlock() LEAVE");
	};	
	/** Overriden to clear boolean block buffer so
	that {@link #hasElementaryDataImpl} could properly detect
	the case.
	*/
	@Override protected void endBooleanBlock()throws IOException
	{
		if (TRACE) TOUT.println("endBooleanBlock() ENTER");
		boolean_block_bits_remaning = -1;
		boolean_block_bit =0;
		super.endBooleanBlock();
		if (TRACE) TOUT.println("endBooleanBlock() LEAVE");
	};	
	/** Arms next packed boolean block if possible
	@param throw_on_eof if true will throw EEof to tell apart
		if returns -1 due to eof or signal.
	@return true if there is next block, false if there isn't 
	@throws IOException if failed
	@throws EUnexpectedEof if throw_on_eof==true and {@link #in()} had thrown it.
	@see #in
	@see #inOpt
	*/
	private boolean armNextBooleanBlock(boolean throw_on_eof)throws IOException
	{
		int r = throw_on_eof ?  in() : inOpt();
		if (r==-1)
		{
			boolean_block_bits_remaning = 0;
			boolean_block_bit=0;
			if (TRACE) TOUT.println("armNextBooleanBlock, no next block");		
			return false;
		}else
		{
			boolean_block_bits_remaning = r+1;
			boolean_block_bit = 0;
			boolean_block_buffer = (byte)inNext();
			if (TRACE) TOUT.println("armNextBooleanBlock, armed "+boolean_block_bits_remaning+" bits");
			return true;
		}
	};
	/** Fetches next boolean block bit
	@param throw_on_eof if true will throw EEof to tell apart
		if returns -1 due to eof or signal.
	@return -1 if there is no more data, 1 for true, 0 for false.
	@throws IOException if downstream failed
	@throws EUnexpectedEof if throw_on_eof==true and {@link #armNextBooleanBlock} had thrown it.	
	*/
	private int readBooleanBlockBit(boolean throw_on_eof)throws IOException
	{
		if (boolean_block_bits_remaning==0) 
		{
			if (!armNextBooleanBlock(throw_on_eof)) return -1;
		};
		//check if needs to pick next byte
		if (boolean_block_bit==8)
		{
			boolean_block_buffer = (byte)inNext();
			boolean_block_bit=0;
		};
		byte mask = (byte)(1<<boolean_block_bit);
		boolean_block_bits_remaning--;
		boolean_block_bit++;
		return ((boolean_block_buffer & mask)!=0)
				?
				1
				:
				0;
	};
	@Override protected boolean readBooleanBlockImpl()throws IOException,ENoMoreData
	{
		switch(readBooleanBlockBit(true))
		{
			case 0: return false;
			case 1: return true;
			case -1: throw new ENoMoreData();
			default: throw new AssertionError();
		}
	};
	@Override protected int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException
	{
		int cnt = 0;		
		while(length-->0)
		{
			switch(readBooleanBlockBit(false))
			{
				case 0: cnt++; buffer[offset++]=false; break;
				case 1: cnt++; buffer[offset++]=true; break;
				case -1: return cnt==0 ? -1 : cnt;
				default: throw new AssertionError();
			}
		};
		return cnt;
	};
	/* .............................................................
				other blocks.
	...............................................................*/
	@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
	{
		//In this case we can't fall back to  readByteImpl due to different signal treatment.
		int cnt = 0;
		while(length-->0)
		{
			int r = inOpt();
			if (r==-1)return cnt==0 ? -1 : cnt;
			cnt++;
			buffer[offset++]=(byte)r;
		};
		return cnt;
	};
	@Override protected byte readByteBlockImpl()throws IOException,ENoMoreData
	{
		return readByteImpl();	
	};
	@Override protected int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException
	{
		//In this case we can't fall back to  readByteImpl due to different signal treatment.
		int cnt = 0;
		while(length-->0)
		{
			int r = inOpt();
			if (r==-1) return cnt==0 ? -1 : cnt;
			r  |= (inNext()<<8);
			cnt++;
			buffer[offset++]=(char)r;
		};
		return cnt;
	}
	@Override protected char readCharBlockImpl()throws IOException,ENoMoreData
	{
		return readCharImpl();
	};
	@Override protected int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException
	{
		//In this case we can't fall back to  readByteImpl due to different signal treatment.
		int cnt = 0;
		while(length-->0)
		{
			int r = inOpt();
			if (r==-1) return cnt==0 ? -1 : cnt;
			r  |= (inNext()<<8);
			cnt++;
			buffer[offset++]=(short)r;
		};
		return cnt;
	}
	@Override protected short readShortBlockImpl()throws IOException,ENoMoreData
	{
		return readShortImpl();
	};
	@Override protected int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException
	{
		//In this case we can't fall back to  readByteImpl due to different signal treatment.
		int cnt = 0;
		while(length-->0)
		{
			int r = inOpt();
			if (r==-1) return cnt==0 ? -1 : cnt;
			r  |= (inNext()<<8)
					|
					(inNext()<<(2*8))
					|
					(inNext()<<(3*8));
			cnt++;
			buffer[offset++]=r;
		};
		return cnt;
	}
	@Override protected int readIntBlockImpl()throws IOException,ENoMoreData
	{
		return readIntImpl();
	};
	@Override protected int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException
	{
		//In this case we can't fall back to  readByteImpl due to different signal treatment.
		int cnt = 0;
		while(length-->0)
		{
			int r = inOpt();
			if (r==-1) return cnt==0 ? -1 : cnt;
			long v =		(long)(r)
							|
							(((long)inNext())<<(8))
							|
							(((long)inNext())<<(2*8))
							|
							(((long)inNext())<<(3*8))
							|
							(((long)inNext())<<(4*8))
							|
							(((long)inNext())<<(5*8))
							|
							(((long)inNext())<<(6*8))
							|
							(((long)inNext())<<(7*8));
							
			cnt++;
			buffer[offset++]=v;
		};
		return cnt;
	}
	@Override protected long readLongBlockImpl()throws IOException,ENoMoreData
	{
		return readLongImpl();
	};			
	@Override protected int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException
	{
		//In this case we can't fall back to  readByteImpl due to different signal treatment.
		int cnt = 0;
		while(length-->0)
		{
			int r = inOpt();
			if (r==-1) return cnt==0 ? -1 : cnt;
			r  |= (inNext()<<8)
					|
					(inNext()<<(2*8))
					|
					(inNext()<<(3*8));
			cnt++;
			buffer[offset++]=Float.intBitsToFloat(r);
		};
		return cnt;
	};
	@Override protected float readFloatBlockImpl()throws IOException,ENoMoreData
	{
		return readFloatImpl();
	};
	@Override protected int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException
	{
		//In this case we can't fall back to  readByteImpl due to different signal treatment.
		int cnt = 0;
		while(length-->0)
		{
			int r = inOpt();
			if (r==-1) return cnt==0 ? -1 : cnt;
			long v =(long)(
							r
							|
							(inNext()<<8)
							|
							(inNext()<<(2*8))
							|
							(inNext()<<(3*8))
							)
							|
							((long)(
							inNext()
							|
							(inNext()<<8)
							|
							(inNext()<<(2*8))
							|
							(inNext()<<(3*8))
							)<<32);
			cnt++;
			buffer[offset++]=Double.longBitsToDouble(v);
		};
		return cnt;
	};
	@Override protected double readDoubleBlockImpl()throws IOException,ENoMoreData
	{
		return readDoubleImpl();
	};
	@Override protected int readStringImpl(Appendable characters, int length)throws IOException
	{
		//In this case we can't fall back to  readByteImpl due to different signal treatment.
		int cnt = 0;
		while(length-->0)
		{
			if (!super.hasElementaryDataImpl()) break; //to prevent EEof from being thrown.
			int r = decodeStringChar();
			if (r==-1) return cnt==0 ? -1 : cnt;
			cnt++;
			characters.append((char)r);
		};
		return cnt;
	};
		
	@Override protected char readStringImpl()throws IOException,ENoMoreData
	{
		int r = decodeStringChar();
		if (r==-1) throw new ENoMoreData();
		return (char)r;
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