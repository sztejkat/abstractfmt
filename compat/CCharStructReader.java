package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.EClosed;
import java.io.Reader;
import java.io.IOException;


/**
	An {@link Reader} laid over a char sequence
	of {@link IStructReadFormat}.
	<p>
	Opposite to superclass is strictly declared asn
	<u>NOT thread safe</u> and is using a common 
	shared buffer to implement some operations.
*/
public class CCharStructReader extends Reader
{
			/** Underlying format */
			private final IStructReadFormat in;
			/** State tracker */
			private boolean is_closed;
			/** buffer for implementing {@link #read} */
			private char [] temp = new char[1];
			
	/*  *****************************************************
		
			Construction
		
		
		******************************************************/
	/** Creates
	@param in non null format on which it will be laid over.
		The cursor in format should be either in front of
		or inside the byte sequence available through
		{@link IStructReadFormat#readCharBlock} family of
		methods.
	*/
	public CCharStructReader(IStructReadFormat in)
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
         int r = in.readCharBlock(temp,0,1);
         return r==-1 ? -1 : temp[0];
    };
	@Override public int read(char[] buffer,
                         	 int offset,
                         	 int length)throws IOException
    {
    	validateNotClosed();
		return in.readCharBlock(buffer,offset,length);
    };
    /** Always false */
    @Override public boolean ready()throws IOException{ return false; };
}	