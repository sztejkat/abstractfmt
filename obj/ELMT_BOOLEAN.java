package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeBooleanImpl}. */
public final class ELMT_BOOLEAN extends ABooleanValue implements IObjStructFormat0
{
			private static final ELMT_BOOLEAN TRUE = new ELMT_BOOLEAN(true);
			private static final ELMT_BOOLEAN FALSE = new ELMT_BOOLEAN(false);
			
			public final boolean v;
	private ELMT_BOOLEAN(boolean v)
	{
		this.v = v;
	};
	public static ELMT_BOOLEAN valueOf(boolean v){ return v ? TRUE : FALSE; };
	public String toString(){ return "ELMT_BOOLEAN("+v+")";};
	
	@Override public boolean booleanValue(){ return v; };
};