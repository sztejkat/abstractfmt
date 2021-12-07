package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
/**
	Defines contract for abstract event based format support 
	as specified in package	description, writing end of a format.	
	
	<h1>Thread safety</h1>
	As low level component formats are <u>not thread safe</u>.	
*/
public interface ISignalWriteFormat extends Closeable, Flushable
{	
	/* *************************************************************
	
			Signals and events	
	
	* *************************************************************/

		/** Returns maximum supported length of a signal name in this
		format. A life-time constant. 
		@return maximum number of characters allowed in
			   {@link #begin}. It is recommended that it is
			   not less than 8.
			   <p>
			   Too small value may prevent format from any
			   usability, too large value (esp. un-bound) 
			   may open paths to 
			   <a href="doc-files/security.html#OOMEVENT_NAME">"out-of-memory"</a> attacks.
		*/
		public int getMaxSignalNameLength();
		/** <a name="BEGIN"></a>
		Writes "begin" signal. Begin signals do indicate
		the beginning of an <a href="package.html#event">event</a>
		and may be contained in already existing events.
		
		@param signal non null name of a signal which now begins.
				Empty string is allowed. 
				<p>
				The usual use of "" (empty name signal) is to
				provide a "fence" or a "guard" around certain data
				so that anyone who reads them may not read past the
				"fence" and does no thave to read all the "guarded" content
				because {@link ISignalReadFormat#next} can skip it.
		@param do_not_optimize if true informs the stream format that this signal
				is so rare, that there is no reason optimize it's writing by any
				means. Especially formats which convert strings to numbers of 
				a limited set are expected to not do this conversion and store
				this signal directly as string.
				<p>
				This is usefull when writing MIME-type headers which by definition
				do appear in stream only once.
				
		@throws AssertionError if <code>signal</code> is null.
		@throws IllegalArgumentException if name of signal is too long.
		@throws IllegalStateException if events recursion depth control is enabled
			and this limit is exceeded. See <a href="doc-files/security.html#STACK_OVERFLOW_ATTACK">"stack overflow"</a> attack.
		@throws IOException if low level i/o fails.
		*/
		public void begin(String signal,boolean do_not_optimize)throws IOException;
		
		/** 
		As {@link #begin(String,boolean)} with <code>do_not_optimize=false</code>
		@param signal as {@link #begin(String,boolean)}
		@throws IOException --//--
		@throws AssertionError --//--
		@throws IllegalArgumentException  --//--
		*/
		default public void begin(String signal)throws IOException{ begin(signal,false); };
	
		/** Writes "end" of signal, thous closing an <a href="package.html#event">event</a>.
		<p>
		<i>Note: Implementations should take in account that the <code>end();begin(...)</code>
		sequence of operations will be very frequent.</i>
		<p>
		@throws IllegalStateException if there is no un-closed event signal.
		@throws IOException if low level i/o fails.
		*/
		public void end()throws IOException;
		
		
		/** True if stream implementation is "described".
		A described implementation <u>must</u> write type information for each primitive
		operation.
		<p>
		A "non-described" implementation <u>must not</u> write type information for any
		primitive operation.
		@return true if described. A life time constant.		
		*/
		public boolean isDescribed();
		
	/* *************************************************************
	
			Primitives	
	
	* *************************************************************/
	/*=============================================================
	
		Elementatry primitives.
		
		Note:
			All elementary primitives do behave the same
			and do throw in the same conditions.
		
	===============================================================*/
		
		/** An elementary primitive write.
		<p>
		An elementry primitive write may happen anywhere in a stream, including
		outside of an <a href="package.html#event">event</a>.
		
		@param v value to write
		@throws IllegalStateException if invoked after any block-operation was initiated and
			 is still not terminated.
		@throws IOException if low level i/o fails.
		*/
		public void writeBoolean(boolean v)throws IOException;
		/** A elementary primitive write
		@param v value to write
		@throws IOException if low level i/o fails.
		@see #writeBoolean 
		*/
		public void writeByte(byte v)throws IOException;
		/** A elementaryprimitive write
		@param v value to write
		@throws IOException if low level i/o fails.
		@see #writeBoolean
		*/
		public void writeChar(char v)throws IOException;
		/** A elementary primitive write		
		@param v value to write
		@throws IOException if low level i/o fails.
		@see #writeBoolean
		*/
		public void writeShort(short v)throws IOException;
		/** A elementary primitive write
		@param v value to write
		@throws IOException if low level i/o fails.
		@see #writeBoolean
		*/
		public void writeInt(int v)throws IOException;
		/** A elementary primitive write
		@param v value to write
		@throws IOException if low level i/o fails.
		@see #writeBoolean
		*/
		public void writeLong(long v)throws IOException;
		/** A elementary primitive write
		@param v value to write
		@throws IOException if low level i/o fails.
		@see #writeBoolean
		*/
		public void writeFloat(float v)throws IOException;
		/** A  elementary primitive write
		@param v value to write
		@throws IOException if low level i/o fails.
		@see #writeBoolean
		*/
		public void writeDouble(double v)throws IOException;
		
		/*=============================================================
		
			Primitive blocks
			
		=============================================================*/		
		/* -----------------------------------------------------------
			Primary
		-----------------------------------------------------------*/
		/** Writes a part of a bit-block.
		<p>
		Same rules applies as for byte-block.
		@param buffer source of data, non-null.
		@param offset first bit to write in <code>buffer</code>
		@param length number of bytes to write
		@throws IllegalStateException if there is block operation of another type in progress
						or there is no active event.
		@throws IOException if low level i/o fails.
		@see #writeByteBlock
		*/
		public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;
		
		/** Falls back to {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer  data write, non null
		@throws AssertionError if s is null
		@throws IOException if called method thrown.
		*/
		public default void writeBooleanBlock(boolean [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeBooleanBlock(buffer,0,buffer.length);
		}
		
		/** Writes a part of a byte-block.
		<p>
		An initial byte block write may happen 
		in any place inside and an <a href="package.html#event">event</a>.
		<p>
		A byte-block write may be followed <u>only</u> by other byte-block write
		or by writing of "end" or "begin" signal
		
		@param buffer source of data, non-null.
		@param offset first byte to write in <code>buffer</code>
		@param length number of bytes to write
		@throws AssertionError if <code>buffer</code> is null
		@throws AssertionError if <code>offset</code> or <code>length</code> are negative
		@throws AssertionError if <code>buffer.length</code> with <code>offset</code> and <code>length</code>
							   would result in {@link ArrayIndexOutOfBoundsException} exception;
						
		@throws IllegalStateException if there is block operation of another type in progress
										or there is no active event.
		@throws IOException if low level i/o fails.
		*/
		public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException;
		
		/** Falls back to {@link #writeByteBlock(byte[],int,int)}
		@param buffer data write, non null
		@throws AssertionError if s is null
		@throws IOException if called method thrown.
		*/
		public default void writeByteBlock(byte [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeByteBlock(buffer,0,buffer.length);
		}
		
		/** Writes a part of a byte-block consisting of a single byte.
		<p>
		This operation is usefull when implementing other raw block operations in per-byte basis.
		<p>
		An equivalent of 
		<pre>
		writeByteBlock(new byte[]{data},0,1)
		</pre>
		@param data data to write
		@throws IllegalStateException if there is block operation of another type in progress
								or there is no active event.
		@throws IOException if low level i/o fails.
		*/
		public void writeByteBlock(byte data)throws IOException;
		
		
		/** Writes a part of a character-block
		<p>
		Same rules applies as for byte-block.
		<p>
		All java characters	must be allowed and no assumptions can be
		made about the structure of strings (ie. null termination, encoding, invalid unicodes and etc.)		
		@param characters source of data, non-null.
		@param offset first bit to write in <code>buffer</code>
		@param length number of bytes to write
		@throws IllegalStateException if there is block operation of another type in progress
							or there is no active event.
		@throws IOException if low level i/o fails.
		@see #writeByteBlock
		*/
		public void writeCharBlock(CharSequence characters, int offset, int length)throws IOException;
		
		/** Falls back to {@link #writeCharBlock(CharSequence,int,int)}
		@param s text to write, non null
		@throws AssertionError if s is null
		@throws IOException if {@link #writeCharBlock(CharSequence,int,int)} thrown.
		*/
		public default void writeCharBlock(CharSequence s)throws IOException
		{
				assert(s!=null);
				writeCharBlock(s,0,s.length());
		}
		
		/* -----------------------------------------------------------
			Secondary
		-----------------------------------------------------------*/
		/** As {@link #writeCharBlock} block, but takes params as {@link #writeByteBlock}.
		@param buffer source of data, non-null.
		@param offset first position to write in <code>buffer</code>
		@param length number of elements to write
		@throws IOException if low level i/o fails.
		*/
		public void writeCharBlock(char [] buffer, int offset, int length)throws IOException;
		
		/** Falls back to {@link #writeCharBlock(char[],int,int)}
		@param buffer data write, non null
		@throws AssertionError if s is null
		@throws IOException if called method thrown.
		*/
		public default void writeCharBlock(char [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeCharBlock(buffer,0,buffer.length);
		}
		
		
		/** As {@link #writeByteBlock}
		@param buffer source of data, non-null.
		@param offset first position to write in <code>buffer</code>
		@param length number of elements to write
		@throws IOException if low level i/o fails.
		*/
		public void writeShortBlock(short [] buffer, int offset, int length)throws IOException;
		
		/** Falls back to {@link #writeShortBlock(short[],int,int)}
		@param buffer data write, non null
		@throws AssertionError if s is null
		@throws IOException if called method thrown.
		*/
		public default void writeShortBlock(short [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeShortBlock(buffer,0,buffer.length);
		}
		
		/** As {@link #writeByteBlock}
		@param buffer source of data, non-null.
		@param offset first position to write in <code>buffer</code>
		@param length number of elements to write
		@throws IOException if low level i/o fails.
		*/
		public void writeIntBlock(int [] buffer, int offset, int length)throws IOException;
		
		/** Falls back to {@link #writeIntBlock(int[],int,int)}
		@param buffer  data write, non null
		@throws AssertionError if s is null
		@throws IOException if called method thrown.
		*/
		public default void writeIntBlock(int [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeIntBlock(buffer,0,buffer.length);
		}
		
		
		/** As {@link #writeByteBlock}
		@param buffer source of data, non-null.
		@param offset first position to write in <code>buffer</code>
		@param length number of elements to write
		@throws IOException if low level i/o fails.
		*/
		public void writeLongBlock(long [] buffer, int offset, int length)throws IOException;
		
		/** Falls back to {@link #writeLongBlock(long[],int,int)}
		@param buffer  data write, non null
		@throws AssertionError if s is null
		@throws IOException if called method thrown.
		*/
		public default void writeLongBlock(long [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeLongBlock(buffer,0,buffer.length);
		}
		
		
		/** As {@link #writeByteBlock}
		@param buffer source of data, non-null.
		@param offset first position to write in <code>buffer</code>
		@param length number of elements to write
		@throws IOException if low level i/o fails.
		*/
		public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException;
		
		/** Falls back to {@link #writeFloatBlock(float[],int,int)}
		@param buffer  data write, non null
		@throws AssertionError if s is null
		@throws IOException if called method thrown.
		*/
		public default void writeFloatBlock(float [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeFloatBlock(buffer,0,buffer.length);
		}
		
		
		
		/** As {@link #writeByteBlock}
		@param buffer source of data, non-null.
		@param offset first position to write in <code>buffer</code>
		@param length number of elements to write
		@throws IOException if low level i/o fails.
		*/
		public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException;
		
		/** Falls back to {@link #writeDoubleBlock(double[],int,int)}
		@param buffer  data write, non null
		@throws AssertionError if s is null
		@throws IOException if called method thrown.
		*/
		public default void writeDoubleBlock(double [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeDoubleBlock(buffer,0,buffer.length);
		}
		
		
		/*=============================================================
		
			Status
			
		=============================================================*/
		/** All buffers should be passed down to low level i/o and that i/o should be flushed.
		No data may be left in a format writer which are not passed down to low level i/o.
		<p>
		Note: In some formats, esp. so called <i>chunk-based</i> when stream is cut to <i>chunks</i>
		of known size flushing may cause stream fragmentation and have an impact on it's structure.
		@throws IOException if low level i/o fails.
		*/
		public void flush()throws IOException;
		/** Calls {@link #flush} */
		public default void close()throws IOException{ flush(); };
};