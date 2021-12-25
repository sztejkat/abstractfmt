package sztejkat.abstractfmt.xml;
import java.io.Reader;
/**
	A reading counterpart for {@link CXMLIndicatorWriteFormat}
	using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>.
*/	
public class CXMLIndicatorReadFormat extends AXMLIndicatorReadFormat
{
				/** Described status */
				private final boolean is_described;
				
	/** Creates 
	@param input see {@link AXMLIndicatorReadFormat#AXMLIndicatorReadFormat}
	@param settings --//--			
	@param is_described true to require primitive type description data.
	*/
	public CXMLIndicatorReadFormat(final Reader input,
								   final CXMLSettings settings,
								   boolean is_described
								   )
   {
   		super(input, settings);
   		this.is_described=is_described;
   };
   
   /* ******************************************************
	
			IIndicatorReadFormat
	
	* *****************************************************/
	/** Returns what is specified in constructor */
	@Override public boolean isDescribed(){ return is_described; };
	
};