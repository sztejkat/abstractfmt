package sztejkat.abstractfmt;
import java.io.PipedReader;
/**
	A simplified toolbox class which is intended to 
	be used for testing purpose of character based formats
	and provides an ability to write and read characters from
	a certain buffer.
	<p>
	<i>Notes: This class could be a pair {@link PipedReader} and {@link PipedWriter}
	if those could be tuned to have a different behavior on circular buffer overrun.
	The primary intent of those pipes was to provide a blocking I/O to exchange
	data between <u>threads</u>. In this package and for testing I need a 
	different approach.
	</i>	
	<h1>Thread safety</h1>
	This class is NOT thread safe and is intended to be used in single
	thread to buffer and read characters, possibly interleaving those operations.
*/
public final class CCharExchangeBuffer
{	
			/** How much of size will be added to {@link #buffer}
			when it is necessary */
			private static final int GROWTH_INCREMENT = 1024;
			/** How much of data needs to be read from {@link #buffer}
			to move data to beginning of it. */
			private static final int MOVE_TRIGGER = GROWTH_INCREMENT/2;
			/** How much of data must be free in buffer after 
			move trigger to shrink it.
			The shrunken size will be current size + {@link #GROWTH_INCREMENT}
			so this value must be larger than {@link #GROWTH_INCREMENT} */
			private static final int SHRINK_TRIGGER = GROWTH_INCREMENT*2; //<-- must be greater.
			/** A buffer, which may adaptively grow */
			private char [] buffer;
			/** Where to write next character */
			private int write_at;
			/** Where to read next character */
			private int read_at;
			
		public CCharExchangeBuffer()
		{
			this.buffer = new char[GROWTH_INCREMENT];			
		};
		/** True if any character is available for reading
		@return false if do not have anything to read */
		private boolean available()
		{
			return (read_at<write_at);
		};
		/** Number of characters available for reading 
		@return chars */
		private int length()
		{
			return write_at-read_at;
		};
		
		public String toString()
		{
			return new String(
						buffer,//char[] value, int offset, int count)
						read_at,
						length());
		};
		/** Reads character or returns -1
		@return 0...0xFFFF or -1 if nothing to read
		*/
		public int read()
		{
			if (!available()) return -1;
			int at = read_at;
			char c = buffer[at++];
			if (at>MOVE_TRIGGER)
			{
					//move data or shrink and move.
					int wat = write_at;
					int L = wat - at;
					int free_after_move = buffer.length-L;
					if (free_after_move>SHRINK_TRIGGER)
					{
						//shrink AND move
						char [] nb = new char[L+GROWTH_INCREMENT];
						System.arraycopy(buffer,at, nb, 0, L);
						buffer = nb;
					}else
					{
						//just move
						System.arraycopy(buffer,at, buffer, 0, L);
					};
					//update pointers
					read_at =0;
					write_at=L;
			}else
			{
				//just update read pointer.
				read_at = at;
			};
			return c;
		};
		/** Writes character possibly enlarging buffer.
		No buffer limiting is applied */
		public void write(char c)
		{
			int at = write_at;
			//check if has free space?
			if (at==buffer.length)
			{
				buffer = java.util.Arrays.copyOf(buffer, at + GROWTH_INCREMENT);
			};
			//store
			buffer[at++] = c;
			//update pointer.
			write_at = at;
		};
		
		public void write(CharSequence s)
		{
			for(int i=0,n=s.length(); i<n; i++)
									write(s.charAt(i));
		};
		
		
		
		/* ********************************************************************************
		
		
		
					Junit org test arena
		
		
			Note: tests might need update if GROWTH_INCREMENT/MOVE_TRIGGER/SHRINK_TRIGGER
			are changed.
		
		* *********************************************************************************/
		public static final class Test extends sztejkat.utils.test.ATest
		{
			private static char of(int i){ return (char)((i*31) +i ); }
			private void testReadAndWrite(final int chunk_size, CCharExchangeBuffer b)
			{
				for(int i=0;i<chunk_size; i++)
				{
					b.write(of(i));
				};
				org.junit.Assert.assertTrue(b.length()==chunk_size);
				for(int i=0;i<chunk_size; i++)
				{
					org.junit.Assert.assertTrue(b.available());
					org.junit.Assert.assertTrue(b.read()==of(i));
				};
			};
			@org.junit.Test public void testReadAndWrite1()
			{	
				enter();
				/*
						We do small, sub growth/move/shring read-write test.
				*/
				CCharExchangeBuffer b = new CCharExchangeBuffer();
				for(int i=0;i<GROWTH_INCREMENT/16-1; i++)
				{
					testReadAndWrite(16,b);
				};
				leave();
			};
			
			
			@org.junit.Test public void testReadAndWrite2()
			{	
				enter();
				/*
						do large sub growth/move/shring read-write test
						which should trigger multiple increments and shrinks
				*/
				CCharExchangeBuffer b = new CCharExchangeBuffer();
				for(int i=0;i<GROWTH_INCREMENT*4-1; i++)
				{
					testReadAndWrite(128,b);
				};
				//And now take a look at buffer length
				int L = b.buffer.length;
				System.out.println("Buffer length after operation is "+L+" pointers are r:"+b.read_at+" w:"+b.write_at);
				org.junit.Assert.assertTrue( L<= SHRINK_TRIGGER );
				leave();
			};
			
			@org.junit.Test public void testReadAndWrite3()
			{	
				enter();
				/*
						do large sub growth/move/shring read-write test
						which should trigger multiple increments and shrinks
						with large operations.
				*/
				CCharExchangeBuffer b = new CCharExchangeBuffer();
				for(int i=0;i<10; i++)
				{
					testReadAndWrite(GROWTH_INCREMENT+5,b);
				};
				//And now take a look at buffer length
				int L = b.buffer.length;
				System.out.println("Buffer length after operation is "+L+" pointers are r:"+b.read_at+" w:"+b.write_at);
				org.junit.Assert.assertTrue( L<= SHRINK_TRIGGER );
				leave();
			};
		};
};