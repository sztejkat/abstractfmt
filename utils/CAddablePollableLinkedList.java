package sztejkat.abstractfmt.utils;
import java.util.LinkedList;
/**
	Adds interface to LinkedList.
	<p>
	{@link IPollable} implementation is not safe against using other
	remove methods than {@link #poll} and won't work if null is added to the collection.
*/
public class CAddablePollableLinkedList<T extends Object> extends LinkedList<T>
									    		 implements IAddable<T>,
									    		 		    IPollable<T>
{
				private static final long serialVersionUID=1L;
				
		/** Overriden to prevent null addition */
		@Override public boolean add(T element)
		{
			assert(element!=null);
			return super.add(element);
		};
};