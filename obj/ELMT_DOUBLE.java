package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeDoubleImpl}. */
public final class ELMT_DOUBLE extends ADoubleValue implements IObjStructFormat0
{
			public final double v;
	public ELMT_DOUBLE(double v)
	{
		this.v = v;
	};
	public String toString(){ return "ELMT_DOUBLE("+v+")";};
	public static ELMT_DOUBLE valueOf(double x){ return new ELMT_DOUBLE(x); };
	@Override public double doubleValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof ELMT_DOUBLE)) return false;
		return ((ELMT_DOUBLE)x).v == v;
	};
};