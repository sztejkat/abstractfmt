package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.util.CAdaptivePushBackReader;
import static sztejkat.abstractfmt.util.SHex.HEX2D;
import java.io.IOException;
import sztejkat.abstractfmt.EBrokenFormat;
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
				/** An element of input processing chain. */
				private final CXMLProcessingCommandFilter xml_processing_filter;
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
								this.xml_processing_filter = new CXMLProcessingCommandFilter(input)
								)),16,32,settings);
	};
	/* ************************************************
		
				Prolog processing	
		
	*************************************************/
	/** Checks if prolog is required, and if it is validates it and consumes.
	Then checks if root element is required, and if it is checks it, validates
	and consumes. At the return from this method cursor is set after
	the root element */
	public void open()throws IOException
	{
		//Now depening on settings we may expect prolog.
		if (settings.PROLOG!=null)
		{
			//Prolog is something what is normally skipped
			//because it is a sequence of processing commands.
			//We need to validate if prolog is present and if it matches.
			
			//We need to normalize prolog from settings and then try to read it
			//from stream and compare.
			String normalized_prolog;
			{			
				CXMLWhitespaceNormalizingFilter f = 
						new CXMLWhitespaceNormalizingFilter(
											new java.io.StringReader(settings.PROLOG)
														);			
				char [] c= new char[settings.PROLOG.length()];
				int ns = f.read(c);
				normalized_prolog = new String(c,0,ns);
			}
			try{
				xml_processing_filter.setBypassEnabled(true);
				
				//Now we need to read characters and compare them with normalized prolog.
				//First skip all spaces.
				char c;
				do{
					 c = input.readChar();
				}while(Character.isWhitespace(c));
				input.unread(c);
				//Now compare. We can do it char-by-char, because we are using the same whitespace normalization.
				for(int i=0,n = normalized_prolog.length();i<n;i++)
				{
					c = input.readChar();
					if (c!=normalized_prolog.charAt(i)) 
						throw new EBrokenFormat("Expecting \""+normalized_prolog+"\" but \""+c+"\" does not match "+i+"-th (\""+normalized_prolog.charAt(i)+"\") prolog character");
					
				};
				//Fine, prolog matched.
			}finally{ xml_processing_filter.setBypassEnabled(false);}
		};
		if (settings.ROOT_ELEMENT!=null)
		{
			//Now we should look for <ROOT_ELEMENT>.
			char c;
			do{
				 c = input.readChar();
			}while(Character.isWhitespace(c));
			if (c!='<') throw new EBrokenFormat("expected <"+settings.ROOT_ELEMENT+">, but \""+c+"\" found instead of opening < ");
			//Now normalization will ensure, we can directly compare it with 
			for(int i=0,n = settings.ROOT_ELEMENT.length();i<n;i++)
			{
				c = input.readChar();
				if (c!=settings.ROOT_ELEMENT.charAt(i)) 
					throw new EBrokenFormat("expected <"+settings.ROOT_ELEMENT+"> but at "+i+"-th position found \""+c+"\" instead of \""+settings.ROOT_ELEMENT.charAt(i)+"\"");
			};
			c = input.readChar();
			if (c!='>') throw new EBrokenFormat("expected <"+settings.ROOT_ELEMENT+">, but \""+c+"\" found instead of closing >");
		};
	};
	
	/* ***********************************************************************
	
	
			Closeable
		
	
	************************************************************************/
	/** Closes input.
	*/
	@Override public void close()throws IOException
	{
		input.close();
	};
};