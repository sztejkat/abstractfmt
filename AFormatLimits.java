package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
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
 		 private static final long TLEVEL = SLogging.getDebugLevelForClass(AFormatLimits.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AFormatLimits.",AFormatLimits.class) : null;
       

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
			Creates, initialized to default limits.
			<p>
			This is NOT checked if default limits:
			(1024 characters and inifinite recursion)
			are in supported bounds.
			<p>
			Subclasses which do override {@link #getMaxSupportedSignalNameLength}/{@link #getMaxSupportedStructRecursionDepth}
			to report supported limits which may not cover
			above defaults and have limits not hardcoded in those methods are expected to invoke {@link #trimLimitsToSupportedLimits}.
			If they won't call set limits may be above boundaries.
			<p>
			This a bit tricky apporach is necessary since overriden
			{@link #getMaxSupportedSignalNameLength}/{@link #getMaxSupportedStructRecursionDepth}
			may in some cases report limits which are initialized in subclass constructor
			and any call to them made in this class constructor will return 0 since subclass fields
			won't be initialized yet.
			<p>
			This constructor will check if name limit is non-zero and will call {@link #trimLimitsToSupportedLimits} automatically.
			This means that classes which do:
			<pre>
			protected int getMaxSupportedSignalNameLength(){ return 100; };
			....
			</pre>
			do not have to call {@link #trimLimitsToSupportedLimits} but those which do:
			<pre>
			class X ...
			{
					int limit;
				X(...
				{
					limit = 100;
					<b>trimLimitsToSupportedLimits();</b>
				}
				protected int getMaxSupportedSignalNameLength(){ return limit; };
			}
			</pre>
			have to make that call.
		*/
		AFormatLimits()
		{
			//Check if supported limits are initialized?
			//Zero is not allowed for name limit, so it is a good initialization
			//indicator.
			if (getMaxSupportedSignalNameLength()!=0)
							trimLimitsToSupportedLimits();
		};
		
		/** 
			Makes sure that current limits do not exceed those supported.
			<p>
			Requires that {@link #getMaxSupportedSignalNameLength}
			and {@link #getMaxSupportedStructRecursionDepth} are 
			initialized.
			
			@see AFormatLimits#AFormatLimits
		*/
		protected void trimLimitsToSupportedLimits()
		{
			//name
			int l = getMaxSupportedSignalNameLength();		
			assert(l>0) : "possibly not initialized?";
			if (l< current_max_signal_name_length) 
						current_max_signal_name_length = l;			
			int r = getMaxSupportedStructRecursionDepth();
			assert(r>=-1);
			if (r!= max_struct_recursion)
					max_struct_recursion = r;
			if (TRACE) TOUT.println("initializeToSupportedLimits() current_max_signal_name_length="+current_max_signal_name_length+
									",max_struct_recursion="+max_struct_recursion);
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
	       if (TRACE) TOUT.println("enterStruct() @current_recursion_depth="+current_recursion_depth+" max_struct_recursion="+max_struct_recursion);
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
	        if (TRACE) TOUT.println("leaveStruct() @current_recursion_depth="+current_recursion_depth);
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
			if (TRACE) TOUT.println("close()");
			this.current_recursion_depth=0;
		}
		/* ********************************************************
		
				IFormatLimits		
		
		********************************************************/
		@Override public void setMaxSignalNameLength(int characters)
		{		
			assert(characters>0):"characters="+characters+" <= 0";
			assert(getMaxSupportedSignalNameLength()>0):"not initialized?";
			if (TRACE) TOUT.println("setMaxSignalNameLength("+characters+")");
			if(characters>getMaxSupportedSignalNameLength()) throw new IllegalArgumentException("characters="+characters+" >getMaxSupportedSignalNameLength()="+getMaxSupportedSignalNameLength());
			this.current_max_signal_name_length=characters;
		};
		@Override public final int getMaxSignalNameLength()
		{
			return current_max_signal_name_length; 
		};
		
		@Override public final int getMaxStructRecursionDepth()
		{
			return max_struct_recursion; 
		};
		
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
				if (TRACE) TOUT.println("setMaxStructRecursionDepth("+max_depth+")"+
										" supported="+supported+
										" current_recursion_depth="+current_recursion_depth);
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
		/** {@inheritDoc}
		Remember to call {@link #trimLimitsToSupportedLimits} if You override it 
		to return non-hardcoded value */
		@Override public abstract int getMaxSupportedSignalNameLength();
		/** {@inheritDoc}
		Remember to call {@link #trimLimitsToSupportedLimits} if You override it 
		to return non-hardcoded value */
		@Override public abstract int getMaxSupportedStructRecursionDepth();
};