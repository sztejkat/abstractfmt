package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;
/**
	Defines contract for abstract event based format support 
	as specified in <a href="package-summary.html">package	description</a>.
	<p>
	This is a reading end of a format.
	
	<h1 id="TEMPEOF">Support for a temporary lack of data</h1>
	Implementations must specify they support for 
	case when low-level I/O returnes End-of-file condition.
	<p>
	This support may be:
	<table border="1">
	<caption>Eof handling support model</caption>
	<tr>
		<td>Support model</td>
		<td>{@link #next()}</td>
		<td>elementary reads</td>
		<td>sequence reads</td>		
	</tr>
	<tr>
		<td>None</td>
		<td>throws {@link EUnexpectedEof};</td>
		<td>throws {@link EUnexpectedEof};</td>
		<td>returns with a partial read or throws {@link EUnexpectedEof}
		if could not read any data;</td>
	</tr>
	<tr>
		<td>Frame</td>
		<td>If structure recursion level is zero it throws {@link ETemporaryEndOfFile} and allows 
		operation to be re-tried to check if next signal did appear.
		<br>
		If structure recursion level is higher behaves as "None";</td>
		<td>as "None";</td>
		<td>as "None"</td>
	</tr>
	<tr>
		<td>Signal</td>
		<td>throws {@link ETemporaryEndOfFile} and allows 
		operation to be re-tried to check if next signal did appear;</td>
		<td>as "None";</td>
		<td>as "None"</td>
	</tr>
	<tr>
		<td>Full</td>
		<td>throws {@link ETemporaryEndOfFile} and allows 
		operation to be re-tried to check if next signal did appear;</td>
		<td>throws {@link ETemporaryEndOfFile} and allows operation
		to re-try reading this primitive element again. Notice reading
		other primitive element is not allowed;</td>
		<td>returns with a partial read or throws {@link ETemporaryEndOfFile}
		if could not read any data. Subsequent calls to the same block
		read are allowed to try to read newly incomming data;</td>
	</tr>
	</table>
	<p>
	Note: Usuall file stream or stream wrapped in carrier protocols
	will use "None" model. Only low-level direct hardware connection
	streams which decided to base their protocol directly on the 
	structure format will need to support "Frame" model since only 
	in that context a <i>timeout</i> condition indicates the end of protocol
	frame or the lack of any protocol frame on the buss.
	<p>
	"Signal" and "Full" models will be rarely needed. 
	
	<h1>Thread safety</h1>
	Format are <u>not thread safe</u>.	
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
		@throws IOException if failed due to any reason. A dedicated subclass should
				be used, especially according to <a href="#TEMPEOF">this description</a>.
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
		@param levels how many levels upwards to skip.
		@return number of levels it has left to skip. This value
			can be non-zero only for <a href="#TEMPEOF">eof models
			different than "None"</a> and can be used to re-try this
			operation after an temporary end of file condition.
		@throws IOException if low level i/o failed or an appropriate
			subclass to represent encountered problem.
			This method never throws {@link ETemporaryEndOfFile} because
			it is intercepted and converted to non-zero return value.
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
		@throws IOException if fails due to many reasons. See also <a href="#TEMPEOF">there</a>.
		@throws ENoMoreData if stream cursor is at the signal and there is no
				data to initiate operation.			
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
		/** Reads a part of a primitive data block.
		<h3>End-of-sequence vs temporary lack of data</h3>
		The partial read condition may be returned both due to 
		temporary lack of data or reaching a signal terminating the
		sequence.
		<p>
		The only difference is in how it behaves if it could not
		read <u>any data at all</u> (would return 0):
		<ul>
			<li>it returns -1 if signal is reached or;</li>
			<li>it throws, as described in <a href="#TEMPEOF">there</a>;</li>
		</ul>
		
		@param buffer place to store data, can't be null;
		@param offset first byte to write in <code>buffer</code>
		@param length number of bytes to read
		@return number of read primitives, can return a partial read if there is no
				data or -1 did not read any data at all due to end-of-sequence. 
		@throws AssertionError if <code>buffer</code> is null
		@throws AssertionError if <code>offset</code> or <code>length</code> are negative
		@throws AssertionError if <code>buffer.length</code> with <code>offset</code> and <code>length</code>
							   would result in {@link ArrayIndexOutOfBoundsException} exception;
		@throws IOException if low level i/o fails. See also <a href="#TEMPEOF">there</a>.
		@throws ESequenceEof accordingly
		@throws IllegalStateException if a sequence of incompatible type is in progress.
		@see IStructWriteFormat#writeBooleanBlock(boolean[],int,int)
		*/
		public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
		/** See {@link #readBooleanBlock(boolean[],int,int)}
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
		Subclasses are recommended to implement it in more reasonable way, thous no default implementation is provided.
		@return read sequence element
 		@throws IOException see block read
 		@throws ENoMoreData if a block read would return -1.
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
		/** See {@link #readBooleanBlock(boolean[],int,int)}
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
		/** See {@link #readBooleanBlock(boolean[],int,int)}
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
		/** See {@link #readBooleanBlock(boolean[],int,int)}
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
		/** See {@link #readBooleanBlock(boolean[],int,int)}
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
		/** See {@link #readBooleanBlock(boolean[],int,int)}
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
		/** See {@link #readBooleanBlock(boolean[],int,int)}
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
		/** See {@link #readBooleanBlock(boolean[],int,int)}
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