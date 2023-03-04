package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EClosed;
import java.io.DataOutput;
import java.io.DataInput;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.UTFDataFormatException;


/** 
		An implementation of {@link DataOutput}
		which maps all operations to their elementary
		primitive operations in {@link IStructWriteFormat}.
		<p>
		This class is paired with {@link CStructDataInput}.
		<p>
		This class is not thread safe.
		<p>
		<b>Warning:</b> The way this class is implemented
		breaks details of {@link DataOutput}. Produced 
		stream won't be a raw binary one.
		
		@see CStructDataInput
*/
public class CStructDataOutput implements DataOutput,Closeable,Flushable
{
			/** Where to write */
			protected final IStructWriteFormat out;
			/** State tracker */
			private boolean is_closed;
	/*  *****************************************************
	
		Construction
	
	
	******************************************************/
	/**
		Creates
		@param out a struct format, non null.
			   All operations will be directed 
			   apropriate operations of {@link IStructWriteFormat}
	*/
	public CStructDataOutput(IStructWriteFormat out)
	{
		assert(out!=null);
		this.out = out;
	};
	/*  *****************************************************
		
			Support services
		
		
	******************************************************/
	/** State tracker.
	@return true if {@link #close} was run at least once */
	protected final boolean isClosed(){ return is_closed; };
	/** State validator
	@throws EClosed if {@link #isClosed} gives true. */
	protected final void validateNotClosed()throws EClosed
	{
		if (is_closed) throw new EClosed();
	};
	/** Invoked at first call to {@link #close}.
	Subclasses may override it to perform additional
	operations during close, like for an example
	writing "end" signal
	@throws IOException if failed
	@see #out
	*/
	protected void closeImpl()throws IOException{};
	/* *****************************************************
	
			Closeable
	
	
	******************************************************/
	/**
		Makes this object unusable.  
	*/
	@Override public void close()throws IOException
	{
		//Intentionally: no flushing!
		if (!is_closed)
		{
			try{
				closeImpl();
			}finally{ is_closed = true; }
		};
	};
	/* *****************************************************
	
			Flushable
	
	
	******************************************************/
	/** Invokes the downstream flush.
	Notice, this method is <u>not</u> invoked during
	{@link #close} because it is not necessary and because
	flushing may have side effects on a downstream . */
	@Override public void flush()throws IOException
	{
		validateNotClosed();
		out.flush();
	};
	/* *****************************************************
	
			DataOutput
	
	
	******************************************************/
	/** Calls {@link IStructWriteFormat#writeByte} */
	@Override public void write(int b)throws IOException
	{
		validateNotClosed();
		out.writeByte((byte)b);
	};
	/** Calls {@link #write(byte[],int,int)} */
	@Override public void write(byte[] b)throws IOException
	{
		assert(b!=null);
		write(b,0,b.length);
	}
	/** Calls {@link IStructWriteFormat#writeByte} in a loop.
	<p>
	<i>Note: The {@link DataOutput} contract do allow mixing block and
	elementary writes in any order so we can't use struct format 
	block operation in here</i>.
	*/
	@Override public void write(byte[] buffer,int offset,int length)throws IOException
	{
		assert(buffer!=null):"null buffer";
		assert(offset>=0):"offset="+offset;
		assert(length>=0):"length="+length;
		assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
		validateNotClosed();
		while(length-->0)
		{
			out.writeByte(buffer[offset++]);
		};
	};
	/** Calls {@link IStructWriteFormat#writeBoolean} */
	@Override public void writeBoolean(boolean v)throws IOException
	{
		validateNotClosed();
		out.writeBoolean(v);
	}
	/** Calls {@link IStructWriteFormat#writeByte} */
	@Override public void writeByte(int v)throws IOException
	{
		validateNotClosed();
		out.writeByte((byte)v);
	}
	/** Calls {@link IStructWriteFormat#writeShort} */
	@Override public void writeShort(int v)throws IOException
	{
		validateNotClosed();
		out.writeShort((short)v);
	}
	/** Calls {@link IStructWriteFormat#writeChar} */
	@Override public void writeChar(int v)throws IOException
	{
		validateNotClosed();
		out.writeChar((char)v);
	}
	/** Calls {@link IStructWriteFormat#writeInt} */        
	@Override public void writeInt(int v)throws IOException
	{
		validateNotClosed();
		out.writeInt(v);
	}
	/** Calls {@link IStructWriteFormat#writeLong} */
	@Override public void writeLong(long v)throws IOException
	{
		validateNotClosed();
		out.writeLong(v);
	}
	/** Calls {@link IStructWriteFormat#writeFloat} */
	@Override public void writeFloat(float v)throws IOException
	{
		validateNotClosed();
		out.writeFloat(v);
	}
	/** Calls {@link IStructWriteFormat#writeDouble} */
	@Override public void writeDouble(double v)throws IOException
	{
		validateNotClosed();
		out.writeDouble(v);
	}
	/** Calls {@link IStructWriteFormat#writeByte} in a loop.
	<p>
	Notice there is no matching reading API declared in
	{@link DataInput}.
	*/
	@Override public void writeBytes(String s)throws IOException
	{
		validateNotClosed();
		for(int i=0,n=s.length();i<n;i++)
			out.writeByte((byte)s.charAt(i));
	}
	/** Calls {@link IStructWriteFormat#writeChar} in a loop.
	<p>
	Notice there is no matching reading API declared in
	{@link DataInput}.
	*/
	@Override public void writeChars(String s)throws IOException
	{
		validateNotClosed();
		for(int i=0,n=s.length();i<n;i++)
			out.writeChar(s.charAt(i));
	}
	/** Writes anonymous begin signal, uses 
	{@link IStructWriteFormat#writeString} and writes end signal.
	<p>
	<i>Note: We do treat this method differently from 
	{@link #write(byte[])} because the {@link DataOutput} contract
	do warrant that single call to this method must map to 
	single call to {@link DataInput#readUTF} and no other methods
	can be <u>reasonably</u> used to read data stored by 
	<code>writeUTF</code>.</i>
	@throws UTFDataFormatException if s is longer than 65536 characters.
	*/
	@Override public void writeUTF(String s)throws IOException
	{
		assert(s!=null);
		if (s.length()>65536) throw new UTFDataFormatException("String longer than 64k characters");
		validateNotClosed();
		out.begin("");
		out.writeString(s);
		out.end();
	}
};