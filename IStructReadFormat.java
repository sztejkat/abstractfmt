package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;
/**
	Defines contract for abstract event based format support 
	as specified in <a href="package-summary.html">package	description</a>.
	<p>
	This is a reading end of a format.
	
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
		@throws IOException if failed due to any reason.
		@throws EFormatBoundaryExceeded if either name is longer than
			{@link IFormatLimits#getMaxSignalNameLength} or 
			recursion is too deep (see {@link IFormatLimits#setMaxStructRecursionDepth})
		@throws EBrokenFormat if broken permanently
		*/
		public String next()throws IOException;
		
		
		/** Skips all remaining primitives and all nested events until
		it will read end signal for current event.
		<p>
		This method is to be used when You have read a "begin" signal,
		figured out that You are not interested at all in what is in it,
		regardless how many prmitives and events is inside
		and You need to move to after the end of it.
		<pre>
			A{
			      -- here call skip
				B{
					C
				}
				-- here call skip
				D{		}
				E{		}
			  }
			F{	  -- this will be returned by nearest {@link #next}
			}
		</pre>
		
		@throws IOException if low level i/o failed or an appropriate
		subclass to represent encountered problem.
		@throws EBrokenFormat if broken permanently
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
		<p>
		<i>Note:The exact behaviour on end-of-file condition and I/O exceptions
		is implementation specific and classes which implement this must describe it.</i>
		
		@return value read from stream.
		@throws IOException if fails due to many reasons.
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
		<p>
		<i>Note:The exact behaviour on end-of-file condition and I/O exceptions
		is implementation specific and classes which implement this must describe it.</i>
		@param buffer place to store data, non-null.
		@param offset first byte to write in <code>buffer</code>
		@param length number of bytes to read
		@return number of read primitives, can return a partial read if there is no
				data. Especially if <code>length</code> is zero it returns:
				<ul>
					<li>0 if there is no more data due to temporary lack of data at
					low level;</li>
					<li>-1 if there is no more data due ot end of sequence reached;</li>
				</ul>
		@throws AssertionError if <code>buffer</code> is null
		@throws AssertionError if <code>offset</code> or <code>length</code> are negative
		@throws AssertionError if <code>buffer.length</code> with <code>offset</code> and <code>length</code>
							   would result in {@link ArrayIndexOutOfBoundsException} exception;
		@throws IOException if low level i/o fails.
		@throws ENoMoreData if signal appears to be inside a primitive entity (ie. single boolean) 
							preventing a read.		
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
		/** See {@link #readBooleanBlock(boolean[],int,int)}
		@return single byte 0...0xFF or -1 if there was no data due to
			end of sequence reached and -2 if there was no more data 
			due to temporary end lack of data at low level.
 		@throws IOException --//--
		*/
		public int readByteBlock()throws IOException;
		
		
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param characters where to read data to
		@param length --//--		
		@return --//--
 		@throws IOException --//--
 		@see IStructWriteFormat#writeString(CharSequence,int,int)
		*/
		public int readString(Appendable characters,  int length)throws IOException;		
		
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
		*/
		public void close()throws IOException;	
};