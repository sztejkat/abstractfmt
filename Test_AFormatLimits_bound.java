package sztejkat.abstractfmt;
import org.junit.Test;
import org.junit.Assert;

/**
	Test of a public portion of {@link AFormatLimits},
	with bound recursion depth
*/
public class Test_AFormatLimits_bound extends ATest_IFormatLimits<Test_AFormatLimits_bound.DUT>
{
			static final class DUT extends AFormatLimits
			{
					DUT(){
						//Note: no need to call trim... because
						//we have hardcoded values.
					};
					@Override public int getMaxSupportedSignalNameLength(){ return 100; };
					@Override public int getMaxSupportedStructRecursionDepth(){ return 5; };
			};

	@Override protected DUT create()
	{
		return new DUT();
	};
	@Override protected void enterStruct(DUT limits)throws EFormatBoundaryExceeded
	{
		limits.enterStruct();
	};
	@Override protected void leaveStruct(DUT limits)throws EFormatBoundaryExceeded
	{
		limits.leaveStruct();
	}; 
};