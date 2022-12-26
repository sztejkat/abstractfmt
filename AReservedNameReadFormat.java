package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.util.TreeSet;
import java.util.TreeMap;

/**
	A reading counterpart for {@link AReservedNameWriteFormat}.
	<p>
	This reading counterpart do require, that the same set of 
	reserved names is registered in the reading and writing end
	by hand. Opposite to name registry this set is <u>not</u>
	passed through the stream events.
	<p>
	This class assumes, that the writing end is using 
	<pre>
		{@link AReservedNameWriteFormat#beginReserved}.
		...
		{@link AReservedNameWriteFormat#end}.
	</pre>
	to surround some section of a stream which requires that
	the reading side is turned into some special state inside
	surrounded fragment.
	<p>
	This class itself does not provide any facility to trace this
	state and limits itself only to provide an API which allows to
	detect reserved name and un-escape it.
	<p>
	A user of this class can do:
	<pre>
		String n = next();
		if (isReservedName(n))
		{
			//do specific action
		}else
		{
			n = unescape(n);
			//process name a usuall way.
		}
	</pre>
	
	<h1>Name length limit</h1>
	The fact that some names are escaped do mean, that the real logic name 
	limit set on stream may be one character lower than the limit set on the underlying
	stream.
	<p>
	Since this condition do vary depending on the use of format and selected
	set of reserved names it is up to user of this format to ensure,
	that limit is large enough.
*/
public abstract class AReservedNameReadFormat extends AStructReadFormatAdapter
{
					/** Reserved words sets */
					private final TreeSet<String> reserved;
					/** Maps escaped forms of reserved names to un-escaped.
					This helps to avoid memory allocation during un-escaping 
					process */
					private final TreeMap<String,String> escaped;
					/** An escape character */
					private final char escape;
	/** Creates
		@param engine see {@link AStructReadFormatAdapter#AStructReadFormatAdapter}
		@param escape an escape character. This character must not be a first character of any reserved name.
				See {@link #isReservedName}
		@see #reserveName
	*/
	protected AReservedNameReadFormat(IStructReadFormat engine, char escape)
	{
		super(engine);
		this.reserved = new TreeSet<String>();
		this.escaped = new TreeMap<String,String>();
		this.escape = escape;
	};	
	/* *********************************************************
	
			Services provided for subclasses
	
	**********************************************************/
	/** Reserves name. Can be used on closed stream.
	@param name name to reserve. If reserved more than once nothing happens.
			It cannot start with {@link #escape} and can't be null.
	@throws IllegalArgumentException if either name of signal or its escaped
			form is longer than the current {@link #getMaxSignalNameLength}.
	*/
	protected void reserveName(String name)
	{
		assert(name!=null);
		final int nl = name.length();
		assert(
				(nl==0)
				||
				(
					(nl!=0)
					&&
					(name.charAt(0)!=escape)
				)):"name "+name+" starts with escape. It can't be";
		int max = getMaxSignalNameLength();
		
		if (nl>max) throw new IllegalArgumentException("reserved name \""+name+"\" is longer than set limit");
		if (nl+1>max) throw new IllegalArgumentException("reserved name \""+name+"\" escaped form is longer than set limit");
		
		reserved.add(name);
		escaped.put(escape+name,name);
	};
	/** Checks if name is reserved
	@param name a name to check 
	@return true if it is
	*/
	protected final boolean isReservedName(String name){ return reserved.contains(name); };
	/** This method takes a specified name and un-escapes it. 
	@param name an escaped form or non-escaped form. Passing a reserved name here 
		is pointless, but won't cause a failure and it will be processed as a normal
		name.
	@return either name or it's un-escaped form.
	@throws EBrokenFormat if name escapes a name which is not a known reserved name.
	*/	
	protected final String unescape(String name)throws EBrokenFormat
	{
		//quick check
		int n = name.length();
		if (n>=1)
		{
			//Now first character may be escape?
			if (name.charAt(0)==escape)
			{
				//It may have second char, but does not have to because empty reserved name is allowed. 				
				if ((n>=2)&&(name.charAt(1)==escape))
				{
					//double escape. Sadly we need to do memory reservation.
					return name.substring(1);
				}else
				{
					//it must be a reserved word then. To save on allocation we 
					//do pick it from a map.
					final String u = escaped.get(name);
					//Now if escaped map does not containt the name format is broken
					//because it means we have escaped something which is not a reserved word.
					if (u==null) throw new EBrokenFormat("unexpected escaped name \""+name+"\". Inconsistent set of reserved names?");
					return u;
				}
			}else
			{
				//no, not escaped
				return name;
			}
		}else
			return name; //cannot be escaped, too short.
	};
};