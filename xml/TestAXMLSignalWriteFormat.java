package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import java.io.IOException;


/**
	An elementary test which check how {@link AXMLSignalWriteFormat}
	writes data. This test basically compares produced data with hand prooven results
	so it is not a very good test but surely can fight regression problems.
*/
public class TestAXMLSignalWriteFormat extends sztejkat.utils.test.ATest
{
			private static final class DUT extends AXMLSignalWriteFormat
			{
							public final StringBuilder result;
							
					public DUT()
					{
						super(
								 	 32,//final int max_name_length,
									 10,//final int max_events_recursion_depth,
									 '%',';',//final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
									 ';',//final char PRIMITIVES_SEPARATOR_CHAR,
									 "e","n"//final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR
									 );
						result=new StringBuilder(16384);
					};
					@Override protected void closeImpl(){};
					@Override protected boolean isReservedElement(String signal_name){ return "root".equals(signal_name); };
					@Override protected Appendable getOutput(){ return result; };
					
			};
	@org.junit.Test public void testPrimitiveSequence()throws IOException
	{
		enter();
		/*
				Just write some primitives and check how do they look like.
		*/
		DUT d = new DUT();
		d.writeBoolean(true);
		d.writeBoolean(false);
		d.writeByte((byte)7);
		d.writeByte((byte)-120);
		d.writeChar('X');d.writeChar('%');d.writeChar(';');
		d.writeShort((short)3286);d.writeShort((short)-3286);
		d.writeInt(348978934);d.writeInt(-2390490);
		d.writeLong(40909990909L);d.writeLong(-40909990909L);
		d.writeFloat(99.99E17f);d.writeFloat(-99.99E17f);
		d.writeDouble(91.99E37);d.writeDouble(-91.99E37);
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("t;f;7;-120;X;%%;;%3B;;3286;-3286;348978934;-2390490;40909990909;-40909990909;9.999E18;-9.999E18;9.199E38;-9.199E38;".equals(d.result.toString()));
	
		leave();
	};
	
	@org.junit.Test public void testEvents()throws IOException
	{
		enter();
		/*
				Just write some events with and without primitives.
		*/
		DUT d = new DUT();
		d.begin("mamooth");		//<-- this should be a simple name
		d.writeBoolean(true);
		d.begin("Collaboration with dogs");	//this should be encoded.
		d.end();
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<mamooth>t;<e n=\"Collaboration%20;with%20;dogs\"></e></mamooth>".equals(d.result.toString()));
	
		leave();
	};
	
	@org.junit.Test public void testBooleanBlock_1()throws IOException
	{
		enter();
		/*
				See how boolean block is written without a preceeding primitive.
		*/
		DUT d = new DUT();
		d.begin("block");		
		d.writeBooleanBlock(new boolean[]{true,true,true,false,true,false,false});	//intentionally with two operations
		d.writeBooleanBlock(new boolean[]{true,true,true,false,true,false,false});
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>tttftfftttftff</block>".equals(d.result.toString()));
	
		leave();
	};
	@org.junit.Test public void testBooleanBlock_2()throws IOException
	{
		enter();
		/*
				See how boolean block is written with a preceeding primitive.
		*/
		DUT d = new DUT();
		d.begin("block");		
		d.writeChar('A');
		d.writeBooleanBlock(new boolean[]{true,true,true,false,true,false,false});	
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>A;tttftff</block>".equals(d.result.toString()));
	
		leave();
	};
	@org.junit.Test public void testByteBlock_1()throws IOException
	{
		enter();
		/*
				See how byte block is written without a preceeding primitive.
		*/
		DUT d = new DUT();
		
		d.begin("block");		
		d.writeByteBlock(new byte[]{(byte)0xFE,(byte)0x0A,(byte)0x10,(byte)0x12,(byte)0x34,(byte)0x56,(byte)0x78,(byte)0x9A,(byte)0xBC,(byte)0xDE,(byte)0xF0});
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>FE0A10123456789ABCDEF0</block>".equals(d.result.toString()));
	
		leave();
	};
	
	@org.junit.Test public void testByteBlock_2()throws IOException
	{
		enter();
		/*
				See how byte block is written with a preceeding primitive
				and using single byte writes.
		*/
		DUT d = new DUT();
		
		d.begin("block");		
		d.writeByte((byte)-10);
		d.writeByteBlock((byte)0xFE);
		d.writeByteBlock((byte)0xA0);
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>-10;FEA0</block>".equals(d.result.toString()));
	
		leave();
	};
	
	@org.junit.Test public void testCharBlock_1()throws IOException
	{
		enter();
		/*
				See how character block is written
		*/
		DUT d = new DUT();
		
		d.begin("block");		
		d.writeCharBlock("PERPEKU LATOR\n\n\n\tPRZELA<>CZNY;");
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>PERPEKU%20;LATOR%A;%A;%A;%9;PRZELA%3C;%3E;CZNY;</block>".equals(d.result.toString()));
	
		leave();
	};
	@org.junit.Test public void testCharBlock_2()throws IOException
	{
		enter();
		/*
				See how character block is written
		*/
		DUT d = new DUT();
		
		d.begin("block");		
		d.writeCharBlock("PERPEKU LATOR\n\n\n\tPRZELA<>CZNY;".toCharArray());
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>PERPEKU%20;LATOR%A;%A;%A;%9;PRZELA%3C;%3E;CZNY;</block>".equals(d.result.toString()));
	
		leave();
	};
	@org.junit.Test public void testShortBlock()throws IOException
	{
		enter();
		/*
				See how  block is written
		*/
		DUT d = new DUT();
		
		d.begin("block");		
		d.writeShortBlock(new short[]{(short)-32700,(short)9023});
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>-32700;9023;</block>".equals(d.result.toString()));
	
		leave();
	};
	@org.junit.Test public void testIntBlock()throws IOException
	{
		enter();
		/*
				See how  block is written
		*/
		DUT d = new DUT();
		
		d.begin("block");		
		d.writeIntBlock(new int[]{-3002700,999023});
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>-3002700;999023;</block>".equals(d.result.toString()));
	
		leave();
	};
	@org.junit.Test public void testLongBlock()throws IOException
	{
		enter();
		/*
				See how  block is written
		*/
		DUT d = new DUT();
		
		d.begin("block");		
		d.writeLongBlock(new long[]{-30027004890L,434499902443L});
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>-30027004890;434499902443;</block>".equals(d.result.toString()));
	
		leave();
	};
	@org.junit.Test public void testFloatBlock()throws IOException
	{
		enter();
		/*
				See how  block is written
		*/
		DUT d = new DUT();
		
		d.begin("block");		
		d.writeFloatBlock(new float[]{1.55e-3f,-4.4444E5f});
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>0.00155;-444440.0;</block>".equals(d.result.toString()));
	
		leave();
	};
	@org.junit.Test public void testDoubleBlock()throws IOException
	{
		enter();
		/*
				See how  block is written
		*/
		DUT d = new DUT();
		
		d.begin("block");		
		d.writeDoubleBlock(new double[]{1.55999e-3,-4.4444999E5});
		d.end();
		d.close();
		
		System.out.println("\""+d.result+"\"");
		
		org.junit.Assert.assertTrue("<block>0.00155999;-444449.99;</block>".equals(d.result.toString()));
	
		leave();
	};
};