package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.ASignalWriteFormat;
import java.io.IOException;
import java.util.Arrays;
/**
		An implementation {@link ASignalWriteFormat} which
		writes signals and data to {@link CObjListFormat}.
		<p>
		Intended to be used in tests, but users may look at it as
		on a base, primitive implementation.
		<p>
		This implementation is un-described, that is does not
		write any type information nor flush information.
		<p>
		This implementation is not optimizing <i>end</i> followed by
		<i>begin</i> into <i>end-begin</i> single indicator.
		
		
		<h1>Thread safety</h1>
		Absolutely not thread safe.
*/
public class CObjListWriteFormat extends ASignalWriteFormat
{
					/** A media to which this class writes. */
					public final CObjListFormat media;
					
		/* *************************************************************
		
				Construction
		
		
		***************************************************************/
		/** Creates
		@param names_registry_size see {@link ASignalWriteFormat#ASignalWriteFormat(int,int,int)}
		@param max_name_length --//--
		@param max_events_recursion_depth --//--
		@param media non null media to write data to.
		*/
		public CObjListWriteFormat(
									 int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth,
									 CObjListFormat media
									 )
		{
			super(  names_registry_size,max_name_length,max_events_recursion_depth);
			assert(media!=null);
			this.media = media;
		};		
		/* *************************************************************
		
				Services required by superclass.
		
		
		***************************************************************/
		/** Empty */
		@Override protected void closeImpl()throws IOException{};
		@Override protected void writeBeginSignalIndicator()throws IOException
		{
			media.addLast(CObjListFormat.BEGIN_INDICATOR);
		};
		@Override protected void writeEndSignalIndicator()throws IOException
		{
			media.addLast(CObjListFormat.END_INDICATOR);
		};
		@Override protected void writeDirectName()throws IOException
		{
			media.addLast(CObjListFormat.DIRECT_INDICATOR);
		};
		@Override protected void writeSignalNameData(String name)throws IOException
		{
			assert(name!=null);
			media.addLast(name);
		};
		@Override protected void writeRegisterName(int name_index)throws IOException
		{
			media.addLast(new CObjListFormat.REGISTER_INDICATOR(name_index));
		};
		@Override protected void writeRegisterUse(int name_index)throws IOException
		{
			media.addLast(new CObjListFormat.REGISTER_USE_INDICATOR(name_index));
		};
		
		@Override protected  void writeBooleanImpl(boolean v)throws IOException{ media.addLast(Boolean.valueOf(v));}
		@Override protected  void writeByteImpl(byte v)throws IOException{ media.addLast(Byte.valueOf(v));}
		@Override protected  void writeCharImpl(char v)throws IOException{ media.addLast(Character.valueOf(v));}
		@Override protected  void writeShortImpl(short v)throws IOException{ media.addLast(Short.valueOf(v));}
		@Override protected  void writeIntImpl(int v)throws IOException{ media.addLast(Integer.valueOf(v));}
		@Override protected  void writeLongImpl(long v)throws IOException{ media.addLast(Long.valueOf(v));}
		@Override protected  void writeFloatImpl(float v)throws IOException{ media.addLast(Float.valueOf(v));}
		@Override protected  void writeDoubleImpl(double v)throws IOException{ media.addLast(Double.valueOf(v));}
		@Override protected  void writeBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException
		{
			media.addLast(Arrays.copyOfRange(buffer,offset,offset+length));
		};
		@Override protected  void writeByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
		{
			media.addLast(Arrays.copyOfRange(buffer,offset,offset+length));
		};
		@Override protected  void writeByteBlockImpl(byte data)throws IOException
		{
			media.addLast(new byte[]{data});
		};
		@Override protected  void writeCharBlockImpl(char [] buffer, int offset, int length)throws IOException
		{
			media.addLast(Arrays.copyOfRange(buffer,offset,offset+length));
		};
		@Override protected  void writeCharBlockImpl(CharSequence characters, int offset, int length)throws IOException
		{
			media.addLast(characters.subSequence(offset,offset+length).toString().toCharArray());
		};
		@Override protected  void writeShortBlockImpl(short [] buffer, int offset, int length)throws IOException
		{
			media.addLast(Arrays.copyOfRange(buffer,offset,offset+length));
		};  
		@Override protected  void writeIntBlockImpl(int [] buffer, int offset, int length)throws IOException
		{
			media.addLast(Arrays.copyOfRange(buffer,offset,offset+length));
		};
		@Override protected  void writeLongBlockImpl(long [] buffer, int offset, int length)throws IOException
		{
			media.addLast(Arrays.copyOfRange(buffer,offset,offset+length));
		};
		@Override protected  void writeFloatBlockImpl(float [] buffer, int offset, int length)throws IOException
		{
			media.addLast(Arrays.copyOfRange(buffer,offset,offset+length));
		};
		@Override protected  void writeDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException
		{
			media.addLast(Arrays.copyOfRange(buffer,offset,offset+length));
		};
};