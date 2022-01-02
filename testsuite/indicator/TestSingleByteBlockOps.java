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
	exchange byte blocks, assuming one-byte writes
	and reads are used.
*/
public class TestSingleByteBlockOps extends ATestByteOps
{	
	/** Writes sequence using fixed size block operations. 
	@param x what to write 
	@param write where to write
	*/
	private void writeBlock(byte [] x, IIndicatorWriteFormat write)throws IOException
	{
		enter("writBlock x["+x.length+"]");
		//Note: We always need to store type info
		write.writeType(TIndicator.TYPE_BYTE_BLOCK);			
		for(int i=0;i<x.length;i++)
		{
			write.writeByteBlock(x[i]);		
		};
		write.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
		leave();
	};
	
	/* ------------------------------------------------------------------------
			Without triggering partial reads.
			
			Note: Due to the requirement of testing getIndicator prior
			to read there is no possibility of getting partial read in single
			byte block operations.
	------------------------------------------------------------------------*/
	/** Read sequence using elementary single ops and validates if correct. 
	This operation is using reads which will excactly match the size of x array.
	@param x what to expect	
	@param read where to read from
	*/
	private void expectBlockAccurate(byte [] x, IIndicatorReadFormat read)throws IOException
	{
		enter("expectElementary x["+x.length+"]");		
		//Note: We always need to store type info
		if (read.isDescribed())
		{
			assertReadIndicator(read,TIndicator.TYPE_BYTE_BLOCK);
		}
		for(int i=0;i<x.length;i++)
		{
			assertGetIndicator(read,TIndicator.DATA);
			int r = read.readByteBlock();
			if (r<0)
				Assert.fail("read block gave unexpected partial read");
				
			//now compare this fragment.
			if (x[i]!=(byte)r)
					Assert.fail("x["+i+"]="+x[i]+" != "+(byte)r);
		};
		if (read.isFlushing())
		{
			assertReadIndicator(read,TIndicator.FLUSH,TIndicator.FLUSH);
		};
		leave();
	};
	
	
	
	
	private void blockByteAccurate(int length)throws IOException
	{
		enter("length="+length);
		
		/*
			We just write some short block of bytes and
			validate it using size accurate read-back.
		*/
		enter();
		byte [] sequence = createSequence(length);
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("");
			writeBlock(sequence,  p.write);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("".equals(p.read.getSignalName()));
			expectBlockAccurate(sequence, p.read);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.close();
		
		leave();
	};
	
	
	@Test public void blockBytes_1a()throws IOException
	{
		enter();
		blockByteAccurate(1);
		leave();
	};
	@Test public void blockBytes_10a()throws IOException
	{
		enter();
		blockByteAccurate(10);
		leave();
	};
	@Test public void blockBytes_1000a()throws IOException
	{
		enter();
		blockByteAccurate(1000);
		leave();
	};
	
	
	
	
	
	
	
	
};