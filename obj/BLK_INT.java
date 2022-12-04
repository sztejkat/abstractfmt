package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeIntBlockImpl}. */
public final class BLK_INT extends AIntValue implements IObjStructFormat0
{
			public final int v;
	public BLK_INT(int v)
	{
		this.v = v;
	};
	public String toString(){ return "BLK_INT("+v+")";};
	@Override public int intValue(){ return v; };
};