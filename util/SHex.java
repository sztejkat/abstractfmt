package sztejkat.abstractfmt.util;

/**
	A common decimal-to-hex and reverse conversions
	used in XML and JSON formats.
*/
public final class SHex
{
				/** Binary nibble to hex conversion table */
				private static final char [] _D2HEX= new char[]{'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	/** Prevents creating instances of this class */ 			
	private SHex(){};
	
	/** Nibble to hex conversion
	@param nibble 0...15
	@return '0'...'F' character.
	*/
	public static char D2HEX(int nibble)
	{
		assert((nibble & 0x0F)==nibble);
		return _D2HEX[nibble];
	}		
	/** Hex to nibble conversion
	@param digit 0...9,a...f,A...F
	@return value 0x0...0x0F or -1 if not hex digit.
	*/
	public static int HEX2D(char digit)
	{
		if ((digit>='0')&&(digit<='9')) return digit-'0';
		if ((digit>='a')&&(digit<='f')) return digit-'a'+10;
		if ((digit>='A')&&(digit<='F')) return digit-'A'+10;
		return -1;
	};
};