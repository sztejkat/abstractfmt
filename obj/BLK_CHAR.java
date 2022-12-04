package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeCharBlockImpl}. */
public final class BLK_CHAR extends ACharValue implements IObjStructFormat0
{
			private static final BLK_CHAR INSTANCE [];
			static{
				INSTANCE = new BLK_CHAR[65536];
				char v = Character.MIN_VALUE;
				for(int i=0;i<65536;i++)
				{
					INSTANCE[i]  = new BLK_CHAR(v);
					v++;
				};
			};
			
			public final char v;
	private BLK_CHAR(char v)
	{
		this.v = v;
	};
	public static BLK_CHAR valueOf(char v)
	{
		return INSTANCE[(int)v-Character.MIN_VALUE];
	};
	public String toString(){ return "BLK_CHAR("+v+")";};
	@Override public char charValue(){ return v; };
};