package sztejkat.abstractfmt.utils;
import java.io.*;
/**
	An alternative to <tt>PushBackReader</tt>
	which adds automatically growing push-back buffer
	and maintains the count of line and characters in line.
	
	<h2>Thread safey</h2>
	Thread safe.
	<p>
	<i>Note: This is a real pain in behind that all
	<code>Reader</code> subclasses must be thread safe.</i> 
*/
public class CAdaptivePushBackReader extends Reader
{
//Note: This is copied from my another project (sztejkat.utils.io)
//		Intentionally, to not enter "dependency hell"
//		because that project is highly coupled with
//		other projects.
		/** Input */
		private final Reader in;
		/** Size increment for re-allocation */
		private final int size_increment;
		/** A buffer. Data from buffer
		are read at {@link #read_ptr}
		and written at {@link #read_ptr}.
		Null if closed.
		*/
		private char [] buffer;
		/** End pointer to a {@link #buffer} */
		private int end_ptr;
		/** Read pointer from a buffer */
		private int read_ptr;
		/** Tracks line number of recently returned character,
		counted from 0*/
		private int line_number;
		/** Tracks char-in-line number of recently returned character,
		counted from 0.
		<p>
		This value may negative, if line was un-read and 
		in such case indicate the number of character since the 
		end of line.
		*/
		private int char_number;
		/** Eol type trackers. 
		@see #updateLineInfoOnUnread
		@see #updateLineInfoOnRead
		*/
		private boolean was_r;
		/** Eol type trackers. 
		@see #updateLineInfoOnUnread
		@see #updateLineInfoOnRead
		*/
		private boolean was_n;
		
	/* -------------------------------------------------------------------
	
		Creation
	
	-------------------------------------------------------------------*/
	/** 
	Creates with 1k buffers
	@param in source to read from
	*/
	public CAdaptivePushBackReader(Reader in)
	{
		this(in,1024,1024);
	};
	/** 
	Creates
	@param in source to read from. This class will synchronize
			on this object as a lock. Usually it will be the same
			object as <code>in.lock</code> but not always.
			Mainly due to performance reasons, since re-locking
			is facter than acquiring full lock and this class
			will frequently delegate operations to the source stream.
	@param initial_size intial buffer capacity
	@param size_increment buffer growth increment.
	*/
	public CAdaptivePushBackReader(Reader in, int initial_size,int size_increment)
	{
		//Note: I am a bit surprised that I can't get to in.lock
		//		even tough I am a reader. If I would belong to java.io. I could do it.
		super(in);
		assert(in!=null);		
		assert(initial_size>0);
		assert(size_increment>0);
		this.in = in;
		this.size_increment = size_increment;
		this.buffer = new char[initial_size];
	};
	/** 
	Creates
	@param in source to read from. 
	@param lock This class will synchronize on this object as a lock.
	@param initial_size intial buffer capacity
	@param size_increment buffer growth increment.
	*/
	public CAdaptivePushBackReader(Reader in, Object lock, int initial_size,int size_increment)
	{
		super(lock);
		assert(in!=null);		
		assert(initial_size>0);
		assert(size_increment>0);
		this.in = in;
		this.size_increment = size_increment;
		this.buffer = new char[initial_size];
	};
	/* -------------------------------------------------------------------
	
		Buffer managment
	
	-------------------------------------------------------------------*/
	private void validateNotClosed()throws IOException
	{
		if (buffer==null) throw new IOException("Closed");
	};
	private int getWriteSpace(){ return buffer.length - end_ptr; };
	/** Makes sure, that there is enough place in a buffer
	for data
	@param characters number of required characters
	@throws IOException if failed
	*/
	private void ensureCapacity(int characters) throws IOException 
	{
		validateNotClosed();
		int free=getWriteSpace();
		if (free<characters)
		{
			//Now test if moving characters is enough or
			//if we need to re-allocate buffer?
			final int L=(end_ptr-read_ptr);
			if ((read_ptr!=0)&&((free + read_ptr-1) >=characters))
			{
				//This is enough to move characters.
				System.arraycopy(buffer,read_ptr, buffer, 0, L);
				read_ptr=0;
				end_ptr=L;
			}else
			{
				//Need to grow buffer.
				int needs = (( characters + L -1) /size_increment +1)*size_increment;
				char [] new_buffer = new char[needs];
				System.arraycopy(buffer,read_ptr, new_buffer, 0, L);
				buffer = new_buffer;
				read_ptr=0;
				end_ptr=L;
			};
			
		};
		assert(getWriteSpace()>=characters);
	};
	/* -------------------------------------------------------------------
	
		Line and char counting.
	
	-------------------------------------------------------------------*/
	/** Taking character which is un-read updates line information
	stored in {@link #line_number}, {@link #char_number}
	@param c uread character
	*/
	private void updateLineInfoOnUnread(char c)
	{
		//Note: we can't correctly update character-in-line information
		switch(c)
		{
			//We need to handle: \n, \r\n, \r , \n\r as single liners.
			//The fully symetric form allows us to use the same schema as for
			//reading.
			case '\r':	
					if (!was_n) { line_number--;};
					was_r = true;
					was_n = false;
					char_number=0; 
					break;
			case '\n':
					if (!was_r) { line_number--;  };
					was_r = false;
					was_n = true;
					char_number=0;
					break;
			default:
					was_r = false;
					was_n = false;
					char_number--;
					break;
		}
	};
	/** Taking character read updates line information
	stored in {@link #line_number}, {@link #char_number}
	@param c 0...0xFFFF or -1, read character
	*/
	private void updateLineInfoOnRead(int c)
	{
		if (c<0) return;
		switch(c)
		{
			//We need to handle: \n, \r\n, \r , \n\r as single liners.
			case '\r':	
					if (!was_n) { line_number++;  };
					was_r = true;
					was_n = false;
					char_number=0;//unconditionally
					break;
			case '\n':
					if (!was_r) { line_number++; };
					was_r = false;
					was_n = true;
					char_number=0;
					break;
			default:
					was_r = false;
					was_n = false;
					char_number++;
					break;
		}
	};
	/** Taking buffer filled by {@link #readImpl} updates line information
	stored in {@link #line_number}, {@link #char_number}
	@param cbuff buffer, filled
	@param off offse where first char was filled
	@param len actually filled number of characters or -1
	*/
	private void updateLineInfoOnRead(char [] cbuff, int off, int len)
	{
		for(int i=0;i<len;i++)
		{
			updateLineInfoOnRead(cbuff[i+off]);
		};
	};

	/* -------------------------------------------------------------------
	
		Reader
	
	-------------------------------------------------------------------*/
	public int read(char[] cbuf,
                         int off,
                         int len)
			 throws IOException
	{
		synchronized(lock)
		{
			int r = readImpl(cbuf, off,len);
			updateLineInfoOnRead(cbuf,off,r);
			return r;
		}
	}
	private int readImpl(char[] cbuf,
                         int off,
                         int len)
			 throws IOException
	{
		//check if from buffer?
		validateNotClosed();
		final int L = end_ptr-read_ptr;
		assert(L>=0);
		if (L>0)
		{
			//from buffer, up to L bytes
			if (L>=len)
			{
				//single burst.
				System.arraycopy(buffer, read_ptr, cbuf, off, len);
				read_ptr+=len;
				if (read_ptr==end_ptr) 
				{
					read_ptr=0;
					end_ptr=0;
				};
				return len;
			}else
			{
				//fragmented.
				System.arraycopy(buffer, read_ptr, cbuf, off, L);
				len-=L;
				off+=L;
				read_ptr=0;
				end_ptr=0;
				
				int ext = in.read(cbuf,off,len);
				return ext==-1 ? L : L+ext;
			}
		}else
			return in.read(cbuf,off,len);
	};
	
	public int read()throws IOException
	{
		synchronized(lock)
		{
			int r= readImpl();
			updateLineInfoOnRead(r);
			return r;
		}
	};
	private int readImpl()throws IOException
	{
		validateNotClosed();
		final int L = end_ptr-read_ptr;
		assert(L>=0);
		if (L>0)
		{
			//from buffer.
			char c = buffer[read_ptr];
			read_ptr++;
			if (read_ptr==end_ptr) 
			{
				read_ptr=0;
				end_ptr=0;
			};
			return c;
		}else
			return in.read();
	};
	
	public void close()throws IOException
	{
		synchronized(lock)
		{
			buffer=null;
			read_ptr=-1;
			end_ptr=-1;
			
			in.close();
		}
	};
	
	public boolean ready()throws IOException
	{
		synchronized(lock){ return read_ptr!=end_ptr; }
	};
	/** False, does not support it */
	public boolean markSupported(){ return false; };
	/* -------------------------------------------------------------------
	
		Specialty reading
	
	-------------------------------------------------------------------*/
	/** Alike {@link #read(char[],int,int)} but into an appendable
	@param cbuf non null, where to append
	@param length up to how many chars
	@return -1 if eof, otherwise number of chars
	@throws IOException if failed
	*/
	public int read(Appendable cbuf, int length)throws IOException
	{
		synchronized(lock)
		{
			int r = 0;
			while(--length>0)
			{
				int c = read();
				assert((c>=-1)&&(c<=0xFFFF));
				if (c==-1) return r==0 ? -1 : r;
				cbuf.append((char)c);
				r++;
			};	
			return r;
		}
	}
	
	/* -------------------------------------------------------------------
	
		Push-back
	
	-------------------------------------------------------------------*/
	/** Un-reads
	@param c character to un-read.
		After return from this method the next {@link #read} will
		return un-read character. Notice, the 
		<pre>
			unread("ABC") results in reading "ABC"
			<i>but</i>
			unread('A');unread('B');unread('C'); results in reading "CBA"
		</pre>
	@throws IOException if failed
	*/
	public void unread(char c)throws IOException
	{
		synchronized(lock)
		{
			ensureCapacity(1);
			//Now we need to write it so that next read would fetch it.
			if (read_ptr!=end_ptr)
			{
				//need to move it.
				System.arraycopy(buffer,read_ptr,buffer, read_ptr+1, end_ptr-read_ptr);
			}
			buffer[read_ptr]=c;
			end_ptr++;
			updateLineInfoOnUnread(c);
		}
	};
	/** Un-reads.
	Subsequent read will read characters as present in an array 
	@param cbuf array, non null
	@throws IOException if failed
	*/
	public void unread(char[] cbuf)throws IOException
	{
		assert(cbuf!=null);
		unread(cbuf,0,cbuf.length);
	};
	/** Un-reads.
	Subsequent read will read characters as present in an array 
	@param cbuf array, non-null 
	@param off offset
	@param len length
	@throws IOException if failed
	*/
	public void unread(char[] cbuf,int off,int len)throws IOException
	{
		assert(cbuf!=null);
		assert(off>=0);
		assert(len>=0);
		assert(off+len<=cbuf.length);
		
		synchronized(lock)
		{
			ensureCapacity(len);
			if (read_ptr!=end_ptr)
			{
				//need to move it.
				System.arraycopy(buffer,read_ptr,buffer, read_ptr+len, end_ptr-read_ptr);
			}
			System.arraycopy(cbuf,off, buffer, read_ptr, len);
			end_ptr+=len;
			for(int i=len; --i>=0; )
			{
				updateLineInfoOnUnread(cbuf[off+i]);
			};
		}
	};
	/** Un-reads.
	Subsequent read will read characters as present in a sequence 
	@param chars sequence, non null
	@throws IOException if failed
	*/
	public void unread(CharSequence chars)throws IOException
	{
		assert(chars!=null);
		unread(chars,0,chars.length());
	};
	/** Un-reads.
	Subsequent read will read characters as present in a sequence 
	@param chars sequence, non null
	@param from offset
	@param length length
	@throws IOException if failed
	*/
	public void unread(CharSequence chars,int from, int length)throws IOException
	{
		assert(chars!=null);
		assert(from>=0);
		assert(length>=0);
		assert(from+length<=chars.length());
		synchronized(lock)
		{
			ensureCapacity(length);
			if (read_ptr!=end_ptr)
			{
				//need to move it.
				System.arraycopy(buffer,read_ptr,buffer, read_ptr+length, end_ptr-read_ptr);
			}
			for(int i=length; --i>=0; )
			{
				updateLineInfoOnUnread(chars.charAt(from+i));
			};
			int i = read_ptr;
			end_ptr+=length;
			while(length>0)
			{
				buffer[i]=chars.charAt(from);
				i++;
				length--;
				from++;
			};
		}
	};
	/* -------------------------------------------------------------------
	
		Line info
	
	-------------------------------------------------------------------*/
	/** Returns line number of most recently read characters.
	The result if most rencetly character is a part of multi
	char end-of line marker is a boundary condition and 
	may be either line it ends or line it begins.
	<p>
	This method correctly handles un-reading.
	@return number of lines processed, starting from 0.
			This number is incremented at each eol found.
	*/
	public final int getLineNumber()
	{ 
		synchronized(lock)
		{
			return line_number; 
		}
	};
	/** Returns a position in line of most recent character.
	For end-of-line characters this position is zero.
	<p>
	This method does <u>not</u> handle unreading well.
	@return position of character in line. Can be negative
		if line terminator was un-read and in such case
		indicates the distance from the end of current line
		instead of the distance from beginning.
		<p>
		This number is set to zero at each eol found,
		incremented at each read non-eol character and decremented
		at each un-read non-eol character.
	*/
	public final int getCharNumber()
	{ 
		synchronized(lock)
		{
			return char_number; 
		}
	};
	/* -------------------------------------------------------------------
	
	
			Junit org Test arena
	
	
	-------------------------------------------------------------------*/
	public static final class Test extends sztejkat.utils.test.ATest
	{
		private static void readN(Reader r, StringBuilder sb, int fragment_length)throws IOException
		{
			int read=0;
			while(read<fragment_length)
			{
				int t= r.read();
				if (t==-1) throw new AssertionError("failed to read fragment");
				read++;
				sb.append((char)t);
			};
		};
		
		private static void readUpTo(Reader r, StringBuilder sb, int fragment_length)throws IOException
		{
			int read=0;
			while(read<fragment_length)
			{
				int t= r.read();
				if (t==-1) break;
				read++;
				sb.append((char)t);
			};
		};
		
		private void dump(CAdaptivePushBackReader r)
		{
			System.out.println("end_ptr="+r.end_ptr);
			System.out.println("read_ptr="+r.read_ptr);
			for(int i=0;i<r.buffer.length;i++)
			{
				System.out.print(r.buffer[i]);
			};
			System.out.println();
			for(int i=0;i<r.buffer.length;i++)
			{
				System.out.print( ((i>=r.read_ptr)&&(i<r.end_ptr)) ? '^' : '_'); 
			};
			System.out.println();
		};
		
		private static void readNcb(Reader r, StringBuilder sb, int fragment_length)throws IOException
		{
			int read=0;
			int remaning = fragment_length;
			char [] buffer= new char[fragment_length+10];
			int off = 10;
			while(remaning>0)
			{
				int t= r.read(buffer,off,remaning);
				if (t<=0) throw new AssertionError("failed to read fragment");
				read+=t;
				off+=t;
				remaning-=t;
			};
			sb.append(buffer,10,fragment_length);
		};
		@org.junit.Test public void testReadThrough()throws IOException
		{
			enter();
			final String S = "POKAZAŁAM CO MAM NAJLEPSZEGO I DAŁAM TO TADEUSZOWI";
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader(S),8,8);
			
			StringBuilder sb = new StringBuilder();
			
			readN(R, sb, S.length());
			
			org.junit.Assert.assertTrue(sb.toString().equals(S));
			org.junit.Assert.assertTrue(R.read()==-1);
			
			
			leave();
		};
		
		@org.junit.Test public void testReadThroughArray()throws IOException
		{
			enter();
			final String S = "POKAZAŁAM CO MAM NAJLEPSZEGO I DAŁAM TO TADEUSZOWI";
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader(S),8,8);
			
			StringBuilder sb = new StringBuilder();
			
			readNcb(R, sb, S.length());
			
			org.junit.Assert.assertTrue(sb.toString().equals(S));
			org.junit.Assert.assertTrue(R.read()==-1);
			
			
			leave();
		};
		
		@org.junit.Test public void testPushback_chars()throws IOException
		{
			enter();
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("BLONDIE"),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread('x');
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread('a');
			R.unread('v');
			R.unread('i');
			R.unread('e');
			R.unread('r');
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='r');
			org.junit.Assert.assertTrue(R.read()=='e');
			org.junit.Assert.assertTrue(R.read()=='i');
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='v');
			org.junit.Assert.assertTrue(R.read()=='a');
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='D');
			org.junit.Assert.assertTrue(R.read()=='I');
			org.junit.Assert.assertTrue(R.read()=='E');
			org.junit.Assert.assertTrue(R.read()==-1);
			
			leave();
		};
		
		
		@org.junit.Test public void testPushback_array()throws IOException
		{
			enter();
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("BLONDIE"),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread(new char[]{'x'});
			
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread(new char[]{'a','v','i','e','r'});
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='a');
			org.junit.Assert.assertTrue(R.read()=='v');
			org.junit.Assert.assertTrue(R.read()=='i');
			org.junit.Assert.assertTrue(R.read()=='e');
			org.junit.Assert.assertTrue(R.read()=='r');
			
			org.junit.Assert.assertTrue(R.read()=='D');
			org.junit.Assert.assertTrue(R.read()=='I');
			org.junit.Assert.assertTrue(R.read()=='E');
			org.junit.Assert.assertTrue(R.read()==-1);
			
			leave();
		};
		
		@org.junit.Test public void testPushback_array2()throws IOException
		{
			enter();
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("BLONDIE"),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread(new char[]{'x'});
			
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread(new char[]{'a','v'});
			R.unread(new char[]{'i','e','r'});
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='i');
			org.junit.Assert.assertTrue(R.read()=='e');
			org.junit.Assert.assertTrue(R.read()=='r');
			org.junit.Assert.assertTrue(R.read()=='a');
			org.junit.Assert.assertTrue(R.read()=='v');
			
			org.junit.Assert.assertTrue(R.read()=='D');
			org.junit.Assert.assertTrue(R.read()=='I');
			org.junit.Assert.assertTrue(R.read()=='E');
			org.junit.Assert.assertTrue(R.read()==-1);
			
			leave();
		};
		
		
		@org.junit.Test public void testPushback_sub_array2()throws IOException
		{
			enter();
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("BLONDIE"),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread(new char[]{'-','x','c'},1,1);
			
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread(new char[]{'p','o','a','v','q'},2,2);
			R.unread(new char[]{'i','e','r','c','o'},0,3);
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='i');
			org.junit.Assert.assertTrue(R.read()=='e');
			org.junit.Assert.assertTrue(R.read()=='r');
			org.junit.Assert.assertTrue(R.read()=='a');
			org.junit.Assert.assertTrue(R.read()=='v');
			
			org.junit.Assert.assertTrue(R.read()=='D');
			org.junit.Assert.assertTrue(R.read()=='I');
			org.junit.Assert.assertTrue(R.read()=='E');
			org.junit.Assert.assertTrue(R.read()==-1);
			
			leave();
		};
		
		
		@org.junit.Test public void testPushback_sequence()throws IOException
		{
			enter();
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("BLONDIE"),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread("x");
			
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread("avier");
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='a');
			org.junit.Assert.assertTrue(R.read()=='v');
			org.junit.Assert.assertTrue(R.read()=='i');
			org.junit.Assert.assertTrue(R.read()=='e');
			org.junit.Assert.assertTrue(R.read()=='r');
			
			org.junit.Assert.assertTrue(R.read()=='D');
			org.junit.Assert.assertTrue(R.read()=='I');
			org.junit.Assert.assertTrue(R.read()=='E');
			org.junit.Assert.assertTrue(R.read()==-1);
			
			leave();
		};
		
		@org.junit.Test public void testPushback_sequence2()throws IOException
		{
			enter();
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("BLONDIE"),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread("x");
			
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread("av");
			R.unread("ier");
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='i');
			org.junit.Assert.assertTrue(R.read()=='e');
			org.junit.Assert.assertTrue(R.read()=='r');
			org.junit.Assert.assertTrue(R.read()=='a');
			org.junit.Assert.assertTrue(R.read()=='v');
			
			org.junit.Assert.assertTrue(R.read()=='D');
			org.junit.Assert.assertTrue(R.read()=='I');
			org.junit.Assert.assertTrue(R.read()=='E');
			org.junit.Assert.assertTrue(R.read()==-1);
			
			leave();
		};
		@org.junit.Test public void testPushback_sub_sequence()throws IOException
		{
			enter();
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("BLONDIE"),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread("okox",3,1);
			
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread("savedf",1,2);
			R.unread("rotier",3,3);
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='i');
			org.junit.Assert.assertTrue(R.read()=='e');
			org.junit.Assert.assertTrue(R.read()=='r');
			org.junit.Assert.assertTrue(R.read()=='a');
			org.junit.Assert.assertTrue(R.read()=='v');
			
			org.junit.Assert.assertTrue(R.read()=='D');
			org.junit.Assert.assertTrue(R.read()=='I');
			org.junit.Assert.assertTrue(R.read()=='E');
			org.junit.Assert.assertTrue(R.read()==-1);
			
			leave();
		};
		
		
		@org.junit.Test public void testPushback_1()throws IOException
		{
			enter();
			final String S1 = "POKAZAŁAM CO MAM NAJLEPSZEGO";
			final String S2=  "I DAŁAM TO TADEUSZOWI";
			final String insert_1="ALE ZABRAŁAM WSZYSTKO I ";
			final String insert_2="NIE ";
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader(S1+S2),8,8);
			
			StringBuilder sb = new StringBuilder();
			
			readNcb(R, sb, S1.length());
			R.unread(insert_1);
			R.unread(insert_2);
			
			readNcb(R, sb, insert_2.length());
			readNcb(R, sb, insert_1.length());
			readNcb(R, sb, S2.length());
			
			System.out.println(sb.toString());
			org.junit.Assert.assertTrue(sb.toString().equals(S1+insert_2+insert_1+S2));
			org.junit.Assert.assertTrue(R.read()==-1);
			
			
			leave();
		};
		
		@org.junit.Test public void testPushback_2()throws IOException
		{
			enter();
			final String S1 = "POKAZAŁAM CO MAM NAJLEPSZEGO";
			final String S2=  "I DAŁAM TO TADEUSZOWI";
			final String insert_1="ALE ZABRAŁAM WSZYSTKO I ";
			final String insert_2="NIE ";
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader(S1+S2),8,8);
			
			StringBuilder sb = new StringBuilder();
			
			readN(R, sb, S1.length());
			R.unread(insert_1);
			R.unread(insert_2);
			readN(R, sb, insert_2.length());
			readN(R, sb, insert_1.length());
			readN(R, sb, S2.length());
			
			System.out.println(sb.toString());
			org.junit.Assert.assertTrue(sb.toString().equals(S1+insert_2+insert_1+S2));
			org.junit.Assert.assertTrue(R.read()==-1);
			
			
			leave();
		};
		
		
		
		@org.junit.Test public void testPushback_3()throws IOException
		{
			enter();
			final String S1 = "POKAZAŁAM CO MAM NAJLEPSZEGO";
			final String S2=  "I DAŁAM TO TADEUSZOWI";
			final String insert_1="ALE ZABRAŁAM WSZYSTKO I ";
			final String insert_2="NIE ";
			
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader(S1+S2),8,8);
			
			StringBuilder sb = new StringBuilder();
			
			readNcb(R, sb, S1.length());
			R.unread(insert_1);
			R.unread(insert_2);
			readNcb(R, sb, insert_2.length()+insert_1.length()+S2.length());
			
			System.out.println(sb.toString());
			org.junit.Assert.assertTrue(sb.toString().equals(S1+insert_2+insert_1+S2));
			org.junit.Assert.assertTrue(R.read()==-1);
			
			
			leave();
		};
		
		
		@org.junit.Test public void testPushback_4()throws IOException
		{
			enter();
		
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("ALICJA MALICKA"),8,8);
			
			StringBuilder sb = new StringBuilder();
			
			readNcb(R, sb, "ALICJA ".length());
			R.unread("BEATA");
			readNcb(R, sb, "BEA".length());
			R.unread("--");
			readUpTo(R, sb, "--TAMALICKA".length());
			System.out.println(sb.toString());
			org.junit.Assert.assertTrue(sb.toString().equals("ALICJA BEA--TAMALICKA"));
			org.junit.Assert.assertTrue(R.read()==-1);
			
			
			leave();
		};
		
		
		@org.junit.Test public void testPushback_5()throws IOException
		{
			enter();
		
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol value"),8,8);
			
			StringBuilder sb = new StringBuilder();
			
			readNcb(R, sb, "symbol ".length());
			R.unread("symbol");
			dump(R);
			R.unread(' ');
			dump(R);
			org.junit.Assert.assertTrue(R.read()==' ');
			leave();
		};
		
		
		@org.junit.Test public void test_line_numbering_n()throws IOException
		{
			enter();		
				CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \nvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readN(R, sb, "symbol ".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==0);
				readN(R, sb, "\nvalue".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==1);
			leave();
		};
		@org.junit.Test public void test_line_numbering_nn()throws IOException
		{
			enter();		
			CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \n\nvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readN(R, sb, "symbol ".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==0);
				readN(R, sb, "\n\nvalue".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==2);
			leave();
		};
		@org.junit.Test public void test_line_numbering_rn()throws IOException
		{
			enter();		
				CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \r\nvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readN(R, sb, "symbol ".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==0);
				readN(R, sb, "\r\nvalue".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==1);
			leave();
		};
		@org.junit.Test public void test_line_numbering_nr()throws IOException
		{
			enter();		
				CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \n\rvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readN(R, sb, "symbol ".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==0);
				readN(R, sb, "\r\nvalue".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==1);
			leave();
		};
		@org.junit.Test public void test_line_numbering_r()throws IOException
		{
			enter();		
				CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \rvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readN(R, sb, "symbol ".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==0);
				readN(R, sb, "\rvalue".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==1);
			leave();
		};
		@org.junit.Test public void test_line_numbering_rr()throws IOException
		{
			enter();		
				CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \r\rvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readN(R, sb, "symbol ".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==0);
				readN(R, sb, "\r\rvalue".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==2);
			leave();
		};
		
		
		
		@org.junit.Test public void test_char_numbering()throws IOException
		{
			enter();		
				CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \nvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readN(R, sb, "symbol ".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==0);
				org.junit.Assert.assertTrue(R.getCharNumber()==7);
				readN(R, sb, "\nvalue".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==1);
				org.junit.Assert.assertTrue(R.getCharNumber()==5);
			leave();
		};
		
		@org.junit.Test public void test_char_numbering_nn()throws IOException
		{
			enter();		
				CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \n\nvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readN(R, sb, "symbol ".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==0);
				org.junit.Assert.assertTrue(R.getCharNumber()==7);
				readN(R, sb, "\n\nvalue".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==2);
				org.junit.Assert.assertTrue(R.getCharNumber()==5);
			leave();
		};
		@org.junit.Test public void test_char_numbering_rr()throws IOException
		{
			enter();		
				CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \r\rvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readN(R, sb, "symbol ".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==0);
				org.junit.Assert.assertTrue(R.getCharNumber()==7);
				readN(R, sb, "\r\rvalue".length());
				org.junit.Assert.assertTrue(R.getLineNumber()==2);
				org.junit.Assert.assertTrue(R.getCharNumber()==5);
			leave();
		};
		
		
		@org.junit.Test public void test_line_numbering_unreading_n()throws IOException
		{
			enter();		
				CAdaptivePushBackReader R = new CAdaptivePushBackReader(new StringReader("symbol \r\rvalue"),8,8);			
				StringBuilder sb = new StringBuilder();			
				readUpTo(R, sb, 1024);
				org.junit.Assert.assertTrue(R.getLineNumber()==2);
				org.junit.Assert.assertTrue(R.getCharNumber()==5);
				R.unread('e');
				org.junit.Assert.assertTrue(R.getCharNumber()==4);
				R.unread('\n');
				org.junit.Assert.assertTrue(R.getLineNumber()==1);
				org.junit.Assert.assertTrue(R.getCharNumber()==0);
				R.unread('\r');
				org.junit.Assert.assertTrue(R.getLineNumber()==1);
				org.junit.Assert.assertTrue(R.getCharNumber()==0);
				R.unread('a');
				org.junit.Assert.assertTrue(R.getCharNumber()==-1);
				readUpTo(R, sb, 1024);
				org.junit.Assert.assertTrue(R.getLineNumber()==2);
				org.junit.Assert.assertTrue(R.getCharNumber()==1);
			leave();
		};
	};
};

