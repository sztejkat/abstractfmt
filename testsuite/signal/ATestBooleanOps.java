package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.testsuite.*;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.util.Random;
/**
	A test which check if stream do correctly
	exchange boolean primitives.
*/
abstract class ATestBooleanOps extends ASignalTest
{
	/** 
		Produces test sequence, repeatable, of specified
		length
		@param length size of sequence
		@return sequence, random but always the same.
	*/ 
	protected boolean [] createSequence(int length)
	{
		enter("length="+length);
		Random r = new Random(0x3349058L);	//to get repeatable sequence.
		boolean [] x = new boolean[length];
		for(int i=0;i<length;i++)
		{
			x[i]=r.nextBoolean();
		};
		leave();
		return x;
	};
}