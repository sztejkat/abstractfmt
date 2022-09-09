package sztejkat.abstractfmt;

/** Limiting values for streams, adjustable */
public interface IFormatLimits
{
	/* .........................................................
			Signal names
	  .........................................................*/
		/** Allows to set limit for signal name.
		<p>
		Default value is 1024.
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
	  	Default value is zero (disabled)	  	
	  	@return limit set in {@link #setMaxStructRecursionDepth}. Zero means: limit is disabled.
	  	*/
	  	public int getMaxStructRecursionDepth();
	  	/**
	  	Sets current event recursion depth limit. 
	  	
	  	@param max_depth specifies the allowed depth of elements
			nesting. Zero disables limit, 1 sets limit to: "no nested elements allowed",
			2 allows element within an element and so on. 
			Changing limit on the fly is allowed.
		@throws AssertionError if <code>max_depth</code> is negative
		@throws IllegalStateException if specified limit is non zero and lower than
		current event recursion depth, or higher than format allows.
		@see #getMaxSupportedEventRecursionDepth
		*/
	  	public void setMaxStructRecursionDepth(int max_depth)throws IllegalStateException;
		/** Returns 0 if format supports un-bound recursion, otherwise a 
		maximum recursion depth which can be set in {@link #setMaxStructRecursionDepth}
		@return 0 or max recursion depth supported. */
	  	public int getMaxSupportedEventRecursionDepth();
};