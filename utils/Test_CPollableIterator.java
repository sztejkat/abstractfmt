package sztejkat.abstractfmt.utils;
import java.util.Iterator;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.Assert;
/**
	Junit tests for {@link CPollableIterator}
*/
public final class Test_CPollableIterator extends sztejkat.abstractfmt.test.ATest
{
	@Test public void testContinousPolling()
	{
		/*
				Just poll all data
		*/
		enter();
			
			CPollableIterator<String> p = new CPollableIterator<String> (
											new CArrayIterator<String>(
													new String[]
													{
														"A","B","C","D","E"
													}),false);
			
			Assert.assertTrue("A".equals(p.poll()));
			Assert.assertTrue("B".equals(p.poll()));
			Assert.assertTrue("C".equals(p.poll()));
			Assert.assertTrue("D".equals(p.poll()));
			Assert.assertTrue("E".equals(p.poll()));
			Assert.assertTrue(null==p.poll());
			Assert.assertTrue(null==p.poll());
			Assert.assertTrue(null==p.poll());
		leave();
	};
	
	
	@Test public void testContinousPeekPolling()
	{
		/*
				Just peek-poll all data
		*/
		enter();
			
			CPollableIterator<String> p = new CPollableIterator<String> (
											new CArrayIterator<String>(
													new String[]
													{
														"A","B","C","D","E"
													}),false);
			
			Assert.assertTrue("A".equals(p.peek()));
			Assert.assertTrue("A".equals(p.poll()));
			Assert.assertTrue("B".equals(p.peek()));
			Assert.assertTrue("B".equals(p.poll()));
			Assert.assertTrue("C".equals(p.peek()));
			Assert.assertTrue("C".equals(p.poll()));
			Assert.assertTrue("D".equals(p.peek()));
			Assert.assertTrue("D".equals(p.poll()));
			Assert.assertTrue("E".equals(p.peek()));
			Assert.assertTrue("E".equals(p.peek()));
			Assert.assertTrue("E".equals(p.poll()));
			Assert.assertTrue(null==p.poll());
			Assert.assertTrue(null==p.peek());
			Assert.assertTrue(null==p.poll());
		leave();
	};
	
	
	
	@Test public void testContinousPollingRemove()
	{
		/*
				Just poll all data, check if removed
		*/
		enter();
			ArrayList<String> A = new ArrayList<String>();
			A.add("A");
			A.add("B");
			A.add("C");
			A.add("D");
			A.add("E");
			CPollableIterator<String> p = new CPollableIterator<String> (A.iterator(),true);
			
			Assert.assertTrue("A".equals(p.poll()));Assert.assertTrue(A.size()==4);
			Assert.assertTrue("B".equals(p.poll()));Assert.assertTrue(A.size()==3);
			Assert.assertTrue("C".equals(p.poll()));Assert.assertTrue(A.size()==2);
			Assert.assertTrue("D".equals(p.poll()));Assert.assertTrue(A.size()==1);
			Assert.assertTrue("E".equals(p.poll()));Assert.assertTrue(A.size()==0);
			Assert.assertTrue(null==p.poll());
			Assert.assertTrue(null==p.poll());
			Assert.assertTrue(null==p.poll());
		leave();
	};
	@Test public void testContinousPeekPollingRemove()
	{
		/*
				Just peek-poll all data, check if removed
		*/
		enter();
			ArrayList<String> A = new ArrayList<String>();
			A.add("A");
			A.add("B");
			A.add("C");
			A.add("D");
			A.add("E");
			CPollableIterator<String> p = new CPollableIterator<String> (A.iterator(),true);
			
			Assert.assertTrue("A".equals(p.peek()));Assert.assertTrue(A.size()==5);
			Assert.assertTrue("A".equals(p.poll()));Assert.assertTrue(A.size()==4);
			Assert.assertTrue("B".equals(p.peek()));Assert.assertTrue(A.size()==4);
			Assert.assertTrue("B".equals(p.poll()));Assert.assertTrue(A.size()==3);
			Assert.assertTrue("C".equals(p.peek()));
			Assert.assertTrue("C".equals(p.poll()));Assert.assertTrue(A.size()==2);
			Assert.assertTrue("D".equals(p.peek()));
			Assert.assertTrue("D".equals(p.poll()));Assert.assertTrue(A.size()==1);
			Assert.assertTrue("E".equals(p.peek()));Assert.assertTrue(A.size()==1);
			Assert.assertTrue("E".equals(p.peek()));Assert.assertTrue(A.size()==1);
			Assert.assertTrue("E".equals(p.poll()));Assert.assertTrue(A.size()==0);
			Assert.assertTrue(null==p.poll());
			Assert.assertTrue(null==p.poll());
			Assert.assertTrue(null==p.poll());
		leave();
	};
};