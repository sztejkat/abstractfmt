package sztejkat.abstractfmt;
import java.io.IOException;
import java.util.Arrays;
/**
		A second level test bed for {@link ASignalWriteFormat} which
		writes signals and data to {@link CObjListFormat}.
		
		<p>
		This class DOES write the optimized end-begin indicator.
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
			super(  names_registry_size,max_name_length,max_events_recursion_depth, media);
			
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