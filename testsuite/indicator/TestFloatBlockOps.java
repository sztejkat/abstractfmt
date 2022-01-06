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
	exchange float blocks.
*/
public class TestFloatBlockOps extends ATestFloatOps
{	
	/** Writes sequence using fixed size block operations. 
	@param x what to write
	@param op_size  block transfer size 
	@param write where to write
	@throws IOException if failed at low level
	*/
	private void writeBlock(float [] x, int op_size, IIndicatorWriteFormat write)throws IOException
	{
		enter("writBlock x["+x.length+"]");
		//Note: We always need to store type info
		write.writeType(TIndicator.TYPE_FLOAT_BLOCK);			
		for(int i=0;i<x.length;)
		{
			int left = x.length-i;
			int transfer = Math.min( op_size, left );			
			System.out.println("writeFloatBlock(x,"+i+","+transfer+")");
			write.writeFloatBlock(x, i, transfer);
			i+=transfer;			
		};
		write.writeFlush(TIndicator.FLUSH_FLOAT_BLOCK);
		leave();
	};
	
	
	
	
	
	/* -----------------------------------------------------------------------------
	
				Size accurate transfers
	
	-----------------------------------------------------------------------------*/
	/** Read sequence using elementary single ops and validates if correct. 
	This operation is using reads which will excactly match the size of x array.
	@param x what to expect	
	@param op_size  block transfer size 
	@param read where to read from
	@throws IOException if failed at low level
	*/
	private void expectBlockAccurate(float [] x, int op_size, IIndicatorReadFormat read)throws IOException
	{
		enter("expectElementary x["+x.length+"]");		
		//Note: We always need to store type info
		if (read.isDescribed())
		{
			assertReadIndicator(read,TIndicator.TYPE_FLOAT_BLOCK);
		}
		final float [] y = new float [ x.length ];	//buffer, size we try out section reads.
		for(int i=0;i<x.length;)
		{
			assertGetIndicator(read,TIndicator.DATA);
			int left = x.length-i;
			int transfer = Math.min(  op_size, left );	
			System.out.println("readFloatBlock(y,"+i+","+transfer+")");		
			int r = read.readFloatBlock(y, i, transfer);
			if (r!=transfer)
				Assert.fail("read block at "+i+" gave "+r+" while requested "+transfer);
				
			//now compare this fragment.
			for(int j=0;j<r;j++)
			{
				int ii = i+j;
				if (x[ii]!=y[ii])
					Assert.fail("x["+ii+"]="+x[ii]+" != y["+ii+"]="+y[ii]);
			};
			i+=transfer;				
		};
		assertReadFlushIndicator(read,TIndicator.FLUSH_FLOAT_BLOCK);
		
		leave();
	};
	
	
	
	
	private void blockFloatAccurate(int length, int max_opsize)throws IOException
	{
		enter("length="+length);
		
		/*
			We just write some short block of floats and
			validate it using size accurate read-back.
		*/
		enter();
		float [] sequence = createSequence(length);
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("");
			writeBlock(sequence, max_opsize,  p.write);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("".equals(p.read.getSignalName()));
			expectBlockAccurate(sequence, max_opsize, p.read);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.close();
		
		leave();
	};
	
	
	@Test public void blockFloats_1a()throws IOException
	{
		enter();
		blockFloatAccurate(1,10);
		leave();
	};
	@Test public void blockFloats_10a_1()throws IOException
	{
		enter();
		blockFloatAccurate(10,100);
		leave();
	};
	@Test public void blockFloats_10a_2()throws IOException
	{
		enter();
		blockFloatAccurate(10,7);
		leave();
	};
	@Test public void blockFloats_10a_3()throws IOException
	{
		enter();
		blockFloatAccurate(10,3);
		leave();
	};
	@Test public void blockFloats_1000a_1()throws IOException
	{
		enter();
		blockFloatAccurate(1000,2000);
		leave();
	};
	@Test public void blockFloats_1000a_2()throws IOException
	{
		enter();
		blockFloatAccurate(1000,520);
		leave();
	};
	@Test public void blockFloats_1000a_3()throws IOException
	{
		enter();
		blockFloatAccurate(1000,103);
		leave();
	};
	
	
	
	
	
	
	
	
	
	/* -----------------------------------------------------------------------------
	
				Size inaccurate transfers with partial reads.
	
	-----------------------------------------------------------------------------*/
	/** Read sequence using elementary single ops and validates if correct. 
	This operation is using reads which may give partial read
	@param x what to expect	
	@param op_size  block transfer size 
	@param read where to read from
	@throws IOException if failed at low level
	*/
	private void expectBlockInaccurate(float [] x, int op_size, IIndicatorReadFormat read)throws IOException
	{
		enter("expectElementary x["+x.length+"]");		
		//Note: We always need to store type info
		if (read.isDescribed())
		{
			assertReadIndicator(read,TIndicator.TYPE_FLOAT_BLOCK);
		}
		final float [] y = new float [ x.length +op_size ];	//buffer, size we try out section reads.
																 //we need to reserve more space so transfer
																 //request at end do not barf.
		for(int i=0;i<x.length;)
		{
			assertGetIndicator(read,TIndicator.DATA);
			int transfer = op_size;
			System.out.println("readFloatBlock(y,"+i+","+transfer+")");		
			int r = read.readFloatBlock(y, i, transfer);
				
			//now compare this fragment.
			for(int j=0;j<r;j++)
			{
				int ii = i+j;
				if (x[ii]!=y[ii])
					Assert.fail("x["+ii+"]="+x[ii]+" != y["+ii+"]="+y[ii]);
			};
			i+=r;
			if (r<transfer)
			{
				if (i!=x.length)
					Assert.fail("Got partial read "+r+" but something is left for transfer");
			};				
		};
		assertReadFlushIndicator(read,TIndicator.FLUSH_FLOAT_BLOCK);
		
		leave();
	};
	private void blockFloatInaccurate(int length, int max_opsize)throws IOException
	{
		enter("length="+length);
		
		/*
			We just write some short block of floats and
			validate it using possible partial read operations.
		*/
		enter();
		float [] sequence = createSequence(length);
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("");
			writeBlock(sequence, max_opsize,  p.write);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("".equals(p.read.getSignalName()));
			expectBlockInaccurate(sequence, max_opsize, p.read);
		assertGetIndicator(p.read,TIndicator.END);
		p.read.close();
		
		leave();
	};
	
	
	
	
	
	@Test public void blockFloats_1ia()throws IOException
	{
		enter();
		blockFloatInaccurate(1,10);
		leave();
	};
	@Test public void blockFloats_10ia_1()throws IOException
	{
		enter();
		blockFloatInaccurate(10,20);
		leave();
	};
	@Test public void blockFloats_10ia_2()throws IOException
	{
		enter();
		blockFloatInaccurate(10,4);
		leave();
	};
	@Test public void blockFloats_10ia_3()throws IOException
	{
		enter();
		blockFloatInaccurate(10,3);
		leave();
	};
	@Test public void blockFloats_1000ia_1()throws IOException
	{
		enter();
		blockFloatInaccurate(1000,99);
		leave();
	};
	@Test public void blockFloats_1000ia_2()throws IOException
	{
		enter();
		blockFloatInaccurate(1000,499);
		leave();
	};
	@Test public void blockFloats_1000ia_3()throws IOException
	{
		enter();
		blockFloatInaccurate(1000,19);
		leave();
	};
	
	
	
	
};