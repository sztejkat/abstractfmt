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
	Tests if format correctly provides
	information about described primitives.
	<p>
	Valid for both described and un-described
	formats.
*/
public class TestWhatNext extends ATestShortOps
{
	/*
		Note: This test case makes use of 
		the assertWhatNext() transparently processing
		described and undescribed requests.
	*/	
	@Test public void detectsEofInEmpty()throws IOException
	{
		/*
			Check if end-of-file is correctly detected
			in empty stream.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	@Test public void detectsSignals()throws IOException
	{
		/*
			Check if signals are correctly detected.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("Aniki");
			p.write.end();
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertNext(p.read,"Aniki");
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertNext(p.read,null);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	@Test public void detectsSignalsStable()throws IOException
	{
		/*
			Check if signals are correctly detected
			and are stable (not moving cursor)
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("Aniki");
			p.write.end();
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertNext(p.read,"Aniki");
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertNext(p.read,null);
			assertWhatNext(p.read,TContentType.EOF);
			assertWhatNext(p.read,TContentType.EOF);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	@Test public void detectsSignalsNested()throws IOException
	{
		/*
			Check if signals are correctly detected.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("Aniki");
			p.write.begin("Honto");
			p.write.end();
			p.write.end();
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertNext(p.read,"Aniki");
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertNext(p.read,"Honto");
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertNext(p.read,null);
			assertWhatNext(p.read,TContentType.SIGNAL);
			assertNext(p.read,null);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	
	@Test public void detectPrimitiveType_boolean()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.writeBoolean(false);
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.PRMTV_BOOLEAN);
			Assert.assertTrue(p.read.readBoolean()==false);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	@Test public void detectPrimitiveType_byte()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.writeByte((byte)-33);
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.PRMTV_BYTE);
			Assert.assertTrue(p.read.readByte()==(byte)-33);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	@Test public void detectPrimitiveType_char()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.writeChar((char)-33);
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.PRMTV_CHAR);
			Assert.assertTrue(p.read.readChar()==(char)-33);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	@Test public void detectPrimitiveType_short()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.writeShort((short)-33);
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.PRMTV_SHORT);
			Assert.assertTrue(p.read.readShort()==(short)-33);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	@Test public void detectPrimitiveType_int()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.writeInt(-33);
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.PRMTV_INT);
			Assert.assertTrue(p.read.readInt()==-33);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	@Test public void detectPrimitiveType_long()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.writeLong((long)-33);
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.PRMTV_LONG);
			Assert.assertTrue(p.read.readLong()==(long)-33);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	@Test public void detectPrimitiveType_float()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.writeFloat((float)-33);
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.PRMTV_FLOAT);
			Assert.assertTrue(p.read.readFloat()==(float)-33);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	@Test public void detectPrimitiveType_double()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.writeDouble((double)-33);
			p.write.close();
			
			p.read.open();
			assertWhatNext(p.read,TContentType.PRMTV_DOUBLE);
			Assert.assertTrue(p.read.readDouble()==(double)-33);
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	
	
	
	@Test public void detectPrimitiveType_boolean_block_fullread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeBooleanBlock(new boolean[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_BOOLEAN_BLOCK);
			Assert.assertTrue(p.read.readBooleanBlock(new boolean[50])==50);
			assertWhatNext(p.read,TContentType.PRMTV_BOOLEAN_BLOCK);
			Assert.assertTrue(p.read.readBooleanBlock(new boolean[50])==50);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	@Test public void detectPrimitiveType_boolean_block_partialread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeBooleanBlock(new boolean[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_BOOLEAN_BLOCK);
			Assert.assertTrue(p.read.readBooleanBlock(new boolean[150])==100);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	@Test public void detectPrimitiveType_byte_block_fullread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeByteBlock(new byte[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_BYTE_BLOCK);
			Assert.assertTrue(p.read.readByteBlock(new byte[50])==50);
			assertWhatNext(p.read,TContentType.PRMTV_BYTE_BLOCK);
			Assert.assertTrue(p.read.readByteBlock(new byte[50])==50);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	@Test public void detectPrimitiveType_byte_block_partialread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeByteBlock(new byte[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_BYTE_BLOCK);
			Assert.assertTrue(p.read.readByteBlock(new byte[150])==100);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	@Test public void detectPrimitiveType_char_block_fullread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeCharBlock(new char[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_CHAR_BLOCK);
			Assert.assertTrue(p.read.readCharBlock(new char[50])==50);
			assertWhatNext(p.read,TContentType.PRMTV_CHAR_BLOCK);
			Assert.assertTrue(p.read.readCharBlock(new char[50])==50);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	@Test public void detectPrimitiveType_char_block_partialread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeCharBlock(new char[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_CHAR_BLOCK);
			Assert.assertTrue(p.read.readCharBlock(new char[150])==100);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	@Test public void detectPrimitiveType_short_block_fullread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeShortBlock(new short[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_SHORT_BLOCK);
			Assert.assertTrue(p.read.readShortBlock(new short[50])==50);
			assertWhatNext(p.read,TContentType.PRMTV_SHORT_BLOCK);
			Assert.assertTrue(p.read.readShortBlock(new short[50])==50);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	@Test public void detectPrimitiveType_short_block_partialread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeShortBlock(new short[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_SHORT_BLOCK);
			Assert.assertTrue(p.read.readShortBlock(new short[150])==100);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	@Test public void detectPrimitiveType_int_block_fullread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeIntBlock(new int[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_INT_BLOCK);
			Assert.assertTrue(p.read.readIntBlock(new int[50])==50);
			assertWhatNext(p.read,TContentType.PRMTV_INT_BLOCK);
			Assert.assertTrue(p.read.readIntBlock(new int[50])==50);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	@Test public void detectPrimitiveType_int_block_partialread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeIntBlock(new int[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_INT_BLOCK);
			Assert.assertTrue(p.read.readIntBlock(new int[150])==100);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	@Test public void detectPrimitiveType_long_block_fullread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeLongBlock(new long[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_LONG_BLOCK);
			Assert.assertTrue(p.read.readLongBlock(new long[50])==50);
			assertWhatNext(p.read,TContentType.PRMTV_LONG_BLOCK);
			Assert.assertTrue(p.read.readLongBlock(new long[50])==50);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	@Test public void detectPrimitiveType_long_block_partialread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeLongBlock(new long[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_LONG_BLOCK);
			Assert.assertTrue(p.read.readLongBlock(new long[150])==100);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	@Test public void detectPrimitiveType_float_block_fullread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeFloatBlock(new float[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
			Assert.assertTrue(p.read.readFloatBlock(new float[50])==50);
			assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
			Assert.assertTrue(p.read.readFloatBlock(new float[50])==50);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	@Test public void detectPrimitiveType_float_block_partialread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeFloatBlock(new float[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_FLOAT_BLOCK);
			Assert.assertTrue(p.read.readFloatBlock(new float[150])==100);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	@Test public void detectPrimitiveType_double_block_fullread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeDoubleBlock(new double[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_DOUBLE_BLOCK);
			Assert.assertTrue(p.read.readDoubleBlock(new double[50])==50);
			assertWhatNext(p.read,TContentType.PRMTV_DOUBLE_BLOCK);
			Assert.assertTrue(p.read.readDoubleBlock(new double[50])==50);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
	@Test public void detectPrimitiveType_double_block_partialread()throws IOException
	{
		/*
			Check if primitive types are correctly detected
			
			Note: this test is valid for both described and un-described.
		*/
		enter();
			Pair p = create();
			p.write.open();
			p.write.begin("x");
			p.write.writeDoubleBlock(new double[100]);
			p.write.end();
			p.write.close();
			
			p.read.open();
			p.read.next();
			assertWhatNext(p.read,TContentType.PRMTV_DOUBLE_BLOCK);
			Assert.assertTrue(p.read.readDoubleBlock(new double[150])==100);
			assertWhatNext(p.read,TContentType.SIGNAL);
			p.read.next();
			assertWhatNext(p.read,TContentType.EOF);
			p.read.close();
		leave();
	};
}