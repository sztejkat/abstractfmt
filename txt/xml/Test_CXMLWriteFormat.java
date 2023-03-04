package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.utils.CMuxWriter;
import java.io.*;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.Assert;
import javax.xml.stream.*;
/**
	Test for {@link CXMLWriteFormat} against known data
	and known expected produced results.
*/
public class Test_CXMLWriteFormat extends sztejkat.abstractfmt.test.ATest
{
	/*
		Note: All tests in this class will produce XML file in dedicated
		folder for a manual inspection.
	*/
	/** Finds, wipes and creates a file which is to be used for 
	a temporary file for calling test method. 
	@return file found, deleted, on existing path.
	@throws IOException if failed.
	*/
	static File getTempXMLFile()throws IOException
	{
		File folder = getTempFolder(Test_CXMLWriteFormat.class);
		folder.mkdirs();
		File f = new File(folder, "content.xml");
		if (f.exists()) f.delete();
		return f;
	};
	
	/**
		Tells <code>javax.xml.stream</code> to load specified
		file and iterate over it in hope, that malformed XML
		is reported as a problematic one.
		@param f file to validate through Java standard parser.
		@throws IOException if failed at low level
		@throws AssertionError if XML reported warning or error
	*/
	static void validateXMLFile(File f)throws IOException,  AssertionError
	{
		//Prepare xml factory for readers.
		XMLInputFactory xmlf = XMLInputFactory.newFactory();
		//Prepare something what would barf at bad XML
		XMLReporter reporter = new XMLReporter()
		{
			@Override public void report(String message, String errorType, Object relatedInformation, Location location)
			{
				throw new AssertionError(errorType+"="+message+"\n"+location+"\n"+relatedInformation);
			};
		};
		//arm with it.
		xmlf.setXMLReporter(reporter);
		try{
			//Note: The creation of XMLEventReader DOES perform some I/O. This is NOT specified in contract.
			XMLEventReader reader = xmlf.createXMLEventReader(
											new FileInputStream(f),//InputStream stream,
											"UTF-8"	//String encoding)
											);
			//And now iterate over it		
			try{
					while(reader.hasNext())
					{
						reader.nextEvent();
					};
			}finally{ 
						try{ reader.close();}catch(Throwable ex){};
			};
		}catch(XMLStreamException ex)
		{
			throw new AssertionError(ex.toString(),ex);
		};
	};
	/**
		Tells <code>javax.xml.stream</code> to load specified
		file and iterate over it in hope, that malformed XML
		is reported as a problematic one.
		@param f file which pass through standard Java parser.
		@throws IOException if failed at low level
		@throws AssertionError if XML did NOT report warning or error.
	*/
	static void assertXMLFileIsBad(File f)throws IOException,  AssertionError
	{
			boolean thrown = false;
			try{
					validateXMLFile(f);					
			}catch(AssertionError ex)
			{
				thrown = true;
				System.out.println("Detected expected failure:");
				System.out.println(ex);
			};
			Assert.assertTrue(thrown);
	};
	/* ------------------------------------------------------------------------------------
		
		Tests checking how good Java built-in XML failure detection actually is
		
		Tests which did failed to detect problems are turned off by commenting out @Test
	
	------------------------------------------------------------------------------------*/
	/*@Test*/ public void testIfValidationOfXMLWorks_missing_prolog()throws Exception
	{
		/*
				Test if missing xml prolog
				
				<?xml version=\"1.0\" encoding=\"UTF-8\" ?>
				
				is detected.
		*/
		enter();
		
			File temp = getTempXMLFile();
			OutputStreamWriter o = new OutputStreamWriter(
											new FileOutputStream(temp),
											"UTF-8");
			
			o.write("<body></body>");
			o.close();
			assertXMLFileIsBad(temp);
		leave();
		/*
			Result: JDK 8 - FAILED to detect problem.
		*/
	};
	@Test public void testIfValidationOfXMLWorks_bad_prolog()throws Exception
	{
		/*
				Test if bad xml prolog
				
				<?xml version=\"1.0\" encoding=\"UTF-8\" ?>
				
				is detected.
		*/
		enter();
		
			File temp = getTempXMLFile();
			OutputStreamWriter o = new OutputStreamWriter(
											new FileOutputStream(temp),
											"UTF-8");
			o.write("<?xml version=\"1.0\" encoding=\"UTF-8\" >");
			o.write("<body></body>");
			o.close();
			assertXMLFileIsBad(temp);
		leave();
		/*
			Result: JDK 8 - did detect the problem.
		*/
	};
	@Test public void testIfValidationOfXMLWorks_missing_closing_element()throws Exception
	{
		/*
				Test if missing closing element is detected
		*/
		enter();
		
			File temp = getTempXMLFile();
			OutputStreamWriter o = new OutputStreamWriter(
											new FileOutputStream(temp),
											"UTF-8");
			o.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			o.write("<sztejkat.abstractfmt.txt.xml><body></body>");
			o.close();
			
			assertXMLFileIsBad(temp);
		leave();
		/*
			Result: JDK 8 - did detect the problem.
		*/
	};
	@Test public void testIfValidationOfXMLWorks_not_matching_closing_element()throws Exception
	{
		/*
				Test if misplaced closing element is detected
		*/
		enter();
		
			File temp = getTempXMLFile();
			OutputStreamWriter o = new OutputStreamWriter(
											new FileOutputStream(temp),
											"UTF-8");
			o.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			o.write("<sztejkat.abstractfmt.txt.xml></body>");
			o.close();
			
			assertXMLFileIsBad(temp);
		leave();
		/*
			Result: JDK 8 - did detect the problem.
		*/
	};   
	@Test public void testIfValidationOfXMLWorks_invalid_character_in_name()throws Exception
	{
		/*
				Test if incorrect element name is detected
		*/
		enter();
		
			File temp = getTempXMLFile();
			OutputStreamWriter o = new OutputStreamWriter(
											new FileOutputStream(temp),
											"UTF-8");
			o.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			o.write("<+parma></+parma>");
			o.close();
			assertXMLFileIsBad(temp);
		leave();
		/*
			Result: JDK 8 - did detect the problem.
		*/
	};
	@Test public void testIfValidationOfXMLWorks_invalid_character_in_name_2()throws Exception
	{
		/*
				Test if incorrect element name is detected
		*/
		enter();
		
			File temp = getTempXMLFile();
			OutputStreamWriter o = new OutputStreamWriter(
											new FileOutputStream(temp),
											"UTF-8");
			o.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			o.write("<sztejkat.abstractfmt.txt.xml><&amp;parma><&amp;parma></sztejkat.abstractfmt.txt.xml>");
			o.close();
			
			assertXMLFileIsBad(temp);
		leave();
		/*
			Result: JDK 8 - did detect the problem.
		*/
	};
	
	@Test public void testIfValidationOfXMLWorks_detect_invalid_surogate()throws Exception
	{
		/*
				Test if invalid surogate is detected.
				
				Notice since we pass it through OutputStreamWriter the bad surogate 
				will be REMOVED from datastream during write. This is why we do 
				use entity escapes to feed into bad data.
		*/
		enter();
		
			File temp = getTempXMLFile();
			OutputStreamWriter o = new OutputStreamWriter(
											new FileOutputStream(temp),
											"UTF-8");
			o.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");			
			o.write("<sztejkat.abstractfmt.txt.xml>&amp;&#xDC01;</sztejkat.abstractfmt.txt.xml>");	
			o.close();
			
			assertXMLFileIsBad(temp);
		leave();
		/*
			Result: JDK 8 - did detect the problem.
		*/
	};
	
	
	
	
	/* *********************************************************************
	

	
				Actual tests of XML write format.
	
	
	**********************************************************************/
	
	@Test public void testOpenClose()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//Compare against known code
			Assert.assertTrue("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>".equals(s.toString()));
		leave();
	};
	
	
	
	@Test public void testEmptyStruct()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.begin("spartan");
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//Compare against known code
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><sztejkat.abstractfmt.txt.xml>"+
			"<spartan></spartan>"+
			"</sztejkat.abstractfmt.txt.xml>").equals(s.toString()));
		leave();
	};
	
	@Test public void testEmptyNonameStruct()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.begin("");
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//Compare against known code
			// Note: We do not allow empty XML names, so, as package description
			//		 is saying, we do expect _. This is handled by name escaping
			//		 engine tough, but I did not add own test to it.
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><sztejkat.abstractfmt.txt.xml>"+
			"<_></_>"+
			"</sztejkat.abstractfmt.txt.xml>").equals(s.toString()));
		leave();
	};
	
	@Test public void testSurogateName()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			//Force 1.1 to allow surogates in names.
			CXMLWriteFormat o = new CXMLWriteFormat(mux, new CXMLChar_classifier_1_1_E2());
			o.open();
				o.begin("\uD830\uDC30<>");
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.1\" encoding=\"UTF-8\" ?><sztejkat.abstractfmt.txt.xml>"+
			"<\uD830\uDC30_003C_003E></\uD830\uDC30_003C_003E>"+
			"</sztejkat.abstractfmt.txt.xml>").equals(s.toString()));
		leave();
	};
	
	
	@Test public void testNestedStruct()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.begin("andy");
					o.begin("mandy");
					o.end();
					o.begin("dandy");
					o.end();
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><sztejkat.abstractfmt.txt.xml>"+
			"<andy><mandy></mandy><dandy></dandy></andy>"+
			"</sztejkat.abstractfmt.txt.xml>").equals(s.toString()));
		leave();
	};
	
	
	@Test public void testBoolean()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeBoolean(false);
				o.writeBoolean(true);
				o.begin("andy");
					o.writeBoolean(false);
					o.writeBoolean(true);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>false,true<andy>false,true</andy></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	@Test public void testByteShortIntLong()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeByte((byte)-7);
				o.writeShort((short)3332);
				o.writeInt(12345678);
				o.writeLong(-3949849494L);
				o.begin("andy");
					o.writeByte((byte)-7);
					o.writeShort((short)3332);
					o.writeInt(12345678);
					o.writeLong(-3949849494L);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>-7,3332,12345678,-3949849494<andy>-7,3332,12345678,-3949849494</andy></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	@Test public void testFloatDouble()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeFloat(-33.3E-3f);
				o.writeDouble(+33.3E-3);
				o.begin("andy");
					o.writeFloat(-33.3E-3f);
					o.writeDouble(+33.3E-3);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>-0.0333,0.0333<andy>-0.0333,0.0333</andy></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	
	@Test public void testCharInterlaved()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeChar('m');
				o.writeChar('o');
				o.writeChar('u');
				o.begin("andy");
					o.writeChar('s');
					o.writeChar('e');
					o.writeInt(0);
					o.writeChar('r');
				o.end();
				o.writeChar('d');
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>\"mou\"<andy>\"se\",0,\"r\"</andy>\"d\"</sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	@Test public void testCharEscaping()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeChar('m');
				o.writeChar('\"');
				o.writeChar('_');
				o.begin("andy");
					o.writeChar('\uD800');
					o.writeChar('\u0000');
					o.writeInt(0);
					o.writeChar('r');
				o.end();
				o.writeChar('d');
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>\"m&quot;__\"<andy>\"_D800_0000\",0,\"r\"</andy>\"d\"</sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	
	@Test public void testBooleanBlock()throws IOException
	{
		/*
			Note: 
				Due to the fact that blocks are handled by 
				superclass testing boolean block is enough.
				This is the only block which differs from
				superclas block.
		*/
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeBooleanBlock(new boolean[]{ true, false, false, true});
				o.begin("blk");
					o.writeBooleanBlock(new boolean[]{ true, false, false, true});
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>t,f,f,t<blk>t,f,f,t</blk></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	
	@Test public void testByteBlock()throws IOException
	{
		/*
			A bit excessive test
		*/
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeByteBlock(new byte[4]);
				o.begin("blk");
					o.writeByteBlock(new byte[2]);
					o.writeByteBlock(new byte[]{(byte)0x00,(byte)0x3C,(byte)0x11},1,2);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>00000000<blk>00003C11</blk></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	
	@Test public void testShortBlock()throws IOException
	{
		/*
			A bit excessive test
		*/
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeShortBlock(new short[4]);
				o.begin("blk");
					o.writeShortBlock(new short[2]);
					o.writeShortBlock(new short[2]);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>0,0,0,0<blk>0,0,0,0</blk></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	@Test public void testIntBlock()throws IOException
	{
		/*
			A bit excessive test
		*/
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeIntBlock(new int[4]);
				o.begin("blk");
					o.writeIntBlock(new int[2]);
					o.writeIntBlock(new int[2]);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>0,0,0,0<blk>0,0,0,0</blk></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	@Test public void testLongBlock()throws IOException
	{
		/*
			A bit excessive test
		*/
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeLongBlock(new long[4]);
				o.begin("blk");
					o.writeLongBlock(new long[2]);
					o.writeLongBlock(new long[2]);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>0,0,0,0<blk>0,0,0,0</blk></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	@Test public void testFloatBlock()throws IOException
	{
		/*
			A bit excessive test
		*/
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeFloatBlock(new float[4]);
				o.begin("blk");
					o.writeFloatBlock(new float[2]);
					o.writeFloatBlock(new float[2]);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>0.0,0.0,0.0,0.0<blk>0.0,0.0,0.0,0.0</blk></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	
	@Test public void testDoubleBlock()throws IOException
	{
		/*
			A bit excessive test
		*/
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeDoubleBlock(new double[4]);
				o.begin("blk");
					o.writeDoubleBlock(new double[2]);
					o.writeDoubleBlock(new double[2]);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>0.0,0.0,0.0,0.0<blk>0.0,0.0,0.0,0.0</blk></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	
	@Test public void testCharBlock()throws IOException
	{
		/*
			A bit excessive test
		*/
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeCharBlock(new char[4]);
				o.begin("blk");
					o.writeCharBlock(new char[]{'a','s'});
					o.writeCharBlock(new char[2]);
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>\"_0000_0000_0000_0000\"<blk>\"as_0000_0000\"</blk></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	@Test public void testStringBlock()throws IOException
	{
		/*
			A bit excessive test
		*/
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeString('a');
				o.writeString('\u0000');
				o.begin("blk");
					o.writeString("boruta");
					o.writeString("druch");
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>\"a_0000\"<blk>\"borutadruch\"</blk></sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
	
	
	@Test public void testCommentWithinStringBlock()throws IOException
	{
		enter();
			File temp = getTempXMLFile();		 //file for manual inspection
			StringWriter s = new StringWriter(); //memory buffer for fast compare
			CMuxWriter mux = new CMuxWriter(
						new Writer[]
						{
							s,
							new OutputStreamWriter(
										new FileOutputStream(temp),
										"UTF-8")
						});						//mux writing to both
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.writeString('a');
				o.writeComment("Nothing special \n but few lines <>");
				o.writeString('\u0000');
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
							   "<sztejkat.abstractfmt.txt.xml>\"a\"<!-- Nothing special \n"+
							   " but few lines &lt;&gt; -->,\"_0000\"</sztejkat.abstractfmt.txt.xml>"
			).equals(s.toString()));
		leave();
	};
}