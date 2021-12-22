package sztejkat.abstractfmt.util;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import java.io.IOException;
/**
	A string-builder like class which implements {@link Appendable}
	contract which throws {@link EFormatBoundaryExceeded}
	if an attempt is made to put too many data into it.
	<p>
	This class may be used when You do not trust the char processing 
	stream routines to trully obey string size limits.
*/
public class CBoundAppendable implements Appendable,CharSequence
{
				/** Buffer */
				private final char [] buffer;
				/** Where to put char and length of buffer */
				private int at;
				/** Hash code 
				@see #hashCode
				*/
				private int hash;
				
	/* ********************************************************
	
			Construction
	
	*********************************************************/
	/** Creates, pre allocating buffer of specified limit
	@param limit non-negative, can be zero.
	*/
	public CBoundAppendable(int limit)
	{
		assert(limit>=0);
		this.buffer = new char[limit];
		this.at = 0;
	};
	/* ********************************************************
	
			Access
	
	*********************************************************/
	/** Capactiy of buffer 
	@return capacity of buffer, in chars
	*/
	public final int capacity(){ return buffer.length; };
	/** Resets to zero size */
	public final void reset(){ at = 0; hash =0;};
	/** Transforms to independent string 
	@return string representation of collected chars
	*/
	public final String toString(){ return new String(buffer,0,at); };
	/* ********************************************************
	
			CharSequence	
	
	*********************************************************/
	/** Returns length used in a buffer 
	@return length of string in buffer */
	public final int length(){return at; };
	public final boolean isEmpty(){ return at==0;};
	public final char charAt(int index)
	{
		if (index>=at) throw new IndexOutOfBoundsException(index+" is not in 0..."+at+"-1");
		return buffer[index];
	};
	public final CharSequence subSequence(int start,int end)
	{
		return toString().subSequence(start,end);
	};
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
    	if (at>=buffer.length) throw new EFormatBoundaryExceeded("String capacity "+at+" is already used up. The string \""+this.toString()+""+c+"\" is too long");
		buffer[at]=c;
		at++;
		return this;
    };
    /** Copied from JDK8 String.hashCode
    to match standard.
    @return hashCode.
    */
    public final int hashCode()
    {
        /* Please notice the inherent inefficiency
           The 31*h will cross the 32 bit boundary
           after about 20 characters or so. If computation
           will continue after next 20 characters the
           effect of first characters will get lost.

           Why they have choosen unlimited hash, when
           hash is just used to guess if texts are
           "similiar"?
        */
        int h = hash;
        final int L = length();
        if (h == 0 && L > 0) {
            final char val[] = buffer;

            for (int i = 0; i < L; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
    }
    /** Checks if it starts with string. Avoids any buffers creation.
    @param s starts with what
    @return -1 does not start with, 0 starts with, 1 equals to.
    */
    public int startsWith(String s)
    {
    	final int n = s.length();
    	final int len = length();
    	if (len<n) return -1; //shorter can't match.
    	for(int i=0; i<n; i++)
    	{
    		if (buffer[i]!=s.charAt(i)) return -1;
    	};
    	//Now all characters in s have found match.
    	return (n==len) ? 1 : 0; 
    };
    
     /** Checks if it string starts with this buffer. Avoids any buffers creation.
    @param s string to check
    @return -1 does not start with, 0 starts with, 1 equals to.
    */
    public int isStartOf(String s)
    {
    	final int n = s.length();
    	final int len = length();
    	if (len>n) return -1; //shorter can't match.
    	for(int i=0; i<len; i++)
    	{
    		if (buffer[i]!=s.charAt(i)) return -1;
    	};
    	//Now all characters in s have found match.
    	return (n==len) ? 1 : 0; 
    };
    
    
    /** A quick test, faster than 
    <code>s.equals(this.toString)</code>
    because it avoids creation of a temporary object
    @param s string to compare with, non-null
    @return true if content is equal
    */
    public boolean equalsString(String s)
    {
    	assert(s!=null);
    	//Note: Objects with different hashes can't be 
    	//the same.
        //Notice however, that computing hash code
        //may be more time consuming than
        //exact comparison for longer strings.
        //But it may be more efficient if we
        //compare the same string with multiple
        //textes, since then hascode is cached.
        //This is precisely why it could be used
        //in switch statement.
        //
        //We can easliy check if we know own hascode,
        //but we can't test if s knows it.
    	int L = length();
    	if (L!=s.length()) return false;
        if ((L<=32)&&(hash!=0))   //optimizing guess.
        {
               	if (hashCode()!=s.hashCode()) return false;
        };
    	for(int i=L;--i>=0;)
    	{
    		if (buffer[i]!=s.charAt(i)) return false;
    	};
    	return true;
    };
};