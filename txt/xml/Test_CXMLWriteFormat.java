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
		File folder = getTempFolder(Test_CXMLWriteFormat.class, null);
		folder.mkdirs();
		File f = new File(folder, "content.xml");
		if (f.exists()) f.delete();
		return f;
	};
	
	/**
		Tells <code>javax.xml.stream</code> to load specified
		file and iterate over it in hope, that malformed XML
		is reported as a problematic one.
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
				
				<?xml version=\"1.1\" encoding=\"UTF-8\" ?>
				
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
				
				<?xml version=\"1.1\" encoding=\"UTF-8\" ?>
				
				is detected.
		*/
		enter();
		
			File temp = getTempXMLFile();
			OutputStreamWriter o = new OutputStreamWriter(
											new FileOutputStream(temp),
											"UTF-8");
			o.write("<?xml version=\"1.1\" encoding=\"UTF-8\" >");
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
			o.write("<?xml version=\"1.1\" encoding=\"UTF-8\" ?>");
			o.write("<xml><body></body>");
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
			o.write("<?xml version=\"1.1\" encoding=\"UTF-8\" ?>");
			o.write("<xml></body>");
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
			o.write("<?xml version=\"1.1\" encoding=\"UTF-8\" ?>");
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
			o.write("<?xml version=\"1.1\" encoding=\"UTF-8\" ?>");
			o.write("<xml><&amp;parma><&amp;parma></xml>");
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
			o.write("<?xml version=\"1.1\" encoding=\"UTF-8\" ?>");			
			o.write("<xml>&amp;&#xDC01;</xml>");	
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
			Assert.assertTrue("<?xml version=\"1.1\" encoding=\"UTF-8\" ?><xml></xml>".equals(s.toString()));
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
			Assert.assertTrue(("<?xml version=\"1.1\" encoding=\"UTF-8\" ?><xml>"+
			"<spartan></spartan>"+
			"</xml>").equals(s.toString()));
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
			Assert.assertTrue(("<?xml version=\"1.1\" encoding=\"UTF-8\" ?><xml>"+
			"<_></_>"+
			"</xml>").equals(s.toString()));
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
			
			CXMLWriteFormat o = new CXMLWriteFormat(mux);
			o.open();
				o.begin("\uD830\uDC30<>");
				o.end();
			o.close();
			//Test if it is a valid XML
			validateXMLFile(temp);
			//None of chars is valid so all should be escaped with _
			Assert.assertTrue(("<?xml version=\"1.1\" encoding=\"UTF-8\" ?><xml>"+
			"<\uD830\uDC30_003C_003E></\uD830\uDC30_003C_003E>"+
			"</xml>").equals(s.toString()));
		leave();
	};
}