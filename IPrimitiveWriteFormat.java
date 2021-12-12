package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
/**
	A generic contract for writing primitives.
	<p>
	This interface is used just to save typing.
	Specific uses of this interface may declare different
	requirements and restrictions. This is assumed that
	they do explain it in {@link #writeBoolean} and {@link #writeBooleanBlock}.
	<p>
	All the <code>writeXXXX</code> behave as thier boolean counterparts. 
*/
public interface IPrimitiveWriteFormat
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
		
		/** An elementary primitive write.
		@param v value to write
		@throws IOException if low level i/o fails.
		*/
		public void writeBoolean(boolean v)throws IOException;
		/** See {@link #writeBoolean(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
		public void writeByte(byte v)throws IOException;
		/** See {@link #writeBoolean(boolean)}
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
		/** See {@link #writeBoolean(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
		public void writeFloat(float v)throws IOException;
		/** See {@link #writeBoolean(boolean)}
		@param v --//--
		@throws IOException --//--
		*/
		public void writeDouble(double v)throws IOException;
		
		/*=============================================================
		
			Primitive blocks
			
		=============================================================*/		
		/** Writes a part of a primitive data block.
		
		@param buffer source of data, non-null.
		@param offset first byte to write in <code>buffer</code>
		@param length number of bytes to write
		@throws AssertionError if <code>buffer</code> is null
		@throws AssertionError if <code>offset</code> or <code>length</code> are negative
		@throws AssertionError if <code>buffer.length</code> with <code>offset</code> and <code>length</code>
							   would result in {@link ArrayIndexOutOfBoundsException} exception;
		@throws IOException if low level i/o fails.
		*/
		public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@throws IOException --//--
		*/
		public default void writeBooleanBlock(boolean [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeBooleanBlock(buffer,0,buffer.length);
		}
		
		
		
		
		/**  See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException;		
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@throws IOException --//--
		*/
		public default void writeByteBlock(byte [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeByteBlock(buffer,0,buffer.length);
		}		
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param data single byte to write
		@throws IOException --//--
		*/
		public void writeByteBlock(byte data)throws IOException;
		
		
		
		
		/**  See {@link #writeBooleanBlock(boolean[],int,int)}
		@param characters --//--
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		public void writeCharBlock(CharSequence characters, int offset, int length)throws IOException;		
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param characters --//--
		@throws IOException --//--
		*/
		public default void writeCharBlock(CharSequence characters)throws IOException
		{
				assert(characters!=null);
				writeCharBlock(characters,0,characters.length());
		}
		/**  See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		public void writeCharBlock(char [] buffer, int offset, int length)throws IOException;		
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@throws IOException --//--
		*/
		public default void writeCharBlock(char [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeCharBlock(buffer,0,buffer.length);
		}
		
		
		
		
		/**  See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		public void writeShortBlock(short [] buffer, int offset, int length)throws IOException;		
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@throws IOException --//--
		*/
		public default void writeShortBlock(short [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeShortBlock(buffer,0,buffer.length);
		}
		
		
		
		/**  See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		public void writeIntBlock(int [] buffer, int offset, int length)throws IOException;		
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@throws IOException --//--
		*/
		public default void writeIntBlock(int [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeIntBlock(buffer,0,buffer.length);
		}
		
		
		
		/**  See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		public void writeLongBlock(long [] buffer, int offset, int length)throws IOException;		
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@throws IOException --//--
		*/
		public default void writeLongBlock(long [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeLongBlock(buffer,0,buffer.length);
		}
		
		
		
		
		/**  See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException;
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@throws IOException --//--
		*/
		public default void writeFloatBlock(float [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeFloatBlock(buffer,0,buffer.length);
		}
		
		
		
		/**  See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@param offset --//--
		@param length --//--
		@throws IOException --//--
		*/
		public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException;
		/** See {@link #writeBooleanBlock(boolean[],int,int)}
		@param buffer --//--
		@throws IOException --//--
		*/
		public default void writeDoubleBlock(double [] buffer)throws IOException
		{
				assert(buffer!=null);
				writeDoubleBlock(buffer,0,buffer.length);
		}
};