package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeFloatBlockImpl}. */
public final class Strict_BLK_FLOAT extends AStrictValue implements IObjStructFormat0
{
			public final float v;
	public Strict_BLK_FLOAT(float v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_BLK_FLOAT("+v+")";};
	public static Strict_BLK_FLOAT valueOf(float x){ return new Strict_BLK_FLOAT(x); };
	@Override public float blockFloatValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_BLK_FLOAT)) return false;
		return ((Strict_BLK_FLOAT)x).v == v;
	};
};