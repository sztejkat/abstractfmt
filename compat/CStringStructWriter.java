package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EClosed;
import java.io.Writer;
import java.io.IOException;


/**
	An implementation of {@link Writer} over {@link IStructWriteFormat}
	string blocks.
	<p>
	Instances of this class are not thread safe.
*/
public class CStringStructWriter extends Writer
{
	/* Design note:
		In my opinion decission that java.io.Writer has a "lock" field
		was an example of bad design.
	*/
	
			/** Where to write */
			private final IStructWriteFormat out;
			/** State tracker */
			private boolean is_closed;
	/* ********************************************************************
	
	
			Construction
	
	
	*********************************************************************/
	/**
		Creates. 
		@param out a struct format, non null.
			   All operations will be directed to
			   {@link IStructWriteFormat#writeString}
			   and closing the stream will write the 
			   "end signal" to a stream.
	*/
	public CStringStructWriter(IStructWriteFormat out)
	{
		super();
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
	/*  *****************************************************
		
			Writer
		
	Note: This is unspecified if standard Writer implementation
		  will go through append() or write(char [] ) 
		  They saying "append(... will behave as write(String...)"
		  is not equal to saying "append will invoke write(String ...")".
		  Considering the historical reasons (append was added later)
		  it should be that way, but we can't rely on it.
		  
	******************************************************/
	/**
		Makes this object unusable. If called for 
		a first time writes "end signal". 
	*/
	@Override public void close()throws IOException
	{
		if (!is_closed)
		{
			//Intentionally: no flushing!
			is_closed = true;
			out.end();
		};
	};
	/** Invokes the downstream flush.
	Notice, this method is <u>not</u> invoked during
	{@link #close} because it is not necessary and because
	flushing may have side effects on a downstream . */
	@Override public void flush()throws IOException
	{
		validateNotClosed();
		out.flush();
	};
	@Override public void write(char[] buffer, int offset, int length)throws IOException
	{
		//Note: we need to use String API which, for clarity of use
		//		does not provide the char[] API.
		assert(buffer!=null):"null buffer";
		assert(offset>=0):"offset="+offset;
		assert(length>=0):"length="+length;
		assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
		validateNotClosed();
		while(length-->0)
		{
			out.writeString(buffer[offset++]);
		}
	}
	@Override public void write(int character)throws IOException
	{
		validateNotClosed();
		out.writeString((char)character);
	};
	@Override public void write(String str, int off, int len)throws IOException
	{
		
		validateNotClosed();
		out.writeString(str,off,len);
	}
	@Override public void write(String str)throws IOException
	{		
		validateNotClosed();
		out.writeString(str);
	}
	@Override public Writer append(char c)throws IOException
	{
		validateNotClosed();
		out.writeString(c);
		return this;
	};
	@Override public Writer append(CharSequence csq, int start, int end)throws IOException
	{
		validateNotClosed();
		out.writeString(csq, start, end-start);
		return this;
	};
};