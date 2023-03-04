package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeShortImpl}. */
public final class Strict_ELMT_SHORT  extends AStrictValue implements IObjStructFormat0
{
			public final short v;
	public Strict_ELMT_SHORT(short v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_ELMT_SHORT("+v+")";};
	public static Strict_ELMT_SHORT valueOf(short x){ return new Strict_ELMT_SHORT(x); };
	@Override public short shortValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_ELMT_SHORT)) return false;
		return ((Strict_ELMT_SHORT)x).v == v;
	};
};