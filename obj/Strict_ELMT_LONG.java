package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeLongImpl}. */
public final class Strict_ELMT_LONG  extends AStrictValue implements IObjStructFormat0
{
			public final long v;
	public Strict_ELMT_LONG(long v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_ELMT_LONG("+v+")";};
	public static Strict_ELMT_LONG valueOf(long x){ return new Strict_ELMT_LONG(x); };
	@Override public long longValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_ELMT_LONG)) return false;
		return ((Strict_ELMT_LONG)x).v == v;
	};
};