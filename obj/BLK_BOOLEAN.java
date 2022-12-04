package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeBooleanBlockImpl}. */
public final class BLK_BOOLEAN extends ABooleanValue implements IObjStructFormat0
{
			private static final BLK_BOOLEAN TRUE = new BLK_BOOLEAN(true);
			private static final BLK_BOOLEAN FALSE = new BLK_BOOLEAN(false);
			
			public final boolean v;
	private BLK_BOOLEAN(boolean v)
	{
		this.v = v;
	};
	public static BLK_BOOLEAN valueOf(boolean v){ return v ? TRUE : FALSE; };
	public String toString(){ return "BLK_BOOLEAN("+v+")";};
	
	@Override public boolean booleanValue(){ return v; };
};