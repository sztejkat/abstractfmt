package sztejkat.abstractfmt.util;
import java.io.*;
/**
	An alternative to <tt>PushbackInputStream</tt>
	which adds automatically growing push-back buffer.	
	
	<h2>Thread safey</h2>
	Not thread safe
	 
*/
public class CAdaptivePushBackInputStream extends InputStream
{
//Note: This is just copy&paste+replace of CAdaptivePushBackReader

		/** Input */
		private final InputStream in;
		/** Size increment for re-allocation */
		private final int size_increment;
		/** A buffer. Data from buffer
		are read at {@link #read_ptr}
		and written at {@link #read_ptr}.
		Null if closed.
		*/
		private byte [] buffer;
		/** End pointer to a {@link #buffer} */
		private int end_ptr;
		/** Read pointer from a buffer */
		private int read_ptr;
	
	/* -------------------------------------------------------------------
	
		Creation
	
	-------------------------------------------------------------------*/
	/** 
	Creates with 1k buffers
	@param in source to read from
	*/
	public CAdaptivePushBackInputStream(InputStream in)
	{
		this(in,1024,1024);
	};
	/** 
	Creates
	@param in source to read from
	@param initial_size intial buffer capacity
	@param size_increment buffer growth increment.
	*/
	public CAdaptivePushBackInputStream(InputStream in, int initial_size,int size_increment)
	{
		assert(in!=null);
		assert(initial_size>0);
		assert(size_increment>0);
		this.in = in;
		this.size_increment = size_increment;
		this.buffer = new byte[initial_size];
	};
	
	
	/* -------------------------------------------------------------------
	
		Buffer managment
	
	-------------------------------------------------------------------*/
	private void validateNotClosed()throws IOException
	{
		if (buffer==null) throw new IOException("Closed");
	};
	private int getWriteSpace(){ return buffer.length - end_ptr; };
	private int getUnreadCount(){ return end_ptr-read_ptr; };
	/** Makes sure, that there is enough place in a buffer
	for data
	@param bytes number of required bytes
	@throws IOException if failed
	*/
	private void ensureCapacity(int bytes) throws IOException 
	{
		validateNotClosed();
		int free=getWriteSpace();
		if (free<bytes)
		{
			//Now test if moving bytes is enough or
			//if we need to re-allocate buffer?
			final int L=(end_ptr-read_ptr);
			if ((read_ptr!=0)&&((free + read_ptr-1) >=bytes))
			{
				//This is enough to move bytes.
				System.arraycopy(buffer,read_ptr, buffer, 0, L);
				read_ptr=0;
				end_ptr=L;
			}else
			{
				//Need to grow buffer.
				int needs = (( bytes + L -1) /size_increment +1)*size_increment;
				byte [] new_buffer = new byte[needs];
				System.arraycopy(buffer,read_ptr, new_buffer, 0, L);
				buffer = new_buffer;
				read_ptr=0;
				end_ptr=L;
			};
			
		};
		assert(getWriteSpace()>=bytes);
	};
	/* -------------------------------------------------------------------
	
		InputStream
	
	-------------------------------------------------------------------*/
	public int read(byte[] cbuf,
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
			validateNotClosed();
			final int L = end_ptr-read_ptr;
			assert(L>=0);
			if (L>0)
			{
				//from buffer.
				byte c = buffer[read_ptr];
				read_ptr++;
				if (read_ptr==end_ptr) 
				{
					read_ptr=0;
					end_ptr=0;
				};
				return c & 0xFF;
			}else
				return in.read();
	};
	
	public void close()throws IOException
	{
		buffer=null;
		read_ptr=-1;
		end_ptr=-1;
		in.close();
	};
	
	public int available()throws IOException
	{
		return getUnreadCount()+in.available();
	}; 
	
	public long skip(long bytes)throws IOException
	{
		if (bytes<=0) return 0;
		long skipped = 0; 
		//We need to have a bit more efficient skip.
		{
			final int L = getUnreadCount();
			int to_skip_from_buffer = (int)(Math.min(bytes,L));
			read_ptr+=to_skip_from_buffer;
			if (read_ptr==end_ptr) 
			{
				read_ptr=0;
				end_ptr=0;
			};
			skipped+=to_skip_from_buffer;
			bytes-=to_skip_from_buffer;
		};
		//and what is left is to be skipped from input.
		if (bytes>=0)
		{
			skipped+=in.skip(bytes);
		};
		return skipped;
	};
	
	/** False, does not support it */
	public boolean markSupported(){ return false; };
	
	
	/* -------------------------------------------------------------------
	
		Push-back
	
	-------------------------------------------------------------------*/
	/** Un-reads
	@param c byte to un-read.
		After return from this method the next {@link #read} will
		return un-read byte. Notice, the 
		<pre>
			unread(1);unread(2);unread(3); results in reading 3,2,1
		</pre>
	@throws IOException if failed
	*/
	public void unread(byte c)throws IOException
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
	};
	/** Un-reads.
	Subsequent read will read bytes as present in an array 
	@param cbuf array, non null
	@throws IOException if failed
	*/
	public void unread(byte[] cbuf)throws IOException
	{
		assert(cbuf!=null);
		unread(cbuf,0,cbuf.length);
	};
	/** Un-reads.
	Subsequent read will read bytes as present in an array 
	@param cbuf array, non-null 
	@param off offset
	@param len length
	@throws IOException if failed
	*/
	public void unread(byte[] cbuf,int off,int len)throws IOException
	{
		assert(cbuf!=null);
		assert(off>=0);
		assert(len>=0);
		assert(off+len<=cbuf.length);
		
		
			ensureCapacity(len);
			if (read_ptr!=end_ptr)
			{
				//need to move it.
				System.arraycopy(buffer,read_ptr,buffer, read_ptr+len, end_ptr-read_ptr);
			}
			System.arraycopy(cbuf,off, buffer, read_ptr, len);
			end_ptr+=len;
	};
	
	
	/* -------------------------------------------------------------------
	
	
			Junit org Test arena
	
	
	-------------------------------------------------------------------*/
	public static final class Test extends sztejkat.utils.test.ATest
	{
		private static void readN(InputStream r, ByteArrayOutputStream sb, int fragment_length)throws IOException
		{
			int read=0;
			while(read<fragment_length)
			{
				int t= r.read();
				if (t==-1) throw new AssertionError("failed to read fragment");
				read++;
				sb.write(t);
			};
		};
		
		private static void readUpTo(InputStream r, ByteArrayOutputStream sb, int fragment_length)throws IOException
		{
			int read=0;
			while(read<fragment_length)
			{
				int t= r.read();
				if (t==-1) break;
				read++;
				sb.write(t);
			};
		};
		
		private void dump(CAdaptivePushBackInputStream r)
		{
			System.out.println("end_ptr="+r.end_ptr);
			System.out.println("read_ptr="+r.read_ptr);
			for(int i=0;i<r.buffer.length;i++)
			{
				System.out.print(r.buffer[i]+(r.buffer[i]>=32 ? ("("+(char)(r.buffer[i])+")") : "")+";");
			};
			System.out.println();
		};
		
		private static void readNcb(InputStream r, ByteArrayOutputStream sb, int fragment_length)throws IOException
		{
			int read=0;
			int remaning = fragment_length;
			byte [] buffer= new byte[fragment_length+10];
			int off = 10;
			while(remaning>0)
			{
				int t= r.read(buffer,off,remaning);
				if (t<=0) throw new AssertionError("failed to read fragment");
				read+=t;
				off+=t;
				remaning-=t;
			};
			sb.write(buffer,10,fragment_length);
		};
		@org.junit.Test public void testReadThrough()throws IOException
		{
			enter();
			final byte[] S = new byte[]{(byte)1,(byte)2,(byte)5,(byte)7,(byte)9,(byte)11,(byte)22,(byte)50,
									    (byte)3,(byte)4,(byte)9};
			
			CAdaptivePushBackInputStream R = new CAdaptivePushBackInputStream(new ByteArrayInputStream(S),8,8);
			
			ByteArrayOutputStream sb = new ByteArrayOutputStream();
			
			readN(R, sb, S.length);
			
			org.junit.Assert.assertTrue(java.util.Arrays.equals(sb.toByteArray(),S));
			org.junit.Assert.assertTrue(R.read()==-1);
			
			
			leave();
		};
		
		@org.junit.Test public void testReadThroughArray()throws IOException
		{
			enter();
			final byte[] S = new byte[]{(byte)1,(byte)2,(byte)5,(byte)7,(byte)9,(byte)11,(byte)22,(byte)50,
									    (byte)3,(byte)4,(byte)9};
			
			CAdaptivePushBackInputStream R = new CAdaptivePushBackInputStream(new ByteArrayInputStream(S),8,8);
			
			ByteArrayOutputStream sb = new ByteArrayOutputStream();
			
			readNcb(R, sb, S.length);
			
			org.junit.Assert.assertTrue(java.util.Arrays.equals(sb.toByteArray(),S));
			org.junit.Assert.assertTrue(R.read()==-1);
			
			
			leave();
		};
		
		@org.junit.Test public void testPushback_bytes()throws IOException
		{
			enter();
			
			CAdaptivePushBackInputStream R = new CAdaptivePushBackInputStream(
									new ByteArrayInputStream(
											new byte[]{(byte)'B',(byte)'L',(byte)'O',(byte)'N',(byte)'D',(byte)'I',(byte)'E'}
														),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread((byte)'x');
			dump(R);
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread((byte)'a');
			R.unread((byte)'v');
			R.unread((byte)'i');
			R.unread((byte)'e');
			R.unread((byte)'r');
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
			
			CAdaptivePushBackInputStream R = new CAdaptivePushBackInputStream(
									new ByteArrayInputStream(
											new byte[]{(byte)'B',(byte)'L',(byte)'O',(byte)'N',(byte)'D',(byte)'I',(byte)'E'}
														),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread(new byte[]{(byte)'x'});
			
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread(new byte[]{(byte)'a',(byte)'v',(byte)'i',(byte)'e',(byte)'r'});
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
			
			CAdaptivePushBackInputStream R = new CAdaptivePushBackInputStream(
									new ByteArrayInputStream(
											new byte[]{(byte)'B',(byte)'L',(byte)'O',(byte)'N',(byte)'D',(byte)'I',(byte)'E'}
														),3,3);
			
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread(new byte[]{(byte)'x'});
			
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread(new byte[]{(byte)'a',(byte)'v'});
			R.unread(new byte[]{(byte)'i',(byte)'e',(byte)'r'});
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
			
			CAdaptivePushBackInputStream R = new CAdaptivePushBackInputStream(
									new ByteArrayInputStream(
											new byte[]{(byte)'B',(byte)'L',(byte)'O',(byte)'N',(byte)'D',(byte)'I',(byte)'E'}
														),3,3);
																
														
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			R.unread(new byte[]{(byte)'-',(byte)'x',(byte)'c'},1,1);
			
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='N');
			R.unread(new byte[]{(byte)'p',(byte)'o',(byte)'a',(byte)'v',(byte)'q'},2,2);
			R.unread(new byte[]{(byte)'i',(byte)'e',(byte)'r',(byte)'c',(byte)'o'},0,3);
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
		
	
		
		@org.junit.Test public void testAvailable()throws IOException
		{
			enter();
			
			CAdaptivePushBackInputStream R = new CAdaptivePushBackInputStream(
									new ByteArrayInputStream(
											new byte[]{(byte)'B',(byte)'L',(byte)'O',(byte)'N',(byte)'D',(byte)'I',(byte)'E'}
														),3,3);
																
														
			org.junit.Assert.assertTrue(R.available()==7);
			R.unread((byte)'x');
			org.junit.Assert.assertTrue(R.available()==8);
			
			leave();
		};
		
		@org.junit.Test public void testSkipUnread()throws IOException
		{
			enter();
			
			CAdaptivePushBackInputStream R = new CAdaptivePushBackInputStream(
									new ByteArrayInputStream(
											new byte[]{(byte)'B',(byte)'L',(byte)'O',(byte)'N',(byte)'D',(byte)'I',(byte)'E'}
														),3,3);
																
														
			org.junit.Assert.assertTrue(R.available()==7);
			R.unread((byte)'x');
			R.unread((byte)'y');
			R.unread((byte)'z');
			System.out.println((char)R.read());
			R.skip(1);
			org.junit.Assert.assertTrue(R.read()=='x');
			org.junit.Assert.assertTrue(R.read()=='B');
			org.junit.Assert.assertTrue(R.read()=='L');
			leave();
		};
		@org.junit.Test public void testSkipMoreThanUnread()throws IOException
		{
			enter();
			
			CAdaptivePushBackInputStream R = new CAdaptivePushBackInputStream(
									new ByteArrayInputStream(
											new byte[]{(byte)'B',(byte)'L',(byte)'O',(byte)'N',(byte)'D',(byte)'I',(byte)'E'}
														),3,3);
																
														
			org.junit.Assert.assertTrue(R.available()==7);
			R.unread((byte)'x');
			R.unread((byte)'y');
			R.unread((byte)'z');
			System.out.println((char)R.read());
			R.skip(3);
			org.junit.Assert.assertTrue(R.read()=='L');
			org.junit.Assert.assertTrue(R.read()=='O');
			org.junit.Assert.assertTrue(R.read()=='N');
			leave();
		};
	};
};

