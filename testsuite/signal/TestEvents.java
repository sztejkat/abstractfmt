package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.ISignalWriteFormat;
import sztejkat.abstractfmt.TContentType;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;

/**
	Tests how format is handling begin/end signals
	in more complex scenarios.
*/
public class TestEvents extends ATestShortOps
{

	@Test public void testSequentialEmptyNames()throws IOException
	{
		/*
			In this test we write many signals, one after another
			and check if they can be read-back.
			
			We use many empty name signals since they will
			frequently used as grouping signals 
		*/
		enter();
			Pair p = create();
			p.write.open();
			
			for(int i=0;i<1000;i++)
			{
				p.write.begin("");
				p.write.end();
			};
			p.write.close();
			
			p.read.open();
			for(int i=0;i<1000;i++)
			{			
				assertNext(p.read,"");
				assertNext(p.read,null);
			};				
			p.read.close();
		leave();
	};
	
	
	@Test public void testSequentialSignals()throws IOException
	{
		/*
			In this test we write many signals, one after another
			and check if they can be read-back.
			
			We use many signals to possibly stress signals name
			registry.
		*/
		enter();
			Pair p = create();
			p.write.open();
			
			for(int i=0;i<1000;i++)
			{
				p.write.begin("signal_"+i);
				p.write.end();
			};
			p.write.close();
			
			p.read.open();
			for(int i=0;i<1000;i++)
			{			
				assertNext(p.read,"signal_"+i);
				assertNext(p.read,null);
			};				
			p.read.close();
		leave();
	};
	
	@Test public void testSequentialSignalsNopt()throws IOException
	{
		/*
			In this test we write many signals, one after another
			and check if they can be read-back.
			
			We tell stream to NOT use name registry.
			
			Note:
				This is outside the scope of this test, as it is a
				contract test, to actually check if names registry
				is used or not. You should either provide a dedictated
				implementation tests or inspect produced files manually
				and check if this is done correctly.
		*/
		enter();
			Pair p = create();
			p.write.open();
			
			for(int i=0;i<1000;i++)
			{
				p.write.begin("signal_"+i, true);
				p.write.end();
			};
			p.write.close();
			
			p.read.open();
			for(int i=0;i<1000;i++)
			{			
				assertNext(p.read,"signal_"+i);
				assertNext(p.read,null);
			};				
			p.read.close();
		leave();
	};
	
	
	@Test public void testNestedSignals()throws IOException
	{
		/*
			In this test we write many signals, one inside another
			and check if they can be read-back.
		*/
		enter();
			Pair p = create();
			p.write.open();
			
			for(int i=0;i<1000;i++)
			{
				p.write.begin("signal_"+i);
			};
			for(int i=0;i<1000;i++)
			{
				p.write.end();
			};
			p.write.close();
			
			p.read.open();
			for(int i=0;i<1000;i++)
			{			
				assertNext(p.read,"signal_"+i);
			}
			for(int i=0;i<1000;i++)
			{
				assertNext(p.read,null);
			};				
			p.read.close();
		leave();
	};
	
	
	@Test public void testSequentialSignalsWithData()throws IOException
	{
		/*
			In this test we write many signals, one after another
			and check if they can be read-back.
			
			This time we dump some data inside just to make 
			sure something needs to be skipped.
		*/
		enter();
			Pair p = create();
			p.write.open();
			
			for(int i=0;i<1000;i++)
			{
				p.write.begin("signal_"+i);
				p.write.writeCharBlock("missisipi");
				p.write.end();
			};
			p.write.close();
			
			p.read.open();
			for(int i=0;i<1000;i++)
			{			
				assertNext(p.read,"signal_"+i);
				assertNext(p.read,null);
			};				
			p.read.close();
		leave();
	};
	
	@Test public void testSequentialSignalsWithDataAtZeroLevel()throws IOException
	{
		/*
			In this test we write many signals, one after another
			and check if they can be read-back.
			
			This time we dump some data inside just to make 
			sure something needs to be skipped.
		*/
		enter();
			Pair p = create();
			p.write.open();
			
			for(int i=0;i<1000;i++)
			{
				p.write.begin("signal_"+i);
				p.write.writeCharBlock("missisipi");
				p.write.end();
				p.write.writeInt(3349);
			};
			p.write.close();
			
			p.read.open();
			for(int i=0;i<1000;i++)
			{			
				assertNext(p.read,"signal_"+i);
				assertNext(p.read,null);
			};				
			p.read.close();
		leave();
	};
	
	
	@Test public void testNestedSignalsWithData()throws IOException
	{
		/*
			In this test we write many signals, one inside another
			and check if they can be read-back.
		*/
		enter();
			Pair p = create();
			p.write.open();
			
			for(int i=0;i<1000;i++)
			{
				p.write.begin("signal_"+i);
				p.write.writeLong(0x0490340894L);
			};
			for(int i=0;i<1000;i++)
			{
				p.write.writeLong(0x0490340894L);
				p.write.end();
				p.write.writeLong(0x0490340894L);
			};
			p.write.close();
			
			p.read.open();
			for(int i=0;i<1000;i++)
			{			
				assertNext(p.read,"signal_"+i);
			}
			for(int i=0;i<1000;i++)
			{
				assertNext(p.read,null);
			};				
			p.read.close();
		leave();
	};
	
	
	
	
	private String mkName(int len)
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<len;i++)
		{
			sb.append((char)(33+i));
		};
		return sb.toString();
	};
	@Test public void testWriteNameLengthDefense()throws IOException
	{
		/*
			Check if writing end will prevent correctly
			writing too long names.
		*/
		enter();
			Pair p = create();
			int m = Math.min(16,p.write.getMaxSupportedSignalNameLength());
			p.write.setMaxSignalNameLength(m);
			Assert.assertTrue(p.write.getMaxSignalNameLength()==m);
			p.write.open();
			p.write.begin(mkName(m));
			try{
				p.write.begin(mkName(m+1));
				Assert.fail();
			}catch(IllegalArgumentException ex){ System.out.println(ex);};
			p.write.close();
		leave();
	};
	@Test public void testReadNameLengthDefense()throws IOException
	{
		/*
			Check if reading end will prevent correctly
			receiving too long names.
		*/
		enter();
			Pair p = create();
			int m = Math.min(16,p.read.getMaxSupportedSignalNameLength());
			p.read.setMaxSignalNameLength(m);
			Assert.assertTrue(p.read.getMaxSignalNameLength()==m);
			p.write.open();
			p.write.begin(mkName(m));
			p.write.begin(mkName(m+1));
			p.write.close();
			
			p.read.open();
			p.read.next();
			try{
				p.read.next();	
				Assert.fail();
			}catch(EFormatBoundaryExceeded ex){ System.out.println(ex);};
			
		leave();
	};
	
	
	
	
	@Test public void testWriteRecursionDepthDefense()throws IOException
	{
		/*
			Check if writing end will prevent correctly
			writing too many recursions.
		*/
		enter();
			Pair p = create();
			final int R = 4; 
			p.write.setMaxEventRecursionDepth(R);
			Assert.assertTrue(p.write.getMaxEventRecursionDepth()==R);
			p.write.open();
			p.write.begin("1");			
			p.write.begin("2");
			p.write.begin("3");
			p.write.begin("4");
			try{
					p.write.begin("5");
					Assert.fail();
				}catch(IllegalStateException ex){ System.out.println(ex); };
			p.write.close();
		leave();
	};
	
	
	@Test public void testWriteRecursionDepthDefense2()throws IOException
	{
		/*
			Check if writing end will prevent correctly
			writing too many recursions.
		*/
		enter();
			Pair p = create();
			final int R = 4; 
			p.write.setMaxEventRecursionDepth(R);
			Assert.assertTrue(p.write.getMaxEventRecursionDepth()==R);
			p.write.open();
			p.write.begin("1");			
			p.write.begin("2");
			p.write.begin("3");
			p.write.begin("4");
			p.write.end();
			p.write.begin("4");
			try{
					p.write.begin("5");
					Assert.fail();
				}catch(IllegalStateException ex){ System.out.println(ex); };
			p.write.close();
		leave();
	};
	
	
	@Test public void testWriteRecursionDepthDefenseWithEnds()throws IOException
	{
		/*
			Check if writing end will prevent correctly
			writing too many recursions.
		*/
		enter();
			Pair p = create();
			final int R = 4; 
			p.write.setMaxEventRecursionDepth(R);
			Assert.assertTrue(p.write.getMaxEventRecursionDepth()==R);
			p.write.open();
			p.write.begin("0");
			p.write.end();
			p.write.begin("1");
			p.write.begin("2");
			p.write.begin("3");
			p.write.begin("4");
			try{
					p.write.begin("5");
					Assert.fail();
				}catch(IllegalStateException ex){ System.out.println(ex); };
			p.write.close();
		leave();
	};
	
	
	
	
	@Test public void testReadRecursionDepthDefense()throws IOException
	{
		/*
			Check if reading end will prevent correctly
			writing too many recursions.
		*/
		enter();
			Pair p = create();
			final int R = 4; 
			p.read.setMaxEventRecursionDepth(R);
			Assert.assertTrue(p.read.getMaxEventRecursionDepth()==R);
			p.write.open();
			p.write.begin("1");			
			p.write.begin("2");
			p.write.begin("3");
			p.write.begin("4");
			p.write.begin("5");
			p.write.end();
			p.write.end();
			p.write.end();
			p.write.end();
			p.write.end();
			p.write.close();
			
			p.read.open();
			assertNext(p.read, "1");
			assertNext(p.read, "2");
			assertNext(p.read, "3");
			assertNext(p.read, "4");
			try{
					assertNext(p.read, "5");
					Assert.fail();
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	@Test public void testReadRecursionDepthAllow()throws IOException
	{
		/*
			Check if reading end will correctly allow
			exactly limit of many recursions.
		*/
		enter();
			Pair p = create();
			final int R = 4; 
			p.read.setMaxEventRecursionDepth(R);
			Assert.assertTrue(p.read.getMaxEventRecursionDepth()==R);
			p.write.open();
			p.write.begin("1");			
			p.write.begin("2");
			p.write.begin("3");
			p.write.begin("4");
			p.write.end();
			p.write.end();
			p.write.end();
			p.write.end();
			p.write.close();
			
			p.read.open();
			assertNext(p.read, "1");
			assertNext(p.read, "2");
			assertNext(p.read, "3");
			assertNext(p.read, "4");
			assertNext(p.read, null);
			assertNext(p.read, null);
			assertNext(p.read, null);
			assertNext(p.read, null);
			p.read.close();
		leave();
	};
	
	
	
	@Test public void testReadRecursionDepthDefenseWithEnds()throws IOException
	{
		/*
			Check if reading end will prevent correctly
			writing too many recursions.
		*/
		enter();
			Pair p = create();
			final int R = 4; 
			p.read.setMaxEventRecursionDepth(R);
			Assert.assertTrue(p.read.getMaxEventRecursionDepth()==R);
			p.write.open();
			p.write.begin("0");
			p.write.end();
			p.write.begin("1");			
			p.write.begin("2");
			p.write.begin("3");
			p.write.begin("4");
			p.write.begin("5");
			p.write.end();
			p.write.end();
			p.write.end();
			p.write.end();
			p.write.end();
			p.write.close();
			
			p.read.open();
			assertNext(p.read, "0");
			assertNext(p.read, null);
			assertNext(p.read, "1");
			assertNext(p.read, "2");
			assertNext(p.read, "3");
			assertNext(p.read, "4");
			try{
					assertNext(p.read, "5");
					Assert.fail();
				}catch(EFormatBoundaryExceeded ex){ System.out.println(ex); };
			p.read.close();
		leave();
	};
};