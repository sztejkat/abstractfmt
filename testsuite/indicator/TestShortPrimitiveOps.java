package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.testsuite.*;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
/**
	A test which check if indicator do correctly
	exchange short primitives.
*/
public class TestShortPrimitiveOps extends ATestShortOps
{
	
	/** Writes sequence using elementary single ops 
	@param x what to write
	@param write where to write
	*/
	private void writeElementary(short [] x, IIndicatorWriteFormat write)throws IOException
	{
		enter("writeElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			write.writeType(TIndicator.TYPE_SHORT);
			write.writeShort(x[i]);
			write.writeFlush(TIndicator.FLUSH_SHORT);
		};
		leave();
	};
	/** Read sequence using elementary single ops and validates if correct. 
	@param x what to expect
	@param read where to read from
	*/
	private void expectElementary(short [] x, IIndicatorReadFormat read)throws IOException
	{
		enter("expectElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			if (read.isDescribed())
			{
				assertReadIndicator(read,TIndicator.TYPE_SHORT);
			}
			assertGetIndicator(read,TIndicator.DATA);
			short v = read.readShort();
			if (v!=x[i])
				Assert.fail("At x["+i+"] read "+v+" while expected "+x[i]);
			if (read.isFlushing())
			{
				assertReadIndicator(read,TIndicator.FLUSH,TIndicator.FLUSH);
			};
		};
		leave();
	};
	
	private void elementaryShorts(int length)throws IOException
	{
		enter("length="+length);
		
		/*
			We just write some short sequence of shorts and
			validate it.
		*/
		enter();
		short [] sequence = createSequence(length);
		Pair p = create();
		p.write.open();
			writeElementary(sequence, p.write);
		p.write.flush();
		p.write.close();
		p.read.open();
			expectElementary(sequence, p.read);
		p.read.close();
		leave();
		
		leave();
	};
	
	@Test public void elementaryShorts_1()throws IOException
	{
		enter();
		elementaryShorts(1);
		leave();
	};
	@Test public void elementaryShorts_10()throws IOException
	{
		enter();
		elementaryShorts(10);
		leave();
	};
	@Test public void elementaryShorts_100()throws IOException
	{
		enter();
		elementaryShorts(100);
		leave();
	};
	@Test public void elementaryShorts_1000()throws IOException
	{
		enter();
		elementaryShorts(1000);
		leave();
	};
	@Test public void elementaryShorts_5000()throws IOException
	{
		enter();
		elementaryShorts(5000);
		leave();
	};
};