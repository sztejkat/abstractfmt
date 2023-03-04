package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.EClosed;
import sztejkat.abstractfmt.ENoMoreData;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EEof;
import java.io.DataInput;
import java.io.DataInput;
import java.io.Closeable;
import java.io.IOException;
import java.io.EOFException;

/** 
		An implementation of {@link DataInput}
		which maps all operations to their elementary
		primitive operations in {@link IStructReadFormat}.
		<p>
		This class is paired with {@link CStructDataOutput}.
		<p>
		This class is not thread safe.
		<p>
		<b>Warning:</b> The way this class is implemented
		breaks details of {@link DataInput}. To reliably use
		this contract the kind of methods used to read data
		must match the kind of methods used to write data.
		For an example You can't {@link #skipBytes} through ints.
		
		@see CStructDataOutput
*/
public class CStructDataInput implements DataInput,Closeable
{
			/** Where to write */
			protected final IStructReadFormat in;
			/** State tracker */
			private boolean is_closed;
	/*  *****************************************************
	
		Construction
	
	
	******************************************************/
	/**
		Creates
		@param in a struct format, non null.
			   All operations will be directed 
			   apropriate operations of {@link IStructReadFormat}.
	*/
	public CStructDataInput(IStructReadFormat in)
	{
		assert(in!=null);
		this.in = in;
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
	skipping remaning data.
	@throws IOException if failed
	@see #in
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
	
			DataInput
	
	
	******************************************************/
	/** Calls {@link #readFully(byte[],int,int)} */
	@Override public void readFully(byte[] b)throws IOException
	{
		assert(b!=null);
		readFully(b,0,b.length);
	}
	/** Calls {@link IStructReadFormat#readByte}
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	@throws EEof if reached true end of file. */
	@Override public void readFully(byte[] buffer,int offset,int length)throws IOException
	{
		assert(buffer!=null):"null buffer";
		assert(offset>=0):"offset="+offset;
		assert(length>=0):"length="+length;
		assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
		validateNotClosed();
		while(length-->0)
		{
			try{
				byte b = in.readByte();
				buffer[offset++]=b;
			}catch(ENoMoreData ex){ throw new EOFException(); };
			
		};
	}
	/** Calls {@link IStructReadFormat#readByte} specified number of
	times or until end of file or the signal boundary is reached.
	<p>
	Note: An attempt to skip anything else than data written with
	{@link java.io.DataOutput#write(byte[],int,int)} may result in fatal exceptions.
	*/
	@Override public int skipBytes(int n)throws IOException
	{
		validateNotClosed();
		int skipped = 0;
		while(n-->0)
		{
			try{
				in.readByte();
			}catch(ENoMoreData | EEof ex){ return skipped; }
			skipped++;
		};
		return skipped;
	};
	/** Calls {@link IStructReadFormat#readBoolean} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public boolean readBoolean()throws IOException
	{
		validateNotClosed();
		try{
				return in.readBoolean();
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	/** Calls {@link IStructReadFormat#readByte} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public byte readByte()throws IOException
	{
		validateNotClosed();
		try{
				return in.readByte();
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	/** Calls {@link IStructReadFormat#readByte} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public int readUnsignedByte()throws IOException
	{
		validateNotClosed();
		try{
				return in.readByte() & 0xFF;
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	
	
	/** Calls {@link IStructReadFormat#readShort} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public short readShort()throws IOException
	{
		validateNotClosed();
		try{
				return in.readShort();
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	/** Calls {@link IStructReadFormat#readShort} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public int readUnsignedShort()throws IOException
	{
		validateNotClosed();
		try{
				return in.readShort() & 0xFFFF;
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	
	
	/** Calls {@link IStructReadFormat#readChar} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public char readChar()throws IOException
	{
		validateNotClosed();
		try{
				return in.readChar();
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	
	
	/** Calls {@link IStructReadFormat#readInt} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public int readInt()throws IOException
	{
		validateNotClosed();
		try{
				return in.readInt();
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	
	
	/** Calls {@link IStructReadFormat#readLong} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public long readLong()throws IOException
	{
		validateNotClosed();
		try{
				return in.readLong();
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	
	
	/** Calls {@link IStructReadFormat#readFloat} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public float readFloat()throws IOException
	{
		validateNotClosed();
		try{
				return in.readFloat();
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	
	
	
	/** Calls {@link IStructReadFormat#readDouble} 
	@throws EOFException if reached signal boundary, {@link EEof} if reached real end of file.
	*/
	@Override public double readDouble()throws IOException
	{
		validateNotClosed();
		try{
				return in.readDouble();
		}catch(ENoMoreData ex){ throw new EOFException(); }
	};
	
	/** Implemented over calls to {@link #readByte}
	<p>
	Note: Please be aware that it is an "unbound" operation and may
	read data till memory will be full.
	<p>
	This implementation do treat \n and \r characters the same way
	what is <u>different</u> than contract do require. If You need
	to strictly obey to {@link DataInput} contract use <code>DataInputStream</code>
	laid over {@link CStructInputStream}.
	*/
	@Override public String readLine()throws IOException
	{
		validateNotClosed();
		StringBuilder sb = new StringBuilder();
		if (!in.hasElementaryData()) return null; //as contract says: null if nothing could be read.
		for(;;)
		{
			if (!in.hasElementaryData()) return sb.toString();
			byte b = in.readByte();
			switch(b)
			{
				case '\n': return sb.toString();
				case '\r': 
						//Note: implementing it as the contract says
						//is trickier than You may thing. We need to
						//do a look ahead and then un-read the character
						//if it is not the \n.
						//Unfortunately this can be implemented correctly
						// __only__ if we would operated on raw byte streams.
						return sb.toString();
				default: sb.append((char)( b & 0xFF));
			};
		}
	}
	/** Uses the string block operation {@link IStructReadFormat#readString(int)}
	with 65536 limit to fetch data. Then skips touched "end" signal.
	@throws EBrokenFormat if not at anonymous "begin" signal, or string is longer than 64k or
			next signal is not an "end" signal.
	*/
	@Override public String readUTF()throws IOException
	{
		validateNotClosed();
		if (in.hasElementaryData()) throw new EBrokenFormat("Can't do that, some non-utf data in stream at the cursor");
		if (!"".equals(in.next()))throw new EBrokenFormat("Some unexpected signal in stream");
		final String v = in.readString(65536);
		if (in.hasElementaryData()) throw new EBrokenFormat("String longer than 64k");
		if (null!=in.next())throw new EBrokenFormat("Expected end signal after utf, but begin found.");
		return v;
	};
}