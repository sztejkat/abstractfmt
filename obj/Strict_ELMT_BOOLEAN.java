package sztejkat.abstractfmt.obj;

/** Strict version, single elementary operation.
See {@link CObjStructWriteFormat0#writeBooleanImpl}. 
*/
public final class Strict_ELMT_BOOLEAN extends AStrictValue implements IObjStructFormat0
{
			private static final Strict_ELMT_BOOLEAN TRUE = new Strict_ELMT_BOOLEAN(true);
			private static final Strict_ELMT_BOOLEAN FALSE = new Strict_ELMT_BOOLEAN(false);
			
			public final boolean v;
	private Strict_ELMT_BOOLEAN(boolean v)
	{
		this.v = v;
	};
	public static Strict_ELMT_BOOLEAN valueOf(boolean v){ return v ? TRUE : FALSE; };
	public String toString(){ return "Strict_ELMT_BOOLEAN("+v+")";};
	
	@Override public boolean booleanValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof ELMT_BOOLEAN)) return false;
		return ((ELMT_BOOLEAN)x).v == v;
	};
};