package sztejkat.abstractfmt;
import java.io.IOException;

/**
	A core implementation of {@link IStructWriteFormat}.
	<p>
	This implementation supports:
	<ul>
		<li>optimization of <code>end-begin</code> sequence into one call for
		compact structure sequencing;</li>
		<li>flush and flush on close support;</li>
		<li>arguments validation for all block writes;</li>
		<li>state validation for block and primitive writes;</li>
		<li>name and recursion boundary checking;</li>
	</ul>
*/
abstract class AStructWriteFormatBase0 extends AStructFormatBase implements IStructWriteFormat
{
					/** Used by {@link #end} to postpone the operation 
					and eventually optimize the <code>end();begin(...);</code>
					sequence into a one operation */
					private boolean pending_end;
					
					
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
		/** See {@link #writeBooleanImpl(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
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
		
		
		
		/** See {@link #writeBooleanBlockImpl(boolean[],int,int)}.
		@param characters --//--
		@param offset --//--
		@param length --//--
		@throws IOException if failed. */
		protected void writeStringImpl(CharSequence characters, int offset, int length)throws IOException
		{
			while(length--!=0)
			{
				writeStringImpl(characters.charAt(offset++));
			};
		};
		protected abstract void writeStringImpl(char c)throws IOException;
		
		
			
	
		/* ***********************************************************************
		
				IStructWriteFormat
		
		
		************************************************************************/
		
		/** {@inheritDoc}
		Calls {@link #terminatePendingBlockOperation} and {@link #leaveStruct}.
		<p>
		Uses {@link #pending_end} to handle <code>end-begin</code> optimization
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
		/** {@inheritDoc}	
		Calls {@link #terminatePendingBlockOperation} and {@link #enterStruct}.
		*/		
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
		
		/** {@inheritDoc}
		  After validating if no block operaion is in progresss
		  calls {@link #writeBooleanImpl}
		*/
		@Override public void writeBoolean(boolean v)throws IOException
		{
				validateCanDoElementaryOp();
				writeBooleanImpl(v);
		};
		@Override public void writeByte(byte v)throws IOException
		{
				validateCanDoElementaryOp();
				writeByteImpl(v);
		};
		@Override public void writeChar(char v)throws IOException
		{
				validateCanDoElementaryOp();
				writeCharImpl(v);
		};
		@Override public void writeShort(short v)throws IOException
		{
				validateCanDoElementaryOp();
				writeShortImpl(v);
		};
		@Override public void writeInt(int v)throws IOException
		{
				validateCanDoElementaryOp();
				writeIntImpl(v);
		};
		@Override public void writeLong(long v)throws IOException
		{
				validateCanDoElementaryOp();
				writeLongImpl(v);
		};
		@Override public void writeFloat(float v)throws IOException
		{
				validateCanDoElementaryOp();
				writeFloatImpl(v);
		};
		@Override public void writeDouble(double v)throws IOException
		{
				validateCanDoElementaryOp();
				writeDoubleImpl(v);
		};
		/* -----------------------------------------------------------------------------
		
				Primitive sequences
			
		-----------------------------------------------------------------------------*/
		
		/** {@inheritDoc}
			After validating state and arguments call {@link #startBooleanBlock}
			(through {@link #validateBooleanBlock})
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
			writeStringImpl(characters,offset,length);	
		};
		@Override public void writeString(char c)throws IOException
		{
			validateStringBlock();
			writeStringImpl(c);
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
		   Handles state and delegates to {@link #closeImpl} and {@link #flush}
		   when necessary
		*/
		@Override public void close()throws IOException
		{
			if (isClosed()) return;
			if (isOpen()) flush();
			super.close();
		};
}