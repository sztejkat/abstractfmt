package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.ISignalWriteFormat;
import sztejkat.abstractfmt.TContentType;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EClosed;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;

/**
	An elementary test which checks if we can open what was written
	and if open/close state is monitored correctly.
*/
public class TestOpenClose extends ASignalTest
{

	@Test public void justOpen()throws IOException
	{
		/*
			We do write an empty stream and try
			to read it.
		*/
		enter();
			Pair p = create();
			
			p.write.open();
			p.write.close();
			
			p.read.open();
			p.read.close();
		leave();
	};
	
	@Test public void justOpenIsEof()throws IOException
	{
		/*
			We do write an empty stream, try
			to read it and validate if next element is EOF.
		*/
		enter();
			Pair p = create();
			
			p.write.open();
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	@Test public void justOpenThrowsEof()throws IOException
	{
		/*
			We do write an empty stream, try
			to read it and validate if next element is EOF.
		*/
		enter();
			Pair p = create();
			
			p.write.open();
			p.write.close();
			
			p.read.open();
			try{
				p.read.next();
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			p.read.close();
		leave();
	};
	
	
	@Test public void allowsDualOpen()throws IOException
	{
		/*
			We check if double opening does not fail.
		*/
		enter();
			Pair p = create();
			
			p.write.open();
			p.write.open();
			p.write.close();
			
			p.read.open();
			p.read.open();
			p.read.close();
		leave();
	};
	
	
	@Test public void allowsDualClose()throws IOException
	{
		/*
			We check if double closing does not fail.
		*/
		enter();
			Pair p = create();
			
			p.write.open();
			p.write.close();
			p.write.close();
			
			p.read.open();
			p.read.close();
			p.read.close();
		leave();
	};
	
	@Test public void allowsWriteCloseBeforOpen()throws IOException
	{
		/*
			We check if can close without opening.
		*/
		enter();
			Pair p = create();
			
			p.write.close();
		leave();
	};
	
	@Test public void allowsReadCloseBeforOpen()throws IOException
	{
		/*
			We check if can close without opening.
		*/
		enter();
			enter();
			Pair p = create();
			
			p.write.open();
			p.write.close();
			
			p.read.close();
		leave();
	};
	
	@Test public void testDefaultLimits()throws IOException
	{
		/*
			We check if default limits are according to contract.
		*/
		enter();
			enter();
			Pair p = create();
			
			Assert.assertTrue(p.write.getMaxSignalNameLength()==1024);
			Assert.assertTrue(p.write.getMaxEventRecursionDepth()==0);
			
			Assert.assertTrue(p.read.getMaxSignalNameLength()==1024);
			Assert.assertTrue(p.read.getMaxEventRecursionDepth()==0);
		leave();
	};
	
	@Test public void allowsSettingsBeforeOpen()throws IOException
	{
		/*
			We check if we can access and modify settings before opening.
		*/
		enter();
			enter();
			Pair p = create();
			
			p.write.getMaxSignalNameLength();
			p.write.setMaxSignalNameLength(p.write.getMaxSupportedSignalNameLength());
			p.write.setMaxEventRecursionDepth(1);
			p.write.isDescribed();
			
			p.write.open();
			p.write.close();
			
			p.read.getMaxSignalNameLength();
			p.read.setMaxSignalNameLength(p.read.getMaxSupportedSignalNameLength());
			p.read.setMaxEventRecursionDepth(1);
			p.read.isDescribed();
			p.read.close();
		leave();
	};
	
	
};