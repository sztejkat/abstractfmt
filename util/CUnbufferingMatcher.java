package sztejkat.abstractfmt.util;

/**
	A simple class which is provided with an array 
	of strings and attempts to match sequence of
	characters it is presented with with items.
	<p>
	This class does not use any character buffer.
*/
public class CUnbufferingMatcher
{
			/** Set of possible matches */
			private final String [] matches;
			/** Character position in strings to match */
			private int at;
			
	/** Creates matcher which will match sequence
	against specified set of strings
	@param matches non-null, not containing null.
	*/
	public CUnbufferingMatcher(String [] matches)
	{
		assert(matches!=null);
		this.matches = matches;
	};
	/** Restarts matching engine */
	public void reset(){ this.at = 0; };
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
				it's position in table specified in constructor.
			</li>
	*/		
	public int match(char c)
	{
		//persistent disable. Once all were tested and do not start with sequence
		//they can't match, right?
		if (at==-1) return -2;
		//now try each string
		for(int i=matches.length;--i>=0;)
		{
			String s = matches[i];
			if (s.length()<=at) continue;	//too short, can't match.
			if (s.charAt(at)==c)
			{
				//Surely starts with, so either equal or begins with.
				at++;	// next comparison must be with next character.
				return (s.length()==at+1) ? i : -1;
			}
		}
		//none catched, so fail permanently till reset.
		at = -1;
		return -2;
	};
};