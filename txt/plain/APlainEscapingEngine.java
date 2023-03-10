package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.AEscapingEngine;
import java.io.IOException;


/**
	An escaping engine implements escaping syntax as defined for
	a "plain" text format.
	<p>
	It is doing as follows:
	<ul>
		<li>The \ is an escape character;</li>
		<li>The \\ represents \ after escaping;</li>
		<li>The \" represents " after escaping;</li>
		<li>The \XXXX; represents (char)0xXXXX where XXXX 
		are from none to <u>up to</u> four digits, leading zeros removed.
		The \; is <code>(char)0</code>.</li>
		<li>bad surogates are hex escaped.</li>
	</ul>
*/
abstract class APlainEscapingEngine extends AEscapingEngine
{
				/** Hex conversion table for escaping */
				private static final char [] HEX = new char[]
										{
											'0','1','2','3','4','5','6','7','8','9',
											'A','B','C','D','E','F'
										};
										
	/* ***************************************************************************
	
			Construction
	
	
	*****************************************************************************/
	protected APlainEscapingEngine(){};
	
	/* **********************************************************
	
			Services for AEscapingEngine
	
	***********************************************************/
	/** Forces escaping " and \  
	@param c what to generate
	*/
	@Override protected boolean mustEscape(char c)
	{
		//Note: This is enough to consider chars since all upper
		//		code-points will be automatically split back
		//		to UTF-16 surogates and all improper surogates
		//		are also automatically escaped.
		switch(c)
		{
			case '\"':
			case '\\': return true;
			default	 : return false;
		}
	};
	/** Always false, no additional escapes */
	@Override protected boolean mustEscapeCodepoint(int code_point){ return false; };
	
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
	
	/** Generates \XXXX; hex escape for character, stripping ALL leading zeroes 
	@param c what to generate
	@throws IOException if failed.
	*/
	protected void hex_escape(char c)throws IOException
	{
		out('\\');
		boolean was_emited = false;
		for(int i=4;--i>=0;)
		{
			int nibble = (c & 0xF000)>>>(4+4+4);
			if (!was_emited)
			{
				if (nibble==0) continue;
			};
			was_emited = true;
			out(HEX[nibble]);
			c<<=4;
		};
		out(';');
	};
};



