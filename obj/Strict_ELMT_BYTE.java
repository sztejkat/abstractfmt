package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeByteImpl}. */
public final class Strict_ELMT_BYTE extends AStrictValue implements IObjStructFormat0
{
			private static final Strict_ELMT_BYTE INSTANCE [];
			static{
				INSTANCE = new Strict_ELMT_BYTE[256];
				byte v = Byte.MIN_VALUE;
				for(int i=0;i<256;i++)
				{
					INSTANCE[i]  = new Strict_ELMT_BYTE(v);
					v++;
				};
			};
			public final byte v;
			
	private Strict_ELMT_BYTE(byte v)
	{
		this.v = v;
	};
	public static Strict_ELMT_BYTE valueOf(byte v)
	{
		return INSTANCE[(int)v-Byte.MIN_VALUE];
	};
	public String toString(){ return "Strict_ELMT_BYTE("+v+")";};
	@Override public byte byteValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof Strict_ELMT_BYTE)) return false;
		return ((Strict_ELMT_BYTE)x).v == v;
	};
};