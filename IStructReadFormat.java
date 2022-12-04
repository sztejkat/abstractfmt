package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;
/**
	Defines contract for abstract event based format support 
	as specified in <a href="package-summary.html">package	description</a>.
	<p>
	This is a reading end of a format.
	
	<h1 id="TEMPEOF">Support for a temporary lack of data</h1>
	Implementations must specify how do they behave in 
	case when low-level I/O returns with an "End-of-file" condition.
	<p>
	This support may be:
	<table border="1">
	<caption>Eof handling support types</caption>
	<tr>
		<td><b>Support type</b></td>
		<td><b>{@link #next()}</b></td>
		<td><b>elementary reads</b></td>
		<td><b>sequence reads</b></td>		
	</tr>
	<tr>
		<td><b>None</b></td>
		<td>throws {@link EUnexpectedEof}, the effect of future use of stream is unpredicatable;</td>
		<td>throws {@link EUnexpectedEof}, the effect of future use of stream is unpredicatable;</td>
		<td>returns with a partial read or throws {@link EUnexpectedEof}
		if could not read any data. If thrown, the effect of future use of stream is unpredicatable;</td>
	</tr>
	<tr>
		<td><b>Frame</b></td>
		<td>If structure recursion level is zero it throws {@link ETemporaryEndOfFile} and allows 
		operation to be re-tried to check if next signal did appear.
		<br>
		If structure recursion level is higher behaves as "None";</td>
		<td>as "None";</td>
		<td>as "None"</td>
	</tr>
	<tr>
		<td><b>Signal</b></td>
		<td>throws {@link ETemporaryEndOfFile} and allows 
		operation to be re-tried to check if next signal did appear;</td>
		<td>as "None";</td>
		<td>as "None"</td>
	</tr>
	<tr>
		<td><b>Full</b></td>
		<td>throws {@link ETemporaryEndOfFile} and allows 
		operation to be re-tried to check if next signal did appear;</td>
		<td>throws {@link ETemporaryEndOfFile} and allows operation
		to re-try reading this exact primitive element again using this exact
		method. Any partially read element must not be discarded and must be available
		for subsequent reads;</td>
		<td>returns with a partial read or throws {@link ETemporaryEndOfFile}
		if could not read any data. Subsequent calls to the same block
		read are allowed to try to read newly incomming data. 
		Any partially read element must not be discarded and must be available
		for subsequent reads;</td>
	</tr>
	</table>
	<p>
	<i>Note: File-based stream or stream wrapped in carrier protocols
	which do warrant the delivery will use "None" model since it does not
	have any benefits from using other eof-support models.</i>
	<p>
	<i>The low-level direct hardware connection	streams which decided to use this format
	as <u>their own protocol</u> will need "Frame" model to allow for infinite silence 
	between frames and to be able to detect lack of response from remote party.</i>
	<p>
	<i>"Signal" and "Full" models will be rarely needed. Implementing the "Full" model
	is especially cumbersome and tricky	and thous not recommended.</i>
	
	<h1>Thread safety</h1>
	Formats are <u>not thread safe</u>.	Yes, this is intentional. Check package description
	for detailed explanation.
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
		*/
		public String next()throws IOException;
		
		
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
		@return number of levels it has left to skip. This value
			can be non-zero only for <a href="#TEMPEOF">eof models
			different than "None"</a> and should be used to re-try this
			operation after an temporary end of file condition.
		@throws IOException if low level i/o failed or an appropriate
			subclass to represent encountered problem.
			This method never throws {@link ETemporaryEndOfFile} because
			default implementation depends on it to be thrown
			by {@link #next} in which case the exception 
			is intercepted and converted to non-zero return value.
		@throws EBrokenFormat if broken permanently
		*/ 
		public default int skip(int levels)throws IOException
		{
			int depth = levels+1;
			String s;
			do{
				try{
						//Note: we need to handle temporary EOF to 
						//let this operation to be re-tried.
						s=next();
					}catch( ETemporaryEndOfFile ex) 
					{
					 	break;
					};
				if (s!=null)
				{
					depth++;
				}else
				{
					depth--;
				};
			}while(depth!=0);
			return depth;
		};
		
		/** An equivalent of <code>skip(0)</code>. 
		Should <u>not</u> be used with streams supporting
		<a href="#TEMPEOF">eof models different than "None"</a>
		because in such case the information necessary to re-try
		the operation is lost.  
		@throws IOException if {@link #skip(int)} failed
		@throws EEof if {@link #skip(int)} returned
		non zero, since re-trying in this case is impossible.
		*/ 
		public default void skip()throws IOException
		{
			 if (skip(0)!=0) throw new EEof();  
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
					<li><code>n==0</code>, possible only only when <code>length==0</code>;</li>
					<li><code>n==-1</code> nothing is read and signal is reached. All subsequent
					calls will return -1 till signal will be read;</li>					
				</ul>
		@throws AssertionError if <code>buffer</code> is null
		@throws AssertionError if <code>offset</code> or <code>length</code> are negative
		@throws AssertionError if <code>buffer.length</code> with <code>offset</code> and <code>length</code>
							   would result in {@link ArrayIndexOutOfBoundsException} exception;
		@throws IOException if low level i/o fails. 
		@throws ESequenceEof accordingly, to indiate a problem.
		@throws EEof of specific subclass depending on <a href="#TEMPEOF">eof handling type</a>
			if could not read <u>any data</u> due to low level end of file.
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
			final int r = readBoolean(b);
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