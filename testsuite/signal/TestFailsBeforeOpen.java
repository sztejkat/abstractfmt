package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.ISignalWriteFormat;
import sztejkat.abstractfmt.TContentType;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EClosed;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;

/**
	An elementary test which checks if we some operations
	fail before opening a stream
*/
public class TestFailsBeforeOpen extends ASignalTest
{
	
	
	@Test public void failsFlushBeforeOpen()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.flush(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsBeginBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.begin("a",false); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsBeginBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.begin("a"); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsEndBeforeOpen()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.end(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	
	
	
	
	@Test public void failsWriteBooleanBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeBoolean(true); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsWriteBooleanBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeBooleanBlock(new boolean[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteByteBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeByte((byte)9); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsWriteByteBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeByteBlock(new byte[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteCharBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeChar('x'); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsWriteCharBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeCharBlock(new char[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteShortBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeShort((short)0); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsWriteShortBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeShortBlock(new short[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteIntBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeInt(0); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsWriteIntBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeIntBlock(new int[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteLongBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeLong(0); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsWriteLongBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeLongBlock(new long[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteFloatBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeFloat(0); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsWriteFloatBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeFloatBlock(new float[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsWriteDoubleBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeDouble(0); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsWriteDoubleBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.write.writeDoubleBlock(new double[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	
	
	
	@Test public void failsNextBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.next(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsWhatNextBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.whatNext(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	@Test public void failsReadBooleanBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readBoolean(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsReadBooleanBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readBooleanBlock(new boolean[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsReadByteBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readByte(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsReadByteBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readByteBlock(new byte[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsReadCharBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readChar(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsReadCharBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readCharBlock(new char[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsReadShortBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readShort(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsReadShortBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readShortBlock(new short[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsReadIntBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readInt(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsReadIntBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readIntBlock(new int[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsReadLongBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readLong(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsReadLongBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readLongBlock(new long[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsReadFloatBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readFloat(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsReadFloatBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readFloatBlock(new float[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	
	
	@Test public void failsReadDoubleBeforeOpen_1()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readDouble(); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
	@Test public void failsReadDoubleBeforeOpen_2()throws IOException
	{
		/*
			We check if certain method fails before opening.
		*/
		enter();
			Pair p = create();
			
			try{
				p.read.readDoubleBlock(new double[3]); Assert.fail();
			}catch(ENotOpen ex){}
			
		leave();
	};
};