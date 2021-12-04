package sztejkat.abstractfmt;
import java.io.IOException;
import java.util.Arrays;
/**
		A third level test bed for {@link ASignalWriteFormat} which
		writes signals and data to {@link CObjListFormat}.
		<p>
		This class DOES write the optimized end-begin indicator
		and DOES write start-type and end-type indicators
*/
public class CFullyDescrObjListWriteFormat extends COptObjListWriteFormat
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
		public CFullyDescrObjListWriteFormat(
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
		@Override protected  void writeBooleanTypeEnd()throws IOException{ media.addLast(CObjListFormat.TYPE_BOOLEAN_END);};
		@Override protected  void writeByteTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_BYTE_END);};
		@Override protected  void writeCharTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_CHAR_END);};
		@Override protected  void writeShortTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_SHORT_END);};
		@Override protected  void writeIntTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_INT_END);};
		@Override protected  void writeLongTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_LONG_END);};
		@Override protected  void writeFloatTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_FLOAT_END);};
		@Override protected  void writeDoubleTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_DOUBLE_END);};
		
		@Override protected  void writeBooleanBlockTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_BOOLEAN_BLOCK_END);};
		@Override protected  void writeByteBlockTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_BYTE_BLOCK_END);};
		@Override protected  void writeCharBlockTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_CHAR_BLOCK_END);};
		@Override protected  void writeShortBlockTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_SHORT_BLOCK_END);};
		@Override protected  void writeIntBlockTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_INT_BLOCK_END);};
		@Override protected  void writeLongBlockTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_LONG_BLOCK_END);};
		@Override protected  void writeFloatBlockTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_FLOAT_BLOCK_END);};
		@Override protected  void writeDoubleBlockTypeEnd()throws IOException{media.addLast(CObjListFormat.TYPE_DOUBLE_BLOCK_END);};
};