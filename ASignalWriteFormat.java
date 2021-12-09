package sztejkat.abstractfmt;
import java.io.IOException;
/**
	A core, elementary implementation of {@link ISignalWriteFormat}
	<p>
	This implementation is based on concept of 
	<a href="package-description.html#indicators">indicators</a>
	and provides base for both described and un-described formats.
	
	<h2>Described formats</h2>
	This class provides a no-operation set of methods
	<code>writeTypeXXX</code> which are invoked in every place
	in which type information must be written. The described
	formats should override those methods (see their description)
	and override {@link #isDescribed} to return true.
	
	<h2>Testing</h2>
	Through tests of this class are performed in <code>sztejkat.abstractfmt.obj</code>
	package with an apropriate test vehicle. Basic tests are performend in this
	package <code>TestXXX</code> classes.
*/
public abstract class ASignalWriteFormat implements ISignalWriteFormat
{
				/** See constructor */
				private final int max_name_length;
				/** See constructor */
				private final int max_events_recursion_depth;
				
				/** State indicating that elementary
				primitive element was written. This is 
				also an initial state of a stream */
				private static final byte STATE_PRIMITIVE = 0;
				/** State indicating that begin signal
				was written */
				private static final byte STATE_BEGIN = (byte)1;
				/** State indicating that end signal
				is pending for writing due to end-begin optimization
				*/
				private static final byte STATE_END_PENDING = (byte)2;
				/** State indicating that end signal
				was written */
				private static final byte STATE_END = (byte)3;
				/** State indicating that initial boolean block write
				was written. Each type of lock has own state */
				private static final byte STATE_BOOLEAN_BLOCK=(byte)4;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_BYTE_BLOCK=(byte)5;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_CHAR_BLOCK=(byte)6;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_SHORT_BLOCK=(byte)7;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_INT_BLOCK=(byte)8;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_LONG_BLOCK=(byte)9;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_FLOAT_BLOCK=(byte)10;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_DOUBLE_BLOCK=(byte)11;
				/** If closed */
				private static final byte STATE_CLOSED = (byte)12;
				
				
				/** Keeps track of current events depth */
				private int current_depth;
				/** A names registry, filled up with names, first
				null indicates end of used area. Null if registry is not used.*/
				private final String [] names_registry;
				/** A names registry, hash codes for registered names.
				Null if registry is not used.*/
				private final int [] names_registry_hash;
				/** State variable, can take one of 
				<code>STATE_xxx</code> constants.
				Initially {@link #STATE_PRIMITIVE}*/
				private byte state;
				
		/* *******************************************************
		
		
						Construction
		
		
		*********************************************************/
		/** Creates write format
		@param names_registry_size maximum number of names 
			to register if compact names should be implemented.
			This will be the maximum number of calls to {@link #writeRegisterName}
			and maximum index passed to that method will be <code>names_registry_size-1</code>.
			<p>
			Zero to disable registry and always use {@link #writeDirectName}
		@param max_name_length greater or equal to 8. Maximum length of names
			to be accepted in {@link #begin(String, boolean)} and be passed to
			{@link #writeSignalNameData}
		@param max_events_recursion_depth specifies the allowed depth of elements
			nesting. Zero disables limit, 1 sets limit to: "no nested elements allowed",
			2 allows element within an element and so on. If this limit is exceed
			the {@link #begin(String,boolean)} will throw <code>IllegalStateException</code>.
		*/
		protected ASignalWriteFormat(int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth
									 )
		{
			assert(names_registry_size>=0):"names_registry_size="+names_registry_size;
			assert(max_name_length>=8):"max_name_length="+max_name_length;
			assert(max_events_recursion_depth>=0):"max_events_recursion_depth="+max_events_recursion_depth;
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
		
		
				Core services required from subclasses		
				
		
		**********************************************************/
		/*========================================================
		
				Low level I/O
		
		=========================================================*/
		/** Should close low level operations.
		This class ensured that this method is called only once
		@see #close
		@throws IOException if failed.
		*/
		protected abstract void closeImpl()throws IOException;
		/*========================================================
		
				Indicators
		
		=========================================================*/
		/* -------------------------------------------------------
				Signals		
		---------------------------------------------------------*/
		/** Should write to stream an indicator that this is a begin
		signal being written now.
		<p>
		Once this method is called only following calls
		can be made:
		<ul>
			<li>{@link #writeDirectName};</li>
			<li>{@link #writeRegisterName};</li>
			<li>{@link #writeRegisterUse};</li>
		</ul>
		@throws IOException if low level i/o failed.
		*/
		protected abstract void writeBeginSignalIndicator()throws IOException;
		/** Should write to stream an indicator that this is an end signal.
		@throws IOException if low level i/o failed.
		*/
		protected abstract void writeEndSignalIndicator()throws IOException;
		/** Should have, functionally same effect as:
		<pre>
			writeEndSignalIndicator()
			writeBeginSignalIndicator()
		</pre>
		and default implementation is such.
		@throws IOException if low level i/o failed.
		*/
		protected void writeEndBeginSignalIndicator()throws IOException
		{
			writeEndSignalIndicator();
			writeBeginSignalIndicator();
		};
		/* -------------------------------------------------------
				Names in signals.		
		---------------------------------------------------------*/
		/** Invoked only after {@link #writeBeginSignalIndicator} to
		indicate that signal name will be encoded directly.
		<p>
		Once this method is called only following calls
		can be made:
		<ul>
			<li>{@link #writeSignalNameData};</li>
		</ul>
		@throws IOException if low level i/o failed.*/
		protected abstract void writeDirectName()throws IOException;
		/** Invoked only after {@link #writeDirectName} or {@link #writeRegisterName}
		to specify the signal name.
		@param name never null, always not longer than {@link ISignalWriteFormat#getMaxSignalNameLength}.
		@throws IOException if low level i/o failed.*/
		protected abstract void writeSignalNameData(String name)throws IOException;
		/** Invoked only after {@link #writeBeginSignalIndicator} to
		indicate that signal name will be encoded directly AND that this signal
		is used to indicate what index is assigned to a name.		
		<p>
		Once this method is called only following calls
		can be made:
		<ul>
			<li>{@link #writeSignalNameData};</li>
		</ul>
		@param name_index the non-negative name index assigned to name which will
			follow. This class ensures that:
			<ul>
				<li><code>name_index</code> is zero for first call of this
				method, 1 for next and so on, until stream is closed or
				the name registry pool is full. If stream is fine with
				this method of numbers assignments it may avoid writing 
				this number to a stream;</li>
			</ul>
		@throws IOException if low level i/o failed.*/
		protected abstract void writeRegisterName(int name_index)throws IOException;
		/** Invoked only after {@link #writeBeginSignalIndicator} to
		indicate that signal name will be passes as a number only and won't be written
		directly.
		@param name_index the non-negative name index assigned to name
				in apropriate {@link #writeRegisterName} call.
		@throws IOException if low level i/o failed.*/
		protected abstract void writeRegisterUse(int name_index)throws IOException;
		/* -------------------------------------------------------
				Types of primitives for described streams		
		---------------------------------------------------------*/
		/** Should write type indicator.
		Un-described formats may implement is a no-op. 
		<p>
		This implementation is no-op. 
		<p>
		This type write operation will be always invoked in following sequence:
		<pre>
			writeBooleanType()
			writeBooleanImpl(v)
			flushBoolean()
		</pre>
		End indicators are allowed to be no-ops even for typed streams.
		@throws IOException if low level i/o failed.
		@see #writeBoolean
		@see #writeBooleanImpl
		@see ASignalReadFormat#readIndicator
		*/
		protected  void writeBooleanType()throws IOException{};
		/** An entry point to make it easier to add type information
		for un-typed stream implementation if primitives must
		be surrounded by some data
		@see #writeBooleanType
		@throws IOException if low level i/o failed.
		*/
		protected  void flushBoolean()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeByteType()throws IOException{};
		/** See {@link #flushBoolean}
		@throws IOException if low level i/o failed.*/
		protected  void flushByte()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeCharType()throws IOException{};
		/** See {@link #flushBoolean}
		@throws IOException if low level i/o failed.*/
		protected  void flushChar()throws IOException{};
		
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeShortType()throws IOException{};		
		/** See {@link #flushBoolean}
		@throws IOException if low level i/o failed.*/
		protected  void flushShort()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeIntType()throws IOException{};
		/** See {@link #flushBoolean}
		@throws IOException if low level i/o failed.*/
		protected  void flushInt()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeLongType()throws IOException{};
		/** See {@link #flushBoolean}
		@throws IOException if low level i/o failed.*/
		protected  void flushLong()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeFloatType()throws IOException{};
		/** See {@link #flushBoolean}
		@throws IOException if low level i/o failed.*/
		protected  void flushFloat()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeDoubleType()throws IOException{};
		/** See {@link #flushBoolean}
		@throws IOException if low level i/o failed.*/
		protected  void flushDouble()throws IOException{};
		
		/** Should write type indicator.
		Un-described formats may implement is a no-op.
		<p>
		This implementation is no-op. 
		<p>
		This type write operation will be always invoked in following sequence:
		<pre>
			writeBooleanBlockType() //for initial write only
			writeBooleanBlockImpl(....)
			writeBooleanBlockImpl(....)
			writeBooleanBlockImpl(....)
			flushBooleanBlock()
		</pre>
		End indicators are allowed to be no-ops even for typed streams.
		@throws IOException if low level i/o failed.
		@see #writeBooleanBlock
		@see #writeBooleanBlockImpl
		@see ASignalReadFormat#readIndicator
		*/
		protected  void writeBooleanBlockType()throws IOException{};
		/** This method is invoked when block write operation is 
		terminated becaue the signal is written. This is an entry
		point for "flushig" operations which may be necessary
		if blocks are using "packing" what will be typical for
		bit-encoded boolean blocks.
		
		@throws IOException if low level i/o failed.
		@see #writeBooleanBlockType
		*/
		protected  void flushBooleanBlock()throws IOException{};
		
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeByteBlockType()throws IOException{};
		/** See {@link #flushBooleanBlock}
		@throws IOException if low level i/o failed.*/
		protected  void flushByteBlock()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeCharBlockType()throws IOException{};
		/** See {@link #flushBooleanBlock}
		@throws IOException if low level i/o failed.*/
		protected  void flushCharBlock()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeShortBlockType()throws IOException{};
		/** See {@link #flushBooleanBlock}
		@throws IOException if low level i/o failed.*/
		protected  void flushShortBlock()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeIntBlockType()throws IOException{};
		/** See {@link #flushBooleanBlock}
		@throws IOException if low level i/o failed.*/
		protected  void flushIntBlock()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeLongBlockType()throws IOException{};
		/** See {@link #flushBooleanBlock}
		@throws IOException if low level i/o failed.*/
		protected  void flushLongBlock()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeFloatBlockType()throws IOException{};
		/** See {@link #flushBooleanBlock}
		@throws IOException if low level i/o failed.*/
		protected  void flushFloatBlock()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeDoubleBlockType()throws IOException{};
		/** See {@link #flushBooleanBlock}
		@throws IOException if low level i/o failed.*/
		protected  void flushDoubleBlock()throws IOException{};
		
		/*========================================================
		
				primitive writes
				
				Core write methods invoked after state validation
				and transition, type write (if necessary) and arguments
				validation.
		
		=========================================================*/
		/* -------------------------------------------------------
				elementary primitives.
		-------------------------------------------------------*/
		/** Invoked in {@link #writeBoolean} after validation if it is
		allowed and after writing a type indicator.
		@param v as above
		@throws IOException as above 
		@see #writeBooleanType
		*/
		protected abstract void writeBooleanImpl(boolean v)throws IOException;
		/** See {@link #writeBooleanImpl}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeByteImpl(byte v)throws IOException;
		/** See {@link #writeBooleanImpl}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeCharImpl(char v)throws IOException;
		/** See {@link #writeBooleanImpl}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeShortImpl(short v)throws IOException;
		/** See {@link #writeBooleanImpl}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeIntImpl(int v)throws IOException;
		/** See {@link #writeBooleanImpl}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeLongImpl(long v)throws IOException;
		/** See {@link #writeBooleanImpl}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeFloatImpl(float v)throws IOException;
		/** See {@link #writeBooleanImpl}
		@param v --//--
		@throws IOException --//--
		*/
		protected abstract void writeDoubleImpl(double v)throws IOException;
		/* -------------------------------------------------------
				Block primitives
		-------------------------------------------------------*/
		/** Invoked inside {@link #writeBooleanBlock} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param buffer --//--, validated
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		protected abstract void writeBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException;
		
		/** Invoked inside {@link #writeByteBlock(byte[],int,int)} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param buffer --//--, validated
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		protected abstract void writeByteBlockImpl(byte [] buffer, int offset, int length)throws IOException;
		
		/** Invoked inside {@link #writeByteBlock(byte)} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param data --//--, validated
		@throws IOException --//--
		*/
		protected abstract void writeByteBlockImpl(byte data)throws IOException;
		
		
		/** Invoked inside {@link #writeCharBlock(char[],int,int)} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param buffer --//--, validated
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		protected abstract void writeCharBlockImpl(char [] buffer, int offset, int length)throws IOException;
	
		/** Invoked inside {@link #writeCharBlock(CharSequence,int,int)} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param characters --//--, validated
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		protected abstract void writeCharBlockImpl(CharSequence characters, int offset, int length)throws IOException;
	
		/** Invoked inside {@link #writeShortBlock(short[],int,int)} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param buffer --//--, validated
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		protected abstract void writeShortBlockImpl(short [] buffer, int offset, int length)throws IOException;
	
		/** Invoked inside {@link #writeIntBlock(int[],int,int)} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param buffer --//--, validated
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		protected abstract void writeIntBlockImpl(int [] buffer, int offset, int length)throws IOException;
	
		/** Invoked inside {@link #writeLongBlock(long[],int,int)} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param buffer --//--, validated
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		protected abstract void writeLongBlockImpl(long [] buffer, int offset, int length)throws IOException;
	
		/** Invoked inside {@link #writeFloatBlock(float[],int,int)} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param buffer --//--, validated
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		protected abstract void writeFloatBlockImpl(float [] buffer, int offset, int length)throws IOException;
	
		/** Invoked inside {@link #writeDoubleBlock(double[],int,int)} after
		validating if it can be done, writing a type (for initial operation only)
		and after validating arguments with assertions.
		@param buffer --//--, validated
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		protected abstract void writeDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException;
	
		
		/* ********************************************************
		
		
				State tracking support and validation
				
		
		**********************************************************/
		/** Checks if current state allows any elementary primitive write
		and toggles state to {@link #STATE_PRIMITIVE}
		@throws IllegalStateException if not.
		@throws IOException if needed to flush pending end indicator and it failed.
		*/
		private void startElementaryPrimitiveWrite()throws IllegalStateException,IOException
		{			
			validateNotClosed();
			flushPendingEnd();//this might be pending.
			
			assert(STATE_DOUBLE_BLOCK>STATE_BOOLEAN_BLOCK);//just to make sure that const assumptions are ok.
			assert(STATE_BOOLEAN_BLOCK>STATE_END);
			assert(STATE_BOOLEAN_BLOCK>STATE_BEGIN);
			assert(STATE_BOOLEAN_BLOCK>STATE_PRIMITIVE);
			
			if (state>=STATE_BOOLEAN_BLOCK) 
				throw new IllegalStateException("Cannot do elementary primitive write when block write is in progress.");
			state = STATE_PRIMITIVE;
		};
		
		/** If state is {@link #STATE_END_PENDING}
		writes an end indicator and togles state to {@link #STATE_END}.
		Used to optimize end-being sequence 
		@throws IOException if idicator write failed
		*/
		private void flushPendingEnd()throws IOException
		{
			if (state==STATE_END_PENDING)
			{
				state=STATE_END;
				writeEndSignalIndicator();
			};
		};
		/** Checs if any block operation is pending and calls
		their <code>flushXXXBlock</code>
		@throws IOException if called method thrown 
		*/
		private void closePendingBlocks()throws IOException
		{			
			switch(state)
			{
				case STATE_BOOLEAN_BLOCK: 	flushBooleanBlock(); break;
				case STATE_BYTE_BLOCK:	 	flushByteBlock(); break;
				case STATE_CHAR_BLOCK: 		flushCharBlock(); break;
				case STATE_SHORT_BLOCK: 	flushShortBlock(); break;
				case STATE_INT_BLOCK: 		flushIntBlock(); break;
				case STATE_LONG_BLOCK: 		flushLongBlock(); break;
				case STATE_FLOAT_BLOCK: 	flushFloatBlock(); break;
				case STATE_DOUBLE_BLOCK: 	flushDoubleBlock(); break;
				//no default block.
			};
		};
		/** Returns true if most recent operation has written a begin signal.
		<p>
		Specifically will be true inside 
		{@link #writeBeginSignalIndicator},{@link #writeDirectName} and other
		related to writing of begin signal.
		@return true if yes
		*/
		protected final boolean wasBeginWritten(){ return state==STATE_BEGIN; };
		/** Returns true if most recent operation has written an end signal.
		<p>
		Specifically will be true inside 
		{@link #writeEndBeginSignalIndicator}
		@return true if yes
		*/
		protected final boolean wasEndWritten(){ return state==STATE_END; };
		/** Returns true if most recent operation has written an elementary primitive
		or it is the beginning of a stream.
		<p>
		Specifically will be true inside all <code>writeXXXImpl</code> 
		and <code>writeXXXType</code> elementary primitive operations.
		@return true if yes
		*/
		protected final boolean wasElementaryPrimitiveWritten(){ return state==STATE_PRIMITIVE; };
		/** Returns true if most recent operation has written a primitive block
		or it is the beginning of a stream.
		<p>
		Specifically will be true inside all <code>writeXXXImpl</code> 
		and <code>writeXXXType</code> primitive block operations.
		@return true if yes
		*/
		protected final boolean wasBlockWritten(){ return state>=STATE_BYTE_BLOCK; };
		/** Throws if closed
		@throws EClosed if closed
		*/
		protected final void validateNotClosed()throws EClosed
		{
			if (state==STATE_CLOSED) throw new EClosed("Already closed");
		};
		/* ********************************************************
		
		
				ISignalWriteFormat
				
		
		**********************************************************/
		/*=============================================================
	
			Signal and events
			
		 =============================================================*/
		@Override public final int getMaxSignalNameLength(){ return max_name_length; };
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
			
			current_depth++;
			
			//Now we need to perform all pending actions.
			//Especially we need to close pending blocks
			closePendingBlocks();
			
			//We have to write an apropriate begin indicator
			//to handle optimized out end indicator.
			if (state==STATE_END_PENDING)
			{
					state=STATE_BEGIN;
					writeEndBeginSignalIndicator();
			}else
			{
					state=STATE_BEGIN;
					writeBeginSignalIndicator();
			};
			
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
						writeDirectName();
						writeSignalNameData(signal);
					}else
					{
						registered_index = putToIndex(signal);
						if (registered_index!=-1)
						{
							//name is registered.
							writeRegisterName(registered_index);
							writeSignalNameData(signal);
						}else
						{
							//we failed to register, need to store it directly
							writeDirectName();
							writeSignalNameData(signal);
						};
					};
				}else
				{
					//we have the name registered
					writeRegisterUse(registered_index);
				};
			}else
			{
					writeDirectName();
					writeSignalNameData(signal);
			};
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
			closePendingBlocks();
			
			if (current_depth==0) throw new IllegalStateException("Can't do end(), no event is active");
			current_depth--;		
				
			//and we need to put this operation on hold so end-begin can be optimized
			state = STATE_END_PENDING;
		};
		/** Always returns false. See class description for implementing "described" subclasses */
		@Override public boolean isDescribed(){ return false; };
		/*=============================================================
	
			Elementatry primitives.
			
			All primitives do validate if they can do it, write type 
			and delegate to writeXXXImpl methods.
		
			
		===============================================================*/
		/** {@inheritDoc}
		@see #startElementaryPrimitiveWrite
		@see #writeBooleanType
		@see #writeBooleanImpl
		*/
		@Override public final void writeBoolean(boolean v)throws IOException
		{
			startElementaryPrimitiveWrite();
			writeBooleanType();
			writeBooleanImpl(v);
			flushBoolean();
		};
		/** {@inheritDoc}
		@see #writeBoolean
		*/
		@Override public final void writeByte(byte v)throws IOException
		{
			startElementaryPrimitiveWrite();
			writeByteType();
			writeByteImpl(v);
			flushByte();
		};
		/** {@inheritDoc}
		@see #writeBoolean
		*/
		@Override public final void writeChar(char v)throws IOException
		{
			startElementaryPrimitiveWrite();
			writeCharType();
			writeCharImpl(v);
			flushChar();
		};
		/** {@inheritDoc}
		@see #writeBoolean
		*/
		@Override public final void writeShort(short v)throws IOException
		{
			startElementaryPrimitiveWrite();
			writeShortType();
			writeShortImpl(v);
			flushShort();
		};
		/** {@inheritDoc}
		@see #writeBoolean
		*/
		@Override public final void writeInt(int v)throws IOException
		{
			startElementaryPrimitiveWrite();
			writeIntType();
			writeIntImpl(v);
			flushInt();
		};
		/** {@inheritDoc}
		@see #writeBoolean
		*/
		@Override public final void writeLong(long v)throws IOException
		{
			startElementaryPrimitiveWrite();
			writeLongType();
			writeLongImpl(v);
			flushLong();
		};
		/** {@inheritDoc}
		@see #writeBoolean
		*/
		@Override public final void writeFloat(float v)throws IOException
		{
			startElementaryPrimitiveWrite();
			writeFloatType();
			writeFloatImpl(v);
			flushFloat();
		};
		/** {@inheritDoc}
		@see #writeBoolean
		*/
		@Override public final void writeDouble(double v)throws IOException
		{
			startElementaryPrimitiveWrite();
			writeDoubleType();
			writeDoubleImpl(v);
			flushDouble();
		};
		/*=============================================================
	
			Block primitives.
			
			All primitives do validate if they can do it, validate params,
			write type and delegate to writeXXXImpl methods.		
			
		===============================================================*/
		
		/** Validates and changes state, checks arguments, calls {@link #writeBooleanBlockType}
		and delegates writing to {@link #writeBooleanBlockImpl}
		*/
		@SuppressWarnings("fallthrough")
		@Override public final void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
		{		
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE: 
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:				
						state = STATE_BOOLEAN_BLOCK;
						writeBooleanBlockType();
						break;
				case STATE_BOOLEAN_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
				
			writeBooleanBlockImpl(buffer,offset,length);
		};
		/** See {@link #writeBooleanBlock} */
		@SuppressWarnings("fallthrough")
		@Override public final void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE:
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:						
						state = STATE_BYTE_BLOCK;
						writeByteBlockType();
						break;
				case STATE_BYTE_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
				
			writeByteBlockImpl(buffer,offset,length);
		};
		
		/** See {@link #writeBooleanBlock} */
		@SuppressWarnings("fallthrough")
		@Override public final void writeByteBlock(byte data)throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE:
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:				
						state = STATE_BYTE_BLOCK;
						writeByteBlockType();
						break;
				case STATE_BYTE_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			
			writeByteBlockImpl(data);
		};
		
		/** See {@link #writeBooleanBlock} */
		@SuppressWarnings("fallthrough")
		@Override public final void writeCharBlock(char [] buffer, int offset, int length)throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE:
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:				
						state = STATE_CHAR_BLOCK;
						writeCharBlockType();
						break;
				case STATE_CHAR_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
				
			writeCharBlockImpl(buffer,offset,length);
		};
		
		/** See {@link #writeBooleanBlock} */
		@SuppressWarnings("fallthrough")
		@Override public final void writeCharBlock(CharSequence characters, int offset, int length)throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE:
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:				
						state = STATE_CHAR_BLOCK;
						writeCharBlockType();
						break;
				case STATE_CHAR_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			assert(characters!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=characters.length()):"characters.length="+characters.length()+" but offset="+offset+" length="+length+" do point outside buffer";
				
			writeCharBlockImpl(characters,offset,length);
		};
		
		/** See {@link #writeBooleanBlock} */
		@SuppressWarnings("fallthrough")
		@Override public final void writeShortBlock(short [] buffer, int offset, int length)throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE:
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:				
						state = STATE_SHORT_BLOCK;
						writeShortBlockType();
						break;
				case STATE_SHORT_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
				
			writeShortBlockImpl(buffer,offset,length);
		};
		
		/** See {@link #writeBooleanBlock} */
		@SuppressWarnings("fallthrough")
		@Override public final void writeIntBlock(int [] buffer, int offset, int length)throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE:
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:				
						state = STATE_INT_BLOCK;
						writeIntBlockType();
						break;
				case STATE_INT_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
				
			writeIntBlockImpl(buffer,offset,length);
		};
		
		/** See {@link #writeBooleanBlock} */
		@SuppressWarnings("fallthrough")
		@Override public final void writeLongBlock(long [] buffer, int offset, int length)throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE:
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:				
						state = STATE_LONG_BLOCK;
						writeLongBlockType();
						break;
				case STATE_LONG_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
				
			writeLongBlockImpl(buffer,offset,length);
		};
		
		/** See {@link #writeBooleanBlock} */
		@SuppressWarnings("fallthrough")
		@Override public final void writeFloatBlock(float [] buffer, int offset, int length)throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE:
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:				
						state = STATE_FLOAT_BLOCK;
						writeFloatBlockType();
						break;
				case STATE_FLOAT_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
				
			writeFloatBlockImpl(buffer,offset,length);
		};
		
		/** See {@link #writeBooleanBlock} */
		@SuppressWarnings("fallthrough")
		@Override public final void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{
			validateNotClosed();
			flushPendingEnd();
			switch(state)
			{
				case STATE_PRIMITIVE:
				case STATE_END:
						if (current_depth==0)
								throw new IllegalStateException("Can't start block write if there is no event active");
						//fallthrough
				case STATE_BEGIN:				
						state = STATE_DOUBLE_BLOCK;
						writeDoubleBlockType();
						break;
				case STATE_DOUBLE_BLOCK:
						break;
				default: throw new IllegalStateException("Can't do primitive block write if other type of block write is in progress");
			}
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
				
			writeDoubleBlockImpl(buffer,offset,length);
		};
		/*=============================================================
		
			Status
			
		=============================================================*/
		/** Implemented to ensure that any pending optimized out end indicator
		is flushed to underlying stream.
		<p>
		This means, that flushing may affect stream content in such a way,
		that the:
		<pre>
			begin(...)
			end()
			begin(...)
			end()
		</pre>
		results in following sequence of indicators:
		<pre>
			 begin indicator
			 end-begin indicator
			 end indicator
		</pre>
		while
		<pre>
			begin(...)
			end()
			flush()
			begin(...)
			end()
		</pre>
		will produce:
		<pre>
			 begin indicator
			 end indicator
			 begin indicator
			 end indicator
		</pre>
		*/
		@Override public void flush()throws IOException
		{
			validateNotClosed();
			flushPendingEnd(); 
		};
		
		/** Overriden to toggle state to closed.
		Calls {@link #closeImpl}
		*/
		@Override public final void close()throws IOException
		{ 
			if (state!=STATE_CLOSED)
			{
				try{
					flush(); 					
				}finally{ state=STATE_CLOSED; closeImpl(); };
			};
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* *****************************************************************************
		
		
				Junit test area (junit 4 style)
		
				
				This test is intendend to test private routines.
		
		* *****************************************************************************/	
		/** Routine for internal tests */
		public static final class Test extends sztejkat.utils.test.ATest
		{
				/** Test bed device, throws on almost all methods */
				private static final class DUT extends ASignalWriteFormat
				{
					DUT(int names_registry_size,
						int max_name_length,
						int max_events_recursion_depth)
					{
						super(names_registry_size,max_name_length,max_events_recursion_depth);
					};
					protected void closeImpl()throws IOException{ throw new AbstractMethodError(); }
					protected void writeBeginSignalIndicator()throws IOException{ throw new AbstractMethodError(); }
					protected void writeEndSignalIndicator()throws IOException{ throw new AbstractMethodError(); }
					protected void writeDirectName()throws IOException{ throw new AbstractMethodError(); }
					protected void writeSignalNameData(String name)throws IOException{ throw new AbstractMethodError(); }
					protected void writeRegisterName(int name_index)throws IOException{ throw new AbstractMethodError(); }
					protected void writeRegisterUse(int name_index)throws IOException{ throw new AbstractMethodError(); }
					protected  void writeBooleanImpl(boolean v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeByteImpl(byte v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeCharImpl(char v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeShortImpl(short v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeIntImpl(int v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeLongImpl(long v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeFloatImpl(float v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeDoubleImpl(double v)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeByteBlockImpl(byte [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeByteBlockImpl(byte data)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeCharBlockImpl(char [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeCharBlockImpl(CharSequence characters, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeShortBlockImpl(short [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeIntBlockImpl(int [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeLongBlockImpl(long [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeFloatBlockImpl(float [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					protected  void writeDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
				
		
				};
			@org.junit.Test public void testNameResistry()
			{
				enter();
				/*
					In this test we check if we can put a name to a registry
					and if indexes are returned in proper order.
				*/
					ASignalWriteFormat D = 
							new DUT(3,//int names_registry_size,
									45,//int max_name_length,
									0//int max_events_recursion_depth
									);
					{
						int i = D.putToIndex("SPAMMER");
						org.junit.Assert.assertTrue(i==0);
					};
					{
						int i = D.putToIndex("GRONK");
						org.junit.Assert.assertTrue(i==1);
					};
					{
						int i = D.putToIndex("PARABOOM");
						org.junit.Assert.assertTrue(i==2);
					};
					{
						int i = D.putToIndex("KABOOM");
						org.junit.Assert.assertTrue(i==-1);
					};
				
				leave();
			};
			
			@org.junit.Test public void testNameResistry2()
			{
				enter();
				/*
					In this test we check if we can put a name to a registry
					and find it.
				*/
					ASignalWriteFormat D = 
							new DUT(3,//int names_registry_size,
									45,//int max_name_length,
									0//int max_events_recursion_depth
									);
					{
						int i = D.putToIndex("SPAMMER");
						org.junit.Assert.assertTrue(i==0);
					};
					{
						int i = D.putToIndex("GRONK");
						org.junit.Assert.assertTrue(i==1);
					};
					{
						int i = D.findInIndex("PARABOOM");
						org.junit.Assert.assertTrue(i==-1);
					};
					{
						int i = D.findInIndex("SPAMMER");
						org.junit.Assert.assertTrue(i==0);
					};
					{
						int i = D.findInIndex("GRONK");
						org.junit.Assert.assertTrue(i==1);
					};
				
				leave();
			};
		};
};