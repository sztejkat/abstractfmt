package sztejkat.abstractfmt;
import java.io.IOException;
/**
	This is an utility class which main purpose is to protect
	{@link IIndicatorReadFormat} against missuse.
	<p>
	The {@link IIndicatorReadFormat} contract does NOT require
	that it defends agains any miss-use. This simplifies implementing
	it a lot, but opens possible reliability problems. To fight with
	it this class exists and it protects against:
	<ul>
		<li>all calls which may result in unpredictable results;</li>
		<li>validates if indicator format which it protects does not
		violate contract itself;</li>
	</ul>
	but it does NOT protect against returning what was read from a stream
	which is malformed. For an example, if there are end indicators without
	begin, this is a correct situation. But if there is a call to primitive
	made when indicator was not validated that it is at DATA it is incorrect.
	<p>
	This class is throwing {@link AssertionError} if encounters
	a problem, but is NOT using <code>assert()</code> so the control
	is always enabled.
*/
public class CIndicatorReadFormatProtector implements IIndicatorReadFormat
{
				/** Bare, unprotected format */
				private IIndicatorReadFormat bare;
				
				private boolean is_open;
				private boolean is_closed;
				private int set_characters;
				
				/** Used to track what indicator is current */
				private TIndicator last_indicator;
				/** Used to track getIndicator() stability */
				private TIndicator last_get_indicator;
				/** Used to track what type indicator is current */
				private TIndicator last_type_indicator;
				
	/** Creates protector
	@param to_protect an indicator format to protect.
	*/
	public CIndicatorReadFormatProtector(IIndicatorReadFormat to_protect)
	{
		assert(to_protect!=null);
		this.bare=to_protect;
		this.set_characters=-1;
	};
	
	private void validateOpenWasCalled()throws AssertionError
	{
		if (!is_open) throw new AssertionError("Can't do it without open()");
	};
	private void validateNotClosed()throws AssertionError
	{
		if (is_closed) throw new AssertionError("Can't do it after close()");
	};	
	private void validateReady()throws AssertionError
	{
		validateOpenWasCalled();
		validateNotClosed();
	};
	
	/* ****************************************************
		
				IIndicatorReadFormat
		
	****************************************************/
	/* ---------------------------------------------------
		
				Information and settings.

	---------------------------------------------------*/
	@Override public int getMaxRegistrations()
	{
		final int r = bare.getMaxRegistrations();
		if (r<0) throw new AssertionError(bare.getClass()+".getMaxRegistrations() returned "+r);
		return r;
	};
	@Override public boolean isDescribed()
	{
		final boolean d = bare.isDescribed();
		if (!d)
		{
			if (bare.isFlushing()) throw new AssertionError(bare.getClass()+".isDescribed() returned false, but isFlushing() returned true");
		};
		return d;
	};
	@Override public boolean isFlushing()
	{
		final boolean d = bare.isFlushing();
		if (d)
		{
			if (!bare.isDescribed()) throw new AssertionError(bare.getClass()+".isFlushing() returned true, but isDescribed() returned false");
		};
		return d;
	};
	@Override public int getMaxSupportedSignalNameLength()
	{
		final int l = bare.getMaxSupportedSignalNameLength();
		if (l<=0) throw new AssertionError(bare.getClass()+".getMaxSupportedSignalNameLength() returned "+l);
		return l;
	};
	@Override public void setMaxSignalNameLength(int characters)
	{
		if (is_open) throw new AssertionError("calling setMaxSignalNameLength() when stream is open results in unpredictable effects");
		validateNotClosed();
		if (characters<=0) throw new AssertionError("characters="+characters+" is negative or zero");	
		if (characters>bare.getMaxSupportedSignalNameLength())
			throw new AssertionError("characters="+characters+" is greater than getMaxSupportedSignalNameLength()="+bare.getMaxSupportedSignalNameLength());
		set_characters =characters;
		bare.setMaxSignalNameLength(characters);
	};
	@Override public int getMaxSignalNameLength()
	{
		final int c = bare.getMaxSignalNameLength();
		if ((set_characters!=-1)&&(c!=set_characters))
			throw new AssertionError(bare.getClass()+".getMaxSignalNameLength() returned "+c+" while "+set_characters+" was recently set");
		if (c<=0) throw new AssertionError(bare.getClass()+".getMaxSignalNameLength() returned "+c+" but should return positive non-zero");
		if (c>bare.getMaxSupportedSignalNameLength()) 
			throw new AssertionError(bare.getClass()+".getMaxSignalNameLength() returned "+c+" what is above getMaxSupportedSignalNameLength()");
		return c;
	};
	
	/* ------------------------------------------------------
	
			Indicators.			
	
	
	------------------------------------------------------*/
	@Override public TIndicator getIndicator()throws IOException
	{
		validateReady();
		TIndicator i = bare.getIndicator();
		if (i==null) throw new AssertionError(bare.getClass()+".getIndicator() returned null");
		//Now last_indicator may be used to track both readIndicator()
		//and getIndicator(). We need dedicated field to track if value is stable.
		if ((last_get_indicator!=null)&&(i!=last_get_indicator))
				throw new AssertionError("Detected unexpected indicator change, last returned was "+last_get_indicator+
									" but "+bare.getClass()+".getIndicator() returned "+i);
		last_indicator =  i;
		last_get_indicator = i;
		if ((i.FLAGS & TIndicator.TYPE)!=0) last_type_indicator = i;
				else 
		if (i!=TIndicator.DATA)	last_type_indicator = null;
		return i;
	};
	@Override public void next()throws IOException
	{
		validateReady();
		last_get_indicator=null;
		last_indicator =null;
		last_type_indicator = null;
		bare.next();
	};
	@Override public void skip()throws IOException
	{
		validateReady();
		last_get_indicator=null;
		last_indicator =null;
		last_type_indicator = null;
		bare.skip();
	};
	@Override public TIndicator readIndicator()throws IOException
	{
		validateReady();
		final TIndicator i = bare.readIndicator();
		if (i==null) throw new AssertionError(bare.getClass()+".readIndicator() returned null");		
		last_indicator =i;
		last_get_indicator=null;
		last_type_indicator = null;
		return i;
	};
	@Override public String getSignalName()
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use getSignalName() without valid getIndicator()");
		if ((last_indicator.FLAGS & TIndicator.NAME)==0)
			throw new AssertionError("Can't use getSignalName() with "+last_indicator+" indicator");
		final String n = bare.getSignalName();
		if (n==null) throw new AssertionError(bare.getClass()+".getSignalName() returned null");
		if (n.length()>bare.getMaxSupportedSignalNameLength()) 
				throw new AssertionError(bare.getClass()+".getSignalName() returned too long name, name length barrier failed?");
		return n;			
	};
	@Override public int getSignalNumber()
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use getSignalNumber() without valid getIndicator()");
		if ((last_indicator.FLAGS & TIndicator.REGISTER)==0)
			throw new AssertionError("Can't use getSignalNumber() with "+last_indicator+" indicator");
		final int n = bare.getSignalNumber();
		if (n<0) throw new AssertionError(bare.getClass()+".getSignalNumber() returned negative");
		return n;			
	};
	/* ------------------------------------------------------
	
			Elementary reads.
	
	------------------------------------------------------*/
	@Override public boolean readBoolean()throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readBoolean() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_BOOLEAN)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readBoolean() with indicator different than DATA, but it is now "+last_indicator);
				
		last_indicator=null;
		last_get_indicator=null;
		return bare.readBoolean();	
	};
	@Override public byte readByte()throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readByte() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_BYTE)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readByte() with indicator different than DATA, but it is now "+last_indicator);
				
		last_indicator=null;
		last_get_indicator=null;
		return bare.readByte();	
	};
	@Override public char readChar()throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readChar() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_CHAR)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readChar() with indicator different than DATA, but it is now "+last_indicator);
				
		last_indicator=null;
		last_get_indicator=null;
		return bare.readChar();	
	};
	@Override public short readShort()throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readShort() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_SHORT)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readShort() with indicator different than DATA, but it is now "+last_indicator);
				
		last_indicator=null;
		last_get_indicator=null;
		return bare.readShort();	
	};
	@Override public int readInt()throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readInt() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_INT)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readInt() with indicator different than DATA, but it is now "+last_indicator);
				
		last_indicator=null;
		last_get_indicator=null;
		return bare.readInt();	
	};
	@Override public long readLong()throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readLong() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_LONG)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readLong() with indicator different than DATA, but it is now "+last_indicator);
				
		last_indicator=null;
		last_get_indicator=null;
		return bare.readLong();	
	};
	@Override public float readFloat()throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readFloat() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_FLOAT)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readFloat() with indicator different than DATA, but it is now "+last_indicator);
				
		last_indicator=null;
		last_get_indicator=null;
		return bare.readFloat();	
	};
	@Override public double readDouble()throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readDouble() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_DOUBLE)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readDouble() with indicator different than DATA, but it is now "+last_indicator);
				
		last_indicator=null;
		last_get_indicator=null;
		return bare.readDouble();	
	};
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readBooleanBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_BOOLEAN_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_BOOLEAN_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readBooleanBlock() with indicator different than DATA, but it is now "+last_indicator);				
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		last_indicator=null;
		last_get_indicator=null;
		final int r = bare.readBooleanBlock(buffer,offset,length);
		if (r<0) throw new AssertionError(bare.getClass()+".readBooleanBlock() returned "+r);
		if (r>length) throw new AssertionError(bare.getClass()+".readBooleanBlock() returned "+r+" which reports longer read than requested");
		return r;
	}		
	
	@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readByteBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_BYTE_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_BYTE_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readByteBlock() with indicator different than DATA, but it is now "+last_indicator);				
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		last_indicator=null;
		last_get_indicator=null;
		final int r = bare.readByteBlock(buffer,offset,length);
		if (r<0) throw new AssertionError(bare.getClass()+".readByteBlock() returned "+r);
		if (r>length) throw new AssertionError(bare.getClass()+".readByteBlock() returned "+r+" which reports longer read than requested");
		return r;
	}	
	
	@Override public int readByteBlock()throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readByteBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_BYTE_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_BYTE_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readByteBlock() with indicator different than DATA, but it is now "+last_indicator);				
		last_indicator=null;
		last_get_indicator=null;
		final int r = bare.readByteBlock();
		if ((r<-1)||(r>255)) throw new AssertionError(bare.getClass()+".readByteBlock() returned "+r);
		return r;
	}	
	
		
	
	@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readCharBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_CHAR_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_CHAR_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readCharBlock() with indicator different than DATA, but it is now "+last_indicator);				
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		last_indicator=null;
		last_get_indicator=null;
		final int r = bare.readCharBlock(buffer,offset,length);
		if (r<0) throw new AssertionError(bare.getClass()+".readCharBlock() returned "+r);
		if (r>length) throw new AssertionError(bare.getClass()+".readCharBlock() returned "+r+" which reports longer read than requested");
		return r;
	}		
	@Override public int readCharBlock(Appendable characters,  int length)throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readCharBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_CHAR_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_CHAR_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readCharBlock() with indicator different than DATA, but it is now "+last_indicator);				
		if (characters==null) 
			throw new AssertionError("characters is null");
		if (length<0) 
			throw new AssertionError("length is negative");
		last_indicator=null;
		last_get_indicator=null;
		final int r = bare.readCharBlock(characters,length);
		if (r<0) throw new AssertionError(bare.getClass()+".readCharBlock() returned "+r);
		if (r>length) throw new AssertionError(bare.getClass()+".readCharBlock() returned "+r+" which reports longer read than requested");
		return r;
	}			
	
	@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readShortBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_SHORT_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_SHORT_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readShortBlock() with indicator different than DATA, but it is now "+last_indicator);				
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		last_indicator=null;
		last_get_indicator=null;
		final int r = bare.readShortBlock(buffer,offset,length);
		if (r<0) throw new AssertionError(bare.getClass()+".readShortBlock() returned "+r);
		if (r>length) throw new AssertionError(bare.getClass()+".readShortBlock() returned "+r+" which reports longer read than requested");
		return r;
	}		
	
	@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readIntBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_INT_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_INT_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readIntBlock() with indicator different than DATA, but it is now "+last_indicator);				
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		last_indicator=null;
		last_get_indicator=null;
		final int r = bare.readIntBlock(buffer,offset,length);
		if (r<0) throw new AssertionError(bare.getClass()+".readIntBlock() returned "+r);
		if (r>length) throw new AssertionError(bare.getClass()+".readIntBlock() returned "+r+" which reports longer read than requested");
		return r;
	}		
	
	@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readLongBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_LONG_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_LONG_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readLongBlock() with indicator different than DATA, but it is now "+last_indicator);				
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		last_indicator=null;
		last_get_indicator=null;
		final int r = bare.readLongBlock(buffer,offset,length);
		if (r<0) throw new AssertionError(bare.getClass()+".readLongBlock() returned "+r);
		if (r>length) throw new AssertionError(bare.getClass()+".readLongBlock() returned "+r+" which reports longer read than requested");
		return r;
	}
	
	@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_indicator==null) throw new AssertionError("Can't use readFloatBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_FLOAT_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_FLOAT_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readFloatBlock() with indicator different than DATA, but it is now "+last_indicator);				
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		last_indicator=null;
		final int r = bare.readFloatBlock(buffer,offset,length);
		if (r<0) throw new AssertionError(bare.getClass()+".readFloatBlock() returned "+r);
		if (r>length) throw new AssertionError(bare.getClass()+".readFloatBlock() returned "+r+" which reports longer read than requested");
		return r;
	}	
	
	@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		validateReady();		
		if (last_indicator==null) throw new AssertionError("Can't use readDoubleBlock() without valid getIndicator()");
		if ((last_type_indicator!=null)&&(last_type_indicator!=TIndicator.TYPE_DOUBLE_BLOCK)) 
								  throw new AssertionError("Request does not match "+last_type_indicator);
		last_type_indicator = TIndicator.TYPE_DOUBLE_BLOCK;
		if (last_indicator!=TIndicator.DATA)
				throw new AssertionError("Can't use readDoubleBlock() with indicator different than DATA, but it is now "+last_indicator);				
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		last_indicator=null;
		last_get_indicator=null;
		final int r = bare.readDoubleBlock(buffer,offset,length);
		if (r<0) throw new AssertionError(bare.getClass()+".readDoubleBlock() returned "+r);
		if (r>length) throw new AssertionError(bare.getClass()+".readDoubleBlock() returned "+r+" which reports longer read than requested");
		return r;
	}	
	
		/* ---------------------------------------------------
		
				State, Closeable, Flushable		
		
	---------------------------------------------------*/
	
	@Override public void open()throws IOException
	{
		validateNotClosed();
		if (is_open) throw new AssertionError("Calling open() twice may have unpredictable results");
		is_open = true;
		bare.open();
	};
	@Override public void close()throws IOException
	{
		if (is_closed) throw new AssertionError("Calling call() twice may have unpredictable results");
		is_closed = true;
		bare.close();
	};
					
};