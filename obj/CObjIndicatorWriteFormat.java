package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;
import java.util.Arrays;
import java.io.IOException;

/**
	An indicator format implementation over {@link CObjListFormat} media.
	<p>
	For best results wrap it in {@link CIndicatorWriteFormatProtector}.
*/
public class CObjIndicatorWriteFormat implements IIndicatorWriteFormat
{
			/** Media in use */
			private final CObjListFormat media;
			/** Design limit */
			private final int max_registrations;
			/** Design limit */
			private final int max_supported_signal_name_length;
			/** Support status to report, also controls what type
			writes do. */
			private final boolean is_described;
			/** Support status to report, also controls what flush
			writes do. */
			private final boolean is_flushing;
			
			
		/** Creates
		@param media non null media to write to
		@param max_registrations number returned from {@link #getMaxRegistrations}
		@param max_supported_signal_name_length number returned from {@link #getMaxSupportedSignalNameLength}		
		@param is_described returned from {@link #isDescribed}. If false
			type writes are non-op
		@param is_flushing returned from {@link #isFlushing}. If false
			type writes are non-op
		@throws AssertionError is something is wrong.
		*/
		public CObjIndicatorWriteFormat(final CObjListFormat media,
										final int max_registrations,
										final int max_supported_signal_name_length,										
										final boolean is_described,
										final boolean is_flushing
											)
		{
			assert(media!=null);
			assert(max_registrations>=0);
			assert(max_supported_signal_name_length>0);			
			assert( !is_flushing || (is_flushing && is_described)):"invalid is_described/is_flushing combination";
			this.media = media; 
			this.max_registrations=max_registrations;
			this.max_supported_signal_name_length = max_supported_signal_name_length;			
			this.is_described=is_described;
			this.is_flushing=is_flushing;
		};
		
		/* ***************************************************************
		
		
				IIndicatorWriteFormat
		
		
		****************************************************************/
		/* ------------------------------------------------------
		
				Information and settings
		
		------------------------------------------------------*/
		@Override public final int getMaxRegistrations(){ return max_registrations; };
		@Override public final boolean isDescribed(){return is_described; };
		@Override public final boolean isFlushing(){return is_flushing; };
		@Override public final int getMaxSupportedSignalNameLength()
		{
			return max_supported_signal_name_length;
		};
		/* ------------------------------------------------------
		
				Signals
		
		------------------------------------------------------*/
		@Override public void writeBeginDirect(String signal_name)throws IOException
		{
			assert(signal_name!=null);
			media.add(TIndicator.BEGIN_DIRECT);
			media.add(signal_name);
		};
		@Override public void writeEndBeginDirect(String signal_name)throws IOException		
		{
			assert(signal_name!=null);
			media.add(TIndicator.END_BEGIN_DIRECT);
			media.add(signal_name);
		};
		@Override public void writeBeginRegister(String signal_name, int number)throws IOException
		{
			assert(signal_name!=null);
			assert(number>=0);
			media.add(TIndicator.BEGIN_REGISTER);
			media.add(Integer.valueOf(number));
			media.add(signal_name);
		};
		@Override public void writeEndBeginRegister(String signal_name, int number)throws IOException
		{
			assert(number>=0);
			assert(signal_name!=null);
			media.add(TIndicator.END_BEGIN_REGISTER);
			media.add(Integer.valueOf(number));
			media.add(signal_name);
		};
		@Override public void writeBeginUse(int number)throws IOException
		{
			assert(number>=0);
			media.add(TIndicator.BEGIN_USE);
			media.add(Integer.valueOf(number));
		};
		@Override public void writeEndBeginUse( int number)throws IOException
		{
			assert(number>=0);
			media.add(TIndicator.END_BEGIN_USE);
			media.add(Integer.valueOf(number));
		};
		@Override public void writeEnd()throws IOException
		{
			media.add(TIndicator.END);
		};
		@Override public void writeType(TIndicator type)throws IOException
		{
			assert(type!=null);
			assert((type.FLAGS & TIndicator.TYPE)!=0);
			if (is_described)
				media.add(type);
		};
		@Override public void writeFlush(TIndicator flush)throws IOException
		{
			assert(flush!=null);
			assert((flush.FLAGS & TIndicator.FLUSH)!=0);
			assert((flush.FLAGS & TIndicator.READ_ONLY)==0);
			if (is_flushing)
				media.add(flush);
		};
		/* ------------------------------------------------------
		
				Closeable
		
		------------------------------------------------------*/
		@Override public void open()throws IOException
		{
			media.add(CObjListFormat.OPEN);
		};
		@Override public void close()throws IOException
		{
			flush();
		};
		/* ------------------------------------------------------
		
				Flushable
		
		------------------------------------------------------*/
		@Override public void flush(){};
		/* ***************************************************************
		
		
				IPrimitiveWriteFormat
		
		
		****************************************************************/	
		 public  void writeBoolean(boolean v)throws IOException{ media.add(Boolean.valueOf(v));}
		 public  void writeByte(byte v)throws IOException{ media.add(Byte.valueOf(v));}
		 public  void writeChar(char v)throws IOException{ media.add(Character.valueOf(v));}
		 public  void writeShort(short v)throws IOException{ media.add(Short.valueOf(v));}
		 public  void writeInt(int v)throws IOException{ media.add(Integer.valueOf(v));}
		 public  void writeLong(long v)throws IOException{ media.add(Long.valueOf(v));}
		 public  void writeFloat(float v)throws IOException{ media.add(Float.valueOf(v));}
		 public  void writeDouble(double v)throws IOException{ media.add(Double.valueOf(v));}
		
		 public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
		 {
			media.add(Arrays.copyOfRange(buffer,offset,offset+length));
		 };
		 public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
		 {
			media.add(Arrays.copyOfRange(buffer,offset,offset+length));
		 };
		 public void writeByteBlock(byte data)throws IOException
		 {
			media.add(new byte[]{data});
		 };
		 public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
		 {
			media.add(Arrays.copyOfRange(buffer,offset,offset+length));
		 };
		 public void writeCharBlock(CharSequence characters, int offset, int length)throws IOException
		 {
			media.add(characters.subSequence(offset,offset+length).toString().toCharArray());
		 };
		 public void writeShortBlock(short [] buffer, int offset, int length)throws IOException
		 {
			media.add(Arrays.copyOfRange(buffer,offset,offset+length));
		 };  
		 public void writeIntBlock(int [] buffer, int offset, int length)throws IOException
		 {
			media.add(Arrays.copyOfRange(buffer,offset,offset+length));
		 };
		 public void writeLongBlock(long [] buffer, int offset, int length)throws IOException
		 {
			media.add(Arrays.copyOfRange(buffer,offset,offset+length));
		 };
		 public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException
		 {
			media.add(Arrays.copyOfRange(buffer,offset,offset+length));
		 };
		 public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException
		 {
			media.add(Arrays.copyOfRange(buffer,offset,offset+length));
		};
};