package sztejkat.abstractfmt;
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
			/** A registration data description. */
			public final static class Name
			{
						private final String name;
						private final int index;
						private boolean has_been_written;
					
					Name(String name, int index)					
					{
						this.index = index;
						this.name = name;						
					}
					/** Name of a signal registered with this descriptor.  
					@return a name. 
					*/
					public String getName(){ return name; };
					/** Returns an index assigned to name 
					@return 0...*/
					public int getIndex(){ return index; };
					/** Checks if name needs writing a registration
					data to a stream and remembers the status.
					@return true if it needs, false if already done. One this method
						returns true it will return false in all subseqent calls */
					public boolean needsStreamRegistartion()
					{
					 	if (!has_been_written)
					 	{
					 		has_been_written = true;
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
		/** Creates
			@param capacity maximum capacity of stream names.
				Up to that count can be registered with {@link #optimizeBeginName}.
		*/
		public CNameRegistrySupport_Write(int capacity)
		{
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
			assert(name!=null);
			//test boundary saturation
			if (assign_next>=capacity_limit) return false;
			//test if already there
			if (map.containsKey(name)) return true;
			//assign next possible index.
			map.put(name, new Name(name, assign_next++));
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
};