package sztejkat.abstractfmt.util;
/**
	A matcher which selects character from maps-to array
	based on match result
*/
public class CUnbufferingMapper extends CUnbufferingMatcher
{
				private char [] result;
	/** Creates matcher which will match sequence
	against specified set of strings
	@param matches non-null, not containing null.
	@param result an array used when match is found
			to fetch value from. Same length as <code>matches</code>.
	*/			
	public CUnbufferingMapper(String [] matches, char [] result)
	{
		super(matches);
		assert(result!=null);
		assert(result.length==matches.length);
		this.result=result;
	};
	/** Assuming c represents next character in matched
	string compares it with set of matches
	@param c next character in string
	@return <ul>
			<li>-1 if there is at least one string 
				which starts with presented sequence of characters;
			</li>
			<li>-2 if there is no such string, none of string matches;</li>
			<li>0... if there is a string which is equal to presented
				sequence of characters. Returned value represents
				value from {@link #result} table at index matching
				index of found string.
			</li>
	*/		
	@Override public int match(char c)
	{
		int r = super.match(c);
		if (r<0) return r;
		return result[r];
	};
};