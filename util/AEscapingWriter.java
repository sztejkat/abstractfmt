package sztejkat.abstractfmt.util;
import java.io.IOException;
import java.io.Writer;

/**
	A writer which detects that some characters cannot be encoded
	and have to be escaped.
*/
public abstract class AEscapingWriter extends Writer
{
				/** Output to which data are written */
				protected final Writer output;
		
	/** Creates
	@param output output to which write, can't be null.
	*/		
	protected AEscapingWriter(Writer output)
	{
		assert(output!=null);
		this.output = output;
	};
	/** Invoked for each character to check , if character
	can be encoded. 
	@param c character to check.
	@return true if it can be written down-stream withtout any
	escaping. False if it needs to use {@link #escape} 
	*/
	protected abstract boolean canWrite(char c);
	/** Writes escaped form of <code>c</code> to {@link #output}
	@throws IOException if failed */
	protected abstract void escape(char c)throws IOException;
	
	/* ********************************************************
	
			Writer
	
	*********************************************************/
	
	@Override final public Writer append(char c)throws IOException
	{
		if (canWrite(c)) output.append(c);
		else
			escape(c);
		return this;
	};
	@Override final public Writer append(CharSequence csq)throws IOException
	{
		return append(csq,0,csq.length());
	};
	@Override final public Writer append(CharSequence csq,
                     int start,
                     int end)
              		throws IOException
	{
		//Use bulk method to write down-stream.
		int s = start;
		int i =s;
		for(; i<end; i++)
		{
			char c= csq.charAt(i);
			if (!canWrite(c))
			{
				//write in bulk
				if (s<i)
				{
					output.append(csq,s,i);
				};
				escape(c);
				s = i+1;
			};
			//just continue checking
		};
		if (s<i)
			output.append(csq,s,i);
		return this;
	};
	@Override public void close()throws IOException
	{
		output.close();
	};
	@Override public void flush()throws IOException
	{
		output.flush();
	};
	@Override final public void write(char[] cbuf)throws IOException
	{
		write(cbuf,0,cbuf.length);
	};
	@Override final public void write(char[] cbuf, int off, int len)throws IOException
	{
		//Use bulk method to write down-stream.
		int s = off;
		while(len>0)
		{
			char c= cbuf[off];
			if (!canWrite(c))
			{
				//write in bulk
				if (s<off)
				{
					output.write(cbuf,s,off-s);
				};
				escape(c);
				s=off+1;
			};
			//just continue checking
			off++;
			len--;
		};
		if (s<off)
			output.write(cbuf,s,off-s);
	};
	@Override final public void write(int c)throws IOException
	{
		assert((c & ~0xFFFF)==0):c+" is not a char";
		char _c = (char)c;
		if (canWrite(_c)) output.write(c);
		else
			escape(_c);
	};
	@Override final public void write(String str)throws IOException
	{
		write(str,0,str.length());
	};
	@Override final public void write(String str, int off, int len)throws IOException
	{
		append(str, off, off+len);
	};
	
	
	
	
	/* **************************************************************************
	
	
	
				Junit org test area.
	
	
	
	* ***************************************************************************/
	public static final class Test extends sztejkat.utils.test.ATest
	{
				private static final class DUT extends AEscapingWriter
				{
					protected DUT(Writer output){ super(output); };
					protected boolean canWrite(char c)
					{
						return c!='A';
					};
					protected void escape(char c)throws IOException
					{
						assert(!canWrite(c));
						output.write("????");
					};
				};
				
		
		@org.junit.Test public void testNoEscaping_1()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			
			d.append('x');
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("x".equals(s));
			
			leave();
		};
		
		@org.junit.Test public void testNoEscaping_2()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			d.append("");
			d.append("xalamander");
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("xalamander".equals(s));
			
			leave();
		};
		@org.junit.Test public void testNoEscaping_3()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			
			d.write("roporopo".toCharArray());
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("roporopo".equals(s));
			
			leave();
		};
		@org.junit.Test public void testNoEscaping_4()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			
			d.write("roporopo".toCharArray(),1,4);
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("opor".equals(s));
			
			leave();
		};
		@org.junit.Test public void testNoEscaping_5()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			
			d.write('r');
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("r".equals(s));
			
			leave();
		};
		@org.junit.Test public void testNoEscaping_6()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			
			d.write("roporopo");
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("roporopo".equals(s));
			
			leave();
		};
		@org.junit.Test public void testNoEscaping_7()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			
			d.write("roporopo",1,4);
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("opor".equals(s));
			
			leave();
		};
		
		
		
		@org.junit.Test public void testEscaping_1()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			
			d.append('A');
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("????".equals(s));
			
			leave();
		};
		@org.junit.Test public void testEscaping_2()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			d.append("AalaAandeA");
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("????ala????ande????".equals(s));
			
			leave();
		};
		@org.junit.Test public void testEscaping_3()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			d.write("AalaAandeA".toCharArray());
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("????ala????ande????".equals(s));
			
			leave();
		};
		@org.junit.Test public void testEscaping_4()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			
			d.write('A');
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("????".equals(s));
			
			leave();
		};
		@org.junit.Test public void testEscaping_5()throws IOException
		{
			enter();
			java.io.StringWriter sw= new java.io.StringWriter();
			DUT d = new DUT(sw);
			d.write("salaAander");
			d.close();
			
			String s = sw.toString();
			
			org.junit.Assert.assertTrue("sala????ander".equals(s));
			
			leave();
		};
	};
	
}; 