package sztejkat.abstractfmt;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
/**
	A set of interoperatibilty tests focused on exchanging primitive
	data. Tests in this class are agnostic to whether streams
	are described or not.
*/
public abstract class ATestISignalFormat_Primitives extends ATestISignalFormatBase
{
		/* ********************************************************************
		
		
				"Positive" tests which check how stream reacts
				when all data are present and in correct place, type
				and amount.
		
		
		
		*********************************************************************/
		/* -----------------------------------------------------------------
		
				Just elementary tests.				
		
		-----------------------------------------------------------------*/
		@Test public void testByte_1()throws IOException
		{
			enter();
			/*
				In this test we just write single byte and read it back
			*/			
			Pair p = create(8,8);
			try{
				p.write.writeByte((byte)-45);
				p.write.close();			
				Assert.assertTrue(p.read.readByte()==(byte)-45);
				}finally{ p.close(); };
		};
		/* -----------------------------------------------------------------
		
		
				Test capability of carrying large subset
				of possible numbers through elementary primitives.
				
		
		-----------------------------------------------------------------*/
		@Test public void testBytes()throws IOException
		{
			enter();
			/*
				In this test we just write all bytes and read them back
			*/			
			Pair p = create(8,8);
			try{
			
					for(int i=-100;i<100;i+=10)
					{
							p.write.writeByte((byte)(i));
					};
					p.write.close();			
					for(int i=-100;i<100;i+=10)
					{
							Assert.assertTrue( p.read.readByte()==(byte)(i));
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@Test public void testChars()throws IOException
		{
			enter();
			/*
				In this test we just write all chars
			*/			
			Pair p = create(8,8);
			try{
					for(int i=0;i<65536;i+=37)
					{
							p.write.writeChar((char)i);
					};
					p.write.close();					
					for(int i=0;i<65536;i+=37)
					{
							Assert.assertTrue( p.read.readChar()==(char)i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		@Test public void testShorts()throws IOException
		{
			enter();
			/*
				In this test we just write all chars
			*/			
			Pair p = create(8,8);
			try{
					for(int i=-32767;i<32768;i+=97)
					{
							p.write.writeShort((short)i);
					};
					p.write.close();					
					for(int i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readShort()==(short)i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@Test  public void testInts()throws IOException
		{
			enter();
			/*
				In this test we just write some ints
			*/			
			Pair p = create(8,8);
			try{
					for(int i=-32767;i<32768;i+=97)
					{
							p.write.writeInt(i + 37*i);
					};
					p.write.close();					
					for(int i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readInt()==i + 37*i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@Test public void testLongs()throws IOException
		{
			enter();
			/*
				In this test we just write some longs
			*/			
			Pair p = create(8,8);
			try{
					for(long i=-32767;i<32768;i+=97)
					{
							p.write.writeLong(i + 37*i + 997*i);
					};
					p.write.close();					
					for(long i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readLong()==i + 37*i+ 997*i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@Test  public void testFloats()throws IOException
		{
			enter();
			/*
				In this test we just write some floats
			*/			
			Pair p = create(8,8);
			try{
					for(long i=-32767;i<32768;i+=97)
					{
							p.write.writeFloat((1e-2f*(i + 37*i + 997*i)-i));
					};
					p.write.close();					
					for(long i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readFloat()==(1e-2f*(i + 37*i + 997*i)-i));
					};
			}finally{ p.close(); };
			
			leave();
		};
		@Test public void testDouble()throws IOException
		{
			enter();
			/*
				In this test we just write some doubles
			*/			
			Pair p = create(8,8);
			try{
					for(long i=-32767;i<32768;i+=97)
					{
							p.write.writeDouble((double)(1e-2f*(i + 37*i + 997*i)-i));
					};
					p.write.flush();
					
					for(long i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readDouble()==(double)(1e-2f*(i + 37*i + 997*i)-i));
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		
		/* -----------------------------------------------------------------
		
		
		
			Signals, as necessary to ensure block data reads.
				
				
		
		-----------------------------------------------------------------*/
		@Test public void testBeginEnd()throws IOException
		{
			enter();
			/*
				In this test we write a signal and test 
				if it is readable.
			*/			
			Pair p = create(8,8);
			try{
				
					p.write.begin("pershing");
					p.write.end();
					p.write.close();
				
				System.out.println("fetching begin...");	
					Assert.assertTrue("pershing".equals(p.read.next()));
				System.out.println("fetching end...");
					Assert.assertTrue(null==p.read.next());
				System.out.println("polling for eof...");
					Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		
		
		
		
		/* -----------------------------------------------------------------
		
		
		
				Blocks of data
				
				
		
		-----------------------------------------------------------------*/
		
		
		/* ................................................................
					Boolean
		................................................................*/
		private static boolean [] prepareBooleanBlock(int size)
		{	
			boolean [] a = new boolean[size];
			for(int i=0;i<size;i++){ a[i] = ((i &0x01)!=0); }
			return a;
		};
		private void testBooleanBlock(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create(8,8);
			try{
				int wL = wlength/2;
				int wH = wlength - wL;
				
				int rL = wlength/3;	//<-- intentionally wlength
				int rH = rlength - rL;
				
				
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					boolean [] blk = prepareBooleanBlock(woffset+wlength);
					p.write.writeBooleanBlock(blk,woffset,wL);
					p.write.writeBooleanBlock(blk,woffset+wL,wH);
					p.write.end();
					p.write.close();
					
					Assert.assertTrue("block".equals(p.read.next()));
					boolean [] res = new boolean[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r = p.read.readBooleanBlock(res, roffset, rL);
						r += p.read.readBooleanBlock(res, roffset+rL, rH);
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==ISignalReadFormat.PRMTV_BOOLEAN_BLOCK)
										:
										(p.read.whatNext()==ISignalReadFormat.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test public void testBooleanBlock_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testBooleanBlock(0,0,16,16);
				testBooleanBlock(0,5,16,16);
				testBooleanBlock(5,0,16,16);
			leave();
		};
		@Test public void testBooleanBlock_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testBooleanBlock(0,0,16,19);
				testBooleanBlock(0,5,16,19);
				testBooleanBlock(5,0,16,19);
			leave();
		};
		@Test public void testBooleanBlock_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testBooleanBlock(0,0,16,10);
				testBooleanBlock(0,5,16,10);
				testBooleanBlock(5,0,16,10);
			leave();
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* ********************************************************************
		
		
		
		
				Negative tests, checking how stream behaves when
				there is no data.
				
				
				
		
		*********************************************************************/
		/* ------------------------------------------------------------------
		
				If there is no elementary primitive data in stream at all
				to initate operation (EUnexpectedEof)
		
		------------------------------------------------------------------*/		
		@Test  @org.junit.Ignore public void testUEof_Bool()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeBoolean(false);
					p.write.flush();
					
					Assert.assertTrue( p.read.readBoolean()==false);
					try{
							p.read.readBoolean();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  @org.junit.Ignore public void testUEof_Byte()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeByte((byte)34);
					p.write.flush();
					
					Assert.assertTrue( p.read.readByte()==(byte)34);
					try{
							p.read.readByte();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  @org.junit.Ignore public void testUEof_Char()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeChar((char)34);
					p.write.flush();
					
					Assert.assertTrue( p.read.readChar()==(char)34);
					try{
							p.read.readChar();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  @org.junit.Ignore public void testUEof_Shrt()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeShort((short)34);
					p.write.flush();
					
					Assert.assertTrue( p.read.readShort()==(short)34);
					try{
							p.read.readShort();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  @org.junit.Ignore public void testUEof_Int()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeInt(34);
					p.write.flush();
					
					Assert.assertTrue( p.read.readInt()==34);
					try{
							p.read.readInt();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  @org.junit.Ignore public void testUEof_Lng()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeLong((long)34);
					p.write.flush();
					
					Assert.assertTrue( p.read.readLong()==(long)34);
					try{
							p.read.readLong();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  @org.junit.Ignore public void testUEof_Flt()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeFloat((float)34);
					p.write.flush();
					
					Assert.assertTrue( p.read.readFloat()==(float)34);
					try{
							p.read.readFloat();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  @org.junit.Ignore public void testUEof_Dbl()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeDouble((double)34);
					p.write.flush();
					
					Assert.assertTrue( p.read.readDouble()==(double)34);
					try{
							p.read.readDouble();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		/* ------------------------------------------------------------------
		
				If there is no elementary primitive data in event
				to initate operation (ENoMoreData)
		
		------------------------------------------------------------------*/
		@Test  @org.junit.Ignore public void testENoMoreData_elemntry()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
				We do run single test to check if all possible subsequent
				attempts do fail and if signal is then read correctly.
			*/			
			Pair p = create(8,8);
			try{
					p.write.begin("paris");
					p.write.end();
					p.write.flush();
					
					Assert.assertTrue( p.read.next()!=null);
					try{
							p.read.readBoolean();
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readByte();
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readChar();
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readInt();
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readLong();
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readFloat();
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readDouble();
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					Assert.assertTrue( p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};	
		
		@Test  @org.junit.Ignore public void testENoMoreData_block()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
				We do run single test to check if all possible subsequent
				attempts do fail and if signal is then read correctly.
			*/			
			Pair p = create(8,8);
			try{
					p.write.begin("paris");
					p.write.end();
					p.write.flush();
					
					Assert.assertTrue( p.read.next()!=null);
					try{
							p.read.readBooleanBlock(new boolean[3],0,1);
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readByteBlock();
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readByteBlock(new byte[3],0,1);
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readCharBlock(new char[3],0,1);
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readCharBlock(new StringBuilder(),3);
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readIntBlock(new int[3],0,1);
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readLongBlock(new long[5],1,3);
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readFloatBlock(new float[4],0,2);
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readDoubleBlock(new double[5],0,4);
							Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					Assert.assertTrue( p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};	
};