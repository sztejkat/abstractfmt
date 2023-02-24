package sztejkat.abstractfmt.txt.xml;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EBrokenFormat;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;
import org.junit.Assert;
/**
	Test for {@link AXMLUnescapingEngine}.
*/
public class Test_AXMLUnescapingEngine extends sztejkat.abstractfmt.test.ATest
{
			static final class DUT extends AXMLUnescapingEngine
			{
					CAdaptivePushBackReader i;
				DUT(String content)
				{
					 i = new CAdaptivePushBackReader(new StringReader(content));
				};
				void reset(String content)
				{
					i = new CAdaptivePushBackReader(new StringReader(content));
					super.reset();
				};
				@Override protected int readImpl()throws IOException{ return i.read(); };
				@Override protected void unread(char c)throws IOException{ i.unread(c); };
				private static final IXMLCharClassifier c = new CXMLChar_classifier_1_0_E4();
				@Override protected IXMLCharClassifier getClassifier(){ return c; };
			};
		
	@Test public void readEmpty()throws IOException
	{
		enter();
			DUT d = new DUT("");
			
			Assert.assertTrue(d.read()==-1);
			
		leave();
	};
	@Test public void readNoEscapes()throws IOException
	{
		enter();
			DUT d = new DUT("par");
			
			Assert.assertTrue(d.read()=='p');
			Assert.assertTrue(d.read()=='a');
			Assert.assertTrue(d.read()=='r');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void void_escapes()throws IOException
	{
		enter();
			DUT d = new DUT("_ _ 3");
			
			Assert.assertTrue(d.read()==' ');
			Assert.assertTrue(d.read()==' ');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	
	@Test public void underscore()throws IOException
	{
		enter();
			DUT d = new DUT("____int");
			
			Assert.assertTrue(d.read()=='_');
			Assert.assertTrue(d.read()=='_');
			Assert.assertTrue(d.read()=='i');
			Assert.assertTrue(d.read()=='n');
			Assert.assertTrue(d.read()=='t');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	
	@Test public void underscore_hex()throws IOException
	{
		enter();
			DUT d = new DUT("0_3456_3344");
			
			Assert.assertTrue(d.read()=='0');
			Assert.assertTrue(d.read()=='\u3456');
			Assert.assertTrue(d.read()=='\u3344');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	
	@Test public void amp_gt_lt()throws IOException
	{
		enter();
			DUT d = new DUT("&gt;3&lt;1");
			
			Assert.assertTrue(d.read()=='>');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='<');
			Assert.assertTrue(d.read()=='1');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void amp_quotes()throws IOException
	{
		enter();
			DUT d = new DUT("&apos;3&quot;1");
			
			Assert.assertTrue(d.read()=='\'');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='\"');
			Assert.assertTrue(d.read()=='1');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void amp_amp()throws IOException
	{
		enter();
			DUT d = new DUT("&amp;&amp;1");
			
			Assert.assertTrue(d.read()=='&');
			Assert.assertTrue(d.read()=='&');
			Assert.assertTrue(d.read()=='1');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	
	@Test public void amp_decimal()throws IOException
	{
		enter();
			DUT d = new DUT("&#345678;&#1;&#0;&#21;");
			
			char U= Character.highSurrogate(345678);
			char L= Character.lowSurrogate(345678);
			
			Assert.assertTrue(d.read()==U);
			Assert.assertTrue(d.read()==L);
			Assert.assertTrue(d.read()==1);
			Assert.assertTrue(d.read()==0);
			Assert.assertTrue(d.read()==21);
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void amp_hex()throws IOException
	{
		enter();
			DUT d = new DUT("&#x3FFFF;&#x1;&#x000;&#x21;");
			
			char U= Character.highSurrogate(0x3FFFF);
			char L= Character.lowSurrogate(0x3FFFF);
			
			Assert.assertTrue(d.read()==U);
			Assert.assertTrue(d.read()==L);
			Assert.assertTrue(d.read()==0x1);
			Assert.assertTrue(d.read()==0);
			Assert.assertTrue(d.read()==0x21);
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	
	@Test public void amp_void()throws IOException
	{
		enter();
			DUT d = new DUT("&;3&;");
			
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	
	@Test public void amp_emptyzero()throws IOException
	{
		enter();
			DUT d = new DUT("&#;3&#;");
			
			Assert.assertTrue(d.read()==0);
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==0);
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	
	
	@Test public void too_long_symbolic_amp_and_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("&nosymbolmaybethatlong;3&#;");
			
			try{
				d.read();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	
	@Test public void too_long_decimal_amp_and_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("&#3456878985789;3&#;");
			
			try{
				d.read();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void too_long_hex_amp_and_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("&#x10FFFFE;3&#;");
			
			try{
				d.read();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void out_of_range_amp_hex_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("&#x11FFFF;3&#;");
			
			try{
				d.read();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void out_of_range_amp_dec_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("&#"+Integer.toString(0x10FFFF+1)+";3&#;");
			
			try{
				d.read();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void bad_digit_amp_hex_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("&#xabgh;3&#;");
			
			try{
				d.read();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void bad_digit_amp_dec_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("&#abgh;3&#;");
			
			try{
				d.read();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void bad_digit_underscode_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("_459Y1");
			
			try{
				d.read();
				Assert.fail();
			}catch(EBrokenFormat ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void tooshort_underscode_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("_51");
			
			try{
				d.read();
				Assert.fail();
			}catch(EUnexpectedEof ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
	@Test public void tooshort_amp_recovery()throws IOException
	{
		enter();
			DUT d = new DUT("&51");
			
			try{
				d.read();
				Assert.fail();
			}catch(EUnexpectedEof ex){ System.out.println(ex); };
			d.reset("33");
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()=='3');
			Assert.assertTrue(d.read()==-1);
		leave();
	};
};
	