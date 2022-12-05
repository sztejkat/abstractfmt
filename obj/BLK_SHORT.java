package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeShortBlockImpl}. */
public final class BLK_SHORT extends AShortValue implements IObjStructFormat0
{
			public final short v;
	public BLK_SHORT(short v)
	{
		this.v = v;
	};
	public String toString(){ return "BLK_SHORT("+v+")";};
	@Override public short shortValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof BLK_SHORT)) return false;
		return ((BLK_SHORT)x).v == v;
	};
};