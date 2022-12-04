package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeIntImpl}. */
public final class ELMT_INT  extends AIntValue  implements IObjStructFormat0
{
			public final int v;
	public ELMT_INT(int v)
	{
		this.v = v;
	};
	public String toString(){ return "ELMT_INT("+v+")";};
	@Override public int intValue(){ return v; };
};