package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeDoubleImpl}. */
public final class Strict_ELMT_DOUBLE extends AStrictValue implements IObjStructFormat0
{
			public final double v;
	public Strict_ELMT_DOUBLE(double v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_ELMT_DOUBLE("+v+")";};
	public static Strict_ELMT_DOUBLE valueOf(double x){ return new Strict_ELMT_DOUBLE(x); };
	@Override public double doubleValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_ELMT_DOUBLE)) return false;
		return ((Strict_ELMT_DOUBLE)x).v == v;
	};
};