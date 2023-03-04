package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeCharBlockImpl}. */
public final class Strict_BLK_CHAR extends AStrictValue implements IObjStructFormat0
{
			private static final Strict_BLK_CHAR INSTANCE [];
			static{
				INSTANCE = new Strict_BLK_CHAR[65536];
				char v = Character.MIN_VALUE;
				for(int i=0;i<65536;i++)
				{
					INSTANCE[i]  = new Strict_BLK_CHAR(v);
					v++;
				};
			};
			
			public final char v;
	private Strict_BLK_CHAR(char v)
	{
		this.v = v;
	};
	public static Strict_BLK_CHAR valueOf(char v)
	{
		return INSTANCE[(int)v-Character.MIN_VALUE];
	};
	public String toString(){ return "Strict_BLK_CHAR("+v+")";};
	@Override public char blockCharValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_BLK_CHAR)) return false;
		return ((Strict_BLK_CHAR)x).v == v;
	};
};