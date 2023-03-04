package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeDoubleBlockImpl}. */
public final class Strict_BLK_DOUBLE extends AStrictValue implements IObjStructFormat0
{
			public final double v;
	public Strict_BLK_DOUBLE(double v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_BLK_DOUBLE("+v+")";};
	public static Strict_BLK_DOUBLE valueOf(double x){ return new Strict_BLK_DOUBLE(x); };
	@Override public double blockDoubleValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_BLK_DOUBLE)) return false;
		return ((Strict_BLK_DOUBLE)x).v == v;
	};
};