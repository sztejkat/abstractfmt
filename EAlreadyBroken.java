package sztejkat.abstractfmt;
import java.io.IOException;
/**
	An exception thrown when stream detects it have thrown
	broken format exception already and turned to broken state.
*/
public class EAlreadyBroken extends EBrokenFormat
{
		private static final long serialVersionUID=1L;
	public EAlreadyBroken(EBrokenFormat cause){super("Already broken by "+cause,cause);};
};