package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	Adds signal name registry to support {@link #optimizeBeginName}.
	<p>
	The signal name registry requires that the set of signals:
	<pre>
			begin
			end
			<i>optional:</i>
			end-begin
	</pre>
	is extended to:
	<pre>
			begin-direct <i>(with string name)</i>
			begin-and-register <i>(with string name and numeric argument)</i>
			begin-registered <i>(with numeric argument)</i>
			end
			<i>optional:</i>
			end-begin-direct <i>(with string name)</i>
			end-begin-and-register <i>(with string name and numeric argument)</i>
			end-begin-registered <i>(with numeric argument)</i>
	</pre>
*/
public abstract class ARegisteringStructWriteFormat extends AStructWriteFormatBase0
{
				/** Signal names registry. Null if registry is disabled */
				private final CNameRegistrySupport_Write registry;
				
		/** Creates
		@param name_registry_capacity capactity of name registry used
			to support {@link #optimizeBeginName}. Zero to disable optimization.
		*/
		protected ARegisteringStructWriteFormat(int name_registry_capacity)
		{
				this.registry = 
						name_registry_capacity == 0 ? null: new CNameRegistrySupport_Write(name_registry_capacity);
		};
		
		/* ***********************************************************************
		
				Services required from subclasses
		
		
		************************************************************************/
		/** Should write single "begin" signal as {@link #begin} do specify
		plus all the information necessary to indicate that
		subsequent calls to {@link #beginRegisteredImpl(int,int)} with <code>(index,order)</code>
		as an argument should have the same logic effect as {@link #beginImpl} with
		<code>name</code>.
		<p>
		The implementation may choose to use either <code>index</code> or <code>order</code>
		as a "signal identifier" and implement one of following policies:
		<ul>
			<li>direct name mapping, in which <code>index</code> is written to a stream
			together with a <code>name</code> during registration.
			Subsequent calls to <code>beginRegisteredImpl(index,order)</code> should
			write <code>index</code> to the stream;
			</li>
			<li>direct order based mapping, in which <code>order</code> is written to a stream
			together with a <code>name</code> during registration.
			Subsequent calls to <code>beginRegisteredImpl(index,order)</code> should
			write <code>order</code> to the stream;
			</li>			
			<li>indirect name mapping, in which neither <code>index</code> nor <code>order</code>
			is written together with a <code>name</code>, but it is assumed that reading side will
			count registration events and restore <code>order</code> from that count.
			Subsequent calls to <code>beginRegisteredImpl(index,order)</code> should
			write <code>order</code> to the stream;
			</li>			 
		</ul>
		The choice between methods should take in an account following facts:
		<ul>
			<li>direct methods are easier to debug, especially in human readable formats;</li>
			<li>direct methods theoretically allow to un-register the name in future,
			altough this format does not provide such functionality;</li>
			<li>indirect method is more compact and especially well suited for non-human readable formats
			like binary ones;</li>
			<li>index based methods will assign smaller, possibly more compact encoded numbers,
			to names <u>registered</u> first, while order based methods to names <u>used</u> first.
			This means, that a {@link ITypedStructWriteFormat} implementation
			over a compact binary format with variable length "signal identifier" encoding may benefit
			strongly from <u>direct index based</u> method, because such an implementation
			may after opening a stream quickly register names describing types as optimized ones
			and thous make them shortest possible;</li>
		</ul>
		Will be called in sane conditions.
		<p>
		Will not be called if name registry capacity is zero.
		@param name a sane, validated name.
		@param index an index assigned to it during registration process.
				Will be in range 0...name_registry_capacity-1 (see constructor).
		@param order an ordinal number assigned to this call.
				This number will start from 0 and be incremented by one 
				after each call to this method, efficiently will also
				be in bounds of 0...name_registry_capacity-1
		@throws IOException as {@link #begin}
		*/
		protected abstract void beginAndRegisterImpl(String name, int index, int order)throws IOException;
		/**
			Alike {@link #beginAndRegisterImpl} but should write a compact end-begin signal pair
			together with registration data.
			<p>
			Default implemented without end-begin optimization
			@param name --//--
			@param index --//--
			@param order --//--
			@throws IOException --//--
		*/
		protected void endBeginAndRegisterImpl(String name, int index, int order)throws IOException
		{
			endImpl();
			beginAndRegisterImpl(name,index,order);
		};
		
		
		/** Should have the same logic effect as {@link #beginImpl} with a name
		passed to {@link #beginAndRegisterImpl}.
		<p>
		Will be called only for subsequent uses of registered names and always with consistent
		pair of numbers.
		@param index index passed to {@link #beginAndRegisterImpl}
		@param order order passed to {@link #beginAndRegisterImpl}
		@throws IOException as {@link #begin}
		*/
		protected abstract void beginRegisteredImpl(int index, int order)throws IOException;
		/** Alike {@link #beginRegisteredImpl} but have the same logic effect as {@link #endBeginImpl} 
		<p>
		Default implemented without end-begin optimization
		
		@param index --//--
		@param order --//--
		@throws IOException --//--
		*/
		protected void endBeginRegisteredImpl(int index, int order)throws IOException
		{
			endImpl();
			beginRegisteredImpl(index,order);
		};
		
		
		
		
		/** Exactly as {@link AStructWriteFormatBase0#beginImpl}.
		Writes begin signal without any registry based optimization.
		@param name --//--
		@throws IOException --//--
		*/ 
		protected abstract void beginDirectImpl(String name)throws IOException;
		/** Exactly as {@link AStructWriteFormatBase0#endBeginImpl}.
		Writes end-begin signal without any registry based optimization.
		<p>
		Default implemented without end-begin optimization
		@param name --//--
		@throws IOException --//--
		*/ 
		protected void endBeginDirectImpl(String name)throws IOException
		{
			endImpl();
			beginDirectImpl(name);
		};
		/* ***********************************************************************
		
				Services for superclass
		
		
		************************************************************************/
		/** Dispatches to {@link #beginDirectImpl},{@link #beginAndRegisterImpl} or 
		{@link #beginRegisteredImpl} */
		@Override protected final void beginImpl(String name)throws IOException
		{
		 	if (registry==null)
		 	{
		 		//no optimization support.
		 		beginDirectImpl(name);
		 	}else
		 	{
		 		//optimization is supported.
		 		CNameRegistrySupport_Write.Name n = registry.getOptmizedName(name);
		 		if (n==null)
		 		{
		 			//not in registry
		 			beginDirectImpl(name);
		 		}else
		 		{
		 			//possibly needs registering
		 			if (n.needsStreamRegistartion())
		 			{
		 				beginAndRegisterImpl(name,n.getIndex(), n.getOrder());
		 			}else
		 			{
		 				beginRegisteredImpl(n.getIndex(),n.getOrder());
		 			};
		 		};
		 	};
		};
		/** Dispatches to {@link #endBeginDirectImpl},{@link #endBeginAndRegisterImpl} or 
		{@link #endBeginRegisteredImpl} 
		*/
		@Override protected final void endBeginImpl(String name)throws IOException
		{
			//Note: same logic as beginImpl, just calls different methods.
		 	if (registry==null)
		 	{
		 		//no optimization support.
		 		endBeginDirectImpl(name);
		 	}else
		 	{
		 		//optimization is supported.
		 		CNameRegistrySupport_Write.Name n = registry.getOptmizedName(name);
		 		if (n==null)
		 		{
		 			//not in registry
		 			endBeginDirectImpl(name);
		 		}else
		 		{
		 			//possibly needs registering
		 			if (n.needsStreamRegistartion())
		 			{
		 				endBeginAndRegisterImpl(name,n.getIndex(), n.getOrder());
		 			}else
		 			{
		 				endBeginRegisteredImpl(n.getIndex(),n.getOrder());
		 			};
		 		};
		 	};
		};
		
		/* ***********************************************************************
		
				IStructWriteFormat
		
		
		************************************************************************/
		/**
		Makes an attempt to put a name into a registry and change the 
		way it will be used later from slow and large, string-based {@link #beginDirectImpl} to
		optimized, fast and compact numeric based {@link #beginRegisteredImpl}.
		<p>
		The actual registration of a name by writing data to the stream will happen at first use
		in call to {@link #begin(String)}.
		@return true if either registry optimization is disabled or  it is enabled
			and name was registered for optimization.
			<p>
			False if the whole capacity of registry
			is already consumed and this name cannot be turned from string form to numeric form.
			<p>
			Returning false has no other impact on functionality except the lack of optimalization. 
		*/
		@Override public boolean optimizeBeginName(String name)
		{
			 if (registry==null) return true; //we don't support optimization.
			 //ok, pass to registery
			 return registry.optimizeBeginName(name);  
	    };
};