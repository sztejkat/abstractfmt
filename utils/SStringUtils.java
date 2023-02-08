package sztejkat.abstractfmt.utils;

/**
	Various String / StringBuilder related methods
	<p>
	Toolbox class
*/
public final class SStringUtils
{
	private SStringUtils(){};
	/** Compares content of string buffer, case insensitive, with specified text. 
	@param buffer non-null buffer to compare with <code>text</code>
	@param text text to compare
	@return true if identical
	*/
	public static boolean equalsCaseInsensitive(StringBuilder buffer, String text)
	{
		if (buffer.length()!=text.length()) return false;
		for(int i=0,n=buffer.length(); i<n; i++)
		{
			char bc = buffer.charAt(i);
			char tc = text.charAt(i);
			//Since to-lower/upper may be inconsistent in some langs avoid them
			if (
				(Character.isLowerCase(bc) && Character.isLowerCase(tc))
					||
				(Character.isUpperCase(bc) && Character.isUpperCase(tc))
			   )
			   {
			   	   	//compare directly
			   	   	if (bc!=tc) return false;
			   }else
			   {
			   	   //compare lower case
			   	   if (Character.toLowerCase(bc)!=Character.toLowerCase(tc)) return false;
			   };
		};
		return true;
	};
	/** Compares content of string buffer, case ssnsitive, with specified text. 
	@param buffer non-null buffer to compare with <code>text</code>
	@param text text to compare
	@return true if identical
	*/
	public static boolean equalsCaseSensitive(StringBuilder buffer, String text)
	{
		if (buffer.length()!=text.length()) return false;
		for(int i=0,n=buffer.length(); i<n; i++)
		{
			char bc = buffer.charAt(i);
			char tc = text.charAt(i);
			if (bc!=tc) return false;
		};
		return true;
	};
	
};