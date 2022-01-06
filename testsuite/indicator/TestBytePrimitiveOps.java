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
	exchange byte primitives.
*/
public class TestBytePrimitiveOps extends ATestByteOps
{
	
	/** Writes sequence using elementary single ops 
	@param x what to write
	@param write where to write
	@throws IOException if failed at low level
	*/
	private void writeElementary(byte [] x, IIndicatorWriteFormat write)throws IOException
	{
		enter("writeElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			write.writeType(TIndicator.TYPE_BYTE);
			write.writeByte(x[i]);
			write.writeFlush(TIndicator.FLUSH_BYTE);
		};
		leave();
	};
	/** Read sequence using elementary single ops and validates if correct. 
	@param x what to expect
	@param read where to read from
	@throws IOException if failed at low level
	*/
	private void expectElementary(byte [] x, IIndicatorReadFormat read)throws IOException
	{
		enter("expectElementary x["+x.length+"]");
		for(int i=0;i<x.length;i++)
		{
			//Note: We always need to store type info
			if (read.isDescribed())
			{
				assertReadIndicator(read,TIndicator.TYPE_BYTE);
			}
			assertGetIndicator(read,TIndicator.DATA);
			byte v = read.readByte();
			if (v!=x[i])
				Assert.fail("At x["+i+"] read "+v+" while expected "+x[i]);
			assertReadFlushIndicator(read,TIndicator.FLUSH_BYTE);
		};
		leave();
	};
	
	private void elementaryBytes(int length)throws IOException
	{
		enter("length="+length);
		
		/*
			We just write some short sequence of bytes and
			validate it.
		*/
		enter();
		byte [] sequence = createSequence(length);
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
	
	@Test public void elementaryBytes_1()throws IOException
	{
		enter();
		elementaryBytes(1);
		leave();
	};
	@Test public void elementaryBytes_10()throws IOException
	{
		enter();
		elementaryBytes(10);
		leave();
	};
	@Test public void elementaryBytes_100()throws IOException
	{
		enter();
		elementaryBytes(100);
		leave();
	};
	@Test public void elementaryBytes_1000()throws IOException
	{
		enter();
		elementaryBytes(1000);
		leave();
	};
	@Test public void elementaryBytes_5000()throws IOException
	{
		enter();
		elementaryBytes(5000);
		leave();
	};
};