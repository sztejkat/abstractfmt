package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeStringImpl}. */
public final class Strict_BLK_STRING  extends AStrictValue implements IObjStructFormat0
{
			private static final Strict_BLK_STRING INSTANCE [];
			static{
				INSTANCE = new Strict_BLK_STRING[65536];
				char v = Character.MIN_VALUE;
				for(int i=0;i<65536;i++)
				{
					INSTANCE[i]  = new Strict_BLK_STRING(v);
					v++;
				};
			};
			
			public final char v;
	private Strict_BLK_STRING(char v)
	{
		this.v = v;
	};
	public static Strict_BLK_STRING valueOf(char v)
	{
		return INSTANCE[(int)v-Character.MIN_VALUE];
	};	
	public String toString(){ return "Strict_BLK_STRING("+v+")";};
	@Override public char blockStringValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_BLK_STRING)) return false;
		return ((Strict_BLK_STRING)x).v == v;
	};
};