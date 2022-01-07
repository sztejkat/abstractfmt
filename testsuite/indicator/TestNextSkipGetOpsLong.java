package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.testsuite.*;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
import java.io.IOException;
import java.util.Random;
/**
	A test which check if skip, next, read and getIndicator
	are stable operations when interacting long operations
*/
public class TestNextSkipGetOpsLong extends ATestLongOps
{
	@Test public void testNextInitialData_ud()throws IOException
	{
		/* 
			We check if we can do next() to skip intial 
			primitives. 
			
			This test is for un-described format
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());
		p.write.open();			
		//We write some number of primitives.
		p.write.writeType(TIndicator.TYPE_LONG);
			p.write.writeLong(0x4444_0000_0000_AAAAL);
		p.write.writeFlush(TIndicator.FLUSH_LONG);
		p.write.writeType(TIndicator.TYPE_LONG);
			p.write.writeLong(0x7788_3344_0000_AAAAL);
		p.write.writeFlush(TIndicator.FLUSH_LONG);
		p.write.writeBeginDirect("A");
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.DATA);
		assertGetIndicator(p.read,TIndicator.DATA);
		//Now skip content without reading it.
		p.read.next();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
	
	
	@Test public void testNextInitialData_d()throws IOException
	{
		/* 
			We check if we can do next() to skip intial 
			primitives. 
			
			This test is for described format
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		p.write.open();			
		//We write some number of primitives.
		p.write.writeType(TIndicator.TYPE_LONG);
			p.write.writeLong(0x4444_0000_0000_AAAAL);
		p.write.writeFlush(TIndicator.FLUSH_LONG);
		p.write.writeType(TIndicator.TYPE_LONG);
			p.write.writeLong(0x7788_3344_0000_AAAAL);
		p.write.writeFlush(TIndicator.FLUSH_LONG);
		p.write.writeBeginDirect("A");
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.TYPE_LONG);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.DATA);
		p.read.next();
		assertReadFlushIndicator(p.read,TIndicator.FLUSH_LONG);
		
		assertGetIndicator(p.read,TIndicator.TYPE_LONG);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.DATA);
		p.read.next();
		assertReadFlushIndicator(p.read,TIndicator.FLUSH_LONG);
		
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
	
	
	
	
	
	@Test public void testNextData_ud()throws IOException
	{
		/* 
			We check if we can do next() to skip data within 
			and event.
			
			This test is for un-described format
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG);
					p.write.writeLong(0x4444_0000_0000_AAAAL);
				p.write.writeFlush(TIndicator.FLUSH_LONG);
				p.write.writeType(TIndicator.TYPE_LONG);
					p.write.writeLong(0x7788_3344_0000_AAAAL);
				p.write.writeFlush(TIndicator.FLUSH_LONG);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.DATA);
		p.read.next();		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};     
	
	
	
	
	@Test public void testNextData_d()throws IOException
	{
		/* 
			We check if we can do next() to skip
			primitives inside event.
			
			This test is for described format
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		p.write.open();			
		p.write.writeBeginDirect("A");
		
			//We write some number of primitives.
			p.write.writeType(TIndicator.TYPE_LONG);
				p.write.writeLong(0x4444_0000_0000_AAAAL);
			p.write.writeFlush(TIndicator.FLUSH_LONG);
			p.write.writeType(TIndicator.TYPE_LONG);
				p.write.writeLong(0x7788_3344_0000_AAAAL);
			p.write.writeFlush(TIndicator.FLUSH_LONG);
		
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
	
		
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		
			assertGetIndicator(p.read,TIndicator.TYPE_LONG);
			p.read.next();
			assertGetIndicator(p.read,TIndicator.DATA);
			p.read.next();
			assertReadFlushIndicator(p.read,TIndicator.FLUSH_LONG);
		
			assertGetIndicator(p.read,TIndicator.TYPE_LONG);
			p.read.next();
			assertGetIndicator(p.read,TIndicator.DATA);
			p.read.next();
			assertReadFlushIndicator(p.read,TIndicator.FLUSH_LONG);
		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
	
	
	
	
	@Test public void testNextDataRd_ud()throws IOException
	{
		/* 
			We check if we can do next() to skip data within 
			an event if some data are read.
			
			This test is for un-described format
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG);
					p.write.writeLong(0x4444_0000_0000_AAAAL);
				p.write.writeFlush(TIndicator.FLUSH_LONG);
				p.write.writeType(TIndicator.TYPE_LONG);
					p.write.writeLong(0x7788_3344_0000_AAAAL);
				p.write.writeFlush(TIndicator.FLUSH_LONG);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.DATA);
		//read some data.
		p.read.readLong();
		//and skip the rest.
		p.read.next();		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};    
	
	
	
	
	
	@Test public void testSkipData_ud()throws IOException
	{
		/* 
			We check if we can do skip() to skip data within 
			an event.
			
			This test is for un-described format
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG);
					p.write.writeLong(0x4444_0000_0000_AAAAL);
				p.write.writeFlush(TIndicator.FLUSH_LONG);
				p.write.writeType(TIndicator.TYPE_LONG);
					p.write.writeLong(0x7788_3344_0000_AAAAL);
				p.write.writeFlush(TIndicator.FLUSH_LONG);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.DATA);
		p.read.skip();		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
	
	
	
	
	
	
	@Test public void testNextDataBlock_ud()throws IOException
	{
		/* 
			We check if we can do next() to skip data block within
			an event
			
			This test is for un-described format
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
			
				final long [] data = createSequence(757);
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
					p.write.writeLongBlock(data);
				p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.DATA);
		//skip data
		p.read.next();		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};   
	
	
	
	@Test public void testNextDataBlock_d()throws IOException
	{
		/* 
			We check if we can do next() to skip data block within
			an event
			
			This test is for described format
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
			
				final long [] data = createSequence(157);
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
					p.write.writeLongBlock(data);
				p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.TYPE_LONG_BLOCK);
			p.read.next();
			assertGetIndicator(p.read,TIndicator.DATA);
			//skip data
			p.read.next();
			assertReadFlushIndicator(p.read,TIndicator.FLUSH_LONG_BLOCK);				
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};  
	
	
	
	@Test public void testNextDataBlockRd_ud()throws IOException
	{
		/* 
			We check if we can do next() to skip data block within
			an event when some piece of data was read.
			
			This test is for un-described format
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
			
				final long [] data = createSequence(757);
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
					p.write.writeLongBlock(data);
				p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.DATA);
		final long [] y = new long [data.length/2];
		Assert.assertTrue(p.read.readLongBlock(y)==y.length); 
		//skip remaning data
		p.read.next();		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};
	
	
	@Test public void testNextDataBlockRd_d()throws IOException
	{
		/* 
			We check if we can do next() to skip data block within
			an event when some piece of data was read.
			
			This test is for described format
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
			
				final long [] data = createSequence(757);
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
					p.write.writeLongBlock(data);
				p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.TYPE_LONG_BLOCK);
		p.read.next();
			assertGetIndicator(p.read,TIndicator.DATA);
			final long [] y = new long [data.length/2];
			Assert.assertTrue(p.read.readLongBlock(y)==y.length); 
			//skip remaning data
			p.read.next();		
			assertReadFlushIndicator(p.read,TIndicator.FLUSH_LONG_BLOCK);
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	};    
	
	
	@Test public void testNextDataBlockRdf_ud()throws IOException
	{
		/* 
			We check if we can do next() to skip data block within
			an event exactly all data was read.
			
			This test is for un-described format
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
			
				final long [] data = createSequence(1757);
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
					p.write.writeLongBlock(data);
				p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.DATA);
		final long [] y = new long [data.length];
		Assert.assertTrue(p.read.readLongBlock(y)==y.length);
		//Now cursor should be at the END
		assertGetIndicator(p.read,TIndicator.END);
		//And skip should be no-operation 
		p.read.skip();		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	}; 
	
	
	@Test public void testNextDataBlockRdf_d()throws IOException
	{
		/* 
			We check if we can do next() to skip data block within
			an event exactly all data was read.
			
			This test is for described format
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
			
				final long [] data = createSequence(757);
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
					p.write.writeLongBlock(data);
				p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.TYPE_LONG_BLOCK);
		p.read.next();
			assertGetIndicator(p.read,TIndicator.DATA);
			final long [] y = new long [data.length];
			Assert.assertTrue(p.read.readLongBlock(y)==y.length);
			assertReadFlushIndicator(p.read,TIndicator.FLUSH_LONG_BLOCK);
			//Now cursor should be at the END				
		assertGetIndicator(p.read,TIndicator.END);
		//And skip should be no-operation 
		p.read.skip();		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	}; 
	
	
	@Test public void testNextDataBlockRdp_ud()throws IOException
	{
		/* 
			We check if we can do next() to skip data block within
			an event exactly all data was read with a partial read.
			
			This test is for un-described format
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
			
				final long [] data = createSequence(7);
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
					p.write.writeLongBlock(data);
				p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.DATA);
		final long [] y = new long [data.length+5];
		Assert.assertTrue(p.read.readLongBlock(y)==data.length);
		//Now cursor should be at the END
		assertGetIndicator(p.read,TIndicator.END);
		//And skip should be no-operation 
		p.read.skip();		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	}; 
	
	
	
	
	@Test public void testNextDataBlockRdp_d()throws IOException
	{
		/* 
			We check if we can do next() to skip data block within
			an event exactly all data was read with a partial read.
			
			This test is for described format
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		
		p.write.open();
		p.write.writeBeginDirect("A");		
			
				final long [] data = createSequence(256);
				//We write some number of primitives.
				p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
					p.write.writeLongBlock(data);
				p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("A".equals(p.read.getSignalName()));
		p.read.next();
		assertGetIndicator(p.read,TIndicator.TYPE_LONG_BLOCK);
			p.read.next();
			assertGetIndicator(p.read,TIndicator.DATA);
			final long [] y = new long [data.length+5];
			Assert.assertTrue(p.read.readLongBlock(y)==data.length);
			assertReadFlushIndicator(p.read,TIndicator.FLUSH_LONG_BLOCK);	
			//Now cursor should be at the END
		assertGetIndicator(p.read,TIndicator.END);
		//And skip should be no-operation 
		p.read.skip();		
		assertGetIndicator(p.read,TIndicator.END);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.next();
		assertGetIndicator(p.read,TIndicator.EOF);
		assertGetIndicator(p.read,TIndicator.EOF);
		p.read.close();
		
		leave();
	}; 
};