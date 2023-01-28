package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
/**
	Defines contract for structured format support 
	as specified in <a href="package-summary.html">package description</a>.
	<p>
	This is a writing end of a format.	
	
	<h1>Method groups</h1>
	<table border="2">
	<caption> Method groups</caption>
		<tr>
		<td>
			State
		</td>
		<td>
			{@link #open},{@link #close},{@link #flush}
		</td>
		</tr>
		<tr>
		<td>
			Structure production
		</td>
		<td>
			{@link #begin},{@link #end},{@link #optimizeBeginName}
		</td>
		</tr>
		<tr>
		<td>
			Single primitive elements 
		</td>
		<td>
			{@link #writeBoolean},
			{@link #writeByte},
			{@link #writeChar},
			{@link #writeInt},
			{@link #writeLong},
			{@link #writeFloat},
			{@link #writeDouble}
		</td>
		</tr>
		<tr>
		<td>
			Sequences of primitive elements, array variants 
		</td>
		<td>
			{@link #writeBooleanBlock(boolean[],int,int)},{@link #writeBooleanBlock(boolean[])},<br>
			{@link #writeByteBlock(byte[],int,int)},{@link #writeByteBlock(byte[])},<br>
			{@link #writeCharBlock(char[],int,int)},{@link #writeCharBlock(char[])},<br>
			{@link #writeShortBlock(short[],int,int)},{@link #writeShortBlock(short[])},<br>
			{@link #writeIntBlock(int[],int,int)},{@link #writeIntBlock(int[])},<br>
			{@link #writeLongBlock(long[],int,int)},{@link #writeLongBlock(long[])},<br>
			{@link #writeFloatBlock(float[],int,int)},{@link #writeFloatBlock(float[])},<br>
			{@link #writeDoubleBlock(double[],int,int)},{@link #writeDoubleBlock(double[])},<br>
		</td>
		</tr>
		<tr>
		<td>
			Sequences of primitive elements, single element variants 
		</td>
		<td>
			{@link #writeBooleanBlock},
			{@link #writeByteBlock},
			{@link #writeCharBlock},
			{@link #writeIntBlock},
			{@link #writeLongBlock},
			{@link #writeFloatBlock},
			{@link #writeDoubleBlock}
		</td>
		</tr>
		<tr>
		<td>
			Sequences of <code>String</code>
		</td>
		<td>
			{@link #writeString(CharSequence,int,int)},
			{@link #writeString(CharSequence)},
			{@link #writeString(char)},
		</td>
		</tr>
	</table>
	
	<h1>Thread safety</h1>
	Format is <u>not thread safe</u>.	
*/
public interface IStructWriteFormat extends Closeable, Flushable, IFormatLimits
{	
	/* *************************************************************
	
			Signals 	
	
	* *************************************************************/
		
	/* ------------------------------------------------------------
				Signals
	------------------------------------------------------------*/
	/** <a name="BEGIN"></a>
	Writes "begin" signal opening the structure.
	
	@param name non null name of a structure which now begins. 
			
	@throws AssertionError if <code>name</code> is null.
	@throws EFormatBoundaryExceeded if name of signal is too long.
		See {@link IFormatLimits#getMaxSignalNameLength}
	@throws EFormatBoundaryExceeded if structure recursion depth control is enabled
		and this limit is exceeded. See {@link IFormatLimits#setMaxStructRecursionDepth}
	@throws IOException if low level i/o fails or stream is closed/not opened
	*/
	public void begin(String name)throws IOException;
	
	/** Writes "end" signal, thous closing the structure.
	
	@throws EFormatBoundaryExceeded if there is no un-closed structure.
	@throws IOException if low level i/o fails or stream is closed/not opened
	*/
	public void end()throws IOException;
	
	/** Suggests to the stream implementation that specified name will
	be frequently used and should be optimized as much as possible.
	This will usually require using some kind of signal registry
	and mapping from strings to numbers.
	<p>
	Default implementationd doesn't do anything and returns true.
	<p>
	This operation by itself must not create any I/O operation and
	any information stored to stream must be delayed till this name
	is actually used for a first time.
	<p>
	Subsequent calls with the same parameter should be silently ignored
	and return the same value as when they were done for the first time.
	@param name name to optimize, non null.
	@return true if could optimize name or there is no need to do it at all
		 (ie. stream does not support any optimization). 
		 <br>
		 Returns false if there is such a possiblity but resources are exceeded
		 so it could not optimize that name. 
		 <p>
		 Returning false may not cause stream to fail but
		 may negatively impact performance.			  
	@throws EFormatBoundaryExceeded if name is too long
		See {@link IFormatLimits#getMaxSignalNameLength}
	@throws IOException if stream is closed/not opened.
	*/
	public boolean optimizeBeginName(String name)throws IOException;
	
	
	
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
	@param v value to write
	@throws IOException if low level i/o fails
	@throws EBrokenFormat if broken permanently
	@throws IllegalStateException if any primitive sequence is in progress
	*/
	public void writeBoolean(boolean v)throws IOException;
	/** See {@link #writeBoolean(boolean)}
	@param v --//--
	@throws IOException --//--
	*/
	public void writeByte(byte v)throws IOException;
	/** See {@link #writeBoolean(boolean)} and {@link #writeCharBlock(char[],int,int)}.
	<p>
	Alike <code>char[]</code> block write this method is explicite required to
	be able to store the whole 0x0000...0xFFFF range regardless if the sequence
	of characters to create a valid unicode code point.
	@param v --//--
	@throws IOException --//--
	*/
	public void writeChar(char v)throws IOException;
	/** See {@link #writeBoolean(boolean)}
	@param v --//--
	@throws IOException --//--
	*/
	public void writeShort(short v)throws IOException;
	/** See {@link #writeBoolean(boolean)}
	@param v --//--
	@throws IOException --//--
	*/
	public void writeInt(int v)throws IOException;
	/** See {@link #writeBoolean(boolean)}
	@param v --//--
	@throws IOException --//--
	*/
	public void writeLong(long v)throws IOException;
	/** See {@link #writeBoolean(boolean)}.
	Must preserve NaN and +/-Infinity correctly.
	@param v --//--
	@throws IOException --//--
	*/
	public void writeFloat(float v)throws IOException;
	/** See {@link #writeBoolean(boolean)}
	Must preserve NaN and +/-Infinity correctly.
	@param v --//--
	@throws IOException --//--
	*/
	public void writeDouble(double v)throws IOException;
	
	/*=============================================================
	
		Primitive blocks
		
	=============================================================*/		
	/** Writes a part of a boolean primitive data sequence.
	
	@param buffer source of data, non-null.
	@param offset first byte to write in <code>buffer</code>
	@param length number of bytes to write
	@throws AssertionError if <code>buffer</code> is null
	@throws AssertionError if <code>offset</code> or <code>length</code> are negative
	@throws AssertionError if <code>buffer.length</code> with <code>offset</code> and <code>length</code>
						   would result in {@link ArrayIndexOutOfBoundsException} exception;
	@throws IOException if low level i/o fails.
	@throws EBrokenFormat if broken permanently
	@throws IllegalStateException if a sequence of incompatible type is in progress.
	*/
	public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
	/** Writes a part of a boolean  primitive data sequence.
	@param buffer  {@link #writeBooleanBlock(boolean[],int,int)}, an entire array is written.
	@throws IOException --//--
	*/
	public default void writeBooleanBlock(boolean [] buffer)throws IOException
	{
			assert(buffer!=null);
			writeBooleanBlock(buffer,0,buffer.length);
	}
	/** See {@link #writeBooleanBlock(boolean[],int,int)},
	single element version.
	@param v single element
	@throws IOException --//--
	*/
	public void writeBooleanBlock(boolean v)throws IOException;
	
	
	
	/**  The byte variant of {@link #writeBooleanBlock(boolean[],int,int)}
	@param buffer --//--
	@param offset --//--
	@param length --//--
	@throws IOException --//--
	*/
	public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException;		
	/** The byte variant of {@link #writeBooleanBlock(boolean[])}
	@param buffer --//--
	@throws IOException --//--
	*/
	public default void writeByteBlock(byte [] buffer)throws IOException
	{
			assert(buffer!=null);
			writeByteBlock(buffer,0,buffer.length);
	}		
	/** The byte variant of {@link #writeBooleanBlock(boolean)}
	@param v single byte to write
	@throws IOException --//--
	*/
	public void writeByteBlock(byte v)throws IOException;
	
	
	
	
	/** The String variant of {@link #writeBooleanBlock(boolean[],int,int)}.
	<p>
	This method writes string of characters using a compact, non random access mode,
	ie. UTF-8 or alike.
	<p>
	<i>
	Note: sequence written with this method is <u>not</u> compatible with sequence 
	written with {@link #writeCharBlock}.
	</i>
	<p>
	See also notes in {@link #writeCharBlock(char[],int,int)}.
	@param characters a character sequence of absolutely any combination,
				including invalid unicode higher/lower surogates combinations.
	@param offset --//--
	@param length --//--
	@throws IOException --//--
	@throws IllegalArgumentException if implmentation will detect invalid surogate
			pair.
	*/
	public void writeString(CharSequence characters, int offset, int length)throws IOException;		
	/** The String variant of  {@link #writeBooleanBlock(boolean[])}
	@param characters --//--
	@throws IOException --//--
	*/
	public default void writeString(CharSequence characters)throws IOException
	{
			assert(characters!=null);
			writeString(characters,0,characters.length());
	}
	/** A single character version.
	@param c string character
	@throws IOException see {@link #writeString(CharSequence)} */
	public void writeString(char c)throws IOException;
	
	/** The char variant of {@link #writeBooleanBlock(boolean[],int,int)}.
	<p>
	This method write sequence of characters using fast, random access mode, ie. as a
	sequence of 16 bit integers.
	
	@param buffer what to write. May contain absolutely any combination of characters,
		including invalid unicode higher/lower surogates combinations.
	@param offset --//--
	@param length --//--
	@throws IOException --//--
	 
	*/
	public void writeCharBlock(char [] buffer, int offset, int length)throws IOException;		
	/** The char variant of  {@link #writeBooleanBlock(boolean[])}
	@param buffer --//--
	@throws IOException --//--
	*/
	public default void writeCharBlock(char [] buffer)throws IOException
	{
			assert(buffer!=null);
			writeCharBlock(buffer,0,buffer.length);
	}
	/** The char variant of  {@link #writeBooleanBlock(boolean)}
	@param data single element to write
	@throws IOException --//--
	*/
	public void writeCharBlock(char data)throws IOException;
	
	
	
	
	/**  The short variant of  {@link #writeBooleanBlock(boolean[],int,int)}
	@param buffer --//--
	@param offset --//--
	@param length --//--
	@throws IOException --//--
	*/
	public void writeShortBlock(short [] buffer, int offset, int length)throws IOException;		
	/** The short variant of {@link #writeBooleanBlock(boolean[])}
	@param buffer --//--
	@throws IOException --//--
	*/
	public default void writeShortBlock(short [] buffer)throws IOException
	{
			assert(buffer!=null);
			writeShortBlock(buffer,0,buffer.length);
	}
	/** The short variant of {@link #writeBooleanBlock(boolean)}
	@param data single element to write
	@throws IOException --//--
	*/
	public void writeShortBlock(short data)throws IOException;
	
	
	
	/**  The int variant of {@link #writeBooleanBlock(boolean[],int,int)}
	@param buffer --//--
	@param offset --//--
	@param length --//--
	@throws IOException --//--
	*/
	public void writeIntBlock(int [] buffer, int offset, int length)throws IOException;		
	/** The int variant of  {@link #writeBooleanBlock(boolean[])}
	@param buffer --//--
	@throws IOException --//--
	*/
	public default void writeIntBlock(int [] buffer)throws IOException
	{
			assert(buffer!=null);
			writeIntBlock(buffer,0,buffer.length);
	}
	/** The int variant of  {@link #writeBooleanBlock(boolean)}
	@param data single element to write
	@throws IOException --//--
	*/
	public void writeIntBlock(int data)throws IOException;
	
	
	
	/**  The long variant of  {@link #writeBooleanBlock(boolean[],int,int)}
	@param buffer --//--
	@param offset --//--
	@param length --//--
	@throws IOException --//--
	*/
	public void writeLongBlock(long [] buffer, int offset, int length)throws IOException;		
	/** The long variant of  {@link #writeBooleanBlock(boolean[])}
	@param buffer --//--
	@throws IOException --//--
	*/
	public default void writeLongBlock(long [] buffer)throws IOException
	{
			assert(buffer!=null);
			writeLongBlock(buffer,0,buffer.length);
	}
	/** The long variant of  {@link #writeBooleanBlock(boolean)}
	@param data single element to write
	@throws IOException --//--
	*/
	public void writeLongBlock(long data)throws IOException;
	
	
	
	
	/**  The float variant of  {@link #writeBooleanBlock(boolean[],int,int)}.
	<p>
	Must preserve NaN and +/-Infinity correctly.
	@param buffer --//--
	@param offset --//--
	@param length --//--
	@throws IOException --//--
	*/
	public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException;
	/** The float variant of  {@link #writeBooleanBlock(boolean[])}.
	<p>
	Must preserve NaN and +/-Infinity correctly.
	@param buffer --//--
	@throws IOException --//--
	*/
	public default void writeFloatBlock(float [] buffer)throws IOException
	{
			assert(buffer!=null);
			writeFloatBlock(buffer,0,buffer.length);
	}
	/** The float variant of  {@link #writeBooleanBlock(boolean)}.
	<p>
	Must preserve NaN and +/-Infinity correctly.
	@param data single element to write
	@throws IOException --//--
	*/
	public void writeFloatBlock(float data)throws IOException;
	
	
	
	/**  The double variant of  {@link #writeBooleanBlock(boolean[],int,int)}.
	<p>
	Must preserve NaN and +/-Infinity correctly.
	@param buffer --//--
	@param offset --//--
	@param length --//--
	@throws IOException --//--
	*/
	public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException;
	/**  The double variant of {@link #writeBooleanBlock(boolean[])}.
	<p>
	Must preserve NaN and +/-Infinity correctly.
	@param buffer --//--
	@throws IOException --//--
	*/
	public default void writeDoubleBlock(double [] buffer)throws IOException
	{
			assert(buffer!=null);
			writeDoubleBlock(buffer,0,buffer.length);
	}
	/**  The double variant of {@link #writeBooleanBlock(boolean)}.
	<p>
	Must preserve NaN and +/-Infinity correctly.
	@param data single element to write
	@throws IOException --//--
	*/
	public void writeDoubleBlock(double data)throws IOException;
	
			
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
		<li>if format is closed, throws {@link EClosed}.</li>
	</ul>		 	
	<p>
	Until format is open all methods except {@link #close}
	and defined in {@link IFormatLimits} should throw {@link ENotOpen}
	@throws IOException if failed.
	*/ 
	public void open()throws IOException;
	/** All buffers should be passed down to low level i/o and that i/o should be flushed.
	No data may be left in a format writer which are not passed down to low level i/o.
	<p>
	<i>Note: In some formats flushing may have an impact on stream structure.
	</i>
	@throws IOException if low level i/o fails.
	*/
	public void flush()throws IOException;
	/**
	This method closes format and makes it unusable.
	<p>
	This method depending on state should:
	<ul>
		<li>if format is already closed, don't do anything;</li>
		<li>if format is not open, closes low level resources without doing anything;</li>
		<li>if format is open call {@link #flush}, write closing sequence if necessary
		and close low level resources.</li>
	</ul>		 	
	<p>
	Once format is closed all methods except  {@link #close} and defined in {@link IFormatLimits} 
	should throw {@link EClosed}.
	<p>
	Note: Closed format <u>cannot be opened</u>.
	*/
	public void close()throws IOException;
};