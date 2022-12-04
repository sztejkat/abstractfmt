package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeDoubleImpl}. */
public final class ELMT_DOUBLE extends ADoubleValue implements IObjStructFormat0
{
			public final double v;
	public ELMT_DOUBLE(double v)
	{
		this.v = v;
	};
	public String toString(){ return "ELMT_DOUBLE("+v+")";};
	@Override public double doubleValue(){ return v; };
};