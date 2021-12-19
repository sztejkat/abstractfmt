package sztejkat.abstractfmt.util;
import java.io.Reader;
import java.io.IOException;

/**
	A reader which reads from input,
	performs some filtering, puts filtered
	data to output buffer and offers data
	from the output buffer.
	<p>
	Internally this class is very alike 
	{@link CAdaptivePushBackReader}
	but is organized a bit differently.
*/
public abstract class AAdaptiveFilterReader extends Reader
{
			/** Size increment for re-allocation */
			private final int size_increment;
			/** A filtered buffer.
			As long as there any data in buffer
			they are offered to read calls
			and when buffer becomes empty
			filtering do happen.
			*/
			private char [] buffer;
			/** End pointer to a {@link #buffer} */
			private int end_ptr;
			/** Read pointer from a buffer */
			private int read_ptr;
			
	/** 
	Creates
	@param in source to read from
	@param initial_size intial buffer capacity
	@param size_increment buffer growth increment.
	*/
	public AAdaptiveFilterReader(int initial_size,int size_increment)
	{
		assert(initial_size>0);
		assert(size_increment>0);
		this.size_increment = size_increment;
		this.buffer = new char[initial_size];
	};
	/* -------------------------------------------------------------------
	
		Services required from subclasses
	
	-------------------------------------------------------------------*/
	/** Invoked when buffer is empty. should perform
	reading from input and offer data with {@link #write(char)} 
	{@link #write(char [], int, int)} or {@link #write(CharSequence, int l)}.
	If this method does not write anything reading methods will return
	either partial read or end-of-file condition.
	@return --//--
	@throws IOException --//--
	*/
	protected abstract void filter()throws IOException;	
	
	/* -------------------------------------------------------------------
	
		Services for subclasses
	
	-------------------------------------------------------------------*/	
	protected final void write(char c)
	{
		ensureCapacity(1);
		buffer[end_ptr++]=c;
	};
	protected final void write(char [] buffer, int off, int len)
	{
		ensureCapacity(len);
		System.arraycopy(buffer,off,this.buffer,end_ptr,len);
		end_ptr+=len;
	};
	protected final void write(char [] buffer)
	{
		write(buffer,0,buffer.length);
	};
	protected final void write(CharSequence s, int len)
	{
		ensureCapacity(len);
		for(int i=0;i<len;i++)
		{
			buffer[end_ptr+i]=s.charAt(i);
		};		
		end_ptr+=len;
	};
	protected final void write(CharSequence s)
	{
		write(s,s.length());
	};
	/* -------------------------------------------------------------------
	
		Buffer managment
	
	-------------------------------------------------------------------*/
	private void validateNotClosed()throws IOException
	{
		if (buffer==null) throw new IOException("Closed");
	};
	private boolean isEmpty(){ return end_ptr==read_ptr; };
	/** Makes sure, that there is enough place in a buffer
	for data specified number of data
	@param characters number of required characters
	@throws IOException if failed
	*/
	private void ensureCapacity(int characters)
	{
		//Note: This method will be called only from filter
		//so only when read_ptr is at zero.
		assert(read_ptr==0):"can be called only from fill() and only on demand from read()";
		int free=buffer.length - end_ptr;
		if (free<characters)
		{
			//Need to grow buffer.
			int needs = (( characters + buffer.length - free -1) /size_increment +1)*size_increment;
			char [] new_buffer = new char[needs];
			System.arraycopy(buffer,0, new_buffer, 0, buffer.length);
			buffer = new_buffer;
		};
		assert(buffer.length - end_ptr >=characters);
	};
	/* -------------------------------------------------------------------
	
						Reader
	
	-------------------------------------------------------------------*/
	@Override public int read()throws IOException
	{
		synchronized(lock)
		{	
			validateNotClosed();		
			for(;;)
			{			
				final int L = end_ptr-read_ptr;
				assert(L>=0);
				if (L>0)
				{
					//from buffer.
					char c = buffer[read_ptr];
					read_ptr++;
					if (read_ptr==end_ptr) 
					{	//reset buffer.
						read_ptr=0;
						end_ptr=0;
					};
					return c;
				}else
				{
					//from filter
					filter();
					if (isEmpty()) return -1;	//filter did not produce any data.
				}
			}
		}
	};
	
	
	@Override public int read(char[] cbuf,
                         int off,
                         int len)
			 throws IOException
	{
		synchronized(lock)
		{
			//check if from buffer?
			validateNotClosed();
			int readen = 0;
			while(len>0)
			{
				final int L = end_ptr-read_ptr;
				assert(L>=0);
				if (L>0)
				{
					//from buffer, up to L bytes
					int to_read_from_buffer = L >= len ? len : L;
					//copy
					System.arraycopy(buffer, read_ptr, cbuf, off, to_read_from_buffer);
					read_ptr+=to_read_from_buffer;					
					if (read_ptr==end_ptr) 
					{  //reset buffer.
						read_ptr=0;
						end_ptr=0;
					};
					off+=to_read_from_buffer;
					readen+=to_read_from_buffer;
					len-=to_read_from_buffer;
				}else
				{
					//no data, attempt to fill-in
					filter();
					if (isEmpty()) break;
				};
			};
			return readen==0 ? -1: readen;
		}
	};
	
	@Override public void close()
	{
		synchronized(lock)
		{
			buffer=null;
			read_ptr=-1;
			end_ptr=-1;
		}
	};
	
	@Override public boolean ready()throws IOException
	{
		synchronized(lock){ return read_ptr!=end_ptr; }
	};
	/** False, does not support it */
	@Override public boolean markSupported(){ return false; };
	
	
	
	
	
	
	
};
