package sztejkat.abstractfmt;
import sztejkat.abstractfmt.obj.*;
import sztejkat.abstractfmt.utils.*;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
/**
	A test bed for {@link AStructReadFormatBase0}, stand alone junit tests.
	<p>
	Note: Those tests are based on {@link CObjStructReadFormat0} implementation
	since this is an easiest way to check how exactly the read format do behave
	in its implementation-specific aspects
*/
public class Test_AStructReadFormatBase0 extends sztejkat.abstractfmt.test.ATest
{
				
					/** Just a convinience implementation, if for the future I would have to
					change it or modify */
					private static final class DUT extends CObjStructReadFormat0
					{
						public DUT(Iterator<IObjStructFormat0> stream,
											  int max_supported_recursion_depth,
											  int max_supported_name_length
											  ){ super( new CPollableIterator<IObjStructFormat0>(stream,false),
														max_supported_recursion_depth,
														max_supported_name_length
														);};
								int _openImpl;	
						@Override protected void openImpl()throws IOException{_openImpl++;};
								int _closeImpl;
						@Override protected void closeImpl()throws IOException{_closeImpl++;};
								
					};
					
	@Test public void testOpenClose()throws IOException
	{
		/*
			We test if open() can be done twice and close too.
			We also check if can't re open closed.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										
									}),-1,1000
							);
				d.open();
				Assert.assertTrue(d._openImpl==1);
				d.open();
				Assert.assertTrue(d._openImpl==1);
				d.close();
				Assert.assertTrue(d._closeImpl==1);
				d.close();
				Assert.assertTrue(d._closeImpl==1);
				
				try{
					d.open();
					Assert.fail();
				}catch(EClosed ex){};
		leave();
	};
	
	@Test public void testBasicPrimitiveReads()throws IOException
	{
		/*
			We test if we can correctly read primitive data
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										ELMT_BOOLEAN.valueOf(true),
										ELMT_BOOLEAN.valueOf(false),
										ELMT_BYTE.valueOf((byte)33),
										ELMT_CHAR.valueOf('c'),
										ELMT_SHORT.valueOf((short)-13444),
										ELMT_INT.valueOf(123323334),
										ELMT_LONG.valueOf(34844484L),
										ELMT_FLOAT.valueOf(334.3f),
										ELMT_DOUBLE.valueOf(234.3e33)
									}),-1,1000
							);
				d.open();
				Assert.assertTrue(d.readBoolean()==true);
				Assert.assertTrue(d.readBoolean()==false);
				Assert.assertTrue(d.readByte()==(byte)33);
				Assert.assertTrue(d.readChar()=='c');
				Assert.assertTrue(d.readShort()==(short)-13444);
				Assert.assertTrue(d.readInt()==123323334);
				Assert.assertTrue(d.readLong()==34844484L);
				Assert.assertTrue(d.readFloat()==334.3f);
				Assert.assertTrue(d.readDouble()==234.3e33);
				//And eof handling.
				try{
					d.readInt();
					Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------------------------------------------------------------
	
	
	
				Boolean block operations
	
	
	------------------------------------------------------------------------------*/
	
	@Test public void testBasicPrimitiveBlockRead_boolean()throws IOException
	{
		/*
			We test if we can correctly read block, based on booleaneger case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_BOOLEAN.valueOf(true),
										BLK_BOOLEAN.valueOf(false),
										BLK_BOOLEAN.valueOf(true)
									}),-1,1000
							);
				d.open();
				boolean [] t = new boolean [32];
				Assert.assertTrue(d.readBooleanBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==true);
				Assert.assertTrue(t[2]==false);
				Assert.assertTrue(t[3]==true);
				//and if we correctly poll for eof?
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readBooleanBlock(t,1,30);
				Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_boolean()throws IOException
	{
		/*
			We test if we can correctly read block, based on booleaneger case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_BOOLEAN.valueOf(true),
										BLK_BOOLEAN.valueOf(false),
										BLK_BOOLEAN.valueOf(true),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				boolean [] t = new boolean [32];
				Assert.assertTrue(d.readBooleanBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==true);
				Assert.assertTrue(t[2]==false);
				Assert.assertTrue(t[3]==true);
				Assert.assertTrue(d.readBooleanBlock(t,1,30)==-1);
				Assert.assertTrue(d.readBooleanBlock(t,1,30)==-1);
				Assert.assertTrue(d.readBooleanBlock(t,1,30)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_boolean_single_item()throws IOException
	{
		/*
			We test if we can correctly read block, based on booleaneger case,
			when it is enclosed in signals, using single item block read operation.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_BOOLEAN.valueOf(true),
										BLK_BOOLEAN.valueOf(false),
										BLK_BOOLEAN.valueOf(true),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				boolean [] t = new boolean [32];
				Assert.assertTrue(d.readBooleanBlock()==true);
				Assert.assertTrue(d.readBooleanBlock()==false);
				Assert.assertTrue(d.readBooleanBlock()==true);
				try{
					d.readBooleanBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				try{
					d.readBooleanBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testPrimitiveBooleanBlockSkip()throws IOException
	{
		/*
			We test if we can skip an entire block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_BOOLEAN.valueOf(true),
										BLK_BOOLEAN.valueOf(false),
										BLK_BOOLEAN.valueOf(true),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	@Test public void testPrimitiveBooleanBlockSkip_part()throws IOException
	{
		/*
			We test if we can skip a part of a block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_BOOLEAN.valueOf(true),
										BLK_BOOLEAN.valueOf(false),
										BLK_BOOLEAN.valueOf(true),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				d.readBooleanBlock();
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------------------------------------------------------------
	
	
	
				Byte block operations
	
	
	------------------------------------------------------------------------------*/
	
	@Test public void testBasicPrimitiveBlockRead_byte()throws IOException
	{
		/*
			We test if we can correctly read block, based on byteeger case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_BYTE.valueOf((byte)44),
										BLK_BYTE.valueOf((byte)14),
										BLK_BYTE.valueOf((byte)45)
									}),-1,1000
							);
				d.open();
				byte [] t = new byte [32];
				Assert.assertTrue(d.readByteBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==(byte)44);
				Assert.assertTrue(t[2]==(byte)14);
				Assert.assertTrue(t[3]==(byte)45);
				//and if we correctly poll for eof?
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readByteBlock(t,1,30);
				Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_byte()throws IOException
	{
		/*
			We test if we can correctly read block, based on byteeger case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_BYTE.valueOf((byte)44),
										BLK_BYTE.valueOf((byte)14),
										BLK_BYTE.valueOf((byte)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				byte [] t = new byte [32];
				Assert.assertTrue(d.readByteBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==(byte)44);
				Assert.assertTrue(t[2]==(byte)14);
				Assert.assertTrue(t[3]==(byte)45);
				Assert.assertTrue(d.readByteBlock(t,1,30)==-1);
				Assert.assertTrue(d.readByteBlock(t,1,30)==-1);
				Assert.assertTrue(d.readByteBlock(t,1,30)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_byte_single_item()throws IOException
	{
		/*
			We test if we can correctly read block, based on byteeger case,
			when it is enclosed in signals, using single item block read operation.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_BYTE.valueOf((byte)44),
										BLK_BYTE.valueOf((byte)14),
										BLK_BYTE.valueOf((byte)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				byte [] t = new byte [32];
				Assert.assertTrue(d.readByteBlock()==(byte)44);
				Assert.assertTrue(d.readByteBlock()==(byte)14);
				Assert.assertTrue(d.readByteBlock()==(byte)45);
				try{
					d.readByteBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				try{
					d.readByteBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testPrimitiveByteBlockSkip()throws IOException
	{
		/*
			We test if we can skip an entire block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_BYTE.valueOf((byte)44),
										BLK_BYTE.valueOf((byte)14),
										BLK_BYTE.valueOf((byte)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	@Test public void testPrimitiveByteBlockSkip_part()throws IOException
	{
		/*
			We test if we can skip a part of a block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_BYTE.valueOf((byte)44),
										BLK_BYTE.valueOf((byte)14),
										BLK_BYTE.valueOf((byte)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				d.readByteBlock();
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	/* ----------------------------------------------------------------------------
	
	
	
				Char block operations
	
	
	------------------------------------------------------------------------------*/
	
	@Test public void testBasicPrimitiveBlockRead_char()throws IOException
	{
		/*
			We test if we can correctly read block, based on chareger case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_CHAR.valueOf((char)44),
										BLK_CHAR.valueOf((char)14),
										BLK_CHAR.valueOf((char)45)
									}),-1,1000
							);
				d.open();
				char [] t = new char [32];
				Assert.assertTrue(d.readCharBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==(char)44);
				Assert.assertTrue(t[2]==(char)14);
				Assert.assertTrue(t[3]==(char)45);
				//and if we correctly poll for eof?
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readCharBlock(t,1,30);
				Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_char()throws IOException
	{
		/*
			We test if we can correctly read block, based on chareger case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_CHAR.valueOf((char)44),
										BLK_CHAR.valueOf((char)14),
										BLK_CHAR.valueOf((char)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				char [] t = new char [32];
				Assert.assertTrue(d.readCharBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==(char)44);
				Assert.assertTrue(t[2]==(char)14);
				Assert.assertTrue(t[3]==(char)45);
				Assert.assertTrue(d.readCharBlock(t,1,30)==-1);
				Assert.assertTrue(d.readCharBlock(t,1,30)==-1);
				Assert.assertTrue(d.readCharBlock(t,1,30)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_char_single_item()throws IOException
	{
		/*
			We test if we can correctly read block, based on chareger case,
			when it is enclosed in signals, using single item block read operation.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_CHAR.valueOf((char)44),
										BLK_CHAR.valueOf((char)14),
										BLK_CHAR.valueOf((char)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				char [] t = new char [32];
				Assert.assertTrue(d.readCharBlock()==(char)44);
				Assert.assertTrue(d.readCharBlock()==(char)14);
				Assert.assertTrue(d.readCharBlock()==(char)45);
				try{
					d.readCharBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				try{
					d.readCharBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testPrimitiveCharBlockSkip()throws IOException
	{
		/*
			We test if we can skip an entire block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_CHAR.valueOf((char)44),
										BLK_CHAR.valueOf((char)14),
										BLK_CHAR.valueOf((char)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	@Test public void testPrimitiveCharBlockSkip_part()throws IOException
	{
		/*
			We test if we can skip a part of a block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_CHAR.valueOf((char)44),
										BLK_CHAR.valueOf((char)14),
										BLK_CHAR.valueOf((char)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				d.readCharBlock();
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	/* ----------------------------------------------------------------------------
	
	
	
				Short block operations
	
	
	------------------------------------------------------------------------------*/
	
	@Test public void testBasicPrimitiveBlockRead_short()throws IOException
	{
		/*
			We test if we can correctly read block, based on shorteger case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_SHORT.valueOf((short)44),
										BLK_SHORT.valueOf((short)14),
										BLK_SHORT.valueOf((short)45)
									}),-1,1000
							);
				d.open();
				short [] t = new short [32];
				Assert.assertTrue(d.readShortBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				//and if we correctly poll for eof?
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readShortBlock(t,1,30);
				Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_short()throws IOException
	{
		/*
			We test if we can correctly read block, based on shorteger case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_SHORT.valueOf((short)44),
										BLK_SHORT.valueOf((short)14),
										BLK_SHORT.valueOf((short)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				short [] t = new short [32];
				Assert.assertTrue(d.readShortBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				Assert.assertTrue(d.readShortBlock(t,1,30)==-1);
				Assert.assertTrue(d.readShortBlock(t,1,30)==-1);
				Assert.assertTrue(d.readShortBlock(t,1,30)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_short_single_item()throws IOException
	{
		/*
			We test if we can correctly read block, based on shorteger case,
			when it is enclosed in signals, using single item block read operation.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_SHORT.valueOf((short)44),
										BLK_SHORT.valueOf((short)14),
										BLK_SHORT.valueOf((short)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				short [] t = new short [32];
				Assert.assertTrue(d.readShortBlock()==44);
				Assert.assertTrue(d.readShortBlock()==14);
				Assert.assertTrue(d.readShortBlock()==45);
				try{
					d.readShortBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				try{
					d.readShortBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testPrimitiveBlockSkip()throws IOException
	{
		/*
			We test if we can skip an entire block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_SHORT.valueOf((short)44),
										BLK_SHORT.valueOf((short)14),
										BLK_SHORT.valueOf((short)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	@Test public void testPrimitiveBlockSkip_part()throws IOException
	{
		/*
			We test if we can skip a part of a block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_SHORT.valueOf((short)44),
										BLK_SHORT.valueOf((short)14),
										BLK_SHORT.valueOf((short)45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				d.readShortBlock();
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------------------------------------------------------------
	
	
	
				Long block operations
	
	
	------------------------------------------------------------------------------*/
	
	@Test public void testBasicPrimitiveBlockRead_long()throws IOException
	{
		/*
			We test if we can correctly read block, based on longeger case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_LONG.valueOf(44),
										BLK_LONG.valueOf(14),
										BLK_LONG.valueOf(45)
									}),-1,1000
							);
				d.open();
				long [] t = new long [32];
				Assert.assertTrue(d.readLongBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				//and if we correctly poll for eof?
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readLongBlock(t,1,30);
				Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_long()throws IOException
	{
		/*
			We test if we can correctly read block, based on longeger case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_LONG.valueOf(44),
										BLK_LONG.valueOf(14),
										BLK_LONG.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				long [] t = new long [32];
				Assert.assertTrue(d.readLongBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				Assert.assertTrue(d.readLongBlock(t,1,30)==-1);
				Assert.assertTrue(d.readLongBlock(t,1,30)==-1);
				Assert.assertTrue(d.readLongBlock(t,1,30)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_long_single_item()throws IOException
	{
		/*
			We test if we can correctly read block, based on longeger case,
			when it is enclosed in signals, using single item block read operation.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_LONG.valueOf(44),
										BLK_LONG.valueOf(14),
										BLK_LONG.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				long [] t = new long [32];
				Assert.assertTrue(d.readLongBlock()==44);
				Assert.assertTrue(d.readLongBlock()==14);
				Assert.assertTrue(d.readLongBlock()==45);
				try{
					d.readLongBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				try{
					d.readLongBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testPrimitiveLongBlockSkip()throws IOException
	{
		/*
			We test if we can skip an entire block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_LONG.valueOf(44),
										BLK_LONG.valueOf(14),
										BLK_LONG.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	@Test public void testPrimitiveLongBlockSkip_part()throws IOException
	{
		/*
			We test if we can skip a part of a block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_LONG.valueOf(44),
										BLK_LONG.valueOf(14),
										BLK_LONG.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				d.readLongBlock();
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------------------------------------------------------------
	
	
	
				Float block operations
	
	
	------------------------------------------------------------------------------*/
	
	@Test public void testBasicPrimitiveBlockRead_float()throws IOException
	{
		/*
			We test if we can correctly read block, based on floateger case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_FLOAT.valueOf(44),
										BLK_FLOAT.valueOf(14),
										BLK_FLOAT.valueOf(45)
									}),-1,1000
							);
				d.open();
				float [] t = new float [32];
				Assert.assertTrue(d.readFloatBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				//and if we correctly poll for eof?
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readFloatBlock(t,1,30);
				Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_float()throws IOException
	{
		/*
			We test if we can correctly read block, based on floateger case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_FLOAT.valueOf(44),
										BLK_FLOAT.valueOf(14),
										BLK_FLOAT.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				float [] t = new float [32];
				Assert.assertTrue(d.readFloatBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				Assert.assertTrue(d.readFloatBlock(t,1,30)==-1);
				Assert.assertTrue(d.readFloatBlock(t,1,30)==-1);
				Assert.assertTrue(d.readFloatBlock(t,1,30)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_float_single_item()throws IOException
	{
		/*
			We test if we can correctly read block, based on floateger case,
			when it is enclosed in signals, using single item block read operation.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_FLOAT.valueOf(44),
										BLK_FLOAT.valueOf(14),
										BLK_FLOAT.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				float [] t = new float [32];
				Assert.assertTrue(d.readFloatBlock()==44);
				Assert.assertTrue(d.readFloatBlock()==14);
				Assert.assertTrue(d.readFloatBlock()==45);
				try{
					d.readFloatBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				try{
					d.readFloatBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testPrimitiveFloatBlockSkip()throws IOException
	{
		/*
			We test if we can skip an entire block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_FLOAT.valueOf(44),
										BLK_FLOAT.valueOf(14),
										BLK_FLOAT.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	@Test public void testPrimitiveFloatBlockSkip_part()throws IOException
	{
		/*
			We test if we can skip a part of a block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_FLOAT.valueOf(44),
										BLK_FLOAT.valueOf(14),
										BLK_FLOAT.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				d.readFloatBlock();
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------------------------------------------------------------
	
	
	
				Double block operations
	
	
	------------------------------------------------------------------------------*/
	
	@Test public void testBasicPrimitiveBlockRead_double()throws IOException
	{
		/*
			We test if we can correctly read block, based on doubleeger case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_DOUBLE.valueOf(44),
										BLK_DOUBLE.valueOf(14),
										BLK_DOUBLE.valueOf(45)
									}),-1,1000
							);
				d.open();
				double [] t = new double [32];
				Assert.assertTrue(d.readDoubleBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				//and if we correctly poll for eof?
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readDoubleBlock(t,1,30);
				Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_double()throws IOException
	{
		/*
			We test if we can correctly read block, based on doubleeger case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_DOUBLE.valueOf(44),
										BLK_DOUBLE.valueOf(14),
										BLK_DOUBLE.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				double [] t = new double [32];
				Assert.assertTrue(d.readDoubleBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				Assert.assertTrue(d.readDoubleBlock(t,1,30)==-1);
				Assert.assertTrue(d.readDoubleBlock(t,1,30)==-1);
				Assert.assertTrue(d.readDoubleBlock(t,1,30)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_double_single_item()throws IOException
	{
		/*
			We test if we can correctly read block, based on doubleeger case,
			when it is enclosed in signals, using single item block read operation.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_DOUBLE.valueOf(44),
										BLK_DOUBLE.valueOf(14),
										BLK_DOUBLE.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				double [] t = new double [32];
				Assert.assertTrue(d.readDoubleBlock()==44);
				Assert.assertTrue(d.readDoubleBlock()==14);
				Assert.assertTrue(d.readDoubleBlock()==45);
				try{
					d.readDoubleBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				try{
					d.readDoubleBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testPrimitiveDoubleBlockSkip()throws IOException
	{
		/*
			We test if we can skip an entire block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_DOUBLE.valueOf(44),
										BLK_DOUBLE.valueOf(14),
										BLK_DOUBLE.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	@Test public void testPrimitiveDoubleBlockSkip_part()throws IOException
	{
		/*
			We test if we can skip a part of a block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_DOUBLE.valueOf(44),
										BLK_DOUBLE.valueOf(14),
										BLK_DOUBLE.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				d.readDoubleBlock();
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------------------------------------------------------------
	
	
	
				String block operations
	
	
	------------------------------------------------------------------------------*/
	
	@Test public void testBasicPrimitiveBlockRead_string()throws IOException
	{
		/*
			We test if we can correctly read block, based on string case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_STRING.valueOf('a'),
										BLK_STRING.valueOf('v'),
										BLK_STRING.valueOf('z')
									}),-1,1000
							);
				d.open();
				StringBuffer sb = new StringBuffer();
				Assert.assertTrue(d.readString(sb,100)==3);
				Assert.assertTrue(sb.charAt(0)=='a');
				Assert.assertTrue(sb.charAt(1)=='v');
				Assert.assertTrue(sb.charAt(2)=='z');
				//and if we correctly poll for eof?            
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readString(sb,100);
						Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_string()throws IOException
	{
		/*
			We test if we can correctly read block, based on string case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_STRING.valueOf('a'),
										BLK_STRING.valueOf('v'),
										BLK_STRING.valueOf('z'),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				StringBuffer sb = new StringBuffer();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.readString(sb,100)==3);
				Assert.assertTrue(sb.charAt(0)=='a');
				Assert.assertTrue(sb.charAt(1)=='v');
				Assert.assertTrue(sb.charAt(2)=='z');
				Assert.assertTrue(d.readString(sb,100)==-1);
				Assert.assertTrue(d.readString(sb,100)==-1);
				Assert.assertTrue(d.readString(sb,100)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_string_single_item()throws IOException
	{
		/*
			We test if we can correctly read block, based on stringeger case,
			when it is enclosed in signals, using single item block read operation.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_STRING.valueOf('a'),
										BLK_STRING.valueOf('v'),
										BLK_STRING.valueOf('z'),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));				
				Assert.assertTrue(d.readString()=='a');
				Assert.assertTrue(d.readString()=='v');
				Assert.assertTrue(d.readString()=='z');
				try{
					d.readString();
					Assert.fail();
				}catch(ENoMoreData ex){};
				try{
					d.readString();
					Assert.fail();
				}catch(ENoMoreData ex){};
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testPrimitiveStringBlockSkip()throws IOException
	{
		/*
			We test if we can skip an entire block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_STRING.valueOf('a'),
										BLK_STRING.valueOf('v'),
										BLK_STRING.valueOf('z'),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	@Test public void testPrimitiveStringBlockSkip_part()throws IOException
	{
		/*
			We test if we can skip a part of a block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_STRING.valueOf('a'),
										BLK_STRING.valueOf('v'),
										BLK_STRING.valueOf('z'),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				d.readString();
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ----------------------------------------------------------------------------
	
	
	
				Int block operations
	
	
	------------------------------------------------------------------------------*/
	
	@Test public void testBasicPrimitiveBlockRead_int()throws IOException
	{
		/*
			We test if we can correctly read block, based on integer case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_INT.valueOf(44),
										BLK_INT.valueOf(14),
										BLK_INT.valueOf(45)
									}),-1,1000
							);
				d.open();
				int [] t = new int [32];
				Assert.assertTrue(d.readIntBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				//and if we correctly poll for eof?
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readIntBlock(t,1,30);
				Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_int()throws IOException
	{
		/*
			We test if we can correctly read block, based on integer case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_INT.valueOf(44),
										BLK_INT.valueOf(14),
										BLK_INT.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				int [] t = new int [32];
				Assert.assertTrue(d.readIntBlock(t,1,30)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				Assert.assertTrue(d.readIntBlock(t,1,30)==-1);
				Assert.assertTrue(d.readIntBlock(t,1,30)==-1);
				Assert.assertTrue(d.readIntBlock(t,1,30)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_int_single_item()throws IOException
	{
		/*
			We test if we can correctly read block, based on integer case,
			when it is enclosed in signals, using single item block read operation.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_INT.valueOf(44),
										BLK_INT.valueOf(14),
										BLK_INT.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				int [] t = new int [32];
				Assert.assertTrue(d.readIntBlock()==44);
				Assert.assertTrue(d.readIntBlock()==14);
				Assert.assertTrue(d.readIntBlock()==45);
				try{
					d.readIntBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				try{
					d.readIntBlock();
					Assert.fail();
				}catch(ENoMoreData ex){};
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	
	@Test public void testPrimitiveIntBlockSkip()throws IOException
	{
		/*
			We test if we can skip an entire block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_INT.valueOf(44),
										BLK_INT.valueOf(14),
										BLK_INT.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
	
	@Test public void testPrimitiveIntBlockSkip_part()throws IOException
	{
		/*
			We test if we can skip a part of a block.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_INT.valueOf(44),
										BLK_INT.valueOf(14),
										BLK_INT.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				d.readIntBlock();
				Assert.assertTrue(d.skip(0)==0);
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
};