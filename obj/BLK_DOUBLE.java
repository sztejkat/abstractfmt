package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeDoubleBlockImpl}. */
public final class BLK_DOUBLE extends ADoubleValue implements IObjStructFormat0
{
			public final double v;
	public BLK_DOUBLE(double v)
	{
		this.v = v;
	};
	public String toString(){ return "BLK_DOUBLE("+v+")";};
	@Override public double doubleValue(){ return v; };
};