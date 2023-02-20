package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.txt.*;
import sztejkat.abstractfmt.EBrokenFormat;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;

/**
	An XML reader compatible with {@link AXMLWriteFormat0}.
	This reader is using a "state-graph" concept, as specified
	in {@link ATxtReadFormatStateBase1}.
*/
public abstract class AXMLReadFormat0 extends ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax>
{
					 private static final long TLEVEL = SLogging.getDebugLevelForClass(AXMLReadFormat0.class);
					 private static final boolean TRACE = (TLEVEL!=0);
					 private static final boolean DUMP = (TLEVEL>=2);
					 private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AXMLReadFormat0.",AXMLReadFormat0.class) : null;
			 
        /* ***********************************************************************************
        
        
        			Toolboxing
        
        
        
        * ***********************************************************************************/
        
        		/** A state handler which is responsible for skipping all processing commands,
        		comments and CDATA.
        		<p>
        		This state handler is very forgiving and is skipping everything till nearest &gt;
        		character, including. It does NOT validate if the closing syntax do match 
        		required opening syntax.
        		*/
        		private static abstract class ASkippper extends AToNextSyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
        		{
        			protected ASkippper(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p){ super(p); };
        			@Override public void toNextChar()throws IOException
        			{
        				if (TRACE) TOUT.println("ASkippper.toNextChar() ENTER");
						int r= read();
						if (r==-1)
						{
							if (TRACE) TOUT.println("ASkippper.toNextChar(), eof LEAVE");
							return;
						};
						char c = (char)r;
						if (c=='>')
						{
							//consume char and move to next;
							leaveStateHandler();
							if (TRACE) TOUT.println("ASkippper.toNextChar(), finished LEAVE");
						}else
						{
							//do not report this character at all.
							if (TRACE) TOUT.println("ASkippper.toNextChar() LEAVE");
						}
        			};
        		};
        		/** A skipper with detecting capabilities, for comments, processing instructions
        		and cdata.
        		First character is reported as {@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR} since
        		those do break tokens.
        		*/
        		private static abstract class ABreakingSkippper extends ASkippper
        		{
        						/** A phrase which begins it */
        						private final String catch_phrase;
        			/** Creates
        			@param catch_phrase a phrase which begins it, non null
        			@param p parser, non null */
        			protected ABreakingSkippper(String catch_phrase, 
        									  ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
        			{ 
        				super(p); 
        				assert(catch_phrase!=null);
        				this.catch_phrase = catch_phrase;
        			};
        			/** Overriden to return null as those are overlapping states 
        			and return to previous state after processing. */
        			@Override protected IStateHandler getNextHandler(){ return null; }; 
        			@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("ABreakingSkippper.tryEnter() ENTER");
						if (tryLooksAt(catch_phrase))
						{
							queueNextChar(' ', ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
							enterStateHandler();
							if (TRACE) TOUT.println("ABreakingSkippper.tryEnter()=true LEAVE");
							return true;
						}else
						{
							unread();
							if (TRACE) TOUT.println("ABreakingSkippper.tryEnter()=false LEAVE");
							return false;
						}
					};
        		};
        
		/* ***********************************************************************************
		
		
				State graph.
		
					A state graph is a set of private fianal fields
					each implementing ATxtReadFormatStateBase0.IStateHandler
					or ATxtReadFormatStateBase0.ISyntaxHandler.
		
					The parser asks states to process characters and qualify
					them and they do move over the stream.
		
		
		
		
		*************************************************************************************/
		
			/* --------------------------------------------------------------------------------
			
			
						XML prolog
			
			
			--------------------------------------------------------------------------------*/
				/** A state responsible for initializing file processing.
				<p>
				We do allow for ByteOrderMark character and this state is handling it.
				Moves to {@link #PROLOG} or {@link #BOM}.
				*/
				private final IStateHandler START = new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "START"; };
					@Override public void toNextChar()throws IOException
					{
						
						if (TRACE) TOUT.println("START.toNextChar() ENTER");
						//Without bom?
						if (!PROLOG.tryEnter())
						if (!BOM.tryEnter())
						{
							int r= read();
							if (r!=-1) throw new EBrokenFormat("Invalid syntax, \'"+(char)r+"\'  ByteOrderMark or <?xml expected :"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("START.toNextChar() LEVE");
					};
				};
				/** A ByteOrderMar handler.
				Moves to {@link #PROLOG} after consuming the BOM character.
				*/
				private final ISyntaxHandler BOM = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "BOM"; };
					@Override public boolean tryEnter()throws IOException
					{
						/* We just check if it is an allowed UTF-8 BOM? */
						if (TRACE) TOUT.println("BOM.tryEnter() ENTER");
						int r= tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("BOM.tryEnter()=false, eof LEAVE");
							return false;
						};
						char c = (char)r;
						if (c==0xFEFF)
						{							
							setStateHandler(this);
							if (TRACE) TOUT.println("BOM.tryEnter()=true, UTF-8 BOM, LEAVE");
							return true;
						}else
						{
							unread(c);
							if (TRACE) TOUT.println("BOM.tryEnter()=false, LEAVE");
							return false;
						}
					};
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("BOM.toNextChar() ENTER");
						if (!PROLOG.tryEnter())
						{
							int r= read();
							if (r!=-1) throw new EBrokenFormat("Invalid syntax, \'"+(char)r+"\', <?xml expected:"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("BOM.toNextChar() LEVE");
					};
				};
				/** The xml prolog detector and skipper, moves to {@link #XML_BODY_LOOKUP}.
				<p>
				Since our parser is very forgiving we just do check if there is
				any prolog and we skip it. We do ignore any kind of encoding information
				and anything else what can be found in a prolog. 
				*/
				private final ISyntaxHandler PROLOG = new ASkippper(this)
				{
					@Override public String getName(){ return "PROLOG"; };
					@Override protected IStateHandler getNextHandler(){ return XML_BODY_LOOKUP; };
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("PROLOG.tryEnter() ENTER");
						if (tryLooksAt("<?xml"))
						{
							//yep, this is prolog.
							enterStateHandler();
							if (TRACE) TOUT.println("PROLOG.tryEnter()=true, LEAVE");
							return true;
						}else
						{
							unread();
							if (TRACE) TOUT.println("PROLOG.tryEnter()=false, LEAVE");
							return false;
						}
					};
				};
				/** A state responsible for looking for the opening tag specified by
				{@link #getXMLBodyElement} */				
				private final IStateHandler XML_BODY_LOOKUP = new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "XML_BODY_LOOKUP"; };
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("XML_BODY_LOOKUP.toNextChar() ENTER");
						if (!XML_BODY.tryEnter())
						if (!COMMENT.tryEnter())
						if (!PI.tryEnter())
						if (!CDATA.tryEnter())
						if (!WHITESPACE.tryEnter())
						{
							int r= read();
							if (r!=-1) throw new EBrokenFormat("Invalid syntax, \'"+(char)r+"\', <"+getXMLBodyElement()+" expected:"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("XML_BODY_LOOKUP.toNextChar() LEVE");
					};
				};
				/** A comment detector and skipper. First character is reported as 
				{@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR} since comments do break tokens.
				*/
				private final ISyntaxHandler COMMENT = new ABreakingSkippper("<!--",this)
				{
					@Override public String getName(){ return "COMMENT"; };
				};
				/** A processing instruction detector and skipper. First character is reported as 
				{@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR} since it do break tokens.
				*/
				private final ISyntaxHandler PI = new ABreakingSkippper("<?",this)
				{
					@Override public String getName(){ return "PI"; };
				};
				/** A cdata instruction detector and skipper. First character is reported as 
				{@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR} since it do break tokens.
				*/
				private final ISyntaxHandler CDATA = new ABreakingSkippper("<![CDATA[",this)
				{
					@Override public String getName(){ return "CDATA"; };
				};
				/** A state handler reporting all white-spaces as separators 
				and returns to previous state. */
				private final ISyntaxHandler WHITESPACE = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "WHITESPACE"; };
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("WHITESPACE.tryEnter() ENTER");
						int r= tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("WHITESPACE.tryEnter()=false, eof LEAVE");
							return false;
						};
						if (classifier.isXMLSpace(r))
						{							
							pushStateHandler(this);
							queueNextChar((char)r, ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
							if (TRACE) TOUT.println("WHITESPACE.tryEnter()=true, LEAVE");
							return true;
						}else
						{
							unread();
							if (TRACE) TOUT.println("WHITESPACE.tryEnter()=false, LEAVE");
							return false;
						}
					};
				   @Override public void toNextChar()throws IOException
				   {
				   	   if (TRACE) TOUT.println("WHITESPACE.toNextChar() ENTER");
				   	    int r= read();
						if (r==-1)
						{
							if (TRACE) TOUT.println("WHITESPACE.toNextChar(), eof LEAVE");
							return;
						};
						char c = (char)r;
						if (classifier.isXMLSpace(r))
						{
							queueNextChar(c, ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
						}else
						{
							unread(c);
							popStateHandler();
						};
						if (TRACE) TOUT.println("WHITESPACE.toNextChar() LEAVE");
				   };
				};
				
				
				/** A state processing the body element.
				<p>
				This is a main element of the stream and a main node of a state graph.
				<p>
				Since that moment the syntax stack is used to not only traverse the graph but also 
				to trace the nesting of xml elements.
				*/
				private final ISyntaxHandler XML_BODY = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "XML_BODY"; };
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("XML_BODY.tryEnter() ENTER");
						//Note: below call should be "<"+getXMLBodyElement()+">"
						//      but it would be always constructing new String
						//		so instead I will use sequence of less expensive
						//		calls but in a bit peculiar order and unread sequence.
						if (tryLooksAt("<"))
						{
							if (tryLooksAt(getXMLBodyElement()))
							{
								if (tryLooksAt(">"))
								{
									setStateHandler(this); //become active state
									if (TRACE) TOUT.println("XML_BODY.tryEnter()=true LEAVE");
									return true;
								}else
								{
									unread();
									unread(getXMLBodyElement()); //this is known to matched.
									unread('<');
								}
							}else
							{
								unread();
								unread('<'); //this is known to matched.
							}
						}else
						{
							unread();							
						}
						if (TRACE) TOUT.println("XML_BODY.tryEnter()=false, LEAVE");
						return false;
					};
				   @Override public void toNextChar()throws IOException
				   {
				   	   if (TRACE) TOUT.println("XML_BODY.toNextChar() ENTER");
				   	   if (!BEGIN_TAG.tryEnter())	//new elements
				   	   if (!COMMENT.tryEnter())
				   	   if (!PI.tryEnter())
					   if (!CDATA.tryEnter())
					   if (!WHITESPACE.tryEnter())
					   if (!PLAIN_TOKEN.tryEnter())
					   if (!STRING_TOKEN.tryEnter())
					   if (!XML_BODY_END.tryEnter())  //dedicated end handler for this type of element.
				   	   {
							int r= read();
							if (r!=-1) throw new EBrokenFormat("Invalid syntax, \'"+(char)r+"\', <"+getXMLBodyElement()+" expected:"+getLineInfoMessage());
					   }
					   if (TRACE) TOUT.println("XML_BODY.toNextChar() LEAVE");
				   };
				};
				
				/** A terminating state for closing main body tag. It's sole purpose is to return end-of-file
				for any other possible access.
				*/
				private final ISyntaxHandler XML_BODY_END = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "XML_BODY_END"; };
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("XML_BODY_END.tryEnter() ENTER");
						//Note: below call should be "<"+getXMLBodyElement()+">"
						//      but it would be always constructing new String
						//		so instead I will use sequence of less expensive
						//		calls but in a bit peculiar order and unread sequence.
						if (tryLooksAt("</"))
						{
							if (tryLooksAt(getXMLBodyElement()))
							{
								if (tryLooksAt(">"))
								{
									setStateHandler(this); //become active state
									if (TRACE) TOUT.println("XML_BODY_END.tryEnter()=true LEAVE");
									return true;
								}else
								{
									unread();
									unread(getXMLBodyElement()); //this is known to matched.
									unread('<');
								}
							}else
							{
								unread();
								unread('<'); //this is known to matched.
							}
						}else
						{
							unread();							
						}
						if (TRACE) TOUT.println("XML_BODY_END.tryEnter()=false, LEAVE");
						return false;
					};
				   @Override public void toNextChar()throws IOException
				   {
				   	   if (TRACE) TOUT.println("XML_BODY_END.toNextChar() ENTER");
				   	   //Persisten "no more data" regardless of what is in stream.
				   	   queueNextChar(-1,null);
					   if (TRACE) TOUT.println("XML_BODY_END.toNextChar() LEAVE");
				   };
				};
				
				/** An inner handler responsible for handling _XXXX escapes.
					This handler is pushed on stack if detects escapes and is
					popped when finishes parsing it.
				*/
				private static abstract class AUnderscoreHexEscapeHandler extends ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
				{
								/** What to report character as */
								private final ATxtReadFormat1.TIntermediateSyntax report_unescaped_as;
								/** Counts digits */
								private	int count = 0;
								/** Preserves un-escaped value */
								private	char unescaped = 0;
								
						protected AUnderscoreHexEscapeHandler(ATxtReadFormat1.TIntermediateSyntax report_unescaped_as,
													ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(p);
							this.report_unescaped_as;
						};
						/* **********************************************************
									Tunable services
						***********************************************************/
						/** Invoked with each processed character. Used by escapes in signals to 
						capture un-escaped raw form of a signal name necessary for start tag/ end tag
						matching. Default is empty.
						@param c what is processed */
						protected void collectDirectly(char c){};
						/* **********************************************************
									AStateHandler
						***********************************************************/
						@Override public void onEnter(){ count=0; unescaped=0; };
						/* **********************************************************
												ASyntaxHandler
						***********************************************************/
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("AUnderscoreHexEscapeHandlertryEnter() ENTER");
							int r = tryRead();
							if (r==-1)
							{
								if (TRACE) TOUT.println("AUnderscoreHexEscapeHandler.tryEnter()=false, eof LEAVE");
								return false;
							};
							char c = (char)r;
							if (TRACE) TOUT.println("AUnderscoreHexEscapeHandler.tryEnter() checking \'"+c+"\'");
							if (c=='_')
							{
								pushStateHandler(this);
								//collect it for direct processing.
								collectDirectly(c);
								if (TRACE) TOUT.println("AUnderscoreHexEscapeHandler.tryEnter()=true LEAVE");
								return true;
							}else
							{
								unread(c);
								if (TRACE) TOUT.println("AUnderscoreHexEscapeHandler.tryEnter()=false LEAVE");
								return false;
							}
						};
						@Override public void toNextChar()throws IOException
						{
							if (TRACE) TOUT.println("AUnderscoreHexEscapeHandler.toNextChar() ENTER");
							char c = readAlways();										
							if (TRACE) TOUT.println("AUnderscoreHexEscapeHandler.toNextChar() unescaping \'"+c+"\'");
							final char digit = c;
							final int nibble;
							if ((digit>='0')&&(digit<='9'))
							{
								nibble = digit - '0';
							}else
							if ((digit>='A')&&(digit<='F'))
							{
								nibble = digit - 'A'+10;
							}else
							if ((digit>='a')&&(digit<='f'))
							{
								nibble = digit - 'a'+10;
							}else
								throw new EBrokenFormat("Not a hex digit '"+digit+"\'"+getLineInfoMessage());
								
							collectDirectly(c); //for direct processing.
							unescaped<<=4;
							unescaped|=nibble;
							count++;
							if (count==4)
							{
								queueNextChar(unescaped,report_unescaped_as);
								popStateHandler();
								if (TRACE) TOUT.println("AUnderscoreHexEscapeHandler.toNextChar(), unescaped LEAVE");
							};
						};
				}
				
				
				
				/** An inner handler responsible for handling __ escapes.
					This handler is pushed on stack if detects escapes and is
					popped when finishes parsing it.
				*/
				private static abstract class AUnderscoreSelfEscapeHandler extends ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
				{
								/** What to report character as */
								private final ATxtReadFormat1.TIntermediateSyntax report_unescaped_as;
								
						protected AUnderscoreSelfEscapeHandler(ATxtReadFormat1.TIntermediateSyntax report_unescaped_as,
													ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(p);
							this.report_unescaped_as;
						};
						/* **********************************************************
									Tunable services
						***********************************************************/
						/** Invoked with each processed character. Used by escapes in signals to 
						capture un-escaped raw form of a signal name necessary for start tag/ end tag
						matching. Default is empty.
						@param c what is processed */
						protected void collectDirectly(char c){};
						/* **********************************************************
												ASyntaxHandler
						***********************************************************/
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("AUnderscoreSelfEscapeHandler.tryEnter() ENTER");
							if (tryLooksAt("__"))
							{
								if (TRACE) TOUT.println("AUnderscoreSelfEscapeHandler.tryEnter()=false collected=\""+collected+"\", LEAVE");
								queueNextChar('_',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
								collectDirectly('_');
								collectDirectly('_');
								return true;
							}else
							{
								if (TRACE) TOUT.println("AUnderscoreSelfEscapeHandler.tryEnter()=false collected=\""+collected+"\", LEAVE");
								unread();
								return false;
							}
						};
						@Override public void toNextChar()throws IOException{throw new AssertionError();}
				}
				
				/** Detects the starting tag, processes the body and detects the closing
				tag. This class is a bit special, because when it detects the starting
				tag it does NOT push self on a stack but instead the instance of
				{@link CElementBodyHandler} with matching element name.
				<p>
				This way it is using the syntax stack to track both the names of elements
				and the syntax graph.
				*/
				private final ISyntaxHandler BEGIN_TAG = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
								/** A hex-escape sequence handler */
							private final ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax> HEX_ESCAPE_SEQUENCE = 
									new AUnderscoreHexEscapeHandler(ATxtReadFormat1.TIntermediateSyntax.SIG_NAME,parser)
									{
										@Override protected void collectDirectly(char c)
										{
											directCollected().append(c);
										};
									}
								
							/** A __ escape handler */
							private final ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax> SELF_ESCAPE_SEQUENCE = 
								new AUnderscoreSelfEscapeHandler(ATxtReadFormat1.TIntermediateSyntax.SIG_NAME,parser)
								{
									@Override protected void collectDirectly(char c)
									{
										directCollected().append(c);
									};
								};
								
					/** Just for inner classes because I can't figure out how to reference anonymous outer class. Can You?
					If yes drop me a note.
					@return {@link #collected}
					*/
					final StringBuilder directCollected(){ return collected; };
					
					@Override public String getName(){ return "BEGIN_TAG"; };
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("BEGIN_TAG.tryEnter() ENTER");
						if (orTryLooksAt("<_>") || orTryLooksAt("<>"))
						{
							//an empty name proper and improper.
							queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);	//begin
							queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID); //empty name
							queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);	//end of name
							pushStateHandler(new CElementBodyHandler("_",parser));
							if (TRACE) TOUT.println("BEGIN_TAG.tryEnter()=true, empty name, LEAVE");
							return true;
						}else
						if (tryLooksAt("<"))//now full text variants.
						{
							//Now check if it is not the end tag?
							char c= readAlways();
							unread(c);//in all cases it will be processed.
							if (c=='/')
							{
								if (TRACE) TOUT.println("BEGIN_TAG.tryEnter()=false, end tag LEAVE");
								return false;
							}else
							if (classifier.isNameStartChar(c))
							{
								queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);	//begin
								pushStateHandler(this);
								if (TRACE) TOUT.println("BEGIN_TAG.tryEnter()=true, full name, LEAVE");
								return true;
							}else
								throw new EBrokenFormat("Invalid first character in element name \'"+c+"\' :"+getLineInfoMessage());
						}else
						{
							unread();
							if (TRACE) TOUT.println("BEGIN_TAG.tryEnter()=false, LEAVE");
							return false;
						}
					};
					@Override public void toNextChar()throws IOException
					{
						//Now we have a name to emit. Notice the name length limit is catched by
						//superclass so we can just ignore bounds and process it char by char 
						//and will be stopped if something wrong happens.
						
						//first read character to collect it for direct processing
						if (!SELF_ESCAPE_SEQUENCE.tryEnter())
						if (!HEX_ESCAPE_SEQUENCE.tryEnter())
						{
							char c = readAlways();						
							if (c=='>')
							{
								unread(c);
								queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID); //empty name, just in case
								queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);	//end of name
								pushStateHandler(new CElementBodyHandler(collected.toString(),parser));
							}else
							if (c=='/')
							{
								//possibly a self closing tag?
								char cn = readAlways();
								if (cn=='>')
								{
									//yes, self closing.
									queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_END);	//end of name, end.
									popStateHandler();
								}else
								{
									throw new EBrokenFormat("Invalid self closing tag character \'"+c+"\', expected > :"+getLineInfoMessage()); 
								}
							}else
							if (classifier.isNameChar(c))
							{
								//Now collect it
								collected.append(c);
								//now pass it up.
								queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME); //empty name
							}else
							throw new EBrokenFormat("Invalid character in element name \'"+c+"\', attributes are not allowed:"+getLineInfoMessage());
						};
					};
				};
				
				/** 
					A class which is processing a body of an element and detects the dedicated closing
					tag after which it returns to parent syntax element.
				*/
				private final class CElementBodyHandler extends AStateHandler<ATxtReadFormat1.TIntermediateSyntax>
				{
								/** A name of element it is handling, a raw un-escaped form,
								enclosed in end-tag syntax.*/
								private final String end_tag;
								
								private final ISyntaxHandler END_TAG = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(parser)
								{
									@Override public String getName(){ return "CElementBodyHandler.END_TAG"; };
									@Override public boolean tryEnter()throws IOException
									{
										if (orTryLooksAt("</>") || orTryLooksAt(end_tag))
										{
											//anonymous end tag or full end tag.
											popStateHandler();	//yes, pop current!
											queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
											return true;
										}else
										{
											return false;
										}
									};
								};
								
						CElementBodyHandler(String element_name,  ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(p);
							this.end_tag = "</"+element_name+">";
						};
						/* **************************************************************
							
								AStateHandler
						
						***************************************************************/
						@Override public String getName(){ return "CElementBodyHandler("+element_name+")"; };
						@Override public void toNextChar()throws IOException
						{
						   if (!END_TAG.tryEnter())		//sef end
						   if (!BEGIN_TAG.tryEnter())	//new elements
						   if (!COMMENT.tryEnter())		//this state is pushed over us
						   if (!PI.tryEnter())			//this state is pushed over us
						   if (!CDATA.tryEnter())		//this state is pushed over us
						   if (!WHITESPACE.tryEnter())	//this state is pushed over us
						   if (!PLAIN_TOKEN.tryEnter()) 
						   if (!STRING_TOKEN.tryEnter())
						   {
								int r= read();
								if (r!=-1) throw new EBrokenFormat("Invalid syntax, \'"+(char)r+"\' :"+getLineInfoMessage());
						   }
						};
				}
	
				........... todo: tokens? How to deal with token separators in an easy way?
				
							push token
								set token separator:
											whitespace, comment
											separator
								pop after separator
								
							hmmm.... maybe avoid body handler as separate instance and chain it
							using external stack? This may be easier due to syntax chaining in tokens,
							will it however work correctly with XML body? hmmm....
							Most probably it will allow multiple bodies in XML what is not bad?
							or we can detect the empty end_tags in end tag handler and move state
							to special no more data? Yes, it will be best.
							
				........... refactor xml body to have the same structure.
				
				/** XML classifier */
				private final IXMLCharClassifier classifier;
				/** Expected end-tags stack */
				private final ArrayDeque<String> end_tags;
				
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
	protected AXMLReadFormat0(Reader in)
	{
		this(in,new CXMLChar_classifier_1_0_E4());
	};
	/** Creates
	@param in non null down-stream reader. This reader will be wrapped in
			{@link CAdaptivePushBackReader} and will be accessible through
			{@link #in} field. Will be closed on {@link #close}.
			<p>
			No I/O operation will be generate till {@link #open}.
	@param classifier classifier to use, non null. Remember to get in sync the necessary
			prolog.
	*/
	protected AXMLReadFormat0(Reader in,IXMLCharClassifier classifier )
	{
		//No name registry supported.
		super(in,
			  0, //int name_registry_capacity,
			  64);//int token_size_limit
		assert(classifier!=null);
		this.classifier = classifier;
	};	
	/* ***************************************************************
	
			Tunable services
	
	
	*****************************************************************/
	/** Returns the name of XML body element opened by {@link AXMLWriteFormat0#writeXMLProlog}.*/
	protected String getXMLBodyElement(){ return "xml"; }
	
	/* ***************************************************************
	
			AStructFormatBase
	
	
	****************************************************************/
	/** Overriden to initialize a state machine
	and validate if it is a valid XML.*/
	@Override protected void openImpl()throws IOException
	{
		setStateHandler(START);
		//Now we need to trigger any file action which will
		//automatically consume the states which are necessary
		//to move to actual content production. Those will
		//fail if we are not a valid XML.
		hasElementaryData();
	}; 
	
};
