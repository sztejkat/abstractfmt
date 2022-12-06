package sztejkat.abstractfmt.utils;
import java.util.*;
//Note: Orignally from sztejkat.utils.datastructs.
/**
	Iterator, which joins set of <u>iterators</u> in one. It supports remove.
*/
public class CJoinedIterator<T> implements Iterator<T>
{
		/** Iterator returning Iterator */
		private Iterator<Iterator<T>> on;
		private Iterator<T> current;
	/**
		Creates iterator which joins iterators returned by <code>on</code>.
		@param on iterator to Iterator objects.
	*/
	public CJoinedIterator(Iterator<Iterator<T>> on)
	{
		this.on = on;
		current = null;
	};
	/**
		Creates iterator which joins iterators specified in array.
		Only non empty, non null, not containing null array is allowed,
		or you will get NullPointerException's somewhere.
		@param on list of iterators to join. Can't be null, can't contain null.
	*/
	public CJoinedIterator(Iterator<T> [] on)
	{
		this.on = new CArrayIterator<Iterator<T>>(on);
		current = null;
	};
	public boolean hasNext()
	{
		if ((current==null)||(!current.hasNext()))
			{
			  if (on.hasNext())
			  	{
					current = on.next();
                                        assert(current!=null):"current==null?";
					return hasNext();
				}else
					return false;
			}else
			return true;
	};
	public T next()
	{
                if ((current==null)||(!current.hasNext()))
                {
                        if (!hasNext()) throw new NoSuchElementException();
                };
		return current.next();
	};
	public void remove()
	{
		current.remove();
	};
};
