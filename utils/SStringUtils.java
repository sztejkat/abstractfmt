package sztejkat.abstractfmt.utils;

/**
	Various String / StringBuilder related methods
	<p>
	Toolbox class.
*/
public final class SStringUtils
{
	private SStringUtils(){};
	/** Compares content of string buffer, case insensitive, with specified text. 
	<p>
	<i>Note: case insensitive test is intentionally incorrectly handling surogates.</i> 
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
	
	/** Tests if <code>buffer</code> can represent <code>text</code>
	@param text text which should be compared with buffer
	@param buffer buffer to compare
	@return <ul>
				<li>-1 if buffer cannot represent text;</li>
				<li>0 if buffer do represent a starting portion if the text, but not full text;</li>
				<li>1 if buffer do represent text;</li>
			</ul>
	*/
	public static int canStartWithCaseSensitive(String text, StringBuilder buffer)
	{
		final int BL = buffer.length();
		final int TL = text.length();
		if (BL>TL) return -1; //this cannot ever match.
		int lim = BL < TL ? BL : TL;
		for(int i=0;i<lim; i++)
		{
			char bc = buffer.charAt(i);
			char tc = text.charAt(i);
			if (bc!=tc) return -1;
		};
		//Now starting portion did match, but did we match all?
		return BL==TL ? 1 : 0;
	};
	/** Tests if <code>buffer</code> can represent <code>text</code>, case insensitve.
	<p>
	<i>Note: case insensitive test is intentionally incorrectly handling surogates.</i>
	@param text text which should be compared with buffer
	@param buffer buffer to compare
	@return <ul>
				<li>-1 if buffer cannot represent text;</li>
				<li>0 if buffer do represent a starting portion if the text, but not full text;</li>
				<li>1 if buffer do represent text;</li>
			</ul>
	*/
	public static int canStartWithCaseInsensitive(String text, StringBuilder buffer)
	{
		final int BL = buffer.length();
		final int TL = text.length();
		if (BL>TL) return -1; //this cannot ever match.
		int lim = BL < TL ? BL : TL;
		for(int i=0;i<lim; i++)
		{
			char bc = buffer.charAt(i);
			char tc = text.charAt(i);
			if (
				(Character.isLowerCase(bc) && Character.isLowerCase(tc))
					||
				(Character.isUpperCase(bc) && Character.isUpperCase(tc))
			   )
			   {
			   	   	//compare directly
			   	   	if (bc!=tc) return -1;
			   }else
			   {
			   	   //compare lower case
			   	   if (Character.toLowerCase(bc)!=Character.toLowerCase(tc)) return -1;
			   };
		};
		//Now starting portion did match, but did we match all?
		return BL==TL ? 1 : 0;
	};
};