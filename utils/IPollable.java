package sztejkat.abstractfmt.utils;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
	A missing simpleton in <i>java collection framework</i>
	which consists only of methods needed to poll elements
	from a list. 
*/
public interface IPollable<T extends Object> 
{
	/** Retrieves and removes the head of this queue, or returns null if this queue is empty.
	@return element or null if queue is empty */
	public T poll();
	/** Retrieves but not removes the head of this queue, or returns null if this queue is empty.
	@return element or null if queue is empty */
	public T peek();
	
};