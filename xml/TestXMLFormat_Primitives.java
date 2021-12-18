package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CCharExchangeBuffer;

/**
	Tests for primitives inter-exchange using {@link SXMLSettings#BARE} XML format
	without type information. 
*/
public class TestXMLFormat_Primitives extends ATestIIndicatorFormat_Primitives
{
	@Override protected Pair create()
	{
	
		CCharExchangeBuffer media = new CCharExchangeBuffer();
		return new Pair(
					new CXMLIndicatorWriteFormat(
										media.getWriter(),//Writer out,
								    	null,//Charset charset,
								    	SXMLSettings.LONG_BARE,//CXMLSettings settings,
								    	false //boolean is_described
										),
					new CXMLIndicatorReadFormat(
										media.getReader(),//final Reader input,
								   		SXMLSettings.LONG_BARE, //final CXMLSettings settings,
								   		1000,//final int maximum_idle_characters,
								   		false//boolean is_described
										)
					);
	};
};