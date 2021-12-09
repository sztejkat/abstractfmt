package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.ASignalReadFormat;
import java.io.IOException;

/**
	An implementation of {@link ASignalReadFormat} which
	reads signals and data from {@link CObjListFormat}.
	<p>
	This implementation is described.
*/
public class CDescrObjListReadFormat extends CObjListReadFormat
{
		/* *************************************************************
		
				Construction
		
		
		***************************************************************/
		/** Creates
		@param names_registry_size see {@link ASignalReadFormat#ASignalReadFormat(int,int,int)}
		@param max_name_length --//--
		@param max_events_recursion_depth --//--
		@param media non null media from which read data.
		*/
		public CDescrObjListReadFormat(
									 int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth,
									 CObjListFormat media
									 )
		{
			super(  names_registry_size,max_name_length,max_events_recursion_depth, media);
		};	
		/* *************************************************************
		
				ISignalReadFormat
				
				Note: Since described and un-described reads do basically
				rely on different processing of some indicators and the
				type indicators are correctly understood by superclass
				this is enough to tell a ASignalReadFormat that it
				must require type information.
		
		***************************************************************/
		@Override public final boolean isDescribed(){ return true; };
		
};