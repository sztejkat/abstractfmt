package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CCharExchangeBuffer;
import java.io.IOException;

/**
	Tests for primitives inter-exchange using {@link SXMLSettings#BARE} XML format
	with type information. 
*/
public class TestXMLFormat_D_Primitives extends ATestIIndicatorFormat_Primitives
{
	@Override protected Pair create()
	{
	
		CCharExchangeBuffer media = new CCharExchangeBuffer();
		return new Pair(
					new CXMLIndicatorWriteFormat(
										media.getWriter(),//Writer out,
								    	null,//Charset charset,
								    	SXMLSettings.LONG_BARE,//CXMLSettings settings,
								    	true //boolean is_described
										)
										{
											protected void closeOnce()throws IOException
											{
												super.closeOnce();
												System.out.println(media);
											};
										},
					new CXMLIndicatorReadFormat(
										media.getReader(),//final Reader input,
								   		SXMLSettings.LONG_BARE, //final CXMLSettings settings,
								   		1000,//final int maximum_idle_characters,
								   		true//boolean is_described
										)
					);
	};
};