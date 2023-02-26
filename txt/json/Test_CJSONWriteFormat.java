package sztejkat.abstractfmt.txt.json;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;
import org.junit.Assert;
/**
	Test for {@link CJSONWriteFormat} against well known
	good cases.
*/
public class Test_CJSONWriteFormat extends sztejkat.abstractfmt.test.ATest
{
	@Test public void testOpenCloseWritten()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[]".equals(s));
		leave();
	};
	
	@Test public void testWriteStandAlonePrimitives()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.writeBoolean(false);
			d.writeBoolean(true);
			d.writeInt(-345);
			d.writeShort((short)100);
			d.writeLong(132345);
			d.writeFloat(3.4f);
			d.writeDouble(9.9);
			d.writeChar('a');
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[false,true,-345,100,132345,3.4,9.9,\"a\"]".equals(s));
		leave();
	};
	
	
	@Test public void testElementaryCharsNotStitched()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.writeChar('a');
			d.writeChar('a');
			d.writeChar('a');
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[\"a\",\"a\",\"a\"]".equals(s));
		leave();
	};
	
	@Test public void testCharBlockStitched()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.writeCharBlock('a');
			d.writeCharBlock(new char[]{'c','d','e'});
			d.writeCharBlock(new char[]{'c','d','e'});
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[\"acdecde\"]".equals(s));
		leave();
	};
	
	@Test public void testStringBlockStitched()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.writeString('a');
			d.writeString("cde");
			d.writeString("cde");
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[\"acdecde\"]".equals(s));
		leave();
	};
	
	
	@Test public void testEmptyStruct()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[]}]".equals(s));
		leave();
	};
	
	@Test public void testSingleElementStruct()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeInt(3);
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":3}]".equals(s));
		leave();
	};
	
	@Test public void testMultiElementStruct()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeInt(3);
			d.writeInt(3);
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[3,3]}]".equals(s));
		leave();
	};
	
	@Test public void testSingleCharElementStruct()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeChar('3');
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":\"3\"}]".equals(s));
		leave();
	};
	
	@Test public void testMultiCharElementStruct()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeChar('3');
			d.writeChar('3');
			d.writeChar('3');
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[\"3\",\"3\",\"3\"]}]".equals(s));
		leave();
	};
	
	@Test public void testSingleCharElementStructFlushed()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeChar('3');
			d.flush(); //this forces array mode.
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[\"3\"]}]".equals(s));
		leave();
	};
	
	@Test public void testSingleElementStructFlushed()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeLong(7);
			d.flush(); //this forces array mode.
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[7]}]".equals(s));
		leave();
	};
	
	@Test public void testStringElementStruct()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeString("mortie");
			d.writeString("marry");
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[\"mortiemarry\"]}]".equals(s));
		leave();
	};
	
	@Test public void testStringElementStructFlushed()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeString("mortie");
			d.flush(); //this forces token completion 
			d.writeString("marry");
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[\"mortie\",\"marry\"]}]".equals(s));
		leave();
	};
	
	
	@Test public void testStructAfterElement()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.writeInt(3);
			d.begin("marcie");
			d.writeInt(3);
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[3,{\"marcie\":3}]".equals(s));
		leave();
	};
	
	@Test public void testStructAfterString()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.writeString("33");
			d.begin("marcie");
			d.writeChar('3');
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[\"33\",{\"marcie\":\"3\"}]".equals(s));
		leave();
	};
	
	@Test public void testStructAfterChar()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.writeChar('3');
			d.begin("marcie");
			d.writeChar('3');
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[\"3\",{\"marcie\":\"3\"}]".equals(s));
		leave();
	};
	
	@Test public void testStructAfterStruct()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeInt(3);
			d.end();
			d.begin("marcie");
			d.writeInt(3);
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":3},{\"marcie\":3}]".equals(s));
		leave();
	};
	
	@Test public void testStructFirstInStruct()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
				d.begin("darcie");
				d.end();
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[{\"darcie\":[]}]}]".equals(s));
		leave();
	};
	
	@Test public void testStructNextInStruct()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
				d.writeString("lora");
				d.begin("darcie");
				d.end();
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[\"lora\",{\"darcie\":[]}]}]".equals(s));
		leave();
	};
	
	@Test public void testStructNextInStruct_2()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
				d.writeInt(234);
				d.begin("darcie");
				d.end();
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[234,{\"darcie\":[]}]}]".equals(s));
		leave();
	};
	@Test public void testStructNextInStruct_3()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
				d.writeInt(234);
				d.writeInt(235);
				d.begin("darcie");
				d.end();
				d.writeInt(235);
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[234,235,{\"darcie\":[]},235]}]".equals(s));
		leave();
	};
	
	
	@Test public void testBlockTerminatedByBeginFollowedByBlock()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.begin("marcie");
			d.writeIntBlock(4);
			d.writeIntBlock(4);
			d.writeIntBlock(4);
				d.begin("darcie");
				d.writeCharBlock('c');
				d.writeCharBlock('c');
				d.writeCharBlock('c');
				d.end();
			d.writeIntBlock(2);
			d.writeIntBlock(2);
			d.writeIntBlock(2);	
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[{\"marcie\":[4,4,4,{\"darcie\":[\"ccc\"]},2,2,2]}]".equals(s));
		leave();
	};
	
	
	@Test public void testAfterMultipleBlockTerminatedByBeginFollowedByBlock()throws IOException
	{
		enter();
		StringWriter w = new StringWriter();
		CJSONWriteFormat d = new CJSONWriteFormat(w);
		
		d.open();
			d.writeInt(44);
			d.writeBoolean(false);
			d.begin("marcie");
			d.writeIntBlock(4);
			d.writeIntBlock(4);
			d.writeIntBlock(4);
				d.begin("darcie");
				d.writeCharBlock('c');
				d.writeCharBlock('c');
				d.writeCharBlock('c');
				d.end();
			d.writeIntBlock(2);
			d.writeIntBlock(2);
			d.writeIntBlock(2);	
			d.end();
		d.close();
		
		String s= w.toString();
		System.out.println(s);
		
		Assert.assertTrue("[44,false,{\"marcie\":[4,4,4,{\"darcie\":[\"ccc\"]},2,2,2]}]".equals(s));
		leave();
	};
};