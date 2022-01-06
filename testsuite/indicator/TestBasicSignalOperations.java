package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.testsuite.*;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
import java.io.IOException;
/**
	A test which check if signal related operations do work
	as expected in basic cases.
*/
public class TestBasicSignalOperations extends AIndicatorTest
{
	private void testSingleDirectBegin(String name)throws IOException
	{
		/* Test if direct begin works */
		enter("testSingleDirectBegin(name="+name+")");
		Pair p = create();
		
		Assume.assumeTrue(p.write.getMaxSupportedSignalNameLength()>=name.length());
		
		p.write.open();
			
			p.write.writeBeginDirect(name);
			p.write.writeEnd();
		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue(name.equals(p.read.getSignalName()));
		
		assertReadIndicator(p.read,TIndicator.END);
		assertReadIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	@Test public void testSingleDirectBeginEmpty()throws IOException
	{
		enter();
		testSingleDirectBegin("");
		leave();
	};
	@Test public void testSingleDirectBeginPlain()throws IOException
	{
		enter();
		testSingleDirectBegin("anice");
		leave();
	};
	@Test public void testSingleDirectBeginStrange1()throws IOException
	{
		enter();
		testSingleDirectBegin(new String(new char[]{'_',(char)0,(char)0x3490,(char)0xffff,(char)0x001,(char)0x011,(char)0x111}));
		leave();
	};
	@Test public void testSingleDirectBeginStrange2()throws IOException
	{
		enter();
		testSingleDirectBegin(new String("&^%!@#$<>"));
		leave();
	};
	@Test public void testSingleDirectBeginStrange3()throws IOException
	{
		enter();
		testSingleDirectBegin(new String("byte"));
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void testSingleRegisterBegin(String name)throws IOException
	{
		/* Test if registered begin works */
		enter("testSingleRegisterBegin(name="+name+")");
		Pair p = create();
		
		Assume.assumeTrue(p.write.getMaxSupportedSignalNameLength()>=name.length());
		Assume.assumeTrue(p.write.getMaxRegistrations()>=1);
		p.write.open();
			
			p.write.writeBeginRegister(name,0);
			p.write.writeEnd();
		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_REGISTER);
		Assert.assertTrue(name.equals(p.read.getSignalName()));
		Assert.assertTrue(0==p.read.getSignalNumber());
		
		assertReadIndicator(p.read,TIndicator.END);
		assertReadIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	@Test public void testSingleRegisterBegin1()throws IOException
	{
		enter();
		testSingleRegisterBegin("");
		leave();
	};
	@Test public void testSingleRegisterBegin2()throws IOException
	{
		enter();
		testSingleRegisterBegin(new String(new char[]{'_',(char)0,(char)0x3490,(char)0xffff,(char)0x001,(char)0x011,(char)0x111}));
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	private void testBeginRegisterUse(String name)throws IOException
	{
		/* Test if registered begin works and if registration can be used later*/
		enter("testBeginRegisterUse(name="+name+")");
		Pair p = create();
		
		Assume.assumeTrue(p.write.getMaxSupportedSignalNameLength()>=name.length());
		Assume.assumeTrue(p.write.getMaxRegistrations()>=2);
		p.write.open();
			
			p.write.writeBeginRegister(name,0);
			p.write.writeEnd();
			p.write.writeBeginRegister(name+"A",1);
			p.write.writeEnd();
			
			p.write.writeBeginUse(1);
			p.write.writeEnd();
		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_REGISTER);
		Assert.assertTrue(name.equals(p.read.getSignalName()));
		Assert.assertTrue(0==p.read.getSignalNumber());		
		assertReadIndicator(p.read,TIndicator.END);
		
		assertReadIndicator(p.read,TIndicator.BEGIN_REGISTER);
		Assert.assertTrue((name+"A").equals(p.read.getSignalName()));
		Assert.assertTrue(1==p.read.getSignalNumber());
		assertReadIndicator(p.read,TIndicator.END);
		
		
		assertReadIndicator(p.read,TIndicator.BEGIN_USE);
		Assert.assertTrue(1==p.read.getSignalNumber());		
		assertReadIndicator(p.read,TIndicator.END);
		
		
		assertReadIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	@Test public void testBeginRegisterUse1()throws IOException
	{
		enter();
		testSingleRegisterBegin("");
		leave();
	};
	@Test public void testBeginRegisterUse2()throws IOException
	{
		enter();
		testSingleRegisterBegin("Amalgamatic");
		leave();
	};
	
	
	
	
	
	
	
	
	
	private void testDirectEndBegin(String name)throws IOException
	{
		/* Test if direct end-begin works */
		enter("testDirectEndBegin(name="+name+")");
		Pair p = create();
		
		Assume.assumeTrue(p.write.getMaxSupportedSignalNameLength()>=name.length());
		
		p.write.open();
			
			p.write.writeBeginDirect(name);
			p.write.writeEndBeginDirect("A"+name);
			p.write.writeEnd();
		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue(name.equals(p.read.getSignalName()));
		//Now we may have some streams which do not in fact
		//store the mixed end-begin indicator. This test
		//is not testing them.
		TIndicator i = p.read.readIndicator();
		if (i!=TIndicator.END_BEGIN_DIRECT)
		{
			Assert.assertTrue(i==TIndicator.END);
			Assume.assumeTrue(false);
		};
				
		Assert.assertTrue(("A"+name).equals(p.read.getSignalName()));		
		assertReadIndicator(p.read,TIndicator.END);
		assertReadIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	@Test public void testDirectEndBegin1()throws IOException
	{
		enter();
		testSingleDirectBegin("");
		leave();
	};
	@Test public void testDirectEndBegin2()throws IOException
	{
		enter();
		testSingleDirectBegin("&lt;");
		leave();
	};
	
	
	
	
	
	
	
	
	
	private void testEndBeginRegister(String name)throws IOException
	{
		/* Test if direct end-begin-register works */
		enter("testEndBeginRegister(name="+name+")");
		Pair p = create();
		
		Assume.assumeTrue(p.write.getMaxSupportedSignalNameLength()>=name.length());
		Assume.assumeTrue(p.write.getMaxRegistrations()>=1);
		
		p.write.open();
			
			p.write.writeBeginDirect(name);
			p.write.writeEndBeginRegister("A"+name,0);
			p.write.writeEnd();
		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue(name.equals(p.read.getSignalName()));
		//Now we may have some streams which do not in fact
		//store the mixed end-begin indicator. This test
		//is not testing them.
		TIndicator i = p.read.readIndicator();
		if (i!=TIndicator.END_BEGIN_REGISTER)
		{
			Assert.assertTrue(i==TIndicator.END);
			Assume.assumeTrue(false);
		};
				
		Assert.assertTrue(("A"+name).equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.getSignalNumber()==0);
		assertReadIndicator(p.read,TIndicator.END);
		assertReadIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	@Test public void testEndBeginRegister1()throws IOException
	{
		enter();
		testSingleDirectBegin("");
		leave();
	};
	@Test public void testEndBeginRegister2()throws IOException
	{
		enter();
		testSingleDirectBegin("ą⁻]©");
		leave();
	};
	
	
	
	
	private void testEndBeginUse(String name)throws IOException
	{
		/* Test if direct end-begin-use works */
		enter("testEndBeginUse(name="+name+")");
		Pair p = create();
		
		Assume.assumeTrue(p.write.getMaxSupportedSignalNameLength()>=name.length());
		Assume.assumeTrue(p.write.getMaxRegistrations()>=1);
		
		p.write.open();
			
			p.write.writeBeginRegister(name,0);
			p.write.writeEndBeginUse(0);
			p.write.writeEnd();
		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_REGISTER);
		Assert.assertTrue(name.equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.getSignalNumber()==0);
		//Now we may have some streams which do not in fact
		//store the mixed end-begin indicator. This test
		//is not testing them.
		TIndicator i = p.read.readIndicator();
		if (i!=TIndicator.END_BEGIN_USE)
		{
			Assert.assertTrue(i==TIndicator.END);
			Assume.assumeTrue(false);
		};
				
		Assert.assertTrue(p.read.getSignalNumber()==0);
		assertReadIndicator(p.read,TIndicator.END);
		assertReadIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	@Test public void testEndBeginUse1()throws IOException
	{
		enter();
		testSingleDirectBegin("");
		leave();
	};
	@Test public void testEndBeginUse2()throws IOException
	{
		enter();
		testSingleDirectBegin("półśżącz");
		leave();
	};
	
	
	@Test public void testMaxSignalNameLength()throws IOException
	{
		/*
			Check if read format correctly defends against
			too long signal names.
		*/
		enter();
		Pair p = create();
		int max = Math.min(p.write.getMaxSupportedSignalNameLength(),16);
		Assert.assertTrue(p.write.getMaxSupportedSignalNameLength()==
						  p.read.getMaxSupportedSignalNameLength());
		p.read.setMaxSignalNameLength(max);
		Assert.assertTrue(p.read.getMaxSignalNameLength()==max);
		
		p.write.open();
			p.write.writeBeginDirect("0123456789abcdef");
			p.write.writeEnd();
			p.write.writeBeginDirect("0123456789abcdefG");
			p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("0123456789abcdef".equals(p.read.getSignalName()));
		assertReadIndicator(p.read,TIndicator.END);
		try{
			assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
			Assert.fail();
		}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
		p.read.close();
		
		leave();
	};
};