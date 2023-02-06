package sztejkat.abstractfmt.utils;
import java.io.*;

/**
	A writer which directs all <u>operations</u> to all writers 
	passed to it.
	<p>
	If any of redirected operations fails
	during an action the operation is aborted and it won't reach
	remaining writers.
	<p>
	Due to that policy this class is NOT intended to be used
	in production environment and its primary use is interception
	of data during tests.
*/
public class CMuxWriter extends Writer
{
	/* Design note: I did intentionally override all methods
	   including those which are implemented over another 
	   methods to be sure, that all operations are invoked
	   on muxed writers as they are on this writer */
				/** Targets */
				private final Writer [] mux;
				
	/** Creates
	@param mux will send all operations to all writers in that 
			array, in order of appearance. Can't be null, can't
			contain nulls.
	*/
	public CMuxWriter(Writer [] mux)
	{
		assert(mux!=null);
		this.mux = mux;
	};
	@Override public void write(int c)throws IOException
	{
		for(Writer w: mux){ w.write(c); };
	};
	@Override public void write(char[] cbuf)throws IOException
	{
		for(Writer w: mux){ w.write(cbuf); };
	};
	@Override public void write(char[] cbuf,
                           int off,
                           int len)
                    throws IOException
    {
		for(Writer w: mux){ w.write(cbuf,off,len); };
	};
	@Override public void write(String str)throws IOException
	{
		for(Writer w: mux){ w.write(str); };
	};
	@Override public void write(String str,
                  int off,
                  int len)
           			throws IOException
    {
		for(Writer w: mux){ w.write(str,off,len); };
	};
	@Override public Writer append(CharSequence csq)throws IOException
	{
		for(Writer w: mux){ w.append(csq); };
		return this;	
	}
	@Override public Writer append(CharSequence csq,
                     int start,
                     int end)
              	throws IOException
    {
		for(Writer w: mux){ w.append(csq,start,end); };
		return this;	
	}
	@Override public Writer append(char c)throws IOException
	{
		for(Writer w: mux){ w.append(c); };
		return this;
	};
	@Override public void flush()throws IOException
	{
		for(Writer w: mux){ w.flush(); };
	};
	@Override public void close()throws IOException
	{
		for(Writer w: mux){ w.close(); };
	};
};

