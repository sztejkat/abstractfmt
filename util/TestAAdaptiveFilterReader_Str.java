package sztejkat.abstractfmt.util;
import java.io.IOException;

public class TestAAdaptiveFilterReader_Str extends ATestAAdaptiveFilterReader
{
		private static final class DUT extends AAdaptiveFilterReader
		{
				private String [] sq;
				private int i;
				
				DUT(String [] sq)
				{
					super(4,4);
					this.sq=sq;
				};
				protected void filter()throws IOException
				{
					if (i==sq.length) return;
					write(sq[i++]);
				};	
		};
		
	protected AAdaptiveFilterReader create(String [] sq){ return new DUT(sq); };
};