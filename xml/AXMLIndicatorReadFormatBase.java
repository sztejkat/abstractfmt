package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.util.CAdaptivePushBackReader;
import java.io.IOException;
import sztejkat.abstractfmt.EClosed;
import java.io.Reader;

/**
	A base reading counterpart for {@link AXMLIndicatorWriteFormatBase}
	using XML as specified in <A href="doc-files/xml-syntax.html">syntax definition</a>.
	<p>
	Basically set of intermediate I/O.
*/
abstract class AXMLIndicatorReadFormatBase extends AXMLFormat 
												  implements IIndicatorReadFormat
{				
				/** Low level input.
				This input is passed through following transparent processes:
				<ul>
					<li>process of removing XML comments;</li>
					<li>process of removing XML processing commands;</li>
					<li>process of white-space normalization which removes
					all whitespaces right before and after &gt; &lt; and replaces 
					all sequences of white-spaces with single ' ' character
					regardless if found in a body, element or attribute;</li>
				</ul>
				 */
				protected final CDecodingXMLReader input;
				
				/** Used to monitor closed status */
				private boolean is_closed;
				
	/** Creates 
	@param input reader from which to read content.
	@param settings XML settings to use
	*/
	protected AXMLIndicatorReadFormatBase(
					final Reader input,
					final CXMLSettings settings
					)
	{
		super(settings);
		assert(input !=null);
		this.input = new CDecodingXMLReader(
						new CXMLWhitespaceNormalizingFilter(
							new CXMLCommentFilter(
								new CXMLProcessingCommandFilter(input)
								)),16,32,settings);
	};
	/* ****************************************************
		
			Services required from subclasses
			
	*****************************************************/
	/** Called in {@link #close} when closed for a first 
	time. By default closes input.
	@throws IOException if failed.
	*/
	protected void closeOnce()throws IOException
	{
		input.close();
	};
	/* *************************************************************************
			State validation	
	* *************************************************************************/
	/** Throws if closed */
	protected void validateNotClosed()throws EClosed
	{
		if (is_closed) throw new EClosed();
	};
	/* ***********************************************************************
	
	
			Closeable
		
	
	************************************************************************/
	/** Sets closed status to true.
	If it was false calls {@link #closeOnce}
	@see #validateNotClosed
	*/
	@Override public void close()throws IOException
	{
		try{
			if (!is_closed)
					closeOnce();
		}finally { is_closed=true;}
	};
};