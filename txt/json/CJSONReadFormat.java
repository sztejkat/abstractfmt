package sztejkat.abstractfmt.txt.json;
import sztejkat.abstractfmt.txt.*;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Reader;


/**
	A JSON format reader.
*/
public class CJSONReadFormat extends ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax>
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CJSONReadFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CJSONReadFormat.",CJSONReadFormat.class) : null;
 
				/* ************************************************************************
				
				
						State graph
				
				
				
				*************************************************************************/
				
				/* -----------------------------------------------------------------------
					
						Support classes
				
				-----------------------------------------------------------------------*/
				/** A whitespace skipper handler */
				private static class CWhitespace extends ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
				{
								/** What to report first character as  */
								protected final ATxtReadFormat1.TIntermediateSyntax report_first_as;
						protected CWhitespace(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
												 ATxtReadFormat1.TIntermediateSyntax report_first_as
												)
						{
							super(parser);
							assert(report_first_as!=null);
							this.report_first_as=report_first_as;
						};
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println(getName()+".tryEnter() ENTER");
							int r = tryRead();
							if (r==-1)
							{
								if (TRACE) TOUT.println(getName()+".tryEnter()=false, eof, LEAVE");
								return false;
							}
							char c = (char)r;
							if (isJSONWhitespace(c))
							{
								//We do report first white-space as a specified type
								//subsequent as VOID.
								queueNextChar(c,report_first_as);
								pushStateHandler(this); //place self over current state.
								if (TRACE) TOUT.println(getName()+".tryEnter()=true, \'"+c+"\' is whitespace LEAVE");
								return true;
							}else
							{
								unread(c);
								if (TRACE) TOUT.println(getName()+".tryEnter()=false LEAVE");
								return false;
							}
						};
						@Override public void toNextChar()throws IOException
						{
							if (TRACE) TOUT.println(getName()+".toNextChar() ENTER");
							int r = read();
							if (r==-1)
							{
								if (TRACE) TOUT.println(getName()+".toNextChar() eof LEAVE");
								return;
							}
							char c = (char)r;
							if (!isJSONWhitespace(c))
							{
								unread(c);//restore for others to process
								popStateHandler(); //restore parent state handler.
								if (TRACE) TOUT.println(getName()+".toNextChar() end of whitespaces, LEAVE");
							}else
							{
								queueNextChar(c,TIntermediateSyntax.VOID);
								if (TRACE) TOUT.println(getName()+".toNextChar(), consumed LEAVE");
							}
						}
				}
				/** Base for escapes handlers */
        		private static abstract class AEscapeHandler extends ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
        		{
        						/** What to report character as */
								protected final ATxtReadFormat1.TIntermediateSyntax report_unescaped_as;
						protected AEscapeHandler(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
												 ATxtReadFormat1.TIntermediateSyntax report_unescaped_as
												)
						{
							super(parser);
							assert(report_unescaped_as!=null);
							this.report_unescaped_as=report_unescaped_as;
						};
        		};
				/** An handler responsible for handling &#x5C;uXXXX escapes.
					This handler is pushed on stack if detects escapes and is
					popped when finishes parsing it.
				*/
				private static class CHexEscapeHandler extends AEscapeHandler
				{
								/** Counts digits */
								protected int count = 0;
								/** Preserves un-escaped value of character */
								protected char unescaped = 0;
						protected CHexEscapeHandler(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
													ATxtReadFormat1.TIntermediateSyntax report_unescaped_as)
						{
							super(parser,report_unescaped_as);
						};
						@Override public String getName(){ return "CHexEscapeHandler"; };
						/* **********************************************************
									AStateHandler
						***********************************************************/
						@Override public void onEnter(){ count=0; unescaped=0; };
						/* **********************************************************
									ASyntaxHandler
						***********************************************************/
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("CHexEscapeHandler() ENTER");
							if (tryLooksAt("\\u"))
							{
								pushStateHandler(this);
								if (TRACE) TOUT.println("CHexEscapeHandler.tryEnter()=true LEAVE");
								return true;
							}else
							{
								unread();
								if (TRACE) TOUT.println("CHexEscapeHandler.tryEnter()=false LEAVE");
								return false;
							}
						};
						@Override public void toNextChar()throws IOException
						{
							if (TRACE) TOUT.println("CHexEscapeHandler.toNextChar() ENTER");
							char c = readAlways();										
							if (TRACE) TOUT.println("CHexEscapeHandler.toNextChar() unescaping \'"+c+"\'");
							final char digit = c;
							final char nibble;
							if ((digit>='0')&&(digit<='9'))
							{
								nibble = (char)(digit - '0');
							}else
							if ((digit>='A')&&(digit<='F'))
							{
								nibble = (char)(digit - 'A'+10);
							}else
							if ((digit>='a')&&(digit<='f'))
							{
								nibble = (char)(digit - 'a'+10);
							}else
								throw new EBrokenFormat("Not a hex digit '"+digit+"\' in _XXXX style escape"+getLineInfoMessage());
								
							unescaped<<=4;
							unescaped|=nibble;
							count++;
							if (count==4)
							{
								queueNextChar(unescaped,report_unescaped_as);
								popStateHandler();
								if (TRACE) TOUT.println("CHexEscapeHandler.toNextChar(), unescaped LEAVE");
							};
						};
				}
				
				
				/** An handler responsible for handling &#x5C;? symbolic JSON escapes.
				*/
				private static class CSymbolicEscapeHandler extends AEscapeHandler
				{
								private static final String [] ESCAPES =
											new String[]
											{  "\\\"","\\\\","\\/","\\b","\\f","\\n","\\r","\\t" };
								private static final char   [] UNESCAPED = new char []
											{  '\"',  '\\',  '/',  '\b',  '\f', '\n', '\r', '\t' };
												
						protected CSymbolicEscapeHandler(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
													ATxtReadFormat1.TIntermediateSyntax report_unescaped_as)
						{
							super(parser,report_unescaped_as);
						};
						@Override public String getName(){ return "CSymbolicEscapeHandler"; };
						/* **********************************************************
									ASyntaxHandler
						***********************************************************/
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("CSymbolicEscapeHandler.tryEnter() ENTER");
							for(int i = ESCAPES.length; --i>=0;)
							{
								if (orTryLooksAt(ESCAPES[i]))
								{
									queueNextChar(UNESCAPED[i],report_unescaped_as);
									if (TRACE) TOUT.println("CSymbolicEscapeHandler.tryEnter()=true, matched "+ESCAPES[i]+", LEAVE");
									return true;
								}
							};
							if (TRACE) TOUT.println("CSymbolicEscapeHandler.tryEnter()=false, LEAVE");
							return false;
						};
						@Override public void toNextChar()throws IOException{ throw new AssertionError(); };
				}
				
				/** A JSON string collecting state.
				Responsible for collecting a string, un-escaping it and reporting
				as a specified syntax to upper class.
				*/
				private static abstract class AJSONString extends ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
				{
								private final ATxtReadFormat1.TIntermediateSyntax report_void_as;
								private final ATxtReadFormat1.TIntermediateSyntax report_character_as;
								private final ISyntaxHandler HEX_ESCAPE;
								private final ISyntaxHandler SYMBOLIC_ESCAPE;
						/**
							Creates
							@param parser see {@link ASyntaxHandler#ASyntaxHandler}
							@param report_void_as how to report insignificant characters;
							@param report_character_as how to report significant characters.
						*/
						AJSONString(
											  ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
											  ATxtReadFormat1.TIntermediateSyntax report_void_as,
											  ATxtReadFormat1.TIntermediateSyntax report_character_as
											 )
						{
							super(parser);
							assert(report_void_as!=null);
							assert(report_character_as!=null);
							this.report_void_as =report_void_as ;
							this.report_character_as =report_character_as ;
							this.HEX_ESCAPE = new CHexEscapeHandler(parser, report_character_as);
							this.SYMBOLIC_ESCAPE = new CSymbolicEscapeHandler(parser, report_character_as);
						}
						/* ************************************************************
								Services required from subclasses
						*************************************************************/
						/** State to become active after string completion.
						<p>
						This method is declared as abstract istead of just having some field
						to overcome problems with cyclic initialization when constructing state
						graph.
						@return next state handler, non null.*/
						protected abstract IStateHandler getNextState();						
						/* ************************************************************
								ASyntaxHandler
						*************************************************************/
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("AJSONString.tryEnter() ENTER");
							int r = tryRead();
							if (r==-1)
							{
								if (TRACE) TOUT.println("AJSONString.tryEnter()=false, eof LEAVE");
								return false;
							}
							char c = (char)r;
							if (c=='\"')
							{
								setStateHandler(this);
								queueNextChar(c,report_void_as); //to allow for empty strings.
								if (TRACE) TOUT.println("AJSONString.tryEnter()=true, LEAVE");
								return true;
							}else
							{
								unread(c);
								if (TRACE) TOUT.println("AJSONString.tryEnter()=false, LEAVE");
								return false;
							}
						}
						@Override public void toNextChar()throws IOException
						{
							if (TRACE) TOUT.println("AJSONString.toNextChar() ENTER");
							if (!HEX_ESCAPE.tryEnter())
							if (!SYMBOLIC_ESCAPE.tryEnter())
							{
								int r = read();
								if (r==-1)
								{
									if (TRACE) TOUT.println("AJSONString.toNextChar(), eof LEAVE");
									return;
								};
								char c = (char)r;
								if (c=='\"')
								{
									queueNextChar(c,report_void_as);				//to ensure zero size token.
									queueNextChar(0,TIntermediateSyntax.SEPARATOR);	//to ensure that token finished.
									setStateHandler(getNextState());
									if (TRACE) TOUT.println("AJSONString.toNextChar(), end of string LEAVE");
								}else
								{
									//to report character.
									if (TRACE) TOUT.println("AJSONString.toNextChar() \'"+c+"\' LEAVE");
									queueNextChar(c,report_character_as);
								}
							}
							if (TRACE) TOUT.println("AJSONString.toNextChar() LEAVE");
						}
							
				};
				
				
				/** A JSON plain value collecting state.
				Responsible for collecting a plain value it and reporting it as a token to upstream.
				*/
				private static abstract class AJSONPlainValue extends ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
				{
						/**
							Creates
							@param parser see {@link ASyntaxHandler#ASyntaxHandler}
						*/
						AJSONPlainValue(
											  ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser
											 )
						{
							super(parser);
						}
						/* ************************************************************
								Services required from subclasses
						*************************************************************/
						/** State to become active after token completion 
						@return never null, life time constant*/
						protected abstract IStateHandler getNextState();						
						/* ************************************************************
								ASyntaxHandler
						*************************************************************/
						/** Tests if character can be a plain value character 
						@param c character
						@return true if can be.
						*/
						private static boolean isPlainValueChar(char c)
						{
							if (isJSONWhitespace(c)) return false;
							//Note: this is a bit awkward set. 
							//If we would strictly conform to JSON we would
							//allow here only those making numbers and booleans.
							//To be a bit relaxed we need to allow all non-syntax characters.
							switch(c)
							{
								case ',':	//separator
								case '\"':  //string token
								case '[':   //array body start
								case ']':   //array body end
								case '{':   //object start
								case '}':   //object end
										return false;
							};
							return true;
						};
						@Override public boolean tryEnter()throws IOException
						{
							if (TRACE) TOUT.println("AJSONPlainValue.tryEnter() ENTER");
							int r = tryRead();
							if (r==-1)
							{
								if (TRACE) TOUT.println("AJSONPlainValue.tryEnter()=false, eof LEAVE");
								return false;
							}
							char c = (char)r;
							if (isPlainValueChar(c))
							{
								//this is a plain token character
								queueNextChar(c,TIntermediateSyntax.TOKEN);
								setStateHandler(this);
								if (TRACE) TOUT.println("AJSONPlainValue.tryEnter()=true LEAVE");
								return true;
							}else
							{
								unread(c);
								if (TRACE) TOUT.println("AJSONPlainValue.tryEnter()=false LEAVE");
								return false;
							}
						};
						@Override public void toNextChar()throws IOException
						{
							if (TRACE) TOUT.println("AJSONPlainValue.toNextChar() ENTER");
							int r = read();
							if (r==-1)
							{
								if (TRACE) TOUT.println("AJSONPlainValue.toNextChar(), eof LEAVE");
								return;
							}
							char c = (char)r;
							if (isPlainValueChar(c))
							{
								queueNextChar(c,TIntermediateSyntax.TOKEN);
								if (TRACE) TOUT.println("AJSONPlainValue.toNextChar() \'"+c+"\' LEAVE");
							}else
							{
								unread(c);
								setStateHandler(getNextState());
								if (TRACE) TOUT.println("AJSONPlainValue.toNextChar() end of token LEAVE");
							}
						};
				};
				
				/* -----------------------------------------------------------------------
					
				
				
				
						State graph proper.
						
						
						
				
				-----------------------------------------------------------------------*/
				
				/**
					An initial state for JSON stream processing.
					<p>
         			A state responsible for allowing optional ByteOrderMark.
         			<p>
         			Moves to {@link #JSON_BODY_LOOKUP}.
         		*/
				private final IStateHandler JSON_START = new  AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "JSON_START";}
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("JSON_START.toNextChar() ENTER");
						if (!BOM.tryEnter())
						{
							//in this case move to next sate unconditionally.							
							setStateHandler(JSON_BODY_LOOKUP);
						}
						if (TRACE) TOUT.println("JSON_START.toNextChar() LEAVE");
					}
				};
				
				
				/**
					A ByteOrderMark lookup state.
					<p>
					Moves to {@link #JSON_BODY_LOOKUP}
				*/
				private final  ISyntaxHandler BOM = new  ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "BOM";}
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("BOM.tryEnter() ENTER");
						int r = tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("BOM.tryEnter()=false, eof LEAVE");
							return false;
						}
						char c = (char)r;
						if (0xFEFF == c)
						{
							setStateHandler(JSON_BODY_LOOKUP);
							if (TRACE) TOUT.println("BOM.tryEnter()=true, LEAVE");
							return true;
						}else
						{
							unread(c);
							if (TRACE) TOUT.println("BOM.tryEnter()=false, LEAVE");
							return false;
						}
					};
					@Override public  void toNextChar()throws IOException{ throw new AssertionError(); };
				};
				
				
				/**
         			A state responsible for looking up for JSON body which is a start of a JSON array
         			preceeded, eventually, by whitespaces.
         			<p>
         			Moves to either {@link #WHITESPACE} or {@link #JSON_ARRAY_LOOKUP}
         		*/
				private final IStateHandler JSON_BODY_LOOKUP = new  AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "JSON_BODY_LOOKUP";}
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("JSON_BODY_LOOKUP.toNextChar() ENTER");
						if (!WHITESPACE.tryEnter())
						if (!JSON_ARRAY_LOOKUP.tryEnter())
						{
							int r= read();
							if (r==-1)
							{
								if (TRACE) TOUT.println("JSON_BODY_LOOKUP.toNextChar() eof, LEAVE");
								return;
							}
							throw new EBrokenFormat("JSON stream must be an array [...], but \'"+((char)r)+"\' was found :"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("JSON_BODY_LOOKUP.toNextChar() LEAVE");
					}
				};
				
				/** A state responsible for recognition and search for beginning of JSON array
				<p>
				Moves to {@link #JSON_ARRAY_BODY}*/
				private final ISyntaxHandler JSON_ARRAY_LOOKUP = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "JSON_ARRAY_LOOKUP";}
					@Override public boolean tryEnter()throws IOException					
					{
						if (TRACE) TOUT.println("JSON_ARRAY_LOOKUP.tryEnter() ENTER");
						int r= tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("JSON_ARRAY_LOOKUP.tryEnter()=false, eof LEAVE");
							return false;
						}
						char c = (char)r;
						if (c=='[')
						{
							//Now we are in JSON body.
							setStateHandler(JSON_ARRAY_BODY);
							queueNextChar(c,TIntermediateSyntax.VOID);//we need to emit some char since
																	  //otherwise openImpl won't catch this.
							if (TRACE) TOUT.println("JSON_ARRAY_LOOKUP.tryEnter()=true LEAVE");
							return true;
						}else
						{
							unread(c);
							if (TRACE) TOUT.println("JSON_ARRAY_LOOKUP.tryEnter()=false LEAVE");
							return false;
						}
					};
					@Override public void toNextChar()throws IOException{ throw new AssertionError(); };
				};
					
				/** 
					A state responsible for processing first and subsequent elements of JSON array,
					basically recognizes end of array representing part of end-signal, 
					plain value, string or object representing a begin signal of sub-structure.
				*/
				private final  IStateHandler JSON_ARRAY_BODY = new  AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "JSON_ARRAY_BODY";}
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("JSON_ARRAY_BODY.toNextChar() ENTER");
						if (!WHITESPACE.tryEnter())
						if (!END_OF_JSON_ARRAY.tryEnter())
						if (!JSON_PLAIN_VALUE.tryEnter())
						if (!JSON_STRING_VALUE.tryEnter())
						if (!JSON_OBJECT.tryEnter())
						{
							int r= read();
							if (r==-1)
							{
								if (TRACE) TOUT.println("JSON_ARRAY_BODY.toNextChar(), eof LEAVE");
								return;
							}
							throw new EBrokenFormat("Expected [, comma, space, numeric or logic value or string but \'"+((char)r)+"\' was found :"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("JSON_ARRAY_BODY.toNextChar() LEAVE");
					};
				};
				/** A state recognizing a begin of JSON object and thous a begin signal */
				private final ISyntaxHandler JSON_OBJECT = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "JSON_OBJECT";}
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("JSON_OBJECT.tryEnter() ENTER");
						int r = tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("JSON_OBJECT.tryEnter()=false, eof LEAVE");
							return false;
						}
						char c = (char)r;
						if (c=='{')
						{
							setStateHandler(this);
							queueNextChar(c, TIntermediateSyntax.SIG_BEGIN);
							if (TRACE) TOUT.println("JSON_OBJECT.tryEnter()=true, LEAVE");
							return true;
						}else
						{
							unread(c);
							if (TRACE) TOUT.println("JSON_OBJECT.tryEnter()=false, LEAVE");
							return false;
						}
					}
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("JSON_OBJECT.toNextChar() ENTER");
						//A whitespace in this area cannot produce SEPARATOR
						//because it will generate nameless signal.
						if (!VOIDWHITESPACE.tryEnter())
						if (!BEGIN_NAME.tryEnter())
						{
							int r= read();
							if (r==-1)
							{
								if (TRACE) TOUT.println("JSON_OBJECT.toNextChar(), eof LEAVE");
								return;
							}
							throw new EBrokenFormat("JSON object must have a single named field indicating the name of begin signal :"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("JSON_OBJECT.toNextChar() LEAVE");
					};
				};
				/** A begin signal name handler */
				private final ISyntaxHandler BEGIN_NAME = new AJSONString(
															 this,//ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
															 TIntermediateSyntax.SIG_NAME_VOID,// ATxtReadFormat1.TIntermediateSyntax report_void_as,
															 TIntermediateSyntax.SIG_NAME //ATxtReadFormat1.TIntermediateSyntax report_character_as
															 )
				{
					@Override public String getName(){ return "BEGIN_NAME";}
					@Override protected IStateHandler getNextState(){ return NAME_SEPARATOR_LOOKUP; };		
				};
				
				/** A state looking for : separating signal name from possibly single
				element structure
				*/
				private final IStateHandler NAME_SEPARATOR_LOOKUP = new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "NAME_SEPARATOR_LOOKUP";}
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("NAME_SEPARATOR_LOOKUP.toNextChar() ENTER");
						if (!WHITESPACE.tryEnter())
						if (!NAME_SEPARATOR.tryEnter())
						{
							//Now we have a possiblity that it can be a single element
							int r= read();
							if (r==-1)
							{
								if (TRACE) TOUT.println("NAME_SEPARATOR_LOOKUP.toNextChar() eof, LEAVE");
								return;
							}
							throw new EBrokenFormat("JSON object must have a single named field indicating the name of begin signal :"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("NAME_SEPARATOR_LOOKUP.toNextChar() LEAVE");
					};
				};
				/** A syntax responsible for capturing : separating signal name from structure body.
				*/
				private final ISyntaxHandler NAME_SEPARATOR = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "NAME_SEPARATOR";}
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("NAME_SEPARATOR.tryEnter() ENTER");
						int r = tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("NAME_SEPARATOR.tryEnter()=false, eof LEAVE");
							return false;
						}
						char c = (char)r;
						if (c==':')
						{
							setStateHandler(STRUCT_BODY_LOOKUP);
							if (TRACE) TOUT.println("NAME_SEPARATOR.tryEnter()=true, LEAVE");
							return true;
						}else
						{
							unread(c);
							if (TRACE) TOUT.println("NAME_SEPARATOR.tryEnter()=false, LEAVE");
							return false;
						}
					};
					@Override public void toNextChar()throws IOException{ throw new AssertionError(); };
				};
				
				/** A state looking for either [ opening structure body or for a single element body struct.
				*/
				private final IStateHandler STRUCT_BODY_LOOKUP = new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "STRUCT_BODY_LOOKUP";}
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("STRUCT_BODY_LOOKUP.toNextChar() ENTER");
						if (!WHITESPACE.tryEnter())
						if (!JSON_ARRAY_LOOKUP.tryEnter())	//search this first, or tokens may catch up.
						if (!SINGLE_ELEMENT_STRUCT_PLAIN_VALUE.tryEnter())
						if (!SINGLE_ELEMENT_STRUCT_STRING_VALUE.tryEnter())
						{
							int r= read();
							if (r==-1)
							{
								if (TRACE) TOUT.println("STRUCT_BODY_LOOKUP.toNextChar() eof LEAVE");
								return;
							}
							throw new EBrokenFormat("JSON object must have a primitive value or array :"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("STRUCT_BODY_LOOKUP.toNextChar() LEAVE");
					};
				};
				
				/** 
					A state responsible for parsing a "plain token" of single element structure, that is any
					token which is not JSON string. 
					<p>
					Moves to {@link #JSON_AFTER_ARRAY}.
				*/
				private final ISyntaxHandler SINGLE_ELEMENT_STRUCT_PLAIN_VALUE = new  AJSONPlainValue(this)
				{
						@Override public String getName(){ return "SINGLE_ELEMENT_STRUCT_PLAIN_VALUE";}
						@Override protected IStateHandler getNextState(){ return JSON_AFTER_ARRAY; };
				};
				
				/** 
					A state responsible for parsing a "string token" of single element structure.
					<p>
					Parser will allow here not only single element primitive char, but, mainly because
					it is easier to do, also variable length string.
					<p>
					Moves to {@link #JSON_AFTER_ARRAY}.
				*/
				private final ISyntaxHandler SINGLE_ELEMENT_STRUCT_STRING_VALUE = new  AJSONString(
								 							this,//ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
								 							TIntermediateSyntax.TOKEN_VOID,//ATxtReadFormat1.TIntermediateSyntax report_void_as,
								 							TIntermediateSyntax.TOKEN //ATxtReadFormat1.TIntermediateSyntax report_character_as
								 							)
				{
						@Override public String getName(){ return "SINGLE_ELEMENT_STRUCT_STRING_VALUE";}
						@Override protected IStateHandler getNextState(){ return JSON_AFTER_ARRAY; };
				};
				
				
				
				
				/** A state responsible for recognition and processing of ] json array,
				including end of JSON file body.
				<p>
				 Moves to {@link #AFTER_JSON_BODY} if recursion depth is zero or
				 to {@link #JSON_AFTER_ARRAY} otherwise.
				 */
				private final ISyntaxHandler END_OF_JSON_ARRAY = new  ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "END_OF_JSON_ARRAY";}
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("END_OF_JSON_ARRAY.tryEnter() ENTER");
						int r= tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("END_OF_JSON_ARRAY.tryEnter()=false, eof LEAVE");
							return false;
						}
						char c = (char)r;
						if (c==']')
						{
							//check if we are finished JSON body processing?
							if (getCurrentStructRecursionDepth()==0)
									setStateHandler(AFTER_JSON_BODY);
								else
									setStateHandler(JSON_AFTER_ARRAY);
							if (TRACE) TOUT.println("END_OF_JSON_ARRAY.tryEnter()=true LEAVE");
							return true;
						}else
						{
							unread(c);
							if (TRACE) TOUT.println("END_OF_JSON_ARRAY.tryEnter()=false  LEAVE");
							return false;
						}
					};
					@Override public void toNextChar()throws IOException{ throw new AssertionError(); };
				};
				
				/** 
					A state responsible for parsing a "plain token" array element, that is any
					token which is not JSON string. 
					<p>
					Moves to {@link #JSON_ARRAY_NEXT_LOOKUP}.
				*/
				private final ISyntaxHandler JSON_PLAIN_VALUE = new  AJSONPlainValue(this)
				{
						@Override public String getName(){ return "JSON_PLAIN_VALUE";}
						@Override protected IStateHandler getNextState(){ return JSON_ARRAY_NEXT_LOOKUP; };
				};
				/** 
					A state responsible for parsing a "string token" array element.
					<p>
					Moves to {@link #JSON_ARRAY_NEXT_LOOKUP}.
				*/
				private final ISyntaxHandler JSON_STRING_VALUE = new  AJSONString(
								 							this,//ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
								 							TIntermediateSyntax.TOKEN_VOID,//ATxtReadFormat1.TIntermediateSyntax report_void_as,
								 							TIntermediateSyntax.TOKEN //ATxtReadFormat1.TIntermediateSyntax report_character_as
								 							)
				{
						@Override public String getName(){ return "JSON_STRING_VALUE";}
						@Override protected IStateHandler getNextState(){ return JSON_ARRAY_NEXT_LOOKUP; };
				};
				
				
				/** A state responsible for looking up of element sepearator in JSON array,
				end of array or a whitespace.
				<p>
				Moves to {@link #JSON_ARRAY_BODY} or {@link #END_OF_JSON_ARRAY}.
				*/
				private final IStateHandler JSON_ARRAY_NEXT_LOOKUP = new  AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "JSON_ARRAY_NEXT_LOOKUP";}
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("JSON_ARRAY_NEXT_LOOKUP.toNextChar() ENTER");
						if (!WHITESPACE.tryEnter())
						if (!END_OF_JSON_ARRAY.tryEnter())
						{
							int r= read();
							if (r==-1) return;
							char c = (char)r;
							if (c==',')
							{
								queueNextChar(c,TIntermediateSyntax.SEPARATOR);
								setStateHandler(JSON_ARRAY_BODY);
								if (TRACE) TOUT.println("JSON_ARRAY_NEXT_LOOKUP.toNextChar(), found separator, LEAVE");
								return;
							}else
								throw new EBrokenFormat("Expected ] or comma but \'"+((char)r)+"\' was found :"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("JSON_ARRAY_NEXT_LOOKUP.toNextChar() LEAVE");
					};
				};
				
				
				/** A state responsible for tracking what should be after ] or after a single element
				strcucture. Basically } is expected because of how we defined the valid structure
				of JSON file.
				*/
				private final IStateHandler JSON_AFTER_ARRAY = new  AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "JSON_AFTER_ARRAY";}
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("JSON_AFTER_ARRAY.toNextChar() ENTER");
						/* Note:
								Full JSON syntax do allow array within an array or array after an array.
								In our context however only  ]} is possible, because we use JSON array
								_only_ to enclose the body of a structure
						*/
						if (!WHITESPACE.tryEnter())	//a white space
						if (!JSON_OBJ_END.tryEnter()) //a } indicating that this is an end of a struture.
						{
							int r= read();
							if (r==-1)
							{
								if (TRACE) TOUT.println("JSON_AFTER_ARRAY.toNextChar(), eof LEAVE");
								return;
							}
							throw new EBrokenFormat("Expected } \'"+((char)r)+"\' was found :"+getLineInfoMessage());
						}
						if (TRACE) TOUT.println("JSON_AFTER_ARRAY.toNextChar() LEAVE");
					}
				};
				
				/** A state responsible for recognizing } end singnal
				<p>
				Moves to {@link #JSON_ARRAY_NEXT_LOOKUP}
				*/
				private final ISyntaxHandler JSON_OBJ_END = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "JSON_OBJ_END";}
					@Override public boolean tryEnter()throws IOException
					{
						if (TRACE) TOUT.println("JSON_OBJ_END.tryEnter() ENTER");
						int r = tryRead();
						if (r==-1)
						{
							if (TRACE) TOUT.println("JSON_OBJ_END.tryEnter()=false, eof LEAVE");
							return false;
						}
						char c = (char)r;
						if (c=='}')
						{
							queueNextChar(c,TIntermediateSyntax.SIG_END);
							setStateHandler(JSON_ARRAY_NEXT_LOOKUP);
							if (TRACE) TOUT.println("JSON_OBJ_END.tryEnter()=true LEAVE");
							return true;
						}else
						{
							unread(c);
							if (TRACE) TOUT.println("JSON_OBJ_END.tryEnter()=false LEAVE");
							return false;
						}
					}
					@Override public void toNextChar()throws IOException{ throw new AssertionError(); };
				};
				
				
				
				/**
					A whitespace skipper between JSON elements.
					This is an overlay state.
					<p>
					Returns to previous state at first non-whitespace character.
					@see #isJSONWhitespace
				*/
				private final ISyntaxHandler WHITESPACE = new CWhitespace(this,TIntermediateSyntax.SEPARATOR)
				{
					@Override public String getName(){ return "WHITESPACE";}
				};
				
				/**
					A whitespace skipper between JSON elements which are not a part of upstream syntax.
					This is an overlay state.
					<p>
					Returns to previous state at first non-whitespace character.
					@see #isJSONWhitespace
				*/
				private final ISyntaxHandler VOIDWHITESPACE = new CWhitespace(this,TIntermediateSyntax.VOID)
				{
					@Override public String getName(){ return "WHITESPACE";}
				};
				
				/**
					A state handler whis always reports end-of-file
					because reading past end of JSON body is not allowed.
				*/
				private final IStateHandler AFTER_JSON_BODY = new  AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public String getName(){ return "AFTER_JSON_BODY";}
					@Override public void toNextChar()throws IOException
					{
						if (TRACE) TOUT.println("AFTER_JSON_BODY.toNextChar()");
					   //Persisten "no more data" regardless of what is in stream.
				   	   // Notice we have two choices:
				   	   //		throw or queue -1.
				   	   //		The contract of ATxtReadFormat1.toNextChar forbids 
				   	   //		us to throw if we encounter	end of file.
				   	   queueNextChar(-1,null);
					}
				};
				
				
	/* ************************************************************************
	
	
			Construction
	
	
	
	*************************************************************************/
	public CJSONReadFormat(Reader in)
	{
		super(in, //Reader in,
				0,//int name_registry_capacity,
				64//int token_size_limit
		   );
	};
	/* ************************************************************************
	
				JSON syntax support
	
	
	*************************************************************************/
	/** Tests if character is JSON white space 
	@param c char to test
	@return true if it is a white-space according to JSON specs.
	*/
	protected static final boolean isJSONWhitespace(char c)
	{
		switch(c)
		{
			case (char)9:
			case (char)0xA:
			case (char)0xD:
			case (char)0x20:
						return true;
		};
		return false;
	};
	/* ************************************************************************
	
				AStructFormatBase
	
	
	*************************************************************************/
	/** Overriden to consume opening sequence */
	@Override public void openImpl()throws IOException 
	{
		if (TRACE) TOUT.println("openImpl ENTER");
		setStateHandler(JSON_START);
		//Now we need to trigger any file action which will
		//automatically consume the states which are necessary
		//to move to actual content production. Those will
		//fail if we are not a valid JSON.
		//We are however still not opened so we can't just ask
		//the high level routines.
		//We should however enforce going down to JSON array body.
		while(getStateHandler()!=JSON_ARRAY_BODY)
		{
			if (TRACE) TOUT.println("openImpl() state is "+getStateHandler().getName());
			toNextChar();
			if (getNextSyntaxElement()==null) throw new EUnexpectedEof("While looking for [ opening JSON array.");
		};
		if (TRACE) TOUT.println("openImpl LEAVE");
	}
	/** Overriden to close input */
	@Override public void closeImpl()throws IOException
	{
		super.closeImpl();
		in.close();
	}
	/* ************************************************************************
	
				AFormatLimits
	
	
	*************************************************************************/
	/** Returns Integer.MAX_VALUE */
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	/** Unlimited
	@return -1*/
	@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
	
};