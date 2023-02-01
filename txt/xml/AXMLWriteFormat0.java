package sztejkat.abstractfmt.txt.xml;


/**

	An XML writer.
	<p>
	This class add following functionality to pure elementary primitives
	related functionality of {@link #ATxtWriteFormat1}:
	<table boder="2">
	<caption>Methods grouped by functionality</caption>
	<tr>
	<td>
		XML token writes
	</td>
	<td>
		{@link #outXMLToken(char)},
		{@link #outXMLToken(String)},
		{@link #openXMLToken)},
		{@link #closeXMLToken)}
	</td>
	</tr>
	
	<tr>
	<td>
		XML prolog and closure
	</td>
	<td>
		{@link #writeXMLProlog}},{@link #writeXMLClosure}
	</td>
	</tr>
	
	
	</table>

*/
public abstract class AXMLWriteFormat0 extends ATxtWriteFormat1
{
				/** Set to true if upper surogate was in 
				a token and write was post-poned to check
				if it needs to be esacped.
				The link {@link #upper_surogate_pending}
				do carry the surogate in question*/
				private boolean is_upper_surogate_pending;
				private char upper_surogate_pending;
				
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates
	*/
	protected AXMLWriteFormat0()
	{
		super(0);	//We do not support registered names.
					//Due to JAVA lacking virtual multiple inheritance
					//it was for me easier to inherite the ARegisteringStructWriteFormat
					//in generic text support and then disable it here rather
					//than playing with class composition.
	};
	/* *****************************************************************
	
			Services required from subclasses	
	
	******************************************************************/
	/* ---------------------------------------------------------------
			XML elements processing
	---------------------------------------------------------------*/
	/** Used to write XML element character, like a part of XML
	entity and etc. A sequence of calls to this method
	constituting to a single XML element should be enclosed in
	{@link #openXMLToken},{@link #closeXMLToken}:
	<pre>
		openXMLToken
			outXMLToken('a')
			outXMLToken("supertag")
		closeXMLToken
	</pre>
	@param c a character which is convering then entire JAVA char
		space including not allowed compostions.
	@throws IOException if failed. */
	protected abstract void outXMLToken(char c)throws IOException;
	/** A method which together with {@link #closeXMLToken}
	surrounds a sequence of calls to {@link #outXMLToken}.
	<p>
	A symetric method to {@link #openPlainToken}/{@link #closePlainToken}
	@throws IOException if failed. */
	protected abstract void openXMLToken()throws IOException;
	/** A method which together with {@link #openXMLToken}
	surrounds a sequence of calls to {@link #outXMLToken}.
	<p>
	A symetric method to {@link #openPlainToken}/{@link #closePlainToken}
	@throws IOException if failed. */
	protected abstract void closeXMLToken()throws IOException;
	/* *****************************************************************
	
			Services tunable by subclasses
	
	******************************************************************/
	/** A method which will be called by {@link #openImpl} to
	write an XML file prolog and open the master element enclosing the
	content of the file.
	<p>
	Standard implementation writes a single XML token:
	<pre>
	&lt;?xml version="1.0" encoding="UTF-8"?&gt;
	&lt;xml&gt;
	</pre>
	@see #outXMLToken(String)	
	*/
	protected void writeXMLProlog()throws IOException
	{
		openXMLToken();
			outXMLToken("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><xml>");
		closeXMLToken();
	};
	/** A method which will be called by {@link #closeImpl} to
	terminate the element started by {@link #writeXMLProlog}.
	Standard implementation writes a single XML token:
	<pre>
	&lt;/xml&gt;
	</pre>
	*/
	protected void writeXMLClosure()throws IOException
	{
		openXMLToken();
			outXMLToken("</xml>");
		closeXMLToken();
	};
	/* *****************************************************************
	
			Services which can be used by subclasses to tune
			some operations
	
	******************************************************************/
				
	/** Just iterates over a string and calls {@link #outXMLToken}
	@param xml non null, text to pass char-by-char to {@link #outXMLToken}
	@throws IOException if failed.
	*/
	protected void outXMLToken(String xml)throws IOException
	{
		assert(xml!=null);
		for(int i=0, n=xml.length(); i<n; i++)
		{
			outXMLToken(n.charAt(i));
		};
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
	........... todo
		
		
}