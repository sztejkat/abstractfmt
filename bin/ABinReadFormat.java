package sztejkat.abstractfmt.bin;
import  sztejkat.abstractfmt.logging.SLogging;
import  sztejkat.abstractfmt.*;
import java.io.IOException;
/**

	A common base for binary formats, especially chunk and escape.
	<p>
	This class provides content encoding around a dummy
	abstract {@link #in} methods which is expected to read a byte
	to binary "payload" of a signal.
	<p>
	The encoding method is described in <code>sztejkat.abstractfmt.bin.chunk</code>
	format specification and is common for most of provided binary implementations.
*/
public abstract class ABinReadFormat extends ARegisteringStructReadFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ABinWriteFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ABinWriteFormat.",ABinWriteFormat.class) : null;
       			  
     /* ***************************************************************************
	
			Construction
	
	
	*****************************************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}			
	*/
	public ABinReadFormat(int name_registry_capacity)
	{
		super(name_registry_capacity);
	};    
	/* **************************************************************************
	
			Services required from subclases.
	
	
	* ***************************************************************************/
	/** Reads data from a payload buffer, transparently handling all necessary 
	decoding and payload management (ie. un-escaping or chunk continuation )
	
	@return -1 if reached signal.
			Otherwise 0...0xff representing a byte from payload.
		
	@throws IOException if low level failed
	@throws EBrokenFormat if found structural problem.
	@throws EEof if encountered end of file. 
	*/
	protected abstract int in()throws IOException;
	/** A separated portion of {@link #hasElementaryDataImpl} which does 
	check if there is something in payload to be read at byte level. 
	@return true if there are some data, false if there is no more data 
			in payload or end-of-file was reached.
	@throws IOException if failed
	*/
	protected abstract boolean hasUnreadPayload()throws IOException;
	/* *****************************************************************************
	
			String decoding			
	
	* *****************************************************************************/
	
	/** Loads string using {@link #decodeStringChar} to specified buffer
	@param b where to load, non-null
	@param chars_limit how many chars to append, non negative.
	@return number of loaded characters, non-negative regardless if end of payload is reached
			or not.
	@throws IOException if failed
	*/
	protected int decodeString(final Appendable b, final int chars_limit)throws IOException
	{
		if (TRACE) TOUT.println("decodeString(chars_limit="+chars_limit+") ENTER");
		assert(b!=null);
		assert(chars_limit>=0);
		int loaded_chars = 0;
		while( loaded_chars < chars_limit)
		{			
			int c = decodeStringChar();
			assert((c>=-1)&&(c<=0xFFFF));
			if (c==-1)
			{
				if (TRACE) TOUT.println("decodeString, no more data");
				break;
			};
			b.append((char)c);
			loaded_chars++;
		}
		if (TRACE) TOUT.println("decodeString=\""+b+"\","+loaded_chars+",  LEAVE");
		return loaded_chars;
	};
	/** Loads next string char, decoding it as specified in specs.
	@return 0..0xffff representing decoded character or -1 if {@link #in} returned -1.
	@throws IOException if downstream failed
	@throws EEof if encountered end of file 
	@throws EBrokenFormat if detected incorrectly encoded character
			or missing necessary data.
	*/
	protected int decodeStringChar()throws IOException
	{
		int n = in();
		if (n==-1) return -1;
		char c = (char)(n & 0x7F);
		if ((n & 0x80)!=0)
		{
			 n = in();
			 if (n==-1) throw new EBrokenFormat();
			 c |= (char)((( n & 0x7F)<<7));
			 if ((n & 0x80)!=0)
			 {
			 	 n = in();
			 	 if (n==-1) throw new EBrokenFormat();
			 	 if ((n & 0b1111_1100)!=0) throw new EBrokenFormat("Invalid string character");
			 	 c |= (char)(( n & 0x3) << (7+7));
			 };
		};
		if (DUMP) TOUT.println("decodeStringChar()=0x"+Integer.toHexString(c)+" (\'"+c+"\') LEAVE");
		return c;
	};
	/* *******************************************************
		
			AStructReadFormatBase0
		
	* ******************************************************/
		
	/** This method tests if there is a buffered section of boolean block in progress
	and if it is returns true. If it is not, calls {@link #hasUnreadPayload}
	to test if there is anything at byte level.
	*/
	@Override protected final boolean hasElementaryDataImpl()throws IOException
	{
		if (TRACE) TOUT.println("hasElementaryDataImpl() ENTER");
		//The positive non zero in boolean_block_bits_remaning indicates,
		//that for sure we have some boolean bits in packed stream to get.
		//Negative means, no packed boolean op is in progress.
		//And zero means, that packed op block is exhaused but we don't know
		//if there is next block. Since those blocks are carying at least
		//one bit, checking if there is a byte available will be sufficient,
		//so we can return false and trigger byte level validation
		final boolean v =  boolean_block_bits_remaning>0
							?
							true
							:
							hasUnreadPayload();
		if (TRACE) TOUT.println("hasElementaryDataImpl()="+v+" LEAVE");
		return v;				
	};
	/* -----------------------------------------------------------------------------
			
			Common content fetching tools
	
	-----------------------------------------------------------------------------*/
	/** Loads next string char from chunk, decoding it as specified in specs.
	@return 0..0xffff representing decoded character or -1 if reached end of payload ("continue" is handled transparently)
			<u>or end of file</u>.
	@throws IOException if downstream failed
	@throws EBrokenFormat if detected incorrectly encoded character
			or missing necessary data.
	
	@see #decodeStringChar
	*/
	private int decodeStringCharOpt()throws IOException
	{
		if (!hasElementaryDataImpl()) return -1;
		return decodeStringChar();
	};
	/** Reads first byte of primitive element, returns -1
	if there are no data either due to signal <u>or physical eof</u>.
	@return 0...0xFF or -1
	@throws IOException if back-end failed.
	@see #inNext
	*/
	private int inOpt()throws IOException
	{
		if (!hasElementaryDataImpl()) return -1;
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
	/** Reads subsequent bytes of primitive element, throws {@link ESignalCrossed}
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
	/** Overriden to initialize boolean block buffer */
	@Override protected void startBooleanBlock()throws IOException
	{
		if (TRACE) TOUT.println("startBooleanBlock() ENTER");
		super.startBooleanBlock();
		armNextBooleanBlock(false);
		if (TRACE) TOUT.println("startBooleanBlock() LEAVE");
	};	
	/** Overriden to clear boolean block buffer so
	that {@link #hasElementaryDataImpl} could properly detect
	the case that operation is dropped.
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
			switch(readBooleanBlockBit(cnt==0))
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
			int r = (cnt==0 ? in() : inOpt()); //first EOF should be thrown.
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
			int r = (cnt==0 ? in() : inOpt()); //first EOF should be thrown.
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
			int r = (cnt==0 ? in() : inOpt()); //first EOF should be thrown.
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
			int r = (cnt==0 ? in() : inOpt()); //first EOF should be thrown.
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
			int r = (cnt==0 ? in() : inOpt()); //first EOF should be thrown.
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
			int r = (cnt==0 ? in() : inOpt()); //first EOF should be thrown.
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
			int r = (cnt==0 ? in() : inOpt()); //first EOF should be thrown.
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
			//Now the requested behavior is to throw EOF if failed to fetch anything
			//and not throw if otheriwse
			int r = (cnt==0 ? decodeStringChar() : decodeStringCharOpt());
			if (r==-1) return cnt==0 ? -1 : cnt;
			cnt++;
			characters.append((char)r);
		};
		return cnt;
	};
		
	@Override protected char readStringImpl()throws IOException,ENoMoreData
	{
		int r = decodeStringChar(); //with EOF throwing!
		if (r==-1) throw new ENoMoreData();
		return (char)r;
	};
		
};         
