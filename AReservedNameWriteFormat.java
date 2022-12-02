package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.util.TreeMap;
/**
	A write format filter which allows to introduce a 
	certain set of begin signal names which are reserved
	and hidden from a user.	
	<p>
	A subclass may register a certain set of names as "reserved".
	Whenever user attempts to {@link #begin} a signal name
	it is tested if signal matches the reserved name.
	If it is, it is pre-pended with an escape char.
	<p>
	If name is not reserved, but starts with escape char,
	and additional escape char is added in front of it.
	<p>
	At last, if it is neither reserved nor starts with escape
	it is written as supplied.
	<p>
	Reserved signals may be written only with a dedicated
	{@link #beginReserved} method.
	<p>
	The reading side runs below process:
	<pre>
		n - read signal name
		if n is one of known reserved names a special action is performed.
		if n starts with escape char, this character is removed
			and modified name is returned from an API. 
	</pre>
	
*/
public class AReservedNameWriteFormat extends AStructWriteFormatAdapter
{
					/** Reserved words, maps word to it's escaped form.
					This kind of operation saves us allocation of temporary
					strings when escaping reserved names. */
					private final TreeMap<String,String> reserved;
					/** An escape character */
					private final char escape;
	/** Creates
		@param engine see {@link AStructWriteFormatAdapter#AStructWriteFormatAdapter}
		@param escape an escape character. This character must not be a first character of any reserved name.
				See {@link #isReservedName}
		@see #reserveName
	*/
	protected AReservedNameWriteFormat(IStructWriteFormat engine, char escape)
	{
		super(engine);
		this.reserved = new TreeMap<String,String>();
		this.escape = escape;
	};
	/* *********************************************************
	
			Services required from subclasses
	
	**********************************************************/
	/** Reserves name 
	@param name name to reserve. If reserved more than once nothing happens.
			It cannot start with {@link #escape} and can't be null.
	*/
	protected void reserveName(String name)
	{
		assert(name!=null);
		assert(
				(name.length()==0)
				||
				(
					(name.length()!=0)
					&&
					(name.charAt(0)!=escape)
				)):"name "+name+" starts with escape. It can't be";
		reserved.put(name, escape+name);
	};
	/** Checks if name is reserved
	@param name a name to check 
	@return true if it is
	*/
	protected final boolean isReservedName(String name){ return reserved.containsKey(name); };
	/* *********************************************************
	
			Services for subclasses.	
	
	**********************************************************/
	/** Writes "begin" signal using specified reserved name directly, without any escaping.
	@param name a reserved name.
	@throws AssertionError if <code>name</code> is null.
	@throws AssertionError if <code>name</code> was not reserved 
		previously with {@link #reserveName}
	@throws IllegalArgumentException if name of signal is too long.
		See {@link IFormatLimits#getMaxSignalNameLength}	 
	@throws EFormatBoundaryExceeded if structure recursion depth control is enabled
		and this limit is exceeded. See {@link IFormatLimits#setMaxStructRecursionDepth}
	@throws IOException if low level i/o fails.
	*/
	protected void beginReserved(String name)throws IOException
	{
		assert(name!=null);
		assert(isReservedName(name));
		super.begin(name);
	};
	
	
	/* ***********************************************************
	
		IStructWriteFormat
	
	
	* ***********************************************************/
	/**
		Overriden to avoid name clash between regular names
		and reserved names.
		@see #beginReserved
	*/
	public void begin(String name)throws IOException
	{
			String escaped_reserved = reserved.get(name);
			if (escaped_reserved!=null)
			{
				//ok, we know reserved for.
				assert(escaped_reserved.charAt(0)==escape);
				super.begin(escaped_reserved);
			}else
			{
				//it is not a reserved word, but it may start with escape
				//which needs to be extracted.
				if ((name.length()!=0)&&(name.charAt(0)==escape))
				{
					//sadly we need to perform buffer allocation this time.
					super.begin(escape+name);
				}else
				{
					super.begin(name);
				};
			};
	};
};