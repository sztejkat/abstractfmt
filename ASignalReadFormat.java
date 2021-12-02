package sztejkat.abstractfmt;
import java.io.IOException;

/**
	A core, elementary implementation.
	<p>
	This class is actually providing all services necessary for
	{@link IDescribedSignalReadFormat} but does not implement it.
	Subclasses are allowed to implement it if necessary.
	<p>
	The entire process is made around {@link #readIndicator}
	which is a facility necessary to provide informations
	about <i>indicators</i> written to stream by {@link ASignalWriteFormat}
	
	<h1>Stream syntax</h1>
	The stream syntax is built by {@link ASignalWriteFormat} and is 
	made around <i>indicators</i> and primitive data.
	<p>
	The event inside a stream follows, rougly speaking, following
	structure, which can be represented by sequence of calls presented 
	below.
	
	<h2>Begin signal, directly encoded name</h2>
	<pre>
			{@link #readIndicator()}=={@link #BEGIN_INDICATOR}
			{@link #readIndicator()}=={@link #DIRECT_INDICATOR}
			{@link #readSignalNameData}
	</pre>
	<h2>Begin signal, registration of name</h2>
	<pre>
			{@link #readIndicator()}=={@link #BEGIN_INDICATOR}
			{@link #readIndicator()}=={@link #REGISTER_INDICATOR}
			{@link #readRegisterIndex}
			{@link #readSignalNameData}
	</pre>
	<h2>Begin signal, use of registered</h2>
	<pre>
			{@link #readIndicator()}=={@link #BEGIN_INDICATOR}
			{@link #readIndicator()}=={@link #REGISTER_USE_INDICATOR}
			{@link #readRegisterUse}
	</pre>
	
	<h2>Body, un-described</h2>
	<pre>
			{@link #readIndicator()}=={@link #NO_INDICATOR}
			<i>primitive read of any kind</i>
			{@link #readIndicator()}=={@link #NO_INDICATOR}
			<i>primitive read of any kind</i>
			...
			{@link #readIndicator()}=={@link #END_INDICATOR} or {@link #END_BEGIN_INDICATOR}
	</pre>
	
	<h2>Body, described</h2>
	<pre>
			{@link #readIndicator()}==TYPE_XXXX
			<i>primitive read of requested kind</i>
			{@link #readIndicator()}==TYPE_XXX_END //optionally
			{@link #readIndicator()}==TYPE_XXXX
			<i>primitive read of requested kind</i>
			...
			{@link #readIndicator()}=={@link #END_INDICATOR} or {@link #END_BEGIN_INDICATOR}
	</pre>
	
*/
public abstract class ASignalReadFormat implements ISignalReadFormat
{
				/** See constructor */
				private final int max_events_recursion_depth;				
				/** Keeps track of current events depth */
				private int current_depth;
				
				/** A names registry, filled up with names, first
				null indicates end of used area. Null if registry is not used.
				This array is fixed in size and initialized to the size
				of names registry. May be null if name registry is not used.
				*/
				private final String [] names_registry;
				/** Name buffer, reusable */
				private final CBoundAppendable name_buffer;
				/** If set to true this class strictly validates
				that stream do describe all primitive elements.
				If false allows some elements to be described and some not.
				*/
				private final boolean strict_described_types;
				
				/** Pending, not processed yet indicator.
				If set to {@link #EOF_INDICATOR} means, that
				the indicator should be updated from {@link #readIndicator}.
				<p>
				This variable is used to change the easier to implement
				in subclass paradigm <i>read_indicator + move cursor</i>
				to <i>get indicator + consume indicator</i>.
				*/
				private int pending_indicator = EOF_INDICATOR;
				
				/** State indicating that any primitive operation
				can be initialized.*/
				private static final byte STATE_PRIMITIVE = 0;			
				
				/** State indicating that initial boolean block write
				was written. Each type of lock has own state */
				private static final byte STATE_BOOLEAN_BLOCK=(byte)2;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_BYTE_BLOCK=(byte)3;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_CHAR_BLOCK=(byte)4;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_SHORT_BLOCK=(byte)5;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_INT_BLOCK=(byte)6;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_LONG_BLOCK=(byte)7;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_FLOAT_BLOCK=(byte)8;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_DOUBLE_BLOCK=(byte)9;
				/** If closed */
				private static final byte STATE_CLOSED = (byte)10;
				/** If broken */
				private static final byte STATE_BROKEN = (byte)11;
				/** State variable, can take one of 
				<code>STATE_xxx</code> constants.
				Initially {@link #STATE_PRIMITIVE}*/
				private byte state;
				
		/* ********************************************************
		
				Construction
				
		
		*********************************************************/
		/** Creates write format
		@param names_registry_size maximum number of names 
			to register if compact names should be implemented.			
			<p>
			Zero to disable registry and reject streams which are using it.
		@param max_name_length greater or equal to 8. Maximum length of names
			to be accepted in as signal names in stream.
		@param max_events_recursion_depth specifies the allowed depth of elements
			nesting. Zero disables limit, 1 sets limit to: "no nested elements allowed",
			2 allows element within an element and so on. If this limit is exceed
			the {@link #next} will throw {@link EFormatBoundaryExceeded} if stream
			contains too deep recursion of elements.
		@param strict_described_types if true this class requires that stream is described.
			If false it accepts primitives elements descriptions and serves it to user,
			but does not require it.
		*/
		protected ASignalReadFormat(int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth,
									 boolean strict_described_types
									 )
		{
			assert(names_registry_size>=0):"names_registry_size="+names_registry_size;
			assert(max_name_length>=8):"max_name_length="+max_name_length;
			assert(max_events_recursion_depth>=0):"max_events_recursion_depth="+max_events_recursion_depth;
			if (names_registry_size>0)
			{
				this.names_registry 	 = new String[names_registry_size];
			}else
			{
				this.names_registry = null;
			}
			this.name_buffer = new CBoundAppendable(max_name_length);
			this.max_events_recursion_depth=max_events_recursion_depth;
			this.strict_described_types=strict_described_types;
		};				
		/* ********************************************************
		
		
				Core services required from subclasses		
				
		
		**********************************************************/
		/*========================================================
		
				Indicators
		
		=========================================================*/
				
			    /** Returned by {@link #readIndicator} to inform,
			    that there is no more data in stream */
				protected static final  int EOF_INDICATOR = -1;
				/** Returned by {@link #readIndicator} to inform,
			    that there is no indicator under cursor, so nothing
			    could have been read.
			    <p>
			    Un-described streams are returning it inside
			    a primitive data. Described streams should not return it.
			    */
				protected static final int NO_INDICATOR = 0;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a begin indicator and it was read.
				<p>
				After this indicator only {@link #DIRECT_INDICATOR},
				{@link #REGISTER_INDICATOR} and {@link #REGISTER_USE_INDICATOR}
				can appear in stream.
				@see ASignalWriteFormat#writeBeginSignalIndicator
				*/
				protected static final  int BEGIN_INDICATOR = 1;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a an end indicator and it was read.
				@see ASignalWriteFormat#writeEndSignalIndicator
				*/
				protected static final  int END_INDICATOR = 2;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a an end-begin indicator and it was read.
				@see ASignalWriteFormat#writeEndBeginSignalIndicator
				*/
				protected static final  int END_BEGIN_INDICATOR = 3;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a a "direct name" indicator.
				<p>
				Once this indicator is read the {@link #readSignalNameData}
				is the only method which can be invoked.
				@see ASignalWriteFormat#writeDirectName
				@see ASignalWriteFormat#writeSignalNameData
				*/ 
				protected static final int DIRECT_INDICATOR=4;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a a "register name" indicator.
				<p>
				Once this indicator is read following calls have to 
				be made:
				<ul>
					<li>{@link #readRegisterIndex};</li>
					<li>{@link #readSignalNameData};</li>
				</ul>
				@see ASignalWriteFormat#writeRegisterName
				@see ASignalWriteFormat#writeSignalNameData
				*/ 
				protected static final int REGISTER_INDICATOR=5;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a a "register use" indicator.
				<p>
				Once this indicator is read following calls have to 
				be made:
				<ul>
					<li>{@link #readRegisterUse};</li>
				</ul>
				@see ASignalWriteFormat#writeRegisterUse
				*/ 
				protected static final int REGISTER_USE_INDICATOR=6;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a primitive type begin
				indicator.
				<p>
				This value is returned only by typed
				streams and this class will use it to throw {@link EDataMissmatch}
				when an inapropriate read data is called.
				<p>
				Once this indicator is returned the only method
				which is allowed to be called is either
				{@link #skip} or apropriate primitive read.
				
				@see ASignalWriteFormat#writeBooleanType
				*/
				protected static final int TYPE_BOOLEAN = 7;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a primitive type end
				indicator.
				<p>
				This value is returned by typed streams and only if
				typed stream do implement end indicator.
				<p>
				This operation has no special effects.
				@see ASignalWriteFormat#writeBooleanTypeEnd
				*/
				protected static final int TYPE_BOOLEAN_END = 7+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_BYTE = 8;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_BYTE_END = 8+0x100;
				
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_CHAR = 9;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_CHAR_END = 9+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_SHORT = 10;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_SHORT_END = 10+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_INT = 11;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_INT_END = 11+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_LONG = 12;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_LONG_END = 12+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_FLOAT = 13;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_FLOAT_END = 13+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_DOUBLE = 14;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_DOUBLE_END = 14+0x100;
				
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_BOOLEAN_BLOCK = 15;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_BOOLEAN_BLOCK_END = 15+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_BYTE_BLOCK = 16;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_BYTE_BLOCK_END = 16+0x100;
				
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_CHAR_BLOCK = 17;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_CHAR_BLOCK_END = 17+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_SHORT_BLOCK = 18;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_SHORT_BLOCK_END = 18+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_INT_BLOCK = 19;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_INT_BLOCK_END = 19+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_LONG_BLOCK = 20;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_LONG_BLOCK_END = 20+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_FLOAT_BLOCK = 21;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_FLOAT_BLOCK_END = 21+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_DOUBLE_BLOCK = 22;
				/** See {@link #TYPE_BOOLEAN} */
				protected static final int TYPE_DOUBLE_BLOCK_END = 22+0x100;
				
		/** Checks if there is indicator under cursor
		and if it is, reads it.
		<ul>
				<li>{@link #EOF_INDICATOR} if there is an end-of-stream;</li>
				<li>{@link #NO_INDICATOR} if there is no indicator at cursor. 
				In such case cursor	is not moved and subsequent calls to this 
				method must return the same value;</li>
				<li><code>xxx_INDICATOR</code> or <code>TYPE_xxx</code>
				if there were indicators under cursor. In such case indicators
				are read from stream and cursor is move to next position
				after indicators;</li>
		</ul>
		Note: Regardless of how it is implemented {@link #skip} must be always
		possible.
		
		@return indicator type
		@throws IOException if failed at low level i/o
		@throws EBrokenStream if broken beyond repair.
		*/
		protected abstract int readIndicator()throws IOException;
		/** Skips data to next indicator, stopping with cursor at the indicator so that
		next call to {@link #readIndicator} could return found indicator.
		Can be invoked in any place.
		<p>
		Note: Subclasses may restrict the meaning "invoked in any place" and
		throw {@link EBrokenStream} if it is invoked due to possibly errornous operation,
		like inside a "begin" signal processing, which suggests that stream is too much
		corrupt to be usable. Skipping inside a primitive data must be always possible.
		</p>
		
		@throws IOException if failed at low level
		@throws EBrokenStream if broken beyond repair.
		@throws EUnexpectedEof if encounterd end of stream before reaching indicator
		@throws ECorruptedFormat if stream is broken beyond repair.
		*/
		protected abstract void skip()throws IOException,EUnexpectedEof;
		/* -------------------------------------------------------
				Signals		
		---------------------------------------------------------*/
		/** Should read characters representing name of a signal after a direct or
		register name indicators.
		@param a where to append characters
		@param limit up to how many characters append.
		@throws IOException if failed at low level
		@throws EFormatBoundaryExceeded if found in stream was longer than <code>limit</code>
		@throws EBrokenStream if it is broken beyond repair.
		@throws EUnexpectedEof if end of stream was found in name
		*/
		protected abstract void readSignalNameData(Appendable a, int limit)throws IOException;
		/** Should read number stored within "register" indicator written
		with {@link ASignalWriteFormat#writeRegisterName}
		@return either index, if it was actually written to a stream or -1 if stream
				assumes that indexes are to be deduced from an occurence number of "register" indicator.
		@throws IOException if failed at low level
		@throws EBrokenStream if it is broken beyond repair.
		*/	
		protected abstract int readRegisterIndex()throws IOException;
		/** Should read number stored within "register" indicator written
		with {@link ASignalWriteFormat#writeRegisterUse}
		@return stored index.
		@throws IOException if failed at low level
		@throws EBrokenStream if it is broken beyond repair.
		*/	
		protected abstract int readRegisterUse()throws IOException;
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
		
				primitive reads
		
		=========================================================*/
		/* -------------------------------------------------------
				Elementary
		-------------------------------------------------------*/
		/** Will be invoked from within {@link #readBoolean}.
		This method will be called only if all validations are ok,
		including state and type information (if present and
		depending on {@link #strict_described_types}).		
		@return read value
		@throws IOException if failed at low level, or any subclass as {@link #readBoolean}
				defines.
		@throws EBrokenStream if it is broken beyond repair.		
		*/
		protected abstract boolean readBooleanImpl()throws IOException;
		/** See {@link #readBooleanImpl}
		@return value
		@throws IOException --//-- */
		protected abstract byte readByteImpl()throws IOException;
		/** See {@link #readBooleanImpl}
		@return value
		@throws IOException --//-- */
		protected abstract char readCharImpl()throws IOException;
		/** See {@link #readBooleanImpl}
		@return value
		@throws IOException --//-- */
		protected abstract short readShortImpl()throws IOException;
		/** See {@link #readBooleanImpl}
		@return value
		@throws IOException --//-- */
		protected abstract int readIntImpl()throws IOException;
		/** See {@link #readBooleanImpl}
		@return value
		@throws IOException --//-- */
		protected abstract long readLongImpl()throws IOException;
		/** See {@link #readBooleanImpl}
		@return value
		@throws IOException --//-- */
		protected abstract float readFloatImpl()throws IOException;
		/** See {@link #readBooleanImpl}
		@return value
		@throws IOException --//-- */
		protected abstract double readDoubleImpl()throws IOException;
		/* -------------------------------------------------------
				Blocks
		-------------------------------------------------------*/
		/** Invoked inside {@link #readBooleanBlock(boolean[],int,int)} when this operation
		is allowed. All arguments are validated and if stream is described type
		is checked.
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return number of read items or 0 if reached any kind of indicator, including type end information.
				In such case indicator is not read from stream. Returns -1 if end of stream was reached.
		@throws IOException if failed at low level
		@throws EBrokenStream if it is broken beyond repair.
		*/
		protected abstract int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException;
	
		/** See {@link #readBooleanBlockImpl}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException --//--
		*/
		protected abstract int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException;
		/** Invoked inside {@link #readByteBlock()} when this operation
		is allowed. All arguments are validated and if stream is described type
		is checked.
		@return as {@link #readBooleanBlock}
		@throws IOException if failed at low level
		@throws EBrokenStream if it is broken beyond repair.
		*/
		protected abstract int readByteBlockImpl()throws IOException;
		/** See {@link #readBooleanBlockImpl}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException --//--
		*/
		protected abstract int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException;
		/** See {@link #readBooleanBlockImpl}
		@param buffer --//--
		@param length --//--
		@return --//--
		@throws IOException --//--
		*/
		protected abstract int readCharBlockImpl(Appendable buffer, int length)throws IOException;
		
		/** See {@link #readBooleanBlockImpl}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException --//--
		*/
		protected abstract int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException;
		/** See {@link #readBooleanBlockImpl}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException --//--
		*/
		protected abstract int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException;
		/** See {@link #readBooleanBlockImpl}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException --//--
		*/
		protected abstract int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException;
		/** See {@link #readBooleanBlockImpl}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException --//--
		*/
		protected abstract int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException;
		/** See {@link #readBooleanBlockImpl}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return --//--
		@throws IOException --//--
		*/
		protected abstract int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException;
		
		/* ***************************************************************
		
				processing
				
		
		****************************************************************/
		/* -----------------------------------------------------------------
				State validation
		-----------------------------------------------------------------*/
		/** Throws if broken
		@throws EBrokenStream if broken
		*/
		protected final void validateNotBroken()throws EBrokenStream
		{
			if (state==STATE_BROKEN) throw new EBrokenStream("Stream is broken, cannot continue reading it.");
		};
		/** Breaks stream due to caught broken exception
		Always use in:
		<pre>
			try{
				}catch(EBrokenStream ex){ throw breakStream(ex); };
		</pre>
		@param due_to what to return
		@return due_to		
		*/
		protected final EBrokenStream breakStream(EBrokenStream due_to)
		{
			state = STATE_BROKEN;
			return due_to;
		};	
		
		/** Throws if closed 
		@throws EClosed if closed 
		*/
		protected final void validateNotClosed()throws EClosed
		{
			if (state==STATE_CLOSED) throw new EClosed("Already closed");			
		};
		/* -----------------------------------------------------------------
				Indicators processing.
				
				The pending_indicator
		-----------------------------------------------------------------*/
		/** Converts <code>TYPE_xxx</code> and <code>TYPE_xxx_END</code>
		to string representation
		@param type_indicator indicator to convert
		@return string form
		@throws AssertionError if type_indicator does not represen known types.
		*/
		private static String typeIndicatorToString(int type_indicator)
		{
				type_indicator &= ~0x100; //wipe END marker.
				switch(type_indicator)
				{
						case TYPE_BOOLEAN: 			return "boolean";	
						case TYPE_BYTE: 			return "byte";
						case TYPE_CHAR: 			return "char";
						case TYPE_SHORT: 			return "short";
						case TYPE_INT: 				return "int";
						case TYPE_LONG: 			return "long";
						case TYPE_FLOAT: 			return "float";
						case TYPE_DOUBLE: 			return "double";
						case TYPE_BOOLEAN_BLOCK: 	return "boolean[]";	
						case TYPE_BYTE_BLOCK: 		return "byte[]";
						case TYPE_CHAR_BLOCK: 		return "char[]";
						case TYPE_SHORT_BLOCK: 		return "short[]";
						case TYPE_INT_BLOCK: 		return "int[]";
						case TYPE_LONG_BLOCK: 		return "long[]";
						case TYPE_FLOAT_BLOCK: 		return "float[]";
						case TYPE_DOUBLE_BLOCK: 	return "double[]";
				};                                  
				throw new AssertionError("type_indicator="+type_indicator);
		};
		/** Picks up current pending indicator and turns it to be EOF_INDICATOR
		so that it won't linger anymore.
		@return indicator, EOF_INDICATOR if already consumed.
		*/
		private int consumeIndicator()
		{
			 int i = pending_indicator;
			 pending_indicator = EOF_INDICATOR;
			 return i;
		};
		/** Either picks currently pending indicator or reads it from 
		stream. Does not consume it, so subsequent calls do return
		the same value.
		@return indicator
		@throws IOException if failed in readIndicator
		@throws EBrokenStream if broken
		@throws EClosed if closed.
		*/
		private int getIndicator()throws IOException
		{
			validateNotClosed();
			validateNotBroken();			
			if (pending_indicator==EOF_INDICATOR)
			{
				try{
						pending_indicator = readIndicator();
					}catch(EBrokenStream ex){ throw breakStream(ex); };
			}
			return pending_indicator;
		};
		/** Either picks currently pending indicator or reads it from 
		stream and do consume it. Subsequent calls always read
		indicator from stream.
		@return indicator
		@throws IOException if failed in readIndicator
		@throws EBrokenStream if broken
		@throws EClosed if closed.
		*/
		private int getAndConsumeIndicator()throws IOException
		{
			validateNotClosed();
			validateNotBroken();	
			int indicator = pending_indicator;
			pending_indicator = EOF_INDICATOR;
			if (indicator==EOF_INDICATOR)
			{
				try{
						indicator = readIndicator();
					}catch(EBrokenStream ex){ throw breakStream(ex); };
			}
			return indicator;
		};
		/* ---------------------------------------------------------------------------
					Primitive ops.
		---------------------------------------------------------------------------*/
		/* ........................................................................
					Commons.
		........................................................................*/
		/** Checks if stream indicators do point to the fact, that a primitive
		operation of specified type can be performed.
		@param expected_type_indicator one of <code>TYPE_xxxx</code> indicators 
		@throws IOException if failed, including type and syntax validation.
		*/
		private void validatePrimitiveType(int expected_type_indicator)throws IOException
		{
			//check what indicator is under cursor?
			int indicator = getIndicator();
			if (indicator==expected_type_indicator)
			{
					//ideal type match, consume it.
					consumeIndicator();
					return;
			};
			//Handle non-ideal matches and end/begin signals.
			switch(indicator)
			{
				case NO_INDICATOR:
						//Undescribed streams do allow operation, but strict described do not
						//allow it.
						if (strict_described_types)
							throw new EDataMissmatch("Type information was expected but nothing was found");
						consumeIndicator();
						return;
				//rest is not consumed.
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: throw new ENoMoreData("Signal reached");
				case TYPE_BOOLEAN: 			
				case TYPE_BYTE: 			
				case TYPE_CHAR: 			
				case TYPE_SHORT: 			
				case TYPE_INT: 				
				case TYPE_LONG: 			
				case TYPE_FLOAT: 			        
				case TYPE_DOUBLE: 			
				case TYPE_BOOLEAN_BLOCK: 	
				case TYPE_BYTE_BLOCK: 		
				case TYPE_CHAR_BLOCK: 		
				case TYPE_SHORT_BLOCK: 		
				case TYPE_INT_BLOCK: 		
				case TYPE_LONG_BLOCK: 		
				case TYPE_FLOAT_BLOCK: 		
				case TYPE_DOUBLE_BLOCK: 	throw new EDataMissmatch("Expected "+typeIndicatorToString(expected_type_indicator)+
																 " but found type in stream is "+typeIndicatorToString(indicator));
				default:
										throw new ECorruptedFormat("Unexpected indicator "+indicator);		
			}
		};
		/** Checks if stream indicators do point to the fact, that a primitive
		operation of specified type can be finished.
		@param expected_type_end_indicator one of <code>TYPE_xxxx_END</code> indicators 
		@throws IOException if failed, including type and syntax validation.
		*/
		private void validatePrimitiveTypeEnd(int expected_type_end_indicator)throws IOException	
		{
			//check what indicator is under cursor?
			int indicator = getIndicator();
			if (indicator==expected_type_end_indicator)
			{
					//ideal type match, consume it.
					consumeIndicator();
					return;
			};
			//Handle non-ideal matches and end/begin signals.
			switch(indicator)
			{
				case NO_INDICATOR:
						//Undescribed streams do allow operation, but strict described do not
						//allow it.
						if (strict_described_types)
							throw new EDataMissmatch("Type information was expected but nothing was found");
						consumeIndicator();
						return;
				//The end indicator is optional, so we may also expect begin type indicator here.
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: throw new ENoMoreData("Signal reached");
				case TYPE_BOOLEAN: 			
				case TYPE_BYTE: 			
				case TYPE_CHAR: 			
				case TYPE_SHORT: 			
				case TYPE_INT: 				
				case TYPE_LONG: 			
				case TYPE_FLOAT: 			        
				case TYPE_DOUBLE: 			
				case TYPE_BOOLEAN_BLOCK: 	
				case TYPE_BYTE_BLOCK: 		
				case TYPE_CHAR_BLOCK: 		
				case TYPE_SHORT_BLOCK: 		
				case TYPE_INT_BLOCK: 		
				case TYPE_LONG_BLOCK: 		
				case TYPE_FLOAT_BLOCK: 		
				case TYPE_DOUBLE_BLOCK:
						//this should not be consumed, it is about next primitive operation.
						return;
				//but if it is end, it has to match
				case TYPE_BOOLEAN_END: 			
				case TYPE_BYTE_END: 			
				case TYPE_CHAR_END: 			
				case TYPE_SHORT_END: 			
				case TYPE_INT_END: 				
				case TYPE_LONG_END: 			
				case TYPE_FLOAT_END: 			        
				case TYPE_DOUBLE_END: 			
				case TYPE_BOOLEAN_BLOCK_END: 	
				case TYPE_BYTE_BLOCK_END: 		
				case TYPE_CHAR_BLOCK_END: 		
				case TYPE_SHORT_BLOCK_END: 		
				case TYPE_INT_BLOCK_END: 		
				case TYPE_LONG_BLOCK_END: 		
				case TYPE_FLOAT_BLOCK_END: 		
				case TYPE_DOUBLE_BLOCK_END: 	throw new EDataMissmatch("Expected "+typeIndicatorToString(expected_type_end_indicator)+
																 " but found type in stream is "+typeIndicatorToString(indicator));
				default:
										throw new ECorruptedFormat("Unexpected indicator "+indicator);		
			}
		}
		
		/* ........................................................................
					Elementary
		........................................................................*/
		private void validateNoBlockOperationInProgress()throws IllegalStateException
		{
			if ((state>=STATE_BOOLEAN_BLOCK)&&(state<=STATE_DOUBLE_BLOCK)) 
					throw new IllegalStateException("Block operation in progress"); 
		};
		/** Starts elementary primitive read operation of specified <code>TYPE_xxx</code>
		@param type_indicator <code>TYPE_xxx</code> constant
		@throws IOException if anything failed, including syntax and type check.
		@throws IllegalStateException if not allowed due to block operations.
		*/
		private void startElementaryPrimitive(int type_indicator)throws IOException, IllegalStateException
		{
				//check stream broken status
				validateNotBroken();
				validateNotClosed();			
				validateNoBlockOperationInProgress();
				validatePrimitiveType(type_indicator);
		};
		/** Ends elementary primitive read operation of specified <code>TYPE_xxx</code>
		@param type_indicator <code>TYPE_xxx</code> constant
		@throws IOException if anything failed, including syntax check.
		*/
		private void endElementaryPrimitive(int type_indicator)throws IOException
		{
				validatePrimitiveTypeEnd(type_indicator+0x100);
		};
		/*.........................................................................
					block
		..........................................................................*/
		/** Checks if stream indicators do point to the fact, that a primitive
		block operation of specified type can be initialized.
		@param expected_type_indicator one of <code>TYPE_xxxx_BLOCK</code> indicators
		@throws IOException if failed, including type and syntax validation.
		*/
		private void startPrimitiveBlockType(int expected_type_indicator)throws IOException
		{
			//check what indicator is under cursor?
			int indicator = getIndicator();
			if (indicator==expected_type_indicator)
			{
					//ideal type match, consume it.
					consumeIndicator();
					return;
			};
			//Handle non-ideal matches and end/begin signals.
			switch(indicator)
			{
				case NO_INDICATOR:
						//Undescribed streams do allow operation, but strict described do not
						//allow it.
						if (strict_described_types)
							throw new EDataMissmatch("Type information was expected but nothing was found");
						consumeIndicator();
						return;
				//rest is not consumed.
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: return;
				case TYPE_BOOLEAN: 			
				case TYPE_BYTE: 			
				case TYPE_CHAR: 			
				case TYPE_SHORT: 			
				case TYPE_INT: 				
				case TYPE_LONG: 			
				case TYPE_FLOAT: 			        
				case TYPE_DOUBLE: 			
				case TYPE_BOOLEAN_BLOCK: 	
				case TYPE_BYTE_BLOCK: 		
				case TYPE_CHAR_BLOCK: 		
				case TYPE_SHORT_BLOCK: 		
				case TYPE_INT_BLOCK: 		
				case TYPE_LONG_BLOCK: 		
				case TYPE_FLOAT_BLOCK: 		
				case TYPE_DOUBLE_BLOCK: 	throw new EDataMissmatch("Expected "+typeIndicatorToString(expected_type_indicator)+
																 " but found type in stream is "+typeIndicatorToString(indicator));
				default:
										throw new ECorruptedFormat("Unexpected indicator "+indicator);		
			}
		};
		/** Checks if stream indicators do point to the fact, that a primitive
		block operation of specified type can be continues.
		@param expected_type_indicator one of <code>TYPE_xxxx_BLOCK</code> indicators
		@return true if can continue and pass read request to lower level, false if it
				should not do it.
		@throws IOException if failed, including type and syntax validation.
		*/
		private boolean continuePrimitiveBlockType(int expected_type_indicator)throws IOException
		{
			//check what indicator is under cursor?
			int indicator = getIndicator();
			if (indicator==expected_type_indicator+0x100)
			{
					//ideal type match, ending indicator, consume it.
					consumeIndicator();
					return false;
			};
			//Handle non-ideal matches and end/begin signals.
			switch(indicator)
			{
				case NO_INDICATOR: return true;	//normal condition inside a block				
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: return false; //end of block
				default:
										throw new ECorruptedFormat("Unexpected indicator "+indicator);		
			}
		};
		
		
		
		/* ****************************************************************
		
				ISignalReadFormat
				
		
		* ****************************************************************/
		/* =================================================================
		
				Signals
		
		=================================================================*/
		@SuppressWarnings("fallthrough")
		public String next()throws IOException
		{
			//Skip to signal if not already at it.
			skip_loop:
			for(;;)
			{
				//check if stream did not break.
				validateNotBroken();
				try{
					//consume pending indicator, so that if any operation fails we will move to next indicator.
					final int indicator = consumeIndicator();
					switch(indicator)
					{
							case BEGIN_INDICATOR:
								{
									//validate depth
									if ((max_events_recursion_depth!=0)&&(max_events_recursion_depth<=current_depth))
										throw new EFormatBoundaryExceeded("Too deep elements recursion, max "+max_events_recursion_depth+" is allowed");
									current_depth++;
									//Manipulate state
									state = STATE_PRIMITIVE;									
									//Now check what next indicator is present
									final int begin_style_indicator = readIndicator();
									switch(begin_style_indicator)
									{
										case DIRECT_INDICATOR:
												//read name, safe bound way
												name_buffer.reset();
												readSignalNameData(name_buffer, name_buffer.capacity());
												return name_buffer.toString();												
										case REGISTER_INDICATOR:
												//read and process with registration
												if (names_registry==null) 
													throw new ECorruptedFormat("Name registry not in use but name registration indicator is present in stream");
												{
													//process with index.
													int index = readRegisterIndex();
													//validate it
													if (index<-1) throw new ECorruptedFormat("Name index "+index+" is negative");
													//solve auto indexation.
													if (index==-1)
													{
														final int n= names_registry.length;
														for(index=0;index<n;index++)
														{
															if (names_registry[index]==null) break;
														};
														//naturally end with index==names_registry.length if there is no place.
													};
													if (index>=names_registry.length)
																throw new ECorruptedFormat("Name index "+index+" is larger than supported registry or the registry is full.");
													//re-check if name can be stored?
													if (names_registry[index]!=null)
																throw new ECorruptedFormat("Name registry at "+index+" is already defined. Cannot re-define it");
													//fetch name
													name_buffer.reset();
													readSignalNameData(name_buffer, name_buffer.capacity());	//note: buffer validates length.
													//store it and return
													return names_registry[index]=name_buffer.toString();
												}
										case REGISTER_USE_INDICATOR:
												//fetch from registry
												if (names_registry==null) 
													throw new ECorruptedFormat("Name registry not in use but name registry use indicator is present in stream");												
												{
													int index = readRegisterUse();
													if (index<0) throw new ECorruptedFormat("Name index "+index+" is negative");
													if (index>=names_registry.length)
																throw new ECorruptedFormat("Name index "+index+" is larger than supported registry space");
													String name = names_registry[index];
													if (name==null)
															throw new ECorruptedFormat("Name registry at "+index+" is undefined.");
													return name;
												}
										default: 
											//we have set of not allowed indicators. Throw, but allow to recover.
											throw new ECorruptedFormat("Unexpected indicator "+begin_style_indicator);
										}
								} //no break, unreachable.
							case END_BEGIN_INDICATOR:
									//We need to fake BEGIN
									pending_indicator = BEGIN_INDICATOR;
									//fallthrough
							case END_INDICATOR:
									if (current_depth==0) throw new ECorruptedFormat("Found end signal, but currently there is no opened event");
									current_depth--;
									state = STATE_PRIMITIVE;	
									return null;									
									
							default:
									//skip content till next indicator 
									skip();
									pending_indicator = readIndicator(); //refresh it
									assert(pending_indicator!=EOF_INDICATOR):"skip contract is broken. It should have thrown EUnexpectedEof";
					 }
				  }catch(EBrokenStream ex){ throw breakStream(ex); };
			}
		};
		/** Implements both {@link ISignalReadFormat#hasData} and
		{@link IDescribedSignalReadFormat#hasData}.
		<p>
		Subclasses which are implementing {@link IDescribedSignalReadFormat} should
		set <code>strict_described_types</code> to true in constructor.
		*/
		public int hasData()throws IOException
		{
				final int indicator = getIndicator();
				switch(getIndicator())
				{
						case EOF_INDICATOR: 		return ISignalReadFormat.EOF;
							//Note: No indicator is returned by un-described streams
							//inside a data stream.
						case NO_INDICATOR:		
									if (strict_described_types) 
										throw new ECorruptedFormat("This is un-described stream or some errors broken stream to miss descriptions");
									return 0xFFFF;	//<-- as generic, non typed indicator.
						case BEGIN_INDICATOR:		//fallthrough
						case END_INDICATOR:			//fallthrough
						case END_BEGIN_INDICATOR: 	return ISignalReadFormat.SIGNAL;
						case TYPE_BOOLEAN: 			return IDescribedSignalReadFormat.PRMTV_BOOLEAN;
						case TYPE_BYTE: 			return IDescribedSignalReadFormat.PRMTV_BYTE;
						case TYPE_CHAR: 			return IDescribedSignalReadFormat.PRMTV_CHAR;
						case TYPE_SHORT: 			return IDescribedSignalReadFormat.PRMTV_SHORT;
						case TYPE_INT: 				return IDescribedSignalReadFormat.PRMTV_INT;
						case TYPE_LONG: 			return IDescribedSignalReadFormat.PRMTV_LONG;
						case TYPE_FLOAT: 			return IDescribedSignalReadFormat.PRMTV_FLOAT;
						case TYPE_DOUBLE: 			return IDescribedSignalReadFormat.PRMTV_DOUBLE;
						case TYPE_BOOLEAN_BLOCK: 	return IDescribedSignalReadFormat.PRMTV_BOOLEAN_BLOCK;
						case TYPE_BYTE_BLOCK: 		return IDescribedSignalReadFormat.PRMTV_BYTE_BLOCK;
						case TYPE_CHAR_BLOCK: 		return IDescribedSignalReadFormat.PRMTV_CHAR_BLOCK;
						case TYPE_SHORT_BLOCK: 		return IDescribedSignalReadFormat.PRMTV_SHORT_BLOCK;
						case TYPE_INT_BLOCK: 		return IDescribedSignalReadFormat.PRMTV_INT_BLOCK;
						case TYPE_LONG_BLOCK: 		return IDescribedSignalReadFormat.PRMTV_LONG_BLOCK;
						case TYPE_FLOAT_BLOCK: 		return IDescribedSignalReadFormat.PRMTV_FLOAT_BLOCK;
						case TYPE_DOUBLE_BLOCK: 	return IDescribedSignalReadFormat.PRMTV_DOUBLE_BLOCK;
						case DIRECT_INDICATOR:
						case REGISTER_INDICATOR:
						case REGISTER_USE_INDICATOR:
						default:					//fallthrough
								//we have set of not allowed indicators.
								throw new ECorruptedFormat("Unexpected indicator "+indicator);
				}
		};
		/* =================================================================
		
				Primitives
		
		=================================================================*/
		/* ---------------------------------------------------------------
		
				Elementary
		
		---------------------------------------------------------------*/
		/** Overriden to test initial conditions, check types (if present)
		and call {@link #readBooleanImpl}
		*/
		@Override public final boolean readBoolean()throws IOException
		{
				startElementaryPrimitive(TYPE_BOOLEAN);
				try{
					boolean v = readBooleanImpl();
					endElementaryPrimitive(TYPE_BOOLEAN);
					return v;
				}catch(EBrokenStream ex){ throw breakStream(ex); }
		};
		/** see {@link #readBoolean}
		*/
		@Override public final byte readByte()throws IOException
		{
				startElementaryPrimitive(TYPE_BYTE);
				try{
					byte v = readByteImpl();
					endElementaryPrimitive(TYPE_BYTE);
					return v;
				}catch(EBrokenStream ex){ throw breakStream(ex); }
		};
		/** see {@link #readBoolean}
		*/
		@Override public final char readChar()throws IOException
		{
				startElementaryPrimitive(TYPE_CHAR);
				try{
					char v = readCharImpl();
					endElementaryPrimitive(TYPE_CHAR);
					return v;
				}catch(EBrokenStream ex){ throw breakStream(ex); }
		};
		/** see {@link #readBoolean}
		*/
		@Override public final short readShort()throws IOException
		{
				startElementaryPrimitive(TYPE_SHORT);
				try{
					short v = readShortImpl();
					endElementaryPrimitive(TYPE_SHORT);
					return v;
				}catch(EBrokenStream ex){ throw breakStream(ex); }
		};
		/** see {@link #readBoolean}
		*/
		@Override public final int readInt()throws IOException
		{
				startElementaryPrimitive(TYPE_INT);
				try{
					int v = readIntImpl();
					endElementaryPrimitive(TYPE_INT);
					return v;
				}catch(EBrokenStream ex){ throw breakStream(ex); }
		};
		/** see {@link #readBoolean}
		*/
		@Override public final long readLong()throws IOException
		{
				startElementaryPrimitive(TYPE_LONG);
				try{
					long v = readLongImpl();
					endElementaryPrimitive(TYPE_LONG);
					return v;
				}catch(EBrokenStream ex){ throw breakStream(ex); }
		};
		/** see {@link #readBoolean}
		*/
		@Override public final float readFloat()throws IOException
		{
				startElementaryPrimitive(TYPE_FLOAT);
				try{
					float v = readFloatImpl();
					endElementaryPrimitive(TYPE_FLOAT);
					return v;
				}catch(EBrokenStream ex){ throw breakStream(ex); }
		};
		/** see {@link #readBoolean}
		*/
		@Override public final double readDouble()throws IOException
		{
				startElementaryPrimitive(TYPE_DOUBLE);
				try{
					double v = readDoubleImpl();
					endElementaryPrimitive(TYPE_DOUBLE);
					return v;
				}catch(EBrokenStream ex){ throw breakStream(ex); }
		};
		
		/* ---------------------------------------------------------------
		
				blocks
		
		---------------------------------------------------------------*/
		/** Overriden to test initial conditions, check types (if present), state and arguments 
		and call {@link #readBooleanBlockImpl}
		*/
		@Override public final int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_BOOLEAN_BLOCK);
						state=STATE_BOOLEAN_BLOCK;
						break;
				case STATE_BOOLEAN_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_BOOLEAN_BLOCK))
			{
				try{
					return readBooleanBlockImpl(buffer,offset,length);
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readByteBlock(byte [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_BYTE_BLOCK);
						state=STATE_BYTE_BLOCK;
						break;
				case STATE_BYTE_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_BYTE_BLOCK))
			{
				try{
					return readByteBlockImpl(buffer,offset,length);
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readByteBlock()throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_BYTE_BLOCK);
						state=STATE_BYTE_BLOCK;
						break;
				case STATE_BYTE_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_BYTE_BLOCK))
			{
				try{
					return readByteBlockImpl();
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readCharBlock(char [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_CHAR_BLOCK);
						state=STATE_CHAR_BLOCK;
						break;
				case STATE_CHAR_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_CHAR_BLOCK))
			{
				try{
					return readCharBlockImpl(buffer,offset,length);
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readCharBlock(Appendable buffer, int length)throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_CHAR_BLOCK);
						state=STATE_CHAR_BLOCK;
						break;
				case STATE_CHAR_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			assert(buffer!=null):"buffer==null";
			assert(length>=0):"length="+length+" is negative";
		
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_CHAR_BLOCK))
			{
				try{
					return readCharBlockImpl(buffer,length);
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readShortBlock(short [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_SHORT_BLOCK);
						state=STATE_SHORT_BLOCK;
						break;
				case STATE_SHORT_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_SHORT_BLOCK))
			{
				try{
					return readShortBlockImpl(buffer,offset,length);
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readIntBlock(int [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_INT_BLOCK);
						state=STATE_INT_BLOCK;
						break;
				case STATE_INT_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_INT_BLOCK))
			{
				try{
					return readIntBlockImpl(buffer,offset,length);
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readLongBlock(long [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_LONG_BLOCK);
						state=STATE_LONG_BLOCK;
						break;
				case STATE_LONG_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_LONG_BLOCK))
			{
				try{
					return readLongBlockImpl(buffer,offset,length);
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readFloatBlock(float [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_FLOAT_BLOCK);
						state=STATE_FLOAT_BLOCK;
						break;
				case STATE_FLOAT_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_FLOAT_BLOCK))
			{
				try{
					return readFloatBlockImpl(buffer,offset,length);
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateNotBroken();
			validateNotClosed();
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						startPrimitiveBlockType(TYPE_DOUBLE_BLOCK);
						state=STATE_DOUBLE_BLOCK;
						break;
				case STATE_DOUBLE_BLOCK:
						break;
				default: throw new IllegalStateException("Block operation of different type in progress");
			};
			assert(buffer!=null):"buffer==null";
			assert(offset>=0):"offset="+offset+" is negative";
			assert(length>=0):"length="+length+" is negative";
			assert(offset+length<=buffer.length):"buffer.length="+buffer.length+" but offset="+offset+" length="+length+" do point outside buffer";
		
			//pass to low level routine, but only if there is no signal blocking
			//it. Notice, without this check we might initialize operation with zero size
			//by reading from low stream the END signal and thous request reading data after this signal.
			if (continuePrimitiveBlockType(TYPE_DOUBLE_BLOCK))
			{
				try{
					return readDoubleBlockImpl(buffer,offset,length);
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/* =================================================================
		
				Status
		
		=================================================================*/
		/** Overriden to toggle state to closed.
		Calls {@link closeImpl}
		*/
		@Override public final void close()throws IOException
		{ 
			if (state!=STATE_CLOSED)
			{
				state=STATE_CLOSED;
				closeImpl();
			};
		};
};