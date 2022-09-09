package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;
/**
	Implementation of {@link IFormatLimits}
*/
abstract class AFormatLimits implements IFormatLimits,Closeable
{
				/** A maximum signal name length, initialized to 1024  */
				private int current_max_signal_name_length = 1024;
				/** A max struct recursion, default = 0; control disabled */
				private int max_struct_recursion = 0;
				/** A current recursion depth */
				private int current_recursion_depth =0;
				
	    /* *****************************************************
	    
	    	Services for subclasses
	    
	    
	    ******************************************************/
	    /* --------------------------------------------------
	    	Relating recursion depth
	    --------------------------------------------------*/
	    /**
	    	Should be invoked when structure is entered.
	    	@throws EFormatBoundaryExceeded if current depth limit is exceeded
	    	@throws Error if recursion depth counter wraps around after 2^31 recursions.
	    	@see #getCurrentStructRecursionDepth
	    */	    
	    protected void enterStruct()throws EFormatBoundaryExceeded
	    {
	       if (max_struct_recursion!=0)
	       {
	       		if (current_recursion_depth>=max_struct_recursion) throw new EFormatBoundaryExceeded("maximum structure recursion depth of "+max_struct_recursion+" is exceeded");
	       };	       
	       if (++current_recursion_depth<0) throw new Error("Structure recursion cursor wrapped around");
	    };
	    /**
	    	Should be invoked when structure is left, indented to be used during writes.
	    	@throws IllegalStateException if there is no unclosed structure;
	    	@see #getCurrentStructRecursionDepth
	    */
	    protected void leaveStruct()throws IllegalStateException
	    {
	    	if (current_recursion_depth==0) throw new IllegalStateException("No unclosed structure");
	    	current_recursion_depth--;
	    };
	    /**
	    	Should be invoked when structure is left, indented to be used during reading.
	    	<p>
	    	Note: The only difference between this method and {@link #leaveStruct} is in a type of
	    	thrown exception.
	    	@throws EBrokenFormat if there is no unclosed structure;
	    	@see #getCurrentStructRecursionDepth
	    */
	    protected void leaveStructBreak()throws EBrokenFormat
	    {
	    	if (current_recursion_depth==0) throw new EBrokenFormat("No unclosed structure");
	    	current_recursion_depth--;
	    };
	    /** Returns current recursion depth tracked by {@link #enterStruct} and {@link #leaveStruct}/{@link #leaveStructBreak} 
	    @return recursion depht, 0 no struct is open.
	    */
	    protected final int getCurrentStructRecursionDepth(){ return current_recursion_depth; };
		/* ********************************************************
		
				Closable		
		
		********************************************************/
		/** Resets depth counters. */
		@Override public void close()throws IOException
		{
			this.current_recursion_depth=0;
		}
		/* ********************************************************
		
				IFormatLimits		
		
		********************************************************/
		@Override public void setMaxSignalNameLength(int characters)
		{
			assert(characters>0):"characters="+characters+" <= 0";
			if(characters>getMaxSupportedSignalNameLength()) throw new IllegalArgumentException("characters="+characters+" >getMaxSupportedSignalNameLength()="+getMaxSupportedSignalNameLength());
			this.current_max_signal_name_length=characters;
		};
		@Override public final int getMaxSignalNameLength(){ return current_max_signal_name_length; };
		
		@Override public final int getMaxStructRecursionDepth(){ return max_struct_recursion; };
		
		/** {@inheritDoc}
			
			This method uses {@link #enterStruct} and {@link #leaveStruct}/{@link #leaveStructBreak} to track
			depth of structs recursion 
		*/
		@Override public void setMaxStructRecursionDepth(int max_depth)throws IllegalStateException
		{
				assert(max_depth>=0):"max_depth="+max_depth;
				if (max_depth!=0)
				{
				   if ( max_depth<current_recursion_depth )
				   	  	 throw new IllegalStateException("Requested max_depth="+max_depth+" is shallower than current depth ="+current_recursion_depth);
				   if (
				   	   (getMaxSupportedEventRecursionDepth()!=0)
				   	     &&
				   	   ( max_depth>getMaxSupportedEventRecursionDepth())
				   	  )
				   	    throw new IllegalStateException("Requested max_depth="+max_depth+" is greater than format supports getMaxSupportedEventRecursionDepth()="+getMaxSupportedEventRecursionDepth());
		   	    };
		   	    this.max_struct_recursion=max_depth;
		};
};