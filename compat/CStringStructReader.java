package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.EClosed;
import java.io.Reader;
import java.io.IOException;


/**
	An {@link Reader} laid over a string sequence
	of {@link IStructReadFormat}.
	<p>
	Opposite to superclass is strictly declared asn
	<u>NOT thread safe</u> and is using a common 
	shared buffer to implement some operations.
*/
public class CStringStructReader extends Reader
{
			/** Underlying format */
			private final IStructReadFormat in;
			/** State tracker */
			private boolean is_closed;
			/** buffer for implementing {@link #read} */
			private StringBuilder temp = new StringBuilder(1);
			
	/*  *****************************************************
		
			Construction
		
		
		******************************************************/
	/** Creates
	@param in non null format on which it will be laid over.
		The cursor in format should be either in front of
		or inside the byte sequence available through
		{@link IStructReadFormat#readString} family of
		methods.
	*/
	public CStringStructReader(IStructReadFormat in)
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
	/*  *****************************************************
		
			Reader
		
			
	******************************************************/
	/**
		Makes this object unusable. Doesn't do 
		anything with an underlying format.
	*/
	@Override public void close()throws IOException
	{
		is_closed = true;
	};
	/** Always false. */
	@Override public final boolean markSupported(){ return false; };
	
	@Override public int read()throws IOException
	{
         validateNotClosed();
         int r = in.readString(temp,1);
         return r==-1 ? -1 : temp.charAt(0);
    };
	@Override public int read(char[] buffer,
                         	 int offset,
                         	 int length)throws IOException
    {
    	assert(buffer!=null):"null buffer";
		assert(offset>=0):"offset="+offset;
		assert(length>=0):"length="+length;
		assert(buffer.length>=offset+length):"Out of buffer operation: buffer.length="+buffer.length+", offset="+offset+", length="+length;
		validateNotClosed();
		int readen = 0;
		while(length-->0)
		{
			int r= read();
			if (r==-1) break;
			buffer[offset++]=(char)r;
			readen++;
		}
		return readen==0 ? -1 : readen;
    };
    /** Always false */
    @Override public boolean ready()throws IOException{ return false; };
}	