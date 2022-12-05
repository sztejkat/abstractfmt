package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeLongBlockImpl}. */
public final class BLK_LONG extends ALongValue implements IObjStructFormat0
{
			public final long v;
	public BLK_LONG(long v)
	{
		this.v = v;
	};
	public String toString(){ return "BLK_LONG("+v+")";};
	public static BLK_LONG valueOf(long x){ return new BLK_LONG(x); };
	@Override public long longValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof BLK_LONG)) return false;
		return ((BLK_LONG)x).v == v;
	};
};