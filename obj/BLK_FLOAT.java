package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeFloatBlockImpl}. */
public final class BLK_FLOAT extends AFloatValue implements IObjStructFormat0
{
			public final float v;
	public BLK_FLOAT(float v)
	{
		this.v = v;
	};
	public String toString(){ return "BLK_FLOAT("+v+")";};
	public static BLK_FLOAT valueOf(float x){ return new BLK_FLOAT(x); };
	@Override public float floatValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof BLK_FLOAT)) return false;
		return ((BLK_FLOAT)x).v == v;
	};
};