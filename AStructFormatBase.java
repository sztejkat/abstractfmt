package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;

/**
	A common denominator for read and write streams, 
	basically open/close support
*/
abstract class AStructFormatBase extends AFormatLimits implements Closeable
{
					/** Tracks format open state */
					private boolean opened;
					/** Tracks format closes state */
					private boolean closed;
					/** Non-null if stream is doing a block write. Carries block type */
					private TContentType block_type;
		/* ***********************************************************************
		
				Services required from subclasses
		
		
		************************************************************************/
		/* -------------------------------------------------
			State related
		-------------------------------------------------*/
		/** Called by {@link #open} only when necessary 
		@throws IOException if failed */
		protected abstract void openImpl()throws IOException;
		/** Called by {@link #close} only when necessary.
		@throws IOException if failed  */
		protected abstract void closeImpl()throws IOException;
		/* -------------------------------------------------
			Block related
		-------------------------------------------------*/
		/** Invoked when {@link #validateBooleanBlock} is used for a first time
		inside a structure and block operation is started.
		<p>
		Some implementations may find it usefull. 
		<p>
		Default: doesn't do anything 
		@throws IOException if failed.*/
		protected void startBooleanBlock()throws IOException{};
		/** Invoked by {@link #terminatePendingBlockOperation} when boolean block is active and is to be finished.
		Some implementations may find it usefull. Usually boolean[] blocks can be optimized
		this way into bit-streams. 
		<p>
		Default: doesn't do anything 
		@throws IOException if failed.*/
		protected void endBooleanBlock()throws IOException{};
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startByteBlock()throws IOException{};
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endByteBlock()throws IOException{};
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startCharBlock()throws IOException{};
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endCharBlock()throws IOException{};
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startShortBlock()throws IOException{};
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endShortBlock()throws IOException{};
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startIntBlock()throws IOException{};
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endIntBlock()throws IOException{};
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startLongBlock()throws IOException{};
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endLongBlock()throws IOException{};
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startFloatBlock()throws IOException{};
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endFloatBlock()throws IOException{};
		
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startDoubleBlock()throws IOException{};
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endDoubleBlock()throws IOException{};
		
		/** See {@link #startBooleanBlock}
		@throws IOException --//-- */
		protected void startStringBlock()throws IOException{};
		/** See {@link #endBooleanBlock}
		@throws IOException --//--*/
		protected void endStringBlock()throws IOException{};
		
		/* ***********************************************************************
		
				Services for subclasses
		
		
		************************************************************************/
		/* ----------------------------------------------------------------
					State validation
		----------------------------------------------------------------*/
		/** Throws if closed 
		@throws EClosed .*/
		protected final void validateNotClosed()throws EClosed
		{
			if (closed) throw new EClosed("Already closed");
		};
		/** @return set to true by {@link #close} */
		protected final boolean isClosed(){ return closed; };
		
		/** Throws if not opened 
		@throws ENotOpen .*/
		protected final void validateOpen()throws ENotOpen
		{
			if (!opened) throw new ENotOpen("Not open yet");
		};
		/** @return set to true by {@link #open} */
		protected final boolean isOpen(){ return opened; };
				
		/** Calls {@link #validateOpen} and {@link #validateNotClosed}
		@throws EClosed if already closed
		@throws ENotOpen if not open yet.
		*/
		protected final void validateUsable()throws EClosed,ENotOpen
		{
			validateOpen();
		 	validateNotClosed();
		};
		/* ----------------------------------------------------------------
					Block state validation and managment
		----------------------------------------------------------------*/
		/** Ensures that elementary primitive operations are allowed
		@throws EClosed if already closed
		@throws ENotOpen if not open yet.
		@throws IllegalStateException if not allowed.
		@see #validateUsable
		@see #validateNoBlockOp
		*/
		final void validateCanDoElementaryOp()throws IllegalStateException,EClosed,ENotOpen
		{
			validateUsable();
			validateNoBlockOp();
		}
		/** Throws is any block operation is in progress 
		@throws IllegalStateException if true */
		final void validateNoBlockOp()throws IllegalStateException
		{
			if (block_type!=null) throw new IllegalStateException("Block operation in progress");
		};
		/** Terminates block operation, if any
		by calling <code>endXXXBlock()</code>.
		<p>
		To be invoked by a code which is handling signal processing.
		@throws IOException if an eventual termination of block failed.
		@see #block_type
		@see #endBooleanBlock */
		final void terminatePendingBlockOperation()throws IOException
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
		
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startBooleanBlock */
		final void validateBooleanBlock()throws IOException, IllegalStateException
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
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startByteBlock */
		final void validateByteBlock()throws IOException, IllegalStateException
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
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startCharBlock */
		final void validateCharBlock()throws IOException, IllegalStateException
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
			/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startShortBlock */
		final void validateShortBlock()throws IOException, IllegalStateException
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
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startIntBlock */
		final void validateIntBlock()throws IOException, IllegalStateException
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
			/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startLongBlock */
		final void validateLongBlock()throws IOException, IllegalStateException
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
			/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startFloatBlock */
		final void validateFloatBlock()throws IOException, IllegalStateException
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
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startDoubleBlock */
		final void validateDoubleBlock()throws IOException, IllegalStateException
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
		/** Validates if can do this block operation and initializes it if necessary
		@throws IOException if stream is not open/closed or low level failed.
		@throws IllegalStateException if can't do this block now.
		@see #startDoubleBlock */
		final void validateStringBlock()throws IOException, IllegalStateException
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
		/* ****************************************************
		
			State operations
		
		****************************************************/
		/** 
		   Follows {@link IStructReadFormat#open} and
		   {@link IStructWriteFormat#open} API.
		   <p>
		   Handles state and delegates to {@link #openImpl}
		   when necessary.
		   @throws IOException if failed
		   @throws EClosed if already closed.
		*/
		public void open()throws IOException,EClosed
		{
			validateNotClosed();
			if (opened) return;			
			openImpl();
			opened = true; //not set if openImpl failed.
						 //low level close will be invoked regardless.
		};
		/** {@inheritDoc}
		   Handles state and delegates to {@link #closeImpl}
		   when necessary
		*/
		@Override public void close()throws IOException
		{
			if (closed) return;
			
			try{					
					closeImpl();
					super.close(); //zero depth tracking. We can skip it if failed tough.
					//always set closed, even if low level failed.
				}finally{ closed = true; }
		};
};
