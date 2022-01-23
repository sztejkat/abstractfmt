package sztejkat.abstractfmt.json;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.Writer;

/**
	A JSON indicator format which can be switched between
	bare/enclosed and described or un-described.
*/
public class CJSONIndicatorWriteFormat extends AJSONIndicatorWriteFormat
{
				/** True if runs in described mode */
				private final boolean is_described;
				/** True if runs in enclosed mode, false if in bare mode. */
				private final boolean is_enclosed;
	/** Creates.
	<p>
	An AJAX compatible writer can be created using following parameters:
	<pre>
		new CJSONIndicatorWriteFormat( output, 
										charset, 
										CJSONSettings.AJAX,
										false,
										true
										)
	</pre>
	
	@param output output to which write.
	@param charset optional charset which will be used to detect
		characters which can't be correctly encoded by output.
		If this value is null this format assumes that <code>out</code>
		can encode a full <code>char</code> space.
	@param settings JSON settings, non null. 
	@param is_described true if run in described mode
	@param is_enclosed true to run in enclosed mode.
	*/			
	public CJSONIndicatorWriteFormat(
										  Writer output,
										  Charset charset,
										  CJSONSettings settings,
										  boolean is_described,
										  boolean is_enclosed
										  )	
	{
		super(output,charset,settings);
		this.is_described=is_described;
		this.is_enclosed=is_enclosed;
	};
	/* **********************************************************************
	
			IIndicatorWriteFormat
	
	***********************************************************************/
	@Override public final boolean isDescribed(){ return is_described; };
	@Override public void open()throws IOException
	{
		if (is_enclosed) output.write('[');
	};
	@Override public void close()throws IOException
	{
		if (is_enclosed) output.write(']');
		super.close();
	};
};