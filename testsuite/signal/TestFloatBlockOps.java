package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.ISignalWriteFormat;
import sztejkat.abstractfmt.TContentType;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EClosed;
import sztejkat.abstractfmt.ENoMoreData;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;

/**
	Tests if float blocks are properly processed,
	including boundary conditions. 
*/
public class TestFloatBlockOps extends ATestFloatOps
{
	
	@Test public void fullBlockWriteRead()throws IOException
	{
		/*
			We test if we can perform a full block 
			write and read back both in  same size
			operation.
		*/
		enter();
		Pair p = create();
		float [] sequence = createSequence(100);
		p.write.open();
		p.write.begin("array");
		p.write.writeFloatBlock(sequence);
		p.write.end();
		p.write.close();		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"array");
		assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
		float [] x = new float[sequence.length];
		int r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==x.length);
		assertEqual(x,0,r,sequence,0);
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,null);
		assertWhatNext(p.read,TContentType.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void fullBlockWriteReadWithBegin()throws IOException
	{
		/*
			We test if we can perform a full block 
			write and read back both in  same size
			operation.
		*/
		enter();
		Pair p = create();
		float [] sequence = createSequence(100);
		p.write.open();
		p.write.begin("array");
		p.write.writeFloatBlock(sequence);
		p.write.begin("borka");
		p.write.end();
		p.write.end();
		p.write.close();		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"array");
		assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
		float [] x = new float[sequence.length];
		int r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==x.length);
		assertEqual(x,0,r,sequence,0);
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,"borka");
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,null);
		assertNext(p.read,null);
		assertWhatNext(p.read,TContentType.EOF);
		p.read.close();
		leave();
	};
	
	
	
	private void scatterBlockWrite(int block_size, int transfer_size)throws IOException
	{
		/*
			We test if we can perform a scatter block 
			write and read back in one operation
		*/
		enter();
		Pair p = create();
		float [] sequence = createSequence(block_size);
		p.write.open();
		p.write.begin("array");
		for(int i=0;i<sequence.length;i+=transfer_size)
		{
			int t = Math.min(transfer_size,sequence.length-i);
			p.write.writeFloatBlock(sequence,i,t);
		};
		p.write.end();
		p.write.close();		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"array");
		assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
		float [] x = new float[sequence.length];
		int r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==x.length);
		assertEqual(x,0,r,sequence,0);
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,null);
		assertWhatNext(p.read,TContentType.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void scatterBlockWrite_small()throws IOException
	{
		scatterBlockWrite(200,101);
	};
	@Test public void scatterBlockWrite_large()throws IOException
	{
		scatterBlockWrite(1000,501);
	};
	@Test public void scatterBlockWrite_high_scatter()throws IOException
	{
		scatterBlockWrite(250,7);
	};
	
	
	
	private void scatterBlockRead(int block_size, int transfer_size)throws IOException
	{
		/*
			We test if we can perform a scatter block 
			read with large single write
		*/
		enter();
		Pair p = create();
		float [] sequence = createSequence(block_size);
		p.write.open();
		p.write.begin("array");
		p.write.writeFloatBlock(sequence);
		
		p.write.end();
		p.write.close();		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"array");
		
		for(int i=0;i<sequence.length;i+=transfer_size)
		{
			int t = Math.min(transfer_size,sequence.length-i);
			assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
			float [] x = new float[t+10];
			int r = p.read.readFloatBlock(x,10,t);
			Assert.assertTrue(r==t);
			assertEqual(x,10,r,sequence,i);
		};
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,null);
		assertWhatNext(p.read,TContentType.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void scatterBlockRead_small()throws IOException
	{
		scatterBlockRead(207,101);
	};
	@Test public void scatterBlockRead_large()throws IOException
	{
		scatterBlockRead(999,501);
	};
	@Test public void scatterBlockRead_high_scatter()throws IOException
	{
		scatterBlockRead(250,7);
	};
	
	
	
	
	
	
	private void scatterPartialBlockRead(int block_size, int transfer_size)throws IOException
	{
		/*
			We test if we can perform a scatter block 
			read with large single write.
			
			This time read operation will be performed in a such
			a way, that partial read do take place.
		*/
		assert(block_size % transfer_size!=0):"this setting will not produce partial read.";
		enter();
		Pair p = create();
		float [] sequence = createSequence(block_size);
		p.write.open();
		p.write.begin("array");
		p.write.writeFloatBlock(sequence);
		
		p.write.end();
		p.write.close();		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"array");
		
		for(int i=0;i<sequence.length;i+=transfer_size)
		{
			
			assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
			float [] x = new float[transfer_size];
			int r = p.read.readFloatBlock(x,0,transfer_size);
			Assert.assertTrue(r>=0);
			Assert.assertTrue(r<=transfer_size);
			assertEqual(x,0,r,sequence,i);
			if (r<transfer_size)
			{
				//must be last operation
				Assert.assertTrue(r+i==sequence.length);
				assertWhatNext(p.read,TContentType.SIGNAL);
				break;
			}
		};
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,null);
		assertWhatNext(p.read,TContentType.EOF);
		p.read.close();
		leave();
	};
	@Test public void scatterPartialBlockRead_single()throws IOException
	{
		scatterPartialBlockRead(100,500);
	};
	@Test public void scatterPartialBlockRead_small()throws IOException
	{
		scatterPartialBlockRead(100,99);
	};
	@Test public void scatterPartialBlockRead_large()throws IOException
	{
		scatterPartialBlockRead(1000,77);
	};
	
	
	
	@Test public void testRepeatedEndOfBlock()throws IOException
	{
		/*
			We test if we can perform a full block 
			write and read back and how it behaves if we continue
			to ask it to read.
		*/
		enter();
		Pair p = create();
		float [] sequence = createSequence(100);
		p.write.open();
		p.write.begin("array");
		p.write.writeFloatBlock(sequence);
		p.write.end();
		p.write.close();		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"array");
		assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
		float [] x = new float[sequence.length];
		
		int r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==x.length);
		r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==0);
		r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==0);
		
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,null);
		assertWhatNext(p.read,TContentType.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testRepeatedEndOfBlockWithBegin()throws IOException
	{
		/*
			We test if we can perform a full block 
			write and read back and how it behaves if we continue
			to ask it to read.
		*/
		enter();
		Pair p = create();
		float [] sequence = createSequence(100);
		p.write.open();
		p.write.begin("array");
		p.write.writeFloatBlock(sequence);
		p.write.begin("borka");
		p.write.end();
		p.write.end();
		p.write.close();		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"array");
		assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
		float [] x = new float[sequence.length];
		
		int r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==x.length);
		r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==0);
		r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==0);
		
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,"borka");
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,null);
		assertNext(p.read,null);
		assertWhatNext(p.read,TContentType.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testZeroOperation()throws IOException
	{
		/*
			We test what happens if we perform zero
			size write and cannot initialize read.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("array");
		p.write.writeFloatBlock(new float[0]);
		p.write.end();
		p.write.close();		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"array");
		float [] x = new float[100];
		int r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==0);
		r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==0);
		r = p.read.readFloatBlock(x);
		Assert.assertTrue(r==0);
		
		assertWhatNext(p.read,TContentType.SIGNAL);
		assertNext(p.read,null);
		assertWhatNext(p.read,TContentType.EOF);
		p.read.close();
		leave();
	};
	
	
	
};
	