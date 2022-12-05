package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeLongImpl}. */
public final class ELMT_LONG  extends ALongValue implements IObjStructFormat0
{
			public final long v;
	public ELMT_LONG(long v)
	{
		this.v = v;
	};
	public String toString(){ return "ELMT_LONG("+v+")";};
	public static ELMT_LONG valueOf(long x){ return new ELMT_LONG(x); };
	@Override public long longValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof ELMT_LONG)) return false;
		return ((ELMT_LONG)x).v == v;
	};
};