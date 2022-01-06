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
	Tests if short primitives are properly processed,
	including boundary conditions. 
*/
public class TestShortPrimitiveOps extends ATestShortOps
{
	@Test public void standAloneOperations()throws IOException
	{
		/*
			Run basic exchange tests of elementary data
			not enclosed in any event.
		*/
		enter();
		Pair p = create();
		short [] sequence = createSequence(100);
		p.write.open();
		//write elementary
		for(int i=0;i<sequence.length;i++)
				p.write.writeShort(sequence[i]);
		p.write.close();
		
		
		//read them back.
		p.read.open();
		for(int i=0;i<sequence.length;i++)
		{
				short v= p.read.readShort(); 
				if (v!=sequence[i]) Assert.fail(v+"!="+sequence[i]+" at i="+i);
		}		
		assertWhatNext(p.read,TContentType.EOF);
		p.read.close();
			
		leave();
	};
	
	@Test public void tooManyStandAloneReads()throws IOException
	{
		/*
			Run basic exchange tests of elementary data
			not enclosed in any event when we reach past end of stream.
		*/
		enter();
		Pair p = create();
		short [] sequence = createSequence(5);
		p.write.open();
		//write elementary
		for(int i=0;i<sequence.length;i++)
				p.write.writeShort(sequence[i]);
		p.write.close();
		
		
		//read them back.
		p.read.open();
		for(int i=0;i<sequence.length;i++)
		{
				short v= p.read.readShort(); 
				if (v!=sequence[i]) Assert.fail(v+"!="+sequence[i]+" at i="+i);
		}		
		try{
			p.read.readShort(); 
			Assert.fail();
		}catch(EUnexpectedEof ex){};
		p.read.close();
			
		leave();
	};
	
	
	
	@Test public void inEventOperation()throws IOException
	{
		/*
			Run basic exchange tests of elementary data
			enclosed in an event.
		*/
		enter();
		Pair p = create();
		short [] sequence = createSequence(100);
		p.write.open();
		p.write.begin("test data");
		p.write.open();
		//write elementary
		for(int i=0;i<sequence.length;i++)
				p.write.writeShort(sequence[i]);
		p.write.end();
		p.write.close();
		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"test data");
		for(int i=0;i<sequence.length;i++)
		{
				short v= p.read.readShort(); 
				if (v!=sequence[i]) Assert.fail(v+"!="+sequence[i]+" at i="+i);
		}		
		assertNext(p.read,null);
		p.read.close();
		leave();
	};
	
	
	
	
	@Test public void tooManyReadsInEvent()throws IOException
	{
		/*
			Run basic exchange tests of elementary data
			enclosed in an event and check what happens
			if we read past event boundary.
		*/
		enter();
		Pair p = create();
		short [] sequence = createSequence(100);
		p.write.open();
		p.write.begin("test data");
		p.write.open();
		//write elementary
		for(int i=0;i<sequence.length;i++)
				p.write.writeShort(sequence[i]);
		p.write.end();
		p.write.close();
		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"test data");
		for(int i=0;i<sequence.length;i++)
		{
				short v= p.read.readShort(); 
				if (v!=sequence[i]) Assert.fail(v+"!="+sequence[i]+" at i="+i);
		}	
		try{
			p.read.readShort(); 
			Assert.fail();
		}catch(ENoMoreData ex){};
		try{
			p.read.readShort(); 
			Assert.fail();
		}catch(ENoMoreData ex){};
			
		assertNext(p.read,null);
		p.read.close();
		leave();
	};
	
	
	
	
	@Test public void notEnoughReadsInEvent()throws IOException
	{
		/*
			Run basic exchange tests of elementary data
			enclosed in an event and check what happens
			if we will nor read all data.
		*/
		enter();
		Pair p = create();
		short [] sequence = createSequence(100);
		p.write.open();
		p.write.begin("test data");
		p.write.open();
		//write elementary
		for(int i=0;i<sequence.length;i++)
				p.write.writeShort(sequence[i]);
		p.write.end();
		p.write.close();
		
		
		//read them back.
		p.read.open();
		assertNext(p.read,"test data");
		for(int i=0;i<sequence.length/2;i++)
		{
				short v= p.read.readShort(); 
				if (v!=sequence[i]) Assert.fail(v+"!="+sequence[i]+" at i="+i);
		}		
		assertNext(p.read,null);
		p.read.close();
		leave();
	};
};