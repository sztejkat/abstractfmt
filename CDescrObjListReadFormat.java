package sztejkat.abstractfmt;

/**
		Primarily a test bed for {@link ASignalReadFormat} which
		reads signals and data from {@link CObjListFormat} and
		requires and validates types as a <a href="package.html#fullydescribed">
		<i>fully described</i></a> format stream would do.
		<p>
		Intended to be used in tests, but users may look at it as
		on a base, primitive implementation.
		<p>
		Absolutely not thread safe.
*/
public class CDescrObjListReadFormat extends CObjListReadFormat implements IDescribedSignalReadFormat
{
		/* *************************************************************
		
				Construction
		
		
		***************************************************************/
		/** Creates
		@param names_registry_size see {@link ASignalReadFormat#ASignalReadFormat(int,int,int,boolean)}
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
			super(  names_registry_size,max_name_length,max_events_recursion_depth,true,media);
		};	
};