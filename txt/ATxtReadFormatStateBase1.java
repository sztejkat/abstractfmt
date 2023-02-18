package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.CBoundStack;
import sztejkat.abstractfmt.utils.SStringUtils;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
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
	/** Returns reader bound with this state machine.
	All reads and un-reads should get directly through it without
	any additional buffering or state machine may get confused. 
	@return reader passed in constructor or wrapped there, non null */
	public final CAdaptivePushBackReader in(){ return in; };
	
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