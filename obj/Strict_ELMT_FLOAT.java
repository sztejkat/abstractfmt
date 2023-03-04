package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeFloatImpl}. */
public final class Strict_ELMT_FLOAT extends AStrictValue implements IObjStructFormat0
{
			public final float v;
	public Strict_ELMT_FLOAT(float v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_ELMT_FLOAT("+v+")";};
	public static Strict_ELMT_FLOAT valueOf(float x){ return new Strict_ELMT_FLOAT(x); };
	@Override public float floatValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_ELMT_FLOAT)) return false;
		return ((Strict_ELMT_FLOAT)x).v == v;
	};
};