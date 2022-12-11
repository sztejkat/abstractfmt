package sztejkat.abstractfmt.utils;
import java.util.Iterator;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.Assert;
/**
	Junit tests for {@link CRollbackPollable}
*/
public final class Test_CRollbackPollable extends sztejkat.abstractfmt.test.ATest
{
	
	
	
	@Test public void testSingleRollback()
	{
		/*
				Just iterate over given data set
				and rollback in the middle.
		*/
		enter();
				CAddablePollableLinkedList<String> l = new CAddablePollableLinkedList<String>();
				l.add("a");
				l.add("b");
				l.add("c");
				l.add("d");
				
				CRollbackPollable<String> I = new CRollbackPollable<String>(l);
				//Try with double hasNext
				Assert.assertTrue("a".equals(I.poll()));
				Assert.assertTrue("b".equals(I.poll()));
				Assert.assertTrue("c".equals(I.poll()));
				I.rollback();
				Assert.assertTrue("c".equals(I.poll()));
				Assert.assertTrue("d".equals(I.poll()));
				Assert.assertTrue(I.poll()==null); 
		leave();
	};
	
	
	@Test public void testDoubleRollbackWith()
	{
		/*
				Just iterate over given data set
				and rollback(s) in the middle.
		*/
		enter();
				CAddablePollableLinkedList<String> l = new CAddablePollableLinkedList<String>();
				l.add("a");
				l.add("b");
				l.add("c");
				l.add("d");
				
				CRollbackPollable<String> I = new CRollbackPollable<String>(l);
				//Try with double hasNext
				Assert.assertTrue("a".equals(I.poll()));
				Assert.assertTrue("b".equals(I.poll()));
				Assert.assertTrue("c".equals(I.poll()));
				Assert.assertTrue("d".equals(I.peek()));
				I.rollback();
				Assert.assertTrue("c".equals(I.peek()));
				Assert.assertTrue("c".equals(I.poll()));				
				I.rollback();
				Assert.assertTrue("c".equals(I.poll()));
				Assert.assertTrue("d".equals(I.poll()));
				Assert.assertTrue(I.peek()==null);
				Assert.assertTrue(I.poll()==null); 
		leave();
	};
	
	
	@Test public void testTailingDoubleRollbackWithHasNext()
	{
		/*
				Just iterate over given data set
				and rollback(s) at the end
		*/
		enter();
				CAddablePollableLinkedList<String> l = new CAddablePollableLinkedList<String>();
				l.add("a");
				l.add("b");
				l.add("c");
				l.add("d");
				
				CRollbackPollable<String> I = new CRollbackPollable<String>(l);
				//Try with double hasNext
				Assert.assertTrue("a".equals(I.poll()));
				Assert.assertTrue("b".equals(I.poll()));
				Assert.assertTrue("c".equals(I.poll()));
				Assert.assertTrue("d".equals(I.poll()));
				I.rollback();
				Assert.assertTrue("d".equals(I.poll()));
				Assert.assertTrue(I.peek()==null); 
				I.rollback();				
				Assert.assertTrue("d".equals(I.poll()));
				Assert.assertTrue(I.poll()==null);
		leave();
	};
};