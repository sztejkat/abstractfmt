package sztejkat.abstractfmt.xml;
import java.io.Reader;
import java.io.IOException;
/**
		Implementation which is using {@link java.io.Reader}
		as a back-end. 
*/
public class CXMLIndicatorReadFormat extends AXMLIndicatorReadFormat
{
				/** Low level output */
				private final Reader input;
				/** Described status */
				private final boolean is_described;
	/** Creates 
	@param input input from which read data
	@param settings xml settings, not null
	@param maximum_idle_characters safety limit, setting upper boundary
		 for comment, processing commands and other skipable characters.
		 <p>
		 Non zero, positive
	@param is_described true to require primitive type description data.
	*/
	public CXMLIndicatorReadFormat(final Reader input,
								   final CXMLSettings settings,
								   final int maximum_idle_characters,
								   boolean is_described
								   )
   {
   		super(settings,maximum_idle_characters);
   		this.is_described=is_described;
   		this.input=input;
   };
   	/* ****************************************************
		
			Services required byAXMLIndicatorReadFormat
			
	*****************************************************/
	/** Called in {@link #close} when closed for a first 
	time.
	@throws IOException if failed.
	*/
	protected void closeOnce()throws IOException
	{	
			input.close();
	};
	/* ************************************************************
	
			Low level I/O
	
	* ************************************************************/
	@Override protected int readFromInput()throws IOException
	{
		return input.read();
	};
   /* ******************************************************
	
			IIndicatorReadFormat
	
	* *****************************************************/
	/** Returns what is specified in constructor */
	@Override public boolean isDescribed(){ return is_described; };
};