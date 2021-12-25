package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;
/**
	Defines contract for abstract event based format support 
	as specified in package	description, read end of a format.
	
	<h1>Notes</h1>
	All notes for {@link ISignalWriteFormat} are binding.
*/
public interface ISignalReadFormat extends Closeable, IPrimitiveReadFormat
{	
	/* ************************************************************
	
			Information
	
	* *************************************************************/
		/** Allows to set limit for signal name. Default
		value is 1024. 
		@param characters name limit, non-negative. Zero is silly,
		no signal names can be read 
		@throws AssertionError if characters exceeds {@link #getMaxSupportedSignalNameLength} 
		*/
		public void setMaxSignalNameLength(int characters);
		/** Returns value set in {@link #setMaxSignalNameLength}
		@return value set in {@link #setMaxSignalNameLength} */
		public int getMaxSignalNameLength();
		/** Returns maximum supported signal name length by this format.
		This limit is non-adjustable and relates to physcial boundaries
		of format.
		@return length limit, non-zero positive */
		public int getMaxSupportedSignalNameLength();
		
		/** True if stream implementation is "described".
		A described implementation <u>do require</u> type information
		and <u>do validate</u> type information. They must throw
		{@link EDataMissmatch} if type validation failed and 
		must throw {@link EDataTypeRequired} if there is no required type information.
		<p>
		A "non-described" implementation <u>must not</u> provide type information,
		<u>must not</u> validate it and <u>should complain</u> if stream do contain
		type information by throwing {@link ECorruptedFormat}.
		@return true if described. A life time constant.		
		*/
		public boolean isDescribed();
	/* ************************************************************
	
			Signals and events	
	
	* *************************************************************/
		/** <a name="NEXT"></a>
		Skips all remaning primitve data, moves to next
		signal, reads it and returns. The stream cursor is
		placed right after the signal.
		<p>
		Implementations must directly defend against
		<a href="doc-files/security.html#OOMEVENT_NAME">"out-of-memory"</a> attacks
		by validating the length of a name of signal prior to
		loading it to memory.
		
		@return null if read "end" signal from a stream, non-null
			string if read "begin" signal. Each instance of 
			"begin" signal is allowed to return different instance
			of a <code>String</code>, even tough it is carrying an identical text.
		@throws IOException if low level i/o failed
		@throws EUnexpectedEof if encountered an end-of-stream.
		@throws ECorruptedFormat if could not decode end signal due to other errors.
		@throws EFormatBoundaryExceeded if name found in stream is too long.
		See {@link ISignalWriteFormat#setMaxSignalNameLength}.
		@throws EFormatBoundaryExceeded if events recursion depth control is enabled
		and this limit is exceeded.
		See <a href="doc-files/security.html#STACK_OVERFLOW_ATTACK">"stack overflow"</a> attack.
		@see #whatNext
		*/
		public String next()throws IOException;
		
		
		/** Skips all remaining primitives and all nested events until
		it will read end signal for current event.
		<p>
		This method is to be used when You have read a "begin" signal,
		figured out that You are not interested at all in what is in it,
		regardless how many prmitives and events is inside
		and You need to move to after the end of it.
		
		@throws IOException if low level i/o failed or an appropriate
		subclass to represent encountered problem.
		*/ 
		public default void skip()throws IOException
		{
			int depth=1;
			String s;
			do{
				s=next();
				if (s!=null)
				{
					depth++;
				}else
				{
					depth--;
				};
			}while(depth!=0);
		};
		
		
		
				/** Indicates that {@link #next} would not skip anything
				and would return a signal.
				<p>
				This constant is cast in stone zero.*/
				public static final int SIGNAL=0;
				/** Indicates that {@link #next} would have thrown {@link EUnexpectedEof}
				if called right now. This condition may change if stream is
				a network connection or other produced-on-demand stream. This condition
				<u>must</u> appear if no data about next signal is present in stream, but
				is not expected to detect the possibility of an EOF inside a begin
				signal itself, ie. when reading a name of a signal.
				<p>
				This constant is cast in stone -1.
				*/
				public static final int EOF=-1;				
				/** Indicates that next element in stream is an elementary primitive
				which should be processed by {@link #readBoolean} */
				public static final int PRMTV_BOOLEAN=1;
				/** Indicates that next element in stream is an elementary primitive
				which should be processed by {@link #readByte} */
				public static final int PRMTV_BYTE=2;
				/** Indicates that next element in stream is an elementary primitive
				which should be processed by {@link #readChar} */
				public static final int PRMTV_CHAR=3;
				/** Indicates that next element in stream is an elementary primitive
				which should be processed by {@link #readShort} */
				public static final int PRMTV_SHORT=4;
				/** Indicates that next element in stream is an elementary primitive
				which should be processed by {@link #readInt} */
				public static final int PRMTV_INT=5;
				/** Indicates that next element in stream is an elementary primitive
				which should be processed by {@link #readLong} */
				public static final int PRMTV_LONG=6;
				/** Indicates that next element in stream is an elementary primitive
				which should be processed by {@link #readFloat} */
				public static final int PRMTV_FLOAT=7;
				/** Indicates that next element in stream is an elementary primitive
				which should be processed by {@link #readDouble} */
				public static final int PRMTV_DOUBLE=8;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readBooleanBlock}.*/
				public static final int PRMTV_BOOLEAN_BLOCK=9;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readByteBlock}. */
				public static final int PRMTV_BYTE_BLOCK=10;				
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readCharBlock}. */
				public static final int PRMTV_CHAR_BLOCK=11;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readShortBlock}. */
				public static final int PRMTV_SHORT_BLOCK=12;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readIntBlock}. */
				public static final int PRMTV_INT_BLOCK=13;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readLongBlock}.*/
				public static final int PRMTV_LONG_BLOCK=14;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readFloatBlock}.*/
				public static final int PRMTV_FLOAT_BLOCK=15;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readDoubleBlock}.*/
				public static final int PRMTV_DOUBLE_BLOCK=16;
				/** Indicates stread carries no type information about what
				is under cursor, but surely this is not a signal.
				<p>
				Note: All PRMTV_xxx constants are &gt;0*/
				public static final int PRMTV_UNTYPED=0xFFFF;
		/** 
			Tests what kind of information is under a cursor and what methods
			are safe to call.
			<p>
			Subsequent calls to this method must return the same value and must neither 
			affect the stream nor move stream cursor. They may however cause
			some data to be read from a low level stream if necessary.
		@return one of:
				<ul>
					<li>{@link #EOF} (this constant is -1)</li>
					<li>{@link #SIGNAL} (this constant is zero);</li>
					<li>if stream is {@link #isDescribed}:
							<ul>
								<li>{@link #PRMTV_BOOLEAN};</li>
								<li>{@link #PRMTV_BYTE};</li>
								<li>{@link #PRMTV_CHAR};</li>
								<li>{@link #PRMTV_SHORT};</li>
								<li>{@link #PRMTV_INT};</li>
								<li>{@link #PRMTV_LONG};</li>
								<li>{@link #PRMTV_FLOAT};</li>
								<li>{@link #PRMTV_DOUBLE};</li>
								<li>{@link #PRMTV_BYTE_BLOCK};</li>
								<li>{@link #PRMTV_BOOLEAN_BLOCK};</li>
								<li>{@link #PRMTV_CHAR_BLOCK};</li>
								<li>{@link #PRMTV_SHORT_BLOCK};</li>
								<li>{@link #PRMTV_INT_BLOCK};</li>
								<li>{@link #PRMTV_LONG_BLOCK};</li>
								<li>{@link #PRMTV_FLOAT_BLOCK};</li>
								<li>{@link #PRMTV_DOUBLE_BLOCK};</li>
							</ul>
							Note:All above constants are &gt;0.
					<li>
					<li>if stream is not {@link #isDescribed}:
							<ul>
								<li>{@link #PRMTV_UNTYPED};</li>
							</ul>
							Note:Above constant is &gt;0.
					</li>
				</ul>
				Specifically if if block operation is in progress this operation 
				is expected to return <code>PRMTV_UNTYPED</code>(undescribed)
				or <code>PRMTV_XXX_BLOCK</code>.. 
				<p>
				If the partial block read <u>was returned</u> or the entire
				data block was consumed this method	should return what is
				next in a stream after a block.	Since block is terminated 
				with "end signal" only {@link #SIGNAL} or {@link #EOF}
				are allowed.
				
		@throws IOException if low level i/o failed, except of end-of-stream condition in allowed
							places which is indicated by a dedicated return value.
		@throws EUnexpectedEof when end of stream is not expected, ie. within data block
							and etc.
		@throws ECorruptedFormat if could not decode content due to other errors.
		@see #isDescribed
		*/
		public int whatNext()throws IOException;
		
		
	
	/* *************************************************************
	
			IPrimitiveReadFormat	
	
	* *************************************************************/
		
		/** 
		@throws IllegalStateException if this method is called when any block operation
				is in progress.
		@throws IOException if low level i/o failed
		@throws ENoMoreData if stream cursor is at the signal and there is no
				data to initiate operation.
		@throws EUnexpectedEof if read resulted in end-of-file condition
		@throws EDataMissmatch if format is {@link #isDescribed} and the type information
				describing this primitive found in stream is not matching the type of
				requested read.
		@throws EDataTypeRequired if format is {@link #isDescribed} but there is no type
				information.
		*/
		public boolean readBoolean()throws IOException;
		
		/*=============================================================
		
			Primitive blocks
			
		=============================================================*/	
		/* -----------------------------------------------------------
			Primary
		-----------------------------------------------------------*/
		/** 
		
		An initial read block read may happen in any place <a href="package.html#event">event</a>,
		including  after any <i>elementary</i> primitive read.
		<br>
		A block read may be followed <u>only</u> by other block read of the same type
		or by reading of an "end" or "begin" signal with {@link #next}/{@link #skip}
		<br>
		@param buffer buffer for read data, non-null.
		@param offset where to save first data element in <code>buffer</code>
		@param length number of bytes to read
		@return number of bytes read, which can be less that <code>length</code> (including zero)
					only if signal was reached. Once the partial read is returned this method
					must always return zero;
				
		@throws IllegalStateException if call is made outside of an event.
		@throws IllegalStateException if there is block operation of another type in progress.  
		@throws IOException if failed at low level.
		@throws ENoMoreData if it was a first read in block reading sequence and it could not
						start because cursor is already at signal. Notice this exception is not
						thrown if reading reaches a signal. In such case a partial read is returned.
		@throws EUnexpectedEof if physical end of stream was reached before the "end signal" was 
						reached.
		@throws EDataMissmatch if this is an initial read, format is {@link #isDescribed} and the type
				 information found in stream is not matching the type of this request.		
		@throws EDataTypeRequired  if this is an initial read,
		 		format is {@link #isDescribed} but there is no type	information.
		*/
		public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;
	
		/* ***********************************************************
		
			Status, Closable
			
		************************************************************/
		/**
		This method prepares format and makes it usable.
		<p>
		This method depending on state should:
		<ul>
			<li>if format is already open, don't do anything;</li>
			<li>if format is not open, reads necessary opening sequence if any and validates it;</li>
			<li>if format is closed, thorw.</li>
		</ul>		 	
		<p>
		Until format is open all methods except <code>close</code>
		should throw IOException. Throwing {@link ENotOpen} is recommended.
		@throws IOException if failed.
		*/ 
		public void open()throws IOException;
		/**
		This method closes format and makes it unusable.
		<p>
		This method depending on state should:
		<ul>
			<li>if format is already closed, don't do anything;</li>
			<li>if format is not open, close low level resources without doing anything;</li>
			<li>if format is open close low level resources.
			<p>
			<i>Note:there is specifically NO request to read and validate closing sequence
			because premature closing is a normal condition.</i>
			</li>
		</ul>		 	
		<p>
		Once format is closed all methods except <code>close</code>
		should throw IOException. Throwing {@link EClosed} is recommended.
		*/
		public void close()throws IOException;	
};