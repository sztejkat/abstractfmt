package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeShortImpl}. */
public final class ELMT_SHORT  extends AShortValue implements IObjStructFormat0
{
			public final short v;
	public ELMT_SHORT(short v)
	{
		this.v = v;
	};
	public String toString(){ return "ELMT_SHORT("+v+")";};
	@Override public short shortValue(){ return v; };
};