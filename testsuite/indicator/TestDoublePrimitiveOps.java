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
	exchange double primitives.
*/
public class TestDoublePrimitiveOps extends ATestDoubleOps
{
	
	/** Writes sequence using elementary single ops 
	@param x what to write
	@param write where to write
	*/
	private void writeElementary(double [] x, IIndicatorWriteFormat write)throws IOException
	{
		enter("writeElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			write.writeType(TIndicator.TYPE_DOUBLE);
			write.writeDouble(x[i]);
			write.writeFlush(TIndicator.FLUSH_DOUBLE);
		};
		leave();
	};
	/** Read sequence using elementary single ops and validates if correct. 
	@param x what to expect
	@param read where to read from
	*/
	private void expectElementary(double [] x, IIndicatorReadFormat read)throws IOException
	{
		enter("expectElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			if (read.isDescribed())
			{
				assertReadIndicator(read,TIndicator.TYPE_DOUBLE);
			}
			assertGetIndicator(read,TIndicator.DATA);
			double v = read.readDouble();
			if (v!=x[i])
				Assert.fail("At x["+i+"] read "+v+" while expected "+x[i]);
			if (read.isFlushing())
			{
				assertReadIndicator(read,TIndicator.FLUSH,TIndicator.FLUSH);
			};
		};
		leave();
	};
	
	private void elementaryDoubles(int length)throws IOException
	{
		enter("length="+length);
		
		/*
			We just write some short sequence of doubles and
			validate it.
		*/
		enter();
		double [] sequence = createSequence(length);
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
	
	@Test public void elementaryDoubles_1()throws IOException
	{
		enter();
		elementaryDoubles(1);
		leave();
	};
	@Test public void elementaryDoubles_10()throws IOException
	{
		enter();
		elementaryDoubles(10);
		leave();
	};
	@Test public void elementaryDoubles_100()throws IOException
	{
		enter();
		elementaryDoubles(100);
		leave();
	};
	@Test public void elementaryDoubles_1000()throws IOException
	{
		enter();
		elementaryDoubles(1000);
		leave();
	};
	@Test public void elementaryDoubles_5000()throws IOException
	{
		enter();
		elementaryDoubles(5000);
		leave();
	};
};