package sztejkat.abstractfmt.obj;
import java.util.Arrays;
import sztejkat.abstractfmt.*;
import java.io.IOException;

/**
	An indicator format implementation over {@link CObjListFormat} media.
*/
public class CObjIndicatorWriteFormat implements IIndicatorWriteFormat
{
			private final CObjListFormat media;
			
		/** Creates
		@param media non null media to write to 
		*/
		public CObjIndicatorWriteFormat(CObjListFormat media){ this.media = media; };
		
		/* ***************************************************************
		
		
				IIndicatorWriteFormat
		
		
		****************************************************************/
		/** @return Integer.MAX_VALUE */		
		public int getMaxRegistrations(){ return Integer.MAX_VALUE; };
		/** @return false */
		public boolean requiresFlushes(){ return false; };
		
		public void writeBeginDirect(String signal_name)throws IOException
		{
			media.add(TIndicator.BEGIN_DIRECT);
			media.add(signal_name);
		};
		public void writeEndBeginDirect(String signal_name)throws IOException		
		{
			media.add(TIndicator.END_BEGIN_DIRECT);
			media.add(signal_name);
		};
		public void writeBeginRegister(String signal_name, int number)throws IOException
		{
			media.add(TIndicator.BEGIN_REGISTER);
			media.add(Integer.valueOf(number));
			media.add(signal_name);
		};
		public void writeEndBeginRegister(String signal_name, int number)throws IOException
		{
			media.add(TIndicator.END_BEGIN_REGISTER);
			media.add(Integer.valueOf(number));
			media.add(signal_name);
		};
		public void writeBeginUse(int number)throws IOException
		{
			media.add(TIndicator.BEGIN_USE);
			media.add(Integer.valueOf(number));
		};
		public void writeEndBeginUse( int number)throws IOException
		{
			media.add(TIndicator.END_BEGIN_USE);
			media.add(Integer.valueOf(number));
		};
		public void writeEnd()throws IOException
		{
			media.add(TIndicator.END);
		};
		public void writeType(TIndicator type)throws IOException
		{
			assert(type!=null);
			assert((type.FLAGS & TIndicator.TYPE)!=0);
			media.add(type);
		};
		public void writeFlush(TIndicator flush)throws IOException
		{
			assert(flush!=null);
			assert((flush.FLAGS & TIndicator.FLUSH)!=0);
			assert((flush.FLAGS & TIndicator.READ_ONLY)==0);
			media.add(flush);
		};
		public void close()throws IOException
		{
			flush();
		};
		public void flush(){};
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