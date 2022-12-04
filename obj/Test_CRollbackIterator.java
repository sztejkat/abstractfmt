package sztejkat.abstractfmt.obj;
import java.util.Iterator;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.Assert;
/**
	Junit tests for {@link CRollbackIterator}
*/
public final class Test_CRollbackIterator extends sztejkat.abstractfmt.test.ATest
{
	@Test public void testTransparentIteration()
	{
		/*
				Just iterate over given data set
		*/
		enter();
				ArrayList<String> l = new ArrayList<String>();
				l.add("a");
				l.add("b");
				l.add("c");
				l.add("d");
				
				CRollbackIterator<String> I = new CRollbackIterator<String>(l.iterator());
				//Try with double hasNext
				Assert.assertTrue(I.hasNext());
				Assert.assertTrue(I.hasNext());
				Assert.assertTrue("a".equals(I.next()));
				Assert.assertTrue(I.hasNext());
				//try without hasNext
				Assert.assertTrue("b".equals(I.next()));
				Assert.assertTrue("c".equals(I.next()));
				Assert.assertTrue("d".equals(I.next()));
				Assert.assertTrue(!I.hasNext()); 
		leave();
	};
	
	
	@Test public void testSingleRollback()
	{
		/*
				Just iterate over given data set
				and rollback in the middle.
		*/
		enter();
				ArrayList<String> l = new ArrayList<String>();
				l.add("a");
				l.add("b");
				l.add("c");
				l.add("d");
				
				CRollbackIterator<String> I = new CRollbackIterator<String>(l.iterator());
				//Try with double hasNext
				Assert.assertTrue("a".equals(I.next()));
				Assert.assertTrue("b".equals(I.next()));
				Assert.assertTrue("c".equals(I.next()));
				I.rollback();
				Assert.assertTrue("c".equals(I.next()));
				Assert.assertTrue("d".equals(I.next()));
				Assert.assertTrue(!I.hasNext()); 
		leave();
	};
	
	@Test public void testSingleRollbackWithHasNext()
	{
		/*
				Just iterate over given data set
				and rollback in the middle.
		*/
		enter();
				ArrayList<String> l = new ArrayList<String>();
				l.add("a");
				l.add("b");
				l.add("c");
				l.add("d");
				
				CRollbackIterator<String> I = new CRollbackIterator<String>(l.iterator());
				//Try with double hasNext
				Assert.assertTrue("a".equals(I.next()));
				Assert.assertTrue("b".equals(I.next()));
				Assert.assertTrue("c".equals(I.next()));
				I.rollback();
				Assert.assertTrue(I.hasNext());
				Assert.assertTrue("c".equals(I.next()));
				Assert.assertTrue("d".equals(I.next()));
				Assert.assertTrue(!I.hasNext()); 
		leave();
	};
	
	@Test public void testDoubleRollbackWithHasNext()
	{
		/*
				Just iterate over given data set
				and rollback(s) in the middle.
		*/
		enter();
				ArrayList<String> l = new ArrayList<String>();
				l.add("a");
				l.add("b");
				l.add("c");
				l.add("d");
				
				CRollbackIterator<String> I = new CRollbackIterator<String>(l.iterator());
				//Try with double hasNext
				Assert.assertTrue("a".equals(I.next()));
				Assert.assertTrue("b".equals(I.next()));
				Assert.assertTrue("c".equals(I.next()));
				I.rollback();
				Assert.assertTrue(I.hasNext());
				Assert.assertTrue("c".equals(I.next()));
				I.rollback();
				Assert.assertTrue(I.hasNext());
				Assert.assertTrue("c".equals(I.next()));
				Assert.assertTrue("d".equals(I.next()));
				Assert.assertTrue(!I.hasNext()); 
		leave();
	};
	
	
	@Test public void testTailingDoubleRollbackWithHasNext()
	{
		/*
				Just iterate over given data set
				and rollback(s) at the end
		*/
		enter();
				ArrayList<String> l = new ArrayList<String>();
				l.add("a");
				l.add("b");
				l.add("c");
				l.add("d");
				
				CRollbackIterator<String> I = new CRollbackIterator<String>(l.iterator());
				//Try with double hasNext
				Assert.assertTrue("a".equals(I.next()));
				Assert.assertTrue("b".equals(I.next()));
				Assert.assertTrue("c".equals(I.next()));
				Assert.assertTrue("d".equals(I.next()));
				I.rollback();
				Assert.assertTrue(I.hasNext());
				Assert.assertTrue("d".equals(I.next()));
				Assert.assertTrue(!I.hasNext()); 
				I.rollback();
				Assert.assertTrue(I.hasNext());
				Assert.assertTrue("d".equals(I.next()));
				Assert.assertTrue(!I.hasNext()); 
		leave();
	};
};