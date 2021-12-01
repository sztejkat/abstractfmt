package sztejkat.abstractfmt;
import java.io.IOException;
/**
	A string-builder like class which implements {@link Appendable}
	contract which throws {@link EFormatBoundaryExceeded}
	if an attempt is made to put too many data into it.
	<p>
	This class may be used when You do not trust the char processing 
	stream routines to trully obey string size limits.
*/
public class CBoundAppendable implements Appendable
{
				private final char [] buffer;
				private int at;
				
	/* ********************************************************
	
			Construction
	
	*********************************************************/
	/** Creates, pre allocating buffer of specified limit
	@param limit non-negative
	*/
	public CBoundAppendable(int limit)
	{
		assert(limit>0);
		this.buffer = new char[limit];
		this.at = 0;
	};
	/* ********************************************************
	
			Access
	
	*********************************************************/
	/** Returns length used in a buffer 
	@return length of string in buffer */
	public final int length(){return at; };
	/** Capactiy of buffer 
	@return capacity of buffer, in chars
	*/
	public final int capacity(){ return buffer.length; };
	/** Resets to zero size */
	public final void reset(){ at = 0; };
	/** Transforms to independent string 
	@return string representation of collected chars
	*/
	public final String toString(){ return new String(buffer,0,at); };
	
	/* ********************************************************
	
			Appendable	
	
	*********************************************************/
	private void appendImpl(CharSequence csq,
           			       int start,
           			       int end)throws EFormatBoundaryExceeded
    {
    		for(int i=start; i<end; i++)
			{
				if (at>=buffer.length) throw new EFormatBoundaryExceeded("String capacity "+at+" is already used up. Some string in stream is too long");
				buffer[at]=csq.charAt(i);
				at++;
			};
    };
	public Appendable append(CharSequence csq)throws IOException
	{
		if (csq==null)
		{ 
			append('n'); append('u'); append('l'); append('l');
		}else
		{
			appendImpl(csq,0,csq.length());
		};
		return this;
	};
	public Appendable append(CharSequence csq,
           			       int start,
           			       int end)throws IOException
    {
    	if (csq==null)
		{ 
			append('n'); append('u'); append('l'); append('l');
		}
		else
		{
			appendImpl(csq,start,end);
		}
		return this;
    };
    public Appendable append(char c)throws IOException
    {
    	if (at>=buffer.length) throw new EFormatBoundaryExceeded("String capacity "+at+" is already used up. Some string in stream is too long");
		buffer[at]=c;
		at++;
		return this;
    };
};