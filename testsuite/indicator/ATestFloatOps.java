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
abstract class ATestFloatOps extends AIndicatorTest
{
	/** 
		Produces test sequence, repeatable, of specified
		length
		@param length size of sequence
		@return sequence, random but always the same.
	*/ 
	protected float [] createSequence(int length)
	{
		enter("length="+length);
		Random r = new Random(0x3349058L);	//to get repeatable sequence.
		float [] x = new float[length];
		for(int i=0;i<length;i++)
		{
			float f;
			do{
				f = Float.intBitsToFloat(r.nextInt());
				}while(Float.isNaN(f));
			x[i]=f;
		};
		leave();
		return x;
	};
}