package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.EClosed;
import sztejkat.abstractfmt.EEof;
import java.io.InputStream;
import java.io.IOException;


/**
	An {@link InputStream} laid over a byte sequence
	of {@link IStructReadFormat}.
	<p>
	Opposite to superclass is strictly declared as
	<u>NOT thread safe</u>. 
	Uses internal shared data to implement some methods.
*/
public class CStructInputStream extends InputStream
{
			/** Underlying format */
			protected final IStructReadFormat in;
			/** State tracker */
			private boolean is_closed;
			/** buffer for implementing {@link #read} */
			private byte [] temp = new byte[1];
			
	/*  *****************************************************
		
			Construction
		
		
		******************************************************/
	/** Creates
	@param in non null format on which it will be laid over.
		The cursor in format should be either in front of
		or inside the byte sequence available through
		{@link IStructReadFormat#readByteBlock} family of
		methods.
	*/
	public CStructInputStream(IStructReadFormat in)
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
	/*  *****************************************************
		
			InputStream
		
			
	******************************************************/
	/**
		Returns zero.
		<p>
		Note: InputStream contract strictly says about 
		"will not block". The struct format on the other
		hand provides only some information about
		"will not fail" via {@link IStructReadFormat#hasElementaryData}
		but doesn't say anything about blocking.
		<p>
		Most of implementations will however have to make some
		read-ahead in response to {@link IStructReadFormat#hasElementaryData}
		and will, as a side effect, ensure that "will not fail"=="will not block".
		Since this is not always warranted this method was made
		to return zero regardless of {@link IStructReadFormat#hasElementaryData}.
		
		@throws EClosed if {@link #close} was called.
	*/
	@Override public int available()throws IOException
	{
		validateNotClosed();
		return 0;
	};
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
	/** Always false. */
	@Override public final boolean markSupported(){ return false; };
	
	/** Redirects to {@link IStructReadFormat#readByteBlock(byte [],int,int)} 
	without any additional processing. */
	@Override public int read(byte[] b,
                				int off,
                				int len)
         						throws IOException
	{
		validateNotClosed();
		try{
			return in.readByteBlock(b,off,len);
		}catch(EEof ex){ return -1; } //since InputStream should return -1 while we are allowed to
									   //throw on physical eof when we read zero data.
	};
	/** Redirects to {@link IStructReadFormat#readByteBlock(byte [],int,int)}
	using a shared one byte long temporary array.
	*/
	@Override public int read()throws IOException
	{
		validateNotClosed();
		try{
			int r = in.readByteBlock(temp);
			return r==-1  ? -1 : (temp[0] & 0xFF);
		}catch(EEof ex){ return -1; } //since InputStream should return -1 while we are allowed to
									   //throw on physical eof when we read zero data.
	};
}