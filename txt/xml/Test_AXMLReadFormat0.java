package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.test.*;
import sztejkat.abstractfmt.*;
import java.io.*;
import org.junit.Test;
import org.junit.Assert;

/**
	
	A test bed for {@link AXMLReadFormat0} over known good and known bad examples.
*/
public class Test_AXMLReadFormat0 extends ATest
{
			/** Tested class */
			public static final class DUT extends AXMLReadFormat0
			{
					DUT(String s)
					{
						super(new StringReader(s));
					};
					@Override public int getMaxSupportedStructRecursionDepth(){ return Integer.MAX_VALUE; };
					@Override public int getMaxSupportedSignalNameLength(){ return -1; };
			};
			
			
	/* *******************************************************************************
			
		
				Prolog and body recognition
	
	
	 ********************************************************************************/
	@Test public void testCanProcessPrologAtOpen()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml ?><xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	@Test public void testCanProcessPrologAtOpenWithLineBreak()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml something to skip ?>\n"+
"<xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	@Test public void testCanProcessPrologAtOpenWithAttrib()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml system=\"<suuu?>\"?><xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	@Test public void testCanProcessPrologAtOpenWithDOCTYPE()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml something to skip ?>\n"+
"<!DOCTYPE no doctype >\n"+
"<xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	@Test public void testCanProcessPrologAtOpenWithDOCTYPENested()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml something to skip ?>\n"+
"<!DOCTYPE no doctype <!ELEMENT s> summarum >\n"+
"<xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	@Test public void testCanProcessPrologAtOpenWithCDATA()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml something to skip?>\n"+
"<![CDATA[ no doctype ]]>\n"+
"<xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	
	@Test public void testCanProcessPrologAtOpenWithCDATAcontent()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml something to skip?>\n"+
"<![CDATA[ some <  badly >>!> ]> cdata ]]>\n"+
"<xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	
	@Test public void testCanProcessPrologAtOpenWithComment()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml something to skip?>\n"+
"<!-- comment -->\n"+
"<xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	
	@Test public void testCanProcessPrologAtOpenWithAllElements()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml something to skip?>\n"+
"<!DOCTYPE no doctype <!ELEMENT s> summarum >\n"+
"<![CDATA[ some <  badly >>!> ]> cdata ]]>\n"+
"<!-- comment -->\n"+
"<? PI ?>\n"+
"<xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	
	@Test public void testDetectsMissingProlog()throws IOException
	{
		enter();
			DUT d = new DUT(
"<xml></xml>"
						);
			try{
				d.open();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	@Test public void testDetectsMissingBody()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml something to skip?>\n"
						);
			try{
				d.open();
				Assert.fail();
			}catch(EUnexpectedEof ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	@Test public void testDetectsInvalidBody()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml something to skip?>\n<body></body>"
						);
			try{
				d.open();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	@Test public void testCanProcessByteOrderMark()throws IOException
	{
		enter();
			DUT d = new DUT(
"\uFEFF<?xml something to skip ?><xml></xml>"
						);
			d.open();
			d.close();
		leave();
	};
	
	/* *******************************************************************************
			
		
				Primitive elemements within a body
				and body end detection.
	
	
	 ********************************************************************************/
};