package sztejkat.abstractfmt;
import java.io.IOException;
/**
	This is an utility class which main purpose is to protect
	{@link IIndicatorWriteFormat} against missuse.
	<p>
	The {@link IIndicatorWriteFormat} contract does NOT require
	that it defends agains any miss-use. This simplifies implementing
	it a lot, but opens possible reliability problems. To fight with
	it this class exists and it protects against:
	<ul>
		<li>all calls which may result in unpredictable results;</li>
		<li>validates if indicator format which it protects does not
		violate contract itself, if it is possible to test without
		causing side effects;</li>
	</ul>
	<p>
	This class is throwing {@link AssertionError} if encounters
	a problem, but is NOT using <code>assert()</code> so the control
	is always enabled.
	<p>
	Not for production use.
*/
public class CIndicatorWriteFormatProtector implements IIndicatorWriteFormat
{
				/** Bare, unprotected format */
				private IIndicatorWriteFormat bare;
				
				private boolean is_open;
				private boolean is_closed;
				
				private int begin_counter;
				private int expected_register_number;
				
				private TIndicator last_type;
				private boolean primitive_written;
				private final boolean validate_if_flushed_before_close;
				private boolean is_flushed;
				
	/** Creates protector
	@param to_protect an indicator format to protect.
	*/
	public CIndicatorWriteFormatProtector(IIndicatorWriteFormat to_protect)
	{
		this(to_protect, false);
	};
	/** Creates protector
	@param to_protect an indicator format to protect.
	@param validate_if_flushed_before_close if true {@link #close} without
		{@link #flush} will throw. 
	*/
	public CIndicatorWriteFormatProtector(IIndicatorWriteFormat to_protect,
										boolean validate_if_flushed_before_close)
	{
		assert(to_protect!=null);
		this.bare=to_protect;
		this.validate_if_flushed_before_close=validate_if_flushed_before_close;
		this.is_flushed = true; //to allow close without open.
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
				
	/* ****************************************************************
	
			IIndicatorWriteFormat
	
	*****************************************************************/
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
	/* ---------------------------------------------------
		
				Signals related indicators.
		
	---------------------------------------------------*/
	@Override public void writeBeginDirect(String signal_name)throws IOException
	{
		validateReady();
		if (signal_name==null) throw new AssertionError("Null signal name");
		if (signal_name.length()>bare.getMaxSupportedSignalNameLength())
			 throw new AssertionError("Signal name longer than supported");
		begin_counter++; if (begin_counter<0) throw new IllegalStateException("more than 2^32 begins");
		bare.writeBeginDirect(signal_name);
		last_type = null;
		primitive_written=false;
		is_flushed = false;
	};
	@Override public void writeEndBeginDirect(String signal_name)throws IOException
	{
		validateReady();
		if (signal_name==null) throw new AssertionError("Null signal name");
		if (signal_name.length()>bare.getMaxSupportedSignalNameLength())
			 throw new AssertionError("Signal name longer than supported");
		if (begin_counter==0)
			throw new AssertionError("Writing end without begin is not allowed");
		bare.writeEndBeginDirect(signal_name);
		last_type = null;
		primitive_written=false;
		is_flushed = false;
	};
	@Override public void writeBeginRegister(String signal_name, int number)throws IOException
	{
		validateReady();
		if (signal_name==null) throw new AssertionError("Null signal name");
		if (signal_name.length()>bare.getMaxSupportedSignalNameLength())
			 throw new AssertionError("Signal name longer than supported");
		if (number<0)
			 throw new AssertionError("Signal number "+number+" negative");
		if (number>=bare.getMaxRegistrations())
			 throw new AssertionError("Signal number "+number+" too large");
		if (expected_register_number!=number)
			 throw new AssertionError("Signal number "+number+" out of expected sequence");		
		expected_register_number++;
		begin_counter++; if (begin_counter<0) throw new IllegalStateException("more than 2^32 begins");
		bare.writeBeginRegister(signal_name, number);
		last_type = null;
		primitive_written=false;
		is_flushed = false;
	};
	@Override public void writeEndBeginRegister(String signal_name, int number)throws IOException		
	{
		validateReady();
		if (signal_name==null) throw new AssertionError("Null signal name");
		if (signal_name.length()>bare.getMaxSupportedSignalNameLength())
			 throw new AssertionError("Signal name longer than supported");
		if (number<0)
			 throw new AssertionError("Signal number "+number+" negative");
		if (number>=bare.getMaxRegistrations())
			 throw new AssertionError("Signal number "+number+" too large");
		if (expected_register_number!=number)
			 throw new AssertionError("Signal number "+number+" out of expected sequence");
		if (begin_counter==0)
			throw new AssertionError("Writing end without begin is not allowed");
			
		expected_register_number++;
		bare.writeEndBeginRegister(signal_name, number);
		last_type = null;
		primitive_written=false;
		is_flushed = false;
	};	
	@Override public void writeBeginUse(int number)throws IOException
	{
		validateReady();
		if (number<0)
			 throw new AssertionError("Signal number "+number+" negative");
		if (number>=bare.getMaxRegistrations())
			 throw new AssertionError("Signal number "+number+" too large");
		if (number>=expected_register_number)
			throw new AssertionError("Signal number "+number+" not registered yet.");
		begin_counter++; if (begin_counter<0) throw new IllegalStateException("more than 2^32 begins");
		bare.writeBeginUse(number);
		last_type = null;
		primitive_written=false;
		is_flushed = false;
	};
	@Override public void writeEndBeginUse(int number)throws IOException
	{
		validateReady();
		if (number<0)
			 throw new AssertionError("Signal number "+number+" negative");
		if (number>=bare.getMaxRegistrations())
			 throw new AssertionError("Signal number "+number+" too large");
		if (number>=expected_register_number)
			throw new AssertionError("Signal number "+number+" not registered yet.");
		if (begin_counter==0)
			throw new AssertionError("Writing end without begin is not allowed");		
		bare.writeEndBeginUse(number);
		last_type = null;
		primitive_written=false;
		is_flushed = false;
	};
	@Override public void writeEnd()throws IOException
	{
		validateReady();
		if (begin_counter==0)
			throw new AssertionError("Writing end without begin is not allowed");
		begin_counter--;	
		bare.writeEnd();
		last_type = null;
		primitive_written=false;
		is_flushed = false;
	};
	/* ---------------------------------------------------
		
				Type related indicators.
		
	---------------------------------------------------*/
	@Override public void writeType(TIndicator type)throws IOException
	{
		validateReady();
		if ((type.FLAGS & TIndicator.TYPE)==0) throw new AssertionError(type+" is not TYPE");
		if (last_type!=null)
			throw new AssertionError("Writing type twice");
		last_type =type;
		bare.writeType(type);
		primitive_written = false;
		is_flushed = false;
	};
	@Override public void writeFlush(TIndicator flush)throws IOException
	{
		validateReady();
		if ((flush.FLAGS & TIndicator.FLUSH)==0) throw new AssertionError(flush+" is not FLUSH");
		if ((flush.FLAGS & TIndicator.READ_ONLY)!=0) throw new AssertionError(flush+" is READ_ONLY");
		if (last_type==null)
			throw new AssertionError("Writing flush without type");
		if (flush!=TIndicator.getFlush(last_type))
			throw new AssertionError(flush+" does not match "+last_type);
		if ((!primitive_written)&&((flush.FLAGS & TIndicator.BLOCK)==0))
			throw new AssertionError("elementary flush, but no elementary primitive data were written.");
		last_type =null;
		bare.writeFlush(flush);
		primitive_written = false;
		is_flushed = false;
	};
	/* ---------------------------------------------------
		
				IPrimitiveWriteFormat
		
	---------------------------------------------------*/
	@Override public void writeBoolean(boolean v)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_BOOLEAN)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (primitive_written)
			throw new AssertionError("Writing primitive twice");
		primitive_written = true;
		is_flushed = false;
		bare.writeBoolean(v);
	};
	
	@Override public void writeByte(byte v)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_BYTE)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (primitive_written)
			throw new AssertionError("Writing primitive twice");
		primitive_written = true;
		is_flushed = false;
		bare.writeByte(v);
	};
	
	@Override public void writeChar(char v)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_CHAR)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (primitive_written)
			throw new AssertionError("Writing primitive twice");
		primitive_written = true;
		is_flushed = false;
		bare.writeChar(v);
	};
	
	@Override public void writeShort(short v)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_SHORT)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (primitive_written)
			throw new AssertionError("Writing primitive twice");
		primitive_written = true;
		is_flushed = false;
		bare.writeShort(v);
	};
	@Override public void writeInt(int v)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_INT)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (primitive_written)
			throw new AssertionError("Writing primitive twice");
		primitive_written = true;
		is_flushed = false;
		bare.writeInt(v);
	};	
	@Override public void writeLong(long v)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_LONG)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (primitive_written)
			throw new AssertionError("Writing primitive twice");
		primitive_written = true;
		is_flushed = false;
		bare.writeLong(v);
	};	
	@Override public void writeFloat(float v)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_FLOAT)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (primitive_written)
			throw new AssertionError("Writing primitive twice");
		primitive_written = true;
		is_flushed = false;
		bare.writeFloat(v);
	};	
	@Override public void writeDouble(double v)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_DOUBLE)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (primitive_written)
			throw new AssertionError("Writing primitive twice");
		primitive_written = true;
		is_flushed = false;
		bare.writeDouble(v);
	};	
	
	
	@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_BOOLEAN_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		primitive_written = true;
		is_flushed = false;
		bare.writeBooleanBlock(buffer,offset,length);
	};	
	
	@Override public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_BYTE_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		primitive_written = true;
		is_flushed = false;
		bare.writeByteBlock(buffer,offset,length);
	};	
	
	
	@Override public void writeByteBlock(byte data)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_BYTE_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		primitive_written = true;	
		is_flushed = false;	
		bare.writeByteBlock(data);
	};	
	
	@Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_CHAR_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		primitive_written = true;
		is_flushed = false;
		bare.writeCharBlock(buffer,offset,length);
	};	
	
	@Override public void writeCharBlock(CharSequence characters, int offset, int length)throws IOException		
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_CHAR_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (characters==null) 
			throw new AssertionError("characters is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>characters.length()) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+characters.length());
		primitive_written = true;		
		is_flushed = false;
		bare.writeCharBlock(characters,offset,length);
	};	
	
	@Override public void writeShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_SHORT_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		primitive_written = true;
		is_flushed = false;
		bare.writeShortBlock(buffer,offset,length);
	};	
	
	
	@Override public void writeIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_INT_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		primitive_written = true;
		is_flushed = false;
		bare.writeIntBlock(buffer,offset,length);
	};	
	
	
	@Override public void writeLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_LONG_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		primitive_written = true;
		is_flushed = false;
		bare.writeLongBlock(buffer,offset,length);
	};	
	
	@Override public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_FLOAT_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		primitive_written = true;
		is_flushed = false;
		bare.writeFloatBlock(buffer,offset,length);
	};	
	
	@Override public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		validateReady();
		if (last_type==null)
			throw new AssertionError("Writing primitive without type");
		if (last_type!=TIndicator.TYPE_DOUBLE_BLOCK)
			throw new AssertionError("Writing primitive without incorrect type, "+last_type+" expected");
		if (buffer==null) 
			throw new AssertionError("buffer is null");
		if (offset<0) 
			throw new AssertionError("offset is negative");
		if (length<0) 
			throw new AssertionError("length is negative");
		if (length+offset>buffer.length) 
			throw new AssertionError("length+offset="+(length+offset)+" outside buffer boundary "+buffer.length);
		primitive_written = true;
		is_flushed = false;
		bare.writeDoubleBlock(buffer,offset,length);
	};	
	
	
	/* ---------------------------------------------------
		
				State, Closeable, Flushable		
		
	---------------------------------------------------*/
	@Override public void flush()throws IOException
	{
		validateReady();
		bare.flush();
		is_flushed = true;
	};	
	@Override public void open()throws IOException
	{
		validateNotClosed();
		if (is_open) throw new AssertionError("Calling open() twice may have unpredictable results");
		is_open = true;
		is_flushed = false;	//because open could have written some data.
		bare.open();
	};
	@Override public void close()throws IOException
	{
		if (is_closed) throw new AssertionError("Calling close() twice may have unpredictable results");
		is_closed = true;
		if (validate_if_flushed_before_close)
		{
			if (!is_flushed)
				throw new AssertionError("Calling close() after open with some un-flushed data may result in unpredictable read errors."); 
		}
		bare.close();
	};
	
};