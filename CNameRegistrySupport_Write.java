package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.util.TreeMap;
/**
	A support class for name registry, writing end.
	<p>
	This registry support maps <code>String</code>
	to <code>int</code> and is intended to aid implementation
	of {@link IStructWriteFormat#optimizeBeginName}
	<p>
	The implementation is nothing more sophisticated
	than <code>TreeMap</code>.
	<p>
	The optimized implementation is expected to write into
	a stream a three kind "signals":
	<ul>
		<li>direct endcoded <code>String</code> named "begin" signals;</li>
		<li>indirect encoded named begin signals, with a sub-range of <code>int</code>
		used as a short form name;</li>
		<li>a mixed "begin" signal which both uses the name and assign a number
		to it;</li>
	</ul>
	@see CNameRegistrySupport_Read
*/
public class CNameRegistrySupport_Write
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CNameRegistrySupport_Write.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CNameRegistrySupport_Write.",CNameRegistrySupport_Write.class) : null;

			/** A registration data description. */
			public final class Name
			{
						/** Name */
						private final String name;
						/** Index assigned during registration */
						private final int index;
						/** Order assigned during a call to 
						{@link #needsStreamRegistartion} which returned true */
						private int order;
						/** Set by {@link #needsStreamRegistartion} */
						private boolean has_been_written;
					
					Name(String name, int index)					
					{
						if (TRACE) TOUT.println("new Name(\""+name+"\", index="+index+")");
						this.index = index;
						this.name = name;						
					}
					/** Name of a signal registered with this descriptor.  
					@return a name. 
					*/
					public String getName(){ return name; };
					/** Returns an index assigned to name during
					construction. 
					@return 0...*/
					public int getIndex(){ return index; };
					/** Returns an unique ordinal number assigned during 
					first call to {@link #needsStreamRegistartion} 
					@return 0..., unique ordinal number
					@throws AssertionError if order is not assigned yet*/
					public int getOrder()
					{
						 assert(has_been_written):"order not assigned yet";
						 return order; 
					};
					/** Checks if name needs writing a registration
					data to a stream and remembers the status.
					@return true if it needs, false if already done. One this method
						returns true it will return false in all subseqent calls */
					public boolean needsStreamRegistartion()
					{
					 	if (!has_been_written)
					 	{
					 		if (TRACE) TOUT.println("Name.needsStreamRegistartion()=true");
					 		has_been_written = true;
					 		order = order_next++;
					 		assert(order_next<=assign_next);
					 		return true;
					 	};
					 	return false;
					}; 
			};
				/** A map */
				private final TreeMap<String,Name> map;
				/** Capacity limit */
				private final int capacity_limit;
				/** Next index to be assigned */
				private int assign_next;	
				/** Next order to be assigned */
				private int order_next;
		/** Creates
			@param capacity maximum capacity of stream names.
				Up to that count can be registered with {@link #optimizeBeginName}.
		*/
		public CNameRegistrySupport_Write(int capacity)
		{
			if (TRACE) TOUT.println("new(capacity="+capacity+")");
			assert(capacity>=0):"capacity="+capacity;			
			this.map = new TreeMap<String,Name>();
			this.capacity_limit = capacity;
		};
		/** Registers name in name optimization map, if possible.
		@param name name to register
		@return true if name could be registered or is already registered.
		*/
		public boolean optimizeBeginName(String name)
		{
			if (TRACE) TOUT.println("optimizeBeginName(\""+name+"\") ENTER");
			assert(name!=null);
			//test boundary saturation
			if (assign_next>=capacity_limit)
			{
				 if (TRACE) TOUT.println("optimizeBeginName()=false, no more space, LEAVE");
				 return false;
			};
			//test if already there
			if (map.containsKey(name))
			{
				if (TRACE) TOUT.println("optimizeBeginName()=true, already registered, LEAVE");
			 	return true;
			 };
			//assign next possible index.
			map.put(name, new Name(name, assign_next++));
			if (TRACE) TOUT.println("optimizeBeginName()=true, registered as new, LEAVE");
			return true;
		};
		/** Checks if name is registered in optimization base
		and returns assigned index.
		@param name non-null name.
		@return null or name descriptor
		*/
		public Name getOptmizedName(String name)
		{
			return map.get(name);
		};
		/** Clears everything to virgin state */
		public void clear()
		{
			map.clear();
			assign_next = 0;
			order_next =0;
		};
};