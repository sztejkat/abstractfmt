package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.ENoMoreData;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.TIndicator;
import java.io.Reader;
import java.io.IOException;
/**
	A reading counterpart for {@link CXMLIndicatorWriteFormat}
	using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>.
*/	
public class CXMLIndicatorReadFormat extends AXMLIndicatorReadFormat
{
				/** Described status */
				private final boolean is_described;
				
	/** Creates 
	@param input input from which read data
	@param settings XML settings, non null. 
		If those settings carry non-null value in 
		{@link CXMLSettings#ROOT_ELEMENT} then
		this class will ensure to check if root element
		is opened before every operation and will
		start returning EOF/UnexpectedEof if root
		element is closed.		
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