package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeCharImpl}. */
public final class Strict_ELMT_CHAR extends AStrictValue implements IObjStructFormat0
{
			private static final Strict_ELMT_CHAR INSTANCE [];
			static{
				INSTANCE = new Strict_ELMT_CHAR[65536];
				char v = Character.MIN_VALUE;
				for(int i=0;i<65536;i++)
				{
					INSTANCE[i]  = new Strict_ELMT_CHAR(v);
					v++;
				};
			};
			
			public final char v;
	private Strict_ELMT_CHAR(char v)
	{
		this.v = v;
	};
	public static Strict_ELMT_CHAR valueOf(char v)
	{
		return INSTANCE[(int)v-Character.MIN_VALUE];
	};
	public String toString(){ return "Strict_ELMT_CHAR("+v+")";};
	@Override public char charValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_ELMT_CHAR)) return false;
		return ((Strict_ELMT_CHAR)x).v == v;
	};
};