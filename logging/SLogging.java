package sztejkat.abstractfmt.logging;

/**
   An extremally simplfified logging utility which is used
   as a narrow entry point for logging for <b>debuging purposes</b>
   across the entire <code>sztejkat.abstractfmt.*</code>
   library.
   <p>
   See notes in sources about how to control it.
   <p>
   Use pattern:
   <pre>
   class X
   {
         private static final long TLEVEL = SLogging.getDebugLevelForClass(X.class);
         private static final boolean TRACE = (TLEVEL!=0)
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("X.",X.class) : null;
         ....

         public void x()
         {
            if (TRACE) TOUT.println("x() ENTER");
            ...
         };
   };
   </pre>
*/
public class SLogging
{
 //Note: This is a very, very simplified rip-off
 //      of my generic sztejkat.utils.logging.SLogging
 //      tool which is far more complex and usefull.
 //
 //      I decided to provide such a minimalistic support
 //      because each company is using own logging facility and
 //      loves it to be consistent across all libraries.
 //
 //      The standards (java.util.logging and log4j) are
 //      defined, but are two distinct one. The slfj is a third
 //      bridging well known standard but again it is not somethig what
 //      is built in into a JDK.
 //
 //      The performance of util.logging and log4j sucks ass.
 //      Please take a look on how they check if logging is enabled and how they
 //      generate messages just to drop them if logging is disabled.
 //      Not that they do it wrong - their purpose is not providing
 //      a trace for debugging with a minimum time and code overhead,
 //      but to log how a production system works. And they do
 //      their job well. They are simply too fat for a purpose I need
 //      in here.
 //
 //      The use pattern with private static final fields
 //      sets-up logging __per class__ which needs to be debuged
 //      very early, that is during class loading. This means
 //      that once hot-spot decides to compile the
 //
 //           if (TRACE) TOUT...
 //
 //      it knows well if TRACE is true or false and can
 //      simply optimize out the whole logging if not needed.
 //
 //      This is exactly the same mechanism assert() is using.
 //
 //      So to control logging or redirect it to Your company
 //      logging system simply fork the sztejkat.abstractfmt
 //      repository and patch those two methods below
 //      to Your liking.
 //
 //
		 private SLogging(){};//prevent creation

         /** Returns debug level for class
         @param _class for which class
         @return zero - no debugging, otherwise some level
         */
         public static long getDebugLevelForClass(Class<?> _class)
         {
              //Note: Full blown implementation is using command line,
              //      system properties and etc to deduce if debuging
              //      for a class is enabled or not.
              //
              //      This implementation is oversimplfied and just
              //      returns a constant. This means, that enabling
              //      logs requires re-compilation of this class.
              return 1;
         };


         /** Returns debug level for class
         @param prefix optional prefix to prepend to messages printed by <code>println(...)</code>
         @param _class for which class
         @return zero - no debugging, otherwise some level
         */
         public static java.io.PrintStream createDebugOutputForClass(final String prefix, Class<?> _class)
         {
              //Note: Full blown implementation is using command line,
              //      system properties and etc to deduce where to dump
              //      debug messages (files, streams etc) and what additional
              //      information append to (thread,source,time)
              //
              //      This implementation is oversimplfied and just
              //      returns System.out optionally wrapped in such a way
              //      that it's println() do pre-pend a prefix to each printed message.
              //
                return prefix==null
                       ?
                       System.out
                       :
                       new java.io.PrintStream(System.out,true)
			{
				public void println(String str)
				{
					super.println(prefix+str);
				};
			};
         }
};