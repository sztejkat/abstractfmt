package sztejkat.abstractfmt.json;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.CIndicatorWriteFormatProtector;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Test;
import org.junit.Assert;
/**
	A test bed for {@link CJSONIndicatorWriteFormat}
	used to test production of known good test cases
*/
public class TestCJSONIndicatorWriteFormat extends sztejkat.utils.test.ATest
{
	@Test public void testOpenClose_Bare()throws IOException
	{
		enter();
		/*
			Just open and close
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("UTF-8"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  false,//boolean is_described,
										  false //boolean is_enclosed
										  )
					);
					
		D.open();
		D.flush();
		D.close();
		
		Assert.assertTrue(o.toString().equals(""));
		leave();
	};
	
	@Test public void testOpenClose_Enclosed()throws IOException
	{
		enter();
		/*
			Just open and close
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("UTF-8"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  false,//boolean is_described,
										  true //boolean is_enclosed
										  )
					);
					
		D.open();
		D.flush();
		D.close();
		System.out.println(o.toString());
		Assert.assertTrue(o.toString().equals("[]"));
		leave();
	};
	
	
	@Test public void testZeroLevelPrimitive_Bare_Boolean_1()throws IOException
	{
		enter();
		/*
			Primitives in bare zero level.
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("UTF-8"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  false,//boolean is_described,
										  false //boolean is_enclosed
										  )
					);
					
		D.open();
			D.writeType(TIndicator.TYPE_BOOLEAN);
			D.writeBoolean(false);
			D.writeFlush(TIndicator.FLUSH_BOOLEAN);
		D.flush();
		D.close();
		System.out.println(o.toString());
		Assert.assertTrue(o.toString().equals("false"));
		leave();
	};
	@Test public void testZeroLevelPrimitive_Bare_Boolean_2()throws IOException
	{
		enter();
		/*
			Primitives in bare zero level.
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("UTF-8"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  false,//boolean is_described,
										  false //boolean is_enclosed
										  )
					);
					
		D.open();
			D.writeType(TIndicator.TYPE_BOOLEAN);
			D.writeBoolean(false);
			D.writeFlush(TIndicator.FLUSH_BOOLEAN);
			D.writeType(TIndicator.TYPE_BOOLEAN);
			D.writeBoolean(true);
			D.writeFlush(TIndicator.FLUSH_BOOLEAN);
		D.flush();
		D.close();
		System.out.println(o.toString());
		Assert.assertTrue(o.toString().equals("false,true"));
		leave();
	};
	
	@Test public void testZeroLevelPrimitive_Bare_X()throws IOException
	{
		enter();
		/*
			Primitives in bare zero level.
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("UTF-8"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  false,//boolean is_described,
										  false //boolean is_enclosed
										  )
					);
					
		D.open();
			D.writeType(TIndicator.TYPE_BOOLEAN);
			D.writeBoolean(false);
			D.writeFlush(TIndicator.FLUSH_BOOLEAN);
			D.writeType(TIndicator.TYPE_BYTE);
			D.writeByte((byte)33);
			D.writeFlush(TIndicator.FLUSH_BYTE);
			D.writeType(TIndicator.TYPE_CHAR);
			D.writeChar('X');
			D.writeFlush(TIndicator.FLUSH_CHAR);
			D.writeType(TIndicator.TYPE_SHORT);
			D.writeShort((short)(32761));
			D.writeFlush(TIndicator.FLUSH_SHORT);
			D.writeType(TIndicator.TYPE_INT);
			D.writeInt(-6789735);
			D.writeFlush(TIndicator.FLUSH_INT);
			D.writeType(TIndicator.TYPE_LONG);
			D.writeLong(-6789735789274L);
			D.writeFlush(TIndicator.FLUSH_LONG);
			D.writeType(TIndicator.TYPE_FLOAT);
			D.writeFloat(1.344E3f);
			D.writeFlush(TIndicator.FLUSH_FLOAT);
			D.writeType(TIndicator.TYPE_DOUBLE);
			D.writeDouble(1.344E3f);
			D.writeFlush(TIndicator.FLUSH_DOUBLE);
		D.flush();
		D.close();
		System.out.println(o.toString());
		//Note: float/double conversion is ambigous, so I just put it here what was really produced. 
		Assert.assertTrue(o.toString().equals("false,33,\"X\",32761,-6789735,-6789735789274,1344.0,1344.0"));
		leave();
	};
	
	
	@Test public void testZeroLevelPrimitive_Enclosed_X()throws IOException
	{
		enter();
		/*
			Primitives in bare zero level.
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("UTF-8"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  false,//boolean is_described,
										  true //boolean is_enclosed
										  )
					);
					
		D.open();
			D.writeType(TIndicator.TYPE_BOOLEAN);
			D.writeBoolean(false);
			D.writeFlush(TIndicator.FLUSH_BOOLEAN);
			D.writeType(TIndicator.TYPE_BYTE);
			D.writeByte((byte)33);
			D.writeFlush(TIndicator.FLUSH_BYTE);
			D.writeType(TIndicator.TYPE_CHAR);
			D.writeChar('X');
			D.writeFlush(TIndicator.FLUSH_CHAR);
			D.writeType(TIndicator.TYPE_SHORT);
			D.writeShort((short)(32761));
			D.writeFlush(TIndicator.FLUSH_SHORT);
			D.writeType(TIndicator.TYPE_INT);
			D.writeInt(-6789735);
			D.writeFlush(TIndicator.FLUSH_INT);
			D.writeType(TIndicator.TYPE_LONG);
			D.writeLong(-6789735789274L);
			D.writeFlush(TIndicator.FLUSH_LONG);
			D.writeType(TIndicator.TYPE_FLOAT);
			D.writeFloat(1.344E3f);
			D.writeFlush(TIndicator.FLUSH_FLOAT);
			D.writeType(TIndicator.TYPE_DOUBLE);
			D.writeDouble(1.344E3f);
			D.writeFlush(TIndicator.FLUSH_DOUBLE);
		D.flush();
		D.close();
		System.out.println(o.toString());
		//Note: float/double conversion is ambigous, so I just put it here what was really produced. 
		Assert.assertTrue(o.toString().equals("[false,33,\"X\",32761,-6789735,-6789735789274,1344.0,1344.0]"));
		leave();
	};
	
	
	@Test public void testZeroLevelPrimitive_Enclosed_Described_X()throws IOException
	{
		/*
			Primitives in bare zero level.
		*/
		enter();
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("UTF-8"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  true,//boolean is_described,
										  true //boolean is_enclosed
										  )
					);
					
		D.open();
			D.writeType(TIndicator.TYPE_BOOLEAN);
			D.writeBoolean(false);
			D.writeFlush(TIndicator.FLUSH_BOOLEAN);
			D.writeType(TIndicator.TYPE_BYTE);
			D.writeByte((byte)33);
			D.writeFlush(TIndicator.FLUSH_BYTE);
			D.writeType(TIndicator.TYPE_CHAR);
			D.writeChar('X');
			D.writeFlush(TIndicator.FLUSH_CHAR);
			D.writeType(TIndicator.TYPE_SHORT);
			D.writeShort((short)(32761));
			D.writeFlush(TIndicator.FLUSH_SHORT);
			D.writeType(TIndicator.TYPE_INT);
			D.writeInt(-6789735);
			D.writeFlush(TIndicator.FLUSH_INT);
			D.writeType(TIndicator.TYPE_LONG);
			D.writeLong(-6789735789274L);
			D.writeFlush(TIndicator.FLUSH_LONG);
			D.writeType(TIndicator.TYPE_FLOAT);
			D.writeFloat(1.344E3f);
			D.writeFlush(TIndicator.FLUSH_FLOAT);
			D.writeType(TIndicator.TYPE_DOUBLE);
			D.writeDouble(-1.344E3f);
			D.writeFlush(TIndicator.FLUSH_DOUBLE);
		D.flush();
		D.close();
		System.out.println(o.toString());
		//Note: float/double conversion is ambigous, so I just put it here what was really produced. 
		Assert.assertTrue(o.toString().equals(
		"[{\"bool\":false},{\"byte\":33},{\"char\":\"X\"},"+
		"{\"short\":32761},{\"int\":-6789735},{\"long\":-6789735789274},"+
		"{\"float\":1344.0},{\"double\":-1344.0}]"));
		leave();
	};
	
	
	
	
	
	@Test public void testPrimitiveArrays_1()throws IOException
	{
		enter();	
		/*
			Primitives arrays enclosed in an event
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("UTF-8"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  false,//boolean is_described,
										  false //boolean is_enclosed
										  )
					);
				
		D.open();
			D.writeBeginDirect("array");
			D.writeType(TIndicator.TYPE_BOOLEAN_BLOCK);
				D.writeBooleanBlock(new boolean[]{false,true,false});			
			D.writeFlush(TIndicator.FLUSH_BOOLEAN_BLOCK);
			D.writeEndBeginDirect("array");			
			D.writeType(TIndicator.TYPE_BYTE_BLOCK);
				D.writeByteBlock(new byte[]{(byte)0x33,(byte)0xAA});
				D.writeByteBlock((byte)0xF4);				
			D.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
			D.writeEnd();
			
		D.close();
		System.out.println(o.toString());
		Assert.assertTrue(o.toString().equals("{\"array\":[[,false,true,false]]},{\"array\":[\"33AAF4\"]}"));
		leave();
	};
	
	
	@Test public void testPrimitiveArrays_2()throws IOException
	{
		enter();	
		/*
			Primitives arrays enclosed in an event
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("UTF-8"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  true,//boolean is_described,
										  false //boolean is_enclosed
										  )
					);
				
		D.open();
			D.writeBeginDirect("array");
			D.writeType(TIndicator.TYPE_BOOLEAN_BLOCK);
				D.writeBooleanBlock(new boolean[]{false,true,false});			
			D.writeFlush(TIndicator.FLUSH_BOOLEAN_BLOCK);
			D.writeEndBeginDirect("array");			
			D.writeType(TIndicator.TYPE_BYTE_BLOCK);
				D.writeByteBlock(new byte[]{(byte)0x33,(byte)0xAA});
				D.writeByteBlock((byte)0xF4);				
			D.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
			D.writeEnd();
			
		D.close();
		System.out.println(o.toString());
		Assert.assertTrue(o.toString().equals("{\"array\":[{\"bool[]\":[false,true,false]}]},{\"array\":[{\"byte[]\":\"33AAF4\"}]}"));
		leave();
	};
	
	
	@Test public void testPrimitiveArrays_3()throws IOException
	{
		enter();	
		/*
			Primitives arrays enclosed in an event
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("ASCII"),//Charset charset,
										  SJSONSetting.PLAIN,//CJSONSettings settings,
										  false,//boolean is_described,
										  false //boolean is_enclosed
										  )
					);
				
		D.open();
			D.writeBeginDirect("byte");
			D.writeType(TIndicator.TYPE_SHORT_BLOCK);
				D.writeShortBlock(new short[]{(short)0,(short)-1});
				D.writeShortBlock(new short[]{(short)0,(short)-1});			
			D.writeFlush(TIndicator.FLUSH_SHORT_BLOCK);
			D.writeEndBeginDirect("begin");			
			D.writeType(TIndicator.TYPE_CHAR_BLOCK);
				D.writeCharBlock("ARMAGEDON\"\n\r\t");
				D.writeCharBlock(new char[]{(char)0xFF44});				
			D.writeFlush(TIndicator.FLUSH_CHAR_BLOCK);
			D.writeEnd();
			
		D.close();
		System.out.println(o.toString());
		Assert.assertTrue(o.toString().equals(
		    // {"byte":[[,0,-1,0,-1]]},{"begin":"begin","content":["ARMAGEDON\"\n\r\t\uFF44"]}
			"{\"byte\":[[,0,-1,0,-1]]},{\"begin\":\"begin\",\"content\":[\"ARMAGEDON\\\"\\n\\r\\t\\uFF44\"]}"));
		leave();
	};
	
	
	@Test public void testPrimitiveArrays_4()throws IOException
	{
		enter();	
		/*
			Primitives arrays enclosed in an event
		*/
		StringWriter o = new StringWriter(); 
		IIndicatorWriteFormat D = 
			new CIndicatorWriteFormatProtector(
					new CJSONIndicatorWriteFormat(
										  o, //Writer output,
										  Charset.forName("ASCII"),//Charset charset,
										  SJSONSetting.AJAX,//CJSONSettings settings,
										  true,//boolean is_described,
										  true //boolean is_enclosed
										  )
					);
				
		D.open();
			D.writeBeginDirect("private");
			D.writeType(TIndicator.TYPE_BOOLEAN_BLOCK);
				D.writeBooleanBlock(new boolean[]{false,true,false});			
			D.writeFlush(TIndicator.FLUSH_BOOLEAN_BLOCK);
			D.writeEndBeginDirect("array");			
			D.writeType(TIndicator.TYPE_BYTE_BLOCK);
				D.writeByteBlock(new byte[]{(byte)0x33,(byte)0xAA});
				D.writeByteBlock((byte)0xF4);				
			D.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
			D.writeEnd();
			
			D.writeBeginDirect("byte");
			D.writeType(TIndicator.TYPE_SHORT_BLOCK);
				D.writeShortBlock(new short[]{(short)0,(short)-1});
				D.writeShortBlock(new short[]{(short)0,(short)-1});			
			D.writeFlush(TIndicator.FLUSH_SHORT_BLOCK);
			D.writeEndBeginDirect("begin");			    
			D.writeType(TIndicator.TYPE_CHAR_BLOCK);
				D.writeCharBlock("ARMAGEDON");				
			D.writeFlush(TIndicator.FLUSH_CHAR_BLOCK);
			D.writeEnd();
			
			D.writeBeginDirect("hammer");
			D.writeType(TIndicator.TYPE_INT_BLOCK);
				D.writeIntBlock(new int[]{1,2,3,4});
				D.writeIntBlock(new int[]{5,5,5});			
			D.writeFlush(TIndicator.FLUSH_INT_BLOCK);
			D.writeEnd();
			
			
			D.writeBeginDirect("mutter");
			D.writeType(TIndicator.TYPE_LONG_BLOCK);
				D.writeLongBlock(new long[]{1,2,3,4});
				D.writeLongBlock(new long[]{5,5,5});			
			D.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
			D.writeEnd();
			
			D.writeBeginDirect("fazer");
			D.writeType(TIndicator.TYPE_FLOAT_BLOCK);
				D.writeFloatBlock(new float[]{1,2,3,4});
				D.writeFloatBlock(new float[]{5,5,5});			
			D.writeFlush(TIndicator.FLUSH_FLOAT_BLOCK);
			D.writeEnd();
			
			D.writeBeginDirect("oksan");
			D.writeType(TIndicator.TYPE_DOUBLE_BLOCK);
				D.writeDoubleBlock(new double[]{1,2,3,4});
				D.writeDoubleBlock(new double[]{5,5,5});			
			D.writeFlush(TIndicator.FLUSH_DOUBLE_BLOCK);
			D.writeEnd();
			
		D.close();
		System.out.println(o.toString());
		Assert.assertTrue(o.toString().equals(
		/*
		  [{"begin":"private","content":[{"_bools":[false,true,false]}]},
		   {"array":[{"_bytes":"33AAF4"}]},
		   {"begin":"byte","content":[{"_shorts":[0,-1,0,-1]}]},
		   {"begin":"begin","content":[{"_text":"ARMAGEDON"}]},
		   {"hammer":[{"_ints":[1,2,3,4,5,5,5]}]},
		   {"mutter":[{"_longs":[1,2,3,4,5,5,5]}]},
		   {"fazer":[{"_floats":[1.0,2.0,3.0,4.0,5.0,5.0,5.0]}]},
		   {"oksan":[{"_doubles":[1.0,2.0,3.0,4.0,5.0,5.0,5.0]}]}]
		 */
		   "[{\"begin\":\"private\",\"content\":[{\"_bools\":[false,true,false]}]},"+
		   "{\"array\":[{\"_bytes\":\"33AAF4\"}]},"+
		   "{\"begin\":\"byte\",\"content\":[{\"_shorts\":[0,-1,0,-1]}]},"+
		   "{\"begin\":\"begin\",\"content\":[{\"_text\":\"ARMAGEDON\"}]},"+
		   "{\"hammer\":[{\"_ints\":[1,2,3,4,5,5,5]}]},"+
		   "{\"mutter\":[{\"_longs\":[1,2,3,4,5,5,5]}]},"+
		   "{\"fazer\":[{\"_floats\":[1.0,2.0,3.0,4.0,5.0,5.0,5.0]}]},"+
		   "{\"oksan\":[{\"_doubles\":[1.0,2.0,3.0,4.0,5.0,5.0,5.0]}]}]"
		   ));
		leave();
	};
};