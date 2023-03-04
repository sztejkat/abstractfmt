package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeByteBlockImpl}. */
public final class Strict_BLK_BYTE extends AStrictValue implements IObjStructFormat0
{
			private static final Strict_BLK_BYTE INSTANCE [];
			static{
				INSTANCE = new Strict_BLK_BYTE[256];
				byte v = Byte.MIN_VALUE;
				for(int i=0;i<256;i++)
				{
					INSTANCE[i]  = new Strict_BLK_BYTE(v);
					v++;
				};
			};
			public final byte v;
			
	private Strict_BLK_BYTE(byte v)
	{
		this.v = v;
	};
	public static Strict_BLK_BYTE valueOf(byte v)
	{
		return INSTANCE[(int)v-Byte.MIN_VALUE];
	};
	public String toString(){ return "Strict_BLK_BYTE("+v+")";};
	@Override public byte blockByteValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_BLK_BYTE)) return false;
		return ((Strict_BLK_BYTE)x).v == v;
	};
};