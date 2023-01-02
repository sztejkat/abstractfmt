package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.ITypedStructReadFormat;
import sztejkat.abstractfmt.ITypedStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
import static sztejkat.abstractfmt.ITypedStructReadFormat.TElement;

/**
	A peek() test case for 
	{@link ITypedStructReadFormat}/{@link ITypedStructWriteFormat}
	testing if everything is peek()'ed correctly when seen
*/
public class ATestCase_PeekOperations extends AInterOpTestCase<ITypedStructReadFormat,ITypedStructWriteFormat>
{
	@Test public void testCanPeekEof()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.close();			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.EOF);
			p.reader.close();
		leave();
	};
	@Test public void testCanPeekBoolean()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeBoolean(true);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN);
			p.reader.close();
		leave();
	};
	@Test public void testCanPeekByte()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeByte((byte)0);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.BYTE);
			p.reader.close();
		leave();
	};
	@Test public void testCanPeekShort()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeShort((short)0);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SHORT);
			p.reader.close();
		leave();
	};
	@Test public void testCanPeekChar()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeChar((char)0);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.CHAR);
			p.reader.close();
		leave();
	};
	@Test public void testCanPeekInt()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeInt(0);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.INT);
			p.reader.close();
		leave();
	};
	@Test public void testCanPeekLong()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeLong((long)0);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.LONG);
			p.reader.close();
		leave();
	};
	@Test public void testCanPeekFloat()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeFloat((float)0);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT);
			p.reader.close();
		leave();
	};
	@Test public void testCanPeekDouble()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeDouble((double)0);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE);
			p.reader.close();
		leave();
	};
	
	
	@Test public void testCanPeekBooleanBlock()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Note: blocks should be enclosed in own begin-end, but it is NOT
			//absolutely necessary. Since we are not reading anything we just
			//dump it.
			p.writer.writeBooleanBlock(new boolean[13]);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN_BLK);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekBooleanBlock_cont()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Check if we can peek block during read and at the end.
			p.writer.begin("a");
			p.writer.writeBooleanBlock(new boolean[13]);
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN_BLK);
			Assert.assertTrue(p.reader.readBooleanBlock(new boolean[2])==2);
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN_BLK);
			Assert.assertTrue(p.reader.readBooleanBlock(new boolean[14])==13-2);
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN_BLK);
			Assert.assertTrue(p.reader.next()==null);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekByteBlock()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Note: blocks should be enclosed in own begin-end, but it is NOT
			//absolutely necessary. Since we are not reading anything we just
			//dump it.
			p.writer.writeByteBlock(new byte[13]);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.BYTE_BLK);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekByteBlock_cont()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Check if we can peek block during read and at the end.
			p.writer.begin("a");
			p.writer.writeByteBlock(new byte[13]);
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.BYTE_BLK);
			Assert.assertTrue(p.reader.readByteBlock(new byte[2])==2);
			Assert.assertTrue(p.reader.peek()==TElement.BYTE_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.BYTE_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.BYTE_BLK);
			Assert.assertTrue(p.reader.readByteBlock(new byte[14])==13-2);
			Assert.assertTrue(p.reader.peek()==TElement.BYTE_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.BYTE_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.BYTE_BLK);
			Assert.assertTrue(p.reader.next()==null);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekCharBlock()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Note: blocks should be enclosed in own begin-end, but it is NOT
			//absolutely necessary. Since we are not reading anything we just
			//dump it.
			p.writer.writeCharBlock(new char[13]);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.CHAR_BLK);
			p.reader.close();
		leave();
	};
	
	
	@Test public void testCanPeekCharBlock_cont()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Check if we can peek block during read and at the end.
			p.writer.begin("a");
			p.writer.writeCharBlock(new char[13]);
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.CHAR_BLK);
			Assert.assertTrue(p.reader.readCharBlock(new char[2])==2);
			Assert.assertTrue(p.reader.peek()==TElement.CHAR_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.CHAR_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.CHAR_BLK);
			Assert.assertTrue(p.reader.readCharBlock(new char[14])==13-2);
			Assert.assertTrue(p.reader.peek()==TElement.CHAR_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.CHAR_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.CHAR_BLK);
			Assert.assertTrue(p.reader.next()==null);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekShortBlock()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Note: blocks should be enclosed in own begin-end, but it is NOT
			//absolutely necessary. Since we are not reading anything we just
			//dump it.
			p.writer.writeShortBlock(new short[13]);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SHORT_BLK);
			p.reader.close();
		leave();
	};
	
	
	@Test public void testCanPeekShortBlock_cont()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Check if we can peek block during read and at the end.
			p.writer.begin("a");
			p.writer.writeShortBlock(new short[13]);
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.SHORT_BLK);
			Assert.assertTrue(p.reader.readShortBlock(new short[2])==2);
			Assert.assertTrue(p.reader.peek()==TElement.SHORT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.SHORT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.SHORT_BLK);
			Assert.assertTrue(p.reader.readShortBlock(new short[14])==13-2);
			Assert.assertTrue(p.reader.peek()==TElement.SHORT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.SHORT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.SHORT_BLK);
			Assert.assertTrue(p.reader.next()==null);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekIntBlock()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Note: blocks should be enclosed in own begin-end, but it is NOT
			//absolutely necessary. Since we are not reading anything we just
			//dump it.
			p.writer.writeIntBlock(new int[13]);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.INT_BLK);
			p.reader.close();
		leave();
	};
	
	
	@Test public void testCanPeekIntBlock_cont()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Check if we can peek block during read and at the end.
			p.writer.begin("a");
			p.writer.writeIntBlock(new int[13]);
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.INT_BLK);
			Assert.assertTrue(p.reader.readIntBlock(new int[2])==2);
			Assert.assertTrue(p.reader.peek()==TElement.INT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.INT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.INT_BLK);
			Assert.assertTrue(p.reader.readIntBlock(new int[14])==13-2);
			Assert.assertTrue(p.reader.peek()==TElement.INT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.INT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.INT_BLK);
			Assert.assertTrue(p.reader.next()==null);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekLongBlock()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Note: blocks should be enclosed in own begin-end, but it is NOT
			//absolutely necessary. Since we are not reading anything we just
			//dump it.
			p.writer.writeLongBlock(new long[13]);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.LONG_BLK);
			p.reader.close();
		leave();
	};
	
	
	@Test public void testCanPeekLongBlock_cont()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Check if we can peek block during read and at the end.
			p.writer.begin("a");
			p.writer.writeLongBlock(new long[13]);
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.LONG_BLK);
			Assert.assertTrue(p.reader.readLongBlock(new long[2])==2);
			Assert.assertTrue(p.reader.peek()==TElement.LONG_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.LONG_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.LONG_BLK);
			Assert.assertTrue(p.reader.readLongBlock(new long[14])==13-2);
			Assert.assertTrue(p.reader.peek()==TElement.LONG_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.LONG_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.LONG_BLK);
			Assert.assertTrue(p.reader.next()==null);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekFloatBlock()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Note: blocks should be enclosed in own begin-end, but it is NOT
			//absolutely necessary. Since we are not reading anything we just
			//dump it.
			p.writer.writeFloatBlock(new float[13]);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT_BLK);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekFloatBlock_cont()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Check if we can peek block during read and at the end.
			p.writer.begin("a");
			p.writer.writeFloatBlock(new float[13]);
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT_BLK);
			Assert.assertTrue(p.reader.readFloatBlock(new float[2])==2);
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT_BLK);
			Assert.assertTrue(p.reader.readFloatBlock(new float[14])==13-2);
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT_BLK);
			Assert.assertTrue(p.reader.next()==null);
			p.reader.close();
		leave();
	};
	
	
	@Test public void testCanPeekDoubleBlock()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Note: blocks should be enclosed in own begin-end, but it is NOT
			//absolutely necessary. Since we are not reading anything we just
			//dump it.
			p.writer.writeDoubleBlock(new double[13]);
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE_BLK);
			p.reader.close();
		leave();
	};
	
	
	@Test public void testCanPeekDoubleBlock_cont()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Check if we can peek block during read and at the end.
			p.writer.begin("a");
			p.writer.writeDoubleBlock(new double[13]);
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE_BLK);
			Assert.assertTrue(p.reader.readDoubleBlock(new double[2])==2);
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE_BLK);
			Assert.assertTrue(p.reader.readDoubleBlock(new double[14])==13-2);
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE_BLK);
			Assert.assertTrue(p.reader.next()==null);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekStringBlock()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Note: blocks should be enclosed in own begin-end, but it is NOT
			//absolutely necessary. Since we are not reading anything we just
			//dump it.
			p.writer.writeString("martiny");
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.STRING_BLK);
			p.reader.close();
		leave();
	};
	
	
	@Test public void testCanPeekStringBlock_cont()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			//Check if we can peek block during read and at the end.
			p.writer.begin("a");
			p.writer.writeString("12345678");
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.STRING_BLK);
			Assert.assertTrue(p.reader.readString(new StringBuilder(),2)==2);
			Assert.assertTrue(p.reader.peek()==TElement.STRING_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.STRING_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.STRING_BLK);
			Assert.assertTrue(p.reader.readString(new StringBuilder(),100)==8-2);
			Assert.assertTrue(p.reader.peek()==TElement.STRING_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.STRING_BLK);
			Assert.assertTrue(p.reader.peek()==TElement.STRING_BLK);
			Assert.assertTrue(p.reader.next()==null);
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekSignals()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.begin("a");	
			p.writer.end();
			p.writer.begin("c"); //this may be NOT a subject of end-begin optimization
				p.writer.begin("d"); //this may be subject of end-begin optimization
				p.writer.end();
			p.writer.end();
			
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue(p.reader.next()==null);
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("c".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("d".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue(p.reader.next()==null);
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue(p.reader.next()==null);
			Assert.assertTrue(p.reader.peek()==TElement.EOF);
			
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekSignals_Mixed_With_data()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.begin("a");	
			p.writer.writeInt(0);
			p.writer.end();
			
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.INT);
			p.reader.readInt(); //fetch content
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue(p.reader.next()==null);
			Assert.assertTrue(p.reader.peek()==TElement.EOF);
			
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekSignals_Mixed_With_data_next()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.begin("a");	
			p.writer.writeInt(0);
			p.writer.end();
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.INT);
			Assert.assertTrue(p.reader.next()==null);//skip content
			Assert.assertTrue(p.reader.peek()==TElement.EOF);
			
			p.reader.close();
		leave();
	};
	
	@Test public void testCanPeekSignals_Mixed_With_data_next_and_data()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.begin("a");	
			p.writer.writeInt(0);
			p.writer.end();
			p.writer.writeChar('c');
			p.writer.close();
			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.SIG);
			Assert.assertTrue("a".equals(p.reader.next()));
			Assert.assertTrue(p.reader.peek()==TElement.INT);
			Assert.assertTrue(p.reader.next()==null);//skip content
			Assert.assertTrue(p.reader.peek()==TElement.CHAR);
			
			p.reader.close();
		leave();
	};
	
	
	@Test public void testCanTypeChangeDuringRead()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeBoolean(true);
			p.writer.writeByte((byte)0);
			p.writer.writeChar((char)0);
			p.writer.writeShort((short)0);
			p.writer.writeInt(0);
			p.writer.writeLong(0);
			p.writer.writeFloat(0);
			p.writer.writeDouble(0);
			p.writer.close();			
			
			p.reader.open();
			Assert.assertTrue(p.reader.peek()==TElement.BOOLEAN);
			p.reader.readBoolean();
			Assert.assertTrue(p.reader.peek()==TElement.BYTE);
			p.reader.readByte();
			Assert.assertTrue(p.reader.peek()==TElement.CHAR);
			p.reader.readChar();
			Assert.assertTrue(p.reader.peek()==TElement.SHORT);
			p.reader.readShort();
			Assert.assertTrue(p.reader.peek()==TElement.INT);
			p.reader.readInt();
			Assert.assertTrue(p.reader.peek()==TElement.LONG);
			p.reader.readLong();
			Assert.assertTrue(p.reader.peek()==TElement.FLOAT);
			p.reader.readFloat();
			Assert.assertTrue(p.reader.peek()==TElement.DOUBLE);
			p.reader.readDouble();
			p.reader.close();
		leave();
	};
};