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
								'%',';',//final char ESCAPE_CHAR, final char ESCAPE_CHAR_END, 
								';',//final char PRIMITIVES_SEPARATOR_CHAR,
								"e","n",//final String LONG_SIGNAL_ELEMENT,final String LONG_SIGNAL_ELEMENT_ATTR,
								4, //final int max_type_tag_length
								1024 //max_inter_signal_chars
								); 
					};
					DUT(String s)
					{
						this();
						open(s);
					};
					@Override public boolean isDescribed(){ return false; };
					void open(String s){ in = new java.io.StringReader(s); };
					@Override protected int readImpl()throws IOException
					{
						return in.read();
					};
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
				stand-along primitive when there is signal instead of data
			*/ 
			DUT d = new DUT(
						"<begin>"
						);
			try{
				d.readBoolean();
				org.junit.Assert.fail();
			}catch(ENoMoreData ex){};
			org.junit.Assert.assertTrue("begin".equals(d.next()));
			leave();
		};
		
		@org.junit.Test public void testReadBoolean_3()throws IOException
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
		@org.junit.Test public void testReadByte_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along primitive when there is signal instead of data
			*/ 
			DUT d = new DUT(
						"<begin>"
						);
			try{
				d.readByte();
				org.junit.Assert.fail();
			}catch(ENoMoreData ex){};
			org.junit.Assert.assertTrue("begin".equals(d.next()));
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
		@org.junit.Test public void testReadChar_4()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read simple
				stand-along primitive when there is signal instead of data
			*/ 
			DUT d = new DUT(
						"<begin>"
						);
			try{
				d.readChar();
				org.junit.Assert.fail();
			}catch(ENoMoreData ex){};
			org.junit.Assert.assertTrue("begin".equals(d.next()));
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
		
		
		
		
		/*-----------------------------------------------------------
					Blocks of primitives
		-----------------------------------------------------------*/
		@org.junit.Test public void testReadBooleanBlock_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read boolean block
			*/
			DUT d = new DUT(
						"<bools>01FtT10111110</bools>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				final boolean [] a = new boolean[20];
				int r = d.readBooleanBlock(a,0,20);
				System.out.println(r+" "+java.util.Arrays.toString(a));
				org.junit.Assert.assertTrue(r==13);
				org.junit.Assert.assertTrue(a[0]==false);
				org.junit.Assert.assertTrue(a[1]==true);
				org.junit.Assert.assertTrue(a[2]==false);
				org.junit.Assert.assertTrue(a[3]==true);
				org.junit.Assert.assertTrue(a[4]==true);
				
				org.junit.Assert.assertTrue(d.readBooleanBlock(a,0,20)==0);
				org.junit.Assert.assertTrue(d.readBooleanBlock(a,0,20)==0);
			};
			leave();
		};
		@org.junit.Test public void testReadBooleanBlock_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read boolean block with separators
				within.
			*/
			DUT d = new DUT(
						"<bools>01FtT101\n 11\t   110</bools>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				final boolean [] a = new boolean[20];
				int r = d.readBooleanBlock(a,0,20);
				System.out.println(r+" "+java.util.Arrays.toString(a));
				org.junit.Assert.assertTrue(r==13);
				org.junit.Assert.assertTrue(a[0]==false);
				org.junit.Assert.assertTrue(a[1]==true);
				org.junit.Assert.assertTrue(a[2]==false);
				org.junit.Assert.assertTrue(a[3]==true);
				org.junit.Assert.assertTrue(a[4]==true);
				
				org.junit.Assert.assertTrue(d.readBooleanBlock(a,0,20)==0);
			};
			leave();
		};
		@org.junit.Test public void testReadByteBlock_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read byte block
			*/
			DUT d = new DUT(
						"<bools>000102 03FAfbac </bools>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				final byte [] a = new byte[20];
				int r = d.readByteBlock(a,0,20);
				System.out.println(r+" "+java.util.Arrays.toString(a));
				org.junit.Assert.assertTrue(r==7);
				org.junit.Assert.assertTrue(a[0]==(byte)0);
				org.junit.Assert.assertTrue(a[1]==(byte)1);
				org.junit.Assert.assertTrue(a[2]==(byte)2);
				org.junit.Assert.assertTrue(a[3]==(byte)3);
				org.junit.Assert.assertTrue(a[4]==(byte)0xFA);
				org.junit.Assert.assertTrue(a[5]==(byte)0xFB);
				org.junit.Assert.assertTrue(a[6]==(byte)0xAC);
				
				org.junit.Assert.assertTrue(d.readByteBlock(a,0,20)==0);
			};
			leave();
		};
		@org.junit.Test public void testReadByteBlock_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read byte block
				when fetching with byte-by-byte routine
			*/
			DUT d = new DUT(
						"<bools>000102 03FAfbac </bools>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
				
				org.junit.Assert.assertTrue(d.readByteBlock()==0);
				org.junit.Assert.assertTrue(d.readByteBlock()==1);
				org.junit.Assert.assertTrue(d.readByteBlock()==2);
				org.junit.Assert.assertTrue(d.readByteBlock()==3);
				org.junit.Assert.assertTrue(d.readByteBlock()==0xFA);
				org.junit.Assert.assertTrue(d.readByteBlock()==0xFB);
				org.junit.Assert.assertTrue(d.readByteBlock()==0xAC);
				org.junit.Assert.assertTrue(d.readByteBlock()==-1);
				org.junit.Assert.assertTrue(d.readByteBlock()==-1);
				
			leave();
		};
		
		@org.junit.Test public void testReadCharBlock_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read char block
			*/
			DUT d = new DUT(
						"<bools>1234%0020;6789A</bools><wool>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				final char [] a = new char[20];
				int r = d.readCharBlock(a,1,18);
				System.out.println(r+" "+java.util.Arrays.toString(a));
				org.junit.Assert.assertTrue(r==10);
				org.junit.Assert.assertTrue(a[0+1]=='1');
				org.junit.Assert.assertTrue(a[1+1]=='2');
				org.junit.Assert.assertTrue(a[2+1]=='3');
				org.junit.Assert.assertTrue(a[3+1]=='4');
				org.junit.Assert.assertTrue(a[4+1]==' ');
				org.junit.Assert.assertTrue(a[5+1]=='6');
				org.junit.Assert.assertTrue(a[6+1]=='7');
				org.junit.Assert.assertTrue(a[7+1]=='8');
				org.junit.Assert.assertTrue(a[8+1]=='9');
				org.junit.Assert.assertTrue(a[9+1]=='A');
				org.junit.Assert.assertTrue(d.readCharBlock(a,1,18)==0);
				org.junit.Assert.assertTrue(d.readCharBlock(a,1,18)==0);
				
				org.junit.Assert.assertTrue(d.next()==null);
				org.junit.Assert.assertTrue("wool".equals(d.next()));
			}	
			leave();
		};
		
		@org.junit.Test public void testReadCharBlock_2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can char block with appendable
			*/
			DUT d = new DUT(
						"<bools>123456789A</bools><wool>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				StringBuilder sb = new StringBuilder(); 
				int r = d.readCharBlock(sb,18);
				System.out.println(r+" "+sb);
				org.junit.Assert.assertTrue(r==10);
				org.junit.Assert.assertTrue("123456789A".equals(sb.toString()));
				
				org.junit.Assert.assertTrue(d.readCharBlock(sb,18)==0);
				org.junit.Assert.assertTrue(d.readCharBlock(sb,18)==0);
				
				org.junit.Assert.assertTrue(d.next()==null);
				org.junit.Assert.assertTrue("wool".equals(d.next()));
			}	
			leave();
		};
		@org.junit.Test public void testReadCharBlock_3()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read char block with appendable, encoded
			*/
			DUT d = new DUT(
						"<bools>123456789%20;</bools><wool>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				StringBuilder sb = new StringBuilder(); 
				int r = d.readCharBlock(sb,18);
				System.out.println(r+" "+sb);
				org.junit.Assert.assertTrue(r==10);
				org.junit.Assert.assertTrue("123456789 ".equals(sb.toString()));
				
				org.junit.Assert.assertTrue(d.readCharBlock(sb,18)==0);
				org.junit.Assert.assertTrue(d.readCharBlock(sb,18)==0);
				
				org.junit.Assert.assertTrue(d.next()==null);
				org.junit.Assert.assertTrue("wool".equals(d.next()));
			}	
			leave();
		};
		
		@org.junit.Test public void testReadShortBlock_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read short block
			*/
			DUT d = new DUT(
						"<bools>1;-999;</bools><wool>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				short [] a = new short[10];
				int r = d.readShortBlock(a,0,10);
				System.out.println(r+" "+java.util.Arrays.toString(a));
				org.junit.Assert.assertTrue(r==2);
				
				org.junit.Assert.assertTrue(a[0]==(short)1);
				org.junit.Assert.assertTrue(a[1]==(short)-999);
				
				org.junit.Assert.assertTrue(d.readShortBlock(a,0,1)==0);
				org.junit.Assert.assertTrue(d.readShortBlock(a,0,1)==0);
				
				org.junit.Assert.assertTrue(d.next()==null);
				org.junit.Assert.assertTrue("wool".equals(d.next()));
			}	
			leave();
		};
		
		@org.junit.Test public void testReadIntBlock_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read int block
			*/
			DUT d = new DUT(
						"<bools>1;-999;</bools><wool>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				int [] a = new int[10];
				int r = d.readIntBlock(a,0,10);
				System.out.println(r+" "+java.util.Arrays.toString(a));
				org.junit.Assert.assertTrue(r==2);
				
				org.junit.Assert.assertTrue(a[0]==1);
				org.junit.Assert.assertTrue(a[1]==-999);
				
				org.junit.Assert.assertTrue(d.readIntBlock(a,0,1)==0);
				org.junit.Assert.assertTrue(d.readIntBlock(a,0,1)==0);
				
				org.junit.Assert.assertTrue(d.next()==null);
				org.junit.Assert.assertTrue("wool".equals(d.next()));
			}	
			leave();
		};
		@org.junit.Test public void testReadLongBlock_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read long block
			*/
			DUT d = new DUT(
						"<bools>1;-999;</bools><wool>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				long [] a = new long[10];
				int r = d.readLongBlock(a,0,10);
				System.out.println(r+" "+java.util.Arrays.toString(a));
				org.junit.Assert.assertTrue(r==2);
				
				org.junit.Assert.assertTrue(a[0]==(long)1);
				org.junit.Assert.assertTrue(a[1]==(long)-999);
				
				org.junit.Assert.assertTrue(d.readLongBlock(a,0,1)==0);
				org.junit.Assert.assertTrue(d.readLongBlock(a,0,1)==0);
				
				org.junit.Assert.assertTrue(d.next()==null);
				org.junit.Assert.assertTrue("wool".equals(d.next()));
			}	
			leave();
		};
		@org.junit.Test public void testReadFloatBlock_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read float block
			*/
			DUT d = new DUT(
						"<bools>1.0;-999.0;</bools><wool>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				float [] a = new float[10];
				int r = d.readFloatBlock(a,0,10);
				System.out.println(r+" "+java.util.Arrays.toString(a));
				org.junit.Assert.assertTrue(r==2);
				
				org.junit.Assert.assertTrue(a[0]==(float)1);
				org.junit.Assert.assertTrue(a[1]==(float)-999);
				
				org.junit.Assert.assertTrue(d.readFloatBlock(a,0,1)==0);
				org.junit.Assert.assertTrue(d.readFloatBlock(a,0,1)==0);
				
				org.junit.Assert.assertTrue(d.next()==null);
				org.junit.Assert.assertTrue("wool".equals(d.next()));
			}	
			leave();
		};
		@org.junit.Test public void testReadDoubleBlock_1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can read double block
			*/
			DUT d = new DUT(
						"<bools>1.0;-999.0;</bools><wool>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				double [] a = new double[10];
				int r = d.readDoubleBlock(a,0,10);
				System.out.println(r+" "+java.util.Arrays.toString(a));
				org.junit.Assert.assertTrue(r==2);
				
				org.junit.Assert.assertTrue(a[0]==(double)1);
				org.junit.Assert.assertTrue(a[1]==(double)-999);
				
				org.junit.Assert.assertTrue(d.readDoubleBlock(a,0,1)==0);
				org.junit.Assert.assertTrue(d.readDoubleBlock(a,0,1)==0);
				
				org.junit.Assert.assertTrue(d.next()==null);
				org.junit.Assert.assertTrue("wool".equals(d.next()));
			}	
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
		
		@org.junit.Test public void testIntercharLimit()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can detect
				if continous block of skippable characters is too large,
				ie. because attackers likes us to hang by sending 
				<item> ....bilions of spaces... </item>
			*/
			StringBuilder sb = new StringBuilder();
			sb.append("<item>");
			for(int i=0;i<1025;i++) sb.append(' ');
			sb.append("</item>");
			DUT d = new DUT(
							sb.toString()
						);
			d.next();
			try{
				System.out.println("next() <- which should fail");
				d.next();	
				org.junit.Assert.fail("should have failed, too many data were read");
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex);};
			leave();
		};
		
		@org.junit.Test public void testReadBooleanBlock_Bad1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can detect incorrect boolean digits
			*/
			DUT d = new DUT(
						"<bools>00013</bools>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				final boolean [] a = new boolean[20];
				try{
					int r = d.readBooleanBlock(a,0,20);
					org.junit.Assert.fail();
				}catch(ECorruptedFormat ex){ System.out.println(ex);};
			};
			leave();
		};
		@org.junit.Test public void testReadByteBlock_Bad1()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can detect incorrect hex digits
			*/
			DUT d = new DUT(
						"<bools>000102 03FAfbax </bools>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				final byte [] a = new byte[20];
				try{
					int r = d.readByteBlock(a,0,20);
					org.junit.Assert.fail();
				}catch(ECorruptedFormat ex){ System.out.println(ex);};
			};
			leave();
		};
		@org.junit.Test public void testReadByteBlock_Bad2()throws IOException
		{
			enter();
			/*
				This is a test in which we check if we can detect incorrect hex number length
			*/
			DUT d = new DUT(
						"<bools>0001A</bools>"
						);
			org.junit.Assert.assertTrue("bools".equals(d.next()));
			{
				final byte [] a = new byte[20];
				try{
					int r = d.readByteBlock(a,0,20);
					org.junit.Assert.fail();
				}catch(ECorruptedFormat ex){ System.out.println(ex);};
			};
			leave();
		};
};