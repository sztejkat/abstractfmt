package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;
/**
	Defines contract for abstract event based format support 
	as specified in <a href="package-summary.html">package	description</a>.
	<p>
	This is a reading end of a format.
	
	<h1>Method groups</h1>
	<table border="2">
	<caption> Method groups</caption>
		<tr>
		<td>
			State
		</td>
		<td>
			{@link #open},{@link #close}
		</td>
		</tr>
		<tr>
		<td>
			Structure traversing
		</td>
		<td>
			{@link #next},{@link #hasElementaryData},{@link #skip},{@link #skip(int)}
		</td>
		</tr>
		<tr>
		<td>
			Single primitive elements 
		</td>
		<td>
			{@link #readBoolean},
			{@link #readByte},
			{@link #readChar},
			{@link #readInt},
			{@link #readLong},
			{@link #readFloat},
			{@link #readDouble}
		</td>
		</tr>
		<tr>
		<td>
			Sequences of primitive elements, array variants 
		</td>
		<td>
			{@link #readBooleanBlock(boolean[],int,int)},{@link #readBooleanBlock(boolean[])},<br>
			{@link #readByteBlock(byte[],int,int)},{@link #readByteBlock(byte[])},<br>
			{@link #readCharBlock(char[],int,int)},{@link #readCharBlock(char[])},<br>
			{@link #readShortBlock(short[],int,int)},{@link #readShortBlock(short[])},<br>
			{@link #readIntBlock(int[],int,int)},{@link #readIntBlock(int[])},<br>
			{@link #readLongBlock(long[],int,int)},{@link #readLongBlock(long[])},<br>
			{@link #readFloatBlock(float[],int,int)},{@link #readFloatBlock(float[])},<br>
			{@link #readDoubleBlock(double[],int,int)},{@link #readDoubleBlock(double[])},<br>
		</td>
		</tr>
		<tr>
		<td>
			Sequences of primitive elements, single element variants 
		</td>
		<td>
			{@link #readBooleanBlock},
			{@link #readByteBlock},
			{@link #readCharBlock},
			{@link #readIntBlock},
			{@link #readLongBlock},
			{@link #readFloatBlock},
			{@link #readDoubleBlock}
		</td>
		</tr>
		<tr>
		<td>
			Sequences of <code>String</code>
		</td>
		<td>
			{@link #readString(Appendable,int)},
			{@link #readString(int)},
			{@link #readString()}
		</td>
		</tr>
	</table>
	
	<h1>Thread safety</h1>
	Formats are <u>not thread safe</u>.	Yes, this is intentional. Check package description
	for detailed explanation.
	
	<h1>IOException</h1>
	Whenever this contract states that something <i>throws IOException</i> it is strictly
	required that a <u>specific exception class</u> is thrown for a specific reason.
	
	<h2 id="TEMPEOF">eof handling model</h2>
	This class, in generic assumes that if end-of-file is reported by throwing 
	an exception then it is an un-recoverable error and {@link EUnexpectedEof}
	should be used.
	<p>
	If end-of-file is expected but non-recoverable the {@link EEof} should be thrown.
	<p>
	If however the subclass decides that some end-of-file exceptions are 
	recoverable it should throw {@link ETemporaryEndOfFile}. This contract does
	not specify which operations and under which conditions can recover from
	end-of-file. 
	<p>
	Implementations which do support recoverable end of file <u>must</u>
	clearly specifiy when and how one can recover from the exception.
	If it is not specified user should assume that there is no support for
	recovering from end-of-file exception.
*/
public interface IStructReadFormat extends Closeable, IFormatLimits
{	
	/* ************************************************************
	
			Signals 
	
	* *************************************************************/
		/** <a name="NEXT"></a>
		Skips all remaning primitve data, moves to next
		signal, reads it and returns. The stream cursor is
		placed right after the signal.
		
		@return null if read "end" signal from a stream, non-null
			    string if read "begin" signal. Each instance of 
				"begin" signal is allowed to return different instance
				of a <code>String</code>, even tough it is carrying an identical text.
		@throws IOException if failed due to any reason. 
		@throws EEof of specific subclass according to <a href="#TEMPEOF">eof handling model</a>.
		@throws EFormatBoundaryExceeded if either name is longer than
			{@link IFormatLimits#getMaxSignalNameLength} or 
			recursion is too deep (see {@link IFormatLimits#setMaxStructRecursionDepth})
		@see #hasElementaryData
		*/
		public String next()throws IOException;
		
		/** Tests if {@link #next} will skip any elementary data before moving to
		the signal. May result in some I/O operation if determining if there
		are any primitive data do require that. Must have no visible effect on
		any other format operation and can be invoked at any moment.
		<p>
		The fact that this method returns true means that an <i>apropriate</i>
		elementary read, including block should not fail and return with non
		zero amount of data. Which read is <i>apropriate</i> is not the subject
		of this contract.
		
		@return true if {@link #next} will have to skip some primitive 
			data, false if there is nothing what can be read before reaching
			either signal or end of file. 
			<p>
			Notice that the fact that this method returns true does not
			warrant that {@link #next} will not throw {@link EEof}. It may 
			throw it if there are primitive elemnts but there is no any
			signal it may stop at before end of file is reached.
			
		@throws IOException if failed, including stream closed or not opened.
	    */
		public boolean hasElementaryData()throws IOException;
		
		
		/** Skips all remaining primitives and all nested structures until
		it will read and consume the "end" signal for structure event.
		<p>
		This method is to be used when You have read a "begin" of a structure,
		figured out that You are not interested at all in what is in it,
		regardless how many prmitives and events is inside
		and You need to move cursor after the end of it.
		<p>
		Example structure:
		<pre>
			A{			      
				B{
					C{
					 }
				}
				D{		}
				E{		}
			  }
			F{	
			}
		</pre>
		Example sequence:
		<pre>
			next()=="A"
			skip(0)
			next()=="F"
		</pre>
		<pre>
			next()=="A"
			next()=="B"
			skip(0)
			next()=="D"
		</pre>
		<pre>
			next()=="A"
			next()=="B"
			skip(1)
			next()=="F"
		</pre>
		<p>		 
		@param levels how many levels upwards to skip. Zero: skip just all remaning
			body of current structure and its end signal, including all sub-structures.
		@throws IOException if low level i/o failed or an appropriate
			subclass to represent encountered problem.
		@throws EBrokenFormat if broken permanently
		@throws EFormatBoundaryExceeded if skipping required to dig too deep into
			a structures or encountered a begin signal name out of allowed limits.
		*/ 
		public default void skip(int levels)throws IOException
		{
			int depth = levels+1;
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
		
		/** An equivalent of <code>skip(0)</code>.
		@throws IOException if {@link #skip(int)} failed		
		*/ 
		public default void skip()throws IOException
		{
			 skip(0);  
		};
		
		
	/* *************************************************************
	
			Primitives	
	
	* *************************************************************/
	/*=============================================================
	
		Elementatry primitives.
		
		Note:
			All elementary primitives do behave the same
			and do throw in the same conditions.
		
	===============================================================*/
		
		/** An elementary primitive read.
		@return value read from stream.
		@throws IOException if fails due to many reasons. 
		@throws ENoMoreData if stream cursor is at the signal and there is no
				data to initiate operation.			
		@throws ESignalCrossed if an attempt to cross a signal is made inside a primitive element.
		@throws EEof of specific subclass depending on <a href="#TEMPEOF">eof handling type</a>
		@throws IllegalStateException if this method is called when any block operation
				is in progress.
		@see IStructWriteFormat#writeBoolean
		*/
		public boolean readBoolean()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		@see IStructWriteFormat#writeByte
		*/
		public byte readByte()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		@see IStructWriteFormat#writeChar
		*/
		public char readChar()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		@see IStructWriteFormat#writeShort
		*/
		public short readShort()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		@see IStructWriteFormat#writeInt
		*/
		public int readInt()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		@see IStructWriteFormat#writeLong
		*/
		public long readLong()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		@see IStructWriteFormat#writeFloat
		*/
		public float readFloat()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		@see IStructWriteFormat#writeDouble
		*/
		public double readDouble()throws IOException;
		
		/*=============================================================
		
			Primitive blocks
			
		=============================================================*/		
		/** Initiates a read of a primitive elements sequence or continues it. 
		
		@param buffer place to store data, can't be null;
		@param offset first byte to write in <code>buffer</code>
		@param length number of elements to read
		@return <code>n</code>, number of read primitive elements:
				<ul>
					<li><code>n==length</code> buffer is filled with data. Whether there are more
					data in sequence or not is not specified;</li>
					<li><code>n&lt;length &amp;&amp; n&gt;0</code> buffer is partially filled with
					data. If due to end-of-file or a signal it is not specified;</li>
					<li><code>n==0</code> returned only only when <code>length==0</code>;</li>
					<li><code>n==-1</code> nothing is read and signal is reached. All subsequent
					calls will return -1 till signal will be read;</li>					
				</ul>
		@throws AssertionError if <code>buffer</code> is null
		@throws AssertionError if <code>offset</code> or <code>length</code> are negative
		@throws AssertionError if <code>buffer.length</code> with <code>offset</code> and <code>length</code>
							   would result in {@link ArrayIndexOutOfBoundsException} exception;
		@throws IOException if low level i/o fails. 
		@throws ESequenceEof accordingly, to indicate a problem.
		@throws EEof of specific subclass depending on <a href="#TEMPEOF">eof handling type</a>
						if could not read <u>any data</u> due to low level end of file.
                        <br>
                        Notice this may not always hold since some implementations will always guard
                        blocks in such way, that even without explict {@link IStructWriteFormat#end} signal the
                        -1 return value will be more appropriate unless the stream is actually physically
                        broken.
		@throws IllegalStateException if a sequence of incompatible type is in progress.
		@see IStructWriteFormat#writeBooleanBlock(boolean[],int,int)
		*/
		public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
		/** See {@link #readBooleanBlock(boolean[],int,int)}, fills whole buffer from zero.
		@param buffer --//--
		@return --//--
 		@throws IOException --//--
		*/
		public default int readBooleanBlock(boolean [] buffer)throws IOException
		{
				assert(buffer!=null);
				return readBooleanBlock(buffer,0,buffer.length);
		}
		/** Behaves as below code dictates:
		<pre>
			final boolean b = new boolean[1];
			final int r = readBooleanBlock(b);
			if (r==-1) throw new ENoMoreData();
			return b[0];
		</pre>
		Subclasses are recommended to implement it in more reasonable way thous no default implementation is provided.
		@return read sequence element
 		@throws IOException see block read
 		@throws ENoMoreData if a block read would return -1.
 		@throws ESequenceEof accordingly to above code
 		@throws EEof accordingly to above code
		*/
		public boolean readBooleanBlock()throws IOException,ENoMoreData;
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		
		@return --//--
 		@throws IOException --//--
 		@see IStructWriteFormat#writeByteBlock(byte[],int,int)
		*/
		public int readByteBlock(byte [] buffer, int offset, int length)throws IOException;		
		/** See {@link #readBooleanBlock(boolean[],int,int)}, fills whole buffer from zero.
		@param buffer --//--		
		@return --//--
 		@throws IOException --//--
		*/
		public default int readByteBlock(byte [] buffer)throws IOException
		{
				assert(buffer!=null);
				return readByteBlock(buffer,0,buffer.length);
		}		
		/** See {@link #readBooleanBlock()}
		@return read sequence element
 		@throws IOException --//--
 		@throws ENoMoreData --//-
		*/
		public byte readByteBlock()throws IOException,ENoMoreData;
		
		
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param characters where to read data to
		@param length --//--		
		@return --//--
 		@throws IOException --//--
 		@see IStructWriteFormat#writeString(CharSequence,int,int)
		*/
		public int readString(Appendable characters,  int length)throws IOException;	
		/** See {@link #readBooleanBlock()}
		@return read sequence element
 		@throws IOException --//--
 		@throws ENoMoreData --//-
		*/
		public char readString()throws IOException,ENoMoreData;
		/** Allocates string buffer and reads into it up to specified number
		of characters.
		@param length size limit
		@return read buffer, up to <code>length</code> long.
		@throws IOException if failed, see {@link #readString(Appendable,int)}.
		*/
		default public String readString(int length)throws IOException
		{
			StringBuilder sb = new StringBuilder(length); 
			readString(sb, length);
			return sb.toString();
		};
		
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--		
		@return --//--
 		@throws IOException --//--
 		@see IStructWriteFormat#writeCharBlock(char[],int,int)
		*/
		public int readCharBlock(char [] buffer, int offset, int length)throws IOException;		
		/** See {@link #readBooleanBlock(boolean[],int,int)}, fills whole buffer from zero.
		@param buffer --//--
		@return --//--
 		@throws IOException --//--
		*/
		public default int readCharBlock(char [] buffer)throws IOException
		{
				assert(buffer!=null);
				return readCharBlock(buffer,0,buffer.length);
		}
		/** See {@link #readBooleanBlock()}
		@return read sequence element
 		@throws IOException --//--
 		@throws ENoMoreData --//-
		*/                                                           
		public char readCharBlock()throws IOException,ENoMoreData;
		
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--		
		@return --//--
 		@throws IOException --//--
 		@see IStructWriteFormat#writeShortBlock(short[],int,int)
		*/
		public int readShortBlock(short [] buffer, int offset, int length)throws IOException;		
		/** See {@link #readBooleanBlock(boolean[],int,int)}, fills whole buffer from zero.
		@param buffer --//--		
		@return --//--
 		@throws IOException --//--
		*/
		public default int readShortBlock(short [] buffer)throws IOException
		{
				assert(buffer!=null);
				return readShortBlock(buffer,0,buffer.length);
		}
		/** See {@link #readBooleanBlock()}
		@return read sequence element
 		@throws IOException --//--
 		@throws ENoMoreData --//-
		*/                                                           
		public short readShortBlock()throws IOException,ENoMoreData;
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--		
		@return --//--
 		@throws IOException --//--
 		@see IStructWriteFormat#writeIntBlock(int[],int,int)
		*/
		public int readIntBlock(int [] buffer, int offset, int length)throws IOException;		
		/** See {@link #readBooleanBlock(boolean[],int,int)}, fills whole buffer from zero.
		@param buffer --//--		
		@return --//--
 		@throws IOException --//--
		*/
		public default int readIntBlock(int [] buffer)throws IOException
		{
				assert(buffer!=null);
				return readIntBlock(buffer,0,buffer.length);
		}
		/** See {@link #readBooleanBlock()}
		@return read sequence element
 		@throws IOException --//--
 		@throws ENoMoreData --//-
		*/                                                           
		public int readIntBlock()throws IOException,ENoMoreData;
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--		
		@return --//--
 		@throws IOException --//--
 		@see IStructWriteFormat#writeLongBlock(long[],int,int)
		*/
		public int readLongBlock(long [] buffer, int offset, int length)throws IOException;		
		/** See {@link #readBooleanBlock(boolean[],int,int)}, fills whole buffer from zero.
		@param buffer --//--		
		@return --//--
 		@throws IOException --//--
		*/
		public default int readLongBlock(long [] buffer)throws IOException
		{
				assert(buffer!=null);
				return readLongBlock(buffer,0,buffer.length);
		}
		/** See {@link #readBooleanBlock()}
		@return read sequence element
 		@throws IOException --//--
 		@throws ENoMoreData --//-
		*/                                                           
		public long readLongBlock()throws IOException,ENoMoreData;
		
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--		
		@return --//--
 		@throws IOException --//--
 		@see IStructWriteFormat#writeFloatBlock(float[],int,int)
		*/
		public int readFloatBlock(float [] buffer, int offset, int length)throws IOException;
		/** See {@link #readBooleanBlock(boolean[],int,int)}, fills whole buffer from zero.
		@param buffer --//--		
		@return --//--
 		@throws IOException --//--
		*/
		public default int readFloatBlock(float [] buffer)throws IOException
		{
				assert(buffer!=null);
				return readFloatBlock(buffer,0,buffer.length);
		}
		/** See {@link #readBooleanBlock()}
		@return read sequence element
 		@throws IOException --//--
 		@throws ENoMoreData --//-
		*/                                                           
		public float readFloatBlock()throws IOException,ENoMoreData;
		
		
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--		
		@return --//--
 		@throws IOException --//--
 		@see IStructWriteFormat#writeDoubleBlock(double[],int,int)
		*/
		public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException;
		/** See {@link #readBooleanBlock(boolean[],int,int)}, fills whole buffer from zero.
		@param buffer --//--		
		@return --//--
 		@throws IOException --//--
		*/
		public default int readDoubleBlock(double [] buffer)throws IOException
		{
				assert(buffer!=null);
				return readDoubleBlock(buffer,0,buffer.length);
		}
		/** See {@link #readBooleanBlock()}
		@return read sequence element
 		@throws IOException --//--
 		@throws ENoMoreData --//-
		*/                                                           
		public double readDoubleBlock()throws IOException,ENoMoreData;
		
		
		
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
			<li>if format is closed, throws {@link EClosed}</li>
		</ul>		 	
		<p>
		Until format is open all methods except
		{@link #close} and defined in {@link IFormatLimits}
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
			<li>if format is not open, closes low level resources without doing anything;</li>
			<li>if format is open closes low level resources.
			<p>
			<i>Note:there is specifically NO request to read and validate closing sequence
			because premature closing is a normal condition.</i>
			</li>
		</ul>		 	
		<p>
		Until format is open all methods except
		{@link #close} and defined in {@link IFormatLimits}
		should throw IOException.  Throwing {@link EClosed} is recommended.
		<p>
		Note: Closed format <u>cannot be opened</u>.
		*/
		public void close()throws IOException;	
};