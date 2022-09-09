package sztejkat.abstractfmt;
import java.io.IOException;

/**
	A core implementation of {@link IStructWriteFormat}.
	<p>
	This implementation supports:
	<ul>
		<li>optimization of <code>end-begin</code> sequence into one call;</li>
		<li>open/close state validation, but no {@link EBrokenFormat} detection
		and forced continous breaking of a stream;</li>
		<li>arguments validation for all block writes;</li>
		<li>state validation for block and primitive writes;</li>
		<li>name and recursion boundary checking;</li>
	</ul>
*/
abstract class AStructWriteFormatBase0 extends AFormatLimits implements IStructWriteFormat
{
					/** Used by {@link #end} to postpone the operation 
					and eventually optimize the <code>end();begin(...);</code>
					sequence into a one operation */
					private boolean pending_end;
					/** Tracks format open state */
					private boolean opened;
					/** Tracks format closes state */
					private boolean closed;
					/** Non-null if stream is doing a block write. Carries block type */
					private TContentType block_type;
					
		/* ***********************************************************************
		
				Services required from subclasses
		
		
		************************************************************************/
		/* ------------------------------------------------------------------
				Signal related
		------------------------------------------------------------------*/
		/** Should write "end" signal, exactly as {@link #end} specifies.
		Will be called after <code>end-begin</code> optimization decides, that
		single "end" signal should be written.
		Will be called in sane conditions.
		@throws IOException as {@link #begin}
		 */
		protected abstract void endImpl()throws IOException;
		/** Should write signle "begin" signal as {@link #begin} do specify.
		Will be called in sane conditions.
		@param name a sane, validated name.
		@throws IOException as {@link #begin}
		*/
		protected abstract void beginImpl(String name)throws IOException;
		/** Will be invoked when the composed, compact "end-begin" signal
		should be written.
		Will be called in sane conditions.
		By default implemented by calling {@link #endImpl} and {@link #beginImpl}
		so there is no composed signal ever written and no optimization in effect.
		@param name a sane, validated name.
		@throws IOException as {@link #begin}/{@link #end}
		*/
		protected void endBeginImpl(String name)throws IOException
		{
			endImpl();
			beginImpl(name);
		};
		/* ------------------------------------------------------------------
				State related.
		------------------------------------------------------------------*/
		/** Called by {@link #open} only when necessary 
		@throws IOException if failed */
		protected abstract void openImpl()throws IOException;
		/** Called by {@link #close} only when necessary, after calling {@link #flush}.
		@throws IOException if failed  */
		protected abstract void closeImpl()throws IOException;
		/** Called by {@link #flush} when flushed all pending states to actually 
		perform lower level flush 
		@throws IOException if failed */
		protected abstract void flushImpl()throws IOException;
		/* ------------------------------------------------------------------
				Primitive related, elementary
		------------------------------------------------------------------*/
		/** Invoked by an elementary primitive write after ensuring that write
		is possible.
		<p>
		Note: all <code>writeXXXImpl(v)</code> share the same contract.
		@param v value
		@throws IOException if failed */
		protected abstract void writeBooleanImpl(boolean v)throws IOException;
		protected abstract void writeByteImpl(byte v)throws IOException;
		/** See {@link #writeBooleanImpl(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeCharImpl(char v)throws IOException;
		/** See {@link #writeBooleanImpl(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeShortImpl(short v)throws IOException;
		/** See {@link #writeBooleanImpl(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeIntImpl(int v)throws IOException;
		/** See {@link #writeBooleanImpl(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeLongImpl(long v)throws IOException;
		/** See {@link #writeBooleanImpl(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeFloatImpl(float v)throws IOException;
		/** See {@link #writeBooleanImpl(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeDoubleImpl(double v)throws IOException;
		/* ------------------------------------------------------------------
				Datablock related.
		------------------------------------------------------------------*/
		/** Invoked by {@link #writeBooleanBlock} when boolean block is used for a first time
		inside a structure and block operation is started.
		<p>
		Some implementations may find it usefull. 
		<p>
		Default: doesn't do anything 
		@throws IOException if failed.*/
		protected void startBooleanBlock()throws IOException{};
		/** Invoked by block operation {@link #writeBooleanBlock(boolean[],int,int)} after managing state and validating arguments.
		By default implemented by using {@link #writeBooleanBlock(boolean)}.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeBooleanBlockImpl(buffer[offset++]);
			};
		};
		protected abstract void writeBooleanBlockImpl(boolean v)throws IOException;
		/** Invoked by {@link #end} when boolean block is active and is to be finished.
		Some implementations may find it usefull. Usually boolean[] blocks can be optimized
		this way into bit-streams. 
		<p>
		Default: doesn't do anything 
		@throws IOException if failed.*/
		protected void endBooleanBlock()throws IOException{};
		
		
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startByteBlock()throws IOException{};
		/** See {@link #writeBooleanBlockImpl(boolean[],int,int)}.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeByteBlockImpl(buffer[offset++]);
			};
		};
		protected abstract void writeByteBlockImpl(byte v)throws IOException;
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endByteBlock()throws IOException{};
		
		
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startCharBlock()throws IOException{};
		/** See {@link #writeBooleanBlockImpl(boolean[],int,int)}.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeCharBlockImpl(char [] buffer, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeCharBlockImpl(buffer[offset++]);
			};
		};
		protected abstract void writeCharBlockImpl(char v)throws IOException;
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endCharBlock()throws IOException{};
		
		
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startShortBlock()throws IOException{};
		/** See {@link #writeBooleanBlockImpl(boolean[],int,int)}.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeShortBlockImpl(short [] buffer, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeShortBlockImpl(buffer[offset++]);
			};
		};
		protected abstract void writeShortBlockImpl(short v)throws IOException;
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endShortBlock()throws IOException{};
		
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startIntBlock()throws IOException{};
		/** See {@link #writeBooleanBlockImpl(boolean[],int,int)}.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeIntBlockImpl(int [] buffer, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeIntBlockImpl(buffer[offset++]);
			};
		};
		protected abstract void writeIntBlockImpl(int v)throws IOException;
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endIntBlock()throws IOException{};
		
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startLongBlock()throws IOException{};
		/** See {@link #writeBooleanBlockImpl(boolean[],int,int)}.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeLongBlockImpl(long [] buffer, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeLongBlockImpl(buffer[offset++]);
			};
		};
		protected abstract void writeLongBlockImpl(long v)throws IOException;
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endLongBlock()throws IOException{};
		
		
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startFloatBlock()throws IOException{};
		/** See {@link #writeBooleanBlockImpl(boolean[],int,int)}.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeFloatBlockImpl(float [] buffer, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeFloatBlockImpl(buffer[offset++]);
			};
		};
		protected abstract void writeFloatBlockImpl(float v)throws IOException;
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endFloatBlock()throws IOException{};
		
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startDoubleBlock()throws IOException{};
		/** See {@link #writeBooleanBlockImpl(boolean[],int,int)}.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeDoubleBlockImpl(buffer[offset++]);
			};
		};
		protected abstract void writeDoubleBlockImpl(double v)throws IOException;
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endDoubleBlock()throws IOException{};
		
		
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startStringBlock()throws IOException{};
		/** See {@link #writeBooleanBlockImpl(boolean[],int,int)}.
		@param characters --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeStringBlockImpl(CharSequence characters, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeStringBlockImpl(characters.charAt(offset++));
			};
		};
		protected abstract void writeStringBlockImpl(char c)throws IOException;
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endStringBlock()throws IOException{};
		
		/* ***********************************************************************
		
				Services for subclasses
		
		
		************************************************************************/
		/** Throws if closed 
		@throws EClosed .*/
		private void validateNotClosed()throws EClosed
		{
			if (closed) throw new EClosed("Already closed");
		};
		/** @return set to true by {@link #close} */
		protected final boolean isClosed(){ return closed; };
		
		/** Throws if not opened 
		@throws ENotOpen .*/
		private void validateOpen()throws ENotOpen
		{
			if (!opened) throw new ENotOpen("Not open yet");
		};
		/** @return set to true by {@link #open} */
		protected final boolean isOpen(){ return opened; };
				
		/** Throws is any block operation is in progress 
		@throws IllegalStateException if true */
		private final void validateNoBlockOp()throws IllegalStateException
		{
			if (block_type!=null) throw new IllegalStateException("Block operation in progress");
		};
		/** Calls {@link #validateOpen} and {@link #validateNotClosed}
		@throws EClosed if already closed
		@throws ENotOpen if not open yet.
		*/
		private final void validateUsable()throws EClosed,ENotOpen
		{
			validateOpen();
		 	validateNotClosed();
		};
		/* ***********************************************************************
		
				IStructWriteFormat
		
		
		************************************************************************/
		/** Terminates block operation, if any
		by calling <code>endXXXBlock()</code>
		@throws IOException if an eventual termination of block failed.
		@see #block_type */
		private void terminatePendingBlockOperation()throws IOException
		{
			if (block_type!=null)
			{
				switch(block_type)
				{
					case BOOLEAN_BLK: 	endBooleanBlock(); break;
					case BYTE_BLK: 		endByteBlock(); break;
					case CHAR_BLK: 		endCharBlock(); break;
					case STRING: 		endStringBlock(); break;
					case SHORT_BLK: 	endShortBlock(); break;
					case INT_BLK: 		endIntBlock(); break;
					case LONG_BLK: 		endLongBlock(); break;
					case FLOAT_BLK: 	endFloatBlock(); break;
					case DOUBLE_BLK: 	endDoubleBlock(); break;
					default: throw new AssertionError("not a block "+block_type);
				};
				block_type = null;
			};
		};
		/** {@inheritDoc}
		Used {@link #pending_end} to handle <code>end-begin</code> optimization
		and eventually passes call to {@link #endImpl} to actually perform single
		"end" signal write operation 
		*/
		@Override public void end()throws IOException
		{
			validateUsable();
			//validat recursion levels.			
			leaveStruct();
			//Terminate block type, according to type
			terminatePendingBlockOperation();
			
			//Handle end-begin optimization
			if (!pending_end) 
			{
				pending_end = true;
			}else
			{
				//still keep one end() pending.
				endImpl();
			};
		}
		@Override public void begin(String name)throws IOException
		{
			//Sanitize arguments
		    assert(name!=null):"null name";
		    if (name.length()>getMaxSignalNameLength()) throw new IllegalArgumentException("begin signal name of "+name.length()+" chars is longer than set limit "+getMaxSignalNameLength());
		    
		    validateUsable();
		    //validat recursion levels.
		    enterStruct();
		    
		    //Terminate block type, according to type
			terminatePendingBlockOperation();
		    
			//Handle end-begin optimization
			if (!pending_end)
			{
				 beginImpl(name);
			}else
			{
				pending_end = false;
				endBeginImpl(name);
			};
		};
		/* -----------------------------------------------------------------------------
		
				Elementary primitives.
			
		-----------------------------------------------------------------------------*/
		private void validateCanDoElementaryWrite()throws IOException
		{
				validateUsable();
				validateNoBlockOp();
		}
		/** {@inheritDoc}
		  After validating if no block operaion is in progresss
		  calls {@link #writeBooleanImpl}
		*/
		@Override public void writeBoolean(boolean v)throws IOException
		{
				validateCanDoElementaryWrite();
				writeBooleanImpl(v);
		};
		@Override public void writeByte(byte v)throws IOException
		{
				validateCanDoElementaryWrite();
				writeByteImpl(v);
		};
		@Override public void writeChar(char v)throws IOException
		{
				validateCanDoElementaryWrite();
				writeCharImpl(v);
		};
		@Override public void writeShort(short v)throws IOException
		{
				validateCanDoElementaryWrite();
				writeShortImpl(v);
		};
		@Override public void writeInt(int v)throws IOException
		{
				validateCanDoElementaryWrite();
				writeIntImpl(v);
		};
		@Override public void writeLong(long v)throws IOException
		{
				validateCanDoElementaryWrite();
				writeLongImpl(v);
		};
		@Override public void writeFloat(float v)throws IOException
		{
				validateCanDoElementaryWrite();
				writeFloatImpl(v);
		};
		@Override public void writeDouble(double v)throws IOException
		{
				validateCanDoElementaryWrite();
				writeDoubleImpl(v);
		};
		/* -----------------------------------------------------------------------------
		
				Primitive sequences
			
		-----------------------------------------------------------------------------*/
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startBooleanBlock */
		private void validateBooleanBlock()throws IOException, IllegalStateException
		{
			//state 
			validateUsable();
			//block state
			if (block_type==null)
			{
				//initialize
				block_type=TContentType.BOOLEAN_BLK;
				startBooleanBlock();
			}else
				//validate type consistency
				if (block_type!=TContentType.BOOLEAN_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/** {@inheritDoc}
			After validating state and arguments call {@link #startBooleanBlock}
			and {@link #writeBooleanBlockImpl} according to situation
		*/
		@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException		
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateBooleanBlock();
			writeBooleanBlockImpl(buffer,offset,length);	
		};
		@Override public void writeBooleanBlock(boolean v)throws IOException
		{
			validateBooleanBlock();
			writeBooleanBlockImpl(v);
		};
		
		
		
		
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startByteBlock */
		private void validateByteBlock()throws IOException, IllegalStateException
		{
			//state 
			validateUsable();
			//block state
			if (block_type==null)
			{
				//initialize
				block_type=TContentType.BYTE_BLK;
				startByteBlock();
			}else
				//validate type consistency
				if (block_type!=TContentType.BYTE_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/** {@inheritDoc}
			After validating state and arguments call {@link #startByteBlock}
			and {@link #writeByteBlockImpl} according to situation
		*/
		@Override public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException		
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateByteBlock();
			writeByteBlockImpl(buffer,offset,length);	
		};
		@Override public void writeByteBlock(byte v)throws IOException
		{
			validateByteBlock();
			writeByteBlockImpl(v);
		};
		
		
			/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startCharBlock */
		private void validateCharBlock()throws IOException, IllegalStateException
		{
			//state 
			validateUsable();
			//block state
			if (block_type==null)
			{
				//initialize
				block_type=TContentType.CHAR_BLK;
				startCharBlock();
			}else
				//validate type consistency
				if (block_type!=TContentType.CHAR_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/** {@inheritDoc}
			After validating state and arguments call {@link #startCharBlock}
			and {@link #writeCharBlockImpl} according to situation
		*/
		@Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException		
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateCharBlock();
			writeCharBlockImpl(buffer,offset,length);	
		};
		@Override public void writeCharBlock(char v)throws IOException
		{
			validateCharBlock();
			writeCharBlockImpl(v);
		};
		
		
		
			/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startShortBlock */
		private void validateShortBlock()throws IOException, IllegalStateException
		{
			//state 
			validateUsable();
			//block state
			if (block_type==null)
			{
				//initialize
				block_type=TContentType.SHORT_BLK;
				startShortBlock();
			}else
				//validate type consistency
				if (block_type!=TContentType.SHORT_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/** {@inheritDoc}
			After validating state and arguments call {@link #startShortBlock}
			and {@link #writeShortBlockImpl} according to situation
		*/
		@Override public void writeShortBlock(short [] buffer, int offset, int length)throws IOException		
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateShortBlock();
			writeShortBlockImpl(buffer,offset,length);	
		};
		@Override public void writeShortBlock(short v)throws IOException
		{
			validateShortBlock();
			writeShortBlockImpl(v);
		};
		
		
		
			/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startIntBlock */
		private void validateIntBlock()throws IOException, IllegalStateException
		{
			//state 
			validateUsable();
			//block state
			if (block_type==null)
			{
				//initialize
				block_type=TContentType.INT_BLK;
				startIntBlock();
			}else
				//validate type consistency
				if (block_type!=TContentType.INT_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/** {@inheritDoc}
			After validating state and arguments call {@link #startIntBlock}
			and {@link #writeIntBlockImpl} according to situation
		*/
		@Override public void writeIntBlock(int [] buffer, int offset, int length)throws IOException		
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateIntBlock();
			writeIntBlockImpl(buffer,offset,length);	
		};
		@Override public void writeIntBlock(int v)throws IOException
		{
			validateIntBlock();
			writeIntBlockImpl(v);
		};
		
		
		
			/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startLongBlock */
		private void validateLongBlock()throws IOException, IllegalStateException
		{
			//state 
			validateUsable();
			//block state
			if (block_type==null)
			{
				//initialize
				block_type=TContentType.LONG_BLK;
				startLongBlock();
			}else
				//validate type consistency
				if (block_type!=TContentType.LONG_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/** {@inheritDoc}
			After validating state and arguments call {@link #startLongBlock}
			and {@link #writeLongBlockImpl} according to situation
		*/
		@Override public void writeLongBlock(long [] buffer, int offset, int length)throws IOException		
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateLongBlock();
			writeLongBlockImpl(buffer,offset,length);	
		};
		@Override public void writeLongBlock(long v)throws IOException
		{
			validateLongBlock();
			writeLongBlockImpl(v);
		};
		
		
		
			/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startFloatBlock */
		private void validateFloatBlock()throws IOException, IllegalStateException
		{
			//state 
			validateUsable();
			//block state
			if (block_type==null)
			{
				//initialize
				block_type=TContentType.FLOAT_BLK;
				startFloatBlock();
			}else
				//validate type consistency
				if (block_type!=TContentType.FLOAT_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/** {@inheritDoc}
			After validating state and arguments call {@link #startFloatBlock}
			and {@link #writeFloatBlockImpl} according to situation
		*/
		@Override public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException		
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateFloatBlock();
			writeFloatBlockImpl(buffer,offset,length);	
		};
		@Override public void writeFloatBlock(float v)throws IOException
		{
			validateFloatBlock();
			writeFloatBlockImpl(v);
		};
		
		
		
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startDoubleBlock */
		private void validateDoubleBlock()throws IOException, IllegalStateException
		{
			//state 
			validateUsable();
			//block state
			if (block_type==null)
			{
				//initialize
				block_type=TContentType.DOUBLE_BLK;
				startDoubleBlock();
			}else
				//validate type consistency
				if (block_type!=TContentType.DOUBLE_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/** {@inheritDoc}
			After validating state and arguments call {@link #startDoubleBlock}
			and {@link #writeDoubleBlockImpl} according to situation
		*/
		@Override public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException		
		{
			//arguments
			assert(buffer!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(buffer.length<=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			validateDoubleBlock();
			writeDoubleBlockImpl(buffer,offset,length);	
		};
		@Override public void writeDoubleBlock(double v)throws IOException
		{
			validateDoubleBlock();
			writeDoubleBlockImpl(v);
		};
		
		
		
		
		
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startDoubleBlock */
		private void validateStringBlock()throws IOException, IllegalStateException
		{
			//state 
			validateUsable();
			//block state
			if (block_type==null)
			{
				//initialize
				block_type=TContentType.STRING;
				startDoubleBlock();
			}else
				//validate type consistency
				if (block_type!=TContentType.STRING) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/** {@inheritDoc}
			After validating state and arguments call {@link #startDoubleBlock}
			and {@link #writeDoubleBlockImpl} according to situation
		*/
		@Override public void writeString(CharSequence characters, int offset, int length)throws IOException		
		{
			//arguments
			assert(characters!=null):"null buffer";
			assert(offset>=0):"offset="+offset;
			assert(length>=0):"length="+length;
			assert(characters.length()<=offset+length):"Out of buffer operation: characters.length="+characters.length()+", offset="+offset+", length="+length;
			validateStringBlock();
			writeStringBlockImpl(characters,offset,length);	
		};
		@Override public void writeString(char c)throws IOException
		{
			validateStringBlock();
			writeStringBlockImpl(c);
		};
		
		
		
		/* -----------------------------------------------------------------------------
		
				State
			
		-----------------------------------------------------------------------------*/		
		/** {@inheritDoc}
		   Implemented to write "end" signal postponed by "end-begin" optimization.
		   <p>
		   After writing this signal calls {@link #flushImpl}
		   <p>
		   This method do have a structural side effect on stream where:
		   <pre>
		   		begin("x")
		   		end();
		   		begin("x")
		   		end();
		   </pre> 
		   writes signals:
		   <pre>
		   		begin "x"
		   		end-begin "x"
		   		end
		   </pre>
		   while:
		    <pre>
		   		begin("x")
		   		end();
		   		flush();
		   		begin("x")
		   		end();
		   </pre> 
		    writes signals:
		   <pre>
		   		begin "x"
		   		end
		   		begin "x"
		   		end
		   </pre>
		   This has no functional impact on stream operation tough.
		   
		*/
		@Override public void flush()throws IOException
		{			
			//handle end-begin optimization
			if (pending_end)
			{
				pending_end = false;
				endImpl();
			};
			//do low level flushing.
			flushImpl();
		}
		/** {@inheritDoc}
		   Handles state and delegates to {@link #openImpl}
		   when necessary
		*/
		@Override public void open()throws IOException
		{
			validateNotClosed();
			if (opened) return;			
			openImpl();
			opened = true; //not set if openImpl failed.
						 //low level close will be invoked regardless.
		};
		/** {@inheritDoc}
		   Handles state and delegates to {@link #openImpl}
		   when necessary
		*/
		@Override public void close()throws IOException
		{
			if (closed) return;
			if (opened)
						flush();
			try{					
					closeImpl();
					super.close(); //zero depth tracking. We can skip it if failed tough.
					//always set closed, even if low level failed.
			}finally{ closed = true; }
		};
}