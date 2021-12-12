package sztejkat.abstractfmt;
import java.io.IOException;

/**
	An utility class which provide indicator state tracking
	for {@link IIndicatorReadFormat}
	<p>
	The signal read streams must be able to check what
	indicator is under a cursor without actually picking it.
	We need a bit of state machine for it.
	<p>
	This machine basically extends the API with
	{@link #getIndicator} which either returns
	cached value or refreshes cache from stream.
	<p>
	The {@link #readIndicator} behaves alike, but
	when returning cached value it invalidates cache.
	<p>
	All calls which do move cursor (all reads)
	do also invalidate the cache.
*/
final class CIndicatorReadFormatAdapter implements IIndicatorReadFormat
{
				/** Input */
				private final IIndicatorReadFormat in;
				/** Current indicator, reset by any operation
				which is moving a cursor. */
				private TIndicator current;
				
	CIndicatorReadFormatAdapter(IIndicatorReadFormat in)
	{
		this.in = in;
	};
	/** Retrives an indicator under a cursor.
	Does not move a cursor.
	@return what is under a cursor.
	@throws IOException as {@link #readIndicator}
	*/
	public TIndicator getIndicator()throws IOException
	{
		if (current==null) current = in.readIndicator();
		return current;
	};
	/** Invalidates cached indicator */
	private void invalidate(){ current=null; };
	
	public int getMaxRegistrations(){ return in.getMaxRegistrations(); };
	/**	
		If there is cached value, invalidates cache and returns it.
		If there is no cached value, returns it from stream and does NOT
		update cache, thous effectively consumig it.
		@return read indicator.
	*/
	public TIndicator readIndicator()throws IOException
	{
		if (current!=null)
		{
			TIndicator i  = current;
			current = null;
			return i;
		}
		return in.readIndicator();
	};
	public String getSignalName()
	{
		return in.getSignalName();
	};
	public int getSignalNumber()
	{
		return in.getSignalNumber();
	};
	public void skip()throws IOException
	{
		invalidate();	
		in.skip();
	};
	public boolean readBoolean()throws IOException
	{
		invalidate();
		return in.readBoolean();
	};
	public byte readByte()throws IOException
	{
		invalidate();
		return in.readByte();
	};
	public char readChar()throws IOException
	{
		invalidate();
		return in.readChar();
	};
	public short readShort()throws IOException
	{
		invalidate();
		return in.readShort();
	};
	public int readInt()throws IOException
	{
		invalidate();
		return in.readInt();
	};
	public long readLong()throws IOException
	{
		invalidate();
		return in.readLong();
	};
	public float readFloat()throws IOException
	{
		invalidate();
		return in.readFloat();
	};
	public double readDouble()throws IOException
	{
		invalidate();
		return in.readDouble();
	};
	public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException		
	{
		invalidate();
		return in.readBooleanBlock(buffer,offset,length);
	};
	public int readByteBlock(byte [] buffer, int offset, int length)throws IOException		
	{
		invalidate();
		return in.readByteBlock(buffer,offset,length);
	};
	public int readByteBlock()throws IOException
	{
		invalidate();
		return in.readByteBlock();
	};
	public int readCharBlock(Appendable characters,  int length)throws IOException
	{
		invalidate();
		return in.readCharBlock(characters,length);
	};
	public int readCharBlock(char [] buffer, int offset, int length)throws IOException		
	{
		invalidate();
		return in.readCharBlock(buffer,offset,length);
	};
	public int readShortBlock(short [] buffer, int offset, int length)throws IOException		
	{
		invalidate();
		return in.readShortBlock(buffer,offset,length);
	};
	public int readIntBlock(int [] buffer, int offset, int length)throws IOException		
	{
		invalidate();
		return in.readIntBlock(buffer,offset,length);
	};
	public int readLongBlock(long [] buffer, int offset, int length)throws IOException		
	{
		invalidate();
		return in.readLongBlock(buffer,offset,length);
	};
	public int readFloatBlock(float [] buffer, int offset, int length)throws IOException		
	{
		invalidate();
		return in.readFloatBlock(buffer,offset,length);
	};
	public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException		
	{
		invalidate();
		return in.readDoubleBlock(buffer,offset,length);
	};
	
	public void close()throws IOException
	{
		in.close();
	};
};