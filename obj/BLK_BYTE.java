package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeByteBlockImpl}. */
public final class BLK_BYTE extends AByteValue implements IObjStructFormat0
{
			private static final BLK_BYTE INSTANCE [];
			static{
				INSTANCE = new BLK_BYTE[256];
				byte v = Byte.MIN_VALUE;
				for(int i=0;i<256;i++)
				{
					INSTANCE[i]  = new BLK_BYTE(v);
					v++;
				};
			};
			public final byte v;
			
	private BLK_BYTE(byte v)
	{
		this.v = v;
	};
	public static BLK_BYTE valueOf(byte v)
	{
		return INSTANCE[(int)v-Byte.MIN_VALUE];
	};
	public String toString(){ return "BLK_BYTE("+v+")";};
	@Override public byte byteValue(){ return v; };
};