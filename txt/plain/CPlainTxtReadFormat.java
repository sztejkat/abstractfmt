package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.ATxtReadFormatStateBase0;
import sztejkat.abstractfmt.txt.ATxtReadFormat1;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Reader;

/**
	A reference plain text format implementation, reading side.
	This is also a code template which shows the easiest and cleaniest 
	path to parsing text files into format readers.
*/
public class CPlainTxtReadFormat extends ATxtReadFormatStateBase0<ATxtReadFormat1.TIntermediateSyntax>
{
			/** Used to  {@link CPlainTxtReadFormat#toNextChar} in a state dependent way,
			using a separate class per state.
			*/
			private abstract class AStateHandler extends ATxtReadFormatStateBase0<ATxtReadFormat1.TIntermediateSyntax>.AStateHandler
			{
				/** Reads from {@link #in} semi transparently handles eof 
				@return -1 or 0...0xFFFF. If -1 eof is already send to {@link #queueNextChar} 
				@throws IOException if {@link #in} thrown.*/
				protected int read()throws IOException
				{
					int c = in.read();
					assert((c>=-1)&&(c<=0xFFFF));
					if (c==-1)
					{
						queueNextChar(-1,null);
						return -1;
					}else
						return c;
				};
				
				/** Arms exception for invalid, unexpected character
				and additional message.
				@param c char to show
				@param explain explanation
				@return exception armed with line info.
				*/
				protected EBrokenFormat unexpectedCharException(char c,String explain)
				{
					assert(explain!=null);
					return new EBrokenFormat("Unexpected character \'"+c+"\'(0x"+Integer.toHexString(c)+")"+getLineInfoMessage()+"\n"+explain);
				};
			};
			
			/** Initial state, equal to TOKEN_BODY_LOOKUP */
			private final class NOTHING_StateHandler extends TOKEN_BODY_LOOKUP_StateHandler
			{
			}
			
			private final class COMMENT_StateHandler extends AStateHandler
			{
				/** Tests if we have double eol sequence
				@param current_eol char to report, current recognized eol sequence start.
				@param next_eol expected next part of eol sequence
				@throws IOException if failed */
				private void handlePossibleNextEol(char current_eol,char next_eol)throws IOException
				{
					 assert(current_eol!=next_eol);
					 int j = in.read();
					 if (j==-1)
					 {
						 //in this case this is just an end of a comment
						 queueNextChar(current_eol,ATxtReadFormat1.TIntermediateSyntax.VOID);
						 popStateHandler();
					 }else
					 if (j==next_eol)
					 {
						 //this is a part of a a comment, eat it.
						 queueNextChar(current_eol,ATxtReadFormat1.TIntermediateSyntax.VOID);
						 popStateHandler();
					 }else
					 {
						 //this is not a part of a comment.
						 in.unread((char)j);	//leave for future processing.
						 queueNextChar(current_eol,ATxtReadFormat1.TIntermediateSyntax.VOID);
						 popStateHandler();
					 };
				};
				@Override protected void toNextChar()throws IOException
				{
					final int i= in.read();
					//Note: For comments to be fully transparent the EOL must
					//belong to comment and reported as VOID. We need to handle
					//all eols: \r \n \r\n \n\r
					switch(i)
					{	
						case -1: popStateHandler();  //eof terminates comment.
								 queueNextChar(-1,null);
								 break;
						//handle possible double eol termination.
						case '\r':handlePossibleNextEol((char)i,'\n'); break;
						case '\n':handlePossibleNextEol((char)i,'\r'); break;
						default:
							//this is a comment, dump it to trash.
							queueNextChar(i,ATxtReadFormat1.TIntermediateSyntax.VOID);
					}
				};
			};
			/**  We collected {@link CPlainTxtWriteFormat#TOKEN_SEPARATOR_CHAR}
			     or we started the file, or we collected begin signal completely,
			     or we collected end signal.
			     <p>
				 Now are actively looking for a token body.
				 We expect {@link CPlainTxtWriteFormat#TOKEN_SEPARATOR_CHAR} 
				 or {@link CPlainTxtWriteFormat#BEGIN_SIGNAL_CHAR} 
				 or {@link CPlainTxtWriteFormat#END_SIGNAL_CHAR}
				 or {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR}
				 or {@link #isTokenBodyChar}
				 or {@link #isEmptyChar}
				 */
			private class TOKEN_BODY_LOOKUP_StateHandler extends AStateHandler
			{
				@Override protected void toNextChar()throws IOException
				{
					final int i= read();
					if (i==-1) return;
					final char c=(char)i;
					if (c==CPlainTxtWriteFormat.COMMENT_CHAR)
					{
						pushStateHandler(COMMENT);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.VOID);
					}else
					if (c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
					{
						//This is an empty token since we have found separator instead
						//of token body.
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.TOKEN_VOID);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
						//No state change
					}else
					if (c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
					{
						setStateHandler(COLLECTED_BEGIN);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
					}else
					if (c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
					{
						//We don't change state, we indicate to superclass we got end.
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
					}else
					if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
					{
						setStateHandler(STRING_TOKEN_BODY);
						//Sending TOKEN_VOID will trigger collection of a token body
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.TOKEN_VOID);
					}else					
					if (isTokenBodyChar(c))
					{
						setStateHandler(PLAIN_TOKEN_BODY);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.TOKEN);
					}else
					if (isEmptyChar(c))
					{
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.VOID);
					}else
						throw unexpectedCharException(c,"while looking for next token body");
				};
			}
			/**  We finished collecting token body, either string or plain.
				 We are now on look-up for a token separator or signal.
				 We do expect {@link #isEmptyChar} or {@link CPlainTxtWriteFormat#TOKEN_SEPARATOR_CHAR}
				 or {@link CPlainTxtWriteFormat#BEGIN_SIGNAL_CHAR} or {@link CPlainTxtWriteFormat#END_SIGNAL_CHAR}
		    */
			private class TOKEN_LOOKUP_StateHandler extends AStateHandler
			{
				@Override protected void toNextChar()throws IOException
				{
					final int i= read();
					if (i==-1) return;
					final char c=(char)i;
					if (c==CPlainTxtWriteFormat.COMMENT_CHAR)
					{
						pushStateHandler(COMMENT);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.VOID);
					}else
					if (c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
					if (c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
					}else
					if (c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
					{
						setStateHandler(COLLECTED_BEGIN);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
					}else
					if (isEmptyChar(c))
					{
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
						throw unexpectedCharException(c,"while looking for next token separator or signal");
				};
			};
			 /** Collected the {@link CPlainTxtWriteFormat#BEGIN_SIGNAL_CHAR},
				 now expecting either 
				 {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR} for "" enclosed name,
				 or {@link #isTokenBodyChar} for plain name or 
				 or {@link #isEmptyChar},
				  {@link CPlainTxtWriteFormat#END_SIGNAL_CHAR},
				  {@link CPlainTxtWriteFormat#BEGIN_SIGNAL_CHAR} for empty non name signal.
				 */
			private final class COLLECTED_BEGIN_StateHandler extends AStateHandler
			{
				@Override protected void toNextChar()throws IOException
				{
					final int i= read();
					if (i==-1) return;
					final char c=(char)i;
					if (c==CPlainTxtWriteFormat.COMMENT_CHAR)
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						pushStateHandler(COMMENT);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
					if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
					{
						setStateHandler(COLLECTING_STRING_BEGIN_NAME);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
					}else
					if (c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
					{
						setStateHandler(TOKEN_LOOKUP);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
					}else
					if (c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
					{
						setStateHandler(COLLECTED_BEGIN);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
					}else
					if (isTokenBodyChar(c))
					{
						setStateHandler(COLLECTING_BEGIN_NAME);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
					}else
					if (isEmptyChar(c))
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
						queueNextChar(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
						throw unexpectedCharException(c,"while looking for begin signal name");
				};
			};
			/** Collected first character of begin signal name, un-quoted. 
				Expecting more {@link #isTokenBodyChar} or any character which is not 
				a body char to terminate a name. 
		    */
			private final class COLLECTING_BEGIN_NAME_StateHandler extends AStateHandler
			{
				@Override protected void toNextChar()throws IOException
				{
					final int i= read();
					if (i==-1) return;
					final char c=(char)i;
					if (c==CPlainTxtWriteFormat.COMMENT_CHAR)
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						pushStateHandler(COMMENT);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
					if (isTokenBodyChar(c))
					{
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
					}else
					{
						//Now basically we go to lookup. Easiest way is to un-read it, because
						//it may be a signal.
						in.unread(c);
						setStateHandler(TOKEN_BODY_LOOKUP);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}
				}
			};
			/**  A string body collector.
				 <p>
				 Collected {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR}
				 opening string token. Now expecting un-escaped
				 {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR}
				 or any other char forming the string.
				 <p>
				 Will correctly handle all escapes.
		    */
			private abstract class AStringBody_StateHandler extends AStateHandler
			{
						private final ATxtReadFormat1.TIntermediateSyntax body_syntax;
						private final ATxtReadFormat1.TIntermediateSyntax terminator_syntax;
						private final AStateHandler next_handler;
						private final APlainUnescapingEngine escaper = new APlainUnescapingEngine()
						{
							@Override protected int readImpl()throws IOException{ return in.read(); };
							@Override protected void unread(char c)throws IOException
							{
								in.unread(c);
							};
						};
				/** 
				@param body_syntax a syntax element to set when encountering body character
				@param terminator_syntax a syntax element to set when encountering string terminator
				@param next_handler a state handler to set after finising collection
									together with returning <code>terminator_syntax</code>;
				*/
				AStringBody_StateHandler(
								ATxtReadFormat1.TIntermediateSyntax body_syntax,
								ATxtReadFormat1.TIntermediateSyntax terminator_syntax,
								AStateHandler next_handler
								)
				{
					assert(body_syntax!=null);
					assert(terminator_syntax!=null);
					assert(next_handler!=null);
					this.next_handler = next_handler;
					this.body_syntax=body_syntax;
					this.terminator_syntax=terminator_syntax;
				};
				@Override protected void onEnter()
				{
					//Just to be sure, that if escaper thrown AND state transition was mande AND something
					//was read in an another state (without passing through the escaper) the escaper syntax
					//is reset.
					escaper.reset();
				};
				@Override protected void toNextChar()throws IOException
				{
					int i = escaper.read();
					if (i==-1) return;
					assert((i>=-1)&&(i<=0xFFFF));
					char c = (char)i;
					//detect un-escaped string terminator.
					if ((c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)&&(!escaper.isEscaped()))
					{
						setStateHandler(next_handler);
						queueNextChar(c,terminator_syntax);
					}else
					{
						//everything else is a string body
						queueNextChar(c,body_syntax);
					}
				}
			};
			/**
			Responsible for collecting double quouted token body. 
			*/
			private final class STRING_TOKEN_BODY_StateHandler extends AStringBody_StateHandler
			{
					STRING_TOKEN_BODY_StateHandler()
					{
						super(
									ATxtReadFormat1.TIntermediateSyntax.TOKEN,
									ATxtReadFormat1.TIntermediateSyntax.SEPARATOR,
									TOKEN_LOOKUP
									);
					};
			};
			/**
			Responsible for collecting double quouted begin signal name. 
			*/
			private final class COLLECTING_STRING_BEGIN_NAME_StateHandler extends AStringBody_StateHandler
			{
					COLLECTING_STRING_BEGIN_NAME_StateHandler()
					{
						super(
									ATxtReadFormat1.TIntermediateSyntax.SIG_NAME,
									ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID,
									TOKEN_BODY_LOOKUP
									);
					};
			};
			/**
			     We collected first {@link #isTokenBodyChar} and are on the look-up for
				 subsequent {@link #isTokenBodyChar} or 
				 or {@link CPlainTxtWriteFormat#BEGIN_SIGNAL_CHAR} 
				 or {@link CPlainTxtWriteFormat#END_SIGNAL_CHAR}
				 or {@link #isEmptyChar} 
				 or {@link CPlainTxtWriteFormat#TOKEN_SEPARATOR_CHAR} */
			private final class PLAIN_TOKEN_BODY_StateHandler extends AStateHandler
			{
				@Override protected void toNextChar()throws IOException
				{
					final int i= read();
					if (i==-1) return;
					final char c=(char)i;
					if (c==CPlainTxtWriteFormat.COMMENT_CHAR)
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						pushStateHandler(COMMENT);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
					if (c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
					{
						setStateHandler(COLLECTED_BEGIN);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
					}else
					if (c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
					{
						//We don't change state, we indicate to superclass we got end.
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
					}else
					if (c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
					if (isTokenBodyChar(c))
					{
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.TOKEN);
					}else
					if (isEmptyChar(c))
					{
						setStateHandler(TOKEN_LOOKUP);
						queueNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
						throw unexpectedCharException(c,"while collecting token body");
				}
			};
						
				private final AStateHandler NOTHING = new NOTHING_StateHandler();
				private final AStateHandler COLLECTED_BEGIN = new COLLECTED_BEGIN_StateHandler();
				private final AStateHandler TOKEN_LOOKUP = new TOKEN_LOOKUP_StateHandler();
				private final AStateHandler TOKEN_BODY_LOOKUP = new TOKEN_BODY_LOOKUP_StateHandler();
				private final AStateHandler COLLECTING_BEGIN_NAME = new COLLECTING_BEGIN_NAME_StateHandler();
				private final AStateHandler STRING_TOKEN_BODY = new  STRING_TOKEN_BODY_StateHandler();
				private final AStateHandler PLAIN_TOKEN_BODY = new  PLAIN_TOKEN_BODY_StateHandler();
				private final AStateHandler COLLECTING_STRING_BEGIN_NAME = new  COLLECTING_STRING_BEGIN_NAME_StateHandler();
				private final AStateHandler COMMENT = new  COMMENT_StateHandler();
				/** Push-back input */
				private final CAdaptivePushBackReader in;
				
					
	/* *********************************************************
	
	
			Construction
	
	
	**********************************************************/
	public CPlainTxtReadFormat(Reader in)
	{
		super(0,//int name_registry_capacity - disabled
			  64//int token_size_limit 
			  );
		assert(in!=null);
		this.in = new CAdaptivePushBackReader(in,1,1);
		setStateHandler(NOTHING);
	}
	private String getLineInfoMessage()
	{
		int c = in.getCharNumber();
		return " at line "+(in.getLineNumber()+1)+" position "+c+ (c<=0 ? " from the end of line" : "");
	};
	/* ********************************************************************
	
		
			Syntax definitions
	
	
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
				);
	};
	/*private static boolean isStringTokenBodyChar(char c)
	{
		return c!=CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR;
	};*/
	
	/* ********************************************************************
	
		
			AStructReadFormatBase0
	
	
	*********************************************************************/
	/** Closes input */
	@Override protected void closeImpl()throws IOException
	{
		in.close();
	};
	/** Empty */
	@Override protected void openImpl()throws IOException{};
	/* ********************************************************************
	
		
			IFormatLimits
	
	
	*********************************************************************/
	/** Unbound*/
	@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
	/** Unbound*/
	@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
	
};