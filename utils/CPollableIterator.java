package sztejkat.abstractfmt.utils;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
	A {@link IPollable} over an iterator. 
*/
public class CPollableIterator<T extends Object> implements IPollable<T> 
{
				private final Iterator<T> on;
				private T what_next;
				/** True indicates that <code>on.next()</code>
				was used to pick up {@link #what_next} */
				private boolean what_next_valid;
				/** True if remove from iterator on <code>poll()</code> */
				private final boolean remove;
				
	/** Creates
	@param on non null iterator to base this pollable on
	@param remove true if remove from iterator on <code>poll()</code>,
		false to leave data in an iterator.
	*/
	public CPollableIterator(Iterator<T> on, boolean remove)
	{
		assert(on!=null);
		this.on = on;
		this.remove = remove;
	};
	@Override public T poll()
	{
		if (what_next_valid)
		{
			what_next_valid =false;
			if (remove) on.remove();	//pending remove to consume on.next()
			return what_next;
		}else
		{
			if (on.hasNext())
			{
				T x = on.next();
				if (remove) on.remove();	//remove immediately.
				return x;
			}else
			{
				return null;
			}
		}
	}
	@Override public T peek()
	{
		if (what_next_valid)
		{
				return what_next;
		}
		else
		{
			if (on.hasNext())
			{
				what_next_valid = true;
				what_next = on.next();
				return what_next;
			}else
				return null;
		}
	};
	
};