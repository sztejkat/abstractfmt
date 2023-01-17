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
		Should be invoked from a method annotated with <code>#64;Test</code>.
		
		@param test_case_class either a test case class which defines all test methods.
		@param test_case_implementation a test case implementation which may be 
				a subclass of {@link IInteropTestDeviceFactory} or null or just anything.
				If this is an inner class the declaring class will be used instead.
		@return a folder made of <code>test_case_class</code>/<code>test_case_implementation</code>/test method name.
				This folder may exist or may not exist.
		@throws IOException if failed to create folder path.
		*/
	public static File getTempFolder(Class<?> test_case_class, Object test_case_implementation)throws IOException
	{
			//We take it from "test case style"
			String base = test_case_class.getSimpleName();
			//Now we take part from test case implementation, if any.
			if (test_case_implementation!=null)
			{
				Class<?> p = test_case_implementation.getClass();
				{
					Class<?> c = p;
					do
					{
						p = c;
						c = p.getEnclosingClass(); //Note: getDeclaringClass works only for non-anonymous classes.							
					}while(c!=null);
				};
				//we have it.
					base = p.getSimpleName()+"-temp/"+base+"/";
			}else
			{
					base = base + "-temp/";
			};
			//Now look up for nearest method annotated with @Test
			String tail = null;
			{
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
								Class<?> _cc = Class.forName(_c_name);
								Method [] ms = _cc.getDeclaredMethods();
								Method identified_method = null;
								for(Method m : ms)
								{
									if (_m_name.equals(m.getName()))
									{
										identified_method = m;
										break;
									};
								};
								if (identified_method!=null)
								{
									if (identified_method.getDeclaredAnnotation(org.junit.Test.class)!=null)
									{
										tail = _m_name;
										break loop;
									};
								};

							}catch(Exception ex)
							{
									//ignore it silently,
							};
						};
					};
				};
				//check if method is found?
				if (tail==null)
				{
					//no, randomize it.
					tail = Integer.toString(name_generator++);
				};
			}
			return new File(base+tail);
	};
};