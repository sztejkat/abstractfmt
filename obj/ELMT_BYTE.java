package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeByteImpl}. */
public final class ELMT_BYTE extends AByteValue implements IObjStructFormat0
{
			private static final ELMT_BYTE INSTANCE [];
			static{
				INSTANCE = new ELMT_BYTE[256];
				byte v = Byte.MIN_VALUE;
				for(int i=0;i<256;i++)
				{
					INSTANCE[i]  = new ELMT_BYTE(v);
					v++;
				};
			};
			public final byte v;
			
	private ELMT_BYTE(byte v)
	{
		this.v = v;
	};
	public static ELMT_BYTE valueOf(byte v)
	{
		return INSTANCE[(int)v-Byte.MIN_VALUE];
	};
	public String toString(){ return "ELMT_BYTE("+v+")";};
	@Override public byte byteValue(){ return v; };
	@Override public boolean equalsTo(IObjStructFormat0 x)
	{
		if (x==null) return false;
		if (!(x instanceof ELMT_BYTE)) return false;
		return ((ELMT_BYTE)x).v == v;
	};
};