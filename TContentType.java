package sztejkat.abstractfmt;
/**
	Lists types of data which can be
	stored in streams. Used internally
	and by a typed stream extension.
*/
public enum TContentType
{
	BEGIN,
	END,
	BOOLEAN(Boolean.TYPE),
	BYTE(Byte.TYPE),
	CHAR(Character.TYPE),
	SHORT(Short.TYPE),
	INT(Integer.TYPE),
	LONG(Long.TYPE),
	FLOAT(Float.TYPE),
	DOUBLE(Double.TYPE),
	BOOLEAN_BLK(boolean[].class),
	BYTE_BLK(byte[].class),
	CHAR_BLK(char[].class),
	STRING(String.class),
	SHORT_BLK(short[].class),
	INT_BLK(int[].class),
	LONG_BLK(long[].class),
	FLOAT_BLK(float[].class),
	DOUBLE_BLK(double[].class);
	
			/** A class identifier of carried element,
			null for {@link #BEGIN} and {@link #END}.
			Uses <code>Integer.TYPE</code> and so on,
			and <code>int[].class</code> and so on.			
			*/
			public final Class<?> clazz;
			
	TContentType(){ clazz=null; };
	TContentType(Class<?> t){ clazz=t; };
};