package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeFloatImpl}. */
public final class ELMT_FLOAT extends AFloatValue implements IObjStructFormat0
{
			public final float v;
	public ELMT_FLOAT(float v)
	{
		this.v = v;
	};
	public String toString(){ return "ELMT_FLOAT("+v+")";};
	public static ELMT_FLOAT valueOf(float x){ return new ELMT_FLOAT(x); };
	@Override public float floatValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof ELMT_FLOAT)) return false;
		return ((ELMT_FLOAT)x).v == v;
	};
};