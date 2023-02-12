package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.ATxtReadFormatStateBase0;
import sztejkat.abstractfmt.txt.ATxtReadFormat1;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EUnexpectedEof;
import java.io.IOException;
import java.util.ArrayDeque;

/**
	An XML reader compatible with {@link AXMLWriteFormat0}.
*/
public abstract class AXMLReadFormat0 extends ATxtReadFormatStateBase0<ATxtReadFormat1.TIntermediateSyntax>
{
				/** A basic tool-boxing for xml text processing */
				private abstract class AStateHandler extends ATxtReadFormatStateBase0<ATxtReadFormat1.TIntermediateSyntax>.AStateHandler 
				{
					/** Reads character from down-stream.
					@return -1 if eof or 0...0xFFFFF. If returns -1 a
							proper {@link #queueNextChar} is already invoked
					@throws IOException if failed. */
					protected final int read()throws IOException
					{
						int r = AXMLReadFormat0.this.readImpl();
						assert((r>=-1)&&(r<=0xFFFF));
						if (r==-1)
						{
							queueNextChar(-1, null);
						};
						return r;
					};
				};
				/** A handler which requires specific raw character 
				and reports it as VOID */
				private abstract class ARequiresSpecificCharHandler extends AStateHandler
				{
							private final AStateHandler next;
					/** Creates
					@param next next handler to set or null to pop state handler */
					protected ARequiresSpecificCharHandler(AStateHandler next)
					{
						this.required = required;
						this.next = next;
					};
					protected abstract boolean isRequiredChar(char c); 
					protected void toNextChar()throws IOException
					{
						int r = read();
						if (r!=-1)
						{
							char c = (char)r;
							if (!isRequiredChar(c)) throw new EBrokenFormat("expected unexpected \'"+c+"\'");
							queueSyntax(c,TIntermediateSyntax.VOID);
							if (next==null)
								popStateHandler();
							else
								setStateHandler(next);
						};
					};
				};
				
				/** A handler which requires specific raw character 
				and reports it as VOID */
				private class CRequiresSpecificCharHandler extends ARequiresSpecificCharHandler
				{
							private final char required;
					/** Creates
					@param required required character
					@param next next handler to set or null to pop state handler */
					protected CRequiresSpecificCharHandler(char required, AStateHandler next)
					{
						super(next);
						this.required = required;
					};
					@Override protected boolean isRequiredChar(char c){ return c==required; };
				};
				/** A handler which requires XML whitespace 
				and reports it as VOID */
				private class CRequiresWhitespaceHandler extends ARequiresSpecificCharHandler
				{
							private final char required;
					/** Creates
					@param required required character
					@param next next handler to set or null to pop state handler */
					protected CRequiresWhitespaceHandler(AStateHandler next)
					{
						super(next);
					};
					@Override protected boolean isRequiredChar(char c){ return classifier.isXMLSpace(c); };
				};
					/** A handler which skips characters till it consume a required one 
				and reports all as VOID */
				private abstract class ASkipToRequiredCharHandler extends AStateHandler
				{
							private final AStateHandler next;
					/** Creates
					@param next next handler to set or null to pop state handler */
					protected ASkipToRequiredCharHandler(AStateHandler next)
					{
						this.next = next;
					};
					protected abstract boolean isRequiredChar(char c); 
					protected void toNextChar()throws IOException
					{
						int r = read();
						if (r!=-1)
						{
							char c = (char)r;
							queueSyntax(c,TIntermediateSyntax.VOID);
							if (isRequiredChar(c))
							{
								if (next==null)
									popStateHandler();
								else
									setStateHandler(next);
							};
						};
					};
				};
				/** A class which skips what is left in element declaration, that is everything till the
				&gt;.*/
				private class CSkipElement extends ASkipToRequiredCharHandler
				{
						/** Creates
						@param next next handler to set or null to pop state handler */
						protected CSkipElement(AStateHandler next){ super('>'); };	
				};
				
				/**
					A handler which is "alterantive", that is expressed in BNF syntax by <i>someting | something </i>.
				*/
				private abstract class AAltHandler extends AStateHandler
				{
								private final AStateHandler [] alternatives;
								private AStateHandler active;
								
					/** Is supposed to check character and recon if handler is
					supposed to process the content, that is it  do appear 
					in text.
					@return true if it stays, false if it does not appear and {@link #next}
							should be immediately used to process it and become current. 
							If false character should be un-read and no syntax should be queued.
					*/
					protected abstract boolean nextCharOpt()throws IOException;
					
					protected final void nextChar()throws IOException
					{
						if (!nextCharOpt())
						{
							setStateHandler(next);
							next.nextChar();
						};
					};
				};
				/* ************************************************************************
				
						Implement XML "by the book" 
				
				
				* ***********************************************************************/
				/*
					Document:
					document	   ::=   	( prolog element Misc* ) - ( Char* RestrictedChar Char* ) 
						prolog	   ::=   	XMLDecl? Misc* (doctypedecl Misc*)?
						XMLDecl	   ::=   	'<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
				*/
				private final XMLDECL_SKIP = new CSkipElement(XML_ELEMENT_LOOKUP);
				private final XMLDECL_5 = new CRequiresWhitespaceHandler(XMLDECL_SKIP);
				private final XMLDECL_4 = new CRequiresSpecificCharHandler('l',XMLDECL_5);
				private final XMLDECL_3 = new CRequiresSpecificCharHandler('m',XMLDECL_4);
				private final XMLDECL_2 = new CRequiresSpecificCharHandler('x',XMLDECL_3);
				private final XMLDECL_1 = new CRequiresSpecificCharHandler('?',XMLDECL_2);
				private final XMLDECL_0 = new CRequiresSpecificCharHandler('<',XMLDECL_1);
				/* 
					element	   ::=   	EmptyElemTag
										| STag content ETag 
					EmptyElemTag	   ::=   	'<' Name (S Attribute)* S? '/>'
				*/
				
				/** An un-escaping engine */
				private final AXMLUnescapingEngine unescaper = new AXMLUnescapingEngine()
				{
					@Override protected int readImpl()throws IOException
					{
						return AXMLReadFormat0.this.readImpl();
					};
					@Override protected void unread(char c)throws IOException
					{
						AXMLReadFormat0.this.unread(c);
					};
					@Override protected IXMLCharClassifier getClassifier(){ return classifier; };
				};
				
	
				/** XML classifier */
				private final IXMLCharClassifier classifier;
				/** A signals stack necessary for tracking opening and closing XML tags.
				Notice I intentionally do not use own CBoundStack because the depth 
				limiting is handled by AFormatLimits.*/
				private final ArrayDeque<String> signals_stack;
	/* ****************************************************************
	
			Creation
	
	
	*****************************************************************/
	/** Creates, using XML 1.0 E4
	*/
	protected AXMLReadFormat0()
	{
		this(new CXMLChar_classifier_1_0_E4());
	};
	/** Creates
	@param classifier classifier to use, non null. Remember to get in sync the necessary
			prolog.
	*/
	protected AXMLReadFormat0(IXMLCharClassifier classifier )
	{
		//No name registry supported.
		super(0, //int name_registry_capacity,
			  64);//int token_size_limit
		assert(classifier!=null);
		this.classifier = classifier;
		this.signals_stack = new ArrayDeque<String>();
	};	
	/* ***************************************************************
	
			Tunable services
	
	
	*****************************************************************/
	/** Returns the name of XML body element opened by {@link AXMLWriteFormat0#writeXMLProlog} */
	protected String getXMLBodyElement(){ return "xml"; }
	/* ***************************************************************
	
			Services required from subclasses 
	
	
	*****************************************************************/
	/** Should read character from the downstream
	@return -1 on eof, otherwise 0...0xFFFF
	@throws IOException if failed */
	protected abstract int readImpl()throws IOException;
	/** Should un-read character back to the downstream.
	This class wont un-read more that a few characters.
	@param c character
	@throws IOException if failed */
	protected abstract void unread(char c)throws IOException;
	/* ***************************************************************
	
			AStructFormatBase
	
	
	****************************************************************/
	/** Overriden to initialize a state machine */
	@Override protected void openImpl()
	{
		//setStateHandler(null);	
	}; 
	/** Overriden to drop a state machine */
	@Override protected void closeImpl()
	{
		setStateHandler(null);
	};
};
