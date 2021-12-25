package sztejkat.abstractfmt.util;
import java.io.IOException;
import java.io.Reader;

/**
	A filter removing some blocks enclosed in specifined begin/end sequences.
	<p>
	Not thread safe.
*/
public class CBlockFilter extends AAdaptiveFilterReader
{
				/** Input armend with read-back capabilities.
				This input is always feed back with characters
				which were not processed. */
				protected final CAdaptivePushBackReader in;
				/** Buffer for matching sequences */
				private final char [] temp;
				/** Begin sequence */
				private final String begin;
				/** End sequence */
				private final String end;
				/** True if skipping is in progress */
				private boolean skipping;
				
	/** Creates.
	@param in input which is to be filtered. If it is a {@link CAdaptivePushBackReader}
		it will be used directly. If it is not, it will be wrapped in new instance
		and this instance will be used to do processing. Having push-back reader
		is necessary, because begin/end detection requires some read-ahead processing.
		
	@param begin begin sequence which starts block which must be removed from 
		data served to output.
	@param end sequence which terminates block which must be removed from 
		data served to output.
	*/
	public CBlockFilter(Reader in,String begin, String end)
	{
		super(4,32);	//we need very little buffering,
						//just to <!-- 
		assert(in!=null);
		assert(begin!=null);
		assert(end!=null);
		this.in = (in instanceof CAdaptivePushBackReader) ? 
				(CAdaptivePushBackReader)in 
				:
				new CAdaptivePushBackReader(in, 4,32);
		this.begin = begin;
		this.end= end;
		int L = Math.max(end.length(),begin.length());	
		this.temp = new char[L];
	};
	
	/** Checks if part 0...len of c is beginning of s.
	@param c buffer
	@param len chars in buffer, less or equal to size of s
	@param s string to check
	@return true if it is. If len==0 return value is true.
	*/
	private static boolean isBeginOf(char []c, int len, String s)
	{
		assert(s.length()>=len);
		for(int i=0;i<len;i++)
		{
			if (s.charAt(i)!=c[i]) return false;
		};
		return true;
	};
	@Override protected void filter()throws IOException
	{		
		for(;;)
		{
			if (skipping)
			{
				//possible end of block to skip
				//Try to fetch "end"
				int r = in.read(temp,0,end.length());
				if (r==-1) r=0;
				if (isBeginOf(temp, r, end))
				{
					if (r==end.length())
					{
						//We have end of skipped section
						skipping = false;
						continue;
					}else
					{
						//this is premature eof, we need to be able to
						//compare it again
						in.unread(temp,0,r);
						return;
					}
				}else
				{
					//Not an end sequence, skip first char and un-read remaning
					if (r>1)
					{ 
							in.unread(temp,1,r-1);
					}; 
				}
			}else
			{
				//possible begin of block to skip.
				//Try to fetch "begin"
				int r = in.read(temp,0,begin.length());
				if (r==-1) r=0;
				if (isBeginOf(temp, r, begin))
				{
					if (r==begin.length())
					{						
						//We have begin
						skipping = true;
						continue;
					}else
					{
						//this is premature eof, we need to be able to
						//compare it again
						in.unread(temp,0,r);
						return;
					}
				}else
				{
					//not a begin, pass up first character, unread remaining
					if (r>1)
					{ 
							in.unread(temp,1,r-1);
					};
					//System.out.println("Passig up "+temp[0]);
					write(temp,0,1);
					return;
				}
			}
		}
	}
	@Override public void close()throws IOException
	{
		super.close();
		in.close();
	};
};