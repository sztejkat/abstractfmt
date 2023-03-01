package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.*;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;

/**
	A test bed for {@link ATxtWriteFormat1}
*/
public class Test_ATxtWriteFormat1 extends ATest
{
		private static class DUT extends ATxtWriteFormat1
		{
					public final StringBuilder stream = new StringBuilder(); 
				DUT()
				{
					super(5);
				};
				@Override protected void outBeginSignalSeparator()throws IOException
				{ 
					stream.append(' ');
				};
				@Override protected void outEndSignalSeparator()throws IOException
				{ 
					stream.append('+');
				};
				@Override protected void outTokenSeparator()throws IOException
				{
					stream.append(',');
				};
				@Override protected void outTokenToEndSignalSeparator()throws IOException
				{
					stream.append('-');
				};
				@Override protected void outTokenToBeginSignalSeparator()throws IOException
				{
					stream.append('!');
				};
				@Override protected void openPlainTokenImpl()throws IOException
				{
					stream.append('>');
				};
				@Override protected void closePlainTokenImpl()throws IOException
				{
					stream.append('<');
				};
				@Override protected void openStringTokenImpl()throws IOException
				{
					stream.append('\"');
				};
				@Override protected void closeStringTokenImpl()throws IOException
				{
					stream.append('\"');
				};
				@Override protected void outPlainToken(char c)throws IOException
				{
					stream.append('p');
					stream.append(c);
				};
				@Override protected void outStringToken(char c)throws IOException
				{
					stream.append('s');
					stream.append(c);
				};
				@Override protected void beginAndRegisterImpl(String name, int index, int order)throws IOException
				{
					stream.append("*beginAndRegisterImpl*");
				};
				@Override protected void beginRegisteredImpl(int index, int order)throws IOException
				{
					stream.append("*beginRegisteredImpl*");
				};
				@Override protected  void beginDirectImpl(String name)throws IOException
				{
					stream.append("*beginDirectImpl*");
				};
				@Override protected  void endImpl()throws IOException
				{
					stream.append("*endImpl*");
				};
				@Override protected  void closeImpl()throws IOException{};
				@Override protected  void openImpl()throws IOException{};
				@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
				@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
		};
		/** This class tests how the engine works if string tokens stitching is disabled */
		private static class NonStitchingDUT extends DUT
		{
				@Override protected void closeStringToken()throws IOException
				{
					closeStringToken_no_stitching();
				};
		};
		/** This test check how to treat char and chars blocks differently from strings */
		private static class NonStringDUT extends DUT
		{
				NonStringDUT()
				{
					super();
				};
				
				
				@Override protected void openBlockCharToken()throws IOException
				{
					defaultOpenBlockCharToken();
				};
				@Override protected void openBlockCharTokenImpl()throws IOException
				{
					stream.append("[b");
				};
				@Override protected void closeBlockCharToken()throws IOException
				{
					defaultCloseBlockCharToken();
				};
				@Override protected void closeBlockCharTokenImpl()throws IOException
				{
					stream.append("b]");
				};
				@Override protected void outBlockCharToken(char c)throws IOException
				{
					stream.append('b');
					stream.append(c);
				};
				
				
				
				@Override protected void openSingleCharToken()throws IOException
				{
					defaultOpenSingleCharToken();
				};
				@Override protected void openSingleCharTokenImpl()throws IOException
				{
					stream.append("[c");
				};
				@Override protected void closeSingleCharToken()throws IOException
				{
					defaultCloseSingleCharToken();
				};
				@Override protected void closeSingleCharTokenImpl()throws IOException
				{
					stream.append("c]");
				};
				@Override protected void outSingleCharToken(char c)throws IOException
				{
					stream.append('c');
					stream.append(c);
				};
		};
		
		
		/** This test check how to treat chars blocks differently from strings */
		private static class NonStringBlocksDUT extends DUT
		{					 
				NonStringBlocksDUT()
				{
					super();
				};
				
				
				@Override protected void openBlockCharToken()throws IOException
				{
					defaultOpenBlockCharToken();
				};
				@Override protected void openBlockCharTokenImpl()throws IOException
				{
					stream.append("[b");
				};
				@Override protected void closeBlockCharToken()throws IOException
				{
					defaultCloseBlockCharToken();
				};
				@Override protected void closeBlockCharTokenImpl()throws IOException
				{
					stream.append("b]");
				};
				@Override protected void outBlockCharToken(char c)throws IOException
				{
					stream.append('b');
					stream.append(c);
				};
		};
		
		
		/** This class tests how the engine works if byte blocks are packed. */
		private static class PackedByteBlocksDUT extends DUT
		{
				@Override protected void startByteBlock()throws IOException{ startPackedByteBlock(); }
				@Override protected void endByteBlock()throws IOException{ endPackedByteBlock(); }
				@Override protected String formatByteBlock(byte v){ throw new AssertionError(); }
				@Override protected void writeByteBlockImpl(byte v)throws IOException{ writePackedByteBlockImpl(v); }
		}
		
	@Test public void testPlainTokenSequencingWithoutSignal()throws IOException
	{
		enter();
			DUT d = new DUT();
			d.open();
				d.writeInt(10);
				d.writeInt(0);
				d.writeInt(0);
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue(">p1p0<,>p0<,>p0<".equals(o));
		leave();
	};
	
	@Test public void testPlainTokenSequencingWithSignals()throws IOException
	{
		enter();
			DUT d = new DUT();
			d.open();
				d.begin("");
				d.writeInt(10);
				d.writeInt(0);
				d.end();
				d.writeInt(10);
				d.writeInt(0);
				d.begin("");
				d.writeInt(10);
				d.writeInt(0);
				d.end();
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue(("*beginDirectImpl* >p1p0<,>p0<-*endImpl*+"+
								">p1p0<,>p0<"+
								"!*beginDirectImpl* >p1p0<,>p0<-*endImpl*").equals(o));
		leave();
	};
	
	@Test public void testStringTokenStitchingWithoutSignal()throws IOException
	{
		enter();
			DUT d = new DUT();
			d.open();
				d.writeChar('a');
				d.writeString("DA");
				d.writeString("DA");
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue("\"sasDsAsDsA\"".equals(o));
		leave();
	};
	
	
	@Test public void testDisabledStringTokenStitchingWithoutSignal()throws IOException
	{
		enter();
			NonStitchingDUT d = new NonStitchingDUT();
			d.open();
				d.writeChar('a');
				d.writeString("DA");
				d.writeString("DA");
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue("\"sa\",\"sDsA\",\"sDsA\"".equals(o));
		leave();
	};
	
	
	@Test public void testStringTokenStitchingWithSignal()throws IOException
	{
		enter();
			DUT d = new DUT();
			d.open();
				d.writeChar('a');
				d.writeString("DA");
				d.begin("");
				d.writeString("DA");
				d.end();
				d.writeString("DA");
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue(
							("\"sasDsA\""+
								"!*beginDirectImpl*"+
								" \"sDsA\""+
								"-*endImpl*"+
								"+\"sDsA\"")
								.equals(o));
		leave();
	};
	@Test public void testStringTokenStitchingMixedWithPlain()throws IOException
	{
		enter();
			DUT d = new DUT();
			d.open();
				d.writeChar('a');
				d.writeChar('D');
				d.writeChar('A');
				d.writeInt(3);
				d.writeChar('a');
				d.writeChar('D');
				d.writeChar('A');
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue(("\"sasDsA\""+
							   ",>p3<,"+
							   "\"sasDsA\"")
							   .equals(o));
		leave();
	};
	
	@Test public void testStructMixup()throws IOException
	{
		enter();
			DUT w = new DUT();
		
			w.open();
			w.begin(""); //this DOES require enclosing
				w.begin("");
					w.writeInt(33);
				w.end();
				w.begin("");
					w.writeInt(90);
					w.writeInt(40);
					w.writeInt(90);
				w.end();
			w.end();
			w.close();
			
			String o = w.stream.toString();
			System.out.println(o);
			Assert.assertTrue(("*beginDirectImpl**beginDirectImpl* >p3p3<-*endImpl*"+
							  "*beginDirectImpl* >p9p0<,>p4p0<,>p9p0<-*endImpl**endImpl*").equals(o));
			
		leave();
	};
	
	
	@Test public void testSingleCharDifferentThanString()throws IOException
	{
		enter();
			NonStringDUT d = new NonStringDUT();
			d.open();
				d.writeChar('a');
				d.writeChar('D');
				d.writeInt(3);
				d.writeChar('a');
				d.writeChar('D');
				d.writeString("aDA");
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue(("[ccac],[ccDc]"+
							   ",>p3<,"+
							   "[ccac],[ccDc]"+
							   ",\"sasDsA\"")
							   .equals(o));
		leave();
	};
	
	@Test public void testBlockCharDifferentThanString()throws IOException
	{
		enter();
			NonStringDUT d = new NonStringDUT();
			d.open();
				d.writeChar('a');
				d.writeChar('D');
				d.writeCharBlock(new char[]{'x','y','z'});
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue(("[ccac],[ccDc]"+
							   ",[bbxbybzb]")
							   .equals(o));
		leave();
	};
	
	@Test public void testBlockCharDifferentThanStringTerminatesStitched()throws IOException
	{
		enter();
			NonStringBlocksDUT d = new NonStringBlocksDUT();
			d.open();
				d.writeChar('a');
				d.writeChar('D');
				d.writeCharBlock(new char[]{'x','y','z'});
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue(("\"sasD\""+
							   ",[bbxbybzb]")
							   .equals(o));
		leave();
	};
	
	
	@Test public void testByteBlockPackedInStruct()throws IOException
	{
		enter();
			PackedByteBlocksDUT d = new PackedByteBlocksDUT();
			d.open();
				d.writeByteBlock(new byte[]{(byte)0x3A});
				d.begin("a");
					d.writeByteBlock(new byte[]{(byte)0x3C});
				d.end();
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue((">p3pA<!*beginDirectImpl* >p3pC<-*endImpl*")
							   .equals(o));
		leave();
	};
	
	@Test public void testByteBlockPackedInStructEnd()throws IOException
	{
		enter();
			PackedByteBlocksDUT d = new PackedByteBlocksDUT();
			d.open();
				d.begin("a");
					d.begin("a");
						d.writeByteBlock(new byte[]{(byte)0x3A});
					d.end();
					d.writeByteBlock(new byte[]{(byte)0x3C});
				d.end();
			d.close();
			String o = d.stream.toString();
			System.out.println(o);
			Assert.assertTrue(("*beginDirectImpl**beginDirectImpl* >p3pA<-*endImpl*+>p3pC<-*endImpl*")
							   .equals(o));
		leave();
	};
};