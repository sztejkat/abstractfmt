package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.ASignalWriteFormat;
import java.io.IOException;
/**
	An implementation {@link ASignalWriteFormat} which
	writes signals and data to {@link CObjListFormat}.
	<p>
	This implementation is adds optimization of <i>end</i> followed by
	<i>begin</i> into <i>end-begin</i> single indicator.
*/
public class COptObjListWriteFormat extends CObjListWriteFormat
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
		public COptObjListWriteFormat(
									 int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth,
									 CObjListFormat media
									 )
		{
			super(  names_registry_size,max_name_length,max_events_recursion_depth,media);
		};
		/* *************************************************************
		
				Services tuneable in superclass
		
		
		***************************************************************/
		/** Overriden to write optimized indicator */
		@Override protected void writeEndBeginSignalIndicator()throws IOException
		{
			media.addLast(CObjListFormat.END_BEGIN_INDICATOR);
		};	
};