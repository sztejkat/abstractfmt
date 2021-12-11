package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;
/**
	A generic contract for writing primitives.
	<p>
	This interface is used just to save typing.
	Specific uses of this interface may declare different
	requirements and restrictions. This is assumed that
	they do explain it in {@link #readBoolean} and {@link #readBooleanBlock}.
	<p>
	All the <code>readXXXX</code> behave as thier boolean counterparts. 
*/
public interface IPrimitiveReadFormat extends Closeable
{
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
		@throws IOException if low level i/o fails.
		*/
		public boolean readBoolean()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		*/
		public byte readByte()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		*/
		public char readChar()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		*/
		public short readShort()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		*/
		public int readInt()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		*/
		public long readLong()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		*/
		public float readFloat()throws IOException;
		/** See {@link #readBoolean()}
		@return --//--
		@throws IOException --//--
		*/
		public double readDouble()throws IOException;
		
		/*=============================================================
		
			Primitive blocks
			
		=============================================================*/		
		/** Reads a part of a primitive data block.
		
		@param buffer place to store data, non-null.
		@param offset first byte to write in <code>buffer</code>
		@param length number of bytes to read
		@return number of read primitives, can return a partial read if there is no
				data.
		@throws AssertionError if <code>buffer</code> is null
		@throws AssertionError if <code>offset</code> or <code>length</code> are negative
		@throws AssertionError if <code>buffer.length</code> with <code>offset</code> and <code>length</code>
							   would result in {@link ArrayIndexOutOfBoundsException} exception;
		@throws IOException if low level i/o fails.
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
		@return single byte 0...0xFF or -1 if there was no data.
 		@throws IOException --//--
		*/
		public int readByteBlock()throws IOException;
		
		
		
		
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param characters --//--
		@param offset --//--
		@param length --//--		
		@return --//--
 		@throws IOException --//--
		*/
		public int readCharBlock(CharSequence characters, int offset, int length)throws IOException;		
		/** See {@link #readBooleanBlock(boolean[],int,int)}
		@param s --//--	
		@return --//--
 		@throws IOException --//--
		*/
		public default int readCharBlock(CharSequence s)throws IOException
		{
				assert(s!=null);
				return readCharBlock(s,0,s.length());
		}
		/**  See {@link #readBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--		
		@return --//--
 		@throws IOException --//--
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
};