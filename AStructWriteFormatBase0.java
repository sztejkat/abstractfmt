package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
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
	<p>
	This implementation requires that stream is using following
	set of signals:
	<pre>
			begin
			end
			<i>optional:</i>
			end-begin
	</pre> 
	<p>This class is symetric to {@link AStructReadFormatBase0}.
*/
public abstract class AStructWriteFormatBase0 extends AStructFormatBase implements IStructWriteFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(AStructWriteFormatBase0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AStructWriteFormatBase0.",AStructWriteFormatBase0.class) : null;
  
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
		/** Invoked in {@link #optimizeBeginName}
		@param name --//--
		@return --//--. This implementation returns true to indicate that no optimization
			is necessary.
		@throws IOException if any other format error that not-opened, closed or name too long
			happens.
		*/
		protected boolean optimizeBeginNameImpl(String name)throws IOException
		{
			assert(name!=null); 
			assert(name.length()<=getMaxSignalNameLength());
			return true; 
		};
		/** Should write "end" signal, exactly as {@link #end} specifies.
		Will be called after <code>end-begin</code> optimization decides, that
		single "end" signal should be written.
		Will be called in sane conditions.
		@throws IOException as {@link #begin}
		 */
		protected abstract void endImpl()throws IOException;
		/** Should write single "begin" signal as {@link #begin} do specify.
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
		perform lower level flush. 
		<p>
		Note: flush() is always called inside a close().
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
		/** Common implementation for 
		{@link #flushSignalPayloadBeginNext} and 
		{@link #flushSignalPayloadBeginNext}.
		Invokes {@link #terminatePendingBlockOperation} 
		@throws IOException if failed to terminate block.
		*/
		private void defaultFlushSignalPayload()throws IOException
		{
			//Terminate block type, according to type
			terminatePendingBlockOperation();
		}
		/** A housekeeping method for {@link #begin}/{@link #end} responsible
		for perfoming any closure operation on payload carried between signals.
		Default implementation calls {@link #defaultFlushSignalPayload}.
		<p>
		This method is called when "begin" signal is written.
		<p>
		After this method is called the {@link #beginImpl} or {@link #endBeginImpl} is invoked
		depending on end-begin optimization state.
		@throws IOException if generation of closure failed.
		*/
		protected void flushSignalPayloadBeginNext()throws IOException
		{
			defaultFlushSignalPayload();
		};
		/** Alike method to {@link #flushSignalPayloadBeginNext} but
		for "end" signal. Default implementation calls {@link #defaultFlushSignalPayload}.
		<p>
		This method is called when "end" signal is written.
		<p>
		After this method is called the {@link #endImpl} is invoked, however
		the invocation may be delayed till some subsequent operation due
		to end-begin optimization.
		@throws IOException if generation of closure failed.
		*/
		protected void flushSignalPayloadEndNext()throws IOException
		{
			defaultFlushSignalPayload();
		};
		/** Tests if end-begin optimization has pending and and flushes it.
		To be invoked before any primitive operation.
		@throws IOException if failed 
		*/
		private void flushPendingEnd()throws IOException
		{			
		 	if (pending_end)
		 	{
		 		pending_end = false;
		 		if (TRACE) TOUT.println("flushPendingEnd()->has pending, flushing it");
		 		endImpl();
		 	}else
		 	{
		 		if (TRACE) TOUT.println("flushPendingEnd()->no pending end.");
		 	};
		};
		/** {@inheritDoc}
		Calls {@link #terminatePendingBlockOperation} and {@link #leaveStruct}.
		<p>
		Uses {@link #pending_end} to handle <code>end-begin</code> optimization
		and eventually passes call to {@link #endImpl} to actually perform single
		"end" signal write operation 
		*/
		@Override public void end()throws IOException
		{
			if (TRACE) TOUT.println("end() ENTER");
			validateUsable();
			//validat recursion levels.			
			leaveStruct();
			//Do necessary cleanup.
			flushSignalPayloadEndNext();
			
			//Handle end-begin optimization
			if (!pending_end) 
			{
				if (TRACE) TOUT.println("end(), marking as pending");
				pending_end = true;
			}else
			{
				//still keep one end() pending.
				if (TRACE) TOUT.println("end(), has pending end -> endImpl()");
				endImpl();
			};
			if (TRACE) TOUT.println("end() LEAVE");
		}
		/** {@inheritDoc}	
		Calls {@link #terminatePendingBlockOperation} and {@link #enterStruct}.
		*/		
		@Override public void begin(String name)throws IOException
		{
			//Sanitize arguments
		    assert(name!=null):"null name";
		    if (TRACE) TOUT.println("begin(\""+name+"\") ENTER");
		    if (name.length()>getMaxSignalNameLength()) throw new EFormatBoundaryExceeded("begin signal name of "+name.length()+" chars is longer than set limit "+getMaxSignalNameLength());
		    
		    validateUsable();
		    //validat recursion levels.
		    enterStruct();
		    
		    //Do necessary cleanup regardless if it was actually necessary
		    //to keep state tracking in sync.
			flushSignalPayloadBeginNext();
			//Handle end-begin optimization
			if (!pending_end)
			{
				if (TRACE) TOUT.println("begin()->beginImpl()");				
			    beginImpl(name);
			}else
			{
				if (TRACE) TOUT.println("begin()->endBeginImpl()");
				pending_end = false;
				endBeginImpl(name);
			};
		};
		
		/** {@inheritDoc}	
		Calls {@link #optimizeBeginNameImpl}.
		*/		
		@Override public boolean optimizeBeginName(String name)throws IOException
		{
			//Sanitize arguments
		    assert(name!=null):"null name";
		    if (TRACE) TOUT.println("optimizeBeginName(\""+name+"\") ENTER");
		    if (name.length()>getMaxSignalNameLength()) throw new EFormatBoundaryExceeded("begin signal name \""+name+"\" of "+name.length()+" chars is longer than set limit "+getMaxSignalNameLength());
		    
		    validateUsable();
		    
		    final boolean r = optimizeBeginNameImpl(name);
		    if (TRACE) TOUT.println("optimizeBeginName()="+r+" LEAVE");
		    return r;
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
				if (TRACE) TOUT.println("writeBoolean("+v+")");
				validateCanDoElementaryOp();
				flushPendingEnd();
				writeBooleanImpl(v);
		};
		@Override public void writeByte(byte v)throws IOException
		{
				if (TRACE) TOUT.println("writeByte("+v+")");
				validateCanDoElementaryOp();
				flushPendingEnd();
				writeByteImpl(v);
		};
		@Override public void writeChar(char v)throws IOException
		{
				if (TRACE) TOUT.println("writeChar("+v+")");
				validateCanDoElementaryOp();
				flushPendingEnd();
				writeCharImpl(v);
		};
		@Override public void writeShort(short v)throws IOException
		{
				if (TRACE) TOUT.println("writeShort("+v+")");
				validateCanDoElementaryOp();
				flushPendingEnd();
				writeShortImpl(v);
		};
		@Override public void writeInt(int v)throws IOException
		{
				if (TRACE) TOUT.println("writeInt("+v+")");
				validateCanDoElementaryOp();
				flushPendingEnd();
				writeIntImpl(v);
		};
		@Override public void writeLong(long v)throws IOException
		{
				if (TRACE) TOUT.println("writeLong("+v+")");
				validateCanDoElementaryOp();
				flushPendingEnd();
				writeLongImpl(v);
		};
		@Override public void writeFloat(float v)throws IOException
		{
				if (TRACE) TOUT.println("writeFloat("+v+")");
				validateCanDoElementaryOp();
				flushPendingEnd();
				writeFloatImpl(v);
		};
		@Override public void writeDouble(double v)throws IOException
		{
				if (TRACE) TOUT.println("writeDouble("+v+")");
				validateCanDoElementaryOp();
				flushPendingEnd();
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
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			
			if (TRACE) TOUT.println("writeBooleanBlock(...,"+offset+","+length+") ENTER");
			flushPendingEnd();
			validateBooleanBlock();
			writeBooleanBlockImpl(buffer,offset,length);
			if (TRACE) TOUT.println("writeBooleanBlock() LEAVE");	
		};
		@Override public void writeBooleanBlock(boolean v)throws IOException
		{
			if (TRACE) TOUT.println("writeBooleanBlock("+v+") ENTER");
			flushPendingEnd();
			validateBooleanBlock();
			writeBooleanBlockImpl(v);
			if (TRACE) TOUT.println("writeBooleanBlock() LEAVE");
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
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("writeByteBlock(...,"+offset+","+length+") ENTER");
			flushPendingEnd();
			validateByteBlock();
			writeByteBlockImpl(buffer,offset,length);	
			if (TRACE) TOUT.println("writeByteBlock() LEAVE");
		};
		@Override public void writeByteBlock(byte v)throws IOException
		{
			if (TRACE) TOUT.println("writeByteBlock("+v+") ENTER");
			flushPendingEnd();
			validateByteBlock();
			writeByteBlockImpl(v);
			if (TRACE) TOUT.println("writeByteBlock() LEAVE");
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
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("writeCharBlock(...,"+offset+","+length+") ENTER");
			flushPendingEnd();
			validateCharBlock();
			writeCharBlockImpl(buffer,offset,length);	
			if (TRACE) TOUT.println("writeCharBlock() LEAVE");
		};
		@Override public void writeCharBlock(char v)throws IOException
		{
			if (TRACE) TOUT.println("writeCharBlock("+v+") ENTER");
			flushPendingEnd();
			validateCharBlock();
			writeCharBlockImpl(v);
			if (TRACE) TOUT.println("writeCharBlock() LEAVE");
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
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("writeShortBlock(...,"+offset+","+length+") ENTER");
			flushPendingEnd();
			validateShortBlock();
			writeShortBlockImpl(buffer,offset,length);	
			if (TRACE) TOUT.println("writeShortBlock() LEAVE");
		};
		@Override public void writeShortBlock(short v)throws IOException
		{
			if (TRACE) TOUT.println("writeShortBlock("+v+") ENTER");
			flushPendingEnd();
			validateShortBlock();
			writeShortBlockImpl(v);
			if (TRACE) TOUT.println("writeShortBlock() LEAVE");
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
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("writeIntBlock(...,"+offset+","+length+") ENTER");
			flushPendingEnd();
			validateIntBlock();
			writeIntBlockImpl(buffer,offset,length);
			if (TRACE) TOUT.println("writeIntBlock() LEAVE");	
		};
		@Override public void writeIntBlock(int v)throws IOException
		{		
			if (TRACE) TOUT.println("writeIntBlock("+v+") ENTER");
			flushPendingEnd();
			validateIntBlock();
			writeIntBlockImpl(v);
			if (TRACE) TOUT.println("writeIntBlock() LEAVE");
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
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("writeLongBlock(...,"+offset+","+length+") ENTER");
			flushPendingEnd();
			validateLongBlock();
			writeLongBlockImpl(buffer,offset,length);
			if (TRACE) TOUT.println("writeLongBlock() LEAVE");			
		};
		@Override public void writeLongBlock(long v)throws IOException
		{
			if (TRACE) TOUT.println("writeLongBlock("+v+") ENTER");
			flushPendingEnd();
			validateLongBlock();
			writeLongBlockImpl(v);
			if (TRACE) TOUT.println("writeLongBlock() LEAVE");
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
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("writeFloatBlock(...,"+offset+","+length+") ENTER");
			flushPendingEnd();
			validateFloatBlock();
			writeFloatBlockImpl(buffer,offset,length);
			if (TRACE) TOUT.println("writeFloatBlock() LEAVE");	
		};
		@Override public void writeFloatBlock(float v)throws IOException
		{
			if (TRACE) TOUT.println("writeFloatBlock("+v+") ENTER");
			flushPendingEnd();
			validateFloatBlock();
			writeFloatBlockImpl(v);
			if (TRACE) TOUT.println("writeFloatBlock() LEAVE");
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
			assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("writeDoubleBlock(...,"+offset+","+length+") ENTER");
			flushPendingEnd();
			validateDoubleBlock();
			writeDoubleBlockImpl(buffer,offset,length);
			if (TRACE) TOUT.println("writeDoubleBlock() LEAVE");	
		};
		@Override public void writeDoubleBlock(double v)throws IOException
		{
			if (TRACE) TOUT.println("writeDoubleBlock("+v+") ENTER");
			flushPendingEnd();
			validateDoubleBlock();
			writeDoubleBlockImpl(v);
			if (TRACE) TOUT.println("writeDoubleBlock() LEAVE");
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
			assert(characters.length()>=offset+length):"Out of buffer operation: characters.length="+characters.length()+", offset="+offset+", length="+length;
			if (TRACE) TOUT.println("writeString(...,"+offset+","+length+") ENTER");
			flushPendingEnd();
			validateStringBlock();
			writeStringImpl(characters,offset,length);	
			if (TRACE) TOUT.println("writeString() LEAVE");
		};
		@Override public void writeString(char c)throws IOException
		{
			if (TRACE) TOUT.println("writeString("+c+") ENTER");
			flushPendingEnd();
			validateStringBlock();
			writeStringImpl(c);
			if (TRACE) TOUT.println("writeString() LEAVE");
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
			if (TRACE) TOUT.println("flush() ENTER");
			//handle end-begin optimization
			flushPendingEnd();
			//do low level flushing.
			if (TRACE) TOUT.println("flush()->fluhsImpl()");
			flushImpl();
			if (TRACE) TOUT.println("flush() LEAVE");
		}
		
		/** {@inheritDoc}
		   Handles state and delegates to {@link #closeImpl} and {@link #flush}
		   when necessary
		*/
		@Override public void close()throws IOException
		{
			if (TRACE) TOUT.println("close() ENTER");
			if (isClosed())
			{
				 if (TRACE) TOUT.println("close(), already closed, LEAVE");
				 return;
			};
			if (isOpen())
			{
				 if (TRACE) TOUT.println("close(), is open, flushing");
				 flush();
			};
			super.close();
			if (TRACE) TOUT.println("close() LEAVE");
		};
}