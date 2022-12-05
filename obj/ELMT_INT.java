package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeIntImpl}. */
public final class ELMT_INT  extends AIntValue  implements IObjStructFormat0
{
			public final int v;
	public ELMT_INT(int v)
	{
		this.v = v;
	};
	public String toString(){ return "ELMT_INT("+v+")";};
	public static ELMT_INT valueOf(int x){ return new ELMT_INT(x); };
	@Override public int intValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof ELMT_INT)) return false;
		return ((ELMT_INT)x).v == v;
	};
};