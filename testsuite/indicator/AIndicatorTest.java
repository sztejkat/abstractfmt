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
	
	/** Reads indicator and validates if is expected. 
	This method transparently handles lack of end-begin optimization
	for streams which do not implement it.
	@param f from where to read
	@param expected what is expected
	@throws IOException if failed to read
	@throws AssertionError if read is different than expected.
	*/
	protected void assertReadOptIndicator(IIndicatorReadFormat f, TIndicator expected)throws IOException
	{
		if (
			((expected.FLAGS & TIndicator.IS_BEGIN)!=0)
			&&
			((expected.FLAGS & TIndicator.IS_END)!=0)
		   )
		   {
		   	//possible optimization case.
		   	TIndicator i = f.readIndicator();
			if (i!=expected)
			{
				if (i==TIndicator.END)
				{
					//possible lack of end-begin optimization
					i = f.readIndicator();
					expected = TIndicator.getBegin(expected);
				};
				if (i!=expected)
					Assert.fail("lack of end-begin optimization, second readIndicator()="+i+" while expected "+expected);
			};
		   }else
		   	assertReadIndicator(f,expected);
	};
	/** Reads indicator and validates if it a matching flush operation.
	Does not check indicator at all if read format is not flushing.
	If it is tests for ideal match or for generic flushes of apropriate types.
	@param f from where to get
	@param expected what is expected, must be FLUSH_xxx indicator.
	@throws IOException if failed to get
	@throws AssertionError if indicator is different than expected and
		is not a generic flush.
	*/
	protected void assertReadFlushIndicator(IIndicatorReadFormat f,TIndicator expected
							 
							 )throws IOException
	{
		assert((expected.FLAGS & TIndicator.FLUSH)!=0):"not FLUSH indicator!";
		if (f.isFlushing())
		{
			TIndicator i = f.readIndicator();
			if (i!=expected)
			{
				if (i==TIndicator.FLUSH_ANY) return;
				if ((expected.FLAGS & TIndicator.BLOCK)!=0)
											if (i==TIndicator.FLUSH_BLOCK) return;
				if ((expected.FLAGS & TIndicator.ELEMENT)!=0)
											if (i==TIndicator.FLUSH_ELEMENTARY) return;
				Assert.fail("getIndicator()="+i+" while expected "+expected);
			}
		}
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
	
	
	/** Gets indicator and validates if is expected. 
	This method transparently handles lack of end-begin optimization
	for streams which do not implement it.
	@param f from where to read
	@param expected what is expected
	@throws IOException if failed to read
	@throws AssertionError if read is different than expected.
	*/
	protected void assertGetOptIndicator(IIndicatorReadFormat f, TIndicator expected)throws IOException
	{
		if (
			((expected.FLAGS & TIndicator.IS_BEGIN)!=0)
			&&
			((expected.FLAGS & TIndicator.IS_END)!=0)
		   )
		   {
		   	//possible optimization case.
		   	TIndicator i = f.getIndicator();
			if (i!=expected)
			{
				if (i==TIndicator.END)
				{
					//possible lack of end-begin optimization
					f.next();	//just skip the indicator.
					i = f.getIndicator();
					expected = TIndicator.getBegin(expected);
				};
				if (i!=expected)
					Assert.fail("lack of end-begin optimization, second readIndicator()="+i+" while expected "+expected);
			};
		   }else
		   	assertGetIndicator(f,expected);
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