package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeBooleanBlockImpl}. */
public final class Strict_BLK_BOOLEAN extends AStrictValue implements IObjStructFormat0
{
			private static final Strict_BLK_BOOLEAN TRUE = new Strict_BLK_BOOLEAN(true);
			private static final Strict_BLK_BOOLEAN FALSE = new Strict_BLK_BOOLEAN(false);
			
			public final boolean v;
	private Strict_BLK_BOOLEAN(boolean v)
	{
		this.v = v;
	};
	public static Strict_BLK_BOOLEAN valueOf(boolean v){ return v ? TRUE : FALSE; };
	public String toString(){ return "Strict_BLK_BOOLEAN("+v+")";};
	
	@Override public boolean blockBooleanValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_BLK_BOOLEAN)) return false;
		return ((Strict_BLK_BOOLEAN)x).v == v;
	};
};