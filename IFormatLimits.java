package sztejkat.abstractfmt;

/** Limiting values for streams, adjustable.
	<p>
	This contract defines how to access and adjust limits for signal names length
	and recursion depth. This contract is common for both reading and writing and
	thous it is defined separately.
*/
public interface IFormatLimits
{
	/* .........................................................
			Signal names
	  .........................................................*/
		/** Allows to set limit for signal name.
		<p>
		Default value is 1024 or {@link #getMaxSupportedSignalNameLength}
		whichever is less.
		<p>
		Changing limit on the fly is allowed.
		@param characters name limit, non-zero positive.
		@throws IllegalArgumentException if characters exceeds {@link #getMaxSupportedSignalNameLength}
		*/
		public void setMaxSignalNameLength(int characters);
		/** Returns value set in {@link #setMaxSignalNameLength}
		@return value set in {@link #setMaxSignalNameLength} */
		public int getMaxSignalNameLength();
		/** Returns maximum supported signal name length by this format.
		This limit is non-adjustable and relates to physcial boundaries
		of format.
		@return length limit, non-zero positive, life-time constant */
		public int getMaxSupportedSignalNameLength();
	/* .........................................................
			Events nesting limits.
	  .........................................................*/
	  	/** Returns currently set {@link #setMaxStructRecursionDepth}
	  	<p>
	  	Default value is -1 (disabled)	  	
	  	@return limit set in {@link #setMaxStructRecursionDepth}.
	  	*/
	  	public int getMaxStructRecursionDepth();
	  	/**
	  	Sets current event recursion depth limit. 
	  	
	  	@param max_depth specifies the allowed depth of structures nesting:
	  		<ul>
	  			<li>-1 - limit is disabled;</li>
	  			<li>0 - no structure is allowed;</li>
	  			<li>1 - begin... end - is allowed;</li>
	  			<li>2 - begin begin end end - is allowed;</li>
	  			<li>3 and so on</li>
	  		</ul> 
			Changing limit on the fly is allowed.
		@throws AssertionError if <code>max_depth</code> is &lt;-1
		@throws IllegalStateException if specified limit is enbled and lower than current event recursion depth
		@throws IllegalArgumentException if we request disabling a limit,
			 but format is limited naturally ({@link #getMaxSupportedStructRecursionDepth} returns non -1).
		@throws IllegalArgumentException if max_depth is greater than {@link #getMaxSupportedStructRecursionDepth}
		@see #getMaxSupportedStructRecursionDepth
		*/
	  	public void setMaxStructRecursionDepth(int max_depth)throws IllegalStateException,IllegalArgumentException;
		/** Returns -1 if format supports un-bound recursion, otherwise a 
		maximum recursion depth which can be set in {@link #setMaxStructRecursionDepth}
		@return -1 or max recursion depth supported. */
	  	public int getMaxSupportedStructRecursionDepth();
};