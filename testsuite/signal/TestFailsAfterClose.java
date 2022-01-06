package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.ISignalWriteFormat;
import sztejkat.abstractfmt.TContentType;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EClosed;
import sztejkat.abstractfmt.EClosed;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;

/**
	An elementary test which checks if we some operations
	fail after closing a stream
*/
public class TestFailsAfterClose extends ASignalTest
{
	
	
	@Test public void failsFlushAfterClose()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{			
				p.write.flush(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsBeginAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.begin("a",false); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsBeginAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.begin("a"); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsEndAfterClose()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.end(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	
	
	
	
	@Test public void failsWriteBooleanAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeBoolean(true); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsWriteBooleanAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeBooleanBlock(new boolean[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteByteAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeByte((byte)9); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsWriteByteAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeByteBlock(new byte[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteCharAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeChar('x'); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsWriteCharAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeCharBlock(new char[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteShortAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeShort((short)0); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsWriteShortAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeShortBlock(new short[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteIntAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeInt(0); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsWriteIntAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeIntBlock(new int[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteLongAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeLong(0); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsWriteLongAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeLongBlock(new long[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteFloatAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeFloat(0); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsWriteFloatAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeFloatBlock(new float[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteDoubleAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeDouble(0); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsWriteDoubleAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			try{
				p.write.writeDoubleBlock(new double[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	
	
	
	@Test public void failsNextAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.next(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsWhatNextAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.whatNext(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	@Test public void failsReadBooleanAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readBoolean(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsReadBooleanAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readBooleanBlock(new boolean[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsReadByteAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readByte(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsReadByteAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readByteBlock(new byte[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsReadCharAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readChar(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsReadCharAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readCharBlock(new char[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsReadShortAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readShort(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsReadShortAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readShortBlock(new short[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsReadIntAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readInt(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsReadIntAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readIntBlock(new int[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsReadLongAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readLong(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsReadLongAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readLongBlock(new long[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsReadFloatAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readFloat(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsReadFloatAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readFloatBlock(new float[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	
	
	@Test public void failsReadDoubleAfterClose_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readDouble(); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
	@Test public void failsReadDoubleAfterClose_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			p.read.open();
			p.read.close();
			try{
				p.read.readDoubleBlock(new double[3]); Assert.fail();
			}catch(EClosed ex){}
			
		leave();
	};
};