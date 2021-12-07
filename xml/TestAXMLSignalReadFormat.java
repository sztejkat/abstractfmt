package sztejkat.abstractfmt.xml;
import sztejkat.abstractfmt.*;
import java.io.IOException;
import java.io.StringReader;

/**
	Test for {@link AXMLSignalReadFormat}, based on reading of known good/bad content.
*/
public class TestAXMLSignalReadFormat extends sztejkat.utils.test.ATest
{
				private static final class DUT extends AXMLSignalReadFormat
				{
						StringReader in; 
					DUT()
					{
						super(
								16,//final int max_name_length,
								16,// final int max_events_recursion_depth,
								false,//final boolean strict_described_types,
								'%',';',//final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
								';',//final char PRIMITIVES_SEPARATOR_CHAR,
								"e","n",//final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR,
								4 //final int max_type_tag_length
								); 
					};
					DUT(String s)
					{
						this();
						open(s);
					};
					void open(String s){ in = new java.io.StringReader(s); };
					@Override protected int readImpl()throws IOException
					{
						return in.read();
					};
					@Override protected int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readByteBlockImpl()throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readCharBlockImpl(Appendable buffer, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException{ throw new AbstractMethodError(); };
					@Override protected void closeImpl()throws IOException{ throw new AbstractMethodError(); };;	
				};
				
		/* ********************************************************
		
		
					Known GOOD tests
		
		
		
		********************************************************/
	
		/*---------------------------------------------------------
					Signals
		---------------------------------------------------------*/
		@org.junit.Test public void testSignal_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				begin and end signal
			*/
			DUT d = new DUT(
						"<branderburg></branderburg>"
						);
			org.junit.Assert.assertTrue("branderburg".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testSignal_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				begin and end signal when there is present some 
				garbage in front of begin
			*/
			DUT d = new DUT(
						"\n\t <branderburg></branderburg>"
						);
			org.junit.Assert.assertTrue("branderburg".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testSignal_3()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				begin and end signal when there is present some 
				garbage in front of end
			*/
			DUT d = new DUT(
						"<branderburg>\n\t </branderburg>"
						);
			org.junit.Assert.assertTrue("branderburg".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testSignal_4()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				begin and end signal when there is present some 
				garbage after the end
			*/
			DUT d = new DUT(
						"<branderburg></branderburg>\n\t "
						);
			org.junit.Assert.assertTrue("branderburg".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testSignal_5()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				begin and end signal when there are some comments,
				which include other signals.
			*/
			DUT d = new DUT(
						"<!-- <mekefeke></mekefeke> -->  <branderburg></branderburg>\n\t "
						);
			org.junit.Assert.assertTrue("branderburg".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testSignal_6()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				begin and end signal when there are some processing commands,
				which include other signals.
			*/
			DUT d = new DUT(
						"<? porcupine tree ?>  <branderburg></branderburg>\n\t "
						);
			org.junit.Assert.assertTrue("branderburg".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		
		@org.junit.Test public void testSignal_7()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				begin and end signal when there is present a space
				after element
			*/
			DUT d = new DUT(
						"<branderburg  ></branderburg  >\n\t "
						);
			org.junit.Assert.assertTrue("branderburg".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		
		
		@org.junit.Test public void testLongSignal_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read long begin/end
			*/
			DUT d = new DUT(
						"<e n=\"ber-nice\"></e>"
						);
			org.junit.Assert.assertTrue("ber-nice".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testLongSignal_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read long begin/end
				with escaped names.
			*/
			DUT d = new DUT(
						"<e n=\"ber%20;nice\"></e>"
						);
			org.junit.Assert.assertTrue("ber nice".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		
		@org.junit.Test public void testNestedSignal()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read sequence 
				of nesteed signals
			*/
			DUT d = new DUT(
						"<bridgite><e n=\"bardo\"></e></bridgite>"
						);
			org.junit.Assert.assertTrue("bridgite".equals(d.next()));
			org.junit.Assert.assertTrue("bardo".equals(d.next()));
			org.junit.Assert.assertTrue(null==d.next());
			org.junit.Assert.assertTrue(null==d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		
		
		
		
		
		
		/*---------------------------------------------------------
					elementary primitives.
		---------------------------------------------------------*/
		@org.junit.Test public void testReadBoolean_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along primitives
			*/
			DUT d = new DUT(
						"t;f;"
						);
			org.junit.Assert.assertTrue(d.readBoolean()==true);
			org.junit.Assert.assertTrue(d.readBoolean()==false);
			try{
				d.readBoolean();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testReadBoolean_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along primitives with spearators
			*/
			DUT d = new DUT(
						"\n\rt;\n\r\tf;"
						);
			org.junit.Assert.assertTrue(d.readBoolean()==true);
			org.junit.Assert.assertTrue(d.readBoolean()==false);
			try{
				d.readBoolean();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testReadByte()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along primitives
			*/
			DUT d = new DUT(
						"-120;120;"
						);
			org.junit.Assert.assertTrue(d.readByte()==(byte)-120);
			org.junit.Assert.assertTrue(d.readByte()==(byte)120);
			try{
				d.readByte();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testReadShort()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along primitives with separators
			*/
			DUT d = new DUT(
						" -1345; 1349; "
						);
			org.junit.Assert.assertTrue(d.readShort()==(short)-1345);
			org.junit.Assert.assertTrue(d.readShort()==(short)1349);
			try{
				d.readShort();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testReadChar_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along char
			*/
			DUT d = new DUT(
						";;1;A;"
						);
			org.junit.Assert.assertTrue(d.readChar()==';');
			org.junit.Assert.assertTrue(d.readChar()=='1');
			org.junit.Assert.assertTrue(d.readChar()=='A');
			try{
				d.readChar();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testReadChar_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along char, encoded with full termination 
			*/
			DUT d = new DUT(
						"%0;;%9;;%AA;;%AAC;;%CCAD;;%%;; "
						);
			org.junit.Assert.assertTrue(d.readChar()=='\u0000');
			org.junit.Assert.assertTrue(d.readChar()=='\u0009');
			org.junit.Assert.assertTrue(d.readChar()=='\u00AA');
			org.junit.Assert.assertTrue(d.readChar()=='\u0AAC');
			org.junit.Assert.assertTrue(d.readChar()=='\uCCAD');
			org.junit.Assert.assertTrue(d.readChar()=='%');
			try{
				d.readChar();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testReadChar_3()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along char, encoded with stripped termination 
			*/
			DUT d = new DUT(
						"%0;%9;%AA;%AAC;%CCAD;%%; "
						);
			org.junit.Assert.assertTrue(d.readChar()=='\u0000');
			org.junit.Assert.assertTrue(d.readChar()=='\u0009');
			org.junit.Assert.assertTrue(d.readChar()=='\u00AA');
			org.junit.Assert.assertTrue(d.readChar()=='\u0AAC');
			org.junit.Assert.assertTrue(d.readChar()=='\uCCAD');
			org.junit.Assert.assertTrue(d.readChar()=='%');
			try{
				d.readChar();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testReadInt()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along primitives with separators
			*/
			DUT d = new DUT(
						"-684598;7673980;"
						);
			org.junit.Assert.assertTrue(d.readInt()==-684598);
			org.junit.Assert.assertTrue(d.readInt()==7673980);
			try{
				d.readInt();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		@org.junit.Test public void testReadLong()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along primitives with separators
			*/
			DUT d = new DUT(
						"-68459899898;7673984888930;"
						);
			org.junit.Assert.assertTrue(d.readLong()==-68459899898L);
			org.junit.Assert.assertTrue(d.readLong()==7673984888930L);
			try{
				d.readLong();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		
		@org.junit.Test public void testReadFloat()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-afloat primitives with separators
			*/
			DUT d = new DUT(
						"-684598.898;76739.8480;"
						);
			org.junit.Assert.assertTrue(d.readFloat()==-684598.898f);
			org.junit.Assert.assertTrue(d.readFloat()==76739.8480f);
			try{
				d.readFloat();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		
		@org.junit.Test public void testReadDouble()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-adouble primitives with separators
			*/
			DUT d = new DUT(
						"-68459899898.0;7673984888930.0;"
						);
			org.junit.Assert.assertTrue(d.readDouble()==-68459899898.0);
			org.junit.Assert.assertTrue(d.readDouble()==7673984888930.0);
			try{
				d.readDouble();
				org.junit.Assert.fail();
			}catch(EUnexpectedEof ex){};
			leave();
		};
		
		
		
		
		
		
		
		
		/* ********************************************************
		
		
					Known BAD tests
		
		
		
		********************************************************/
		/*---------------------------------------------------------
					Signals
		---------------------------------------------------------*/
		@org.junit.Test public void testBadSignal_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we fail on 
				improper simple tag
			*/
			DUT d = new DUT(
						"<bridgite></bridgit>"
						);
			System.out.println(d.next());
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testBadSignal_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we fail on 
				improper simple tag
			*/
			DUT d = new DUT(
						"<bridgiteressefonhungarianmasterofstones></bridgit>"
						);
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testBadSignal_3()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we fail on 
				improper simple tag
			*/
			DUT d = new DUT(
						"<bridgite e3f></bridgite>"
						);
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testBadSignal_4()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we fail on 
				improper simple tag
			*/
			DUT d = new DUT(
						"<e></bridgite>"
						);
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testBadSignal_5()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we fail on 
				improper simple tag
			*/
			DUT d = new DUT(
						"<e namae=\"dreed\"></bridgite>"
						);
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testBadSignal_6()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we fail on 
				improper simple tag
			*/
			DUT d = new DUT(
						"<e n></bridgite>"
						);
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testBadSignal_7()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we fail on 
				improper simple tag
			*/
			DUT d = new DUT(
						"<e n=></bridgite>"
						);
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testBadSignal_8()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we fail on 
				improper simple tag
			*/
			DUT d = new DUT(
						"<e n=\"></bridgite>"
						);
			try{
					d.next();
					org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testReadByte_Bad_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can detected byte
				error if const is too large.
			*/
			DUT d = new DUT(
						"-129;120;"
						);
			try{
				d.readByte();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testReadShort_Bad_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can 
				detect short error if there is no digit.
			*/
			DUT d = new DUT(
						"394A3;"
						);
			try{
				d.readShort();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testReadShort_Bad_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can detect too
				long number
			*/
			DUT d = new DUT(
						"0000000000000000000000000000000000000000000000000000112;"
						);
			try{
				d.readShort();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testReadChar_Bad_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can detect
				errornous chars
			*/
			DUT d = new DUT(
						"%;;"
						);
			try{
				d.readChar();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
		@org.junit.Test public void testReadChar_Bad_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can detect
				errornous chars
			*/
			DUT d = new DUT(
						"%ZA00;;"
						);
			try{
				d.readChar();
				org.junit.Assert.fail();
			}catch(ECorruptedFormat ex){ System.out.println(ex);};
			leave();
		};
};