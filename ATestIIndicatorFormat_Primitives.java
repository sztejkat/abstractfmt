package sztejkat.abstractfmt;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
/**
		A test framework focused on primitives related operations
*/
public abstract class ATestIIndicatorFormat_Primitives extends ATestIIndicatorFormatBase
{
	private void writeTypesAndFlushes(IIndicatorWriteFormat w)throws IOException
	{
		w.writeType(TIndicator.TYPE_BOOLEAN);
		w.writeBoolean(false);
		w.writeFlush(TIndicator.FLUSH_BOOLEAN);
		
		w.writeType(TIndicator.TYPE_BYTE);
		w.writeByte((byte)1);
		w.writeFlush(TIndicator.FLUSH_BYTE);
		
		w.writeType(TIndicator.TYPE_CHAR);
		w.writeChar('c');
		w.writeFlush(TIndicator.FLUSH_CHAR);
			
		w.writeType(TIndicator.TYPE_SHORT);
		w.writeShort((short)8888);
		w.writeFlush(TIndicator.FLUSH_SHORT);
		
		w.writeType(TIndicator.TYPE_INT);
		w.writeInt(34445544);
		w.writeFlush(TIndicator.FLUSH_INT);
		
		w.writeType(TIndicator.TYPE_LONG);
		w.writeLong(135245525252454L);
		w.writeFlush(TIndicator.FLUSH_LONG);
		
		w.writeType(TIndicator.TYPE_FLOAT);
		w.writeFloat(1.33f);
		w.writeFlush(TIndicator.FLUSH_FLOAT);
		
		w.writeType(TIndicator.TYPE_DOUBLE);
		w.writeDouble(-2.4090459E3);
		w.writeFlush(TIndicator.FLUSH_DOUBLE);
	};
	@Test public void testTypesAndFlushes_Df()throws IOException
	{
		enter();
		/*	Check if we can read primitives and detect indicators.
		*/
		Pair p = create();
		Assume.assumeTrue("must be described",p.write.isDescribed());
		Assume.assumeTrue("must be flushing",p.write.isFlushing());
		writeTypesAndFlushes(p.write);		
		p.write.close();		
		
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
	
	
	
	@Test public void testTypesAndFlushes_Dnf()throws IOException
	{
		enter();
		/*	Check if we can read primitives and detect indicators.
		*/
		Pair p = create();
		Assume.assumeTrue("must be described",p.write.isDescribed());
		Assume.assumeTrue("must be not flushing",!p.write.isFlushing());
		writeTypesAndFlushes(p.write);		
		p.write.close();		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_BOOLEAN);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readBoolean()==false);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_BYTE);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readByte()==(byte)1);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_CHAR);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readChar()=='c');
		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_SHORT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readShort()==(short)8888);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_INT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readInt()==34445544);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_LONG);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readLong()==135245525252454L);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_FLOAT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readFloat()==1.33f);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_DOUBLE);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readDouble()==-2.4090459E3);
					
		leave();
	};	
	
	
	@Test public void testTypesAndFlushes_nDnf()throws IOException
	{
		enter();
		/*	Check if we can read primitives and detect indicators.
		*/
		Pair p = create();
		Assume.assumeTrue("must be not described",!p.write.isDescribed());
		writeTypesAndFlushes(p.write);		
		p.write.close();		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readBoolean()==false);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readByte()==(byte)1);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readChar()=='c');
		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readShort()==(short)8888);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readInt()==34445544);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readLong()==135245525252454L);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readFloat()==1.33f);
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readDouble()==-2.4090459E3);
					
		leave();
	};	
	
	
	
	
	
	
	
	@Test public void testEUnexpectedEof()throws IOException
	{
		enter();
		/* Test if layer properly report unexpected end-of-file if there is no data.
		
			Notice, since contract requires that getIndicator is called 
			before any primitive read and primitive read is required to
			be called only when curstor is at data we may not expect
			primitive reads to behave in a consitent way on eof.
		
		 */
		Pair p = create();
		p.write.writeBoolean(false);
		p.write.close();
		p.read.getIndicator();
		p.read.readBoolean();
		Assert.assertTrue(p.read.getIndicator()==TIndicator.EOF);
		leave();
	};
	
	
	
	
	
	
	
}