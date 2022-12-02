package sztejkat.abstractfmt;
import org.junit.Test;
import org.junit.Assert;

public class Test_CNameRegistrySupport_Read extends sztejkat.abstractfmt.test.ATest 
{
	@Test public void testEmulateOperation()throws Throwable
	{
		/*
			This test emulates stream operation in which 
			we do receive "register-name" commands in stream
			where each such command is simply associated
			with a number ranging 0...capacity-1.
			
			This is how the stream will be used.
			
		*/
		enter();
			CNameRegistrySupport_Read reg = new CNameRegistrySupport_Read(4);
			
			reg.registerBeginName("marrie", 0 );
			reg.registerBeginName("anje", 3 );
			reg.registerBeginName("rogi", 2 );
			reg.registerBeginName("sogie", 1 );
			
			Assert.assertTrue( "marrie".equals(reg.getOptimizedName(0)));
			Assert.assertTrue( "anje".equals(reg.getOptimizedName(3)));
			Assert.assertTrue( "rogi".equals(reg.getOptimizedName(2)));
			Assert.assertTrue( "sogie".equals(reg.getOptimizedName(1)));	
				
		leave();
	};
	
	@Test public void testTooMany()throws Throwable
	{
		/*
			Check if registry correctly barfs if we register too
			many names.
		*/
		enter();
			CNameRegistrySupport_Read reg = new CNameRegistrySupport_Read(4);
			
			reg.registerBeginName("marrie", 0 );
			reg.registerBeginName("anje", 3 );
			reg.registerBeginName("rogi", 2 );
			reg.registerBeginName("sogie", 1 );
			
			try{
				reg.registerBeginName("unko", 4 );
				Assert.fail("Should have thrown");
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
				
		leave();
	};
	
	@Test public void testDuplicatedIndex()throws Throwable
	{
		/*
			Check if registry correctly barfs if we register
			same index twice
		*/
		enter();
			CNameRegistrySupport_Read reg = new CNameRegistrySupport_Read(4);
			
			reg.registerBeginName("marrie", 0 );
			reg.registerBeginName("anje", 3 );
			reg.registerBeginName("rogi", 2 );
			reg.registerBeginName("sogie", 1 );
			
			try{
				reg.registerBeginName("unko", 0 );
				Assert.fail("Should have thrown");
			}catch(EBrokenFormat ex){ System.out.println(ex); };
				
		leave();
	};
	
	
};