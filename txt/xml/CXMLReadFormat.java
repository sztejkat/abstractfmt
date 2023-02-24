package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;//for javadocs
import java.io.Reader;
/**
	An XML reader compatible with {@link CXMLWriteFormat}
*/
public class CXMLReadFormat extends AXMLReadFormat0
{
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates, using XML 1.0 E4
	@param in non null down-stream reader. This reader will be wrapped in
			{@link CAdaptivePushBackReader} and will be accessible through
			{@link #in} field. Will be closed on {@link #close}.
			<p>
			No I/O operation will be generate till {@link #open}.
 
	*/
	public CXMLReadFormat(Reader in)
	{
		super(in);
	};
	/** Creates, using XML 1.0 E4
	@param in non null down-stream reader. This reader will be wrapped in
			{@link CAdaptivePushBackReader} and will be accessible through
			{@link #in} field. Will be closed on {@link #close}.
			<p>
			No I/O operation will be generate till {@link #open}.
	@param xml_classifier classifier to use, non null.
	*/
	public CXMLReadFormat(Reader in, IXMLCharClassifier xml_classifier)
	{
		super(in,xml_classifier,PREFEFINED_ENTITIES_ESCAPES,PREFEFINED_ENTITIES_ESCAPES_CHARS);
	};
	/* *******************************************************************
	
			IFormatLimits
	
	********************************************************************/
	/** Unlimited */
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	/** Unlimited */
	@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
};