package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeFloatImpl}. */
public final class ELMT_FLOAT extends AFloatValue implements IObjStructFormat0
{
			public final float v;
	public ELMT_FLOAT(float v)
	{
		this.v = v;
	};
	public String toString(){ return "ELMT_FLOAT("+v+")";};
	@Override public float floatValue(){ return v; };
};