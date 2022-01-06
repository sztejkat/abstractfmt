package sztejkat.abstractfmt;
import java.io.IOException;

/**
		An implementation of {@link ISignalWriteFormat} over the
		{@link IIndicatorWriteFormat}
*/
public abstract class ASignalWriteFormat implements ISignalWriteFormat
{		
					/* -------------------------------------------------------
					
							State graph.
					
					-------------------------------------------------------*/
					private enum TState
					{		
							NOT_OPEN(),
							READY(TState.CAN_START_BLOCK),
							
							BOOLEAN(TState.ELEMENT+TState.CAN_START_BLOCK, TIndicator.TYPE_BOOLEAN, TIndicator.FLUSH_BOOLEAN),
							BYTE(TState.ELEMENT+TState.CAN_START_BLOCK,  TIndicator.TYPE_BYTE,TIndicator.FLUSH_BYTE),
							CHAR(TState.ELEMENT+TState.CAN_START_BLOCK,  TIndicator.TYPE_CHAR,TIndicator.FLUSH_CHAR),
							SHORT(TState.ELEMENT+TState.CAN_START_BLOCK,  TIndicator.TYPE_SHORT,TIndicator.FLUSH_SHORT),
							INT(TState.ELEMENT+TState.CAN_START_BLOCK,  TIndicator.TYPE_INT,TIndicator.FLUSH_INT),
							LONG(TState.ELEMENT+TState.CAN_START_BLOCK,  TIndicator.TYPE_LONG,TIndicator.FLUSH_LONG),
							FLOAT(TState.ELEMENT+TState.CAN_START_BLOCK, TIndicator.TYPE_FLOAT, TIndicator.FLUSH_FLOAT),
							DOUBLE(TState.ELEMENT+TState.CAN_START_BLOCK,  TIndicator.TYPE_DOUBLE,TIndicator.FLUSH_DOUBLE),
							
							/** State indicating that begin signal
							was just written */
							BEGIN(TState.CAN_START_BLOCK),
							/** State indicating that writing "end signa"
							was requested but it is pending for writing due
							to end-begin optimization
							*/
							END_PENDING(),
							/** State indicating that end signal
							was written */
							END(TState.CAN_START_BLOCK),
							/** State indicating that initial boolean block write
							was written. Each type of block has own state */
							BOOLEAN_BLOCK(TState.BLOCK, TIndicator.TYPE_BOOLEAN_BLOCK, TIndicator.FLUSH_BOOLEAN_BLOCK),
							BYTE_BLOCK(TState.BLOCK,  TIndicator.TYPE_BYTE_BLOCK,TIndicator.FLUSH_BYTE_BLOCK),
							CHAR_BLOCK(TState.BLOCK,  TIndicator.TYPE_CHAR_BLOCK,TIndicator.FLUSH_CHAR_BLOCK),
							SHORT_BLOCK(TState.BLOCK,  TIndicator.TYPE_SHORT_BLOCK,TIndicator.FLUSH_SHORT_BLOCK),
							INT_BLOCK(TState.BLOCK,  TIndicator.TYPE_INT_BLOCK,TIndicator.FLUSH_INT_BLOCK),
							LONG_BLOCK(TState.BLOCK,  TIndicator.TYPE_LONG_BLOCK,TIndicator.FLUSH_LONG_BLOCK),
							FLOAT_BLOCK(TState.BLOCK, TIndicator.TYPE_FLOAT_BLOCK, TIndicator.FLUSH_FLOAT_BLOCK),
							DOUBLE_BLOCK(TState.BLOCK,  TIndicator.TYPE_DOUBLE_BLOCK,TIndicator.FLUSH_DOUBLE_BLOCK),
							/** If closed and unusable */
							CLOSED();
							
							/** Flags combination */
							final int FLAGS;
							/** Indicator associated with state, if any.
							Used to select type indicator. */
							final TIndicator TYPE_INDICATOR;
							/** Indicator associated with state, if any.
							Used to select flush indicator. */
							final TIndicator FLUSH_INDICATOR;							
							
							/** Flag indicating that state represents block operation
							in progress */
							static final int BLOCK = 0x01;
							/** Flag indicating that state represents elementary
							operation executed */
							static final int ELEMENT = 0x02;
							/** Flag indicating that state represents state in which
							block can be started */
							static final int CAN_START_BLOCK = 0x04;
							
							
							TState(int f,
									TIndicator type_indicator,
									TIndicator flush_indicator
									)
							{ 
								this.FLAGS=f;
								this.TYPE_INDICATOR=type_indicator;
								this.FLUSH_INDICATOR=flush_indicator;								
							};
							TState(int f){ this(f,null,null); };
							TState(){ this(0,null,null); };	
					};
					
					
					
					/** An output to which write all data.	*/
					protected final IIndicatorWriteFormat output;					
					/** Set with {@link #setMaxSignalNameLength} */
					private int max_name_length = 1024;
					/** See constructor */
					private int max_events_recursion_depth;				
					/** Keeps track of current events depth */
					private int current_depth;
					/** A names registry, filled up with names, first
					null indicates end of used area. Null if registry is not used.*/
					private final String [] names_registry;
					/** A names registry, hash codes for registered names.
					Null if registry is not used.*/
					private final int [] names_registry_hash;
					/** State variable initially {@link TState#READY}*/
					private TState state;
				
		/* *******************************************************
		
		
						Construction
		
		
		*********************************************************/
		/** Creates write format
		@param output output to set. If null will be set to <code>(IIndicatorWriteFormat)this</code>.
		@throws AssertionError error if parameters do not match.
		@see IIndicatorWriteFormat#getMaxRegistrations
		*/
		protected ASignalWriteFormat(
								IIndicatorWriteFormat output
									 )
		{
			if (output==null) output = (IIndicatorWriteFormat)this;
			this.output = output;
			
			int names_registry_size = output.getMaxRegistrations();
			if (names_registry_size>0)
			{
				this.names_registry 	 = new String[names_registry_size];
				this.names_registry_hash = new int[names_registry_size];
			}else
			{
				this.names_registry = null;
				this.names_registry_hash=null;
			}
			this.max_events_recursion_depth=max_events_recursion_depth;			
			this.state = TState.NOT_OPEN;
		};
		/* ********************************************************
		
		
				Tunable services	
				
		
		**********************************************************/
		/** Called inside {@link #close} after flushing, 
		but only once in a life-time.
		Default closes {@link #output}
		@see #close
		@throws IOException if failed.
		*/
		protected void closeOnce()throws IOException
		{
			output.close();
		};
		/** Called inside {@link #open} but only one time
		Default opens {@link #output}
		@see #open
		@throws IOException if failed.
		*/
		protected void openOnce()throws IOException
		{
			output.open();
		};
		
		/* *******************************************************
		
		
						Names registry manipulation
		
		
		*********************************************************/
		/** Attempts to locate name into an index.
		@param name name to look for
		@return non-negative indicating position at which 
				it is found in {@link #names_registry}, -1 if it is 
				not found
		*/				
		private int findInIndex(String name)
		{
			assert(name!=null);
			int hash = name.hashCode();
			for(int i=names_registry.length;--i>=0;)
			{
				//Note: it is possible that some strings have zero hash.				
				if (names_registry_hash[i]==hash)
				{
					String n = names_registry[i];
					if ((n!=null)&&(n.equals(name))) return i;
				}
			};
			return -1;
		};
		/** Attempts to put name into an index, assuming it is not there.
		@param name name to put
		@return non-negative indicating position at which 
				it is found in {@link #names_registry}, -1 if it could not be put.
		*/
		private int putToIndex(String name)
		{
			assert(name!=null);
			assert(findInIndex(name)==-1):"already in index";
			//Note: This time we need to iterate from zero, because we need
			//to assign numbers from zero upwards in writeRegisterName
			for(int i=0, n=names_registry.length; i<n;i++)
			{
				if (names_registry[i]==null)
				{
					names_registry[i] = name;
					names_registry_hash[i]=name.hashCode();
					return i;
				}
			};
			return -1;
		};
		
		/* ********************************************************
		
		
				State information and checking.
				
		
		**********************************************************/		
		/** Throws if closed
		@throws EClosed if closed
		*/
		private final void validateNotClosed()throws EClosed
		{
			if (state==TState.CLOSED) throw new EClosed("Already closed");
		};
		/** Throws if not open
		@throws ENotOpen if not open
		*/
		private final void validateOpen()throws ENotOpen
		{
			if (state==TState.NOT_OPEN) throw new ENotOpen("Not opened.");
		};
		/** Throws if not open or closed.
		@throws ENotOpen if not open
		@throws EClosed if closed
		*/
		private final void validateReady()throws EClosed,ENotOpen
		{
		 	validateOpen();
			validateNotClosed();
		};
		
		/* ********************************************************
		
		
				State manipulation 
				
		
		**********************************************************/
		
		
		/** If state is {@link TState#END_PENDING}
		writes an end indicator and togles state to {@link TState#END}.
		Used to optimize end-being sequence.
		@throws IOException if idicator write failed
		@see IIndicatorWriteFormat#writeEnd
		*/
		private void flushPendingEnd()throws IOException
		{
			if (state==TState.END_PENDING)
			{
				state=TState.END;
				output.writeEnd();
			};
		};
		/** Checks if any block operation is in progress, and if it 
		is and flushing is enable writes flush indicator of correct type. 
		@throws IOException if called method thrown 
		*/
		private void flushBlockOperation()throws IOException
		{			
			if((state.FLAGS & TState.BLOCK)!=0)
			{ 
				output.writeFlush(state.FLUSH_INDICATOR);
			};
		};
		
		
		/* ********************************************************
		
		
				ISignalWriteFormat
				
		
		**********************************************************/
		/* ---------------------------------------------------------
					Generic
		---------------------------------------------------------*/
		@Override public void setMaxSignalNameLength(int characters)
		{ 
			assert(characters>0);
			assert(characters<=getMaxSupportedSignalNameLength());
			this.max_name_length = characters; 
		};		
		@Override final public int getMaxSignalNameLength()
		{ 
			return this.max_name_length;
		};
		/** Returns what output returns.	*/
		@Override final public boolean isDescribed(){ return output.isDescribed(); };
		/** Returns what output returns.	*/
		@Override final public int getMaxSupportedSignalNameLength(){ return output.getMaxSupportedSignalNameLength(); };
		@Override public void setMaxEventRecursionDepth(int max_events_recursion_depth)throws IllegalStateException
		{
			assert(max_events_recursion_depth>=0):"max_events_recursion_depth="+max_events_recursion_depth;
			this.max_events_recursion_depth=max_events_recursion_depth;
			if ((max_events_recursion_depth>0)&&(max_events_recursion_depth<current_depth))
				throw new IllegalStateException("Too deep events recursion, limit set to "+max_events_recursion_depth);			
		};
		@Override final public int getMaxEventRecursionDepth(){ return max_events_recursion_depth; };
		
		/* ---------------------------------------------------------
					Signals
		---------------------------------------------------------*/
		/** Dispatcher method, sending depending one end-begin optimization
		to correct writes in {@link IIndicatorWriteFormat}
		@param name signal name to write
		@throws IOException if failed.
		@see IIndicatorWriteFormat#writeEndBeginDirect
		@see IIndicatorWriteFormat#writeBeginDirect
		*/
		private void writeBeginDirect(String name)throws IOException
		{			
			if (state == TState.END_PENDING)
			{
				output.writeEndBeginDirect( name );
			}else
			{
				output.writeBeginDirect( name );
			};
		};
		/** Dispatcher method, sending depending one end-begin optimization
		to correct writes in {@link IIndicatorWriteFormat}
		@param name signal name to write
		@param number signal number to write
		@throws IOException if failed.
		@see IIndicatorWriteFormat#writeEndBeginRegister
		@see IIndicatorWriteFormat#writeBeginRegister
		*/
		private void writeBeginRegister(String name, int number)throws IOException
		{
			if (state == TState.END_PENDING)
			{
				output.writeEndBeginRegister( name, number );
			}else
			{
				output.writeBeginRegister( name, number);
			};
		};
		/** Dispatcher method, sending depending one end-begin optimization
		to correct writes in {@link IIndicatorWriteFormat}
		@param number signal number to write
		@throws IOException if failed.
		@see IIndicatorWriteFormat#writeEndBeginUse
		@see IIndicatorWriteFormat#writeBeginUse 
		*/
		private void writeBeginUse(int number)throws IOException
		{
			if (state == TState.END_PENDING)
			{
				output.writeEndBeginUse( number );
			}else
			{
				output.writeBeginUse( number);
			};
		};
		
		
		
		@Override public void begin(String signal,boolean do_not_optimize)throws IOException
		{
			validateReady();
			
			assert(signal!=null):"null signal name";
			//Validate length
			if (signal.length()>max_name_length) 
				throw new IllegalArgumentException("Signal name too long. \""+signal+"\", max="+max_name_length);
			//validate depth
			if ((max_events_recursion_depth>0)&&(max_events_recursion_depth<=current_depth))
				throw new IllegalStateException("Too deep events recursion, limit set to "+max_events_recursion_depth);
			
			
			try{//Use "finally" to set state transition once all is done, regardless if failed or not.
			
				//Now we need to perform all pending actions.
				//Especially we need to close pending blocks
				flushBlockOperation();
			
				//now proceed with names registration, if registry is enabled.
				if (names_registry!=null)
				{
					//even if user requested to not attempt to register name.
					//it may however be already registered, so let us check.
					
					//attempt to use registered name
					int registered_index = findInIndex(signal);
					if (registered_index==-1)
					{
						//Name is not registered, try to register it.
						if (do_not_optimize)
						{
							//Now always write direct.
							writeBeginDirect(signal);
						}else
						{
							registered_index = putToIndex(signal);
							if (registered_index!=-1)
							{
								//name is registered.
								writeBeginRegister(signal, registered_index);
							}else
							{
								//we failed to register, need to store it directly
								writeBeginDirect(signal);
							};
						};
					}else
					{
						//we have the name registered
						writeBeginUse(registered_index);
					};
				}else
				{
						writeBeginDirect(signal);
				};
			 }finally{ current_depth++; state=TState.BEGIN; };
		};
		/** Implemented in such a way, that actuall writing of
		end indicator is put-off to let it to make some end-begin optimization.
		Basically it is delayed till nearest 
		{@link #begin}, {@link #end} or any primitive write.
		@see #flushPendingEnd
		@see #flushBlockOperation
		@see TState#END_PENDING
		*/
		@Override public void end()throws IOException
		{
			validateReady();
			
			//Flush any pending end signal operation.
			//This operation should be done prior to depth
			//test, because in end();end() sequence first end
			//is valid, but pending. It should reach stream, even
			//if we barf later.
			flushPendingEnd();	
			
			//We need to close pending blocks.
			flushBlockOperation();
			
			if (current_depth==0) throw new IllegalStateException("Can't do end(), no event is active");
			current_depth--;		
				
			//and we need to put this operation on hold so end-begin can be optimized
			state = TState.END_PENDING;
		};
		
		/* ---------------------------------------------------------
					Elementary primitives.
		---------------------------------------------------------*/
		/** Checks if current state allows any elementary primitive write,
		 toggles state to specified state and writes type indicator according to <code>state</code>.
		@param state state to set to state variable.
		@throws IllegalStateException if not.
		@throws IOException if needed to flush pending end indicator and it failed.
		*/
		private void startElementaryPrimitiveWrite(TState state)throws IllegalStateException,IOException
		{			
			validateReady();
			flushPendingEnd();//this might be pending.
			
			if ((this.state.FLAGS & TState.BLOCK)!=0) 
				throw new IllegalStateException("Cannot do elementary primitive write when block write is in progress.");
			this.state = state;
			
			output.writeType(state.TYPE_INDICATOR);
		};
		
		/** Writes elementary primitive flush if known and necessary. Takes information
		form state 
		@throws IOException if failed. */
		private void writeElementaryFlush()throws IOException
		{
			output.writeFlush(state.FLUSH_INDICATOR);
		};
		@Override public  void writeBoolean(boolean v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.BOOLEAN);			
			output.writeBoolean(v);
			writeElementaryFlush();
		};
		@Override public  void writeByte(byte v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.BYTE);			
			output.writeByte(v);
			writeElementaryFlush();
		};
		@Override public  void writeChar(char v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.CHAR);			
			output.writeChar(v);
			writeElementaryFlush();
		};
		@Override public  void writeShort(short v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.SHORT);			
			output.writeShort(v);
			writeElementaryFlush();
		};
		@Override public  void writeInt(int v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.INT);			
			output.writeInt(v);
			writeElementaryFlush();
		};
		@Override public  void writeLong(long v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.LONG);			
			output.writeLong(v);
			writeElementaryFlush();
		};
		@Override public  void writeFloat(float v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.FLOAT);			
			output.writeFloat(v);
			writeElementaryFlush();
		};
		@Override public void writeDouble(double v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.DOUBLE);			
			output.writeDouble(v);
			writeElementaryFlush();
		};
		/* ---------------------------------------------------------
					Blocks
		---------------------------------------------------------*/
		/** Like {@link #startElementaryPrimitiveWrite} but for blocks.
		Correctly handles continuation. 
		@param target_state expected state after return
		@throws IOException if failed.
		*/
		private void startBlockPrimitiveWrite(TState target_state)throws IOException
		{
			validateReady();
			if (state==target_state) return;	//continuation
			//initialization
			flushPendingEnd();
			if ((state.FLAGS & TState.CAN_START_BLOCK)==0)
				throw new IllegalStateException("Can't start block in "+state);
			if (current_depth==0)
				throw new IllegalStateException("Can't start block write if there is no event active");				
			this.state =  target_state;
			output.writeType(state.TYPE_INDICATOR);
		};
		
		@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.BOOLEAN_BLOCK);	
			output.writeBooleanBlock(buffer,offset,length);
		};
		@Override public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.BYTE_BLOCK);	
			output.writeByteBlock(buffer,offset,length);
		};
		@Override public void writeByteBlock(byte data)throws IOException
		{		
			startBlockPrimitiveWrite(TState.BYTE_BLOCK);	
			output.writeByteBlock(data);
		};
		@Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.CHAR_BLOCK);	
			output.writeCharBlock(buffer,offset,length);
		};
		@Override public void writeCharBlock(CharSequence characters,  int offset, int length)throws IOException
		{		
			assert(characters!=null):"characters==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=characters.length()):"characters.length="+characters.length()+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.CHAR_BLOCK);	
			output.writeCharBlock(characters,offset, length);
		};
		
		@Override public void writeShortBlock(short [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.SHORT_BLOCK);	
			output.writeShortBlock(buffer,offset,length);
		};
		
		@Override public void writeIntBlock(int [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.INT_BLOCK);	
			output.writeIntBlock(buffer,offset,length);
		};
		
		@Override public void writeLongBlock(long [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.LONG_BLOCK);	
			output.writeLongBlock(buffer,offset,length);
		};
		
		@Override public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.FLOAT_BLOCK);	
			output.writeFloatBlock(buffer,offset,length);
		};
		
		@Override public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.DOUBLE_BLOCK);	
			output.writeDoubleBlock(buffer,offset,length);
		};
		/* **********************************************************************
		
		
				Closeable
		
		
		**********************************************************************/		
		/** Overriden to flush, toggle state to closed and ensure that
		it happens only once.
		<p>
		Calls {@link #closeOnce}
		*/
		@Override public final void close()throws IOException
		{ 
			if (state!=TState.CLOSED)
			{
				try{
					if (state!=TState.NOT_OPEN)
										flush(); 					
				}finally{ 
						try{
							closeOnce();
							}finally{ state=TState.CLOSED; }; 
					};
			};
		};
		@Override public final void open()throws IOException
		{
			validateNotClosed();
			if (state==TState.NOT_OPEN)
			{
				openOnce();
				state = TState.READY;//intentionally not in finally
			}; 
		};
		/* **********************************************************************
		
		
				Flushable
		
				
		**********************************************************************/
		/** Flushes the output */
		@Override public void flush()throws IOException
		{
			validateReady();
			flushPendingEnd();
			output.flush();
		};	
};