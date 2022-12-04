package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#endImpl}. Singleton */
public final class SIG_END implements IObjStructFormat0
{
			public static final SIG_END INSTANCE = new SIG_END();
			
	private SIG_END(){};
	public String toString(){ return "SIG_END";};
	@Override public final boolean isSignal(){ return true; };
};