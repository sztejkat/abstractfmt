package sztejkat.abstractfmt;
/**
	An extension to {@link IStructWriteFormat} which transparently do 
	store in stream information necessary for matching {@link IStructReadFormat}
	implementation.
	<p>
	No change in contract, this class can be used excactly the same
	way as the standard <i>undescribed</i> writer.
*/
public interface ITypedStructWriteFormat extends IStructWriteFormat
{
};