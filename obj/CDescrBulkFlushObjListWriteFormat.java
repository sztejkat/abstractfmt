package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.ASignalWriteFormat;
import java.io.IOException;
import java.util.Arrays;
/**
		A third level test bed for {@link ASignalWriteFormat} which
		writes signals and data to {@link CObjListFormat}.
		<p>
		This implementation write both type information and flush indicators,
		using generic, bulk unspecific flushes (FLUSH_ANY)
*/
public class CDescrBulkFlushObjListWriteFormat extends CDescrObjListWriteFormat
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
		public CDescrBulkFlushObjListWriteFormat(
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
		@Override protected  void flushBoolean()throws IOException{ media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushByte()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushChar()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushShort()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushInt()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushLong()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushFloat()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushDouble()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		
		@Override protected  void flushBooleanBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushByteBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushCharBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushShortBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushIntBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushLongBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushFloatBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
		@Override protected  void flushDoubleBlock()throws IOException{media.addLast(CObjListFormat.FLUSH_ANY);};
};