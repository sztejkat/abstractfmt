package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import java.io.IOException;
/**
	An interoperational tests for {@link ADescXMLSignalWriteFormat}/{@link ADescXMLSignalReadFormat}
	both running in undescribed mode, and running described mode specific tests.
*/
public class TestADescXMLFormatFormat_Descr extends ATestISignalFormat_Descr
{
		private static class DUT_w  extends ADescXMLSignalWriteFormat
		{
					private final CCharExchangeBuffer media;
					
				DUT_w(CCharExchangeBuffer media,int max_name_length, int max_recursion_depth)
				{
				super(
									max_name_length,//final int max_name_length,
									max_recursion_depth,//final int max_events_recursion_depth,
									'%',';',//final char ESCAPE_CHAR, ,//final char ESCAPE_CHAR_END, 
									';',//final char PRIMITIVES_SEPARATOR_CHAR,
									"event","name",//final String LONG_SIGNAL_ELEMENT,,//final String LONG_SIGNAL_ELEMENT_ATTR,
									"boolean",//final String TYPE_BOOLEAN_TAG,
									"byte",//final String TYPE_BYTE_TAG,
									"char",//final String TYPE_CHAR_TAG,
									"short",//final String TYPE_SHORT_TAG,
									"int",//final String TYPE_INT_TAG,
									"long",//final String TYPE_LONG_TAG,
									"float",//final String TYPE_FLOAT_TAG,
									"double",//final String TYPE_DOUBLE_TAG,
									"boolean_array",//final String TYPE_BOOLEAN_BLOCK_TAG,
									"byte_array",//final String TYPE_BYTE_BLOCK_TAG,
									"string",//final String TYPE_CHAR_BLOCK_TAG,
									"short_array",//final String TYPE_SHORT_BLOCK_TAG,
									"int_array",//final String TYPE_INT_BLOCK_TAG,
									"long_array",//final String TYPE_LONG_BLOCK_TAG,
									"float_array",//final String TYPE_FLOAT_BLOCK_TAG,
									"double_array"//final String TYPE_DOUBLE_BLOCK_TAG
							);
							this.media = media;
				};
				@Override protected void write(char c)throws IOException{ media.write(c); };
				@Override protected void write(CharSequence csq)throws IOException{ media.write(csq); };
				@Override protected void closeImpl(){ };
		};
		private static class DUT_r  extends ADescXMLSignalReadFormat
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
								13,//	 final int max_type_tag_length,
								32,//	 final int max_inter_signal_chars
								"boolean",//final String TYPE_BOOLEAN_TAG,
								"byte",//final String TYPE_BYTE_TAG,
								"char",//final String TYPE_CHAR_TAG,
								"short",//final String TYPE_SHORT_TAG,
								"int",//final String TYPE_INT_TAG,
								"long",//final String TYPE_LONG_TAG,
								"float",//final String TYPE_FLOAT_TAG,
								"double",//final String TYPE_DOUBLE_TAG,
								"boolean_array",//final String TYPE_BOOLEAN_BLOCK_TAG,
								"byte_array",//final String TYPE_BYTE_BLOCK_TAG,
								"string",//final String TYPE_CHAR_BLOCK_TAG,
								"short_array",//final String TYPE_SHORT_BLOCK_TAG,
								"int_array",//final String TYPE_INT_BLOCK_TAG,
								"long_array",//final String TYPE_LONG_BLOCK_TAG,
								"float_array",//final String TYPE_FLOAT_BLOCK_TAG,
								"double_array"//final String TYPE_DOUBLE_BLOCK_TAG
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
