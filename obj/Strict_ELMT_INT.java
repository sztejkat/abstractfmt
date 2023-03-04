package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeIntImpl}. */
public final class Strict_ELMT_INT  extends AStrictValue  implements IObjStructFormat0
{
			public final int v;
	public Strict_ELMT_INT(int v)
	{
		this.v = v;
	};
	public String toString(){ return "Strict_ELMT_INT("+v+")";};
	public static Strict_ELMT_INT valueOf(int x){ return new Strict_ELMT_INT(x); };
	@Override public int intValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_ELMT_INT)) return false;
		return ((Strict_ELMT_INT)x).v == v;
	};
};