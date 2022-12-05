package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeShortImpl}. */
public final class ELMT_SHORT  extends AShortValue implements IObjStructFormat0
{
			public final short v;
	public ELMT_SHORT(short v)
	{
		this.v = v;
	};
	public String toString(){ return "ELMT_SHORT("+v+")";};
	public static ELMT_SHORT valueOf(short x){ return new ELMT_SHORT(x); };
	@Override public short shortValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof ELMT_SHORT)) return false;
		return ((ELMT_SHORT)x).v == v;
	};
};