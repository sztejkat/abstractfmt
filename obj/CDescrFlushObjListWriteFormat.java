package sztejkat.abstractfmt.obj;
import java.io.IOException;
import java.util.Arrays;
/**
		A third level test bed for {@link ASignalWriteFormat} which
		writes signals and data to {@link CObjListFormat}.
		<p>
		This implementation write both type information and flush indicators,
		using type specific flushes.
*/
public class CDescrFlushObjListWriteFormat extends CDescrObjListWriteFormat
{
		/* *************************************************************
		
				Construction
		
		
		***************************************************************/
		/** Creates
		@param names_registry_size see {@link ASignalWriteFormat#ASignalWriteFormat(int,int,int)}
		@param max_name_length --//--
		@param max_events_recursion_depth --//--
		@param media non null media to write data to.
		*/
		public CDescrFlushObjListWriteFormat(
									 int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth,
									 CObjListFormat media
									 )
		{
			super(  names_registry_size,max_name_length,max_events_recursion_depth, media);
			
		};	
		/* *************************************************************
		
				Services tuneable in superclass
		
		
		***************************************************************/
		@Override protected  void flushBoolean()throws IOException{ media.addLast(CObjListFormat.FLUSH_BOOLEAN);};
		@Override protected  void flushByte()throws IOException{media.addLast(CObjListFormat.FLUSH_BYTE);};
		@Override protected  void flushChar()throws IOException{media.addLast(CObjListFormat.FLUSH_CHAR);};
		@Override protected  void flushShort()throws IOException{media.addLast(CObjListFormat.FLUSH_SHORT);};
		@Override protected  void flushInt()throws IOException{media.addLast(CObjListFormat.FLUSH_INT);};
		@Override protected  void flushLong()throws IOException{media.addLast(CObjListFormat.FLUSH_LONG);};
		@Override protected  void flushFloat()throws IOException{media.addLast(CObjListFormat.FLUSH_FLOAT);};
		@Override protected  void flushDouble()throws IOException{media.addLast(CObjListFormat.FLUSH_DOUBLE);};
		
		@Override protected  void flushBooleanBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_BOOLEAN_BLOCK);};
		@Override protected  void flushByteBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_BYTE_BLOCK);};
		@Override protected  void flushCharBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_CHAR_BLOCK);};
		@Override protected  void flushShortBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_SHORT_BLOCK);};
		@Override protected  void flushIntBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_INT_BLOCK);};
		@Override protected  void flushLongBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_LONG_BLOCK);};
		@Override protected  void flushFloatBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_FLOAT_BLOCK);};
		@Override protected  void flushDoubleBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_DOUBLE_BLOCK);};
};