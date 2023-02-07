package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.AUnescapingEngine;
import sztejkat.abstractfmt.EBrokenFormat;
import java.io.IOException;

/**
	A reverse engine for {@link APlainEscapingEngine}
*/
abstract class APlainUnescapingEngine extends AUnescapingEngine
{
			/** A mode handler, used to handle all characters
			after reconing what type of escape we have at hand.
			<p>
			Notice, this is necessary since not all escapes do have
			a terminating semicolon.
			*/
			private static abstract class AModeHandler
			{
				protected abstract TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException;
				protected abstract int unescape(StringBuilder collection_buffer)throws IOException;
			};
			
			/** Direct single char collection mode */
			private static final AModeHandler MODE_SINGLE_CHAR =
			new AModeHandler()
			{
				protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
				{
					assert(sequence_index!=0);
					//First char is a last char.
					return TEscapeCharType.ESCAPE_LAST_BODY;  
				};
				protected int unescape(StringBuilder collection_buffer)throws IOException
				{
					assert(collection_buffer.length()==1);
					return collection_buffer.charAt(0);
				};
			};
			
			/** Hex collection mode */
			private static final AModeHandler MODE_HEX =
			new AModeHandler()
			{
				protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
				{
					assert(sequence_index!=0);
					if(sequence_index>5) throw new EBrokenFormat("Too long escape sequence");
					//just collect till ; ignoring syntax.
					if (c==';')
						return TEscapeCharType.ESCAPE_LAST_BODY_VOID;
					else
						return TEscapeCharType.ESCAPE_BODY;
				};
				protected int unescape(StringBuilder collection_buffer)throws IOException
				{
					assert(collection_buffer.length()<=4);
					char v = 0;
					for(int i=0,n=collection_buffer.length();i<n;i++)
					{
						final char digit = collection_buffer.charAt(i);
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
							throw new EBrokenFormat("\""+digit+"\" is not hex digit 0...9A...Fa...f");
						v<<=4;
						v|=nibble;
					};
					return v;
				};
			};
			
			private AModeHandler mode_handler = null;
	protected APlainUnescapingEngine(){};	
	/* **********************************************************
	
			Services for AUnescapingEngine
	
	***********************************************************/
	/** Ensures no state is keept */
	@Override public void reset()
	{
		this.mode_handler = null;
		super.reset();
	};
	@SuppressWarnings("fallthrough")
	@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
	{
		switch(sequence_index)
		{
			case 0:
				this.mode_handler = null;
				//initial recognition
				if (c=='\\')
					return TEscapeCharType.ESCAPE_BODY_VOID;	//start, but not collect it.
				else
					return TEscapeCharType.REGULAR_CHAR;
			case 1:
				//First character decides on mode.
				switch(c)
				{
					case '\"':
					case '\\':
							this.mode_handler = MODE_SINGLE_CHAR;
							break;
					default:
							this.mode_handler = MODE_HEX;
							break;
				};
				break;
		};
		assert(sequence_index>=1);
		//this is handled always, regardless of sequence index.
		return this.mode_handler.isEscape(c,escape_sequence_length,sequence_index);
	};
	@Override protected int unescape(StringBuilder collection_buffer)throws IOException
	{
		assert(this.mode_handler!=null); //can be called only once per buffer collection.
		final int c= this.mode_handler.unescape(collection_buffer);
		this.mode_handler = null;
		return c;
	};
};