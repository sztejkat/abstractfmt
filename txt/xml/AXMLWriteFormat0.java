package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;
import java.io.IOException;
import java.util.ArrayDeque;
/**
	An XML writer, base routines.
	<p>
	See package description for format specification.
*/
public abstract class AXMLWriteFormat0 extends ATxtWriteFormat1
{
				/** An escaping engine used to write XML element names
				dedicated for signals processing */
				private final AEscapingEngine xml_element_escaper = new AXMLElementNameEscapingEngine()
				{
					@Override protected void out(char c)throws IOException
					{
						AXMLWriteFormat0.this.outXML(c);
					};
					@Override protected IXMLCharClassifier getClassifier(){ return classifier; };
				};
				/** An escaping engine used to write string token content
				inside an XML element body */
				private final AEscapingEngine string_token_escaper = new AStringTokenEscapingEngine()
				{
					@Override protected void out(char c)throws IOException
					{
						AXMLWriteFormat0.this.outXML(c);
					};
					@Override protected IXMLCharClassifier getClassifier(){ return classifier; };
				};
				/** An escaping engine used to write comments */
				private final AEscapingEngine comment_escaper = new AXMLBodyEscapingEngine()
				{
					@Override protected void out(char c)throws IOException
					{
						AXMLWriteFormat0.this.outXML(c);
					};
					@Override protected IXMLCharClassifier getClassifier(){ return classifier; };
				};
				/** A signals stack necessary for tracking opening and closing XML tags.
				Notice I intentionally do not use own CBoundStack because the depth 
				limiting is handled by AFormatLimits.*/
				private final ArrayDeque<String> signals_stack;
				/** XML classifier */
				private final IXMLCharClassifier classifier;
				
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates, using XML 1.0 E4
	*/
	protected AXMLWriteFormat0()
	{
		this(new CXMLChar_classifier_1_0_E4());
	};
	/** Creates
	@param classifier classifier to use, non null. Remember to get in sync the necessary
			prolog.
	*/
	protected AXMLWriteFormat0(IXMLCharClassifier classifier )
	{
		super(0);	//We do not support registered names.
					//Due to JAVA lacking virtual multiple inheritance
					//it was for me easier to inherite the ARegisteringStructWriteFormat
					//in generic text support and then disable it here rather
					//than playing with class composition.
		assert(classifier!=null);
		this.classifier = classifier;
		this.signals_stack = new ArrayDeque<String>();
	};
	/* *****************************************************************
	
			Services required from subclasses	
	
	******************************************************************/
	/**	Writes XML character to a down-stream.
	@param c a character which should be written as it is,
			without any processing.
	@throws IOException if failed. */
	protected abstract void outXML(char c)throws IOException;				
	/** Just iterates over a string and calls {@link #outXML}
	@param xml non null, text to pass char-by-char to {@link #outXML}
	@throws IOException if failed.
	*/
	protected void outXML(String xml)throws IOException
	{
		assert(xml!=null);
		for(int i=0, n=xml.length(); i<n; i++)
		{
			outXML(xml.charAt(i));
		};
	};
	/* *****************************************************************
	
			Services related to XML format.
	
	******************************************************************/
	/** A method which will be called by {@link #openImpl} to
	write an XML file prolog and open the master element enclosing the
	content of the file.
	<p>
	Standard implementation writes following XML:
	<pre>
	&lt;?xml version="<i>from classifier</i>" encoding="UTF-8"?&gt;
	&lt;{@link #getXMLBodyElement()}&gt;
	</pre>
	@see #outXML(String)
	@throws IOException if {@link #outXML} failed.
	*/
	protected void writeXMLProlog()throws IOException
	{
		outXML("<?xml version=\""+classifier.getXMLVersion()+"\" encoding=\"UTF-8\" ?>");
		outXML('<');
		outXML(getXMLBodyElement());
		outXML('>');
	};
	/** Returns the name of XML body element opened by {@link AXMLWriteFormat0#writeXMLProlog}.
	@return XML body element, default is "sztejkat.abstractfmt.txt.xml"*/
	protected String getXMLBodyElement(){ return "sztejkat.abstractfmt.txt.xml"; }
	/** A method which will be called by {@link #closeImpl} to
	terminate the element started by {@link #writeXMLProlog}.
	Standard implementation writes a single XML token:
	<pre>
	&lt;/{@link #getXMLBodyElement()}&gt;
	</pre>
	@throws IOException if failed.
	*/
	protected void writeXMLClosure()throws IOException
	{
		outXML('<');outXML('/');
		outXML(getXMLBodyElement());
		outXML('>');
	};
	/** Writes single XML comment.
	@param comment a comment string. Will be correctly escaped 
			to avoid all problematic characters so that parser
			is not fooled by it. 
			<p>
			Notice however that escaped comment may be a problematic 
			for a human	to read, so You should avoid such comments.
	@throws IOException if failed to write off-band data.
	*/
	public void writeComment(String comment)throws IOException
	{
		openOffBandData();
		outXML("<!-- ");
			comment_escaper.append(comment);
		outXML(" -->");
		closeOffBandData();
	};
	/* *****************************************************************
	
			ATxtWriteFormat0
	
	******************************************************************/
	/** Just passes to {@link #outXML} */
	@Override protected void outPlainToken(char c)throws IOException
	{
		outXML(c);
	};
	/** Passes thorugh {@link #string_token_escaper} */
	@Override protected void outStringToken(char c)throws IOException
	{
		string_token_escaper.write(c);
	};
	/* ----------------------------------------------------------------
				tuning
	 ----------------------------------------------------------------*/
	/** Formats to "t" and "f" to get denser boolean blocks*/
	@Override protected String formatBooleanBlock(boolean v)
	{
		return v ? "t" : "f";
	};
	/* *****************************************************************
	
			ATxtWriteFormat1
	
	******************************************************************/
	/** Resets {@link #string_token_escaper} and opens " 
	*/
	@Override protected void openStringTokenImpl()throws IOException
	{
		string_token_escaper.reset();
		outXML('\"');
	};
	/** Flushes {@link #string_token_escaper} and closes "
	*/
	@Override protected void closeStringTokenImpl()throws IOException
	{		
		string_token_escaper.flush();
		outXML('\"');
	};
	@Override protected void outTokenToSignalSeparator()throws IOException
	{
	};
	/** Nothing needs to be done */
	@Override protected void openPlainTokenImpl()throws IOException{};
	/** Nothing needs to be done */
	@Override protected void closePlainTokenImpl()throws IOException{};
	/** Nothing needs to be done */
	@Override protected void outSignalSeparator()throws IOException{};
	/** Writes , */
	@Override protected void outTokenSeparator()throws IOException
	{
		outXML(',');
	};
	/* ***********************************************************************
		
				AStructFormatBase
		
		
	************************************************************************/
	/** Overriden to call {@link #writeXMLProlog} */
	@Override protected void openImpl()throws IOException
	{
		writeXMLProlog();
	};
	/** Conditionally calls {@link #writeXMLClosure} */
	@Override protected void closeImpl()throws IOException
	{
		//As protocol specs say, we do close only if there is
		//no dangling signals.
		if (getCurrentStructRecursionDepth()==0)
									writeXMLClosure();
	};
	/* ***********************************************************************
		
				ARegisteringStructWriteFormat
		
	************************************************************************/
	/** Always throws, should not be ever called */
	@Override protected final void beginAndRegisterImpl(String name, int index, int order)throws IOException
	{
		throw new UnsupportedOperationException();
	};
	/** Always throws, should not be ever called */
	@Override protected void beginRegisteredImpl(int index, int order)throws IOException
	{
		throw new UnsupportedOperationException();		
	};
	/** Writes name through {@link #xml_element_escaper} and pushes it on stack */
	@Override protected void beginDirectImpl(String name)throws IOException
	{
		//push on signals stack 
		signals_stack.push(name);
		outXML('<');
			xml_element_escaper.reset();
			xml_element_escaper.append(name);
			xml_element_escaper.flush();
		outXML('>');
	};
	/* ***********************************************************************
		
				AStructWriteFormatBase0
		
	************************************************************************/
	/** Pops name froma a stack and writes name through {@link #xml_element_escaper} */
	@Override protected void endImpl()throws IOException
	{
		outXML('<');outXML('/');
			xml_element_escaper.reset();
			xml_element_escaper.append(signals_stack.pop());
			xml_element_escaper.flush();
		outXML('>');
	};
	
	/* *******************************************************************************
	
		Design notes.
		
			A reader may start pondering why I did not use the javax.xml.stream.XMLEventWriter
			
			Well... 
			
			There are following reasons:
			
			1. The XMLEventWriter is not specified well enough for me. Especially it does not
				say how it will handle characters which are prohibited in XML body, 
				bad surogate pairs and Unicode code points which are not valid XML characters.
				
				From what I can suspect, it won't do it as I need it so I would have to
				provide a heavy escaping by myself anyway.
				
			2. It is based around String as an elementary entity, while I did specify my
				own generic text API at char level. It should not be a problem tough, if not the fact
				that the javax.xml.stream.XMLEventReader is also String based. What is worse
				the XMLEventReader.nextEvent() is an un-bound read which will try to load
				the whole XML element body (or at least - I do suspect that it is allowed
				since specs do not say a word about it). This is out-of-memory attack prone 
				and unacceptable in this format API.
				
			3. Writing XML is trivial. No need to overcomplicate it unless using DOM model.
				
	* ***************************************************************************************/
}