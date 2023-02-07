package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;
import java.io.IOException;

/**
	A common rountines for un-escaping engines for XML processing
*/
abstract class AXMLUnescapingEngineBase extends AUnescapingEngine
{
	/** Compares content of string buffer, case ssnsitive, with specified text. 
	@param buffer non-null buffer to compare with <code>text</code>
	@param text text to compare
	@return true if identical
	*/
	protected static boolean equals(StringBuilder buffer, String text)
	{
		return ATxtReadFormat0.equalsCaseSensitive(buffer,text);
	};
	/** Unescapes &amp; escape. Recognizes symetric set of predefined
	symbols to {@link AXMLEscapingEngineBase#escapeCodePointAsEntity}
	@param collection_buffer a collection buffer carying the whole
			escape sequence, including trailing ; Size limitation
			must be done by caller.
	*/
	protected int uescapeCodePointAsEntity(StringBuilder collection_buffer)throws IOException
	{
		if (collection_buffer.length()<2) throw new EBrokenFormat("Invalid XML entity:"+collection_buffer);
		if (collection_buffer.charAt(0)!='&') throw new EBrokenFormat("Invalid XML entity, bad first character:"+collection_buffer);
		if (collection_buffer.charAt(collection_buffer.length()-1)!=';') throw new EBrokenFormat("Invalid XML entity, expects ; to be last character:"+collection_buffer);
		
		//Now check if numeric escape?
		if ((collection_buffer.length()>=/*&#??;*/3) && (collection_buffer.charAt(1)=='#'))
		{
			//numeric. Test if decimal or hex?
			//Note: to save on creating new String we do it char-by-char over strigbuilder.
			if ((collection_buffer.length()>=/*&#x??;*/4) && (collection_buffer.charAt(2)=='x'))
			{
				//hex
				int v = 0;
				for(int i=3,n=collection_buffer.length()-1;i<n;i++)
				{
					final char digit = collection_buffer.charAt(i);
					final int nibble;
					if ((digit>='0')&&(digit<='9'))
					{
						nibble = digit - '0';
					}else
					if ((digit>='A')&&(digit<='F'))
					{
						nibble = digit - 'A'+10;
					}else
					if ((digit>='a')&&(digit<='f'))
					{
						nibble = digit - 'a'+10;
					}else
						throw new EBrokenFormat("\""+digit+"\" is not hex digit 0...9A...Fa...f");
					v<<=4;
					v|=nibble;
				};
				if ((v<0)||(c>0x10FFFF))throw new EBrokenFormat(collection_buffer+" represents 0x"+Integer.toHexString(v)+" which is not a valid unicode code-point");
				return v;
			}else
			{
				//dec
				int v = 0;
				for(int i=3,n=collection_buffer.length()-1;i<n;i++)
				{
					final char digit = collection_buffer.charAt(i);
					final int digit_value;
					if ((digit>='0')&&(digit<='9'))
					{
						digit_value = digit - '0';
					}else
						throw new EBrokenFormat("\""+digit+"\" is not decimal digit 0...9");
					v=v*10+digit_value;
				};
				if ((v<0)||(c>0x10FFFF))throw new EBrokenFormat(collection_buffer+" represents 0x"+Integer.toHexString(v)+" which is not a valid unicode code-point");
				return v;
			};
		}else
		{
			//A direct set
			//Now a tough choice - use switch-case and allocate String or
			//use direct equals?
			
			//Note: We might use here a TreeMap and support defining of entities, but our format
			//		is specified to NOT allow entity definition. 
			if (equals(collection_buffer,"&lt;")) return '<';
			if (equals(collection_buffer,"&gt;")) return '>';
			if (equals(collection_buffer,"&amp;")) return '&';
			if (equals(collection_buffer,"&apos;")) return '\'';
			if (equals(collection_buffer,"&quot;")) return '\"';
			throw new EBrokenFormat("Unrecognized XML entity: "+collection_buffer);
		}
	};
	
	... todo 
};