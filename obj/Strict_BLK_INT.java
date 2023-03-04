package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeIntBlockImpl}. */
public final class Strict_BLK_INT extends AStrictValue implements IObjStructFormat0
{
			public final int v;
	public Strict_BLK_INT(int v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_BLK_INT("+v+")";};
	public static Strict_BLK_INT valueOf(int x){ return new Strict_BLK_INT(x); };
	@Override public int blockIntValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_BLK_INT)) return false;
		return ((Strict_BLK_INT)x).v == v;
	};
};