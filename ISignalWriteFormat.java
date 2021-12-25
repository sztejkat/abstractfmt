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
public interface ISignalWriteFormat extends Closeable, Flushable,IPrimitiveWriteFormat
{	
	/* *************************************************************
	
			Signals and events	
	
	* *************************************************************/
	/* ------------------------------------------------------------
				Limits
	------------------------------------------------------------*/
		/** Allows to set limit for signal name.
		<p>
		Default value is 1024.
		@param characters name limit, non-negative. Zero is silly,
		no signal names can be written 
		@throws AssertionError if characters exceeds {@link #getMaxSupportedSignalNameLength} 
		@throws IllegalStateException if closed.
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
	/* ------------------------------------------------------------
				Signals
	------------------------------------------------------------*/
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
	
			IPrimitiveWriteFormat
	
		* *************************************************************/
		
		/** An elementary primitive write.
		<p>
		An elementry primitive write may happen anywhere in a stream, including
		outside of an <a href="package.html#event">event</a>.
		<p>
		If format is described it must write type information.
		
		@param v value to write
		@throws IllegalStateException if invoked after any block-operation was initiated and
			 is still not terminated.
		@throws IOException if low level i/o fails.
		*/
		@Override public void writeBoolean(boolean v)throws IOException;
		
		/*=============================================================
		
			Primitive blocks
			
		=============================================================*/		
		/** 
		An initial  block write may happen in any place inside and an <a href="package.html#event">event</a>.
		<p>
		A block write may be followed <u>only</u> by other block write of the same type.
		or by writing of "end" or "begin" signal.
		<p>
		If format is described it must write type information.
						
		@throws IllegalStateException if there is block operation of another type in progress
					or there is no active event.
		*/
		@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;
			
		/* ***********************************************************
		
			Status, Closable, Flushable
			
		************************************************************/
		/**
		This method prepares format and makes it usable.
		<p>
		This method depending on state should:
		<ul>
			<li>if format is already open, don't do anything;</li>
			<li>if format is not open, write necessary opening sequence if any;</li>
			<li>if format is closed, thorw.</li>
		</ul>		 	
		<p>
		Until format is open all methods except <code>close</code>
		should throw IOException. Throwing {@link ENotOpen} is recommended.
		@throws IOException if failed.
		*/ 
		public void open()throws IOException;
		/** All buffers should be passed down to low level i/o and that i/o should be flushed.
		No data may be left in a format writer which are not passed down to low level i/o.
		<p>
		Note: In some formats, esp. so called <i>chunk-based</i> when stream is cut to <i>chunks</i>
		of known size flushing may cause stream fragmentation and have an impact on it's structure.
		@throws IOException if low level i/o fails.
		*/
		public void flush()throws IOException;
		/**
		This method closes format and makes it unusable.
		<p>
		This method depending on state should:
		<ul>
			<li>if format is already closed, don't do anything;</li>
			<li>if format is not open, close low level resources without doing anything;</li>
			<li>if format is open call {@link #flush}, write closing sequence if necessary
			and close low level resources.</li>
		</ul>		 	
		<p>
		Once format is closed all methods except <code>close</code>
		should throw IOException. Throwing {@link EClosed} is recommended.
		*/
		public void close()throws IOException;
};