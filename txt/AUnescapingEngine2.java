package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;

/**
	An {@link AUnescapingEngine} which
	does use own unread buffer.
*/
public abstract class AUnescapingEngine2 extends AUnescapingEngine
{
				/** Set if escape was terminated by REGULAR_CHAR and we
				need to double-buffer a char in {@link #char_pending}*/
				private boolean is_char_pending;
				/** See {@link #is_char_pending} */
				private char char_pending;
				
				
	/* ********************************************
	
			Services required from subclasses
			
	  *********************************************/
	 /** Like {@link AUnescapingEngine#readImpl}, reads single <code>char</code>
	from downstream, dumb way without any processing.
	@return -1 if end-of-file, 0...0xFFFF represeting single UTF-16 of Java
		<code>char</code> otherwise
	@throws IOException if failed.
	*/
	protected abstract int readImpl2()throws IOException; 
	/* ********************************************
	
			AUnescapingEngine
			
	  *********************************************/
	 @Override protected final void unread(char c)throws IOException
	 {
	 	 	assert(!is_char_pending): "forgot to reset?";
	 	 	this.is_char_pending = true;
	 	 	this.char_pending=c;
	 };
	 @Override public void reset()
	 {
		is_char_pending = false;
		char_pending = 0;
		super.reset(); 
	 };
	 
	 @Override protected int readImpl()throws IOException
	 {
	 	 //handle pending?
		if (is_char_pending)
		{
			is_char_pending = false;
			return char_pending;
		};
		return readImpl2();
	 };
};