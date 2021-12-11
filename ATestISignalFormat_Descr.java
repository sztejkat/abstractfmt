package sztejkat.abstractfmt;
import java.io.IOException;

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
			
		@param max_name_length as declared in {@link ASignalWriteFormat#ASignalWriteFormat(int,int,int)}
		@param max_events_recursion_depth --//--
		@return created, coupled pair.
		*/
		protected final Pair createDesc(int max_name_length, int max_events_recursion_depth)
		{
			final Pair p = create(max_name_length,max_events_recursion_depth);
			org.junit.Assert.assertTrue(p.write.isDescribed());
			org.junit.Assert.assertTrue(p.read.isDescribed());
			return p;
		};
		
		
		@org.junit.Test public void testWhatNext_prmtv()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of primitives and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
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
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN);
			org.junit.Assert.assertTrue(p.read.readBoolean()==false);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BYTE);
			org.junit.Assert.assertTrue(p.read.readByte()==(byte)-7);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_CHAR);
			org.junit.Assert.assertTrue(p.read.readChar()=='c');
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_SHORT);
			org.junit.Assert.assertTrue(p.read.readShort()==(short)44);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_INT);
			org.junit.Assert.assertTrue(p.read.readInt()==20394);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_LONG);
			org.junit.Assert.assertTrue(p.read.readLong()==3344909);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_FLOAT);
			org.junit.Assert.assertTrue(p.read.readFloat()==3.555f);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_DOUBLE);
			org.junit.Assert.assertTrue(p.read.readDouble()==49.5);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			leave();
		};
		
		
		
		
		
		
		@org.junit.Test public void testWhatNext_block_boolean_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeBooleanBlock(new boolean[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readBooleanBlock(new boolean[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readBooleanBlock(new boolean[16],0,16);
				org.junit.Assert.assertTrue(9+r==16);
				//and after a partial read 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			};
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		@org.junit.Test public void testWhatNext_block_boolean_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeBooleanBlock(new boolean[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readBooleanBlock(new boolean[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readBooleanBlock(new boolean[16],0,16-9);
				org.junit.Assert.assertTrue(9+r==16);
				//There had to be NO partial read. 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN_BLOCK);
			};
			//However NEXT should skip data block.
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		
		
		
		
		@org.junit.Test public void testWhatNext_block_byte_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeByteBlock(new byte[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BYTE_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BYTE_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readByteBlock(new byte[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BYTE_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readByteBlock(new byte[16],0,16);
				org.junit.Assert.assertTrue(9+r==16);
				//and after a partial read 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			};
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		@org.junit.Test public void testWhatNext_block_byte_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeByteBlock(new byte[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BYTE_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BYTE_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readByteBlock(new byte[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BYTE_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readByteBlock(new byte[16],0,16-9);
				org.junit.Assert.assertTrue(9+r==16);
				//There had to be NO partial read. 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_BYTE_BLOCK);
			};
			//However NEXT should skip data block.
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		
		@org.junit.Test public void testWhatNext_block_char_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeCharBlock(new char[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_CHAR_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_CHAR_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readCharBlock(new char[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_CHAR_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readCharBlock(new char[16],0,16);
				org.junit.Assert.assertTrue(9+r==16);
				//and after a partial read 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			};
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		@org.junit.Test public void testWhatNext_block_char_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeCharBlock(new char[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_CHAR_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_CHAR_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readCharBlock(new char[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_CHAR_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readCharBlock(new char[16],0,16-9);
				org.junit.Assert.assertTrue(9+r==16);
				//There had to be NO partial read. 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_CHAR_BLOCK);
			};
			//However NEXT should skip data block.
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		
		@org.junit.Test public void testWhatNext_block_short_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeShortBlock(new short[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_SHORT_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_SHORT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readShortBlock(new short[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_SHORT_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readShortBlock(new short[16],0,16);
				org.junit.Assert.assertTrue(9+r==16);
				//and after a partial read 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			};
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		@org.junit.Test public void testWhatNext_block_short_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeShortBlock(new short[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_SHORT_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_SHORT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readShortBlock(new short[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_SHORT_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readShortBlock(new short[16],0,16-9);
				org.junit.Assert.assertTrue(9+r==16);
				//There had to be NO partial read. 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_SHORT_BLOCK);
			};
			//However NEXT should skip data block.
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		@org.junit.Test public void testWhatNext_block_int_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeIntBlock(new int[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_INT_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_INT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readIntBlock(new int[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_INT_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readIntBlock(new int[16],0,16);
				org.junit.Assert.assertTrue(9+r==16);
				//and after a partial read 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			};
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		@org.junit.Test public void testWhatNext_block_int_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeIntBlock(new int[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_INT_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_INT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readIntBlock(new int[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_INT_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readIntBlock(new int[16],0,16-9);
				org.junit.Assert.assertTrue(9+r==16);
				//There had to be NO partial read. 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_INT_BLOCK);
			};
			//However NEXT should skip data block.
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		@org.junit.Test public void testWhatNext_block_long_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeLongBlock(new long[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_LONG_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_LONG_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readLongBlock(new long[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_LONG_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readLongBlock(new long[16],0,16);
				org.junit.Assert.assertTrue(9+r==16);
				//and after a partial read 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			};
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		@org.junit.Test public void testWhatNext_block_long_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeLongBlock(new long[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_LONG_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_LONG_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readLongBlock(new long[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_LONG_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readLongBlock(new long[16],0,16-9);
				org.junit.Assert.assertTrue(9+r==16);
				//There had to be NO partial read. 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_LONG_BLOCK);
			};
			//However NEXT should skip data block.
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		@org.junit.Test public void testWhatNext_block_float_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeFloatBlock(new float[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readFloatBlock(new float[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readFloatBlock(new float[16],0,16);
				org.junit.Assert.assertTrue(9+r==16);
				//and after a partial read 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			};
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		@org.junit.Test public void testWhatNext_block_float_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeFloatBlock(new float[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readFloatBlock(new float[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readFloatBlock(new float[16],0,16-9);
				org.junit.Assert.assertTrue(9+r==16);
				//There had to be NO partial read. 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_FLOAT_BLOCK);
			};
			//However NEXT should skip data block.
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		
		
		
		
		
		
		
		@org.junit.Test public void testWhatNext_block_double_1()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeDoubleBlock(new double[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_DOUBLE_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_DOUBLE_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readDoubleBlock(new double[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_DOUBLE_BLOCK);
				//Now we should read array to the end, triggering partial read.
				r = p.read.readDoubleBlock(new double[16],0,16);
				org.junit.Assert.assertTrue(9+r==16);
				//and after a partial read 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			};
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
		
		@org.junit.Test public void testWhatNext_block_double_1a()throws IOException
		{
			enter();
			/*
				In this test we do write a bunch of blocks and test
				if whatNext() returns proper type information.
			*/
			Pair p = createDesc(8,8);
			p.write.begin("Angie");
			p.write.writeDoubleBlock(new double[32],0,16);
			p.write.end();
			p.write.close();
			
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue("Angie".equals(p.read.next()));
			//we just ask few times to ensure that cursor is not moved.
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_DOUBLE_BLOCK);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_DOUBLE_BLOCK);
			//Now read a bit of this array
			{
				int r = p.read.readDoubleBlock(new double[16],0,9);
				org.junit.Assert.assertTrue(r==9);
				//The information about safe call to array should be retained.
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_DOUBLE_BLOCK);
				//Now we should read array to the end, but NOT triggering partial read.
				r = p.read.readDoubleBlock(new double[16],0,16-9);
				org.junit.Assert.assertTrue(9+r==16);
				//There had to be NO partial read. 
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.PRMTV_DOUBLE_BLOCK);
			};
			//However NEXT should skip data block.
			org.junit.Assert.assertTrue(p.read.next()==null);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			
			leave();
		};
};