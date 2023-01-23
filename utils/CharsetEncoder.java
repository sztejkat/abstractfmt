package sztejkat.abstractfmt.util;
import java.nio.charset.CharsetEncoder;
/**
	A charset encoder wrapper which makes sure that some characters
	or some characters combinations do not reach the lower level
	stream.
	
	<h1>Background information</h1>
	As described in {@link IStreamWriteFormat#writeChar(char[],int,int)}
	we need to accept absolutely any combination of <code>char</code>
	primitives, including invalid or incomple surogate pairs.
	<p>
	Unfortunately invalid or incomplete surogates are not allowed by 
	any standard charset encoder like UTF-8 or UTF-16.
	<p>
	Additionally some formats, like XML may put additional restrictions
	on what characters are allowed in stream or not and may require
	additional escaping.
	<p>
	In generic we need a kind of character encoder which is more flexible
	
*/
public class CEscapingEncoder extends CharsetEncoder
{
				private CharsetEncoder tool;
				
	public CEscapingEncoder(CharsetEncoder tool)
	{
		this.tool = tool;
	};
};