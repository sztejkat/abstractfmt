package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CCharFileExchangeBuffer;
import java.util.Arrays;
import java.io.IOException;
import java.io.File;
/**
	An indicator format implementation over {@link CObjListFormat} media
	which dumps content to a text file when closed.
*/
public class CDumpingObjIndicatorWriteFormat extends CObjIndicatorWriteFormat
{
				private final File folder;
				private final String file_name_pattern;
	/** Creates
	@param media non null media to write to
	@param max_registrations number returned from {@link #getMaxRegistrations}
	@param max_supported_signal_name_length number returned from {@link #getMaxSupportedSignalNameLength}		
	@param is_described returned from {@link #isDescribed}. If false
		type writes are non-op
	@param is_flushing returned from {@link #isFlushing}. If false
		type writes are non-op
	@param disable_end_begin_opt if true end-begin optimization is disabled
		and no END_BEGIN_xxx indicators are written.
	@param folder optional folder to be used where create files.
		If not null and not existing will be created.			
	@param file_name_pattern file name with # inside to be replaced 
		with {@link #numerator}. File will be created, written
		and read with "UTF-8" encoding.
		Files will be overwritten.
	@throws AssertionError is something is wrong.
	*/
	public CDumpingObjIndicatorWriteFormat(final CObjListFormat media,
									final int max_registrations,
									final int max_supported_signal_name_length,										
									final boolean is_described,
									final boolean is_flushing,
									final boolean disable_end_begin_opt,
									final File folder,
									final String file_name_pattern
										)
	{
		super(media,
			max_registrations,
			 max_supported_signal_name_length,										
			is_described,
			is_flushing,
			disable_end_begin_opt);
		this.folder =folder;
		this.file_name_pattern =file_name_pattern;
	};
	public void close()throws IOException
	{
		super.close();
		//A bit overkill to use exchange buffer, but it makes 
		//automatic folder creation easier.
		CCharFileExchangeBuffer b = new CCharFileExchangeBuffer(
				folder,
				file_name_pattern
				);
		getMedia().printTo(b.getWriter(),"\n");
		b.close();
	};
};