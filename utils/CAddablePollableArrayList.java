package sztejkat.abstractfmt.utils;
import java.util.ArrayList;
/**
	Adds interface to ArrayList.
	<p>
{@link IPollable} implementation is not safe against using other
	remove methods than {@link #poll} and won't work if null is added to the collection.
*/
public class CAddablePollableArrayList<T extends Object> extends ArrayList<T>
									    implements IAddable<T>,
									    		   IPollable<T>
{
			private static final long serialVersionUID=1L;
			/** Used to optimize poll/peek operations */
			private int poll_ptr;
			/** How far a {@link poll_ptr} can grow before acutall collection
			is modified */
			private static final int BLOCK_REMOVE_LIMIT = 100; 
			
	/** Overriden to prevent null addition */
	@Override public boolean add(T element)
	{
		assert(element!=null);
		return super.add(element);
	};
	@Override public T peek()
	{
		if (poll_ptr>=size()) return null;
		return get(poll_ptr);
	};
	@Override public T poll()
	{
		if (poll_ptr>=BLOCK_REMOVE_LIMIT)
		{
			removeRange(0,poll_ptr);
			poll_ptr=0;
		}
		if (poll_ptr>=size()) return null;
		return get(poll_ptr++);
	};
	
	/* ***********************************************************************
	
				Junit org test arena
	
	
	************************************************************************/
	public static class Test extends sztejkat.abstractfmt.test.ATest
	{
		@org.junit.Test public void testPollPeekLarge()
		{
			enter();
			
				CAddablePollableArrayList<String> l = new CAddablePollableArrayList<String>();
				
				for(int i=0;i<5*BLOCK_REMOVE_LIMIT;i++)
				{
					l.add("str"+Integer.toString(i));
				};
				
				for(int i=0;i<5*BLOCK_REMOVE_LIMIT;i++)
				{
					String expected = "str"+Integer.toString(i);
					org.junit.Assert.assertTrue(expected.equals(l.peek()));
					org.junit.Assert.assertTrue(expected.equals(l.poll()));
				};
				org.junit.Assert.assertTrue(null==l.peek());
				org.junit.Assert.assertTrue(null==l.poll());
				org.junit.Assert.assertTrue(l.size()<=BLOCK_REMOVE_LIMIT);
			
			leave();
		};
	};
};