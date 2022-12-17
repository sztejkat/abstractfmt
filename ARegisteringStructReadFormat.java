package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	A reading end for {@link ARegisteringStructWriteFormat}
*/
abstract class ARegisteringStructReadFormat extends AStructReadFormatBase0
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ARegisteringStructReadFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ARegisteringStructReadFormat.",ARegisteringStructReadFormat.class) : null;

            /** Signals returned by {@link #readExtSignal} */
			protected static enum TSignalExt
			{
				/** To be returned by {@link #readSignalExt} when 
				it reads what {@link ARegisteringStructWriteFormat#beginDirectImpl} wrote.
				A a side effect the {@link #pickLastSignalExtName} should be set
				to a proper and <u>validated </u> signal name.
				The effect on {@link #pickLastSignalIndex} is unspecified */
				SIG_BEGIN_DIRECT,
				/** To be returned by {@link #readSignalExt} when 
				it reads what {@link ARegisteringStructWriteFormat#beginAndRegisterImpl} wrote.
				A a side effect the {@link #pickLastSignalExtName} should be set
				to a proper and <u>validated </u> signal name
				and {@link #pickLastSignalIndex} should be set to index written to stream */
				SIG_BEGIN_AND_REGISTER,
				/** To be returned by {@link #readSignalExt} when 
				it reads what {@link ARegisteringStructWriteFormat#beginRegisteredImpl} wrote.
				A a side effect the {@link #pickLastSignalExName} should be set
				to null and {@link #pickLastSignalIndex} should be set to index written to stream */
				SIG_BEGIN_REGISTERED,
				/** Alike {@link TSignal#SIG_END} */
				SIG_END,
				/** To be returned by {@link #readSignalExt} when 
				it reads what {@link ARegisteringStructWriteFormat#endBeginDirectImpl} wrote.
				A a side effect the {@link #pickLastSignalExtName} should be set
				to a proper and <u>validated </u> signal name.
				The effect on {@link #pickLastSignalIndex} is unspecified
				*/
				SIG_END_BEGIN_DIRECT,
				/** To be returned by {@link #readSignalExt} when 
				it reads what {@link ARegisteringStructWriteFormat#endBeginAndRegisterImpl} wrote.
				A a side effect the {@link #pickLastSignalExtName} should be set
				to a proper and <u>validated </u> signal name
				and {@link #pickLastSignalIndex} should be set to index written to stream */
				SIG_END_BEGIN_AND_REGISTER,
				/** To be returned by {@link #readSignalExt} when 
				it reads what {@link ARegisteringStructWriteFormat#endBeginRegisteredImpl} wrote.
				A a side effect the {@link #pickLastSignalExName} should be set
				to null and {@link #pickLastSignalIndex} should be set to index written to stream */
				SIG_END_BEGIN_REGISTERED;
			};
			
					/** A registry, can be null if not supported */
					private final CNameRegistrySupport_Read registry;
					/** A value to be returned from {@link #pickLastSignalName} */
					private String last_name_to_be_reported;
					
		/** Creates
		@param name_registry_capacity capactity of name registry used
			to support {@link ARegisteringStructWriteFormat#optimizeBeginName}.
			Zero to disable optimization. This value should be at least equal
			to value used at writing side or an error will occur.
		*/
		protected ARegisteringStructReadFormat(int name_registry_capacity)
		{
				this.registry = 
						name_registry_capacity == 0 ? null: new CNameRegistrySupport_Read(name_registry_capacity);
		};		
		/* *******************************************************
			
			Services required from subclasses
		
		********************************************************/
		/* ------------------------------------------------------------------
					Signal related
		------------------------------------------------------------------*/
		/** Invoked by {@link #readSignal} to move to next signal in stream.
		Returns signal and updates certain variables according to returned
		state. This method is also responsible for checking if name
		is too long and abort the reading process.
		@return signal, non null.
		@throws IOException if fialed.
		@throws EFormatBoundaryExceeded if name length is exceeded. */
		protected abstract TSignalExt readSignalExt()throws IOException;
		
		/** Set by {@link #readSignalExt} when a signal carying index 
		of name was encountered or the order of signal, depending on
		if stream implements direct indexing or indexing by order
		of registration.
		<p>
		If order based indexing is used the order should be bumped
		up each time {@link #readSignalExt} reads the {@link TSignalExt#SIG_BEGIN_AND_REGISTER}
		or {@link TSignalExt#SIG_END_BEGIN_AND_REGISTER}.
		<p>		
		@return index, non-negative.
		@see ARegisteringStructWriteFormat#beginAndRegisterImpl
		*/
		protected abstract int pickLastSignalIndex();
		/** Set by {@link #readSignalExt} when a validated name of 
		begin signal is read. Subsequent calls of this method
		do return <code>null</code>
		<p>
		@return name, null for {@link TSignalExt#SIG_END}
				or when name was already read.
		*/
		protected abstract String pickLastSignalExtName();
		
		
		/* *******************************************************
			
			Services required by a superclass
		
		********************************************************/
		/** Makes sure that name registry is supported
		@throws EBrokenFormat if not
		*/
		private void validateRegistryIsSupported()throws EBrokenFormat
		{
			if (registry==null) throw new EBrokenFormat("Signal names optimization is not supported by this format.");
		};
		/** Piece of code shared by {@link TSignalExt#SIG_END_BEGIN_AND_REGISTER}
		and {@link TSignalExt#SIG_BEGIN_AND_REGISTER} in {@link #readSignal}
		@throws IOException .
		*/
		private void handleRegistration()throws IOException
		{
			validateRegistryIsSupported();
			final int idx = pickLastSignalIndex();
			final String name = pickLastSignalExtName();
			assert(idx>=0);
			assert(name!=null);
			assert(name.length()<=getMaxSignalNameLength());
			//Note: registry is correctly handling all boundary check
			registry.registerBeginName(name, idx);
			//Now pass to superclass contract
			this.last_name_to_be_reported = name;
		};
		/** Piece of code shared by {@link TSignalExt#SIG_END_BEGIN_REGISTERED}
		and {@link TSignalExt#SIG_BEGIN_REGISTERED} in {@link #readSignal}
		@throws IOException .
		*/
		private void handleRegistered()throws IOException
		{
			validateRegistryIsSupported();
			final int idx =  pickLastSignalIndex();
			assert(idx>=0);
			this.last_name_to_be_reported = registry.getOptimizedName(idx);
		};
		/** Implemented to use {@link #readSignalExt} */
		@Override protected final TSignal readSignal()throws IOException
		{
				TSignalExt signal = readSignalExt();
				switch(signal)
				{
						case SIG_BEGIN_DIRECT:
						//Nothing special. Just copy to proper variables.
						{
							this.last_name_to_be_reported = pickLastSignalExtName();
							return TSignal.SIG_BEGIN;
						}
						case SIG_BEGIN_AND_REGISTER:						
						//handle registration
						{
							handleRegistration();
							return TSignal.SIG_BEGIN;
						}
						case SIG_BEGIN_REGISTERED:
						//handle pick-up from registry
						{
							handleRegistered();
							return TSignal.SIG_BEGIN;
						}
						case SIG_END:							
						//Nothing special to do
						{
							this.last_name_to_be_reported = null;
							return TSignal.SIG_END;
						}
						case SIG_END_BEGIN_DIRECT:
						//Nothing special to do
						{
							this.last_name_to_be_reported = pickLastSignalExtName();
							return TSignal.SIG_END_BEGIN;
						}
						case SIG_END_BEGIN_AND_REGISTER:
						//handle registration
						{
							handleRegistration();
							return TSignal.SIG_END_BEGIN;
						}
						case SIG_END_BEGIN_REGISTERED:
						{
							handleRegistered();
							return TSignal.SIG_END_BEGIN;
						}
						default: throw new AssertionError(); 	
				}
		};
		/** Implemented to use {@link #readSignalExt} */
		@Override protected final String pickLastSignalName()
		{
			String n = last_name_to_be_reported;
			last_name_to_be_reported = null;
			return last_name_to_be_reported;
		};
};