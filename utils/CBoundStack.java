package sztejkat.abstractfmt.utils;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;

/**
	A size-bound stack intended for processing nested syntax which must
	be stacked.
*/
public class CBoundStack<TElement extends Object>
{
				/**  stack, lazy initialized */
				private final ArrayDeque<TElement> _stack;
				/** Current  stack limit, -1 if unlimited */
				private int _stack_limit = -1;
				
	/* *********************************************************************
	
		Construction
		
	
	* *********************************************************************/
	/** Creates, unbound
	@see #setStackLimit
	*/
	public CBoundStack()
	{
		this._stack = new ArrayDeque<TElement>();
	};
	
	/* *********************************************************************
	
		 stack
		
	
	* *********************************************************************/
	/** Pushes  item on  stack
	@param e non null
	@throws EFormatBoundaryExceeded if there is no more place on stack
	*/
	public void push(TElement e)throws EFormatBoundaryExceeded
	{
		int slimit = getStackLimit();
		if ((slimit!=-1)&&(slimit<=_stack.size()))
				throw new EFormatBoundaryExceeded(" stack exceeded, up to "+slimit+" elements are allowed");
		_stack.addLast(e);		
	};
	/** Peeks  element from  stack
	@return null if there is nothing on stack.
	*/
	public final TElement peek()
	{
		return _stack.peekLast();
	};
	
	/** Pops  element from  stack
	@return stack element
	@throws NoSuchElementException if stack is empty
	*/
	public final TElement pop()
	{
		return _stack.removeLast();
	};
	/** Changes limit of  stack size used by {@link #push}
	@param limit new limit, maximum size of stack before failure or -1 if not limit it 
	@throws IllegalStateException if current stack depth is larger than the limit 
	*/
	public void setStackLimit(int limit)throws IllegalStateException
	{
		assert(limit>=-1);
		int current = (_stack==null) ? 0 : _stack.size();
		if ((limit!=-1)&&(limit<current))
			throw new IllegalStateException("New limit is smaller than current stack size="+current);
		this._stack_limit = limit;
	};
	/** Returns current limit of  stack
	@return -1 if disabled */
	public final int getStackLimit()
	{
		return _stack_limit;
	};	
};


