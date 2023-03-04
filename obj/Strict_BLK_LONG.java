package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeLongBlockImpl}. */
public final class Strict_BLK_LONG extends AStrictValue implements IObjStructFormat0
{
			public final long v;
	public Strict_BLK_LONG(long v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_BLK_LONG("+v+")";};
	public static Strict_BLK_LONG valueOf(long x){ return new Strict_BLK_LONG(x); };
	@Override public long blockLongValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_BLK_LONG)) return false;
		return ((Strict_BLK_LONG)x).v == v;
	};
};