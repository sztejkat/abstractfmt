package sztejkat.abstractfmt.util;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.io.IOException;
import java.io.Writer;

/**
	A writer which detects which characters cannot be encoded using known characters
	set and tells them to be escaped.
*/
public abstract class ACharsetEscapingWriter extends AEscapingWriter
{
				/** Encoder used to test if characters can be encoded.
				May be null.
				@see #canWrite
				*/
				private final CharsetEncoder charset_enc;
				
	/** Creates
	@param output output to which write, can't be null.
	@param charset optional charset which will be used to detect
		characters which can't be correctly encoded by output.
		If this value is null this format assumes that <code>out</code>
		can encode a full <code>char</code> space.
	*/			
	public ACharsetEscapingWriter(Writer output,  Charset charset)
	{
		super(output);
		this.charset_enc  = charset==null ? null : charset.newEncoder();
	};
	/** Tests if charset set in constructor can encode the character */
	@Override protected boolean canWrite(char c)
	{
		if (charset_enc==null) return true;
		return charset_enc.canEncode(c);
	}
};