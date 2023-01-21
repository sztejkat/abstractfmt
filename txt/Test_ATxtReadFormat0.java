package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.*;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test bed for {@link ATxtReadFormat0}.
*/
public class Test_ATxtReadFormat0 extends ATest
{
		/** A device under test. Very limited implementation */
		private static final class DUT extends ATxtReadFormat0
		{
					/** A tokens stream to return */
					private final int [] token_chars;
					private int at;
					
			/** Creates
			@param token_chars tokens to read
			*/
			protected DUT(int [] token_chars)
			{
				super(0,65);
				this.token_chars=token_chars;
			};
			/* ******************************************************
				
				ATxtReadFormat0
			
			*******************************************************/
			@Override protected int tokenIn()throws IOException
			{
				if (at==token_chars.length) return TOKEN_EOF;
				int c= token_chars[at];
				if (c!=TOKEN_SIGNAL) at++;
				return c;
			};
			@Override protected int hasUnreadToken()throws IOException
			{
				if (at==token_chars.length) return TOKEN_EOF;
				int c= token_chars[at];
				return c>=0 ? 0 : c;
			};
			/* ******************************************************
				
				ARegisteringStructReadFormat,
				strongly faked.
			
			*******************************************************/
			/** Always returns SIG_BEGIN_DIRECT, searches for TOKEN_SIGNAL */
			@Override protected TSignalReg readSignalReg()throws IOException
			{
				for(;;)
				{
					if (at==token_chars.length) throw new EUnexpectedEof();
					int c= token_chars[at++];
					if (c==TOKEN_SIGNAL) return TSignalReg.SIG_BEGIN_DIRECT;
				}
			};
			@Override protected int pickLastSignalIndex(){ throw new AbstractMethodError(); };
			@Override protected String pickLastSignalRegName(){ return "signal"; };
			/* ****************************************************
	
				AStructReadFormatBase0, strongly faked.
	
	
			* ***************************************************/
			@Override protected void closeImpl(){};
			@Override protected void openImpl(){};
			@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
			@Override public int getMaxSupportedSignalNameLength(){ return 10000; };
		};
		
	@Test public void testDetectsEof()throws IOException
	{
		enter();
		final int [] x = new int[]{ };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	@Test public void testThrowsOnEof()throws IOException
	{
		enter();
		final int [] x = new int[]{ };
		DUT d= new DUT(x);
		d.open();
		try{
			d.next();
			Assert.fail();
		}catch(EEof ex){};
		leave();
	};
	@Test public void testNotEnclosed_Boolean_0()throws IOException
	{
		enter();
		final int [] x = new int[]{ '0' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(false==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_false()throws IOException
	{
		enter();
		final int [] x = new int[]{ 'f','a','l','s','e' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(false==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_FALSE()throws IOException
	{
		enter();
		final int [] x = new int[]{ 'F','A','L','S','E' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(false==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_parsable_zero_1()throws IOException
	{
		enter();
		final int [] x = new int[]{ '0','0','.','0' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(false==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_parsable_zero_2()throws IOException
	{
		enter();
		final int [] x = new int[]{ '0','x','0','0' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(false==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_Sequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ '0',	
									ATxtReadFormat0.TOKEN_BOUNDARY,'f','A','l','s','e',
									//Note: empty boundary due to eof is EOF. Needs to be either terminated by signal
									//or by next boundary.
									ATxtReadFormat0.TOKEN_BOUNDARY,ATxtReadFormat0.TOKEN_BOUNDARY 
									}; 
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(d.hasElementaryData());
		Assert.assertTrue(false==d.readBoolean());
		Assert.assertTrue(d.hasElementaryData());
		Assert.assertTrue(false==d.readBoolean());
		Assert.assertTrue(d.hasElementaryData());
		Assert.assertTrue(false==d.readBoolean());
		Assert.assertTrue(!d.hasElementaryData());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_1()throws IOException
	{
		enter();
		final int [] x = new int[]{ '1' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_true()throws IOException
	{
		enter();
		final int [] x = new int[]{ 't','r','u','e' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_TRUE()throws IOException
	{
		enter();
		final int [] x = new int[]{ 'T','R','U','E' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_parsable_nonzero_1()throws IOException
	{
		enter();
		final int [] x = new int[]{ '1','9','.','9' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_parsable_nonzero_2()throws IOException
	{		
		enter();
		final int [] x = new int[]{ '0','x','A','F' };
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true==d.readBoolean());
		leave();
	};
	@Test public void testNotEnclosed_Boolean_failed_syntax()throws IOException
	{
		enter();
		final int [] x = new int[]{ 'x','e','n','a' };
		DUT d= new DUT(x);
		d.open();
		try{
			d.readBoolean();
			Assert.fail();
		}catch(EBrokenFormat ex){ System.out.println(ex); };
		leave();
	};
	
	
	
	@Test public void testNotEnclosedByteSequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ '9',ATxtReadFormat0.TOKEN_BOUNDARY,
									'-','2', ATxtReadFormat0.TOKEN_BOUNDARY,
									'0','x','3',ATxtReadFormat0.TOKEN_BOUNDARY,
									ATxtReadFormat0.TOKEN_BOUNDARY,	//empty
									'1','.','1'						//float 
									};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((byte)9==d.readByte());
		Assert.assertTrue((byte)-2==d.readByte());
		Assert.assertTrue((byte)3==d.readByte());
		Assert.assertTrue((byte)0==d.readByte());
		Assert.assertTrue((byte)1==d.readByte());
		leave();
	}
	
	@Test public void testNotEnclosedShortSequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ '9',ATxtReadFormat0.TOKEN_BOUNDARY,
									'-','2', ATxtReadFormat0.TOKEN_BOUNDARY,
									'0','x','3',ATxtReadFormat0.TOKEN_BOUNDARY,
									ATxtReadFormat0.TOKEN_BOUNDARY,	//empty
									'1','.','1'						//float 
									};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((short)9==d.readShort());
		Assert.assertTrue((short)-2==d.readShort());
		Assert.assertTrue((short)3==d.readShort());
		Assert.assertTrue((short)0==d.readShort());
		Assert.assertTrue((short)1==d.readShort());
		leave();
	}
	
	@Test public void testNotEnclosedCharSequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ 'c',ATxtReadFormat0.TOKEN_BOUNDARY,
									'-','2', ATxtReadFormat0.TOKEN_BOUNDARY,
									ATxtReadFormat0.TOKEN_BOUNDARY,
									'a'
									};
		DUT d= new DUT(x);
		d.open();
		//Note: char tokens are stiched, so
		Assert.assertTrue('c'==d.readChar());
		Assert.assertTrue('-'==d.readChar());
		Assert.assertTrue('2'==d.readChar());
		Assert.assertTrue('a'==d.readChar());
		leave();
	}
	
	@Test public void testNotEnclosedIntSequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ '9',ATxtReadFormat0.TOKEN_BOUNDARY,
									'-','2', ATxtReadFormat0.TOKEN_BOUNDARY,
									'0','x','3',ATxtReadFormat0.TOKEN_BOUNDARY,
									ATxtReadFormat0.TOKEN_BOUNDARY,	//empty
									'1','.','1'						//float 
									};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(9==d.readInt());
		Assert.assertTrue(-2==d.readInt());
		Assert.assertTrue(3==d.readInt());
		Assert.assertTrue(0==d.readInt());
		Assert.assertTrue(1==d.readInt());
		leave();
	}
	
	@Test public void testNotEnclosedLongSequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ '9',ATxtReadFormat0.TOKEN_BOUNDARY,
									'-','2', ATxtReadFormat0.TOKEN_BOUNDARY,
									'0','x','3',ATxtReadFormat0.TOKEN_BOUNDARY,
									ATxtReadFormat0.TOKEN_BOUNDARY,	//empty
									'1','.','1'						//float 
									};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((long)9==d.readLong());
		Assert.assertTrue((long)-2==d.readLong());
		Assert.assertTrue((long)3==d.readLong());
		Assert.assertTrue((long)0==d.readLong());
		Assert.assertTrue((long)1==d.readLong());
		leave();
	}
	
	
	@Test public void testNotEnclosedFloatSequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ '9',ATxtReadFormat0.TOKEN_BOUNDARY,
									'-','2', ATxtReadFormat0.TOKEN_BOUNDARY,
									'0','x','3',ATxtReadFormat0.TOKEN_BOUNDARY,
									ATxtReadFormat0.TOKEN_BOUNDARY,	//empty
									'1','.','1'						//float 
									};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((float)9==d.readFloat());
		Assert.assertTrue((float)-2==d.readFloat());
		Assert.assertTrue((float)3==d.readFloat());
		Assert.assertTrue((float)0==d.readFloat());
		Assert.assertTrue(1.1f==d.readFloat());
		leave();
	}
	@Test public void testNotEnclosedFloatSpecialSequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ 'N','a','N',ATxtReadFormat0.TOKEN_BOUNDARY,
									'I','n','f','i','n','i','t','y',ATxtReadFormat0.TOKEN_BOUNDARY,
									'+','I','n','f','i','n','i','t','y',ATxtReadFormat0.TOKEN_BOUNDARY,
									'-','I','n','f','i','n','i','t','y',ATxtReadFormat0.TOKEN_BOUNDARY
									};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(Float.isNaN(d.readFloat()));
		Assert.assertTrue(Float.isInfinite(d.readFloat()));
		Assert.assertTrue(Float.isInfinite(d.readFloat()));
		Assert.assertTrue(Float.isInfinite(d.readFloat()));
		leave();
	}
	
	
	@Test public void testNotEnclosedDoubleSequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ '9',ATxtReadFormat0.TOKEN_BOUNDARY,
									'-','2', ATxtReadFormat0.TOKEN_BOUNDARY,
									'0','x','3',ATxtReadFormat0.TOKEN_BOUNDARY,
									ATxtReadFormat0.TOKEN_BOUNDARY,	//empty
									'1','.','1'						//double 
									};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((double)9==d.readDouble());
		Assert.assertTrue((double)-2==d.readDouble());
		Assert.assertTrue((double)3==d.readDouble());
		Assert.assertTrue((double)0==d.readDouble());
		Assert.assertTrue(1.1==d.readDouble());
		leave();
	}
	@Test public void testNotEnclosedDoubleSpecialSequence()throws IOException
	{
		enter();
		final int [] x = new int[]{ 'N','a','N',ATxtReadFormat0.TOKEN_BOUNDARY,
									'I','n','f','i','n','i','t','y',ATxtReadFormat0.TOKEN_BOUNDARY,
									'+','I','n','f','i','n','i','t','y',ATxtReadFormat0.TOKEN_BOUNDARY,
									'-','I','n','f','i','n','i','t','y',ATxtReadFormat0.TOKEN_BOUNDARY
									};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(Double.isNaN(d.readDouble()));
		Assert.assertTrue(Double.isInfinite(d.readDouble()));
		Assert.assertTrue(Double.isInfinite(d.readDouble()));
		Assert.assertTrue(Double.isInfinite(d.readDouble()));
		leave();
	}
	
	
	@Test public void testEnclosed_detectes_elementary_boolean()throws IOException
	{
		//Note: Tested class is implementing the ENoMoreData functionality
		//		in the same piece of code for all numeric, so boolean is enought
		//		to test all except char.
		enter();
		final int [] x = new int[]{  
										'1',
									ATxtReadFormat0.TOKEN_SIGNAL,
										'0'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true== d.readBoolean());
		Assert.assertTrue(!d.hasElementaryData());
		d.next();
		Assert.assertTrue(d.hasElementaryData());
		Assert.assertTrue(false== d.readBoolean());
		leave();
	}
	
	@Test public void testENoMoreDataRecoverable_boolean()throws IOException
	{
		//Note: Tested class is implementing the ENoMoreData functionality
		//		in the same piece of code for all numeric, so boolean is enought
		//		to test all except char.
		enter();
		final int [] x = new int[]{  
										'1',
									ATxtReadFormat0.TOKEN_SIGNAL,
										'0'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true== d.readBoolean());
		try{
				d.readBoolean();
				Assert.fail();
		}catch(ENoMoreData ex){ System.out.println(ex); };
		d.next();
		Assert.assertTrue(false== d.readBoolean());
		leave();
	}
	
	
	
	@Test public void testEnclosed_detectes_elementary_char()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1','2',
									ATxtReadFormat0.TOKEN_SIGNAL,
										'0'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue('1'== d.readChar());
		Assert.assertTrue('2'== d.readChar());
		Assert.assertTrue(!d.hasElementaryData());
		d.next();
		Assert.assertTrue(d.hasElementaryData());
		Assert.assertTrue('0'== d.readChar());
		leave();
	}
	
	@Test public void testENoMoreDataRecoverable_char()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',
									ATxtReadFormat0.TOKEN_SIGNAL,
										'0'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue('1'== d.readChar());
		try{
				d.readChar();
				Assert.fail();
		}catch(ENoMoreData ex){ System.out.println(ex); };
		d.next();
		Assert.assertTrue('0'== d.readChar());
		leave();
	}
	
	
	
	
	
	
	
	
	@Test public void testBooleanBlock_terminated_by_signal()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										 ATxtReadFormat0.TOKEN_SIGNAL
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true== d.readBooleanBlock());
		boolean b[] = new boolean[100];
		Assert.assertTrue(2== d.readBooleanBlock(b,1,10));		
		Assert.assertTrue(b[1]==false);
		Assert.assertTrue(b[2]==true);
		Assert.assertTrue(-1== d.readBooleanBlock(b,1,10));
		try{
			d.readBooleanBlock();
			Assert.fail();
		}catch(ENoMoreData ex){}
		leave();
	}
	
	@Test public void testBooleanBlock_terminated_by_eof_blck()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true== d.readBooleanBlock());
		boolean b[] = new boolean[100];
		Assert.assertTrue(2== d.readBooleanBlock(b,1,10));		
		Assert.assertTrue(b[1]==false);
		Assert.assertTrue(b[2]==true);
		try{
			d.readBooleanBlock(b,1,10);
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	@Test public void testBooleanBlock_terminated_by_eof_single()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(true== d.readBooleanBlock());
		boolean b[] = new boolean[100];
		Assert.assertTrue(2== d.readBooleanBlock(b,1,10));		
		Assert.assertTrue(b[1]==false);
		Assert.assertTrue(b[2]==true);
		try{
			d.readBooleanBlock();
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	
	
	
	
	
	
	
	@Test public void testByteBlock_terminated_by_signal()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'2',ATxtReadFormat0.TOKEN_BOUNDARY,
										ATxtReadFormat0.TOKEN_BOUNDARY,//zero,due to empty
										'3',ATxtReadFormat0.TOKEN_BOUNDARY,
										 ATxtReadFormat0.TOKEN_SIGNAL
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((byte)1== d.readByteBlock());
		byte b[] = new byte[100];
		Assert.assertTrue(3== d.readByteBlock(b,1,10));		
		Assert.assertTrue(b[1]==(byte)2);
		Assert.assertTrue(b[2]==(byte)0);
		Assert.assertTrue(b[3]==(byte)3);
		Assert.assertTrue(-1== d.readByteBlock(b,1,10));
		try{
			d.readByteBlock();
			Assert.fail();
		}catch(ENoMoreData ex){}
		leave();
	}
	
	@Test public void testByteBlock_terminated_by_eof_blck()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'4',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0','x','3','1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((byte)1== d.readByteBlock());
		byte b[] = new byte[100];
		Assert.assertTrue(2== d.readByteBlock(b,1,10));		
		Assert.assertTrue(b[1]==(byte)4);
		Assert.assertTrue(b[2]==(byte)0x31);
		try{
			d.readByteBlock(b,1,10);
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	@Test public void testByteBlock_terminated_by_eof_single()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((byte)1== d.readByteBlock());
		byte b[] = new byte[100];
		Assert.assertTrue(2== d.readByteBlock(b,1,10));		
		Assert.assertTrue(b[1]==(byte)0);
		Assert.assertTrue(b[2]==(byte)1);
		try{
			d.readByteBlock();
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	
	
	@Test public void testShortBlock_terminated_by_signal()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'2',ATxtReadFormat0.TOKEN_BOUNDARY,
										ATxtReadFormat0.TOKEN_BOUNDARY,//zero,due to empty
										'3',ATxtReadFormat0.TOKEN_BOUNDARY,
										 ATxtReadFormat0.TOKEN_SIGNAL
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((short)1== d.readShortBlock());
		short b[] = new short[100];
		Assert.assertTrue(3== d.readShortBlock(b,1,10));		
		Assert.assertTrue(b[1]==(short)2);
		Assert.assertTrue(b[2]==(short)0);
		Assert.assertTrue(b[3]==(short)3);
		Assert.assertTrue(-1== d.readShortBlock(b,1,10));
		try{
			d.readShortBlock();
			Assert.fail();
		}catch(ENoMoreData ex){}
		leave();
	}
	
	@Test public void testShortBlock_terminated_by_eof_blck()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'4',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0','x','3','1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((short)1== d.readShortBlock());
		short b[] = new short[100];
		Assert.assertTrue(2== d.readShortBlock(b,1,10));		
		Assert.assertTrue(b[1]==(short)4);
		Assert.assertTrue(b[2]==(short)0x31);
		try{
			d.readShortBlock(b,1,10);
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	@Test public void testShortBlock_terminated_by_eof_single()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((short)1== d.readShortBlock());
		short b[] = new short[100];
		Assert.assertTrue(2== d.readShortBlock(b,1,10));		
		Assert.assertTrue(b[1]==(short)0);
		Assert.assertTrue(b[2]==(short)1);
		try{
			d.readShortBlock();
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	
	
	
	
	
	
	
	
	@Test public void testIntBlock_terminated_by_signal()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'2',ATxtReadFormat0.TOKEN_BOUNDARY,
										ATxtReadFormat0.TOKEN_BOUNDARY,//zero,due to empty
										'3',ATxtReadFormat0.TOKEN_BOUNDARY,
										 ATxtReadFormat0.TOKEN_SIGNAL
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(1== d.readIntBlock());
		int b[] = new int[100];
		Assert.assertTrue(3== d.readIntBlock(b,1,10));		
		Assert.assertTrue(b[1]==2);
		Assert.assertTrue(b[2]==0);
		Assert.assertTrue(b[3]==3);
		Assert.assertTrue(-1== d.readIntBlock(b,1,10));
		try{
			d.readIntBlock();
			Assert.fail();
		}catch(ENoMoreData ex){}
		leave();
	}
	
	@Test public void testIntBlock_terminated_by_eof_blck()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'4',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0','x','3','1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(1== d.readIntBlock());
		int b[] = new int[100];
		Assert.assertTrue(2== d.readIntBlock(b,1,10));		
		Assert.assertTrue(b[1]==4);
		Assert.assertTrue(b[2]==0x31);
		try{
			d.readIntBlock(b,1,10);
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	@Test public void testIntBlock_terminated_by_eof_single()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(1== d.readIntBlock());
		int b[] = new int[100];
		Assert.assertTrue(2== d.readIntBlock(b,1,10));		
		Assert.assertTrue(b[1]==0);
		Assert.assertTrue(b[2]==1);
		try{
			d.readIntBlock();
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testLongBlock_terminated_by_signal()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'2',ATxtReadFormat0.TOKEN_BOUNDARY,
										ATxtReadFormat0.TOKEN_BOUNDARY,//zero,due to empty
										'3',ATxtReadFormat0.TOKEN_BOUNDARY,
										 ATxtReadFormat0.TOKEN_SIGNAL
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((long)1== d.readLongBlock());
		long b[] = new long[100];
		Assert.assertTrue(3== d.readLongBlock(b,1,10));		
		Assert.assertTrue(b[1]==(long)2);
		Assert.assertTrue(b[2]==(long)0);
		Assert.assertTrue(b[3]==(long)3);
		Assert.assertTrue(-1== d.readLongBlock(b,1,10));
		try{
			d.readLongBlock();
			Assert.fail();
		}catch(ENoMoreData ex){}
		leave();
	}
	
	@Test public void testLongBlock_terminated_by_eof_blck()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'4',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0','x','3','1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((long)1== d.readLongBlock());
		long b[] = new long[100];
		Assert.assertTrue(2== d.readLongBlock(b,1,10));		
		Assert.assertTrue(b[1]==(long)4);
		Assert.assertTrue(b[2]==(long)0x31);
		try{
			d.readLongBlock(b,1,10);
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	@Test public void testLongBlock_terminated_by_eof_single()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue((long)1== d.readLongBlock());
		long b[] = new long[100];
		Assert.assertTrue(2== d.readLongBlock(b,1,10));		
		Assert.assertTrue(b[1]==(long)0);
		Assert.assertTrue(b[2]==(long)1);
		try{
			d.readLongBlock();
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	
	
	
	
	
	
	
	@Test public void testFloatBlock_terminated_by_signal()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1','.','3',ATxtReadFormat0.TOKEN_BOUNDARY,
										'2','.','5',ATxtReadFormat0.TOKEN_BOUNDARY,
										ATxtReadFormat0.TOKEN_BOUNDARY,//zero,due to empty
										'3',ATxtReadFormat0.TOKEN_BOUNDARY,
										 ATxtReadFormat0.TOKEN_SIGNAL
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(1.3f== d.readFloatBlock());
		float b[] = new float[100];
		Assert.assertTrue(3== d.readFloatBlock(b,1,10));		
		Assert.assertTrue(b[1]==2.5f);
		Assert.assertTrue(b[2]==0f);
		Assert.assertTrue(b[3]==3f);
		Assert.assertTrue(-1== d.readFloatBlock(b,1,10));
		try{
			d.readFloatBlock();
			Assert.fail();
		}catch(ENoMoreData ex){}
		leave();
	}
	
	@Test public void testFloatBlock_terminated_by_eof_blck()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'4',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0','x','3','1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(1f== d.readFloatBlock());
		float b[] = new float[100];
		Assert.assertTrue(2== d.readFloatBlock(b,1,10));		
		Assert.assertTrue(b[1]==4f);
		Assert.assertTrue(b[2]==(float)0x31);
		try{
			d.readFloatBlock(b,1,10);
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	@Test public void testFloatBlock_terminated_by_eof_single()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(1f== d.readFloatBlock());
		float b[] = new float[100];
		Assert.assertTrue(2== d.readFloatBlock(b,1,10));		
		Assert.assertTrue(b[1]==0f);
		Assert.assertTrue(b[2]==1f);
		try{
			d.readFloatBlock();
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	
	
	
	
	
	@Test public void testDoubleBlock_terminated_by_signal()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1','.','3',ATxtReadFormat0.TOKEN_BOUNDARY,
										'2','.','5',ATxtReadFormat0.TOKEN_BOUNDARY,
										ATxtReadFormat0.TOKEN_BOUNDARY,//zero,due to empty
										'3',ATxtReadFormat0.TOKEN_BOUNDARY,
										 ATxtReadFormat0.TOKEN_SIGNAL
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(1.3== d.readDoubleBlock());
		double b[] = new double[100];
		Assert.assertTrue(3== d.readDoubleBlock(b,1,10));		
		Assert.assertTrue(b[1]==2.5);
		Assert.assertTrue(b[2]==0);
		Assert.assertTrue(b[3]==3);
		Assert.assertTrue(-1== d.readDoubleBlock(b,1,10));
		try{
			d.readDoubleBlock();
			Assert.fail();
		}catch(ENoMoreData ex){}
		leave();
	}
	
	@Test public void testDoubleBlock_terminated_by_eof_blck()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'4',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0','x','3','1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(1== d.readDoubleBlock());
		double b[] = new double[100];
		Assert.assertTrue(2== d.readDoubleBlock(b,1,10));		
		Assert.assertTrue(b[1]==4);
		Assert.assertTrue(b[2]==(double)0x31);
		try{
			d.readDoubleBlock(b,1,10);
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	@Test public void testDoubleBlock_terminated_by_eof_single()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue(1== d.readDoubleBlock());
		double b[] = new double[100];
		Assert.assertTrue(2== d.readDoubleBlock(b,1,10));		
		Assert.assertTrue(b[1]==0);
		Assert.assertTrue(b[2]==1);
		try{
			d.readDoubleBlock();
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	
	
	
	
	
	@Test public void testCharBlock_terminated_by_signal()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1','.','3',ATxtReadFormat0.TOKEN_BOUNDARY,
										'2','.','5',ATxtReadFormat0.TOKEN_BOUNDARY,
										ATxtReadFormat0.TOKEN_BOUNDARY,//zero,due to empty
										'3',ATxtReadFormat0.TOKEN_BOUNDARY,
										 ATxtReadFormat0.TOKEN_SIGNAL
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue('1'== d.readCharBlock());
		char b[] = new char[100];
		Assert.assertTrue(6== d.readCharBlock(b,1,10));		
		Assert.assertTrue(b[1]=='.');
		Assert.assertTrue(b[2]=='3');
		Assert.assertTrue(b[3]=='2');
		Assert.assertTrue(b[4]=='.');
		Assert.assertTrue(b[5]=='5');
		Assert.assertTrue(b[6]=='3');
		Assert.assertTrue(-1== d.readCharBlock(b,1,10));
		try{
			d.readCharBlock();
			Assert.fail();
		}catch(ENoMoreData ex){}
		leave();
	}
	
	@Test public void testCharBlock_terminated_by_eof_blck()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'4',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0','x','3','1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue('1'== d.readCharBlock());
		char b[] = new char[100];
		Assert.assertTrue(5== d.readCharBlock(b,1,10));		
		Assert.assertTrue(b[1]=='4');
		Assert.assertTrue(b[2]=='0');
		Assert.assertTrue(b[3]=='x');
		Assert.assertTrue(b[4]=='3');
		Assert.assertTrue(b[5]=='1');
		try{
			d.readCharBlock(b,1,10);
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	@Test public void testCharBlock_terminated_by_eof_single()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue('1'== d.readCharBlock());
		char b[] = new char[100];
		Assert.assertTrue(2== d.readCharBlock(b,1,10));		
		Assert.assertTrue(b[1]=='0');
		Assert.assertTrue(b[2]=='1');
		try{
			d.readCharBlock();
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	
	
	
	
	
	
	@Test public void testStringBlock_terminated_by_signal()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1','.','3',ATxtReadFormat0.TOKEN_BOUNDARY,
										'2','.','5',ATxtReadFormat0.TOKEN_BOUNDARY,
										ATxtReadFormat0.TOKEN_BOUNDARY,//zero,due to empty
										'3',ATxtReadFormat0.TOKEN_BOUNDARY,
										 ATxtReadFormat0.TOKEN_SIGNAL
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue('1'== d.readString());
		StringBuilder b = new StringBuilder();
		Assert.assertTrue(6== d.readString(b,10));
		Assert.assertTrue(".32.53".equals(b.toString()));
		Assert.assertTrue(-1== d.readString(b,10));
		try{
			d.readString();
			Assert.fail();
		}catch(ENoMoreData ex){}
		leave();
	}
	
	@Test public void testStringBlock_terminated_by_eof_blck()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'4',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0','x','3','1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue('1'== d.readString());
		StringBuilder b = new StringBuilder();
		Assert.assertTrue(5== d.readString(b,10));
		Assert.assertTrue("40x31".equals(b.toString()));
		try{
			d.readString(b,10);
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
	
	@Test public void testStringBlock_terminated_by_eof_single()throws IOException
	{
		enter();
		final int [] x = new int[]{  
										'1',ATxtReadFormat0.TOKEN_BOUNDARY,
										'0',ATxtReadFormat0.TOKEN_BOUNDARY,
										'1'
										};
		DUT d= new DUT(x);
		d.open();
		Assert.assertTrue('1'== d.readString());
		StringBuilder b = new StringBuilder();
		Assert.assertTrue(2== d.readString(b,10));
		Assert.assertTrue("01".equals(b.toString()));
		try{
			d.readString();
			Assert.fail();
		}catch(EEof ex){}
		leave();
	}
};