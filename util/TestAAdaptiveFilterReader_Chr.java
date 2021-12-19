package sztejkat.abstractfmt.util;
import java.io.IOException;

public class TestAAdaptiveFilterReader_Chr extends ATestAAdaptiveFilterReader
{
		private static final class DUT extends AAdaptiveFilterReader
		{
				private String [] sq;
				private int i;
				
				DUT(String [] sq)
				{
					super(4,32);
					this.sq=sq;
				};
				protected void filter()throws IOException
				{
					if (i==sq.length) return;
					String s = sq[i++];
					for(int j=0,n=s.length();j<n;j++)
					{
						write(s.charAt(j));
					};
				};	
		};
		
	protected AAdaptiveFilterReader create(String [] sq){ return new DUT(sq); };
};