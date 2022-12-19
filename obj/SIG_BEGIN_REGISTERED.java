package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat1#beginRegisteredImpl}. */
public final class SIG_BEGIN_REGISTERED implements IObjStructFormat1
{
			public final int order;
			public final int index;
	public SIG_BEGIN_REGISTERED(int index, int order)
	{
		this.index = index;
		this.order = order;
	};
	public String toString(){ return "SIG_END_BEGIN_REGISTER(index="+index+",order="+order+")";};
	@Override public final boolean isSignal(){ return true; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof SIG_BEGIN_REGISTERED)) return false;
		SIG_BEGIN_REGISTERED xr = (SIG_BEGIN_REGISTERED)x;
		return  (xr.index==index)
			 	&&
			 	(xr.order==order);
		
	};
};