package sztejkat.abstractfmt.utils;

/**
	A missing simpleton in <i>java collection framework</i>
	which consists only if {@link #add} method.
*/
public interface IAddable<T extends Object>
{
	/** Adds specified object to the end of a collection.
	@param element what to add
	@return true if added, false if failed */
	public boolean add(T element);
};