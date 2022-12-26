package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case running signal operations.
*/
public class ATestCase_BasicSignalOperations extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{

	/**
		A test checking if stream refuses to read next signal 
		if not opened.
	@throws IOException .
	*/
	@Test public void testUnopendedFailsNext()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			p.writer.open();
			p.writer.close();
			try{
					p.reader.next();
			}catch(ENotOpen ex){System.out.println(ex); };
	};
	/**
		Test flat sequence of structures
	@throws IOException .
	*/
	@Test public void testFlatNext()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
			w.end();
			w.begin("gimikis");
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(null==r.next());
			Assert.assertTrue("gimikis".equals(r.next()));
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	/**
		Test some nested structure.
	@throws IOException .
	*/
	@Test public void testNestedStructure()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			
			w.open();
			
			for(int i=0;i<32;i++)
			{
				w.begin("struct_"+i);
			};
			for(int i=0;i<32;i++)
			{
				w.end();
			};
			w.close();
			
			r.open();
			for(int i=0;i<32;i++)
				Assert.assertTrue(("struct_"+i).equals(r.next()));
			for(int i=0;i<32;i++)
				Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against no data
	@throws IOException .
	*/
	@Test public void testHasElementaryData_empty()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against eof
	@throws IOException .
	*/
	@Test public void testHasElementaryData_eof()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(!r.hasElementaryData());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against totally empty file
	@throws IOException .
	*/
	@Test public void testHasElementaryData_totalempty()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.close();
			
			r.open();
			Assert.assertTrue(!r.hasElementaryData());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data
	@throws IOException .
	*/
	@Test public void testHasElementaryData_boolean()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeBoolean(false);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
			r.readBoolean();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data
	@throws IOException .
	*/
	@Test public void testHasElementaryData_byte()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeByte((byte)0);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
			r.readByte();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data
	@throws IOException .
	*/
	@Test public void testHasElementaryData_short()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeShort((short)0);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
			r.readShort();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data
	@throws IOException .
	*/
	@Test public void testHasElementaryData_char()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeChar((char)0);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
			r.readChar();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data
	@throws IOException .
	*/
	@Test public void testHasElementaryData_int()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(0);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
			r.readInt();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data
	@throws IOException .
	*/
	@Test public void testHasElementaryData_long()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeLong((long)0);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
			r.readLong();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data
	@throws IOException .
	*/
	@Test public void testHasElementaryData_float()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeFloat((float)0);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
			r.readFloat();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data
	@throws IOException .
	*/
	@Test public void testHasElementaryData_double()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeDouble((double)0);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
			r.readDouble();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data sequence
	@throws IOException .
	*/
	@Test public void testHasElementaryData_boolean_blk()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeBooleanBlock(new boolean[16]);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
				r.readBooleanBlock(new boolean[15]);
			Assert.assertTrue(r.hasElementaryData());
				r.readBooleanBlock();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data sequence
	@throws IOException .
	*/
	@Test public void testHasElementaryData_byte_blk()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeByteBlock(new byte[16]);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
				r.readByteBlock(new byte[15]);
			Assert.assertTrue(r.hasElementaryData());
				r.readByteBlock();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data sequence
	@throws IOException .
	*/
	@Test public void testHasElementaryData_char_blk()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeCharBlock(new char[16]);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
				r.readCharBlock(new char[15]);
			Assert.assertTrue(r.hasElementaryData());
				r.readCharBlock();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data sequence
	@throws IOException .
	*/
	@Test public void testHasElementaryData_short_blk()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeShortBlock(new short[16]);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
				r.readShortBlock(new short[15]);
			Assert.assertTrue(r.hasElementaryData());
				r.readShortBlock();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data sequence
	@throws IOException .
	*/
	@Test public void testHasElementaryData_int_blk()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeIntBlock(new int[16]);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
				r.readIntBlock(new int[15]);
			Assert.assertTrue(r.hasElementaryData());
				r.readIntBlock();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data sequence
	@throws IOException .
	*/
	@Test public void testHasElementaryData_long_blk()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeLongBlock(new long[16]);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
				r.readLongBlock(new long[15]);
			Assert.assertTrue(r.hasElementaryData());
				r.readLongBlock();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data sequence
	@throws IOException .
	*/
	@Test public void testHasElementaryData_float_blk()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeFloatBlock(new float[16]);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
				r.readFloatBlock(new float[15]);
			Assert.assertTrue(r.hasElementaryData());
				r.readFloatBlock();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data sequence
	@throws IOException .
	*/
	@Test public void testHasElementaryData_double_blk()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeDoubleBlock(new double[16]);
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
				r.readDoubleBlock(new double[15]);
			Assert.assertTrue(r.hasElementaryData());
				r.readDoubleBlock();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
	
	
	/**
		Tests {@link IStructReadFormat#hasElementaryData}
		against elementary data sequence
	@throws IOException .
	*/
	@Test public void testHasElementaryData_string()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeString("abcd");
			w.end();
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			Assert.assertTrue(r.hasElementaryData());
				r.readString(new StringBuilder(),3);
			Assert.assertTrue(r.hasElementaryData());
				r.readString();
			Assert.assertTrue(!r.hasElementaryData());
			Assert.assertTrue(null==r.next());
			//Now eof
			try{
				r.next();
				Assert.fail("Should have thrown");
			}catch(EEof ex){ System.out.println(ex);};
	};
};



