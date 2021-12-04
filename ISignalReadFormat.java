package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;
/**
	Defines contract for abstract event based format support 
	as specified in package	description, read end of a format.
	
	<h1>Notes</h1>
	All notes for {@link ISignalWriteFormat} are binding.
*/
public interface ISignalReadFormat extends Closeable
{	
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
		See {@link ISignalWriteFormat#getMaxSignalNameLength}.
		@throws EFormatBoundaryExceeded if events recursion depth control is enabled
		and this limit is exceeded.
		See <a href="doc-files/security.html#STACK_OVERFLOW_ATTACK">"stack overflow"</a> attack.
		*/
		public String next()throws IOException;
		
				/** See {@link #hasData} */
				public static final int SIGNAL=0;
				/** See {@link #hasData} */
				public static final int EOF=-1;
		/** 
			Tests what kind of information is under a cursor.
			<p>
			Subsequent calls to this method must return the same value and must not 
			affect the stream or move stream cursor. They may however cause
			some data to be read from a low level stream.
		@return <ul>
					<li>anything &gt;0	if there are some un-read data 
					and {@link #next} would skip something.
					<p>
					See also {@link IDescribedSignalReadFormat#hasData};</li>
					
					<li>{@link #SIGNAL} if {@link #next} would not have to skip
					anything and cursor it is at the next signal, either;</li>
					
					<li>{@link #EOF}  if {@link #next} would have thrown {@link EUnexpectedEof}
					if called right now. This condition may change if stream is
					a network connection or other produced-on-demand stream. This condition
					<u>must</u> appear if no data about next signal is present in stream, but
					is not expected to detect the possibility of an EOF inside a begin
					signal itself, ie. when reading a name of a signal;</li>
				</ul>
		@throws IOException if low level i/o failed, except of end-of-stream condition
							which is indicated by a dedicated return value.
		@throws ECorruptedFormat if could not decode content due to other errors.
		*/
		public int hasData()throws IOException;
	
	/* *************************************************************
	
			Primitives	
	
	* *************************************************************/
	/*=============================================================
	
		Elementatry primitives.
		
		Note:
			All elementary primitives do behave the same
			and do throw in the same conditions.
		
	===============================================================*/
		
		/** An elementary primitive read 
		@return fetched data
		@throws IllegalStateException if this method is called when any block operation
				is in progress.
		@throws IOException if low level i/o failed
		@throws ENoMoreData if stream cursor is at the signal
		@throws EDataMissmatch if detected signal inside a body of primitive
		@throws EUnexpectedEof if read resulted in end-of-file condition
		*/
		public boolean readBoolean()throws IOException;
		/**  An elementary  primitive read 
		@throws IOException if low level i/o failed
		@return fetched data
		@see #readBoolean
		*/
		public byte readByte()throws IOException;
		/**  An elementary primitive read
		@throws IOException if low level i/o failed
		@return fetched data		
		@see #readBoolean
		*/
		public char readChar()throws IOException;
		/**  An elementary  primitive read
		@throws IOException if low level i/o failed
		@return fetched data		
		@see #readBoolean
		*/
		public short readShort()throws IOException;
		/**  An elementary  primitive read
		@throws IOException if low level i/o failed
		@return fetched data		
		@see #readBoolean
		*/
		public int readInt()throws IOException;
		/**  An elementary  primitive read
		@throws IOException if low level i/o failed
		@return fetched data		
		@see #readBoolean
		*/
		public long readLong()throws IOException;
		/**  An elementary size primitive read
		@throws IOException if low level i/o failed
		@return fetched data		
		@see #readBoolean
		*/
		public float readFloat()throws IOException;
		/**  An elementary  primitive read
		@throws IOException if low level i/o failed
		@return fetched data		
		@see #readBoolean
		*/
		public double readDouble()throws IOException;
		/*=============================================================
		
			Primitive blocks
			
		=============================================================*/	
		/* -----------------------------------------------------------
			Primary
		-----------------------------------------------------------*/
		/** Reads a part of bit-block
		<br>
		An initial read block read may happen in any place <a href="package.html#event">event</a>.
		and after any <i>elementary</i> primitive read, or after any block read of the same type.
		<br>
		A block read may be followed <u>only</u> by other block read of the same type
		or by reading of an "end" or "begin" signal with {@link #next}.
		<br>
		@param buffer buffer for read data, non-null.
		@param offset where to save first data element in <code>buffer</code>
		@param length number of bytes to read
		@return <ul>
					<li>number of bytes read, which can be less that <code>length</code>
					only if signal was reached or;</li>
					<li>0 if could not read anything because signal was reached;</li>
				</ul>
				
		@throws AssertionError if <code>buffer</code> is null
		@throws AssertionError if <code>offset</code> or <code>length</code> are negative
		@throws AssertionError if <code>buffer.length</code> with <code>offset</code> and <code>length</code>
							   would result in {@link ArrayIndexOutOfBoundsException} exception;
		@throws IllegalStateException if call is made outside of an event.
		@throws IllegalStateException if there is block operation of another type in progress.  
		@throws IOException if failed at low level.
		@throws ENoMoreData if it was a first read in block reading sequence and it could not
						start because cursor is already at signal.
		@throws EUnexpectedEof if physical end of stream was reached.
		*/
		public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;
		
		/** Reads a part of byte-block.
		<p>
		Operates like {@link #readBooleanBlock}.
		@param buffer buffer for read data, non-null.
		@param offset where to save first bit in <code>buffer</code>
		@param length number of bytes to read
		@return as in byte-block, but returns number of boolean values regardless of how they
				were encoded.
		@throws IOException if failed at low level.
		*/				    
		public int readByteBlock(byte [] buffer, int offset, int length)throws IOException;
		/** Reads a part of byte block, one byte in size. 
		<p>
		This operation is usefull when implementing other raw block operations in per-byte basis.
		<p>
		@return -1 if could not read because "end" signal was reached, -2 if physical eof,
				otherwise a positive 0...255 number representing a byte.
		@throws IllegalStateException if call is made outside of an event.
		@throws IllegalStateException if there is block operation of another type in progress.  
		@throws IOException if failed at low level.
		*/			
		public int readByteBlock()throws IOException;
		
		/** Reads a part of characters block
		<br>
		Same rules applies as for byte-block.
		@param buffer where to add read characters
		@param length how many characters to read
		@return as in byte-block, but counts characters regardless of how they were encoded.
		@throws IOException if failed at low level.
		*/
		public int readCharBlock(Appendable buffer, int length)throws IOException;
		/* -----------------------------------------------------------
			Secondary
		-----------------------------------------------------------*/
		/** As read byte block
		@param buffer buffer for read data, non-null.
		@param offset where to save first data in <code>buffer</code>
		@param length number of elements to read
		@return as in byte-block
		@throws IOException if failed at low level.
		*/
		public int readCharBlock(char [] buffer, int offset, int length)throws IOException;
		/** As read byte block
		@param buffer buffer for read data, non-null.
		@param offset where to save first data in <code>buffer</code>
		@param length number of elements to read
		@return as in byte-block
		@throws IOException if failed at low level.
		*/
		public int readShortBlock(short [] buffer, int offset, int length)throws IOException;
		/** As read byte block
		@param buffer buffer for read data, non-null.
		@param offset where to save first data in <code>buffer</code>
		@param length number of elements to read
		@return as in byte-block
		@throws IOException if failed at low level.
		*/
		public int readIntBlock(int [] buffer, int offset, int length)throws IOException;
		/** As read byte block
		@param buffer buffer for read data, non-null.
		@param offset where to save first data in <code>buffer</code>
		@param length number of elements to read
		@return as in byte-block
		@throws IOException if failed at low level.
		*/
		public int readLongBlock(long [] buffer, int offset, int length)throws IOException;
		/** As read byte block
		@param buffer buffer for read data, non-null.
		@param offset where to save first data in <code>buffer</code>
		@param length number of elements to read
		@return as in byte-block
		@throws IOException if failed at low level.
		*/
		public int readFloatBlock(float [] buffer, int offset, int length)throws IOException;
		/** As read byte block
		@param buffer buffer for read data, non-null.
		@param offset where to save first data in <code>buffer</code>
		@param length number of elements to read
		@return as in byte-block
		@throws IOException if failed at low level.
		*/
		public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException;
};