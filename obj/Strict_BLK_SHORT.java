package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeShortBlockImpl}. */
public final class Strict_BLK_SHORT extends AStrictValue implements IObjStructFormat0
{
			public final short v;
	public Strict_BLK_SHORT(short v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_BLK_SHORT("+v+")";};
	public static Strict_BLK_SHORT valueOf(short v){ return new Strict_BLK_SHORT(v); };
	@Override public short blockShortValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_BLK_SHORT)) return false;
		return ((Strict_BLK_SHORT)x).v == v;
	};
};