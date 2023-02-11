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
				
				/* ************************************************************************
				
						Implement XML "by the book" 
				
				
				* ***********************************************************************/
				/*
					Document:
					document	   ::=   	( prolog element Misc* ) - ( Char* RestrictedChar Char* ) 
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
