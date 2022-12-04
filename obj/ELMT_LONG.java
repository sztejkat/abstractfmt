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
	@Override public long longValue(){ return v; };
};