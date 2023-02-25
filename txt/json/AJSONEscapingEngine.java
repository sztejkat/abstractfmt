package sztejkat.abstractfmt.txt.json;
import sztejkat.abstractfmt.txt.AEscapingEngine;
import java.io.IOException;
/**
	Implements JSON escapes necessary inside JSON strings.
*/
abstract class AJSONEscapingEngine extends AEscapingEngine
{
				/** Hex conversion table for escaping */
				private static final char [] HEX = new char[]
										{
											'0','1','2','3','4','5','6','7','8','9',
											'A','B','C','D','E','F'
										};
	/* ******************************************
	
			AEscapingEngine
	
	********************************************/
	/** Forces escaping of " and \
	*/
	@Override protected boolean mustEscape(char c)
	{
		//Note: This is enough to consider chars since all upper
		//		code-points will be automatically split back
		//		to UTF-16 surogates and all improper surogates
		//		are also automatically escaped.
		//
		//		JSON is UTF-16 aware and allows surogate pairs
		//		which do not have to be escaped.
		return 
			(
				//required to escape.
					(c=='\"')
					||
					(c=='\\')
				);
	};	
	/** Always false, no additional escapes */
	@Override protected boolean mustEscapeCodepoint(int code_point)
	{ 
		//No more considerations.
		return false; 
	};
	/** Performs either short escape or hex escape depending on character.
	@param c what to generate
	@throws IOException if failed.
	*/
	@Override protected void escape(char c)throws IOException
	{
		//We need to escape every char but some faster, some
		//using full escape.
		switch(c)
		{
			case '\"':
						out('\\');out('\"'); break;
			case '\\': 
						out('\\');out('\\'); break;
			default	 : hex_escape(c);
		}
	};
	/** Generates \\uXXXX hex escape for character 
	@param c what to generate
	@throws IOException if failed.
	*/
	protected void hex_escape(char c)throws IOException
	{
		out('\\');
		out('u');
		for(int i=4;--i>=0;)
		{
			int nibble = (c & 0xF000)>>>(4+4+4);
			out(HEX[nibble]);
			c<<=4;
		};
	};
};