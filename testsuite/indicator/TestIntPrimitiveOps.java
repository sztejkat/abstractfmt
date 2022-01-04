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
	exchange int primitives.
*/
public class TestIntPrimitiveOps extends ATestIntOps
{
	
	/** Writes sequence using elementary single ops 
	@param x what to write
	@param write where to write
	*/
	private void writeElementary(int [] x, IIndicatorWriteFormat write)throws IOException
	{
		enter("writeElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			write.writeType(TIndicator.TYPE_INT);
			write.writeInt(x[i]);
			write.writeFlush(TIndicator.FLUSH_INT);
		};
		leave();
	};
	/** Read sequence using elementary single ops and validates if correct. 
	@param x what to expect
	@param read where to read from
	*/
	private void expectElementary(int [] x, IIndicatorReadFormat read)throws IOException
	{
		enter("expectElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			if (read.isDescribed())
			{
				assertReadIndicator(read,TIndicator.TYPE_INT);
			}
			assertGetIndicator(read,TIndicator.DATA);
			int v = read.readInt();
			if (v!=x[i])
				Assert.fail("At x["+i+"] read "+v+" while expected "+x[i]);
			assertReadFlushIndicator(read,TIndicator.FLUSH_INT);
		};
		leave();
	};
	
	private void elementaryInts(int length)throws IOException
	{
		enter("length="+length);
		
		/*
			We just write some short sequence of ints and
			validate it.
		*/
		enter();
		int [] sequence = createSequence(length);
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
	
	@Test public void elementaryInts_1()throws IOException
	{
		enter();
		elementaryInts(1);
		leave();
	};
	@Test public void elementaryInts_10()throws IOException
	{
		enter();
		elementaryInts(10);
		leave();
	};
	@Test public void elementaryInts_100()throws IOException
	{
		enter();
		elementaryInts(100);
		leave();
	};
	@Test public void elementaryInts_1000()throws IOException
	{
		enter();
		elementaryInts(1000);
		leave();
	};
	@Test public void elementaryInts_5000()throws IOException
	{
		enter();
		elementaryInts(5000);
		leave();
	};
};