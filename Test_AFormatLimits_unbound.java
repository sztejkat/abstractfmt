package sztejkat.abstractfmt;
import org.junit.Test;
import org.junit.Assert;

/**
	Test of a public portion of {@link AFormatLimits},
	with unbound recursion depth and name limit higher than defaults.
*/
public class Test_AFormatLimits_unbound extends ATest_IFormatLimits<Test_AFormatLimits_unbound.DUT>
{
			static final class DUT extends AFormatLimits
			{
					DUT(){  };
					@Override public int getMaxSupportedSignalNameLength(){ return 65536; };
					@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
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