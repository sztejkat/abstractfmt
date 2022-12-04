package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#endBeginImpl}. */
public final class SIG_END_BEGIN implements IObjStructFormat0
{
			public final String name;
	public SIG_END_BEGIN(String name)
	{
		assert(name!=null);
		this.name = name;
	};
	public String toString(){ return "SIG_END_BEGIN(\""+name+"\")";};
	@Override public final boolean isSignal(){ return true; };
};