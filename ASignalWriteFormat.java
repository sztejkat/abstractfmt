package sztejkat.abstractfmt;
import java.io.IOException;

/**
		An implementation of {@link ISignalWriteFormat} over the
		{@link #IIndicatorWriteFormat}
*/
public abstract class ASignalWriteFormat implements ISignalWriteFormat
{		
					/* -------------------------------------------------------
					
							State graph.
					
					-------------------------------------------------------*/
					private enum TState
					{
							/** State indicating that elementary
							primitive element was written (of unspecified type). This is 
							also an initial state of a stream */
							NONE(TState.ELEMENT+TState.CAN_START_BLOCK),
							
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
					/** See constructor */
					private final int max_name_length;
					/** See constructor */
					private final int max_events_recursion_depth;				
					/** Keeps track of current events depth */
					private int current_depth;
					/** A names registry, filled up with names, first
					null indicates end of used area. Null if registry is not used.*/
					private final String [] names_registry;
					/** A names registry, hash codes for registered names.
					Null if registry is not used.*/
					private final int [] names_registry_hash;
					/** State variable initially {@link TState#NONE}*/
					private TState state;
				
		/* *******************************************************
		
		
						Construction
		
		
		*********************************************************/
		/** Creates write format
		@param names_registry_size maximum number of names 
			to register if compact names should be implemented.			
			<p>
			Zero to disable registry and always write names directly.
			<p>
			This value must not be greater than one returned
			by <code>output.getMaxRegistrations</code> and can be
			used to limit registry range if required.
		@param max_name_length greater or equal to 8. Maximum length of names
			to be accepted in {@link #begin(String, boolean)} and be passed to
			{@link #writeSignalNameData}
		@param max_events_recursion_depth specifies the allowed depth of elements
			nesting. Zero disables limit, 1 sets limit to: "no nested elements allowed",
			2 allows element within an element and so on. If this limit is exceed
			the {@link #begin(String,boolean)} will throw <code>IllegalStateException</code>.
		@param output output to set. If null will be set to <code>(IIndicatorWriteFormat)this</code>.
		@throws Assertion error if parameters do not match.
		@see IIndicatorWriteFormat#getMaxRegistrations
		*/
		protected ASignalWriteFormat(int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth,
									 IIndicatorWriteFormat output
									 )
		{
			assert(names_registry_size>=0):"names_registry_size="+names_registry_size;
			assert(max_name_length>=8):"max_name_length="+max_name_length;
			assert(max_events_recursion_depth>=0):"max_events_recursion_depth="+max_events_recursion_depth;
			assert( (output==null)
					||
					((output!=null) &&( output.getMaxRegistrations()>=names_registry_size))
				   );
			if (names_registry_size>0)
			{
				this.names_registry 	 = new String[names_registry_size];
				this.names_registry_hash = new int[names_registry_size];
			}else
			{
				this.names_registry = null;
				this.names_registry_hash=null;
			}
			this.max_name_length=max_name_length;
			this.max_events_recursion_depth=max_events_recursion_depth;
			this.output = output==null ? (IIndicatorWriteFormat)this : output;
			
			this.state = TState.NONE;
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
		/** This method is checked to decide if send type
		indicators to output. Described subclasses should
		override it to true.
		<p>
		This implementation returns false.
		@return true to write type information and optionally
		flushes, false to not write.
		@see IIndicatorWriteFormat#writeType
		@see #isPrimitiveFlushing
		*/
		@Override public boolean isDescribed(){ return false; };
		/** This method is checked to decide if a flush indicators
		should be written to a stream. This is an error to return
		true from this method if {@link #isDescribed} returns false.
		<p>
		This implementation returns:
		<pre>
			 {@link #isDescribed} &amp;&amp; output.requiresFlushes()
		</pre>
		@return true to write flushes, false to not write.
		@see IIndicatorWriteFormat#writeFlush
		*/
		public boolean isPrimitiveFlushing(){ return isDescribed() && output.requiresFlushes();  };
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
				if (names_registry_hash[i]==hash)
				{
					if (names_registry[i].equals(name)) return i;
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
		
		/* ********************************************************
		
		
				State manipulation 
				
		
		**********************************************************/
		
		
		/** If state is {@link #STATE_END_PENDING}
		writes an end indicator and togles state to {@link #STATE_END}.
		Used to optimize end-being sequence 
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
			if(((state.FLAGS & TState.BLOCK)!=0)&&(isPrimitiveFlushing()))
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
		@Override public final int getMaxSignalNameLength(){ return max_name_length; };
		
		/* ---------------------------------------------------------
					Signals
		---------------------------------------------------------*/
		/** Dispatcher method, sending depending one end-begin optimization
		to correct writes in {@link IIndicatorWriteFormat}
		@param name 
		@throws IOException . 
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
		@param name --//--
		@param number --//--
		@throws IOException . 
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
		@param number --//--
		@throws IOException . 
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
			validateNotClosed();
			
			assert(signal!=null):"null signal name";
			//Validate length
			if (signal.length()>max_name_length) 
				throw new IllegalArgumentException("Signal name too long. \""+signal+"\", max="+max_name_length);
			//validate depth
			if ((max_events_recursion_depth!=0)&&(max_events_recursion_depth<=current_depth))
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
		{@link #writeEndSignalIndicator} is posponed till it
		can be decided if it can be optimized to
		{@link #writeEndBeginSignalIndicator}. Basically it is delayed till
		nearest {@link #begin}, {@link #end} or any primitive write.
		@see #flush
		*/
		@Override public void end()throws IOException
		{
			validateNotClosed();
			
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
		/** Checks if current state allows any elementary primitive write
		and toggles state to {@link #STATE_PRIMITIVE}
		@param state state to set to state variable.
		@throws IllegalStateException if not.
		@throws IOException if needed to flush pending end indicator and it failed.
		*/
		private void startElementaryPrimitiveWrite(TState state)throws IllegalStateException,IOException
		{			
			validateNotClosed();
			flushPendingEnd();//this might be pending.
			
			if ((this.state.FLAGS & TState.BLOCK)!=0) 
				throw new IllegalStateException("Cannot do elementary primitive write when block write is in progress.");
			this.state = state;
			
			if (isDescribed()) output.writeType(state.TYPE_INDICATOR);
		};
		
		/** Writes primitive flush if known and necessary. Takes information
		form state 
		@throws IOException if failed. */
		private void writeFlush()throws IOException
		{
			if (isPrimitiveFlushing()) output.writeFlush(state.FLUSH_INDICATOR);
		};
		@Override public  void writeBoolean(boolean v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.BOOLEAN);			
			output.writeBoolean(v);
			writeFlush();
		};
		@Override public  void writeByte(byte v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.BYTE);			
			output.writeByte(v);
			writeFlush();
		};
		@Override public  void writeChar(char v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.CHAR);			
			output.writeChar(v);
			writeFlush();
		};
		@Override public  void writeShort(short v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.SHORT);			
			output.writeShort(v);
			writeFlush();
		};
		@Override public  void writeInt(int v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.INT);			
			output.writeInt(v);
			writeFlush();
		};
		@Override public  void writeLong(long v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.LONG);			
			output.writeLong(v);
			writeFlush();
		};
		@Override public  void writeFloat(float v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.FLOAT);			
			output.writeFloat(v);
			writeFlush();
		};
		@Override public void writeDouble(double v)throws IOException
		{
			startElementaryPrimitiveWrite(TState.DOUBLE);			
			output.writeDouble(v);
			writeFlush();
		};
		/* ---------------------------------------------------------
					Blocks
		---------------------------------------------------------*/
		/** Like {@link #startElementaryPrimitiveWrite} but for blocks.
		Correctly handles continuation. */
		private void startBlockPrimitiveWrite(TState target_state)throws IOException
		{
			validateNotClosed();
			if (state==target_state) return;	//continuation
			//initialization
			flushPendingEnd();
			if ((state.FLAGS & TState.CAN_START_BLOCK)==0)
				throw new IllegalStateException("Can't start block in "+state);
			if (current_depth==0)
				throw new IllegalStateException("Can't start block write if there is no event active");				
			this.state =  target_state;
			if (isDescribed()) output.writeType(state.TYPE_INDICATOR);
		};
		
		@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.BOOLEAN_BLOCK);	
			output.writeBooleanBlock(buffer,offset,length);
			writeFlush();
		};
		@Override public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.BYTE_BLOCK);	
			output.writeByteBlock(buffer,offset,length);
			writeFlush();
		};
		@Override public void writeByteBlock(byte data)throws IOException
		{		
			startBlockPrimitiveWrite(TState.BYTE_BLOCK);	
			output.writeByteBlock(data);
			writeFlush();
		};
		@Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.CHAR_BLOCK);	
			output.writeCharBlock(buffer,offset,length);
			writeFlush();
		};
		@Override public void writeCharBlock(CharSequence characters,  int offset, int length)throws IOException
		{		
			assert(characters!=null):"characters==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=characters.length()):"characters.length="+characters.length()+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.CHAR_BLOCK);	
			output.writeCharBlock(characters,offset, length);
			writeFlush();
		};
		
		@Override public void writeShortBlock(short [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.SHORT_BLOCK);	
			output.writeShortBlock(buffer,offset,length);
			writeFlush();
		};
		
		@Override public void writeIntBlock(int [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.INT_BLOCK);	
			output.writeIntBlock(buffer,offset,length);
			writeFlush();
		};
		
		@Override public void writeLongBlock(long [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.LONG_BLOCK);	
			output.writeLongBlock(buffer,offset,length);
			writeFlush();
		};
		
		@Override public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.FLOAT_BLOCK);	
			output.writeFloatBlock(buffer,offset,length);
			writeFlush();
		};
		
		@Override public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{		
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
			startBlockPrimitiveWrite(TState.DOUBLE_BLOCK);	
			output.writeDoubleBlock(buffer,offset,length);
			writeFlush();
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
					flush(); 					
				}finally{ state=TState.CLOSED; closeOnce(); };
			};
		};
		/* **********************************************************************
		
		
				Flushable
		
				
		**********************************************************************/
		/** Flushes the output */
		@Override public void flush()throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			output.flush();
		};	
};