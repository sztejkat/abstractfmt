package sztejkat.abstractfmt.test;
import org.junit.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
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
	
					/** Used to generate per-test folder names if we have a problem with stack */
					private static int name_generator;
					
	/**
		Computes temporary test folder for specified test case, currently run test.
		Should be invoked from a method annotated with <code>#64;Test</code> which will
		be indentified and used to produce the test name.
		<p>
		The file name will be:
		<pre>
			<i>test_suite_class</i>.getSimpleName()+"-temp/"+<i>test_method_declaring_class.getSimpleName()+"/"+<i>test_method_name</i>
		</pre>
		
		@param test_suite_provider either a class, null or object.
				<ul>
					<li>if it is a class, its outmost enclosing class is
					used as <code>test_suite_class</code>;
					<li>if it is an object its class is used as above;</li>
					<li>if it is null <code>ATest.class</code> is used;</li>
				</ul>
		@return a folder. This folder may exist or may not exist.
		@throws IOException if failed to create folder path.
		*/
	public static File getTempFolder(Object test_suite_provider)throws IOException
	{
		if (test_suite_provider==null)
				return getTempFolder(ATest.class);
			else
		if (test_suite_provider instanceof Class)
				return getTempFolder(extractEnclosingClass((Class<?>)test_suite_provider));
			else
				return getTempFolder(extractEnclosingClass(test_suite_provider.getClass()));
	};
	private static Class<?> extractEnclosingClass(Class<?> e)
	{
		Class<?> enclosing;
		for(;;)
		{	
			enclosing = e.getEnclosingClass();
			if (enclosing==null) return e;
			e = enclosing;
		}
	};
	/**
		Computes temporary test folder for specified test case, currently run test.
		Should be invoked from a method annotated with <code>#64;Test</code> which will
		be indentified and used to produce the test name.
		<p>
		The file name will be:
		<pre>
			test_suite.getSimpleName()+"-temp/"+<i>test_method_declaring_class.getSimpleName()+"/"+<i>test_method_name</i>
		</pre>
		
		@param test_suite a class which defines a test suite for which create the 
				temporary file.
		@return a folder. This folder may exist or may not exist.
		@throws IOException if failed to create folder path.
		*/
	public static File getTempFolder(Class<?> test_suite)throws IOException
	{
			//Form a base name
			String base = test_suite.getSimpleName()+"-temp/";
			//Identify test method.
			
			Method test_method = null;
			{
				//Now look up for nearest method annotated with @Test
				StackTraceElement [] stack = Thread.currentThread().getStackTrace();
				if (stack!=null)
				{
					//look for first annotated with org.junit.Test
					loop:
					for(StackTraceElement e : stack)
					{
						if (!e.isNativeMethod())
						{
							String _c_name = e.getClassName();
							String _m_name = e.getMethodName();
							try{
								Class<?> _cc = Class.forName(_c_name); //class which declares it
								Method [] ms = _cc.getDeclaredMethods(); //look up by name for declared method
								Method identified_method = null;	
								for(Method m : ms)
								{
									if (_m_name.equals(m.getName()))
									{
										//Check if annotated?
										if (m.getDeclaredAnnotation(org.junit.Test.class)!=null)
										{
											test_method= m;
											break loop;
										}
									};
								};
							}catch(Exception ex){ }; //ignore all problems.
						};
					};
				};
			};
			//check if method is found?
			if (test_method==null)
			{
				//no, randomize it.
				base = base +"unidentified_test_"+Integer.toString(name_generator++);
			}else
			{
				//test method is known.
				base = base + test_method.getDeclaringClass().getSimpleName()+"/"+test_method.getName();
			};
			return new File(base);
	};
	
	
};