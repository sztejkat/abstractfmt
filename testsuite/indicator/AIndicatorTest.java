package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.testsuite.*;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
/**
	A base for tests with some utility classes.
*/
abstract class AIndicatorTest extends ATestCase<Pair>
{
	/** Reads indicator and validates if is expected. 
	@param f from where to read
	@param expected what is expected
	@throws IOException if failed to read
	@throws AssertionError if read is different than expected.
	*/
	protected void assertReadIndicator(IIndicatorReadFormat f, TIndicator expected)throws IOException
	{
		TIndicator i = f.readIndicator();
		if (i!=expected)
			Assert.fail("readIndicator()="+i+" while expected "+expected);
	};
	/** Reads indicator and validates if it has specified 
	flags in specified state.
	@param f from where to read
	@param expected_flags_mask mask of expected {@link TIndicator#FLAGS}
	@param expected_flags_values values of those flags.
	@throws IOException if failed to read
	@throws AssertionError if read is different than expected.
	*/
	protected void assertReadIndicator(IIndicatorReadFormat f,
							 int expected_flags_mask,
							 int expected_flags_values
							 )throws IOException
	{
		TIndicator i = f.readIndicator();
		if ((i.FLAGS & expected_flags_mask)!=expected_flags_values)
			Assert.fail("readIndicator()="+i+" FLAGS=0x"+
						Integer.toHexString(i.FLAGS));
	};
	
	
	/** Gets indicator and validates if is expected. 
	@param f from where to get
	@param expected what is expected
	@throws IOException if failed to get
	@throws AssertionError if get is different than expected.
	*/
	protected void assertGetIndicator(IIndicatorReadFormat f, TIndicator expected)throws IOException
	{
		TIndicator i = f.getIndicator();
		if (i!=expected)
			Assert.fail("getIndicator()="+i+" while expected "+expected);
	};
	/** Gets indicator and validates if it has specified 
	flags in specified state.
	@param f from where to get
	@param expected_flags_mask mask of expected {@link TIndicator#FLAGS}
	@param expected_flags_values values of those flags.
	@throws IOException if failed to get
	@throws AssertionError if get is different than expected.
	*/
	protected void assertGetIndicator(IIndicatorReadFormat f,
							 int expected_flags_mask,
							 int expected_flags_values
							 )throws IOException
	{
		TIndicator i = f.getIndicator();
		if ((i.FLAGS & expected_flags_mask)!=expected_flags_values)
			Assert.fail("getIndicator()="+i+" FLAGS=0x"+
						Integer.toHexString(i.FLAGS));
	};
};