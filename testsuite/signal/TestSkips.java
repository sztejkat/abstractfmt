package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.ISignalWriteFormat;
import sztejkat.abstractfmt.TContentType;
import sztejkat.abstractfmt.EUnexpectedEof;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;

/**
	Tests how format is handling complex data skipping
	scenarios.
*/
public class TestSkips extends ATestShortOps
{
	@Test public void nextAtZeroLevel_1()throws IOException
	{
		/*
			We check if we can skip primtives at zero
			level when we write some different combinations
			of them
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.writeInt(0x049349);
		p.write.writeLong(0x490394090L);
		p.write.writeDouble(0);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		leave();
	}  
	
	@Test public void nextAtZeroLevel_Eof()throws IOException
	{
		/*
			We check if we can skip primtives at zero
			level when we write some different combinations
			of them and hit end-of-file
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.writeInt(0x049349);
		p.write.writeLong(0x490394090L);
		p.write.writeDouble(0);
		p.write.close();
		
		p.read.open();
		try{
			p.read.next();
			Assert.fail();
			}catch(EUnexpectedEof ex){};
		p.read.close();
		leave();
	}  
	
	
	@Test public void nextAtZeroLevel_2()throws IOException
	{
		/*
			We check if we can skip primtives at zero
			level when we write some different combinations
			of them
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.writeShort((short)0x049349);
		p.write.writeChar('x');
		p.write.writeFloat(0);
		p.write.begin("mossake");
		p.write.writeFloat(33.3f);
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readFloat()==33.3f);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}
	
	@Test public void nextAtZeroLevel_3()throws IOException
	{
		/*
			We check if we can skip primtives at zero
			level when we write some different combinations
			of them
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.writeShort((short)0x049349);
		p.write.writeBoolean(false);
		p.write.writeFloat(0);
		p.write.begin("mossake");
		p.write.writeFloat(33.3f);
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readFloat()==33.3f);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}
	
	
	@Test public void nextInEvent_1()throws IOException
	{
		/*
			We check if we can skip primtives at event
			level when we write some different combinations
			of them
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeInt(0x049349);
		p.write.writeLong(0x490394090L);
		p.write.writeDouble(0);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}  
	
	
	@Test public void nextInEvent_2()throws IOException
	{
		/*
			We check if we can skip primtives at event
			level when we write some different combinations
			of them
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeInt(0x049349);
		p.write.writeLong(0x490394090L);
		p.write.writeDouble(0);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readInt()==0x049349);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}  
	
	@Test public void nextInEvent_3()throws IOException
	{
		/*
			We check if we can skip primtives at event
			level when we write some different combinations
			of them
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeInt(0x049349);
		p.write.writeLong(0x490394090L);
		p.write.writeDouble(0);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readInt()==0x049349);
		Assert.assertTrue(p.read.readLong()==0x490394090L);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}  
	
	@Test public void nextInEvent_4()throws IOException
	{
		/*
			We check if we can skip primtives at event
			level when we write some different combinations
			of them
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeInt(0x049349);
		p.write.writeLong(0x490394090L);
		p.write.writeDouble(0);
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readInt()==0x049349);
		Assert.assertTrue(p.read.readLong()==0x490394090L);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}  
	
	
	
	
	@Test public void nextInBooleanBlock()throws IOException
	{
		/*
			We check if we can skip block content.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeBooleanBlock(new boolean[100]);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readBooleanBlock(new boolean[10])==10);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}  
	
	
	@Test public void nextInByteBlock()throws IOException
	{
		/*
			We check if we can skip block content.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeByteBlock(new byte[100]);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readByteBlock(new byte[10])==10);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}  
	
	
	@Test public void nextInCharBlock()throws IOException
	{
		/*
			We check if we can skip block content.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeCharBlock(new char[100]);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readCharBlock(new char[10])==10);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}
	
	
	
	@Test public void nextInShortBlock()throws IOException
	{
		/*
			We check if we can skip block content.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeShortBlock(new short[100]);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readShortBlock(new short[10])==10);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}    
	
	
	@Test public void nextInIntBlock()throws IOException
	{
		/*
			We check if we can skip block content.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeIntBlock(new int[100]);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readIntBlock(new int[10])==10);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}  
	
	
	
	@Test public void nextInLongBlock()throws IOException
	{
		/*
			We check if we can skip block content.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeLongBlock(new long[100]);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readLongBlock(new long[10])==10);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}  
	
	
	@Test public void nextInFloatBlock()throws IOException
	{
		/*
			We check if we can skip block content.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeFloatBlock(new float[100]);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readFloatBlock(new float[10])==10);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}  
	
	
	
	
	@Test public void nextInDoubleBlock()throws IOException
	{
		/*
			We check if we can skip block content.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("sake");
		p.write.writeDoubleBlock(new double[100]);
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"sake");
		Assert.assertTrue(p.read.readDoubleBlock(new double[10])==10);
		assertNext(p.read,"mossake");
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,null);
		p.read.close();
		leave();
	}
	
	
	
	
	
	
	@Test public void testSkipEvent()throws IOException
	{
		/*
			We check if we can skip entire event correctly.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
			p.write.begin("amber");
			p.write.writeDoubleBlock(new double[3]);
			p.write.writeDoubleBlock(new double[5]);
				p.write.begin("tonker");
				p.write.writeDoubleBlock(new double[3]);
				p.write.writeDoubleBlock(new double[5]);
				p.write.end();
				p.write.begin("zone");
				p.write.writeInt(0x4444444);
				p.write.end();
			p.write.end();	
			p.write.begin("roger");
				p.write.begin("zone");
				p.write.writeCharBlock("superata");
				p.write.end();
				p.write.begin("zone");
				p.write.writeInt(0x4444444);
				p.write.end();
			p.write.end();		
		p.write.end();
		p.write.begin("zoe");
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"mossake");		
		Assert.assertTrue(p.read.readDouble()==33.3);
		p.read.skip();
		assertNext(p.read,"zoe");
		p.read.close();
		leave();
	}   
	
	
	@Test public void testSkipEvent2()throws IOException
	{
		/*
			We check if we can skip entire event correctly.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("mossake");
		p.write.writeDouble(33.3);
			p.write.begin("amber");
			p.write.writeDoubleBlock(new double[3]);
			p.write.writeDoubleBlock(new double[5]);
				p.write.begin("tonker");
				p.write.writeDoubleBlock(new double[3]);
				p.write.writeDoubleBlock(new double[5]);
				p.write.end();
				p.write.begin("zone");
				p.write.writeInt(0x4444444);
				p.write.end();
			p.write.end();	
			p.write.begin("roger");
				p.write.begin("zone");
				p.write.writeCharBlock("superata");
				p.write.end();
				p.write.begin("zone");
				p.write.writeInt(0x4444444);
				p.write.end();
			p.write.end();		
		p.write.end();
		p.write.begin("zoe");
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"mossake");		
		Assert.assertTrue(p.read.readDouble()==33.3);
		assertNext(p.read,"amber");
		assertNext(p.read,"tonker");
		assertNext(p.read,null);
		assertNext(p.read,"zone");
		assertNext(p.read,null);
		assertNext(p.read,null);						
		p.read.skip();
		assertNext(p.read,"zoe");
		p.read.close();
		leave();
	}   
};	