package sztejkat.abstractfmt.util;
/**
	Like a {@link CCharExchangeBuffer} but 
	for byte Input/Output Streams.
*/
public class CByteExchangeBuffer
{	
		private class _Input extends java.io.InputStream
		{
			public int read()
			{
				return CByteExchangeBuffer.this.read();
			};
			public int available(){ return CByteExchangeBuffer.this.length(); };
			public void close(){};
		}; 
		private class _Output extends java.io.OutputStream
		{
			public void close(){};
			public void flush(){};
			public void write(int c)
			{
				CByteExchangeBuffer.this.write((byte)c);
			};			
		};
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
			private byte [] buffer;
			/** Where to write next character */
			private int write_at;
			/** Where to read next character */
			private int read_at;
			/** Lazy intialized support classes */
			private _Input reader;
			/** Lazy intialized support classes */
			private _Output writer;
			
			
			
		/* **************************************************
		
				Creation
		
		***************************************************/
		public CByteExchangeBuffer()
		{
			this.buffer = new byte[GROWTH_INCREMENT];			
		};
		
		/* **************************************************
		
				Informative routines.
		
		***************************************************/
		/** Number of bytes available for reading 
		@return bytes */
		public int length()
		{
			return write_at-read_at;
		};
		/** True if any byte is available for reading
		@return false if do not have anything to read */
		public boolean available()
		{
			return (read_at<write_at);
		};
		
		
		/** Converts readable part to independent string*/
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			for(int i=0,n =length();i<n;i++)
			{
				if (i!=0)sb.append(',');
				int v = 0xFF & buffer[read_at+i];
				sb.append("0x"+Integer.toHexString(v));
				if ((v>=32)&&(v<=255))
					sb.append("("+(char)v+")");
			}; 
			return sb.toString();
		};
		
		/* **************************************************
		
				java.io.InputStream/OutputStream API
		
		***************************************************/
		/** Returns reader of that buffer 
		@return life time constant.
		*/
		public java.io.InputStream getReader()
		{
			if (reader==null)
				reader = new _Input();
			return reader;
		};
		/** Returns writer of that buffer 
		@return life time constant.
		*/
		public java.io.OutputStream getWriter()
		{
			if (writer==null)
				writer = new _Output();
			return writer;
		};
		
		/* **************************************************
		
			Direct reading API
		
		***************************************************/
		/** Reads byte or returns -1
		@return 0...0xFF or -1 if nothing to read
		*/
		public int read()
		{
			if (!available()) return -1;
			int at = read_at;
			byte c = buffer[at++];
			if (at>MOVE_TRIGGER)
			{
					//move data or shrink and move.
					int wat = write_at;
					int L = wat - at;
					int free_after_move = buffer.length-L;
					if (free_after_move>SHRINK_TRIGGER)
					{
						//shrink AND move
						byte [] nb = new byte[L+GROWTH_INCREMENT];
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
			return c & 0xFF;
		};
		
		
		/* **************************************************
		
			Direct writing
		
		***************************************************/
		/** Writes byte possibly enlarging buffer.
		No buffer limiting is applied 
		@param c byte to write
		*/
		public void write(byte c)
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
		
		
		
		
		
		/* ********************************************************************************
		
		
		
					Junit org test arena
		
		
			Note: tests might need update if GROWTH_INCREMENT/MOVE_TRIGGER/SHRINK_TRIGGER
			are changed.
		
		* *********************************************************************************/
		public static final class Test extends sztejkat.utils.test.ATest
		{
			private static byte of(int i){ return (byte)((i*31) +i-100 ); }
			private void testReadAndWrite(final int chunk_size, CByteExchangeBuffer b)
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
				CByteExchangeBuffer b = new CByteExchangeBuffer();
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
				CByteExchangeBuffer b = new CByteExchangeBuffer();
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
				CByteExchangeBuffer b = new CByteExchangeBuffer();
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