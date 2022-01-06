package sztejkat.abstractfmt.testsuite.signal;
import sztejkat.abstractfmt.ISignalReadFormat;
import sztejkat.abstractfmt.ISignalWriteFormat;
import sztejkat.abstractfmt.TContentType;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EClosed;
import sztejkat.abstractfmt.ENoMoreData;
import sztejkat.abstractfmt.EDataMissmatch;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
import java.io.IOException;

/**
	Testing defnese against abuse of different methods.
*/
public class TestAbuseDefense extends ASignalTest
{
	/*
		Note:
			Due to the fact that it is "by-contract"
			test we can't create abusive streams
			because write formats will not allow us
			to make them.
			
			Testing abusive streams must be done
			in an implementation specific way.
	*/
	@Test public void testWriteEndWithoutBegin()throws IOException
	{
		/*
			Check if write format will prevent end without begin.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.end();
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	
	@Test public void testWriteBlockAtZeroLevel_boolean()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeBooleanBlock(new boolean[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	@Test public void testWriteBlockAtZeroLevel_byte()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeByteBlock(new byte[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	@Test public void testWriteBlockAtZeroLevel_byte2()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeByteBlock((byte)0);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	@Test public void testWriteBlockAtZeroLevel_char()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeCharBlock(new char[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	@Test public void testWriteBlockAtZeroLevel_char2()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeCharBlock("sss");
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	
	@Test public void testWriteBlockAtZeroLevel_short()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeShortBlock(new short[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	
	@Test public void testWriteBlockAtZeroLevel_int()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeIntBlock(new int[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	
	@Test public void testWriteBlockAtZeroLevel_long()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeLongBlock(new long[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	
	@Test public void testWriteBlockAtZeroLevel_float()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeFloatBlock(new float[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	
	@Test public void testWriteBlockAtZeroLevel_double()throws IOException
	{
		/*
			Check if write format will prevent writing block 
			without begin signal.
		*/
		enter();
		Pair p = create();
		p.write.open();
		try{
			p.write.writeDoubleBlock(new double[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		
		leave();
	};
	
	
	
	
	
	
	
	
	@Test public void testWritePrimitiveInBlock_boolean()throws IOException
	{
		/*
			Check if write format will prevent writing primitive 
			after block was initialized.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeBooleanBlock(new boolean[3]);
		try{
			p.write.writeBoolean(false);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	@Test public void testWritePrimitiveInBlock_byte()throws IOException
	{
		/*
			Check if write format will prevent writing primitive 
			after block was initialized.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeByteBlock(new byte[3]);
		try{
			p.write.writeByte((byte)0);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	@Test public void testWritePrimitiveInBlock_char()throws IOException
	{
		/*
			Check if write format will prevent writing primitive 
			after block was initialized.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeCharBlock(new char[3]);
		try{
			p.write.writeChar('c');
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	
	@Test public void testWritePrimitiveInBlock_short()throws IOException
	{
		/*
			Check if write format will prevent writing primitive 
			after block was initialized.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeShortBlock(new short[3]);
		try{
			p.write.writeShort((short)3);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	
	@Test public void testWritePrimitiveInBlock_int()throws IOException
	{
		/*
			Check if write format will prevent writing primitive 
			after block was initialized.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeIntBlock(new int[3]);
		try{
			p.write.writeInt(4);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	@Test public void testWritePrimitiveInBlock_long()throws IOException
	{
		/*
			Check if write format will prevent writing primitive 
			after block was initialized.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeLongBlock(new long[3]);
		try{
			p.write.writeLong(0);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	@Test public void testWritePrimitiveInBlock_float()throws IOException
	{
		/*
			Check if write format will prevent writing primitive 
			after block was initialized.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeFloatBlock(new float[3]);
		try{
			p.write.writeFloat(0);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	@Test public void testWritePrimitiveInBlock_double()throws IOException
	{
		/*
			Check if write format will prevent writing primitive 
			after block was initialized.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeDoubleBlock(new double[3]);
		try{
			p.write.writeDouble(0);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testWriteBlockMissmatch_boolean()throws IOException
	{
		/*
			Check if write format will prevent writing 
			inconsistent block types.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeBooleanBlock(new boolean[3]);
		try{
			p.write.writeIntBlock(new int[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	@Test public void testWriteBlockMissmatch_char()throws IOException
	{
		/*
			Check if write format will prevent writing 
			inconsistent block types.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeCharBlock(new char[3]);
		try{
			p.write.writeIntBlock(new int[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	@Test public void testWriteBlockMissmatch_short()throws IOException
	{
		/*
			Check if write format will prevent writing 
			inconsistent block types.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeShortBlock(new short[3]);
		try{
			p.write.writeBooleanBlock(new boolean[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	@Test public void testWriteBlockMissmatch_int()throws IOException
	{
		/*
			Check if write format will prevent writing 
			inconsistent block types.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeIntBlock(new int[3]);
		try{
			p.write.writeCharBlock(new char[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	@Test public void testWriteBlockMissmatch_long()throws IOException
	{
		/*
			Check if write format will prevent writing 
			inconsistent block types.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeLongBlock(new long[3]);
		try{
			p.write.writeIntBlock(new int[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	@Test public void testWriteBlockMissmatch_float()throws IOException
	{
		/*
			Check if write format will prevent writing 
			inconsistent block types.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeFloatBlock(new float[3]);
		try{
			p.write.writeDoubleBlock(new double[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	@Test public void testWriteBlockMissmatch_double()throws IOException
	{
		/*
			Check if write format will prevent writing 
			inconsistent block types.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.writeDoubleBlock(new double[3]);
		try{
			p.write.writeByteBlock(new byte[3]);
			Assert.fail();
			}catch(IllegalStateException ex){};
		p.close();
		leave();
	};
	
	
	
	
	
	
	@Test public void testTypeMissmatch_boolean()throws IOException
	{
		/*
			Check if read will detect type-missmatch
			and if it will allow to recover by skipping content.
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		Assume.assumeTrue(p.read.isDescribed());
		
		p.write.open();
		p.write.begin("A");
		p.write.writeBoolean(false);
		p.write.begin("B");
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"A");
		try{
			p.read.readFloat();
		}catch(EDataMissmatch ex){};
		assertNext(p.read,"B");
		
		leave();
	};
	
	@Test public void testTypeMissmatch_byte()throws IOException
	{
		/*
			Check if read will detect type-missmatch
			and if it will allow to recover by skipping content.
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		Assume.assumeTrue(p.read.isDescribed());
		
		p.write.open();
		p.write.begin("A");
		p.write.writeByte((byte)0);
		p.write.begin("B");
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"A");
		try{
			p.read.readBoolean();
		}catch(EDataMissmatch ex){};
		assertNext(p.read,"B");
		
		leave();
	};
	
	
	@Test public void testTypeMissmatch_float()throws IOException
	{
		/*
			Check if read will detect type-missmatch
			and if it will allow to recover by skipping content.
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		Assume.assumeTrue(p.read.isDescribed());
		
		p.write.open();
		p.write.begin("A");
		p.write.writeFloat(0);
		p.write.begin("B");
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"A");
		try{
			p.read.readFloatBlock(new float[3]);
		}catch(EDataMissmatch ex){};
		assertNext(p.read,"B");
		
		leave();
	};
	
	@Test public void testTypeMissmatch_char()throws IOException
	{
		/*
			Check if read will detect type-missmatch
			and if it will allow to recover by skipping content.
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		Assume.assumeTrue(p.read.isDescribed());
		
		p.write.open();
		p.write.begin("A");
		p.write.writeChar('d');
		p.write.begin("B");
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"A");
		try{
			p.read.readFloat();
		}catch(EDataMissmatch ex){};
		assertNext(p.read,"B");
		
		leave();
	};
	
	@Test public void testTypeMissmatch_short()throws IOException
	{
		/*
			Check if read will detect type-missmatch
			and if it will allow to recover by skipping content.
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		Assume.assumeTrue(p.read.isDescribed());
		
		p.write.open();
		p.write.begin("A");
		p.write.writeShort((short)0);
		p.write.begin("B");
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"A");
		try{
			p.read.readInt();
		}catch(EDataMissmatch ex){};
		assertNext(p.read,"B");
		
		leave();
	};
	
	@Test public void testTypeMissmatch_long()throws IOException
	{
		/*
			Check if read will detect type-missmatch
			and if it will allow to recover by skipping content.
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		Assume.assumeTrue(p.read.isDescribed());
		
		p.write.open();
		p.write.begin("A");
		p.write.writeLong(0);
		p.write.begin("B");
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"A");
		try{
			p.read.readFloat();
		}catch(EDataMissmatch ex){};
		assertNext(p.read,"B");
		
		leave();
	};
	
	
	@Test public void testTypeMissmatch_int()throws IOException
	{
		/*
			Check if read will detect type-missmatch
			and if it will allow to recover by skipping content.
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		Assume.assumeTrue(p.read.isDescribed());
		
		p.write.open();
		p.write.begin("A");
		p.write.writeInt(0);
		p.write.begin("B");
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"A");
		try{
			p.read.readByte();
		}catch(EDataMissmatch ex){};
		assertNext(p.read,"B");
		
		leave();
	};
	
	@Test public void testTypeMissmatch_double()throws IOException
	{
		/*
			Check if read will detect type-missmatch
			and if it will allow to recover by skipping content.
		*/
		enter();
		Pair p = create();
		Assume.assumeTrue(p.write.isDescribed());
		Assume.assumeTrue(p.read.isDescribed());
		
		p.write.open();
		p.write.begin("A");
		p.write.writeDouble(0);
		p.write.begin("B");
		p.write.end();
		p.write.end();
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"A");
		try{
			p.read.readLong();
		}catch(EDataMissmatch ex){};
		assertNext(p.read,"B");
		
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testNoMoreData_boolean_1()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
			p.write.writeBoolean(false);
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		p.read.readBoolean();
		try{
			p.read.readBoolean();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	@Test public void testNoMoreData_boolean_2()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		try{
			p.read.readBoolean();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	
	
	@Test public void testNoMoreData_byte_1()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
			p.write.writeByte((byte)0);
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		p.read.readByte();
		try{
			p.read.readByte();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	@Test public void testNoMoreData_byte_2()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		try{
			p.read.readByte();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	
	
	@Test public void testNoMoreData_char_1()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
			p.write.writeChar('x');
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		p.read.readChar();
		try{
			p.read.readChar();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	@Test public void testNoMoreData_char_2()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		try{
			p.read.readChar();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	
	@Test public void testNoMoreData_short_1()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
			p.write.writeShort((short)0);
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		p.read.readShort();
		try{
			p.read.readShort();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	@Test public void testNoMoreData_short_2()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		try{
			p.read.readShort();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	
	@Test public void testNoMoreData_int_1()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
			p.write.writeInt(1);
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		p.read.readInt();
		try{
			p.read.readInt();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	@Test public void testNoMoreData_int_2()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		try{
			p.read.readInt();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	
	@Test public void testNoMoreData_long_1()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
			p.write.writeLong(1);
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		p.read.readLong();
		try{
			p.read.readLong();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	@Test public void testNoMoreData_long_2()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		try{
			p.read.readLong();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	
	@Test public void testNoMoreData_float_1()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
			p.write.writeFloat(0);
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		p.read.readFloat();
		try{
			p.read.readFloat();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	@Test public void testNoMoreData_float_2()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		try{
			p.read.readFloat();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	
	@Test public void testNoMoreData_double_1()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
			p.write.writeDouble(0);
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		p.read.readDouble();
		try{
			p.read.readDouble();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
	
	@Test public void testNoMoreData_double_2()throws IOException
	{
		/*
			Check if format will prevent reading primitives
			out of event boundary
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.begin("");
		p.write.end();		
		p.write.close();
		
		p.read.open();
		assertNext(p.read,"");
		try{
			p.read.readDouble();
		}catch(ENoMoreData ex){};
		assertNext(p.read,null);
		p.close();
		leave();
	};
};