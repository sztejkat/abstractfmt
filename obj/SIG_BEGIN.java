package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#beginImpl}. */
public final class SIG_BEGIN implements IObjStructFormat0
{
			public final String name;
	public SIG_BEGIN(String name)
	{
		assert(name!=null);
		this.name = name;
	};
	public String toString(){ return "SIG_BEGIN(\""+name+"\")";};
	@Override public final boolean isSignal(){ return true; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof SIG_BEGIN)) return false;
		return ((SIG_BEGIN)x).name.equals(name);
	};
};