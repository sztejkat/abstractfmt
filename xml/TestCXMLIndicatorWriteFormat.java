package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import java.io.StringWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import org.junit.Test;
import org.junit.Assert;
/**
	A test which check is writes are resulting in known-good files.
*/
public class TestCXMLIndicatorWriteFormat extends sztejkat.utils.test.ATest
{
	@Test public void testWithRoot()throws IOException
	{
		enter();
		/*
			A test which checks if  LONG_FULL_UTF8 correctly encloses
			stream with prolog and root.
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("UTF-8"),
																  SXMLSettings.LONG_FULL_UTF8,
																  true);
		f.open();
			f.writeBeginDirect("PONY");
			//Note: remember, writing type is up to caller!
			f.writeType(TIndicator.TYPE_BOOLEAN);
			f.writeBoolean(false);
			f.writeFlush(TIndicator.FLUSH_BOOLEAN);
			f.writeEnd();
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <?sztejkat.abstractfmt.xml variant=\"long\"?><root><PONY><boolean>f</boolean></PONY></root>"));
		leave();
	};
	
	@Test public void testWithoutRoot()throws IOException
	{
		enter();
		/*
			A test which checks if  LONG_BARE correctly DOES NOT enclose
			stream with prolog and root.
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("UTF-8"),
																  SXMLSettings.LONG_BARE,
																  true);
		f.open();
			f.writeBeginDirect("PONY");
			//Note: remember, writing type is up to caller!
			f.writeType(TIndicator.TYPE_FLOAT);
			f.writeFloat(-1.34f);
			f.writeFlush(TIndicator.FLUSH_FLOAT);
			f.writeEnd();
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals("<PONY><float>-1.34</float></PONY>"));
		leave();
	};
	
	
	
	@Test public void testEventWithStrangeName()throws IOException
	{
		enter();
		/*
			A test which checks if LONG_BARE correctly 
			writes event which can't be written directly
			because carries characters which can't be encoded.
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("ASCII"),
																  SXMLSettings.LONG_BARE,
																  true);
		f.open();
			f.writeBeginDirect("MĘŻATKA");
			f.writeEnd();
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals("<event name=\"M%118;%17B;ATKA\"></event>"));
		leave();
	};
	
	@Test public void testEventWithXMLConflictingName()throws IOException
	{
		enter();
		/*
			A test which checks if LONG_BARE correctly 
			writes event which can't be written directly
			because carries characters which are bad XML
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("UTF-8"),
																  SXMLSettings.LONG_BARE,
																  true);
		f.open();
			f.writeBeginDirect("<>");
			f.writeEnd();
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals("<event name=\"&lt;&gt;\"></event>"));
		leave();
	};
	
	
	@Test public void testPrimitiveExampleDescribed()throws IOException
	{
		enter();
		/*
			A test which checks if  LONG_BARE correctly generates a certain example
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("UTF-8"),
																  SXMLSettings.LONG_BARE,
																  true);
		f.open();
			f.writeType(TIndicator.TYPE_BOOLEAN);
			f.writeBoolean(false);
			f.writeFlush(TIndicator.FLUSH_BOOLEAN);
			
			f.writeType(TIndicator.TYPE_BOOLEAN);
			f.writeBoolean(true);
			f.writeFlush(TIndicator.FLUSH_BOOLEAN);
			
			f.writeType(TIndicator.TYPE_BYTE);
			f.writeByte((byte)1);
			f.writeFlush(TIndicator.FLUSH_BYTE);
			
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar('c');
			f.writeFlush(TIndicator.FLUSH_CHAR);
				
			f.writeType(TIndicator.TYPE_SHORT);
			f.writeShort((short)8888);
			f.writeFlush(TIndicator.FLUSH_SHORT);
			
			f.writeType(TIndicator.TYPE_INT);
			f.writeInt(34445544);
			f.writeFlush(TIndicator.FLUSH_INT);
			
			f.writeType(TIndicator.TYPE_LONG);
			f.writeLong(135245525252454L);
			f.writeFlush(TIndicator.FLUSH_LONG);
			
			f.writeType(TIndicator.TYPE_FLOAT);
			f.writeFloat(1.33f);
			f.writeFlush(TIndicator.FLUSH_FLOAT);
			
			f.writeType(TIndicator.TYPE_DOUBLE);
			f.writeDouble(-2.4090459E3);
			f.writeFlush(TIndicator.FLUSH_DOUBLE);
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals("<boolean>f</boolean><boolean>t</boolean><byte>1</byte><char>c</char><short>8888</short><int>34445544</int><long>135245525252454</long><float>1.33</float><double>-2409.0459</double>"));
		leave();
	};
	
	
	
	@Test public void testPrimitiveExampleUndescribed()throws IOException
	{
		enter();
		/*
			A test which checks if  LONG_BARE correctly generates a certain example
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("UTF-8"),
																  SXMLSettings.LONG_BARE,
																  false);
		f.open();
			f.writeType(TIndicator.TYPE_BOOLEAN);
			f.writeBoolean(false);
			f.writeFlush(TIndicator.FLUSH_BOOLEAN);
			
			f.writeType(TIndicator.TYPE_BOOLEAN);
			f.writeBoolean(true);
			f.writeFlush(TIndicator.FLUSH_BOOLEAN);
			
			f.writeType(TIndicator.TYPE_BYTE);
			f.writeByte((byte)-1);
			f.writeFlush(TIndicator.FLUSH_BYTE);
			
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar('c');
			f.writeFlush(TIndicator.FLUSH_CHAR);
				
			f.writeType(TIndicator.TYPE_SHORT);
			f.writeShort((short)8888);
			f.writeFlush(TIndicator.FLUSH_SHORT);
			
			f.writeType(TIndicator.TYPE_INT);
			f.writeInt(34445544);
			f.writeFlush(TIndicator.FLUSH_INT);
			
			f.writeType(TIndicator.TYPE_LONG);
			f.writeLong(135245525252454L);
			f.writeFlush(TIndicator.FLUSH_LONG);
			
			f.writeType(TIndicator.TYPE_FLOAT);
			f.writeFloat(1.33f);
			f.writeFlush(TIndicator.FLUSH_FLOAT);
			
			f.writeType(TIndicator.TYPE_DOUBLE);
			f.writeDouble(-2.4090459E3);
			f.writeFlush(TIndicator.FLUSH_DOUBLE);
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals("f;t;-1;c;8888;34445544;135245525252454;1.33;-2409.0459;"));
		leave();
	};
	
	
	
	
	@Test public void testCharactersEscapes()throws IOException
	{
		enter();
		/*
			A test which checks if LONG_BARE correctly generates escaped characters,
			especially considerig escape end optimization in described format.
			
			To trigger more precise case we restric charset using ASCII encoding
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("ASCII"),
																  SXMLSettings.LONG_BARE,
																  true);
		f.open();
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar((char)0x08);
			f.writeFlush(TIndicator.FLUSH_CHAR);
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar('%');
			f.writeFlush(TIndicator.FLUSH_CHAR);
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar(';');
			f.writeFlush(TIndicator.FLUSH_CHAR);
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar((char)0x3FA);
			f.writeFlush(TIndicator.FLUSH_CHAR);
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar('<');	//standard AMP escape.
			f.writeFlush(TIndicator.FLUSH_CHAR);			
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		/*
			Note:
				Since we are using described mode we expect trailing ; 
				to be skipped and ; to be un-escaped.
		*/
		//<char>%8</char><char>%%</char><char>%3B</char><char>%3FA</char>
		Assert.assertTrue(r.equals("<char>%8</char><char>%%</char><char>;</char><char>%3FA</char><char>&lt;</char>"));
		leave();
	};
	
	@Test public void testCharactersEscapesUndescribed()throws IOException
	{
		enter();
		/*
			A test which checks if LONG_BARE correctly generates escaped characters,
			especially considerig escape end optimization in un-described format.
			
			To trigger more precise case we restric charset using ASCII encoding
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("ASCII"),
																  SXMLSettings.LONG_BARE,
																  false);
		f.open();
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar((char)0x08);
			f.writeFlush(TIndicator.FLUSH_CHAR);
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar('%');
			f.writeFlush(TIndicator.FLUSH_CHAR);
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar(';');
			f.writeFlush(TIndicator.FLUSH_CHAR);
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar((char)0x3FA);
			f.writeFlush(TIndicator.FLUSH_CHAR);
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar('<');	//standard AMP escape.
			f.writeFlush(TIndicator.FLUSH_CHAR);
			f.writeType(TIndicator.TYPE_CHAR);
			f.writeChar('a');	//standard AMP escape.
			f.writeFlush(TIndicator.FLUSH_CHAR);			
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		/*
			Note:
				Since we are using un-described mode we expect single ; to be present
				and ; to be escaped.
		*/
		Assert.assertTrue(r.equals("%8;%%;%3B;%3FA;&lt;a;"));
		leave();
	};
	
	
	
	
	@Test public void testBooleanBlock()throws IOException
	{
		enter();
		/*
			A test which checks if LONG_BARE correctly wite described boolean block
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("UTF-8"),
																  SXMLSettings.LONG_BARE,
																  true);
		f.open();
			f.writeType(TIndicator.TYPE_BOOLEAN_BLOCK);
			f.writeBooleanBlock(new boolean[]{true,false,false,true});
			f.writeFlush(TIndicator.FLUSH_BOOLEAN_BLOCK);	
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals("<boolean_array>tfft</boolean_array>"));
		leave();
	};
	
	
	@Test public void testByteBlock()throws IOException
	{
		enter();
		/*
			A test which checks if LONG_BARE correctly writes undescribed byte block
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("UTF-8"),
																  SXMLSettings.LONG_BARE,
																  false);
		f.open();
			f.writeType(TIndicator.TYPE_BYTE_BLOCK);
			f.writeByteBlock(new byte[]{(byte)0x00,(byte)0xAB,(byte)0x3F});
			f.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);	
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals("00AB3F"));
		leave();
	};
	
	
	@Test public void testCharBlock()throws IOException
	{
		enter();
		/*
			A test which checks if LONG_BARE correctly writes undescribed 
			character block with escapes
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("ASCII"),
																  SXMLSettings.LONG_BARE,
																  false);
		f.open();
			f.writeType(TIndicator.TYPE_BYTE_BLOCK);
			f.writeCharBlock(";SPIR<>OMETRY"+(char)0x4f4a+";MARCI%E");
			f.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);	
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals(";SPIR&lt;&gt;OMETRY%4F4A;;MARCI%%;E"));
		leave();
	};
	
	
	
	@Test public void testIntBlock()throws IOException
	{
		enter();
		/*
			Now we run a bit of numeric block, but inside and event to see
			if trailing separator is optimized out
		*/
		StringWriter o = new StringWriter(); 
		CXMLIndicatorWriteFormat f = new CXMLIndicatorWriteFormat(o,
																  Charset.forName("UTF-8"),
																  SXMLSettings.LONG_BARE,
																  false);
		f.open();
			f.writeBeginDirect("MARCIE");
			f.writeType(TIndicator.TYPE_INT_BLOCK);
			f.writeIntBlock(new int[]{1,2,3,45,6,7,8});
			f.writeFlush(TIndicator.FLUSH_INT_BLOCK);
			f.writeEnd();	
		f.close();
		
		String r = o.toString();
		System.out.println(r);		
		Assert.assertTrue(r.equals("<MARCIE>1;2;3;45;6;7;8</MARCIE>"));
		leave();
	};
};