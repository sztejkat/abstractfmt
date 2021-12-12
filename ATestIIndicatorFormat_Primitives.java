package sztejkat.abstractfmt;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
		A test framework focused on primitives related operations
*/
public abstract class ATestIIndicatorFormat_Primitives extends ATestIIndicatorFormatBase
{

	@Test public void testTypesAndFlushes()throws IOException
	{
		enter();
		/*	Check if we can read primitives and detect indicators.
		*/
		Pair p = create(8,0);
		p.write.writeType(TIndicator.TYPE_BOOLEAN);
		p.write.writeBoolean(false);
		p.write.writeFlush(TIndicator.FLUSH_BOOLEAN);
		
		p.write.writeType(TIndicator.TYPE_BYTE);
		p.write.writeByte((byte)1);
		p.write.writeFlush(TIndicator.FLUSH_BYTE);
		
		p.write.writeType(TIndicator.TYPE_CHAR);
		p.write.writeChar('c');
		p.write.writeFlush(TIndicator.FLUSH_CHAR);
		
		
		p.write.writeType(TIndicator.TYPE_SHORT);
		p.write.writeShort((short)8888);
		p.write.writeFlush(TIndicator.FLUSH_SHORT);
		
		p.write.writeType(TIndicator.TYPE_INT);
		p.write.writeInt(34445544);
		p.write.writeFlush(TIndicator.FLUSH_INT);
		
		p.write.writeType(TIndicator.TYPE_LONG);
		p.write.writeLong(135245525252454L);
		p.write.writeFlush(TIndicator.FLUSH_LONG);
		
		p.write.writeType(TIndicator.TYPE_FLOAT);
		p.write.writeFloat(1.33f);
		p.write.writeFlush(TIndicator.FLUSH_FLOAT);
		
		p.write.writeType(TIndicator.TYPE_DOUBLE);
		p.write.writeDouble(-2.4090459E3);
		p.write.writeFlush(TIndicator.FLUSH_DOUBLE);
		
		p.write.flush();		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_BOOLEAN);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readBoolean()==false);
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_BYTE);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readByte()==(byte)1);
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_CHAR);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readChar()=='c');
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_SHORT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readShort()==(short)8888);
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_INT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readInt()==34445544);
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_LONG);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readLong()==135245525252454L);
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_FLOAT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readFloat()==1.33f);
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_DOUBLE);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readDouble()==-2.4090459E3);
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
			
		leave();
	};	
	
	
	
	
	
	
	
	
	@Test public void testEUnexpectedEof_boolean()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeBoolean(false);
		p.write.flush();
		
		p.read.readBoolean();
		try{
			p.read.readBoolean();
			Assert.fail();
		}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testEUnexpectedEof_byte()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeByte((byte)0);
		p.write.flush();
		
		p.read.readByte();
		try{
			p.read.readByte();
			Assert.fail();
		}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testEUnexpectedEof_short()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeShort((short)0);
		p.write.flush();
		
		p.read.readShort();
		try{
			p.read.readShort();
			Assert.fail();
		}catch(EUnexpectedEof ex){};
		
		leave();		
	};
	@Test public void testEUnexpectedEof_char()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeChar('v');
		p.write.flush();
		
		p.read.readChar();
		try{
			p.read.readChar();
			Assert.fail();
		}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testEUnexpectedEof_int()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeInt(0);
		p.write.flush();
		
		p.read.readInt();
		try{
			p.read.readInt();
			Assert.fail();
		}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testEUnexpectedEof_long()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeLong(0);
		p.write.flush();
		
		p.read.readLong();
		try{
			p.read.readLong();
			Assert.fail();
		}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testEUnexpectedEof_float()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeFloat(0);
		p.write.flush();
		
		p.read.readFloat();
		try{
			p.read.readFloat();
			Assert.fail();
		}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	@Test public void testEUnexpectedEof_double()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeDouble(0);
		p.write.flush();
		
		p.read.readDouble();
		try{
			p.read.readDouble();
			Assert.fail();
		}catch(EUnexpectedEof ex){};
		
		leave();
	};
	
	
	
	
	
	
	
	
	
	@Test public void testENoMoreData_boolean()throws IOException
	{
		enter();
		/* Test if layer properly report no more data if there is a signal */
		Pair p = create(8,0);
		p.write.writeBoolean(false);
		p.write.writeBeginDirect("A");p.write.flush();
		
		p.read.readBoolean();
		try{
			p.read.readBoolean();
			Assert.fail();
		}catch(ENoMoreData ex){};
		
		leave();
	};
	
	@Test public void testENoMoreData_byte()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeByte((byte)0);
		p.write.writeBeginDirect("A");p.write.flush();
		
		p.read.readByte();
		try{
			p.read.readByte();
			Assert.fail();
		}catch(ENoMoreData ex){};
		
		leave();
	};
	
	@Test public void testENoMoreData_short()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeShort((short)0);
		p.write.writeBeginDirect("A");p.write.flush();
		
		p.read.readShort();
		try{
			p.read.readShort();
			Assert.fail();
		}catch(ENoMoreData ex){};
		
		leave();		
	};
	@Test public void testENoMoreData_char()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeChar('v');
		p.write.writeBeginDirect("A");p.write.flush();
		
		p.read.readChar();
		try{
			p.read.readChar();
			Assert.fail();
		}catch(ENoMoreData ex){};
		
		leave();
	};
	
	@Test public void testENoMoreData_int()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeInt(0);
		p.write.writeBeginDirect("A");p.write.flush();
		
		p.read.readInt();
		try{
			p.read.readInt();
			Assert.fail();
		}catch(ENoMoreData ex){};
		
		leave();
	};
	
	@Test public void testENoMoreData_long()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeLong(0);
		p.write.writeBeginDirect("A");p.write.flush();
		
		p.read.readLong();
		try{
			p.read.readLong();
			Assert.fail();
		}catch(ENoMoreData ex){};
		
		leave();
	};
	
	@Test public void testENoMoreData_float()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeFloat(0);
		p.write.writeBeginDirect("A");p.write.flush();
		
		p.read.readFloat();
		try{
			p.read.readFloat();
			Assert.fail();
		}catch(ENoMoreData ex){};
		
		leave();
	};
	
	@Test public void testENoMoreData_double()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data */
		Pair p = create(8,0);
		p.write.writeDouble(0);
		p.write.writeBeginDirect("A");p.write.flush();
		
		p.read.readDouble();
		try{
			p.read.readDouble();
			Assert.fail();
		}catch(ENoMoreData ex){};
		
		leave();
	};
}