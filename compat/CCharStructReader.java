package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.EClosed;
import sztejkat.abstractfmt.EEof;
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
public class CCharStructReader extends AStructReader
{
			
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
         try{
         	 int r = in.readCharBlock(temp,0,1);
         	 return r==-1 ? -1 : temp[0];
         }catch(EEof ex){ return -1; } //since Reader should return -1 while we are allowed to
									   //throw on physical eof when we read zero data.
    };
	@Override public int read(char[] buffer,
                         	 int offset,
                         	 int length)throws IOException
    {
    	validateNotClosed();
    	try{
			return in.readCharBlock(buffer,offset,length);
		}catch(EEof ex){ return -1; } //since Reader should return -1 while we are allowed to
									   //throw on physical eof when we read zero data.
    };
    /** Always false */
    @Override public boolean ready()throws IOException{ return false; };
}	