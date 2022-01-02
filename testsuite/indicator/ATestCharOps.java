package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.testsuite.*;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.util.Random;
/**
	A test which check if indicator do correctly
	exchange boolean primitives.
*/
abstract class ATestCharOps extends AIndicatorTest
{
	/** 
		Produces test sequence, repeatable, of specified
		length
		@param length size of sequence
		@return sequence, random but always the same.
	*/ 
	protected char [] createSequence(int length)
	{
		enter("length="+length);
		Random r = new Random(0x3349058L);	//to get repeatable sequence.
		char [] x = new char[length];
		for(int i=0;i<length;i++)
		{
			x[i]=(char)(r.nextInt());
		};
		leave();
		return x;
	};
}