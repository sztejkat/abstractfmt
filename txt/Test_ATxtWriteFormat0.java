package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.*;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;
import org.junit.Assert;

/**
	A test bed for {@link ATxtWriteFormat0}.
*/
public class Test_ATxtWriteFormat0 extends ATest
{
			private static class DUT extends ATxtWriteFormat0
			{
						private StringWriter out;
					DUT(StringWriter out)
					{
						super(0);
						this.out = out;
					};
					@Override protected void openPlainToken()throws IOException
					{
						out.write('<');
					};
					@Override protected void closePlainToken()throws IOException
					{
						out.write('>');
					};
					@Override protected void openStringToken()throws IOException
					{
						out.write('{');
					}
					@Override protected void closeStringToken()throws IOException
					{
						out.write('}');
					}
					@Override protected void outPlainToken(char c)throws IOException
					{
						out.write(c);
					};
					@Override protected void outStringToken(char c)throws IOException
					{
						out.write('s');
						out.write(c);
					};
					@Override protected void endImpl()throws IOException
					{
						out.write(';');
					};
					@Override public int getMaxSupportedSignalNameLength(){ return 1024; };
					@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
					
					@Override protected void beginDirectImpl(String s)throws IOException
					{
						out.write("*"+s);
					};
					@Override protected void beginRegisteredImpl(int index, int order)throws IOException
					{
						out.write("*["+index+","+order+"]");
					};
					@Override protected void beginAndRegisterImpl(String name, int index, int order)throws IOException
					{
						out.write("*"+name+"=["+index+","+order+"]");
					};
					@Override protected void flushImpl()throws IOException{};
					@Override protected void closeImpl()throws IOException{};
					@Override protected void openImpl()throws IOException{};
			};
			
			
			/** Variant with packed byte stream */
			private static final class PackedByteStreamDUT extends DUT
			{
						
					PackedByteStreamDUT(StringWriter out)
					{
						super(out);
					};
					@Override protected void startByteBlock()throws IOException{ startPackedByteBlock(); }
					@Override protected void endByteBlock()throws IOException{ endPackedByteBlock(); }
					@Override protected String formatByteBlock(byte v){ throw new AssertionError(); }
					@Override protected void writeByteBlockImpl(byte v)throws IOException{ writePackedByteBlockImpl(v); }

			};
			
	@Test public void testSingleBoolean()throws IOException
	{
		/*
			Check if token separator is correctly managed
			without enclosing signal
		*/
		enter();
			StringWriter ow = new StringWriter();
			DUT w = new DUT(ow);
		
			w.open();
			w.writeBoolean(true);
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("<true>".equals(o));
			
		leave();
	};
	
	
	@Test public void testDualTokens()throws IOException
	{
		/*
			Check if token separator is correctly managed
			without enclosing signal
		*/
		enter();
			StringWriter ow = new StringWriter();
			DUT w = new DUT(ow);
		
			w.open();
			w.writeBoolean(true);
			w.writeBoolean(false);
			w.writeInt(3456);
			w.writeFloat(1.04E-3f);
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("<true><false><3456><0.00104>".equals(o));
			
		leave();
	};
	@Test public void testCharTokens()throws IOException
	{
		/*
			Check if char is represented as string token.
		*/
		enter();
			StringWriter ow = new StringWriter();
			DUT w = new DUT(ow);
		
			w.open();
			w.writeChar('a');
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("{sa}".equals(o));
			
		leave();
	};
	
	@Test public void testPlainBeginEndSignal()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			DUT w = new DUT(ow);
		
			w.open();
			w.begin("mordimer"); //this does NOT require any escaping
			w.writeChar('a');
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*mordimer{sa};".equals(o));
			
		leave();
	};
	@Test public void testPlainBeginEndSignal2()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			DUT w = new DUT(ow);
		
			w.open();
			w.begin("mordimer"); //this does NOT require any escaping
			w.writeChar('a');
			w.writeChar('a');
			w.end();
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("*mordimer{sa}{sa};".equals(o));
			
		leave();
	};
	
	@Test public void testString()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			DUT w = new DUT(ow);
		
			w.open();
			w.writeString("z3a");
			w.writeString("c");
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("{szs3sa}{sc}".equals(o));
			
		leave();
	};
	
	@Test public void testCharBlock()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			DUT w = new DUT(ow);
		
			w.open();
			w.writeCharBlock(new char[]{'z','3','a'});
			w.writeCharBlock(new char[]{'c'});
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("{szs3sa}{sc}".equals(o));
			
		leave();
	};
	
	
	@Test public void testIntBlock()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			DUT w = new DUT(ow);
		
			w.open();
			w.writeIntBlock(new int[]{1,2});
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("<1><2>".equals(o));
			
		leave();
	};
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------------------------------
	
	
			Packed byte-stream
	
	
	----------------------------------------------------------------------------------------------*/
	@Test public void testPackedByteStreamStandAlone()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			PackedByteStreamDUT w = new PackedByteStreamDUT(ow);
		
			w.open();
			w.writeByteBlock(new byte [] {(byte)0x4c,(byte)0xA1});
			w.writeByteBlock((byte)0x00);
			w.writeByteBlock((byte)0x11);
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("<4CA10011>".equals(o));
			
		leave();
	};
	
	@Test public void testPackedByteStreamAfterItem()throws IOException
	{
		enter();
			StringWriter ow = new StringWriter();
			PackedByteStreamDUT w = new PackedByteStreamDUT(ow);
		
			w.open();
			w.writeInt(3);
			w.writeByteBlock(new byte [] {(byte)0x4c,(byte)0xA1});
			w.writeByteBlock((byte)0x00);
			w.writeByteBlock((byte)0x11);
			w.close();
			
			String o = ow.toString();
			
			System.out.println(o);
			
			Assert.assertTrue("<3><4CA10011>".equals(o));
			
		leave();
	};
};