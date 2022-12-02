package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;
/**
	Implementation of {@link IFormatLimits}
	<p>
	Classes using it should call {@link #enterStruct}
	and {@link #leaveStruct} at apropriate moments.
*/
abstract class AFormatLimits implements IFormatLimits,Closeable
{
				/** A maximum signal name length, initialized to 1024  */
				private int current_max_signal_name_length = 1024;
				/** A max struct recursion, default = 0; control disabled */
				private int max_struct_recursion = -1;
				/** A current recursion depth */
				private int current_recursion_depth = 0;
				
				
		/* **************************************************
		
		
				Construction
		
		
		***************************************************/
		/**
			Subclasses should take care to 
			invoke {@link #initializeToSupportedLimits}
		*/
		AFormatLimits(){};
		
		/** See constructor.
		
			Sets limits to either predefined values
			or supported bounds
		*/
		protected void initializeToSupportedLimits()
		{
			//name
			int l = getMaxSupportedSignalNameLength();
			
			assert(current_max_signal_name_length==1024); //<-from defaults
			if (l< current_max_signal_name_length) 
						current_max_signal_name_length = l;			
			assert(max_struct_recursion==-1); //<-from defaults, unbound.			
			int r = getMaxSupportedStructRecursionDepth();
			assert(r>=-1);
			if (r!= max_struct_recursion)
					max_struct_recursion = r;
		};
		
				
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
	       if (max_struct_recursion!=-1)
	       {
	       		if (current_recursion_depth>=max_struct_recursion) throw new EFormatBoundaryExceeded("maximum structure recursion depth of "+max_struct_recursion+" is exceeded");
	       };	       
	       if (++current_recursion_depth<0) throw new Error("Structure recursion cursor wrapped around");
	    };
	    /**
	    	Should be invoked when structure is left
	    	@throws EFormatBoundaryExceeded if there is no unclosed structure;
	    	@see #getCurrentStructRecursionDepth
	    */
	    protected void leaveStruct()throws EFormatBoundaryExceeded
	    {
	    	if (current_recursion_depth==0) throw new EFormatBoundaryExceeded("No unclosed structure");
	    	current_recursion_depth--;
	    };
	    /** Returns current recursion depth tracked by {@link #enterStruct} and {@link #leaveStruct} 
	    @return recursion depth, 0 no struct is open.
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
			
			This method uses {@link #enterStruct} and {@link #leaveStruct} to track
			depth of structs recursion 
		*/
		@Override public void setMaxStructRecursionDepth(int max_depth)throws IllegalStateException
		{
				assert(max_depth>=-1):"max_depth="+max_depth;
				
				//test system limits
				int supported = getMaxSupportedStructRecursionDepth();
				assert(supported>=-1);
				if (supported==-1)
				{
					//unbound case, nothing to check.
				}else
				{
					//bound case, not allow unbound or above limit
					if (max_depth==-1) throw new IllegalArgumentException("Requested un-bound limit, but format does not support unlimited recursion");
					if (max_depth>supported)
						throw new IllegalArgumentException("Requested max_depth="+max_depth+" is greater than format supports getMaxSupportedEventRecursionDepth()="+supported);
				};
				
				//test against current recursion level
				if (max_depth!=-1)
				{
				   if ( max_depth<current_recursion_depth )
				   	  	 throw new IllegalStateException("Requested max_depth="+max_depth+" is shallower than current depth ="+current_recursion_depth);
			    };
		   	    this.max_struct_recursion=max_depth;
		};
};