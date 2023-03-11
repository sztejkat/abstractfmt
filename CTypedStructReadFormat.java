package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	A reading counterpart of {@link CTypedStructWriteFormat}.
*/
public class CTypedStructReadFormat extends AReservedNameReadFormat implements ITypedStructReadFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CTypedStructReadFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CTypedStructReadFormat.",CTypedStructReadFormat.class) : null;

				/** Current element in stream.
				Null if undetermined and needs to be fetched.
				If this element is {@link TElement#SIG}
				the {@link #pending_signal} do carry a pending signal name.
				Updated by {@link #peek} and {@link #next}.
				*/
				private TElement current_element;
				/** A current, pending signal associated
				with {@link #current_element} carying true, user visible begin or end signal 
				Updated by {@link #peek} and {@link #next}.
				*/
				private String pending_signal;
				/** Used to tell apart when block operation is initializing block
				processing or continuing it. Cleared by {@link #next}, set by
				{@link #validateBlock}
				*/
				private boolean block_in_progress;
				
				/** A begin-end depth tracking.
				<p>
				Notice we can't use the adapter depth()
				or skip() because we are injecting own signals to provide type
				information. We do also peek() by moving forwards.
				@see #skip()
				@see #skip(int)
				@see #next()
				*/
				private int depth;
				
				/** An immutable array indexed by <code>CTypedStructWriteFormat.XXX_idx</code> constants
				used to recognize type names. */
				private final String [] type_names;
				/** Maps index in {@link #type_names} to element type.
				@see #typeFromName
				*/ 
				private final static TElement [] NAME_TO_TYPE_MAP = 
					new TElement[]
					{
						TElement.BOOLEAN,
						TElement.BYTE,
						TElement.CHAR,
						TElement.SHORT,
						TElement.INT,
						TElement.LONG,
						TElement.FLOAT,
						TElement.DOUBLE,
						TElement.STRING_BLK,
						TElement.BOOLEAN_BLK,
						TElement.BYTE_BLK,
						TElement.CHAR_BLK,
						TElement.SHORT_BLK,
						TElement.INT_BLK,
						TElement.LONG_BLK,
						TElement.FLOAT_BLK,
						TElement.DOUBLE_BLK
					};
	
		/* *************************************************************************
			
			
					Construction
			
		
		**************************************************************************/
		/** Creates
		@param engine an underlying low level engine, see {@link AStructReadFormatAdapter#AStructReadFormatAdapter}
		@param escape an escape character. This character must not be a first character of any type name
		@param type_names an array of names of types, indexed by <code>XXXX_idx</code> constants.
				Immutable, taken not copied, cannot be null, can't contain null.
		*/
		public CTypedStructReadFormat(
							IStructReadFormat engine, 
							char escape,
							String [] type_names
							)
		{
			super(engine, escape);
			if (TRACE) TOUT.println("new CTypedStructReadFormat( escape="+escape+") ENTER");
			assert(assertTypeNames(type_names));
			this.type_names = type_names;
			//reserve them, order doesn't matter.
			for(int i = type_names.length; --i>=0;)
				reserveName(type_names[i]);
			if (TRACE) TOUT.println("new CTypedStructReadFormat() LEAVE");
		};
		/* ***********************************************************************
		
				Internal services
		
		
		************************************************************************/
		/* ----------------------------------------------------------------
					Type info handling.
		----------------------------------------------------------------*/		
		/** Validates constructor parameter in assertion mode
		@param type_names constructor parameter
		@return always true, but may assert before returning.
		*/
		private static boolean assertTypeNames(String [] type_names)
		{
			assert(type_names!=null);
			assert(type_names.length==1+CTypedStructWriteFormat.DOUBLE_blk_idx):"type_names.length="+type_names.length;
			for(int i = type_names.length; --i>=0;)
			{
				assert(type_names[i]!=null):"null name at type_names["+i+"]";
			};
			return true;
		};
		/** Taking possible type name (from signal denoting reserved name)
		finds it in {@link #type_names} array and returns type associated
		with index at which it found a match.
		@param name name to check
		@return null if is is not a type, but a user signal passed as a reserved name
		*/
		private TElement typeFromName(String name)
		{
			
			for(int i=type_names.length; --i>=0;)
			{
				if (type_names[i].equals(name))
				{
						if (TRACE) TOUT.println("typeFromName("+name+")="+NAME_TO_TYPE_MAP[i]);
						return NAME_TO_TYPE_MAP[i];
				};
			};
			if (TRACE) TOUT.println("typeFromName("+name+")=null");
			return null;
		};
		
		/* *************************************************************************
		
		
				IStructReadFormat
		
		
		**************************************************************************/
		/* ----------------------------------------------------------------------
		
				Signals
		
		----------------------------------------------------------------------*/
		/** Restores default behavior instead of passing it down-stream */
		@Override public void skip()throws IOException
		{ 
			skip(0);
		};
		/** Restores default behavior instead of passing it down-stream
		because this class needs own depth accounting.
		*/
		@Override public void skip(int levels)throws IOException
		{ 
			IStructReadFormat.skip(this,levels);  
		};
		/** {@inheritDoc}
		<p>
		Uses own accounting instead of passing it down-stream.
		@see #depth
		@see #next()
		@see #skip(int)
		*/
		@Override public final int depth()
		{ 
			return depth;
		};
		/** {@inheritDoc}
		<p>
		Adds begin-end balancing accounting for {@link #nextImpl}.
		@see #depth
		@see #skip(int)
		*/
		@Override public String next()throws IOException
		{
			final String s = nextImpl();
			if (s==null)
			{
				assert(depth>0):"Missplace end? Downstream should capture it";
				depth--;
			}
			else
			{
				depth++;
				assert(depth>0):"too deep recursion? Downstream should capture it";
			}
			return s;
		}
		/** Performs {@link #next} but without {@link #depth} accounting 
		@return as {@link #next}
		@throws IOException --//--
		*/
		protected String nextImpl()throws IOException
		{
			if (TRACE) TOUT.println("next() ENTER");
			//In loop go through all possible type information skipping it.
			peek();	//arm type information. This is necessary if this is first
					//call in stream, and stream lacks type information.
					//Without a peek() next would not barf about missing type info.
					
			block_in_progress = false; //block is always terminated by this operation.
			loop:
			for(;;)
			{			
				if (TRACE) TOUT.println("next(), looping");
				//check if we have pending signal to report?
				//This may be a side-effect of peek()
				if (this.current_element==TElement.SIG)
				{
					if (TRACE) TOUT.println("next(), detected pending SIG "+this.pending_signal);
					this.current_element = null;
					final String s = this.pending_signal;
					this.pending_signal = null;
					return s;
				};
				//No pending signal. Now we have to really move to the next
				//user signal.
				String signal = super.next();
				//We can possibly have either:
				//	- end signal of type info
				//	- type info
				//	- user end signal
				//	- user begin signal.
				if (TRACE) TOUT.println("next(), super.next()="+signal);
				if (current_element!=null)
				{
					//we do expect end of type.
					if (signal != null)
						throw new EBrokenFormat("Expected type information end signal but found \""+signal+"\"");
					//consume type information and continue.
					this.current_element = null;
					continue loop;
				}else
				{
					//Now we have possible type info user end and user begin.
					if (signal==null)
					{
						if (TRACE) TOUT.println("next(), detected user end");
						//this is user end. Consume it and return.
						this.current_element = null;	
						this.pending_signal = null;
						return null;
					}else
					{
						//Now we have possible user begin or type info.
						if (isReservedName(signal))
						{
							if (TRACE) TOUT.println("next(), detected reserved name");
							//All type names are reserved, but more could have been reserved.
							TElement type_info = typeFromName(signal);
							if (type_info!=null)
							{
								if (TRACE) TOUT.println("next(), captured type info "+type_info);
								//store it and continue
								this.current_element = type_info;
								this.pending_signal = null;
								continue loop;
							};
							//this is user begin.
						}else
						{
							//this is user begin, may be escaped.
							//unescape
							signal = unescape(signal);
							if (TRACE) TOUT.println("next(), unescaped="+signal);
						};
						//now process it as a user begin
						//Basically consume signal information and return.
						this.current_element = null;	
						this.pending_signal = null;
						if (TRACE) TOUT.println("next()="+signal+" LEAVE");
						return signal;
					}
				}
			}
		};
		 
		@Override public boolean hasElementaryData()throws IOException
		{
			if (TRACE) TOUT.println("hasElementaryData ENTER");
			//This must hide end of data due to end of type information.
			//We do basically peek for TElement.SIG which indicates
			//true end of data, except if we are during a block operation
			//where condition is a bit more complex
			
			//However we should also be aware, that peeking may touch end-of-file.
			//what should not cause us to throw in here.			
			TElement e = peek();
			if (e==TElement.EOF) return false;
			if (e==TElement.SIG)
			{
				if (TRACE) TOUT.println("hasElementaryData=false, due to signal LEAVE");
				return false;
			}else
			{
				if (e.is_block)
				{
					//For blocks we need to ask super to be sure, 
					//if we touched trailing type end() since
					//peek do return block even if we are touching
					//type information end followed by user end().
					final boolean r = super.hasElementaryData();
					if (TRACE) TOUT.println("hasElementaryData="+r+", processing inside a block, LEAVE");
					return r;
				}else
				{
					if (TRACE) TOUT.println("hasElementaryData=true LEAVE");
					return true;
				}
			}
		};
		/* ----------------------------------------------------------------------
		
				Elementatry primitives.
		
		----------------------------------------------------------------------*/
		private void validateElementary(TElement expected_type)throws IOException
		{
			//validate type information. Notice the open()/close() state
			//will be validated by our call to peek()
			assert(expected_type!=null);
			assert(!expected_type.is_block);
			if (TRACE) TOUT.println("validateElementary("+expected_type+") ENTER");
			TElement e = peek();
			//Now e can point to signal, which should trigger ENoMoreData
			//or incorrect type, which should trigger ETypeMissmatch
			//The priorities are:
			//	- IllegalStateException if block operation is in progress.
			//	- ETypeMissmatch
			//	- ENoMoreData 
			//		Notice we can't let super.readXXXX to be called when
			//		we peek at signal, because peeking will fetch the signal
			//		from down-stream and thous let it execute the read.
			//		This differs from the behavior of block operations
			//		where peek does not consume the trailing signal
			//		of block op, but it does consume a trailing signal of a non-block.
			if (block_in_progress) throw new IllegalStateException("block operation "+e+" in progress");
			if (e!=expected_type)
			{
				if (e==TElement.EOF) throw new EEof();
				if (e==TElement.SIG) throw new ENoMoreData(); 
				else
				throw new ETypeMissmatch("Found "+e+" expected "+expected_type);
			};
			if (TRACE) TOUT.println("validateElementary("+expected_type+") LEAVE");
		};
		@Override public boolean readBoolean()throws IOException
		{
			validateElementary(TElement.BOOLEAN);
			return super.readBoolean();
		};
		@Override public byte readByte()throws IOException
		{
			validateElementary(TElement.BYTE);
			return super.readByte();
		};
		@Override public char readChar()throws IOException
		{
			validateElementary(TElement.CHAR);
			return super.readChar();
		};
		@Override public short readShort()throws IOException
		{
			validateElementary(TElement.SHORT);
			return super.readShort();
		};
		@Override public int readInt()throws IOException
		{
			validateElementary(TElement.INT);
			return super.readInt();
		};
		@Override public long readLong()throws IOException
		{
			validateElementary(TElement.LONG);
			return super.readLong();
		};
		@Override public float readFloat()throws IOException
		{
			validateElementary(TElement.FLOAT);
			return super.readFloat();
		};
		@Override public double readDouble()throws IOException
		{
			validateElementary(TElement.DOUBLE);
			return super.readDouble();
		};
		
		
		
		
		/* ----------------------------------------------------------------------
		
				Elementatry blocks.
		
		----------------------------------------------------------------------*/
		private void validateBlock(TElement expected_type)throws IOException
		{
			if (TRACE) TOUT.println("validateBlock("+expected_type+") ENTER");
			//validate type information. Notice the open()/close() state
			//and all others will be validated by our call to peek()
			assert(expected_type!=null);
			assert(expected_type.is_block);
			TElement e = peek();			
			//Now we need ot handle differently initial block operation,
			//which should throw ETypeMissmatch
			//from subsequent calls which should throw IllegalStateException
			//on the same peeked not-matching type.
			
			//The peek() inside a block will never return TElement.SIG and won't			
			//actually consume a terminating signal.
			//We still may however receive it during the initial phase.
			if (e!=expected_type)
			{
				if (e==TElement.EOF) throw new EEof();
				if (!block_in_progress)
				{
					//If we do recive SIG in initial phase we basically are
					//asked to initialize the operation where there is no sequence
					//present. The properly described format would enclose it
					//in zero size type information and would process as the API
					//do say.
					//So if we get SIG here it means, that either type information is missing
					//(format bug) or there was no such a block at all, ie because of there
					//were just propertly typed elementary primitives.
					//The best way to handle it is to throw ETypeMissmatch.
					throw new ETypeMissmatch("Found "+e+" expected "+expected_type);
				}
				else
				{
					assert(e!=TElement.SIG);	
					throw new IllegalStateException("Conflicting block operation "+e+" in progress");
				}
			}
			block_in_progress = true;
			if (TRACE) TOUT.println("validateBlock("+expected_type+") LEAVE");
		};
		@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
		{
			validateBlock(TElement.BOOLEAN_BLK);
			return super.readBooleanBlock(buffer,offset,length);
		};
		@Override public int readBooleanBlock(boolean [] buffer)throws IOException
		{
			validateBlock(TElement.BOOLEAN_BLK);
			return super.readBooleanBlock(buffer);
		};
		@Override public boolean readBooleanBlock()throws IOException,ENoMoreData
		{
			validateBlock(TElement.BOOLEAN_BLK);
			return super.readBooleanBlock();
		};
		
		@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
		{
			validateBlock(TElement.BYTE_BLK);
			return super.readByteBlock(buffer,offset,length);
		};
		@Override public int readByteBlock(byte [] buffer)throws IOException
		{
			validateBlock(TElement.BYTE_BLK);
			return super.readByteBlock(buffer);
		};
		@Override public byte readByteBlock()throws IOException,ENoMoreData
		{
			validateBlock(TElement.BYTE_BLK);
			return super.readByteBlock();
		};
		
		@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
		{
			validateBlock(TElement.CHAR_BLK);
			return super.readCharBlock(buffer,offset,length);
		};
		@Override public int readCharBlock(char [] buffer)throws IOException
		{
			validateBlock(TElement.CHAR_BLK);
			return super.readCharBlock(buffer);
		};
		@Override public char readCharBlock()throws IOException,ENoMoreData
		{
			validateBlock(TElement.CHAR_BLK);
			return super.readCharBlock();
		};
		
		@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
		{
			validateBlock(TElement.SHORT_BLK);
			return super.readShortBlock(buffer,offset,length);
		};
		@Override public int readShortBlock(short [] buffer)throws IOException
		{
			validateBlock(TElement.SHORT_BLK);
			return super.readShortBlock(buffer);
		};
		@Override public short readShortBlock()throws IOException,ENoMoreData
		{
			validateBlock(TElement.SHORT_BLK);
			return super.readShortBlock();
		};
		
		@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
		{
			validateBlock(TElement.INT_BLK);
			return super.readIntBlock(buffer,offset,length);
		};
		@Override public int readIntBlock(int [] buffer)throws IOException
		{
			validateBlock(TElement.INT_BLK);
			return super.readIntBlock(buffer);
		};
		@Override public int readIntBlock()throws IOException,ENoMoreData
		{
			validateBlock(TElement.INT_BLK);
			return super.readIntBlock();
		};
		
		@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
		{
			validateBlock(TElement.LONG_BLK);
			return super.readLongBlock(buffer,offset,length);
		};
		@Override public int readLongBlock(long [] buffer)throws IOException
		{
			validateBlock(TElement.LONG_BLK);
			return super.readLongBlock(buffer);
		};
		@Override public long readLongBlock()throws IOException,ENoMoreData
		{
			validateBlock(TElement.LONG_BLK);
			return super.readLongBlock();
		};
		
		@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
		{
			validateBlock(TElement.FLOAT_BLK);
			return super.readFloatBlock(buffer,offset,length);
		};
		@Override public int readFloatBlock(float [] buffer)throws IOException
		{
			validateBlock(TElement.FLOAT_BLK);
			return super.readFloatBlock(buffer);
		};
		@Override public float readFloatBlock()throws IOException,ENoMoreData
		{
			validateBlock(TElement.FLOAT_BLK);
			return super.readFloatBlock();
		};
		
		@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{
			validateBlock(TElement.DOUBLE_BLK);
			return super.readDoubleBlock(buffer,offset,length);
		};
		@Override public int readDoubleBlock(double [] buffer)throws IOException
		{
			validateBlock(TElement.DOUBLE_BLK);
			return super.readDoubleBlock(buffer);
		};
		@Override public double readDoubleBlock()throws IOException,ENoMoreData
		{
			validateBlock(TElement.DOUBLE_BLK);
			return super.readDoubleBlock();
		};
		
		@Override public int readString(Appendable characters,  int length)throws IOException
		{
			validateBlock(TElement.STRING_BLK);
			return super.readString(characters,length);
		};
		@Override public char readString()throws IOException,ENoMoreData
		{
			validateBlock(TElement.STRING_BLK);
			return super.readString();
		};
		/* *************************************************************************
		
		
				ITypedStructReadFormat
		
		
		**************************************************************************/
		@Override public TElement peek()throws IOException
		{
			if (TRACE) TOUT.println("peek() ENTER");
			//Peek needs to check what can be next in stream and will be
			//intensively used to check if primitive operations are allowed or not.
			
			//First handle pending unconsumed user signal.
			//This must be consumed to move forwards.
			if (current_element == TElement.SIG)
			{
				if (TRACE) TOUT.println("peek()="+current_element+" , unconsumed signal LEAVE");
				return TElement.SIG;
			};
			
			//If we have type information we may need to check if it 
			//terminates.		
			if (current_element!=null)
			{
				if (TRACE) TOUT.println("peek() knows "+current_element);
				assert(this.current_element!=TElement.SIG);
				if (!super.hasElementaryData())
				{
					if (TRACE) TOUT.println("peek(), are elementary data");
					//Now let us think about block operations. If block operation
					//touches this condition block operation still must be allowed
					//and still must be correctly listed as current element by peek()
					//cause user will use peek() to check if block op is allowed.
					//Note: the block_in_progress can't be used because
					//the fact that we peek() at block does NOT mean we did initialize
					//the block operation.
					if (this.current_element.is_block)	
					{
							if (TRACE) TOUT.println("peek()="+current_element+" since we are in block operation LEAVE");
							return this.current_element;
					};
					//not a block operation, so we must terminate it and move to next type info or user signal.
					String s = super.next();
					if (TRACE) TOUT.println("peek(), super.next()="+s);
					if (s!=null) 
						throw new EBrokenFormat("Expected type information end signal but found \""+s+"\"");
					//So it is now terminated
					this.current_element=null;
				}else
				{
					//in this condition we just continue with current type.
					assert(this.current_element!=TElement.SIG);
					assert(this.current_element!=null);
					if (TRACE) TOUT.println("peek()="+this.current_element+" LEAVE"); 
					return this.current_element;
				};
			}else
			{
				//This can be initial operation
				if (super.hasElementaryData())
					throw new EBrokenFormat("Missing type information?");
			};
			//We either terminated the data or we lack information about data we need.
			//Either way we need to pick it up.
			//Notice however, that we can end-up in the end-of-file condition 
			//when there are no more data at all at zero recursion level.
			String signal;
			try{
				 signal = super.next();
			}catch(EEof eof)
			{
				if (TRACE) TOUT.println("peek(), super.next() did eof, returning it");
				//wipe memory, because EOF may be temporary
				this.current_element = null;	
				this.pending_signal = null;
				return TElement.EOF;
			};
			if (TRACE) TOUT.println("peek(), super.next()="+signal);
			//We can possibly have either:
			//	- type info
			//	- user end signal
			//	- user begin signal.
			//Now we have possible type info user end and user begin.
			if (signal==null)
			{
				//this is user end. Make it pending for next() and return;
				this.current_element = TElement.SIG;	
				this.pending_signal = null;
				if (TRACE) TOUT.println("peek(), tracking pending user end signal LEAVE");
				return TElement.SIG;
			}else
			{
				//Now we have possible user begin or type info.
				if (isReservedName(signal))
				{
					if (TRACE) TOUT.println("peek(), detected reserved name");
					//All type names are reserved, but more could have been reserved.
					TElement type_info = typeFromName(signal);
					if (type_info!=null)
					{
						//store it and return.
						//Note: type for empty block is fine, tough empty elementary primitive is a factual bug 
						this.current_element = type_info;
						this.pending_signal = null;
						if (TRACE) TOUT.println("peek()="+type_info+" LEAVE");
						return type_info;
					};
					//this is user begin.
				}else
				{
					//this is user begin, may be escaped.
					//unescape
					signal = unescape(signal);
				};
				//Make it pending for next() and return;
				this.current_element = TElement.SIG;	
				this.pending_signal = signal;
				if (TRACE) TOUT.println("peek()=TElement.SIG, pending_signal="+pending_signal+" LEAVE");
				return TElement.SIG;
			}
		};
};