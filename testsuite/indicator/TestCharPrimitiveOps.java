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
	exchange char primitives.
*/
public class TestCharPrimitiveOps extends ATestCharOps
{
	
	/** Writes sequence using elementary single ops 
	@param x what to write
	@param write where to write
	*/
	private void writeElementary(char [] x, IIndicatorWriteFormat write)throws IOException
	{
		enter("writeElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			write.writeType(TIndicator.TYPE_CHAR);
			write.writeChar(x[i]);
			write.writeFlush(TIndicator.FLUSH_CHAR);
		};
		leave();
	};
	/** Read sequence using elementary single ops and validates if correct. 
	@param x what to expect
	@param read where to read from
	*/
	private void expectElementary(char [] x, IIndicatorReadFormat read)throws IOException
	{
		enter("expectElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			if (read.isDescribed())
			{
				assertReadIndicator(read,TIndicator.TYPE_CHAR);
			}
			assertGetIndicator(read,TIndicator.DATA);
			char v = read.readChar();
			if (v!=x[i])
				Assert.fail("At x["+i+"] read "+v+" while expected "+x[i]);
			if (read.isFlushing())
			{
				assertReadIndicator(read,TIndicator.FLUSH,TIndicator.FLUSH);
			};
		};
		leave();
	};
	
	private void elementaryChars(int length)throws IOException
	{
		enter("length="+length);
		
		/*
			We just write some short sequence of chars and
			validate it.
		*/
		enter();
		char [] sequence = createSequence(length);
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
	
	@Test public void elementaryChars_1()throws IOException
	{
		enter();
		elementaryChars(1);
		leave();
	};
	@Test public void elementaryChars_10()throws IOException
	{
		enter();
		elementaryChars(10);
		leave();
	};
	@Test public void elementaryChars_100()throws IOException
	{
		enter();
		elementaryChars(100);
		leave();
	};
	@Test public void elementaryChars_1000()throws IOException
	{
		enter();
		elementaryChars(1000);
		leave();
	};
	@Test public void elementaryChars_5000()throws IOException
	{
		enter();
		elementaryChars(5000);
		leave();
	};
};