package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeIntBlockImpl}. */
public final class BLK_INT extends AIntValue implements IObjStructFormat0
{
			public final int v;
	public BLK_INT(int v)
	{
		this.v = v;
	};
	public String toString(){ return "BLK_INT("+v+")";};
	public static BLK_INT valueOf(int x){ return new BLK_INT(x); };
	@Override public int intValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof BLK_INT)) return false;
		return ((BLK_INT)x).v == v;
	};
};