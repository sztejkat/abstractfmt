package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.CBoundStack;
import sztejkat.abstractfmt.utils.SStringUtils;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.Reader;

/**
	 A "state graph" based parser with a {@link Reader} as a down-stream.
	 <p>
	 Basically adds a lot of character focused methods which should help You
	 in creating "consumers" and "catchers". 
*/
public abstract class ATxtReadFormatStateBase1<TSyntax extends ATxtReadFormat1.ISyntax> 
			    extends ATxtReadFormatStateBase0<TSyntax>
{	
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ATxtReadFormatStateBase1.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ATxtReadFormatStateBase1.",ATxtReadFormatStateBase1.class) : null;
 
         	/** Adds functions focused on consuming
         	characters provided by {@link ATxtReadFormatStateBase1#in}.
         	*/
         	protected abstract class AStateHandler extends ATxtReadFormatStateBase0<TSyntax>.AStateHandler
			{
				/** Reads data from {@link ATxtReadFormatStateBase1#in}.
				If encounters end-of-file puts eof indicator into {@link ATxtReadFormatStateBase1#queueNextChar}
				and returns -1. Otherwise just returns the character.
				@return -1 or 0...0xFFFF
				@throws IOException if failed.
				*/
				protected final int read()throws IOException
				{
					return ATxtReadFormatStateBase1.this.read();
				};
				/** Reads data from {@link #in}.
				If encounters end-of-file throws {@link EUnexpectedEof}
				 Otherwise just returns the character.
				@return 0...0xFFFF
				@throws IOException if failed.
				@throws EUnexpectedEof if reached end-of-file.
				*/
				protected final char readAlways()throws IOException, EUnexpectedEof
				{
					return ATxtReadFormatStateBase1.this.readAlways();
				};
				/** Calls <code>{@link #in}.unread(...).</code>
				@param c --//--
				@throws IOException --//--
				@see CAdaptivePushBackReader#unread(char)
				*/
				protected final void unread(char c)throws IOException{ in.unread(c); };
				/** Calls <code>{@link #in}.unread(...).</code>
				@param chars --//--
				@throws IOException --//--
				@see CAdaptivePushBackReader#unread(CharSequence)
				*/
				protected final void unread(CharSequence chars)throws IOException{ in.unread(chars); };
				/** Calls <code>{@link #in}.unread(...).</code>
				@param chars --//--
				@param from --//--
				@param length --//--
				@throws IOException --//--
				@see CAdaptivePushBackReader#unread(CharSequence,int,int)
				*/
				protected final void unread(CharSequence chars,int from, int length)throws IOException
				{
					in.unread(chars,from,length);
				}
			};
			
			/** Adds functions focused on consuming and matching
         	characters read from {@link ATxtReadFormatStateBase1#in} by providing
         	various methods which can be used to collect some text from the down-stream
			and compare it with expected pattern(s).
			<p>
			Since the <code>ASyntaxHandler</code> is focused on "catcher"
			part of syntax processing described in {@link ATxtReadFormatStateBase0.ASyntaxHandler}
			this class focuses on supporting detection of "catch phrases".
			<p>
			If the "catch phrase" for the syntax handler is known and fixed the
			{@link #tryEnter} may look like:
			<pre>
	protected boolean tryEnter()throws IOException
	{
		if (!looksAt("phrase")) //<i>or other variants of looksAt</i>
		{
			unread();
			return false;
		}else
		{
			setStateHandler(this); //<i>or pushStateHandler(this)</i>
			return true;
		}
	}
			</pre>
			We intentionally do NOT provide this as default implementation because
			we would have to provice eight variants of them what would be rather
			confusing to a person subclassing it, because there may be more than one
			"catch-phrase" or because a totally different way of finding a match
			will have to be used.
			*/
         	protected abstract class ASyntaxHandler extends ATxtReadFormatStateBase0<TSyntax>.ASyntaxHandler
			{
							/** A "collected token" buffer. 
							    It is wiped out on {@link #onLeave}, so be sure to 
							    pick up all data before making a state transition which 
							    will trigger {@link #onLeave}.
							*/
							protected final StringBuilder collected = new StringBuilder(); 
							
				/* ************************************************************************
							Character level API
				************************************************************************ */
				/** See {@link AStateHandler#read}
				@return --//--
				@throws IOException --//--
				*/
				protected final int read()throws IOException
				{
					return ATxtReadFormatStateBase1.this.read();
				};
				/** See {@link AStateHandler#readAlways}
				@return --//--
				@throws IOException --//--
				@throws EUnexpectedEof --//--
				*/
				protected final char readAlways()throws IOException, EUnexpectedEof
				{
					return ATxtReadFormatStateBase1.this.readAlways();
				};
				/** Calls <code>{@link #in}.unread(...).</code>
				@param c --//--
				@throws IOException --//--
				@see CAdaptivePushBackReader#unread(char)
				*/
				protected final void unread(char c)throws IOException{ in.unread(c); };
				/** Calls <code>{@link #in}.unread(...).</code>
				@param chars --//--
				@throws IOException --//--
				@see CAdaptivePushBackReader#unread(CharSequence)
				*/
				protected final void unread(CharSequence chars)throws IOException{ in.unread(chars); };
				/** Calls <code>{@link #in}.unread(...).</code>
				@param chars --//--
				@param from --//--
				@param length --//--
				@throws IOException --//--
				@see CAdaptivePushBackReader#unread(CharSequence,int,int)
				*/
				protected final void unread(CharSequence chars,int from, int length)throws IOException
				{
					in.unread(chars,from,length);
				}
				/* ************************************************************************
							recognition phrase collection API
				************************************************************************ */
				/** Performs character collection into a {@link #collected} buffer.
				@return <ul>
							<li>if {@link #collected} is empty:
								<ul>
									<li>-1(eof). <u>Nothing</u> is done;</li>
									<li>0...0xFFFF - a collected character. This character
										is automatically appended to {@link #collected} buffer;
									</li>
								</ul>
							</li>
							<li>if {@link #collected} is NOT empty:
								<ul>
									<li>if encounters an end-of-file throws {@link EUnexpectedEof};</li>
									<li>0...0xFFFF - a collected character. This character
										is automatically appended to {@link #collected} buffer;
									</li>
								</ul>
							</li>
						</ul>
					@throws EUnexpectedEof if could not collect subsequent characters, except first
							for which returns -1.
					@throws IOException if failed.
				*/
				protected int collect()throws EUnexpectedEof,IOException
				{
					int r = in.read();
					assert((r>=-1)&&(r<=0xFFFF));
					if (r==-1)
					{
						if (collected.length()!=0) throw new EUnexpectedEof();
						return -1;
					};
					collected.append((char)r);
					return r;
				};
				/** Performs character collection into a {@link #collected} buffer.
				@return <ul>
							<li>-1(eof). <u>Nothing</u> is done;</li>
							<li>0...0xFFFF - a collected character. This character
								is automatically appended to {@link #collected} buffer;
							</li>
						</ul>
					@throws IOException if failed.
				*/
				protected int tryCollect()throws IOException
				{
					int r = in.read();
					assert((r>=-1)&&(r<=0xFFFF));
					if (r==-1)
					{
						return -1;
					};
					collected.append((char)r);
					return r;
				};
				/** Un-reads entire collected buffer to the down-stream and 
				clears the collected buffer.
				@throws IOException if failed.
				@see #collected 
				*/
				protected final void unread()throws IOException
				{
					in.unread(collected);
					collected.setLength(0);
				};
				/** Tests if {@link #collected} can represent <code>text</code>.
				This is a case-sensitive comparison.
				<p>
				Avoids creating temporary String objects.
				@param text text which should be compared with buffer
				@return <ul>
							<li>-1 if {@link #collected} cannot represent text;</li>
							<li>0 if {@link #collected} do represent a starting portion of the text, but not full text;</li>
							<li>1 if {@link #collected} do represent text;</li>
					</ul>
				@see SStringUtils
				*/
				protected final int canStartWithCollected(String text)
				{
					return SStringUtils.canStartWithCaseSensitive(collected,text);
				};
				/** Tests if {@link #collected} can represent <code>text</code>.
				This is a case-insensitive comparison.
				Avoids creating temporary String objects.
				@param text text which should be compared with buffer
				@return <ul>
							<li>-1 if {@link #collected} cannot represent text;</li>
							<li>0 if {@link #collected} do represent a starting portion of the text, but not full text;</li>
							<li>1 if {@link #collected} do represent text;</li>
					</ul>
				@see SStringUtils
				*/
				protected final int canStartWithCollectedCaseInsensitive(String text)
				{
					return SStringUtils.canStartWithCaseInsensitive(collected,text);
				};
				/** Collects up to <code>text.length()</code> characters and passes the collected
				data through {@link #canStartWithCollected} with each collected character.
				<p>
				Collection stops if {@link #canStartWithCollected} returns 1 or -1
				or end-of-file is reached.
				<p>
				Nothing is reported through {@link #queueNextChar}.
				<p>
				After return the {@link #collected} do carry what was collected
				during the process.
				
				@param text to compare with what.
				@return true if {@link #canStartWithCollected} returned 1,
						false if returned -1 or could not read even one character.
				@throws IOException if failed at low level
				@throws EUnexpectedEof if end-of-file is reached at any character except the
						first one.
				*/
				protected final boolean looksAt(String text)throws IOException, EUnexpectedEof
				{
					for(int at=0;;at++)
					{
						if (collect()==-1) return false;
						//For efficiency use counted method
						//Comparison will automatically give -1 if we try to collect too much characters.
						switch(SStringUtils.canStartWithCaseSensitive(collected,text,at))
						{
							case -1: return false;
							case  0: break;
							case  1: return true;
						};
					}
				};
				/** A case-insentive variant of {@link #looksAt}
				@param text text to compare with
				@return see {@link #looksAt}
				@throws IOException --//--
				@throws EUnexpectedEof --//--
				*/
				protected final boolean looksAtCaseInsensitive(String text)throws IOException, EUnexpectedEof
				{
					//See comments in looksAt(...)
					for(int at=0;;at++)
					{
						if (collect()==-1) return false;
						switch(SStringUtils.canStartWithCaseInsensitive(collected,text,at)) 
						{
							case -1: return false;
							case  0: break;
							case  1: return true;
						};
					}
				};
				/** A variant of {@link #looksAt} which never throws {@link EUnexpectedEof}
				returning false instead.
				@param text text to compare with
				@return see {@link #looksAt}
				@throws IOException --//--
				*/
				protected final boolean tryLooksAt(String text)throws IOException
				{
					//See comments in looksAt(...)
					for(int at=0;;at++)
					{
						if (tryCollect()==-1) return false;
						switch(SStringUtils.canStartWithCaseSensitive(collected,text,at)) 
						{
							case -1: return false;
							case  0: break;
							case  1: return true;
						};
					}
				};
				/** A variant of {@link #looksAtCaseInsensitive} which never throws {@link EUnexpectedEof}
				returning false instead.
				@param text text to compare with
				@return see {@link #looksAt}
				@throws IOException --//--
				*/
				protected final boolean tryLooksAtCaseInsensitive(String text)throws IOException
				{
					//See comments in looksAt(...)
					for(int at=0;;at++)
					{
						if (tryCollect()==-1) return false;
						switch(SStringUtils.canStartWithCaseInsensitive(collected,text,at))  
						{
							case -1: return false;
							case  0: break;
							case  1: return true;
						};
					}
				};
				/* ************************************************************************
							AStateHandler
				************************************************************************ */
				/** Overriden to wipe out {@link #collected} */
				@Override protected void onLeave()
				{
					super.onLeave();
					collected.setLength(0);
				};
			};
			
					/** The down-stream wrapped in a push-back 
					capable buffer. 
					<p>
					This buffer is able to adaptively grow when data
					area pushed back to it and to make "line number" and "character
					in line" accounting".
					<p>
					This buffer is closed when {@link #close} is called.
					<p>
					<i>Note: Why not a standard {@link java.io.PushbackReader}?
					Beacuse standard is using a fixed size push-back buffer
					and cannot addapt to changing scenarios. Why not 
					<code>mark()/reset()</code> of {@link java.io.BufferedReader}? Because
					it is also bound with buffer size and does not allow
					a multiple "marks" to be placed. </i>
					*/
					protected final CAdaptivePushBackReader in;
	/* ***************************************************************************
	
			Construction
	
	
	*****************************************************************************/			
	/** Creates. Subclass must initialize state handler by calling
	{@link #setStateHandler} and adjust stack limit with {@link #setHandlerStackLimit}.
	@param in non null down-stream reader. This reader will be wrapped in
			{@link CAdaptivePushBackReader} and will be accessible through
			{@link #in} field. Will be closed on {@link #close}.
			<p>
			No I/O operation will be generate till {@link #open}.
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
	@param token_size_limit non-zero positive, a maximum number of characters which constitutes 
			a primitive element token, excluding string tokens. Basically a maximum
			number of characters which do constitute a primitive numeric value.
	@throws AssertionError if token_size_limit is less than 16+3 which is a minimum
			number to hold hex encoded long value.
	*/				
	protected ATxtReadFormatStateBase1(Reader in,
									   int name_registry_capacity,
									   int token_size_limit
									   )
	{
			super(name_registry_capacity,token_size_limit);
			assert(in!=null);
			this.in = new CAdaptivePushBackReader(in);
	};
	/** Creates. Subclass must initialize state handler by calling
	{@link #setStateHandler} and adjust stack limit with {@link #setHandlerStackLimit}.
	<p>
	Use this form if You need to precisely control on which object
	push-back reader synchronizes or what buffer it is using.
	
	@param in non null down-stream reader. Will be closed on {@link #close}.
			<p>
			No I/O operation will be generate till {@link #open}.
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
	@param token_size_limit non-zero positive, a maximum number of characters which constitutes 
			a primitive element token, excluding string tokens. Basically a maximum
			number of characters which do constitute a primitive numeric value.
	@throws AssertionError if token_size_limit is less than 16+3 which is a minimum
			number to hold hex encoded long value.
	*/				
	protected ATxtReadFormatStateBase1(CAdaptivePushBackReader in,
									   int name_registry_capacity,
									   int token_size_limit
									   )
	{
			super(name_registry_capacity,token_size_limit);
			assert(in!=null);
			this.in = in;
	};
	/* ****************************************************************
	
		Toolboxing for common services across AStateHandler and ASyntaxHandler.
		
		Note: 
			Due to lack of multiplie inheritance I can't just add
			bunch of methods by making AStateHandler extends AStateHandler, ReaderBackendSupport
			I need to do some "composition" and easiest way is to refere to
			private methods of outer class.
	
	**************************************************************** */
	/** Reads data from {@link #in}.
	If encounters end-of-file puts eof indicator into {@link #queueNextChar}
	and returns -1. Otherwise just returns the character.
	@return -1 or 0...0xFFFF
	@throws IOException if failed.
	*/
	private int read()throws IOException
	{
		int r = in.read();
		assert((r>=-1)&&(r<=0xFFFF));
		if (r==-1)
		{
			queueNextChar(-1,null);
		};
		return r;
	};
	/** Reads data from {@link #in}.
	If encounters end-of-file throws {@link EUnexpectedEof}
	 Otherwise just returns the character.
	@return 0...0xFFFF
	@throws IOException if failed.
	@throws EUnexpectedEof if reached end-of-file.
	*/
	private char readAlways()throws IOException, EUnexpectedEof
	{
		int r = in.read();
		assert((r>=-1)&&(r<=0xFFFF));
		if (r==-1)
		{
			throw new EUnexpectedEof();
		};
		return (char)r;
	};
	
	/* ****************************************************************
	
				AStructBaseFormat
	
	
	*****************************************************************/
	@Override protected void closeImpl()throws IOException
	{
		if (TRACE) TOUT.println("closeImpl() ENTER");
		in.close();
		if (TRACE) TOUT.println("closeImpl() LEAVE");
	};
};