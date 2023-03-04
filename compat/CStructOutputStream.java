package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.EClosed;
import java.io.OutputStream;
import java.io.IOException;


/**
	An {@link OutputStream} laid over a byte sequence
	of {@link IStructWriteFormat}.
	<p>
	Opposite to superclass is strictly declared as
	<u>NOT thread safe</u>. 
*/
public class CStructOutputStream extends OutputStream
{
			/** Where to write */
			private final IStructWriteFormat out;
			/** State tracker */
			private boolean is_closed;
	/*  *****************************************************
	
		Construction
	
	
	******************************************************/
	/**
		Creates
		@param out a struct format, non null.
			   All operations will be directed to
			   {@link IStructWriteFormat#writeByteBlock}
			   and closing the stream will write the 
			   "end signal" to a stream.
	*/
	public CStructOutputStream(IStructWriteFormat out)
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
	/* *****************************************************
	
			OutputStream
	
	
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
	@Override public void write(int b)throws IOException
	{
		validateNotClosed();
		out.writeByteBlock((byte)b);
	}
	/** {@inheritDoc}
	<p>
	Note: This method throws {@link AssertionError} instead of 
	{@link NullPointerException} and {@link ArrayIndexOutOfBoundsException} if
	assertions are enabled. */
	@Override public void write(byte[] b,int off,int len)throws IOException
	{
		validateNotClosed();
		out.writeByteBlock(b,off,len);
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
};