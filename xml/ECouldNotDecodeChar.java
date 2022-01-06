package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.ECorruptedFormat;

/**
	Used internally to indicate some
	characters escape decoding problems.
*/
class ECouldNotDecodeChar extends ECorruptedFormat
{
			private static final long serialVersionUID=1L;
	ECouldNotDecodeChar(String msg)
	{
		super(msg);
	};
};