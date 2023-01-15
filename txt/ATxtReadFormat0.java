package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	A bottom most layer of text based formats providing transformation
	of tokens into primitive data, reading end.
	
	<h1>Tokens</h1>
	This class assumes, that the text between signals can be divided 
	into characters and "tokens".
	
	<h2>Elementary primitives except char</h2>
	When this class is processing elementary primitives except character
	it always buffers the entire "token" and makes an attempt to parse 
	it the best way it can do it.
	
	<h2>Elementary char or block chars and strings</h2>
	When this class is processing chars or strings it always requests
	a single token character.
	
	<h1>Token classes</h1>
	Altough this is not directly used in this class it assumes, that You 
	may have two classes of tokens:
	<ul>
		<li>plain tokens, for numeric values;</li>
		<li>string tokens for texts;</li>
	</ul>
	For an example two plain tokens followed by two string tokens may look like:
	<pre>
		1.234 3453 "mortimer" "backend"
	</pre>
	Even tough this class does not make any distinction between them one should 
	consider following examples:
	
	<h2>Example 1</h2>
	<pre>
		"1.234 455.55"
	</pre>
	This is a single token and when requested to be processed by <code>readDouble</code>
	the syntax error will be thrown.
	
	<h2>Example 2</h2>
	<pre>
		3 " pieces of " "bread"
	</pre>
	These are three tokens, but when processed by block string or character routines
	the produced string will be "3 pieces of bread".
	
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
	protected ATxtReadFormat0(int name_registry_capacity,int token_size_limit)
	{
		super(name_registry_capacity);
		assert(token_size_limit>0);
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
				and return either return 0...0xFFFF or {@link #TOKEN_BOUNDARY} when found token is
				empty (contains zero characters);
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
	/* --------------------------------------------------------------------------
				Elementary primitive values.
	--------------------------------------------------------------------------*/
	/** Collects elementary token into a shared {@link #token_completion_buffer}
	and returns it.
	<p>
	The collection is limited in size to {@link #token_size_limit}.
	<p>
	This method should be NOT used to collect string tokens, even tough it will
	correctly collect string tokens shorter than token buffer length.
	
	@return {@link #token_completion_buffer}.
	@throws IOException if low level failed.
	@throws EEof if end-of-file was reached before collecting anything
	@throws ENoMoreData if signal was reached before collecting anything
	@throws EFormatBoundaryExceeded if did not finish collecting token before the
			size limit was reached.
	*/
	protected final StringBuilder collectElementaryToken()throws IOException
	{
		//wipe token buffer.
		token_completion_buffer.setLength(0);
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
							//test if throw or just terminate collection
							if (token_completion_buffer.length()==0) throw new ENoMoreData();
							break collection_loop;
				case TOKEN_EOF:
							//test if throw or just terminate collection
							if (token_completion_buffer.length()==0) throw new EUnexpectedEof();
							break collection_loop;
				case TOKEN_BOUNDARY:
							break collection_loop;
				default:
							//plain chars collection, bound
							if (token_completion_buffer.length()>=token_size_limit)
								throw new EFormatBoundaryExceeded("Token \""+token_completion_buffer+"...\" too long in current context processing");
							token_completion_buffer.append((char) c);
			}
		};
		return token_completion_buffer;
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
	@Override protected boolean readBooleanImpl()throws IOException
	{
		StringBuilder b = collectElementaryToken(); //this will throw on Eof/ENoMoreData if nothing is collected.
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
		StringBuilder b = collectElementaryToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
				//handle decoding
				byte bv = Byte.decode(sb);	//this is sad that boxing has to take place due to lack of "byte decode()"
				return bv;
			}catch(NumberFormatException exb)
			{
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
		StringBuilder b = collectElementaryToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
				//handle decoding
				short bv = Short.decode(sb);	//this is sad that boxing has to take place due to lack of "byte decode()"
				return bv;
			}catch(NumberFormatException exb)
			{
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
		Returns next character in token. If token is terminated moves to next token.
	*/	
	@Override protected char readCharImpl()throws IOException
	{
		for(;;)
		{
			int c= tokenIn();
			assert( (c>=TOKEN_BOUNDARY) && (c<=0xFFFF) );
			switch(c)
			{
					case TOKEN_SIGNAL: throw new ENoMoreData();
					case TOKEN_EOF: throw new EUnexpectedEof();
					case TOKEN_BOUNDARY: continue;
					default: return (char)c;
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
					by {@link Integer#decode};</li>
					<li>collected token is not empty and can be converted to floating
					point number by {@link Double#parseDouble};</li>
				</ul>
	*/	
	@Override protected int readIntImpl()throws IOException
	{
		StringBuilder b = collectElementaryToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
				//handle decoding
				int bv = Integer.decode(sb);	//this is sad that boxing has to take place due to lack of "byte decode()"
				return bv;
			}catch(NumberFormatException exb)
			{
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
		StringBuilder b = collectElementaryToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
				//handle decoding
				long bv = Long.decode(sb);	//this is sad that boxing has to take place due to lack of "byte decode()"
				return bv;
			}catch(NumberFormatException exb)
			{
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
				</ul>
	*/	
	@Override protected float readFloatImpl()throws IOException
	{
		StringBuilder b = collectElementaryToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
					float v = Float.parseFloat(sb);
					return (long)v;
			}catch(NumberFormatException ex)
			{
				throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value", ex);
			}
	};
	
	/**  
		Processes elementary token as a short value.
		<p>
		See notes at {@link #readByteImpl}
		@return	<ul>
					<li>zero if collected token is empty;</li>
					<li>collected token is not empty and can be converted to doubleing
					point number by {@link Double#parseDouble};</li>
				</ul>
	*/	
	@Override protected double readDoubleImpl()throws IOException
	{
		StringBuilder b = collectElementaryToken(); //this will throw on Eof if nothing is collected.
		//Detect special texts
		if (b.length()==0) return (byte)0;
		final String sb = b.toString();				
		try{
					double v = Double.parseDouble(sb);
					return (long)v;
			}catch(NumberFormatException ex)
			{
				throw new EBrokenFormat("Cannot parse \""+b+"\" to numeric value", ex);
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
		//The block and elementary processing differs of 
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (cnt==0) throw new EUnexpectedEof();
							return cnt==0 ? -1 : 0;
				case TOKEN_SIGNAL:
							return cnt==0 ? -1 : 0;
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain boolean value.	
							buffer[offset++] = readBooleanImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		return cnt;
	};
		
	@Override protected boolean readBooleanBlockImpl()throws IOException,ENoMoreData
	{	
		return readBooleanImpl();
	};
		
	
	@SuppressWarnings("fallthrough")
	@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
	{
		//The block and elementary processing differs of 
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (cnt==0) throw new EUnexpectedEof();
							return cnt==0 ? -1 : 0;
				case TOKEN_SIGNAL:
							return cnt==0 ? -1 : 0;
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain byte value.	
							buffer[offset++] = readByteImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		return cnt;
	};
		
	@Override protected byte readByteBlockImpl()throws IOException,ENoMoreData
	{	
		return readByteImpl();
	};
	
	
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException
	{
		//The block and elementary processing differs of 
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (cnt==0) throw new EUnexpectedEof();
							return cnt==0 ? -1 : 0;
				case TOKEN_SIGNAL:
							return cnt==0 ? -1 : 0;
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain short value.	
							buffer[offset++] = readShortImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		return cnt;
	};
		
	@Override protected short readShortBlockImpl()throws IOException,ENoMoreData
	{	
		return readShortImpl();
	};
	
	
	
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException
	{
		//The block and elementary processing differs of 
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (cnt==0) throw new EUnexpectedEof();
							return cnt==0 ? -1 : 0;
				case TOKEN_SIGNAL:
							return cnt==0 ? -1 : 0;
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain int value.	
							buffer[offset++] = readIntImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		return cnt;
	};
		
	@Override protected int readIntBlockImpl()throws IOException,ENoMoreData
	{	
		return readIntImpl();
	};
	
	
	
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException
	{
		//The block and elementary processing differs of 
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (cnt==0) throw new EUnexpectedEof();
							return cnt==0 ? -1 : 0;
				case TOKEN_SIGNAL:
							return cnt==0 ? -1 : 0;
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain long value.	
							buffer[offset++] = readLongImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		return cnt;
	};
		
	@Override protected long readLongBlockImpl()throws IOException,ENoMoreData
	{	
		return readLongImpl();
	};
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException
	{
		//The block and elementary processing differs of 
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (cnt==0) throw new EUnexpectedEof();
							return cnt==0 ? -1 : 0;
				case TOKEN_SIGNAL:
							return cnt==0 ? -1 : 0;
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain float value.	
							buffer[offset++] = readFloatImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		return cnt;
	};
		
	@Override protected float readFloatBlockImpl()throws IOException,ENoMoreData
	{	
		return readFloatImpl();
	};
	
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException
	{
		//The block and elementary processing differs of 
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (cnt==0) throw new EUnexpectedEof();
							return cnt==0 ? -1 : 0;
				case TOKEN_SIGNAL:
							return cnt==0 ? -1 : 0;
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain double value.	
							buffer[offset++] = readDoubleImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		return cnt;
	};
		
	@Override protected double readDoubleBlockImpl()throws IOException,ENoMoreData
	{	
		return readDoubleImpl();
	};
		
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException
	{
		//The block and elementary processing differs of 
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (cnt==0) throw new EUnexpectedEof();
							return cnt==0 ? -1 : 0;
				case TOKEN_SIGNAL:
							return cnt==0 ? -1 : 0;
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain char value.	
							buffer[offset++] = readCharImpl();	//now it must not throw since we handled it.
							cnt++;
			}
		};
		return cnt;
	};
		
	@Override protected char readCharBlockImpl()throws IOException,ENoMoreData
	{	
		return readCharImpl();
	};
	
	
	
	@SuppressWarnings("fallthrough")
	@Override protected int readStringImpl(Appendable characters, int length)throws IOException
	{
		//The block and elementary processing differs of 
		int cnt = 0;
		loop:
		while(length-->0)
		{
			switch(hasUnreadToken())
			{
				case TOKEN_EOF:
							if (cnt==0) throw new EUnexpectedEof();
							return cnt==0 ? -1 : 0;
				case TOKEN_SIGNAL:
							return cnt==0 ? -1 : 0;
				case TOKEN_BOUNDARY:
							//boundary will be undestood as an "empty" and parsed correctly
							//fallthrough.
				default:
							//In both cases we do process the plain char value.	
							characters.append(readCharImpl());	//now it must not throw since we handled it.
							cnt++;
			}
		};
		return cnt;
	};
	@Override protected char readStringImpl()throws IOException,ENoMoreData
	{
		return readCharImpl();
	};	
};		