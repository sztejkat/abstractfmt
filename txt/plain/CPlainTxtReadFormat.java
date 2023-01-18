package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.ATxtReadFormat0;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CAdaptivePushBackReader;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Reader;

/**
	A reference plain text format implementation, reading side.
	This is also a code template which shows the easiest and cleaniest 
	path to parsing text files into format readers.
	
	<h1>Text parsing</h1>
	As You probably noticed the {@link ATxtReadFormat0}
	turns around two methods:
	<ul>
		<li>{@link ATxtReadFormat0#tokenIn} and;</li>
		<li>{@link ATxtReadFormat0#hasUnreadToken};</li>
	</ul>
	Both are practically the same and could be replaced with
	<pre>
		peek
		pop
	</pre>
	theorem.
	<p>
	The lower layer which is {@link ARegisteringStructReadFormat} is
	designed around a single method:
	<ul>
		<li>{@link ARegisteringStructReadFormat#readSignalReg};</li>
	</ul>
	<p>
	
*/
public class CPlainTxtReadFormat extends ATxtReadFormat0
{
	-- re design to be a parsing pattern.
				/** Tokenization state */
				private final enum TTokenState
				{
					...todo
				};
					/** Line counting and push-back capable input */
					private final CAdaptivePushBackReader in;
					/** Tokenization state machine */
					private TTokenState token_state = TTokenState.NOTHING; 
					/** Set by {@link #hasUnreadToken} to avoid multiple moves
					across the stream */
					private boolean has_unread_pending;
					/** What {@link #hasUnreadToken} got from {@link #tokenIn} */
					private int has_unread_collected_tokenIn;
					
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
	}
	private void String getLineInfoMessage()
	{
		int c = in.getCharNumber();
		return " at line "+(in.getLineNumber()+1)+" position "+c+ (c<=0 ? " from the end of line" : "");
	};
	/* *********************************************************
	
	
			ATxtReadFormat0
	
	
	**********************************************************/
	
	@Override protected int tokenIn()throws IOException
	{
		//Handle pending effects buffered by hasUnreadToken
		if (has_unread_pending)
		{
			has_unread_pending = false;
			return has_unread_collected_tokenIn;
		};
		//really move to next token element
		loop:
		for(;;)
		{
			switch(token_state)
			{
				case STATE_SEPARATOR_LOOKUP:
							//We do look-up for a token separator
							{
								int r = in.read();
								if (r==-1) return TOKEN_EOF;
								char c= (char)r;
								if (c==CPlainTxtWriter.BEGIN_SIGNAL_CHAR)
								{
									//we do not need to remember for the future, if we un-read it.
									in.unread(r);
									return TOKEN_SIGNAL;
								}else
								if (c==CPlainTxtWriter.END_SIGNAL_CHAR)
								{
									//alike
									in.unread(r);
									return TOKEN_SIGNAL;
								}else
								if (c==CPlainTxtWriter.TOKEN_SEPARATOR_CHAR)
								{
									token_state = STATE_TOKEN_START_LOOKUP;
								}else
								if (Character.isWhitespace(c))
								{
									//this should be skipped, so do continue
								}else
									//We do require separator before token!
									throw new EBrokenFormat("Unexpected \'"+c+"\'(0x"+Integer.toHexString(c)+")"+getLineInfoMessage());
							};
							break;
				case STATE_TOKEN_START_LOOKUP:
							//We do look for a beginning of next token. Any eventuall separator is already consumed.
							{
								int r = in.read();
								if (r==-1) return TOKEN_EOF;
								char c= (char)r;
								if (c==CPlainTxtWriter.BEGIN_SIGNAL_CHAR)
								{
									//we do not need to remember for the future, if we un-read it.
									in.unread(r);
									return TOKEN_SIGNAL;
								}else
								if (c==CPlainTxtWriter.END_SIGNAL_CHAR)
								{
									//alike
									in.unread(r);
									return TOKEN_SIGNAL;
								}else
								if (c==CPlainTxtWriter.TOKEN_SEPARATOR_CHAR)
								{
									//In this case we do return empty token
									token_state = STATE_TOKEN_START_LOOKUP; //because we consumed the separator
									return TOKEN_BOUNDARY;
								}else
								if (Character.isWhitespace(c))
								{
									//this should be skipped
								}else
								if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
								{
									//and we have a string token token
									token_state = STATE_INSIDE_STRING_TOKEN;
									//consume this and fetch next character.
									//Easiest way is to continue.
								}else
								{
									//we have a regular token
									token_state = STATE_INSIDE_PLAIN_TOKEN;
									return c;
								};
							};
							break; 
				case STATE_INSIDE_PLAIN_TOKEN:
							//we do look for the end of token or signal
							{
								int r = in.read();
								if (r==-1) return TOKEN_EOF;
								char c= (char)r;
								if (c==CPlainTxtWriter.BEGIN_SIGNAL_CHAR)
								{
									//we do not need to remember for the future, if we un-read it.
									in.unread(r);
									return TOKEN_SIGNAL;
								}else
								if (c==CPlainTxtWriter.END_SIGNAL_CHAR)
								{
									//alike
									in.unread(r);
									return TOKEN_SIGNAL;
								}else
								if (c==CPlainTxtWriter.TOKEN_SEPARATOR_CHAR)
								{
									//token is terminated, but we look for next token
									token_state = STATE_TOKEN_START_LOOKUP; //because we consumed the separator
									return TOKEN_BOUNDARY;
								}else
								if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
								{
									//This is a syntax error. " is the reason for beeing enclosed.
									throw new EBrokenFormat("Unexpected string indicator in plain token: \'"+c+"\'(0x"+Integer.toHexString(c)+")"+getLineInfoMessage());
								}else
								{
									//A normal, parsable text.
									return c;
								};
							};
							break; 
				case STATE_INSIDE_STRING_TOKEN:
							//we do look for the end of token or signal
							{
								int r = in.read();
								if (r==-1) return TOKEN_EOF;
								char c= (char)r;
								if (c==CPlainTxtWriter.BEGIN_SIGNAL_CHAR)
								{
									//we do not need to remember for the future, if we un-read it.
									in.unread(r);
									return TOKEN_SIGNAL;
								}else
								if (c==CPlainTxtWriter.END_SIGNAL_CHAR)
								{
									//alike
									in.unread(r);
									return TOKEN_SIGNAL;
								}else
								if (c==CPlainTxtWriter.TOKEN_SEPARATOR_CHAR)
								{
									//token is terminated, but we look for next token
									token_state = STATE_TOKEN_START_LOOKUP; //because we consumed the separator
									return TOKEN_BOUNDARY;
								}else
								if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
								{
									//This may be escape or terminator?
									r = in.read();
									if (r==-1){ in.unread(c); return TOKEN_EOF; };
									c= (char)r;
									if (c==CPlainTxtWriteFormat.STRING_TOKEN_SEPARATOR_CHAR)
									{
										//escape, just return it
										return c;
									}else
									{
										//was terminator, un-read it
										in.unread(c);
										token_state = STATE_SEPARATOR_LOOKUP; 
									};
								}else
								{
									//A normal, parsable text.
									return c;
								};
							};
							break;		
			};
	};
	@Override protected int hasUnreadToken()throws IOException
	{
		//use pending state
		if (!has_unread_pending)
		{
			//collect new state
			has_unread_pending = true;	
			has_unread_collected_tokenIn = tokenIn();
		};
		//return collected state.
		return (has_unread_collected_tokenIn<0 ? has_unread_collected_tokenIn : 0);
		
	};
};