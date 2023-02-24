package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.test.*;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.SStringUtils;
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
					@Override public int getMaxSupportedSignalNameLength(){ return Integer.MAX_VALUE; };
					@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
			};
			
			
	/* *******************************************************************************
			
		
				Prolog and body recognition
	
	
	 ********************************************************************************/
	@Test public void testCanProcessPrologAtOpen()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml ?><sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
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
"<sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			d.close();
		leave();
	};
	@Test public void testCanProcessPrologAtOpenWithAttrib()throws IOException
	{
		enter();
			DUT d = new DUT(
"<?xml system=\"<suuu?>\"?><sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
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
"<sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
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
"<sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
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
"<sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
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
"<sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
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
"<sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
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
"<sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			d.close();
		leave();
	};
	
	@Test public void testDetectsMissingProlog()throws IOException
	{
		enter();
			DUT d = new DUT(
"<sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
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
"\uFEFF<?xml something to skip ?><sztejkat.abstractfmt.txt.xml></sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			d.close();
		leave();
	};
	
	/* *******************************************************************************
			
		
				Primitive elemements within a body
				and body end detection.
	
	
	 ********************************************************************************/
	 @Test public void canProcessSingleInteger()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml something to skip ?><sztejkat.abstractfmt.txt.xml>\n"+
			"1345\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue(d.readInt()==1345);
			d.close();
		leave();
	};
	 @Test public void canProcessMoreIntegers()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml something to skip ?><sztejkat.abstractfmt.txt.xml>\n"+
			"1345,9485,  40404    ,11\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue(d.readInt()==1345);
			Assert.assertTrue(d.readInt()==9485);
			Assert.assertTrue(d.readInt()==40404);
			Assert.assertTrue(d.readInt()==11);
			d.close();
		leave();
	};
	
	 @Test public void canProcessMoreIntegersWithComments()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"1345, <!-- cmt --> 9485 <!-- cmt --> ,  40404    ,11\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue(d.readInt()==1345);
			Assert.assertTrue(d.readInt()==9485);
			Assert.assertTrue(d.readInt()==40404);
			Assert.assertTrue(d.readInt()==11);
			d.close();
		leave();
	};
	
	@Test public void detectsNoMoreData()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml something to skip ?><sztejkat.abstractfmt.txt.xml>\n"+
			"1345,9485\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue(d.hasElementaryData());
			Assert.assertTrue(d.readInt()==1345);
			Assert.assertTrue(d.hasElementaryData());
			Assert.assertTrue(d.readInt()==9485);
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void canReadBoolean()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml something to skip ?><sztejkat.abstractfmt.txt.xml>\n"+
			"true,t,f,false\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue(d.readBoolean()==true);
			Assert.assertTrue(d.readBoolean()==true);
			Assert.assertTrue(d.readBoolean()==false);
			Assert.assertTrue(d.readBoolean()==false);
			d.close();
		leave();
	};
	@Test public void canProcessMoreIntegersWithEmptyTokens()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml something to skip ?><sztejkat.abstractfmt.txt.xml>\n"+
			"1345,,    ,11\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue(d.readInt()==1345);
			Assert.assertTrue(d.readInt()==0);
			Assert.assertTrue(d.readInt()==0);
			Assert.assertTrue(d.readInt()==11);
			d.close();
		leave();
	};
	@Test public void canProcessUnquotedChar()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml something to skip ?><sztejkat.abstractfmt.txt.xml>\n"+
			"abba,,    ,rabin\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue(d.readChar()=='a');
			Assert.assertTrue(d.readChar()=='b');
			Assert.assertTrue(d.readChar()=='b');
			Assert.assertTrue(d.readChar()=='a');
			Assert.assertTrue(d.readChar()==0);
			Assert.assertTrue(d.readChar()==0);
			Assert.assertTrue(d.readChar()=='r');
			Assert.assertTrue(d.readChar()=='a');
			Assert.assertTrue(d.readChar()=='b');
			Assert.assertTrue(d.readChar()=='i');
			Assert.assertTrue(d.readChar()=='n');
			d.close();
		leave();
	};
	@Test public void canProcessQuotedChar()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"\"abba\"\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue(d.readChar()=='a');
			Assert.assertTrue(d.readChar()=='b');
			Assert.assertTrue(d.readChar()=='b');
			Assert.assertTrue(d.readChar()=='a');
			Assert.assertTrue(!d.hasElementaryData());//to pull the closing "
			d.close();
		leave();
	};
	
	@Test public void canProcessQuotedEscapedChar()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"\"_0000&lt;&amp;&gt;&quot;&apos;_0001&#32;&#x44;z__x\"\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue(d.readChar()==0);
			Assert.assertTrue(d.readChar()=='<');
			Assert.assertTrue(d.readChar()=='&');
			Assert.assertTrue(d.readChar()=='>');
			Assert.assertTrue(d.readChar()=='\"');
			Assert.assertTrue(d.readChar()=='\'');
			Assert.assertTrue(d.readChar()==1);
			Assert.assertTrue(d.readChar()==32);
			Assert.assertTrue(d.readChar()==0x44);
			Assert.assertTrue(d.readChar()=='z');
			Assert.assertTrue(d.readChar()=='_');
			Assert.assertTrue(d.readChar()=='x');
			d.close();
		leave();
	};
	
	@Test public void canProcessAmpHexEscapeSurogated()throws IOException
	{
		enter();
		final int code = 0x10001;
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"\"&#x"+Integer.toHexString(code)+";\"\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			char u = d.readChar();
			System.out.println("u="+Integer.toHexString(u));
			System.out.println("us="+Integer.toHexString(Character.highSurrogate(code)));
			char L = d.readChar();
			System.out.println("L="+Integer.toHexString(L));			
			System.out.println("Ls="+Integer.toHexString(Character.lowSurrogate(code)));
			
			Assert.assertTrue(u==Character.highSurrogate(code));
			Assert.assertTrue( L==Character.lowSurrogate(code));
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void canProcessAmpDecEscapeSurogated()throws IOException
	{
		enter();
		final int code = 0x10401;
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"\"&#"+Integer.toString(code)+";\"\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			char u = d.readChar();
			System.out.println("u="+Integer.toHexString(u));
			System.out.println("us="+Integer.toHexString(Character.highSurrogate(code)));
			char L = d.readChar();
			System.out.println("L="+Integer.toHexString(L));			
			System.out.println("Ls="+Integer.toHexString(Character.lowSurrogate(code)));
			
			Assert.assertTrue(u==Character.highSurrogate(code));
			Assert.assertTrue( L==Character.lowSurrogate(code));
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void canProcessAmpHexEscapeBadSurogate()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"\"&#xD801;\"\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			char c = d.readChar();
			System.out.println(Integer.toHexString(c));
			Assert.assertTrue(c==0xD801);
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	/* ********************************************************************
	
	
			Signals
			
	
	
	**********************************************************************/
	@Test public void canProcessPlainStruct()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco></marco>\n"+
			"<pollo></pollo>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			String n = d.next();
			System.out.println("d.next()="+n);
			Assert.assertTrue("marco".equals(n));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("pollo".equals(d.next()));
			Assert.assertTrue(null==d.next());
			d.close();
		leave();
	};
	
	@Test public void canProcessPlainStructDetectsBadClosingTag()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco></cmarco>\n"+
			"<polo></polo>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			Assert.assertTrue("marco".equals(d.next()));
			try{
				Assert.assertTrue(null==d.next());
				Assert.fail();
			}catch(EBrokenFormat ex)
			{
				System.out.println(ex);
			};
			d.close();
		leave();
	};
	
	@Test public void canProcessPlainStructWithAnonymousClosingTag()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco></>\n"+
			"<pollo></>\n"+
			"</>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("pollo".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void canProcessPlainStructWithPI()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<? cmt \n ?><marco></>\n"+
			"<?-- cmt \n ?><pollo><? cmt \n ?></>\n"+
			"</>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("pollo".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	
	@Test public void canProcessPlainStructCDATA()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<![CDATA[ something bad >  can happen > ]]><marco></>\n"+
			"<![CDATA[ something bad >  can happen > ]]><pollo><![CDATA[ something bad >  can happen > ]]></>\n"+
			"</>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("pollo".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void canProcessPlainStructWithComments()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<!-- cmt \n --><marco></>\n"+
			"<!-- cmt \n --><pollo><!-- cmt \n --></>\n"+
			"</>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("pollo".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void canProcessPlainStructWithEmptyTag()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<_></_>\n"+	//XML legal form
			"<></>\n"+  //XML illegal form
			"</>"
						);
			d.open();
			Assert.assertTrue("".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	@Test public void canProcessPlainStructWithEscapedTags()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<__x_0040></__x_0040>\n"+	//XML legal form
			"<a_3445_444A></a_3445_444A>\n"+  //XML illegal form
			"</>"
						);
			d.open();
			Assert.assertTrue("_x\u0040".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("a\u3445\u444A".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());
			d.close();
		leave();
	};
	
	
	
	@Test public void canProcessNestedStructs()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco>345,345,500<polo>\n"+
			"</polo>11,31</marco>\n"+
			"134"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			String n = d.next();
			System.out.println("d.next()="+n);
			Assert.assertTrue("marco".equals(n));
			Assert.assertTrue(345==d.readInt());			
			Assert.assertTrue("polo".equals(d.next()));
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(134==d.readInt());
			d.close();
		leave();
	};
	
	/* *******************************************************************
	
			Sequences?
			
				Well... just in case, because it is up to superclass
				to process as long, as we correctly process tokens.
	
	
	*********************************************************************/
	@Test public void canProcessCharString()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
"<marco> \"Jason\" ,\" gone home\"</>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			String n = d.readString(1000);
			System.out.println(n);
			Assert.assertTrue("Jason gone home".equals(n));
			Assert.assertTrue(null==d.next());
			d.close();
		leave();
	};
	
	@Test public void canProcessCharStringWithComment()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
"<marco> \"Jason\" , <!-- cmt --> \" gone home\"</>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			String n = d.readString(1000);
			System.out.println(n);
			Assert.assertTrue("Jason gone home".equals(n));
			Assert.assertTrue(null==d.next());
			d.close();
		leave();
	};
	
	
	
	@Test public void canProcessCharUnqutedStringWithAmpEscapes()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
"<marco> Jason , &#32;gone&#32;home</>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			String n = d.readString(1000);
			System.out.println(n);
			Assert.assertTrue("Jason gone home".equals(n));
			Assert.assertTrue(null==d.next());
			d.close();
		leave();
	};
	
	
	
	
	
	
	/* *******************************************************************
	
			Defensive structures 
			
				Again, we defend only against
	
	
	*********************************************************************/
	@Test public void detectsBadAmpDecimalEscape()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco> &#4a;</>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			try{
				String n = d.readString(1000);
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	@Test public void detectsVeryLongAmpDecimalEscape()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco> &#000304040343313414;</>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			try{
				String n = d.readString(1000);
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	@Test public void detectsOutOfRangeDecimalEscape()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco> &#9114111;</>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			try{
				String n = d.readString(1000);
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	
	
	
	@Test public void detectsBadAmpHexEscape()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco> &#x4Z;</>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			try{
				String n = d.readString(1000);
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	@Test public void detectsVeryLongAmpHexEscape()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco> &#x3A594821;</>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			try{
				String n = d.readString(1000);
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	@Test public void detectsOutOfRangeHexEscape()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<marco> &#x110000;</>\n"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.open();
			
			Assert.assertTrue("marco".equals(d.next()));
			try{
				String n = d.readString(1000);
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	
	@Test public void readingPastClosingElementFails()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"</sztejkat.abstractfmt.txt.xml>"+
			"103,345"
						);
			d.open();
			Assert.assertTrue(!d.hasElementaryData());
			try{
				d.readInt();
				Assert.fail();
			}catch(EUnexpectedEof ex){ System.out.println(ex); };
			d.close();
		leave();
	};
	
	@Test public void nameBoundaryWorks()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"<fucking__long__name__of__my__beloved__xml__file__format>"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.setMaxSignalNameLength(32);
			d.open();
			try{
				d.next();
				Assert.fail();
			}catch(EFormatBoundaryExceeded ex)
			{
				System.out.println(ex);
				//We also need to ensure that this happend inside a handler
				//before collecting the entire name.
				//We can't test it directly so a bit wobbly test in here:
				Assert.assertTrue(ex.getMessage().equals(
							"Signal name \"fucking_long_name_of_my_beloved_\" too long"
							));
				//This test may fail if exception message is changed,
				//so please notice that the name in exception message
				//is SHORTER than the name in XML above.
			};
			d.close();
		leave();
	};
	
	
	@Test public void tooManyClosingTags()throws IOException
	{
		enter();
			DUT d = new DUT(
			"<?xml ?><sztejkat.abstractfmt.txt.xml>\n"+
			"</f>"+
			"</sztejkat.abstractfmt.txt.xml>"
						);
			d.setMaxSignalNameLength(32);
			d.open();
			try{
				d.next();
				Assert.fail();
			}catch(EBrokenFormat ex)
			{
				System.out.println(ex);
			};
			d.close();
		leave();
	};
};      