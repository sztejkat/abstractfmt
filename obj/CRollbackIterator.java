package sztejkat.abstractfmt.obj;
import java.util.Iterator;
/** A read-only iterator with roll-back future. */
class CRollbackIterator<T extends Object> implements Iterator<T>
{
				/** Back-end iterator */
				private final Iterator<T> on;
				/** A value returned from most recent {@link #next} */
				private T recent_next;
				/** True if {@link #next} was invoked, indicates
					that we can roll back */
				private boolean can_rollback;
				/** True if {@link #rollback} was called and not consumed yet */
				private boolean rollback_active;
				
	/** Creates
	@param on iterator to use as a back-end. Can't be null
		but can return null
	*/
	CRollbackIterator(Iterator<T> on)
	{
		assert(on!=null);
		this.on = on;
	};			
	
	@Override public boolean hasNext()
	{
		if (rollback_active) return true;
		return on.hasNext();
	};
	@Override public T next()
	{
		if (rollback_active)
		{
			assert(can_rollback);
			rollback_active = false;
			return recent_next;
		}else
		{
			recent_next = on.next();
			can_rollback = true;
			return recent_next;
		}
	};
	/** Always throws */
	@Override public void remove(){ throw new UnsupportedOperationException(); };
	/** Makes most recent value to be returned again by this iterator
	@throws IllegalStateException if invoked twice in a row without 
		consuming the rollback by {@link #next}
	@throws IllegalStateException if can't rollback, because {@link #next} was not called.
	*/
	public void rollback()
	{
		if (rollback_active) throw new IllegalStateException("can't rollback twice");
		if (!can_rollback) throw new IllegalStateException("can't rollback without next()");
		rollback_active = true;
	};
	
	
};