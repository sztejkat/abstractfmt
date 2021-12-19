package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.util.CCharExchangeBuffer;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
	Tests for primitives inter-exchange using {@link SXMLSettings#LONG_FULL_UTF8} XML format
	with type information. 
*/
public class TestXMLFormat_FD_Primitives extends ATestIIndicatorFormat_Primitives
{
	@Override protected Pair create()
	{
		
		CCharExchangeBuffer media = new CCharExchangeBuffer();
		Pair p= new Pair(
					new CXMLIndicatorWriteFormat(
										media.getWriter(),//Writer out,
								    	null,//Charset charset,
								    	SXMLSettings.LONG_FULL_UTF8,//CXMLSettings settings,
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
								   		SXMLSettings.LONG_FULL_UTF8, //final CXMLSettings settings,
								   		1000,//final int maximum_idle_characters,
								   		true//boolean is_described
										)
					);
		try{
			((CXMLIndicatorWriteFormat)p.write).open();
			}catch(IOException ex){ throw new UncheckedIOException(ex); };
		return p;
	};
};