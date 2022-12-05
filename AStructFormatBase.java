package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.Closeable;
import java.io.IOException;

/**
	A common denominator for read and write streams, 
	basically open/close support.
	
	<p>
	This class is a state tracking machine which do:
	<ul>
		<li>redirects {@link #close} to {@link #closeImpl} to ensure an actual close it done
			only once;</li>
		<li>redirects {@link #open} to {@link #openImpl} ensuring proper prequisites;</li>
		<li>provides set of {@link #validateBooleanBlock}, 
			{@link #validateByteBlock}, and etc which are to be used by subclasses 
			right inside block operations. Those methods do ensure that subsequent
			attempts to call them will fail/pass according to contract 
			and make sure that {@link #validateCanDoElementaryOp} wont let be run.
			See also {@link #terminatePendingBlockOperation};</li>
	    <li>provides {@link #validateCanDoElementaryOp} to be run at the begining
	       of elementary primitive operations. This method checks if primitive op
	       can be run;</li>	       
	    <li>provides set of hooks {@link #startBooleanBlock}/{@link #endBooleanBlock}
	    	which are invoked by state validation code and can be used by subclasses
	    	to actually write apropriate codes to a stream before block operation
	    	is initiated and after it is finished.</li>
	</ul>
*/
abstract class AStructFormatBase extends AFormatLimits implements Closeable
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(AStructFormatBase.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AStructFormatBase.",AStructFormatBase.class) : null;
 
         		/** Used to for state tracking
         		when managing block operations.
         		*/
         		private static enum TBlockType
				{
					BOOLEAN_BLK(),
					BYTE_BLK(),
					CHAR_BLK(),
					STRING(),
					SHORT_BLK(),
					INT_BLK(),
					LONG_BLK(),
					FLOAT_BLK(),
					DOUBLE_BLK();
				};
					/** Tracks format open state */
					private boolean opened;
					/** Tracks format closes state */
					private boolean closed;
					/** Non-null if stream is doing a block write. Carries block type */
					private TBlockType block_type;
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
		/** Ensures that elementary primitive operations are allowed.
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
		Is to be invoked before {@link #enterStruct}/{@link #leaveStruct}
		@throws IOException if an eventual termination of block failed.
		@see #block_type
		@see #endBooleanBlock */
		final void terminatePendingBlockOperation()throws IOException
		{
			if (TRACE) TOUT.println("terminatePendingBlockOperation() ENTER");
			if (block_type!=null)
			{
			    if (TRACE) TOUT.println("terminatePendingBlockOperation() ->Terminating block of "+block_type);
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
					default: throw new AssertionError("unknown enum "+block_type);
				};
				if (TRACE) TOUT.println("terminatePendingBlockOperation() LEAVE");
				block_type = null;
			}else
			{
				if (TRACE) TOUT.println("terminatePendingBlockOperation(), no block in progress LEAVE");
			}
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
				block_type=TBlockType.BOOLEAN_BLK;
				if (TRACE) TOUT.println("validateBooleanBlock() -> startBooleanBlock()");
				startBooleanBlock();
			}else
				//validate type consistency
				if (block_type!=TBlockType.BOOLEAN_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
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
				block_type=TBlockType.BYTE_BLK;
				if (TRACE) TOUT.println("validateByteBlock() -> startByteBlock()");
				startByteBlock();
			}else
				//validate type consistency
				if (block_type!=TBlockType.BYTE_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
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
				block_type=TBlockType.CHAR_BLK;
				if (TRACE) TOUT.println("validateCharBlock() -> startCharBlock()");
				startCharBlock();
			}else
				//validate type consistency
				if (block_type!=TBlockType.CHAR_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
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
				block_type=TBlockType.SHORT_BLK;
				if (TRACE) TOUT.println("validateShortBlock() -> startShortBlock()");
				startShortBlock();
			}else
				//validate type consistency
				if (block_type!=TBlockType.SHORT_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
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
				block_type=TBlockType.INT_BLK;
				if (TRACE) TOUT.println("validateIntBlock() -> startIntBlock()");
				startIntBlock();
			}else
				//validate type consistency
				if (block_type!=TBlockType.INT_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
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
				block_type=TBlockType.LONG_BLK;
				if (TRACE) TOUT.println("validateLongBlock() -> startLongBlock()");
				startLongBlock();
			}else
				//validate type consistency
				if (block_type!=TBlockType.LONG_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
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
				block_type=TBlockType.FLOAT_BLK;
				if (TRACE) TOUT.println("validateFloatBlock() -> startFloatBlock()");
				startFloatBlock();
			}else
				//validate type consistency
				if (block_type!=TBlockType.FLOAT_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
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
				block_type=TBlockType.DOUBLE_BLK;
				if (TRACE) TOUT.println("validateDoubleBlock() -> startDoubleBlock()");
				startDoubleBlock();
			}else
				//validate type consistency
				if (block_type!=TBlockType.DOUBLE_BLK) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
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
				block_type=TBlockType.STRING;
				if (TRACE) TOUT.println("validateStringBlock() -> startStringBlock()");
				startStringBlock();
			}else
				//validate type consistency
				if (block_type!=TBlockType.STRING) throw new IllegalStateException("Incompatible block operation "+block_type+" in progress");
		}
		/* ****************************************************
		
			AFormatLimits
			
				Note: we do NOT override enterStruct/leaveStruct
				to invoke terminatePendingBlockOperation()
				because the processing of this termination
				differs during writing and reading:
				
				
					writing:
							we DO know if we begin or end so:
							
							begin:
								terminatePendingBlockOperation(); --> generates some I/O on block end
								enterStruct();
								write begin						  --> generates some I/O for a signal
							end:
								terminatePendingBlockOperation(); --> generates some I/O on block end
								leaveStruct();
								write end						  --> generates some I/O for a signal
								
					reading:
					
							we do NOT know when we request next signal to be read
							if it will be begin or end
							
								terminatePendingBlockOperation(); --> generates some I/O to finish reading block, possibly
								move and read signal		      --> also generates I/O
								decide if enter or leave
		
		****************************************************/
		
	    
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
			if (TRACE) TOUT.println("open() ENTER");
			validateNotClosed();
			if (opened)
			{
				if (TRACE) TOUT.println("open(), already open LEAVE");
				 return;
		    };			
		    if (TRACE) TOUT.println("open()->openImpl()");
			openImpl();
			opened = true; //not set if openImpl failed.
						 //low level close will be invoked regardless.
			if (TRACE) TOUT.println("open() LEAVE");
		};
		/** {@inheritDoc}
		   Handles state and delegates to {@link #closeImpl}
		   when necessary
		*/
		@Override public void close()throws IOException
		{
			if (TRACE) TOUT.println("close() ENTER");
			if (closed) 
			{
				if (TRACE) TOUT.println("close(), alreay closed LEAVE");
				return;
			};
			
			try{					
					if (TRACE) TOUT.println("close()->closeImpl()");
					closeImpl();
					if (TRACE) TOUT.println("close()->super.close()");
					super.close(); //zero depth tracking. We can skip it if failed tough.
					//always set closed, even if low level failed.
				}finally{ closed = true; }
			if (TRACE) TOUT.println("close()  LEAVE");
		};
};
