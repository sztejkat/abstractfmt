package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeLongBlockImpl}. */
public final class BLK_LONG extends ALongValue implements IObjStructFormat0
{
			public final long v;
	public BLK_LONG(long v)
	{
		this.v = v;
	};
	public String toString(){ return "BLK_LONG("+v+")";};
	@Override public long longValue(){ return v; };
};