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
};