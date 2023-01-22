package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.Closeable;
import java.io.IOException;

/**
	A base implementation of {@link IStructReadFormat}.
	This implementation is symetric to {@link AStructWriteFormatBase0}.
	<p>
	This implementation is absolutely no validation whatsoever of primitive types.
	<p>
	This implementation supports:
	<ul>
		<li>open/close state validation, but no {@link EBrokenFormat} detection
		and forced continous breaking of a stream;</li>
		<li>arguments validation for all block reads;</li>
		<li>state validation for block and primitive reads;</li>
		<li>recursion boundary checking;</li>
	</ul>
	<p>This class is symetric to {@link AStructWriteFormatBase0}. 
*/
public abstract class AStructReadFormatBase0 extends AStructFormatBase implements IStructReadFormat
{
 		 private static final long TLEVEL = SLogging.getDebugLevelForClass(AStructReadFormatBase0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AStructReadFormatBase0.",AStructReadFormatBase0.class) : null;
			/** Signals returned by {@link #readSignal} */
			protected static enum TSignal
			{
				/** To be returned by {@link #readSignal} when 
				it reads what {@link AStructWriteFormatBase0#beginImpl} wrote.
				A a side effect the {@link #pickLastSignalName} should be set
				to a proper and <u>validated </u> signal name */
				SIG_BEGIN,
				/** To be returned by {@link #readSignal} when 
				it reads what {@link AStructWriteFormatBase0#endImpl} wrote.
				A a side effect the {@link #pickLastSignalName} should be set
				to null */
				SIG_END,
				/** To be returned by {@link #readSignal} when 
				it reads what {@link AStructWriteFormatBase0#endBeginImpl} wrote.
				A a side effect the {@link #pickLastSignalName} should be set
				to a proper and <u>validated </u> signal name */
				SIG_END_BEGIN;
			};
         			/** Set to true if {@link TSignal#SIG_END_BEGIN}
         			is returned by {@link #readSignal} */
         			private boolean begin_pending;
       
		/* *******************************************************
			
			Services required from subclasses
		
		********************************************************/
		/* ------------------------------------------------------------------
					Signal related
		------------------------------------------------------------------*/
			
				
		/** Invoked by {@link #nextImpl} to move to next signal in stream.
		Returns signal and updates certain variables according to returned
		state. This method is also responsible for checking if name
		is too long and abort the reading process.
		@return signal, non null.
		@throws IOException if fialed.
		@throws EFormatBoundaryExceeded if name length is exceeded. */
		protected abstract TSignal readSignal()throws IOException;
		/** Invoked by {@link #hasElementaryData} after checking basic
		conditions 
		@return as in {@link #hasElementaryData}
		@throws IOException if failed at low level */
		protected abstract boolean hasElementaryDataImpl()throws IOException;
		/** Set by {@link #readSignal} when a validated name of 
		begin signal is read. Subsequent calls of this method
		do return <code>null</code>
		<p>
		@return name, null for {@link TSignal#SIG_END}
				or when name was already read.
		*/
		protected abstract String pickLastSignalName();
		
		/* ------------------------------------------------------------------
				Primitive related, elementary
		------------------------------------------------------------------*/
		/** Invoked by an elementary primitive read after ensuring that read
		is allowed. This method is expected to distinguish end-of-file and end
		of signal content boundary. No other state checks are necessary.
		<p>
		Note: all <code>readXXXImpl(v)</code> share the same contract.
		@return value of an elementary primitive
		@throws IOException if fails due to many reasons. 
		@throws ENoMoreData if stream cursor is at the signal and there is no
				data to initiate operation.			
		@throws ESignalCrossed if an attempt to cross a signal is made inside a primitive element.
		@throws EEof of specific subclass depending on <a href="#TEMPEOF">eof handling type</a>
		*/
		protected abstract boolean readBooleanImpl()throws IOException;
		/** See {@link #readBooleanImpl()}
		@return value of an elementary primitive
		@throws IOException --//--
		*/
		protected abstract byte readByteImpl()throws IOException;
		/** See {@link #readBooleanImpl()}
		@return value of an elementary primitive
		@throws IOException --//--
		*/
		protected abstract short readShortImpl()throws IOException;
		/** See {@link #readBooleanImpl()}
		@return value of an elementary primitive
		@throws IOException --//--
		*/
		protected abstract char readCharImpl()throws IOException;
		/** See {@link #readBooleanImpl()}
		@return value of an elementary primitive
		@throws IOException --//--
		*/
		protected abstract int readIntImpl()throws IOException;
		/** See {@link #readBooleanImpl()}
		@return value of an elementary primitive
		@throws IOException --//--
		*/
		protected abstract long readLongImpl()throws IOException;
		/** See {@link #readBooleanImpl()}
		@return value of an elementary primitive
		@throws IOException --//--
		*/
		protected abstract float readFloatImpl()throws IOException;
		/** See {@link #readBooleanImpl()}
		@return value of an elementary primitive
		@throws IOException --//--
		*/
		protected abstract double readDoubleImpl()throws IOException;
		
		
		/* ------------------------------------------------------------------
				Datablock related.				
		------------------------------------------------------------------*/
		/** Invoked by block operation {@link #readBooleanBlock(boolean[],int,int)} after managing state and validating arguments.
		This method is expected to distinguish end-of-file and end
		of signal content boundary. No other state checks are necessary.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException if failed. */
		protected abstract int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException;		
		/** Invoked by block operation {@link #readBooleanBlock()} after managing state.
		@return --//--
		@throws IOException --//--
		@throws ENoMoreData --//-- */		 
		protected abstract boolean readBooleanBlockImpl()throws IOException,ENoMoreData;
		
		
		/** See {@link #readBooleanBlockImpl(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException if failed. */
		protected abstract int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException;
		/** Invoked by block operation {@link #readByteBlock()} after managing state.
		@return --//--
		@throws IOException --//--
		@throws ENoMoreData --//-- */		 
		protected abstract byte readByteBlockImpl()throws IOException,ENoMoreData;
		
		
		/** See {@link #readBooleanBlockImpl(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException if failed. */
		protected abstract int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException;
		/** Invoked by block operation {@link #readCharBlock()} after managing state.
		@return --//--
		@throws IOException --//--
		@throws ENoMoreData --//-- */		 
		protected abstract char readCharBlockImpl()throws IOException,ENoMoreData;
		
		
		
		/** See {@link #readBooleanBlockImpl(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException if failed. */
		protected abstract int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException;
		/** Invoked by block operation {@link #readShortBlock()} after managing state.
		@return --//--
		@throws IOException --//--
		@throws ENoMoreData --//-- */		 
		protected abstract short readShortBlockImpl()throws IOException,ENoMoreData;
		
		
		
		
		/** See {@link #readBooleanBlockImpl(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException if failed. */
		protected abstract int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException;
		/** Invoked by block operation {@link #readIntBlock()} after managing state.
		@return --//--
		@throws IOException --//--
		@throws ENoMoreData --//-- */		 
		protected abstract int readIntBlockImpl()throws IOException,ENoMoreData;
		
		
		
		/** See {@link #readBooleanBlockImpl(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException if failed. */
		protected abstract int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException;
		/** Invoked by block operation {@link #readLongBlock()} after managing state.
		@return --//--
		@throws IOException --//--
		@throws ENoMoreData --//-- */		 
		protected abstract long readLongBlockImpl()throws IOException,ENoMoreData;
		
		
		/** See {@link #readBooleanBlockImpl(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException if failed. */
		protected abstract int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException;
		/** Invoked by block operation {@link #readFloatBlock()} after managing state.
		@return --//--
		@throws IOException --//--
		@throws ENoMoreData --//-- */		 
		protected abstract float readFloatBlockImpl()throws IOException,ENoMoreData;
		
		
		/** See {@link #readBooleanBlockImpl(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException if failed. */
		protected abstract int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException;
		/** Invoked by block operation {@link #readDoubleBlock()} after managing state.
		@return --//--
		@throws IOException --//--
		@throws ENoMoreData --//-- */		 
		protected abstract double readDoubleBlockImpl()throws IOException,ENoMoreData;
		
		
		
		/** See {@link #readBooleanBlockImpl(boolean[],int,int)}
		and {@link #readString(Appendable,int)}
		@param characters --//--
		@param length --//--
		@return --//--
		@throws IOException if failed. */
		protected abstract int readStringImpl(Appendable characters, int length)throws IOException;
		/** Invoked by block operation {@link #readString()} after managing state.
		@return --//--
		@throws IOException --//--
		@throws ENoMoreData --//-- */		 
		protected abstract char readStringImpl()throws IOException,ENoMoreData;
		
		
		/* ************************************************************
		
		
			IStructReadFormat
		
		
		* ***********************************************************/
			
		/** Invoked to implement {@link IStructReadFormat#next} after ensuring that
		all the house keeping is managed. This house-keeping includes:
		exceptions:
		<ul>
			<li>recursion depth control;</li>
			<li>block operation control;</li>
		</ul>
		but does <u>not</u> include name sanitization.
		@return --//-- 
		@throws IOException if fialed.
		@throws EFormatBoundaryExceeded if name length is exceeded. */
		protected String nextImpl()throws IOException
		{
			if (TRACE) TOUT.println("nextImpl() ENTER");
			//check if we have an end-begin optimization in progress?
			if (begin_pending)
			{
					 begin_pending = false;
					 final String signame = pickLastSignalName();
					 assert(signame!=null):"null name for begin signal is not allowed";
					 if (TRACE) TOUT.println("nextImpl(), picking pednging begin \""+signame+" LEAVE");
					 return signame;
			}else
			{
				//ask implementation and deal with state machinery.
				switch(readSignal())
				{
						case SIG_BEGIN:
								{
									final String signame = pickLastSignalName();
									assert(signame!=null):"null name for begin signal is not allowed";
									if (TRACE) TOUT.println("nextImpl(), SIG_BEGIN \""+signame+" LEAVE");
									return signame;
								}
						case SIG_END:
								if (TRACE) TOUT.println("nextImpl()=null, SIG_END  LEAVE");
								return null;
						case SIG_END_BEGIN:
								if (TRACE) TOUT.println("nextImpl()=null, SIG_END_BEGIN, setting up pedning begin LEAVE");
								begin_pending = true;
								return null;
						default: throw new AssertionError("unkown enum");
				}
			}
		};
		/* ----------------------------------------------------------
				Signal operations
		----------------------------------------------------------*/
		@Override public String next()throws IOException
		{
			if (TRACE) TOUT.println("next() ENTER");
			//basic state
			validateUsable();
			//now terminate block operation in progress, if any.
			terminatePendingBlockOperation();
			//ask subclass
			if (TRACE) TOUT.println("next()->nextImpl()");
			String signal = nextImpl();						
			if (signal!=null)
			{
				if (TRACE) TOUT.println("next() signal=\""+signal+"\", begin");
				//Assert if name length check-up was NOT done by implementation?
				assert(signal.length()<=getMaxSignalNameLength()):
					"Signal name length "+signal.length()+" is beyond set limit "+getMaxSignalNameLength()+" Subclass "+this.getClass()+" failed to implement readSignal() contract?";
				//validate and track recursin depth
				enterStruct();
			}else
			{
			    if (TRACE) TOUT.println("next() null signal, end"); 
				//validate and track recursin depth
				leaveStruct();
			};
			if (TRACE) TOUT.println("next()="+(signal==null ? "null" : ("\""+signal+"\""))+" LEAVE");
			return signal;
		};
		@Override public boolean hasElementaryData()throws IOException
		{
			if (TRACE) TOUT.println("hasElementaryData() ENTER");
			//basic state
			validateUsable();
			//Note: Avoid if we have begin_pending
			//		because in such case downstream did already
			//		moved past a signal
			if (begin_pending)
			{
				if (TRACE) TOUT.println("hasElementaryData()=false, begin pending LEAVE");
				return false;
			};
			final boolean r= hasElementaryDataImpl();
			if (TRACE) TOUT.println("hasElementaryData()="+r+" LEAVE");
			return r;
		};
		/* ----------------------------------------------------------
				Elementary primitives
		----------------------------------------------------------*/
		@Override public boolean readBoolean()throws IOException
		{	
			if (TRACE) TOUT.println("readBoolean() ENTER");
			validateCanDoElementaryOp();
			final boolean v= readBooleanImpl();
			if (TRACE) TOUT.println("readBoolean()="+v+" LEAVE");
			return v;
		};
		@Override public byte readByte()throws IOException
		{	
			if (TRACE) TOUT.println("readByte() ENTER");
			validateCanDoElementaryOp();
			final byte v= readByteImpl();
			if (TRACE) TOUT.println("readByte()="+v+" LEAVE");
			return v;
		};
		@Override public char readChar()throws IOException
		{	
			if (TRACE) TOUT.println("readChar() ENTER");
			validateCanDoElementaryOp();
			final char v= readCharImpl();
			if (TRACE) TOUT.println("readChar()="+v+" LEAVE");
			return v;
		};
		@Override public short readShort()throws IOException
		{	
			if (TRACE) TOUT.println("readShort() ENTER");
			validateCanDoElementaryOp();
			final short v= readShortImpl();
			if (TRACE) TOUT.println("readShort()="+v+" LEAVE");
			return v;
		};
		@Override public int readInt()throws IOException
		{	
			if (TRACE) TOUT.println("readInt() ENTER");
			validateCanDoElementaryOp();
			final int v= readIntImpl();
			if (TRACE) TOUT.println("readInt()="+v+" LEAVE");
			return v;
		};
		@Override public long readLong()throws IOException
		{	
			if (TRACE) TOUT.println("readLong() ENTER");
			validateCanDoElementaryOp();
			final long v= readLongImpl();
			if (TRACE) TOUT.println("readLong()="+v+" LEAVE");
			return v;
		};
		@Override public float readFloat()throws IOException
		{	
			if (TRACE) TOUT.println("readFloat() ENTER");
			validateCanDoElementaryOp();
			final float v= readFloatImpl();
			if (TRACE) TOUT.println("readFloat()="+v+" LEAVE");
			return v;
		};
		@Override public double readDouble()throws IOException
		{	
			if (TRACE) TOUT.println("readDouble() ENTER");
			validateCanDoElementaryOp();
			final double v= readDoubleImpl();
			if (TRACE) TOUT.println("readDouble()="+v+" LEAVE");
			return v;
		};
		/* ----------------------------------------------------------
				Primitive sequences
		----------------------------------------------------------*/
		@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("readBooleanBlock(...,"+offset+","+length+") ENTER");
			validateBooleanBlock();
			final int v = readBooleanBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract";
			if (TRACE) TOUT.println("readBooleanBlock()="+v+" LEAVE"); 
			return v;
		}		
		@Override public boolean readBooleanBlock()throws IOException,ENoMoreData
		{
			if (TRACE) TOUT.println("readBooleanBlock() ENTER");
			validateBooleanBlock();
			final boolean v= readBooleanBlockImpl();
			if (TRACE) TOUT.println("readBooleanBlock()="+v+" LEAVE");
			return v;
		};
		@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("readByteBlock(...,"+offset+","+length+") ENTER");
			validateByteBlock();
			final int v = readByteBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			if (TRACE) TOUT.println("readByteBlock()="+v+" LEAVE"); 
			return v;
		}		
		@Override public byte readByteBlock()throws IOException,ENoMoreData
		{
			if (TRACE) TOUT.println("readByteBlock() ENTER");			
			validateByteBlock();
			final byte v= readByteBlockImpl();
			if (TRACE) TOUT.println("readByteBlock()="+v+" LEAVE");
			return v;
		};
		@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("readCharBlock(...,"+offset+","+length+") ENTER");
			validateCharBlock();
			final int v = readCharBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			if (TRACE) TOUT.println("readCharBlock()="+v+" LEAVE"); 
			return v;
		}		
		@Override public char readCharBlock()throws IOException,ENoMoreData
		{
			if (TRACE) TOUT.println("readCharBlock() ENTER");			
			validateCharBlock();
			final char v= readCharBlockImpl();
			if (TRACE) TOUT.println("readCharBlock()="+v+" LEAVE");
			return v;
		};
		
		@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("readShortBlock(...,"+offset+","+length+") ENTER");
			validateShortBlock();
			final int v = readShortBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			if (TRACE) TOUT.println("readShortBlock()="+v+" LEAVE"); 
			return v;
		}		
		@Override public short readShortBlock()throws IOException,ENoMoreData
		{
			if (TRACE) TOUT.println("readShortBlock() ENTER");			
			validateShortBlock();
			final short v= readShortBlockImpl();
			if (TRACE) TOUT.println("readShortBlock()="+v+" LEAVE");
			return v;
		};
		@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("readIntBlock(...,"+offset+","+length+") ENTER");
			validateIntBlock();
			final int v = readIntBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			if (TRACE) TOUT.println("readIntBlock()="+v+" LEAVE"); 
			return v;
		}		
		@Override public int readIntBlock()throws IOException,ENoMoreData
		{
			if (TRACE) TOUT.println("readIntBlock() ENTER");			
			validateIntBlock();
			final int v= readIntBlockImpl();
			if (TRACE) TOUT.println("readIntBlock()="+v+" LEAVE");
			return v;
		};
		@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("readLongBlock(...,"+offset+","+length+") ENTER");
			validateLongBlock();
			final int v = readLongBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			if (TRACE) TOUT.println("readLongBlock()="+v+" LEAVE"); 
			return v;
		}		
		@Override public long readLongBlock()throws IOException,ENoMoreData
		{
			if (TRACE) TOUT.println("readLongBlock() ENTER");			
			validateLongBlock();
			final long v= readLongBlockImpl();
			if (TRACE) TOUT.println("readLongBlock()="+v+" LEAVE");
			return v;
		};
		@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("readFloatBlock(...,"+offset+","+length+") ENTER");
			validateFloatBlock();
			final int v = readFloatBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			if (TRACE) TOUT.println("readFloatBlock()="+v+" LEAVE"); 
			return v;
		}		
		@Override public float readFloatBlock()throws IOException,ENoMoreData
		{
			if (TRACE) TOUT.println("readFloatBlock() ENTER");			
			validateFloatBlock();
			final float v= readFloatBlockImpl();
			if (TRACE) TOUT.println("readFloatBlock()="+v+" LEAVE");
			return v;
		};
		@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("readDoubleBlock(...,"+offset+","+length+") ENTER");
			validateDoubleBlock();
			final int v = readDoubleBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( v>=-1 ):"subclass "+this.getClass()+" failed to implement contract"; 
			if (TRACE) TOUT.println("readDoubleBlock()="+v+" LEAVE"); 
			return v;
		}		
		@Override public double readDoubleBlock()throws IOException,ENoMoreData
		{
			if (TRACE) TOUT.println("readDoubleBlock() ENTER");			
			validateDoubleBlock();
			final double v= readDoubleBlockImpl();
			if (TRACE) TOUT.println("readDoubleBlock()="+v+" LEAVE");
			return v;
		};
		@Override public int readString(Appendable characters,  int length)throws IOException
		{
			//arguments
			assert(characters!=null):"null characters";
			assert(length>=0):"length="+length;
			if (TRACE) TOUT.println("readString(...,"+length+") ENTER");
			validateStringBlock();
			final int v = readStringImpl(characters,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			if (TRACE) TOUT.println("readString()="+v+" LEAVE"); 
			return v;
		}		
		@Override public char readString()throws IOException,ENoMoreData
		{
			if (TRACE) TOUT.println("readString() ENTER");			
			validateStringBlock();
			final char v= readStringImpl();
			if (TRACE) TOUT.println("readString()="+v+" LEAVE");
			return v;
		};
		/* -------------------------------------------------
				State related.
				 Note: Nothing to do, superclass does everything we need.
		-------------------------------------------------*/
};