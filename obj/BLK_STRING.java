package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeStringImpl}. */
public final class BLK_STRING  extends ACharValue implements IObjStructFormat0
{
			private static final BLK_STRING INSTANCE [];
			static{
				INSTANCE = new BLK_STRING[65536];
				char v = Character.MIN_VALUE;
				for(int i=0;i<65536;i++)
				{
					INSTANCE[i]  = new BLK_STRING(v);
					v++;
				};
			};
			
			public final char v;
	private BLK_STRING(char v)
	{
		this.v = v;
	};
	public static BLK_STRING valueOf(char v)
	{
		return INSTANCE[(int)v-Character.MIN_VALUE];
	};
	public String toString(){ return "BLK_STRING("+v+")";};
	@Override public char charValue(){ return v; };
};