package sztejkat.abstractfmt.json;

/** Base of JSON formats */
abstract class AJSONFormat 
{
				/** A JSON settings to work with */
				protected final CJSONSettings settings;
		
	/* ****************************************************************
	
			Construction
	
	
	*****************************************************************/
	/** Creates
	@param settings non-null
	*/				
	AJSONFormat(CJSONSettings settings)
	{
		assert(settings!=null);
		this.settings = settings;
	};
};