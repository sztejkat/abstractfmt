package sztejkat.abstractfmt.utils;
/** A {@link #IPollable} with roll-back capabilities.
*/
public class CRollbackPollable<T extends Object> implements IRollbackPollable<T>
{
				/** Back-end pollable */
				private final IPollable<T> on;
				/** A value returned from most recent {@link #next} */
				private T recent_poll;
				/** True if {@link #next} was invoked, indicates
					that we can roll back */
				private boolean can_rollback;
				/** True if {@link #rollback} was called and not consumed yet */
				private boolean rollback_active;
				
	/** Creates
	@param on iterator to use as a back-end. Can't be null
		but can return null
	*/
	public CRollbackPollable(IPollable<T> on)
	{
		assert(on!=null);
		this.on = on;
	};			
	@Override public T peek()
	{
		if (rollback_active)
				return recent_poll;
			else
				return on.peek();
	};
	@Override public T poll()
	{
		if (rollback_active)
		{
			assert(can_rollback);
			rollback_active = false;
			return recent_poll;
		}else
		{
			recent_poll = on.poll();
			can_rollback = true;
			return recent_poll;
		}
	};
	@Override public void rollback()
	{
		if (rollback_active) throw new IllegalStateException("can't rollback twice");
		if (!can_rollback) throw new IllegalStateException("can't rollback without next()");
		rollback_active = true;
	};
	
	
};