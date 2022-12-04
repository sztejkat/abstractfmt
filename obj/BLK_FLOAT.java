package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeFloatBlockImpl}. */
public final class BLK_FLOAT extends AFloatValue implements IObjStructFormat0
{
			public final float v;
	public BLK_FLOAT(float v)
	{
		this.v = v;
	};
	public String toString(){ return "BLK_FLOAT("+v+")";};
	@Override public float floatValue(){ return v; };
};