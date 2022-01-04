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
	exchange float primitives.
*/
public class TestFloatPrimitiveOps extends ATestFloatOps
{
	
	/** Writes sequence using elementary single ops 
	@param x what to write
	@param write where to write
	*/
	private void writeElementary(float [] x, IIndicatorWriteFormat write)throws IOException
	{
		enter("writeElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			write.writeType(TIndicator.TYPE_FLOAT);
			write.writeFloat(x[i]);
			write.writeFlush(TIndicator.FLUSH_FLOAT);
		};
		leave();
	};
	/** Read sequence using elementary single ops and validates if correct. 
	@param x what to expect
	@param read where to read from
	*/
	private void expectElementary(float [] x, IIndicatorReadFormat read)throws IOException
	{
		enter("expectElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			if (read.isDescribed())
			{
				assertReadIndicator(read,TIndicator.TYPE_FLOAT);
			}
			assertGetIndicator(read,TIndicator.DATA);
			float v = read.readFloat();
			if (v!=x[i])
				Assert.fail("At x["+i+"] read "+v+" while expected "+x[i]);
			assertReadFlushIndicator(read,TIndicator.FLUSH_FLOAT);
		};
		leave();
	};
	
	private void elementaryFloats(int length)throws IOException
	{
		enter("length="+length);
		
		/*
			We just write some short sequence of floats and
			validate it.
		*/
		enter();
		float [] sequence = createSequence(length);
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
	
	@Test public void elementaryFloats_1()throws IOException
	{
		enter();
		elementaryFloats(1);
		leave();
	};
	@Test public void elementaryFloats_10()throws IOException
	{
		enter();
		elementaryFloats(10);
		leave();
	};
	@Test public void elementaryFloats_100()throws IOException
	{
		enter();
		elementaryFloats(100);
		leave();
	};
	@Test public void elementaryFloats_1000()throws IOException
	{
		enter();
		elementaryFloats(1000);
		leave();
	};
	@Test public void elementaryFloats_5000()throws IOException
	{
		enter();
		elementaryFloats(5000);
		leave();
	};
};