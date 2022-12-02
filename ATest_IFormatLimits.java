package sztejkat.abstractfmt;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;

/**
	Test of a public portion of {@link ATest_IFormatLimits}
*/
public abstract class ATest_IFormatLimits<T extends IFormatLimits> extends sztejkat.abstractfmt.test.ATest 
{
	/** Creates limits to test.
	@return limit initialized to native system bounds
	*/
	protected abstract T create();
	/** Should have the same effect on limit system,
	as writing a begin signal 
	@param limits limits to manipulate 
	@throws EFormatBoundaryExceeded if limit detectes recursion limit tripp.
	*/
	protected abstract void enterStruct(T limits)throws EFormatBoundaryExceeded;
	/** Should have the same effect on limit system,
	as writing an end signal 
	@param limits limits to manipulate
	@throws EFormatBoundaryExceeded if limit detects that there is no unclosed
	struct.
	*/
	protected abstract void leaveStruct(T limits)throws EFormatBoundaryExceeded;
	
	
	@Test public void testInitialNameBounds()throws Throwable
	{
		/*
			Check if initial bounds are as per contract.
			
		*/
		enter();
			IFormatLimits dut = create();
			
			int system_max = dut.getMaxSupportedSignalNameLength();
			int current_max = dut.getMaxSignalNameLength();
			
			Assert.assertTrue(system_max>0);
			Assert.assertTrue(
					(current_max==system_max)
					||
					(current_max==1024)
					);
				
		leave();
	};
	
	
	@Test public void testInitialRecursionBounds_bound()throws Throwable
	{
		/*
			Check if initial bounds are as per contract.			
		*/
		enter();
			IFormatLimits dut = create();
			
			int system_max = dut.getMaxSupportedStructRecursionDepth();
			int current_max = dut.getMaxStructRecursionDepth();
			
			Assert.assertTrue(system_max>=-1);
			Assume.assumeTrue(system_max>=0);
			Assert.assertTrue(
					(current_max==system_max)
					);
				
		leave();
	};
	
	@Test public void testInitialRecursionBounds_unbound()throws Throwable
	{
		/*
			Check if initial bounds are as per contract.			
		*/
		enter();
			IFormatLimits dut = create();
			
			int system_max = dut.getMaxSupportedStructRecursionDepth();
			int current_max = dut.getMaxStructRecursionDepth();
			
			Assert.assertTrue(system_max>=-1);
			Assume.assumeTrue(system_max==-1);
			Assert.assertTrue(
								(current_max==-1)
							);
		leave();
	};
	
	
	@Test public void testSetSignalName()throws Throwable
	{
		/*
			Check if we can shrink signal names limit
			
		*/
		enter();
			IFormatLimits dut = create();
			
			int system_max = dut.getMaxSupportedSignalNameLength();
			
			Assert.assertTrue(system_max>0);
			
			int goal = system_max / 4;
			if (goal == 0 ) goal = 1;
			
			dut.setMaxSignalNameLength(goal);
			
			Assert.assertTrue(dut.getMaxSignalNameLength()==goal);
				
		leave();
	};
	
	
	@Test public void testBadSetSignalName()throws Throwable
	{
		/*
			Check if contract captures that we are trying
			to enlagre limit past allowed 
			
		*/
		enter();
			IFormatLimits dut = create();
			
			int system_max = dut.getMaxSupportedSignalNameLength();
			int prev = dut.getMaxSignalNameLength();
			Assert.assertTrue(system_max>0);
			
			int goal = system_max +10;
			try{
				dut.setMaxSignalNameLength(goal);
				Assert.fail();
			}catch(IllegalArgumentException ex){ System.out.println(ex); };
			
			Assert.assertTrue( dut.getMaxSignalNameLength() == prev);
				
		leave();
	};
	
	
	@Test public void testSetRecursionDepth_unbound()throws Throwable
	{
		/*
			Check if we can shrink recursion depth limit
			
		*/
		enter();
			IFormatLimits dut = create();
			
			int system_max = dut.getMaxSupportedStructRecursionDepth();
			
			Assert.assertTrue(system_max>=-1);
			Assume.assumeTrue(system_max==-1); //unbound case
			
			int goal = system_max / 4;
			if (goal == 0 ) goal = 1;
			
			dut.setMaxStructRecursionDepth(goal);
			Assert.assertTrue(dut.getMaxStructRecursionDepth()==goal);
				
		leave();
	};
	
	@Test public void testSetRecursionDepth_bound()throws Throwable
	{
		/*
			Check if we can shrink recursion depth limit
			
		*/
		enter();
			IFormatLimits dut = create();
			
			int system_max = dut.getMaxSupportedStructRecursionDepth();
			
			Assert.assertTrue(system_max>=-1);
			Assume.assumeTrue(system_max>=0); //bound case
			
			int goal = system_max / 4;
			if (goal == 0 ) goal = 1;
			
			dut.setMaxStructRecursionDepth(goal);
			Assert.assertTrue(dut.getMaxStructRecursionDepth()==goal);
				
		leave();
	};
	
	@Test public void testSetRecursionBadDepth_unbound_on_bound()throws Throwable
	{
		/*
			Check if we can't set unbound limit on bound system.
		*/
		enter();
			IFormatLimits dut = create();
			
			int system_max = dut.getMaxSupportedStructRecursionDepth();
			
			Assert.assertTrue(system_max>=-1);
			Assume.assumeTrue(system_max>=0); //bound case
			
			try{
				dut.setMaxStructRecursionDepth(-1);
				}catch(IllegalArgumentException ex){ System.out.println(ex); };
			Assert.assertTrue(dut.getMaxStructRecursionDepth()==system_max);
				
		leave();
	};
	
	@Test public void testSetRecursionBadDepth_too_high_on_bound()throws Throwable
	{
		/*
			Check if we can't set too high limit on bound system.
		*/
		enter();
			IFormatLimits dut = create();
			
			int system_max = dut.getMaxSupportedStructRecursionDepth();
			
			Assert.assertTrue(system_max>=-1);
			Assume.assumeTrue(system_max>=0); //bound case
			
			int goal = system_max+10;
			
			try{
				dut.setMaxStructRecursionDepth(goal);
				}catch(IllegalArgumentException ex){ System.out.println(ex); };
			Assert.assertTrue(dut.getMaxStructRecursionDepth()==system_max);
				
		leave();
	};
	
	
	@Test public void testIfRecursionLimitIsTripped_0()throws EFormatBoundaryExceeded
	{
		/*
				Check if plain recursion do trip limit 
		*/
		enter();
		
			T dut = create();
			
			dut.setMaxStructRecursionDepth(0); //all systems must support it.
			
			try{
					enterStruct(dut);
					Assert.fail();
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			
		leave();
	};
	
	
	@Test public void testIfRecursionLimitIsTripped_3()throws EFormatBoundaryExceeded
	{
		/*
				Check if plain recursion do trip limit 
		*/
		enter();
		
			T dut = create();
			Assume.assumeTrue(
					(dut.getMaxSupportedStructRecursionDepth()==-1)
					||
					(dut.getMaxSupportedStructRecursionDepth()>3)
					);
			dut.setMaxStructRecursionDepth(3); //all systems must support it.
			
			
					enterStruct(dut);
					enterStruct(dut);
					enterStruct(dut);
			try{		
					enterStruct(dut);
					Assert.fail();
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			
		leave();
	};
	
	
	@Test public void testIfRecursionLimitIsTripped_bouncing_3()throws EFormatBoundaryExceeded
	{
		/*
				Check if recursion do trip limit 
		*/
		enter();
		
			T dut = create();
			Assume.assumeTrue(
					(dut.getMaxSupportedStructRecursionDepth()==-1)
					||
					(dut.getMaxSupportedStructRecursionDepth()>3)
					);
			dut.setMaxStructRecursionDepth(3); //all systems must support it.
			
			
					enterStruct(dut);
					leaveStruct(dut);
					enterStruct(dut);
					leaveStruct(dut);
					enterStruct(dut);
					leaveStruct(dut);
					enterStruct(dut);
					
					enterStruct(dut);
					enterStruct(dut);
			try{		
					enterStruct(dut);
					Assert.fail();
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			
		leave();
	};
	
	@Test public void testIfRecursionLeaveLimitIsTripped_3()throws EFormatBoundaryExceeded
	{
		/*
				Check if  recursion do trip limit 
		*/
		enter();
		
			T dut = create();
			Assume.assumeTrue(
					(dut.getMaxSupportedStructRecursionDepth()==-1)
					||
					(dut.getMaxSupportedStructRecursionDepth()>3)
					);
			dut.setMaxStructRecursionDepth(3); //all systems must support it.
			
			
					enterStruct(dut);
					enterStruct(dut);
					leaveStruct(dut);
					leaveStruct(dut);
			try{		
					leaveStruct(dut);
					Assert.fail();
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			
		leave();
	};
	
	
	@Test public void testIfRecursionLeaveLimitIsTripped_bouncing_3()throws EFormatBoundaryExceeded
	{
		/*
				Check if  recursion do trip limit 
		*/
		enter();
		
			T dut = create();
			Assume.assumeTrue(
					(dut.getMaxSupportedStructRecursionDepth()==-1)
					||
					(dut.getMaxSupportedStructRecursionDepth()>3)
					);
			dut.setMaxStructRecursionDepth(3); //all systems must support it.
			
			
					enterStruct(dut);
					enterStruct(dut);
					
					enterStruct(dut);
					leaveStruct(dut);
					
					enterStruct(dut);
					leaveStruct(dut);
					
					enterStruct(dut);
					leaveStruct(dut);
					
					leaveStruct(dut);
					leaveStruct(dut);
			try{		
					leaveStruct(dut);
					Assert.fail();
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			
		leave();
	};
};