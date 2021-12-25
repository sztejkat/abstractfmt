package sztejkat.abstractfmt;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Assume;

/**
	A test bed for paired {@link ISignalReadFormat}/{@link ISignalWriteFormat}
	using focused on described stream operations
	<p>
	All tests in this class do require that that tested stream 
	{@link ISignalReadFormat#isDescribed} is true.
*/
public abstract class ATestISignalFormat_Descr extends ATestISignalFormatBase
{
		/** Creates a pair which is to be used for testing purposes by calling
		{@link #create} and then validate if it is described.
		@return created, coupled pair.
		*/
		protected final Pair createDesc()
		{
			final Pair p = create();
			Assume.assumeTrue(p.write.isDescribed());
			Assume.assumeTrue(p.read.isDescribed());
			return p;
		};
		
		
		@Test public void testWhatNext_prmtv()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of primitives and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.writeBoolean(false);
			p.write.writeByte((byte)-7);
			p.write.writeChar('c');
			p.write.writeShort((short)44);
			p.write.writeInt(20394);
			p.write.writeLong(3344909);
			p.write.writeFloat(3.555f);
			p.write.writeDouble(49.5);
			p.write.close();
			
			//we just ask few times to ensure that cursor is not moved.
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN);
			Assert.assertTrue(p.read.readBoolean()==false);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BYTE);
			Assert.assertTrue(p.read.readByte()==(byte)-7);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_CHAR);
			Assert.assertTrue(p.read.readChar()=='c');
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_SHORT);
			Assert.assertTrue(p.read.readShort()==(short)44);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_INT);
			Assert.assertTrue(p.read.readInt()==20394);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_LONG);
			Assert.assertTrue(p.read.readLong()==3344909);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_FLOAT);
			Assert.assertTrue(p.read.readFloat()==3.555f);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_DOUBLE);
			Assert.assertTrue(p.read.readDouble()==49.5);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			leave();
		};
		
		
		
		
		
		
		@Test public void testWhatNext_block_boolean_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeBooleanBlock(new boolean[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readBooleanBlock(new boolean[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readBooleanBlock(new boolean[16],0,16);
				Assert.assertTrue(9+r==16);
				//and after a partial read 
				Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			};
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		@Test public void testWhatNext_block_boolean_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeBooleanBlock(new boolean[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readBooleanBlock(new boolean[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BOOLEAN_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readBooleanBlock(new boolean[16],0,16-9);
				Assert.assertTrue(9+r==16);
				//There had to be NO partial read, but whole block was read 
				Assert.assertTrue(
							" "+p.read.whatNext(),
							p.read.whatNext()==TContentType.SIGNAL
							);
			};
			//However NEXT should skip data block.
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		
		
		
		
		@Test public void testWhatNext_block_byte_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeByteBlock(new byte[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BYTE_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BYTE_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readByteBlock(new byte[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BYTE_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readByteBlock(new byte[16],0,16);
				Assert.assertTrue(9+r==16);
				//and after a partial read 
				Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			};
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		@Test public void testWhatNext_block_byte_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeByteBlock(new byte[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BYTE_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BYTE_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readByteBlock(new byte[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_BYTE_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readByteBlock(new byte[16],0,16-9);
				Assert.assertTrue(9+r==16);
				//There had to be NO partial read, but whole block was read 
				Assert.assertTrue(
							" "+p.read.whatNext(),
							p.read.whatNext()==TContentType.SIGNAL
							);
			};
			//However NEXT should skip data block.
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		
		@Test public void testWhatNext_block_char_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeCharBlock(new char[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_CHAR_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_CHAR_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readCharBlock(new char[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_CHAR_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readCharBlock(new char[16],0,16);
				Assert.assertTrue(9+r==16);
				//and after a partial read 
				Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			};
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		@Test public void testWhatNext_block_char_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeCharBlock(new char[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_CHAR_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_CHAR_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readCharBlock(new char[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_CHAR_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readCharBlock(new char[16],0,16-9);
				Assert.assertTrue(9+r==16);
				//There had to be NO partial read, but whole block was read 
				Assert.assertTrue(
							" "+p.read.whatNext(),
							p.read.whatNext()==TContentType.SIGNAL
							);
			};
			//However NEXT should skip data block.
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		
		@Test public void testWhatNext_block_short_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeShortBlock(new short[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_SHORT_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_SHORT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readShortBlock(new short[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_SHORT_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readShortBlock(new short[16],0,16);
				Assert.assertTrue(9+r==16);
				//and after a partial read 
				Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			};
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		@Test public void testWhatNext_block_short_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeShortBlock(new short[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_SHORT_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_SHORT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readShortBlock(new short[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_SHORT_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readShortBlock(new short[16],0,16-9);
				Assert.assertTrue(9+r==16);
				//There had to be NO partial read, but whole block was read 
				Assert.assertTrue(
							" "+p.read.whatNext(),
							p.read.whatNext()==TContentType.SIGNAL
							);
			};
			//However NEXT should skip data block.
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		@Test public void testWhatNext_block_int_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeIntBlock(new int[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_INT_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_INT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readIntBlock(new int[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_INT_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readIntBlock(new int[16],0,16);
				Assert.assertTrue(9+r==16);
				//and after a partial read 
				Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			};
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		@Test public void testWhatNext_block_int_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeIntBlock(new int[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();			
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_INT_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_INT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readIntBlock(new int[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_INT_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readIntBlock(new int[16],0,16-9);
				Assert.assertTrue(9+r==16);
				//There had to be NO partial read, but whole block was read 
				Assert.assertTrue(
							" "+p.read.whatNext(),
							p.read.whatNext()==TContentType.SIGNAL
							);
			};
			//However NEXT should skip data block.
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		@Test public void testWhatNext_block_long_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeLongBlock(new long[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_LONG_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_LONG_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readLongBlock(new long[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_LONG_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readLongBlock(new long[16],0,16);
				Assert.assertTrue(9+r==16);
				//and after a partial read 
				Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			};
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		@Test public void testWhatNext_block_long_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeLongBlock(new long[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_LONG_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_LONG_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readLongBlock(new long[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_LONG_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readLongBlock(new long[16],0,16-9);
				Assert.assertTrue(9+r==16);
				//There had to be NO partial read, but whole block was read 
				Assert.assertTrue(
							" "+p.read.whatNext(),
							p.read.whatNext()==TContentType.SIGNAL
							);
			};
			//However NEXT should skip data block.
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		@Test public void testWhatNext_block_float_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeFloatBlock(new float[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_FLOAT_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_FLOAT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readFloatBlock(new float[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_FLOAT_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readFloatBlock(new float[16],0,16);
				Assert.assertTrue(9+r==16);
				//and after a partial read 
				Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			};
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		@Test public void testWhatNext_block_float_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeFloatBlock(new float[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_FLOAT_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_FLOAT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readFloatBlock(new float[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_FLOAT_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readFloatBlock(new float[16],0,16-9);
				Assert.assertTrue(9+r==16);
				//There had to be NO partial read, but whole block was read 
				Assert.assertTrue(
							" "+p.read.whatNext(),
							p.read.whatNext()==TContentType.SIGNAL
							);
			};
			//However NEXT should skip data block.
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		@Test public void testWhatNext_block_double_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeDoubleBlock(new double[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_DOUBLE_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_DOUBLE_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readDoubleBlock(new double[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_DOUBLE_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readDoubleBlock(new double[16],0,16);
				Assert.assertTrue(9+r==16);
				//and after a partial read 
				Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			};
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
		
		@Test public void testWhatNext_block_double_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc();
			p.write.open();
			p.write.begin("Angie");
			p.write.writeDoubleBlock(new double[32],0,16);
			p.write.end();
			p.write.close();
			
			p.read.open();
			Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
			Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_DOUBLE_BLOCK);
			Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_DOUBLE_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readDoubleBlock(new double[16],0,9);
				Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				Assert.assertTrue(p.read.whatNext()==TContentType.PRMTV_DOUBLE_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readDoubleBlock(new double[16],0,16-9);
				Assert.assertTrue(9+r==16);
				//There had to be NO partial read, but whole block was read 
				Assert.assertTrue(
							" "+p.read.whatNext(),
							p.read.whatNext()==TContentType.SIGNAL
							);
			};
			//However NEXT should skip data block.
			Assert.assertTrue(p.read.next()==null);
			Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			
			leave();
		};
};