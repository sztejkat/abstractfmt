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
	public static BLK_DOUBLE valueOf(double x){ return new BLK_DOUBLE(x); };
	@Override public double doubleValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof BLK_DOUBLE)) return false;
		return ((BLK_DOUBLE)x).v == v;
	};
};