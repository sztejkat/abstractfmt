package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.testsuite.*;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.TContentType;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
/**
	A base for tests with some utility classes.
*/
abstract class ASignalTest extends ATestCase<Pair>
{
	/** Calls {@link ISignalReadFormat#next} and validates if is expected. 
	
	@param f from where to read
	@param expected what is expected
	@throws IOException if failed to read
	@throws AssertionError if read is different than expected.
	*/
	protected void assertNext(ISignalReadFormat f, String expected)throws IOException
	{
		String s = f.next();
		if (s==null)
		{
			if (expected!=null)
				Assert.fail("readSignal()=null while expected \""+expected+"\"");
		}else
		{
			if (expected==null)
				Assert.fail("readSignal()=\""+s+"\" while expected null");
			if (!s.equals(expected))
				Assert.fail("readSignal()=\""+s+"\" while expected \""+expected+"\"");
		};
		assert((s==expected)||(s.equals(expected)));
	};
	/** Calls {@link ISignalReadFormat#whatNext} and validates if is expected. 
	It transparently handles un-described and described format accepting
	{@link TContentType#PRMTV_UNTYPED} as valid response for any typed 
	expected in undescribed formats. 
	@param f from where to read
	@param expected what is expected, should assume format is described.
	@throws IOException if failed to read
	@throws AssertionError if read is different than expected.
	*/
	protected void assertWhatNext(ISignalReadFormat f, TContentType expected)throws IOException
	{
		TContentType n = f.whatNext();
		if (n!=expected)
		{
			if (f.isDescribed()) Assert.fail("readSignal()="+n+" while expected "+expected);
			if ((expected.FLAGS & TContentType.CONTENT_TYPED)!=0)
			{
				if (n!=TContentType.PRMTV_UNTYPED)
					Assert.fail("readSignal()="+n+" or PRMTV_UNTYPED while expected "+expected);
			}
		}
	};
};