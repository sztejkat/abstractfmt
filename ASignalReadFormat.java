package sztejkat.abstractfmt;
import java.io.IOException;

/**
	A core, elementary implementation for {@link ISignalReadFormat}
	<p>
	This class is a reading counterpart for {@link ASignalWriteFormat}
	and is also based on a concept of 
	<a href="package-description.html#indicators">indicators</a>.
	<p>
	This class is build around {@link #readIndicator} which provides
	an entry point to "indicators".
	
	<h2>Described vs un-described formats</h2>
	This class calls {@link #isDescribed} to validate how it
	should treat indicators returned by {@link #readIndicator}
	and what conditions should be checked.
	
	<h2>Testing</h2>
	Through tests of this class are performed in <code>sztejkat.abstractfmt.obj</code>
	package with an apropriate test vehicle. Basic tests are performend in this
	package <code>TestXXX</code> classes.
*/
public abstract class ASignalReadFormat implements ISignalReadFormat
{
				/* ---------------------------------------------------
							Defense
				-----------------------------------------------------*/
				/** Protectes against stack overrun attacks. See constructor */
				private final int max_events_recursion_depth;				
				/** Keeps track of current events depth */
				private int current_depth;
				/* ---------------------------------------------------
							Names registry
				-----------------------------------------------------*/
				/** A names registry, filled up with names, first
				null indicates end of used area. Null if registry is not used.
				This array is fixed in size and initialized to the size
				of names registry. May be null if name registry is not used.
				*/
				private final String [] names_registry;
				/** Name buffer, reusable */
				private final CBoundAppendable name_buffer;
				
				/* ---------------------------------------------------
						Indicators buffering.
				-----------------------------------------------------*/
				/** Pending, not processed yet indicator.
				If set to {@link #EOF_INDICATOR} means, that
				the indicator should be updated from {@link #readIndicator}.
				<p>
				This variable is used to change the easier to implement
				in subclass paradigm <i>read_indicator + move cursor</i>
				to <i>get indicator + consume indicator</i>.
				*/
				private int pending_indicator = EOF_INDICATOR;
				
				/* ---------------------------------------------------
					State tracking used to disallow some operation
				-----------------------------------------------------*/
				/** State indicating that any primitive operation
				can be initialized.*/
				private static final byte STATE_PRIMITIVE = 0;
				/** State indicating that initial boolean block write
				was read. Each type of block has own state and 
				state indicating that block reached end-of-data*/
				private static final byte STATE_BOOLEAN_BLOCK=(byte)1;
				private static final byte STATE_FIRST_BLOCK=STATE_BOOLEAN_BLOCK;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_BYTE_BLOCK=(byte)2;				
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_CHAR_BLOCK=(byte)3;				
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_SHORT_BLOCK=(byte)4;
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_INT_BLOCK=(byte)5;				
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_LONG_BLOCK=(byte)6;				
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_FLOAT_BLOCK=(byte)7;				
				/** See {@link #STATE_BOOLEAN_BLOCK} */
				private static final byte STATE_DOUBLE_BLOCK=(byte)8;
				private static final byte STATE_LAST_BLOCK=STATE_DOUBLE_BLOCK;
				/** If closed. Closed always throws. */
				private static final byte STATE_CLOSED = (byte)9;
				/** If broken. Broken behaves like closed, but throws 
				different exception. */
				private static final byte STATE_BROKEN = (byte)10;
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
		*/
		protected ASignalReadFormat(int names_registry_size,
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
			}else
			{
				this.names_registry = null;
			}
			this.name_buffer = new CBoundAppendable(max_name_length);
			this.max_events_recursion_depth=max_events_recursion_depth;
		};				
		
		
		/* ********************************************************
		
		
		
		
				Core services required from subclasses		
				
				
				
				
		
		**********************************************************/
		/*========================================================
		
				Indicators
		
		=========================================================*/
				
				/* ---------------------------------------------------
				
						Definitions for indicators.
				                                                      
				---------------------------------------------------*/
			    /** Returned by {@link #readIndicator} to inform,
			    that there is no more data in stream (physical end-of file)
			    This value is also used in a {@link #pending_indicator}
			    to indicated, that indicator was consumed and next one 
			    must be fetched from {@link #readIndicator}.
			    <p>
			    Note: Only numeric values which will be cast in stone
			    are {@link #EOF_INDICATOR} and {@link #NO_INDICATOR}.
			    All others may vary from version to version and no assumption
			    about their ordering nor values should be made.
			    
			    @see #getIndicator
			    @see #consumeIndicator
			    */
				public static final  int EOF_INDICATOR = -1;
				/** Returned by {@link #readIndicator} to inform
			    that there is no indicator under cursor, so nothing
			    could have been read. 
			    <p>
			    Un-described streams are expected to return it inside
			    or at the beginning of any primitive data.
			    <p>
			    Described streams are expected to return it inside
			    a blocks of primitive data, but are prohibitted
			    from returning it at the begining of primitive data.
			    <p>
			    This class will never ask described stream for an indicator
			    while inside an elementary primitive data.
			    */
				public static final int NO_INDICATOR = 0;
				/** Returned by {@link #readIndicator} to inform
				that under a cursor there was a begin indicator,
				it was fetched and cursor was moved forward.
				<p>
				After this indicator is read only {@link #DIRECT_INDICATOR},
				{@link #REGISTER_INDICATOR} and {@link #REGISTER_USE_INDICATOR}
				can appear in stream.
				@see ASignalWriteFormat#writeBeginSignalIndicator
				*/
				public static final  int BEGIN_INDICATOR = 1;
				/** Returned by {@link #readIndicator} to inform
				that under a cursor there was a an end indicator, it was read
				and cursor was moved forward.
				@see ASignalWriteFormat#writeEndSignalIndicator
				*/
				public static final  int END_INDICATOR = 2;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a an end-begin indicator, it was read
				and cursor was moved forward.
				@see ASignalWriteFormat#writeEndBeginSignalIndicator
				*/
				public static final  int END_BEGIN_INDICATOR = 3;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a a "direct name" indicator, it was read
				and cursor was moved forward.
				<p>
				Once this indicator is read the {@link #readSignalNameData}
				is the only method which can be invoked.
				@see ASignalWriteFormat#writeDirectName
				@see ASignalWriteFormat#writeSignalNameData
				*/ 
				public static final int DIRECT_INDICATOR=4;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a a "register name" indicator, it was read
				and cursor was moved forward.
				<p>
				Once this indicator is read following calls have to 
				be made in that order:
				<ol>
					<li>{@link #readRegisterIndex};</li>
					<li>{@link #readSignalNameData};</li>
				</ol>
				@see ASignalWriteFormat#writeRegisterName
				@see ASignalWriteFormat#writeSignalNameData
				*/ 
				public static final int REGISTER_INDICATOR=5;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a a "register use" indicator, it was read
				and cursor was moved forward.
				<p>
				Once this indicator is read following call has to 
				be made:
				<ul>
					<li>{@link #readRegisterUse};</li>
				</ul>
				@see ASignalWriteFormat#writeRegisterUse
				*/ 
				public static final int REGISTER_USE_INDICATOR=6;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was a primitive type begin
				indicator.
				<p>
				Once this indicator is returned the only method
				which is allowed to be called is either
				{@link #skip} or apropriate primitive read.
				<p>
				If stream is {@link #isDescribed} then it requires
				that this indicator is present prior to making a primitive read.
				<p>
				If stream is NOT {@link #isDescribed} then it requires
				that this indicator is NOT present and that {@link #NO_INDICATOR}
				is read from {@link #readIndicator}
				@see ASignalWriteFormat#writeBooleanType
				*/
				public static final int TYPE_BOOLEAN = 7;
				/** Returned by {@link #readIndicator} to inform,
				that under a cursor there was an optional
				primitive type end indicator. This is basically an entry
				point to make it easier to implement readers which work
				with formats which needs to surround primitives with some
				indicators.
				<p>
				This operation has no special effects.
				<p>
				If stream is {@link #isDescribed} then it accepts
				that this indicator is present after to making a primitive read
				and validates if it matches type,
				but also accepts a situation when there is no such indicator.
				<p>
				If stream is not {@link #isDescribed}, then it required
				that such type of an indicator is not present.
				@see ASignalWriteFormat#flushBoolean
				*/
				public static final int FLUSH_BOOLEAN = 7+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_BYTE = 8;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_BYTE = 8+0x100;
				
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_CHAR = 9;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_CHAR = 9+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_SHORT = 10;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_SHORT = 10+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_INT = 11;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_INT = 11+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_LONG = 12;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_LONG = 12+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_FLOAT = 13;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_FLOAT = 13+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_DOUBLE = 14;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_DOUBLE = 14+0x100;
				
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_BOOLEAN_BLOCK = 15;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_BOOLEAN_BLOCK = 15+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_BYTE_BLOCK = 16;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_BYTE_BLOCK = 16+0x100;
				
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_CHAR_BLOCK = 17;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_CHAR_BLOCK = 17+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_SHORT_BLOCK = 18;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_SHORT_BLOCK = 18+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_INT_BLOCK = 19;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_INT_BLOCK = 19+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_LONG_BLOCK = 20;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_LONG_BLOCK = 20+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_FLOAT_BLOCK = 21;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_FLOAT_BLOCK = 21+0x100;
				
				/** See {@link #TYPE_BOOLEAN} */
				public static final int TYPE_DOUBLE_BLOCK = 22;
				/** See {@link #TYPE_BOOLEAN} */
				public static final int FLUSH_DOUBLE_BLOCK = 22+0x100;
				/** Some described streams may use this indicator
				to surround elementary primitive, but not provide type
				information.
				<p>
				If stream is {@link #isDescribed} it must accept is
				instead of typed flush, ie {@link FLUSH_BOOLEAN}.
				<p>
				If stream is not {@link #isDescribed}  it must complain.
				*/
				public static final int FLUSH = 23+0x100;
				/** Some described streams may use this indicator
				to surround block of primitives, but not provide type
				information.
				<p>
				If stream is {@link #isDescribed} it must accept is
				instead of typed flush, ie {@link FLUSH_BOOLEAN_BLOCK}
				<p>
				If stream is not {@link #isDescribed}  it must complain.
				*/
				public static final int FLUSH_BLOCK = 24+0x100;
				/** Some described streams may use this indicator
				to surround block of primitives or elementary primitives,
				but not provide type information.
				<p>
				If stream is {@link #isDescribed} it must accept is
				instead of typed flush, ie {@link FLUSH_BOOLEAN_BLOCK}
				<p>
				If stream is not {@link #isDescribed}  it must complain.
				*/
				public static final int FLUSH_ANY = 25+0x100;
		
		
		/* ---------------------------------------------------
			
				Fetching and processing indicators.
				                                                      
		---------------------------------------------------*/
		/** Checks if there is indicator under cursor
		and if it is, reads it.
		Returns one of:
		<ul>
				<li>{@link #EOF_INDICATOR}</li>
				<li>{@link #NO_INDICATOR}</li>
				<li>{@link #BEGIN_INDICATOR}</li>
				<li>{@link #END_INDICATOR}</li>
				<li>{@link #END_BEGIN_INDICATOR}</li>
				<li>{@link #DIRECT_INDICATOR}</li>
				<li>{@link #REGISTER_INDICATOR}</li>
				<li>{@link #REGISTER_USE_INDICATOR}</li>
				<li>if format is {@link #isDescribed}:
					<ul>
					<li>{@link #TYPE_BOOLEAN},{@link #FLUSH_BOOLEAN}</li>
					<li>{@link #TYPE_BYTE},{@link #FLUSH_BYTE}</li>
					<li>{@link #TYPE_CHAR},{@link #FLUSH_CHAR}</li>
					<li>{@link #TYPE_SHORT},{@link #FLUSH_SHORT}</li>
					<li>{@link #TYPE_INT},{@link #FLUSH_INT}</li>
					<li>{@link #TYPE_LONG},{@link #FLUSH_LONG}</li>
					<li>{@link #TYPE_FLOAT},{@link #FLUSH_FLOAT}</li>
					<li>{@link #TYPE_DOUBLE},{@link #FLUSH_DOUBLE}</li>
					<li>{@link #TYPE_BOOLEAN_BLOCK},{@link #FLUSH_BOOLEAN_BLOCK}</li>
					<li>{@link #TYPE_BYTE_BLOCK},{@link #FLUSH_BYTE_BLOCK}</li>
					<li>{@link #TYPE_CHAR_BLOCK},{@link #FLUSH_CHAR_BLOCK}</li>
					<li>{@link #TYPE_SHORT_BLOCK},{@link #FLUSH_SHORT_BLOCK}</li>
					<li>{@link #TYPE_INT_BLOCK},{@link #FLUSH_INT_BLOCK}</li>
					<li>{@link #TYPE_LONG_BLOCK},{@link #FLUSH_LONG_BLOCK}</li>
					<li>{@link #TYPE_FLOAT_BLOCK},{@link #FLUSH_FLOAT_BLOCK}</li>
					<li>{@link #TYPE_DOUBLE_BLOCK},{@link #FLUSH_DOUBLE_BLOCK}</li>
					<li>{@link #FLUSH},{@link #FLUSH_BLOCK},{@link #FLUSH_BOOLEAN_BLOCK}</li>
					</ul>
				</li>
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
		corrupt to be usable.
		<p>
		Skipping un-read primitive data must be always possible.
		
		@throws IOException if failed at low level
		@throws EBrokenStream if broken beyond repair.
		@throws EUnexpectedEof if encounterd end of stream before reaching indicator
		@throws ECorruptedFormat if stream is broken beyond repair.
		*/
		protected abstract void skipData()throws IOException,EUnexpectedEof;
		
		/* -------------------------------------------------------
		
		
				Processing of data bound with indicators related
				to signal processing.
				
				
		---------------------------------------------------------*/
		/** Should read characters representing name of a signal after a direct or
		register name indicators.
		@param a where to append characters. This appendable is such, that 
				if more characters than limit is added to it then it throws
				{@link EFormatBoundaryExceeded}.		
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
		<p>
		The caller will invoked this method only if all conditions for safe invocation
		are met, including testing if indicators are pointing to correctly typed regions
		or data. Caller will also validate indicators after this method returns,
		so most implementations should just consume data, either known number of bytes
		or till end-of-primitive indicators. If end-of-primitive indicators are used
		they must be left for {@link #readIndicator} to fetch.
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
		<p>
		If this method is called for a first time to initate block read sequence
		the caller will invoked this method only
		if the same conditions are matches like for {@link #readBoolean}.
		<p>
		If this method is called for a second and next time during block read sequence
		the caller will invoke it only if 
		<ul>
			<li>there is no indicator under cursor;</li>
			<li>there is no physical eof under cursor;</li>
		</ul>
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@return number of read items or 0 if reached any kind of indicator, including type end information.
				In such case indicator is not read from stream. 
		@throws IOException if failed at low level
		@throws EBrokenStream if it is broken beyond repair.
		@throws EUnexpectedEof if physical end of stream was reached.
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
		
		
		
				Services available for subclasses
				
				
				
		
		****************************************************************/
		/* ============================================================
					Related to indicators processing.
		==============================================================*/
		/** Tests if indicator is one of <code>TYPE_xxx</code>
		@param i indicator
		@return true if it is
		*/
		public static boolean isTypeIndicator(int i)
		{
			switch(i)
			{
					case EOF_INDICATOR: 			
					case NO_INDICATOR: 				
					case BEGIN_INDICATOR: 			
					case END_INDICATOR: 			
					case END_BEGIN_INDICATOR: 		
					case DIRECT_INDICATOR: 			
					case REGISTER_INDICATOR: 		
					case REGISTER_USE_INDICATOR: 	return false;   
				
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
					case TYPE_DOUBLE_BLOCK: 	  return true;
						                        
					case FLUSH_BOOLEAN: 		
					case FLUSH_BYTE: 			
					case FLUSH_CHAR: 			
					case FLUSH_SHORT: 			
					case FLUSH_INT: 			
					case FLUSH_LONG: 			
					case FLUSH_FLOAT: 			
					case FLUSH_DOUBLE: 			
					case FLUSH_BOOLEAN_BLOCK:		
					case FLUSH_BYTE_BLOCK: 		
					case FLUSH_CHAR_BLOCK: 		
					case FLUSH_SHORT_BLOCK: 	
					case FLUSH_INT_BLOCK: 		
					case FLUSH_LONG_BLOCK: 		
					case FLUSH_FLOAT_BLOCK: 	
					case FLUSH_DOUBLE_BLOCK: 	
					case FLUSH:				 	
					case FLUSH_BLOCK: 			
					case FLUSH_ANY:	 			  return false;
						
					default: throw new AssertionError("unknown "+i);
			}
		};   
		
		/** Tests if indicator is one of <code>TYPE_xxx</code>
		for elementary primitive operations.
		@param i indicator
		@return true if it is
		*/
		public static boolean isElementaryTypeIndicator(int i)
		{
			switch(i)
			{
					case EOF_INDICATOR: 			
					case NO_INDICATOR: 				
					case BEGIN_INDICATOR: 			
					case END_INDICATOR: 			
					case END_BEGIN_INDICATOR: 		
					case DIRECT_INDICATOR: 			
					case REGISTER_INDICATOR: 		
					case REGISTER_USE_INDICATOR: 	return false;   
				
					case TYPE_BOOLEAN: 			
					case TYPE_BYTE: 			
					case TYPE_CHAR: 			
					case TYPE_SHORT: 			
					case TYPE_INT: 				
					case TYPE_LONG: 			
					case TYPE_FLOAT: 			
					case TYPE_DOUBLE: 				 return true;
					case TYPE_BOOLEAN_BLOCK: 	
					case TYPE_BYTE_BLOCK: 		
					case TYPE_CHAR_BLOCK: 		
					case TYPE_SHORT_BLOCK: 		
					case TYPE_INT_BLOCK: 		
					case TYPE_LONG_BLOCK: 		
					case TYPE_FLOAT_BLOCK: 		
					case TYPE_DOUBLE_BLOCK: 	 
						                        
					case FLUSH_BOOLEAN: 		
					case FLUSH_BYTE: 			
					case FLUSH_CHAR: 			
					case FLUSH_SHORT: 			
					case FLUSH_INT: 			
					case FLUSH_LONG: 			
					case FLUSH_FLOAT: 			
					case FLUSH_DOUBLE: 			
					case FLUSH_BOOLEAN_BLOCK:		
					case FLUSH_BYTE_BLOCK: 		
					case FLUSH_CHAR_BLOCK: 		
					case FLUSH_SHORT_BLOCK: 	
					case FLUSH_INT_BLOCK: 		
					case FLUSH_LONG_BLOCK: 		
					case FLUSH_FLOAT_BLOCK: 	
					case FLUSH_DOUBLE_BLOCK: 	
					case FLUSH:				 	
					case FLUSH_BLOCK: 			
					case FLUSH_ANY:	 			  return false;
						
					default: throw new AssertionError("unknown "+i);
			}
		}; 
		
		/** Tests if indicator is one of <code>FLUSH_xxx</code>
		for elementary primitive operations, excluding generic flushes.
		@param i indicator
		@return true if it is
		*/
		public static boolean isElementaryFlushIndicator(int i)
		{
			switch(i)
			{
					case EOF_INDICATOR: 			
					case NO_INDICATOR: 				
					case BEGIN_INDICATOR: 			
					case END_INDICATOR: 			
					case END_BEGIN_INDICATOR: 		
					case DIRECT_INDICATOR: 			
					case REGISTER_INDICATOR: 		
					case REGISTER_USE_INDICATOR: 	 
				
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
					case TYPE_DOUBLE_BLOCK: 	return false;   
						                        
					case FLUSH_BOOLEAN: 		
					case FLUSH_BYTE: 			
					case FLUSH_CHAR: 			
					case FLUSH_SHORT: 			
					case FLUSH_INT: 			
					case FLUSH_LONG: 			
					case FLUSH_FLOAT: 			
					case FLUSH_DOUBLE: 			 return true;
					case FLUSH_BOOLEAN_BLOCK:		
					case FLUSH_BYTE_BLOCK: 		
					case FLUSH_CHAR_BLOCK: 		
					case FLUSH_SHORT_BLOCK: 	
					case FLUSH_INT_BLOCK: 		
					case FLUSH_LONG_BLOCK: 		
					case FLUSH_FLOAT_BLOCK: 	
					case FLUSH_DOUBLE_BLOCK: 	
					case FLUSH:				 	
					case FLUSH_BLOCK: 			
					case FLUSH_ANY:	 			  return false;
						
					default: throw new AssertionError("unknown "+i);
			}
		}; 
		
		/** Tests if indicator is one of <code>TYPE_xxx_BLOCK</code>
		for block primitive operations.
		@param i indicator
		@return true if it is
		*/
		public static boolean isBlockTypeIndicator(int i)
		{
			switch(i)
			{
					case EOF_INDICATOR: 			
					case NO_INDICATOR: 				
					case BEGIN_INDICATOR: 			
					case END_INDICATOR: 			
					case END_BEGIN_INDICATOR: 		
					case DIRECT_INDICATOR: 			
					case REGISTER_INDICATOR: 		
					case REGISTER_USE_INDICATOR: 	return false;   
				
					case TYPE_BOOLEAN: 			
					case TYPE_BYTE: 			
					case TYPE_CHAR: 			
					case TYPE_SHORT: 			
					case TYPE_INT: 				
					case TYPE_LONG: 			
					case TYPE_FLOAT: 			
					case TYPE_DOUBLE: 				 return false;
					case TYPE_BOOLEAN_BLOCK: 	
					case TYPE_BYTE_BLOCK: 		
					case TYPE_CHAR_BLOCK: 		
					case TYPE_SHORT_BLOCK: 		
					case TYPE_INT_BLOCK: 		
					case TYPE_LONG_BLOCK: 		
					case TYPE_FLOAT_BLOCK: 		
					case TYPE_DOUBLE_BLOCK: 	 	return true;
						                        
					case FLUSH_BOOLEAN: 		
					case FLUSH_BYTE: 			
					case FLUSH_CHAR: 			
					case FLUSH_SHORT: 			
					case FLUSH_INT: 			
					case FLUSH_LONG: 			
					case FLUSH_FLOAT: 			
					case FLUSH_DOUBLE: 			
					case FLUSH_BOOLEAN_BLOCK:		
					case FLUSH_BYTE_BLOCK: 		
					case FLUSH_CHAR_BLOCK: 		
					case FLUSH_SHORT_BLOCK: 	
					case FLUSH_INT_BLOCK: 		
					case FLUSH_LONG_BLOCK: 		
					case FLUSH_FLOAT_BLOCK: 	
					case FLUSH_DOUBLE_BLOCK: 	
					case FLUSH:				 	
					case FLUSH_BLOCK: 			
					case FLUSH_ANY:	 			  return false;
						
					default: throw new AssertionError("unknown "+i);
			}
		}; 
		/** Tests if indicator is one of <code>FLUSH_xxx_BLOCK</code>
		for block primitive operations, excluding generic flushes.
		@param i indicator
		@return true if it is
		*/
		public static boolean isBlockFlushIndicator(int i)
		{
			switch(i)
			{
					case EOF_INDICATOR: 			
					case NO_INDICATOR: 				
					case BEGIN_INDICATOR: 			
					case END_INDICATOR: 			
					case END_BEGIN_INDICATOR: 		
					case DIRECT_INDICATOR: 			
					case REGISTER_INDICATOR: 		
					case REGISTER_USE_INDICATOR: 	 
				
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
						                        
					case FLUSH_BOOLEAN: 		
					case FLUSH_BYTE: 			
					case FLUSH_CHAR: 			
					case FLUSH_SHORT: 			
					case FLUSH_INT: 			
					case FLUSH_LONG: 			
					case FLUSH_FLOAT: 			
					case FLUSH_DOUBLE: 			 return false;
					case FLUSH_BOOLEAN_BLOCK:		
					case FLUSH_BYTE_BLOCK: 		
					case FLUSH_CHAR_BLOCK: 		
					case FLUSH_SHORT_BLOCK: 	
					case FLUSH_INT_BLOCK: 		
					case FLUSH_LONG_BLOCK: 		
					case FLUSH_FLOAT_BLOCK: 	
					case FLUSH_DOUBLE_BLOCK: 	 return true;
					case FLUSH:				 	
					case FLUSH_BLOCK: 			
					case FLUSH_ANY:	 			  return false;
						
					default: throw new AssertionError("unknown "+i);
			}
		}; 
		
		
		/** Converts indicator to String form, as if it would be enum.
		Yes, I know it could be enum, but I like the extensability of integers.
		@param indicator one of indicator constants.
		@return string form or "unknown(<i>indicator</i>)"
		*/
		public static String indicatorToString(int indicator)
		{
			switch(indicator)
			{
					case EOF_INDICATOR: 			return "EOF_INDICATOR";
					case NO_INDICATOR: 				return "NO_INDICATOR";
					case BEGIN_INDICATOR: 			return "BEGIN_INDICATOR";		
					case END_INDICATOR: 			return "END_INDICATOR";
					case END_BEGIN_INDICATOR: 		return "END_BEGIN_INDICATOR";
					case DIRECT_INDICATOR: 			return "DIRECT_INDICATOR";
					case REGISTER_INDICATOR: 		return "REGISTER_INDICATOR";
					case REGISTER_USE_INDICATOR: 	return "REGISTER_USE_INDICATOR";
				
					case TYPE_BOOLEAN: 			return "TYPE_BOOLEAN";	
					case TYPE_BYTE: 			return "TYPE_BYTE";
					case TYPE_CHAR: 			return "TYPE_CHAR";
					case TYPE_SHORT: 			return "TYPE_SHORT";
					case TYPE_INT: 				return "TYPE_INT";
					case TYPE_LONG: 			return "TYPE_LONG";
					case TYPE_FLOAT: 			return "TYPE_FLOAT";
					case TYPE_DOUBLE: 			return "TYPE_DOUBLE";
					case TYPE_BOOLEAN_BLOCK: 	return "TYPE_BOOLEAN_BLOCK";	
					case TYPE_BYTE_BLOCK: 		return "TYPE_BYTE_BLOCK";
					case TYPE_CHAR_BLOCK: 		return "TYPE_CHAR_BLOCK";
					case TYPE_SHORT_BLOCK: 		return "TYPE_SHORT_BLOCK";
					case TYPE_INT_BLOCK: 		return "TYPE_INT_BLOCK";
					case TYPE_LONG_BLOCK: 		return "TYPE_LONG_BLOCK";
					case TYPE_FLOAT_BLOCK: 		return "TYPE_FLOAT_BLOCK";
					case TYPE_DOUBLE_BLOCK: 	return "TYPE_DOUBLE_BLOCK";
						
					case FLUSH_BOOLEAN: 		return "FLUSH_BOOLEAN";	
					case FLUSH_BYTE: 			return "FLUSH_BYTE";
					case FLUSH_CHAR: 			return "FLUSH_CHAR";
					case FLUSH_SHORT: 			return "FLUSH_SHORT";
					case FLUSH_INT: 			return "FLUSH_INT";
					case FLUSH_LONG: 			return "FLUSH_LONG";
					case FLUSH_FLOAT: 			return "FLUSH_FLOAT";
					case FLUSH_DOUBLE: 			return "FLUSH_DOUBLE";
					case FLUSH_BOOLEAN_BLOCK:	return "FLUSH_BOOLEAN_BLOCK";	
					case FLUSH_BYTE_BLOCK: 		return "FLUSH_BYTE_BLOCK";
					case FLUSH_CHAR_BLOCK: 		return "FLUSH_CHAR_BLOCK";
					case FLUSH_SHORT_BLOCK: 	return "FLUSH_SHORT_BLOCK";
					case FLUSH_INT_BLOCK: 		return "FLUSH_INT_BLOCK";
					case FLUSH_LONG_BLOCK: 		return "FLUSH_LONG_BLOCK";
					case FLUSH_FLOAT_BLOCK: 	return "FLUSH_FLOAT_BLOCK";
					case FLUSH_DOUBLE_BLOCK: 	return "FLUSH_DOUBLE_BLOCK";
					case FLUSH:				 	return "FLUSH";
					case FLUSH_BLOCK: 			return "FLUSH_BLOCK";
					case FLUSH_ANY:	 			return "FLUSH_ANY";
						
					default: return "unknown("+indicator+")";
			}
		};
		/** Converts <code>TYPE_xxx</code> indicators to string representation
		@param type_indicator indicator to convert
		@return string form
		*/
		public static String typeIndicatorToString(int type_indicator)
		{
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
						default:					return indicatorToString( type_indicator );
				}
		};
		/* ***************************************************************
		
		
		
				Implementation and data processing.
				
				
				
		
		****************************************************************/
		/* -----------------------------------------------------------------
				State validation
		-----------------------------------------------------------------*/
		/** Throws if stream is unusable.
		@throws EBrokenStream if broken
		@throws EClosed if closed 
		@see #close
		@see #breakStream
		*/
		protected final void validateUsable()throws EBrokenStream,EClosed
		{
			if (state==STATE_BROKEN) throw new EBrokenStream("Stream is broken, cannot continue reading it.");
			if (state==STATE_CLOSED) throw new EClosed("Already closed");			
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
		
		
		/* -----------------------------------------------------------------
				Indicators processing.		
		-----------------------------------------------------------------*/
		
		/** Picks up current pending indicator and turns it to be {@link #EOF_INDICATOR}
		so that it won't linger anymore and {@link #getIndicator} will pick indicator
		from a stream.
		*/
		private void consumeIndicator()
		{
			 pending_indicator = EOF_INDICATOR;
		};
		/** Either picks currently pending indicator or reads it from 
		stream. Does not consume it, so subsequent calls do return
		the same value. To consume an indicator call {@link #consumeIndicator}.
		@return indicator
		@throws IOException if failed in readIndicator
		@throws EBrokenStream if broken
		@throws EClosed if closed.
		*/
		private int getIndicator()throws IOException
		{
			validateUsable();						
			if (pending_indicator==EOF_INDICATOR)
			{
				try{
						pending_indicator = readIndicator();						
					}catch(EBrokenStream ex){  throw breakStream(ex); };
			};
			return pending_indicator;
		};
		
		/* ---------------------------------------------------------------------------
					Primitive ops.
		---------------------------------------------------------------------------*/
		/* ........................................................................
					Commons.
		........................................................................*/
		/** Called in {@link #validatePrimitiveType} to ensure that there are primitive data to read
		so that implementation <code>readXXXImpl</code> could skip testing for it.
		@throws EUnexpectedEof if found it
		@throws IOException if failed at low level
		@throws ENoMoreData if found it
		@throws ECorruptedFormat if found it
		*/
		private void validateAreData()throws IOException, EUnexpectedEof,ENoMoreData,ECorruptedFormat
		{
			//Ensure there are data.
			int indicator;
			switch(indicator=getIndicator())
			{
				case EOF_INDICATOR: throw new EUnexpectedEof();
				case NO_INDICATOR: 
									//This indicator must not be preserved
									//since primitives reads must trigger
									//indicator fetch afterwards
									consumeIndicator();
									return;
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: throw new ENoMoreData("Signal reached");
				default:
					throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator)+" while data were expected");
			}
		};
		/** Invoked within {@link #validatePrimitiveType} during initialization
		of primitive read if stream is undescribed. Validates type indicators
		since they are prohibited.
		@throws IOException if failed at low level
		@throws EUnexpectedEof if physical end of file
		@throws ENoMoreData if signal indicator is reached
		@throws EDataTypeNotSupported if there is a type indicator
		@throws ECorruptedFormat if there is an unexpected indicator.
		*/
		private void validateUntypedPrimitive()throws IOException
		{
			assert(!isDescribed());
			int indicator = getIndicator();
			switch(indicator)
			{
				case EOF_INDICATOR: throw new EUnexpectedEof();
				case NO_INDICATOR:
								//Note: call to validateData is uncessary. We are at it.
								consumeIndicator(); //to let it move forward.			
								return;
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
				case TYPE_DOUBLE_BLOCK: 	throw new EDataTypeNotSupported("This is undescribed format, but type indicator "+
																indicatorToString(indicator)+" is found");
				default:
						throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));	
			}
		};
		/**  Invoked within {@link #validatePrimitiveType} during initialization
		of primitive read if stream is described. Validates type indicators.		
		@param expected_type_indicator one of <code>TYPE_xxxx</code> indicators descibing
			a primitive operation which is to be validated. Both
			primitive and block types are allowed.
		@throws IOException if failed, 
		@throws EUnexpectedEof if physical end of file
		@throws ENoMoreData if signal indicator is reached
		@throws EDataTypeRequired if there is no type indicator
		@throws EDataMissmatch if type does not match.
		@throws ECorruptedFormat if there is an unexpected indicator.
		*/
		private void validateTypedPrimitive(int expected_type_indicator)throws IOException
		{
			assert(isDescribed());
			assert(isTypeIndicator(expected_type_indicator));
					
			//check what indicator is under cursor?
			int indicator = getIndicator();
			if (indicator==expected_type_indicator)
			{
					//ideal type match, consume it.
					consumeIndicator();	
					validateAreData();	//must check if there are data accessible.
					return;
			};
			//Handle non-ideal matches and end/begin signals.
			switch(indicator)
			{
				case EOF_INDICATOR: throw new EUnexpectedEof();
				case NO_INDICATOR:
									throw new EDataTypeRequired("Type information was expected but nothing was found");
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
										throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));		
			}
		};
		/** Invoked by primitive reads to check if primitive read of specified type can
		be initiated. Dispatches to {@link #validateTypedPrimitive} or {@link #validateUntypedPrimitive}
		@param expected_type_indicator one of <code>TYPE_xxxx</code> indicators descibing operation 
		@throws IOException if failed, including all type and syntax validation.
		*/
		private void validatePrimitiveType(int expected_type_indicator)throws IOException
		{
			assert(isElementaryTypeIndicator(expected_type_indicator));
			if (isDescribed())
			{
				validateTypedPrimitive(expected_type_indicator);
			}else
			{
				validateUntypedPrimitive();
			};
		};
		
		/* ........................................................................
					Elementary
		........................................................................*/
		
		/** Validates if there is no <code>FLUSH_xxxx</code>
		indicator after a primitive what is an requirement for undescribed
		streams 
		@throws IOException if failed at low level
		@throws EDataTypeNotSupported if encountered <code>FLUSH_xxx</code> indicators.
		*/
		private void validateUntypedPrimitiveFlush()throws IOException
		{
			assert(!isDescribed());
			//check what indicator is under cursor?
			int indicator = getIndicator();			
			switch(indicator)
			{
				case EOF_INDICATOR: 
				case NO_INDICATOR:								
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: return; //correct, allowed indicators
				case FLUSH_BOOLEAN: 			
				case FLUSH_BYTE: 			
				case FLUSH_CHAR: 			
				case FLUSH_SHORT: 			
				case FLUSH_INT: 				
				case FLUSH_LONG: 			
				case FLUSH_FLOAT: 			        
				case FLUSH_DOUBLE: 			
				case FLUSH_BOOLEAN_BLOCK: 	
				case FLUSH_BYTE_BLOCK: 		
				case FLUSH_CHAR_BLOCK: 		
				case FLUSH_SHORT_BLOCK: 		
				case FLUSH_INT_BLOCK: 		
				case FLUSH_LONG_BLOCK: 		
				case FLUSH_FLOAT_BLOCK: 		
				case FLUSH_DOUBLE_BLOCK:
				case FLUSH:
				case FLUSH_BLOCK:
				case FLUSH_ANY:
					throw new EDataTypeNotSupported("This is undescribed format, but type indicator "+
																indicatorToString(indicator)+" is found");
				default:
						throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));
			}
		};
		
		/** Validates if there is <code>FLUSH_xxxx</code>
		indicator after a primitive, and if it is checks if it is
		a valid type what is an requirement for described streams 
		@param  expected_flush_indicator whats is expected, type specific.
				Only elementary types allowed.
		@throws IOException if failed at low level
		@throws EDataTypeNotSupported if encountered <code>FLUSH_xxx</code> indicators.
		*/
		private void validateTypedPrimitiveFlush(int expected_flush_indicator)throws IOException
		{
			assert(isDescribed());
			assert(isElementaryFlushIndicator(expected_flush_indicator));
			//check what indicator is under cursor?
			int indicator = getIndicator();		
			if (indicator==expected_flush_indicator)
			{
					consumeIndicator();	//remove it, it is allowed.
					return;
			};
			switch(indicator)
			{
				case EOF_INDICATOR: 
				case NO_INDICATOR:								
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: return; //correct, allowed, since indicator is optional.
				
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
				case TYPE_DOUBLE_BLOCK: return; //correct, since flush indicator is optional 
												//a type indicator of next element may be present.						
				case FLUSH_BOOLEAN: 			
				case FLUSH_BYTE: 			
				case FLUSH_CHAR: 			
				case FLUSH_SHORT: 			
				case FLUSH_INT: 				
				case FLUSH_LONG: 			
				case FLUSH_FLOAT: 			        
				case FLUSH_DOUBLE: 			
				case FLUSH_BOOLEAN_BLOCK: 	
				case FLUSH_BYTE_BLOCK: 		
				case FLUSH_CHAR_BLOCK: 		
				case FLUSH_SHORT_BLOCK: 		
				case FLUSH_INT_BLOCK: 		
				case FLUSH_LONG_BLOCK: 		
				case FLUSH_FLOAT_BLOCK: 		
				case FLUSH_DOUBLE_BLOCK:
					throw new EDataMissmatch("Expected "+typeIndicatorToString(expected_flush_indicator)+
												" but found type in stream is "+typeIndicatorToString(indicator));
				case FLUSH:
					//this primitive flush is allowed only for primitive flushes.
					consumeIndicator();	//remove it, it is allowed, since it is a generic flush.
					return;
				case FLUSH_BLOCK:
					//this primitive flush is allowed only for block flushes
					//but we are handling primitves only.
						throw new EDataMissmatch("Expected "+typeIndicatorToString(expected_flush_indicator)+
												" but found type in stream is "+typeIndicatorToString(indicator));
				case FLUSH_ANY:
					consumeIndicator();	//remove it, it is allowed, since it is a generic flush.
					return;
				default:
						throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));		
			}
		};
		
		/** Invoked by primitive reads to check if primitive read of specified type
		was (possibly) correctly flushed.
		Dispatches to {@link #validateTypedPrimitiveFlush} or {@link #validateUntypedPrimitiveFlush}
		@param expected_flush_indicator one of <code>FLUSH_xxxx</code> indicators describing
			elementary primitive operation 
		@throws IOException if failed, including all type and syntax validation.
		*/
		private void validatePrimitiveTypeFlush(int expected_flush_indicator)throws IOException	
		{			
			assert(isElementaryFlushIndicator(expected_flush_indicator));
			if (isDescribed())
			{
				validateTypedPrimitiveFlush(expected_flush_indicator);
			}else
			{
				validateUntypedPrimitiveFlush();
			};
		}
		
		/** Tests if elementary primitive can't be made because there is some
		block operation in progress.
		@throws IllegalStateException if block operation in progress.
		*/
		private void validateNoBlockOperationInProgress()throws IllegalStateException
		{
			if ((state>=STATE_FIRST_BLOCK)&&(state<=STATE_LAST_BLOCK)) 
					throw new IllegalStateException("Block operation in progress"); 
		};
		
		/** Starts elementary primitive read operation of specified <code>TYPE_xxx</code>
		@param type_indicator <code>TYPE_xxx</code> constant
		@throws IOException if anything failed, including syntax and type check.
		@throws ENoMoreData if stream cursor is at the signal
		@throws IllegalStateException if not allowed due to block operations.
		*/
		private void startElementaryPrimitive(int type_indicator)throws IOException, IllegalStateException
		{
				//check stream broken status
				validateUsable();							
				validateNoBlockOperationInProgress();
				validatePrimitiveType(type_indicator);
		};
		/** Ends elementary primitive read operation of specified <code>TYPE_xxx</code>
		@param expected_flush_indicator <code>FLUSH_xxx</code> constant
		@throws IOException if anything failed, including syntax check.
		@throws ENoMoreData if stream cursor is at the signal
		*/
		private void endElementaryPrimitive(int expected_flush_indicator)throws IOException
		{
				validatePrimitiveTypeFlush(expected_flush_indicator);
		};
		/*.........................................................................
					block
		..........................................................................*/
		
		
		/** Invoked by primitive reads to check if primitive read of specified type can
		be initiated. Dispatches to {@link #validateTypedPrimitive} or {@link #validateUntypedPrimitive}
		@param expected_type_indicator one of <code>TYPE_xxxx_BLOCK</code> indicators descibing operation 
		@throws IOException if failed, including all type and syntax validation.
		*/
		private void validatePrimitiveBlockType(int expected_type_indicator)throws IOException
		{
			assert(isBlockTypeIndicator(expected_type_indicator));
			//Same behavior as for elementary primitives.
			if (isDescribed())
			{
				validateTypedPrimitive(expected_type_indicator);
			}else
			{
				validateUntypedPrimitive();
			};
		};
		/** Invoked by primitive reads to check if primitive read of specified type can
		be initiated. Calls {@link #validatePrimitiveBlockType}.
		@param expected_type_indicator one of <code>TYPE_xxxx_BLOCK</code> indicators descibing operation 
		@throws IOException if failed, including all type and syntax validation.
		*/
		private void startPrimitiveBlockType(int expected_type_indicator)throws IOException
		{
			///validateUsable(); <- not necessary, callers are always testing it prior to calling this method.
			if (current_depth==0) throw new IllegalStateException("Cannot initiate block read when there is no event active");
			validatePrimitiveBlockType(expected_type_indicator);
		};
		
		
		/** Called by {@link continuePrimitiveBlock} to work for it.
		@return false if block should not be continued.
		@throws IOException if failed, including incorrect state and indicators.
		*/
		private boolean continueUntypePrimitiveBlock()throws IOException
		{
			assert(!isDescribed());
			//check what indicator is under cursor?
			int indicator = getIndicator();			
			switch(indicator)
			{
				case EOF_INDICATOR:  	  throw new EUnexpectedEof();
				case NO_INDICATOR:		 
									consumeIndicator(); //must be consumed, so next pool will ask stream.
									return true;	//some data
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: return false; //correct, allowed indicators, but no more data.
				case FLUSH_BOOLEAN: 			
				case FLUSH_BYTE: 			
				case FLUSH_CHAR: 			
				case FLUSH_SHORT: 			
				case FLUSH_INT: 				
				case FLUSH_LONG: 			
				case FLUSH_FLOAT: 			        
				case FLUSH_DOUBLE: 			
				case FLUSH_BOOLEAN_BLOCK: 	
				case FLUSH_BYTE_BLOCK: 		
				case FLUSH_CHAR_BLOCK: 		
				case FLUSH_SHORT_BLOCK: 		
				case FLUSH_INT_BLOCK: 		
				case FLUSH_LONG_BLOCK: 		
				case FLUSH_FLOAT_BLOCK: 		
				case FLUSH_DOUBLE_BLOCK:
				case FLUSH:
				case FLUSH_BLOCK:
				case FLUSH_ANY:
					throw new EDataTypeNotSupported("This is undescribed format, but type indicator "+
																indicatorToString(indicator)+" is found");
				default:
						throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));	
			}
		};
		/** Called by {@link continuePrimitiveBlock} to work for it.
		@param  expected_flush_indicator what is expected, type specific.
		Only block types allowed.
		@return false if block should not be continued.	
		@throws IOException if failed, including incorrect state and indicators.
		*/
		private boolean continueTypedPrimitiveBlock(int expected_flush_indicator)throws IOException
		{
			assert(isDescribed());
			assert(isBlockFlushIndicator(expected_flush_indicator));
			//check what indicator is under cursor?
			int indicator = getIndicator();		
			if (indicator==expected_flush_indicator)
			{
					consumeIndicator();	//remove it, it is allowed.
					return false;
			};
			switch(indicator)
			{
				case EOF_INDICATOR:		throw new EUnexpectedEof();
				case NO_INDICATOR:		
										consumeIndicator(); //must be consumed, so next pool will ask stream.
										return true;				
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: return false; //correct, allowed, since indicator is optional.
				
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
				case TYPE_DOUBLE_BLOCK: return false;
										//correct, since flush indicator is optional 
										//a type indicator of next element may be present.						
				case FLUSH_BOOLEAN: 			
				case FLUSH_BYTE: 			
				case FLUSH_CHAR: 			
				case FLUSH_SHORT: 			
				case FLUSH_INT: 				
				case FLUSH_LONG: 			
				case FLUSH_FLOAT: 			        
				case FLUSH_DOUBLE: 	//invalid for any block type.
					throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));
				case FLUSH_BOOLEAN_BLOCK: 	
				case FLUSH_BYTE_BLOCK: 		
				case FLUSH_CHAR_BLOCK: 		
				case FLUSH_SHORT_BLOCK: 		
				case FLUSH_INT_BLOCK: 		
				case FLUSH_LONG_BLOCK: 		
				case FLUSH_FLOAT_BLOCK: 		
				case FLUSH_DOUBLE_BLOCK:	//invalid for type					
				case FLUSH:
					//this primitive flush is allowed only for primitive flushes.
					//but we work only for blocks
					throw new EDataMissmatch("Expected "+typeIndicatorToString(expected_flush_indicator)+
												" but found type in stream is "+typeIndicatorToString(indicator));
				case FLUSH_BLOCK:
				case FLUSH_ANY:
					consumeIndicator();	//remove it, it is allowed.
					return false;
				default:
						throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));		
			}
		};
		
		/** Depending on {@link #isDescribed} dispatches to
		{@link #continueUntypePrimitiveBlock} or {@link #continueTypedPrimitiveBlock}
		to check if there is an indicator which should prevent calls to 
		<code>readXXXBlockImpl</code> methods
		
		@param expected_flush_indicator one of <code>FLUSH_xxxx_BLOCK</code> indicators
				which might possibly terminate block.
		@return true if can continue and pass read request to lower level, false if it
				should not do it.
		@throws IOException if failed, including type and syntax validation.
		*/
		private boolean continuePrimitiveBlock(int expected_flush_indicator)throws IOException
		{
			assert(isBlockFlushIndicator(expected_flush_indicator));
			if (isDescribed())
			{
				return continueTypedPrimitiveBlock(expected_flush_indicator);
			}else
			{
				return continueUntypePrimitiveBlock();
			}
		};
		
		
		
		
		
		/** Validates if there is no <code>FLUSH_xxxx_BLOCK</code>
		indicator after a primitive what is an requirement for undescribed
		streams and if there is no data, since last block read returned partial read.
		@throws IOException if failed at low level
		@throws EDataTypeNotSupported if encountered <code>FLUSH_xxx_BLOCK</code> indicators.
		@throws AssertionError if there are some data left.
		*/
		private void validateUntypedBlockFlush()throws IOException
		{
			assert(!isDescribed());
			//check what indicator is under cursor?
			int indicator = getIndicator();			
			switch(indicator)
			{
				case EOF_INDICATOR: 	return; 
				case NO_INDICATOR:		throw new AssertionError("readXXXBlockImpl() returned partial read, but there are data");			
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: return; //correct, allowed indicators
				case FLUSH_BOOLEAN: 			
				case FLUSH_BYTE: 			
				case FLUSH_CHAR: 			
				case FLUSH_SHORT: 			
				case FLUSH_INT: 				
				case FLUSH_LONG: 			
				case FLUSH_FLOAT: 			        
				case FLUSH_DOUBLE: 			
				case FLUSH_BOOLEAN_BLOCK: 	
				case FLUSH_BYTE_BLOCK: 		
				case FLUSH_CHAR_BLOCK: 		
				case FLUSH_SHORT_BLOCK: 		
				case FLUSH_INT_BLOCK: 		
				case FLUSH_LONG_BLOCK: 		
				case FLUSH_FLOAT_BLOCK: 		
				case FLUSH_DOUBLE_BLOCK:
				case FLUSH:
				case FLUSH_BLOCK:
				case FLUSH_ANY:
					throw new EDataTypeNotSupported("This is undescribed format, but type indicator "+
																indicatorToString(indicator)+" is found");
				default:
						throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));		
			}
		};
		
		/** Validates if there is no <code>FLUSH_xxxx_BLOCK</code>
		indicator after a primitive what is an requirement for undescribed
		streams and if there is no data, since last block read returned partial read.
		@param  expected_flush_indicator whats is expected, type specific.
				Only elementary types allowed.
		@throws IOException if failed at low level
		@throws EDataTypeNotSupported if encountered <code>FLUSH_xxx_BLOCK</code> indicators.
		*/
		private void validateTypedBlockFlush(int expected_flush_indicator)throws IOException
		{
			assert(isDescribed());
			assert(isBlockFlushIndicator(expected_flush_indicator));
			//check what indicator is under cursor?
			int indicator = getIndicator();		
			if (indicator==expected_flush_indicator)
			{
					consumeIndicator();	//remove it, it is allowed.
					return;
			};
			switch(indicator)
			{
				case EOF_INDICATOR: 	return;
				case NO_INDICATOR:		throw new AssertionError("readXXXBlockImpl() returned partial read, but there are data");								
				case BEGIN_INDICATOR:
				case END_INDICATOR:
				case END_BEGIN_INDICATOR: return; //correct, allowed, since indicator is optional.
				
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
				case TYPE_DOUBLE_BLOCK: return; //correct, since flush indicator is optional 
												//a type indicator of next element may be present.						
				case FLUSH_BOOLEAN: 			
				case FLUSH_BYTE: 			
				case FLUSH_CHAR: 			
				case FLUSH_SHORT: 			
				case FLUSH_INT: 				
				case FLUSH_LONG: 			
				case FLUSH_FLOAT: 			        
				case FLUSH_DOUBLE: 			
					throw new EDataMissmatch("Expected "+typeIndicatorToString(expected_flush_indicator)+
												" but found type in stream is "+typeIndicatorToString(indicator));
				case FLUSH_BOOLEAN_BLOCK: 	
				case FLUSH_BYTE_BLOCK: 		
				case FLUSH_CHAR_BLOCK: 		
				case FLUSH_SHORT_BLOCK: 		
				case FLUSH_INT_BLOCK: 		
				case FLUSH_LONG_BLOCK: 		
				case FLUSH_FLOAT_BLOCK: 		
				case FLUSH_DOUBLE_BLOCK:
					consumeIndicator();	//remove it, it is allowed, since it is a generic flush.
					return;
				case FLUSH:
					//this primitive flush is allowed only for primitive flushes.
					throw new EDataMissmatch("Expected "+typeIndicatorToString(expected_flush_indicator)+
												" but found type in stream is "+typeIndicatorToString(indicator));
				case FLUSH_BLOCK:				
					//this primitive flush is allowed only for block flushes										
				case FLUSH_ANY:
					consumeIndicator();	//remove it, it is allowed, since it is a generic flush.
					return;
				default:
						throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));		
			}
		};
		
		/**  Invoked when block read returned partial read, validates
		if proper terminating signals are present or not.
		Dispatches to {@link #validateTypedPrimitiveFlush} or {@link #validateUntypedPrimitiveFlush}
		@param expected_flush_indicator one of <code>FLUSH_xxxx</code> indicators describing
			block primitive operation 
		@throws IOException if failed, including all type and syntax validation.
		*/
		private void validateBlockFlush(int expected_flush_indicator)throws IOException	
		{			
			assert(isBlockFlushIndicator(expected_flush_indicator));
			if (isDescribed())
			{
				validateTypedBlockFlush(expected_flush_indicator);
			}else
			{
				validateUntypedBlockFlush();
			};
		}
		
		
		
		
		
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
				validateUsable();
				try{					
					final int indicator = getIndicator();
					consumeIndicator(); //consume pending indicator, so that if any operation fails we will move to next indicator.
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
											throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(begin_style_indicator));
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
									skipData();
					 }
				  }catch(EBrokenStream ex){ throw breakStream(ex); };
			}
		};
		
		@Override public int whatNext()throws IOException
		{
				final int indicator = getIndicator();
				switch(indicator)
				{
						case EOF_INDICATOR: 		return ISignalReadFormat.EOF;							
						case NO_INDICATOR:		
								//Note: No indicator is returned by un-described streams
								//inside a data stream and for both types
								//of streams inside a block, so we need to continue according to
								//currently processing type.
								if ((state>=STATE_FIRST_BLOCK)&&(state<=STATE_LAST_BLOCK))
								{
									switch(state)
									{
										case STATE_BOOLEAN_BLOCK: return PRMTV_BOOLEAN_BLOCK;
										case STATE_BYTE_BLOCK: return PRMTV_BYTE_BLOCK;
										case STATE_SHORT_BLOCK: return PRMTV_SHORT_BLOCK;
										case STATE_CHAR_BLOCK: return PRMTV_CHAR_BLOCK;
										case STATE_INT_BLOCK: return PRMTV_INT_BLOCK;
										case STATE_LONG_BLOCK: return PRMTV_LONG_BLOCK;
										case STATE_FLOAT_BLOCK: return PRMTV_FLOAT_BLOCK;
										case STATE_DOUBLE_BLOCK: return PRMTV_DOUBLE_BLOCK;
										default: throw new AssertionError("invalid state:"+state);
									}
								}else
								{
									//not in block operation.
									if (isDescribed()) 
										throw new EDataTypeRequired("Described fromat, but NO_INDICATOR is found in stream while type is expected.");
									return PRMTV_UNTYPED;	//<-- as generic, non typed indicator.
								}
						case BEGIN_INDICATOR:		//fallthrough
						case END_INDICATOR:			//fallthrough
						case END_BEGIN_INDICATOR: 	return SIGNAL;
						case TYPE_BOOLEAN: 			return PRMTV_BOOLEAN;
						case TYPE_BYTE: 			return PRMTV_BYTE;
						case TYPE_CHAR: 			return PRMTV_CHAR;
						case TYPE_SHORT: 			return PRMTV_SHORT;
						case TYPE_INT: 				return PRMTV_INT;
						case TYPE_LONG: 			return PRMTV_LONG;
						case TYPE_FLOAT: 			return PRMTV_FLOAT;
						case TYPE_DOUBLE: 			return PRMTV_DOUBLE;
						case TYPE_BOOLEAN_BLOCK: 	return PRMTV_BOOLEAN_BLOCK;
						case TYPE_BYTE_BLOCK: 		return PRMTV_BYTE_BLOCK;
						case TYPE_CHAR_BLOCK: 		return PRMTV_CHAR_BLOCK;
						case TYPE_SHORT_BLOCK: 		return PRMTV_SHORT_BLOCK;
						case TYPE_INT_BLOCK: 		return PRMTV_INT_BLOCK;
						case TYPE_LONG_BLOCK: 		return PRMTV_LONG_BLOCK;
						case TYPE_FLOAT_BLOCK: 		return PRMTV_FLOAT_BLOCK;
						case TYPE_DOUBLE_BLOCK: 	return PRMTV_DOUBLE_BLOCK;
						default:					
								//All remaining are not allowed in this place.
								throw new ECorruptedFormat("Unexpected indicator "+indicatorToString(indicator));
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
					endElementaryPrimitive(FLUSH_BOOLEAN);
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
					endElementaryPrimitive(FLUSH_BYTE);
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
					endElementaryPrimitive(FLUSH_CHAR);
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
					endElementaryPrimitive(FLUSH_SHORT);
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
					endElementaryPrimitive(FLUSH_INT);
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
					endElementaryPrimitive(FLUSH_LONG);
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
					endElementaryPrimitive(FLUSH_FLOAT);
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
					endElementaryPrimitive(FLUSH_DOUBLE);
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
			validateUsable();			
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						validatePrimitiveBlockType(TYPE_BOOLEAN_BLOCK);
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
			if (continuePrimitiveBlock(FLUSH_BOOLEAN_BLOCK))
			{
				try{
					final int r= readBooleanBlockImpl(buffer,offset,length);
					if (r<length) validateBlockFlush(FLUSH_BOOLEAN_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readByteBlock(byte [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateUsable();			
			//check state and type
			switch(state)
			{
				case STATE_PRIMITIVE:
						//We can initiate block operation?
						validatePrimitiveBlockType(TYPE_BYTE_BLOCK);
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
			if (continuePrimitiveBlock(FLUSH_BYTE_BLOCK))
			{
				try{
					final int r= readByteBlockImpl(buffer,offset,length);
					if (r<length) validateBlockFlush(FLUSH_BYTE_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readByteBlock()throws IOException
		{
			//check boundary conditions
			validateUsable();			
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
			if (continuePrimitiveBlock(FLUSH_BYTE_BLOCK))
			{
				try{
					final int r= readByteBlockImpl();
					assert(r>=-1);
					assert(r<=255);
					if (r==-1)  validateBlockFlush(FLUSH_BYTE_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return -1;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readCharBlock(char [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateUsable();			
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
			if (continuePrimitiveBlock(FLUSH_CHAR_BLOCK))
			{
				try{
					final int r= readCharBlockImpl(buffer,offset,length);
					if (r<length) validateBlockFlush(FLUSH_CHAR_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readCharBlock(Appendable buffer, int length)throws IOException
		{
			//check boundary conditions
			validateUsable();			
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
			if (continuePrimitiveBlock(FLUSH_CHAR_BLOCK))
			{
				try{
					final int r= readCharBlockImpl(buffer,length);
					if (r<length) validateBlockFlush(FLUSH_CHAR_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readShortBlock(short [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateUsable();			
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
			if (continuePrimitiveBlock(FLUSH_SHORT_BLOCK))
			{
				try{
					final int r= readShortBlockImpl(buffer,offset,length);
					if (r<length) validateBlockFlush(FLUSH_SHORT_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readIntBlock(int [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateUsable();			
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
			if (continuePrimitiveBlock(FLUSH_INT_BLOCK))
			{
				try{
					final int r= readIntBlockImpl(buffer,offset,length);
					if (r<length) validateBlockFlush(FLUSH_INT_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readLongBlock(long [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateUsable();			
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
			if (continuePrimitiveBlock(FLUSH_LONG_BLOCK))
			{
				try{
					final int r= readLongBlockImpl(buffer,offset,length);
					if (r<length) validateBlockFlush(FLUSH_LONG_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readFloatBlock(float [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateUsable();			
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
			if (continuePrimitiveBlock(FLUSH_FLOAT_BLOCK))
			{
				try{
					final int r= readFloatBlockImpl(buffer,offset,length);
					if (r<length) validateBlockFlush(FLUSH_FLOAT_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/** See {@link #readBooleanBlock}
		*/
		@Override public final int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{
			//check boundary conditions
			validateUsable();			
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
			if (continuePrimitiveBlock(FLUSH_DOUBLE_BLOCK))
			{
				try{
					final int r= readDoubleBlockImpl(buffer,offset,length);
					if (r<length) validateBlockFlush(FLUSH_DOUBLE_BLOCK);
					return r;
				}catch(EBrokenStream ex){ throw breakStream(ex); } 
			}else
				return 0;
		}
		/* =================================================================
		
				Status
		
		=================================================================*/
		/** Overriden to toggle state to closed.
		Calls {@link #closeImpl}
		*/
		@Override public final void close()throws IOException
		{ 
			if (state!=STATE_CLOSED)
			{
				state=STATE_CLOSED;
				closeImpl();
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
				private static final class DUT extends ASignalReadFormat
				{
							/** Allows to inject indicator for testing */
							int next_indicator_to_return;
							/** Counts calls to {@link #readIndicator} */
							int count;
							private final boolean is_descibed;
							
					DUT(int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth,
									 boolean is_descibed)
					{
						super(names_registry_size,max_name_length,max_events_recursion_depth);
						this.is_descibed=is_descibed;
					};
					public final boolean isDescribed(){ return is_descibed; };
					protected int readIndicator()
					{
						System.out.println("readIndicator()="+next_indicator_to_return);
						count++;
						return next_indicator_to_return;
					};
					@Override protected void skipData()throws IOException,EUnexpectedEof{ throw new AbstractMethodError(); };
					@Override protected void readSignalNameData(Appendable a, int limit)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readRegisterIndex()throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readRegisterUse()throws IOException{ throw new AbstractMethodError(); };
					@Override protected void closeImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected boolean readBooleanImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected byte readByteImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected char readCharImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected short readShortImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readIntImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected long readLongImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected float readFloatImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected double readDoubleImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readByteBlockImpl()throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readCharBlockImpl(Appendable buffer, int length)throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					@Override protected int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };;
					
				}
				
				@org.junit.Test public void test_get_consumeIndicator()throws IOException
				{
					enter();
					/*
						In this test we check, if getIndicator() do fetch 
						indicator and if consuming works.
					*/
					DUT D = new DUT(5,10,10,true);
					ASignalReadFormat A = D;					
					{
						D.next_indicator_to_return = TYPE_BOOLEAN;
						int i = A.getIndicator();
						org.junit.Assert.assertTrue(D.count==1);
						org.junit.Assert.assertTrue(i==TYPE_BOOLEAN);
						D.next_indicator_to_return = TYPE_INT;
						i  = A.getIndicator();						
						i  = A.getIndicator();
						org.junit.Assert.assertTrue(D.count==1);
						org.junit.Assert.assertTrue(i==TYPE_BOOLEAN);
					};
					{
						A.consumeIndicator();
						D.next_indicator_to_return = TYPE_INT;
						int i = A.getIndicator();
						org.junit.Assert.assertTrue(D.count==2);
						org.junit.Assert.assertTrue(i==TYPE_INT);
					};
					leave();
				};
				
				@org.junit.Test public void test_get_consumeIndicator2()throws IOException
				{
					enter();
					/*
						In this test we check, if getIndicator() do fetch 
						indicator and if consuming works if EOF_INDICATOR
						is returned.
					*/
					DUT D = new DUT(5,10,10,true);
					ASignalReadFormat A = D;					
					{
						D.next_indicator_to_return = EOF_INDICATOR;
						int i = A.getIndicator();
						org.junit.Assert.assertTrue(D.count==1);
						org.junit.Assert.assertTrue(i==EOF_INDICATOR);
							i = A.getIndicator();
						org.junit.Assert.assertTrue(D.count==2);
						org.junit.Assert.assertTrue(i==EOF_INDICATOR);
							i = A.getIndicator();
						org.junit.Assert.assertTrue(D.count==3);
						org.junit.Assert.assertTrue(i==EOF_INDICATOR);
					};
					leave();
				};
				
		};
};