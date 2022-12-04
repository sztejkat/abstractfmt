package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeShortBlockImpl}. */
public final class BLK_SHORT extends AShortValue implements IObjStructFormat0
{
			public final short v;
	public BLK_SHORT(short v)
	{
		this.v = v;
	};
	public String toString(){ return "BLK_SHORT("+v+")";};
	@Override public short shortValue(){ return v; };
};