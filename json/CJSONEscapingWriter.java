package sztejkat.abstractfmt.json;
import sztejkat.abstractfmt.util.ACharsetEscapingWriter;
import static sztejkat.abstractfmt.util.SHex.D2HEX;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.io.IOException;
import java.io.Writer;


/**
	A writer which escapes characters which are not allowed in JSON
	format or such which cannot be encoded using a current encoding.
*/
class CJSONEscapingWriter extends ACharsetEscapingWriter
{
				/** If this flag is set the {@link #canWrite} additionally
				filters what can't be present in JSON string.
				<p>
				Note: This is a bit superfulous becase we can't escape
				outside JSON string. But it will help us to ensure that
				we use the escaping correctly.				 
				*/
				private boolean string_mode;
	/** Creates
	@param output output to which write.
	@param charset optional charset which will be used to detect
		characters which can't be correctly encoded by output.
		If this value is null this format assumes that <code>out</code>
		can encode a full <code>char</code> space.
	*/			
	CJSONEscapingWriter(Writer output,  Charset charset)
	{
		super(output, charset);
	};
	
	/*  *********************************************************************
	
			Services required by superclass.
	
	********************************************************************* */
	@Override protected boolean canWrite(char c)
	{
		if (string_mode)
		{			
			//escape what can't be directly present inside JSON string.
			if ((c=='"')||(c=='\\')||(c<=0x1F)) return false;
			//and what can't be carried by charset.
			return super.canWrite(c);
		}else
		{
			//Now a bit tricky piece. The JSON allows escaping
			//_ONLY_ inside strings. This means, that if character
			//stream fails to encode a character OUTSIDE string
			//we are boned.
			if (!super.canWrite(c))
				throw new IllegalStateException(
						"The current encoding cannot encode "+Integer.toHexString(c & 0xFFFF)+
						". This character appears outside a JSON string, so it cannot be escaped."+
						"Sorry, I can't write this type of data to JSON stream");
			return true; 
		}
	};
	@Override protected void escape(char c)throws IOException
	{
		assert(string_mode);	//Note: There is no escaping outside JSON string.
		//first write standard escapes.
		switch(c)
		{
			case '"': output.write("\\\""); break;
			case '\\': output.write("\\\\"); break;
			//Note: the / does not have to be escaped so we do not write it as escaped.
			case (char)0x0008: output.write("\\b"); break;
			case (char)0x000C: output.write("\\f"); break;
			case '\n': output.write("\\n"); break; //0x0A
			case '\r': output.write("\\r"); break; //0x0D
			case '\t': output.write("\\t"); break; //0x09
			default:
			//Now we need to write full hex escape. We can't use Integer.toHexString
			//because it is producing variable length sequence.
					output.write("\\u");
					output.write(D2HEX( (c >> (3*4)) & 0x0F));
					output.write(D2HEX( (c >> (2*4)) & 0x0F));
					output.write(D2HEX( (c >> (1*4)) & 0x0F));
					output.write(D2HEX(c & 0x0F));
		}
	}
	
	/* *************************************************************************
			
			Text mode services.
			
			All below services do set string_mode and re-set it
			at the return.
	
			
	**************************************************************************/
	/** Operates in string escaping mode
	@param c char to write
	@throws IOException if failed */
	public void writeString(char c)throws IOException
	{
		string_mode = true;
		try{
			write(c);
		}finally{ string_mode = false; };
	};
	/** Operates in string escaping mode
	@param s string to write
	@throws IOException if failed */
	public void writeString(String s)throws IOException
	{
		string_mode = true;
		try{
			write(s);
		}finally{ string_mode = false; };
	};
	/** Operates in string escaping mode
	@param b block to write
	@param off offset to first char to write
	@param len how many chars to write
	@throws IOException if failed */
	public void writeString(char [] b, int off, int len )throws IOException
	{
		string_mode = true;
		try{
			write(b,off,len);
		}finally{ string_mode = false; };
	};
	/** Operates in string escaping mode
	@param b block to write
	@param off offset to first char to write
	@param len how many chars to write
	@throws IOException if failed */
	public void writeString(CharSequence b, int off, int len )throws IOException
	{
		string_mode = true;
		try{
			append(b, off, off+len);
		}finally{ string_mode = false; }
	};
};