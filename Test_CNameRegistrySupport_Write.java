package sztejkat.abstractfmt;
import org.junit.Test;
import org.junit.Assert;

public class Test_CNameRegistrySupport_Write extends sztejkat.abstractfmt.test.ATest 
{
	@Test public void testMassRegistration()throws Throwable
	{
		/*
			This test emulates stream operation in which 
			we do register all names for optimized operation
			but only first set of them is really optimized.
			
		*/
		enter();
			CNameRegistrySupport_Write reg = new CNameRegistrySupport_Write(4);
			
			Assert.assertTrue(reg.optimizeBeginName("marrie"));
			Assert.assertTrue(reg.optimizeBeginName("rogger"));
			Assert.assertTrue(reg.optimizeBeginName("angie"));
			Assert.assertTrue(reg.optimizeBeginName("sollo"));
			Assert.assertTrue(!reg.optimizeBeginName("sogie"));	
				
		leave();
	};
	
	@Test public void testDuplicateRegistration()throws Throwable
	{
		/*
			This test emulates stream operation in which 
			we do register all names, using duplicated requests,
			for optimized operation
			but only first set of them is really optimized.
			
		*/
		enter();
			CNameRegistrySupport_Write reg = new CNameRegistrySupport_Write(4);
			
			Assert.assertTrue(reg.optimizeBeginName("marrie"));
			Assert.assertTrue(reg.optimizeBeginName("marrie"));
			Assert.assertTrue(reg.optimizeBeginName("marrie"));
			Assert.assertTrue(reg.optimizeBeginName("rogger"));
			Assert.assertTrue(reg.optimizeBeginName("angie"));
			Assert.assertTrue(reg.optimizeBeginName("sollo"));
			Assert.assertTrue(!reg.optimizeBeginName("sogie"));	
				
		leave();
	};
	
	@Test public void testStatus()throws Throwable
	{
		/*
			In this test we register some name and check if
			it is registered and what index is assigned to it.			
		*/
		enter();
			CNameRegistrySupport_Write reg = new CNameRegistrySupport_Write(4);
			CNameRegistrySupport_Write.Name n = null;
			
			Assert.assertTrue(reg.optimizeBeginName("marrie"));
			Assert.assertTrue(reg.optimizeBeginName("donata"));
			
			n=reg.getOptmizedName("marrie");
			Assert.assertTrue(n!=null);
			Assert.assertTrue(n.getIndex()==0);
			
			n=reg.getOptmizedName("donata");
			Assert.assertTrue(n!=null);
			Assert.assertTrue(n.getIndex()==1);
			
			n=reg.getOptmizedName("cularga");
			Assert.assertTrue(n==null);					
				
		leave();
	};
	
	@Test public void testStreamingRegistration()throws Throwable
	{
		/*
			This test emulates a standard 
			"named begin" write which
			must do like:
			
				begin(x)
				{
				   if is optimized
					if needs stream registration
							write begin-register
					else
							write begin-with-index
				   else
				     write begin-with-name
								
		*/
		enter();
			CNameRegistrySupport_Write reg = new CNameRegistrySupport_Write(4);
			CNameRegistrySupport_Write.Name n = null;
			
			Assert.assertTrue(reg.optimizeBeginName("marrie"));
			
			n=reg.getOptmizedName("marrie");
			Assert.assertTrue(n!=null);
			Assert.assertTrue(n.getIndex()==0);
			Assert.assertTrue(n.needsStreamRegistartion());
			//as begin-register is a single shot operation.
			Assert.assertTrue(!n.needsStreamRegistartion());
				
		leave();
	};
	
	
	@Test public void testIndexVsOrder()throws Throwable
	{
		/*
			Tests index and order assignments
		*/
		enter();
			CNameRegistrySupport_Write reg = new CNameRegistrySupport_Write(4);
			CNameRegistrySupport_Write.Name n = null;
			
			Assert.assertTrue(reg.optimizeBeginName("marrie"));
			Assert.assertTrue(reg.optimizeBeginName("carrie"));
			
			n=reg.getOptmizedName("carrie");
			Assert.assertTrue(n!=null);
			Assert.assertTrue(n.getIndex()==1);
			Assert.assertTrue(n.needsStreamRegistartion());
			Assert.assertTrue(n.getOrder()==0);
			
			n=reg.getOptmizedName("carrie");
			Assert.assertTrue(n!=null);
			Assert.assertTrue(n.getIndex()==1);
			Assert.assertTrue(!n.needsStreamRegistartion());
			Assert.assertTrue(n.getOrder()==0);
			
			n=reg.getOptmizedName("marrie");
			Assert.assertTrue(n!=null);
			Assert.assertTrue(n.getIndex()==0);
			Assert.assertTrue(n.needsStreamRegistartion());
			Assert.assertTrue(n.getOrder()==1);
			
			n=reg.getOptmizedName("marrie");
			Assert.assertTrue(n!=null);
			Assert.assertTrue(n.getIndex()==0);
			Assert.assertTrue(!n.needsStreamRegistartion());
			Assert.assertTrue(n.getOrder()==1);
				
		leave();
	};
	
	
};