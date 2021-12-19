package sztejkat.abstractfmt.util;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;


/**
		A sequence of Reader readers which
		returns EOF or partial read when an element of sequence
		returns eof.
		<p>
		For testing only, inefficient.
*/
public class CEofInjectingSequenceReader extends Reader
{
				private Iterator<Reader> readers;
				
				private Reader current;
				
	/** Creates 
	@param readers iterator over readers. Use null element
		to enforce an additional eof.
	*/
	public CEofInjectingSequenceReader(Iterator<Reader> readers)
	{
		this.readers = readers;
		this.current = null;
	};
	@Override public int read(char[] cbuf, int off, int len)throws IOException
	{
		if (current==null)
		{
			if (readers.hasNext()) current = readers.next();
			else
			return -1;
		};
		if (current==null) return -1;
		int r = current.read(cbuf,off,len);
		if (r<len)
		{
			//This will result in eof or partial read and trigger
			//moving to next reader in sequence in next call.
			 current=null;
		};
		return r;
	};
	@Override public void close(){};
		
};	