package sztejkat.abstractfmt;
import java.io.IOException;

/**
		An implementation of {@link ISignalReadFormat} over the
		{@link #IIndicatorReadFormat}.
		<p>
		This class does not handle {@link EBrokenStream} as a permanent break.
*/
abstract class ASignalReadFormat0 implements ISignalReadFormat
{
			/* ------------------------------------------------------
			
					A state graph,
			
			------------------------------------------------------*/
			private enum TState
			{
					/** No information about action in progress */
					IDLE(),
					/** Begin signal was returned */
					BEGIN(),
					/** End signal was returned */
					END(),
					/** End signal was returned, but hidden begin is pending */
					END_BEGIN(),
					/** Elementary primitive read of this type was initialized */
					BOOLEAN(TState.ELEMENT, ISignalReadFormat.PRMTV_BOOLEAN, TIndicator.TYPE_BOOLEAN,  TIndicator.FLUSH_BOOLEAN ),
					BYTE(TState.ELEMENT, ISignalReadFormat.PRMTV_BYTE, TIndicator.TYPE_BYTE,  TIndicator.FLUSH_BYTE ),
					CHAR(TState.ELEMENT, ISignalReadFormat.PRMTV_CHAR, TIndicator.TYPE_CHAR,  TIndicator.FLUSH_CHAR ),
					SHORT(TState.ELEMENT, ISignalReadFormat.PRMTV_SHORT, TIndicator.TYPE_SHORT,  TIndicator.FLUSH_SHORT ),
					INT(TState.ELEMENT, ISignalReadFormat.PRMTV_INT, TIndicator.TYPE_INT,  TIndicator.FLUSH_INT ),
					LONG(TState.ELEMENT, ISignalReadFormat.PRMTV_LONG, TIndicator.TYPE_LONG,  TIndicator.FLUSH_LONG ),
					FLOAT(TState.ELEMENT, ISignalReadFormat.PRMTV_FLOAT, TIndicator.TYPE_FLOAT,  TIndicator.FLUSH_FLOAT ),
					DOUBLE(TState.ELEMENT, ISignalReadFormat.PRMTV_DOUBLE, TIndicator.TYPE_DOUBLE,  TIndicator.FLUSH_DOUBLE ),
					/** Block primitive read of this type was initialized */
					BOOLEAN_BLOCK(TState.BLOCK, ISignalReadFormat.PRMTV_BOOLEAN_BLOCK, TIndicator.TYPE_BOOLEAN_BLOCK,  TIndicator.FLUSH_BOOLEAN_BLOCK ),
					BYTE_BLOCK(TState.BLOCK, ISignalReadFormat.PRMTV_BYTE_BLOCK, TIndicator.TYPE_BYTE_BLOCK,  TIndicator.FLUSH_BYTE_BLOCK ),
					CHAR_BLOCK(TState.BLOCK, ISignalReadFormat.PRMTV_CHAR_BLOCK, TIndicator.TYPE_CHAR_BLOCK,  TIndicator.FLUSH_CHAR_BLOCK ),
					SHORT_BLOCK(TState.BLOCK, ISignalReadFormat.PRMTV_SHORT_BLOCK, TIndicator.TYPE_SHORT_BLOCK,  TIndicator.FLUSH_SHORT_BLOCK ),
					INT_BLOCK(TState.BLOCK, ISignalReadFormat.PRMTV_INT_BLOCK, TIndicator.TYPE_INT_BLOCK,  TIndicator.FLUSH_INT_BLOCK ),
					LONG_BLOCK(TState.BLOCK, ISignalReadFormat.PRMTV_LONG_BLOCK, TIndicator.TYPE_LONG_BLOCK,  TIndicator.FLUSH_LONG_BLOCK ),
					FLOAT_BLOCK(TState.BLOCK, ISignalReadFormat.PRMTV_FLOAT_BLOCK, TIndicator.TYPE_FLOAT_BLOCK,  TIndicator.FLUSH_FLOAT_BLOCK ),
					DOUBLE_BLOCK(TState.BLOCK, ISignalReadFormat.PRMTV_DOUBLE_BLOCK, TIndicator.TYPE_DOUBLE_BLOCK,  TIndicator.FLUSH_DOUBLE_BLOCK ),
					CLOSED();
					
					
					/** Set of flags */
					final int FLAGS;
					/** What to return in {@link #whatNext} in that state
					(block states only ) in described */
					final int PRMTV;
					/** Type indicator for operation */
					final TIndicator TYPE;
					/** Flush indicator for operation */
					final TIndicator FLUSH;
					
					/** Set if state represents block operation */
					public static final int BLOCK = 0x01;
					/** Set if state represents elementary operation */
					public static final int ELEMENT = 0x02;  
					
					TState(int flags,  TIndicator t, TIndicator f)
					{
						this(flags,0,t,f);
					};
					TState(int flags, int p, TIndicator t, TIndicator f)
					{
						this.FLAGS = flags;
						this.PRMTV = p;
						this.TYPE = t;
						this.FLUSH = f;
					};
					TState(){ this(0,0,null,null); };
			};
			
			/* ----------------------------------------------------
					Input			
			----------------------------------------------------*/
			/** Input wrapped in current-indicator tracking
			adapter */
			private final CIndicatorReadFormatAdapter input;
			
			/* ---------------------------------------------------
							Names registry
			-----------------------------------------------------*/
			/** A names registry, filled up with names, first
			null indicates end of used area. Fixed size set to
			the size of registry. 
			*/
			private final String [] names_registry;			
			/* ---------------------------------------------------
							State
			-----------------------------------------------------*/
			/** State variable initially {@link TState#IDLE}*/
			private TState state;
			/** Used to handle end-begin optimized events */
			private String pending_signal_name;
			/* ---------------------------------------------------
							Depth tracking
			-----------------------------------------------------*/
			/** Maximum recursion depth, -1 disables it */
			private final int max_events_recursion_depth;
			/** Current events depth */
			private int depth;
			
	/** Creates write format
		@param names_registry_size maximum number of names 
			to register if compact names should be implemented.			
			<p>
			Zero to disable registry and always write names directly.
			<p>
			This value must not be greater than one returned
			by <code>input.getMaxRegistrations</code> and can be
			used to limit registry range if required.	
			<p>
			Notice, this class will pre-allocate name registry
			to that size so keep this in mind.
		@param max_events_recursion_depth specifies the allowed depth of events
		nesting. -1 disables limit, 0 sets limit to: "no events allowed",
		1 allows event but no events inside and so on. If this limit is exceed
		the {@link #next} will throw {@link EFormatBoundaryExceeded} if stream
		contains too deep recursion of elements.	
		@param input output to set. If null will be set to <code>(IIndicatorWriteFormat)this</code>.
		@throws Assertion error if parameters do not match.
		@see IIndicatorReadFormat#getMaxRegistrations
		*/
	protected ASignalReadFormat0(
								  int names_registry_size,
								   int max_events_recursion_depth,
								  IIndicatorReadFormat input
								 )
	{
		assert(names_registry_size>=0):"names_registry_size="+names_registry_size;
		assert( (input==null)
				||
				((input!=null) &&( input.getMaxRegistrations()>=names_registry_size))
			   );
		assert(max_events_recursion_depth>=0):"max_events_recursion_depth="+max_events_recursion_depth;
		this.max_events_recursion_depth=max_events_recursion_depth;
		this.names_registry= new String[names_registry_size];
		this.input = new CIndicatorReadFormatAdapter( input==null ? (IIndicatorReadFormat)this : input );			
		this.state = TState.IDLE;
	};
	/* ****************************************************************
	
				Tooling
	
	*****************************************************************/
	private void validateNotClosed()throws EClosed
	{
		if (state==TState.CLOSED) throw new EClosed();
	};		
	/* ****************************************************************
	
				ISignalReadFormat
	
	*****************************************************************/
	/* -------------------------------------------------------------
				Signals
	-------------------------------------------------------------*/
	@Override public String next()throws IOException
	{
		if (max_events_recursion_depth>=0)
		{
			if (depth>max_events_recursion_depth)
				throw new EFormatBoundaryExceeded("Too deep recursion");
		};
		final String s = nextImpl();
		if (s!=null)
		{
			depth++;
			if (depth<0) throw new IllegalStateException("Used up more than 2^32 recursion levels");
		}	
		else
		{
			if (depth==0) throw new ECorruptedFormat("end signal, but no active begin signal.");
			depth--;
		};
		return s;
	};
	/** Implements {@link #next} but without depth tracking
	@return --//-- 
	@throws --//-- 
	*/
	private String nextImpl()throws IOException
	{
		validateNotClosed();
		//Handle fake state
		if (state == TState.END_BEGIN)
		{
			state = TState.BEGIN;
			String n = this.pending_signal_name;
			this.pending_signal_name = null;
			return n;
		}
		/*
				This operation must skip what source
				provides until we are at the signal indicator.
				
				Now important warning. If input.skip() is due to 
				some reason ineffective we may get stuck in a loop.
				The number of skipped non-signal indicators can be
				unlimited because there is an unlimited amount of
				TYPE+DATA+FLUSH triplets or TYPE+DATA pairs 
				inside an event. The only way to detect it is to
				validate if skip did not end up in DATA.
		*/
		for(;;)
		{
			TIndicator indicator = input.readIndicator();
			if (indicator==TIndicator.EOF) throw new EUnexpectedEof();
			if ((indicator.FLAGS & TIndicator.SIGNAL)==0)
			{
					//Now do some type testing.The only testing we can do it is
					//to reject type indicators for un-described streams.
					if (!isDescribed())
					{
						if ((indicator.FLAGS & (TIndicator.TYPE+TIndicator.FLUSH))!=0)
							throw new ECorruptedFormat("Type information "+indicator+" found in undescribed stream");
					};
					input.skip();		
					//Skipping must not end in DATA. This is deadly condition so we
					//check it always.
					if (input.getIndicator()==TIndicator.DATA) //<-- get to put in cache.
						throw new IllegalStateException("input.skip() failed to skip DATA. Possibly broken indicator format reader?");
			}else
			{
				/*
					We are at signal indicator.					
					Excavate name.
				*/
				String signal_name = null;
				switch(indicator)
				{
					case BEGIN_REGISTER:
					case END_BEGIN_REGISTER:
							//Put to registery.
							{
								int idx = input.getSignalNumber();
								signal_name= input.getSignalName();
								assert(signal_name!=null);
								if (idx<0) throw new ECorruptedFormat("Negative name index"); 
								if (idx>=names_registry.length) throw new ECorruptedFormat("Name registy too small: index="+idx);
								if (names_registry[idx]!=null) throw new ECorruptedFormat("Name registry at "+idx+" already used up by \""+names_registry[idx]+"\", can't re register \""+signal_name+"\"");
								names_registry[idx]=signal_name;
							};break;
					case BEGIN_USE:
					case END_BEGIN_USE:
							{
							int idx = input.getSignalNumber();
							if (idx<0) throw new ECorruptedFormat("Negative name index");
							if (idx>=names_registry.length) throw new ECorruptedFormat("Name index out of bounds: index="+idx);
							if (names_registry[idx]!=null) throw new ECorruptedFormat("Name registry at "+idx+" is empty");
							signal_name = names_registry[idx];
							};break;
					case BEGIN_DIRECT:
					case END_BEGIN_DIRECT:
							signal_name = input.getSignalName();
							break;
				};
				//Now make a proper state transition.
				if ((indicator.FLAGS & (TIndicator.IS_END+TIndicator.IS_BEGIN))
							==(TIndicator.IS_END+TIndicator.IS_BEGIN))
				{
					state = TState.END_BEGIN;
					pending_signal_name = signal_name;
					return null;
				}else
				if ((indicator.FLAGS & TIndicator.IS_BEGIN)!=0)
				{
					state = TState.BEGIN;
					assert(signal_name!=null);
					return signal_name;
				}else
				{
					assert(signal_name==null);
					assert((indicator.FLAGS & TIndicator.IS_END)!=0);
					state = TState.END;
					return null;
				}
			}
		}
	};
		
	@Override public int whatNext()throws IOException
	{
		validateNotClosed();
		/*
				What is allowed next depends both on state
				and indicator under a cursor. Specifically
				the exact type of allowed block operation is not
				deducable if indicator is at DATA.  
		*/
		TIndicator indicator = input.getIndicator();
		//We do it differently in described and un-described mode
		if (isDescribed())
		{ 
			switch(indicator)
			{
				case EOF: return ISignalReadFormat.EOF;
				case DATA:
						if ((state.FLAGS & TState.BLOCK)!=0)
								return state.PRMTV;
						else
								throw new EDataTypeRequired("Described format requires type information");
						// break;
				//now process known type indicators
				case TYPE_BOOLEAN: return ISignalReadFormat.PRMTV_BOOLEAN;
				case TYPE_BYTE: return ISignalReadFormat.PRMTV_BYTE;
				case TYPE_CHAR: return ISignalReadFormat.PRMTV_CHAR;
				case TYPE_SHORT: return ISignalReadFormat.PRMTV_SHORT;
				case TYPE_INT: return ISignalReadFormat.PRMTV_INT;
				case TYPE_LONG: return ISignalReadFormat.PRMTV_LONG;
				case TYPE_FLOAT: return ISignalReadFormat.PRMTV_FLOAT;
				case TYPE_DOUBLE: return ISignalReadFormat.PRMTV_DOUBLE;
				//and for blocks.
				case TYPE_BOOLEAN_BLOCK: return ISignalReadFormat.PRMTV_BOOLEAN_BLOCK;
				case TYPE_BYTE_BLOCK: return ISignalReadFormat.PRMTV_BYTE_BLOCK;
				case TYPE_CHAR_BLOCK: return ISignalReadFormat.PRMTV_CHAR_BLOCK;
				case TYPE_SHORT_BLOCK: return ISignalReadFormat.PRMTV_SHORT_BLOCK;
				case TYPE_INT_BLOCK: return ISignalReadFormat.PRMTV_INT_BLOCK;
				case TYPE_LONG_BLOCK: return ISignalReadFormat.PRMTV_LONG_BLOCK;
				case TYPE_FLOAT_BLOCK: return ISignalReadFormat.PRMTV_FLOAT_BLOCK;
				case TYPE_DOUBLE_BLOCK: return ISignalReadFormat.PRMTV_DOUBLE_BLOCK;
				//finally we allow signal related indicators.
				default:
					if ((indicator.FLAGS & TIndicator.SIGNAL)!=0) return ISignalReadFormat.SIGNAL;
					//but no flush indicators whastoever.
					throw new ECorruptedFormat("Unexpected indicator "+indicator);
			}
		}else
		{
			switch(indicator)
			{
				case EOF: return ISignalReadFormat.EOF;
				case DATA: return ISignalReadFormat.PRMTV_UNTYPED;
				//No type indicators are allowed.
				//finally we allow signal related indicators
				default:
					if ((indicator.FLAGS & TIndicator.SIGNAL)!=0) return ISignalReadFormat.SIGNAL;
					//but no flush indicators whastoever.
					throw new ECorruptedFormat("Unexpected indicator "+indicator);
			}
		}
	};
	
	
	/* -------------------------------------------------------------
				Primititves
	-------------------------------------------------------------*/
	/* ...........................................................
				Common tools
	...........................................................*/
	/** Starts elementary primitive by validating conditions
	and toggling state to specfied
	@param required_state state to toggle to, used to check what type is needed 
	@throws IOException if failed or detected problems*/
	private void startElementaryPrimitive(TState required_state)throws IOException
	{		
		validateNotClosed();
		if ((state.FLAGS & TState.BLOCK)!=0) throw new IllegalStateException("Block operation in progress, cant do primitive read.");
		startPrimitive(required_state);
	};
	
	/** Starts or continoues block primitive by validating conditions
	and toggling state to specfied
	@param required_state state to toggle to, used to check what type is needed
	@throws IOException if failed or detected problems*/
	private void startBlockPrimitive(TState required_state)throws IOException
	{		
		validateNotClosed();
		if (state!=required_state)
		{
			if ((state.FLAGS & TState.BLOCK)!=0) 
			{
					throw new IllegalStateException("Can't initiate "+required_state+" block operation since "+state+" is in progress");
			};
			startPrimitive(required_state);
		}
	};
	/** Starts primitive by validating conditions
	and toggling state to specfied
	@param required_state state to toggle to, used to check what type is needed 
	@throws IOException if failed or detected problems*/
	private void startPrimitive(TState required_state)throws IOException
	{
		TIndicator required_type_indicator = isDescribed() ? required_state.TYPE : TIndicator.DATA;
		TIndicator indicator = input.getIndicator();
		if (indicator!=required_type_indicator)
		{
			if ((indicator.FLAGS & TIndicator.SIGNAL)!=0) throw new ENoMoreData();
			if (indicator==TIndicator.EOF) throw new EUnexpectedEof();
			if (isDescribed() && (indicator.FLAGS & TIndicator.TYPE)!=0)
					throw new EDataMissmatch("Required type information "+required_state.TYPE+" but "+indicator+" found");
			throw new ECorruptedFormat("Unexpected indicator:"+indicator);
		}	
		this.state = required_state;
	};
	/** Ends elementary primitive by validating conditions and restore state
	to IDLE 
	@throws IOException if failed or detected problems*/
	private void endElementaryPrimitive()throws IOException
	{
		if((state.FLAGS & TState.ELEMENT)==0) throw new EBrokenStream("Broken, something wrong with states, can't recover from that");
		endPrimtive(TState.IDLE);
	};
	/** Ends block primitive by validating conditions and leaving state at block
	condition.
	@throws IOException if failed or detected problems*/
	private void endBlockPrimitive()throws IOException
	{
		if((state.FLAGS & TState.BLOCK)==0) throw new EBrokenStream("Broken, something wrong with states, can't recover from that");
		endPrimtive(this.state);
	};
	/** Ends primitive by validating conditions and toggling state
	to specified.
	@param required_state state to toggle to after return from this method.
	@throws IOException if failed or detected problems*/
	private void endPrimtive(TState required_state)throws IOException
	{
		TState state = this.state;
		this.state = required_state;
		
		if((state.FLAGS & (TState.ELEMENT + TState.BLOCK))==0) throw new EBrokenStream("Broken, something wrong with states, can't recover from that");
		
		TIndicator indicator = input.getIndicator();		
		if (indicator==TIndicator.EOF)	return;	//This is allowed, we can have no more data
												//even in described stream, where flushes are optional.		
		if (isDescribed())
		{
			//Flush indicators are optional, but if they are they have to match.
			if ((indicator.FLAGS & TIndicator.FLUSH)!=0)
			{
				if(!(
					   (state.FLUSH == indicator)
						||
					   ( ((state.FLAGS & TState.BLOCK)!=0) && ((indicator.FLAGS & TIndicator.BLOCK)!=0) )
						||
					   ( ((state.FLAGS & TState.ELEMENT)!=0) && ((indicator.FLAGS & TIndicator.ELEMENT)!=0) )
					   )
					  ) throw new ECorruptedFormat("Flush indicator "+state.FLUSH+" expected but "+indicator+" is found");
			};
		};
		//And in un-described format we can ignore what is now.
	};
	
	/* ...........................................................
				Elementary primitives
	...........................................................*/
	@Override public boolean readBoolean()throws IOException
	{
		startElementaryPrimitive(TState.BOOLEAN);
		try{
				return input.readBoolean();
		}finally{ endElementaryPrimitive(); }
	};
	@Override public byte readByte()throws IOException
	{
		startElementaryPrimitive(TState.BYTE);
		try{
				return input.readByte();
		}finally{ endElementaryPrimitive(); }
	};	
	@Override public char readChar()throws IOException
	{
		startElementaryPrimitive(TState.CHAR);
		try{
				return input.readChar();
		}finally{ endElementaryPrimitive(); }
	};
	@Override public short readShort()throws IOException
	{
		startElementaryPrimitive(TState.SHORT);
		try{
				return input.readShort();
		}finally{ endElementaryPrimitive(); }
	};
	@Override public int readInt()throws IOException
	{
		startElementaryPrimitive(TState.INT);
		try{
				return input.readInt();
		}finally{ endElementaryPrimitive(); }
	};
	@Override public long readLong()throws IOException
	{
		startElementaryPrimitive(TState.LONG);
		try{
				return input.readLong();
		}finally{ endElementaryPrimitive(); }
	};
	@Override public float readFloat()throws IOException
	{
		startElementaryPrimitive(TState.FLOAT);
		try{
				return input.readFloat();
		}finally{ endElementaryPrimitive(); }
	};
	@Override public double readDouble()throws IOException
	{
		startElementaryPrimitive(TState.DOUBLE);
		try{
				return input.readDouble();
		}finally{ endElementaryPrimitive(); }
	};
	/* ...........................................................
				Blocks
	...........................................................*/
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		assert(buffer!=null):"buffer==null";
		assert(offset>=0):"offset="+offset+" is negative";
		assert(length>=0):"length="+length+" is negative";
		assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
		if (length==0) return 0;	//filter out empty reads.		
		
		startBlockPrimitive(TState.BOOLEAN_BLOCK);
		final int r = input.readBooleanBlock(buffer, offset, length);
		if (r<length)
			endBlockPrimitive();
		return r;
	};
	@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		assert(buffer!=null):"buffer==null";
		assert(offset>=0):"offset="+offset+" is negative";
		assert(length>=0):"length="+length+" is negative";
		assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
		if (length==0) return 0;	//filter out empty reads.		
		
		startBlockPrimitive(TState.BYTE_BLOCK);
		final int r = input.readByteBlock(buffer, offset, length);
		if (r<length)
			endBlockPrimitive();
		return r;
	};
	@Override public int readByteBlock()throws IOException
	{
		startBlockPrimitive(TState.BYTE_BLOCK);
		final int r = input.readByteBlock();
		assert((r>=-1)&&(r<=0xFF));
		if (r==-1)
			endBlockPrimitive();
		return r;
	};
	@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		assert(buffer!=null):"buffer==null";
		assert(offset>=0):"offset="+offset+" is negative";
		assert(length>=0):"length="+length+" is negative";
		assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
		if (length==0) return 0;	//filter out empty reads.		
		
		startBlockPrimitive(TState.CHAR_BLOCK);
		final int r = input.readCharBlock(buffer, offset, length);
		if (r<length)
			endBlockPrimitive();
		return r;
	};
	@Override public int readCharBlock(Appendable buffer, int length)throws IOException
	{
		assert(length>=0):"length="+length+" is negative";
		
		if (length==0) return 0;	//filter out empty reads.		
		
		startBlockPrimitive(TState.CHAR_BLOCK);
		final int r = input.readCharBlock(buffer, length);
		if (r<length)
			endBlockPrimitive();
		return r;
	};
	@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		assert(buffer!=null):"buffer==null";
		assert(offset>=0):"offset="+offset+" is negative";
		assert(length>=0):"length="+length+" is negative";
		assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
		if (length==0) return 0;	//filter out empty reads.		
		
		startBlockPrimitive(TState.SHORT_BLOCK);
		final int r = input.readShortBlock(buffer, offset, length);
		if (r<length)
			endBlockPrimitive();
		return r;
	};
	@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		assert(buffer!=null):"buffer==null";
		assert(offset>=0):"offset="+offset+" is negative";
		assert(length>=0):"length="+length+" is negative";
		assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
		if (length==0) return 0;	//filter out empty reads.		
		
		startBlockPrimitive(TState.INT_BLOCK);
		final int r = input.readIntBlock(buffer, offset, length);
		if (r<length)
			endBlockPrimitive();
		return r;
	};	
	@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		assert(buffer!=null):"buffer==null";
		assert(offset>=0):"offset="+offset+" is negative";
		assert(length>=0):"length="+length+" is negative";
		assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
		if (length==0) return 0;	//filter out empty reads.		
		
		startBlockPrimitive(TState.LONG_BLOCK);
		final int r = input.readLongBlock(buffer, offset, length);
		if (r<length)
			endBlockPrimitive();
		return r;
	};
	@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		assert(buffer!=null):"buffer==null";
		assert(offset>=0):"offset="+offset+" is negative";
		assert(length>=0):"length="+length+" is negative";
		assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
		if (length==0) return 0;	//filter out empty reads.		
		
		startBlockPrimitive(TState.FLOAT_BLOCK);
		final int r = input.readFloatBlock(buffer, offset, length);
		if (r<length)
			endBlockPrimitive();
		return r;
	};
	@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		assert(buffer!=null):"buffer==null";
		assert(offset>=0):"offset="+offset+" is negative";
		assert(length>=0):"length="+length+" is negative";
		assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
		if (length==0) return 0;	//filter out empty reads.		
		
		startBlockPrimitive(TState.DOUBLE_BLOCK);
		final int r = input.readDoubleBlock(buffer, offset, length);
		if (r<length)
			endBlockPrimitive();
		return r;
	};
	
	/* *************************************************************
	
			Closeable
	
	**************************************************************/
	/** Invoked in {@link #close}.
	closes input */
	protected void closeOnce()throws IOException
	{
		input.close();
	};
	/** Calls {@link #closeOnce}, but only one time.
	Makes stream unusable */
	public void close()throws IOException
	{
		if (state!=TState.CLOSED)
		{
			state=TState.CLOSED;
			closeOnce();
		};
	};
}