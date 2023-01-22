package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	A bottom most layer of text based formats providing transformation
	of tokens into primitive data, reading end.
	
	<h1>Parsing rules</h1>
	This class is responsible only for parsing a content between signals.
	This is done by using {@link #tokenIn} and {@link #hasUnreadToken}.
	<p>
	This class assumes that the content between signals can be split 
	into "tokens".
	
	
	<h2>Tokens</h2>
	The "token" is made of all characters returned by {@link #tokenIn}
	which are regular characters (not <code>TOKEN_xxx</code>).
	Especially encountering {@link #TOKEN_BOUNDARY} control character
	always indicates termination of a token even if there was no regular
	character and form a token of zero characters in length.
	
	
	<h2>Parsing tokens</h2>
	Tokens may be either fully parsed or partially parsed. 
	
	<h2>Numeric and boolean parsing</h2>
	Numeric and boolean parsing either parses all characters left in a partially
	parsed token or fetches complete next token and parse it.
	<p>
	If the token is empty numeric parsing returns 0 and boolean parsing returns "false".
	<p>
	The maximum size of numeric tokens is limited and set in constructor.
	
	<h3>Boolean parsing</h3>
	Boolean parsing recognizes either "false" or "true" (case insensitive) tokens,
	"0" or "1" or any value which can be parsed to numeric floating point or numeric
	integer and compared with zero. Zero numeric value gives "false", non zero gives "true".
	
	<h3>Numeric parsing</h3>
	Integer numeric parsing makes an attempt to convert number to java Number instance of
	apropriate type by the <code>XXX.decode</code> method and if it fails by
	<code>Double.parseDouble</code> method.
	<p>
	Floating point numeric parsing uses <code>Integer.decode</code> as a fallback
	if floating point parsing fails.
	
	
	<h2>Character parsing</h2>
	Character parsing is always processing tokens partially, character per-character.
	If it encounters empty token, the character <code>(char)0</code> is returned.
	<p>
	If the token is fully consumed it reads next token and starts parsing it.
	
	
	<h1>Sequences</h1>
	Sequences are parsed as sequences of elementary values and are using exactly the
	same rules as above.
	<p>
	There is no indicator which can be used to tell apart an element of sequence from
	a single primitive element. 
	
*/
public abstract class ATxtReadFormat0 extends ARegisteringStructReadFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(ATxtReadFormat0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("ATxtReadFormat0.",ATxtReadFormat0.class) : null;
         
         /** Returned by {@link #tokenIn} to indicate that signal is reached */
         protected static final int  TOKEN_SIGNAL = -1;
         /** Returned by {@link #tokenIn} to indicate that physical end of file is reached */
         protected static final int  TOKEN_EOF = -2;
         /** Returned by {@link #tokenIn} to indicate that end of token is reached. */
         protected static final int  TOKEN_BOUNDARY = -3;
         
         
         	/** A buffer in which we do complete tokens
         	during "token mode".
         	*/
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
	@throws AssertionError if token_size_limit is less than 16+3 which is a minimum
			number to hold hex encoded long value.
	*/
	protected ATxtReadFormat0(int name_registry_capacity,int token_size_limit)
	{
		super(name_registry_capacity);
		if (TRACE) TOUT.println("new ATxtReadFormat0(name_registry_capacity="+name_registry_capacity+",token_size_limit="+token_size_limit+")");
		assert(token_size_limit>0);
		assert(token_size_limit>=16+3):"token_size_limit="+token_size_limit+" not enough for signed hex long int"; 
		this.token_size_limit= token_size_limit;
		this.token_completion_buffer = new StringBuilder(token_size_limit);
	};    
	/* **************************************************************************
	
			Services required from subclases.
	
	
	* ***************************************************************************/
	/* --------------------------------------------------------------------------
				Token processing		
	--------------------------------------------------------------------------*/
	/** Should return next character in token. Specifically it should return:
	@return <ul>
				<li>0...0xFFFF - when token character is read;</li>
				<li>{@link #TOKEN_SIGNAL} - when signal is found. Subsequent calls
				to this method should continue returning {@link #TOKEN_SIGNAL} until
				{@link #next} is called;</li>
				<li>{@link #TOKEN_EOF} - when physical end of file is reached. Subsequent
				calls to this method should make an attempt to read more data;</li>
				<li>{@link #TOKEN_BOUNDARY} - when end of token is reached. Subsequent 
				calls to this method should look for a start of next token
				and return either return 0...0xFFFF or {@link #TOKEN_BOUNDARY} 
				when found token is	empty (contains zero characters);
				</li>
			</ul>
	@throws IOException if failed by any means.
	*/
	protected abstract int tokenIn()throws IOException;
	/** Tests how {@link #tokenIn} would behave 
	@return <ul>
				<li>0 - if {@link #tokenIn} would return 0...0xFFFF;</li>
				<li>{@link #TOKEN_SIGNAL} - when {@link #tokenIn} would return it</li>
				<li>{@link #TOKEN_EOF} - when {@link #tokenIn} would return it</li>
				<li>{@link #TOKEN_BOUNDARY}- when {@link #tokenIn} would return it</li>
			</ul>
	@throws IOException if failed to determine it
	*/
	protected abstract int hasUnreadToken()throws IOException;
	/* **************************************************************************
	
			AStructReadFormatBase0
	
	
	* ***************************************************************************/
	/* ------------------------------------------------------------------
					Signal related
	------------------------------------------------------------------*/
	@Override protected boolean hasElementaryDataImpl()throws IOException
	{
		if (TRACE) TOUT.println("hasElementaryDataImpl ENTER");
		
		switch(hasUnreadToken())
		{
			case 0:
							if (TRACE) TOUT.println("hasElementaryDataImpl=true, token LEAVE");
							return true;
			case TOKEN_BOUNDARY:
							//Boundary seems to a bit tricky.
							//However the rest of class is defined in such a way
							//that when boundary will be returned then for sure at least
							//an empty token is present and such an empty token
							//is a parasable value (0 char or 0 numeric).
							if (TRACE) TOUT.println("hasElementaryDataImpl=true, boundary LEAVE");
							return true;
			case TOKEN_SIGNAL:
							if (TRACE) TOUT.println("hasElementaryDataImpl=false, signal LEAVE");
							return false;
			case TOKEN_EOF:			
							if (TRACE) TOUT.println("hasElementaryDataImpl=false, eof LEAVE");
							return false;
			default: throw new AssertionError();
		}
	};
	/* ------------------------------------------------------------------------
			Tokens collection mode.
	------------------------------------------------------------------------*/
	/** Collects character from an elementary toke using "character mode".
	<p>
	This method collects next token character till eof or signal or boundary is returned
	from {@link #tokenIn}. 
	<p>
	If boundary is found it returns 0 what is consistent with {@link #collectToken}
	behavior. Otherwise returns a character.
	<p>
	If the next character is boundary it is consumed, what is consistent with
	{@link #collectToken} behaviour.
	
	@return collected character 0...0xFFFF or -1 if found end-of-file
	@throws ENoMoreData if found a signal
	@throws IOException if failed at low level.
	*/
	protected final int collectCharacter()throws IOException
	{
		if (TRACE) TOUT.println("collectCharacter ENTER");
		for(;;)
		{
			int c= tokenIn();
			assert( (c>=TOKEN_BOUNDARY) && (c<=0xFFFF) );
			switch(c)
			{
					case TOKEN_SIGNAL: throw new ENoMoreData();
					case TOKEN_EOF: return -1;
					case TOKEN_BOUNDARY:
							//Now we have a token boundary. collectCharacter always
							//consumes tailing token boundary and collectToken also does it.
							//So if we are finding it we have an empty token at hand.
							if (TRACE) TOUT.println("collectCharacter=0 due to empty token, LEAVE");
							return (char)0;
					default:
							//We got a character. This character may be followed by 
							//token boundary. If we leave it like that the collectToken will
							//assume, that the token is empty, while it is a fully legitimately
							//consumed token.
							//This will also nicely stitch tokens.
							switch(hasUnreadToken())
							{
								case TOKEN_BOUNDARY:
										if (TRACE) TOUT.println("collectCharacter, consuming trailing boundary");
										tokenIn(); break;
							};
							if (TRACE) TOUT.println("collectCharacter=\'"+(char)c+"\" LEAVE");
							return c;
			}
		}
	};	
	/** Collects elementary token into a shared {@link #token_completion_buffer}
	and returns it. Collection is done using "token mode".
	<p>
	This method do collect all characters till eof, signal or token boundary
	is returned from {@link #tokenIn}. This includes all characters which are not processed
	by {@link #collectCharacter}.
	<p>
	The trailing boundary is always consumed.
	<p>
	The collection is limited in size to {@link #token_size_limit}.
	
	@return {@link #token_completion_buffer}.
	@throws IOException if low level failed.
	@throws EEof if end-of-file was reached before collecting anything
	@throws ENoMoreData if signal was reached before collecting anything
	@throws EFormatBoundaryExceeded if did not finish collecting token before the
			size limit was reached.
	*/
	protected final StringBuilder collectToken()throws IOException
	{
		if (TRACE) TOUT.println("collectToken() ENTER");
		//wipe token buffer.
		token_completion_buffer.setLength(0);		
		//Now we do completion.
		boolean collected = false;	//to properly handle empty strings.
		collection_loop:
		for(;;)
		{
			//Collect
			int c = tokenIn();
			assert( (c>=TOKEN_BOUNDARY) && (c<=0xFFFF) );
					
			switch(c)
			{
				case TOKEN_SIGNAL:
							if (TRACE) TOUT.println("collectToken, got TOKEN_SIGNAL");
							//test if throw or just terminate collection
							if (token_completion_buffer.length()==0) throw new ENoMoreData();
							break collection_loop;
				case TOKEN_EOF:
							if (TRACE) TOUT.println("collectToken, got TOKEN_EOF");
							//test if throw or just terminate collection
							if (token_completion_buffer.length()==0) throw new EUnexpectedEof();
							break collection_loop;
				case TOKEN_BOUNDARY:
							if (TRACE) TOUT.println("collectToken, got terminating TOKEN_BOUNDARY");
							break collection_loop;
				default:
							//plain chars collection, bound
							if (token_completion_buffer.length()>=token_size_limit)
								throw new EFormatBoundaryExceeded("Token \""+token_completion_buffer+"...\" too long in current context processing");
							if (DUMP) TOUT.println("collectToken+=\'"+c+"'(0x"+Integer.toHexString(c)+")");
							token_completion_buffer.append((char) c);
			}
		};
		if (TRACE) TOUT.println("collectToken()=\""+token_completion_buffer+"\" LEAVE");
		return token_completion_buffer;
	};
	
	/* --------------------------------------------------------------------------
				Token comparison
	--------------------------------------------------------------------------*/
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
		Processes elementary token as a boolean value.
		@return	<ul>
					<li>true if: 
						<ul>
							<li>collected token represents "true", case insensitive;</li>
							<li>collected token represents "1";</li>
							<li>collected token is not empty and can be converted to floating
							point or integer number which would not return true if compared with zero;</li>
						</ul>
					</li>
					<li>false if: 
						<ul>
							<li>collected token is empty;</li>
							<li>collected token represents "false", case insensitive;</li>
							<li>collected token represents "0";</li>
							<li>collected token is not empty and can be converted to floating
							point number or integer which would return true if compared with zero;</li>
						</ul>
					</li>
				</ul>
				The numeric conversion is done by {@link Double#parseDouble}.
	*/
	@Override protected boolean readBooleanImpl()throws IOException
	{
		if (TRACE) TOUT.println("readBooleanImpl->");
		StringBuilder b = collectToken(); //this will throw on Eof/ENoMoreData if nothing is collected.
		//Detect special texts
		if (b.length()==0) return false;
		if (equalsCaseInsensitive(b,"true")) return true;
		if (equalsCaseInsensitive(b,"1")) return true;
		if (equalsCaseInsensitive(b,"false")) return false;
		if (equalsCaseInsensitive(b,"0")) return false;
		String sb = b.toString();
		try{
				//Now Double parses hex string is a limitted manner.
				//it understands only full form 0x00.00p0
				//and refules others.
				double v = Double.parseDouble(sb);
				//Now below comparison is trickier than it looks, due to Inf/Nan
				return v==0 ? false : true;
		}catch(NumberFormatException ex)
		{
			if (TRACE) TOUT.println("readBooleanImpl, failed double parse");
			try{
					long v = Integer.decode(sb);
					return v==0 ? false : true;
			}catch(NumberFormatException ex2)
			{
				throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value\n"+ex+"\n"+ex2, ex2);
			}
		}
	};
	/**  
		Processes elementary token as a byte value.
		<p>
		This method peforms "trimming" down-conversion to byte value
		which is in conformance with standard  saying it can fail misserably
		and without detecting things. Use typed stream if You don't like it.
		@return	<ul>
					<li>zero if collected token is empty;</li>
					<li>collected token is not empty and can be converted to byte number
					by {@link Byte#decode};</li>
					<li>collected token is not empty and can be converted to floating
					point number by {@link Double#parseDouble};</li>
				</ul>
	*/
	@Override protected byte readByteImpl()throws IOException
	{
		if (TRACE) TOUT.println("readByteImpl->");
		StringBuilder b = collectToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
				//handle decoding
				byte bv = Byte.decode(sb);	//this is sad that boxing has to take place due to lack of "byte decode()"
				return bv;
			}catch(NumberFormatException exb)
			{
				if (TRACE) TOUT.println("readByteImpl, failed byte parse");
				try{
						double v = Double.parseDouble(sb);
						return (byte)v;
				}catch(NumberFormatException ex)
				{
					throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value", ex);
				}
		}
	};
	/**  
		Processes elementary token as a short value.
		<p>
		See notes at {@link #readByteImpl}
		@return	<ul>
					<li>zero if collected token is empty;</li>
					<li>collected token is not empty and can be converted to byte number
					by {@link Short#decode};</li>
					<li>collected token is not empty and can be converted to floating
					point number by {@link Double#parseDouble};</li>
				</ul>
	*/	
	@Override protected short readShortImpl()throws IOException
	{
		if (TRACE) TOUT.println("readShortImpl->");
		StringBuilder b = collectToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
				//handle decoding
				short bv = Short.decode(sb);	//this is sad that boxing has to take place due to lack of "byte decode()"
				return bv;
			}catch(NumberFormatException exb)
			{
				if (TRACE) TOUT.println("readShortImp failed short parse");
				try{
						double v = Double.parseDouble(sb);
						return (short)v;
				}catch(NumberFormatException ex)
				{
					throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value", ex);
				}
		}
	};
	/**  
		Returns next character in token or zero if token is empty.
		Silently moves across token boundaries.
	*/	
	@Override protected char readCharImpl()throws IOException
	{
		if (TRACE) TOUT.println("readCharImpl ENTER");
		int c= collectCharacter();
		assert( (c>=-1) && (c<=0xFFFF) );
		if (c==-1) throw new EUnexpectedEof();
		if (TRACE) TOUT.println("readCharImpl=\'"+(char)c+"\" LEAVE");
		return (char)c;
	};	
	/**  
		Processes elementary token as a short value.
		<p>
		See notes at {@link #readByteImpl}
		@return	<ul>
					<li>zero if collected token is empty;</li>
					<li>collected token is not empty and can be converted to byte number
					by {@link Integer#decode};</li>
					<li>collected token is not empty and can be converted to floating
					point number by {@link Double#parseDouble};</li>
				</ul>
	*/	
	@Override protected int readIntImpl()throws IOException
	{
		if (TRACE) TOUT.println("readIntImpl->");
		StringBuilder b = collectToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
				//handle decoding
				int bv = Integer.decode(sb);	//this is sad that boxing has to take place due to lack of "byte decode()"
				return bv;
			}catch(NumberFormatException exb)
			{
				if (TRACE) TOUT.println("readIntImpl, failed int parse");
				try{
						double v = Double.parseDouble(sb);
						return (int)v;
				}catch(NumberFormatException ex)
				{
					throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value", ex);
				}
		}
	};
	
	/**  
		Processes elementary token as a short value.
		<p>
		See notes at {@link #readByteImpl}
		@return	<ul>
					<li>zero if collected token is empty;</li>
					<li>collected token is not empty and can be converted to byte number
					by {@link Long#decode};</li>
					<li>collected token is not empty and can be converted to floating
					point number by {@link Double#parseDouble};</li>
				</ul>
	*/	
	@Override protected long readLongImpl()throws IOException
	{
		if (TRACE) TOUT.println("readLongImpl->");
		StringBuilder b = collectToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
				//handle decoding
				long bv = Long.decode(sb);	//this is sad that boxing has to take place due to lack of "byte decode()"
				return bv;
			}catch(NumberFormatException exb)
			{
				if (TRACE) TOUT.println("readLongImpl, failed long parse");
				try{
						double v = Double.parseDouble(sb);
						return (long)v;
				}catch(NumberFormatException ex)
				{
					throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value", ex);
				}
		}
	};
	
	
	/**  
		Processes elementary token as a short value.
		<p>
		See notes at {@link #readByteImpl}
		@return	<ul>
					<li>zero if collected token is empty;</li>
					<li>collected token is not empty and can be converted to floating
					point number by {@link Float#parseFloat};</li>
					<li>collected token is not empty and can be converted to integer
					point number by {@link Integer#decode};</li>
				</ul>
	*/	
	@Override protected float readFloatImpl()throws IOException
	{
		if (TRACE) TOUT.println("readFloatImpl->");
		StringBuilder b = collectToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();
		try{
					float v = Float.parseFloat(sb);
					return v;
			}catch(NumberFormatException ex)
			{
				if (TRACE) TOUT.println("readFloatImpl, failed float parse");
				//now try integer
				try{
					int v = Integer.decode(sb);
					return v;
				}catch(NumberFormatException ex2)
				{
					throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value\n"+ex+"\n"+ex2, ex);
				}
			}
	};
	
	/**  
		Processes elementary token as a short value.
		<p>
		See notes at {@link #readByteImpl}
		@return	<ul>
					<li>zero if collected token is empty;</li>
					<li>collected token is not empty and can be converted to double floating
					point number by {@link Double#parseDouble};</li>
					<li>collected token is not empty and can be converted to integer
					point number by {@link Integer#decode};</li>
				</ul>
	*/	
	@Override protected double readDoubleImpl()throws IOException
	{
		if (TRACE) TOUT.println("readDoubleImpl->");
		StringBuilder b = collectToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
					double v = Double.parseDouble(sb);
					return v;
			}catch(NumberFormatException ex)
			{
				if (TRACE) TOUT.println("readDoubleImpl, failed double parse");
				//now try integer
				try{
					int v = Integer.decode(sb);
					return v;
				}catch(NumberFormatException ex2)
				{
					throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value\n"+ex+"\n"+ex2, ex);
				}
			}
	};
	
	/* ------------------------------------------------------------------
				Datablock related
				
			All datablock related operations are implemented
			as if elementary primitives.
	------------------------------------------------------------------*/
	@SuppressWarnings("fallthrough")
	@Override protected int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException
	{
		if (TRACE) TOUT.println("readBooleanBlockImpl ENTER");
		//The block and elementary processing differs at signal and eof treatment
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (TRACE) TOUT.println("readBooleanBlockImpl, eof");
							if (cnt==0) throw new EUnexpectedEof();
							if (TRACE) TOUT.println("readBooleanBlockImpl()="+cnt+", eof, LEAVE");
							return cnt;
				case TOKEN_SIGNAL:
							{
							final int r = cnt==0 ? -1 : cnt;
							if (TRACE) TOUT.println("readBooleanBlockImpl()="+r+", signal LEAVE");
							return r;
							}
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain boolean value.	
							buffer[offset++] = readBooleanImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		if (TRACE) TOUT.println("readBooleanBlockImpl()="+cnt+" read all, LEAVE");
		return cnt;
	};
		
	@Override protected boolean readBooleanBlockImpl()throws IOException,ENoMoreData
	{	
		return readBooleanImpl();
	};
		
	
	@SuppressWarnings("fallthrough")
	@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
	{
		if (TRACE) TOUT.println("readByteBlockImpl ENTER");
		//The block and elementary processing differs at signal and eof treatment
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (TRACE) TOUT.println("readByteBlockImpl, eof");
							if (cnt==0) throw new EUnexpectedEof();
							if (TRACE) TOUT.println("readByteBlockImpl()="+cnt+", eof, LEAVE");
							return cnt;
				case TOKEN_SIGNAL:
							{
							final int r = cnt==0 ? -1 : cnt;
							if (TRACE) TOUT.println("readByteBlockImpl()="+r+", signal LEAVE");
							return r;
							}
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain byte value.	
							buffer[offset++] = readByteImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		if (TRACE) TOUT.println("readByteBlockImpl()="+cnt+" read all, LEAVE");
		return cnt;
	};
		
	@Override protected byte readByteBlockImpl()throws IOException,ENoMoreData
	{	
		return readByteImpl();
	};
	
	
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException
	{
		if (TRACE) TOUT.println("readShortBlockImpl ENTER");
		//The block and elementary processing differs at signal and eof treatment
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (TRACE) TOUT.println("readShortBlockImpl, eof");
							if (cnt==0) throw new EUnexpectedEof();
							if (TRACE) TOUT.println("readShortBlockImpl()="+cnt+", eof, LEAVE");							
							return cnt;
				case TOKEN_SIGNAL:
							{
							final int r = cnt==0 ? -1 : cnt;
							if (TRACE) TOUT.println("readShortBlockImpl()="+r+", signal LEAVE");
							return r;
							}
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain short value.	
							buffer[offset++] = readShortImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		if (TRACE) TOUT.println("readShortBlockImpl()="+cnt+" read all, LEAVE");		
		return cnt;
	};
		
	@Override protected short readShortBlockImpl()throws IOException,ENoMoreData
	{	
		return readShortImpl();
	};
	
	
	
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException
	{
		if (TRACE) TOUT.println("readIntBlockImpl ENTER");
		//The block and elementary processing differs at signal and eof treatment
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (TRACE) TOUT.println("readIntBlockImpl, eof");
							if (cnt==0) throw new EUnexpectedEof();
							if (TRACE) TOUT.println("readIntBlockImpl()="+cnt+", eof, LEAVE");
							return cnt;
				case TOKEN_SIGNAL:
							{
							final int r = cnt==0 ? -1 : cnt;
							if (TRACE) TOUT.println("readIntBlockImpl()="+r+", signal LEAVE");
							return r;
							}
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain int value.	
							buffer[offset++] = readIntImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		if (TRACE) TOUT.println("readIntBlockImpl()="+cnt+" read all, LEAVE");
		return cnt;
	};
		
	@Override protected int readIntBlockImpl()throws IOException,ENoMoreData
	{	
		return readIntImpl();
	};
	
	
	
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException
	{
		if (TRACE) TOUT.println("readLongBlockImpl ENTER");
		//The block and elementary processing differs at signal and eof treatment
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (TRACE) TOUT.println("readLongBlockImpl, eof");
							if (cnt==0) throw new EUnexpectedEof();
							if (TRACE) TOUT.println("readLongBlockImpl()="+cnt+", eof, LEAVE");
							return cnt;
				case TOKEN_SIGNAL:
							{
							final int r = cnt==0 ? -1 : cnt;
							if (TRACE) TOUT.println("readLongBlockImpl()="+r+", signal LEAVE");
							return r;
							}
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain long value.	
							buffer[offset++] = readLongImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		if (TRACE) TOUT.println("readLongBlockImpl()="+cnt+" read all, LEAVE");
		return cnt;
	};
		
	@Override protected long readLongBlockImpl()throws IOException,ENoMoreData
	{	
		return readLongImpl();
	};
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException
	{
		if (TRACE) TOUT.println("readFloatBlockImpl ENTER");
		//The block and elementary processing differs at signal and eof treatment
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (TRACE) TOUT.println("readFloatBlockImpl, eof");
							if (cnt==0) throw new EUnexpectedEof();
							if (TRACE) TOUT.println("readFloatBlockImpl()="+cnt+", eof, LEAVE");
							return cnt;
				case TOKEN_SIGNAL:
							{
							final int r = cnt==0 ? -1 : cnt;
							if (TRACE) TOUT.println("readFloatBlockImpl()="+r+", signal LEAVE");
							return r;
							}
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain float value.	
							buffer[offset++] = readFloatImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		if (TRACE) TOUT.println("readFloatBlockImpl()="+cnt+" read all, LEAVE");
		return cnt;
	};
		
	@Override protected float readFloatBlockImpl()throws IOException,ENoMoreData
	{	
		return readFloatImpl();
	};
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException
	{
		if (TRACE) TOUT.println("readDoubleBlockImpl ENTER");
		//The block and elementary processing differs at signal and eof treatment
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (TRACE) TOUT.println("readDoubleBlockImpl, eof");
							if (cnt==0) throw new EUnexpectedEof();
							if (TRACE) TOUT.println("readDoubleBlockImpl()="+cnt+", eof, LEAVE");
							return cnt;
				case TOKEN_SIGNAL:
							{
							final int r = cnt==0 ? -1 : cnt;
							if (TRACE) TOUT.println("readDoubleBlockImpl()="+r+", signal LEAVE");
							return r;
							}
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain double value.	
							buffer[offset++] = readDoubleImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		if (TRACE) TOUT.println("readDoubleBlockImpl()="+cnt+" read all, LEAVE");
		return cnt;
	};
		
	@Override protected double readDoubleBlockImpl()throws IOException,ENoMoreData
	{	
		return readDoubleImpl();
	};
		
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException
	{
		if (TRACE) TOUT.println("readCharBlockImpl ENTER");
		//The block and elementary processing differs at signal and eof treatment
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (TRACE) TOUT.println("readCharBlockImpl, eof");
							if (cnt==0) throw new EUnexpectedEof();
							if (TRACE) TOUT.println("readCharBlockImpl()="+cnt+", eof, LEAVE");
							return cnt;
				case TOKEN_SIGNAL:
							{
							final int r = cnt==0 ? -1 : cnt;
							if (TRACE) TOUT.println("readCharBlockImpl()="+r+", signal LEAVE");
							return r;
							}
				case TOKEN_BOUNDARY:
							//in case of char we need to process it by ourselves,
							//because readCharImpl will crawl through all boundary
							//and throw ENoMoreData.
							if (TRACE) TOUT.println("readCharBlockImpl(), stitching tokens");
							tokenIn();	//consume it.
							continue;
				default:
							//In both cases we do process the plain char value.	
							buffer[offset++] = (char)collectCharacter();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		if (TRACE) TOUT.println("readCharBlockImpl()="+cnt+" read all, LEAVE");
		return cnt;
	};
		
	@Override protected char readCharBlockImpl()throws IOException,ENoMoreData
	{	
		return readCharImpl();
	};
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readStringImpl(Appendable characters, int length)throws IOException
	{
		if (TRACE) TOUT.println("readStringImpl ENTER");
		//The block and elementary processing differs at signal and eof treatment
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (TRACE) TOUT.println("readStringImpl, eof");
							if (cnt==0) throw new EUnexpectedEof();
							if (TRACE) TOUT.println("readStringImpl()="+cnt+", eof, LEAVE");
							return cnt;
				case TOKEN_SIGNAL:
							{
							final int r = cnt==0 ? -1 : cnt;
							if (TRACE) TOUT.println("readStringImpl()="+r+", signal LEAVE");
							return r;
							}
				case TOKEN_BOUNDARY:
							//in case of char we need to process it by ourselves,
							//because readCharImpl will crawl through all boundary
							//and throw ENoMoreData.
							if (TRACE) TOUT.println("readStringImpl(), stitching tokens");
							tokenIn();	//consume it.
							continue;
				default:
							//In both cases we do process the plain char value.	
							characters.append((char)collectCharacter());	//now it must not throw since we handled it.
							cnt++;
			}
		};
		if (TRACE) TOUT.println("readStringImpl()="+cnt+" read all, LEAVE");
		return cnt;
	};
	@Override protected char readStringImpl()throws IOException,ENoMoreData
	{
		return readCharImpl();
	};	
	
	
};		