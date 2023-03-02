package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.*;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Reader;

/**
	A reference plain text format implementation, reading side.
	<p>
	This is also a code template which shows the easiest and cleaniest 
	path to parsing text files into format readers with the help of
	"state-graph" concept presented in {@link ATxtReadFormatStateBase0}
	and extended in {@link ATxtReadFormatStateBase1}.
*/
public class CPlainTxtReadFormat extends ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax>
{
						/** Debug logging level */
						private static final long TLEVEL = SLogging.getDebugLevelForClass(CPlainTxtReadFormat.class);
						private static final boolean TRACE = (TLEVEL!=0);
						private static final boolean DUMP = (TLEVEL>=2);
						private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CPlainTxtReadFormat.",CPlainTxtReadFormat.class) : null;

		  	/* ***********************************************************************************************************
		  	
		  	
		  	
		  	
		  			Tooling related to state graph.
		  	
		  	
		  	
		  	
		  	
		  	************************************************************************************************************/
		  	
			/** 
				Base for plain and string token handlers.
				<p>
				This base keeps information about how to report characters and
				how to transit to next state.
			*/
			private static abstract class ATokenHandler extends AToNextSyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
			{
							/** How to report character which is a part of a token */
							protected final ATxtReadFormat1.TIntermediateSyntax syntax_for_character;
							/** How to report character which belongs to a token, but is not a part of its body */
							protected final ATxtReadFormat1.TIntermediateSyntax syntax_for_void;
							
				/* *****************************************************************************
				
							Construction
				
				******************************************************************************/
				/**
					Creates.
					@param parser parser, non null
					@param syntax_for_character syntax to report for characters in token, non null
					@param syntax_for_void syntax to report for void characters in token, non null
				*/
				protected ATokenHandler(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
									  ATxtReadFormat1.TIntermediateSyntax syntax_for_character,
									  ATxtReadFormat1.TIntermediateSyntax syntax_for_void
									  )
				{
					super(parser);
					assert(syntax_for_character!=null);
					assert(syntax_for_void!=null);
					this.syntax_for_character=syntax_for_character;
					this.syntax_for_void=syntax_for_void;
				}
			};
			
			
			
			
			/** 
				A plain token handler.
				<p>
				This class is responsible for detecting a plain token or a plain begin name
				and process it. It does not handle empty tokens and their detection
				depends on the context.
			*/
			private static abstract class APlainTokenHandler extends ATokenHandler
			{
				/* *****************************************************************************
				
							Construction
				
				******************************************************************************/
				/**
					Creates
					@param parser parser
					@param syntax_for_character syntax to report for characters in token
					@param syntax_for_void syntax to report for void characters in token
				*/
				protected APlainTokenHandler(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
									  ATxtReadFormat1.TIntermediateSyntax syntax_for_character,
									  ATxtReadFormat1.TIntermediateSyntax syntax_for_void
									  )
				{
					super(parser, syntax_for_character, syntax_for_void);
				}
				/* *****************************************************************************
				
							ASyntaxHandler
				
				******************************************************************************/
				@Override public boolean tryEnter()throws IOException
				{
					if (TRACE) TOUT.println("APlainTokenHandler.tryEnter() ENTER");
					int r = tryRead();
					if (r==-1)
					{
						if (TRACE) TOUT.println("APlainTokenHandler.tryEnter()=false, eof LEAVE");
						return false;
					};
					char c = (char)r;
					if (TRACE) TOUT.println("APlainTokenHandler.tryEnter() checking \'"+c+"\'");
					if (isTokenBodyChar(c))
					{
						//Non empty token						
						enterStateHandler();
						queueNextChar(c,syntax_for_character);
						if (TRACE) TOUT.println("APlainTokenHandler.tryEnter()=true, LEAVE");
						return true;
					}else
					{
						//Note: There is no possibility for looking for empty token
						//		at this stage because there is a different way 
						//		of saying "empty name" and "empty token" and we can't do
						//		it out of context.
						unread(c);
						if (TRACE) TOUT.println("APlainTokenHandler.tryEnter()=false, LEAVE");
						return false;
					}
				};
				@Override public void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("APlainTokenHandler.toNextChar() ENTER");
					int r = read();
					if (r==-1) return;
					char c = (char)r;
					if (isTokenBodyChar(c))
					{
						if (DUMP) TOUT.println("APlainTokenHandler.toNextChar(), consuming \'"+c+"\' LEAVE");
						queueNextChar(c,syntax_for_character);
					}else
					{
						unread(c);
						leaveStateHandler();
						if (TRACE) TOUT.println("APlainTokenHandler.toNextChar(), finished due to  \'"+c+"\' LEAVE");
					};
				};
			}
			
			
			
			
			/** 
				A string token handler.
				<p>
				This class is responsible for detecting a string token or a quoted begin name
				and process it. It does handle empty tokens. It does un-escaping of escape squences.
			*/
			private abstract static class AStringTokenHandler extends ATokenHandler
			{
							/** A hex-escape sequence handler */
							private final ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax> HEX_ESCAPE_SEQUENCE = 
								new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(parser)
								{
												/** Counts digits */
												int count = 0;
												/** Preserves un-escaped value */
												char unescaped = 0;
									/* **********************************************************
												AStateHandler
									***********************************************************/
									@Override public void onEnter(){ count=0; unescaped=0; };
									/* **********************************************************
												ASyntaxHandler
									***********************************************************/
									@Override public boolean tryEnter()throws IOException
									{
										if (TRACE) TOUT.println("AStringTokenHandler.HEX_ESCAPE_SEQUENCE.tryEnter() ENTER");
										int r = tryRead();
										if (r==-1)
										{
											if (TRACE) TOUT.println("AStringTokenHandler.HEX_ESCAPE_SEQUENCE.tryEnter()=false, eof LEAVE");
											return false;
										};
										char c = (char)r;
										if (TRACE) TOUT.println("AStringTokenHandler.HEX_ESCAPE_SEQUENCE.tryEnter() checking \'"+c+"\'");
										if (c==CPlainTxtWriteFormat.ESCAPE_CHAR)
										{
											pushStateHandler(this);
											if (TRACE) TOUT.println("AStringTokenHandler.HEX_ESCAPE_SEQUENCE.tryEnter()=true LEAVE");
											return true;
										}else
										{
											unread(c);
											if (TRACE) TOUT.println("AStringTokenHandler.HEX_ESCAPE_SEQUENCE.tryEnter()=false LEAVE");
											return false;
										}
									};
									@Override public void toNextChar()throws IOException
									{
										if (TRACE) TOUT.println("AStringTokenHandler.HEX_ESCAPE_SEQUENCE.toNextChar() ENTER");
										char c = readAlways();
										if (TRACE) TOUT.println("AStringTokenHandler.HEX_ESCAPE_SEQUENCE.toNextChar() unescaping \'"+c+"\'");
										if (c==';')
										{
											queueNextChar(unescaped,syntax_for_character);
											popStateHandler();
											if (TRACE) TOUT.println("AStringTokenHandler.HEX_ESCAPE_SEQUENCE.toNextChar(), unescaped LEAVE");
										}else
										if (count<4)
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
											unescaped<<=4;
											unescaped|=nibble;
											count++;
										}else
											throw new EBrokenFormat("Too long escape"+getLineInfoMessage());
									};
								};
								
							/** A \\ escape handler */
							private final ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax> SELF_ESCAPE_SEQUENCE = 
								new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(parser)
								{
									/* **********************************************************
												ASyntaxHandler
									***********************************************************/
									@Override public boolean tryEnter()throws IOException
									{
										if (TRACE) TOUT.println("AStringTokenHandler.SELF_ESCAPE_SEQUENCE.tryEnter() ENTER");
										if (tryLooksAt("\\\\"))
										{
											if (TRACE) TOUT.println("AStringTokenHandler.SELF_ESCAPE_SEQUENCE.tryEnter()=false collected=\""+collected+"\", LEAVE");
											queueNextChar('\\',syntax_for_character);
											return true;
										}else
										{
											if (TRACE) TOUT.println("AStringTokenHandler.SELF_ESCAPE_SEQUENCE.tryEnter()=false collected=\""+collected+"\", LEAVE");
											unread();
											return false;
										}
									};
									@Override public void toNextChar()throws IOException
									{
										throw new AssertionError();
									};
								};
							/** A \" 
							escape handler */
							private final ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax> QUOTE_ESCAPE_SEQUENCE = 
								new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(parser)
								{
									/* **********************************************************
												ASyntaxHandler
									***********************************************************/
									@Override public boolean tryEnter()throws IOException
									{
										if (TRACE) TOUT.println("AStringTokenHandler.QUOTE_ESCAPE_SEQUENCE.tryEnter() ENTER");
										if (tryLooksAt("\\\""))
										{
											if (TRACE) TOUT.println("AStringTokenHandler.QUOTE_ESCAPE_SEQUENCE.tryEnter()=false collected=\""+collected+"\", LEAVE");
											queueNextChar('\"',syntax_for_character);
											return true;
										}else
										{
											if (TRACE) TOUT.println("AStringTokenHandler.QUOTE_ESCAPE_SEQUENCE.tryEnter()=false collected=\""+collected+"\", LEAVE");
											unread();
											return false;
										}
									};
									@Override public void toNextChar()throws IOException
									{
										throw new AssertionError();
									};
								};
								
				/* *****************************************************************************
				
							Construction
				
				******************************************************************************/
				/**
					Creates
					@param parser parser
					@param syntax_for_character syntax to report for characters in token
					@param syntax_for_void syntax to report for void characters in token
				*/
				AStringTokenHandler(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> parser,
									  ATxtReadFormat1.TIntermediateSyntax syntax_for_character,
									  ATxtReadFormat1.TIntermediateSyntax syntax_for_void
									  )
				{
					super(parser, syntax_for_character, syntax_for_void);
				}
				/* *****************************************************************************
				
							ASyntaxHandler
				
				******************************************************************************/
				@Override public boolean tryEnter()throws IOException
				{
					if (TRACE) TOUT.println("AStringTokenHandler.tryEnter() ENTER");
					int r = tryRead();
					if (r==-1)
					{
						if (TRACE) TOUT.println("AStringTokenHandler.tryEnter()=false, eof LEAVE");
						return false;
					};
					char c = (char)r;
					if (TRACE) TOUT.println("AStringTokenHandler.tryEnter() checking \'"+c+"\'");
					if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
					{
						//Non empty token
						enterStateHandler();
						queueNextChar(c,syntax_for_void); //to indicate, that there is at least an empty token.
						if (TRACE) TOUT.println("AStringTokenHandler.tryEnter()=true, LEAVE");
						return true;
					}else
					{
						unread(c);
						if (TRACE) TOUT.println("AStringTokenHandler.tryEnter()=false, LEAVE");
						return false;
					}
				};
				@Override public void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("AStringTokenHandler.toNextChar() ENTER");
					//First process escapes, because they do need full data
					//and un-read correctly.
					if (!SELF_ESCAPE_SEQUENCE.tryEnter()) //this must be checked first!
					if (!QUOTE_ESCAPE_SEQUENCE.tryEnter())
					if (!HEX_ESCAPE_SEQUENCE.tryEnter()) //this must be checked last.
					{
						//now check termination.
						int r = read();
						if (r==-1)
						{
							if (TRACE) TOUT.println("AStringTokenHandler.toNextChar(), eof LEAVE");
							return;
						};
						char c = (char)r;
						if (TRACE) TOUT.println("AStringTokenHandler.toNextChar() checking \'"+c+"\'");
						if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
						{
							if (TRACE) TOUT.println("AStringTokenHandler.toNextChar(), finished, LEAVE");
							//we do consume token separator so no unreading.
							leaveStateHandler();
							return;
						}else
						{
							if (TRACE) TOUT.println("AStringTokenHandler.toNextChar(), \'"+c+"\', LEAVE");
							queueNextChar(c,syntax_for_character);
							return;
						}
					}
					if (TRACE) TOUT.println("AStringTokenHandler.toNextChar(), unescaped, LEAVE");
				};
			}
			
			
			/* *****************************************************************************
				
			
			
			
			
							A state graph.
				
							
						Each state in a graph is represented by a field to
						which an apropriate handler is assigned.
						
						States names are CAPITALIZAED.
							
							
							
							
		     ******************************************************************************/
			
			
			/** A state which looks up for struct or file body content
				which may be plain token, string token, comment, begin, end, white-space or end-of-file.
				<p>
				This state is an initial state for a file processing, initial state after end or begin
				signal is processed and a state after the token separator is processed.
			*/
			private final IStateHandler STRUCT_BODY = new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
			{
				@Override public void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("STRUCT_BODY.toNextChar() ENTER");
					//Consider what is next?
					if (!PLAIN_TOKEN.tryEnter())
					if (!STRING_TOKEN.tryEnter())
					if (!COMMENT.tryEnter())
					if (!BEGIN.tryEnter())
					if (!END.tryEnter())
					if (!WHITESPACE.tryEnter())
					{
						//Now we check why? 
						int r = read();
						if (r!=-1) throw new EBrokenFormat("Invalid syntax \'"+(char)r+"\':"+getLineInfoMessage());
					}
					if (TRACE) TOUT.println("STRUCT_BODY.toNextChar() LEAVE");
				};
				@Override public String getName(){ return "STRUCT_BODY"; };
			};
			
			/** A white-space skipper. Returns to parent state on first non-white space character.
			All white spaces are reported as {@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR}
			*/
			private final ISyntaxHandler WHITESPACE =  new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
			{
							/** Counts whitespaces if {@link #continous_whitespace_limit}
							is enabled */
							private int count;
				@Override public void onActivated()
				{
					this.count = 0;
				}
				@Override public boolean tryEnter()throws IOException
				{
					if (TRACE) TOUT.println("WHITESPACE.tryEnter() ENTER");
					int r = tryRead();
					if (r==-1)
					{
						if (TRACE) TOUT.println("WHITESPACE.tryEnter()=false, eof LEAVE");
						return false;
					};
					char c = (char)r;
					if (TRACE) TOUT.println("WHITESPACE.tryEnter() checking \'"+c+"\'");
					if (isEmptyChar(c))
					{
						//we are at space, skipp all spaces
						pushStateHandler(this);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
						if (TRACE) TOUT.println("WHITESPACE.tryEnter()=true, LEAVE");
						return true;
					}else
					{
						unread(c);
						if (TRACE) TOUT.println("WHITESPACE.tryEnter()=false, LEAVE");
						return false;
					}
				};
				@Override public void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("WHITESPACE.toNextChar() ENTER");
					int r = read();
					if (r==-1)
					{
						if (TRACE) TOUT.println("WHITESPACE.toNextChar(), eof LEAVE");
						return;
					};
					char c = (char)r;
					if (isEmptyChar(c))
					{
						//skipping
						if (continous_whitespace_limit!=-1)
						{
							if (count==continous_whitespace_limit)
									throw new EFormatBoundaryExceeded("Continous whitespace stream longer than "+continous_whitespace_limit);
							count++;
						};
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
					{
						//non-space, move to parent state.
						unread(c);
						popStateHandler();
						if (TRACE) TOUT.println("WHITESPACE.toNextChar(), finished, LEAVE");
					};
				};
				@Override public String getName(){ return "WHITESPACE"; };
			};
			
			/** A commend syntax handler. Recognizes # comment opening character.
			Reports # as {@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR},
			and a comment body as {@link ATxtReadFormat1.TIntermediateSyntax#VOID}.
			The trailing EOL, as specified in format definition, does not belong to the comment. 
			*/
			private final ISyntaxHandler COMMENT = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
			{		
							/** Counts whitespaces if {@link #continous_comment_limit}
							is enabled */
							private int count;
				@Override public void onActivated()
				{
					this.count = 0;
				}
				@Override public boolean tryEnter()throws IOException
				{
					if (TRACE) TOUT.println("COMMENT.tryEnter() ENTER");
					int r = tryRead();
					if (r==-1)
					{
						if (TRACE) TOUT.println("COMMENT.tryEnter()=false, eof LEAVE");
						return false;
					};
					char c = (char)r;
					if (TRACE) TOUT.println("COMMENT.tryEnter() checking \'"+c+"\'");
					if (c==CPlainTxtWriteFormat.COMMENT_CHAR)
					{					
						pushStateHandler(this);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
						if (TRACE) TOUT.println("COMMENT.tryEnter()=true, LEAVE");
						return true;
					}else
					{
						unread(c);
						if (TRACE) TOUT.println("COMMENT.tryEnter()=false, LEAVE");
						return false;
					}
				};
				@Override public void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("COMMENT.toNextChar() ENTER");
					int r = read();
					if (r==-1)
					{
						if (TRACE) TOUT.println("COMMENT.toNextChar(), eof LEAVE");
						return;
					};
					char c = (char)r;
					if ((c=='\r')||(c=='\n'))
					{
						//comment terminated. Accodring to specs eol does not belong to comment so:
						unread(c);
						//return to parent state.
						popStateHandler();
						if (TRACE) TOUT.println("COMMENT.toNextChar(), eol, LEAVE");
					}else
					{
						//in comment.
						if (continous_comment_limit!=-1)
						{
							if (count==continous_comment_limit)
									throw new EFormatBoundaryExceeded("Comment line longer than "+continous_comment_limit);
							count++;
						};
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.VOID);
						if (TRACE) TOUT.println("COMMENT.toNextChar(), skipped, LEAVE");
						
					};
				};
				@Override public String getName(){ return "COMMENT"; };
			};
	
	
			/** A "begin" handler. Recognizes begin, that is '*'. Reports it as
			{@link ATxtReadFormat1.TIntermediateSyntax#SIG_BEGIN} and recognizes what kind of signal name it is to collect,
			which can be {@link #PLAIN_BEGIN_NAME} or {@link #QUOTED_BEGIN_NAME}.
			*/
			private final ISyntaxHandler BEGIN = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
			{				
				@Override public boolean tryEnter()throws IOException
				{
					if (TRACE) TOUT.println("BEGIN.tryEnter() ENTER");
					int r = tryRead();
					if (r==-1)
					{
						if (TRACE) TOUT.println("BEGIN.tryEnter()=false, eof LEAVE");
						return false;
					};
					char c = (char)r;
					if (TRACE) TOUT.println("BEGIN.tryEnter() checking \'"+c+"\'");
					if (c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
					{
						pushStateHandler(this);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
						if (TRACE) TOUT.println("BEGIN.tryEnter()=true, LEAVE");
						return true;
					}else
					{
						unread(c);
						if (TRACE) TOUT.println("BEGIN.tryEnter()=false, LEAVE");
						return false;
					}
				};
				@Override public void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("BEGIN.toNextChar() ENTER");
					if (!PLAIN_BEGIN_NAME.tryEnter())
					if (!QUOTED_BEGIN_NAME.tryEnter())
					{
						int r = read();
						if (r!=-1) throw new EBrokenFormat("Invalid begin syntax \'"+(char)r+"\':"+getLineInfoMessage());
					};
				if (TRACE) TOUT.println("BEGIN.toNextChar() LEAVE");
				};
				@Override public String getName(){ return "BEGIN"; };
			};
			
			
			/** Recognizes and collects plain begin name and reports it as
			 {@link ATxtReadFormat1.TIntermediateSyntax#SIG_NAME} or
			 {@link ATxtReadFormat1.TIntermediateSyntax#SIG_NAME_VOID}.
			 <p>
			 Moves to {@link #STRUCT_BODY} handler.
			*/
			private final ISyntaxHandler PLAIN_BEGIN_NAME = new APlainTokenHandler
															(
																this,
																ATxtReadFormat1.TIntermediateSyntax.SIG_NAME,
																ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID
																)
			{
				@Override protected IStateHandler getNextHandler(){ return STRUCT_BODY; };
				@Override public boolean tryEnter()throws IOException
				{
					if (TRACE) TOUT.println("PLAIN_BEGIN_NAME.tryEnter() ENTER");
					if (super.tryEnter())
					{
						if (TRACE) TOUT.println("PLAIN_BEGIN_NAME.tryEnter()=true, super LEAVE");
						return true;
					};
					//we might have to look up for empty name, that is a "begin" follwed by 
					//begin, end, token separator, whitespace comment.
					int r = tryRead();
					if (r==-1)
					{
						if (TRACE) TOUT.println("PLAIN_BEGIN_NAME.tryEnter()=false, eof LEAVE");
						return false;
					};
					char c = (char)r;
					if (TRACE) TOUT.println("PLAIN_BEGIN_NAME.tryEnter() checking \'"+c+"\'");
					if (isEmptyChar(c)
							||
						(c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
							||
						(c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
							||
						(c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
							||
						(c==CPlainTxtWriteFormat.COMMENT_CHAR)
						)
					{
						if (TRACE) TOUT.println("PLAIN_BEGIN_NAME.tryEnter(), detected empty name");
						assert(getNextHandler()!=null);
						unread(c);	//leave it for next state handler!
						leaveStateHandler();
						queueNextChar(c,syntax_for_void);
						if (TRACE) TOUT.println("PLAIN_BEGIN_NAME.tryEnter()=true, LEAVE");
						return true;
					}else
					{
						unread(c);
						if (TRACE) TOUT.println("PLAIN_BEGIN_NAME.tryEnter()=false, LEAVE");
						return false;
					}
				}
				@Override public String getName(){ return "PLAIN_BEGIN_NAME"; };
			};
		
			
			/** Recognizes and collects quoted begin name and reports it as
			 {@link ATxtReadFormat1.TIntermediateSyntax#SIG_NAME} or
			 {@link ATxtReadFormat1.TIntermediateSyntax#SIG_NAME_VOID}.
			 <p>
			 Moves to {@link #STRUCT_BODY} handler.
			*/
			private final ISyntaxHandler QUOTED_BEGIN_NAME = new AStringTokenHandler(
																		this,
																		ATxtReadFormat1.TIntermediateSyntax.SIG_NAME,
																		ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID)
			{
				@Override protected IStateHandler getNextHandler(){ return STRUCT_BODY; };
				@Override public String getName(){ return "QUOTED_BEGIN_NAME"; };
			};
			
			/** Recognizes and collects a plain token.
			Reports it as {@link ATxtReadFormat1.TIntermediateSyntax#TOKEN}
			and {@link ATxtReadFormat1.TIntermediateSyntax#TOKEN_VOID}.
			<p>
			Do recognize an empty token if finds separator.
			<p>
			Should be entered from {@link #STRUCT_BODY} and moves to {@link #NEXT_TOKEN}.
			*/
			private final ISyntaxHandler PLAIN_TOKEN = new APlainTokenHandler(
																		this,
																		ATxtReadFormat1.TIntermediateSyntax.TOKEN,
																		ATxtReadFormat1.TIntermediateSyntax.TOKEN_VOID
																		)
			{
				@Override protected IStateHandler getNextHandler(){ return NEXT_TOKEN; };
				@Override public boolean tryEnter()throws IOException
				{
					if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter() ENTER");
					if (super.tryEnter())
					{
						if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter()=true, super LEAVE");
						return true;
					};
					//We might also accept empty token if we will find ,
					int r = tryRead();
					if (r==-1)
					{
						if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter()=false, eof LEAVE");
						return false;
					};
					char c = (char)r;
					if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter() checking \'"+c+"\'");
					if (c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
					{
						if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter(), detected empty token");
						assert(getNextHandler()!=null);
						//but leave character for next state. 
						unread(c);
						leaveStateHandler();
						queueNextChar(c,syntax_for_void);
						if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter()=true, LEAVE");
						return true;
					}else
					{
						unread(c);
						if (TRACE) TOUT.println("PLAIN_TOKEN.tryEnter()=false, LEAVE");
						return false;
					}
				}
				@Override public String getName(){ return "PLAIN_TOKEN"; };
			};
			
			/** Recognizes and collects quoted string token.
			Reports it as {@link ATxtReadFormat1.TIntermediateSyntax#TOKEN}
			and {@link ATxtReadFormat1.TIntermediateSyntax#TOKEN_VOID}.
			<p>
			Do recognize an empty token if finds separator.
			<p>
			Should be entered from {@link #STRUCT_BODY} and moves to {@link #NEXT_TOKEN}.
			*/
			private final ISyntaxHandler STRING_TOKEN = new AStringTokenHandler(
																		this,
																		ATxtReadFormat1.TIntermediateSyntax.TOKEN,
																		ATxtReadFormat1.TIntermediateSyntax.TOKEN_VOID
																		)
			{
				@Override protected IStateHandler getNextHandler(){ return NEXT_TOKEN; };
				@Override public String getName(){ return "STRING_TOKEN"; };
			};
			
			/**
				This state is entered after a token is consumend and is looking for 
				a next element which may be a token separator, begin, end, commend or eof.
			*/
			private final IStateHandler NEXT_TOKEN = new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
			{
				@Override public void toNextChar()throws IOException
				{
					if (TRACE) TOUT.println("NEXT_TOKEN.toNextChar() ENTER");
					//Consider what is next?
					if (!COMMENT.tryEnter())
					if (!BEGIN.tryEnter())
					if (!END.tryEnter())
					if (!WHITESPACE.tryEnter())
					if (!TOKEN_SEPARATOR.tryEnter())
					{
						//Now we 
						int r = read();
						if (r!=-1) throw new EBrokenFormat("Invalid syntax \'"+(char)r+"':"+getLineInfoMessage());
					}
					if (TRACE) TOUT.println("NEXT_TOKEN.toNextChar() LEAVE");
				};
				@Override public String getName(){ return "NEXT_TOKEN"; };
			};
			/**
				A state recognizing and consuming a token separator.
				Reports it as {@link ATxtReadFormat1.TIntermediateSyntax#SEPARATOR}.
				Transits to {@link #STRUCT_BODY}.
			*/
			private final ISyntaxHandler TOKEN_SEPARATOR = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
			{
				@Override public boolean tryEnter()throws IOException
				{
					if (TRACE) TOUT.println("TOKEN_SEPARATOR.tryEnter() ENTER");
					int r = tryRead();
					if (r==-1)
					{
						if (TRACE) TOUT.println("TOKEN_SEPARATOR.tryEnter()=false, eof LEAVE");
						return false;
					};
					char c = (char)r;
					if (TRACE) TOUT.println("TOKEN_SEPARATOR.tryEnter() checking \'"+c+"\'");
					if (c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
					{
						//Found it, move to target state
						if (TRACE) TOUT.println("TOKEN_SEPARATOR->STRUCT_BODY");
						setStateHandler(STRUCT_BODY);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
						if (TRACE) TOUT.println("TOKEN_SEPARATOR.tryEnter()=true, LEAVE");
						return true;
					}else
					{
						if (TRACE) TOUT.println("TOKEN_SEPARATOR.tryEnter()=false, LEAVE");
						unread(c);
						return false;
					}
				};
				@Override public void toNextChar()throws IOException
				{
					throw new AssertionError();
				};
				@Override public String getName(){ return "TOKEN_SEPARATOR"; };
			};
			
			
			/** Recognizes "end", that is ';'. Reports it as {@link ATxtReadFormat1.TIntermediateSyntax#SIG_END}.
			Transits to {@link #STRUCT_BODY}.
			*/
			private final ISyntaxHandler END = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
			{				
				@Override public boolean tryEnter()throws IOException
				{
					int r = tryRead();
					if (r==-1) return false;
					char c = (char)r;
					if (c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
					{
						if (TRACE) TOUT.println("END->STRUCT_BODY");
						setStateHandler(STRUCT_BODY);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
						return true;
					}else
					{
						unread(c);
						return false;
					}
				};
				@Override public void toNextChar()throws IOException{ throw new AssertionError();};
				@Override public String getName(){ return "END"; };
			};
			
						/** A maximum length of continous sequence of white spaces
						which is allowed. Longer sequence of whitespaces will cause
						parser to throw {@link EFormatBoundaryExceeded}. -1 to disable limit. */
						private int continous_whitespace_limit = -1;
						/** A maximum length of comment line
						which is allowed. Longer sequence of white-spaces will cause
						parser to throw {@link EFormatBoundaryExceeded}. -1 to disable limit.
						<p>
						Note: Total length of comment block is not tested by this 
						limit.
						*/
						private int continous_comment_limit = -1;
					
	/* *********************************************************
	
	
			Construction
	
	
	**********************************************************/
	/** Constructs without any additional safety limits.
	@param in non null input
	*/
	public CPlainTxtReadFormat(Reader in)
	{
		this(in,-1,-1);
	}
	/** Constructs without any additional safety limits.
	@param in non null input 
	@param continous_whitespace_limit a maximum length of continous sequence of white spaces
		which is allowed. Longer sequence of whitespaces will cause
		parser to barfto throw {@link EFormatBoundaryExceeded}. -1 to disable limit. 
	@param continous_comment_limit alike, but for a single comment line. 
	*/
	public CPlainTxtReadFormat(Reader in, 
								final int continous_whitespace_limit,
								final int continous_comment_limit
								)
	{
		super(in,
			  0,//int name_registry_capacity - disabled
			  64//int token_size_limit 
			  );
		assert(continous_whitespace_limit>=-1);
		assert(continous_comment_limit>=-1);
		this.continous_whitespace_limit=continous_whitespace_limit;
		this.continous_comment_limit=continous_comment_limit;
	}
	
	/* ********************************************************************
	
		
			Safety settings.
	
	
	*********************************************************************/
	/** Gives what was set in constructor {@link CPlainTxtReadFormat#CPlainTxtReadFormat(Reader,int,int)}
	or by {@link #setContinousWhitespaceLimit}. Default is: disabled.
	@return limit, -1 if disabled */
	public final int getContinousWhitespaceLimit(){ return continous_whitespace_limit; }
	/** Sets safety limit. Must be set before {@link #open}.
	@param continous_whitespace_limit maximum length of continous sequence of white spaces
			which is allowed. Longer sequence of whitespaces will cause
			parser to throw {@link EFormatBoundaryExceeded}. -1 to disable limit. 
	*/
	public void setContinousWhitespaceLimit(int continous_whitespace_limit)
	{
		assert(continous_whitespace_limit>=-1);
		assert(!isOpen());
		this.continous_whitespace_limit = continous_whitespace_limit;
	}
	
	
	
	/** Gives what was set in constructor {@link CPlainTxtReadFormat#CPlainTxtReadFormat(Reader,int,int)}
	or by {@link #setContinousWhitespaceLimit}. Default is: disabled.
	@return limit, -1 if disabled */
	public final int getContinousCommentLimit(){ return continous_comment_limit; }
	/** Sets safety limit. Must be set before {@link #open}.
	@param continous_comment_limit  maximum length of comment line
		which is allowed. Longer sequence of white-spaces will cause
		parser to throw {@link EFormatBoundaryExceeded}. -1 to disable limit.
		<p>
		Note: Total length of comment block(s) in seqence is not assured by this 
		limit.
	*/
	public void setContinousCommentLimit(int continous_comment_limit)
	{
		assert(continous_comment_limit>=-1);
		assert(!isOpen());
		this.continous_comment_limit = continous_comment_limit;
	}
	
	/* ********************************************************************
	
		
			Characters classifiers
	
	
	*********************************************************************/
	private static boolean isEmptyChar(char c)
	{
		return Character.isWhitespace(c);
	};
	private static boolean isTokenBodyChar(char c)
	{
		return !(
				isEmptyChar(c)
				|| 
				(c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
				||
				(c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
				||
				(c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
				||
				(c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
				||
				(c==CPlainTxtWriteFormat.COMMENT_CHAR)
				);
	};
	
	/* ********************************************************************
	
		
			AStructReadFormatBase0
	
	
	*********************************************************************/
	/** Closes input, abandons state handler. */
	@Override protected void closeImpl()throws IOException
	{
		super.closeImpl();
		in.close();
	};
	/** Initializes state handler */
	@Override protected void openImpl()throws IOException
	{		
		if (TRACE) TOUT.println("openImpl->STRUCT_BODY");
		setStateHandler(STRUCT_BODY);
	};
	/* ********************************************************************
	
		
			IFormatLimits
	
	
	*********************************************************************/
	/** Unbound*/
	@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
	/** Unbound*/
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	
};