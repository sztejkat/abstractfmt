package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
/**
	A contract for an inter-operational <u>contract</u> test case
	for reader-writer pair.
	<p>
	Interoperational tests always constis of using writer
	to write some data and then using reader to read them 
	back.
	
	<h2>Contract tests</h2>
	Contract tests are special kinds of tests designed to 
	test not a specific implementation, but <u>any</u> (configurable)
	implementation of specific contracts.
	<p>
	In this case we assume that any <code>AInterOpTestCase</code>
	will test a specific pair:
	<pre>
		class W implements IStructWriteFormat ...
		class R implements IStructReadFormat ...
	</pre>
	only using knowledge and specs declared in {@link IStructWriteFormat}
	and {@link IStructReadFormat} interfaces.
	<p>
	User may assume, that if his/her implementation do pass all
	required contract tests then said implementation is in generic correct.
	<p>
	User <u>should</u> however create tests which expose a dedicated,
	special cases which may expose vulnerabilities of his/her implementation.
	<p>
	Especially user <u>should</u> create dedicated tests with dedicated
	input data to check how his/her {@link IStructReadFormat} implementation
	deals with mallformed, bad input. This is especially important since we
	can't force a correct implementation of {@link IStructWriteFormat}
	to produce bad data.
	
	<h2>Constructing test suite</h2>
	The Junit4 test system has <code>&#64;Suite</code> runner 
	which can be easily used to bundle a bunch of test cases
	into a one, single suite of tests.
	<p>
	The only problem which exists in Junit 4 <i>suite</i> concept
	is parameterzing it. The <code>&#64;Parameterized</code> annotation
	do conflict with <code>&#64;Suite</code> runner, as both are runners and You can't have
	two of them.
	<p>
	This test suite solves that problem by declaring a 
	<pre>
		public static IInteropTestDeviceFactory factory;
	</pre>
	field which is to be initialized by <code>&#64;BeforeClass</code>
	in a suite class.
	<p>
	<i>Note: Please notice, that above procedure prevents parallel 
	run of test of different implementations inside the same JVM (or to be specific:
	in the realm of the same class loader). This is not a problem with 
	a regular Junit4 tests, where tests are run one after another but You should
	be aware of it.</i>
	
	<h3>Test case pattern</h3>
	A test case should follow specific pattern:
	<pre>
	public class ATestCaseXXX... extends AInterOpTestCase...
	{
	    &#64;Test public void testX()
	    {
	    	enter();
	    		CPair&lt;?,?&gt; p = createTestDevice(...)
	    		....
	    		p.writer.open();
	    		....
	    		p.writer.close(); 
	    		p.reader.open();
	    		....
	    		p.reader.close();
	    	leave();
	    }
	};
	</pre>
	Notice the name starting with <code>A</code> indicating that:
	<ul>
		<li>this class should not be run by <code>Ant</code> <code>junit-test</code>
		task;</li>
		<li>this class should not be instantized without special precaution. 
		In this specific case - without initializing the <code>AInterOpTestCase.factory</code>
		static field;</li>
	</ul>
	(see coding standards for the library).
	<p>
	Also notice that <code>p.writer.close()</code> and <code>p.reader.close();</code>
	are <u>intentionally not</u> enclosed in <code>try...finally</code>/<code>try(...){...}</code>
	block. This is because when a test fails we <u>do need</u> to have an access to 
	a data file which caused the problem for a manual inspection.
	
	
	<h3 id="BUNDLE">Test suite pattern</h3>
	A "test suite" is a test class responsible for selecting a set of test cases
	and apply them to chosen implementation of {@link IStructWriteFormat}
	and {@link IStructReadFormat} interfaces.
	<p>
	It should look like:
	<pre>
	&#64;org.junit.runner.RunWith(org.junit.runners.Suite.class)
	//Here You list test cases You like to apply to the implementation.
	&#64;org.junit.runners.Suite.SuiteClasses({ATestCaseXXX.class,ATestCaseXXX_2.class....})	
	public class TestXXX extends ATest
	{
		&#64;org.junit.BeforeClass public static void armImplementation()
		{
			//Here You decide what implementations will be tested
			AInterOpTestCase.factory = new ....
		};
		&#64;org.junit.AfterClass public static void disarmImplementation()
		{
			AInterOpTestCase.factory = null;
		};
	}
	</pre>
*/
public class AInterOpTestCase<R extends IStructReadFormat,
					           W extends IStructWriteFormat>
					           extends ATest
{
			/** A test factory shared for test suite. Should be initialized
			only by a test bundle. See <a href="#BUNDLE">class description</a>.
			Do not use it directly, always use {@link #getFactory} 
			*/
			public static IInteropTestDeviceFactory factory;
			
		/** By default returns {@link #factory}.
		Subclasses which like to modify/wrap or change it can override it.
		@return factory to use by {@link createTestDevice}, never null,
			should be a life-time constant.
		*/
		protected IInteropTestDeviceFactory getFactory()
		{
			assert(factory!=null):"factory not set. Running outside a test suite?";
			return factory; 
		};
		
		/** Just calls <code>getFactory().createTestDevice(temp_folder)</code>
		after ensuring that temp folder do exist.
		<p>
		Used to save on typing.
		@param <R> see {@link IInteropTestDeviceFactory#createTestDevice}
		@param <W> --//--
		@param temp_folder --//--
		@return --//--
		@throws IOException --//--
		*/
		protected final <R extends IStructReadFormat,
			    W extends IStructWriteFormat>
			    CPair<R,W> createTestDevice(File temp_folder)throws IOException
		{
				assert(temp_folder!=null);
				if (!temp_folder.exists()) temp_folder.mkdirs();
				return getFactory().createTestDevice(temp_folder);
		};
					/** Used to generate per-test folder names if we have a problem with stack */
					private static int name_generator;
		/** Calls <code>getFactory().createTestDevice(temp_folder)</code>.
		It is deducing a temp folder from current class simple name followed
		by "-temp" postfix and a sub-folder made of a caller test method name.
		<p>
		Will print on <code>System.out</code> the message indicating what file it is using.
		@param <R> see {@link IInteropTestDeviceFactory#createTestDevice}
		@param <W> --//--
		@return --//--
		@throws IOException --//--
		*/
		protected final <R extends IStructReadFormat,
			    W extends IStructWriteFormat>
			    CPair<R,W> createTestDevice()throws IOException
		{
				String base = this.getClass().getSimpleName();
				//This base do provide information about a test case, but not about
				//in what test suite it is used.	
				//The TestWatcher and Rules do not provide information about test suite either.
				//In most cases however the information will be hidden in factory because it
				//will be most probably declared in a test suite class.
				IInteropTestDeviceFactory f = getFactory();
				if (f!=null)
				{
					//Now figure out either class of factory or in which class it is an inner class?
					Class<?> p = f.getClass();
					{
						Class<?> c = p;
						do
						{
							p = c;
							c = p.getEnclosingClass(); //Note: getDeclaringClass works only for non-anonymous classes.							
						}while(c!=null);
					};
					base = p.getSimpleName()+"-temp/"+base+"/";
				}else
				{
					System.out.println("could not figure out factory class, test data of different suites may be mixed");
					base = base+"-temp/";
				};
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
					if (tail==null)
					{
						System.out.println("some stack info missing, generating random name");
						tail = Integer.toString(name_generator++);
					};
				}
				final File temp_folder = new File(base+tail);
				System.out.println("test is using temp_folder \""+temp_folder.getCanonicalPath()+"\"");
				return createTestDevice(temp_folder);
		};
};