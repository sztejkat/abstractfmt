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
				
				/* -----------------------------------------------------------------------------------
				
							Content skipping
							
				-----------------------------------------------------------------------------------*/
				/**
					A state responsible for skipping a content of an element except it's name.
					This class do allow &gt; and &lt; inside "" segments of text
				*/
				private static abstract class AAttributeSkipper extends AToNextStateHandler<ATxtReadFormat1.TIntermediateSyntax>
				{
								/** Used to track quouted section which unfortunately
								do allows for &gt; */
								private boolean in_quoted_section;
					protected AAttributeSkipper(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
					{
						super(p);
					}
					@Override public void onEnter(){ in_quoted_section = false; };
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("AAttributeSkipper("+getName()+").toNextChar() ENTER");
						char c = readAlways();						
						if (!in_quoted_section)
						{
							switch(c)
							{
								case '>':
									{								
										if (TRACE) TOUT.println("AAttributeSkipper("+getName()+").toNextChar(), finished LEAVE");
										toNextStateHandler();
										return;
									}
								case '\"':
									{								
										if (TRACE) TOUT.println("AAttributeSkipper("+getName()+").toNextChar(), quouted section LEAVE");
										in_quoted_section = true;
									};
									break;
							}
						}else
						{
							if (c=='\"')
							{
								in_quoted_section = false;
								if (TRACE) TOUT.println("AAttributeSkipper("+getName()+").toNextChar(), no longer in quouted section LEAVE");
							};
						};
						if (TRACE) TOUT.println("AAttributeSkipper("+getName()+").toNextChar() LEAVE");
					};
				}
				
				
        		/** A state handler which is responsible for skipping content of elements which
        		are not allowed to contain inner elements.
        		*/
        		private static abstract class ASkippper extends AToNextSyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
        		{	
        							/** A starting phrase */
        							private final String catch_phrase;
        							/** A starting terminating */
        							private final String stop_phrase;
        							/** A char category to assign to skipped content, null to not assign anything */
        							protected final ATxtReadFormat1.TIntermediateSyntax emit_content_as;
        			/** Creates
        			@param catch_phrase a phrase which begins it, non null
        			@param stop_phrase a phrase which terminates it, non null
        			@param emit_content_as what character type use for emiting content.
        						If null not content is emited to upstream class.
        			@param p parser, non null */
        			protected ASkippper(String catch_phrase, 
        								String stop_phrase,
        								ATxtReadFormat1.TIntermediateSyntax emit_content_as,
        							     ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
        			{ 
        				super(p); 
        				assert(catch_phrase!=null);
        				assert(stop_phrase!=null);
        				this.catch_phrase = catch_phrase;
        				this.stop_phrase  = stop_phrase;
        				this.emit_content_as=emit_content_as;
        			};
        			/** Overriden to return null as those are overlapping states 
        			and return to previous state after processing. */
        			@Override protected IStateHandler getNextHandler(){ return null; }; 
        			@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("ASkippper("+getName()+").tryEnter() ENTER");
						if (tryLooksAt(catch_phrase))
						{
							if (emit_content_as!=null) queueNextChars(catch_phrase,emit_content_as);
							enterStateHandler();
							if (TRACE) TOUT.println("ASkippper("+getName()+").tryEnter()=true LEAVE");
							return true;
						}else
						{
							unread();
							if (TRACE) TOUT.println("ASkippper("+getName()+").tryEnter()=false LEAVE");
							return false;
						}
					};
					@Override public void toNextChar()throws IOException
        			{
        				if (TRACE) TOUT.println("ASkippper("+getName()+").toNextChar() ENTER");
        				if (looksAt(stop_phrase))
        				{
        					if (TRACE) TOUT.println("ASkippper("+getName()+").toNextChar(), got stop_phrase LEAVE");
        					if (emit_content_as!=null) queueNextChars(stop_phrase,emit_content_as);
        					leaveStateHandler();
        					return;
        				}else
        				{
        					//now we need to un-read everything from collected buffer except first 
        					//character to have a flow-in iterative lookup.
        					final int n = collected.length();
        					if ((n!=0)&&(emit_content_as!=null)) queueNextChar(collected.charAt(0),emit_content_as);
        					for(int i=1;i<n;i++)
        					{
        						unread(collected.charAt(i));
        					};
        				};
        				if (TRACE) TOUT.println("ASkippper("+getName()+").toNextChar() LEAVE");
        			};
        		}
				
        		/** A state handler which is responsible for skipping content of elements which
        		are allowed to contain nested elements. This class will do a "dumb" skipping assuming
        		always that elements do start with &lt; and ends with &gt; regardless of context. 
        		*/
        		private static abstract class ANestedSkippper extends ASkippper
        		{
        						/** A nested &lt; count. The XML is in generic not 
        						allowing nested comment and exct, but, unfortunately
        						allows nested elements in DOCTYPE. */
        						private int nested;
        			/** Creates
        			@param catch_phrase a phrase which begins it, non null
        			@param stop_phrase a phrase which terminates it, non null
        			@param emit_content_as what character type use for emiting content.
        						If null not content is emited to upstream class.
        			@param p parser, non null */
        			protected ANestedSkippper(String catch_phrase, 
        								String stop_phrase,
        								ATxtReadFormat1.TIntermediateSyntax emit_content_as,
        							    ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
        			{
        				super(catch_phrase,stop_phrase,emit_content_as,p);
        			};
        			@Override public void onEnter(){ nested = 0; };
        			@Override public void toNextChar()throws IOException
        			{
        				if (TRACE) TOUT.println("ANestedSkippper("+getName()+").toNextChar() ENTER");
        				if (nested==0)
        				{
        					char c = readAlways();
							if (c=='<')
							{
								if (TRACE) TOUT.println("ANestedSkippper("+getName()+").toNextChar() detected nested element.");
								if (emit_content_as!=null) queueNextChar('<',emit_content_as);
								nested++;
							}else
							{
								unread(c);
								super.toNextChar();
							};
        				}else
        				{
        					if (TRACE) TOUT.println("ANestedSkippper("+getName()+").toNextChar(), nested processing ENTER");
							char c = readAlways();
							if (emit_content_as!=null) queueNextChar(c,emit_content_as);
							if (c=='<')
							{
								nested++;
							}else
							if (c=='>')
							{
								nested--;
							};
						}
						if (TRACE) TOUT.println("ANestedSkippper("+getName()+").toNextChar() LEAVE");
        			};
        		};
        		
        		
        		/* -----------------------------------------------------------------------------------
				
							Special characters escapes.
							
				-----------------------------------------------------------------------------------*/     
				/* ................................................................................
								Tooling
				................................................................................*/
        		/** Base for escapes handlers */
        		private static abstract class AEscapeHandler extends ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
        		{
        						/** What to report character as */
								protected final ATxtReadFormat1.TIntermediateSyntax report_unescaped_as;
						protected AEscapeHandler(ATxtReadFormat1.TIntermediateSyntax report_unescaped_as,
													ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(p);
							assert(report_unescaped_as!=null);
							this.report_unescaped_as=report_unescaped_as;
						};
						/* **********************************************************
									Tunable services
						***********************************************************/
						/** Invoked with each processed character. Used by escapes in signals to 
						capture un-escaped raw form of a signal name necessary later
						for start tag/ end tag matching. Default is empty.
						@param c what is processed */
						protected void collectDirectly(char c){};
						
        		};
        		
        		
        		/** 
        			A base for escapes with digital code.
				*/
				private static abstract class ACodeBasedEscapeHandler extends AEscapeHandler
				{
								/** Counts digits */
								protected int count = 0;
								/** Preserves un-escaped value of unicode code-point */
								protected int unescaped = 0;
								
						protected ACodeBasedEscapeHandler(ATxtReadFormat1.TIntermediateSyntax report_unescaped_as,
														  ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(report_unescaped_as,p);
						};
						/** queues unescaped code-point after validating it
						and pops state handler.
						@throws EBrokenFormat if un-escaped is not in valid unicode range.
						*/
						protected void queueUnescaped()throws EBrokenFormat
						{
							if (TRACE) TOUT.println("ACodeBasedEscapeHandler.queueUnescaped(), unescaped="+unescaped+" 0x"+Integer.toHexString(unescaped)+" ENTER");
							if (!((unescaped>=0)&&(unescaped<=0x10FFFF)))
								throw new EBrokenFormat("Escape produced 0x"+
											Integer.toHexString(unescaped)+
											" which is not valid 0...0x10FFFF unicode code point :"+getLineInfoMessage());
							queueNextCodepoint(unescaped,report_unescaped_as);
							popStateHandler();
							if (TRACE) TOUT.println("ACodeBasedEscapeHandler.queueUnescaped() LEAVE");
							
						};
						/* **********************************************************
									AStateHandler
						***********************************************************/
						@Override public void onEnter(){ count=0; unescaped=0; };
				};
						
				/* ................................................................................
								Underscore escapes (primiary in names, but also
								for prohibited code point like zero)
				................................................................................*/
        		/** An handler responsible for handling _XXXX escapes.
					This handler is pushed on stack if detects escapes and is
					popped when finishes parsing it.
				*/
				private static class CUnderscoreHexEscapeHandler extends ACodeBasedEscapeHandler
				{
						protected CUnderscoreHexEscapeHandler(ATxtReadFormat1.TIntermediateSyntax report_unescaped_as,
													ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(report_unescaped_as,p);
						};
						/* **********************************************************
												ASyntaxHandler
						***********************************************************/
						@Override public String getName(){ return "CUnderscoreHexEscapeHandler"; };
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("CUnderscoreHexEscapeHandler() ENTER");
							if (tryLooksAt("_"))
							{
								pushStateHandler(this);
								//collect it for direct processing.
								collectDirectly('_');
								if (TRACE) TOUT.println("CUnderscoreHexEscapeHandler.tryEnter()=true LEAVE");
								return true;
							}else
							{
								unread();
								if (TRACE) TOUT.println("CUnderscoreHexEscapeHandler.tryEnter()=false LEAVE");
								return false;
							}
						};
						@Override public void toNextChar()throws IOException
						{
							if (TRACE) TOUT.println("CUnderscoreHexEscapeHandler.toNextChar() ENTER");
							char c = readAlways();										
							if (TRACE) TOUT.println("CUnderscoreHexEscapeHandler.toNextChar() unescaping \'"+c+"\'");
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
								throw new EBrokenFormat("Not a hex digit '"+digit+"\' in _XXXX style escape"+getLineInfoMessage());
								
							collectDirectly(c); //for direct processing.
							unescaped<<=4;
							unescaped|=nibble;
							count++;
							if (count==4)
							{
								queueUnescaped();//Note this is a bit overkill because we can't get over 0xFFFF in _ escape.
								if (TRACE) TOUT.println("CUnderscoreHexEscapeHandler.toNextChar(), unescaped LEAVE");
							};
						};
				}
				/** An handler responsible for handling __ self escape.
					This handler is pushed on stack if detects escapes and is
					popped when finishes parsing it.
				*/
				private static class CUnderscoreSelfEscapeHandler extends AEscapeHandler
				{
								
						protected CUnderscoreSelfEscapeHandler(ATxtReadFormat1.TIntermediateSyntax report_unescaped_as,
													ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(report_unescaped_as,p);
						};
						/* **********************************************************
												ASyntaxHandler
						***********************************************************/
						@Override public String getName(){ return "CUnderscoreSelfEscapeHandler"; };
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("CUnderscoreSelfEscapeHandler.tryEnter() ENTER");
							if (tryLooksAt("__"))
							{
								if (TRACE) TOUT.println("CUnderscoreSelfEscapeHandler.tryEnter()=true collected=\""+collected+"\", LEAVE");
								queueNextChar('_',report_unescaped_as);
								collectDirectly('_');
								collectDirectly('_');
								return true;
							}else
							{
								if (TRACE) TOUT.println("CUnderscoreSelfEscapeHandler.tryEnter()=false collected=\""+collected+"\", LEAVE");
								unread();
								return false;
							}
						};
						@Override public void toNextChar()throws IOException{throw new AssertionError();}
				};
				
				
				/* ................................................................................
								& based escapes.
				................................................................................*/
				/** An handler responsible for handling &amp;#xXXXX; escapes.
					This handler is pushed on stack if detects escapes and is
					popped when finishes parsing it.
				*/
				private static class CAmpHexEscapeHandler extends ACodeBasedEscapeHandler
				{
						protected CAmpHexEscapeHandler(ATxtReadFormat1.TIntermediateSyntax report_unescaped_as,
													ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(report_unescaped_as,p);
						};
						/* **********************************************************
												ASyntaxHandler
						***********************************************************/
						@Override public String getName(){ return "CAmpHexEscapeHandler"; };
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("CAmpHexEscapeHandler() ENTER");
							if (tryLooksAt("&#x"))
							{
								//yep, hex escape
								pushStateHandler(this);
								collectDirectly('&');
								collectDirectly('#');
								collectDirectly('x');
								return true;
							}else
							{
								unread();
								return false;
							}
						}
						@Override public void toNextChar()throws IOException
						{
							if (TRACE) TOUT.println("CAmpHexEscapeHandler.toNextChar() ENTER");
							char c = readAlways();
							collectDirectly(c); //for direct processing.
							if (TRACE) TOUT.println("CAmpHexEscapeHandler.toNextChar() unescaping \'"+c+"\'");
							if (c==';')
							{
								queueUnescaped();
								if (TRACE) TOUT.println("CAmpHexEscapeHandler.toNextChar(), unescaped LEAVE");
							}else
							{
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
								if (count==6)
									throw new EBrokenFormat("too many digins in &#x escape: "+getLineInfoMessage());
								unescaped<<=4;
								unescaped|=nibble;
								count++;
							};
						};
				}
				/** An handler responsible for handling &amp;#xXXXX; escapes.
					This handler is pushed on stack if detects escapes and is
					popped when finishes parsing it.
				*/
				private static class CAmpDecimalEscapeHandler extends ACodeBasedEscapeHandler
				{
						protected CAmpDecimalEscapeHandler(ATxtReadFormat1.TIntermediateSyntax report_unescaped_as,
													ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(report_unescaped_as,p);
						};
						/* **********************************************************
												ASyntaxHandler
						***********************************************************/
						@Override public String getName(){ return "CAmpDecimalEscapeHandler"; };
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("CAmpDecimalEscapeHandler() ENTER");
							if (tryLooksAt("&#"))
							{
								//yep, hex escape
								pushStateHandler(this);
								collectDirectly('&');
								collectDirectly('#');
								return true;
							}else
							{
								unread();
								return false;
							}
						};
						@Override public void toNextChar()throws IOException
						{
							if (TRACE) TOUT.println("CAmpDecimalEscapeHandler.toNextChar() ENTER");
							char c = readAlways();
							collectDirectly(c); //for direct processing.
							if (TRACE) TOUT.println("CAmpDecimalEscapeHandler.toNextChar() unescaping \'"+c+"\'");
							if (c==';')
							{
								queueUnescaped();
								if (TRACE) TOUT.println("CAmpDecimalEscapeHandler.toNextChar(), unescaped LEAVE");
							}else
							{
								final char digit = c;
								final int dec;
								if ((digit>='0')&&(digit<='9'))
								{
									dec = digit - '0';
								}else
									throw new EBrokenFormat("Not a hex digit '"+digit+"\'"+getLineInfoMessage());
								if (count==7)
									throw new EBrokenFormat("too many digins in &# escape: "+getLineInfoMessage());
								unescaped*=10;
								unescaped+=dec;
								count++;
							};
						};
				}
				
				/** A handler responsible for detecting pre-definned <code>&amp;something;</code>
					escapes.
				*/
				private class CAmpEntityEscapeHandler extends AEscapeHandler
				{
						protected CAmpEntityEscapeHandler(ATxtReadFormat1.TIntermediateSyntax report_unescaped_as,
														  ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> p)
						{
							super(report_unescaped_as,p);
						};
						/* **********************************************************
												ASyntaxHandler
						***********************************************************/
						@Override public String getName(){ return "CAmpEntityEscapeHandler"; };
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("CAmpEntityEscapeHandler.tryEnter() ENTER");
							for(int i = entities_escapes.length; --i>=0;)
							{
								if (orTryLooksAt(entities_escapes[i]))
								{
									queueNextChar(entities_escapes_chars[i],report_unescaped_as);
									for(int j =0, n=collected.length(); j<n; j++)
									{
										collectDirectly(collected.charAt(j));
									};
									if (TRACE) TOUT.println("CAmpEntityEscapeHandler.tryEnter()=true, matched "+entities_escapes[i]+", LEAVE");
									return true;
								}
							};
							if (TRACE) TOUT.println("CAmpEntityEscapeHandler.tryEnter()=false, LEAVE");
							return false;
						};
						@Override public void toNextChar()throws IOException{throw new AssertionError();}
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
				/** A ByteOrderMark handler.
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
				private final ISyntaxHandler PROLOG = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "PROLOG"; };
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("PROLOG.tryEnter() ENTER");
						if (tryLooksAt("<?xml"))
						{
							//yep, this is prolog.
							setStateHandler(PROLOG_ATTRSKIPPER);
							if (TRACE) TOUT.println("PROLOG.tryEnter()=true, LEAVE");
							return true;
						}else
						{
							unread();
							if (TRACE) TOUT.println("PROLOG.tryEnter()=false, LEAVE");
							return false;
						}
					};
					@Override public void toNextChar(){ throw new AssertionError(); };
				};
				/** Skips prolog attributes */
				private final IStateHandler PROLOG_ATTRSKIPPER = new AAttributeSkipper(this)
				{
					@Override public String getName(){ return "PROLOG_ATTRSKIPPER"; };
					@Override protected IStateHandler getNextHandler(){ return XML_BODY_LOOKUP; };
				};
				/** A state responsible for looking for the opening tag specified by
				{@link #getXMLBodyElement}. Finally moves to {@link #ELEMENT_BODY}
				*/				
				private final IStateHandler XML_BODY_LOOKUP = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "XML_BODY_LOOKUP"; };
					@Override public boolean tryEnter(){ throw new AssertionError(); };
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("XML_BODY_LOOKUP.toNextChar() ENTER");
						if (!COMMENT.tryEnter())
						if (!PI.tryEnter())
						if (!CDATA.tryEnter())
						if (!DOCTYPE.tryEnter())
						if (!WHITESPACE.tryEnter())
						{
							//Now find that dedicated tag.
							//Note: below call should be "<"+getXMLBodyElement()+">"
							//      but it would be always constructing new String
							//		so instead I will use sequence of less expensive
							//		calls but in a bit peculiar order and unread sequence.
							if (TRACE) TOUT.println("XML_BODY_LOOKUP.toNextChar - looking for <"+getXMLBodyElement());
							if (tryLooksAt("<"))
							{
								if (TRACE) TOUT.println("XML_BODY_LOOKUP.toNextChar got <");
								if (tryLooksAt(getXMLBodyElement()))
								{
									if (TRACE) TOUT.println("XML_BODY_LOOKUP.toNextChar got element");
									if (tryLooksAt(">"))
									{
										if (TRACE) TOUT.println("XML_BODY_LOOKUP.toNextChar got >");
										//Matching required tag. 
										//Remember what is expected to be an end-tag.
										end_tags.push("</"+getXMLBodyElement()+">");
										//Make common body handler current
										setStateHandler(ELEMENT_BODY);		
										//emit character to prevent subsequent scanning of body
										queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.VOID);
										if (TRACE) TOUT.println("XML_BODY_LOOKUP.toNextChar() found, LEAVE");
										return;
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
							int r= read();
							if (r!=-1) throw new EBrokenFormat("Invalid syntax, \'"+(char)r+"\', <"+getXMLBodyElement()+" expected:"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("XML_BODY_LOOKUP.toNextChar() LEVE");
					};
				};
				
				
				
				
				/** A terminating state, entered once detected closing of the main
				xml element. It's sole purpose is to return end-of-file	for any other possible access.
				*/
				private final IStateHandler NO_MORE_DATA = new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "NO_MORE_DATA"; };
					@Override public void toNextChar()throws IOException
				    {
				   	   if (TRACE) TOUT.println("NO_MORE_DATA.toNextChar() ENTER");
				   	   //Persisten "no more data" regardless of what is in stream.
				   	   // Notice we have two choices:
				   	   //		throw or queue -1.
				   	   //		The contract of ATxtReadFormat1.toNextChar forbids 
				   	   //		us to throw if we encounter	end of file.
				   	   //
				   	   // 
				   	   queueNextChar(-1,null);
					   if (TRACE) TOUT.println("NO_MORE_DATA.toNextChar() LEAVE");
				   };
				};
				
				
			
				/** 
					A state responsible for detecting and processing the start tag
					which indicates the "begin-signal".
					<p>
					Do handle start tag with zero length name (against XML standard), accepts name _ as a
					"zero length name", accepts all underscore escapes,
					allows all non-space characters in names (against XML standard)
					and self-closing tags.
				*/
				private final ISyntaxHandler BEGIN_TAG = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
								/** A hex-escape sequence handler */
							private final ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax> HEX_ESCAPE_SEQUENCE = 
									new CUnderscoreHexEscapeHandler(ATxtReadFormat1.TIntermediateSyntax.SIG_NAME,parser)
									{
										@Override protected void collectDirectly(char c)
										{
											directCollected().append(c);
										};
									};
								
							/** A __ escape handler */
							private final ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax> SELF_ESCAPE_SEQUENCE = 
								new CUnderscoreSelfEscapeHandler(ATxtReadFormat1.TIntermediateSyntax.SIG_NAME,parser)
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
							end_tags.push("</_>");
							setStateHandler(ELEMENT_BODY);
							if (TRACE) TOUT.println("BEGIN_TAG.tryEnter()=true, empty name, LEAVE");
							return true;
						}else
						if (orTryLooksAt("<_/>"))
						{
							//self closing empty name.
							queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);	//begin
							queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID); //empty name
							queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);	//end of name
							queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_END);	//self closing.
							setStateHandler(ELEMENT_BODY);
							if (TRACE) TOUT.println("BEGIN_TAG.tryEnter()=true, self closing empty name, LEAVE");
							return true;
						}else
						{
							int ci=tryRead(); 
							if (ci=='<')//now full text variants. Avoid collecting it so tryRead instead of tryLooksAt
							{
								//Now check if it is not the end tag?
								char c= readAlways();
								unread(c);//in all cases it will be processed elsewhere.
								if (c=='/')
								{
									if (TRACE) TOUT.println("BEGIN_TAG.tryEnter()=false, end tag LEAVE");
									return false;
								}else
								//now be flexible and allow all non-space
								if (!classifier.isXMLSpace(c))
								{
									queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);	//begin
									queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID); //empty name, just in case
									pushStateHandler(this);
									if (TRACE) TOUT.println("BEGIN_TAG.tryEnter()=true, full name, LEAVE");
									return true;
								}else
									throw new EBrokenFormat("Invalid first character in element name \'"+c+"\' :"+getLineInfoMessage());
							}else
							{
								if (ci!=-1)	unread((char)ci);
								if (TRACE) TOUT.println("BEGIN_TAG.tryEnter()=false, LEAVE");
								return false;
							}
						}
					};
					@Override public void toNextChar()throws IOException
					{
						//Now we have a name to emit. Notice the name length limit is catched by
						//superclass so we can just ignore bounds and process it char by char 
						//and will be stopped if something wrong happens.
						if (TRACE) TOUT.println("BEGIN_TAG.toNextChar() ENTER");
						//first read character to collect it for direct processing
						if (!SELF_ESCAPE_SEQUENCE.tryEnter())
						if (!HEX_ESCAPE_SEQUENCE.tryEnter())
						{
							char c = readAlways();						
							if (c=='>')
							{								
								queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);	//end of name
								end_tags.push("</"+collected+">"); //includes raw escape sequences								
								if (TRACE) TOUT.println("BEGIN_TAG.toNextChar(), got unescaped name \""+collected+"\" LEAVE");
								setStateHandler(ELEMENT_BODY);
								return;
							}else
							if (c=='/')
							{
								//possibly a self closing tag?
								char cn = readAlways();
								if (cn=='>')
								{
									//yes, self closing.
									queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_END);	//end of name, end.
									setStateHandler(ELEMENT_BODY);
									if (TRACE) TOUT.println("BEGIN_TAG.toNextChar(), self closing tag, LEAVE"); 
									return;
								}else
								{
									throw new EBrokenFormat("Invalid self closing tag character \'"+c+"\', expected > :"+getLineInfoMessage()); 
								}
							}else
							//now be flexible and allow all non-space
							if (classifier.isXMLSpace(c))
							{
								//attribute skipper.
								queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);	//end of name
								end_tags.push("</"+collected+">"); //includes raw escape sequences		
								if (TRACE) TOUT.println("BEGIN_TAG.toNextChar(), got unescaped name \""+collected+"\" but possible attributes LEAVE");
								setStateHandler(ATTRIBUTES_SKIPPER);
								return;
							}else
							{
								//Now collect it
								collected.append(c);
								//now pass it up.
								queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME); //empty name
								if (TRACE) TOUT.println("BEGIN_TAG.toNextChar(), collected \'"+c+"\'");
							}
						};
					};
				};
				
				/** A state responsible for skipping eventuall attributes in dumbest possible way */
				private final IStateHandler ATTRIBUTES_SKIPPER = new AAttributeSkipper(this)
				{
					@Override public String getName(){ return "ATTRIBUTES_SKIPPER"; };
					@Override protected IStateHandler getNextHandler(){ return ELEMENT_BODY; };
				};
				/** 
					A class which is processing a body of an element.					
				*/
				private final IStateHandler ELEMENT_BODY =  new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
								
						/* **************************************************************
							
								AStateHandler
						
						***************************************************************/
						@Override public String getName(){ return "ELEMENT_BODY"; };
						@Override public void toNextChar()throws IOException
						{
						   if (TRACE) TOUT.println("ELEMENT_BODY.toNextChar() ENTER");
						   //Note: comparison must start from longer to shorter pattern
						   //which may conflict.
						   if (!COMMENT.tryEnter())		//<!--
						   if (!PI.tryEnter())			//<?
						   if (!CDATA.tryEnter())		//<[CDATA[					   
						   if (!END_TAG.tryEnter())		//</
						   if (!BEGIN_TAG.tryEnter())	//<   -- this may conflict with above
						   if (!WHITESPACE.tryEnter())	
						   if (!PLAIN_TOKEN.tryEnter()) 
						   if (!STRING_TOKEN.tryEnter())
						   {
								int r= read();
								if (r!=-1) throw new EBrokenFormat("Invalid syntax, \'"+(char)r+"\' :"+getLineInfoMessage());
						   }
						   if (TRACE) TOUT.println("ELEMENT_BODY.toNextChar() LEAVE");
						};
				};
				/** 
					Detects closing tag, either anonymous or expected directly
					as {@link #end_tags} tells. If removed last element
					from a stack moves to {@link #NO_MORE_DATA}.
					Otherwise moves to {@link #ELEMENT_BODY}.
				*/
				private final ISyntaxHandler END_TAG = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "END_TAG"; };
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("END_TAG.tryEnter() ENTER");
						if (orTryLooksAt("</>") 
							|| 
							orTryLooksAt(end_tags.peek())
							//Note: end_tags is never empty, always contains at least main body
							//so peek will never give null gere.
							) 				 
						{							
							//anonymous end tag or full end tag.
							end_tags.pop();
							if (end_tags.isEmpty())
							{
								//Picked up last possible state.
								if (TRACE) TOUT.println("END_TAG.tryEnter(), closed main body element.");
								setStateHandler(NO_MORE_DATA);
								//We do NOT indicate SIG_END, because this is EOF in fact.
							}else
							{
								if (TRACE) TOUT.println("END_TAG.tryEnter(), closed user element");
								setStateHandler(ELEMENT_BODY);
								queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
							}							
							if (TRACE) TOUT.println("END_TAG.tryEnter()=true, LEAVE");
							return true;
						}else
						if (orTryLooksAt("</"))
						{
							//Now we failed anonymous tag and we failed expected tag.
							//This means, we have an unexpected closing tag"
							throw new EBrokenFormat("Closing tag miss-match, either </> or </"+end_tags.peek()+"> is expected:"+getLineInfoMessage());
						}else
						{
							if (TRACE) TOUT.println("END_TAG.tryEnter()=false, LEAVE");
							return false;
						}
					};
					@Override public void toNextChar()throws IOException{ throw new AssertionError(); };
				};
	
				/** A comment detector and skipper. First character is reported as 
				{@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR} since comments do break tokens.
				*/
				private final ISyntaxHandler COMMENT = new ASkippper("<!--","-->",ATxtReadFormat1.TIntermediateSyntax.SEPARATOR, this)
				{
					@Override public String getName(){ return "COMMENT"; };
				};
				/** A processing instruction detector and skipper. First character is reported as 
				{@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR} since it do break tokens.
				*/
				private final ISyntaxHandler PI = new ASkippper("<?","?>",ATxtReadFormat1.TIntermediateSyntax.VOID,this)
				{
					@Override public String getName(){ return "PI"; };
				};
				/** A doctype schema skipper */
				private final ISyntaxHandler DOCTYPE = new ANestedSkippper("<!DOCTYPE",">",ATxtReadFormat1.TIntermediateSyntax.VOID,this)
				{
					@Override public String getName(){ return "DOCTYPE"; };
				};
				/** A cdata instruction detector and skipper. First character is reported as 
				{@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR} since it do break tokens.
				*/
				private final ISyntaxHandler CDATA = new ASkippper("<![CDATA[","]]>",ATxtReadFormat1.TIntermediateSyntax.SEPARATOR,this)
				{
					@Override public String getName(){ return "CDATA"; };
				};
				/** A state handler reporting all white-spaces as separators 
				and returning to previous state. */
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
							unread((char)r);
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
				
				/** A state responsible for collecting a plain token.
				Moves to {@link #TOKEN_SEPARATOR}
				*/
				private final ISyntaxHandler PLAIN_TOKEN = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{	
					@Override public String getName(){ return "PLAIN_TOKEN"; };
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter() ENTER");
						int r= tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter()=false, eof LEAVE");
							return false;
						};
						char c = (char)r;
						unread(c);//in all cases.
						//Now it cannot be " of quouted token 
						if (c=='\"')
						{
							if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter()=false, string token, LEAVE");
							return false;
						}else
						if (c==',')
						{
							//this is an empty token
							queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.TOKEN_VOID);
							queueNextChar(0,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
							//move to token separator lookup.
							setStateHandler(TOKEN_SEPARATOR);
							if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter()=true, empty token, LEAVE");
							return true;
						}else
						if (!classifier.isXMLSpace(r))
						{
							//ok, this is a plain token. 
							if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter()=true, token, LEAVE");
							setStateHandler(this);
							return true;
						}else						
						{
							if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter()=false, LEAVE");
							return false;
						}
					};
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("PLAIN_TOKEN.toNextChar() ENTER");
						if (!TOKEN_AMP_HEX_ESCAPE.tryEnter())	//just XML escapes which must be understood there.
						if (!TOKEN_AMP_DEC_ESCAPE.tryEnter())
						if (!TOKEN_AMP_ENTITY_ESCAPE.tryEnter())
						{
							//must be e token char or end of token.
							int r = read();
							if (r==-1)
							{
								if (TRACE) TOUT.println("PLAIN_TOKEN.toNextChar(), eof LEAVE");
								return;
							};
							char c = (char)r;
							if (
								(c==',')||  //separator
								(classifier.isXMLSpace(r))|| //natural end
								(c=='<')    //tag or any of comment and etc.
								)
							{
								//terminate token
								unread(c);
								queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
								setStateHandler(TOKEN_SEPARATOR);
								if (TRACE) TOUT.println("PLAIN_TOKEN.toNextChar(), terminated token");
							}else
							if (c=='\"')
							{
								throw new EBrokenFormat("plain token cannot contain \'"+c+"\' :"+getLineInfoMessage());
							}else
							{
								//emit as token.
								if (TRACE) TOUT.println("PLAIN_TOKEN.toNextChar() -> \'"+c+"\'");
								queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.TOKEN);
							};
						}
						if (TRACE) TOUT.println("PLAIN_TOKEN.toNextChar() LEAVE");
					};
				};
				/** Escape handler for _XXXX escape in tokens */
				private final ISyntaxHandler TOKEN_UNDERSCORE_ESCAPE = new CUnderscoreHexEscapeHandler(
																					ATxtReadFormat1.TIntermediateSyntax.TOKEN,
																					this
																					);
				/** Escape handler for __ escape in tokens */
				private final ISyntaxHandler TOKEN_UNDERSCORE_SELF_ESCAPE = new CUnderscoreSelfEscapeHandler(
																					ATxtReadFormat1.TIntermediateSyntax.TOKEN,
																					this
																					);
				/** Escape handler for &amp;#xXXXX; escape in tokens */				
				private final ISyntaxHandler TOKEN_AMP_HEX_ESCAPE = new CAmpHexEscapeHandler(
																					ATxtReadFormat1.TIntermediateSyntax.TOKEN,
																					this
																					);
				/** Escape handler for &amp;#dddd; escape in tokens */
				private final ISyntaxHandler TOKEN_AMP_DEC_ESCAPE = new CAmpDecimalEscapeHandler(
																					ATxtReadFormat1.TIntermediateSyntax.TOKEN,
																					this
																					);
				/** Escape handler for &amp;<i>name</i>; escape in tokens */
				private final ISyntaxHandler TOKEN_AMP_ENTITY_ESCAPE = new CAmpEntityEscapeHandler(
																					ATxtReadFormat1.TIntermediateSyntax.TOKEN,
																					this
																					);
				
				
				/** A state responsible for collecting a string token */
				private final ISyntaxHandler STRING_TOKEN = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{	
					@Override public String getName(){ return "STRING_TOKEN"; };
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("STRING_TOKEN.tryEnter() ENTER");
						int r= tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("STRING_TOKEN.tryEnter()=false, eof LEAVE");
							return false;
						};
						char c = (char)r;
						if (c=='\"')
						{
							if (TRACE) TOUT.println("STRING_TOKEN.tryEnter()=true, LEAVE");
							//ok, this is a string token. 
							queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.TOKEN_VOID);//to indicate at least an empty token.
							setStateHandler(this);
							return true;
						}else
						{
							if (TRACE) TOUT.println("STRING_TOKEN.tryEnter()=false, LEAVE");
							unread(c);
							return false;
						}
					};
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("STRING_TOKEN.toNextChar() ENTER");
						if (!TOKEN_UNDERSCORE_SELF_ESCAPE.tryEnter())
						if (!TOKEN_UNDERSCORE_ESCAPE.tryEnter())
						if (!TOKEN_AMP_HEX_ESCAPE.tryEnter())
						if (!TOKEN_AMP_DEC_ESCAPE.tryEnter())
						if (!TOKEN_AMP_ENTITY_ESCAPE.tryEnter())
						{
							//must be e token char or end of token.
							int r = read();
							if (r==-1)
							{
								if (TRACE) TOUT.println("STRING_TOKEN.toNextChar(), eof LEAVE");
								return;
							};
							char c = (char)r;
							switch(c)
							{
									case '\"': //only this terminates the string token.
											//terminate token
											queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
											setStateHandler(TOKEN_SEPARATOR);
											if (TRACE) TOUT.println("STRING_TOKEN.toNextChar(), terminated token");
											return;
									case '<':
											//This is an error to have this is a qouted token even tough xml
											//do allow that.
											throw new EBrokenFormat("Unclosed quouted token or XML command inside it :"+getLineInfoMessage());
									default:
											//emit as token.
											queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.TOKEN);
							}
						}
						if (TRACE) TOUT.println("STRING_TOKEN.toNextChar() LEAVE");
					};
				};
				
				
				/** 
					A state which is looking for a separator between tokens.				
				*/
				private final IStateHandler TOKEN_SEPARATOR =  new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
						/* **************************************************************
							
								AStateHandler
						
						***************************************************************/
						@Override public String getName(){ return "TOKEN_SEPARATOR"; };
						@Override public void toNextChar()throws IOException
						{
						   if (TRACE) TOUT.println("TOKEN_SEPARATOR.toNextChar() ENTER");
						   //Note: comparison must start from longer to shorter pattern
						   //which may conflict.
						   if (!COMMENT.tryEnter())		//<!--
						   if (!PI.tryEnter())			//<?
						   if (!CDATA.tryEnter())		//<[CDATA[					   
						   if (!END_TAG.tryEnter())		//</
						   if (!BEGIN_TAG.tryEnter())	//<   -- this may conflict with above
						   if (!WHITESPACE.tryEnter())	//this state is pushed over us
						   {
								int r= read();
								if (r==-1)
								{
									if (TRACE) TOUT.println("TOKEN_SEPARATOR.toNextChar() LEAVE");
									return;
								};
								if (r==',')
								{
									queueNextChar(',',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
									setStateHandler(ELEMENT_BODY);
									if (TRACE) TOUT.println("TOKEN_SEPARATOR.toNextChar(), found separator, LEAVE");
									return;
								}else
									throw new EBrokenFormat("Invalid syntax, \'"+(char)r+"\' :"+getLineInfoMessage());
						   }
						   if (TRACE) TOUT.println("TOKEN_SEPARATOR.toNextChar() LEAVE");
						};
				};
				/** Pre-defined set of amp based escapes.
				@see #PREFEFINED_ENTITIES_ESCAPES_CHARS
				@see AXMLEscapingEngineBase#escapeCodePointAsEntity
				*/
				public static final String [] PREFEFINED_ENTITIES_ESCAPES =
				{
						"&amp;",
						"&gt;",
						"&lt;",
						"&apos;",
						"&quot;"
				};
				/** Pre-defined set of amp based escaped character,
				matches {@link #PREFEFINED_ENTITIES_ESCAPES}*/
				public static final char [] PREFEFINED_ENTITIES_ESCAPES_CHARS = 
				{
					'&',
					'>',
					'<',
					'\'',
					'\"'
				};
				/** XML classifier */
				private final IXMLCharClassifier classifier;
				/** Expected end-tags stack */
				private final ArrayDeque<String> end_tags = new ArrayDeque<String>();
				/** Amp entities to detect as escapes for
				special characters and produce matching
				{@link #entities_escapes_chars} */
				private final String [] entities_escapes;
				/** Products of matching {@link #entities_escapes} */
				private final char [] entities_escapes_chars;
				
				
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
		this(in,
			 new CXMLChar_classifier_1_0_E4(),
			 PREFEFINED_ENTITIES_ESCAPES,
			 PREFEFINED_ENTITIES_ESCAPES_CHARS
			);
	};
	/** Creates
	@param in non null down-stream reader. This reader will be wrapped in
			{@link CAdaptivePushBackReader} and will be accessible through
			{@link #in} field. Will be closed on {@link #close}.
			<p>
			No I/O operation will be generate till {@link #open}.
	@param classifier classifier to use, non null. 
	@param entities_escapes entities to detect as escapes for special characters and produce matching.
			Non null, can't carry nulls, same length as <code>entities_escapes_chars</code>.
			Usually {@link #PREFEFINED_ENTITIES_ESCAPES}
	@param entities_escapes_chars characters produced by above. Usually {@link #PREFEFINED_ENTITIES_ESCAPES_CHARS}
	*/
	protected AXMLReadFormat0(Reader in,
							  IXMLCharClassifier classifier,
							  String [] entities_escapes,
							  char []   entities_escapes_chars 
							  )
	{
		//No name registry supported.
		super(in,
			  0, //int name_registry_capacity,
			  64);//int token_size_limit
		assert(classifier!=null);
		assert(entities_escapes_chars!=null);
		assert(entities_escapes!=null);
		assert(entities_escapes.length==entities_escapes_chars.length);
		this.classifier = classifier;
		this.entities_escapes =entities_escapes;
		this.entities_escapes_chars =entities_escapes_chars;
	};	
	/* ***************************************************************
	
			Tunable services
	
	
	*****************************************************************/
	/** Returns the name of XML body element opened by {@link AXMLWriteFormat0#writeXMLProlog}.
	@return XML body element, default is "sztejkat.abstractfmt.txt.xml" */
	protected String getXMLBodyElement(){ return "sztejkat.abstractfmt.txt.xml"; }
	/* *****************************************************************
	
			ATxtWriteFormat0
	
	******************************************************************/
	/** Enable packed byte sequenced */
	@Override protected byte readByteBlockImpl()throws IOException{ return readPackedByteBlockImpl(); };
	/** Enable packed byte sequenced */
	@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
	{ 
		return readPackedByteBlockImpl(buffer,offset,length);
	}
	/* ***************************************************************
	
			AStructFormatBase
	
	
	****************************************************************/
	/** Overriden to initialize a state machine
	and validate if it is a valid XML.*/
	@Override protected void openImpl()throws IOException
	{
		if (TRACE) TOUT.println("openImpl ENTER");
		setStateHandler(START);
		//Now we need to trigger any file action which will
		//automatically consume the states which are necessary
		//to move to actual content production. Those will
		//fail if we are not a valid XML.
		//We are however still not opened so we can't just ask
		//the high level routines.
		//We should however enforce going down to main element
		while(getStateHandler()!=ELEMENT_BODY)
		{
			toNextChar();
			if (getNextSyntaxElement()==null) throw new EUnexpectedEof("While looking for <"+getXMLBodyElement()+">");
		};
		if (TRACE) TOUT.println("openImpl LEAVE");
	}; 
	
};
