package sztejkat.abstractfmt.test;
import org.junit.*;

/**
	A base for junit tests.
*/
public abstract class ATest
{
	//Note: This is a rip-off from my own test system used in my other works.
	//	    I decided to copy it here instead of using the dedicated package
	//	    to avoid unnecessary dependencies.
	
	/** Returns stack trace, up to specified depth
	@param depth depth of a trace, required.
	@return stack trace */
	public static String getTrace(int depth)
	{
		Throwable t = new Throwable();
		t.fillInStackTrace();
		StackTraceElement [] trace = t.getStackTrace();
		StringBuilder sb = new StringBuilder();
		for(int i=1;(i<trace.length)&&(i<depth); i++)
		{
			sb.append(trace[i].getClassName()+"."+trace[i].getMethodName()+":"+trace[i].getLineNumber()+"<-");
		};
		return sb.toString();
	};
	/** Prints enter test message */
	protected void enter()
	{
		enter(2);
	};
	/** Prints enter test message
	@param offset index of method on call stack to print, 1 for a caller of this method.
	*/
	protected void enter(int offset)
	{
		enter(null,offset+1); 
	}
	/** Prints test routine enter message with test method name to System.out
	@param _eprefix additional text to append in front of it. */
	protected void enter(String _eprefix)
	{
		enter(_eprefix,2);
	};
	/** Prints test routine enter message with test method name to System.out.
	A core method called by all "enter" methods.
	@param offset index of method on call stack to print, 1 for a caller of this method.
	@param _eprefix optional additional prefix.
	*/
	protected void enter(String _eprefix, int offset)
	{ 
		Throwable t = new Throwable();
		t.fillInStackTrace();
		System.out.println("\n\n\n"+(
				_eprefix==null ? ""
				:
				(_eprefix+" "))+t.getStackTrace()[offset].getMethodName()+":"+t.getStackTrace()[offset].getLineNumber()+" ENTER"); 
	};
	
	/** Prints test routine leave message with test method name to System.out.

	*/
	protected void leave()
	{
		leave(null,2);
	};
	/** Prints, using additional previx 
	@param _lprefix optional additional prefix.
	*/
	protected void leave(String _lprefix)
	{
		leave(_lprefix,2);
	};
	/** Prints test routine leave message with test method name to System.out. using additional previx.
	A core method called by all "enter" methods.
	@param offset index of method on call stack to print, 1 for a caller of this method.
	@param _lprefix optional additional prefix.
	*/	
	protected void leave(String _lprefix, int offset)
	{
		Throwable t = new Throwable();
		t.fillInStackTrace();
		System.out.println("\n"+
			(_lprefix==null ? "" : (_lprefix+" "))
			+t.getStackTrace()[offset].getMethodName()+" LEAVE"); 
	}; 
	
};