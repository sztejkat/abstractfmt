package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.CIndicatorReadFormatProtector;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EUnexpectedEof;
import java.io.StringReader;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;

/**
	A test of {@link CXMLIndicatorReadFormat} over known good hand crafted text files.
*/
public class TestCXMLIndicatorReadFormat extends sztejkat.utils.test.ATest
{
	/** Validates if <code>is</code> is same as <code>expected</code>
		and prints state information.
		@param is what is read from format
		@param expected what is expected from format.
		@throws AssertionError if not the same.
	*/
	private static void expect(TIndicator is, TIndicator expected)
	{
		System.out.println("Expected indicator:"+expected+" found "+is);
		Assert.assertTrue(is==expected);
	};
	@Test public void testBaseEventShort()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<marcie></marcie>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("marcie".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBaseEventShortFull()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test using full format validation.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<?xml  version=\"1.0\"  encoding=\"UTF-8\"?>\n <?sztejkat.abstractfmt.xml  variant=\"long\"?>"+
					" <root>"+
					"<marcie></marcie>"+
					" </root><oka>"	//<-- note <oka> is out of file and should be invisible.
								),//final Reader input,
								SXMLSettings.LONG_FULL_UTF8,//final CXMLSettings settings,
								false //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("marcie".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			//and check if we stuck at it.
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBaseEventAnonymous()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test with anonymous end
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<marcie></>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("marcie".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBaseEventShortWithSpaces()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test when additional spaces
			are injected.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<marcie  >    </marcie  >"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("marcie".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	@Test public void testBaseEventLong()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test, but this time we do use
			an event name which is not encoded directly
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<event name=\"Monet\"></event>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("Monet".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	@Test public void testBaseEventLongWithSpaces()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test, but this time we do use
			an event name which is not encoded directly and inject 
			multiple spaces 
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<event    name =   \"Monet\"   >   </event   >"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			Assert.assertTrue("Monet".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBaseEventLongEscaped()throws IOException
	{
		enter();
		/*
			This is a plain begin-end test, but this time we do use
			an event name which is not encoded directly and carries encoded
			characters 
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<event name=\"%20;&gt;\"></event>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);	//double check if cursor did not move
			System.out.println("f.getSignalName()=\""+f.getSignalName()+"\"");
			Assert.assertTrue(" >".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	
	
	@Test public void testPrimitiveBooleanDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			with description indicators.
			
			Note:
				When testing elementary primitives boolean is 
				a good example because it is using the same core
				engines as all fetches EXCEPT getChar() which
				is different. Thous detailed testing of getBoolean
				will test most of engine and remaning test may be
				more rough.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<boolean>t</boolean>\n<boolean>t</boolean>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	@Test public void testPrimitiveBooleanDescribedWithInterspaces()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			with description indicators.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<boolean  >   t   </boolean  > \n<boolean>  t  </boolean>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	
	@Test public void testPrimitiveBooleanUnDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			without type indicators.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"t;t;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	@Test public void testPrimitiveBooleanUnDescribedWithInterSpaces()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			without type indicators.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"   t;  t; "
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	
	@Test public void testPrimitiveBooleanUnDescribedInEvent()throws IOException
	{
		enter();
		/*
			We check how system detects boolean primitives
			without type indicators, but enclosed in event.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>t;t;</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			Assert.assertTrue("x".equals(f.getSignalName()));
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readBoolean()==true);
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	
	@Test public void testEofOnRootClose()throws IOException
	{
		enter();
		/*
			This is a test in which we check if root close is EOF
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<?xml  version=\"1.0\"  encoding=\"UTF-8\"?>\n <?sztejkat.abstractfmt.xml  variant=\"long\"?>"+
					" <root>"+
					""+
					" </root>"	//<-- note <oka> is out of file and should be invisible.
								),//final Reader input,
								SXMLSettings.LONG_FULL_UTF8,//final CXMLSettings settings,
								false //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	@Test public void testBooleanBlockEofOnRootClose()throws IOException
	{
		enter();
		/*
			This is a test in which we check if root close is EOF in boolean
			block read with partial result.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<?xml  version=\"1.0\"  encoding=\"UTF-8\"?>\n <?sztejkat.abstractfmt.xml  variant=\"long\"?>"+
					" <root>"+
					"tt"+
					" </root>"	//<-- note <oka> is out of file and should be invisible.
								),//final Reader input,
								SXMLSettings.LONG_FULL_UTF8,//final CXMLSettings settings,
								false //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			try{
				expect(f.getIndicator(), TIndicator.DATA);
				f.readBooleanBlock(new boolean[32]);
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testPrimitiveBooleanBlockDescribed()throws IOException
	{
		enter();
		/*
			Test reading boolean block in one operation.
		
			Note:Boolean, char and byte blocks are specific, so we need to
			test them separately.
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<boolean_array>tfttf</boolean_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				boolean [] x = new boolean[11];
				int r= f.readBooleanBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==5);
				Assert.assertTrue(x[1]==true);
				Assert.assertTrue(x[2]==false);
				Assert.assertTrue(x[3]==true);
				Assert.assertTrue(x[4]==true);
				Assert.assertTrue(x[5]==false);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	@Test public void testPrimitiveBooleanBlockUnDescribed()throws IOException
	{
		enter();
		/*
			Test reading boolean block in one operation 
			
			Note: Even in undescribed mode block must be surrounded by events.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>tfttf</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				boolean [] x = new boolean[11];
				int r= f.readBooleanBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==5);
				Assert.assertTrue(x[1]==true);
				Assert.assertTrue(x[2]==false);
				Assert.assertTrue(x[3]==true);
				Assert.assertTrue(x[4]==true);
				Assert.assertTrue(x[5]==false);
			};
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	@Test public void testPrimitiveBooleanBlockUnDescribedWithSpaces()throws IOException
	{
		enter();
		/*
			Test reading boolean block in one operation when there are gaps and spaces
			
			Note: Even in undescribed mode block must be surrounded by events.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>tf\n\n\t\ttf\n\tt</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				boolean [] x = new boolean[11];
				int r= f.readBooleanBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==5);
				Assert.assertTrue(x[1]==true);
				Assert.assertTrue(x[2]==false);
				Assert.assertTrue(x[3]==true);
				Assert.assertTrue(x[4]==false);
				Assert.assertTrue(x[5]==true);
			};
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	
	@Test public void testPrimitiveBooleanBlockUnDescribedSplit()throws IOException
	{
		enter();
		/*
			Test reading boolean block in many operation when there are gaps and spaces
			
			Note: Even in undescribed mode block must be surrounded by events.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>tf tf t</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //boolean is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				boolean [] x = new boolean[10];
				int r= f.readBooleanBlock(x,1, 2);
				System.out.println(r);
				Assert.assertTrue(r==2);
				Assert.assertTrue(x[1]==true);
				Assert.assertTrue(x[2]==false);
			};
			expect(f.getIndicator(), TIndicator.DATA);
			{
				boolean [] x = new boolean[10];
				int r= f.readBooleanBlock(x,1, 3);
				System.out.println(r);
				Assert.assertTrue(r==3);
				Assert.assertTrue(x[1]==true);
				Assert.assertTrue(x[2]==false);
				Assert.assertTrue(x[3]==true);
			};
			
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	
	
	
	
	
	
	@Test public void testPrimitiveBooleanBlockDescribedWithAltSyntax()throws IOException
	{
		enter();
		/*
			Test reading boolean block in one operation, using alternate
			allowed synatx TF01
		
			Note:Boolean, char and byte blocks are specific, so we need to
			test them separately.
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<boolean_array>TF1t0</boolean_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				boolean [] x = new boolean[11];
				int r= f.readBooleanBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==5);
				Assert.assertTrue(x[1]==true);
				Assert.assertTrue(x[2]==false);
				Assert.assertTrue(x[3]==true);
				Assert.assertTrue(x[4]==true);
				Assert.assertTrue(x[5]==false);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	@Test public void testPrimitiveBooleanBlockDescribedFullRead()throws IOException
	{
		enter();
		/*
			Test reading boolean block in one operation, when read reads
			everything exactly.
		
			
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<boolean_array>TF1t0</boolean_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BOOLEAN_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				boolean [] x = new boolean[10];
				int r= f.readBooleanBlock(x,1, 5);
				System.out.println(r);
				Assert.assertTrue(r==5);
				Assert.assertTrue(x[1]==true);
				Assert.assertTrue(x[2]==false);
				Assert.assertTrue(x[3]==true);
				Assert.assertTrue(x[4]==true);
				Assert.assertTrue(x[5]==false);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_BOOLEAN_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	

	
	
	
	
	
	
	
	
	
	
	
	@Test public void testPrimitiveByteDescribed()throws IOException
	{
		enter();
		/*
			Test reading byte primitive. 
			Remember, byte primtive support normal decimal convention.		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<byte>33</byte>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BYTE);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readByte()==(byte)33);		
			expect(f.getIndicator(), TIndicator.FLUSH_BYTE);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	@Test public void testPrimitiveByteDescribedWithAdditionalSeparator()throws IOException
	{
		enter();
		/*
			Test reading byte primitive, but when separator was not optimized out.
			Remember, byte primtive support normal decimal convention.		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<byte>-34;</byte>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BYTE);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readByte()==(byte)-34);		
			expect(f.getIndicator(), TIndicator.FLUSH_BYTE);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveByteUndescribed()throws IOException
	{
		enter();
		/*
			Test reading byte primitives sequence
			Remember, byte primtive support normal decimal convention.		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"-34;22;-100;"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readByte()==(byte)-34);		
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readByte()==(byte)22);	
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readByte()==(byte)-100);
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	
	
	
	
	@Test public void testPrimitiveByteBlockDescribed()throws IOException
	{
		enter();
		/*
			Test reading byte block in one operation.
		
			Note:Boolean, char and byte blocks are specific, so we need to
			test them separately.
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<byte_array>10abcdEF</byte_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BYTE_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				byte [] x = new byte[12];
				int r= f.readByteBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==4);
				Assert.assertTrue(x[1]==(byte)0x10);
				Assert.assertTrue(x[2]==(byte)0xab);
				Assert.assertTrue(x[3]==(byte)0xcd);
				Assert.assertTrue(x[4]==(byte)0xEF);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_BYTE_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveByteBlockDescribedWithSpaces()throws IOException
	{
		enter();
		/*
			Test reading  byte block in one operation, when there are spaces
			in block in ALLOWED places. Notice, spaces are NOT allowed within a
			byte itself.
		
			Note:Boolean, char and byte blocks are specific, so we need to
			test them separately.
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<byte_array>10 ab cd EF </byte_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BYTE_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				byte [] x = new byte[11];
				int r= f.readByteBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==4);
				Assert.assertTrue(x[1]==(byte)0x10);
				Assert.assertTrue(x[2]==(byte)0xab);
				Assert.assertTrue(x[3]==(byte)0xcd);
				Assert.assertTrue(x[4]==(byte)0xEF);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_BYTE_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveByteBlockDescribedWithSpacesPartial()throws IOException
	{
		enter();
		/*
			Test reading  byte block in more operation, when there are spaces
			in block in ALLOWED places. Notice, spaces are NOT allowed within a
			byte itself.
		
			Note:Boolean, char and byte blocks are specific, so we need to
			test them separately.
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<byte_array>10 ab cd EF </byte_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_BYTE_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				byte [] x = new byte[10];
				int r= f.readByteBlock(x,1, 2);
				System.out.println(r);
				Assert.assertTrue(r==2);
				Assert.assertTrue(x[1]==(byte)0x10);
				Assert.assertTrue(x[2]==(byte)0xab);
			};
			expect(f.getIndicator(), TIndicator.DATA);
			{
				byte [] x = new byte[10];
				int r= f.readByteBlock(x,1, 2);
				System.out.println(r);
				Assert.assertTrue(r==2);
				Assert.assertTrue(x[1]==(byte)0xCD);
				Assert.assertTrue(x[2]==(byte)0xEF);
			};			
			expect(f.getIndicator(), TIndicator.FLUSH_BYTE_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	
	
	@Test public void testPrimitiveByteBlockUnDescribedByteByByte()throws IOException
	{
		enter();
		/*
			Test reading  byte block in byte-by-byte mode.
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>10 ab cd EF </x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			
			expect(f.getIndicator(), TIndicator.DATA);			
			Assert.assertTrue(f.readByteBlock()==0x10);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readByteBlock()==0xAB);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readByteBlock()==0xCD);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readByteBlock()==0xEF);						
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	@Test public void testByteBlockEofOnRootClose()throws IOException
	{
		enter();
		/*
			This is a test in which we check if root close is EOF in byte
			block read with partial result.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<?xml  version=\"1.0\"  encoding=\"UTF-8\"?>\n <?sztejkat.abstractfmt.xml  variant=\"long\"?>"+
					" <root>"+
					"0033"+
					" </root>"	//<-- note <oka> is out of file and should be invisible.
								),//final Reader input,
								SXMLSettings.LONG_FULL_UTF8,//final CXMLSettings settings,
								false //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			try{
				expect(f.getIndicator(), TIndicator.DATA);
				f.readByteBlock(new byte[32]);
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testPrimitiveCharDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects primitive characters.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<char>Z</char>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_CHAR);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readChar()=='Z');
			expect(f.getIndicator(), TIndicator.FLUSH_CHAR);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	@Test public void testPrimitiveCharDescribedEscaped()throws IOException
	{
		enter();
		/*
			We check how system detects primitive characters.
			Notice, due to how the whitespaces are optimized
			whitespaces MUST be escaped in characters.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<char>%20</char>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_CHAR);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readChar()==' ');
			expect(f.getIndicator(), TIndicator.FLUSH_CHAR);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	@Test public void testPrimitiveCharDescribedAmpEscape()throws IOException
	{
		enter();	
		/*
			We check how system detects primitive char using amp escape.
		*/	
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<char>&lt;</char>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_CHAR);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readChar()=='<');
			expect(f.getIndicator(), TIndicator.FLUSH_CHAR);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	@Test public void testPrimitiveCharDescribedSpaces()throws IOException
	{
		enter();
		/*
			We check how system detects primitive characters
			when there are optimized out white spaces, especially
			trailing and leading.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<char> a\n\tbc </char>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //boolean is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_CHAR);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readChar()=='a');
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readChar()==' ');//<- Notice SINGLE white space turned to ' '
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readChar()=='b');
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readChar()=='c');				 
			expect(f.getIndicator(), TIndicator.FLUSH_CHAR);	//<-- notice skipped trailing whitespaces.
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	
	
	
	@Test public void testPrimitiveCharBlockDescribed()throws IOException
	{
		enter();
		/*
			Test reading characters block.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<char_array>aBC   </char_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_CHAR_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				char [] x = new char[10];
				int r= f.readCharBlock(x,1, 4);
				System.out.println(r);
				Assert.assertTrue(r==3);
				Assert.assertTrue(x[1]=='a');
				Assert.assertTrue(x[2]=='B');
				Assert.assertTrue(x[3]=='C');
			};
			expect(f.getIndicator(), TIndicator.FLUSH_CHAR_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	
	
	@Test public void testPrimitiveCharBlockUnDescribed()throws IOException
	{
		enter();
		/*
			Test reading characters block.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>aBC %20;&gt;   </x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //byte is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				char [] x = new char[10];
				int r= f.readCharBlock(x,1, 4);
				System.out.println(r);
				Assert.assertTrue(r==4);
				Assert.assertTrue(x[1]=='a');
				Assert.assertTrue(x[2]=='B');
				Assert.assertTrue(x[3]=='C');
				Assert.assertTrue(x[4]==' ');
			};
			expect(f.getIndicator(), TIndicator.DATA);
			{
				char [] x = new char[10];
				int r= f.readCharBlock(x,1, 4);
				System.out.println(r);
				Assert.assertTrue(r==2);
				Assert.assertTrue(x[1]==' ');
				Assert.assertTrue(x[2]=='>');
			};
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};
	@Test public void testCharBlockEofOnRootClose()throws IOException
	{
		enter();
		/*
			This is a test in which we check if root close is EOF in char
			block read with partial result.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<?xml  version=\"1.0\"  encoding=\"UTF-8\"?>\n <?sztejkat.abstractfmt.xml  variant=\"long\"?>"+
					" <root>"+
					"0033"+
					" </root>"	//<-- note <oka> is out of file and should be invisible.
								),//final Reader input,
								SXMLSettings.LONG_FULL_UTF8,//final CXMLSettings settings,
								false //char is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			try{
				expect(f.getIndicator(), TIndicator.DATA);
				f.readCharBlock(new char[32]);
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Test public void testPrimitiveShortDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects short primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<short>"+Short.MAX_VALUE+"</short>\n<short>"+Short.MIN_VALUE+"</short>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //short is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_SHORT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readShort()==Short.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_SHORT);
			f.next();
			expect(f.getIndicator(), TIndicator.TYPE_SHORT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readShort()==Short.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_SHORT);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	@Test public void testPrimitiveShortUnDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects short primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					" "+Short.MAX_VALUE+";"+Short.MIN_VALUE+";"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //short is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readShort()==Short.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readShort()==Short.MIN_VALUE);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveShortUnDescribedEvent()throws IOException
	{
		enter();
		/*
			We check how system detects short primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>"+Short.MAX_VALUE+";"+Short.MIN_VALUE+"</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //short is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readShort()==Short.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readShort()==Short.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	
	@Test public void testPrimitiveShortBlockDescribed()throws IOException
	{
		enter();
		/*
			Test reading short block in one operation.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<short_array>33;1000;-32763</short_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //short is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_SHORT_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				short [] x = new short[11];
				int r= f.readShortBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==3);
				Assert.assertTrue(x[1]==(short)33);
				Assert.assertTrue(x[2]==(short)1000);
				Assert.assertTrue(x[3]==(short)-32763);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_SHORT_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveShortBlockUnDescribed()throws IOException
	{
		enter();
		/*
			Test reading short block in two operations.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>33;1000;-32763</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //short is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				short [] x = new short[10];
				int r= f.readShortBlock(x,1, 2);
				System.out.println(r);
				Assert.assertTrue(r==2);
				Assert.assertTrue(x[1]==(short)33);
				Assert.assertTrue(x[2]==(short)1000);
			};
			expect(f.getIndicator(), TIndicator.DATA);
			{
				short [] x = new short[10];
				int r= f.readShortBlock(x,0, 1);
				System.out.println(r);
				Assert.assertTrue(r==1);
				Assert.assertTrue(x[0]==(short)-32763);
			};
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	@Test public void testShortBlockEofOnRootClose()throws IOException
	{
		enter();
		/*
			This is a test in which we check if root close is EOF in short
			block read with partial result.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<?xml  version=\"1.0\"  encoding=\"UTF-8\"?>\n <?sztejkat.abstractfmt.xml  variant=\"long\"?>"+
					" <root>"+
					"1;3;4"+
					" </root>"	//<-- note <oka> is out of file and should be invisible.
								),//final Reader input,
								SXMLSettings.LONG_FULL_UTF8,//final CXMLSettings settings,
								false //short is_described
								));
			Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			f.open();
			try{
				expect(f.getIndicator(), TIndicator.DATA);
				f.readShortBlock(new short[32]);
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			expect(f.getIndicator(), TIndicator.EOF);
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testPrimitiveIntDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects int primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<int>"+Integer.MAX_VALUE+"</int>\n<int>"+Integer.MIN_VALUE+"</int>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //int is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_INT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readInt()==Integer.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_INT);
			f.next();
			expect(f.getIndicator(), TIndicator.TYPE_INT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readInt()==Integer.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_INT);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	@Test public void testPrimitiveIntUnDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects int primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					" "+Integer.MAX_VALUE+";"+Integer.MIN_VALUE+";"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //int is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readInt()==Integer.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readInt()==Integer.MIN_VALUE);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveIntUnDescribedEvent()throws IOException
	{
		enter();
		/*
			We check how system detects int primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>"+Integer.MAX_VALUE+";"+Integer.MIN_VALUE+"</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //int is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readInt()==Integer.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readInt()==Integer.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	
	@Test public void testPrimitiveIntBlockDescribed()throws IOException
	{
		enter();
		/*
			Test reading int block in one operation.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<int_array>33;1000;-32763</int_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //int is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_INT_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				int [] x = new int[11];
				int r= f.readIntBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==3);
				Assert.assertTrue(x[1]==33);
				Assert.assertTrue(x[2]==1000);
				Assert.assertTrue(x[3]==-32763);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_INT_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveIntBlockUnDescribed()throws IOException
	{
		enter();
		/*
			Test reading int block in two operations.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>33;1000;-32763</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //int is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				int [] x = new int[10];
				int r= f.readIntBlock(x,1, 2);
				System.out.println(r);
				Assert.assertTrue(r==2);
				Assert.assertTrue(x[1]==33);
				Assert.assertTrue(x[2]==1000);
			}
			expect(f.getIndicator(), TIndicator.DATA);
			{
				int [] x = new int[10];
				int r= f.readIntBlock(x,0, 1);
				System.out.println(r);
				Assert.assertTrue(r==1);
				Assert.assertTrue(x[0]==-32763);
			};
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testPrimitiveLongDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects long primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<long>"+Long.MAX_VALUE+"</long>\n<long>"+Long.MIN_VALUE+"</long>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //long is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_LONG);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readLong()==Long.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_LONG);
			f.next();
			expect(f.getIndicator(), TIndicator.TYPE_LONG);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readLong()==Long.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_LONG);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	@Test public void testPrimitiveLongUnDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects long primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					" "+Long.MAX_VALUE+";"+Long.MIN_VALUE+";"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //long is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readLong()==Long.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readLong()==Long.MIN_VALUE);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveLongUnDescribedEvent()throws IOException
	{
		enter();
		/*
			We check how system detects long primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>"+Long.MAX_VALUE+";"+Long.MIN_VALUE+"</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //long is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readLong()==Long.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readLong()==Long.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	
	@Test public void testPrimitiveLongBlockDescribed()throws IOException
	{
		enter();
		/*
			Test reading long block in one operation.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<long_array>33;1000;-32763</long_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //long is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_LONG_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				long [] x = new long[11];
				int r= f.readLongBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==3);
				Assert.assertTrue(x[1]==(long)33);
				Assert.assertTrue(x[2]==(long)1000);
				Assert.assertTrue(x[3]==(long)-32763);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_LONG_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveLongBlockUnDescribed()throws IOException
	{
		enter();
		/*
			Test reading long block in two operations.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>33;1000;-32763</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //long is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				long [] x = new long[10];
				int r= f.readLongBlock(x,1, 2);
				System.out.println(r);
				Assert.assertTrue(r==2);
				Assert.assertTrue(x[1]==(long)33);
				Assert.assertTrue(x[2]==(long)1000);
			};
			expect(f.getIndicator(), TIndicator.DATA);
			{
				long [] x = new long[10];
				int r= f.readLongBlock(x,0, 1);
				System.out.println(r);
				Assert.assertTrue(r==1);
				Assert.assertTrue(x[0]==(long)-32763);
			};
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testPrimitiveFloatDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects float primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<float>"+Float.MAX_VALUE+"</float>\n<float>"+Float.MIN_VALUE+"</float>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //float is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_FLOAT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readFloat()==Float.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_FLOAT);
			f.next();
			expect(f.getIndicator(), TIndicator.TYPE_FLOAT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readFloat()==Float.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_FLOAT);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	@Test public void testPrimitiveFloatUnDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects float primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					" "+(-Float.MAX_VALUE)+";"+Float.MIN_VALUE+";"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //float is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readFloat()==-Float.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readFloat()==Float.MIN_VALUE);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveFloatUnDescribedEvent()throws IOException
	{
		enter();
		/*
			We check how system detects float primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>"+Float.MAX_VALUE+";"+Float.MIN_VALUE+"</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //float is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readFloat()==Float.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readFloat()==Float.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	
	@Test public void testPrimitiveFloatBlockDescribed()throws IOException
	{
		enter();
		/*
			Test reading float block in one operation.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<float_array>33;1000;-32763</float_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //float is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_FLOAT_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				float [] x = new float[11];
				int r= f.readFloatBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==3);
				Assert.assertTrue(x[1]==(float)33);
				Assert.assertTrue(x[2]==(float)1000);
				Assert.assertTrue(x[3]==(float)-32763);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_FLOAT_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveFloatBlockUnDescribed()throws IOException
	{
		enter();
		/*
			Test reading float block in two operations.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>33;1000;-32763</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //float is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				float [] x = new float[10];
				int r= f.readFloatBlock(x,1, 2);
				System.out.println(r);
				Assert.assertTrue(r==2);
				Assert.assertTrue(x[1]==(float)33);
				Assert.assertTrue(x[2]==(float)1000);
			};
			expect(f.getIndicator(), TIndicator.DATA);
			{
				float [] x = new float[10];
				int r= f.readFloatBlock(x,0, 1);
				System.out.println(r);
				Assert.assertTrue(r==1);
				Assert.assertTrue(x[0]==(float)-32763);
			};
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testPrimitiveDoubleDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects double primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<double>"+Double.MAX_VALUE+"</double>\n<double>"+Double.MIN_VALUE+"</double>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //double is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_DOUBLE);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readDouble()==Double.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_DOUBLE);
			f.next();
			expect(f.getIndicator(), TIndicator.TYPE_DOUBLE);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readDouble()==Double.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.FLUSH_DOUBLE);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	@Test public void testPrimitiveDoubleUnDescribed()throws IOException
	{
		enter();
		/*
			We check how system detects double primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					" "+(-Double.MAX_VALUE)+";"+Double.MIN_VALUE+";"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //double is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readDouble()==-Double.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readDouble()==Double.MIN_VALUE);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveDoubleUnDescribedEvent()throws IOException
	{
		enter();
		/*
			We check how system detects double primitives.
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>"+Double.MAX_VALUE+";"+Double.MIN_VALUE+"</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //double is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readDouble()==Double.MAX_VALUE);
			expect(f.getIndicator(), TIndicator.DATA);
			Assert.assertTrue(f.readDouble()==Double.MIN_VALUE);
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	
	@Test public void testPrimitiveDoubleBlockDescribed()throws IOException
	{
		enter();
		/*
			Test reading double block in one operation.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<double_array>33;1000;-32763</double_array>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								true //double is_described
								));
			Assert.assertTrue(f.isDescribed()==true);
			Assert.assertTrue(f.isFlushing()==true);
			f.open();
			expect(f.getIndicator(), TIndicator.TYPE_DOUBLE_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				double [] x = new double[11];
				int r= f.readDoubleBlock(x,1, 10);
				System.out.println(r);
				Assert.assertTrue(r==3);
				Assert.assertTrue(x[1]==(double)33);
				Assert.assertTrue(x[2]==(double)1000);
				Assert.assertTrue(x[3]==(double)-32763);
			};
			expect(f.getIndicator(), TIndicator.FLUSH_DOUBLE_BLOCK);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
	
	
	@Test public void testPrimitiveDoubleBlockUnDescribed()throws IOException
	{
		enter();
		/*
			Test reading double block in two operations.
		
		
		*/
		IIndicatorReadFormat f = 
		 new CIndicatorReadFormatProtector(
		 	new CXMLIndicatorReadFormat(
			new StringReader(
					"<x>33;1000;-32763</x>"
								),//final Reader input,
								SXMLSettings.LONG_BARE,//final CXMLSettings settings,
								false //double is_described
								));
			f.open();Assert.assertTrue(f.isDescribed()==false);
			Assert.assertTrue(f.isFlushing()==false);
			
			expect(f.getIndicator(), TIndicator.BEGIN_DIRECT);
			f.next();
			expect(f.getIndicator(), TIndicator.DATA);
			
			{
				double [] x = new double[10];
				int r= f.readDoubleBlock(x,1, 2);
				System.out.println(r);
				Assert.assertTrue(r==2);
				Assert.assertTrue(x[1]==(double)33);
				Assert.assertTrue(x[2]==(double)1000);
			};
			expect(f.getIndicator(), TIndicator.DATA);
			{
				double [] x = new double[10];
				int r= f.readDoubleBlock(x,0, 1);
				System.out.println(r);
				Assert.assertTrue(r==1);
				Assert.assertTrue(x[0]==(double)-32763);
			};
			expect(f.getIndicator(), TIndicator.END);
			f.next();
			expect(f.getIndicator(), TIndicator.EOF);
			
		leave();
	};	
};

