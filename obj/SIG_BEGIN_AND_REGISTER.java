package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat1#beginAndRegisterImpl}. */
public final class SIG_BEGIN_AND_REGISTER implements IObjStructFormat1
{
			public final String name;
			public final int order;
			public final int index;
	public SIG_BEGIN_AND_REGISTER(String name, int index, int order)
	{
		assert(name!=null);
		this.name = name;
		this.index = index;
		this.order = order;
	};
	public String toString(){ return "SIG_BEGIN_AND_REGISTER(\""+name+"\",index="+index+",order="+order+")";};
	@Override public final boolean isSignal(){ return true; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof SIG_BEGIN_AND_REGISTER)) return false;
		SIG_BEGIN_AND_REGISTER xr = (SIG_BEGIN_AND_REGISTER)x;
		return xr.name.equals(name)
			 	&&
			 	(xr.index==index)
			 	&&
			 	(xr.order==order);
		
	};
};