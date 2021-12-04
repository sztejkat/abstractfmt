package sztejkat.abstractfmt;
import java.io.IOException;
import java.util.Arrays;
/**
		A third level test bed for {@link ASignalWriteFormat} which
		writes signals and data to {@link CObjListFormat}.
		<p>
		This class DOES write the optimized end-begin indicator
		and DOES write start-type indicators but does NOT write end-type
		indicators.
*/
public class CDescrObjListWriteFormat extends COptObjListWriteFormat
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
		public CDescrObjListWriteFormat(
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
		@Override protected  void writeBooleanType()throws IOException{ media.addLast(CObjListFormat.TYPE_BOOLEAN);};
		@Override protected  void writeByteType()throws IOException{media.addLast(CObjListFormat.TYPE_BYTE);};
		@Override protected  void writeCharType()throws IOException{media.addLast(CObjListFormat.TYPE_CHAR);};
		@Override protected  void writeShortType()throws IOException{media.addLast(CObjListFormat.TYPE_SHORT);};
		@Override protected  void writeIntType()throws IOException{media.addLast(CObjListFormat.TYPE_INT);};
		@Override protected  void writeLongType()throws IOException{media.addLast(CObjListFormat.TYPE_LONG);};
		@Override protected  void writeFloatType()throws IOException{media.addLast(CObjListFormat.TYPE_FLOAT);};
		@Override protected  void writeDoubleType()throws IOException{media.addLast(CObjListFormat.TYPE_DOUBLE);};
		
		@Override protected  void writeBooleanBlockType()throws IOException{media.addLast(CObjListFormat.TYPE_BOOLEAN_BLOCK);};
		@Override protected  void writeByteBlockType()throws IOException{media.addLast(CObjListFormat.TYPE_BYTE_BLOCK);};
		@Override protected  void writeCharBlockType()throws IOException{media.addLast(CObjListFormat.TYPE_CHAR_BLOCK);};
		@Override protected  void writeShortBlockType()throws IOException{media.addLast(CObjListFormat.TYPE_SHORT_BLOCK);};
		@Override protected  void writeIntBlockType()throws IOException{media.addLast(CObjListFormat.TYPE_INT_BLOCK);};
		@Override protected  void writeLongBlockType()throws IOException{media.addLast(CObjListFormat.TYPE_LONG_BLOCK);};
		@Override protected  void writeFloatBlockType()throws IOException{media.addLast(CObjListFormat.TYPE_FLOAT_BLOCK);};
		@Override protected  void writeDoubleBlockType()throws IOException{media.addLast(CObjListFormat.TYPE_DOUBLE_BLOCK);};
};
