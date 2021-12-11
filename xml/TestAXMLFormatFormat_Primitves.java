package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import java.io.IOException;
/**
	An interoperational tests for {@link AXMLSignalWriteFormat}/{@link AXMLSignalReadFormat}
	both running in undescribed mode.
*/
public class TestAXMLFormatFormat_Primitves extends ATestISignalFormat_Primitives
{
		private static class DUT_w  extends AXMLSignalWriteFormat
		{
					private final CCharExchangeBuffer media;
					
				DUT_w(CCharExchangeBuffer media,int max_name_length, int max_recursion_depth)
				{
				super(
									max_name_length,// final int max_name_length,
									max_recursion_depth,// final int max_events_recursion_depth,
									'%',';',// final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
									';',//final char PRIMITIVES_SEPARATOR_CHAR,
									"event","name" //final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR
							);
							this.media = media;
				};
				@Override protected void write(char c)throws IOException{ media.write(c); };
				@Override protected void write(CharSequence csq)throws IOException{ media.write(csq); };
				@Override protected void closeImpl(){};
		};
		private static class DUT_r  extends AXMLSignalReadFormat
		{
					private final CCharExchangeBuffer media;
					
				DUT_r(CCharExchangeBuffer media,int max_name_length, int max_recursion_depth)
				{
				super(
								max_name_length,//final int max_name_length,
								max_recursion_depth,//	 final int max_events_recursion_depth,
								'%',';',// 	 final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
								';',//	 final char PRIMITIVES_SEPARATOR_CHAR,
								"event","name", //	 final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR,
								0,//	 final int max_type_tag_length,
								32//	 final int max_inter_signal_chars
							);
							this.media = media;
				};
				@Override protected int readImpl()throws IOException{ return media.read(); };
				@Override protected void closeImpl(){};
		};
		@Override protected Pair create(int max_name_length, int max_recursion_depth)
		{
			final CCharExchangeBuffer media = new CCharExchangeBuffer();
			return new Pair(
						new DUT_w(   media,
									 max_name_length,//int max_name_length,
									 max_recursion_depth//int max_events_recursion_depth
									 ),
						new DUT_r(   media,
									 max_name_length,//int max_name_length,
									 max_recursion_depth//int max_events_recursion_depth
									 ));
						
		};
};
