package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.EClosed;
import sztejkat.abstractfmt.EEof;
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
public class CStringStructReader extends AStructReader
{
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
		super(in);
	};
	
	/*  *****************************************************
		
			Reader
		
			
	******************************************************/
	
	/** Always false. */
	@Override public final boolean markSupported(){ return false; };
	
	@Override public int read()throws IOException
	{
         validateNotClosed();
         temp.setLength(0);
		 try{
			int r = in.readString(temp,1);
			return r==-1 ? -1 : temp.charAt(0);
		 }catch(EEof ex){ return -1; } //since Reader should return -1 while we are allowed to
									   		//throw on physical eof when we read zero data.
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
			int r;
			r = read();
			if (r==-1) break;
			buffer[offset++]=(char)r;
			readen++;
		}
		return readen==0 ? -1 : readen;
    };
    /** Always false */
    @Override public boolean ready()throws IOException{ return false; };
}	