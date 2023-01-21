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
				@return -1 or 0...0xFFFF. If -1 eof is already set to 
				{@link #next_syntax_element}, {@link #next_char} */
				protected int read()throws IOException
				{
					int c = in.read();
					assert((c>=-1)&&(c<=0xFFFF));
					if (c==-1)
					{
						setNextChar(-1,null);
						return -1;
					}else
						return c;
				};
				/** Arms exception for invalid, unexpected character
				@param c char to show
				@return exception armed with line info.
				*/
				protected EBrokenFormat unexpectedCharException(char c)
				{
					return new EBrokenFormat("Unexpected character \'"+c+"\'(0x"+Integer.toHexString(c)+")"+getLineInfoMessage());
				};
			};
			
			/** Initial state, equal to TOKEN_BODY_LOOKUP */
			private final class NOTHING_StateHandler extends TOKEN_LOOKUP_StateHandler
			{
			}
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
					if (c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
					{
						//We don't change state. We indicate to superclass
						//we got next token.
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.NEXT_TOKEN);
					}else
					if (c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
					{
						setStateHandler(COLLECTED_BEGIN);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
					}else
					if (c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
					{
						//We don't change state, we indicate to superclass we got end.
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
					}else
					if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
					{
						setStateHandler(STRING_TOKEN_BODY);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.VOID);
					}else
					if (isTokenBodyChar(c))
					{
						setStateHandler(PLAIN_TOKEN_BODY);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.TOKEN);
					}else
					if (isEmptyChar(c))
					{
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.VOID);
					}else
						throw unexpectedCharException(c);
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
					if (c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.NEXT_TOKEN);
					}else
					if (c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
					}else
					if (c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
					{
						setStateHandler(COLLECTED_BEGIN);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
					}else
					if (isEmptyChar(c))
					{
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.VOID);
					}else
						throw unexpectedCharException(c);
				};
			};
			 /** Collected the {@link CPlainTxtWriteFormat#BEGIN_SIGNAL_CHAR},
				 now expecting either 
				 {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR} for "" enclosed name,
				 or {@link #isTokenBodyChar} for plain name or 
				 or {@link #isEmptyChar} for non name signal. 
				 */
			private final class COLLECTED_BEGIN_StateHandler extends AStateHandler
			{
				@Override protected void toNextChar()throws IOException
				{
					final int i= read();
					if (i==-1) return;
					final char c=(char)i;
					if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
					{
						setStateHandler(COLLECTING_STRING_BEGIN_NAME);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
					}else
					if (isTokenBodyChar(c))
					{
						setStateHandler(COLLECTING_BEGIN_NAME);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
					}else
					if (isEmptyChar(c))
					{
						setStateHandler(TOKEN_LOOKUP);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
					}else
						throw unexpectedCharException(c);
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
					if (isTokenBodyChar(c))
					{
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
					}else
					{
						//Now basically we go to lookup. Easiest way is to un-read it, because
						//it may be a signal.
						in.unread(c);
						setStateHandler(TOKEN_BODY_LOOKUP);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}
				}
			};
			/** A string body collector.
				 <p>
				 Collected {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR}
				 opening string token. Now expecting either {@link #isStringTokenBodyChar}
				 or {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR}
				 or any other char terminating it. We will do a look-ahead for
				 escapes applied to {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR} 
		    */
			private abstract class AStringBody_StateHandler extends AStateHandler
			{
						private final ATxtReadFormat1.TIntermediateSyntax body_syntax;
						private final ATxtReadFormat1.TIntermediateSyntax terminator_syntax;
				/** 
				@param body_syntax a syntax element to set when encountering body character
				@param terminator_syntax a syntax element to set when encountering string terminator 
				*/
				AStringBody_StateHandler(
								ATxtReadFormat1.TIntermediateSyntax body_syntax,
								ATxtReadFormat1.TIntermediateSyntax terminator_syntax
								)
				{
					assert(body_syntax!=null);
					assert(terminator_syntax!=null);
					this.body_syntax=body_syntax;
					this.terminator_syntax=terminator_syntax;
				};
				@Override protected void toNextChar()throws IOException
				{
					int i= read();
					if (i==-1) return;
					final char c=(char)i;
					if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
					{
						//either escaped or end of body
						final int j = read();
						if (j==-1) return;
						char ce = (char)j;
						if (ce==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
						{
							//escaped
							setNextChar(c,body_syntax);
						}else
						{
							//un-read for token lookup.
							in.unread(ce);
							setStateHandler(TOKEN_LOOKUP);
							setNextChar(c,terminator_syntax);
						}
					}else
					if (isStringTokenBodyChar(c))
					{
						setNextChar(c,body_syntax);
					}else
						throw unexpectedCharException(c);
				}
			};
			 /** Collected {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR}
				 opening string token. Now expecting either {@link #isStringTokenBodyChar}
				 or {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR}
				 or any other char terminating it. We will do a look-ahead for
				 escapes applied to {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR} */
			private final class STRING_TOKEN_BODY_StateHandler extends AStringBody_StateHandler
			{
					STRING_TOKEN_BODY_StateHandler()
					{
						super(
									ATxtReadFormat1.TIntermediateSyntax.TOKEN,
									ATxtReadFormat1.TIntermediateSyntax.SEPARATOR
									);
					};
			};
			/** Collected {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR}
				 opening begin name. Now expecting either {@link #isStringTokenBodyChar}
				 or {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR}
				 or any other char terminating it. We will do a look-ahead for
				 escapes applied to {@link CPlainTxtWriteFormat#STRING_TOKEN_SEPARATOR_CHAR} */
			private final class COLLECTING_STRING_BEGIN_NAME_StateHandler extends AStringBody_StateHandler
			{
					COLLECTING_STRING_BEGIN_NAME_StateHandler()
					{
						super(
									ATxtReadFormat1.TIntermediateSyntax.SIG_NAME,
									ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID
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
					if (c==CPlainTxtWriteFormat.BEGIN_SIGNAL_CHAR)
					{
						setStateHandler(COLLECTED_BEGIN);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
					}else
					if (c==CPlainTxtWriteFormat.END_SIGNAL_CHAR)
					{
						//We don't change state, we indicate to superclass we got end.
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SIG_END);
					}else
					if (c==CPlainTxtWriteFormat.TOKEN_SEPARATOR_CHAR)
					{
						setStateHandler(TOKEN_BODY_LOOKUP);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.NEXT_TOKEN);
					}else
					if (isTokenBodyChar(c))
					{
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.TOKEN);
					}else
					if (isEmptyChar(c))
					{
						setStateHandler(TOKEN_LOOKUP);
						setNextChar(c,ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
					}else
						throw unexpectedCharException(c);
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
				/** Push-back input */
				private final CAdaptivePushBackReader in;
				
					
	/* *********************************************************
	
	
			Construction
	
	
	**********************************************************/
	public CPlainTxtReadFormat(Reader in)
	{
		super(0,//int name_registry_capacity - disabled
			  64//int token_size_limit - basically max integer representation
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
	private static boolean isStringTokenBodyChar(char c)
	{
		return c!=CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR;
	};
	
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