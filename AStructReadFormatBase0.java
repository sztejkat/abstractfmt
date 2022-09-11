package sztejkat.abstractfmt;
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
*/
public abstract class AStructReadFormatBase0 extends AStructFormatBase implements IStructReadFormat
{
		/* *******************************************************
			
			Services required from subclasses
		
		********************************************************/
		/* ------------------------------------------------------------------
					Signal related
		------------------------------------------------------------------*/		
		/** Must behave as {@link IStructReadFormat#next} with following 
		exceptions:
		<ul>
			<li>it does not have to check recursion depth limit, it will
			be checked and balanced by a caller;</li>
			<li>it does not have to manage block reads, it will be managed
			by a caller;</li>
			<li>if format supports "end-begin" combining this method must
			un-combine it into two separate calls;</li>
		</ul>
		@return --//-- 
		@throws IOException if fialed.
		@throws EFormatBoundaryExceeded if name length is exceeded. */
		protected abstract String nextImpl()throws IOException;
		/* ------------------------------------------------------------------
				Primitive related, elementary
		------------------------------------------------------------------*/
		/** Invoked by an elementary primitive read after ensuring that read
		is possible.
		<p>
		Note: all <code>readXXXImpl(v)</code> share the same contract.
		@return value of an elementary primitive
		@throws IOException if failed */
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
		/* ----------------------------------------------------------
				Signal operations
		----------------------------------------------------------*/
		@Override public String next()throws IOException
		{
			//basic state
			validateUsable();
			//now terminate block operation in progress, if any.
			terminatePendingBlockOperation();
			//ask subclass
			String signal = nextImpl();			
			if (signal!=null)
			{
				//Assert if name length check-up was NOT done by implementation?
				assert(signal.length()>getMaxSignalNameLength()):
					"Signal name length is beyond set limit. Subclass "+this.getClass()+" failed to implement contract.";
				//validate and track recursin depth
				enterStruct();
			}else
			{
				//validate and track recursin depth
				leaveStruct();
			};
			return signal;
		};
		/* ----------------------------------------------------------
				Elementary primitives
		----------------------------------------------------------*/
		@Override public boolean readBoolean()throws IOException
		{	
			validateCanDoElementaryOp();
			return readBooleanImpl();
		};
		@Override public byte readByte()throws IOException
		{	
			validateCanDoElementaryOp();
			return readByteImpl();
		};
		@Override public char readChar()throws IOException
		{	
			validateCanDoElementaryOp();
			return readCharImpl();
		};
		@Override public short readShort()throws IOException
		{	
			validateCanDoElementaryOp();
			return readShortImpl();
		};
		@Override public int readInt()throws IOException
		{	
			validateCanDoElementaryOp();
			return readIntImpl();
		};
		@Override public long readLong()throws IOException
		{	
			validateCanDoElementaryOp();
			return readLongImpl();
		};
		@Override public float readFloat()throws IOException
		{	
			validateCanDoElementaryOp();
			return readFloatImpl();
		};
		@Override public double readDouble()throws IOException
		{	
			validateCanDoElementaryOp();
			return readDoubleImpl();
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
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateBooleanBlock();
			final int v = readBooleanBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			return v;
		}		
		@Override public boolean readBooleanBlock()throws IOException,ENoMoreData
		{
			validateBooleanBlock();
			return readBooleanBlock();
		};
		@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateByteBlock();
			final int v = readByteBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			return v;
		}		
		@Override public byte readByteBlock()throws IOException,ENoMoreData
		{
			validateByteBlock();
			return readByteBlock();
		};
		@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateCharBlock();
			final int v = readCharBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			return v;
		}		
		@Override public char readCharBlock()throws IOException,ENoMoreData
		{
			validateCharBlock();
			return readCharBlock();
		};
		
		@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateShortBlock();
			final int v = readShortBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			return v;
		}		
		@Override public short readShortBlock()throws IOException,ENoMoreData
		{
			validateShortBlock();
			return readShortBlock();
		};
		@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateIntBlock();
			final int v = readIntBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			return v;
		}		
		@Override public int readIntBlock()throws IOException,ENoMoreData
		{
			validateIntBlock();
			return readIntBlock();
		};
		@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateLongBlock();
			final int v = readLongBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			return v;
		}		
		@Override public long readLongBlock()throws IOException,ENoMoreData
		{
			validateLongBlock();
			return readLongBlock();
		};
		@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateFloatBlock();
			final int v = readFloatBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			return v;
		}		
		@Override public float readFloatBlock()throws IOException,ENoMoreData
		{
			validateFloatBlock();
			return readFloatBlock();
		};
		@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateDoubleBlock();
			final int v = readDoubleBlockImpl(buffer,offset,length);
			//Validate contract?
			assert( v>=-1 ):"subclass "+this.getClass()+" failed to implement contract"; 
			return v;
		}		
		@Override public double readDoubleBlock()throws IOException,ENoMoreData
		{
			validateDoubleBlock();
			return readDoubleBlock();
		};
		@Override public int readString(Appendable characters,  int length)throws IOException
		{
			//arguments
			assert(characters!=null):"null characters";
			assert(length>=0):"length="+length;
			validateStringBlock();
			final int v = readStringImpl(characters,length);
			//Validate contract?
			assert( (v>=-1)&&(v<=length) ):"subclass "+this.getClass()+" failed to implement contract"; 
			return v;
		}		
		@Override public char readString()throws IOException,ENoMoreData
		{
			validateStringBlock();
			return readStringImpl();
		};
		/* -------------------------------------------------
				State related.
				 Note: Nothing to do, superclass does everything we need.
		-------------------------------------------------*/
};