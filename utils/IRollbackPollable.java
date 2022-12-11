package sztejkat.abstractfmt.utils;

/**
	A {@link IPollable} with a roll-back capabilities.
*/
public interface IRollbackPollable<T extends Object> extends IPollable<T>
{
	/** Makes most recent value to be returned again by {@link #poll}
	@throws IllegalStateException if invoked twice in a row without 
		consuming the rollback by {@link #poll}
	@throws IllegalStateException if can't rollback, because {@link #poll} was not called.
	*/
	public void rollback();
};