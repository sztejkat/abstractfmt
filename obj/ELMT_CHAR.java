package sztejkat.abstractfmt.obj;

/** see {@link CObjStructWriteFormat0#writeCharImpl}. */
public final class ELMT_CHAR extends ACharValue implements IObjStructFormat0
{
			private static final ELMT_CHAR INSTANCE [];
			static{
				INSTANCE = new ELMT_CHAR[65536];
				char v = Character.MIN_VALUE;
				for(int i=0;i<65536;i++)
				{
					INSTANCE[i]  = new ELMT_CHAR(v);
					v++;
				};
			};
			
			public final char v;
	private ELMT_CHAR(char v)
	{
		this.v = v;
	};
	public static ELMT_CHAR valueOf(char v)
	{
		return INSTANCE[(int)v-Character.MIN_VALUE];
	};
	public String toString(){ return "ELMT_CHAR("+v+")";};
	@Override public char charValue(){ return v; };
};