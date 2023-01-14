package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.Reader;
import java.io.IOException;

/**
	A bottom most layer of text based formats.
	
	<h1>Leveled processing</h1>
	This class assumes that the processing is done by following queue:
	<pre>
			binary stream
				&darr;
		<i>binary stream to text, aka InputStreamReader</i>
				&darr;
			character stream 
				&darr;
		<i>special characters decoding, as XML entities</i>
				&darr;
		   content chars stream   &rarr; {@link #rawInImpl}
		    	&darr;
		<i>separation of stream to "signal boundaries"</i>
		    	&darr;
		   payload char stream &rarr; {@link #in}
		   		&darr;
		<i>separation of stream to "token boundaries"</i>
		    	&darr;		   
		   token char stream &rarr; {@link #tokenIn}
		    	&darr;
		<i>content parsing</i>
	</pre>
	
	<h2>Payload tokens</h2>
	This class assumes that payload carried between signals can be split to 
	"payload tokens", where each payload token represents boundaries between
	each primitive element.
	<p>
	Basically the process of decoding of any primitive element starts at
	first "payload token" character and stops at first "payload token boundary"
	character or at "signal boundary".
	
	<h3>Types of tokens</h3>
	This class assumes it is using two styles of tokens:
	<ul>
		<li>plain;</li>
		<li>string;</li>
	</ul>
	The only difference is that the set of token boundary characters is different
	in both tokens styles and that there is a special "start string token boundary"
	which tells when to switch from plain to string token collection mode.
	
	
*/
public abstract class ATxtReadFormat0 extends ARegisteringStructReadFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ATxtReadFormat0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ATxtReadFormat0.",ATxtReadFormat0.class) : null;
         /** Size increment of read-back buffer and an initial size of it */
         private static final int READ_BACK_BUFFER_SIZE_INCREMENT = 8;	//Note: we do not need much
         
         /** Constant returned from {@link #in}/{@link #hasUnreadPayload}
         to indicate that signal boundary is reached */
         protected static final int SIGNAL_BOUNDARY = -1;
         /** Constant returned from {@link #in}/{@link #hasUnreadPayload}
         to indicate that stream boundary is reached */
         protected static final int EOF_BOUNDARY = -2;
         /** Constant returned from {@link #tokenIn}
         to indicate that token boundary is reached.
         The processing of token boundaries do depend
         on token mode: {@link #TOKEN_MODE_PLAIN} or {@link #TOKEN_MODE_STRING}*/
         protected static final int TOKEN_BOUNDARY = -3;
         /** Constant returned from {@link #tokenIn}
         to indicate that starting string token boundary is reached.
         Usually the caller should then switch to {@link #TOKEN_MODE_STRING}.
         <p>
         This value can be returned only in {@link #TOKEN_MODE_PLAIN}.
         */
         protected static final int STARTING_STRING_TOKEN_BOUNDARY = -4;
         
         protected static final boolean TOKEN_MODE_PLAIN = false;
         protected static final boolean TOKEN_MODE_STRING = true;
         
         	/** A buffer, auto sized, used for processing the {@link #rawUnread}
         	capabilities. Non-null. This buffer only gorws and is grown in {@link #READ_BACK_BUFFER_SIZE_INCREMENT}
         	increments.
         	@see #rawIn
         	@see #rawUnread
         	*/
         	private char [] read_back_buffer = new char[READ_BACK_BUFFER_SIZE_INCREMENT];
         	/** An index in {@link #read_back_buffer} at which we should read next un-read character */
         	private int read_back_buffer_wr_ptr;
         	/** An index in {@link #read_back_buffer} at which we should write next un-read character.
         	If <code>{@link #read_back_buffer_rd_ptr}=={@link #read_back_buffer_wr_ptr}</code> the read-back
         	buffer is empty*/
         	private int read_back_buffer_rd_ptr;
         	/** A buffer in which we do complete tokens */
         	private final StringBuilder token_completion_buffer;
         	/** A size limit for token completion */
         	private final int token_size_limit;
			  
    /* ***************************************************************************
	
			Construction
	
	
	*****************************************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
	@param token_size_limit non-zero positive, a maximum number of characters which constitutes 
			a primitive element token, excluding string tokens. Basically a maximum
			number of characters which do constitute a primitive numeric value.
	*/
	public ATxtReadFormat0(int name_registry_capacity, int token_size_limit)
	{
		super(name_registry_capacity);
		assert(token_size_limit>0);
		this.token_size_limit= token_size_limit;
		this.token_completion_buffer = new StringBuilder(token_size_limit);
	};    
	/* **************************************************************************
	
			Services required from subclases.
	
	
	* ***************************************************************************/
	/* -------------------------------------------------------------------------
					low level I/O
	------------------------------------------------------------------------- */
	/** Reads raw character from a raw stream. This method should correctly 
	perform all reading and decoding necessary to provide us with Unicode characters,
	but no state dependet decoding should take place. 
	<p>
	For plain text streams noting except binary decoding is necessary.
	<p>
	For text streams with support for non-encodable characters or special characters
	(ie. \x syntax in properties file or &amp;amp; syntax in XML) that syntax should
	be decoded by this function.
	<p>
	If text format supports comments or escaping special charactes which are not
	allowed in <u>some</u> places, then this method <u>must not</u> take it in an account.
	
	@return <ul>
				<li>-1 ({@link #SIGNAL_BOUNDARY}) if reached a signal. This method stays in this state till
				{@link #next} is processed;</li>
				<li>-2 ({@link #EOF_BOUNDARY})  if end of file was reached;</li> 
				<li>0...0xFFFF if character is returned;</li>
			</ul>
	@throws IOException if low level failed
	@throws EBrokenFormat if decoding of special characters failed.
	
	@see #rawIn
	*/
	protected abstract int rawInImpl()throws IOException;
	/* -------------------------------------------------------------------------
					mid-level I/O, payload splitting.
	------------------------------------------------------------------------- */
	/** This method is expected to use {@link #rawIn}/{@link #rawUnread} to fetch
	next <u>meanfull</u> character in signal payload. 
	<p>
	This method is expected to process skipping comments and decoding special characters
	and token boundary detecting.
	<p>
	No other dedicated processing is necessary.
	@return <ul>
				<li>-1 ({@link #SIGNAL_BOUNDARY}) if reached a signal. This method stays in this state till
				{@link #next} is processed;</li>
				<li>-2 ({@link #EOF_BOUNDARY})  if end of file was reached;</li> 
				<li>0...0xFFFF if character is returned;</li>
			</ul>
	@throws IOException if low level failed
	@throws EBrokenFormat if found a structural problem.
	*/
	protected abstract int in()throws IOException;	
	/** Tests what is next in stream available through {@link #in}
	@return <ul>
				<li>-1 ({@link #SIGNAL_BOUNDARY}) if {@link #in} will return it;</li>
				<li>-2 ({@link #EOF_BOUNDARY})  if {@link #in}  will return it;</li>
				<li>0 if {@link #in} will return character;</li>
			</ul>
	@throws IOException if failed
	@throws EBrokenFormat if found structural problem.
	*/
	protected abstract int hasUnreadPayload()throws IOException;
	/* -------------------------------------------------------------------------
					syntax level, token splitting
	------------------------------------------------------------------------- */
	/** 
		This method is expected to use {@link #in} to fetch next character
		and classify it according to its meaning.
		@param token_mode {@link #TOKEN_MODE_PLAIN} or {@link #TOKEN_MODE_STRING}.
				In string mode this method basically looks only for the end-of-string
				indicator. In plain mode it looks for start of string indicator and
				plain token separators.
		@return <ul>
				<li>-1 ({@link #SIGNAL_BOUNDARY}) if {@link #in} returned it;</li>
				<li>-2 ({@link #EOF_BOUNDARY}) if {@link #in} returned it;</li>
				<li>-3 ({@link #TOKEN_BOUNDARY}) if the token boundary character
				was read. This character is consumed and no longer available;</li>
				<li>-3 ({@link #STARTING_STRING_TOKEN_BOUNDARY}) if the token boundary character
				was read and this boundary character indicates the start of string token.
				This character is consumed and no longer available.
				<br>
				This value can be returned only if <code>token_mode=={@link #TOKEN_MODE_PLAIN}</code>;</li>
				<li>0...0xFFFF if token body character is returned;</li>
			</ul>
		@throws IOException if failed
		@throws EBrokenFormat if found a structural problem.
	*/
	protected abstract int tokenIn(boolean token_mode)throws IOException;
	
	/* **************************************************************************
	
			low level reading
	
	* ***************************************************************************/
	/** This method either calls {@link #rawInImpl} or returns characters which
	were put back by {@link #rawUnread} method.
	@return as {@link #rawInImpl}
	@throws IOException if low level failed
	@throws EBrokenFormat if decoding of special characters failed.
	*/
	protected final int rawIn()throws IOException
	{
		if (read_back_buffer_wr_ptr==read_back_buffer_rd_ptr)
		{
			return rawInImpl();
		}else
		{
			int r = read_back_buffer[read_back_buffer_rd_ptr++];
			if (read_back_buffer_rd_ptr==read_back_buffer_wr_ptr)
			{
				//no more data, reset buffer to be used from a beginning
				read_back_buffer_rd_ptr = 0;
				read_back_buffer_wr_ptr = 0;
			};
			return r;
		}
	};
	/** Un-reads character into a buffer which {@link #rawIn} will use.
	Charactes will be returned by {@link #rawIn} in the order in which
	they have been un-read. 
	@param c what to un-read
	*/
	protected final void rawUnread(char c)
	{
		//quick fetch to locals to avoid fields touching
		int p = read_back_buffer_wr_ptr;	
		char [] rdbuff = read_back_buffer;
		{
			final int oL =  rdbuff.length;
			if (p==oL)
			{
				//re-allocate
				char [] new_read_back_buffer = new char[oL+READ_BACK_BUFFER_SIZE_INCREMENT];
				System.arraycopy(rdbuff,0,new_read_back_buffer,0,oL);
				this.read_back_buffer = rdbuff=  new_read_back_buffer;
			};
		};
		rdbuff[p++]=c;
		this.read_back_buffer_wr_ptr = p;
	};
	
	/* **********************************************************************************
	
			AStructReadFormatBase0
			
			
				Notes:
				
					Since 99% of JAVA numeric processing takes in String and refuses Reader
					interface we have to first buffer token into String and then process it.
					
					We could, theoretically, provide incremental decoders which could be
					feed with characters, but it will be a hell lot of probably hard and
					unnecessary work.
	
					The only case we won't do it will be native string and char arrays 
					processing.
	
	***********************************************************************************/
	/** Collects elementary token into a shared {@link #token_completion_buffer}
	and returns it.
	<p>
	The collection is limited in size to {@link #token_size_limit}.
	<p>
	This method should be NOT used to collect string tokens, even tough it will
	correctly collect string tokens shorter than token buffer length.
	
	@return {@link #token_completion_buffer} or null if failed to collect anything.
			Notice empty string can be returned if collected <code>""</code>
	@throws IOException if low level failed.
	@throws EEof if end-of-file was reached before collecting anything
	@throws EFormatBoundaryExceeded if did not finish collecting token before the
			size limit was reached.
	*/
	protected final StringBuilder collectElementaryToken()throws IOException
	{
		//wipe token buffer.
		token_completion_buffer.setLength(0);
		//switch to plain collection
		boolean token_mode = TOKEN_MODE_PLAIN;
		boolean collected = false;	//to properly handle empty strings.
		collection_loop:
		for(;;)
		{
			//Collect
			int c = tokenIn(token_mode);
			assert( (c>=TOKEN_BOUNDARY) && (c<=0xFFFF) );
					
			switch(c)
			{
				case SIGNAL_BOUNDARY: break;
				case EOF_BOUNDARY:
							//test if throw or just terminate collection
							if (token_completion_buffer.length()==0) throw new EUnexpectedEof();
							break collection_loop;
				case TOKEN_BOUNDARY:
							//This either initializes collection or terminates.
							if (token_completion_buffer.length()!=0) break collection_loop;
							break;
				case STARTING_STRING_TOKEN_BOUNDARY:
							//this switches collection mode.
							assert( token_mode==TOKEN_MODE_PLAIN ); //<- can't be now.
							token_mode = TOKEN_MODE_STRING;
							collected = true; 	//strings are always collected
							break;
				default:
							//plain chars collection, bound
							if (token_completion_buffer.length()>=token_size_limit)
								throw new EFormatBoundaryExceeded("Token \""+token_completion_buffer+"...\" too long in current context processing");
							token_completion_buffer.append((char) c);
							collected = true;
			}
		};
		//return it depending on collected length
		return collected ? token_completion_buffer : null;
	};
	/** Compares content of string buffer, case insensitive, with specified text. 
	@param buffer non-null buffer to compare with <code>text</code>
	@param text text to compare
	@return true if identical
	*/
	protected static boolean equalsCaseInsensitive(StringBuilder buffer, String text)
	{
		if (buffer.length()!=text.length()) return false;
		for(int i=0,n=buffer.length(); i<n; i++)
		{
			char bc = buffer.charAt(i);
			char tc = text.charAt(i);
			//Since to-lower/upper may be inconsistent in some langs avoid them
			if (
				(Character.isLowerCase(bc) && Character.isLowerCase(tc))
					||
				(Character.isUpperCase(bc) && Character.isUpperCase(tc))
			   )
			   {
			   	   	//compare directly
			   	   	if (bc!=tc) return false;
			   }else
			   {
			   	   //compare lower case
			   	   if (Character.toLowerCase(bc)!=Character.toLowerCase(tc)) return false;
			   };
		};
		return true;
	};
	/** Compares content of string buffer, case ssnsitive, with specified text. 
	@param buffer non-null buffer to compare with <code>text</code>
	@param text text to compare
	@return true if identical
	*/
	protected static boolean equalsCaseSensitive(StringBuilder buffer, String text)
	{
		if (buffer.length()!=text.length()) return false;
		for(int i=0,n=buffer.length(); i<n; i++)
		{
			char bc = buffer.charAt(i);
			char tc = text.charAt(i);
			if (bc!=tc) return false;
		};
		return true;
	};
	/* --------------------------------------------------------------------------------
				Primitive related, elementary	
	--------------------------------------------------------------------------------*/
	/**  
		Collects elementary token.
		@return	<ul>
					<li>true if: 
						<ul>
							<li>collected token represents "true", case insensitive;</li>
							<li>collected token represents "1";</li>
							<li>collected token is not empty and can be converted to floating
							point number which would not return true if compared with zero;</li>
						</ul>
					</li>
					<li>false if: 
						<ul>
							<li>collected token is empty;</li>
							<li>collected token represents "false", case insensitive;</li>
							<li>collected token represents "0";</li>
							<li>collected token is not empty and can be converted to floating
							point number which would return true if compared with zero;</li>
						</ul>
					</li>
				</ul>
				The numeric conversion is done by {@link Double#parseDouble}.
	*/
	protected boolean readBooleanImpl()throws IOException
	{
		StringBuilder b = collectElementaryToken(); //this will throw on Eof if nothing is collected.
		if (b==null) throw new ENoMoreData();	
		//Detect special texts
		if (b.length()==0) return false;
		if (equalsCaseInsensitive(b,"true")) return true;
		if (equalsCaseInsensitive(b,"1")) return true;
		if (equalsCaseInsensitive(b,"false")) return false;
		if (equalsCaseInsensitive(b,"0")) return false;
		try{
				double v = Double.parseDouble(b.toString());
				//Now below comparison is trickier than it looks, due to Inf/Nan
				return v==0 ? false : true;
		}catch(NumberFormatException ex)
		{
			throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value", ex);
		}
	};
		
};         	
