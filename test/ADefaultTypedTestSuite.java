package sztejkat.abstractfmt.test;
import org.junit.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
	A default composition of test cases to be used as
	a test suite for typed implementations.
*/
@RunWith(Suite.class)
@Suite.SuiteClasses({
			/*
				Note: Sadly Junit4 can't compose inherited SuiteClasses
					  annotation and we can't extends ADefaultUntypedTestSuite
					  
					  Remember to update ADefaultUntypedTestSuite after adding 
					  a test case here.
			*/
					ATestCase_AbuseOfReads_IntNumeric.class,
					ATestCase_BasicSignalOperations.class,
					ATestCase_OptimizedSignalOperations.class,
					ATestCase_SignalOperationsSafety.class,
					ATestCase_ComplexSignalName.class,
					ATestCase_BooleanElementaryPrimitive.class,
					ATestCase_BooleanBlockPrimitive.class,
					ATestCase_ByteElementaryPrimitive.class,
					ATestCase_ByteBlockPrimitive.class,
					ATestCase_ShortElementaryPrimitive.class,
					ATestCase_ShortBlockPrimitive.class,
					ATestCase_CharElementaryPrimitive.class,
					ATestCase_CharBlockPrimitive.class,
					ATestCase_IntElementaryPrimitive.class,
					ATestCase_IntBlockPrimitive.class,
					ATestCase_LongElementaryPrimitive.class,
					ATestCase_LongBlockPrimitive.class,
					ATestCase_FloatElementaryPrimitive.class,
					ATestCase_FloatBlockPrimitive.class,
					ATestCase_DoubleElementaryPrimitive.class,
					ATestCase_DoubleBlockPrimitive.class,
					ATestCase_StringBlockPrimitive.class,
                   //Typed test cases
					ATestCase_PeekOperations.class,
					ATestCase_TypeDefence.class
					})
public abstract class ADefaultTypedTestSuite extends ATest
{
};