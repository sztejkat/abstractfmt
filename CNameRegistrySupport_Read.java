package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.util.TreeMap;


/**
	A support for name registry based
	optimization at reading side.
	<p>
	Internally this class is nothing more 
	than a simple table of fixed size which
	maps index to a String.
	<p>
	If the stream handling code detects the 
	"begin" signal with registration data it
	is supposed to call {@link #registerBeginName}
	
	@see CNameRegistrySupport_Write
*/
public class CNameRegistrySupport_Read
{
 		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CNameRegistrySupport_Read.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CNameRegistrySupport_Read.",CNameRegistrySupport_Read.class) : null;
       
			private final String [] table;
		
	/** Creates name registry pre-allocating name table
	@param capacity name allocation table size. Keep in mind,
		that <code>String[capacity]</code> or alike structure
		will allocated for this structure either now or in the
		future. Using large numbers will place unnecessary memory
		burden. 
	*/	
	public CNameRegistrySupport_Read(int capacity)
	{
		assert(capacity>=0):"capacity="+capacity;
		if (TRACE) TOUT.println("new(capacity="+capacity+")");
		this.table = new String[capacity];
	};
	/** Tests if name is registered, linear cost.
	@param name name to check
	@return true if registered
	*/
	private boolean containsName(String name)
	{
		for(String s: table)
		{
			if ((s!=null)&&(name.equals(s))) return true;
		};
		return false;
	};
	/** Registers name for optimization
	@param name to register, non-null.
	@param assign_number a number which is read from stream and is expected to
			denote the name.
	@throws EFormatBoundaryExceeded if number is too large	
	@throws EBrokenFormat if there is name registered for that number
	@throws AssertionError if number is negative
	@throws AssertionError if the name is already registered under different number.
		This is assertion due to high cost of a lookup.
		This is intended, as there is no logical fault in having same name under two numbers, however
		it shows that something did happen wrong at writing side of a format.
	*/
	public void registerBeginName(String name, int assign_number)throws EFormatBoundaryExceeded,EBrokenFormat
	{
		if (TRACE) TOUT.println("registerBeginName(\""+name+"\","+assign_number+")");
		assert(name!=null);
		assert(assign_number>=0);
		assert(!containsName(name)):"name "+name+" is already registered";
		if (assign_number>=table.length) throw new EFormatBoundaryExceeded("assign_number="+assign_number+" out of bounds");
		if (table[assign_number]!=null) throw new EBrokenFormat("assign_number="+assign_number+" already in use");
		table[assign_number] = name;
	};
	/** Checks if specified number do denote the name
	@param assigned_number a number read from stream
	@throws EFormatBoundaryExceeded if number is too large
	@throws EBrokenFormat if nothing is registered under that number
	@throws AssertionError if number is negative 
	@return String registered for that number.
	*/
	public String getOptimizedName(int assigned_number)throws EFormatBoundaryExceeded,EBrokenFormat
	{
		if (TRACE) TOUT.println("getOptimizedName("+assigned_number+") ENTER");
		assert(assigned_number>=0);
		if (assigned_number>=table.length) throw new EFormatBoundaryExceeded("assign_number="+assigned_number+" out of bounds");
		String s = table[assigned_number];
		if (s==null) throw new EBrokenFormat("assigned_number="+assigned_number+" is not registered yet");
		if (TRACE) TOUT.println("getOptimizedName("+assigned_number+")=\""+s+"\" LEAVE");
		return s;
	};
};