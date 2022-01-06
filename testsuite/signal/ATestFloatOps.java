package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.testsuite.*;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.util.Random;
/**
	A test which check if stream do correctly
	exchange float primitives.
*/
abstract class ATestFloatOps extends ASignalTest
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
			x[i]=r.nextFloat();
		};
		leave();
		return x;
	};
	/** Validates if x is a fragment of expected
	@param x fragment to test
	@param x_offset offset in x to match expected[exp_offset]
	@param length how many elements to compare
	@param expected to match with what
	@param exp_offset offest in expected to match
	*/
	protected static void assertEqual(float []x , int x_offset, int length,
							  		 float []expected, int exp_offset)
    {
    	for(int i=0;i<length;i++)
    	{
    		if (x[x_offset+i]!=expected[exp_offset+i])
    			Assert.fail("Content differs at "+i+", x="+x[x_offset+i]+" expected="+expected[exp_offset+i]);
    	}
    };
}