package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;
import java.io.IOException;
import java.io.Writer;
/**
	An XML writer over the {@link Writer}
	<p>
	See package description for format specification.
*/
public class CXMLWriteFormat extends AXMLWriteFormat0
{
				/** Where to write characters */
				private final Writer out;
	/* *****************************************************************
	
				Creation
	
	******************************************************************/
	/** Creates using XML 1.0 E4
	<p>
	Note: This format <u>does</u> consume memory on each struct recursion level
	so it will be wise for You to set {@link #setMaxStructRecursionDepth} to
	prevent out-of-memory problems.
	
	@param out non null writer which must support the whole Unicode characters
			set. Any actuall writing will happen after {@link #open}.
	*/
	public CXMLWriteFormat(Writer out)
	{
		this(out,new CXMLChar_classifier_1_0_E4());
	};
	/** Creates using specified XML
	<p>
	Note: This format <u>does</u> consume memory on each struct recursion level
	so it will be wise for You to set {@link #setMaxStructRecursionDepth} to
	prevent out-of-memory problems.
	
	@param out non null writer which must support the whole Unicode characters
			set. Any actuall writing will happen after {@link #open}.
	@param IXMLCharClassifier xml classifier to use for version and escapes.
	*/
	public CXMLWriteFormat(Writer out, IXMLCharClassifier xml_classifier)
	{
		super(xml_classifier);
		assert(out!=null);
		this.out = out;
	};
	/* ****************************************************************
	
			AXMLWriteFormat0
	
	*****************************************************************/
	@Override protected void outXML(char c)throws IOException
	{
		out.write(c);
	};
	@Override protected void outXML(String xml)throws IOException
	{
		assert(xml!=null);
		out.write(xml);
	};
	/* ***********************************************************************
		
				AStructFormatBase
		
		
	************************************************************************/
	/** Overrided to close the writer */
	@Override protected void closeImpl()throws IOException
	{
		super.closeImpl();
		out.close();
	};
	/* ***********************************************************************
		
				AStructWriteFormatBase0
		
		
	************************************************************************/
	/** Overrided to flush the writer */
	@Override protected void flushImpl()throws IOException
	{
		super.flushImpl();
		out.flush();
	};
	/* ***********************************************************************
		
				IFormatLimits
		
		
	************************************************************************/
	/** No bound is set, but You should remember that this format DOES consume some
	memory on each recursion due to the necessity of tracking structs names. */
	@Override public int getMaxSupportedStructRecursionDepth(){ return Integer.MAX_VALUE; };
	/** No limit */
	@Override public int getMaxSupportedSignalNameLength(){ return -1; };
}