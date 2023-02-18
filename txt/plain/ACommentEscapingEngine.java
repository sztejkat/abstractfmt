package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.txt.AEscapingEngine;
import java.io.IOException;

/**
	A simplfified engine which is responsible for 
	handling multi-line comments.
	<p>
	It's main purpose is to change multi line comment
	to a stream of linies starting with #. Works with
	all known eol-styles, altough may produce empty
	lines for some of them.
*/
abstract class ACommentEscapingEngine extends AEscapingEngine
{
			/** A previously written character or -1 after reset */
			private int prev_char;
			/** True if \r was detected before \n*/ 
			private boolean dectected_eol_r_before_n;
			/** True if \r was deteced */
			private boolean dectected_eol_r;
			/** True if \n was deteced */
			private boolean dectected_eol_n;
			
	@Override public void reset()
	{
		super.reset();
		prev_char = -1;
		dectected_eol_r = false;
		dectected_eol_n = false;
		dectected_eol_r_before_n = false;
	};
	@Override protected boolean mustEscapeCodepoint(int code_point)
	{
		int pc = this.prev_char;
		this.prev_char = code_point;
		
		if ((code_point!='\r')&&(code_point!='\n'))
		{
			//regular character
			if ((pc=='\r')||(pc=='\n')) return true;
		}else
		{
			if (code_point=='\r')
			{
				dectected_eol_r=true;
			};
			if (code_point=='\n')
			{
				if (!dectected_eol_n && (pc=='\r')) dectected_eol_r_before_n = true;
				dectected_eol_n=true;
			};
		};
		return false;
	};
	@Override protected void escape(char c)throws IOException
	{
		out('#');
		out(c);
	};
	@Override protected void escapeCodepoint(int c, char upper_surogate, char lower_surogate)throws IOException
	{
		out('#');
		out(upper_surogate);
		out(lower_surogate);
	}
	@Override public void flush()throws IOException
	{
		super.flush();
		int pc = this.prev_char;
		//Add trailing eol if not detected.
		if (!((pc=='\r')||(pc=='\n')))
		{
			//first auto-detected eols.
			if ((dectected_eol_r)&&(dectected_eol_r_before_n)) out('\r');
			if (dectected_eol_n) out('\n');
			if ((dectected_eol_r)&&(!dectected_eol_r_before_n)) out('\r');
			//Now fallback Linux.
			if (!dectected_eol_r && !dectected_eol_n) out('\n');
		};
	};
};